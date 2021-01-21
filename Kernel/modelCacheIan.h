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

#ifndef MODELCACHEIAN_H
#define MODELCACHEIAN_H

#include "modelCacheSingleton.h"
#include "dlCompletionTree.h"
#include "dlDag.h"
#include "tSetAsTree.h"

class SaveLoadManager;

/** model caching implementation of Ian's algorithms.
	Very fast cache check, but less precise than AsTree one.
*/
class modelCacheIan: public modelCacheInterface
{
// FIXME!! for now only
friend class DLConceptTaxonomy;
protected:	// types
		/// define the type of an index set
	typedef TSetAsTree IndexSet;
		/// node label iterator
	typedef DlCompletionTree::const_label_iterator l_iterator;
		/// edges iterator
	typedef DlCompletionTree::const_edge_iterator e_iterator;

protected:	// members
	// sets for the cache

		/// named concepts that appears positively det-lly in a root node of a cache
	IndexSet posDConcepts;
		/// named concepts that appears positively non-det in a root node of a cache
	IndexSet posNConcepts;
		/// named concepts that appears negatively det-lly in a root node of a cache
	IndexSet negDConcepts;
		/// named concepts that appears negatively non-det in a root node of a cache
	IndexSet negNConcepts;
#ifdef RKG_USE_SIMPLE_RULES
		/// extra det-lly concepts that are (partial) Simple Rule applications
	IndexSet extraDConcepts;
		/// extra non-det concepts that are (partial) Simple Rule applications
	IndexSet extraNConcepts;
#endif
		/// role names that are labels of the outgoing edges from the root node
	IndexSet existsRoles;
		/// role names that appears in the \A restrictions in the root node
	IndexSet forallRoles;
		/// role names that appears in the atmost restrictions in the root node
	IndexSet funcRoles;

		/// current state of cache model; recalculates on every change
	modelCacheState curState = csValid;

protected:	// methods
		/// log a particular implementation of a cache entry
	void logCacheEntryImpl ( void ) const override;
		/// add single concept from label to cache
	void processConcept ( const DLVertex& cur, bool pos, bool det );
		/// add all roles that are accepted by an automaton from a given entry
	void processAutomaton ( const DLVertex& cur );
		/// process CT label in given interval; set Deterministic accordingly
	void processLabelInterval ( const DLDag& DLHeap, l_iterator start, l_iterator end )
	{
		for ( l_iterator p = start; p != end; ++p )
			processConcept ( DLHeap[*p], isPositive(p->bp()), p->getDep().empty() );
	}
		/// fills cache sets by tree->Label; set Deterministic accordingly
	void initCacheByLabel ( const DLDag& DLHeap, const DlCompletionTree* pCT )
	{
		processLabelInterval ( DLHeap, pCT->beginl_sc(), pCT->endl_sc() );
		processLabelInterval ( DLHeap, pCT->beginl_cc(), pCT->endl_cc() );
	}
		/// adds role to exists- and func-role if necessary
	void addRoleToCache ( const TRole* R )
	{
		existsRoles.insert(R->index());
		if ( R->isTopFunc() )	// all other top-funcs would be added separately
			funcRoles.insert(R->index());
	}
		/// adds role (and all its super-roles) to exists- and funcRoles
	void addExistsRole ( const TRole* R )
	{
		addRoleToCache(R);
		for ( auto sup: R->ancestors() )
			addRoleToCache(sup);
	}

	// access to the arrays

		/// get RW access to D-concepts wrt polarity
	IndexSet& getDConcepts ( bool pos ) { return pos ? posDConcepts : negDConcepts; }
		/// get RW access to N-concepts wrt polarity
	IndexSet& getNConcepts ( bool pos ) { return pos ? posNConcepts : negNConcepts; }
		/// get RO access to D-concepts wrt polarity
	const IndexSet& getDConcepts ( bool pos ) const { return pos ? posDConcepts : negDConcepts; }
		/// get RO access to N-concepts wrt polarity
	const IndexSet& getNConcepts ( bool pos ) const { return pos ? posNConcepts : negNConcepts; }
#ifdef RKG_USE_SIMPLE_RULES
		/// get RW access to extra concepts wrt deterministic flag
	IndexSet& getExtra ( bool det ) { return det ? extraDConcepts : extraNConcepts; }
		/// get RO access to extra concepts wrt deterministic flag
	const IndexSet& getExtra ( bool det ) const { return det ? extraDConcepts : extraNConcepts; }
#endif

	// merge support

		/// implementation of merging with Singleton cache type
	modelCacheState isMergeableSingleton ( BipolarPointer bp ) const;
		/// implementation of merging with Ian's cache type
	modelCacheState isMergeableIan ( const modelCacheIan* p ) const;
		/// actual merge with a singleton cache
	void mergeSingleton ( BipolarPointer bp );
		/// actual merge with an Ian's cache
	void mergeIan ( const modelCacheIan* p );

public:
		/// Create cache model of given CompletionTree using given HEAP
	modelCacheIan ( const DLDag& heap, const DlCompletionTree* p, bool flagNominals, unsigned int nC, unsigned int nR )
		: modelCacheInterface(flagNominals)
		, posDConcepts(nC)
		, posNConcepts(nC)
		, negDConcepts(nC)
		, negNConcepts(nC)
#	ifdef RKG_USE_SIMPLE_RULES
		, extraDConcepts(nC)
		, extraNConcepts(nC)
#	endif
		, existsRoles(nR)
		, forallRoles(nR)
		, funcRoles(nR)
	{
		initCacheByLabel ( heap, p );
		initRolesFromArcs(p);
	}
		/// empty c'tor
	modelCacheIan ( bool flagNominals, unsigned int nC, unsigned int nR )
		: modelCacheInterface(flagNominals)
		, posDConcepts(nC)
		, posNConcepts(nC)
		, negDConcepts(nC)
		, negNConcepts(nC)
#	ifdef RKG_USE_SIMPLE_RULES
		, extraDConcepts(nC)
		, extraNConcepts(nC)
#	endif
		, existsRoles(nR)
		, forallRoles(nR)
		, funcRoles(nR)
		{}
		/// copy c'tor
	modelCacheIan ( const modelCacheIan& m )
		: modelCacheInterface(m.hasNominalNode)
		, posDConcepts(m.posDConcepts)
		, posNConcepts(m.posNConcepts)
		, negDConcepts(m.negDConcepts)
		, negNConcepts(m.negNConcepts)
#	ifdef RKG_USE_SIMPLE_RULES
		, extraDConcepts(m.extraDConcepts)
		, extraNConcepts(m.extraNConcepts)
#	endif
		, existsRoles(m.existsRoles)
		, forallRoles(m.forallRoles)
		, funcRoles(m.funcRoles)
		, curState(m.getState())
		{}
		/// create a clone of the given cache
	modelCacheIan* clone ( void ) const { return new modelCacheIan(*this); }

	/** Check the internal state of the model cache. The check is very fast.
		Does NOT return csUnknown
	*/
	modelCacheState getState ( void ) const override { return curState; }

		/// init existRoles from arcs; can be used to create pseudo-cache with deps of CT edges
	void initRolesFromArcs ( const DlCompletionTree* pCT )
	{
		for ( e_iterator q = pCT->begin(), q_end = pCT->end(); q < q_end; ++q )
			if ( !(*q)->isIBlocked() )
				addExistsRole ( (*q)->getRole() );

		curState = csValid;
	}
		/// clear the cache
	void clear ( void );

		/// check whether two caches can be merged; @return state of "merged" model
	modelCacheState canMerge ( const modelCacheInterface* cache ) const override;
		/// Merge given model to current one; return state of the merged model
	modelCacheState merge ( const modelCacheInterface* cache );

		/// get type of cache (deep or shallow)
	bool shallowCache ( void ) const override { return existsRoles.empty(); }

	//----------------------------------------------
	//-- save/load interface; implementation in SaveLoad.cpp
	//----------------------------------------------

		/// save internal state of the Kernel to a file NAME
	void Save ( SaveLoadManager& m ) const;
		/// load internal state of the Kernel from a file NAME
	void Load ( SaveLoadManager& m );
}; // modelCacheIan

#endif
