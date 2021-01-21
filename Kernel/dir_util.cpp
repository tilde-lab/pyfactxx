/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2014-2015 Dmitry Tsarkov and The University of Manchester
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

#include <cerrno>
#include <sys/stat.h>

#include "dir_util.h"

// MinGW GCC up to version 6.2 does not have permissions parameter in mkdir()
#if defined(__MINGW32__) || defined(__MINGW64__)
#	define mkdir(path, permissions) mkdir(path)
#endif

/// create a directory by a path; @return 0 if success, -1 if not
int dirCreate ( const char *path )
{
    struct stat st;
    int status = 0;
#ifndef _MSC_VER
    if (stat(path, &st) != 0)
    {
        /* Directory does not exist. EEXIST for race condition */
        if (mkdir(path, 0777) != 0 && errno != EEXIST)
            status = -1;
    }
    else if (!S_ISDIR(st.st_mode))
    {
        errno = ENOTDIR;
        status = -1;
    }
#endif
    return status;
}

