/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2007-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef MODELCACHECONST_H
#define MODELCACHECONST_H

#include "modelCacheInterface.h"
#include "BiPointer.h"
#include "fpp_assert.h"

///	Model caching implementation for TOP/BOTTOM nodes.
class modelCacheConst: public modelCacheInterface
{
protected:	// members
		/// the const itself
	bool isTop;

protected:	// methods
		/// log a particular implementation of a cache entry
	void logCacheEntryImpl ( void ) const override
		{ LL << "\nConst cache: element " << (isTop ? "TOP" : "BOTTOM"); }

public:
		/// c'tor: no nominals can be here
	explicit modelCacheConst ( bool top )
		: modelCacheInterface{/*flagNominals=*/false}
		, isTop{top}
		{}
		/// copy c'tor
	modelCacheConst ( const modelCacheConst& m )
		: modelCacheInterface{m.hasNominalNode}
		, isTop{m.isTop}
		{}

		/// Check if the model contains clash
	modelCacheState getState ( void ) const override { return isTop ? csValid : csInvalid; }
		/// get the value of the constant
	bool getConst ( void ) const { return isTop; }

	// mergeable part

		/// check whether two caches can be merged; @return state of "merged" model
	modelCacheState canMerge ( const modelCacheInterface* cache ) const override
	{
		if ( auto cacheConst = dynamic_cast<const modelCacheConst*>(cache) )
			return isTop && cacheConst->isTop ? csValid : csInvalid;
		else
			return cache->canMerge(this);
	}
}; // modelCacheConst

// create const cache by BP; BP should be either bpTOP or bpBOTTOM
inline
modelCacheConst* createConstCache ( BipolarPointer bp )
{
	fpp_assert ( bp == bpTOP || bp == bpBOTTOM );
	return new modelCacheConst{bp==bpTOP};
}

#endif
