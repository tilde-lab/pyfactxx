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

#include "dlTBox.h"

//-----------------------------------------------------------------------------
//--		Subsumption axioms and support
//-----------------------------------------------------------------------------

// return true if undefined concept found
void TBox :: addSubsumeAxiom ( DLTree* sub, DLTree* sup )
{
	// for C [= C: nothing to do
	if ( equalTrees ( sub, sup ) )
	{
		deleteTree(sub);
		deleteTree(sup);
		return;
	}

	// try to apply C [= CN
	if ( isCN(sup) )
	{
		sup = applyAxiomCToCN ( sub, sup );
		if ( sup == nullptr )
			return;
	}

	// try to apply CN [= C
	if ( isCN(sub) )
	{
		sub = applyAxiomCNToC ( sub, sup );
		if ( sub == nullptr )
			return;
	}

	// check if an axiom looks like T [= \AR.C
	if ( axiomToRangeDomain ( sub, sup ) )
		;
	else // general axiom
		processGCI ( sub, sup );
}

/// tries to apply axiom D [= CN; @return NULL if applicable or new CN
DLTree*
TBox :: applyAxiomCToCN ( DLTree* D, DLTree* CN )
{
	TConcept* C = resolveSynonym(getCI(CN));
	if ( C == nullptr )	// not applicable
		return CN;

	// D [= BOTTOM: transform later as GCI
	if ( C == pBottom )
	{
		deleteTree(CN);
		return createBottom();
	}

	// D [= TOP: nothing to do
	if ( C == pTop )
		deleteTree(D);
	// check for D [= CN with CN [= D already defined
	// don't do this for D is a DN and C is an individual as cycle detection will do it better
	else if ( equalTrees ( C->Description, D ) && !( C->isSingleton() && isName(D) ) )
		deleteTree ( makeNonPrimitive(C,D) );
	else	// n/a
		return CN;

	// success
	deleteTree(CN);
	return nullptr;
}

/// tries to apply axiom CN [= D; @return NULL if applicable or new CN
DLTree*
TBox :: applyAxiomCNToC ( DLTree* CN, DLTree* D )
{
	TConcept* C = resolveSynonym(getCI(CN));
	if ( C == nullptr )	// not applicable
		return CN;

	// TOP [= D: transform later as GCI
	if ( C == pTop )
	{
		deleteTree(CN);
		return createTop();
	}

	// BOTTOM [= D: nothing to do
	if ( C == pBottom )
		deleteTree(D);
	else if ( C->isPrimitive() )
		C->addDesc(D);
	else	// C is defined
		addSubsumeForDefined ( C, D );

	// success
	deleteTree(CN);
	return nullptr;
}

/// add an axiom CN [= E for defined CN (CN=D already in base)
void
TBox :: addSubsumeForDefined ( TConcept* C, DLTree* E )
{
	// if E is a syntactic sub-class of D, then nothing to do
	if ( isSubTree ( E, C->Description ) )
	{
		deleteTree(E);
		return;
	}

	// try to see whether C contains a reference to itself at the top level
	if ( C->hasSelfInDesc() )
	{
		// remember the old description value
		DLTree* D = clone(C->Description);
		// remove C from the description
		C->removeSelfFromDescription();
		// the trees should differ here
		fpp_assert ( !equalTrees ( D, C->Description ) );
		// note that we don't know exact semantics of C for now;
		// we need to split it's definition and work via GCIs
		makeDefinitionPrimitive ( C, E, D );
	}
	else	// here we have the definition of C = D, and subsumption C [= E
	{
		if ( 1 )	// for now: it's not clear of what's going wrong
			processGCI ( getTree(C), E );
		else	// here we leave the definition of C = D, and delay the processing of C [= E
		{
			ConceptDefMap::iterator p = ExtraConceptDefs.find(C);
			if ( p == ExtraConceptDefs.end() )	// no such entry
				ExtraConceptDefs[C] = E;	// save C [= E
			else	// we have C [= X; change to C [= (X and E)
				p->second = createSNFAnd ( p->second, E );
		}
	}
}

bool TBox :: axiomToRangeDomain ( DLTree* sub, DLTree* sup )
{
	// applicability check for T [= A R.C
	if ( sub->Element() == TOP && sup->Element () == FORALL )
	{
		resolveRole(sup->Left())->setRange(clone(sup->Right()));
		// free unused memory
		deleteTree(sub);
		deleteTree(sup);
		return true;
	}
	// applicability check for E R.T [= D
	if ( sub->Element() == NOT && sub->Left()->Element() == FORALL && sub->Left()->Right()->Element() == BOTTOM )
	{
		resolveRole(sub->Left()->Left())->setDomain(sup);
		deleteTree(sub);
		return true;
	}
	return false;
}

//-----------------------------------------------------------------------------
//--		Equality axioms and support
//-----------------------------------------------------------------------------

/// process the definition LHS = RHS
void
TBox :: addEqualityAxiom ( DLTree* lhs, DLTree* rhs )
{
	// check whether LHS is a named concept
	TConcept* C = resolveSynonym(getCI(lhs));
	bool isNamedLHS = ( C && C != pTop && C != pBottom );

	// check whether RHS is a named concept
	TConcept* D = resolveSynonym(getCI(rhs));
	bool isNamedRHS = ( D && D != pTop && D != pBottom );

	// try to make a definition C = RHS for C with no definition
	if ( isNamedLHS && addNonprimitiveDefinition ( C, rhs ) )
	{
		deleteTree(lhs);
		return;
	}

	// try to make a definition RHS = LHS for RHS = C with no definition
	if ( isNamedRHS && addNonprimitiveDefinition ( D, lhs ) )
	{
		deleteTree(rhs);
		return;
	}

	// try to make a definition C = RHS for C [= D
	if ( isNamedLHS && switchToNonprimitive ( C, rhs ) )
	{
		deleteTree(lhs);
		return;
	}

	// try to make a definition RHS = LHS for RHS = C with C [= D
	if ( isNamedRHS && switchToNonprimitive ( D, lhs ) )
	{
		deleteTree(rhs);
		return;
	}

	// fail to make a concept definition; separate the definition
	addSubsumeAxiom ( clone(lhs), clone(rhs) );
	addSubsumeAxiom ( rhs, lhs );
}

/// tries to add C = RHS for the concept C; @return true if OK
bool
TBox :: addNonprimitiveDefinition ( TConcept* C, DLTree* rhs )
{
	// check whether the case is C=D for a (concept-like) D
	TConcept* D = getCI(rhs);

	// nothing to do for the case C := D for named concepts C,D with D = C already
	if ( D && resolveSynonym(D) == C )
	{
		deleteTree(rhs);
		return true;
	}

	// can't have C=D where C is a nominal and D is a concept
	if ( C->isSingleton() && D != nullptr && !D->isSingleton() )
		return false;

	// check the case whether C=RHS or C [= \top
	if ( C->canInitNonPrim(rhs) )
	{
		// delete return value in case of (possibly) duplicated description
		deleteTree ( makeNonPrimitive ( C, rhs ) );
		return true;
	}

	// can't make definition
	return false;
}

/// tries to add C = RHS for the concept C [= X; @return true if OK
bool
TBox :: switchToNonprimitive ( TConcept* C, DLTree* rhs )
{
	// make sure that we avoid making an individual equals to something else
	TConcept* D = resolveSynonym(getCI(rhs));
	if ( C->isSingleton() && D && !D->isSingleton() )
		return false;

	// check whether we process C=D where C is defined as C[=E
	if ( alwaysPreferEquals && C->isPrimitive() )	// change C to C=... with additional GCI C[=x
	{
		addSubsumeForDefined ( C, makeNonPrimitive(C,rhs) );
		return true;
	}

	return false;
}

//-----------------------------------------------------------------------------
//--		N-ary concept axioms
//-----------------------------------------------------------------------------

void TBox :: processDisjointC ( ea_iterator beg, ea_iterator end )
{
	ExpressionArray prim, rest;

	for ( ; beg < end; ++beg )
		if ( isName(*beg) &&
			 static_cast<const TConcept*>((*beg)->Element().getNE())->isPrimitive() )
			prim.push_back(*beg);
		else
			rest.push_back(*beg);

	// both primitive concept and others are in DISJ statement
	if ( !prim.empty() && !rest.empty() )
	{
		DLTree* nrest = buildDisjAux ( rest.begin(), rest.end() );

		for ( ea_iterator q = prim.begin(), q_end = prim.end(); q < q_end; ++q )
			addSubsumeAxiom ( clone(*q), clone(nrest) );

		deleteTree(nrest);
	}

	// no primitive concepts between DJ elements

	if ( !rest.empty() )
		processDisjoint ( rest.begin(), rest.end() );

	// all non-PC are done; prim is non-empty
	// FIXME!! do it in more optimal way later
	if ( !prim.empty() )
		processDisjoint ( prim.begin(), prim.end() );
}

void TBox :: processEquivalentC ( ea_iterator beg, ea_iterator end )
{
	for ( ; beg+1 < end; ++beg )
		addEqualityAxiom ( *beg, clone(*(beg+1)) );
	// now beg+1 == end, so beg points to the last element
	deleteTree(*beg);
}

//-----------------------------------------------------------------------------
//--		N-ary individual axioms
//-----------------------------------------------------------------------------

void TBox :: processDifferent ( ea_iterator beg, ea_iterator end )
{
	SingletonVector acc;
	for ( ; beg < end; ++beg )
		if ( isIndividual(*beg) )	// only nominals in DIFFERENT command
		{
			acc.push_back(toIndividual((*beg)->Element().getNE()));
			deleteTree(*beg);
		}
		else
			throw EFaCTPlusPlus("Only individuals allowed in processDifferent()");

	// register vector of disjoint nominals in proper place
	if ( acc.size() > 1 )
		Different.push_back(acc);
}

void TBox :: processSame ( ea_iterator beg, ea_iterator end )
{
	if ( beg == end )
		return;

	if ( !isIndividual(*beg) )	// only nominals in SAME command
		throw EFaCTPlusPlus("Only individuals allowed in processSame()");

	for ( ; beg+1 < end; ++beg )
	{
		if ( !isIndividual(*(beg+1)) )
			throw EFaCTPlusPlus("Only individuals allowed in processSame()");
		addEqualityAxiom ( *beg, clone(*(beg+1)) );
	}
	// now beg+1 == end, so beg points to the last element
	deleteTree(*beg);
}

//-----------------------------------------------------------------------------
//--		N-ary role axioms
//-----------------------------------------------------------------------------

void TBox :: processDisjointR ( ea_iterator beg, ea_iterator end )
{
	if ( beg == end )
		throw EFaCTPlusPlus("Empty disjoint role axiom");

	ea_iterator p, q;

	// check that all id's are correct role names
	for ( p = beg; p < end; ++p )
		if ( isTopRole(*p) )
			throw EFaCTPlusPlus("Universal role in the disjoint roles axiom");

	RoleMaster& RM = getRM(resolveRole(*beg));

	// make a disjoint roles
	for ( p = beg; p < end; ++p )
	{
		TRole* r = resolveRole(*p);

		// FIXME: this could be done more optimal...
		for ( q = p+1; q < end; ++q )
			RM.addDisjointRoles ( r, resolveRole(*q) );

		deleteTree(*p);
	}
}

void TBox :: processEquivalentR ( ea_iterator beg, ea_iterator end )
{
	if ( beg != end )
	{
		RoleMaster& RM = getRM(resolveRole(*beg));
		for ( ; beg != end-1; ++beg )
		{
			RM.addRoleSynonym ( resolveRole(*beg), resolveRole(*(beg+1)) );
			deleteTree(*beg);
		}
		deleteTree(*beg);
	}
}

