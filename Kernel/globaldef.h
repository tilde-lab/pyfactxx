/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2003-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef GLOBALDEF_H
#define GLOBALDEF_H

// global definitions for FaCT++ Reasoning Kernel

// define unused attribute for parameters and (un)likely macro for conditions
#if defined(__GNUC__) && (__GNUC__ >= 4)
#	define ATTR_UNUSED __attribute__((unused))
#	define likely(cond) __builtin_expect((cond),1)
#	define unlikely(cond) __builtin_expect((cond),0)
#else
#	define ATTR_UNUSED
#	define likely(cond) (cond)
#	define unlikely(cond) (cond)
#endif

// uncomment this to have a DAG usage statistics printed
//#define RKG_PRINT_DAG_USAGE

// uncomment this to have sorted ontology reasoning
#define RKG_USE_SORTED_REASONING

// set the default value of USE_LOGGING

#ifndef USE_LOGGING
#	define USE_LOGGING 0
#endif

//#define ENABLE_CHECKING

// set to 1 to allow dynamic backjumping
#ifndef RKG_USE_DYNAMIC_BACKJUMPING
#	define RKG_USE_DYNAMIC_BACKJUMPING 0
#endif

// set to 1 to update role's R&D from super-roles
#ifndef RKG_UPDATE_RND_FROM_SUPERROLES
#	define RKG_UPDATE_RND_FROM_SUPERROLES 0
#endif

// uncomment this to allow simple rules processing
//#define RKG_USE_SIMPLE_RULES

// set to 1 to support fairness constraints
#ifndef RKG_USE_FAIRNESS
#	define RKG_USE_FAIRNESS 0
#endif

// uncomment the following line if IR is defined as a list of elements in node label
#define RKG_IR_IN_NODE_LABEL

// this value is used in classes Reasoner, CGraph and RareSaveStack
constexpr unsigned int InitBranchingLevelValue = 1;

#endif
