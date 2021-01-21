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

#include "globaldef.h"

#include <algorithm>	// for std::sort

#include "Reasoner.h"
#include "logging.h"

#define switchResult(expr)\
do { if (unlikely(expr)) return true; } while (false)

/********************************************************************************
  * Tactics section;
  *
  * Each Tactic should have a (small) Usability function <name>
  * and a Real tactic function <name>Body
  *
  * Each tactic returns:
  * - true		- if expansion of CUR lead to clash
  * - false		- otherwise
  *
  ******************************************************************************/

// main local Tactic
bool DlSatTester :: commonTactic ( void )
{
#ifdef ENABLE_CHECKING
	fpp_assert ( curConcept.bp() != bpINVALID );
#endif

	// check if Node is cached and we tries to expand existing result
	// also don't do anything for p-blocked nodes (can't be unblocked)
	if ( curNode->isCached() || curNode->isPBlocked() )
		return false;

	// informs about starting calculations...
	if ( LLM.isWritable(llGTA) )
		logStartEntry();

	bool ret = false;

	// apply tactic only if Node is not an i-blocked
	if ( !isIBlocked() )
		ret = commonTacticBody ( DLHeap[curConcept] );

	if ( LLM.isWritable(llGTA) )
		logFinishEntry(ret);

	return ret;
}

//-------------------------------------------------------------------------------
//	Simple tactics
//-------------------------------------------------------------------------------

bool DlSatTester :: commonTacticBody ( const DLVertex& cur )
{
	// show DAG usage
#ifdef RKG_PRINT_DAG_USAGE
	const_cast<DLVertex&>(cur).incUsage(isPositive(curConcept.bp()));
#endif
	incStat(nTacticCalls);

	// call proper tactic
	switch ( cur.Type() )
	{
	case dtTop:
		fpp_unreachable();		// can't appear here; addToDoEntry deals with constants
		return false;

	case dtDataType:	// data things are checked by data inferer
	case dtDataValue:
		incStat(nUseless);
		return false;

	case dtPSingleton:
	case dtNSingleton:
		if ( isPositive (curConcept.bp()) )	// real singleton
			return commonTacticBodySingleton(cur);
		else	// negated singleton -- nothing to do with.
			return commonTacticBodyId(cur);

	case dtNConcept:
	case dtPConcept:
		return commonTacticBodyId(cur);

	case dtAnd:
		if ( isPositive (curConcept.bp()) )	// this is AND vertex
			return commonTacticBodyAnd(cur);
		else	// OR
			return commonTacticBodyOr(cur);

	case dtForall:
		if ( isNegative(curConcept.bp()) )	// SOME vertex
			return commonTacticBodySome(cur);

		// ALL vertex
		return commonTacticBodyAll(cur);

	case dtIrr:
		if ( isNegative(curConcept.bp()) )	// SOME R.Self vertex
			return commonTacticBodySomeSelf(cur.getRole());
		else	// don't need invalidate cache, as IRREFL can only lead to CLASH
			return commonTacticBodyIrrefl(cur.getRole());

	case dtLE:
		if ( isNegative (curConcept.bp()) )	// >= vertex
			return commonTacticBodyGE(cur);

		// <= vertex
		if ( isFunctionalVertex(cur) )
			return commonTacticBodyFunc(cur);
		else
			return commonTacticBodyLE(cur);

	case dtProj:
		fpp_assert ( isPositive (curConcept.bp()) );
		return commonTacticBodyProj ( cur.getRole(), cur.getC(), cur.getProjRole() );

	case dtChoose:
		fpp_assert ( isPositive (curConcept.bp()) );
		return applyChooseRule ( curNode, cur.getC() );

	default:
		fpp_unreachable();
		return false;
	}
}

bool DlSatTester :: commonTacticBodyId ( const DLVertex& cur )
{
#ifdef ENABLE_CHECKING
	fpp_assert ( isCNameTag(cur.Type()) );	// safety check
#endif

	incStat(nIdCalls);
	const DepSet& dep = curConcept.getDep();

#ifdef RKG_USE_SIMPLE_RULES
	// check if we have some simple rules
	if ( isPositive(curConcept.bp()) )
		switchResult ( applyExtraRulesIf(static_cast<const TConcept*>(cur.getConcept())) );
#endif

	// get either body(p) or inverse(body(p)), depends on sign of current ID
	BipolarPointer C = isPositive(curConcept.bp()) ? cur.getC() : inverse(cur.getC());
	return addToDoEntry ( curNode, C, dep );
}

/// @return true if the rule is applicable; set the dep-set accordingly
bool
DlSatTester :: applicable ( const TBox::TSimpleRule& rule )
{
	BipolarPointer bp = curConcept.bp();
	const CWDArray& lab = curNode->label().getLabel(/*isComplex=*/false);
	// dep-set to keep track for all the concepts in a rule-head
	DepSet dep = curConcept.getDep();

	for ( const auto& atom: rule.Body )
	{
		if ( atom->pName == bp )
			continue;
		// FIXME!! double check that's correct (no need to negate pName)
		if ( findConceptClash ( lab, atom->pName, dep ) )
			dep = getClashSet();	// such a concept exists -- remember clash set
		else	// no such concept -- can not fire a rule
			return false;
	}

	// rule will be fired -- set the dep-set
	setClashSet(dep);
	return true;
}

bool
DlSatTester :: applyExtraRules ( const TConcept* C )
{
	for ( auto p = C->er_begin(), p_end = C->er_end(); p != p_end; ++p )
	{
		auto rule = tBox.getSimpleRule(*p);
		incStat(nSRuleAdd);
		if ( applicable(*rule) )	// apply the rule's head
		{
			incStat(nSRuleFire);
			switchResult ( addToDoEntry ( curNode, rule->bpHead, getClashSet() ) );
		}
	}

	return false;
}

/// add C to a set of session GCIs; init all nodes with (C,dep)
bool
DlSatTester :: addSessionGCI ( BipolarPointer C, const DepSet& dep )
{
	SessionGCIs.push_back(C);
	for ( auto& node: CGraph )
		if ( isNodeGloballyUsed(node) && addToDoEntry ( node, C, dep, "sg" ) )
			return true;
	return false;
}

bool DlSatTester :: commonTacticBodySingleton ( const DLVertex& cur )
{
#ifdef ENABLE_CHECKING
	fpp_assert ( cur.Type() == dtPSingleton || cur.Type() == dtNSingleton );	// safety check
#endif

	incStat(nSingletonCalls);

	// can use this rule only in the Nominal reasoner
	fpp_assert ( hasNominals() );

	// if the test REALLY uses nominals, remember this
	encounterNominal = true;

	const TIndividual* C = static_cast<const TIndividual*>(cur.getConcept());
	fpp_assert ( C->node != nullptr );

	// if node for C was purged due to merge -- find proper one
	DepSet dep = curConcept.getDep();
	DlCompletionTree* realNode = C->node->resolvePBlocker(dep);

	if ( realNode != curNode )	// check if o-rule is applicable
		// apply o-rule: merge 2 nodes
		// don't need to actually expand P: it was/will be done in C->node
		return Merge ( curNode, realNode, dep );

	// singleton behaves as a general named concepts besides nominal cloud
	return commonTacticBodyId(cur);
}

//-------------------------------------------------------------------------------
//	AND/OR processing
//-------------------------------------------------------------------------------

bool DlSatTester :: commonTacticBodyAnd ( const DLVertex& cur )
{
#ifdef ENABLE_CHECKING
	fpp_assert ( isPositive(curConcept.bp()) && ( cur.Type() == dtAnd ) );	// safety check
#endif

	incStat(nAndCalls);

	const DepSet& dep = curConcept.getDep();

	// FIXME!! I don't know why, but performance is usually BETTER if using r-iters.
	// It's their only usage, so after investigation they can be dropped
	for ( DLVertex::const_reverse_iterator q = cur.rbegin(); q != cur.rend(); ++q )
		switchResult ( addToDoEntry ( curNode, *q, dep ) );

	return false;
}

bool DlSatTester :: commonTacticBodyOr ( const DLVertex& cur )	// for C \or D concepts
{
#ifdef ENABLE_CHECKING
	fpp_assert ( isNegative(curConcept.bp()) && cur.Type() == dtAnd );	// safety check
#endif

	incStat(nOrCalls);

	if ( isFirstBranchCall() )	// check the structure of OR operation (number of applicable concepts)
	{
		DepSet dep;
		if ( planOrProcessing ( cur, dep ) )
		{	// found existing component
			if ( LLM.isWritable(llGTA) )
				LL << " E(" << OrConceptsToTest.back() << ")";
			return false;
		}
		if ( OrConceptsToTest.empty() )
		{	// no more applicable concepts:
			// set global dep-set using accumulated deps
			setClashSet(dep);
			return true;
		}
			// not a branching: just add a single concept
		if ( OrConceptsToTest.size() == 1 )
		{
			BipolarPointer C = OrConceptsToTest.back();
			return insertToDoEntry ( curNode, ConceptWDep(C,dep), DLHeap[C].Type(), "bcp" );
		}

		// more than one alternative: use branching context
		createBCOr();
		bContext->branchDep = dep;
		static_cast<BCOr*>(bContext)->applicableOrEntries.swap(OrConceptsToTest);
	}

	// now it is OR case with 1 or more applicable concepts
	return processOrEntry();
}

bool DlSatTester :: planOrProcessing ( const DLVertex& cur, DepSet& dep )
{
	OrConceptsToTest.clear();
	dep = curConcept.getDep();
	DepSet dummy;

	// check all OR components for the clash
	const CGLabel& lab = curNode->label();
	for ( DLVertex::const_iterator q = cur.begin(), q_end = cur.end(); q < q_end; ++q )
	{
		BipolarPointer C = inverse(*q);
		switch ( tryAddConcept ( lab.getLabel(DLHeap[C].Type()), C, dummy ) )
		{
		case acrClash:	// clash found -- OK
			dep.add(getClashSet());
			continue;
		case acrExist:	// already have such concept -- save it to the 1st position
			OrConceptsToTest.resize(1);
			OrConceptsToTest[0] = C;
			return true;
		case acrDone:
			OrConceptsToTest.push_back(C);
			continue;
		default:		// safety check
			fpp_unreachable();
		}
	}

	return false;
}

bool DlSatTester :: processOrEntry ( void )
{
	// save the context here as after save() it would be lost
	const BCOr* bcOr = static_cast<BCOr*>(bContext);
	BCOr::or_iterator p = bcOr->orBeg(), p_end = bcOr->orCur();
	BipolarPointer C = *p_end;
	const char* reason = nullptr;
	DepSet dep;

	if ( bcOr->isLastOrEntry() )
	{
		// cumulative dep-set will be used
		prepareBranchDep();
		dep = getBranchDep();
		// no more branching decisions
		determiniseBranchingOp();
		reason = "bcp";
	}
	else
	{
		// save current state
		save();
		// new (just branched) dep-set
		dep = getCurDepSet();
		incStat(nOrBrCalls);
	}

	// if semantic branching is in use -- add previous entries to the label
	if ( useSemanticBranching() )
		for ( ; p < p_end; ++p )
			if ( addToDoEntry ( curNode, ConceptWDep(inverse(*p),dep), "sb" ) )
				fpp_unreachable();	// Both Exists and Clash are errors

	// add new entry to current node
	if ( RKG_USE_DYNAMIC_BACKJUMPING )
		return addToDoEntry ( curNode, ConceptWDep(C,dep), reason );
	else // we know the result would be DONE
		return insertToDoEntry ( curNode, ConceptWDep(C,dep), DLHeap[C].Type(), reason );
}

//-------------------------------------------------------------------------------
//	ALL processing
//-------------------------------------------------------------------------------

bool DlSatTester :: commonTacticBodyAllComplex ( const DLVertex& cur )
{
	const DepSet& dep = curConcept.getDep();
	unsigned int state = cur.getState();
	BipolarPointer C = curConcept.bp()-(BipolarPointer)state;	// corresponds to AR{0}.X
	auto& RST = cur.getRole()->getAutomaton()[state];

	// apply all empty transitions
	if ( RST.hasEmptyTransition() )
		for ( const auto& trans: RST )
		{
			incStat(nAutoEmptyLookups);

			if ( trans.empty() )
				switchResult ( addToDoEntry ( curNode, C+BipolarPointer(trans.final()), dep, "e" ) );
		}

	// apply all top-role transitions
	if ( unlikely(RST.hasTopTransition()) )
		for ( const auto& trans: RST )
		{
			if ( trans.isTop() )
				switchResult ( addSessionGCI ( C+BipolarPointer(trans.final()), dep ) );
		}

	// apply final-state rule
	if ( state == 1 )
		switchResult ( addToDoEntry ( curNode, cur.getC(), dep ) );

	// check whether automaton applicable to any edges
	incStat(nAllCalls);

	// check all neighbours
	for ( const auto& neighbour: *curNode )
	{
		if ( RST.recognise(neighbour->getRole()) )
			switchResult ( applyTransitions ( neighbour, RST, C, dep+neighbour->getDep() ) );
	}

	return false;
}

bool DlSatTester :: commonTacticBodyAllSimple ( const DLVertex& cur )
{
	const RAStateTransitions& RST = cur.getRole()->getAutomaton()[0];
	const DepSet& dep = curConcept.getDep();
	BipolarPointer C = cur.getC();

	// check whether automaton applicable to any edges
	incStat(nAllCalls);

	// check all neighbours; as the role is simple then recognise() == applicable()
	for ( const auto& neighbour: *curNode )
	{
		if ( RST.recognise(neighbour->getRole()) )
			switchResult ( addToDoEntry ( neighbour->getArcEnd(), C, dep+neighbour->getDep() ) );
	}

	return false;
}

//-------------------------------------------------------------------------------
//	Support for ALL processing
//-------------------------------------------------------------------------------

/** Perform expansion of (C=\AR{state}.X).DEP to an EDGE with a given reason */
bool DlSatTester :: applyTransitions ( const DlCompletionTreeArc* edge,
											const RAStateTransitions& RST,
											BipolarPointer C,
											const DepSet& dep, const char* reason )
{
	DlCompletionTree* node = edge->getArcEnd();
	// fast lane: the single transition which is applicable
	if ( RST.isSingleton() )
		return addToDoEntry ( node, C+(BipolarPointer)RST.getTransitionEnd(), dep, reason );

	const TRole* R = edge->getRole();

	// try to apply all transitions to edge
	for ( const auto& trans: RST )
	{
		incStat(nAutoTransLookups);
		if ( trans.applicable(R) )
			switchResult ( addToDoEntry ( node, C+BipolarPointer(trans.final()), dep, reason ) );
	}

	return false;
}

//-------------------------------------------------------------------------------
//	SOME processing
//-------------------------------------------------------------------------------

bool DlSatTester :: commonTacticBodySome ( const DLVertex& cur )	// for ER.C concepts
{
#ifdef ENABLE_CHECKING
	fpp_assert ( isNegative(curConcept.bp()) && cur.Type() == dtForall );
#endif

	const DepSet& dep = curConcept.getDep();
	const TRole* R = cur.getRole();
	BipolarPointer C = inverse(cur.getC());

	if ( unlikely(R->isTop()) )
		return commonTacticBodySomeUniv(cur);

	// check if we already have R-neighbour labeled with C
	if ( isSomeExists ( R, C ) )
		return false;
	// try to check the case (some R (or C D)), where C is in the label of an R-neighbour
	if ( isNegative(C) && DLHeap[C].Type() == dtAnd )
		for ( const auto& arg: DLHeap[C] )
			if ( isSomeExists ( R, inverse(arg) ) )
				return false;

	// check for the case \ER.{o}
	if ( tBox.testHasNominals() && isPositive(C) )
	{
		const DLVertex& nom = DLHeap[C];
		if ( nom.Type() == dtPSingleton || nom.Type() == dtNSingleton )
			return commonTacticBodyValue ( R, static_cast<const TIndividual*>(nom.getConcept()) );
	}

	incStat(nSomeCalls);

	// check if we have functional role
	if ( R->isFunctional() )
		for ( const auto& topf: R->topfuncs() )
			switch ( tryAddConcept ( curNode->label().getLabel(/*isComplex=*/true), topf->getFunctional(), dep ) )
			{
			case acrClash:	// addition leads to clash
				return true;
			case acrDone:	// should be add to a label
			{
				// we are changing current Node => save it
				updateLevel ( curNode, dep );

				ConceptWDep rFuncRestriction ( topf->getFunctional(), dep );
				// NOTE! not added into TODO (because will be checked right now)
				CGraph.addConceptToNode ( curNode, rFuncRestriction, /*isComplex=*/true );
				setUsed(rFuncRestriction.bp());

				if ( LLM.isWritable(llGTA) )
					LL << " nf(" << rFuncRestriction << ")";
			}
				break;
			case acrExist:	// already exists
				break;
			default:		// safety check
				fpp_unreachable();
			}


	bool rFunc = false;				// flag is true if we have functional restriction with this Role name
	const TRole* RF = R;			// most general functional super-role of given one
	ConceptWDep rFuncRestriction;	// role's functional restriction w/dep

	// set up rFunc; rfRole contains more generic functional superrole of rName
	for ( DlCompletionTree::const_label_iterator pc = curNode->beginl_cc(); pc != curNode->endl_cc(); ++pc )
	{	// found such vertex (<=1 R)
		const ConceptWDep& LC = *pc;
		const DLVertex& ver = DLHeap[LC];

		if ( isPositive(LC.bp()) && isFunctionalVertex(ver) && *ver.getRole() >= *R )
			if ( !rFunc ||	// 1st functional restriction found or another one...
				 *ver.getRole() >= *RF )	// ... with more generic role
			{
				rFunc = true;
				RF = ver.getRole();
				rFuncRestriction = LC;
			}
	}

	if ( rFunc )	// functional role found => add new concept to existing node
	{
		const DlCompletionTreeArc* functionalArc = nullptr;

		// check if we have an (R)-successor or (R-)-predecessor
		for ( const auto& edge: *curNode )
			if ( edge->isNeighbour (RF) )
			{
				functionalArc = edge;
				break;
			}

		// perform actions if such arc was found
		if ( functionalArc != nullptr )
		{
			if ( LLM.isWritable(llGTA) )
				LL << " f(" << rFuncRestriction << "):";

			DlCompletionTree* succ = functionalArc->getArcEnd();

			// add current dependencies (from processed entry)
			DepSet newDep = { functionalArc->getDep() };
			newDep.add(dep);

			// check if merging will lead to clash because of disjoint roles
			if ( R->isDisjoint() && checkDisjointRoleClash ( curNode, succ, R, newDep ) )
				return true;

			// add current role label (to both arc and its reverse)
			functionalArc = CGraph.addRoleLabel ( curNode, succ, functionalArc->isPredEdge(), R, newDep );

			// adds concept to the end of arc
			switchResult ( addToDoEntry ( succ, C, newDep ) );

			// if new role label was added...
			if ( RF != R )
			{
				// add Range and Domain of a new role; this includes functional, so remove it from the latter
				switchResult ( initHeadOfNewEdge ( curNode, R, newDep, "RD" ) );
				switchResult ( initHeadOfNewEdge ( succ, R->inverse(), newDep, "RR" ) );

				// check AR.C in both sides of functionalArc
				// FIXME!! for simplicity, check the functionality here (see bEx017). It seems
				// only necessary when R has several functional super-roles, so the condition
				// can be simplified
				switchResult ( applyUniversalNR ( curNode, functionalArc, newDep, redoForall | redoFunc ) );
				// if new role label was added to a functionalArc, some functional restrictions
				// in the SUCC node might became applicable. See bFunctional1x test
				switchResult ( applyUniversalNR ( succ, functionalArc->getReverse(), newDep,
													   redoForall | redoFunc | redoAtMost ) );
			}

			return false;
		}
	}

	//------------------------------------------------
	// no functional role or 1st arc -- create new arc
	//------------------------------------------------

	// there no such neighbour - create new successor
	// all FUNCs are already checked; no (new) irreflexivity possible
	return createNewEdge ( cur.getRole(), C, redoForall|redoAtMost );
}

/// expansion rule for existential quantifier in the form ER {o}
bool DlSatTester :: commonTacticBodyValue ( const TRole* R, const TIndividual* nom )
{
	DepSet dep(curConcept.getDep());

	// check blocking conditions
	if ( isCurNodeBlocked() )
		return false;

	incStat(nSomeCalls);

	fpp_assert ( nom->node != nullptr );

	// if node for NOM was purged due to merge -- find proper one
	DlCompletionTree* realNode = nom->node->resolvePBlocker(dep);

	// check if merging will lead to clash because of disjoint roles
	if ( R->isDisjoint() && checkDisjointRoleClash ( curNode, realNode, R, dep ) )
		return true;

	// here we are sure that there is a nominal connected to a root node
	encounterNominal = true;

	// create new edge between curNode and the given nominal node
	DlCompletionTreeArc* edge =
		CGraph.addRoleLabel ( curNode, realNode, /*linkToParent=*/false, R, dep );

	// add all necessary concepts to both ends of the edge
	return setupEdge ( edge, dep, redoEverything );
}

bool
DlSatTester :: commonTacticBodySomeUniv ( const DLVertex& cur )
{
	// check blocking conditions
	if ( isCurNodeBlocked() )
		return false;

	incStat(nSomeCalls);

	BipolarPointer C = inverse(cur.getC());
	// check whether C is already in CGraph
	for ( const auto& node: CGraph )
		if ( isObjectNodeUnblocked(node) && node->label().contains(C) )
			return false;
	// make new node labeled with C
	return initNewNode ( CGraph.getNewNode(), curConcept.getDep(), C );
}

//-------------------------------------------------------------------------------
//	Support for SOME processing
//-------------------------------------------------------------------------------

bool DlSatTester :: createNewEdge ( const TRole* R, BipolarPointer C, unsigned int redoFlags )
{
	const DepSet& dep = curConcept.getDep();

	// check blocking conditions
	if ( isCurNodeBlocked() )
	{
		incStat(nUseless);
		return false;
	}

	DlCompletionTreeArc* pA = createOneNeighbour ( R, dep );

	// add necessary label
	return initNewNode ( pA->getArcEnd(), dep, C ) || setupEdge ( pA, dep, redoFlags );
}

/// create new ROLE-neighbour to curNode; return edge to it
DlCompletionTreeArc* DlSatTester :: createOneNeighbour ( const TRole* R, const DepSet& dep, CTNominalLevel level )
{
	// check whether is called from NN-rule
	bool forNN = level != BlockableLevel;

	// create a proper neighbour
	DlCompletionTreeArc* pA = CGraph.createNeighbour ( curNode, /*isPredEdge=*/forNN, R, dep );
	DlCompletionTree* node = pA->getArcEnd();

	// set nominal node's level if necessary
	if ( forNN )
		node->setNominalLevel(level);

	// check whether created node is data node
	if ( R->isDataRole() )
		node->setDataNode();

	// log newly created node
	CHECK_LL_RETURN_VALUE(llGTA,pA);

	if ( R->isDataRole() )
		LL << " DN(";
	else
		LL << " cn(";
	LL << node->getId() << dep << ")";

	return pA;
}

bool DlSatTester :: isCurNodeBlocked ( void )
{
	// for non-lazy blocking blocked status is correct
	if ( !useLazyBlocking() )
		return curNode->isBlocked();

	// update node's blocked status
	if ( !curNode->isBlocked() && curNode->isAffected() )
	{
		updateLevel ( curNode, curConcept.getDep() );
		CGraph.detectBlockedStatus(curNode);
	}

	return curNode->isBlocked();
}

void
DlSatTester :: applyAllGeneratingRules ( DlCompletionTree* node )
{
	const CGLabel& label = node->label();
	for ( CGLabel::const_iterator p = label.begin_cc(), p_end = label.end_cc(); p != p_end; ++p )
	{
		// need only ER.C or >=nR.C concepts
		if ( isPositive(p->bp()) )
			continue;

		switch ( DLHeap[*p].Type() )
		{
		case dtForall:
		case dtLE:
			addExistingToDoEntry ( node, label.getCCOffset(p), "ubd" );
			break;

		default:
			break;
		}
	}
}

bool
DlSatTester :: setupEdge ( DlCompletionTreeArc* pA, const DepSet& dep, unsigned int redoFlags )
{
	DlCompletionTree* child = pA->getArcEnd();
	DlCompletionTree* from = pA->getReverse()->getArcEnd();

	// adds Range and Domain
	switchResult ( initHeadOfNewEdge ( from, pA->getRole(), dep, "RD" ) );
	switchResult ( initHeadOfNewEdge ( child, pA->getReverse()->getRole(), dep, "RR" ) );

	// check if we have any AR.X concepts in current node
	if ( redoFlags != 0 )
		switchResult ( applyUniversalNR ( from, pA, dep, redoFlags ) );

	// for nominal children and loops -- just apply things for the inverses
	if ( pA->isPredEdge() || child->isNominalNode() || child == from )
	{
		if ( redoFlags != 0 )
			switchResult ( applyUniversalNR ( child, pA->getReverse(), dep, redoFlags ) );
	}
	else
	{
		if ( child->isDataNode() )
		{
			checkDataNode = true;
			switchResult ( checkDataClash(child) );
		}
		else	// check if it is possible to use cache for new node
			switchResult ( usageByState(tryCacheNode(child)) );
	}

	// all done
	return false;
}

bool DlSatTester :: applyUniversalNR ( DlCompletionTree* Node,
											  const DlCompletionTreeArc* arcSample,
											  const DepSet& dep_, unsigned int redoFlags )
{
	// check whether a flag is set
	if ( redoFlags == 0 )
		return false;

	const TRole* R = arcSample->getRole();
	DepSet dep = dep_ + arcSample->getDep();

	for ( DlCompletionTree::const_label_iterator
		  p = Node->beginl_cc(), p_end = Node->endl_cc(); p != p_end; ++p )
	{
		// need only AR.C concepts where ARC is labelled with R
		if ( isNegative(p->bp()) )
			continue;

		const DLVertex& v = DLHeap[*p];
		const TRole* vR = v.getRole();

		switch ( v.Type() )
		{
		case dtIrr:
			if ( redoFlags & redoIrr )
				switchResult ( checkIrreflexivity ( arcSample, vR, dep ) );
			break;

		case dtForall:
		{
			if ( (redoFlags & redoForall) == 0 )
				break;
			if ( unlikely(vR->isTop()) )
				break;

			/// check whether transition is possible
			const RAStateTransitions& RST = vR->getAutomaton()[v.getState()];
			if ( !RST.recognise(R) )
				break;

			if ( vR->isSimple() )	// R is recognised so just add the final state!
				switchResult ( addToDoEntry ( arcSample->getArcEnd(), v.getC(), dep+p->getDep(), "ae" ) );
			else
				switchResult ( applyTransitions ( arcSample, RST, p->bp()-(BipolarPointer)v.getState(), dep+p->getDep(), "ae" ) );
			break;
		}

		case dtLE:
			if ( isFunctionalVertex(v) )
			{
				if ( (redoFlags & redoFunc) && (*vR >= *R) )
					addExistingToDoEntry ( Node, Node->label().getCCOffset(p), "f" );
			}
			else
				if ( (redoFlags & redoAtMost) && (*vR >= *R) )
					addExistingToDoEntry ( Node, Node->label().getCCOffset(p), "le" );
			break;

		default:
			break;
		}
	}

	return false;
}

	/// add necessary concepts to the head of the new EDGE
bool
DlSatTester :: initHeadOfNewEdge ( DlCompletionTree* node, const TRole* R, const DepSet& dep, const char* reason )
{
	// if R is functional, then add FR with given DEP-set to NODE
	if ( R->isFunctional() )
		for ( const auto& topf: R->topfuncs() )
			switchResult ( addToDoEntry ( node, topf->getFunctional(), dep, "fr" ) );

	// setup Domain for R
	switchResult ( addToDoEntry ( node, R->getBPDomain(), dep, reason ) );

	if ( !RKG_UPDATE_RND_FROM_SUPERROLES )
		for ( const auto& sup: R->ancestors() )
			switchResult ( addToDoEntry ( node, sup->getBPDomain(), dep, reason ) );

	return false;
}

//-------------------------------------------------------------------------------
//	Func/LE/GE processing
//-------------------------------------------------------------------------------

bool DlSatTester :: commonTacticBodyFunc ( const DLVertex& cur )	// for <=1 R concepts
{
#ifdef ENABLE_CHECKING
	fpp_assert ( isPositive(curConcept.bp()) && isFunctionalVertex(cur) );
#endif

	const TRole* R = cur.getRole();

	if ( unlikely(R->isTop()) )
		return processTopRoleFunc(cur);

	// check whether we need to apply NN rule first
	if ( isNNApplicable ( R, bpTOP, curConcept.bp()+1 ) )
		return commonTacticBodyNN(cur);	// after application func-rule would be checked again

	incStat(nFuncCalls);

	if ( isQuickClashLE(cur) )
		return true;

	// locate all R-neighbours of curNode
	DepSet dummy;	// not used
	findNeighbours ( R, bpTOP, dummy );

	// check if we have nodes to merge
	if ( EdgesToMerge.size() < 2 )
		return false;

	// merge all nodes to the first (the least wrt nominal hierarchy) found node
	EdgeVector::iterator q = EdgesToMerge.begin(), q_end = EdgesToMerge.end();
	DlCompletionTree* sample = (*q)->getArcEnd();
	DepSet depF (curConcept.getDep());	// dep-set for merging
	depF.add((*q)->getDep());

	// merge all elements to sample (sample wouldn't be merge)
	while ( ++q != q_end )
		// during merge EdgesToMerge may became purged (see Nasty4) => check this
		if ( !(*q)->getArcEnd()->isPBlocked() )
			switchResult ( Merge ( (*q)->getArcEnd(), sample, depF+(*q)->getDep() ) );

	return false;
}


bool DlSatTester :: commonTacticBodyLE ( const DLVertex& cur )	// for <=nR.C concepts
{
#ifdef ENABLE_CHECKING
	fpp_assert ( isPositive(curConcept.bp()) && ( cur.Type() == dtLE ) );
#endif

	incStat(nLeCalls);
	const TRole* R = cur.getRole();

	if ( unlikely(R->isTop()) )
		return processTopRoleLE(cur);

	BipolarPointer C = cur.getC();
	bool needInit = true;

	if ( !isFirstBranchCall() )
	{
		if ( dynamic_cast<BCNN*>(bContext) != nullptr )
			return commonTacticBodyNN(cur);	// after application <=-rule would be checked again
		if ( dynamic_cast<BCLE<DlCompletionTreeArc>*>(bContext) != nullptr )
			needInit = false;	// clash in LE-rule: skip the initial checks
		else	// the only possible case is choose-rule; in this case just continue
			fpp_assert ( dynamic_cast<BCChoose*>(bContext) != nullptr );
	}
	else	// if we are here that it IS first LE call
		if ( isQuickClashLE(cur) )
			return true;

	// initial phase: choose-rule, NN-rule
	if ( needInit )
	{
		// check if we have Qualified NR
		if ( C != bpTOP )
			switchResult ( commonTacticBodyChoose ( R, C ) );

		// check whether we need to apply NN rule first
		if ( isNNApplicable ( R, C, /*stopper=*/curConcept.bp()+(BipolarPointer)cur.getNumberLE() ) )
			return commonTacticBodyNN(cur);	// after application <=-rule would be checked again
	}

	// we need to repeat merge until there will be necessary amount of edges
	for (;;)
	{
		if ( isFirstBranchCall() )
			if ( initLEProcessing(cur) )
				return false;

		BCLE<DlCompletionTreeArc>* bcLE = static_cast<BCLE<DlCompletionTreeArc>*>(bContext);

		if ( bcLE->noMoreLEOptions() )
		{	// set global clashset to cumulative one from previous branch failures
			useBranchDep();
			return true;
		}

		// get from- and to-arcs using corresponding indexes in Edges
		DlCompletionTreeArc* fromArc = bcLE->getFrom();
		DlCompletionTreeArc* toArc = bcLE->getTo();
		DlCompletionTree* from = fromArc->getArcEnd();
		DlCompletionTree* to = toArc->getArcEnd();

		DepSet dep;	// empty dep-set
		// fast check for FROM =/= TO
		if ( CGraph.nonMergeable ( from, to, dep ) )
		{
			// need this for merging two nominal nodes
			dep.add(fromArc->getDep());
			dep.add(toArc->getDep());
			// add dep-set from labels
			if ( C == bpTOP )	// dep-set is known now
				setClashSet(dep);
			else	// QCR: update dep-set wrt C
			{
				// here we know that C is in both labels; set a proper clash-set
				bool isComplex = CGLabel::isComplexConcept(DLHeap[C].Type());
				bool test {false};

				// here dep contains the clash-set
				test = findConceptClash ( from->label().getLabel(isComplex), C, dep );
				fpp_assert(test);
				dep += getClashSet();	// save new dep-set

				test = findConceptClash ( to->label().getLabel(isComplex), C, dep );
				fpp_assert(test);
				// both clash-sets are now in common clash-set
			}

			nextBranchingOption();
			fpp_assert(!isFirstBranchCall());
			continue;
		}

		save();

		// add depset from current level and FROM arc and to current dep.set
		DepSet curDep(getCurDepSet());
		curDep.add(fromArc->getDep());

		switchResult ( Merge ( from, to, curDep ) );
		// it might be the case (see bIssue28) that after the merge there is an R-neighbour
		// that have neither C or ~C in its label (it was far in the nominal cloud)
		if ( C != bpTOP )
			switchResult ( commonTacticBodyChoose ( R, C ) );
	}
}

bool
DlSatTester :: initLEProcessing ( const DLVertex& cur )
{
	DepSet dep;
	// check the amount of neighbours we have
	findNeighbours ( cur.getRole(), cur.getC(), dep );

	// if the number of R-neighbours satisfies condition -- nothing to do
	if ( EdgesToMerge.size() <= cur.getNumberLE() )
		return true;

	// init context
	createBCLE();
	bContext->branchDep += dep;

	// setup BCLE
	BCLE<DlCompletionTreeArc>* bcLE = static_cast<BCLE<DlCompletionTreeArc>*>(bContext);

	bcLE->ItemsToMerge.swap(EdgesToMerge);
	bcLE->resetMCI();
	return false;
}


bool DlSatTester :: commonTacticBodyGE ( const DLVertex& cur )	// for >=nR.C concepts
{
#ifdef ENABLE_CHECKING
	fpp_assert ( isNegative(curConcept.bp()) && cur.Type() == dtLE );
#endif

	// check blocking conditions
	if ( isCurNodeBlocked() )
		return false;

	const TRole* R = cur.getRole();

	if ( unlikely(R->isTop()) )
		return processTopRoleGE(cur);

	incStat(nGeCalls);

	if ( isQuickClashGE(cur) )
		return true;

	// create N new different edges
	return createDifferentNeighbours ( R, cur.getC(), curConcept.getDep(), cur.getNumberGE(), BlockableLevel );
}

//-------------------------------------------------------------------------------
//	Func/LE/GE with top role processing
//-------------------------------------------------------------------------------

bool
DlSatTester :: processTopRoleFunc ( const DLVertex& cur )	// for <=1 R concepts
{
#ifdef ENABLE_CHECKING
	fpp_assert ( isPositive(curConcept.bp()) && isFunctionalVertex(cur) );
#endif

	incStat(nFuncCalls);

	if ( isQuickClashLE(cur) )
		return true;

	// locate all R-neighbours of curNode
	DepSet dummy;	// not used
	findCLabelledNodes ( bpTOP, dummy );

	// check if we have nodes to merge
	if ( NodesToMerge.size() < 2 )
		return false;

	// merge all nodes to the first (the least wrt nominal hierarchy) found node
	NodeVector::iterator q = NodesToMerge.begin(), q_end = NodesToMerge.end();
	DlCompletionTree* sample = *q;
	DepSet dep (curConcept.getDep());	// dep-set for merging

	// merge all elements to sample (sample wouldn't be merge)
	while ( ++q != q_end )
		// during merge EdgesToMerge may became purged (see Nasty4) => check this
		if ( !(*q)->isPBlocked() )
			switchResult ( Merge ( *q, sample, dep ) );

	return false;
}

bool
DlSatTester :: processTopRoleLE ( const DLVertex& cur )	// for <=nR.C concepts
{
#ifdef ENABLE_CHECKING
	fpp_assert ( isPositive(curConcept.bp()) && ( cur.Type() == dtLE ) );
#endif

	BipolarPointer C = cur.getC();
	bool needInit = true;

	if ( !isFirstBranchCall() )
	{
		if ( dynamic_cast<BCLE<DlCompletionTree>*>(bContext) != nullptr )
			needInit = false;	// clash in LE-rule: skip the initial checks
		else	// the only possible case is choose-rule; in this case just continue
			fpp_assert ( dynamic_cast<BCChoose*>(bContext) != nullptr );
	}
	else	// if we are here that it IS first LE call
		if ( isQuickClashLE(cur) )
			return true;

	// initial phase: choose-rule, NN-rule
	if ( needInit )
	{
		// check if we have Qualified NR
		if ( C != bpTOP )
			switchResult ( applyChooseRuleGlobally(C) );
	}

	// we need to repeat merge until there will be necessary amount of edges
	for (;;)
	{
		if ( isFirstBranchCall() )
			if ( initTopLEProcessing(cur) )
				return false;

		BCLE<DlCompletionTree>* bcLE = static_cast<BCLE<DlCompletionTree>*>(bContext);

		if ( bcLE->noMoreLEOptions() )
		{	// set global clashset to cumulative one from previous branch failures
			useBranchDep();
			return true;
		}

		// get from- and to-arcs using corresponding indexes in Edges
		DlCompletionTree* from = bcLE->getFrom();
		DlCompletionTree* to = bcLE->getTo();

		DepSet dep;	// empty dep-set
		// fast check for FROM =/= TO
		if ( CGraph.nonMergeable ( from, to, dep ) )
		{
			// add dep-set from labels
			if ( C == bpTOP )	// dep-set is known now
				setClashSet(dep);
			else	// QCR: update dep-set wrt C
			{
				// here we know that C is in both labels; set a proper clash-set
				bool isComplex = CGLabel::isComplexConcept(DLHeap[C].Type());
				bool test {false};

				// here dep contains the clash-set
				test = findConceptClash ( from->label().getLabel(isComplex), C, dep );
				fpp_assert(test);
				dep += getClashSet();	// save new dep-set

				test = findConceptClash ( to->label().getLabel(isComplex), C, dep );
				fpp_assert(test);
				// both clash-sets are now in common clash-set
			}

			nextBranchingOption();
			fpp_assert(!isFirstBranchCall());
			continue;
		}

		save();

		// add depset from current level and FROM arc and to current dep.set
		DepSet curDep(getCurDepSet());

		switchResult ( Merge ( from, to, curDep ) );
	}
}

bool
DlSatTester :: processTopRoleGE ( const DLVertex& cur )	// for >=nR.C concepts
{
#ifdef ENABLE_CHECKING
	fpp_assert ( isNegative(curConcept.bp()) && cur.Type() == dtLE );
	fpp_assert ( !isCurNodeBlocked() );
#endif

	incStat(nGeCalls);

	if ( isQuickClashGE(cur) )
		return true;

	// create N new different edges
	// FIXME!! for now
	return createDifferentNeighbours ( cur.getRole(), cur.getC(), curConcept.getDep(), cur.getNumberGE(), BlockableLevel );
}

bool
DlSatTester :: initTopLEProcessing ( const DLVertex& cur )
{
	DepSet dep;
	// check the amount of neighbours we have
	findCLabelledNodes ( cur.getC(), dep );

	// if the number of R-neighbours satisfies condition -- nothing to do
	if ( NodesToMerge.size() <= cur.getNumberLE() )
		return true;

	// init context
	createBCTopLE();
	bContext->branchDep += dep;

	// setup BCLE
	BCLE<DlCompletionTree>* bcLE = static_cast<BCLE<DlCompletionTree>*>(bContext);

	bcLE->ItemsToMerge.swap(NodesToMerge);
	bcLE->resetMCI();
	return false;
}


//-------------------------------------------------------------------------------
//	Support for Func/LE/GE processing
//-------------------------------------------------------------------------------

/// create N R-neighbours of curNode with given Nominal LEVEL labelled with C
bool DlSatTester :: createDifferentNeighbours ( const TRole* R, BipolarPointer C, const DepSet& dep,
													   unsigned int n, CTNominalLevel level )
{
	// create N new edges with the same IR
	DlCompletionTreeArc* pA = nullptr;
	CGraph.initIR();
	for ( unsigned int i = 0; i < n; ++i )
	{
		pA = createOneNeighbour ( R, dep, level );
		DlCompletionTree* child = pA->getArcEnd();

		// make CHILD different from other created nodes
		// don't care about return value as clash can't occur
		CGraph.setCurIR ( child, dep );

		// add necessary new node labels and setup new edge
		switchResult ( initNewNode ( child, dep, C ) );
		switchResult ( setupEdge ( pA, dep, redoForall ) );
	}
	CGraph.finiIR();

	// re-apply all <= NR in curNode; do it only once for all created nodes; no need for Irr
	return applyUniversalNR ( curNode, pA, dep, redoFunc|redoAtMost );
}

		/// check if ATLEAST and ATMOST restrictions are in clash; setup depset from CUR
bool DlSatTester :: isNRClash ( const DLVertex& atleast, const DLVertex& atmost, const ConceptWDep& reason )
{
	if ( atmost.Type() != dtLE || atleast.Type() != dtLE )
		return false;
	if ( !checkNRclash ( atleast, atmost ) )	// no clash between them
		return false;

	// clash exists: create dep-set
	setClashSet ( curConcept.getDep() + reason.getDep() );

	// log clash reason
	if ( LLM.isWritable(llGTA) )
		logClash ( curNode, reason );

	return true;
}

/// aux method that checks whether clash occurs during the merge of labels
bool
DlSatTester :: checkMergeClash ( const CGLabel& from, const CGLabel& to, const DepSet& dep, unsigned int nodeId )
{
	CGLabel::const_iterator p, p_end;
	DepSet clashDep(dep);
	bool clash = false;
	for ( p = from.begin_sc(), p_end = from.end_sc(); p < p_end; ++p )
		if ( isUsed(inverse(p->bp()))
			 && findConceptClash ( to.getLabel(/*isComplex=*/false), inverse(*p) ) )
		{
			clash = true;
			clashDep.add(getClashSet());
			// log the clash
			if ( LLM.isWritable(llGTA) )
				LL << " x(" << nodeId << "," << p->bp() << getClashSet()+dep << ")";
		}
	for ( p = from.begin_cc(), p_end = from.end_cc(); p < p_end; ++p )
		if ( isUsed(inverse(p->bp()))
			 && findConceptClash ( to.getLabel(/*isComplex=*/true), inverse(*p) ) )
		{
			clash = true;
			clashDep.add(getClashSet());
			// log the clash
			if ( LLM.isWritable(llGTA) )
				LL << " x(" << nodeId << "," << p->bp() << getClashSet()+dep << ")";
		}
	if ( clash )
		setClashSet(clashDep);
	return clash;
}

bool DlSatTester :: mergeLabels ( const CGLabel& from, DlCompletionTree* to, const DepSet& dep )
{
	CGLabel::const_iterator p, p_end;
	CGLabel& lab(to->label());
	// arrays for simple- and complex concepts in the merged-to vector
	CWDArray& sc(lab.getLabel(/*isComplex=*/false));
	CWDArray& cc(lab.getLabel(/*isComplex=*/true));

	// due to merging, all the concepts in the TO label
	// should be updated to the new dep-set DEP
	// TODO!! check whether this is really necessary
	for ( p = sc.begin(), p_end = sc.end(); p < p_end; ++p )
		CGraph.saveRareCond ( sc.updateDepSet ( p->bp(), dep ) );
	for ( p = cc.begin(), p_end = cc.end(); p < p_end; ++p )
		CGraph.saveRareCond ( cc.updateDepSet ( p->bp(), dep ) );

	// if the concept is already exists in the node label --
	// we still need to update it with a new dep-set (due to merging)
	// note that DEP is already there
	for ( p = from.begin_sc(), p_end = from.end_sc(); p < p_end; ++p )
		if ( findConcept ( sc, *p ) )
			CGraph.saveRareCond ( sc.updateDepSet ( p->bp(), p->getDep() ) );
		else
			switchResult ( insertToDoEntry ( to, ConceptWDep(*p,dep), DLHeap[*p].Type(), "M" ) );
	for ( p = from.begin_cc(), p_end = from.end_cc(); p < p_end; ++p )
		if ( findConcept ( cc, *p ) )
			CGraph.saveRareCond ( cc.updateDepSet ( p->bp(), p->getDep() ) );
		else
			switchResult ( insertToDoEntry ( to, ConceptWDep(*p,dep), DLHeap[*p].Type(), "M" ) );

	return false;
}

bool DlSatTester :: Merge ( DlCompletionTree* from, DlCompletionTree* to, const DepSet& depF )
{
	// if node is already purged -- nothing to do
	fpp_assert ( !from->isPBlocked() );

	// prevent node to be merged to itself
	fpp_assert ( from != to );

	// never merge nominal node to blockable one
	fpp_assert ( to->getNominalLevel() <= from->getNominalLevel() );

	if ( LLM.isWritable(llGTA) )
		LL << " m(" << from->getId() << "->" << to->getId() << ")";

	incStat(nMergeCalls);

	// can't merge 2 nodes which are in inequality relation
	DepSet dep(depF);
	if ( CGraph.nonMergeable ( from, to, dep ) )
	{
		setClashSet(dep);
		return true;
	}

	// check for the clash before doing anything else
	if ( checkMergeClash ( from->label(), to->label(), depF, to->getId() ) )
		return true;

	// copy all node labels
	switchResult ( mergeLabels ( from->label(), to, depF ) );

	// correct graph structure
	typedef std::vector<DlCompletionTreeArc*> edgeVector;
	edgeVector edges;
	CGraph.Merge ( from, to, depF, edges );

	// check whether a disjoint roles lead to clash
	for ( const auto& edge: edges )
		if ( edge->getRole()->isDisjoint() &&
			 checkDisjointRoleClash ( edge->getReverse()->getArcEnd(), edge->getArcEnd(), edge->getRole(), depF ) )
			return true;

	// nothing more to do with data nodes
	if ( to->isDataNode() )	// data concept -- run data center for it
		return checkDataClash(to);

	// for every node added to TO, every ALL, Irr and <=-node should be checked
	for ( const auto& edge: edges )
		switchResult ( applyUniversalNR ( to, edge, depF, redoEverything ) );

	// we do real action here, so the return value
	return false;
}

bool
DlSatTester :: checkDisjointRoleClash ( const DlCompletionTree* from, const DlCompletionTree* to,
										const TRole* R, const DepSet& dep )
{
	// try to check whether link from->to labelled with something disjoint with R
	for ( const auto& edge: *from )
		if ( checkDisjointRoleClash ( edge, to, R, dep ) )
			return true;
	return false;
}

// compare 2 CT edges wrt blockable/nominal nodes at their ends
class EdgeCompare
{
public:
	bool operator() ( DlCompletionTreeArc* pa, DlCompletionTreeArc* qa ) const
		{ return *(pa)->getArcEnd() < *(qa)->getArcEnd(); }
}; // EdgeCompare

/// aux method to check whether edge ended to NODE should be added to EdgesToMerge
template <typename Iterator>
bool isNewEdge ( const DlCompletionTree* node, Iterator begin, Iterator end )
{
	for ( Iterator q = begin; q != end; ++q )
		if ( (*q)->getArcEnd() == node )	// skip edges to the same node
			return false;

	return true;
}

void
DlSatTester :: findNeighbours ( const TRole* Role, BipolarPointer C, DepSet& Dep )
{
	EdgesToMerge.clear();
	bool isComplex = CGLabel::isComplexConcept(DLHeap[C].Type());

	for ( auto& edge: *curNode )
		if ( edge->isNeighbour(Role)
			 && isNewEdge ( edge->getArcEnd(), EdgesToMerge.begin(), EdgesToMerge.end() )
			 && findChooseRuleConcept ( edge->getArcEnd()->label().getLabel(isComplex), C, Dep ) )
			EdgesToMerge.push_back(edge);

	// sort EdgesToMerge: From named nominals to generated nominals to blockable nodes
	std::sort ( EdgesToMerge.begin(), EdgesToMerge.end(), EdgeCompare() );
}

class NodeCompare
{
public:
	bool operator() ( DlCompletionTree* p, DlCompletionTree* q ) const { return *p < *q; }
};

void
DlSatTester :: findCLabelledNodes ( BipolarPointer C, DepSet& Dep )
{
	NodesToMerge.clear();
	bool isComplex = CGLabel::isComplexConcept(DLHeap[C].Type());

	// FIXME!! do we need this for d-blocked nodes?
	for ( auto& node: CGraph )
		if ( isNodeGloballyUsed(node) && findChooseRuleConcept ( node->label().getLabel(isComplex), C, Dep ) )
			NodesToMerge.push_back(node);

	// sort EdgesToMerge: From named nominals to generated nominals to blockable nodes
	std::sort ( NodesToMerge.begin(), NodesToMerge.end(), NodeCompare() );
}


//-------------------------------------------------------------------------------
//	Choose-rule processing
//-------------------------------------------------------------------------------

bool DlSatTester :: commonTacticBodyChoose ( const TRole* R, BipolarPointer C )
{
	// apply choose-rule for every R-neighbour
	for ( auto& edge: *curNode )
	{
		if ( edge->isNeighbour(R) )
			switchResult ( applyChooseRule ( edge->getArcEnd(), C ) );
	}

	return false;
}

/// apply choose-rule for all vertices (necessary for Top role in QCR)
bool
DlSatTester :: applyChooseRuleGlobally ( BipolarPointer C )
{
	for ( auto& node: CGraph )
	{
		if ( isObjectNodeUnblocked(node) )	// FIXME!! think about d-blocked nodes
			switchResult ( applyChooseRule ( node, C ) );
	}

	return false;
}

//-------------------------------------------------------------------------------
//	Support for choose-rule processing
//-------------------------------------------------------------------------------

/// apply choose-rule to given node
bool DlSatTester :: applyChooseRule ( DlCompletionTree* node, BipolarPointer C )
{
	if ( node->isLabelledBy(C) || node->isLabelledBy(inverse(C)) )
		return false;

	// now node will be labelled with ~C or C
	if ( isFirstBranchCall() )
	{
		createBCCh();
		// save current state
		save();

		return addToDoEntry ( node, inverse(C), getCurDepSet(), "cr0" );
	}
	else
	{
		prepareBranchDep();
		DepSet dep(getBranchDep());
		determiniseBranchingOp();
		return addToDoEntry ( node, C, dep, "cr1" );
	}
}

//-------------------------------------------------------------------------------
//	NN rule processing
//-------------------------------------------------------------------------------

bool DlSatTester :: commonTacticBodyNN ( const DLVertex& cur )	// NN-rule
{
	// here we KNOW that NN-rule is applicable, so skip some tests
	incStat(nNNCalls);

	if ( isFirstBranchCall() )
		createBCNN();

	const BCNN* bcNN = static_cast<BCNN*>(bContext);

	// check whether we did all possible tries
	if ( bcNN->noMoreNNOptions(cur.getNumberLE()) )
	{	// set global clashset to cumulative one from previous branch failures
		useBranchDep();
		return true;
	}

	// take next NN number; save it as SAVE() will reset it to 0
	unsigned int NN = bcNN->value;

	// prepare to addition to the label
	save();

	// new (just branched) dep-set
	const DepSet curDep(getCurDepSet());

	// make a stopper to mark that NN-rule is applied
	switchResult ( addToDoEntry ( curNode, curConcept.bp() + (BipolarPointer)cur.getNumberLE(), DepSet(), "NNs" ) );

	// create curNN new different edges
	switchResult ( createDifferentNeighbours ( cur.getRole(), cur.getC(), curDep, NN, curNode->getNominalLevel()+1 ) );

	// now remember NR we just created: it is (<= curNN R), so have to find it
	return addToDoEntry ( curNode, curConcept.bp() + (BipolarPointer)(cur.getNumberLE()-NN), curDep, "NN" );
}

//-------------------------------------------------------------------------------
//	Support for NN rule processing
//-------------------------------------------------------------------------------

/// @return true iff NN-rule wrt (<= R) is applicable to the curNode
bool DlSatTester :: isNNApplicable ( const TRole* r, BipolarPointer C, BipolarPointer stopper ) const
{
	// NN rule is only applicable to nominal nodes
	if ( !curNode->isNominalNode() )
		return false;

	// check whether the NN-rule was already applied here for a given concept
	if ( isUsed(stopper) && curNode->isLabelledBy(stopper) )
		return false;

	// check for the real applicability of the NN-rule here
	for ( const auto& edge: *curNode )
	{
		const DlCompletionTree* suspect = edge->getArcEnd();

		// if there is an edge that require to run the rule, then we need it
		if ( edge->isPredEdge() && suspect->isBlockableNode() && edge->isNeighbour(r) && suspect->isLabelledBy(C) )
		{
			if ( LLM.isWritable(llGTA) )
				LL << " NN(" << suspect->getId() << ")";

			return true;
		}
	}

	// can't apply NN-rule
	return false;
}

//-------------------------------------------------------------------------------
//	Support for (\neg) \E R.Self
//-------------------------------------------------------------------------------

bool DlSatTester :: commonTacticBodySomeSelf ( const TRole* R )
{
	// check blocking conditions
	if ( isCurNodeBlocked() )
		return false;

	// nothing to do if R-loop already exists
	for ( const auto& edge: *curNode )
		if ( edge->getArcEnd() == curNode && edge->isNeighbour(R) )
			return false;

	// create an R-loop through curNode
	const DepSet& dep = curConcept.getDep();
	DlCompletionTreeArc* pA = CGraph.createLoop ( curNode, R, dep );
	return setupEdge ( pA, dep, redoEverything );
}

bool DlSatTester :: commonTacticBodyIrrefl ( const TRole* R )
{
	// return clash if R-loop is found
	for ( const auto& edge: *curNode )
		if ( checkIrreflexivity ( edge, R, curConcept.getDep() ) )
			return true;

	return false;
}

//-------------------------------------------------------------------------------
//	Support for projection R\C -> P
//-------------------------------------------------------------------------------

bool DlSatTester :: commonTacticBodyProj ( const TRole* R, BipolarPointer C, const TRole* ProjR )
{
	// find an R-edge, try to apply projection-rule to it

	// if ~C is in the label of curNode, do nothing
	if ( curNode->isLabelledBy(inverse(C)) )
		return false;

	// checkProjection() might change curNode's edge vector and thus invalidate iterators
	DlCompletionTree::const_edge_iterator p = curNode->begin(), p_end = curNode->end();

	for ( std::ptrdiff_t i = 0, n = p_end - p; i < n; ++i )
	{
		p = curNode->begin() + i;
		if ( (*p)->isNeighbour(R) )
			switchResult ( checkProjection ( *p, C, ProjR ) );
	}

	return false;
}

bool DlSatTester :: checkProjection ( DlCompletionTreeArc* pA, BipolarPointer C, const TRole* ProjR )
{
	// nothing to do if pA is labelled by ProjR as well
	if ( pA->isNeighbour(ProjR) )
		return false;
	// if ~C is in the label of curNode, do nothing
	if ( curNode->isLabelledBy(inverse(C)) )
		return false;

	// neither C nor ~C are in the label: make a choice
	DepSet dep(curConcept.getDep());
	dep.add(pA->getDep());

	if ( !curNode->isLabelledBy(C) )
	{
		if ( isFirstBranchCall() )
		{
			createBCCh();
			// save current state
			save();

			return addToDoEntry ( curNode, inverse(C), getCurDepSet(), "cr0" );
		}
		else
		{
			prepareBranchDep();
			dep.add(getBranchDep());
			determiniseBranchingOp();
			switchResult ( addToDoEntry ( curNode, C, dep, "cr1" ) );
		}
	}
	// here C is in the label of curNode: add ProjR to the edge if necessary
	DlCompletionTree* child = pA->getArcEnd();
	pA = CGraph.addRoleLabel ( curNode, child, pA->isPredEdge(), ProjR, dep );
	return setupEdge ( pA, dep, redoEverything );
}
