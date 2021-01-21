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
|* Implementation of taxonomy building for the FaCT++  *|
\*******************************************************/

#include "Taxonomy.h"
#include "logging.h"

/********************************************************\
|* 			Implementation of class Taxonomy			*|
\********************************************************/
Taxonomy :: ~Taxonomy()
{
	delete Current;
	for ( TaxVertexVec::iterator p = Graph.begin(), p_end = Graph.end(); p < p_end; ++p )
		delete *p;
}

void Taxonomy :: print ( std::ostream& o ) const
{
	o << "All entries are in format:\n\"entry\" {n: parent_1 ... parent_n} {m: child_1 child_m}\n\n";

	TVSet sorted(Graph.begin()+2, Graph.end());

	getTopVertex()->print(o);
	for ( const TaxonomyVertex* vertex : sorted )
		if ( likely(vertex->isInUse()) )
			vertex->print(o);
	getBottomVertex()->print(o);
}

//---------------------------------------------------
// classification part
//---------------------------------------------------
void
Taxonomy :: addCurrentToSynonym ( TaxonomyVertex* syn )
{
	const ClassifiableEntry* curEntry = Current->getPrimer();
	if ( queryMode() )	// no need to insert; just mark SYN as a host to curEntry
		syn->setVertexAsHost(curEntry);
	else
	{
		syn->addSynonym(curEntry);

		if ( LLM.isWritable(llTaxInsert) )
			LL << "\nTAX:set " << curEntry->getName() << " equal " << syn->getPrimer()->getName();
	}
}

/// insert current node either directly or as a synonym
void
Taxonomy :: finishCurrentNode ( void )
{
	TaxonomyVertex* syn = Current->getSynonymNode();
	if ( syn )
		addCurrentToSynonym(syn);
	else if ( !queryMode() )	// insert node into taxonomy
	{
		Current->incorporate();
		Graph.push_back(Current);
		// we used the Current so need to create a new one
		Current = new TaxonomyVertex();
	}
}

bool Taxonomy :: processSynonym ( void )
{
	const ClassifiableEntry* curEntry = Current->getPrimer();
	const ClassifiableEntry* syn = resolveSynonym(curEntry);

	if ( syn == curEntry )
		return false;	// not a synonym

	// update synonym vertex:
	fpp_assert ( syn->getTaxVertex() != nullptr );
	addCurrentToSynonym(syn->getTaxVertex());

	return true;
}

/// call this method after taxonomy is built
void
Taxonomy :: finalise ( void )
{	// create links from leaf concepts to bottom
	const bool upDirection = false;
	for ( TaxVertexVec::iterator p = Graph.begin()+1, p_end = Graph.end(); p < p_end; ++p )
		if ( likely((*p)->isInUse()) && (*p)->noNeighbours(upDirection) )
		{
			(*p)->addNeighbour ( upDirection, getBottomVertex() );
			getBottomVertex()->addNeighbour ( !upDirection, *p );
		}
	willInsertIntoTaxonomy = false;	// after finalisation one shouldn't add new entries to taxonomy
}

/// unlink the bottom from the taxonomy
void
Taxonomy :: deFinalise ( void )
{
	const bool upDirection = true;
	TaxonomyVertex* bot = getBottomVertex();
	for ( TaxonomyVertex::iterator p = bot->begin(upDirection), p_end = bot->end(upDirection); p != p_end; ++p )
		(*p)->removeLink ( !upDirection, bot );
	bot->clearLinks(upDirection);
	willInsertIntoTaxonomy = true;	// it's possible again to add entries
}
