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

    if (tBox.precacheRelated)
        precacheRelated();

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

bool NominalReasoner::hasIndividuals(const DlCompletionTree* node)
{
    auto& label = node->label().getLabel(false);

    for (auto p = label.begin(); p != label.end(); p++)
    {
        auto pName = (*p).bp();

        if (pName > 0 && (DLHeap[pName].Type() == dtPSingleton || DLHeap[pName].Type() == dtNSingleton))
            return true;
    }

    return false;
}

std::vector<TIndividual*> NominalReasoner::getIndividuals(const DlCompletionTree* node)
{
    std::vector<TIndividual*> individuals;

    auto& label = node->label().getLabel(false);

    for (auto p = label.begin(); p != label.end(); p++)
    {
        auto pName = (*p).bp();

        if (pName > 0 && (DLHeap[pName].Type() == dtPSingleton || DLHeap[pName].Type() == dtNSingleton))
            individuals.push_back(static_cast<TIndividual*>(DLHeap[pName].getConcept()));
    }

    return individuals;
}

void NominalReasoner::followTransition(std::map<TIndividual*, std::map<const TRole*, std::set<TIndividual*>>>& role_map, const DlCompletionTree* first, const TRole* role, const DlCompletionTree* current, const RoleAutomaton& automaton, RAState state, std::map<RAState, std::set<const DlCompletionTree*>>& visited)
{
    // Check whether we already visited this function with this state and this node
    // If so we won't be able to derive anything new - just return

    if (visited.count(state) == 0)
        visited[state] = std::set<const DlCompletionTree*>();

    if (visited[state].count(current) > 0)
        return;

    visited[state].insert(current);

    
    // Collect all states accessible through an empty transition from the current state

    std::set<RAState> states;
    states.insert(state);

    for (const RATransition& transition : automaton[state])
    {
        if (transition.begin() == transition.end())
            states.insert(transition.final());
    }

    
    // If the set of states contains the final state - add a relation to the map of individual relations

    if (states.count(automaton.final()) > 0)
    {
        for (TIndividual* a : getIndividuals(first))
        {
            for (TIndividual* b : getIndividuals(current))
            {
                if (role_map.count(a) == 0)
                    role_map[a] = std::map<const TRole*, std::set<TIndividual*>>();

                if (role_map[a].count(role) == 0)
                    role_map[a][role] = std::set<TIndividual*>();

                role_map[a][role].insert(b);
            }
        }
    }

    
    // Check the current node for possible (non-empty) transitions from the current state and follow them

    for (auto p_arc = current->begin(); p_arc != current->end(); p_arc++)
    {
        DlCompletionTreeArc* arc = *p_arc;

        const TRole* next_role = arc->getRole();

        if (next_role != nullptr)
        {
            const DlCompletionTree* next_node = arc->getArcEnd();

            for (RAState state : states)
            {
                for (const RATransition& transition : automaton[state])
                {
                    for (RATransition::const_iterator q = transition.begin(); q != transition.end(); q++)
                    {
                        if (*q == next_role)
                            followTransition(role_map, first, role, next_node, automaton, transition.final(), visited);
                    }
                }
            }
        }
    }
}

void NominalReasoner::precacheRelated()
{
    auto& ORM = tBox.getORM();
    std::map<TIndividual*, std::map<const TRole*, std::set<TIndividual*>>> role_map;

    
    // Collect information of all relations going out of all individual nodes in the completion graph

    for (auto p_node = CGraph.begin(); p_node != CGraph.end(); p_node++)
    {
        const DlCompletionTree* node = *p_node;

        if (hasIndividuals(node))
        {
            for (RoleMaster::iterator p = ORM.begin(); p != ORM.end(); p++)
            {
                const RoleAutomaton& automaton = (*p)->getAutomaton();
                std::map<RAState, std::set<const DlCompletionTree*>> visited;
                followTransition(role_map, node, *p, node, automaton, automaton.initial(), visited);
            }
        }
    }


    // Set individual cache for all collected relations

    for (auto& ind_roles : role_map)
    {
        TIndividual* a = ind_roles.first;

        for (auto& role_obj : ind_roles.second)
        {
            const TRole* role = role_obj.first;
            a->setRelatedCache(role, std::vector<const TIndividual*>(role_obj.second.begin(), role_obj.second.end()));
        }
    }


    // For the rest - either copy related from the role synonym or set up an empty cache

   std::vector<const TIndividual*> empty_cache;

    for (auto& individual : Nominals)
    {
        for (RoleMaster::iterator role = ORM.begin(); role != ORM.end(); role++)
        {
            if (!individual->hasRelatedCache(*role))
            {
                TRole* synonym = static_cast<TRole*>((*role)->getSynonym());

                if (synonym != nullptr && individual->hasRelatedCache(synonym))
                    individual->setRelatedCache(*role, individual->getRelatedCache(synonym));
                else
                    individual->setRelatedCache(*role, empty_cache);
            }
        }
    }
}
