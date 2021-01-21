/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2013-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef MEMORYSTAT_H
#define MEMORYSTAT_H

#include <ostream>
#include <string>

#include "procTimer.h"

/// class for measuring time and memory
class MemoryStatistics
{
protected:	// members
	TsProcTimer timer;
	std::string operation;
	size_t startMem;

public:
		/// c'tor: start timer, save memory usage
	explicit MemoryStatistics ( const std::string& name );
		/// d'tor: dump taken time and memory usage
	~MemoryStatistics();
}; // MemoryStatistics

#endif
