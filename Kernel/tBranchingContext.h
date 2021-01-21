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

#ifndef TBRANCHINGCONTEXT_H
#define TBRANCHINGCONTEXT_H

#include "ConceptWithDep.h"

class DlCompletionTree;

	/// class for saving branching context of a Reasoner
class BranchingContext
{
public:		// members
		/// currently processed node
	DlCompletionTree* curNode = nullptr;
		/// currently processed concept
	ConceptWDep curConcept {bpINVALID};
		/// positions of the Used members
	size_t pUsedIndex = 0, nUsedIndex = 0;
		/// size of a session GCIs vector
	size_t SGsize = 0;
		/// dependencies for branching clashes
	DepSet branchDep;

public:		// interface
		/// empty c'tor
	BranchingContext() = default;
		/// no copy c'tor
	BranchingContext ( const BranchingContext& ) = delete;
		/// no assignment
	BranchingContext& operator = ( const BranchingContext& ) = delete;
		/// empty d'tor
	virtual ~BranchingContext() = default;

		/// init indices (if necessary)
	virtual void init ( void ) {}
		/// give the next branching alternative
	virtual void nextOption ( void ) {}
}; // BranchingContext

		/// branching context for the OR operations
class BCOr: public BranchingContext
{
public:		// types
		/// short OR indexes
	typedef std::vector<BipolarPointer> OrIndex;
		/// short OR index iterator
	typedef OrIndex::const_iterator or_iterator;

public:		// members
		/// relevant disjuncts (ready to add)
	OrIndex applicableOrEntries;
		/// current branching index
	size_t branchIndex = 0;

public:		// interface
		/// init branch index
	void init ( void ) override { branchIndex = 0; }
		/// give the next branching alternative
	void nextOption ( void ) override { ++branchIndex; }

	// access to the fields

		/// check if the current processing OR entry is the last one
	bool isLastOrEntry ( void ) const { return applicableOrEntries.size() == branchIndex+1; }
		/// 1st element of OrIndex
	or_iterator orBeg ( void ) const { return applicableOrEntries.begin(); }
		/// current element of OrIndex
	or_iterator orCur ( void ) const { return orBeg() + branchIndex; }
}; // BCOr

	/// branching context for the Choose-rule
class BCChoose: public BranchingContext
{
}; // BCChoose

	/// branching context for the NN-rule
class BCNN: public BranchingContext
{
public:		// members
		/// the value of M used in the NN rule
	unsigned int value = 0;

public:		// interface
		/// init value
	void init ( void ) override { value = 1; }
		/// give the next branching alternative
	void nextOption ( void ) override { ++value; }

	// access to the fields

		/// check if the NN has no option to process
	bool noMoreNNOptions ( unsigned int n ) const { return value > n; }
}; // BCNN

	/// branching context for the LE operations
template <typename T>
class BCLE: public BranchingContext
{
public:		// types
		/// Cardinality Restriction index type
		// TODO: make it 8-bit or so
	typedef unsigned short int CRIndex;
		/// vector of edges
	typedef std::vector<T*> EdgeVector;

public:		// members
		/// vector of edges to be merged
	EdgeVector ItemsToMerge;
		/// index of a edge into which the merge is performing
	CRIndex toIndex = 0;
		/// index of a merge candidate
	CRIndex fromIndex = 0;

public:		// interface
		/// init indices
	void init ( void ) override
	{
		toIndex = 0;
		fromIndex = 0;
	}
		/// correct fromIndex after changing
	void resetMCI ( void ) { fromIndex = static_cast<CRIndex>(ItemsToMerge.size() - 1); }
		/// give the next branching alternative
	void nextOption ( void ) override
	{
		--fromIndex;	// get new merge candidate
		if ( fromIndex == toIndex )	// nothing more can be mergeable to BI node
		{
			++toIndex;	// change the candidate to merge to
			resetMCI();
		}
	}

	// access to the fields

		/// get FROM pointer to merge
	T* getFrom ( void ) const { return ItemsToMerge[fromIndex]; }
		/// get FROM pointer to merge
	T* getTo ( void ) const { return ItemsToMerge[toIndex]; }
		/// check if the LE has no option to process
	bool noMoreLEOptions ( void ) const { return fromIndex <= toIndex; }
}; // BCLE

	/// branching context for the barrier
class BCBarrier: public BranchingContext
{
}; // BCBarrier

#endif
