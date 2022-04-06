/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2003-2015 Dmitry Tsarkov and The University of Manchester
Copyright (C) 2015-2017 Dmitry Tsarkov

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
*/

//-------------------------------------------------------------------------
//
//  Scanner implementation for FaCT++ program
//
//-------------------------------------------------------------------------
#include <cctype>		// isalnum

#include "fpp_assert.h"

#include "scanner.h"

// main methods
bool TsScanner :: isLegalIdChar ( char c ) const	//id=[_a..z0-9[].]
{
	switch (c)
	{
	case '(':
	case ')':
	case ';':
	case '|':
		return false;
	default:
		return !isspace(c) && !eof(c);
	}
}

// Word must be in a CAPITAL LETTERS
void TsScanner :: FillBuffer ( char c )
{
	unsigned int i = 0;
	LexBuff [0] = c;

	while ( i < MaxIDLength && isLegalIdChar ( c = NextChar() ) )
	  LexBuff[++i] = c;

	LexBuff[++i] = 0;

	if ( i > MaxIDLength )
	{
		fpp_assert ( i == MaxIDLength + 1 );
		std::cerr << "Identifier was restricted to " << LexBuff << "\n";
		do {
			c = NextChar();
		} while ( isLegalIdChar(c) );
	}
	// OK or read the end of ID

	PutBack ( c );
}

void TsScanner :: FillNameBuffer ( char c )
{
	unsigned int i = 0;
	const char stop = c;

	while ( i <= MaxIDLength && ( c = NextChar() ) != stop )
	  LexBuff[i++] = c;

	LexBuff[i] = 0;

	if ( i > MaxIDLength )
	{
		fpp_assert ( i == MaxIDLength + 1 );
		std::cerr << "Identifier was restricted to " << LexBuff << std::endl;
		do {
			c = NextChar();
		} while ( c != stop );
	}
}

LispToken TsScanner :: GetLex ( void )
{
	char c;

	while ( !eof( c = NextChar() ) )
	{
		if ( c == ' ' || c == '\t' || c == 13 )
			continue;

		if ( c == '\n' )
		{
			CurLine++;
			continue;
		}

		if ( c == ';' )
		{	// Skip comments
			do {
				c = NextChar();
			} while ( c != '\n' && !eof(c) );
			CurLine++;
			continue;
		}

		// one-symbol tokens
		if ( c == '(' )
			return LBRACK;

		if ( c == ')' )
			return RBRACK;

		if ( c == ':' )	// some keyword
		{	// skip colon
			FillBuffer ( NextChar () );
			return ID;
		}

		if ( c == '|' || c == '"' )
		{
			FillNameBuffer ( c );
			return ID;
		}

		if ( isdigit ( c ) )	// number
		{
			FillBuffer ( c );
			return NUM;
		}
		else	// id
		{
			FillBuffer ( c );
			return ID;
		}
	}
	// read EOF - end of lex

	return LEXEOF;
}

// recognize TOP, BOTTOM or general ID
LispToken TsScanner :: getNameKeyword ( void ) const
{
	if ( isKeyword ("*TOP*") || isKeyword ("TOP") )
		return L_TOP;

	if ( isKeyword ("*BOTTOM*") || isKeyword ("BOTTOM") )
		return L_BOTTOM;

	// not a keyword just an ID
	return ID;
}

// concept/role constructors; return BAD_LEX in case of error
LispToken TsScanner :: getExpressionKeyword ( void ) const
{
	if ( isKeyword ("and") )
		return L_AND;

	if ( isKeyword ("or") )
		return L_OR;

	if ( isKeyword ("not") )
		return L_NOT;

	if ( isKeyword ("inv") || isKeyword ("inverse") )
		return L_INV;

	if ( isKeyword ("compose") )
		return L_RCOMPOSITION;

	if ( isKeyword ("project_into") )
		return L_PROJINTO;

	if ( isKeyword ("project_from") )
		return L_PROJFROM;

	if ( isKeyword ("some") )
		return L_EXISTS;

	if ( isKeyword ("all") )
		return L_FORALL;

	if ( isKeyword("min") || isKeyword("at-least") || isKeyword("atleast") )
		return L_GE;

	if ( isKeyword("max") || isKeyword("at-most") || isKeyword("atmost") )
		return L_LE;

	if ( isKeyword ("one-of") )
		return ONEOF;

	if ( isKeyword ("self-ref") )
		return REFLEXIVE;

	if ( isKeyword ("string") )
		return STRING;

	if ( isKeyword ("number") )
		return NUMBER;

	if ( isKeyword ("real") )
		return REAL;

	if ( isKeyword ("bool") )
		return BOOL;

	if ( isKeyword("gt") )
		return DTGT;

	if ( isKeyword("lt") )
		return DTLT;

	if ( isKeyword("ge") )
		return DTGE;

	if ( isKeyword("le") )
		return DTLE;

	if ( isKeyword("d-one-of") )
		return DONEOF;

	// not a keyword -- error
	return BAD_LEX;
}

// recognize FaCT++ keywords; return BAD_LEX if not found
LispToken TsScanner :: getCommandKeyword ( void ) const
{
	// definitions
	if ( isKeyword ("defprimconcept") )
		return PCONCEPT;

	if ( isKeyword ("defconcept") )
		return CONCEPT;

	if ( isKeyword ("defprimrole") )
		return PROLE;

	if ( isKeyword ("defdatarole") )
		return DATAROLE;

	if ( isKeyword ("defprimattribute") )
		return PATTR;

	if ( isKeyword ("defindividual") )
		return DEFINDIVIDUAL;

	// general relations
	if ( isKeyword ("implies") || isKeyword ("implies_c") )
		return SUBSUMES;

	if ( isKeyword ("equal_c") )
		return EQUAL_C;

	if ( isKeyword ("disjoint") || isKeyword ("disjoint_c") )
		return DISJOINT;

	if ( isKeyword ("implies_r") )
		return IMPLIES_R;

	if ( isKeyword ("equal_r") )
		return EQUAL_R;

	if ( isKeyword ("disjoint_r") )
		return DISJOINT_R;

	if ( isKeyword ("inverse") )
		return INVERSE;

	// role stuff
	if ( isKeyword ("functional") )
		return FUNCTIONAL;

	if ( isKeyword ("transitive") )
		return TRANSITIVE;

	if ( isKeyword ("reflexive") )
		return REFLEXIVE;

	if ( isKeyword ("irreflexive") )
		return IRREFLEXIVE;

	if ( isKeyword ("symmetric") )
		return SYMMETRIC;

	if ( isKeyword ("asymmetric") )
		return ASYMMETRIC;

	if ( isKeyword ("range") )
		return ROLERANGE;

	if ( isKeyword ("domain") )
		return ROLEDOMAIN;

	// individual stuff
	if ( isKeyword ("instance") )
		return INSTANCE;

	if ( isKeyword ("related") )
		return RELATED;

    if (isKeyword("valueOf"))
        return VALUEOF;

    if ( isKeyword ("same") )
		return SAME;

	if ( isKeyword ("different") )
		return DIFFERENT;

	if ( isKeyword ("fairness") )
		return FAIRNESS;

	// not a keyword -- error
	return BAD_LEX;
}
