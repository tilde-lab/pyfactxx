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

#ifndef TAXONOMYCREATOR_H
#define TAXONOMYCREATOR_H

// taxonomy creator for DL

#include "Taxonomy.h"
#include "SearchableStack.h"
#include "tSignature.h"

class TaxonomyCreator
{
protected:	// internal typedefs
		/// set of the subsumers
	typedef ClassifiableEntry::linkSet SubsumerSet;
		/// SS RW iterator
	typedef SubsumerSet::iterator ss_iterator;
		/// abstract class to represent the known subsumers of a concept
	class KnownSubsumers
	{
	public:		// interface
			/// empty  d'tor
		virtual ~KnownSubsumers() = default;

		// iterators

			/// begin of the Sure subsumers interval
		virtual ss_iterator s_begin ( void ) = 0;
			/// end of the Sure subsumers interval
		virtual ss_iterator s_end ( void ) = 0;
			/// begin of the Possible subsumers interval
		virtual ss_iterator p_begin ( void ) = 0;
			/// end of the Possible subsumers interval
		virtual ss_iterator p_end ( void ) = 0;

		// flags

			/// whether there are no sure subsumers
		bool s_empty ( void ) { return s_begin() == s_end(); }
			/// whether there are no possible subsumers
		bool p_empty ( void ) { return p_begin() == p_end(); }
			/// @return true iff CE is the possible subsumer
		virtual bool isPossibleSub ( const ClassifiableEntry* ) const { return true; }
	}; // KnownSubsumers

		/// class to represent the TS's
	class ToldSubsumers: public KnownSubsumers
	{
	protected:		// members
			/// two iterators for the TS of a concept
		ss_iterator beg, end;

	public:		// interface
			/// c'tor
		ToldSubsumers ( ss_iterator b, ss_iterator e ) : beg(b), end(e) {}

		// iterators

			/// begin of the Sure subsumers interval
		ss_iterator s_begin ( void ) override { return beg; }
			/// end of the Sure subsumers interval
		ss_iterator s_end ( void ) override { return end; }
			/// begin of the Possible subsumers interval
		ss_iterator p_begin ( void ) override { return end; }
			/// end of the Possible subsumers interval
		ss_iterator p_end ( void ) override { return end; }
	}; // ToldSubsumers

protected:	// members
		/// Taxonomy to be build
	Taxonomy* pTax;
		/// aux array to keep synonyms found during TS classification
	std::vector<ClassifiableEntry*> Syns;

		/// labeller for marking nodes with a label wrt classification
	TLabeller valueLabel;
		/// pointer to currently classified entry
	const ClassifiableEntry* curEntry= nullptr;

		/// number of tested entries
	unsigned int nEntries = 0;
		/// number of completely-defined entries
	unsigned long nCDEntries = 0;

		/// session flag: shows the direction of the search
	bool upDirection = false;
		/// optimisation flag: if entry is completely defined by it's told subsumers, no other classification required
	bool useCompletelyDefined = false;

		/// stack for Taxonomy creation
	SearchableStack <ClassifiableEntry*> waitStack;
		/// told subsumers corresponding to a given entry
	SearchableStack <KnownSubsumers*> ksStack;
		/// signature of a \bot-module corresponding to a given entry
	SearchableStack <const TSignature*> sigStack;

protected:	// methods
		/// initialise aux entry with given concept p
	void setCurrentEntry ( const ClassifiableEntry* p )
	{
		pTax->getCurrent()->clear();
		pTax->getCurrent()->setSample(p);
		curEntry = p;
	}

	//-----------------------------------------------------------------
	//--	General classification support
	//-----------------------------------------------------------------

		/// return 1 if current entry is classified as a synonym of already classified one
	virtual bool classifySynonym ( void ) { return pTax->processSynonym(); }

		/// set up Told Subsumers for the current entry
	void setToldSubsumers ( void );
		/// add non-redundant candidates for the current entry
	void setNonRedundantCandidates ( void );

	//-----------------------------------------------------------------
	//--	Tunable methods (depending on taxonomy type)
	//-----------------------------------------------------------------

		/// check if no classification needed (synonym, orphan, unsatisfiable)
	virtual bool immediatelyClassified ( void ) { return classifySynonym(); }
		/// setup TD phase (ie, identify/set parent candidates)
	void setupTopDown ( void )
	{
		setToldSubsumers();
		if ( !needTopDown() )
		{
			++nCDEntries;
			setNonRedundantCandidates();
		}
	}
		/// check if it is possible to skip TD phase
	virtual bool needTopDown ( void ) const { return false; }
		/// explicitly run TD phase
	virtual void runTopDown ( void ) {}
		/// check if it is possible to skip BU phase
	virtual bool needBottomUp ( void ) const { return false; }
		/// explicitly run BU phase
	virtual void runBottomUp ( void ) {}

		/// actions that to be done BEFORE entry will be classified
	virtual void preClassificationActions ( void ) {}

	//-----------------------------------------------------------------
	//--	General classification methods
	//-----------------------------------------------------------------

		/// Common pre- and post-action to setup 2-phase algorithm
	void performClassification ( void );
		/// fills parents and children of Current using tunable general approach
	void generalTwoPhaseClassification ( void );
		/// @return true if V is a direct parent of current wrt labels
	bool isDirectParent ( TaxonomyVertex* v ) const;
		/// add PARENT as a parent if it exists and is direct parent
	void addPossibleParent ( TaxonomyVertex* parent )
	{
		if ( parent && isDirectParent(parent) )
			pTax->getCurrent()->addNeighbour ( /*upDirection=*/true, parent );
	}

	//-----------------------------------------------------------------
	//--	DFS-based classification
	//-----------------------------------------------------------------

		/// @return true if a NODE has been valued during current classification pass
	bool isValued ( TaxonomyVertex* node ) const { return node->isValued(valueLabel); }
		/// get the subsumption value of a NODE wrt currently classified one
	bool getValue ( TaxonomyVertex* node ) const { return node->getValue(); }
		/// set the classification value of a NODE to VALUE
	bool setValue ( TaxonomyVertex* node, bool value ) const { return node->setValued ( value, valueLabel ); }

		/// prepare known subsumers for given entry if necessary
	virtual KnownSubsumers* buildKnownSubsumers ( ClassifiableEntry* p )
		{ return new ToldSubsumers(p->told().begin(), p->told().end()); }
		/// prepare signature for given entry
	virtual const TSignature* buildSignature ( ClassifiableEntry* ) { return nullptr; }
		/// add top entry together with its known subsumers
	void addTop ( ClassifiableEntry* p )
	{
		waitStack.push(p);
		ksStack.push(buildKnownSubsumers(p));
		sigStack.push(buildSignature(p));
	}
		/// remove top entry
	void removeTop ( void )
	{
		waitStack.pop();
		delete ksStack.top();
		ksStack.pop();
		sigStack.pop();
	}
		/// ensure that all TS of the top entry are classified. @return the reason of cycle or NULL.
	ClassifiableEntry* prepareTS ( ClassifiableEntry* cur );
		/// classify top entry of the stack
	void classifyTop ( void )
	{
		fpp_assert ( !waitStack.empty() );	// sanity check
		setCurrentEntry(waitStack.top());
		performClassification();
		removeTop();
	}
		/// propagate the TRUE value of the KS subsumption up the hierarchy
	void propagateTrueUp ( TaxonomyVertex* node );
		/// propagate the FALSE value of the KS subsumption down the hierarchy
	void propagateFalseDown ( TaxonomyVertex* node );
		/// propagate constant VALUE into an appropriate direction
	bool setAndPropagate ( TaxonomyVertex* node, bool value )
	{
		if ( value )
			propagateTrueUp(node);
		else
			propagateFalseDown(node);
		return value;
	}

	ss_iterator told_begin ( void ) { return ksStack.top()->s_begin(); }
	ss_iterator told_end ( void ) { return ksStack.top()->s_end(); }

		/// check if it is necessary to log taxonomy action
	virtual bool needLogging ( void ) const { return false; }

public:		// interface
		/// init c'tor
	explicit TaxonomyCreator ( Taxonomy* tax ) : pTax(tax) {}
		/// no copy c'tor
	TaxonomyCreator ( const TaxonomyCreator& ) = delete;
		/// no assignment
	TaxonomyCreator& operator = ( const TaxonomyCreator& ) = delete;
		/// d'tor
	virtual ~TaxonomyCreator() = default;

	//------------------------------------------------------------------------------
	//--	classification interface
	//------------------------------------------------------------------------------

		/// classify given entry: general method is by DFS
	void classifyEntry ( ClassifiableEntry* p )
	{
		fpp_assert ( waitStack.empty() );	// sanity check

		// don't classify artificial concepts
		if ( p->isNonClassifiable() )
			return;
		prepareTS(p);
	}
 		/// clear all labels from Taxonomy vertices
	void clearLabels ( void ) { pTax->clearVisited(); valueLabel.newLabel(); }

	// flags interface

		/// set Completely Defined flag
	void setCompletelyDefined ( bool use ) { useCompletelyDefined = use; }

	// taxonomy info access

		/// print taxonomy info to a stream
	virtual void print ( std::ostream& o ) const;
}; // TaxonomyCreator

#endif // TAXONOMYCREATOR_H
