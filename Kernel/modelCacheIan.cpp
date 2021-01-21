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

#include <ostream>

#include "modelCacheIan.h"

/// clear the cache
void
modelCacheIan :: clear ( void )
{
	posDConcepts.clear();
	posNConcepts.clear();
	negDConcepts.clear();
	negNConcepts.clear();
#ifdef RKG_USE_SIMPLE_RULES
	extraDConcepts.clear();
	extraNConcepts.clear();
#endif
	existsRoles.clear();
	forallRoles.clear();
	funcRoles.clear();
	curState = csValid;
}

void modelCacheIan :: processConcept ( const DLVertex& cur, bool pos, bool det )
{
		switch ( cur.Type() )
		{
		case dtTop:			// sanity checks
		case dtDataType:	// data entries can not be cached
		case dtDataValue:
		case dtDataExpr:
			fpp_unreachable();

		case dtNConcept:	// add concepts to Concepts
		case dtPConcept:
		case dtNSingleton:
		case dtPSingleton:
			(det ? getDConcepts(pos) : getNConcepts(pos)).insert(static_cast<const ClassifiableEntry*>(cur.getConcept())->index());
			break;

		case dtIrr:		// for \neg \ER.Self: add R to AR-set
		case dtForall:	// add AR.C roles to forallRoles
		case dtLE:		// for <= n R: add R to forallRoles
			if ( unlikely ( cur.getRole()->isTop() ) )	// force clash to every other edge
				(pos ? forallRoles : existsRoles).completeSet();
			else if ( pos )	// no need to deal with existentials here: they would be created through edges
			{
				if ( cur.getRole()->isSimple() )
					forallRoles.insert(cur.getRole()->index());
				else
					processAutomaton(cur);
			}
			break;

		default:	// all other -- nothing to do
			break;
		}
}

void
modelCacheIan :: processAutomaton ( const DLVertex& cur )
{
	const RAStateTransitions& RST = cur.getRole()->getAutomaton()[cur.getState()];

	// for every transition starting from a given state,
	// add the role that is accepted by a transition
	for ( const auto& trans: RST )
		for ( const auto& R: trans )
			forallRoles.insert(R->index());
}

modelCacheState modelCacheIan :: canMerge ( const modelCacheInterface* cache ) const
{
	if ( hasNominalClash(cache) )	// fail to merge due to nominal presence
		return csFailed;

	// check if something goes wrong
	if ( cache->getState () != csValid || getState () != csValid )
		return mergeStatus ( cache->getState (), getState () );

	// here both models are valid;

	if ( auto cacheIan = dynamic_cast<const modelCacheIan*>(cache) )
		return isMergeableIan(cacheIan);
	if ( auto cacheSingleton = dynamic_cast<const modelCacheSingleton*>(cache) )
		return isMergeableSingleton(cacheSingleton->getValue());
	if ( dynamic_cast<const modelCacheConst*>(cache) )
		return csValid;	// as we checked for the invalid
	// something unexpected
	return csUnknown;
}

modelCacheState
modelCacheIan :: isMergeableSingleton ( BipolarPointer bp ) const
{
	fpp_assert ( isValid(bp) );
	auto pos = isPositive(bp);
	auto Singleton  = getValue(bp);

	// deterministic clash
	if ( getDConcepts(!pos).contains(Singleton) )
		return csInvalid;
	// non-det clash
	else if ( getNConcepts(!pos).contains(Singleton) )
		return csFailed;

	return csValid;
}

modelCacheState modelCacheIan :: isMergeableIan ( const modelCacheIan* cache ) const
{
	if ( posDConcepts.intersects(cache->negDConcepts)
		 || cache->posDConcepts.intersects(negDConcepts)
#	ifdef RKG_USE_SIMPLE_RULES
		 || getExtra(/*det=*/true).intersects(cache->getExtra(/*det=*/true))
#	endif
		)
		return csInvalid;
	else if (  posDConcepts.intersects(cache->negNConcepts)
			|| posNConcepts.intersects(cache->negDConcepts)
			|| posNConcepts.intersects(cache->negNConcepts)
			|| cache->posDConcepts.intersects(negNConcepts)
			|| cache->posNConcepts.intersects(negDConcepts)
			|| cache->posNConcepts.intersects(negNConcepts)
#		ifdef RKG_USE_SIMPLE_RULES
			|| getExtra(/*det=*/true).intersects(cache->getExtra(/*det=*/false))
			|| getExtra(/*det=*/false).intersects(cache->getExtra(/*det=*/true))
			|| getExtra(/*det=*/false).intersects(cache->getExtra(/*det=*/false))
#		endif
			|| existsRoles.intersects(cache->forallRoles)
			|| cache->existsRoles.intersects(forallRoles)
			|| funcRoles.intersects(cache->funcRoles) )
		return csFailed;
	else	// could be merged
		return csValid;
}

modelCacheState modelCacheIan :: merge ( const modelCacheInterface* cache )
{
	fpp_assert ( cache != nullptr );

	// check for nominal clash
	if ( hasNominalClash(cache) )
	{
		curState = csFailed;
		return getState();
	}

	if ( auto cacheIan = dynamic_cast<const modelCacheIan*>(cache) )
		mergeIan(cacheIan);
	else if ( auto cacheSingleton = dynamic_cast<const modelCacheSingleton*>(cache) )
		mergeSingleton(cacheSingleton->getValue());
	else if ( dynamic_cast<const modelCacheConst*>(cache) )
		curState = mergeStatus ( getState(), cache->getState() ); // adds TOP/BOTTOM
	else
		fpp_unreachable();

	updateNominalStatus(cache);
	return getState();
}

/// actual merge with a singleton cache
void
modelCacheIan :: mergeSingleton ( BipolarPointer bp )
{
	modelCacheState newState = isMergeableSingleton(bp);

	if ( newState != csValid )	// some clash occurred: adjust state
		curState = mergeStatus ( getState(), newState );
	else	// add singleton; no need to change state here
		getDConcepts(isPositive(bp)).insert(getValue(bp));
}

/// actual merge with an Ian's cache
void
modelCacheIan :: mergeIan ( const modelCacheIan* cache )
{
	// setup curState
	curState = isMergeableIan(cache);

	// merge all sets:
	posDConcepts |= cache->posDConcepts;
	posNConcepts |= cache->posNConcepts;
	negDConcepts |= cache->negDConcepts;
	negNConcepts |= cache->negNConcepts;
#ifdef RKG_USE_SIMPLE_RULES
	extraDConcepts |= cache->extraDConcepts;
	extraNConcepts |= cache->extraNConcepts;
#endif
	existsRoles |= cache->existsRoles;
	forallRoles |= cache->forallRoles;
	funcRoles |= cache->funcRoles;
}

// logging
void
modelCacheIan :: logCacheEntryImpl ( void ) const
{
	LL << "\nIan cache: posDConcepts = ";
	posDConcepts.print(LL);
	LL << ", posNConcepts = ";
	posNConcepts.print(LL);
	LL << ", negDConcepts = ";
	negDConcepts.print(LL);
	LL << ", negNConcepts = ";
	negNConcepts.print(LL);
#ifdef RKG_USE_SIMPLE_RULES
	LL << ", extraDConcepts = ";
	extraDConcepts.print(LL);
	LL << ", extraNConcepts = ";
	extraNConcepts.print(LL);
#endif
	LL << ", existsRoles = ";
	existsRoles.print(LL);
	LL << ", forallRoles = ";
	forallRoles.print(LL);
	LL << ", funcRoles = ";
	funcRoles.print(LL);
}
