/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2011-2015 Dmitry Tsarkov and The University of Manchester
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

#include "parseTime.h"

/// fills TM with a time value from string STR; @return true iff fail to load
static bool
fillTmByString ( const char* str, struct tm* tm )
{
	return strptime ( str, "%Y-%m-%dT%H:%M:%S%z", tm ) != nullptr;
}
/// this function gets a string representing time in an ISO 8601 format and returns time_t value built out of it
time_t parseTimeString ( const char* str )
{
	struct tm temp;
	// decode time value in format YYYY-MM-DDThh:mm:ssTZ, as in xsd:dateTime
	fillTmByString ( str, &temp );
	return timegm(&temp);
}
