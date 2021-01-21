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

/*******************************************************\
|* Implementation of taxonomy building for the FaCT++  *|
\*******************************************************/

#include "TaxonomyCreator.h"
#include "logging.h"

void TaxonomyCreator :: print ( std::ostream& o ) const
{
	o << "Taxonomy consists of " << nEntries << " entries\n";
	o << "            of which " << nCDEntries << " are completely defined\n\n";
	pTax->print(o);
}

void
TaxonomyCreator :: performClassification ( void )
{
	// do something before classification (tunable)
	preClassificationActions();

	++nEntries;

	if ( LLM.isWritable(llStartCfyEntry) && needLogging() )
		LL << "\n\nTAX: start classifying entry " << curEntry->getName();

	// if no classification needed -- nothing to do
	if ( immediatelyClassified() )
		return;

	// perform main classification
	generalTwoPhaseClassification();

	// create new vertex
	pTax->finishCurrentNode();

	// clear all labels
	clearLabels();
}

void TaxonomyCreator :: generalTwoPhaseClassification ( void )
{
	// Top-Down phase

	// setup TD phase (ie, identify parent candidates)
	setupTopDown();

	// run TD phase if necessary (ie, entry is completely defined)
	if ( needTopDown() )
	{
		setValue ( pTax->getTopVertex(), true );		// C [= TOP == true
		setValue ( pTax->getBottomVertex(), false );	// C [= BOT == false (caught by UNSAT)
		upDirection = false;
		runTopDown();
	}

	clearLabels();

	// Bottom-Up phase

	// run BU if necessary
	if ( needBottomUp() )
	{
		setValue ( pTax->getBottomVertex(), true );		// BOT [= C == true
		upDirection = true;
		runBottomUp();
	}

	clearLabels();
}

bool
TaxonomyCreator :: isDirectParent ( TaxonomyVertex* v ) const
{
	for ( TaxonomyVertex::const_iterator q = v->begin(/*upDirection=*/false), q_end = v->end(/*upDirection=*/false); q != q_end; ++q )
		if ( isValued(*q) && getValue(*q) )
			return false;
	return true;
}

void TaxonomyCreator :: setNonRedundantCandidates ( void )
{
	if ( LLM.isWritable(llCDConcept) && needLogging() )
	{
		if ( !curEntry->hasToldSubsumers() )
			LL << "\nTAX: TOP";
		LL << " completely defines concept " << curEntry->getName();
	}

	// test if some "told subsumer" is not an immediate TS (ie, not a border element)
	for ( ss_iterator p = told_begin(), p_end = told_end(); p < p_end; ++p )
		addPossibleParent((*p)->getTaxVertex());
}

void TaxonomyCreator :: setToldSubsumers ( void )
{
	if ( LLM.isWritable(llTSList) && needLogging() && !ksStack.top()->s_empty() )
		LL << "\nTAX: told subsumers";

	for ( ss_iterator p = told_begin(), p_end = told_end(); p < p_end; ++p )
	{
		if ( !(*p)->isClassified() )	// non-primitive/non-classifiable concept
			continue;	// safety check

		if ( LLM.isWritable(llTSList) && needLogging() )
			LL << " '" << (*p)->getName() << "'";

		propagateTrueUp((*p)->getTaxVertex());
	}

	if ( !ksStack.top()->p_empty() && LLM.isWritable(llTSList) && needLogging() )
	{
		LL << " and possibly ";

		for ( ss_iterator q = ksStack.top()->p_begin(), q_end = ksStack.top()->p_end(); q < q_end; ++q )
			LL << " '" << (*q)->getName() << "'";
	}
}

void
TaxonomyCreator :: propagateTrueUp ( TaxonomyVertex* node )
{
	// if taxonomy class already checked -- do nothing
	if ( isValued(node) )
	{
		fpp_assert ( getValue(node) );
		return;
	}

	// otherwise -- value it...
	setValue ( node, true );

	// ... and value all parents
	for ( TaxonomyVertex::iterator p = node->begin(/*upDirection=*/true), p_end = node->end(/*upDirection=*/true); p != p_end; ++p )
		propagateTrueUp(*p);
}

/// propagate the FALSE value of the KS subsumption down the hierarchy
void
TaxonomyCreator :: propagateFalseDown ( TaxonomyVertex* node )
{
	// if taxonomy class already checked -- do nothing
	if ( isValued(node) )
	{
		fpp_assert ( getValue(node) == false );
		return;
	}

	// otherwise -- value it...
	setValue ( node, false );

	// ... and value all children
	for ( TaxonomyVertex::iterator p = node->begin(/*upDirection=*/false), p_end = node->end(/*upDirection=*/false); p != p_end; ++p )
		propagateFalseDown(*p);
}

//-----------------------------------------------------------------
//--	DFS-based classification methods
//-----------------------------------------------------------------

ClassifiableEntry*
TaxonomyCreator :: prepareTS ( ClassifiableEntry* cur )
{
	// we just found that TS forms a cycle -- return stop-marker
	if ( waitStack.contains(cur) )
		return cur;

	// starting from the topmost entry
	addTop(cur);
	// true iff CUR is a reason of the cycle
	bool cycleFound = false;
	// for all the told subsumers...
	for ( ss_iterator p = told_begin(), p_end = told_end(); p < p_end; ++p )
		if ( !(*p)->isClassified() )	// need to classify it first
		{
			if ( unlikely((*p)->isNonClassifiable()) )
				continue;
			// prepare TS for *p
			ClassifiableEntry* v = prepareTS(*p);
			// if NULL is returned -- just continue
			if ( v == nullptr )
				continue;
			if ( v == cur )	// current cycle is finished, all saved in Syns
			{
				// after classification of CUR we need to mark all the Syns as synonyms
				cycleFound = true;
				// continue to prepare its classification
				continue;
			}
			else
			{
				// arbitrary vertex in a cycle: save in synonyms of a root cause
				Syns.push_back(cur);
				// don't need to classify it
				removeTop();
				// return the cycle cause
				return v;
			}
		}
	// all TS are ready here -- let's classify!
	classifyTop();
	// now if CUR is the reason of cycle mark all SYNs as synonyms
	if ( cycleFound )
	{
		TaxonomyVertex* syn = cur->getTaxVertex();
		for ( const ClassifiableEntry* entry : Syns )
			syn->addSynonym(entry);
		Syns.clear();
	}
	// here the cycle is done
	return nullptr;
}

