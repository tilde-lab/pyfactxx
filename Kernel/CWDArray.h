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

#ifndef CWDARRAY_H
#define CWDARRAY_H

#include <ostream>
#include <algorithm>	// find

#include "globaldef.h"
#include "ConceptWithDep.h"

enum addConceptResult { acrClash, acrExist, acrDone };

class TRestorer;

/// array of concepts with dep-set, which may be viewed as a label of a completion-graph
class CWDArray
{
protected:	// internal typedefs
		/// base type
	typedef std::vector<ConceptWDep> ConceptSet;
		/// RW iterator
	typedef ConceptSet::iterator iterator;

		/// restorer for the merge
	friend class UnMerge;

public:		// type interface
		/// class for saving one label
	class SaveState
	{
	public:
			/// end pointer of the label
		size_t ep = 0;
	}; // SaveState

		/// const iterator on label
	typedef ConceptSet::const_iterator const_iterator;

protected:	// members
		/// array of concepts together with dep-sets
	ConceptSet Base;

public:		// interface
		/// init/clear label with given size
	void init ( size_t size )
	{
		Base.reserve(size);
		Base.clear();
	}

	//----------------------------------------------
	// Label access interface
	//----------------------------------------------

	// label iterators

		/// begin RO iterator
	const_iterator begin ( void ) const { return Base.begin(); }
		/// end RO iterator
	const_iterator end ( void ) const { return Base.end(); }

	// add concept

		/// adds concept P to a label
	void add ( const ConceptWDep& p ) { Base.push_back(p); }
		/// update concept BP with a dep-set DEP; @return the appropriate restorer
	TRestorer* updateDepSet ( BipolarPointer bp, const DepSet& dep );

	// access concepts

		/// check whether label contains BP (ignoring dep-set)
	bool contains ( BipolarPointer bp ) const { return std::find ( begin(), end(), bp ) != end(); }
		/// get the concept by given index in the node's label
	const ConceptWDep& getConcept ( size_t n ) const { return Base[n]; }

	//----------------------------------------------
	// Blocking support
	//----------------------------------------------

		/// check whether LABEL is a superset of a current one
	bool operator <= ( const CWDArray& label ) const
	{
		for ( const_iterator p = begin(), p_end = end(); p < p_end; ++p )
			if ( !label.contains(p->bp()) )
				return false;

		return true;
	}
		/// check whether LABEL is a subset of a current one
	bool operator >= ( const CWDArray& label ) const { return label <= *this; }
		/// check whether LABEL is the same as a current one
	bool operator == ( const CWDArray& label ) const
		{ return (*this <= label) && (label <= *this); }

	//----------------------------------------------
	// Save/restore interface
	//----------------------------------------------

		/// save label using given SS
	void save ( SaveState& ss ) const { ss.ep = Base.size(); }
		/// restore label to given LEVEL using given SS
	void restore ( const SaveState& ss, unsigned int level );

	//----------------------------------------------
	// Output
	//----------------------------------------------

		/// print the whole label
	void print ( std::ostream& o ) const;
}; // CWDArray

#endif
