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

#ifndef PRIORITYMATRIX_H
#define PRIORITYMATRIX_H

#include "eFaCTPlusPlus.h"
#include "dlVertex.h"	// DagTag
#include "logging.h"

typedef unsigned short int ToDoListIndex;

/// number of regular options (o- and NN-rules are not included)
constexpr ToDoListIndex nRegularOps = 7;
/// priority index for o- and ID operations (note that these ops have the highest priority)
constexpr ToDoListIndex iId = nRegularOps+1;
/// priority index for <= operation in nominal node
constexpr ToDoListIndex iNN = nRegularOps+2;

/// Auxiliary class to get priorities on operations
class ToDoPriorMatrix
{
protected:	// members
	// regular operation indexes
	ToDoListIndex iAnd = nRegularOps,
				  iOr = nRegularOps,
				  iExists = nRegularOps,
				  iForall = nRegularOps,
				  iLE = nRegularOps,
				  iGE = nRegularOps;

public:		// interface
		/// init priorities via given string OPTIONS
	void initPriorities ( const std::string& options, const char* optionName );
		/// get an index corresponding given Op, Sign and NominalNode
	ToDoListIndex getIndex ( DagTag Op, bool Sign, bool NominalNode ) const;
}; // ToDoPriorMatrix

inline void ToDoPriorMatrix :: initPriorities ( const std::string& options, const char* optionName )
{
	// check for correctness
	if ( options.size () != 7 )
		throw EFaCTPlusPlus ( "ToDo List option string should have length 7" );

	// get int priority value out of a char
	auto char2int = [] (const char c) { return static_cast<ToDoListIndex>(c - '0'); };

	// init values by symbols loaded
	iAnd	= char2int(options[1]);
	iOr		= char2int(options[2]);
	iExists	= char2int(options[3]);
	iForall	= char2int(options[4]);
	iLE		= char2int(options[5]);
	iGE		= char2int(options[6]);

	// correctness checking
	if ( iAnd >= nRegularOps ||
		 iOr >= nRegularOps ||
		 iExists >= nRegularOps ||
		 iForall >= nRegularOps ||
		 iGE >= nRegularOps ||
		 iLE >= nRegularOps )
		throw EFaCTPlusPlus ( "ToDo List option out of range" );

	// inform about used rules order
	if ( LLM.isWritable(llAlways) )
		LL << "\nInit " << optionName << " = " << iAnd << iOr << iExists << iForall << iLE << iGE;
}

inline ToDoListIndex ToDoPriorMatrix :: getIndex ( DagTag Op, bool Sign, bool NominalNode ) const
{
	switch ( Op )
	{
	case dtAnd:
		return (Sign?iAnd:iOr);

	case dtForall:
	case dtIrr:		// process local (ir-)reflexivity as a FORALL
		return (Sign?iForall:iExists);

	case dtProj:	// it should be the lowest priority but now just OR's one
	case dtChoose:	// probably should be highest branching one
		return iOr;

	case dtLE:
		return (Sign?(NominalNode?iNN:iLE):iGE);

	case dtDataType:
	case dtDataValue:
	case dtDataExpr:
	case dtNN:
	case dtTop:			// no need to process these ops
		return nRegularOps;

	case dtPSingleton:
	case dtPConcept:	// no need to process neg of PC
		return (Sign?iId:nRegularOps);

	case dtNSingleton:
	case dtNConcept:	// both NC and neg NC are processed
		return iId;

	default:	// safety check
		fpp_unreachable();
	}
}

#endif
