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

#include "dlCompletionGraph.h"

DlCompletionTreeArc*
DlCompletionGraph :: createEdge (
	DlCompletionTree* from,
	DlCompletionTree* to,
	bool isPredEdge,
	const TRole* roleName,
	const DepSet& dep )
{
	// create 2 arcs between FROM and TO
	DlCompletionTreeArc* forward = CTEdgeHeap.get();
	forward->init ( roleName, dep, to );
	forward->setSuccEdge(!isPredEdge);
	DlCompletionTreeArc* backward = CTEdgeHeap.get();
	backward->init ( roleName->inverse(), dep, from );
	backward->setSuccEdge(isPredEdge);
	// set the reverse link
	forward->setReverse(backward);

	// connect them to nodes
	saveNode ( from, branchingLevel );
	saveNode ( to, branchingLevel );
	// add the edges as corresponding neighbours
	from->addNeighbour(forward);
	to->addNeighbour(backward);
	if ( LLM.isWritable(llGTA) )
	{
		LL << " ce(";
		if ( isPredEdge )
			LL << to->getId() << "<-" << from->getId();
		else
			LL << from->getId() << "->" << to->getId();
		LL << "," << roleName->getName() << ")";
	}

	return forward;
}

/// replace the EDGE (that comes from old node to X) with a new one, that comes from NODE to X
DlCompletionTreeArc*
DlCompletionGraph :: moveEdge ( DlCompletionTree* node, DlCompletionTreeArc* edge,
								bool isPredEdge, const DepSet& dep )
{
	// skip already purged edges
	if ( edge->isIBlocked())
		return nullptr;

	// skip edges not leading to nominal nodes
	if ( !isPredEdge && !edge->getArcEnd()->isNominalNode() )
		return nullptr;

	const TRole* R = edge->getRole();

	// we shall copy reflexive edges in a specific way
	if ( edge->isReflexiveEdge() )
		return createLoop ( node, R, dep );

	DlCompletionTree* to = edge->getArcEnd();

	// invalidate old edge
	invalidateEdge(edge);

	// try to find for NODE->TO (TO->NODE) whether we
	// have TO->NODE (NODE->TO) edge already
	for ( DlCompletionTree::const_edge_iterator p = node->begin(), p_end = node->end(); p < p_end; ++p )
		if ( (*p)->getArcEnd() == to && (*p)->isPredEdge() != isPredEdge )
			return addRoleLabel ( node, to, !isPredEdge, R, dep );

	return addRoleLabel ( node, to, isPredEdge, R, dep );
}

// merge labels; see SHOIN paper for detailed description
void DlCompletionGraph :: Merge ( DlCompletionTree* from, DlCompletionTree* to,
								  const DepSet& dep,
								  std::vector<DlCompletionTreeArc*>& edges )
{
	edges.clear();

	// 1. For all x: x->FROM make x->TO
	// FIXME!! no optimisations (in case there exists an edge TO->x labelled with R-)
	// 2. For all nominal x: FROM->x make TO->x
	// FIXME!! no optimisations (in case there exists an edge x->TO labelled with R-)
	for ( DlCompletionTree::const_edge_iterator p = from->begin(), p_end = from->end(); p < p_end; ++p )
	{
		if ( (*p)->isPredEdge() || (*p)->getArcEnd()->isNominalNode() )
		{
			DlCompletionTreeArc* temp = moveEdge ( to, *p, (*p)->isPredEdge(), dep );
			if ( temp != nullptr )
				edges.push_back(temp);
		}
		if ( (*p)->isSuccEdge() )
			purgeEdge ( *p, to, dep );
	}

	// 4. For all x: FROM \neq x, add TO \neq x
	updateIR ( to, from, dep );

	// 5. Purge FROM
	purgeNode ( from, to, dep );
}

void
DlCompletionGraph :: purgeNode ( DlCompletionTree* p, const DlCompletionTree* root, const DepSet& dep )
{
	if ( p->isPBlocked() )
		return;

	saveRareCond ( p->setPBlocked ( root, dep ) );

	// update successors
	for ( DlCompletionTree::const_edge_iterator q = p->begin(); q != p->end(); ++q )
		if ( (*q)->isSuccEdge() && !(*q)->isIBlocked() )
			purgeEdge ( *q, root, dep );
}

/// purge edge E with given ROOT and DEP-set
void
DlCompletionGraph :: purgeEdge ( DlCompletionTreeArc* e, const DlCompletionTree* root, const DepSet& dep )
{
	invalidateEdge(e);	// invalidate given link
	if ( e->getArcEnd()->isBlockableNode() )
		purgeNode ( e->getArcEnd(), root, dep );	// purge blockable successor
}

// save/restore

void DlCompletionGraph :: save ( void )
{
	SaveState* s = Stack.push();
	s->nNodes = endUsed;
	s->sNodes = SavedNodes.size();
	s->nEdges = CTEdgeHeap.size();
	RareStack.incLevel();
	++branchingLevel;
}

void DlCompletionGraph :: restore ( unsigned int level )
{
	fpp_assert ( level > 0 );
	branchingLevel = level;
	RareStack.restore(level);
	SaveState* s = Stack.pop(level);
	endUsed = s->nNodes;
	size_t nSaved = s->sNodes;
	iterator p = SavedNodes.begin()+(long)nSaved, p_end = SavedNodes.end();
	if ( endUsed < size_t(p_end-p) )	// it's cheaper to restore all nodes
		for ( p = begin(), p_end = end(); p < p_end; ++p )
			restoreNode ( (*p), level );
	else
		for ( ; p < p_end; ++p )
			if ( (*p)->getId() < endUsed )	// don't restore nodes that are dead anyway
				restoreNode ( (*p), level );
	SavedNodes.resize(nSaved);
	CTEdgeHeap.resize(s->nEdges);
}

// printing CGraph

void DlCompletionGraph :: Print ( std::ostream& o )
{
	// init indentation and node labels
	CGPIndent = 0;
	std::vector<bool> temp ( endUsed, false );
	CGPFlag.swap(temp);

	const_iterator p = begin(), p_end = end();
	unsigned int i = 1;	// node id

	// mark all nominals as already printed: they full subtrees will be output with a nominal cloud
	for ( ++p; p < p_end && (*p)->isNominalNode(); ++p, ++i )
		CGPFlag[i] = true;

	// print tree starting from the root node
	p = begin();
	PrintNode ( *p, o );

	// if there are nominals in the graph -- print the nominal cloud
	for ( ++p; p < p_end && (*p)->isNominalNode(); ++p )
	{
		// print given nominal node in full
		CGPFlag[(*p)->getId()] = false;
		PrintNode ( *p, o );
	}
	o << "\n";
}

void
DlCompletionGraph :: PrintEdge ( DlCompletionTree::const_edge_iterator edge, const DlCompletionTree* parent, std::ostream& o )
{
	const DlCompletionTree* node = (*edge)->getArcEnd();
	bool succEdge = (*edge)->isSuccEdge();

	PrintIndent(o);
	for ( ; edge != parent->end(); ++edge )
		if ( (*edge)->getArcEnd() == node && (*edge)->isSuccEdge() == succEdge )
			o << " ", (*edge)->Print(o);		// print edge's label

	if ( node == parent )	// print loop
	{
		PrintIndent(o);
		o << "-loop to node " << parent->getId();
	}
	else
		PrintNode ( node, o );
}

/// print node of the graph with proper indentation
void
DlCompletionGraph :: PrintNode ( const DlCompletionTree* node, std::ostream& o )
{
	if ( CGPIndent )
	{
		PrintIndent(o);
		o << "-";
	}
	else
		o << "\n";

	node->PrintBody(o);				// print node's label

	// don't print subtree twice
	if ( CGPFlag[node->getId()] )
	{
		o << "d";
		return;
	}

	CGPFlag[node->getId()] = true;	// mark node printed

	// we want to print incoming edges for the nominal cloud
	bool wantPred = node->isNominalNode();

	// print all children
	++CGPIndent;
	for ( DlCompletionTree::const_edge_iterator p = node->begin(); p != node->end(); ++p )
		if ( (*p)->isSuccEdge() || ( wantPred && (*p)->getArcEnd()->isNominalNode() ) )
			PrintEdge ( p, node, o );
	--CGPIndent;
}
