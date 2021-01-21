/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2008-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef TFASTSET_H
#define TFASTSET_H

#include <algorithm>
#include <vector>

/// the implementation of the set with constant-time "in" check
template <typename T>
class TFastSet
{
protected:	// internal types
		/// type for the array of values
	typedef std::vector<T> ValArray;
		/// type of the index
	typedef size_t indexType;

public:		// type interface
		/// RW iterator on values
	typedef typename ValArray::iterator iterator;
		/// RO iterator on values
	typedef typename ValArray::const_iterator const_iterator;

protected:	// members
		/// values to be kept in the set
	ValArray Value;
		/// indices of the used values
	std::vector<indexType> Index;

protected:	// methods
		/// transform element into an integer; is tunable
	static indexType toInt ( const T& t ) { return static_cast<indexType>(t); }

public:		// interface
		/// ensure that the index can cope with sets not larger that SIZE
	void ensureMaxSetSize ( size_t size ) { Index.resize(size); }
		/// empty c'tor
	TFastSet ( void ) = default;
		/// c'tor with a given max set SIZE
	explicit TFastSet ( size_t size ) { ensureMaxSetSize(size); }
		/// copy c'tor
	TFastSet ( const TFastSet& ) = default;
		/// move c'tor
	TFastSet ( TFastSet&& ) = default;
		/// assignment
	TFastSet& operator = ( const TFastSet& ) = default;
		/// move assignment
	TFastSet& operator = ( TFastSet && ) = default;
		/// reserve the set size to the SIZE elements
	void reserve ( size_t size ) { Value.reserve(size); }

	// set iterators

		/// begin RW iterator
	iterator begin ( void ) { return Value.begin(); }
		/// end RW iterator
	iterator end ( void ) { return Value.end(); }
		/// begin RO iterator
	const_iterator begin ( void ) const { return Value.begin(); }
		/// end RO iterator
	const_iterator end ( void ) const { return Value.end(); }

	// set operations -- containment

		/// check whether T is in set
	bool in ( const T& t ) const
	{
		auto index = Index[toInt(t)];
		return index < Value.size() && Value[index] == t;
	}
		/// check whether FS contains all the elements from the given set
	bool operator <= ( const TFastSet& fs ) const
	{
		return std::all_of ( begin(), end(), [&] (const T& val) { return fs.in(val); } );
	}
		/// check whether given set contains all the elements from FS
	bool operator >= ( const TFastSet& fs ) const { return fs <= *this; }
		/// equality of the sets
	bool operator == ( const TFastSet& fs ) const { return size() == fs.size() && (*this <= fs); }
		/// strict containment
	bool operator < ( const TFastSet& fs ) const { return size() < fs.size() && (*this <= fs); }
		/// strict containment
	bool operator > ( const TFastSet& fs ) const { return fs < *this; }

	// set operations -- adding

		/// add an element T to a set; assume T is not in the set (unique)
	void addu ( const T& t )
	{
		Index[toInt(t)] = Value.size();
		Value.emplace_back(t);
	}
		/// add element T to a set
	void add ( const T& t ) { if ( !in(t) ) addu(t); }

	// misc

		/// check whether the set is empty
	bool empty ( void ) const { return Value.empty(); }
		/// get the size of the set
	size_t size ( void ) const { return Value.size(); }
		/// clear the set
	void clear ( void ) { Value.clear(); }
		/// set the size of the set to be a VALUE
	void resize ( size_t value ) { Value.resize(value); }
}; // TFastSet

#endif
