/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2005-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef DELETELESSALLOCATOR_H
#define DELETELESSALLOCATOR_H

#include "growingArrayP.h"

/**
 * Class for the allocator that does not allowed 'delete'. Instead
 * it allows user to reuse all allocated memory.
 */
template <typename T>
class DeletelessAllocator: public growingArrayP<T>
{
public:
		/// get a new object from the heap
	T* get ( void )
	{
		this->ensureHeapSize();
		return this->Base[this->last++];
	}
}; // DeletelessAllocator

#endif
