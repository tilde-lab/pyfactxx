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

//-------------------------------------------------------------------------
//
//  Grammar for FaCT++
//
//-------------------------------------------------------------------------
#ifndef GRAMMAR_H
#define GRAMMAR_H

// constants for symbols
enum Token {
	AND,
	OR,
	NOT,
	INV,
	RCOMPOSITION,	// role composition
	PROJINTO,		// role projection into
	PROJFROM,		// role projection from
	SELF,

	TOP,
	BOTTOM,
	EXISTS,
	FORALL,
	GE,
//	ATLEAST = GE,
	LE,
//	ATMOST = LE,

	// common meta-symbols
	DATAEXPR,	// any data expression: data value, [constrained] datatype

	// more precise ID's discretion
	CNAME,	// name of a concept
	INAME,	// name of a singleton
	RNAME,	// name of a role
	DNAME,	// name of a data role
};

// some multi-case defines

// any name (like ID)
#define NAME CNAME: case INAME: case RNAME: case DNAME

#endif
