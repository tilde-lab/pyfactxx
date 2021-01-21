/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2015-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef TAXGATHERINGWALKER_H
#define TAXGATHERINGWALKER_H

#include "WalkerInterface.h"
#include "taxVertex.h"

/// taxonomy walkers that gathers all the relevant nodes
class TaxGatheringWalker : public WalkerInterface
{
protected:	// members
		/// vertices that satisfy the condition
	std::vector<const TaxonomyVertex*> found;

protected:	// methods
		/// check whether actor is applicable to the ENTRY
	virtual bool applicable ( const ClassifiableEntry* entry ) const = 0;
		/// @return true iff current entry is visible
	bool tryEntry ( const ClassifiableEntry* p ) const { return !p->isSystem() && applicable(p); }
		/// @return true if at least one entry of a vertex V is visible
	bool tryVertex ( const TaxonomyVertex& v ) const
	{
		if ( tryEntry(v.getPrimer()) )
			return true;
		for ( const auto& synonym: v.synonyms() )
			if ( tryEntry(synonym) )
				return true;
		return false;
	}

public:		// interface
		/// clear found
	virtual void clear ( void ) { found.clear(); }

		/// taxonomy walking method.
		/// @return true if node was processed
		/// @return false if node can not be processed in current settings
	bool apply ( const TaxonomyVertex& v ) override
	{
		if ( tryVertex(v) )
		{
			found.push_back(&v);
			return true;
		}
		return false;
	}
}; // TaxGatheringWalker

#endif
