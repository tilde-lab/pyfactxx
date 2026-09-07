cmake_minimum_required(VERSION 3.15)
project(factplusplus)

# global definition
set(CMAKE_CXX_STANDARD 11)

option(FPP_USE_LOGGING "Switch on FaCT++ internal logging" OFF)

# define warnings/optimisaiton flags for GCC/CLANG compilers
if (NOT MSVC)
	set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -ffast-math -fPIC -fomit-frame-pointer")
	# Additional warnings (from GCC 6.2)
	set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wpedantic -Wsuggest-override")
else()
	# Set exception handling in VC++
	set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} /EHsc")
	# Disable warning about treating int as bool in VC++
	set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} /wd4800")
endif()

if (FPP_USE_LOGGING)
	set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -DUSE_LOGGING=1")
endif()

add_subdirectory(Kernel)

if(SKBUILD)
	# Building the Python distribution.  The extension module needs the
	# reasoner kernel and the FaCT++ sources (globbed directly by
	# FaCT++.Python/CMakeLists.txt), but none of the other targets: the
	# command line tool, the C API and the JNI bindings are not shipped
	# in the wheel.  FaCT++.JNI is also excluded from the sdist, so
	# adding it unconditionally makes `pip install` from an sdist fail
	# at configure time with "add_subdirectory given source
	# FaCT++.JNI which is not an existing directory".
	add_subdirectory(FaCT++.Python)
else()
	add_subdirectory(FaCT++)
	add_subdirectory(FaCT++.C)
	add_subdirectory(FaCT++.JNI)
endif()
