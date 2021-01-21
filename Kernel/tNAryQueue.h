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

#ifndef TNARYQUEUE_H
#define TNARYQUEUE_H

#include <vector>

/// queue for n-ary operations
template <typename Expression>
class TNAryQueue
{
public:		// types
		/// type of the array
	typedef std::vector<Expression*> DLExpressionArray;

protected:	// types
		/// type of a base storage
	typedef std::vector<DLExpressionArray*> BaseType;
		/// base storage iterator
	typedef typename BaseType::iterator iterator;

private:	// members
		/// all lists of arguments for n-ary predicates/commands
	BaseType Base;
		/// pre-current index of n-ary statement
	size_t level = 0;

private:	// methods
		/// increase size of internal AUX array
	void grow ( void )
	{
		size_t n = Base.size();
		Base.resize(2*n);
		for ( iterator p = Base.begin()+(long)n, p_end = Base.end(); p < p_end; ++p )
			*p = new DLExpressionArray;
	}

public:		// interface
		/// empty c'tor
	TNAryQueue()
	{
		Base.push_back(nullptr);	// corresponds to level 0 -- never used
		Base.push_back(new DLExpressionArray);
	}
		/// d'tor
	~TNAryQueue()
	{
		for ( iterator q = Base.begin(), q_end = Base.end(); q < q_end; ++q )
			delete *q;
	}

	// queue interface

		/// init the next argument list
	void openArgList ( void )
	{
		if ( ++level >= Base.size() )
			grow();
		Base[level]->clear();
	}
		/// add the next element to the current argument list
	void addArg ( Expression* p ) { Base[level]->push_back(p); }

		/// get access to the last closed argument list
	const DLExpressionArray& getLastArgList ( void ) { return *Base[level--]; }
}; // TNAryQueue

#endif
