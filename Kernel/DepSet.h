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

#ifndef DEPSET_H
#define DEPSET_H

#include "tDepSet.h"

// define type for dependency set
typedef TDepSet DepSet;

// common operations with the dep-set
template <typename O>
inline O& operator << ( O& o, const DepSet& s )
{ return s.print(o); }

inline DepSet operator + ( const DepSet& ds1, const DepSet& ds2 )
{
	DepSet ret(ds1);
	return ret += ds2;
}

#endif
