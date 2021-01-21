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

#ifndef TSAVESTACK_H
#define TSAVESTACK_H

#include "growingArrayP.h"
#include "fpp_assert.h"

/**
 *	Template stack for Saving/Restoring internal state of template parameter.
 *  Implemented as a growing array of pointers with more ops
 */
template <typename T>
class TSaveStack: public growingArrayP<T>
{
public:		// interface
	// stack operations

		/// get a new object from the stack;it will be filled by caller
	T* push ( void )
	{
		this->ensureHeapSize();
		return this->Base[this->last++];
	}
		/// get an object from the top of the stack
	T* pop ( void ) { return this->Base[--this->last]; }
		/// get an object from a fixed depth
	T* pop ( unsigned int depth )
	{
		fpp_assert ( this->last >= depth );
		this->last = depth;
		return pop();
	}
		/// get an object from a fixed depth
	T* top ( unsigned int depth )
	{
		fpp_assert ( this->last >= depth );
		this->last = depth;
		return this->Base[depth-1];
	}
}; // TSaveStack

#endif
