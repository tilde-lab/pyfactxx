/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2006-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef EFPPSAVELOAD_H
#define EFPPSAVELOAD_H

#include <string>

#include "eFaCTPlusPlus.h"

/// exception thrown for the save/load operations
class EFPPSaveLoad: public EFaCTPlusPlus
{
private:	// members
		/// error string
	std::string str;

public:		// interface
		/// c'tor with a given "what" string
	explicit EFPPSaveLoad ( const std::string& why )
		: EFaCTPlusPlus()
		, str(why)
	{
		reason = str.c_str();
	}
		/// c'tor "Char not found"
	explicit EFPPSaveLoad ( const char c )
		: EFaCTPlusPlus()
	{
		str = "Expected character '";
		str += c;
		str += "' not found";
		reason = str.c_str();
	}
		/// c'tor: create an output string for the bad filename
	EFPPSaveLoad ( const std::string& filename, bool save )
		: EFaCTPlusPlus()
	{
		const char* action = save ? "save" : "load";
		const char* prep = save ? "to" : "from";
		str = "Unable to ";
		str += action;
		str += " internal state ";
		str += prep;
		str += " file '";
		str += filename;
		str += "'";
		reason = str.c_str();
	}
}; // EFppSaveLoad

#endif


