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
#ifndef COMSCANNER_H
#define COMSCANNER_H

#include <cstdlib>
#include <cstring>
#include <iostream>

/// max ID length for scanned objects
const unsigned int MaxIDLength = 10240;

/// more-or-less general simple scanner implementation
class CommonScanner
{
protected:	// members
		/// input stream
	std::istream* InFile;
		/// buffer for names
	char LexBuff [ MaxIDLength + 1 ];
		/// currently processed line of input (used in error diagnosis)
	unsigned int CurLine;

protected:	// methods
		/// get next symbol from the stream
	char NextChar ( void ) const { return (char)InFile->get(); }
		/// return given symbol back to stream
	void PutBack ( char c ) const { InFile->putback(c); }
		/// check whether C is a EOF char
	static bool eof ( char c ) { return c == std::char_traits<char>::eof(); }

public:		// interface
		/// c'tor
	explicit CommonScanner ( std::istream* inp )
		: InFile(inp)
		, CurLine(1)
		{}
		/// d'tor
	virtual ~CommonScanner() = default;

		/// get string collected in buffer
	const char* GetName ( void ) const { return LexBuff; }
		/// get number by string from buffer
	unsigned long GetNumber ( void ) const { return (unsigned long) atol(LexBuff); }
		/// get current input line
	unsigned int Line ( void ) const { return CurLine; }
		/// check if Buffer contains given Word (in any register)
	bool isKeyword ( const char* Word ) const
		{ return strlen(Word) == strlen(LexBuff) ? !strcmp ( Word, LexBuff ) : false; }

		/// reset scanner on the same file
	void ReSet ( void )
	{
		InFile->clear();
		InFile->seekg ( 0L, std::ios::beg );
		CurLine = 1;
	}
		/// reset scanner to a given file
	void reIn ( std::istream* in ) { InFile = in; CurLine = 1; }

		/// output an error message
	void error ( const char* msg = nullptr ) const
	{
		std::cerr << "\nError at input line " << Line() << ": "
				  << (msg?msg:"illegal syntax") << "\n";
		exit (1);
	}
};	// CommonScanner

#endif
