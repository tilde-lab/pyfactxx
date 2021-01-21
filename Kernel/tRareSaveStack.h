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

#ifndef TRARESAVESTACK_H
#define TRARESAVESTACK_H

#include <vector>

#include "globaldef.h"
#include "tRestorer.h"

/**
 *	Stack for Saving/Restoring rarely changing information.
 *	Uses self-contained Restorer as a way to update state of object.
 */
class TRareSaveStack
{
protected:	// typedefs
		/// vector of restorers
	typedef std::vector<TRestorer*> TBaseType;

protected:	// members
		/// heap of saved objects
	TBaseType Base;
		/// current level
	unsigned int curLevel = InitBranchingLevelValue;

public:		// interface
		/// empty c'tor: stack will most likely be empty
	TRareSaveStack() = default;
		/// d'tor
	~TRareSaveStack() { clear(); }

	// stack operations

		/// increment current level
	void incLevel ( void ) { ++curLevel; }
		/// check that stack is empty
	bool empty ( void ) const { return Base.empty(); }
		/// add a new object to the stack
	void push ( TRestorer* p )
	{
		p->setLevel(curLevel);
		Base.push_back(p);
	}
		/// get all object from the top of the stack with levels >= LEVEL
	void restore ( unsigned int level )
	{
		curLevel = level;
		while ( !Base.empty() )
		{
			TRestorer* cur = Base.back();
			if ( cur->level() <= level )
				break;

			// need to restore: restore last element, remove it from stack
			cur->restore();
			delete cur;
			Base.pop_back();
		}
	}
		/// clear stack
	void clear ( void )
	{
		for ( TBaseType::iterator p = Base.begin(), p_end = Base.end(); p < p_end; ++p )
			delete *p;
		Base.clear();
		curLevel = InitBranchingLevelValue;
	}
}; // TRareSaveStack

#endif
