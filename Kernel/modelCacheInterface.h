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

#ifndef MODELCACHEINTERFACE_H
#define MODELCACHEINTERFACE_H

#include "globaldef.h"
#include "logging.h"

/// status of model cache or merge operation
enum modelCacheState
{
	csInvalid,	///> clash in model/merging fails because of direct contradiction;
	csValid,	///> valid model/success in merging;
	csFailed,	///> incorrect model/merging fails because of incompleteness of procedure;
	csUnknown	///> untested model cache.
};

/// create united status for 2 given statuses
inline modelCacheState mergeStatus ( modelCacheState s1, modelCacheState s2 )
{
	// if one of caches is definitely UNSAT, then merge will be the same
	if ( s1 == csInvalid || s2 == csInvalid )
		return csInvalid;
	// if one of caches is unsure then result will be the same
	if ( s1 == csFailed || s2 == csFailed )
		return csFailed;
	// if one of caches is not inited, than result would be the same
	if ( s1 == csUnknown || s2 == csUnknown )
		return csUnknown;
	else	// valid+valid = valid
		return csValid;
}

/// interface for general model caching.
class modelCacheInterface
{
protected:	// members
		/// flag to show that model contains nominals
	bool hasNominalNode;

protected:	// methods
		/// log a particular implementation of a cache entry
	virtual void logCacheEntryImpl ( void ) const = 0;

public:		// interface
		/// Create cache model with given presence of nominals
	explicit modelCacheInterface ( bool flagNominals ) : hasNominalNode{flagNominals} {}
		/// empty d'tor
	virtual ~modelCacheInterface() = default;

		/// check whether both models have nominals; in this case, merge is impossible
	bool hasNominalClash ( const modelCacheInterface* p ) const
		{ return hasNominalNode && p->hasNominalNode; }
		/// update knowledge about nominals in the model after merging
	void updateNominalStatus ( const modelCacheInterface* p ) { hasNominalNode |= p->hasNominalNode; }

	// mergeable part

		/// Check the model cache internal state.
	virtual modelCacheState getState ( void ) const = 0;
		/// check whether two caches can be merged; @return state of "merged" model
	virtual modelCacheState canMerge ( const modelCacheInterface* p ) const = 0;

		/// get type of cache (deep or shallow)
	virtual bool shallowCache ( void ) const { return true; }
		/// log this cache entry (with given level)
	void logCacheEntry ( unsigned int level ) const
	{
		if ( LLM.isWritable(level) )
			logCacheEntryImpl();
	}
}; // modelCacheInterface

#endif
