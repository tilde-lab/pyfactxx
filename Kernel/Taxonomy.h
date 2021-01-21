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

#ifndef TAXONOMY_H
#define TAXONOMY_H

// taxonomy graph for DL

#include "taxVertex.h"
#include "WalkerInterface.h"

class SaveLoadManager;

class Taxonomy
{
protected:	// typedefs
		/// type for a vector of TaxVertex
	typedef std::vector<TaxonomyVertex*> TaxVertexVec;

protected:	// members
		/// array of taxonomy vertices
	TaxVertexVec Graph;
		/// vertex with parent Top and child Bot, represents the fresh entity
	TaxonomyVertex FreshNode;

		/// labeller for marking nodes as checked
	TLabeller visitedLabel;
		/// aux vertex to be included to taxonomy
	TaxonomyVertex* Current;

		/// behaviour flag: if true, insert temporary vertex into taxonomy
	bool willInsertIntoTaxonomy = true;

public:		// classification interface

	//-----------------------------------------------------------------
	//--	General classification support
	//-----------------------------------------------------------------

		/// @return true if current entry is a synonym of an already classified one
	bool processSynonym ( void );
		/// add current entry to a synonym SYN
	void addCurrentToSynonym ( TaxonomyVertex* syn );
		/// remove node from the taxonomy; assume no references to the node
	void removeNode ( TaxonomyVertex* node ) { node->setInUse(false); }
		/// insert current node either directly or as a synonym
	void finishCurrentNode ( void );

		/// @return true if taxonomy works in a query mode (no need to insert query vertex)
	bool queryMode ( void ) const { return !willInsertIntoTaxonomy; }

		/// set node NODE as checked within taxonomy
	void setVisited ( TaxonomyVertex* node ) const { node->setChecked(visitedLabel); }
		/// check whether NODE is checked within taxonomy
	bool isVisited ( TaxonomyVertex* node ) const { return node->isChecked(visitedLabel); }
		/// clear the CHECKED label from all the taxonomy vertex
	void clearVisited ( void ) { visitedLabel.newLabel(); }

		/// call this method after taxonomy is built
	void finalise ( void );
		/// unlink the bottom from the taxonomy
	void deFinalise ( void );

protected:	// methods
		/// apply ACTOR to subgraph starting from NODE as defined by flags
	template<bool onlyDirect, bool upDirection, class Actor>
	void getRelativesInfoRec ( TaxonomyVertex* node, Actor& actor )
	{
		// recursive applicability checking
		if ( isVisited(node) )
			return;

		// label node as visited
		setVisited(node);

		// if current node processed OK and there is no need to continue -- exit
		// if node is NOT processed for some reasons -- go to another level
		if ( actor.apply(*node) && onlyDirect )
			return;

		// apply method to the proper neighbours with proper parameters
		for ( TaxonomyVertex::iterator p = node->begin(upDirection), p_end = node->end(upDirection); p != p_end; ++p )
			getRelativesInfoRec<onlyDirect, upDirection> ( *p, actor );
	}

public:		// interface
		/// init c'tor
	Taxonomy ( const ClassifiableEntry* pTop, const ClassifiableEntry* pBottom )
		: Current(new TaxonomyVertex())
	{
		Graph.push_back (new TaxonomyVertex(pBottom));	// bottom
		Graph.push_back (new TaxonomyVertex(pTop));		// top
		// set up fresh node
		FreshNode.addNeighbour ( /*upDirection=*/true, getTopVertex() );
		FreshNode.addNeighbour ( /*upDirection=*/false, getBottomVertex() );
	}
		/// no copy c'tor
	Taxonomy ( const Taxonomy& ) = delete;
		/// no assignment
	Taxonomy& operator = ( const Taxonomy& ) = delete;
		/// d'tor
	~Taxonomy();

	//------------------------------------------------------------------------------
	//--	Access to taxonomy entries
	//------------------------------------------------------------------------------

		/// special access to TOP of taxonomy
	TaxonomyVertex* getTopVertex ( void ) const { return *(Graph.begin()+1); }
		/// special access to BOTTOM of taxonomy
	TaxonomyVertex* getBottomVertex ( void ) const { return *Graph.begin(); }
		/// get node for fresh entity E
	TaxonomyVertex* getFreshVertex ( const ClassifiableEntry* e ) { FreshNode.setSample(e,false); return &FreshNode; }
		/// get RW access to current
	TaxonomyVertex* getCurrent ( void ) { return Current; }
		/// get RO access to current
	const TaxonomyVertex* getCurrent ( void ) const { return Current; }
		/// set current to a given node
	void setCurrent ( TaxonomyVertex* cur ) { Current = cur; }

		/// apply ACTOR to subgraph starting from NODE as defined by flags;
	template<bool needCurrent, bool onlyDirect, bool upDirection, class Actor>
	void getRelativesInfo ( TaxonomyVertex* node, Actor& actor )
	{
		// if current node processed OK and there is no need to continue -- exit
		// this is the helper to the case like getDomain():
		//   if there is a named concept that represent's a domain -- that's what we need
		if ( needCurrent )
			if ( actor.apply(*node) && onlyDirect )
				return;

		for ( TaxonomyVertex::iterator p = node->begin(upDirection), p_end = node->end(upDirection); p != p_end; ++p )
			getRelativesInfoRec<onlyDirect, upDirection> ( *p, actor );

		clearVisited();
	}

	// taxonomy info access

		/// print taxonomy info to a stream
	void print ( std::ostream& o ) const;

	// save/load interface; implementation is in SaveLoad.cpp

		/// save entry
	void Save ( SaveLoadManager& m, const std::set<const TNamedEntry*>& excluded ) const;
		/// load entry
	void Load ( SaveLoadManager& m );
}; // Taxonomy

#endif // TAXONOMY_H
