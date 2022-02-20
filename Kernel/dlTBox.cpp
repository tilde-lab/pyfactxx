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

#include "dlTBox.h"

#include <fstream>

#include "cppi.h"

#include "globaldef.h"
#include "ReasonerNom.h"
#include "DLConceptTaxonomy.h"
#include "procTimer.h"
#include "dumpLisp.h"
#include "logging.h"

// uncomment the following line to print currently checking subsumption
//#define FPP_DEBUG_PRINT_CURRENT_SUBSUMPTION

TBox :: TBox ( const ifOptionSet* Options, const std::string& TopORoleName, const std::string& BotORoleName, const std::string& TopDRoleName, const std::string& BotDRoleName )
	: DLHeap(Options)
	, pOptions (Options)
	, Concepts("concept")
	, Individuals("individual")
	, ORM ( /*data=*/false, TopORoleName, BotORoleName )
	, DRM ( /*data=*/true, TopDRoleName, BotDRoleName )
	, Axioms(*this)
{
	readConfig ( Options );
	initTopBottom ();

	setForbidUndefinedNames(false);
}

TBox :: ~TBox()
{
	// remove all RELATED structures
	for ( RelatedCollection::iterator p = RelatedI.begin(), p_end = RelatedI.end(); p < p_end; ++p )
		delete *p;

	// remove all simple rules
	for ( TSimpleRules::iterator q = SimpleRules.begin(), q_end = SimpleRules.end(); q < q_end; ++q )
		delete *q;

	// empty R-C cache
	for ( TRCCache::iterator r = RCCache.begin(), r_end = RCCache.end(); r < r_end; ++r )
		deleteTree(r->first);

	// remove all concepts
	delete pTop;
	delete pBottom;
	delete pTemp;
	delete pQuery;

	// remove aux structures
	delete stdReasoner;
	delete nomReasoner;
	delete pTax;
	delete pTaxCreator;
}

/// get unique aux concept
TConcept* TBox :: getAuxConcept ( DLTree* desc )
{
	std::string name {" aux"};
	name += std::to_string(++auxConceptID);
	bool old = setForbidUndefinedNames(false);
	TConcept* C = getConcept(name);
	setForbidUndefinedNames(old);
	C->setSystem();
	C->setNonClassifiable();
	C->setPrimitive();
	C->Description = desc;
	C->initToldSubsumers();	// it is created after this is done centrally
	return C;
}

/// replace (AR:C) with X such that C [= AR^-:X for fresh X. @return X
TConcept*
TBox :: replaceForall ( DLTree* RC )
{
	// check whether we already did this before for given R,C
	TConcept* X = getRCCache(RC);

	if ( X != nullptr )
	{
		deleteTree(RC);
		return X;
	}

	// see R and C at the first time
	X = getAuxConcept();
	DLTree* C = createSNFNot(clone(RC->Right()));
	DLTree* Forall = createSNFForall ( createInverse(clone(RC->Left())), getTree(X) );
#ifdef RKG_DEBUG_ABSORPTION
	std::cout << "\nReplaceForall: add" << C << " [=" << Forall;
#endif
	// create ax axiom C [= AR^-.X
	addSubsumeAxiom ( C, Forall );
	// save cache for R,C
	setRCCache ( RC, X );

	return X;
}

void TBox :: initTopBottom ( void )
{
	// create BOTTOM concept
	TConcept* p = new TConcept ("BOTTOM");
	p->setBottom();
	p->setId(-1);
	p->pName = p->pBody = bpBOTTOM;
	pBottom = p;

	// create TOP concept
	p = new TConcept ("TOP");
	p->setTop();
	p->setId(-1);
	p->pName = p->pBody = bpTOP;
	p->tsDepth=1;
	p->classTag = cttTrueCompletelyDefined;
	pTop = p;

	// "fresh" concept
	p = new TConcept (" ");
	p->setId(-1);
	p->tsDepth=1;
	p->classTag = cttTrueCompletelyDefined;
	pTemp = p;

	// query concept
	p = new TConcept("FaCT++.default");
	p->setSystem();
	pQuery = p;
}

void TBox :: prepareReasoning ( void )
{
	// do the preprocessing
	Preprocess();

	// init reasoner (if not exist)
	initReasoner();

	// check if it is necessary to dump relevant part TBox
	if ( dumpQuery )
	{
		// set up relevance info
		markAllRelevant();
		std::ofstream of ( "tbox" );
		fpp_assert ( of.good() );
		dumpLisp lDump(of);
		dump(&lDump);
		clearRelevanceInfo();
	}

	// init values for SAT tests -- either cache, or consistency check
	DLHeap.setSatOrder();
}

/// prepare features for SAT(P), or SUB(P,Q) test
void TBox :: prepareFeatures ( const TConcept* pConcept, const TConcept* qConcept )
{
	auxFeatures = GCIFeatures;
	if ( pConcept != nullptr )
		updateAuxFeatures(pConcept->posFeatures);
	if ( qConcept != nullptr )
		updateAuxFeatures(qConcept->negFeatures);
	if ( auxFeatures.hasSingletons() )
		updateAuxFeatures(NCFeatures);
	curFeature = &auxFeatures;

	// set blocking method for the current reasoning session
	getReasoner()->setBlockingMethod ( isIRinQuery(), isNRinQuery() );
}

void
TBox :: buildSimpleCache ( void )
{
	// set cache for BOTTOM entry
	initConstCache(bpBOTTOM);

	// set all the caches for the temp concept
	initSingletonCache ( pTemp, /*pos=*/true );
	initSingletonCache ( pTemp, /*pos=*/false );

	// inapplicable if KB contains CGIs in any form
	if ( GCIs.isGCI() || GCIs.isReflexive() )
		return;

	// it is now safe to make a TOP cache
	initConstCache(bpTOP);

	for ( c_const_iterator c = c_begin(), cend = c_end(); c < cend; ++c )
		if ( (*c)->isPrimitive() )
			initSingletonCache ( (*c), /*pos=*/false );

	for ( i_const_iterator i = i_begin(), iend = i_end(); i < iend; ++i )
		if ( (*i)->isPrimitive() )
			initSingletonCache ( (*i), /*pos=*/false );
}

/// check if the ontology is consistent
bool
TBox :: performConsistencyCheck ( void )
{
	if ( verboseOutput )
		std::cerr << "Consistency checking...";
	TsProcTimer pt;
	pt.Start();

	buildSimpleCache();

	TConcept* test = ( NCFeatures.hasSingletons() ? *i_begin() : nullptr );
	prepareFeatures ( test, nullptr );
//	DlSatTester* Reasoner = getReasoner();
	bool ret = false;

	if ( test )
	{
		// make a cache for TOP if it is not there
		if ( DLHeap.getCache(bpTOP) == nullptr )
			initConstCache(bpTOP);

		ret = static_cast<NominalReasoner*>(nomReasoner)->consistentNominalCloud();
	}
	else
		ret = isSatisfiable(pTop);

	// setup cache for GCI
	if ( GCIs.isGCI() )
	{
		// there is no much win to have it together with special-domains-as-GCIs ATM.
//		DLHeap.setCache ( T_G, Reasoner->buildCacheByCGraph(ret) );
		DLHeap.setCache ( inverse(T_G), new modelCacheConst(/*sat=*/false) );
	}

	pt.Stop();
	consistTime = pt;
	if ( verboseOutput )
		std::cerr << " done in " << pt << " seconds\n";

	return ret;
}

bool
TBox :: isSatisfiable ( const TConcept* pConcept )
{
	fpp_assert ( pConcept != nullptr );

	// check whether we already does the test
	const modelCacheInterface* cache = DLHeap.getCache(pConcept->pName);
	if ( cache != nullptr )
		return ( cache->getState() != csInvalid );

	// logging the startpoint
	if ( LLM.isWritable(llBegSat) )
		LL << "\n--------------------------------------------\n"
			  "Checking satisfiability of '" << pConcept->getName() << "':";
	if ( LLM.isWritable(llGTA) )
		LL << "\n";

	// perform reasoning with a proper logical features
	prepareFeatures ( pConcept, nullptr );
	bool result = getReasoner()->runSat ( pConcept->resolveId(), bpTOP );
	// save cache
	DLHeap.setCache ( pConcept->pName, getReasoner()->buildCacheByCGraph(result) );
	clearFeatures();

	CHECK_LL_RETURN_VALUE(llSatResult,result);

#if 1
	LL << "\n";		// usual checking -- time is extra info
#else
	LL << " ";		// time tests -- time is necessary info
#endif

	LL << "The '" << pConcept->getName() << "' concept is ";
	if ( !result )
		LL << "un";
	LL << "satisfiable w.r.t. TBox";

	return result;
}

bool
TBox :: isSubHolds ( const TConcept* pConcept, const TConcept* qConcept )
{
	fpp_assert ( pConcept != nullptr && qConcept != nullptr );

#ifdef FPP_DEBUG_PRINT_CURRENT_SUBSUMPTION
	std::cerr << "Checking '" << pConcept->getName() << "' [= '" << qConcept->getName() << "'...";
#endif

	// logging the startpoint
	if ( LLM.isWritable(llBegSat) )
		LL << "\n--------------------------------------------\nChecking subsumption '"
		   << pConcept->getName() << " [= " << qConcept->getName() << "':";
	if ( LLM.isWritable(llGTA) )
		LL << "\n";

	// perform reasoning with a proper logical features
	prepareFeatures ( pConcept, qConcept );
	bool result = !getReasoner()->runSat ( pConcept->resolveId(), inverse(qConcept->resolveId()) );
	clearFeatures();

#ifdef FPP_DEBUG_PRINT_CURRENT_SUBSUMPTION
	std::cerr << " done\n";
#endif

	CHECK_LL_RETURN_VALUE(llSatResult,result);

#if 1
	LL << "\n";		// usual checking -- time is extra info
#else
	LL << " ";		// time tests -- time is necessary info
#endif

	LL << "The '" << pConcept->getName() << " [= " << qConcept->getName() << "' subsumption";
	if (!result)
			LL << " NOT";
	LL << " holds w.r.t. TBox";

	return result;
}

/// check that 2 individuals are the same
bool TBox :: isSameIndividuals ( const TIndividual* a, const TIndividual* b )
{
	a = resolveSynonym(a);
	b = resolveSynonym(b);
	if ( a == b )	// known synonyms
		return true;
	if ( !isIndividual(a) || !isIndividual(b) )
		throw EFaCTPlusPlus("Individuals are expected in the isSameIndividuals() query");
	if ( a->node == nullptr || b->node == nullptr )	// fresh individuals couldn't be the same
		return false;
	return a->getTaxVertex() == b->getTaxVertex();
}

/// check if 2 roles are disjoint
bool
TBox :: isDisjointRoles ( const TRole* R, const TRole* S )
{
	fpp_assert ( R != nullptr && S != nullptr );

	// object roles are disjoint with data roles
	if ( R->isDataRole() != S->isDataRole() )
		return true;
	// prepare feature that are KB features
	// FIXME!! overkill, but fine for now as it is sound
	curFeature = &KBFeatures;
	getReasoner()->setBlockingMethod ( isIRinQuery(), isNRinQuery() );
	bool result = getReasoner()->checkDisjointRoles ( R, S );
	clearFeatures();
	return result;
}

/// check if the role R is irreflexive
bool
TBox :: isIrreflexive ( const TRole* R )
{
	fpp_assert ( R != nullptr );

	// data roles are irreflexive
	if ( R->isDataRole() )
		return true;
	// prepare feature that are KB features
	// FIXME!! overkill, but fine for now as it is sound
	curFeature = &KBFeatures;
	getReasoner()->setBlockingMethod ( isIRinQuery(), isNRinQuery() );
	bool result = getReasoner()->checkIrreflexivity(R);
	clearFeatures();
	return result;
}

// load init values from config file
void TBox :: readConfig ( const ifOptionSet* Options )
{
	fpp_assert ( Options != nullptr );	// safety check

// define a macro for registering boolean option
#	define addBoolOption(name)				\
	name = Options->getBool ( #name );		\
	if ( LLM.isWritable(llAlways) )			\
		LL << "Init " #name " = " << name << "\n"

	// TBox options
	addBoolOption(useCompletelyDefined);
	addBoolOption(dumpQuery);
	addBoolOption(alwaysPreferEquals);
	addBoolOption(useSpecialDomains);
	// reasoner's options
	addBoolOption(useSemanticBranching);
	addBoolOption(useBackjumping);
	addBoolOption(useLazyBlocking);
	addBoolOption(useAnywhereBlocking);
    addBoolOption(precacheRelated);

	if ( Axioms.initAbsorptionFlags(Options->getText("absorptionFlags")) )
		throw EFaCTPlusPlus ( "Incorrect absorption flags given" );

	testTimeout = (unsigned)Options->getInt("testTimeout");
	if ( LLM.isWritable(llAlways) )
		LL << "Init testTimeout = " << testTimeout << "\n";

	PriorityMatrix.initPriorities ( Options->getText("IAOEFLG"), "IAOEFLG" );

	if ( RKG_USE_FAIRNESS )
	{
		nSkipBeforeBlock = Options->getInt("skipBeforeBlock");
		if ( LLM.isWritable(llAlways) )
			LL << "Init nSkipBeforeBlock = " << nSkipBeforeBlock << "\n";
	}
	else
		nSkipBeforeBlock = 0;

	verboseOutput = false;
#undef addBoolOption
}

/// create (and DAG-ify) query concept via its definition
TConcept*
TBox :: createQueryConcept ( const DLTree* desc )
{
	fpp_assert ( desc != nullptr );

	// make sure that an old query is gone
	clearQueryConcept();
	// create description
//	std::cerr << "Create new temp concept with description =" << desc << "\n";
	deleteTree ( makeNonPrimitive ( pQuery, clone(desc) ) );
	pQuery->setIndex(nC-1);

	return pQuery;
}

/// preprocess query concept: put description into DAG
void
TBox :: preprocessQueryConcept ( TConcept* query )
{
	// build DAG entries for the default concept
	addConceptToHeap(query);

	// gather statistics about the concept
	setConceptRelevant(query);

	// DEBUG_ONLY: print the DAG info
//	std::ofstream debugPrint ( defConceptName, std::ios::app|std::ios::out );
//	Print (debugPrint);
//	debugPrint << std::endl;

	// check satisfiability of the concept
	initCache(query);
}

/// classify query concept
void
TBox :: classifyQueryConcept ( void )
{
	// prepare told subsumers for classification; as it is non-primitive, it is not CD
	pQuery->initToldSubsumers();

	// setup taxonomy behaviour flags
	fpp_assert ( pTax != nullptr );
	pTaxCreator->setCompletelyDefined(false);	// non-primitive concept
//    printf("qz 3\n");

	// classify the concept
	pTaxCreator->classifyEntry(pQuery);
   // printf("qz 4\n");
}

/// knowledge exploration: build a model and return a link to the root
const DlCompletionTree*
TBox :: buildCompletionTree ( const TConcept* pConcept )
{
	const DlCompletionTree* ret = nullptr;
	// perform reasoning with a proper logical features
	prepareFeatures ( pConcept, nullptr );
	// turn off caching of CT nodes during reasoning
	setUseNodeCache(false);
	// do the SAT test, save the CT if satisfiable
	if ( getReasoner()->runSat ( pConcept->resolveId() ) )
		ret = getReasoner()->getRootNode();
	// turn on caching of CT nodes during reasoning
	setUseNodeCache(true);
	clearFeatures();
	return ret;
}

/// dump QUERY processing time, reasoning statistics and a (preprocessed) TBox
void
TBox :: writeReasoningResult ( std::ostream& o, float time ) const
{
	if ( nomReasoner )
	{
		o << "Query processing reasoning statistic: Nominals";
		nomReasoner->writeTotalStatistic(o);
	}
	o << "Query processing reasoning statistic: Standard";
	stdReasoner->writeTotalStatistic(o);

	// we know here whether KB is consistent
	fpp_assert ( getStatus() >= kbCChecked );
	if ( Consistent )
		o << "Required";
	else
		o << "KB is inconsistent. Query is NOT processed\nConsistency";

	float sum = preprocTime + consistTime;
	o << " check done in " << time << " seconds\nof which:\nPreproc. takes "
	  << preprocTime << " seconds\nConsist. takes " << consistTime << " seconds";

	if ( nomReasoner )
	{
		o << "\nReasoning NOM:";
		sum += nomReasoner->printReasoningTime(o);
	}
	o << "\nReasoning STD:";
	sum += stdReasoner->printReasoningTime(o);

	o << "\nThe rest takes ";
	// determine and normalize the rest
	float f = time - sum;
	if ( f < 0 )
		f = 0;
	f = ((unsigned long)(f*100))/100.f;
	o << f << " seconds\n";
	Print(o);
}

void TBox :: PrintDagEntry ( std::ostream& o, BipolarPointer p ) const
{
	fpp_assert ( isValid (p) );

	// primitive ones -- check first
	if ( p == bpTOP )
	{
		o << " *TOP*";
		return;
	}
	else if ( p == bpBOTTOM )
	{
		o << " *BOTTOM*";
		return;
	}

	// checks inversion
	if ( isNegative(p) )
	{
		o << " (not";
		PrintDagEntry ( o, inverse(p) );
		o << ")";
		return;
	}

	const DLVertex& v = DLHeap[p];

	switch ( v.Type() )
	{
	case dtTop:
		o << " *TOP*";
		return;

	case dtName:
	case dtDataType:
	case dtDataValue:
		o << ' ' << v.getConcept()->getName();
		return;

	case dtDataExpr:
		o << ' ' << *getDataEntryByBP(p)->getFacet();
		return;

	case dtIrr:
		o << " (" << v.getTagName() << ' ' << v.getRole()->getName() << ")";
		return;

	case dtAnd:
		o << " (" << v.getTagName();
		for ( DLVertex::const_iterator q = v.begin(); q != v.end(); ++q )
			PrintDagEntry ( o, *q );
		o << ")";
		return;

	case dtForall:
	case dtLE:
		o << " (" << v.getTagName();
		if ( v.Type() == dtLE )
			o << ' ' << v.getNumberLE();
		o << ' ' << v.getRole()->getName();
		PrintDagEntry ( o, v.getC() );
		o << ")";
		return;

	case dtProj:
		o << " (" << v.getTagName() << ' ' << v.getRole()->getName() << ' ';
		PrintDagEntry ( o, v.getC() );
		o << " => " << v.getProjRole()->getName() << ")";
		return;

	case dtNN:	// shouldn't appears in the expressions
	case dtChoose:
		fpp_unreachable();

	default:	// invalid value
		std::cerr << "Error printing vertex of type " << v.getTagName() << "(" << v.Type () << ")";
		fpp_unreachable();
		return;
	}
}

void TBox :: PrintConcept ( std::ostream& o, const TConcept* p ) const
{
	// print only relevant concepts
	if ( isValid(p->pName) )
	{
		o << getCTTagName(p->getClassTag());

		if ( p->isSingleton() )
			o << (p->isNominal() ? 'o' : '!');

		o << '.' << p->getName() << " [" << p->tsDepth
		  << "] " << (p->isNonPrimitive() ? "=" : "[=");

		if ( isValid (p->pBody) )
			PrintDagEntry ( o, p->pBody );

		// if you want to check correctness of translation (print following info)
		// you should comment out RemoveExtraDescription() in TBox::Preprocess()
		// but check hasSynonym assignment
		if ( p->Description != nullptr )
			o << (p->isNonPrimitive() ? "\n-=" : "\n-[=") << p->Description;

		o << "\n";
	}
}

void
TBox :: printFeatures ( void ) const
{
	KBFeatures.writeState();

	if ( LLM.isWritable(llAlways) )
		LL << "KB contains " << (GCIs.isGCI() ? "" : "NO ") << "GCIs\nKB contains "
		   << (GCIs.isReflexive() ? "" : "NO ") << "reflexive roles\nKB contains "
		   << (GCIs.isRnD() ? "" : "NO ") << "range and domain restrictions\n";
}

const TBox::SingletonVector* TBox::getIndividuals()
{
    if (nomReasoner == nullptr)
        return nullptr;
    else
        return nomReasoner->getNominals();
}

