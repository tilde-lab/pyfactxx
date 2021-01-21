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

/*******************************************************\
|*      Implementation of taxonomy vertex class        *|
\*******************************************************/

#include <algorithm>
#include <ostream>

#include "taxVertex.h"
#include "logging.h"

// removes given pointer from neigh.
bool TaxonomyVertex :: removeLink ( bool upDirection, TaxonomyVertex* p )
{
	// for all neighbours of current vertex...
	auto& neighbours = neigh(upDirection);
	auto exists = std::find(neighbours.begin(), neighbours.end(), p);
	// Vertex P is not a neighbour -- nothing to do
	if ( exists == neighbours.end() )
		return false;
	// replace P with the last entry
	*exists = neighbours.back();
	// remove last entry (by resizing)
	neighbours.pop_back();
	// there is at most one link to a node, so return now
	return true;
}

void TaxonomyVertex :: incorporate ( void )
{
	// setup links
	auto& subs = neigh(/*upDirection=*/false);
	auto& supers = neigh(/*upDirection=*/true);

	// correct links on sub concepts...
	for ( auto& sub: subs )
	{
		// remove all links between subs and supers, if exists
		for ( auto& super: supers )
			if ( sub->removeLink ( /*upDirection=*/true, super ) )
				super->removeLink ( /*upDirection=*/false, sub );

		// add new link between sub-class and current
		sub->removeLink ( /*upDirection=*/true, this );	// safe in general case (link doesn't exists), crucial for incremental
		sub->addNeighbour ( /*upDirection=*/true, this );
	}

	// add new links between current and supers
	for ( auto& super: supers )
		super->addNeighbour ( /*upDirection=*/false, this );

	CHECK_LL_RETURN(llTaxInsert);

	LL << "\nTAX:inserting '" << getPrimer()->getName() << "' with up =";
	printNeighbours ( LL, /*upDirection=*/true );
	LL << " and down =";
	printNeighbours ( LL, /*upDirection=*/false );
}

/// remove one half of a given node from a graph
void
TaxonomyVertex :: removeLinks ( bool upDirection )
{
	for ( auto& vertex: neigh(upDirection) )
		vertex->removeLink ( !upDirection, this );

	clearLinks(upDirection);
}

void TaxonomyVertex :: printSynonyms ( std::ostream& o ) const
{
	fpp_assert ( sample != nullptr );

	if ( likely(Synonyms.empty()) )
		o << '"' << getPrimer()->getName() << '"';
	else
	{
		o << "(\"" << getPrimer()->getName();
		for ( const auto& synonym: synonyms() )
			o << "\"=\"" << synonym->getName();
		o << "\")";
	}
}

void TaxonomyVertex :: printNeighbours ( std::ostream& o, bool upDirection ) const
{
	// write number of elements
	o << " {" << neigh(upDirection).size() << ":";

	TVSet sorted ( begin(upDirection), end(upDirection) );
	for  ( auto vertex: sorted)
		o << " \"" << vertex->getPrimer()->getName() << '"';

	o << "}";
}

/// print taxonomy vertex in format <equals parents children>
void
TaxonomyVertex :: print ( std::ostream& o ) const
{
	printSynonyms(o);
	printNeighbours ( o, true );
	printNeighbours ( o, false );
	o << "\n";
}
