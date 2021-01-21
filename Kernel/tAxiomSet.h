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

#ifndef TAXIOMSET_H
#define TAXIOMSET_H

#include "tAxiom.h"

class TBox;

namespace Stat
{
	class SAbsInput: public counter<SAbsInput> {};
	class SAbsAction: public counter<SAbsAction> {};
}

/// set of GCIs, absorbable and not
class TAxiomSet
{
protected:	// internal types
		/// set of GCIs
	typedef std::vector<TAxiom*> AxiomCollection;
		/// method applying to the axiom
	typedef bool (TAxiomSet::*AbsMethod)(const TAxiom*);
		/// array of methods in application order
	typedef std::vector<AbsMethod> AbsActVector;

protected:	// members
		/// host TBox that holds all concepts/etc
	TBox& Host;
		/// set of axioms that accumulates incoming (and newly created) axioms;
	AxiomCollection Accum;
		/// set of absorption action, in order
	AbsActVector ActionVector;

protected:	// methods

		/// add already built GCI p
	void insertGCI ( TAxiom* p )
	{
#	ifdef RKG_DEBUG_ABSORPTION
		std::cout << "\n new axiom (" << Accum.size() << "):";
		p->dump(std::cout);
#	endif
		Accum.push_back(p);
	}
		/// @return true iff axiom Q is a copy of already existing axiom
	bool copyOfExisting ( const TAxiom* q ) const
	{
		for ( const TAxiom* axiom : Accum )
			if ( *q == *axiom )
			{
#			ifdef RKG_DEBUG_ABSORPTION
				std::cout << " same as (" << p-Accum.begin() << "); skip";
#			endif
				return true;
			}
		return false;
	}
		/// absorb single GCI wrt absorption flags
	bool absorbGCI ( const TAxiom* p );
		/// split given axiom
	bool split ( const TAxiom* p );
		/// helper that inserts an axiom into Accum; @return bool if success
	bool processNewAxiom ( TAxiom* q )
	{
		// no input axiom -- nothing to add
		if ( q == nullptr )
			return false;
		// if an axiom is a copy of already processed one -- fail to add (will result in a cycle)
		if ( q->isCyclic() )
		{
			delete q;
			return false;
		}
		// if an axiom is a copy of a new one -- succeed but didn't really add anything
		if ( copyOfExisting(q) )
		{
			delete q;
			return true;
		}
		// fresh axiom -- add it
		insertGCI(q);
		return true;
	}
		/// replace a defined concept with its description
	bool simplifyCN ( const TAxiom* p ) { return processNewAxiom(p->simplifyCN(Host)); }
		/// replace a universal restriction with a fresh concept
	bool simplifyForall ( const TAxiom* p ) { return processNewAxiom(p->simplifyForall(Host)); }
		/// replace a simple universal restriction with a fresh concept
	bool simplifySForall ( const TAxiom* p ) { return processNewAxiom(p->simplifySForall(Host)); }

		/// absorb single axiom AX into BOTTOM; @return true if succeed
	bool absorbIntoBottom ( const TAxiom* ax ) { return ax->absorbIntoBottom(); }
		/// absorb single axiom AX into TOP; @return true if succeed
	bool absorbIntoTop ( const TAxiom* ax ) { return ax->absorbIntoTop(Host); }
		/// absorb single axiom AX into concept; @return true if succeed
	bool absorbIntoConcept ( const TAxiom* ax ) { return ax->absorbIntoConcept(Host); }
		/// absorb single axiom AX into negated concept; @return true if succeed
	bool absorbIntoNegConcept ( const TAxiom* ax ) { return ax->absorbIntoNegConcept(Host); }
		/// absorb single axiom AX into role domain; @return true if succeed
	bool absorbIntoDomain ( const TAxiom* ax ) { return ax->absorbIntoDomain(); }

public:		// interface
		/// c'tor
	explicit TAxiomSet ( TBox& host )
		: Host(host)
		{}
		/// d'tor
	~TAxiomSet();

		/// init all absorption-related flags using given set of option
	bool initAbsorptionFlags ( const std::string& flags );
		/// add axiom for the GCI C [= D
	void addAxiom ( DLTree* C, DLTree* D )
	{
		Stat::SAbsInput();
		TAxiom* p = new TAxiom(nullptr);
		p->add(C);
		p->add(createSNFNot(D));
		insertGCI(p);
	}

		/// absorb set of axioms; @return size of not absorbed set
	size_t absorb ( void );
		/// get number of (not absorbed) GCIs
	size_t size ( void ) const { return Accum.size(); }
		/// @return true if non-concept absorption were executed
	bool wasRoleAbsorptionApplied ( void ) const { return Stat::SAbsRApply::objects_created > 0; }
		/// get GCI of all non-absorbed axioms
	DLTree* getGCI ( void ) const
	{
		DLTree* ret = createTop();
		for ( const auto& axiom: Accum )
			ret = createSNFAnd ( ret, axiom->createAnAxiom() );

		return ret;
	}

		/// print absorption statistics
	void PrintStatistics ( void ) const;
}; // TAxiomSet

#endif
