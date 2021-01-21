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

#ifndef ACTOR_H
#define ACTOR_H

#include "taxVertex.h"
#include "TaxGatheringWalker.h"

/// class for acting with concept taxonomy
class Actor: public TaxGatheringWalker
{
public:		// types
		/// entry in an output
	typedef ClassifiableEntry EntryType;
		/// 1D vector of entries
	typedef std::vector<const EntryType*> Array1D;
		/// 2D vector of entries
	typedef std::vector<Array1D> Array2D;

protected:	// members
		/// flag to look at concept-like or role-like entities
	bool isRole = false;
		/// flag to look at concepts or object roles
	bool isStandard = true;

protected:	// methods
		/// check whether actor is applicable to the ENTRY
	bool applicable ( const EntryType* entry ) const override;
		/// fills an array with all suitable data from the vertex
	void fillArray ( const TaxonomyVertex& v, Array1D& array ) const
	{
		if ( tryEntry(v.getPrimer()) )
			array.push_back(v.getPrimer());
		for ( const auto& synonym: v.synonyms() )
			if ( tryEntry(synonym) )
				array.push_back(synonym);
	}

public:		// interface
		/// empty c'tor
	Actor() = default;

	// flags setup

		/// set the actor to look for classes
	void needConcepts ( void ) { isRole = false; isStandard = true; }
		/// set the actor to look for individuals
	void needIndividuals ( void ) { isRole = false; isStandard = false; }
		/// set the actor to look for object properties
	void needObjectRoles ( void ) { isRole = true; isStandard = true; }
		/// set the actor to look for individuals
	void needDataRoles ( void ) { isRole = true; isStandard = false; }

	// fill structures according to what's in the taxonomy

		/// return data as a 1D-array
	void getFoundData ( Array1D& array ) const
	{
		array.clear();
		for ( size_t i = 0; i < found.size(); i++ )
			fillArray ( *found[i], array );
	}
		/// return data as a 2D-array
	void getFoundData ( Array2D& array ) const
	{
		array.clear();
		array.resize(found.size());
		for ( size_t i = 0; i < found.size(); i++ )
			fillArray ( *found[i], array[i] );
	}
}; // Actor

#endif
