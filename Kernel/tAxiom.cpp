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

#include "tAxiom.h"
#include "dlTBox.h"

// this is to define the hard cycle in concepts: C = \exists R D, D = \exists S C
// we saw cycles of that type of length 2

typedef std::set<const TConcept*> ConceptSet;

static bool
hasDefCycle ( const TConcept* C, ConceptSet& visited )
{
	// interested in non-primitive
	if ( C->isPrimitive() )
		return false;
	// already seen -- cycle
	if ( visited.count(C) > 0 )
		return true;
	// check the structure: looking for the \exists R.C
	const DLTree* p = C->Description;
	if ( p->Element().getToken() != NOT )
		return false;
	p = p->Left();
	if ( p->Element().getToken() != FORALL )
		return false;
	p = p->Right();
	if ( p->Element().getToken() != NOT )
		return false;
	p = p->Left();
	if ( !isName(p) )
		return false;
	// here P is a concept
	// remember C
	visited.insert(C);
	// check p
	return hasDefCycle ( static_cast<const TConcept*>(p->Element().getNE()), visited );
}

static bool
hasDefCycle ( const TConcept* C )
{
	ConceptSet visited;
	return hasDefCycle ( C, visited );
}

bool
InAx :: isNP ( const TConcept* C, TBox& )
{
	return C->isNonPrimitive() && !hasDefCycle(C);
}

/// add DLTree to an axiom
void
TAxiom :: add ( DLTree* p )
{
	if ( InAx::isBot(p) )	// BOT or X == X
		return;	// nothing to do
	// flatten the disjunctions on the fly
	if ( InAx::isOr(p) )
	{
		add(clone(p->Left()));
		add(clone(p->Right()));
		deleteTree(p);
		return;
	}
	for ( const auto& C: Disjuncts )
		if ( equalTrees(p,C) )
		{
			deleteTree(p);
			return;
		}
	Disjuncts.push_back(p);
}

TAxiom*
TAxiom :: simplifyCN ( TBox& KB ) const
{
	for ( const auto& C: Disjuncts )
	{
		if ( InAx::isPosNP(C,KB) )
			return simplifyPosNP(C);
		else if ( InAx::isNegNP(C,KB) )
			return simplifyNegNP(C);
	}

	return nullptr;
}

TAxiom*
TAxiom :: simplifyForall ( TBox& KB ) const
{
	for ( const auto& C: Disjuncts )
		if ( InAx::isAbsForall(C) )
			return simplifyForall ( C, KB );

	return nullptr;
}

TAxiom*
TAxiom :: simplifySForall ( TBox& KB ) const
{
	for ( const auto& C: Disjuncts )
		if ( InAx::isSimpleForall(C) )
			return simplifyForall ( C, KB );

	return nullptr;
}

TAxiom*
TAxiom :: simplifyForall ( const DLTree* rep, TBox& KB ) const
{
	Stat::SAbsRepForall();
	DLTree* pAll = rep->Left();	// (all R ~C)
#ifdef RKG_DEBUG_ABSORPTION
	std::cout << " simplify ALL expression" << pAll;
#endif
	TAxiom* ret = copy(rep);
	ret->add(KB.getTree(KB.replaceForall(clone(pAll))));
	return ret;
}

DLTree*
TAxiom :: createAnAxiom ( const DLTree* skip ) const
{
	// create new OR vertex for the axiom:
	DLTree* Or = createTop();
	for ( const auto& C: Disjuncts )
		if ( C != skip )
			Or = createSNFAnd ( clone(C), Or );

	return createSNFNot(Or);
}

#ifdef RKG_DEBUG_ABSORPTION
void TAxiom :: dump ( std::ostream& o ) const
{
	o << " (neg-and";
	for ( const auto& C: Disjuncts )
		o << C;
	o << ")";
}
#endif

/// absorb into BOTTOM; @return true if absorption is performed
bool
TAxiom :: absorbIntoBottom ( void ) const
{
	absorptionSet Pos, Neg;
	for ( const auto& C: Disjuncts )
		switch ( C->Element().getToken() )
		{
		case BOTTOM:	// axiom in the form T [= T or ...; nothing to do
			Stat::SAbsBApply();
#		ifdef RKG_DEBUG_ABSORPTION
			std::cout << " Absorb into BOTTOM";
#		endif
			return true;
		case TOP:	// skip it here
			break;
		case NOT:	// something negated: put it into NEG
			Neg.push_back(C->Left());
			break;
		default:	// something positive: save in POS
			Pos.push_back(C);
			break;
		}

	// now check whether there is a concept in both POS and NEG
	for ( const auto& neg: Neg )
		for ( const auto& pos: Pos )
			if ( equalTrees ( neg, pos ) )
			{
				Stat::SAbsBApply();
#			ifdef RKG_DEBUG_ABSORPTION
				std::cout << " Absorb into BOTTOM due to (not" << neg << ") and" << pos;
#			endif
				return true;
			}
	return false;
}

bool
TAxiom :: absorbIntoTop ( TBox& KB ) const
{
	TConcept* Cand = nullptr;

	// check whether the axiom is Top [= C
	for ( const auto& C: Disjuncts )
		if ( InAx::isBot(C) )	// BOTTOMS are fine
			continue;
		else if ( InAx::isPosCN(C) )	// CN found
		{
			if ( Cand != nullptr )	// more than one concept
				return false;
			Cand = InAx::getConcept(C->Left());
			if ( Cand->isSingleton() )	// doesn't work with nominals
				return false;
		}
		else
			return false;

	if ( Cand == nullptr )
		return false;

	// make an absorption
	Stat::SAbsTApply();
	DLTree* desc = KB.makeNonPrimitive ( Cand, createTop() );

#ifdef RKG_DEBUG_ABSORPTION
	std::cout << " T-Absorb GCI to axiom";
	if ( desc )
		std::cout << "s *TOP* [=" << desc << " and";
	std::cout << " " << Cand->getName() << " = *TOP*";
#endif
	if ( desc )
		KB.addSubsumeAxiom ( createTop(), desc );

	return true;
}

bool
TAxiom :: absorbIntoConcept ( TBox& KB ) const
{
	WorkSet Cons;
	DLTree* bestConcept = nullptr;

	// finds all primitive concept names
	for ( const auto& C: Disjuncts )
		if ( InAx::isNegPC(C) )	// FIXME!! review this during implementation of Nominal Absorption
		{
			Stat::SAbsCAttempt();
			Cons.push_back(C);
			if ( InAx::getConcept(C)->isSystem() )
				bestConcept = C;
		}

	// if no concept names -- return;
	if ( Cons.empty() )
		return false;

	Stat::SAbsCApply();
	// FIXME!! as for now: just take the 1st concept name
	if ( bestConcept == nullptr )
		bestConcept = Cons[0];

	// normal concept absorption
	TConcept* Concept = InAx::getConcept(bestConcept);

#ifdef RKG_DEBUG_ABSORPTION
	std::cout << " C-Absorb GCI to concept " << Concept->getName();
	if ( Cons.size() > 1 )
	{
		std::cout << " (other options are";
		for ( const auto& C: Cons )
			if ( C != bestConcept )
				std::cout << " " << InAx::getConcept(C)->getName();
		std::cout << ")";
	}
#endif

	// adds a new definition
	Concept->addDesc(createAnAxiom(bestConcept));
	Concept->removeSelfFromDescription();
	// in case T [= (A or \neg B) and (B and \neg A) there appears a cycle A [= B [= A
	// so remove potential cycle
	// FIXME!! just because TConcept can't get rid of cycle by itself
	KB.clearRelevanceInfo();
	KB.checkToldCycle(Concept);
	KB.clearRelevanceInfo();

	return true;
}

/// absorb into negation of a concept; @return true if absorption is performed
bool
TAxiom :: absorbIntoNegConcept ( TBox& KB ) const
{
	WorkSet Cons;
	TConcept* Concept;
	const DLTree* bestConcept = nullptr;

	// finds all primitive negated concept names without description
	for ( const auto& C: Disjuncts )
		if ( C->Element().getToken() == NOT && isName(C->Left())
			 && (Concept = InAx::getConcept(C->Left()))->isPrimitive()
			 && !Concept->isSingleton() && Concept->Description == nullptr )
		{
			Stat::SAbsNAttempt();
			Cons.push_back(C);
		}

	// if no concept names -- return;
	if ( Cons.empty() )
		return false;

	Stat::SAbsNApply();
	// FIXME!! as for now: just take the 1st concept name
	if ( bestConcept == nullptr )
		bestConcept = Cons[0];

	// normal concept absorption
	Concept = InAx::getConcept(bestConcept->Left());

#ifdef RKG_DEBUG_ABSORPTION
	std::cout << " N-Absorb GCI to concept " << Concept->getName();
	if ( Cons.size() > 1 )
	{
		std::cout << " (other options are";
		for ( const auto& C: Cons )
			if ( C != bestConcept )
				std::cout << " " << InAx::getConcept(C->Left())->getName();
		std::cout << ")";
	}
#endif

	// replace ~C [= D with C=~notC, notC [= D:
	// make notC [= D
	TConcept* nC = KB.getAuxConcept(createAnAxiom(bestConcept));
	// define C = ~notC; C had an empty desc, so it's safe not to delete it
	KB.makeNonPrimitive ( Concept, createSNFNot(KB.getTree(nC)) );
	return true;
}

bool
TAxiom :: absorbIntoDomain ( void ) const
{
	WorkSet Cons;
	const DLTree* bestSome = nullptr;

	// find all forall concepts
	for ( const auto& C: Disjuncts )
		if ( C->Element() == NOT &&
			 ( C->Left()->Element() == FORALL	// \neg ER.C
			   || C->Left()->Element() == LE ))	// \neg >= n R.C
		{
			Stat::SAbsRAttempt();
			Cons.push_back(C);
			// check for the direct domain case
			if ( C->Left()->Right()->Element() == BOTTOM )
			{	// found proper absorption candidate
				bestSome = C;
				break;
			}
		}

	// if there are no EXISTS concepts -- return;
	if ( Cons.empty() )
		return false;

	Stat::SAbsRApply();
	TRole* Role;

	if ( bestSome != nullptr )
		Role = resolveRole(bestSome->Left()->Left());
	else
		// FIXME!! as for now: just take the 1st role name
		Role = resolveRole(Cons[0]->Left()->Left());

#ifdef RKG_DEBUG_ABSORPTION
	std::cout << " R-Absorb GCI to the domain of role " << Role->getName();
	if ( Cons.size() > 1 )
	{
		std::cout << " (other options are";
		for ( const auto& C: Cons )
			if ( C != bestSome )
				std::cout << " " << resolveRole(C->Left()->Left())->getName();
		std::cout << ")";
	}
#endif

	// here bestSome is either actual domain, or END(); both cases are fine
	Role->setDomain(createAnAxiom(bestSome));

	return true;
}
