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

#ifndef MODELCACHESINGLETON_H
#define MODELCACHESINGLETON_H

#include "modelCacheConst.h"
#include "BiPointer.h"

/** Model caching implementation for singleton models.
	Such models contains only one [negated] concept in completion tree.
	Reduced set of operations, but very efficient.
*/
class modelCacheSingleton: public modelCacheInterface
{
protected:	// members
		/// the singleton itself
	BipolarPointer Singleton;

protected:	// methods
		/// log a particular implementation of a cache entry
	void logCacheEntryImpl ( void ) const override { LL << "\nSingleton cache: element " << getValue(); }

public:		// interface
		/// c'tor: no nominals can be here
	explicit modelCacheSingleton ( BipolarPointer bp )
		: modelCacheInterface{/*flagNominals=*/false}
		, Singleton{bp}
		{}
		/// copy c'tor
	modelCacheSingleton ( const modelCacheSingleton& m )
		: modelCacheInterface{m.hasNominalNode}
		, Singleton{m.Singleton}
		{}

		/// Check if the model contains clash
	modelCacheState getState ( void ) const override { return csValid; }
		/// access to internal value
	BipolarPointer getValue ( void ) const { return Singleton; }

	// mergeable part

		/// check whether two caches can be merged; @return state of "merged" model
	modelCacheState canMerge ( const modelCacheInterface* cache ) const override
	{
		// TOP/BOTTOM: the current node can't add anything to the result
		if ( auto cacheConst = dynamic_cast<const modelCacheConst*>(cache) )
			return cacheConst->getState();
		// check another singleton: can be clash
		if ( auto cacheSingleton = dynamic_cast<const modelCacheSingleton*>(cache) )
			return cacheSingleton->getValue() == inverse(getValue()) ? csInvalid : csValid;
		// more complex cache: ask them to check
		return cache->canMerge(this);
	}
}; // modelCacheSingleton

#endif
