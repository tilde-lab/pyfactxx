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

#ifndef LOGGING_H
#define LOGGING_H

#include "LeveLogger.h"

// constants for use by different logger users
enum
{
	llAlways = 0,
	llStartCfyConcepts = 0,	// "start classifying * concepts" message
	llStartCfyEntry = 1,	// "start classifying entry" message
	llBegSat = 1,			// "begin sat/sub test" message
	llSatResult = 1,		// "sat/sub does [NOT] holds" message
	llTaxTrying = 2,		// "Try C [= D" message
	llCDConcept = 2,		// "completely defines concept" message
	llTSList = 2,			// print list of TS's
	llTaxInsert = 2,		// "insert C with parents={} and children={}" message
	llDagSat = 3,			// "checking SAT of DAG entry" message
	llSatTime = 3,			// print time for the test
	llRStat = 5,			// local reasoning statistic
	llSRInfo = 15,			// node save/restore info
	llSRState = 15,			// s/r info of the reasoning state
	llGTA = 10,				// general tactic action
	llCDAction = llGTA,		// any concrete domain info
};

#endif
