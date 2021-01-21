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

#ifndef CGLABEL_H
#define CGLABEL_H

#include "CWDArray.h"
#include "dlVertex.h"	// for DagTag

/// class implementing a label in a node of a completion graph
class CGLabel
{
public:		// type interface
		/// RO iterator on label
	typedef CWDArray::const_iterator const_iterator;

public:		// external classes
		/// class for save/restore
	class SaveState
	{
	public:
			/// states for simple-, complex- and extra labels
		CWDArray::SaveState sc, cc;
	}; // SaveState

protected:	// members
		/// all simple concepts, labelled a node
	CWDArray scLabel;
		/// all complex concepts (ie, FORALL, GE), labelled a node
	CWDArray ccLabel;

public:	// static methods
		/// @return true iff TAG represents complex concept
	static bool isComplexConcept ( DagTag tag )
	{
		switch(tag)
		{
		case dtForall:
		case dtLE:
		case dtIrr:
		case dtNN:
		case dtChoose:
			return true;
		default:
			return false;
		}
	}

public:		// interface
		/// init newly created node
	void init ( void );

	//----------------------------------------------
	// Label access interface
	//----------------------------------------------

	// label iterators

		/// begin() iterator for a label with simple concepts
	const_iterator begin_sc ( void ) const { return scLabel.begin(); }
		/// end() iterator for a label with simple concepts
	const_iterator end_sc ( void ) const { return scLabel.end(); }
		/// begin() iterator for a label with complex concepts
	const_iterator begin_cc ( void ) const { return ccLabel.begin(); }
		/// end() iterator for a label with complex concepts
	const_iterator end_cc ( void ) const { return ccLabel.end(); }

	//----------------------------------------------
	// Label access interface
	//----------------------------------------------
		/// get (RW) label associated with the concepts defined by the flag
	CWDArray& getLabel ( bool isComplex ) { return isComplex ? ccLabel : scLabel; }
		/// get (RO) label associated with the concepts defined by the flag
	const CWDArray& getLabel ( bool isComplex ) const { return isComplex ? ccLabel : scLabel; }
		/// get (RW) label associated with the concepts defined by TAG
	CWDArray& getLabel ( DagTag tag ) { return getLabel(isComplexConcept(tag)); }
		/// get (RO) label associated with the concepts defined by TAG
	const CWDArray& getLabel ( DagTag tag ) const { return getLabel(isComplexConcept(tag)); }

	// TODO table interface

		/// get ToDoEntry offset by given SC labels' iterator
	int getSCOffset ( const_iterator p ) const { return int(p-begin_sc()); }
		/// get ToDoEntry offset by given CC labels' iterator
	int getCCOffset ( const_iterator p ) const { return int(-(p-begin_cc()+1)); }
		/// get the index of the latest (ie just inserted) concept in the label
	int getLast ( DagTag tag ) const
	{
		if ( isComplexConcept(tag) )
			return getCCOffset(end_cc()-1);	// complex concept
		else
			return getSCOffset(end_sc()-1);	// simple concept
	}
		/// get the concept by given index in the node's label
	const ConceptWDep& getConcept ( int n ) const
	{
		if ( n < 0 )
			return ccLabel.getConcept(size_t(-n-1));
		else
			return scLabel.getConcept(size_t(n));
	}
		/// get CC offset of a complex concept BP that appears in the label
	int getCCOffset ( BipolarPointer bp ) const
	{
		for ( const_iterator p = begin_cc(), p_end = end_cc(); p < p_end; ++p )
			if ( *p == bp )
				return getCCOffset(p);
		// BP should appear in the label
		fpp_unreachable();
	}

	// check if node is labelled by given concept

		/// check whether node is labelled by (arbitrary) concept P
	bool contains ( BipolarPointer p ) const;
		/// check whether node is labelled by simple concept P
	bool containsSC ( BipolarPointer p ) const { return scLabel.contains(p); }
		/// check whether node is labelled by complex concept P
	bool containsCC ( BipolarPointer p ) const { return ccLabel.contains(p); }

	//----------------------------------------------
	// Blocking support
	//----------------------------------------------

		/// check whether LABEL is a superset of a current one
	bool operator <= ( const CGLabel& label ) const
	{
		return ( scLabel <= label.scLabel ) && ( ccLabel <= label.ccLabel );
	}
		/// check whether LABEL is a subset of a current one
	bool operator >= ( const CGLabel& label ) const { return label <= *this; }
		/// check whether LABEL is the same as a current one
	bool operator == ( const CGLabel& label ) const
		{ return (*this <= label) && (label <= *this); }

	//----------------------------------------------
	// Save/restore interface
	//----------------------------------------------

		/// save label using given SS
	void save ( SaveState& ss ) const
	{
		scLabel.save(ss.sc);
		ccLabel.save(ss.cc);
	}
		/// restore label to given LEVEL using given SS
	void restore ( const SaveState& ss, unsigned int level )
	{
		scLabel.restore(ss.sc,level);
		ccLabel.restore(ss.cc,level);
	}

	//----------------------------------------------
	// Output
	//----------------------------------------------

		/// print the whole label
	void print ( std::ostream& o ) const
	{
		scLabel.print(o);
		ccLabel.print(o);
	}
}; // CGLabel

inline void
CGLabel :: init ( void )
{
	// init label with reasonable size
	scLabel.init(8);	// FIXME!! correct size later on
	ccLabel.init(4);	// FIXME!! correct size later on
}

inline bool
CGLabel :: contains ( BipolarPointer p ) const
{
#ifdef ENABLE_CHECKING
	fpp_assert ( isCorrect(p) );	// sanity checking
#endif
	switch(p)
	{
	case bpTOP:		return true;
	case bpBOTTOM:	return false;
	default:		return containsSC(p) || containsCC(p);
	}
}

#endif
