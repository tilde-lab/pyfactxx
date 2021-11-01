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

#ifndef COMPARSER_H
#define COMPARSER_H

#include "comscanner.h"

/// generic class for parsing with usage of scanner derived from CommonScanner
template <typename Scanner>
class CommonParser
{
protected:	// members
		/// used scanner
	Scanner scan;
		/// last scanned token
	GenericToken Current;

protected:	// methods
		/// get current token
	GenericToken Code ( void ) const { return Current; }
		/// receive (and save) next token
	void NextLex ( void ) { Current = scan.GetLex(); }
		/// ensure that current token has given value; return error if it's not a case
	void MustBe ( GenericToken t, const char* message = nullptr ) const
	{
		if ( Current != t )
			scan.error(message);
	}
		/// ensure that current token has given value; return error if it's not a case; get new token
	void MustBeM ( GenericToken t, const char* message = nullptr )
		{ MustBe ( t, message ); NextLex (); }
		/// general error message
	[[noreturn]] void parseError ( const char* p ) const { scan.error(p); }

public:		// interface
		/// c'tor
	explicit CommonParser ( std::istream* in ) : scan ( in ) { NextLex (); }
		/// empty d'tor
	virtual ~CommonParser() = default;
};	// CommonParser

#endif // _COMMON_PARSER_HPP
