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

#include "Actor.h"
#include "tConcept.h"

	/// check whether actor is applicable to the ENTRY
bool
Actor :: applicable ( const EntryType* entry ) const
{
	if ( isRole )	// object- or data-role
	{
		if ( isStandard )	// object role
			return true;
		else	// data role -- need only direct ones and TOP/BOT
			return entry->getId() >= 0;
	}
	else	// concept or individual: standard are concepts
		return static_cast<const TConcept*>(entry)->isSingleton() != isStandard;
}
