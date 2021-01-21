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

#ifndef TLEXEME_H
#define TLEXEME_H

#include "grammar.h"
#include "tNamedEntry.h"

/// Lexeme (smallest lexical element) in a syntax tree
class TLexeme
{
private:	// members
		/// Lexeme's Token
	Token token;
		/// pointer to information (for names)
	union
	{
		TNamedEntry* pNE;
		unsigned int data;
	} value;

public:		// interface
		/// default c'tor for pointers
	explicit TLexeme ( Token tok, TNamedEntry* p = nullptr ) : token(tok) { value.pNE = p; }
		/// default c'tor for numbers
	TLexeme ( Token tok, unsigned int val ) : token(tok) { value.data = val; }
		/// Copy c'tor
	TLexeme ( const TLexeme& ) = default;
		/// Assignment
	TLexeme& operator = ( const TLexeme& ) = default;

	// access

		/// get Token of given Lexeme
	Token getToken ( void ) const { return token; }
		/// get name pointer of given lexeme
	TNamedEntry* getNE ( void ) const { return value.pNE; }
		/// get name pointer of given lexeme
	const char* getName ( void ) const { return value.pNE->getName(); }
		/// get data value of given lexeme
	unsigned int getData ( void ) const { return value.data; }

	// comparison

		/// full lexeme comparison (equality)
	bool operator == ( const TLexeme& lex ) const { return ( token == lex.token && value.data == lex.value.data ); }
		/// full lexeme comparison (inequality)
	bool operator != ( const TLexeme& lex ) const { return !( *this == lex ); }
		/// just token comparison (equality)
	bool operator == ( Token tok ) const { return ( token == tok ); }
		/// just token comparison (inequality)
	bool operator != ( Token tok ) const { return ( token != tok ); }
}; // TLexeme

#endif
