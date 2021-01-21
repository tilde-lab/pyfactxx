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

#include "ReasonerNom.h"

//-----------------------------------------------------------------------------
//--		internal nominal reasoning interface
//-----------------------------------------------------------------------------

// register all nominals defined in TBox
void
NominalReasoner :: initNominalVector ( void )
{
	Nominals.clear();

	for ( TBox::i_iterator pi = tBox.i_begin(); pi != tBox.i_end(); ++pi )
		if ( !(*pi)->isSynonym() )
			Nominals.push_back(*pi);
}

/// prepare Nominal Reasoner to a new job
void
NominalReasoner :: prepareReasoner ( void )
{
	if ( LLM.isWritable(llSRState) )
		LL << "\nInitNominalReasoner:";

	restore(1);

	// check whether branching op is not a barrier...
	if ( dynamic_cast<BCBarrier*>(bContext) == nullptr )
	{	// replace it with a barrier
		Stack.pop();
		createBCBarrier();
	}
	// save the barrier (also remember the entry to be produced)
	save();
	// free the memory used in the pools before
	Stack.clearPools();

	// clear last session information
	resetSessionFlags();
}

bool
NominalReasoner :: consistentNominalCloud ( void )
{
	if ( LLM.isWritable(llBegSat) )
		LL << "\n--------------------------------------------\n"
			  "Checking consistency of an ontology with individuals:";
	if ( LLM.isWritable(llGTA) )
		LL << "\n";

	bool result = false;

	// reserve the root for the forthcoming reasoning
	if ( initNewNode ( CGraph.getRoot(), DepSet(), bpTOP ) ||
		 initNominalCloud() )	// clash during initialisation
		result = false;
	else	// perform a normal reasoning
		result = runSat();

	if ( result && noBranchingOps() )
	{	// all nominal cloud is classified w/o branching -- make a barrier
		if ( LLM.isWritable(llSRState) )
			LL << "InitNominalReasoner[";
		curNode = nullptr;
		createBCBarrier();
		save();
		nonDetShift = 1;	// the barrier doesn't introduce branching itself
		if ( LLM.isWritable(llSRState) )
			LL << "]";
	}

	if ( LLM.isWritable(llSatResult) )
		LL << "\nThe ontology is " << (result ? "consistent" : "INCONSISTENT");

	if ( !result )
		return false;

	// ABox is consistent -> create cache for every nominal in KB
	for ( auto& ind: Nominals )
		updateClassifiedSingleton(ind);

	return true;
}

/// create nominal nodes for all individuals in TBox
bool
NominalReasoner :: initNominalCloud ( void )
{
	// create nominal nodes and fills them with initial values
	for ( const auto& ind: Nominals )
		if ( initNominalNode(ind) )
			return true;	// ABox is inconsistent

	// create edges between related nodes
	for ( TBox::RelatedCollection::const_iterator q = tBox.RelatedI.begin(); q != tBox.RelatedI.end(); ++q, ++q )
		if ( initRelatedNominals(*q) )
			return true;	// ABox is inconsistent

	// create disjoint markers on nominal nodes
	if ( tBox.Different.empty() )
		return false;

	DepSet dummy;	// empty dep-set for the CGraph

	for ( const auto& di: tBox.Different )
	{
		CGraph.initIR();
		for ( const auto& ind: di )
			if ( CGraph.setCurIR ( resolveSynonym(ind)->node, dummy ) )	// different(c,c)
				return true;
		CGraph.finiIR();
	}

	// init was OK
	return false;
}

bool
NominalReasoner :: initRelatedNominals ( const TRelated* rel )
{
	DlCompletionTree* from = resolveSynonym(rel->a)->node;
	DlCompletionTree* to = resolveSynonym(rel->b)->node;
	TRole* R = resolveSynonym(rel->R);
	DepSet dep;	// empty dep-set

	// check if merging will lead to clash because of disjoint roles
	if ( R->isDisjoint() && checkDisjointRoleClash ( from, to, R, dep ) )
		return true;

	// create new edge between FROM and TO
	DlCompletionTreeArc* pA =
		CGraph.addRoleLabel ( from, to, /*isPredEdge=*/false, R, dep );

	// return OK iff setup new edge didn't lead to clash
	// do NOT need to re-check anything: nothing was processed yet
	return setupEdge ( pA, dep );
}
