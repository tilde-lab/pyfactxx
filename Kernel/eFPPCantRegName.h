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

#ifndef EFPPCANTREGNAME_H
#define EFPPCANTREGNAME_H

#include <string>

#include "eFaCTPlusPlus.h"

/// exception thrown in case name can't be registered
class EFPPCantRegName: public EFaCTPlusPlus
{
private:	// members
		/// error string
	std::string str;
		/// name string
	std::string Name;

public:		// interface
		/// c'tor: create an output string
	EFPPCantRegName ( const std::string& name, const std::string& type )
		: EFaCTPlusPlus()
		, Name(name)
	{
		str = "Unable to register '";
		str += name;
		str += "' as a ";
		str += type;
		reason = str.c_str();
	}

		/// get access to the unregistered name
	const char* getName ( void ) const { return Name.c_str(); }
}; // EFPPCantRegName

#endif


