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

#ifndef TKBFLAGS_H
#define TKBFLAGS_H

#include "flags.h"

/// flags that reflects KB structure: GCIs, etc
class TKBFlags: public Flags
{
public:		// interface
		/// empty c'tor
	TKBFlags() = default;
		/// copy c'tor
	TKBFlags ( const TKBFlags& flags ) = default;

		/// register flag for GCIs
	FPP_ADD_FLAG(GCI,0x1);
		/// register flag for Range and Domain axioms
	FPP_ADD_FLAG(RnD,0x2);
		/// register flag for Reflexive roles
	FPP_ADD_FLAG(Reflexive,0x4);
}; // TKBFlags

#endif
