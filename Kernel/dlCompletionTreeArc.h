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

#ifndef DLCOMPLETIONTREEARC_H
#define DLCOMPLETIONTREEARC_H

#include "globaldef.h"
#include "DeletelessAllocator.h"
#include "DepSet.h"
#include "tRole.h"
#include "tRestorer.h"

class DlCompletionTree;

class DlCompletionTreeArc
{
friend class DlCompletionGraph;
public:		// external type definitions
		/// type for the edges allocator
	typedef DeletelessAllocator<DlCompletionTreeArc> EdgeAllocator;

protected:	// members
		/// pointer to "to" node
	DlCompletionTree* Node = nullptr;
		/// role, labelling given arc
	const TRole* Role = nullptr;
		/// dep-set of the arc
	DepSet depSet;
		/// pointer to reverse arc
	DlCompletionTreeArc* Reverse = nullptr;
		/// true if the edge going from a predecessor to a successor
	bool SuccEdge = true;

private:	// methods
		/// init an arc with R as a label and NODE on given LEVEL; use it inside MAKEARCS only
	void init ( const TRole* role, const DepSet& dep, DlCompletionTree* node )
	{
		Role = role;
		depSet = dep;
		Node = node;
		Reverse = nullptr;
	}

protected:	// classes
		/// class for restoring edge
	class TCTEdgeRestorer: public TRestorer
	{
	protected:
		DlCompletionTreeArc* p;
		const TRole* r;
	public:
		explicit TCTEdgeRestorer ( DlCompletionTreeArc* q ) : p(q), r(q->Role) {}
		void restore ( void ) override { p->Role = r; p->Reverse->Role = r->inverse(); }
	}; // TCTEdgeRestorer

		/// class for restoring dep-set
	class TCTEdgeDepRestorer: public TRestorer
	{
	protected:
		DlCompletionTreeArc* p;
		DepSet dep;
	public:
		explicit TCTEdgeDepRestorer ( DlCompletionTreeArc* q ) : p(q), dep(q->getDep()) {}
		void restore ( void ) override { p->depSet = dep; }
	}; // TCTEdgeDepRestorer

protected:	// methods

		/// set given arc as a reverse of current
	void setReverse ( DlCompletionTreeArc* v )
	{
		Reverse = v;
		v->Reverse = this;
	}

public:		// interface
		/// empty c'tor
	DlCompletionTreeArc() = default;
		/// no copy c'tor
	DlCompletionTreeArc ( const DlCompletionTreeArc& ) = delete;
		/// no assignment
	DlCompletionTreeArc& operator = ( const DlCompletionTreeArc& ) = delete;

		/// get label of the edge
	const TRole* getRole ( void ) const { return Role; }
		/// get dep-set of the edge
	const DepSet& getDep ( void ) const { return depSet; }

		/// set the successor field
	void setSuccEdge ( bool val ) { SuccEdge = val; }
		/// @return true if the edge is the successor one
	bool isSuccEdge ( void ) const { return SuccEdge; }
		/// @return true if the edge is the predecessor one
	bool isPredEdge ( void ) const { return !SuccEdge; }

		/// get (RW) access to the end of arc
	DlCompletionTree* getArcEnd ( void ) const { return Node; }
		/// get access to reverse arc
	DlCompletionTreeArc* getReverse ( void ) const { return Reverse; }

		/// check if arc is labelled by a super-role of PROLE
	bool isNeighbour ( const TRole* pRole ) const
	{
		return !isIBlocked() && ( *pRole >= *getRole() );
	}

		/// is arc merged to another
	bool isIBlocked ( void ) const { return (Role == nullptr); }
		/// check whether the edge is reflexive
	bool isReflexiveEdge ( void ) const { return getArcEnd() == getReverse()->getArcEnd(); }

	//----------------------------------------------
	// saving/restoring
	//----------------------------------------------

		/// save and invalidate arc (together with reverse arc)
	TRestorer* save ( void )
	{
		if ( Role == nullptr )	// don't invalidate edge twice
			return nullptr;

		TRestorer* ret = new TCTEdgeRestorer(this);
		Role = nullptr;
		Reverse->Role = nullptr;
		return ret;
	}

		/// add dep-set to an edge; return restorer
	TRestorer* addDep ( const DepSet& dep )
	{
		if ( dep.empty() )
			return nullptr;
		TRestorer* ret = new TCTEdgeDepRestorer(this);
		depSet.add(dep);
		return ret;
	}

	// output

		/// print current arc
	void Print ( std::ostream& o ) const
		{ o << "<" << ( isIBlocked() ? "-" : Role->getName() ) << depSet << ">"; }
}; // DlCompletionTreeArc

#endif // DLCOMPLETIONTREEARC_H
