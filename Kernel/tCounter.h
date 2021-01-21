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

#ifndef TCOUNTER_H
#define TCOUNTER_H

/** define class that allows user to have an integer with restricted
 *  access (i.e. get value, increment and reset). This is useful when
 *  integral value used as a static var of some class
 */
template <typename T>
class TCounter
{
public:		// type interface
		/// define integral type of a counter
	typedef T IntType;

protected:	// members
		/// counter itself
	IntType counter;

public:		// interface
		/// init c'tor
	explicit TCounter ( const IntType n ) : counter(n) {}
		/// copy c'tor
	template <typename U>
	explicit TCounter ( const TCounter<U>& copy ) : counter(copy.counter) {}
		/// assignment
	template <typename U>
	TCounter& operator= ( const TCounter<U>& copy )
	{
		counter = copy.counter;
		return *this;
	}

	// operators

		/// get value of a counter
	IntType val ( void ) const { return counter; }
		/// increment value of a counter; @return new value
	IntType inc ( void ) { return ++counter; }
		/// set new value of a counter
	void reset ( const IntType n ) { counter = n; }
}; // TCounter

#endif
