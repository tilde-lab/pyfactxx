/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2006-2015 Dmitry Tsarkov and The University of Manchester
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

#include "RAutomaton.h"
#include "tRole.h"

/// check whether transition is TopRole one
bool
RATransition :: isTop ( void ) const
{
	return label.size() == 1 && unlikely(label.front()->isTop());
}

/// set up state transitions: no more additions to the structure
void
RAStateTransitions :: finalise ( RAState state, size_t nRoles, bool data )
{
	from = state;
	DataRole = data;
	ApplicableRoles.ensureMaxSetSize(nRoles);
	// fills the set of recognisable roles
	for ( const auto& trans: Base )
		std::for_each ( trans.begin(), trans.end(), [&] (const TRole* R) { ApplicableRoles.add(R->getIndex()); } );
}

/// add information from TRANS to existing transition between the same states. @return false if no such transition found
bool
RAStateTransitions :: addToExisting ( const RATransition& trans )
{
	RAState to = trans.final();
	bool tEmpty = trans.empty();
	for ( auto& transition: Base )
		if ( transition.final() == to && transition.empty() == tEmpty )
		{	// found existing transition
			transition.addIfNew(trans);
			return true;
		}
	// no transition from->to found
	return false;
}

void
RATransition::Print ( std::ostream& o, RAState from ) const
{
	o << "\n" << from << " -- ";
	if ( empty() )
		o << "e";
	else
	{
		const_iterator p = label.begin();
		o << '"' << (*p)->getName() << '"';

		for ( ++p; p != label.end(); ++p )
			o << ",\"" << (*p)->getName() << '"';
	}
	o << " -> " << final();
}

void
RoleAutomaton :: addCopy ( const RoleAutomaton& RA )
{
	for ( RAState i = 0; i < RA.size(); ++i )
	{
		RAState from = map[i];
		RAStateTransitions& RST = Base[from];
		const RAStateTransitions& RSTOrig = RA[i];

		if ( RSTOrig.empty() )
			continue;

		for ( auto& origTrans: RSTOrig )
		{
			// find a map from original transitions' end to the updated transitions' end
			RAState to = origTrans.final();
			RAState final = map[to];
			checkTransition ( from, final );
			RATransition trans(final);
			trans.add(origTrans);

			// try to merge transitions going to the original final state
			// we will move the TRANS in the ADD case, so the temporary copy
			if ( to != 1 || !RST.addToExisting(trans) )
				RST.add(RATransition(trans));
		}
	}
}

/// init internal map according to RA size, with new initial state from chainState and final (FRA) states
void
RoleAutomaton :: initMap ( size_t RASize, RAState fRA )
{
	map.resize(RASize);
	// new state in the automaton
	RAState newState = (RAState) size()-1;

	// fill initial state; it is always known in the automaton
	map[0] = iRA;

	// fills the final state; if it is not known -- adjust newState
	if ( fRA >= size() )
	{
		fRA = (RAState) size();	// make sure we don't create an extra unused state
		++newState;
	}
	map[1] = fRA;

	// check transitions as it may turns out to be a single transition
	checkTransition ( iRA, fRA );

	// set new initial state
	iRA = fRA;

	// fills the rest of map
	for ( unsigned int i = 2; i < RASize; ++i )
		map[i] = ++newState;

	// reserve enough space for the new automaton
	ensureState(newState);
}

/// add an Automaton to the chain that would start from the iRA; OSAFE shows the safety of a previous automaton in a chain
bool
RoleAutomaton :: addToChain ( const RoleAutomaton& RA, bool oSafe, RAState fRA )
{
	fpp_assert(!isCompleted());
	bool needFinalTrans = ( fRA < size() && !RA.isOSafe() );
	// we can skip transition if chaining automaton are i- and o-safe
	if ( !oSafe && !RA.isISafe() )
		nextChainTransition(newState());
	// check whether we need an output transition
	initMap ( RA.size(), needFinalTrans ? (RAState) size() : fRA );
	addCopy(RA);
	if ( needFinalTrans )
		nextChainTransition(fRA);

	return RA.isOSafe();
}
