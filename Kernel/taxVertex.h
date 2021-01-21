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

#ifndef TAXVERTEX_H
#define TAXVERTEX_H

#include <set>
#include <vector>
#include <iosfwd>
#include <cstring>

#include "taxNamEntry.h"
#include "tLabeller.h"

class SaveLoadManager;

class TaxonomyVertex
{
protected:	// typedefs
		/// vertex vector for links to parents and children
	typedef std::vector<TaxonomyVertex*> TaxVertexLink;
		/// vector of entries for synonyms
	typedef std::vector<const ClassifiableEntry*> EqualNames;

public:		// typedefs
	// accessors type

		/// RW iterator for the neighbours
	typedef TaxVertexLink::iterator iterator;
		/// RO iterator for the neighbours
	typedef TaxVertexLink::const_iterator const_iterator;

private:	// members
		/// immediate parents and children
	TaxVertexLink Links[2];

protected:	// members
		/// entry corresponding to current tax vertex
	const ClassifiableEntry* sample = nullptr;
		/// synonyms of the sample entry
	EqualNames Synonyms;

	// labels for different purposes. all for 2 directions: top-down and bottom-up search

		/// flag if given vertex was checked; connected with checkLab
	TLabeller::LabelType theChecked;
		/// flag if given vertex has value; connected with valuedLab
	TLabeller::LabelType theValued;
		/// number of common parents of a node
	unsigned int common = 0;
		/// satisfiability value of a valued vertex
	bool checkValue = false;
		/// flag to check whether the vertex is in use
	bool inUse = true;

protected:	// methods
		/// indirect RW access to Links
	TaxVertexLink& neigh ( bool upDirection ) { return Links[!upDirection]; }
		/// indirect RO access to Links
	const TaxVertexLink& neigh ( bool upDirection ) const { return Links[!upDirection]; }

		/// print entry name and its synonyms (if any)
	void printSynonyms ( std::ostream& o ) const;
		/// print neighbours of a vertex in given direction
	void printNeighbours ( std::ostream& o, bool upDirection ) const;

public:		// flags interface

	// checked part
	bool isChecked ( const TLabeller& checkLab ) const { return checkLab.isLabelled(theChecked); }
	void setChecked ( const TLabeller& checkLab ) { checkLab.set(theChecked); }

	// value part
	bool isValued ( const TLabeller& valueLab ) const { return valueLab.isLabelled(theValued); }
	bool getValue ( void ) const { return checkValue; }
	bool setValued ( bool val, const TLabeller& valueLab )
	{
		valueLab.set(theValued);
		checkValue = val;
		return val;
	}

	// common part
	bool isCommon ( void ) const { return common != 0; }
	void setCommon ( void ) { ++common; }
	void clearCommon ( void ) { common = 0; }
		/// keep COMMON flag iff both flags are set; @return true if it is the case
	bool correctCommon ( unsigned int n )
	{
		if ( common == n )
			return true;
		clearCommon();
		return false;
	}

		/// put initial values on the flags
	void initFlags ( void )
	{
		TLabeller::clear(theChecked);
		TLabeller::clear(theValued);
		clearCommon();
	}

	// get info about taxonomy structure

	const EqualNames& synonyms ( void ) const { return Synonyms; }

		/// mark vertex as the one corresponding to a given ENTRY
	void setVertexAsHost ( const ClassifiableEntry* entry ) { const_cast<ClassifiableEntry*>(entry)->setTaxVertex(this); }
		/// set sample to ENTRY
	void setSample ( const ClassifiableEntry* entry, bool linkBack = true )
	{
		sample = entry;
		if ( likely(linkBack) )
			setVertexAsHost(entry);
	}

public:
		/// empty c'tor
	TaxonomyVertex() { initFlags(); }
		/// init c'tor; use it only for Top/Bot initialisations
	explicit TaxonomyVertex ( const ClassifiableEntry* p ) : TaxonomyVertex()
		{ setSample(p); }
		/// no copy
	TaxonomyVertex ( const TaxonomyVertex& ) = delete;
		/// no assignment
	TaxonomyVertex& operator = ( const TaxonomyVertex& ) = delete;

		/// add P as a synonym to current vertex
	void addSynonym ( const ClassifiableEntry* p )
	{
		Synonyms.push_back(p);
		setVertexAsHost(p);
	}
		/// clears the vertex
	void clear ( void )
	{
		Links[0].clear();
		Links[1].clear();
		sample = nullptr;
		initFlags();
	}
		/// get RO access to the primer
	const ClassifiableEntry* getPrimer ( void ) const { return sample; }

		/// add link in given direction to vertex
	void addNeighbour ( bool upDirection, TaxonomyVertex* p ) { neigh(upDirection).push_back(p); }
		/// check if vertex has no neighbours in given direction
	bool noNeighbours ( bool upDirection ) const { return neigh(upDirection).empty(); }

	// iterator access to parents/children

	iterator begin ( bool upDirection ) { return neigh(upDirection).begin(); }
	iterator end ( bool upDirection ) { return neigh(upDirection).end(); }

	const_iterator begin ( bool upDirection ) const { return neigh(upDirection).begin(); }
	const_iterator end ( bool upDirection ) const { return neigh(upDirection).end(); }

	/** Adds vertex to existing graph. For every Up, Down such that (Up->Down)
		creates couple of links (Up->this), (this->Down). Don't work with synonyms!!!
	*/
	void incorporate ( void );
		/// @return v if node represents a synonym (v=Up[i]==Down[j]); @return NULL otherwise
	TaxonomyVertex* getSynonymNode ( void )
	{
		// try to find Vertex such that Vertex\in Up and Vertex\in Down
		for ( auto& upV: neigh(true) )
			for ( const auto& downV: neigh(false) )
				if ( upV == downV )	// found such vertex
					return upV;
		return nullptr;
	}
		/// Remove link to P from neighbours (given by flag). @return true if such link was removed
	bool removeLink ( bool upDirection, TaxonomyVertex* p );
		/// clear all links in a given direction
	void clearLinks ( bool upDirection ) { neigh(upDirection).clear(); }
		/// remove one half of a given node from a graph
	void removeLinks ( bool upDirection );
		/// remove given node from a graph
	void remove ( void ) { removeLinks(true); removeLinks(false); setInUse(false); }

	// usage methods

		/// @return true iff the node is in use
	bool isInUse ( void ) const { return inUse; }
		/// set the inUse value of the node
	void setInUse ( bool value ) { inUse = value; }

	// output methods

		/// print taxonomy vertex in format <equals parents children>
	void print ( std::ostream& o ) const;

	// save/load interface; implementation is in SaveLoad.cpp

		/// save label of the entry
	void SaveLabel ( SaveLoadManager& m ) const;
		/// load label of the entry
	void LoadLabel ( SaveLoadManager& m );
		/// save neighbours of the entry
	void SaveNeighbours ( SaveLoadManager& m ) const;
		/// load neighbours of the entry
	void LoadNeighbours ( SaveLoadManager& m );
}; // TaxonomyVertex

/// structure to sort tax vertices
struct TaxVertexLess
{
	bool operator()(const TaxonomyVertex* s1, const TaxonomyVertex* s2) const
		{ return strcmp(s1->getPrimer()->getName(), s2->getPrimer()->getName()) < 0; }
};

/// sorted vertices set
typedef std::set<const TaxonomyVertex*, TaxVertexLess> TVSet;

#endif // TAXVERTEX_H
