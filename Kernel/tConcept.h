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

#ifndef TCONCEPT_H
#define TCONCEPT_H

#include <set>

#include "taxNamEntry.h"
#include "tNameSet.h"
#include "tLabeller.h"
#include "dltree.h"
#include "dlVertex.h"
#include "LogicFeature.h"

/// type of concept wrt classifiability
enum CTTag
{
	/// not specified
	cttUnspecified,
	/// concept with all parents -- TCD
	cttTrueCompletelyDefined,
	/// concept w/o any told subsumers
	cttOrphan,
	/// concept with all parents -- LCD, TCD or Orphans
	cttLikeCompletelyDefined,
	/// concept with non-primitive TS
	cttHasNonPrimitiveTS,
	/// any other primitive concept
	cttRegular,
	/// any non-primitive concept (except synonyms)
	cttNonPrimitive,
};

inline char getCTTagName ( CTTag tag )
{
	switch(tag)
	{
	case cttUnspecified:			return 'u';
	case cttTrueCompletelyDefined:	return 'T';
	case cttOrphan:					return 'O';
	case cttLikeCompletelyDefined:	return 'L';
	case cttHasNonPrimitiveTS:		return 'N';
	case cttRegular:				return 'r';
	case cttNonPrimitive:			return 'n';
	default:						return char();
	}
}

class TRole;
class SaveLoadManager;

/// class for representing concept-like entries
class TConcept: public ClassifiableEntry
{
private:	// members
		/// label to use in relevant-only checks
	TLabeller::LabelType rel = 0;

protected:	// types
		/// set of extra rules
	typedef std::vector<size_t> ERSet;
		/// set of roles
	typedef std::set<const TRole*> RoleSSet;

public:		// type interface
		/// extra rules iterators
	typedef ERSet::const_iterator er_iterator;

public:		// members
		/// description of a concept
	DLTree* Description = nullptr;
		/// classification type of concept: completely defined (true- or like-), no TS, other
	CTTag classTag = cttUnspecified;
		/// depth of the concept wrt told subsumers
	unsigned int tsDepth = 0;

		/// pointer to the entry in DAG with concept name
	BipolarPointer pName = bpINVALID;
		/// pointer to the entry in DAG with concept definition
	BipolarPointer pBody = bpINVALID;

		/// features for C
	LogicFeatures posFeatures;
		/// features for ~C
	LogicFeatures negFeatures;

		/// all extra rules for a given concept
	ERSet erSet;

protected:	// methods
	// classification TAGs manipulation

		/// calculate value of classification TAG based on told subsumers. WARNING: no TS cycles allowed
	CTTag determineClassTag ( void );

		/// @return true iff description contains references to THIS concept
	bool hasSelfInDesc ( const DLTree* p ) const;
		/// @return copy of P with every entry of THIS replaced with true
	DLTree* replaceSelfWithConst ( const DLTree* p ) const;

	// told subsumers interface

		/// adds concept as a told subsumer of current one; @return value for CDC analysis
	bool addToldSubsumer ( TConcept* p )
	{
		if ( p != this )
		{
			addParentIfNew(p);
			if ( p->isSingleton() || p->isHasSP() )
				setHasSP();		// this has singleton parent
		}

		// if non-primitive concept was found in a description, it's not CD
		return p->isPrimitive();
	}
		/// init told subsumers of the concept by given DESCription; @return TRUE iff concept is CD
	bool initToldSubsumers ( const DLTree* desc, RoleSSet& RolesProcessed );
		/// init told subsumers of the concept by given DESCription; @return TRUE iff concept is CD
	bool initToldSubsumers ( const DLTree* desc )
	{
		RoleSSet RolesProcessed;
		return initToldSubsumers ( desc, RolesProcessed );
	}
		/// find told subsumers by given role's domain
	void SearchTSbyRole ( const TRole* R, RoleSSet& RolesProcessed );
		/// find told subsumers by given role and its supers domains
	void SearchTSbyRoleAndSupers ( const TRole* R, RoleSSet& RolesProcessed );

public:		// methods
		/// the only c'tor
	explicit TConcept ( const std::string& name )
		: ClassifiableEntry (name)
	{
		setPrimitive();
	}
		/// no copy c'tor
	TConcept ( const TConcept& ) = delete;
		/// no assignment
	TConcept& operator = ( const TConcept& ) = delete;
		/// d'tor
	~TConcept() override { deleteTree(Description); }
		/// clear all info of the concept. Use it in removeConcept()
	void clear ( void );

	// simple rules support

		/// add index of a simple rule in TBox to the ER set
	void addExtraRule ( size_t index )
	{
		erSet.push_back(index);
		// FIXME!! double check this!
		setCompletelyDefined(false);
	}
		/// check if a concept is in a disjoint relation with anything
	bool hasExtraRules ( void ) const { return !erSet.empty(); }
		/// iterator for accessing DJ elements
	er_iterator er_begin ( void ) const { return erSet.begin(); }
		/// iterator for accessing DJ elements
	er_iterator er_end ( void ) const { return erSet.end(); }

	// classification TAGs manipulation

		/// get value of tag as it is
	CTTag getClassTag ( void ) const { return classTag; }
		/// get value of a tag; determine it if unset
	CTTag getClassTag ( void )
	{
		if ( classTag == cttUnspecified )
			classTag = determineClassTag();
		return classTag;
	}

	// description manipulation

		/// add concept expression to concept description
	void addDesc ( DLTree* Desc );
		/// @return true iff description contains top-level references to THIS concept
	bool hasSelfInDesc ( void ) const { return hasSelfInDesc(Description); }
		/// remove concept from its own definition (like in case C [= (or C ...)
	void removeSelfFromDescription ( void )
	{
		if ( hasSelfInDesc() )
		{
			DLTree* desc = Description;
			Description = replaceSelfWithConst(desc);
			deleteTree(desc);
		}
		initToldSubsumers();
	}
		/// remove concept description (to save space)
	void removeDescription ( void )
	{	// save Synonym value
		deleteTree(Description);
		Description = nullptr;
	}
		/// check whether it is possible to init this as a non-primitive concept with DESC
	bool canInitNonPrim ( DLTree* desc )
	{
		if ( Description == nullptr )
			return true;
		if ( isNonPrimitive() && equalTrees(Description,desc) )
			return true;
		return false;
	}
		/// switch primitive concept to non-primitive with new definition; @return old definition
	DLTree* makeNonPrimitive ( DLTree* desc )
	{
		DLTree* ret = Description;
		Description = desc;
		setPrimitive(false);
		return ret;
	}

		/// init told subsumers of the concept by it's description
	virtual void initToldSubsumers ( void )
	{
		toldSubsumers.clear();
		clearHasSP();
		// normalise description if the only parent is TOP
		if ( isPrimitive() && Description && Description->Element() == TOP )
			removeDescription();

		bool CD = !hasExtraRules() && isPrimitive();	// not a completely defined if there are extra rules
		if ( Description != nullptr )	// init (additional) told subsumers from definition
			CD &= initToldSubsumers(Description);
		setCompletelyDefined(CD);
	}
		/// init TOP told subsumer if necessary
	void setToldTop ( TConcept* top )
	{
		if ( Description == nullptr && !hasToldSubsumers() )
			addParent(top);
	}
		/// calculate depth wrt told subsumers; return the depth
	unsigned int calculateTSDepth ( void );

	// used in the start procedure of SAT/SUBSUME tests
	BipolarPointer resolveId ( void ) const;	// returns either pName or pBody

		/// register a Primitive flag
	FPP_ADD_FLAG(Primitive,0x10);
		/// register a HasSingletonParent flag
	FPP_ADD_FLAG(HasSP,0x20);
		/// register a Nominal flag
	FPP_ADD_FLAG(Nominal,0x40);
		/// register a Singleton flag
	FPP_ADD_FLAG(Singleton,0x80);

	// concept non-primitivity methods

		/// check if concept is non-primitive concept
	bool isNonPrimitive ( void ) const { return !isPrimitive(); }

	// relevance part

		/// is given concept relevant to given Labeller's state
	bool isRelevant ( const TLabeller& lab ) const { return lab.isLabelled(rel); }
		/// make given concept relevant to given Labeller's state
	void setRelevant ( const TLabeller& lab ) { lab.set(rel); }
		/// make given concept irrelevant to given Labeller's state
	void dropRelevant ( const TLabeller& lab ) { lab.clear(rel); }

	// save/load interface; implementation is in SaveLoad.cpp

		/// save entry
	void Save ( SaveLoadManager& m ) const override;
		/// load entry
	void Load ( SaveLoadManager& m ) override;
}; // TConcept

/// Class for comparison of TConcepts wrt told subsumer depth
class TSDepthCompare
{
public:
	bool operator() ( const TConcept* p, const TConcept* q ) const
		{ return p->tsDepth < q->tsDepth; }
}; // TSDepthCompare

//----------------------------------------------------------------------------
//-- 		implementation
//----------------------------------------------------------------------------

inline BipolarPointer TConcept :: resolveId ( void ) const
{
	// check for Top/Bottom
	if ( pName == bpINVALID )
		return pBody;

	if ( isSynonym() )	// resolve synonyms
		return resolveSynonym(this)->resolveId();

	return pName;	// return concept's name
}

#endif
