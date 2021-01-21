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

#include <fstream>

#include "MemoryStat.h"
#include "globaldef.h"

// set to 1 for memory logging
#ifndef USE_MEMORY_LOG
#	define USE_MEMORY_LOG 0
#endif

std::ofstream StatLogFile
#if USE_MEMORY_LOG
	("MemoryLog.txt")
#endif
;

#if defined(__APPLE__)
#	include <mach/mach.h>
#elif defined(_WINDOWS)
#	include <windows.h>
#	include <psapi.h>
#endif

static size_t getProcessMemory ( bool resident ATTR_UNUSED = true )
{
#ifdef __APPLE__
	struct task_basic_info t_info;
	mach_msg_type_number_t t_info_count = TASK_BASIC_INFO_COUNT;

	if (KERN_SUCCESS != task_info(mach_task_self(),
                              	  TASK_BASIC_INFO, (task_info_t)&t_info,
                              	  &t_info_count))
		return 0;
	return resident ? t_info.resident_size : t_info.virtual_size;
#elif defined(_WINDOWS)
	PROCESS_MEMORY_COUNTERS pmc;
	if ( 0 == GetProcessMemoryInfo(GetCurrentProcess(), &pmc, sizeof(pmc)) )
		return 0;
//	return resident ? pmc.WorkingSetSize : pmc.PrivateUsage;
	return resident ? pmc.WorkingSetSize : pmc.PagefileUsage;
#else	// undefined platform
	return 0;
#endif
}

MemoryStatistics :: MemoryStatistics ( const std::string& name )
	: operation(name)
{
	if ( USE_MEMORY_LOG )
	{
		timer.Start();
		startMem = getProcessMemory()/1024/1024;
	}
}

MemoryStatistics :: ~MemoryStatistics()
{
	if ( USE_MEMORY_LOG )
	{
		timer.Stop();
		size_t endMem = getProcessMemory()/1024/1024;
		StatLogFile << operation << ": time " << timer << " sec, op memory " << endMem-startMem << " Mb, total memory " << endMem << " Mb\n";
		timer.Reset();
	}
}
