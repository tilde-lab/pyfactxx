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

#include "dlCompletionTree.h"

/// check if transitive R-successor of the NODE labelled with C
const DlCompletionTree*
DlCompletionTree :: isTSuccLabelled ( const TRole* R, BipolarPointer C ) const
{
	if ( isLabelledBy(C) )
		return this;
	// don't check nominal nodes (prevent cycles)
	if ( isNominalNode() )
		return nullptr;

	// check all other successors
	const DlCompletionTree* ret = nullptr;
	for ( const_edge_iterator p = begin(), p_end = end(); p < p_end; ++p )
		if ( (*p)->isSuccEdge() &&
			 (*p)->isNeighbour(R) &&
			 !(*p)->isReflexiveEdge() &&	// prevent cycles
			 (ret = (*p)->getArcEnd()->isTSuccLabelled(R,C)) != nullptr )
			return ret;

	// not happens
	return nullptr;
}

/// check if transitive R-predecessor of the NODE labelled with C; skip FROM node
const DlCompletionTree*
DlCompletionTree :: isTPredLabelled ( const TRole* R, BipolarPointer C, const DlCompletionTree* from ) const
{
	if ( isLabelledBy(C) )
		return this;
	// don't check nominal nodes (prevent cycles)
	if ( isNominalNode() )
		return nullptr;

	// check all other successors
	const DlCompletionTree* ret = nullptr;
	for ( const_edge_iterator p = begin(), p_end = end(); p < p_end; ++p )
		if ( (*p)->isSuccEdge() && (*p)->isNeighbour(R) && (*p)->getArcEnd() != from &&
			 (ret = (*p)->getArcEnd()->isTSuccLabelled(R,C)) != nullptr )
			return ret;

	// check predecessor
	if ( hasParent() && isParentArcLabelled(R) )
		return getParentNode()->isTPredLabelled ( R, C, this );
	else
		return nullptr;
}

const DlCompletionTree*
DlCompletionTree :: isNSomeApplicable ( const TRole* R, BipolarPointer C ) const
{
	for ( DlCompletionTree::const_edge_iterator p = begin(), p_end = end(); p < p_end; ++p )
		if ( (*p)->isNeighbour(R) && (*p)->getArcEnd()->isLabelledBy(C) )
			return (*p)->getArcEnd();	// already contained such a label

	return nullptr;
}

const DlCompletionTree*
DlCompletionTree :: isTSomeApplicable ( const TRole* R, BipolarPointer C ) const
{
	const DlCompletionTree* ret = nullptr;

	for ( DlCompletionTree::const_edge_iterator p = begin(), p_end = end(); p < p_end; ++p )
		if ( (*p)->isNeighbour(R) )
		{
			if ( (*p)->isPredEdge() )
				ret = (*p)->getArcEnd()->isTPredLabelled(R,C,this);
			else
				ret = (*p)->getArcEnd()->isTSuccLabelled(R,C);

			if ( ret )
				return ret;	// already contained such a label
		}

	return nullptr;
}

#ifdef RKG_IR_IN_NODE_LABEL
	//----------------------------------------------
	// inequality relation methods
	//----------------------------------------------

// check if IR for the node contains C
bool DlCompletionTree :: inIRwithC ( const ConceptWDep& C, DepSet& dep ) const
{
	if ( IR.empty() )
		return false;

	for ( const auto& cwd: IR )
		if ( cwd.bp() == C.bp() )
		{
			dep += cwd.getDep();
			dep += C.getDep();
			return true;
		}

	return false;
}

// check if the NODE's and current node's IR are labeled with the same level
bool DlCompletionTree :: nonMergeable ( const DlCompletionTree* node, DepSet& dep ) const
{
	if ( IR.empty() || node->IR.empty() )
		return false;

	for ( const auto& cwd: node->IR )
		if ( inIRwithC ( cwd, dep ) )
			return true;

	return false;
}

/// update IR of the current node with IR from NODE and additional clash-set; @return restorer
TRestorer* DlCompletionTree :: updateIR ( const DlCompletionTree* node, const DepSet& toAdd )
{
	if ( node->IR.empty() )
		return nullptr;	// nothing to do

	// save current state
	TRestorer* ret = new IRRestorer(this);

	// copy all elements from NODE's IR to current node.
	// FIXME!! do not check if some of them are already in there
	for ( const auto& cwd: node->IR )
		IR.emplace_back ( cwd, toAdd );

	return ret;
}
#endif // RKG_IR_IN_NODE_LABEL

// saving/restoring
void DlCompletionTree :: save ( SaveState* nss ) const
{
	nss->curLevel = curLevel;
	nss->nNeighbours = Neighbour.size();
	Label.save(nss->lab);

	logSRNode("SaveNode");
}

void DlCompletionTree :: restore ( SaveState* nss )
{
	if ( nss == nullptr )
		return;

	// level restore
	curLevel = nss->curLevel;

	// label restore
	Label.restore ( nss->lab, getCurLevel() );

	// remove new neighbours
	if ( RKG_USE_DYNAMIC_BACKJUMPING )
	{	// FIXME!! do nothing for now
//	for ( int j = Neighbour.size()-1; j >= 0; --j )
//		if ( Neighbour[j]->getNode()->creLevel <= getCurLevel() )
//		{
//			Neighbour.resize(j+1);
//			break;
//		}
	}
	else
		Neighbour.resize(nss->nNeighbours);

	// it's cheaper to dirty affected flag than to consistently save nodes
	affected = true;

	// clear memory
	delete nss;

	logSRNode("RestNode");
}

// output
void DlCompletionTree :: PrintBody ( std::ostream& o ) const
{
	o << id;
	if ( isNominalNode() )
		o << "o" << getNominalLevel();
	o << '(' << getCurLevel() << ')';

	// data node?
	if ( isDataNode() )
		o << "d";

	// label (if any)
	Label.print(o);

	// blocking status information
	logNodeBStatus(o);
}
