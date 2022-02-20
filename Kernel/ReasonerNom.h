/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2003-2015 Dmitry Tsarkov and The University of Manchester
Copyright (C) 2015-2017 Dmitry Tsarkov

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
*/

#ifndef REASONERNOM_H
#define REASONERNOM_H

#include "Reasoner.h"

class NominalReasoner: public DlSatTester
{
protected:	// type definition
		/// vector of singletons
	typedef TBox::SingletonVector SingletonVector;

protected:	// members
		/// all nominals defined in TBox
	SingletonVector Nominals;

protected:	// methods
		/// prepare reasoning
	void prepareReasoner ( void ) override;
		/// there are nominals
	bool hasNominals ( void ) const override { return true; }

//-----------------------------------------------------------------------------
//--		internal nominal reasoning interface
//-----------------------------------------------------------------------------

		/// init vector of nominals defined in TBox
	void initNominalVector ( void );

		/// create cache entry for given singleton
	void registerNominalCache ( const TIndividual* p )
		{ DLHeap.setCache ( p->pName, createModelCache(p->node->resolvePBlocker()) ); }
		/// init single nominal node
	bool initNominalNode ( const TIndividual* nom )
	{
		DlCompletionTree* node = CGraph.getNewNode();
		node->setNominalLevel();
		const_cast<TIndividual*>(nom)->node = node;	// init nominal with associated node
		return initNewNode ( node, DepSet(), nom->pName );	// ABox is inconsistent
	}
		/// create nominal nodes for all individuals in TBox
	bool initNominalCloud ( void );
		/// make an R-edge between related nominals
	bool initRelatedNominals ( const TRelated* rel );
		/// use classification information for the nominal P
	void updateClassifiedSingleton ( TIndividual* p )
	{
		registerNominalCache(p);
		if ( unlikely(p->node->isPBlocked()) )
		{
			// BP of the individual P is merged to
			BipolarPointer bp = p->node->getBlocker()->label().begin_sc()->bp();
			TIndividual* blocker = (TIndividual*)DLHeap[bp].getConcept();
			fpp_assert ( blocker->node == p->node->getBlocker() );
			tBox.SameI[p] = std::make_pair ( blocker, p->node->getPurgeDep().empty() );
		}
	}

    // Checks whether a completion graph node has references to individuals (nominals)
    bool hasIndividuals(const DlCompletionTree* node);

    // Returns a list of individuals referenced by a completion graph node
    std::vector<TIndividual*> getIndividuals(const DlCompletionTree* node);

    // Populates individual relations using the completion graph and the automaton transitions of a complex role
    void followTransition(std::map<TIndividual*, std::map<const TRole*, std::set<TIndividual*>>>& role_map, const DlCompletionTree* first, const TRole* role, const DlCompletionTree* current, const RoleAutomaton& automaton, RAState state, std::map<std::vector<std::pair<const DlCompletionTree*, const DlCompletionTree*>>, std::map<RAState, std::set<const DlCompletionTree*>>>& visited, std::vector<std::pair<const DlCompletionTree*, const DlCompletionTree*>> blocker_stack);

    // Creates cache of individual relations based on the completion graph and roles automata (for faster but potentially incomplete queries)
    void precacheRelated();

public:
		/// c'tor
	explicit NominalReasoner ( TBox& tbox )
		: DlSatTester(tbox)
	{
		initNominalVector();
	}

		/// check whether ontology with nominals is consistent
	bool consistentNominalCloud ( void );

    virtual const SingletonVector* getNominals() override
    {
        return &Nominals;
    }
}; // NominalReasoner

//-----------------------------------------------------------------------------
//--	implementation of nominal reasoner-related parts of TBox
//-----------------------------------------------------------------------------

inline void
TBox :: initReasoner ( void )
{
	fpp_assert ( !reasonersInited() );	// do init only once
	stdReasoner = new DlSatTester(*this);

	if ( NCFeatures.hasSingletons() )
	{
		nomReasoner = new NominalReasoner(*this);
	}
}

#endif
