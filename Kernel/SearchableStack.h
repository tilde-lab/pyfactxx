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

#ifndef SEARCHABLESTACK_H
#define SEARCHABLESTACK_H

// template class for stack with opportunity of search duplicate elements
#include <vector>

template <typename T>
class SearchableStack
{
protected:
	std::vector<T> Base;

public:
	// from vector.h
  typedef T value_type;
  typedef typename std::vector<T>::pointer pointer;
  typedef typename std::vector<T>::const_pointer const_pointer;
  typedef typename std::vector<T>::iterator iterator;
  typedef typename std::vector<T>::const_iterator const_iterator;
  typedef typename std::vector<T>::reference reference;
  typedef typename std::vector<T>::const_reference const_reference;

public:

	/***********  constructors  **************/
	SearchableStack ( void ) {}
	SearchableStack ( const SearchableStack<T>& v ) : Base (v.Base) {}

	SearchableStack& operator = ( const SearchableStack<T>& v )
	{
		Base = v.Base;
		return *this;
	}

	/***********  iterators  **************/
  iterator begin() { return Base.begin(); }
  const_iterator begin() const { return Base.begin(); }
  iterator end() { return Base.end(); }
  const_iterator end() const { return Base.end(); }

	/***********  stack operations  **************/
	void push ( const T& val ) { Base.push_back(val); }
	reference top ( void ) { return Base.back(); }
	const_reference top ( void ) const { return Base.back(); }
	void pop ( void ) { Base.pop_back(); }
/*	iterator find ( const T& val ) const
	{
		for ( iterator p = begin(); p != end(); ++p )
			if ( *p == val )
				return p;

		return end();
	}*/
	const_iterator find ( const T& val ) const
	{
		for ( const_iterator p = begin(); p != end(); ++p )
			if ( *p == val )
				return p;

		return end();
	}
	bool contains ( const T& val ) const { return ( find(val) != end() ); }
	bool empty ( void ) const { return Base.empty (); }
};	// SearchableStack

#endif
