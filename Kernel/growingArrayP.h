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

#ifndef GROWINGARRAYP_H
#define GROWINGARRAYP_H

#include <vector>

using std::size_t;

/**
 *	Generic class for structures which creates elements (by pointers) and re-use
 *	them (does not delete things).  Derived types may add operations.
 */
template <typename T>
class growingArrayP
{
protected:	// typedefs
		/// type of the heap
	typedef std::vector<T*> baseType;

public:		// typedefs
		/// heap's iterator
	typedef typename baseType::iterator iterator;

protected:	// members
		/// heap itself
	baseType Base;
		/// index of the next unallocated entry
	size_t last = 0;

protected:	// methods
		/// tunable method for creating new object
	virtual T* createNew ( void ) { return new T; }
		/// init vector [B,E) with new objects T
	void initArray ( iterator b, iterator e )
	{
		for ( iterator p = b; p != e; ++p )
			*p = createNew();
	}
		/// increase heap size
	void grow ( void )
	{
		size_t size = Base.size();
		Base.resize(size?size*2:1);
		initArray ( Base.begin()+(long)size, Base.end() );
	}
		/// ensure that size of vector is enough to fit the last element
	void ensureHeapSize ( void )
	{
		if ( last >= Base.size() )
			grow();
	}
		/// ensure that size of vector is enough to keep N elements
	void ensureHeapSize ( size_t n )
	{
		while ( n >= Base.size() )
			grow();
	}

public:		// interface
		/// c'tor: make SIZE objects
	explicit growingArrayP ( size_t size = 0 ) : Base(size)
	{
		initArray ( Base.begin(), Base.end() );
	}
		/// no copy c'tor
	growingArrayP ( const growingArrayP& ) = delete;
		/// no assignment
	growingArrayP& operator= ( const growingArrayP& ) = delete;
		/// d'tor: delete all allocated objects
	virtual ~growingArrayP()
	{
		for ( auto p = Base.rbegin(), p_end = Base.rend(); p != p_end; ++p )
			delete *p;
	}

		/// resize an array
	void resize ( size_t n ) { ensureHeapSize(n); last = n; }
		/// get the number of elements
	size_t size ( void ) const { return last; }
		/// check if heap is empty
	bool empty ( void ) const { return last == 0; }
		/// mark all array elements as unused
	virtual void clear ( void ) { last = 0; }
}; // growingArrayP

#endif
