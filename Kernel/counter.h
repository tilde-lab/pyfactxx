/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2010-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef COUNTER_H
#define COUNTER_H

/// class counter from Wikipedia:curiously recurring template pattern
template <typename T>
struct counter
{
		/// number of created T-objects
	static unsigned int objects_created;
		/// number of alive T-objects ATM of checking
	static unsigned int objects_alive;
		/// c'tor: inc counters
	counter ( void )
	{
		++objects_created;
		++objects_alive;
	}
		/// d'tor: dec counters
	virtual ~counter()
	{
		--objects_alive;
	}
}; // counter

template <typename T> unsigned int counter<T>::objects_created(0);
template <typename T> unsigned int counter<T>::objects_alive(0);

#endif
