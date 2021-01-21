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

#ifndef RAUTOMATON_H
#define RAUTOMATON_H

#include <vector>
#include <iosfwd>

#include "fpp_assert.h"
#include "tFastSet.h"

class TRole;

/// state of the role automaton
// TODO: think to use short
typedef unsigned int RAState;

/// transition in the automaton for the role in RIQ-like languages
class RATransition
{
protected:	// typedefs
		/// set or roles that labels transition
	typedef std::vector<const TRole*> TLabel;

public:		// typedefs
		/// iterator over roles
	typedef TLabel::const_iterator const_iterator;

protected:	// members
		/// set of roles that may affect the transition
	TLabel label;
		/// final state of the transition
	RAState state;

public:		// interface
		/// create a transition to given state
	explicit RATransition ( RAState st ) : state(st) {}
		/// create a transition with a given label R to given state ST
	RATransition ( RAState st, const TRole* R ) : state(st) { add(R); }
		/// copy c'tor
	RATransition ( const RATransition& ) = default;
		/// move c'tor
	RATransition ( RATransition&& ) = default;
		/// assignment
	RATransition& operator = ( const RATransition& ) = default;
		/// move assignment
	RATransition& operator = ( RATransition&& ) = default;

		/// check whether transition is applicable wrt role R
	bool applicable ( const TRole* R ) const
	{
		return std::any_of ( label.cbegin(), label.cend(), [=] (const TRole* S) { return S == R; } );
	}

	// update the transition

		/// add role R to transition's label
	void add ( const TRole* R ) { label.push_back(R); }
		/// add role R to transition's label
	void addIfNew ( const TRole* R ) { if ( !applicable(R) ) add(R); }
		/// add label of transition TRANS to transition's label
	void add ( const RATransition& trans )
		{ label.insert ( label.end(), trans.label.cbegin(), trans.label.cend() ); }
		/// add label of transition TRANS to transition's label only if they are new
	void addIfNew ( const RATransition& trans )
	{
		std::for_each ( trans.label.cbegin(), trans.label.cend(), [=] (const TRole* R) { addIfNew(R); } );
	}

	// query the transition

		/// get the 1st role in (multi-)transition
	const_iterator begin ( void ) const { return label.begin(); }
		/// get the last role in (multi-)transition
	const_iterator end ( void ) const { return label.end(); }

		/// give a final point of the transition
	RAState final ( void ) const { return state; }
		/// check whether transition is empty
	bool empty ( void ) const { return label.empty(); }
		/// check whether transition is TopRole one
	bool isTop ( void ) const;
		/// print the transition starting from FROM
	void Print ( std::ostream& o, RAState from ) const;
}; // RATransition

/// class to represent transitions from a single state in an automaton
class RAStateTransitions
{
protected:	// types
		/// keep all the transitions
	typedef std::vector<RATransition> RTBase;
		/// RW iterators
	typedef RTBase::iterator iterator;

public:		// type interface
		/// RO iterators
	typedef RTBase::const_iterator const_iterator;

protected:	// members
		/// all transitions
	RTBase Base;
		/// set of all roles that can be applied by one of the transitions
	TFastSet<unsigned int> ApplicableRoles;
		/// state from which all the transition starts
	RAState from;
		/// check whether there is an empty transition going from this state
	bool EmptyTransition = false;
		/// true iff there is a top transition going from this state
	bool TopTransition = false;
		/// flag whether the role is data or not (valid only for simple automata)
	bool DataRole;

public:		// interface
		/// empty c'tor
	RAStateTransitions()= default;
		/// copy c'tor
	RAStateTransitions ( const RAStateTransitions& ) = default;
		/// move c'tor
	RAStateTransitions ( RAStateTransitions&& ) = default;
		/// assignment
	RAStateTransitions& operator = ( const RAStateTransitions& ) = default;
		/// move assignment
	RAStateTransitions& operator = ( RAStateTransitions&& ) = default;

		/// set up state transitions: no more additions to the structure
	void finalise ( RAState state, size_t nRoles, bool data );

		/// add a transition from a given state
	void add ( RATransition&& trans )
	{
		if ( trans.empty() )
			EmptyTransition = true;
		if ( trans.isTop() )
			TopTransition = true;
		Base.push_back(std::move(trans));
	}
		/// copy information from TRANS to existing transition between the same states. @return false if no such transition found
	bool addToExisting ( const RATransition& trans );

		/// @return true iff there are no transitions from this state
	bool empty ( void ) const { return Base.empty(); }
		/// @return true iff there is an empty transition from the state
	bool hasEmptyTransition ( void ) const { return EmptyTransition; }
		/// @return true iff there is a top-role transition from the state
	bool hasTopTransition ( void ) const { return TopTransition; }

	// RO access

		/// RO begin
	const_iterator begin ( void ) const { return Base.begin(); }
		/// RO end
	const_iterator end ( void ) const { return Base.end(); }
		/// get FROM state
	RAState getFrom ( void ) const { return from; }
		/// check whether one of the transitions accept R; implementation is in tRole.h
	bool recognise ( const TRole* R ) const;
		/// @return true iff there is only one transition
	bool isSingleton ( void ) const { return Base.size() == 1; }
		/// @return final state of the 1st transition; used for singletons
	RAState getTransitionEnd ( void ) const { return Base.front().final(); }

		/// print all the transitions starting from the state FROM
	void Print ( std::ostream& o ) const
	{
		for ( auto& trans: Base )
			trans.Print ( o, from );
	}
}; // RAStateTransitions

/// automaton for the role in RIQ-like languages
class RoleAutomaton
{
protected:	// members
		/// all transitions of the automaton, grouped by a starting state
	std::vector<RAStateTransitions> Base;
		/// maps original automata state into the new ones (used in copyRA)
	std::vector<unsigned int> map;
		/// initial state of the next automaton in chain
	RAState iRA = 0;
		/// flag whether automaton is input safe
	bool ISafe = true;
		/// flag whether automaton is output safe
	bool OSafe = true;
		/// flag for the automaton to be completed
	bool Complete = false;

protected:	// methods
		/// make sure that STATE exists in the automaton (update ton's size)
	void ensureState ( RAState state )
	{
		if ( state >= Base.size() )
			Base.resize(state+1);
	}

		/// state that the automaton is i-unsafe
	void setIUnsafe ( void ) { ISafe = false; }
		/// state that the automaton is o-unsafe
	void setOUnsafe ( void ) { OSafe = false; }
		/// check whether transition between FROM and TO breaks safety
	void checkTransition ( RAState from, RAState to )
	{
		if ( from == final() )
			setOUnsafe();
		if ( to == initial() )
			setIUnsafe();
	}

		/// add TRANSition leading from a state FROM; all states are known to fit the ton
	void addTransition ( RAState from, RATransition&& trans )
	{
		checkTransition ( from, trans.final() );
		Base[from].add(std::move(trans));
	}
		/// make the internal chain transition (between chainState and TO)
	void nextChainTransition ( RAState to )
	{
		addTransition ( iRA, RATransition(to) );
		iRA = to;
	}

		/// add copy of the RA to given one; use internal MAP to renumber the states
	void addCopy ( const RoleAutomaton& RA );
		/// init internal map according to RA size and final (FRA) states
	void initMap ( size_t RASize, RAState fRA );

public:		// interface
		/// empty c'tor
	RoleAutomaton() { ensureState(1); }
		/// copy c'tor
	RoleAutomaton ( const RoleAutomaton& ) = default;
		/// move c'tor
	RoleAutomaton ( RoleAutomaton&& ) = default;
		/// assignment
	RoleAutomaton& operator= ( const RoleAutomaton& ) = default;
		/// move assignment
	RoleAutomaton& operator= ( RoleAutomaton&& ) = default;

	// access to states

		/// get the initial state
	RAState initial ( void ) const { return 0; }
		/// get the final state
	RAState final ( void ) const { return 1; }
		/// create new state
	RAState newState ( void )
	{
		RAState ret = (RAState) Base.size();
		ensureState(ret);
		return ret;
	}

		/// get access to the transitions starting from STATE
	const RAStateTransitions& operator [] ( RAState state ) const { return Base[state]; }
		/// set up all transitions passing number of roles
	void setup ( size_t nRoles, bool data )
	{
		for ( RAState i = 0; i < Base.size(); ++i )
			Base[i].finalise ( i, nRoles, data );
	}

	// automaton's construction

		/// add TRANSition leading from a given STATE; check whether all states are correct
	void addTransitionSafe ( RAState state, RATransition&& trans )
	{
		ensureState(state);
		ensureState(trans.final());
		addTransition ( state, std::move(trans) );
	}

	// chain automaton creation

		/// make the beginning of the chain
	void initChain ( RAState from ) { iRA = from; }
		/// add an Automaton to the chain that would start from the iRA; OSAFE shows the safety of a previous automaton in a chain
	bool addToChain ( const RoleAutomaton& RA, bool oSafe, RAState fRA );
		/// add an Automaton to the chain with a default final state
	bool addToChain ( const RoleAutomaton& RA, bool oSafe ) { return addToChain ( RA, oSafe, (RAState) size()+1 ); }

	// i/o safety

		/// get the i-safe value
	bool isISafe ( void ) const { return ISafe; }
		/// get the o-safe value
	bool isOSafe ( void ) const { return OSafe; }

	// automaton completeness

		/// mark an automaton as completed
	void setCompleted ( void ) { Complete = true; }
		/// check whether automaton is completed
	bool isCompleted ( void ) const { return Complete; }

	// get some stats

		/// return number of distinct states
	size_t size ( void ) const { return Base.size(); }
		/// @return true iff the automaton is simple
	bool isSimple ( void ) const
	{
		fpp_assert(isCompleted());
		return size() == 2 && ISafe && OSafe;
	}

	// add single RA

		/// add RA from a subrole to given one
	void addRA ( const RoleAutomaton& RA )
	{
		fpp_assert(!isCompleted());
		if ( RA.isSimple() )
		{
			bool ok = Base[initial()].addToExisting(*RA[initial()].begin());
			fpp_assert(ok);
		}
		else
		{
			initChain(initial());
			addToChain ( RA, /*oSafe=*/false, final() );
		}
	}

		/// print an automaton
	void Print ( std::ostream& o ) const
	{
		for ( auto& StateTransitions: Base )
			StateTransitions.Print(o);
	}
}; // RoleAutomaton

#endif

