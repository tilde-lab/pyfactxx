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

#ifndef REASONER_H
#define REASONER_H

#include "globaldef.h"
#include "tBranchingContext.h"
#include "dlCompletionGraph.h"
#include "dlTBox.h"
#include "dlDag.h"
#include "modelCacheIan.h"
#include "tSaveStack.h"
#include "procTimer.h"
#include "DataReasoning.h"
#include "ToDoList.h"
#include "tFastSet.h"

#if USE_LOGGING	// don't gather statistics w/o logging
#	define USE_REASONING_STATISTICS
#endif

#ifdef USE_REASONING_STATISTICS
/// class for gathering statistic both for session and totally
class AccumulatedStatistic
{
protected:	// static members
		/// all members are linked together
	static AccumulatedStatistic* root;

public:		// static methods
		/// accumulate all registered statistic elements
	static void accumulateAll ( void )
	{
		for ( AccumulatedStatistic* cur = root; cur; cur = cur->next )
			cur->accumulate();
	}

protected:	// members
		/// accumulated statistic
	unsigned int total = 0;
		/// current session statistic
	unsigned int local = 0;
		/// link to the next element
	AccumulatedStatistic* next;

public:		// interface
		/// c'tor: link itself to the list
	AccumulatedStatistic ( void ) : next(root) { root = this; }
		/// no copy c'tor
	AccumulatedStatistic ( const AccumulatedStatistic& ) = delete;
		/// no assignment
	AccumulatedStatistic& operator = ( const AccumulatedStatistic& ) = delete;
		/// d'tor: remove link from the list
	~AccumulatedStatistic()
	{
		// all statistic elements are deleted in the inverted creation order
		if ( root == this )
		{
			root = next;
			return;
		}
		// we are here if it's multi-environment and arbitrary deletion order was chosen
		AccumulatedStatistic *prev = root, *cur = root->next;
		// find a pointer to current node
		while ( cur && cur != this )
			prev = cur, cur = cur->next;
		if ( cur == this )
			prev->next = next;
	}

	// access to the elements

		/// get (RO) value of the element
	unsigned int get ( bool needLocal ) const { return needLocal ? local : total; }

	// general statistic operators

		/// increment local value
	void inc ( void )
	{
		++local;
	}
		/// set local value to particular N
	void set ( unsigned int n )
	{
		local = n;
	}
		/// add local value to a global one
	void accumulate ( void ) { total += local; local = 0; }

	// output

	void Print ( std::ostream& o, bool needLocal, const char* prefix, const char* suffix ) const
	{
		if ( get(needLocal) > 0 )
			o << prefix << get(needLocal) << suffix;
	}
}; // AccumulatedStatistic
#endif

class DlSatTester
{
protected:	// type definition
		/// TODO table type
	typedef ToDoList ToDoTableType;
		/// vector of edges
	typedef std::vector<DlCompletionTreeArc*> EdgeVector;
		/// vector of nodes
	typedef std::vector<DlCompletionTree*> NodeVector;
		/// RO label iterator
	typedef DlCompletionTree::const_label_iterator const_label_iterator;
		/// set to keep BPs (during cascaded cache creation)
	typedef std::set<BipolarPointer> BPSet;

protected:	// constants
		/// possible flags of re-checking ALL-like expressions in new nodes
	const unsigned int redoForall = 1;
	const unsigned int redoFunc = 2;
	const unsigned int redoAtMost = 4;
	const unsigned int redoIrr = 8;
		// Do NOT forget to update when new constant is added
	const unsigned int redoEverything = redoForall | redoFunc | redoAtMost | redoIrr;

protected:	// classes
		/// stack to keep BContext
	class BCStack: public TSaveStack<BranchingContext>
	{
	protected:	// members
			/// pool for OR contexts
		DeletelessAllocator<BCOr> PoolOr;
			/// pool for NN contexts
		DeletelessAllocator<BCNN> PoolNN;
			/// pool for LE contexts
		DeletelessAllocator<BCLE<DlCompletionTreeArc> > PoolLE;
			/// pool for Top-LE contexts
		DeletelessAllocator<BCLE<DlCompletionTree> > PoolTopLE;
			/// pool for Choose contexts
		DeletelessAllocator<BCChoose> PoolCh;
			/// single entry for the barrier (good for nominal reasoner)
		BCBarrier* bcBarrier;

	protected:	// methods
			/// specialise new method as the one doing nothing
		BranchingContext* createNew ( void ) override { return nullptr; }
			/// push method to use
		BranchingContext* push ( BranchingContext* p )
		{
			p->init();
			TSaveStack<BranchingContext>::push();
			this->Base[this->last-1] = p;
			return p;
		}

	public:		// interface
			/// empty c'tor
		BCStack ( void ) : bcBarrier(new BCBarrier) {}
			/// empty d'tor
		~BCStack() override
		{
			// all the members will be deleted in the resp. pools
			for ( iterator p = this->Base.begin(), p_end = this->Base.end(); p < p_end; ++p )
				*p = nullptr;
			delete bcBarrier;
		}

		// push methods

			/// get BC for Or-rule
		BranchingContext* pushOr ( void ) { return push(PoolOr.get()); }
			/// get BC for NN-rule
		BranchingContext* pushNN ( void ) { return push(PoolNN.get()); }
			/// get BC for LE-rule
		BranchingContext* pushLE ( void ) { return push(PoolLE.get()); }
			/// get BC for TopLE-rule
		BranchingContext* pushTopLE ( void ) { return push(PoolTopLE.get()); }
			/// get BC for Choose-rule
		BranchingContext* pushCh ( void ) { return push(PoolCh.get()); }
			/// get BC for the barrier
		BranchingContext* pushBarrier ( void ) { return push(bcBarrier); }

			/// clear all the pools
		void clearPools ( void )
		{
			PoolOr.clear();
			PoolNN.clear();
			PoolLE.clear();
			PoolTopLE.clear();
			PoolCh.clear();
		}
			/// clear the stack and pools
		void clear ( void ) override
		{
			clearPools();
			TSaveStack<BranchingContext>::clear();
		}
	}; // BCStack

protected:	// members
		/// host TBox
	TBox& tBox;
		/// link to dag from TBox
	DLDag& DLHeap;
		/// all the reflexive roles
	RoleMaster::TRoleVec ReflexiveRoles;

		/// manager for all the dep-sets corresponding to a graph here
	TDepSetManager Manager;
		/// Completion Graph of tested concept(s)
	DlCompletionGraph CGraph;
		/// TODO list
	ToDoTableType TODO;
		/// reasoning subsystem for the datatypes
	DataTypeReasoner DTReasoner;
		/// Used sets for pos- and neg- entries
	TFastSet<unsigned int> pUsed, nUsed;
		/// cache for testing whether it's possible to non-expand newly created node
	modelCacheIan newNodeCache;
		/// auxiliary cache that is built from the edges of newly created node
	modelCacheIan newNodeEdges;

		/// GCI-related KB flags
	const TKBFlags& GCIs;

		/// record nodes that were processed during Cascaded Cache construction
	std::set<BipolarPointer> inProcess;

		/// timer for the SAT tests (ie, cache creation)
	TsProcTimer satTimer;
		/// timer for the SUB tests (ie, general subsumption)
	TsProcTimer subTimer;
		/// timer for a single test; use it as a timeout checker
	TsProcTimer testTimer;

	// save/restore option

		/// stack for the local reasoner's state
	BCStack Stack;
		/// context from the restored branching rule
	BranchingContext* bContext = nullptr;
		/// index of last non-det situation
	unsigned int tryLevel = InitBranchingLevelValue;
		/// shift in order to determine the 1st non-det application
	unsigned int nonDetShift = 0;

	// statistic elements

#ifdef USE_REASONING_STATISTICS
	AccumulatedStatistic
		nTacticCalls,
		nUseless,

		nIdCalls,
		nSingletonCalls,
		nOrCalls,
		nOrBrCalls,
		nAndCalls,
		nSomeCalls,
		nAllCalls,
		nFuncCalls,
		nLeCalls,
		nGeCalls,

		nNNCalls,
		nMergeCalls,

		nAutoEmptyLookups,
		nAutoTransLookups,

		nSRuleAdd,
		nSRuleFire,

		nStateSaves,
		nStateRestores,
		nNodeSaves,
		nNodeRestores,

		nLookups,

		nFairnessViolations,

		// reasoning cache
		nCacheTry,
		nCacheFailedNoCache,
		nCacheFailedShallow,
		nCacheFailed,
		nCachedSat,
		nCachedUnsat;
#endif

	// current values

		/// currently processed CTree node
	DlCompletionTree* curNode = nullptr;
		/// currently processed Concept
	ConceptWDep curConcept;
		/// GCIs local to session
	std::vector<BipolarPointer> SessionGCIs;

		/// size of the DAG with some extra space
	size_t dagSize = 0;

		/// temporary array used in OR operation
	BCOr::OrIndex OrConceptsToTest;
		/// temporary array used in <= operations
	EdgeVector EdgesToMerge;
		/// nodes to merge in the TopRole-LE rules
	NodeVector NodesToMerge;
		/// contains clash set if clash is encountered in a node label
	DepSet clashSet;

	// session status flags:

		/// true if nominal-related expansion rule was fired during reasoning
	bool encounterNominal = false;
		/// flag to show if it is necessary to produce DT reasoning immediately
	bool checkDataNode = true;

protected:	// methods

		/// increment statistic counter
#ifdef USE_REASONING_STATISTICS
#	define incStat(stat) stat.inc()
#else
#	define incStat(stat)
#endif

	//-----------------------------------------------------------------------------
	// flags section
	//-----------------------------------------------------------------------------

		/// @return timeout value for a single SAT/Sub test in milliseconds; 0 means no timeout
	unsigned long getSatTimeout ( void ) const { return tBox.testTimeout; }
		/// @return true iff semantic branching is used
	bool useSemanticBranching ( void ) const { return tBox.useSemanticBranching; }
		/// @return true iff lazy blocking is used
	bool useLazyBlocking ( void ) const { return tBox.useLazyBlocking; }

		/// reset all session flags
	void resetSessionFlags ( void );

		/// adds C to the labels of NODE; @return true iff clash occurs
	bool addToDoEntry ( DlCompletionTree* node, const ConceptWDep& C, const char* reason = nullptr );
		/// synonym to the above
	bool addToDoEntry ( DlCompletionTree* node, BipolarPointer c, const DepSet& dep, const char* reason = nullptr )
		{ return addToDoEntry ( node, ConceptWDep(c,dep), reason ); }
		/// insert C to the label of NODE; do necessary updates; may return Clash in case of data entry C
	bool insertToDoEntry ( DlCompletionTree* node, const ConceptWDep& C, DagTag tag, const char* reason );
		/// if something was added to cached node N, un- or re-cache it; @return result of re-cache
	bool correctCachedEntry ( DlCompletionTree* n );

	// CGraph-wide rules support

		/// @return true if node is valid for the reasoning
	bool isNodeGloballyUsed ( const DlCompletionTree* node ) const
		{ return ! ( node->isDataNode() || node->isIBlocked() || node->isPBlocked() ); }
		/// @return true if node is valid for the reasoning
	bool isObjectNodeUnblocked ( const DlCompletionTree* node ) const
		{ return isNodeGloballyUsed(node) && !node->isDBlocked(); }
		/// add C to a set of session GCIs; init all nodes with (C,dep)
	bool addSessionGCI ( BipolarPointer C, const DepSet& dep );


		/// put TODO entry for either BP or inverse(BP) in NODE's label
	void updateName ( DlCompletionTree* node, BipolarPointer bp )
	{
		const CGLabel& lab = node->label();
		CGLabel::const_iterator p;
		for ( p = lab.begin_sc(); p != lab.end_sc(); ++p )
			if ( unlikely ( p->bp() == bp || p->bp() == inverse(bp) ) )
			{
				addExistingToDoEntry ( node, lab.getSCOffset(p), "sp" );
				break;
			}
	}
		/// re-do every BP or inverse(BP) in labels of CGraph
	void updateName ( BipolarPointer bp )
	{
		for ( DlCompletionTree* node : CGraph )
			if ( isNodeGloballyUsed(node) )
				updateName ( node, bp );
	}

	// label access interface

		/// try to add a concept to a label given by TAG; ~C can't appear in the label
	bool findConcept ( const CWDArray& lab, BipolarPointer bp );
		/// try to add a concept to a label given by TAG; ~C can't appear in the label
	bool findConcept ( const CWDArray& lab, const ConceptWDep& C ) { return findConcept ( lab, C.bp() ); }
		/// try to add a concept to a label given by TAG; ~C can't appear in the label; setup clash-set if found
	bool findConceptClash ( const CWDArray& lab, BipolarPointer bp, const DepSet& dep );
		/// try to add a concept to a label given by TAG; ~C can't appear in the label; setup clash-set if found
	bool findConceptClash ( const CWDArray& lab, const ConceptWDep& C ) { return findConceptClash ( lab, C.bp(), C.getDep() ); }
		/// check if it is possible to add a concept to a label given by TAG
	addConceptResult checkAddedConcept ( const CWDArray& lab, BipolarPointer bp, const DepSet& dep );
		/// check if C or ~C is already in LAB
	addConceptResult tryAddConcept ( const CWDArray& lab, BipolarPointer bp, const DepSet& dep );

		/** Adds ToDo entry which already exists in label of NODE. There is no need
			to add entry to label, but it is necessary to provide offset of existing concept.
			This is done by providing OFFSET of the concept in NODE's label
		 */
	void addExistingToDoEntry ( DlCompletionTree* node, int offset, const char* reason = nullptr )
	{
		const ConceptWDep& C = node->label().getConcept(offset);
		auto bp = C.bp();
		TODO.addEntry ( node, DLHeap[bp].Type(), bp, offset );
		if ( LLM.isWritable(llGTA) )
			logEntry ( node, C, reason );
	}
		/// add all elements from NODE label into TODO list
	void redoNodeLabel ( DlCompletionTree* node, const char* reason )
	{
		const CGLabel& lab = node->label();
		CGLabel::const_iterator p { };
		for ( p = lab.begin_sc(); p != lab.end_sc(); ++p )
			addExistingToDoEntry ( node, lab.getSCOffset(p), reason );
		for ( p = lab.begin_cc(); p != lab.end_cc(); ++p )
			addExistingToDoEntry ( node, lab.getCCOffset(p), reason );
	}

		/// main reasoning function
	bool checkSatisfiability ( void );
		/// perform all the actions that should be done once, after all normal rules are not applicable. @return true if the concept is unsat
	bool performAfterReasoning ( void );
		/// make sure that the DAG does not grow larger than that was recorded
	void ensureDAGSize ( void )
	{
		if ( unlikely ( dagSize < DLHeap.size() ) )
		{
			dagSize = DLHeap.maxSize();
			pUsed.ensureMaxSetSize(dagSize);
			nUsed.ensureMaxSetSize(dagSize);
		}
	}

//-----------------------------------------------------------------------------
//--		internal cache support
//-----------------------------------------------------------------------------

		/// build cache suitable for classification
	void prepareCascadedCache ( BipolarPointer p );
		/// create cache for given DAG node bu building model; @return cache
	modelCacheInterface* buildCache ( BipolarPointer p );
		/// return cache of given completion tree (implementation)
	modelCacheInterface* createModelCache ( const DlCompletionTree* p ) const
		{ return new modelCacheIan ( DLHeap, p, encounterNominal, tBox.nC, tBox.nR ); }

		/// check whether given NODE can be cached
	bool canBeCached ( DlCompletionTree* node );
		/// build cache of the node (it is known that caching is possible) in newNodeCache
	void doCacheNode ( DlCompletionTree* node );
		/// mark NODE (un)cached depending on the joint cache STATUS; @return resulting status
	modelCacheState reportNodeCached ( DlCompletionTree* node );
		/// check whether node may be (un)cached; save node if something is changed
	modelCacheState tryCacheNode ( DlCompletionTree* node )
	{
		modelCacheState ret = canBeCached(node) ? reportNodeCached(node) : csFailed;
		// node is cached if RET is csValid
		CGraph.saveRareCond(node->setCached(ret == csValid));
		return ret;
	}
		/// @return true iff cache status is invalid
	static bool usageByState ( modelCacheState status )
	{
		return status == csInvalid;
	}

//-----------------------------------------------------------------------------
//--		internal nominal reasoning interface
//-----------------------------------------------------------------------------

		/// check whether reasoning with nominals is performed
	virtual bool hasNominals ( void ) const { return false; }

		/// check satisfiability of task which is set-up
	bool runSat ( void );

	/*
	 * Tactics description;
	 *
	 * Each tactic represents (possibly combined) expansion rule for
	 * certain type of concept expression (parameter CUR).
	 *
	 * Each tactic returns:
	 * - true		- if expansion of CUR lead to clash
	 * - false		- otherwise
	 */

		/// main calling method; wrapper for Body
	bool commonTactic ( void );
		/// choose proper tactic based on type of a concept constructor
	bool commonTacticBody ( const DLVertex& cur );
		/// expansion rule for (non)primitive concept
	bool commonTacticBodyId ( const DLVertex& cur );
		/// expansion rule for (non)primitive singleton concept
	bool commonTacticBodySingleton ( const DLVertex& cur );
		/// expansion rule for conjunction
	bool commonTacticBodyAnd ( const DLVertex& cur );
		/// expansion rule for disjunction
	bool commonTacticBodyOr ( const DLVertex& cur );
		/// expansion rule for general existential quantifier
	bool commonTacticBodySome ( const DLVertex& cur );
		/// expansion rule for existential quantifier in the form \ER{nom}
	bool commonTacticBodyValue ( const TRole* R, const TIndividual* nom );
		/// expansion rule for the existential quantifier with universal role
	bool commonTacticBodySomeUniv ( const DLVertex& cur );
		/// expansion rule for universal restriction
	bool commonTacticBodyAll ( const DLVertex& cur );
		/// expansion rule for universal restriction with simple role using RA
	bool commonTacticBodyAllSimple ( const DLVertex& cur );
		/// expansion rule for universal restriction with non-simple role using RA
	bool commonTacticBodyAllComplex ( const DLVertex& cur );
		/// expansion rule for \E R{Self}
	bool commonTacticBodySomeSelf ( const TRole* R );
		/// expansion rule for \neg\E R{Self}
	bool commonTacticBodyIrrefl ( const TRole* R );
		/// expansion rule for at-least number restriction
	bool commonTacticBodyGE ( const DLVertex& cur );
		/// expansion rule for at-most number restriction
	bool commonTacticBodyLE ( const DLVertex& cur );
		/// expansion rule for choose-rule
	bool commonTacticBodyChoose ( const TRole* R, BipolarPointer C );
		/// expansion rule for functional restriction
	bool commonTacticBodyFunc ( const DLVertex& cur );
		/// expansion rule for at-most restriction in nominal node (NN-rule)
	bool commonTacticBodyNN ( const DLVertex& cur );
		/// expansion rule for auxiliary projection-construction
	bool commonTacticBodyProj ( const TRole* R, BipolarPointer C, const TRole* ProjR );

		/// expansion rule for the functional restriction with top role
	bool processTopRoleFunc ( const DLVertex& cur );
		/// expansion rule for the at-most restriction with top role
	bool processTopRoleLE ( const DLVertex& cur );
		/// expansion rule for the at-least restriction with top role
	bool processTopRoleGE ( const DLVertex& cur );

	// support for inapplicable tactics

		/// @return true iff current node is i-blocked (ie, no expansion necessary)
	bool isIBlocked ( void ) const { return curNode->isIBlocked(); }
		/// @return true iff NN-rule wrt (<= R.C) is applicable to the curNode
	bool isNNApplicable ( const TRole* r, BipolarPointer C, BipolarPointer stopper ) const;
		/// apply rule-like actions for the concept P
	bool applyExtraRules ( const TConcept* p );
		/// apply rule-like actions for the concept P if necessary
	inline
	bool applyExtraRulesIf ( const TConcept* p )
	{
		if ( !p->hasExtraRules() )
			return false;
		fpp_assert ( p->isPrimitive() );
		return applyExtraRules(p);
	}

	// support for choose-rule

		/// apply choose-rule for given range of edges
	bool applyChooseRule ( DlCompletionTree* node, BipolarPointer C );

	// support for creating/updating edge methods

		/// check whether current node is blocked
	bool isCurNodeBlocked ( void );
		/// apply all the generating rules for the (unblocked) current node
	void applyAllGeneratingRules ( DlCompletionTree* node );
		/// add C and T_G with given DEP-set to a NODE; @return DONE/CLASH
	bool initNewNode ( DlCompletionTree* node, const DepSet& dep, BipolarPointer C );
		/// apply reflexive roles to the (newly created) NODE with appropriate DEP; @return true for clash
	bool applyReflexiveRoles ( DlCompletionTree* node, const DepSet& dep );
		/// add necessary concepts to the NODE of the new edge, labelled with R
	bool initHeadOfNewEdge ( DlCompletionTree* node, const TRole* R, const DepSet& dep, const char* reason );

	// support for existential-like rules

		/// @return true iff there is R-neighbour labelled with C
	bool isSomeExists ( const TRole* R, BipolarPointer C ) const
	{
		if ( !isUsed(C) )
			return false;
		const DlCompletionTree* where = curNode->isSomeApplicable ( R, C );
		if ( where != nullptr )
			if ( LLM.isWritable(llGTA) )
				LL << " E(" << R->getName() << "," << where->getId() << "," << C << ")";
		return where != nullptr;
	}
		/// aux method for setting up new edge PA
	bool setupEdge ( DlCompletionTreeArc* pA, const DepSet& curDep, unsigned int redoFlags = 0 );
		/// aux method for creating new edge from curNode with given ROLE edge label and CONCEPT at the final label
	bool createNewEdge ( const TRole* Role, BipolarPointer Concept, unsigned int redoFlags );
		/// create new ROLE-neighbour with LEVEL to curNode; return edge to it
	DlCompletionTreeArc* createOneNeighbour ( const TRole* Role, const DepSet& dep,
											  CTNominalLevel level = BlockableLevel );

	// support for re-applying expansion rules for FORALL-like concepts in node label

		/**
			apply AR.C in and <= nR (if needed) in NODE's label where R is label of arcSample.
			Set of applicable concepts is defined by redoFlags value.
		*/
	bool applyUniversalNR ( DlCompletionTree* Node, const DlCompletionTreeArc* arcSample,
								   const DepSet& dep, unsigned int redoFlags );

	// support for branching rules

		/// check if branching rule was called for the 1st time
	bool isFirstBranchCall ( void ) const { return bContext == nullptr; }
		/// init branching context with given rule type
	void initBC ( void )
	{
		// save reasoning context
		bContext->curNode = curNode;
		bContext->curConcept = curConcept;
		bContext->branchDep = curConcept.getDep();
		bContext->pUsedIndex = pUsed.size();
		bContext->nUsedIndex = nUsed.size();
		bContext->SGsize = SessionGCIs.size();
	}
		/// clear branching context
	void clearBC ( void ) { bContext = nullptr; }

		/// create BC for Or rule
	void createBCOr ( void ) { bContext = Stack.pushOr(); initBC(); }
		/// create BC for NN-rule
	void createBCNN ( void ) { bContext = Stack.pushNN(); initBC(); }
		/// create BC for LE-rule
	void createBCLE ( void ) { bContext = Stack.pushLE(); initBC(); }
		/// create BC for LE-rule
	void createBCTopLE ( void ) { bContext = Stack.pushTopLE(); initBC(); }
		/// create BC for Choose-rule
	void createBCCh ( void ) { bContext = Stack.pushCh(); initBC(); }
		/// create BC for the barrier
	void createBCBarrier ( void ) { bContext = Stack.pushBarrier(); initBC(); }

	// support for disjunction

		/// Aux method for locating OR node characteristics; @return true if node is labelled by one of DJs
	bool planOrProcessing ( const DLVertex& cur, DepSet& dep );
		/// aux method for disjunction processing
	bool processOrEntry ( void );

	// support for (qualified) number restrictions

		/// create N R-neighbours of curNode with given Nominal LEVEL labelled with C
	bool createDifferentNeighbours ( const TRole* R, BipolarPointer C, const DepSet& dep,
											unsigned int n, CTNominalLevel level );

		/// check whether a node represents a functional one
	static bool isFunctionalVertex ( const DLVertex& v ) { return ( v.Type() == dtLE && v.getNumberLE() == 1 && v.getC() == bpTOP ); }
		/// check if ATLEAST and ATMOST entries are in clash. Both vertex MUST have dtLE type.
	bool checkNRclash ( const DLVertex& atleast, const DLVertex& atmost ) const
	{	// >= n R.C clash with <= m S.D iff...
		return (atmost.getC() == bpTOP || atleast.getC() == atmost.getC()) &&	// either D is TOP or C == D...
			   atleast.getNumberGE() > atmost.getNumberLE() &&	// and n is greater than m...
			   *atleast.getRole() <= *atmost.getRole();			// and R [= S
	}
		/// check if ATLEAST and ATMOST restrictions are in clash; setup depset from CUR
	bool isNRClash ( const DLVertex& atleast, const DLVertex& atmost, const ConceptWDep& reason );
		/// quick check whether CURNODE has a clash with a given ATMOST restriction
	bool isQuickClashLE ( const DLVertex& atmost )
	{
		for ( DlCompletionTree::const_label_iterator q = curNode->beginl_cc(), q_end = curNode->endl_cc(); q < q_end; ++q )
			if ( isNegative(q->bp())		// need at-least restriction
				 && isNRClash ( DLHeap[*q], atmost, *q ) )
				return true;
		return false;
	}
		/// quick check whether CURNODE has a clash with a given ATLEAST restriction
	bool isQuickClashGE ( const DLVertex& atleast )
	{
		for ( DlCompletionTree::const_label_iterator q = curNode->beginl_cc(), q_end = curNode->endl_cc(); q < q_end; ++q )
			if ( isPositive(q->bp())		// need at-most restriction
				 && isNRClash ( atleast, DLHeap[*q], *q ) )
				return true;
		return false;
	}
		/// init the LE processing (fill in the EdgesToMerge); @return true if the CR is satisfied
	bool initLEProcessing ( const DLVertex& cur );
		/// init the LE processing for the TopRole (fill in the NodesToMerge); @return true if the CR is satisfied
	bool initTopLEProcessing ( const DLVertex& cur );

		/// aux method that fills the dep-set for either C or ~C found in the label; @return whether C was found
	bool findChooseRuleConcept ( const CWDArray& label, BipolarPointer C, DepSet& Dep )
	{
		if ( C == bpTOP )
			return true;
		if ( findConceptClash ( label, C, Dep ) )
		{
			Dep.add(getClashSet());
			return true;
		}
		else if ( findConceptClash ( label, inverse(C), Dep ) )
		{
			Dep.add(getClashSet());
			return false;
		}
		else
			fpp_unreachable();
	}
		/// apply choose-rule for all vertices (necessary for Top role in QCR)
	bool applyChooseRuleGlobally ( BipolarPointer C );
		/// aux method which fills EdgesToMerge with *different* ROLE-neighbours of curNode
	void findNeighbours ( const TRole* Role, BipolarPointer C, DepSet& Dep );
		/// the same but for universal role
	void findCLabelledNodes ( BipolarPointer C, DepSet& Dep );
		/// aux method that checks whether clash occurs during the merge of labels
	bool checkMergeClash ( const CGLabel& from, const CGLabel& to, const DepSet& dep, unsigned int nodeId );
		/// aux method that merge FROM label to the TO node with an appropriate dep-set
	bool mergeLabels ( const CGLabel& from, DlCompletionTree* to, const DepSet& dep );
		/// merge FROM node into TO node with additional dep-set DEPF
	bool Merge ( DlCompletionTree* from, DlCompletionTree* to, const DepSet& depF );
		/// check whether clash occurs due to new edge from FROM to TO labelled with R
	bool checkDisjointRoleClash ( const DlCompletionTree* from, const DlCompletionTree* to,
										 const TRole* R, const DepSet& dep );
		/// check whether clash occurs EDGE to TO labelled with S disjoint with R
	bool checkDisjointRoleClash ( const DlCompletionTreeArc* edge, const DlCompletionTree* to,
								  const TRole* R, const DepSet& dep )
	{	// clash found
		if ( edge->getArcEnd() == to && edge->getRole()->isDisjoint(R) )
		{
			setClashSet(dep);
			updateClashSet(edge->getDep());
			return true;
		}
		return false;
	}

	// support for FORALL expansion

		/** Perform expansion of (\neg \ER.Self).DEP to an EDGE */
	bool checkIrreflexivity ( const DlCompletionTreeArc* edge,
									 const TRole* R, const DepSet& dep )
	{
		// only loops counts here...
		if ( edge->getArcEnd() != edge->getReverse()->getArcEnd() )
			return false;
		// which are labelled either with R or with R-
		if ( !edge->isNeighbour(R) && !edge->isNeighbour(R->inverse()) )
			return false;

		// set up clash
		setClashSet(dep);
		updateClashSet(edge->getDep());
		return true;
	}

		/** Perform expansion of (C=\AR{0}.X).DEP wrt RST to an EDGE with a given reason */
	bool applyTransitions ( const DlCompletionTreeArc* edge, const RAStateTransitions& RST,
								   BipolarPointer C, const DepSet& dep, const char* reason = nullptr );

	// support for the projection

		/// apply projection to given edge if necessary
	bool checkProjection ( DlCompletionTreeArc* pA, BipolarPointer C, const TRole* ProjR );

	// datatype staff

		/// @return true iff given data node contains inconsistent data constraints
	bool hasDataClash ( const DlCompletionTree* node );
		/// @return true iff given data node contains inconsistent data constraints
	bool checkDataClash ( const DlCompletionTree* node )
	{
		if ( hasDataClash(node) )
		{
			setClashSet(DTReasoner.getClashSet());
			return true;
		}
		else
			return false;
	}

	// logging actions

		/// log indentation
	void logIndentation ( void ) const;
		/// log start of processing of a ToDo entry
	void logStartEntry ( void ) const;
		/// log finish of processing of a ToDo entry
	void logFinishEntry ( bool res ) const;
		/// log the result of processing ACTION with entry (N,C{DEP})/REASON
	void logNCEntry ( const DlCompletionTree* n, const ConceptWDep& C,
					  const char* action, const char* reason ) const
	{
		CHECK_LL_RETURN(llGTA);	// useless, but safe

		LL << " " << action << "(";
		n->logNode();
		LL << "," << C.bp() << C.getDep() << ")";
		if ( reason )
			LL << reason;
	}
		/// log addition of the entry to ToDo list
	void logEntry ( const DlCompletionTree* n, const ConceptWDep& C, const char* reason ) const
		{ logNCEntry ( n, C, "+", reason ); }
		/// log clash happened during node processing
	void logClash ( const DlCompletionTree* n, const ConceptWDep& C ) const
		{ logNCEntry ( n, C, "x", DLHeap[C].getTagName() ); }
		/// write root subtree of CG with given LEVEL
	void writeRoot ( unsigned int level )
	{
		if ( LLM.isWritable(level) )
			CGraph.Print(LL);
	}

		/// merge session statistics to the global one
	void finaliseStatistic ( void );
		/// write down statistics wrt LOCAL flag
	void logStatisticData ( std::ostream& o, bool needLocal ) const;

	// save/restore methods

		/// restore local state from current branching context
	void restoreBC ( void );
		/// use this method in ALL dependency stuff (never use tryLevel directly)
	unsigned int getCurLevel ( void ) const { return tryLevel; }
		/// set new branching level (never use tryLevel directly)
	void setCurLevel ( unsigned int level ) { tryLevel = level; }
		/// @return true if no branching ops were applied during reasoners; FIXME!! doesn't work properly with a nominal cloud
	bool noBranchingOps ( void ) const { return tryLevel == InitBranchingLevelValue + nonDetShift; }
		/// Get save/restore level based on either current- or DS level
	unsigned int getSaveRestoreLevel ( const DepSet& ds ) const
	{
		// FIXME!!! see more precise it later
		if ( RKG_USE_DYNAMIC_BACKJUMPING )
			return ds.level()+1;
		else
			return getCurLevel();
	}
		/// save current reasoning state
	void save ( void );
		/// restore reasoning state to the latest saved position
	void restore ( void ) { return restore(getCurLevel()-1); }
		/// restore reasoning state to the NEWTRYLEVEL position
	void restore ( unsigned int newTryLevel );
		/// update level in N node and save it's state (if necessary)
	void updateLevel ( DlCompletionTree* n, const DepSet& ds ) { CGraph.saveNode ( n, getSaveRestoreLevel(ds) ); }
		/// finalize branching OP processing making deterministic op
	void determiniseBranchingOp ( void )
	{
		clearBC();		// clear context for the next branching op
		Stack.pop();	// remove unnecessary context from the stack
	}

	// access to global clashset, which contains result of clash during label addition

		/// get value of global dep-set
	const DepSet& getClashSet ( void ) const { return clashSet; }
		/// set value of global dep-set to D
	void setClashSet ( const DepSet& d ) { clashSet = d; }
		/// add D to global dep-set
	void updateClashSet ( const DepSet& d ) { clashSet.add(d); }
		/// get dep-set wrt current level
	DepSet getCurDepSet ( void ) const { return DepSet(Manager.get(getCurLevel()-1)); }

		/// get RW access to current branching dep-set
	DepSet& getBranchDep ( void ) { return bContext->branchDep; }
		/// get RO access to current branching dep-set
	const DepSet& getBranchDep ( void ) const { return bContext->branchDep; }
		/// prepare cumulative dep-set to usage
	void prepareBranchDep ( void ) { getBranchDep().restrict(getCurLevel()); }
		/// prepare cumulative dep-set and copy it to the general clash-set
	void useBranchDep ( void )
	{
		prepareBranchDep();
		setClashSet(getBranchDep());
	}
		/// update cumulative branch-dep with current clash-set and move options forward
	void nextBranchingOption ( void )
	{
		getBranchDep().add(getClashSet());
		bContext->nextOption();
	}

		/// restore one level (no backjumping)
	bool straightforwardRestore ( void );
		/// restore if backjumping is used
	bool backJumpedRestore ( void );
		/// restore state based on usedBackjumping flag
	bool tunedRestore ( void );

		/// check if P was used during current reasoning session
	bool isUsed ( BipolarPointer p ) const { return ( isPositive(p) ? pUsed : nUsed ).in(getValue(p)); }
		/// set P as a used during current reasoning
	void setUsed ( BipolarPointer p ) { ( isPositive(p) ? pUsed : nUsed ).add(getValue(p)); }

public:		// rule's support
		/// @return true if the rule is applicable; set the dep-set accordingly
	bool applicable ( const TBox::TSimpleRule& rule );

public:		// blocking support
		/// re-apply all the relevant expansion rules to a given unblocked NODE
	void repeatUnblockedNode ( DlCompletionTree* node, bool direct )
	{
		if ( direct )		// not blocked -- clear blocked cache
			applyAllGeneratingRules(node);		// re-apply all the generating rules
		else
			redoNodeLabel ( node, "ubi" );
	}

		/// get access to the DAG associated with it (necessary for the blocking support)
	const DLDag& getDAG ( void ) const { return tBox.DLHeap; }

public:
		/// c'tor
	explicit DlSatTester ( TBox& tbox );
		/// no copy c'tor
	DlSatTester ( const DlSatTester& ) = delete;
		/// no assignment
	DlSatTester& operator = ( const DlSatTester& ) = delete;
		/// d'tor
	virtual ~DlSatTester() = default;

		/// prepare reasoner to a new run
	virtual void prepareReasoner ( void );
		/// set-up satisfiability task for given pointers and run runSat on it
	bool runSat ( BipolarPointer p, BipolarPointer q = bpTOP );
		/// set-up role disjointness task for given roles and run SAT test
	bool checkDisjointRoles ( const TRole* R, const TRole* S );
		/// set-up role irreflexivity task for R and run SAT test
	bool checkIrreflexivity ( const TRole* R );

		/// get the ROOT node of the completion graph
	const DlCompletionTree* getRootNode ( void ) const { return CGraph.getRoot(); }

		/// set blocking method for a session
	void setBlockingMethod ( bool hasInverse, bool hasQCR ) { CGraph.setBlockingMethod ( hasInverse, hasQCR ); }

		/// build cache entry for given DAG node, using cascaded schema; @return cache
	const modelCacheInterface* createCache ( BipolarPointer p );
		/// create model cache for the just-classified entry
	modelCacheInterface* buildCacheByCGraph ( bool sat ) const
	{
		if ( sat )	// here we need actual (not a p-blocked) root of the tree
			return createModelCache(getRootNode());
		else		// unsat => cache is just bottom
			return createConstCache(bpBOTTOM);
	}

	void writeTotalStatistic ( std::ostream& o )
	{
#	ifdef USE_REASONING_STATISTICS
		AccumulatedStatistic::accumulateAll();	// ensure that the last reasoning results are in
		logStatisticData ( o, /*needLocal=*/false );
#	endif
		printBlockingStat (o);
		clearBlockingStat();
		o << "\n";
	}

		/// print SAT/SUB timings to O; @return total time spend during reasoning
	float printReasoningTime ( std::ostream& o ) const;
}; // DlSatTester

// implementation

inline void DlSatTester :: resetSessionFlags ( void )
{
	// reflect possible change of DAG size
	ensureDAGSize();

	setUsed(bpTOP);
	setUsed(bpBOTTOM);

	encounterNominal = false;
	checkDataNode = true;
}

inline bool
DlSatTester :: initNewNode ( DlCompletionTree* node, const DepSet& dep, BipolarPointer C )
{
	if ( node->isDataNode() )	// creating new data node -- do data check once in the end
		checkDataNode = false;
	node->setInit(C);
	if ( unlikely ( addToDoEntry ( node, C, dep ) ) )
		return true;
	if ( node->isDataNode() )
		return false;
	if ( unlikely ( addToDoEntry ( node, tBox.getTG(), dep ) ) )
		return true;
	if ( unlikely ( GCIs.isReflexive() && applyReflexiveRoles ( node, dep ) ) )
		return true;
	if ( unlikely ( !SessionGCIs.empty() ) )
		for ( std::vector<BipolarPointer>::iterator p = SessionGCIs.begin(), p_end = SessionGCIs.end(); p != p_end; ++p )
			if ( unlikely ( addToDoEntry ( node, *p, dep, "sg" ) ) )
				return true;

	return false;
}

inline bool
DlSatTester :: runSat ( BipolarPointer p, BipolarPointer q )
{
	prepareReasoner();

	// use general method to init node with P and add Q then
	if ( initNewNode ( CGraph.getRoot(), DepSet(), p ) ||
		 addToDoEntry ( CGraph.getRoot(), ConceptWDep(q) ) )
		return false;		// concept[s] unsatisfiable

	// check satisfiability explicitly
	TsProcTimer& timer = q == bpTOP ? satTimer : subTimer;
	timer.Start();
	bool result = runSat();
	timer.Stop();
	return result;
}

inline bool
DlSatTester :: checkDisjointRoles ( const TRole* R, const TRole* S )
{
	prepareReasoner();

	// use general method to init node...
	DepSet dummy;
	if ( initNewNode ( CGraph.getRoot(), dummy, bpTOP ) )
		return true;
	// ... add edges with R and S...
	curNode = CGraph.getRoot();
	DlCompletionTreeArc* edgeR = createOneNeighbour ( R, dummy );
	DlCompletionTreeArc* edgeS = createOneNeighbour ( S, dummy );
	// init new nodes/edges. No need to apply restrictions, as no reasoning have been done yet.
	if ( initNewNode ( edgeR->getArcEnd(), dummy, bpTOP )
		 || initNewNode ( edgeS->getArcEnd(), dummy, bpTOP )
		 || setupEdge ( edgeR, dummy )
		 || setupEdge ( edgeS, dummy )
		 || Merge ( edgeS->getArcEnd(), edgeR->getArcEnd(), dummy ) )
		return true;

	// 2 roles are disjoint if current setting is unsatisfiable
	curNode = nullptr;
	return !runSat();
}

inline bool
DlSatTester :: checkIrreflexivity ( const TRole* R )
{
	prepareReasoner();

	// use general method to init node...
	DepSet dummy;
	if ( initNewNode ( CGraph.getRoot(), dummy, bpTOP ) )
		return true;
	// ... add an R-loop
	curNode = CGraph.getRoot();
	DlCompletionTreeArc* edgeR = createOneNeighbour ( R, dummy );
	// init new nodes/edges. No need to apply restrictions, as no reasoning have been done yet.
	if ( initNewNode ( edgeR->getArcEnd(), dummy, bpTOP )
		 || setupEdge ( edgeR, dummy )
		 || Merge ( edgeR->getArcEnd(), CGraph.getRoot(), dummy ) )
		return true;

	// R is irreflexive if current setting is unsatisfiable
	curNode = nullptr;
	return !runSat();
}

// restore implementation
inline bool DlSatTester :: backJumpedRestore ( void )
{
	// if empty clash dep-set -- concept is unsatisfiable
	if ( getClashSet().empty () )
		return true;

	// some non-deterministic choices were done
	restore ( getClashSet().level() );
	return false;
}

inline bool DlSatTester :: straightforwardRestore ( void )
{
	if ( noBranchingOps() )	// no non-deterministic choices was made
		return true;		// ... the concept is unsatisfiable
	else
	{	// restoring the state
		restore ();
		return false;
	}
}

inline bool DlSatTester :: tunedRestore ( void )
{
	if ( tBox.useBackjumping )
		return backJumpedRestore ();
	else
		return straightforwardRestore ();
}

inline bool DlSatTester :: commonTacticBodyAll ( const DLVertex& cur )
{
#ifdef ENABLE_CHECKING
	fpp_assert ( isPositive(curConcept.bp()) && cur.Type() == dtForall );
#endif

	if ( unlikely(cur.getRole()->isTop()) )
	{
		incStat(nAllCalls);
		return addSessionGCI ( cur.getC(), curConcept.getDep() );
	}
	// can't skip singleton models for complex roles due to empty transitions
	if ( cur.getRole()->isSimple() )
		return commonTacticBodyAllSimple(cur);
	else
		return commonTacticBodyAllComplex(cur);
}

//-----------------------------------------------------------------------------
//--		implementation of reasoner-related parts of TBox
//-----------------------------------------------------------------------------

inline const modelCacheInterface*
TBox :: initCache ( const TConcept* pConcept, bool sub )
{
	BipolarPointer bp = sub ? inverse(pConcept->pName) : pConcept->pName;
	const modelCacheInterface* cache = DLHeap.getCache(bp);

	if ( cache == nullptr )
	{
		if ( sub )
			prepareFeatures ( nullptr, pConcept );
		else
			prepareFeatures ( pConcept, nullptr );
		cache = getReasoner()->createCache(bp);
		clearFeatures();
	}

	return cache;
}

/// test if 2 concept non-subsumption can be determined by cache merging
inline enum modelCacheState
TBox :: testCachedNonSubsumption ( const TConcept* p, const TConcept* q )
{
	const modelCacheInterface* pCache = initCache ( p, /*sub=*/false );
	const modelCacheInterface* nCache = initCache ( q, /*sub=*/true );
	return pCache->canMerge(nCache);
}

#endif
