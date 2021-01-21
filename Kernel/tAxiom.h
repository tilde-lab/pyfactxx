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

#ifndef TAXIOM_H
#define TAXIOM_H

// uncomment this to have absorption debug messages
//#define RKG_DEBUG_ABSORPTION

#ifdef RKG_DEBUG_ABSORPTION
#	include <iostream>
#else
#	include <iosfwd>
#endif

#include <vector>

#include "globaldef.h"
#include "dltree.h"
#include "tConcept.h"
#include "tRole.h"
#include "counter.h"

class TBox;

// statistical counters lives here
namespace Stat
{
class SAbsRepCN: public counter<SAbsRepCN> {};
class SAbsRepForall: public counter<SAbsRepForall> {};
class SAbsSplit: public counter<SAbsSplit> {};
class SAbsBApply: public counter<SAbsBApply> {};
class SAbsTApply: public counter<SAbsTApply> {};
class SAbsCApply: public counter<SAbsCApply> {};
class SAbsCAttempt: public counter<SAbsCAttempt> {};
class SAbsNApply: public counter<SAbsNApply> {};
class SAbsNAttempt: public counter<SAbsNAttempt> {};
class SAbsRApply: public counter<SAbsRApply> {};
class SAbsRAttempt: public counter<SAbsRAttempt> {};
}

// NS for different DLTree matchers for trees in axiom
namespace InAx
{
		/// build an RW concept from a given [C|I]NAME-rooted DLTree
	inline TConcept* getConcept ( DLTree* p )
		{ return static_cast<TConcept*>(p->Element().getNE()); }
		/// build an RO concept from a given [C|I]NAME-rooted DLTree
	inline const TConcept* getConcept ( const DLTree* p )
		{ return static_cast<const TConcept*>(p->Element().getNE()); }
		/// @ return true if a concept C is a concept is non-primitive
	bool isNP ( const TConcept* C, TBox& KB );

		/// @return true iff P is a TOP
	inline bool isTop ( const DLTree* p ) { return p->Element() == BOTTOM; }
		/// @return true iff P is a BOTTOM
	inline bool isBot ( const DLTree* p ) { return p->Element() == TOP; }
		/// @return true iff P is a positive concept name
	inline bool isPosCN ( const DLTree* p ) { return p->Element() == NOT && isName(p->Left()); }
		/// @return true iff P is a positive non-primitive CN
	inline bool isPosNP ( const DLTree* p, TBox& KB )
		{ return isPosCN(p) && isNP(getConcept(p->Left()),KB); }
		/// @return true iff P is a positive primitive CN
	inline bool isPosPC ( const DLTree* p )
		{ return isPosCN(p) && getConcept(p->Left())->isPrimitive(); }
		/// @return true iff P is a negative concept name
	inline bool isNegCN ( const DLTree* p ) { return isName(p); }
		/// @return true iff P is a negative non-primitive CN
	inline bool isNegNP ( const DLTree* p, TBox& KB )
		{ return isNegCN(p) && isNP(getConcept(p),KB); }
		/// @return true iff P is a negative primitive CN
	inline bool isNegPC ( const DLTree* p )
		{ return isNegCN(p) && getConcept(p)->isPrimitive(); }
		/// @return true iff P is an AND expression
	inline bool isAnd ( const DLTree* p ) { return p->Element() == NOT && p->Left()->Element() == AND; }
		/// @return true iff P is an OR expression
	inline bool isOr ( const DLTree* p ) { return p->Element() == AND; }
		/// @return true iff P is a general FORALL expression
	inline bool isForall ( const DLTree* p ) { return p->Element() == NOT && p->Left()->Element() == FORALL; }
		/// @return true iff P is an object FORALL expression
	inline bool isOForall ( const DLTree* p ) { return isForall(p) && !resolveRole(p->Left()->Left())->isDataRole(); }
		/// @return true iff P is a FORALL expression suitable for absorption
	inline bool isAbsForall ( const DLTree* p )
	{
		if ( !isOForall(p) )
			return false;
		const DLTree* C = p->Left()->Right();
		if ( isTop(C) )	// no sense to replace \AR.BOTTOM as it well lead to the same GCI
			return false;
		return !isName(C) || !getConcept(C)->isSystem();
	}
		/// @return true iff P is a FORALL expression suitable for absorption with name at the end
	inline bool isSimpleForall ( const DLTree* p )
	{
		if ( !isAbsForall(p) )
			return false;
		const DLTree* C = p->Left()->Right();
		// forall is simple if its filler is a name of a primitive concept
		return isName(C) && (getConcept(C)->Description == nullptr);
	}
} // InAx

class TAxiom
{
protected:	// types
		/// type for axiom's representation, suitable for absorption
	typedef std::vector<DLTree*> absorptionSet;
		/// RW iterator for the elements of GCI
	typedef absorptionSet::iterator iterator;
		/// RO iterator for the elements of GCI
	typedef absorptionSet::const_iterator const_iterator;
		/// set of iterators to work with
	typedef absorptionSet WorkSet;

protected:	// members
		/// GCI is presented in the form (or Disjuncts);
	absorptionSet Disjuncts;
		/// the origin of an axiom if obtained during processing
	const TAxiom* origin;

protected:	// methods
	// access to labels

		/// RW begin
	iterator begin ( void ) { return Disjuncts.begin(); }
		/// RW end
	iterator end ( void ) { return Disjuncts.end(); }
		/// RO begin
	const_iterator begin ( void ) const { return Disjuncts.begin(); }
		/// RO end
	const_iterator end ( void ) const { return Disjuncts.end(); }

		/// create a copy of a given GCI; ignore SKIP entry
	TAxiom* copy ( const DLTree* skip ) const
	{
		TAxiom* ret = new TAxiom(this);
		for ( const auto& C: Disjuncts )
			if ( C != skip )
				ret->Disjuncts.push_back(clone(C));
		return ret;
	}

	// single disjunct's optimisations

		/// simplify (OR C ...) for a non-primitive C in a given position
	TAxiom* simplifyPosNP ( const DLTree* rep ) const
	{
		Stat::SAbsRepCN();
		TAxiom* ret = copy(rep);
		ret->add(createSNFNot(clone(InAx::getConcept(rep->Left())->Description)));
#	ifdef RKG_DEBUG_ABSORPTION
		std::cout << " simplify CN expression for" << rep->Left();
#	endif
		return ret;
	}
		/// simplify (OR ~C ...) for a non-primitive C in a given position
	TAxiom* simplifyNegNP ( const DLTree* rep ) const
	{
		Stat::SAbsRepCN();
		TAxiom* ret = copy(rep);
		ret->add(clone(InAx::getConcept(rep)->Description));
#	ifdef RKG_DEBUG_ABSORPTION
		std::cout << " simplify ~CN expression for" << rep;
#	endif
		return ret;
	}
		/// simplify (OR (SOME R C) ...)) in a given position
	TAxiom* simplifyForall ( const DLTree* rep, TBox& KB ) const;
		/// split (OR (AND...) ...) in a given position
	void split ( std::vector<TAxiom*>& acc, const DLTree* rep, DLTree* pAnd ) const
	{
		if ( pAnd->Element().getToken() == AND )
		{	// split the AND
			split ( acc, rep, pAnd->Left() );
			split ( acc, rep, pAnd->Right() );
		}
		else
		{
			TAxiom* ret = copy(rep);
			ret->add(createSNFNot(clone(pAnd)));
			acc.push_back(ret);
		}
	}
		/// create a concept expression corresponding to a given GCI; ignore SKIP entry
	DLTree* createAnAxiom ( const DLTree* skip ) const;

public:		// interface
		/// create an empty GCI
	explicit TAxiom ( const TAxiom* parent ) : origin(parent) {}
		/// copy c'tor
	TAxiom ( const TAxiom& ) = delete;
		/// assignment
	TAxiom& operator = ( const TAxiom& ) = delete;
		/// d'tor: delete elements if AX is not in use
	~TAxiom()
	{
		for ( auto& p: Disjuncts )
			deleteTree(p);
	}

		/// add DLTree to an axiom
	void add ( DLTree* p );
		/// check whether 2 axioms are the same
	bool operator == ( const TAxiom& ax ) const
	{
		if ( Disjuncts.size() != ax.Disjuncts.size() )
			return false;
		auto p = begin(), q = ax.begin(), p_end = end();
		for ( ; p != p_end; ++p, ++q )
			if ( !equalTrees(*p,*q) )
				return false;
		return true;
	}

		/// replace a defined concept with its description
	TAxiom* simplifyCN ( TBox& KB ) const;
		/// replace a universal restriction with a fresh concept
	TAxiom* simplifyForall ( TBox& KB ) const;
		/// replace a simple universal restriction with a fresh concept
	TAxiom* simplifySForall ( TBox& KB ) const;
		/// split an axiom; @return new axiom and/or NULL
	bool split ( std::vector<TAxiom*>& acc ) const
	{
		acc.clear();
		for ( const auto& C: Disjuncts )
			if ( InAx::isAnd(C) )
			{
				Stat::SAbsSplit();
#			ifdef RKG_DEBUG_ABSORPTION
				std::cout << " split AND expression" << C->Left();
#			endif
				split ( acc, C, C->Left() );
				// no need to split more than once:
				// every extra splits would be together with unsplitted parts
				// like: (A or B) and (C or D) would be transform into
				// A and (C or D), B and (C or D), (A or B) and C, (A or B) and D
				// so just return here
				return true;
			}

		return false;
	}
		/// @return true iff an axiom is the same as one of its ancestors
	bool isCyclic ( void ) const
	{
		for ( auto p = origin; p; p = p->origin )
			if ( *p == *this )
			{
#			ifdef RKG_DEBUG_ABSORPTION
				std::cout << " same as ancestor";
#			endif
				return true;
			}
		return false;
	}
		/// absorb into BOTTOM; @return true if absorption is performed
	bool absorbIntoBottom ( void ) const;
		/// absorb into TOP; @return true if absorption is performed
	bool absorbIntoTop ( TBox& KB ) const;
		/// absorb into concept; @return true if absorption is performed
	bool absorbIntoConcept ( TBox& KB ) const;
		/// absorb into negation of a concept; @return true if absorption is performed
	bool absorbIntoNegConcept ( TBox& KB ) const;
		/// absorb into role domain; @return true if absorption is performed
	bool absorbIntoDomain ( void ) const;
		/// create a concept expression corresponding to a given GCI
	DLTree* createAnAxiom ( void ) const { return createAnAxiom(nullptr);	}

#ifdef RKG_DEBUG_ABSORPTION
		/// dump GCI for debug purposes
	void dump ( std::ostream& o ) const;
#endif
}; // TAxiom;

#endif
