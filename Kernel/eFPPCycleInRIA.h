/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2007-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef EFPPCYCLEINRIA_H
#define EFPPCYCLEINRIA_H

#include "eFaCTPlusPlus.h"

/// exception thrown in case there is a (disallowed) cycle in a role-inclusion axiom
class EFPPCycleInRIA: public EFaCTPlusPlus
{
private:	// members
		/// saved name of the role
	const std::string roleName;
		/// error string
	std::string str;

public:		// interface
		/// c'tor: create an output string
	explicit EFPPCycleInRIA ( const std::string& name )
		: EFaCTPlusPlus()
		, roleName(name)
	{
		str = "Role '";
		str += name;
		str += "' appears in a cyclic role inclusion axioms";
		reason = str.c_str();
	}

		/// access to the role
	const char* getRoleName ( void ) const { return roleName.c_str(); }
}; // EFPPCycleInRIA

#endif


