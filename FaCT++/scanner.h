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
//  Scanner class for FaCT++
//
//-------------------------------------------------------------------------
#ifndef SCANNER_H
#define SCANNER_H

#include "lispgrammar.h"
#include "comscanner.h"

/// more-or-less general simple scanner implementation
class TsScanner: public CommonScanner
{
protected:	// methods
		/// fill buffer with name in '|'-s; c should be starting '|'
	void FillNameBuffer ( register char c );
		/// fill buffer with legal ID chars, starting from c
	void FillBuffer ( register char c );
		/// check if given character is legal in ID
	bool isLegalIdChar ( char c ) const;

public:		// interface
		/// c'tor
	explicit TsScanner ( std::istream* inp ) : CommonScanner(inp) {}

		/// get next token from stream
	LispToken GetLex ( void );
		/// get keyword for a command by given text in buffer; @return BAD_LEX if no keyword found
	LispToken getCommandKeyword ( void ) const;
		/// get keyword for a concept/role expression; @return BAD_LEX if no keyword found
	LispToken getExpressionKeyword ( void ) const;
		/// get keyword for a concept/role special name; @return ID if no keyword found
	LispToken getNameKeyword ( void ) const;
};	// TsScanner

#endif
