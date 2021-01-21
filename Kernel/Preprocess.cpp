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

#include <fstream>
#include <iostream>

#include "dlTBox.h"

#include "procTimer.h"
#include "logging.h"

//#define DEBUG_PREPROCESSING

#ifdef DEBUG_PREPROCESSING
#	define BEGIN_PASS(str) std::cerr << "\n" str "... "
#	define END_PASS() std::cerr << "done"
#else
#	define BEGIN_PASS(str)
#	define END_PASS()
#endif

void TBox :: Preprocess ( void )
{
	if ( verboseOutput )
		std::cerr << "Preprocessing...";
	TsProcTimer pt;
	pt.Start();

	// builds role hierarchy
	BEGIN_PASS("Build role hierarchy");
	ORM.initAncDesc();
	DRM.initAncDesc();
	END_PASS();

	if ( verboseOutput )
	{
		std::ofstream oroles("Taxonomy.ORoles");
		ORM.getTaxonomy()->print(oroles);
		std::ofstream droles("Taxonomy.DRoles");
		DRM.getTaxonomy()->print(droles);
	}

	// all concept descriptions contains synonyms. Remove them now
	BEGIN_PASS("Replace synonyms in expressions");
	if ( countSynonyms() > 0 )
		replaceAllSynonyms();
	END_PASS();

	// preprocess Related structure (before classification tags are defined)
	BEGIN_PASS("Preprocess related axioms");
	preprocessRelated();
	END_PASS();

	// FIXME!! find a proper place for this
	TransformExtraSubsumptions();

	// init told subsumers as they would be used soon
	BEGIN_PASS("Init told subsumers");
	initToldSubsumers();
	END_PASS();

	// locate told (definitional) cycles and transform them into synonyms
	BEGIN_PASS("Detect and replace told cycles");
	transformToldCycles();
	END_PASS();

	// detect singleton with singleton parents and make them synonyms
	BEGIN_PASS("Detect and transform singleton hierarchy");
	transformSingletonHierarchy();
	END_PASS();

	// absorb axioms (move some Axioms to Role and Concept Description)
	BEGIN_PASS("Perform absorption");
	AbsorbAxioms();
	END_PASS();

	// set told TOP concepts whether necessary
	BEGIN_PASS("Set told TOP");
	setToldTop();
	END_PASS();

	// no more axiom transformations allowed

	// create DAG (concept normalisation etc)
	BEGIN_PASS("Build DAG");
	buildDAG();
	END_PASS();

	// fills classification tag (strictly after told cycles)
	BEGIN_PASS("Detect classification tags");
	fillsClassificationTag();
	END_PASS();

	// set up TS depth
	BEGIN_PASS("Calculate told subsumer depth");
	calculateTSDepth();
	END_PASS();

	// set indexes for model caching
	BEGIN_PASS("Set all indexes");
	setAllIndexes();
	END_PASS();

	// create sorts for KB
	BEGIN_PASS("Determine sorts");
	determineSorts();
	END_PASS();

	// calculate statistic for the whole KB:
	BEGIN_PASS("Gather relevance info");
	gatherRelevanceInfo();
	END_PASS();

	// here it is safe to print KB features (all are known; the last one was in Relevance)
	printFeatures();

	// GALEN-like flag is known here, so we can set OR defaults
	BEGIN_PASS("Set defaults for OR orderings");
	DLHeap.setOrderDefaults (
		isGalenLikeTBox() ? "Fdn" : isWineLikeTBox() ? "Sdp" : "Sap",	// SAT settings
		isGalenLikeTBox() ? "Ban" : isWineLikeTBox() ? "Fdn" : "Dap"	// SUB settings
		);
	END_PASS();

	// now we can gather DAG statistics (if necessary)
	BEGIN_PASS("Gather usage statistics");
	DLHeap.gatherStatistic();
	END_PASS();

	// calculate statistic on DAG and Roles
	BEGIN_PASS("Gather concept-related statistics");
	CalculateStatistic();
	END_PASS();

	// free extra memory
	BEGIN_PASS("Free unused memory");
	RemoveExtraDescriptions();
	END_PASS();

	pt.Stop();
	preprocTime = pt;
	if ( verboseOutput )
		std::cerr << " done in " << pt << " seconds\n";
}

static bool
replaceSynonymsFromTree ( DLTree* desc )
{
	if ( desc == nullptr )
		return false;

	if ( isName(desc) )
	{
		TLexeme& cur = desc->Element();	// not const
		ClassifiableEntry* entry = static_cast<ClassifiableEntry*>(cur.getNE());

		if ( entry->isSynonym() )
		{
			entry = resolveSynonym(entry);
			// check for TOP/BOTTOM
			if ( entry->isTop() )
				cur = TLexeme(TOP);
			else if ( entry->isBottom() )
				cur = TLexeme(BOTTOM);
			else
				cur = TLexeme ( static_cast<TConcept*>(entry)->isSingleton() ? INAME : CNAME, entry );
			return true;
		}
		else
			return false;
	}
	else
	{
		bool ret = replaceSynonymsFromTree ( desc->Left() );
		ret |= replaceSynonymsFromTree ( desc->Right() );
		return ret;
	}
}

void TBox :: replaceAllSynonyms ( void )
{
	// replace synonyms in role's domain
	for ( const auto& oRole: ORM )
		if ( !oRole->isSynonym() )
			replaceSynonymsFromTree ( oRole->getTDomain() );
	for ( const auto& dRole: DRM )
		if ( !dRole->isSynonym() )
			replaceSynonymsFromTree ( dRole->getTDomain() );

	for ( c_iterator pc = c_begin(); pc != c_end(); ++pc )
		if ( replaceSynonymsFromTree ( (*pc)->Description ) )
			(*pc)->initToldSubsumers();
	for ( i_iterator pi = i_begin(); pi != i_end(); ++pi )
		if ( replaceSynonymsFromTree ( (*pi)->Description ) )
			(*pi)->initToldSubsumers();
}

void TBox :: preprocessRelated ( void )
{
	for ( auto& related: RelatedI )
		related->simplify();
}

void TBox :: transformToldCycles ( void )
{
	// remember number of synonyms appeared in KB
	unsigned int nSynonyms = countSynonyms();

	clearRelevanceInfo();
	for ( c_iterator pc = c_begin(); pc != c_end(); ++pc )
		if ( !(*pc)->isSynonym() )
			checkToldCycle(*pc);

	for ( i_iterator pi = i_begin(); pi != i_end(); ++pi )
		if ( !(*pi)->isSynonym() )
			checkToldCycle(*pi);
	clearRelevanceInfo();

	// update number of synonyms
	nSynonyms = countSynonyms() - nSynonyms;
	if ( nSynonyms )
	{
		if ( LLM.isWritable(llAlways) )
			LL << "\nTold cycle elimination done with " << nSynonyms << " synonyms created";

		replaceAllSynonyms();
	}
}

TConcept* TBox :: checkToldCycle ( TConcept* p )
{
	fpp_assert ( p != nullptr );	// safety check

	// resolve synonym (if happens) to prevent cases like A[=B[=C[=A, A[=D[=B
	p = resolveSynonym(p);

	// no reason to process TOP here
	if ( p == pTop )
		return nullptr;

	// if we found a cycle...
	if ( CInProcess.find(p) != CInProcess.end() )
	{
//		std::cout << "Cycle with " << p->getName() << std::endl;
		return p;
	}

	if ( isRelevant(p) )
	{
//		std::cout << "Already checked: " << p->getName() << std::endl;
		return nullptr;
	}

	TConcept* ret = nullptr;

	// add concept in processing
	CInProcess.insert(p);

redo:

//	std::cout << "Start from " << p->getName() << std::endl;

	for ( auto r: p->told() )
		// if cycle was detected
		if ( (ret = checkToldCycle(static_cast<TConcept*>(r))) != nullptr )
		{
			if ( ret == p )
			{
//				std::cout << "Fill cycle with " << p->getName() << std::endl;
				ToldSynonyms.push_back(p);

				// find a representative for the cycle; nominal is preferable
				for ( auto& rep: ToldSynonyms )
					if ( rep->isSingleton() )
						p = rep;
				// now p is a representative for all the synonyms

				// fill the description
				DLTree* desc = nullptr;
				for ( auto& syn: ToldSynonyms )
					if ( syn != p )	// make it a synonym of RET, save old desc
					{
						desc = createSNFAnd ( desc, makeNonPrimitive ( syn, getTree(p) ) );
						// check whether we had an extra definition for Q
						ConceptDefMap::iterator extra = ExtraConceptDefs.find(syn);
						if ( extra != ExtraConceptDefs.end() )
						{
							desc = createSNFAnd ( desc, extra->second );
							ExtraConceptDefs.erase(extra);
						}
					}

				ToldSynonyms.clear();

				// mark the returned concept primitive (to allow addDesc to work)
				p->setPrimitive();
				p->addDesc(desc);

				// replace all synonyms with TOP
				p->removeSelfFromDescription();

				// re-run the search starting from new sample
				if ( ret != p )	// need to fix the stack
				{
					CInProcess.erase(ret);
					CInProcess.insert(p);
					ret->setRelevant(relevance);
					p->dropRelevant(relevance);
				}

				ret = nullptr;
				goto redo;
			}
			else
			{
				ToldSynonyms.push_back(p);
				// no need to continue; finish with this cycle first
				break;
			}
		}

	// remove processed concept from set
	CInProcess.erase(p);

	p->setRelevant(relevance);
//	std::cout << "Done with " << p->getName() << std::endl;

	return ret;
}

/// transform i [= C [= j into i=C=j for i,j nominals
void
TBox :: transformSingletonHierarchy ( void )
{
	// remember number of synonyms appeared in KB
	unsigned int nSynonyms = countSynonyms();

	// cycle until no new synonyms are created
	bool changed;

	do
	{
		changed = false;

		for ( i_iterator pi = i_begin(); pi != i_end(); ++pi )
			if ( !(*pi)->isSynonym() && (*pi)->isHasSP() )
			{
				TIndividual* i = transformSingletonWithSP(*pi);
				i->removeSelfFromDescription();
				changed = true;
			}
	} while ( changed );

	// update number of synonyms
	nSynonyms = countSynonyms() - nSynonyms;
	if ( nSynonyms )
		replaceAllSynonyms();
}

/// helper to the transformSingletonWithSP() function
TIndividual*
TBox :: getSPForConcept ( TConcept* p )
{
	for ( auto r: p->told() )
	{
		TConcept* i = static_cast<TConcept*>(r);
		if ( i->isSingleton() )	// found the end of the chain
			return static_cast<TIndividual*>(i);
		if ( i->isHasSP() )		// found the continuation of the chain
			return transformSingletonWithSP(i);
	}
	// will always found the entry
	fpp_unreachable();
}

/// make P and all its non-singleton parents synonyms to its singleton parent
TIndividual*
TBox :: transformSingletonWithSP ( TConcept* p )
{
	TIndividual* i = getSPForConcept(p);

	// make p a synonym of i
	if ( p->isSingleton() )
		i->addRelated(static_cast<TIndividual*>(p));
	addSubsumeAxiom ( i, makeNonPrimitive ( p, getTree(i) )	);

	return i;
}

/// @return true if C is referenced in TREE; use PROCESSED to record explored names
bool
TBox :: isReferenced ( TConcept* C, DLTree* tree, ConceptSet& processed ) const
{
	fpp_assert ( tree != nullptr );
	switch ( tree->Element().getToken() )
	{
	// names
	case CNAME:
	case INAME:
	{
		TConcept* D = toConcept(tree->Element().getNE());
		// check whether we found cycle
		if ( C == D )
			return true;
		// check if we already processed D
		if ( processed.count(D) > 0 )
			return false;
		// recurse here
		return isReferenced ( C, D, processed );
	}

	// binary concept operations
	case AND:
	case OR:
		return isReferenced ( C, tree->Left(), processed ) || isReferenced ( C, tree->Right(), processed );

	// operations with a single concept
	case NOT:
		return isReferenced ( C, tree->Left(), processed );
	case EXISTS:
	case FORALL:
	case GE:
	case LE:
		return isReferenced ( C, tree->Right(), processed );

	// operations w/o concept
	case SELF:
	case TOP:
	case BOTTOM:
		return false;

	// non-concept expressions: should not be here
	case INV:
	case RCOMPOSITION:	// role composition
	case PROJINTO:		// role projection into
	case PROJFROM:		// role projection from
	case DATAEXPR:	// any data expression: data value, [constrained] datatype
	case RNAME:
	case DNAME:
		fpp_unreachable();

	default:	// just for safety: all possible options were checked
		fpp_unreachable();
	}
}

/// transform C [= E with C = D into GCIs
void
TBox :: TransformExtraSubsumptions ( void )
{
	auto p = ExtraConceptDefs.begin(), p_end = ExtraConceptDefs.end();

	while ( p != p_end )
	{
		TConcept* C = p->first;
		DLTree* E = p->second;
		// for every C here we have C = D in KB and C [= E in ExtraConceptDefs
		// if there is a cycle for C
		if ( isCyclic(C) )
		{
			DLTree* D = clone(C->Description);
			// then we should make C [= (D and E) and go with GCI D [= C
			makeDefinitionPrimitive ( C, E, D );
		}
		else	// it is safe to keep definition C = D and go with GCI C [= E
			processGCI ( getTree(C), E );
		// remove processed entry from the set, reset the pointer
		p = ExtraConceptDefs.erase(p);
	}
}

void
TBox :: setAllIndexes ( void )
{
	++nC;	// place for the query concept
	nR = 0;	// start with 1 to make index 0 an indicator of "not processed"
	auto setRoleIndex = [&] (TRole* R) { if ( !R->isSynonym() ) R->setIndex(++nR); };
	std::for_each ( ORM.begin(), ORM.end(), setRoleIndex );
	std::for_each ( DRM.begin(), DRM.end(), setRoleIndex );
	// make nR be a number of indexed roles
	++nR;
}

/// determine all sorts in KB (make job only for SORTED_REASONING)
void TBox :: determineSorts ( void )
{
#ifdef RKG_USE_SORTED_REASONING
	// Related individuals does not appears in DLHeap,
	// so their sorts shall be determined explicitly
	for ( RelatedCollection::const_iterator p = RelatedI.begin(), p_end = RelatedI.end(); p < p_end; ++p, ++p )
		DLHeap.updateSorts ( (*p)->a->pName, (*p)->R, (*p)->b->pName );

	// simple rules needs the same treatment
	for ( const auto& rule: SimpleRules )
	{
		mergeableLabel& lab = DLHeap[rule->bpHead].getSort();
		for ( const auto& atom: rule->Body )
			DLHeap.merge ( lab, atom->pName );
	}

	// create sorts for concept and/or roles
	DLHeap.determineSorts ( ORM, DRM );
#endif // RKG_USE_SORTED_REASONING
}

// Told stuff is used here, so run this AFTER fillTold*()
void TBox :: CalculateStatistic ( void )
{
	// skip all statistic if no logging needed
	CHECK_LL_RETURN(llAlways);

	unsigned int npFull = 0, nsFull = 0;		// number of completely defined concepts
	unsigned int nPC = 0, nNC = 0, nSing = 0;	// number of primitive, non-prim and singleton concepts
	unsigned int nNoTold = 0;	// number of concepts w/o told subsumers

	auto getCStat = [&] (const TConcept* C) {
		if ( isValid(C->pName) )
		{
			if ( C->isSingleton() )
				++nSing;
			if ( C->isPrimitive() )
				++nPC;
			else if ( C->isNonPrimitive() )
				++nNC;

			if ( C->isSynonym () )
				++nsFull;

			if ( C->isCompletelyDefined() )
			{
				if ( C->isPrimitive() )
					++npFull;
			}
			else
				if ( !C->hasToldSubsumers() )
					++nNoTold;
		}
	};
	// calculate statistic for all concepts
	std::for_each ( c_begin(), c_end(), getCStat );
	// calculate statistic for all individuals
	std::for_each ( i_begin(), i_end(), getCStat );
	// output all statistics
	LL << "There are " << nPC << " primitive concepts used\n";
	LL << " of which " << npFull << " completely defined\n";
	LL << "      and " << nNoTold << " has no told subsumers\n";
	LL << "There are " << nNC << " non-primitive concepts used\n";
	LL << " of which " << nsFull << " synonyms\n";
	LL << "There are " << nSing << " individuals or nominals used\n";
}

void TBox::RemoveExtraDescriptions ( void )
{
	// remove DLTree* from all named concepts
	auto removeDescription = [] (TConcept* C) { C->removeDescription(); };
	std::for_each ( c_begin(), c_end(), removeDescription );
	std::for_each ( i_begin(), i_end(), removeDescription );
}

