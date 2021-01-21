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

#ifndef DLTREE_H
#define DLTREE_H

#include <vector>
#include <iosfwd>

#include "globaldef.h"
#include "tLexeme.h"
#include "tsttree.h"

typedef TsTTree <TLexeme> DLTree;
class TRole;

	// checking if the tree is in Simplified Normal Form
extern bool isSNF ( const DLTree* t );

	// checks if two trees are the same (syntactically)
extern bool equalTrees ( const DLTree* t1, const DLTree* t2 );
	// check whether t1=(and c1..cn), t2 = (and d1..dm) and ci = dj for all i
extern bool isSubTree ( const DLTree* t1, const DLTree* t2 );

	// builds a copy of the formula t
inline DLTree* clone ( const DLTree* t ) { return (t==nullptr) ? nullptr : t->clone(); }

	// check if DL tree is a concept constant
inline bool isConst ( const DLTree* t )
{
	switch (t->Element().getToken())
	{
	case TOP:
	case BOTTOM:
		return true;
	default:
		return false;
	}
}

	// check if DL tree is a concept/individual name
inline bool isName ( const DLTree* t )
{
	switch (t->Element().getToken())
	{
	case CNAME:
	case INAME:
		return true;
	default:
		return false;
	}
}

	// check if DL tree is a (data)role name
inline bool isRName ( const DLTree* t )
{
	switch (t->Element().getToken())
	{
	case RNAME:
	case DNAME:
		return true;
	default:
		return false;
	}
}

	// check if DL tree is a concept-like name
inline bool isCN ( const DLTree* t )
{
	switch (t->Element().getToken())
	{
	case TOP:
	case BOTTOM:
	case CNAME:
	case INAME:
		return true;
	default:
		return false;
	}
}
	/// check whether T is a bottom (empty) role
inline bool isBotRole ( const DLTree* t ) { return likely(isRName(t)) && unlikely(t->Element().getNE()->isBottom()); }
	/// check whether T is a top (universal) role
inline bool isTopRole ( const DLTree* t ) { return likely(isRName(t)) && unlikely(t->Element().getNE()->isTop()); }

	/// check whether T is an expression in the form (atmost 1 RNAME)
inline bool isFunctionalExpr ( const DLTree* t, const TNamedEntry* R )
{
	return t->Element().getToken() == LE && R == t->Left()->Element().getNE() &&
		   t->Element().getData() == 1 && t->Right()->Element().getToken() == TOP;
}

	// check if DL Tree represents negated ONE-OF constructor
inline bool isNegOneOf ( const DLTree* t )
{
	switch (t->Element().getToken())
	{
	case AND:
		return isNegOneOf(t->Left()) && isNegOneOf(t->Right());
	case NOT:
		return t->Left()->Element().getToken() == INAME;
	default:
		return false;
	}
}
	// check if DL Tree represents ONE-OF constructor
inline bool isOneOf ( const DLTree* t )
{
	switch (t->Element().getToken())
	{
	case INAME:
		return true;
	case NOT:
		return isNegOneOf(t->Left());
	default:
		return false;
	}
}

// create SNF from given parts

	/// create TOP element
inline DLTree* createTop ( void ) { return new DLTree(TLexeme(TOP)); }
	/// create BOTTOM element
inline DLTree* createBottom ( void ) { return new DLTree(TLexeme(BOTTOM)); }

	/// create a tree with tag TAG and an entry ENTRY
inline DLTree* createEntry ( Token tag, TNamedEntry* entry ) { return new DLTree ( TLexeme ( tag, entry ) ); }

	/// create entry for the role R
extern DLTree* createRole ( TRole* R );
	/// create inverse of role R
extern DLTree* createInverse ( DLTree* R );

	/// create negation of given formula
extern DLTree* createSNFNot ( DLTree* C );
	/// create conjunction of given formulas
extern DLTree* createSNFAnd ( DLTree* C, DLTree* D );
	/// create conjunction of given formulas; aggressively reduce for the case C = (and D ...)
extern DLTree* createSNFReducedAnd ( DLTree* C, DLTree* D );
	/// create disjunction of given formulas
inline DLTree* createSNFOr ( DLTree* C, DLTree* D )
{	// C\or D -> \not(\not C\and\not D)
	return createSNFNot ( createSNFAnd ( createSNFNot(C), createSNFNot(D) ) );
}
	/// create universal restriction of given formulas (\AR.C)
extern DLTree* createSNFForall ( DLTree* R, DLTree* C );
	/// create existential restriction of given formulas (\ER.C)
inline DLTree* createSNFExists ( DLTree* R, DLTree* C )
{	// \ER.C -> \not\AR.\not C
	return createSNFNot ( createSNFForall ( R, createSNFNot(C) ) );
}
	/// create SELF restriction for role R
inline DLTree* createSNFSelf ( DLTree* R )
{
	if ( unlikely(isBotRole(R)) )
		return createBottom();	// loop on bottom role is always unsat
	if ( unlikely(isTopRole(R)) )
		return createTop();	// top role is reflexive
	return new DLTree ( TLexeme(SELF), R );
}

	/// create at-least (GE) restriction of given formulas (>= n R.C)
extern DLTree* createSNFGE ( unsigned int n, DLTree* R, DLTree* C );
	/// create at-most (LE) restriction of given formulas (<= n R.C)
extern DLTree* createSNFLE ( unsigned int n, DLTree* R, DLTree* C );

// prints formula

extern const char* TokenName ( Token t );
extern std::ostream& operator << ( std::ostream& o, const DLTree *form );

/// helper that deletes temporary trees
class TreeDeleter
{
protected:
	DLTree* ptr;
public:
	explicit TreeDeleter ( DLTree* p ) : ptr(p) {}
	~TreeDeleter() { deleteTree(ptr); }
	operator DLTree* ( void ) { return ptr; }
	operator const DLTree* ( void ) const { return ptr; }
}; // TreeDeleter

#endif
