/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2010-2015 Dmitry Tsarkov and The University of Manchester
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

#include "tExpressionManager.h"

TExpressionManager :: TExpressionManager ( void )
	: CTop(new TDLConceptTop)
	, CBottom(new TDLConceptBottom)
	, DTop(new TDLDataTop)
	, DBottom(new TDLDataBottom)
	, ORTop(new TDLObjectRoleTop)
	, ORBottom(new TDLObjectRoleBottom)
	, DRTop(new TDLDataRoleTop)
	, DRBottom(new TDLDataRoleBottom)
	, InverseRoleCache(this)
	, OneOfCache(this)
{
}

TExpressionManager :: ~TExpressionManager()
{
	clear();
	delete CTop;
	delete CBottom;
	delete ORTop;
	delete ORBottom;
	delete DRTop;
	delete DRBottom;
	delete DTop;
	delete DBottom;
}

void
TExpressionManager :: clear ( void )
{
	// clear all the names but the datatypes
	NS_C.clear();
	NS_I.clear();
	NS_OR.clear();
	NS_DR.clear();
	InverseRoleCache.clear();
	OneOfCache.clear();
	// delete all the recorded references
	for ( auto& expr: RefRecorder )
		delete expr;
	RefRecorder.clear();
}

/// clear the TNamedEntry cache for all elements of all name-sets
void
TExpressionManager :: clearNameCache ( void )
{
	clearEntriesCache(NS_C);
	clearEntriesCache(NS_I);
	clearEntriesCache(NS_OR);
	clearEntriesCache(NS_DR);
	clearEntriesCache(NS_DT);
}
