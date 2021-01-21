/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2005-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef DLCOMPLETIONGRAPH_H
#define DLCOMPLETIONGRAPH_H

#include <vector>

#include "globaldef.h"
#include "DeletelessAllocator.h"
#include "dlCompletionTree.h"
#include "dlCompletionTreeArc.h"
#include "tSaveStack.h"
#include "tRareSaveStack.h"

class DlSatTester;

/**
 * Class for maintaining graph of CT nodes. Behaves like
 * deleteless allocator for nodes, plus some obvious features
 */
class DlCompletionGraph
{
protected:	// typedefs
		/// type of the heap
	typedef std::vector<DlCompletionTree*> nodeBaseType;

public:		// typedefs
		/// heap's RW iterator
	typedef nodeBaseType::iterator iterator;
		/// heap's RO iterator
	typedef nodeBaseType::const_iterator const_iterator;

protected:	// types
		/// class for S/R local state
	class SaveState
	{
	public:		// members
			/// number of valid nodes
		size_t nNodes = 0;
			/// end pointer of saved nodes
		size_t sNodes = 0;
			/// number of used edges
		size_t nEdges = 0;
	}; // SaveState

private:	// constants
		/// initial value of IR level
	static const BipolarPointer initIRLevel = 0;

private:	// members
		/// allocator for edges
	DeletelessAllocator<DlCompletionTreeArc> CTEdgeHeap;

protected:	// members
		/// heap itself
	nodeBaseType NodeBase;
		/// nodes, saved on current branching level
	nodeBaseType SavedNodes;
		/// host reasoner
	DlSatTester* pReasoner;
		/// remember the last generated ID for the node
	unsigned int nodeId = 0;
		/// index of the next unallocated entry
	size_t endUsed = 0;
		/// current branching level (synchronised with reasoner's one)
	unsigned int branchingLevel;
		/// current IR level (should be valid BP)
	BipolarPointer IRLevel;
		/// stack for rarely changed information
	TRareSaveStack RareStack;
		/// stack for usual saving/restoring
	TSaveStack<SaveState> Stack;

	// helpers for the output

		/// bitmap to remember which node was printed
	std::vector<bool> CGPFlag;
		/// indent to print CGraph nodes
	unsigned int CGPIndent = 0;

	// statistical members

		/// number of node' saves
	unsigned int nNodeSaves = 0;
		/// number of node' saves
	unsigned int nNodeRestores = 0;
		/// maximal size of the graph
	size_t maxGraphSize = 0;

	// flags

		/// how many nodes skip before block; work only with FAIRNESS
	int nSkipBeforeBlock = 0;
		/// use or not lazy blocking (ie test blocking only expanding exists)
	bool useLazyBlocking = true;
		/// whether to use Anywhere blocking as opposed to an ancestor one
	bool useAnywhereBlocking = true;

		/// check if session has inverse roles
	bool sessionHasInverseRoles = false;
		/// check if session has number restrictions
	bool sessionHasNumberRestrictions = false;

protected:	// methods
		/// init vector [B,E) with new objects T
	void initNodeArray ( iterator b, iterator e )
	{
		for ( iterator p = b; p != e; ++p )
			*p = new DlCompletionTree(nodeId++);
	}
		/// increase heap size
	void grow ( void )
	{
		NodeBase.resize(NodeBase.size()*2);
		initNodeArray ( NodeBase.begin()+NodeBase.size()/2, NodeBase.end() );
	}
		/// init root node
	void initRoot ( void )
	{
		fpp_assert ( endUsed == 0 );
		getNewNode();
	}
		/// create edge between nodes with given label and creation level; @return from->to arc
	DlCompletionTreeArc* createEdge (
		DlCompletionTree* from,
		DlCompletionTree* to,
		bool isPredEdge,
		const TRole* roleName,
		const DepSet& dep );

		/// Aux method for Merge(): add EDGE to the NODE wrt flag ISPREDEDGE and dep-set DEP
	DlCompletionTreeArc* moveEdge (
		DlCompletionTree* node,
		DlCompletionTreeArc* edge,
		bool isPredEdge, const DepSet& dep );

		/// invalidate EDGE, save restoring info
	void invalidateEdge ( DlCompletionTreeArc* edge ) { saveRareCond(edge->save()); }

	//----------------------------------------------
	// inequality relation methods
	//----------------------------------------------

		/// update IR in P with IR from Q and additional dep-set
	void updateIR ( DlCompletionTree* p, const DlCompletionTree* q, const DepSet& toAdd );

	//----------------------------------------------
	// re-building blocking hierarchy
	//----------------------------------------------

		/// check whether NODE is blocked by a BLOCKER
	bool isBlockedBy ( const DlCompletionTree* node, const DlCompletionTree* blocker ) const;
		/// check if d-blocked node is still d-blocked
	bool isStillDBlocked ( const DlCompletionTree* node ) const { return node->isDBlocked() && isBlockedBy ( node, node->Blocker ); }
		/// try to find d-blocker for a node using ancestor blocking
	void findDAncestorBlocker ( DlCompletionTree* node );
		/// try to find d-blocker for a node using anywhere blocking
	void findDAnywhereBlocker ( DlCompletionTree* node );
		/// try to find d-blocker for a node
	void findDBlocker ( DlCompletionTree* node )
	{
		saveNode ( node, branchingLevel );
		node->clearAffected();
		if ( node->isBlocked() )
			saveRareCond(node->setUBlocked());
		if ( useAnywhereBlocking )
			findDAnywhereBlocker(node);
		else
			findDAncestorBlocker(node);
	}
		/// unblock all the children of the node
	void unblockNodeChildren ( DlCompletionTree* node )
	{
		for ( DlCompletionTree::const_edge_iterator q = node->begin(), q_end = node->end(); q < q_end; ++q )
			if ( (*q)->isSuccEdge() && !(*q)->isIBlocked() && !(*q)->isReflexiveEdge() )	// all of them are i-blocked
				unblockNode ( (*q)->getArcEnd(), false );
	}
		/// mark node unblocked; unblock all the hierarchy
	void unblockNode ( DlCompletionTree* node, bool wasDBlocked );

		/// mark NODE as a d-blocked by a BLOCKER
	void setNodeDBlocked ( DlCompletionTree* node, const DlCompletionTree* blocker )
	{
		saveRareCond(node->setDBlocked(blocker));
		propagateIBlockedStatus ( node, node );
	}
		/// mark NODE as an i-blocked by a BLOCKER
	void setNodeIBlocked ( DlCompletionTree* node, const DlCompletionTree* blocker )
	{
		// nominal nodes can't be blocked
		if ( node->isPBlocked() || node->isNominalNode() )
			return;

		node->clearAffected();

		// already iBlocked -- nothing changes
		if ( node->isIBlocked() && node->Blocker == blocker )
			return;
		// prevent node to be IBlocked due to reflexivity
		if ( node == blocker )
			return;

		saveRareCond(node->setIBlocked(blocker));
		propagateIBlockedStatus ( node, blocker );
	}
		/// propagate i-blocked status to all children of NODE
	void propagateIBlockedStatus ( DlCompletionTree* node, const DlCompletionTree* blocker )
	{
		for ( DlCompletionTree::const_edge_iterator q = node->begin(), q_end = node->end(); q < q_end; ++q )
			if ( (*q)->isSuccEdge() && !(*q)->isIBlocked() )
				setNodeIBlocked ( (*q)->getArcEnd(), blocker );
	}
		/// @return true iff node might became unblocked
	bool canBeUnBlocked ( DlCompletionTree* node ) const
	{
		// in presence of inverse roles it is not enough
		// to check the affected flag for both node and its blocker
		// see tModal* for example
		if ( sessionHasInverseRoles )
			return true;
		// if node is affected -- it can be unblocked;
		// if blocker became blocked itself -- the same
		return node->isAffected() || node->isIllegallyDBlocked();
	}

	// helpers for the graph printing

		/// print proper indentation
	void PrintIndent ( std::ostream& o )
	{
		o << "\n|";
		for ( unsigned int i = 1; i < CGPIndent; ++i )
			o << " |";
	}
		/// print node of the graph with proper indentation
	void PrintNode ( const DlCompletionTree* node, std::ostream& o );
		/// print edge of the graph with proper indentation
	void PrintEdge ( DlCompletionTree::const_edge_iterator edge, const DlCompletionTree* parent, std::ostream& o );

public:		// interface
		/// c'tor: make INIT_SIZE objects
	DlCompletionGraph ( unsigned int initSize, DlSatTester* p )
		: NodeBase(initSize)
		, pReasoner(p)
		, branchingLevel(InitBranchingLevelValue)
		, IRLevel(initIRLevel)
	{
		initNodeArray ( NodeBase.begin(), NodeBase.end() );
		clearStatistics();
		initRoot();
	}
		/// no copy c'tor
	DlCompletionGraph ( const DlCompletionGraph& ) = delete;
		/// no assignment
	DlCompletionGraph& operator = ( const DlCompletionGraph& ) = delete;
		/// d'tor: delete all allocated nodes
	~DlCompletionGraph()
	{
		for ( DlCompletionTree* node : NodeBase )
			delete node;
	}

	// flag setting

		/// set flags for blocking
	void initContext ( int nSkip, bool useLB, bool useAB )
	{
		nSkipBeforeBlock = nSkip;
		useLazyBlocking = useLB;
		useAnywhereBlocking = useAB;
	}
		/// set blocking method for a session
	void setBlockingMethod ( bool hasInverse, bool hasQCR )
	{
		sessionHasInverseRoles = hasInverse;
		sessionHasNumberRestrictions = hasQCR;
	}
		/// add concept C of a type TAG to NODE; call blocking check if appropriate
	void addConceptToNode ( DlCompletionTree* node, const ConceptWDep& c, bool isComplex )
	{
		node->addConcept ( c, isComplex );

		if ( useLazyBlocking )
			node->setAffected();
		else
			detectBlockedStatus(node);
	}

	// access to nodes

		/// get a root node (non-const)
	DlCompletionTree* getRoot ( void ) { return NodeBase[0]->resolvePBlocker(); }
		/// get a root node (const)
	const DlCompletionTree* getRoot ( void ) const { return NodeBase[0]->resolvePBlocker(); }
		/// get a node by it's ID
	DlCompletionTree* getNode ( unsigned int id )
	{
		if ( id >= endUsed )
			return nullptr;
		return NodeBase[id];
	}
		/// get new node (with internal level)
	DlCompletionTree* getNewNode ( void )
	{
		if ( endUsed >= NodeBase.size() )
			grow();
		DlCompletionTree* ret = NodeBase[endUsed++];
		ret->init(branchingLevel);
		return ret;
	}

		/// begin (RO) of USED nodes
	const_iterator begin ( void ) const { return NodeBase.begin(); }
		/// end (RO) of USED nodes
	const_iterator end ( void ) const { return NodeBase.begin()+(long)endUsed; }
		/// begin (RW) of USED nodes
	iterator begin ( void ) { return NodeBase.begin(); }
		/// end (RW) of USED nodes
	iterator end ( void ) { return NodeBase.begin()+(long)endUsed; }

	// blocking

		/// detect blocked status of current node by checking whether NODE and/or its ancestors are d-blocked
	void detectBlockedStatus ( DlCompletionTree* node );
		/// update blocked status for d-blocked node
	void updateDBlockedStatus ( DlCompletionTree* node )
	{
		if ( !canBeUnBlocked(node) )
			return;
		if ( isStillDBlocked(node) )
			// FIXME!! clear affected in all children
			node->clearAffected();
		else
			detectBlockedStatus(node);
		fpp_assert ( !node->isAffected() );
	}
		/// retest every d-blocked node in the CG. Use it after the CG was build
	void retestCGBlockedStatus ( void )
	{
		bool repeat;
		iterator p, p_beg = begin(), p_end = end();
		do
		{
			for ( p = p_beg; p < p_end; ++p )
				if ( (*p)->isDBlocked() )
					updateDBlockedStatus(*p);

			// we need to repeat the thing if something became unblocked and then blocked again,
			// in case one of the blockers became blocked itself; see tModal3 for such an example
			repeat = false;
			for ( p = p_beg; p < p_end; ++p )
				if ( (*p)->isIllegallyDBlocked() )
				{
					repeat = true;
					break;
				}
		} while ( repeat );
	}

	// fairness support

		/// @ return true if a fairness constraint C is violated in one of the loops in the CGraph
	DlCompletionTree* getFCViolator ( BipolarPointer C ) const
	{
		for ( const_iterator p = begin(), p_end = end(); p < p_end; ++p )
			if ( (*p)->isDBlocked() && !(*p)->isLoopLabelled(C) )
				return const_cast<DlCompletionTree*>((*p)->Blocker);
		return nullptr;
	}

		/// clear all the session statistics
	void clearStatistics ( void )
	{
		nNodeSaves = 0;
		nNodeRestores = 0;
		if ( maxGraphSize < endUsed )
			maxGraphSize = endUsed;
	}
		/// mark all heap elements as unused
	void clear ( void )
	{
		CTEdgeHeap.clear();
		endUsed = 0;
		branchingLevel = InitBranchingLevelValue;
		IRLevel = initIRLevel;
		RareStack.clear();
		Stack.clear();
		SavedNodes.clear();
		initRoot();
	}
		/// get number of nodes in the CGraph
	size_t maxSize ( void ) const { return maxGraphSize; }

		/// save rarely appeared info if P is non-NULL
	void saveRareCond ( TRestorer* p ) { if (p) RareStack.push(p); }
		/// get the rare stack
	TRareSaveStack* getRareStack ( void ) { return &RareStack; }

	//----------------------------------------------
	// role/node
	//----------------------------------------------

		/// add role R with dep-set DEP to the label of the TO arc
	DlCompletionTreeArc* addRoleLabel (
		DlCompletionTree* from,
		DlCompletionTree* to,
		bool isPredEdge,
		const TRole* R,	// name of role (arc label)
		const DepSet& dep )	// dep-set of the arc label
	{
		// check if CGraph already has FROM->TO edge labelled with RNAME
		DlCompletionTreeArc* ret = from->getEdgeLabelled ( R, to );
		if ( ret == nullptr )
			ret = createEdge ( from, to, isPredEdge, R, dep );
		else
			saveRareCond(ret->addDep(dep));

		return ret;
	}
		/// Create an empty R-neighbour of FROM; @return an edge to created node
	DlCompletionTreeArc* createNeighbour (
		DlCompletionTree* from,
		bool isPredEdge,
		const TRole* r,		// name of role (arc label)
		const DepSet& dep )	// dep-set of the arc label
	{
		if ( RKG_USE_DYNAMIC_BACKJUMPING )
			fpp_assert ( branchingLevel == dep.level()+1 );

		return createEdge ( from, getNewNode(), isPredEdge, r, dep );
	}
		/// Create an R-loop of NODE wrt dep-set DEP; @return a loop edge
	DlCompletionTreeArc* createLoop ( DlCompletionTree* node, const TRole* r, const DepSet& dep )
	{
		return addRoleLabel ( node, node, /*isPredEdge=*/false, r, dep );
	}

		/// merge node FROM to node TO (do NOT copy label); fill EDGES with new edges added to TO
	void Merge ( DlCompletionTree* from, DlCompletionTree* to, const DepSet& toAdd,
				 std::vector<DlCompletionTreeArc*>& edges );
		/// purge node P with given ROOT and DEP-set
	void purgeNode ( DlCompletionTree* p, const DlCompletionTree* root, const DepSet& dep );
		/// purge edge E with given ROOT and DEP-set
	void purgeEdge ( DlCompletionTreeArc* e, const DlCompletionTree* root, const DepSet& dep );

	//----------------------------------------------
	// inequality relation interface
	//----------------------------------------------

		/// init new IR set
	void initIR ( void );
		/// make given NODE member of current IR set; @return true iff clash occurs
	bool setCurIR ( DlCompletionTree* node, const DepSet& ds );
		/// finalise current IR set
	void finiIR ( void );

		/// check if P and Q are in IR; if so, put the clash-set to DEP
	bool nonMergeable ( const DlCompletionTree* p, const DlCompletionTree* q, DepSet& dep ) const;

	//----------------------------------------------
	// save/restore
	//----------------------------------------------

		/// save given node wrt level
	void saveNode ( DlCompletionTree* node, unsigned int level )
	{
		if ( node->needSave(level) )
		{
			node->save(level);
			SavedNodes.push_back(node);
			++nNodeSaves;
		}
	}
		/// restore given node wrt level
	void restoreNode ( DlCompletionTree* node, unsigned int level )
	{
		if ( node->needRestore(level) )
		{
			node->restore(level);
			++nNodeRestores;
		}
	}
		/// save local state
	void save ( void );
		/// restore state for the given LEVEL
	void restore ( unsigned int level );

	// statistics

		/// get number of nodes saved during session
	unsigned int getNNodeSaves ( void ) const { return nNodeSaves; }
		/// get number of nodes restored during session
	unsigned int getNNodeRestores ( void ) const { return nNodeRestores; }

	// print

		/// print graph starting from the root
	void Print ( std::ostream& o );
}; // DlCompletionGraph

// blocking

#if defined(RKG_IR_IN_NODE_LABEL)
inline bool
DlCompletionGraph :: nonMergeable ( const DlCompletionTree* p, const DlCompletionTree* q, DepSet& dep ) const
{
	return p->nonMergeable ( q, dep );
}

inline void
DlCompletionGraph :: updateIR ( DlCompletionTree* p, const DlCompletionTree* q, const DepSet& toAdd )
{
	saveRareCond ( p->updateIR ( q, toAdd ) );
}

inline void
DlCompletionGraph :: initIR ( void )
{
	++IRLevel;
}

inline bool
DlCompletionGraph :: setCurIR ( DlCompletionTree* node, const DepSet& ds )
{
	return node->initIR ( IRLevel, ds );
}

inline void
DlCompletionGraph :: finiIR ( void ) {}

#endif	// RKG_IR_IN_NODE_LABEL

#endif
