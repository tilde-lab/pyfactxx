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

#ifndef TSETASTREE_H
#define TSETASTREE_H

#include <set>

// implement model cache set as a tree-set
class TSetAsTree
{
protected:	// types
		/// base type
	typedef std::set<unsigned int> BaseType;

protected:	// members
		/// set implementation
	BaseType Base;
		/// maximal number of elements
	unsigned int nElems;

public:		// interface
		/// empty c'tor taking max possible number of elements in the set
	explicit TSetAsTree ( unsigned int size ) : nElems(size) {}
		/// copy c'tor
	TSetAsTree ( const TSetAsTree& ) = default;
		/// move c'tor
	TSetAsTree ( TSetAsTree&& ) = default;
		/// assignment
	TSetAsTree& operator= ( const TSetAsTree& ) = default;
		/// move assignment
	TSetAsTree& operator= ( TSetAsTree&& ) = default;
		/// empty d'tor
	~TSetAsTree() = default;

		/// adds given index to the set
	void insert ( unsigned int i )
	{
#	ifdef ENABLE_CHECKING
		fpp_assert ( i > 0 );
#	endif
		Base.insert(i);
	}
		/// completes the set with [1,n)
	void completeSet ( void )
	{
		for ( unsigned int i = 1; i < nElems; ++i )
			Base.insert(i);
	}
		/// adds the given set to the current one
	TSetAsTree& operator |= ( const TSetAsTree& is )
	{
		Base.insert ( is.Base.begin(), is.Base.end() );
		return *this;
	}
		/// clear the set
	void clear ( void ) { Base.clear(); }

		/// check whether the set is empty
	bool empty ( void ) const { return Base.empty(); }
		/// check whether I contains in the set
	bool contains ( unsigned int i ) const { return Base.find(i) != Base.end(); }
		/// check whether the intersection between the current set and IS is nonempty
	bool intersects ( const TSetAsTree& is ) const
	{
		if ( Base.empty() || is.Base.empty() )
			return false;

		BaseType::const_iterator p1 = Base.begin(), p1_end = Base.end(), p2 = is.Base.begin(), p2_end = is.Base.end();
		while ( p1 != p1_end && p2 != p2_end )
			if ( *p1 == *p2 )
				return true;
			else if ( *p1 < *p2 )
				++p1;
			else
				++p2;

		return false;
	}
		/// prints the set in a human-readable form
	void print ( std::ostream& o ) const
	{
		o << "{";
		if ( !empty() )
		{
			BaseType::const_iterator p = Base.begin(), p_end = Base.end();
			o << *p;
			while ( ++p != p_end )
				o << ',' << *p;
		}
		o << "}";
	}
	typedef BaseType::const_iterator const_iterator;
	const_iterator begin ( void ) const { return Base.begin(); }
	const_iterator end ( void ) const { return Base.end(); }

		/// size of a set
	size_t size ( void ) const { return Base.size(); }
		/// maximal size of a set
	unsigned int maxSize ( void ) const { return nElems; }
}; // TSetAsTree

#endif
