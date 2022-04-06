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
#ifndef LISPGRAMMAR_H
#define LISPGRAMMAR_H

// constants for symbols
enum LispToken {
	BAD_LEX = 50,

	UNUSED,	// never used one

	LEXEOF,

	// symbols
	LBRACK,
	RBRACK,
	L_AND,
	L_OR,
	L_NOT,
	L_INV,
	L_RCOMPOSITION,	// role composition
	L_PROJINTO,		// role projection into
	L_PROJFROM,		// role projection from

	L_TOP,
	L_BOTTOM,
	L_EXISTS,
	L_FORALL,
	L_GE,
//	ATLEAST = GE,
	L_LE,
//	ATMOST = LE,

	// common metasymbols
	ID,		// should NOT appear in KB -- use *NAME instead
	NUM,
	L_DATAEXPR,	// any data expression: data value, [constrained] datatype

	// FaCT commands
	// definitions
	PCONCEPT,
	PROLE,
	PATTR,
	CONCEPT,
	DATAROLE,

	// FaCT++ commands for internal DataTypes
	NUMBER,
	STRING,
	REAL,
	BOOL,

	// datatype operations command names -- used only as an external commands
	DTGT,
	DTLT,
	DTGE,
	DTLE,
	DONEOF,

	// general commands
	SUBSUMES,
	DISJOINT,
	EQUAL_C,

	// new for roles
	INVERSE,
	EQUAL_R,
	IMPLIES_R,
	DISJOINT_R,
	FUNCTIONAL,
	TRANSITIVE,
	REFLEXIVE,
	IRREFLEXIVE,
	SYMMETRIC,
	ASYMMETRIC,
	ROLERANGE,
	ROLEDOMAIN,

	// new for individuals
	DEFINDIVIDUAL,
	INSTANCE,
	RELATED,
	ONEOF,
	SAME,
	DIFFERENT,
    VALUEOF,

	// fairness constraints
	FAIRNESS,
};

#endif
