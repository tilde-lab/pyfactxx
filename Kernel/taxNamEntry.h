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

#ifndef TAXNAMENTRY_H
#define TAXNAMENTRY_H

#include <vector>

#include "fpp_assert.h"
#include "tNamedEntry.h"
#include "globaldef.h"

class TaxonomyVertex;

class ClassifiableEntry : public TNamedEntry
{
public:		// type definitions
		/// type for the set of told subsumers
	typedef std::vector<ClassifiableEntry*> linkSet;
		/// told subsumers RW iterator
	typedef linkSet::iterator iterator;
		/// told subsumers RO iterator
	typedef linkSet::const_iterator const_iterator;

protected:	// members
		/// link to taxonomy entry for current entry
	TaxonomyVertex* taxVertex = nullptr;
		/// links to 'told subsumers' (entries that are direct super-entries for current)
	linkSet toldSubsumers;
		/// pointer to synonym (entry which contains whole information the same as current)
	ClassifiableEntry* pSynonym = nullptr;
		/// index as a vertex in the SubsumptionMap/model cache
	unsigned int Index = 0;

public:		// interface
		/// C'tor
	explicit ClassifiableEntry ( const std::string& name ) : TNamedEntry(name) {}
		/// no copy c'tor
	ClassifiableEntry ( const ClassifiableEntry& ) = delete;
		/// no assignment
	ClassifiableEntry& operator = ( const ClassifiableEntry& ) = delete;

	// taxonomy entry access

		/// is current entry classified
	bool isClassified ( void ) const { return ( taxVertex != nullptr ); }
		/// set up given entry
	void setTaxVertex ( TaxonomyVertex* vertex ) { taxVertex = vertex; }
		/// get taxonomy vertex of the entry
	TaxonomyVertex* getTaxVertex ( void ) const { return taxVertex; }

	// completely defined interface

		/// register a Completely Defined flag
	FPP_ADD_FLAG(CompletelyDefined,0x2);
		/// register a non-classifiable flag
	FPP_ADD_FLAG(NonClassifiable,0x4);

	// told subsumers interface

		/// RW access to the told subsumers
	linkSet& told ( void ) { return toldSubsumers; }
		/// RO access to the told subsumers
	const linkSet& told ( void ) const { return toldSubsumers; }

		/// check whether the entry has any TS
	bool hasToldSubsumers ( void ) const { return !toldSubsumers.empty(); }
		/// add told subsumer of entry (duplications possible)
	void addParent ( ClassifiableEntry* parent ) { toldSubsumers.push_back (parent); }
		/// add told subsumer if doesn't already recorded
	void addParentIfNew ( ClassifiableEntry* parent );
		/// add all parents (with duplicates) from given container to current node
	template <typename Container>
	void addParents ( const Container& parents )
	{
		for ( auto& parent: parents )
			addParentIfNew(parent);
	}

	// index interface

		/// get the index value
	unsigned int index ( void ) const { return Index; }
		/// set the index value
	void setIndex ( unsigned int ind ) { Index = ind; }

	// synonym interface

		/// check if current entry is a synonym
	bool isSynonym ( void ) const { return (pSynonym != nullptr); }
		/// get synonym of current entry
	ClassifiableEntry* getSynonym ( void ) const { return pSynonym; }
		/// make sure that synonym's representative is not a synonym itself
	void canonicaliseSynonym ( void );
		/// add entry's synonym
	void setSynonym ( ClassifiableEntry* syn )
	{
		fpp_assert ( pSynonym == nullptr );	// do it only once
		pSynonym = syn;
		canonicaliseSynonym();
	}

		/// if two synonyms are in 'told' list, merge them
	void removeSynonymsFromParents ( void )
	{
		linkSet copy;
		copy.swap(toldSubsumers);
		addParents(copy);
	}
}; // ClassifiableEntry

/// general RW resolving synonym operator
template <typename T>
inline T*
resolveSynonym ( T* p )
{
	return !p ? nullptr : p->isSynonym() ? resolveSynonym(static_cast<T*>(p->getSynonym())) : p;
}

/// general RO resolving synonym operator
template <typename T>
inline const T*
resolveSynonym ( const T* p )
{
	return !p ? nullptr : p->isSynonym() ? resolveSynonym(static_cast<const T*>(p->getSynonym())) : p;
}

/// make sure that synonym's representative is not a synonym itself
inline void
ClassifiableEntry :: canonicaliseSynonym ( void )
{
	fpp_assert(isSynonym());
	pSynonym = resolveSynonym(pSynonym);
}


inline void
ClassifiableEntry :: addParentIfNew ( ClassifiableEntry* parent )
{
	// resolve synonyms
	parent = resolveSynonym(parent);

	// node can not be its own parent
	if ( parent == this )
		return;

	// check if such entry already exists
	for ( const auto& p: told() )
		if ( parent == p )
			return;

	addParent(parent);
}

#endif // TAXNAMENTRY_H
