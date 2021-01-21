/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2003-2015 Dmitry Tsarkov and The University of Manchester
Copyright (C) 2015-2017 Dmitry Tsarkov

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/

#include <iostream>

#include "dltree.h"
#include "fpp_assert.h"
#include "tDataEntry.h"
#include "tRole.h"

	/// create entry for the role R
DLTree*
createRole ( TRole* R )
{
	return createEntry ( R->isDataRole() ? DNAME : RNAME, R );
}
	/// create inverse of role R
DLTree* createInverse ( DLTree* R )
{
	fpp_assert ( R != nullptr );	// sanity check
	switch ( R->Element().getToken() )
	{
	case INV:	// R-- = R
	{
		DLTree* p = clone(R->Left());
		deleteTree(R);
		return p;
	}
	case RNAME:	// object role name
		if ( unlikely(isTopRole(R)) || unlikely(isBotRole(R)) )
			return R;	// top/bottom roles are inverses of themselves
		return new DLTree ( TLexeme(INV), R );
	default:	// no other elements can have inverses
		fpp_unreachable();
	}
}

	/// create negation of given formula
DLTree* createSNFNot ( DLTree* C )
{
	fpp_assert ( C != nullptr );	// sanity check
	if ( C->Element() == BOTTOM )
	{	// \not F = T
		deleteTree(C);
		return createTop();
	}
	if ( C->Element() == TOP )
	{	// \not T = F
		deleteTree(C);
		return createBottom();
	}
	if ( C->Element () == NOT )
	{	// \not\not C = C
		DLTree* p = clone(C->Left());
		deleteTree(C);
		return p;
	}

	// general case
	return new DLTree ( TLexeme(NOT), C );
}

	/// create conjunction of given formulas
DLTree* createSNFAnd ( DLTree* C, DLTree* D )
{
	// try to simplify conjunction
	if ( C == nullptr )	// single element
		return D;
	if ( D == nullptr )
		return C;

	if ( C->Element() == TOP ||		// T\and D = D
		 D->Element() == BOTTOM )	// C\and F = F
	{
		deleteTree(C);
		return D;
	}

	if ( D->Element() == TOP ||		// C\and T = C
		 C->Element() == BOTTOM )	// F\and D = F
	{
		deleteTree(D);
		return C;
	}

	// no simplification possible -- return actual conjunction
	return new DLTree ( TLexeme(AND), C, D );
}

static bool
containsC ( DLTree* C, DLTree* D )
{
	switch ( C->Element().getToken() )
	{
	case CNAME:
		return equalTrees ( C, D );
	case AND:
		return containsC ( C->Left(), D ) || containsC ( C->Right(), D );
	default:
		return false;
	}
}

DLTree* createSNFReducedAnd ( DLTree* C, DLTree* D )
{
	if ( C == nullptr || D == nullptr )
		return createSNFAnd ( C, D );

	if ( D->Element().getToken() == CNAME && containsC ( C, D ) )
	{
		deleteTree(D);
		return C;
	}
	else if ( D->Element().getToken() == AND )
	{
		C = createSNFReducedAnd ( C, clone(D->Left()) );
		C = createSNFReducedAnd ( C, clone(D->Right()) );
		deleteTree(D);
		return C;
	}
	else	// can't optimise
		return createSNFAnd ( C, D );
}

// Semantic Locality checking support. DO NOT used in usual reasoning

/// @return true iff a data range DR is semantically equivalent to TOP. FIXME!! good approximation for now
static bool
isSemanticallyDataTop ( DLTree* dr ) { return dr->Element().getToken() == TOP; }

/// @return true iff a data range DR is semantically equivalent to BOTTOM. FIXME!! good approximation for now
static bool
isSemanticallyDataBottom ( DLTree* dr ) { return dr->Element().getToken() == BOTTOM; }

/// @return true iff the cardinality of a given data range DR is greater than N. FIXME!! good approximation for now
static bool
isDataRangeBigEnough ( DLTree*, unsigned int ) { return true; }

/// simplify universal restriction with top data role
static DLTree*
simplifyDataTopForall ( DLTree* dr )
{
	TreeDeleter td(dr);
	// if the filler (dr) is TOP (syntactically or semantically), then the forall is top
	if ( isSemanticallyDataTop(dr) )
		return createTop();
	// in any other case the attempt to restrict the data domain will fail
	return createBottom();
}

/// simplify minimal cardinality restriction with top data role
static DLTree*
simplifyDataTopLE ( unsigned int n, DLTree* dr )
{
	TreeDeleter td(dr);
	// if the filler (dr) is BOTTOM (syntactically or semantically), then the LE is top
	if ( isSemanticallyDataBottom(dr) )
		return createTop();
	// if the size of a filler is smaller than the cardinality, then it's always possible to make a restriction
	if ( !isDataRangeBigEnough ( dr, n ) )
		return createTop();
	// in any other case the attempt to restrict the data domain will fail
	return createBottom();
}

	/// create universal restriction of given formulas (\AR.C)
DLTree* createSNFForall ( DLTree* R, DLTree* C )
{
	if ( C->Element() == TOP )	// \AR.T = T
	{
		deleteTree(R);
		return C;
	}
	if ( unlikely(isBotRole(R)) )
	{	// \A Bot.C = T
		deleteTree(R);
		deleteTree(C);
		return createTop();
	}
	if ( unlikely(isTopRole(R)) && resolveRole(R)->isDataRole() )
	{
		deleteTree(R);
		return simplifyDataTopForall(C);
	}
	// no simplification possible
	return new DLTree ( TLexeme(FORALL), R, C );
}
	/// create at-most (LE) restriction of given formulas (<= n R.C)
DLTree* createSNFLE ( unsigned int n, DLTree* R, DLTree* C )
{
	if ( C->Element() == BOTTOM )
	{				// <= n R.F -> T;
		deleteTree(R);
		deleteTree(C);
		return createTop();
	}
	if ( n == 0 )	// <= 0 R.C -> \AR.\not C
		return createSNFForall ( R, createSNFNot(C) );
	if ( unlikely(isBotRole(R)) )
	{	// <=n Bot.C = T
		deleteTree(R);
		deleteTree(C);
		return createTop();
	}
	if ( unlikely(isTopRole(R)) && resolveRole(R)->isDataRole() )
	{
		deleteTree(R);
		return simplifyDataTopLE ( n, C );
	}
	return new DLTree ( TLexeme ( LE, n ), R, C );
}
	/// create at-least (GE) restriction of given formulas (>= n R.C)
DLTree* createSNFGE ( unsigned int n, DLTree* R, DLTree* C )
{
	if ( n == 0 )
	{		// >= 0 R.C -> T
		deleteTree(R);
		deleteTree(C);
		return createTop();
	}
	if ( C->Element() == BOTTOM )
	{		// >=n R.F -> F
		deleteTree(R);
		return C;
	}
	else	// >= n R.C -> !<= (n-1) R.C
		return createSNFNot ( createSNFLE ( n-1 , R, C ) );
}

//********************************************************************************************
//**	equalTrees implementation
//********************************************************************************************
bool equalTrees ( const DLTree* t1, const DLTree* t2 )
{
	// empty trees are equal
	if ( t1 == nullptr && t2 == nullptr )
		return true;

	// empty and non-empty trees are not equal
	if ( t1 == nullptr || t2 == nullptr )
		return false;

	// non-empty trees are checked recursively
	return ( t1->Element() == t2->Element() ) &&
		   equalTrees ( t1->Left(), t2->Left() ) &&
		   equalTrees ( t1->Right(), t2->Right() );
}

bool isSubTree ( const DLTree* t1, const DLTree* t2 )
{
	if ( t1 == nullptr || t1->Element() == TOP )
		return true;
	if ( t2 == nullptr )
		return false;
	if ( t1->Element() == AND )
		return isSubTree ( t1->Left(), t2 ) && isSubTree ( t1->Right(), t2 );
	// t1 is a single elem, t2 is a (probably) AND-tree
	if ( t2->Element() == AND )
		return isSubTree ( t1, t2->Left() ) || isSubTree ( t1, t2->Right() );
	// t1 and t2 are non-single elements
	return equalTrees(t1,t2);
}
//********************************************************************************************
//**	OnlySNF realization
//********************************************************************************************
bool isSNF ( const DLTree* t )
{
	if ( t == nullptr )
		return true;

	switch ( t -> Element (). getToken () )
	{
	case TOP:
	case BOTTOM:
	case NAME:
	case DATAEXPR:
	case NOT:
	case INV:
	case AND:
	case FORALL:
	case LE:
	case SELF:
	case RCOMPOSITION:
	case PROJFROM:
	case PROJINTO:
		return ( isSNF (t->Left()) && isSNF (t->Right()) );
	default:
		return false;
	}
}

//********************************************************************************************
const char* TokenName ( Token t )
{
	switch ( t )
	{
	case TOP:		return "*TOP*";
	case BOTTOM:	return "*BOTTOM*";
	case CNAME:		return "cname";
	case INAME:		return "iname";
	case RNAME:		return "rname";
	case DNAME:		return "dname";
	case DATAEXPR:	return "dataexpr";
	case INV:		return "inv";
	case OR:		return "or";
	case AND:		return "and";
	case NOT:		return "not";
	case EXISTS:	return "some";
	case FORALL:	return "all";
	case GE:		return "at-least";
	case LE:		return "at-most";
	case RCOMPOSITION: return "compose";
	case SELF:		return "self-ref";
	case PROJINTO:	return "project_into";
	case PROJFROM:	return "project_from";
	default:		std::cerr << "token " << t << "has no name";
					fpp_unreachable();
	};
}

std::ostream& operator << ( std::ostream& o, const DLTree *form )
{
	if ( form == nullptr )
		return o;

	const TLexeme& lex = form->Element();
	switch ( lex.getToken() )
	{
	case TOP:
	case BOTTOM:
		o << ' ' << TokenName(lex.getToken());
		break;
	case RNAME:
	case DNAME:
	case CNAME:
		o << ' ' << lex.getName();
		break;
	case INAME:
		o << " (one-of " << lex.getName() << ')';
		break;

	case DATAEXPR:
		static_cast<TDataEntry*>(lex.getNE())->printLISP(o);
		break;

	case NOT:
	case INV:
	case SELF:
		o << " (" << TokenName (lex.getToken()) << form->Left() << ')';
		break;

	case AND:
	case OR:
	case EXISTS:
	case FORALL:
	case RCOMPOSITION:
	case PROJINTO:
	case PROJFROM:
		o << " (" << TokenName (lex.getToken()) << form->Left() << form->Right() << ')';
		break;

	case GE:
	case LE:
		o << " (" << TokenName (lex.getToken()) << ' ' << lex.getData()
		  << form->Left() << form->Right() << ')';
		break;

	default:
		break;
	}
	return o;
}

//********************************************************************************************
