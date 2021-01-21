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

#include <queue>
#include <iostream>
#include <fstream>

#include "Reasoner.h"
#include "DLConceptTaxonomy.h"
#include "globaldef.h"
#include "logging.h"

/********************************************************\
|* 			Implementation of class Taxonomy			*|
\********************************************************/

bool DLConceptTaxonomy :: testSub ( const TConcept* p, const TConcept* q )
{
	fpp_assert ( p != nullptr );
	fpp_assert ( q != nullptr );

//	std::cout << "Testing sub " << p->getName() << " [= " << q->getName() << std::endl;
	if ( q->isSingleton()		// singleton on the RHS is useless iff...
		 && q->isPrimitive()	// it is primitive
		 && !q->isNominal() )	// nominals should be classified as usual concepts
		return false;

	if ( LLM.isWritable(llTaxTrying) )
		LL << "\nTAX: trying '" << p->getName() << "' [= '" << q->getName() << "'... ";

	if ( tBox.testSortedNonSubsumption ( p, q ) )
	{
		if ( LLM.isWritable(llTaxTrying) )
			LL << "NOT holds (sorted result)";

		++nSortedNegative;
		return false;
	}

	if ( isNotInModule(q->getEntity()) )
	{
		if ( LLM.isWritable(llTaxTrying) )
			LL << "NOT holds (module result)";

		++nModuleNegative;
		return false;
	}

	switch ( tBox.testCachedNonSubsumption ( p, q ) )
	{
	case csValid:	// cached result: satisfiable => non-subsumption
		if ( LLM.isWritable(llTaxTrying) )
			LL << "NOT holds (cached result)";

		++nCachedNegative;
		return false;

	case csInvalid:	// cached result: unsatisfiable => subsumption holds
		if ( LLM.isWritable(llTaxTrying) )
			LL << "holds (cached result)";

		++nCachedPositive;
		return true;

	default:		// need extra tests
		if ( LLM.isWritable(llTaxTrying) )
			LL << "wasted cache test";

		break;
	}

	return testSubTBox ( p, q );
}

bool
DLConceptTaxonomy :: isNotInModule ( const TNamedEntity* entity ) const
{
	if ( upDirection )	// bottom-up phase
		return false;
	const TSignature* sig = sigStack.top();
	if ( sig && entity && !sig->contains(entity) )
		return true;
	return false;
}

TaxonomyCreator::KnownSubsumers*
DLConceptTaxonomy :: buildKnownSubsumers ( ClassifiableEntry* ce )
{
	return TaxonomyCreator::buildKnownSubsumers(ce);
}

/// prepare signature for given entry
const TSignature*
DLConceptTaxonomy :: buildSignature ( ClassifiableEntry* p )
{
	if ( tBox.pName2Sig == nullptr )
		return nullptr;
	if ( p->getEntity() == nullptr )
		return nullptr;
	TBox::NameSigMap::iterator found = tBox.pName2Sig->find(p->getEntity());
	if ( found == tBox.pName2Sig->end() )
		return nullptr;
	return found->second;
}

void DLConceptTaxonomy :: print ( std::ostream& o ) const
{
	o << "Totally " << nTries << " subsumption tests was made\nAmong them ";

	unsigned long n = ( nTries ? nTries : 1 );

	o << nPositives << " (" << nPositives*100/n << "%) successful\n";
	o << "Besides that " << nCachedPositive << " successful and " << nCachedNegative
	  << " unsuccessful subsumption tests were cached\n";
	if ( nSortedNegative )
		o << "Sorted reasoning deals with " << nSortedNegative << " non-subsumptions\n";
	if ( nModuleNegative )
		o << "Modular reasoning deals with " << nModuleNegative << " non-subsumptions\n";
	o << "There were made " << nSearchCalls << " search calls\nThere were made " << nSubCalls
	  << " Sub calls, of which " << nNonTrivialSubCalls << " non-trivial\n";
	o << "Current efficiency (wrt Brute-force) is " << nEntries*(nEntries-1)/n << "\n";

	TaxonomyCreator::print(o);
}

// Baader procedures
void
DLConceptTaxonomy :: searchBaader ( TaxonomyVertex* cur )
{
	// label 'visited'
	pTax->setVisited(cur);

	++nSearchCalls;
	bool noPosSucc = true;

	// check if there are positive successors; use DFS on them.
	for ( TaxonomyVertex::iterator p = cur->begin(upDirection), p_end = cur->end(upDirection); p != p_end; ++p )
		if ( enhancedSubs(*p) )
		{
			if ( !pTax->isVisited(*p) )
				searchBaader(*p);

			noPosSucc = false;
		}

	// in case current node is unchecked (no BOTTOM node) -- check it explicitly
	if ( !isValued(cur) )
		setValue ( cur, testSubsumption(cur) );

	// mark labelled leaf node as a parent (self check for incremental)
	if ( noPosSucc && cur->getValue() && cur != pTax->getCurrent() )
		pTax->getCurrent()->addNeighbour ( !upDirection, cur );
}

bool
DLConceptTaxonomy :: enhancedSubs1 ( TaxonomyVertex* cur )
{
	++nNonTrivialSubCalls;

	// need to be valued -- check all parents
	// propagate false
	for ( TaxonomyVertex::iterator p = cur->begin(!upDirection), p_end = cur->end(!upDirection); p != p_end; ++p )
		if ( !enhancedSubs(*p) )
			return false;

	// all subsumptions holds -- test current for subsumption
	return testSubsumption(cur);
}

bool
DLConceptTaxonomy :: testSubsumption ( TaxonomyVertex* cur )
{
	const TConcept* testC = static_cast<const TConcept*>(cur->getPrimer());
	if ( upDirection )
		return testSub ( testC, curConcept() );
	else
		return testSub ( curConcept(), testC );
}

void
DLConceptTaxonomy :: propagateOneCommon ( TaxonomyVertex* node )
{
	// checked if node already was visited this session
	if ( pTax->isVisited(node) )
		return;

	// mark node visited
	pTax->setVisited(node);
	node->setCommon();
	if ( node->correctCommon(nCommon) )
		Common.push_back(node);

	// mark all children
	for ( TaxonomyVertex::iterator p = node->begin(/*upDirection=*/false), p_end = node->end(/*upDirection=*/false); p != p_end; ++p )
		propagateOneCommon(*p);
}

bool DLConceptTaxonomy :: propagateUp ( void )
{
	// including node always have some parents (TOP at least)
	TaxonomyVertex* Current = pTax->getCurrent();
	TaxonomyVertex::iterator p = Current->begin(/*upDirection=*/true), p_end = Current->end(/*upDirection=*/true);
	fpp_assert ( p != p_end );	// there is at least one parent (TOP)

	TaxVertexVec aux;	// aux set for the vertices in ...
	nCommon = 1;	// number of common parents
	clearCommon();

	// define possible successors of the node
	propagateOneCommon(*p);
	pTax->clearVisited();

	for ( ++p; p != p_end; ++p )
	{
		if ( (*p)->noNeighbours(/*upDirection=*/false) )
			return true;
		if ( Common.empty() )
			return true;

		++nCommon;
		// now Aux contain data from previous run
		aux.swap(Common);
		Common.clear();
		propagateOneCommon(*p);
		pTax->clearVisited();

		// clear all non-common nodes (visited on a previous run)
		for ( TaxVertexVec::iterator q = aux.begin(), q_end = aux.end(); q < q_end; ++q )
			(*q)->correctCommon(nCommon);
	}

	return false;
}

/// check if no BU classification is required as C=TOP
bool
DLConceptTaxonomy :: isEqualToTop ( void )
{
	// check this up-front to avoid Sorted check's flaw wrt equals-to-top
	const modelCacheInterface* cache = tBox.initCache ( curConcept(), /*sub=*/true );
	if ( cache->getState() != csInvalid )
		return false;
	// here concept = TOP
	pTax->getCurrent()->addNeighbour ( /*upDirection=*/false, pTax->getTopVertex() );
	return true;
}

/// @return true iff curEntry is classified as a synonym
bool
DLConceptTaxonomy :: classifySynonym ( void )
{
	if ( TaxonomyCreator::classifySynonym() )
		return true;

	if ( curConcept()->isSingleton() )
	{
		TIndividual* curI = (TIndividual*)const_cast<TConcept*>(curConcept());

		if ( unlikely(tBox.isBlockedInd(curI)) )
		{	// check whether current entry is the same as another individual
			TIndividual* syn = tBox.getBlockingInd(curI);
			fpp_assert ( syn->getTaxVertex() != nullptr );

 			if ( tBox.isBlockingDet(curI) )
			{	// deterministic merge => curI = syn
 				pTax->addCurrentToSynonym(syn->getTaxVertex());
				return true;
			}
			else	// non-det merge: check whether it is the same
			{
				if ( LLM.isWritable(llTaxTrying) )
					LL << "\nTAX: trying '" << curI->getName() << "' = '" << syn->getName() << "'... ";
				if ( testSubTBox ( curI, syn ) )	// they are actually the same
				{
					pTax->addCurrentToSynonym(syn->getTaxVertex());
					return true;
				}
			}
		}
	}

	return false;
}

void 		/// fill candidates
DLConceptTaxonomy :: fillCandidates ( TaxonomyVertex* cur )
{
//	std::cout << "fill candidates: " << cur->getPrimer()->getName() << std::endl;
	if ( isValued(cur) )
	{
		if ( getValue(cur) )	// positive value -- nothing to do
			return;
	}
	else
		candidates.insert(cur);

	for ( TaxonomyVertex::iterator p = cur->begin(true), p_end = cur->end(true); p != p_end; ++p )
		fillCandidates(*p);
}

void
DLConceptTaxonomy :: reclassify ( const std::set<const TNamedEntity*>& plus, const std::set<const TNamedEntity*>& minus )
{
	MPlus = plus;
	MMinus = minus;
	pTax->deFinalise();

	// fill in an order to
	std::queue<TaxonomyVertex*> queue;
	std::vector<const ClassifiableEntry*> toProcess;
	queue.push(pTax->getTopVertex());
	while ( !queue.empty() )
	{
		TaxonomyVertex* cur = queue.front();
		queue.pop();
		if ( pTax->isVisited(cur) )
			continue;
		pTax->setVisited(cur);
		const ClassifiableEntry* entry = cur->getPrimer();
		const TNamedEntity* entity = entry->getEntity();
		if ( MPlus.find(entity) != MPlus.end() || MMinus.find(entity) != MMinus.end() )
			toProcess.push_back(entry);
		for ( TaxonomyVertex::iterator p = cur->begin(/*upDirection=*/false), p_end = cur->end(/*upDirection=*/false); p != p_end; ++p )
			queue.push(*p);
	}
	pTax->clearVisited();
//	std::cout << "Add/Del names Taxonomy:\n";
//	pTax->print(std::cout);

	for ( const ClassifiableEntry* entry : toProcess )
	{
		TaxonomyVertex* node = entry->getTaxVertex();
		const TNamedEntity* entity = entry->getEntity();
		std::cout << "Reclassify " << entity->getName() << " (" << (MPlus.count(entity) > 0 ?"Added":"") << (MMinus.count(entity) > 0 ?" Removed":"") << ")";

		TsProcTimer timer;
		timer.Start();
		reclassify ( node, (*tBox.pName2Sig)[entity] );
		timer.Stop();
		std::cout << "; reclassification time: " << timer << std::endl;
//		tax->print(std::cout);
//		std::cout.flush();
	}

	pTax->finalise();
//	print(std::cout);
//	std::cout.flush();
}

void
DLConceptTaxonomy :: reclassify ( TaxonomyVertex* node, const TSignature* s )
{
	upDirection = false;
	sigStack.push(s);
	curEntry = node->getPrimer();
	TaxonomyVertex* oldCur = pTax->getCurrent();
	pTax->setCurrent(node);

	// FIXME!! check the unsatisfiability later

	bool added = MPlus.count(curEntry->getEntity()) > 0;
	bool removed = MMinus.count(curEntry->getEntity()) > 0;
	fpp_assert ( added || removed );
	typedef std::vector<TaxonomyVertex*> TVArray;
	clearLabels();

	setValue ( pTax->getTopVertex(), true );
	if ( node->noNeighbours(true) )
		node->addNeighbour(true, pTax->getTopVertex());

	// we use candidates set if nothing was added (so no need to look further from current subs)
	useCandidates = !added;
	candidates.clear();
	if ( removed )	// re-check all parents
	{
		TVArray pos, neg;
		for ( TaxonomyVertex::iterator p = node->begin(true), p_end = node->end(true); p != p_end; ++p )
		{
			if ( isValued(*p) && getValue(*p) )
				continue;
			bool sub = testSubsumption(*p);
			if ( sub )
			{
				pos.push_back(*p);
				propagateTrueUp(*p);
			}
			else
			{
				setValue ( *p, sub );
				neg.push_back(*p);
			}
		}
		node->removeLinks(true);
//		for ( TVArray::iterator q = pos.begin(), q_end = pos.end(); q != q_end; ++q )
//			node->addNeighbour(true, *q);
		if ( useCandidates )
			for ( TaxonomyVertex* vertex : neg )
				fillCandidates(vertex);
	}
	else	// all parents are there
	{
		for ( TaxonomyVertex::iterator p = node->begin(true), p_end = node->end(true); p != p_end; ++p )
			propagateTrueUp(*p);
		node->removeLinks(true);
	}

	// FIXME!! for now. later check the equivalence etc
	setValue ( node, true );
	// the landscape is prepared
	searchBaader(pTax->getTopVertex());
	node->incorporate();
	clearLabels();
	sigStack.pop();
	pTax->setCurrent(oldCur);
}

/********************************************************\
|* 			Implementation of class TBox				*|
\********************************************************/

void TBox :: createTaxonomy ( bool needIndividual )
{
	bool needConcept = !needIndividual;

	// if there were SAT queries before -- the query (or other) concepts are there. Delete it
	clearQueryConcept();

	// here we sure that ontology is consistent
	// FIXME!! distinguish later between the 1st run and the following runs
	if ( pTax == nullptr )	// 1st run
		initTaxonomy();

	DLHeap.setSubOrder();	// init priorities in order to do subsumption tests
	pTaxCreator->setBottomUp(GCIs);
	needConcept |= needIndividual;	// together with concepts
//	else	// not a first run
//		return;	// FIXME!! now we don't perform staged reasoning, so everything is done
/*
	{
		fpp_assert ( needIndividual );
		pTax->deFinalise();
	}
*/
	if ( verboseOutput )
		std::cerr << "Processing query...";

	TsProcTimer locTimer;
	locTimer.Start();

	// calculate number of items to be classified
	unsigned int nItems = 0;

	// fills collections
	arrayCD.clear();
	arrayNoCD.clear();
	arrayNP.clear();

//	if ( needConcept )
		nItems += fillArrays ( c_begin(), c_end() );
//	if ( needIndividual || nNominalReferences > 0 )	// TODO ORE
		nItems += fillArrays ( i_begin(), i_end() );

	// taxonomy progress
	if ( pMonitor )
	{
		pMonitor->setClassificationStarted(nItems);
		pTaxCreator->setProgressIndicator(pMonitor);
	}

//	sort ( arrayCD.begin(), arrayCD.end(), TSDepthCompare() );
	classifyConcepts ( arrayCD, true, "completely defined" );
//	sort ( arrayNoCD.begin(), arrayNoCD.end(), TSDepthCompare() );
	classifyConcepts ( arrayNoCD, false, "regular" );
//	sort ( arrayNP.begin(), arrayNP.end(), TSDepthCompare() );
	classifyConcepts ( arrayNP, false, "non-primitive" );

	if ( pMonitor )
	{
		pMonitor->setFinished();
		setProgressMonitor(nullptr);	// no need of PM after classification done
		pTaxCreator->setProgressIndicator(nullptr);
	}
	pTax->finalise();

	locTimer.Stop();
	if ( verboseOutput )
		std::cerr << " done in " << locTimer << " seconds\n";

	if ( needConcept && Status < kbClassified )
		Status = kbClassified;
	if ( needIndividual )
		Status = kbRealised;

	if ( verboseOutput/* && needIndividual*/ )
	{
		std::ofstream of("Taxonomy.log");
		pTaxCreator->print(of);
	}
}

void
TBox :: classifyConcepts ( const ConceptVector& collection, bool curCompletelyDefined, const char* type )
{
	// set CD for taxonomy
	pTaxCreator->setCompletelyDefined(curCompletelyDefined);

	if ( LLM.isWritable(llStartCfyConcepts) )
		LL << "\n\n---Start classifying " << type << " concepts";

	unsigned int n = 0;

	for ( ConceptVector::const_iterator q = collection.begin(), q_end = collection.end(); q < q_end; ++q )
		// check if concept is already classified
		if ( !isCancelled() && !(*q)->isClassified () /*&& (*q)->isClassifiable(curCompletelyDefined)*/ )
		{
			classifyEntry(*q);	// need to classify concept
			if ( (*q)->isClassified() )
				++n;
		}

	if ( LLM.isWritable(llStartCfyConcepts) )
		LL << "\n---Done: " << n << " " << type << " concepts classified";
}
