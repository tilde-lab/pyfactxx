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

#ifndef DLVERTEX_H
#define DLVERTEX_H

#include <vector>
#include <cstring>	// memset
#include <iosfwd>

#include "globaldef.h"
#include "BiPointer.h"
#include "modelCacheInterface.h"
#include "mergeableLabel.h"    // for sort inferences

class DLDag;
class TRole;
class TNamedEntry;
class SaveLoadManager;

/// different Concept Expression tags
/*
 * The one who changing this should always check:
 *	- Additional fields in the class DLVertex
 *		=> operator ==
 *		=> hash functions
 *	- DLVertex methods omitStat(), getTagName(), Print()
 *	- DLDag methods *Stat()
 *	- DLDag methods getIndex(), updateIndex()
 *	- tree2dag()
 *	- mergeSorts(v)
 *	- setRelevant()
 *	- PrintDagEntry()
 *	- ToDoPriorMatrix::getIndex()
 *	- CGLabel::isComplexConcept()
 *	- prepareCascadedCache()
 *	- commonTacticBody()
 *	- DLVertex::Save/Load methods (SaveLoad.cpp)
 */
enum DagTag {
	// illegal entry
	dtBad = 0,
	// operations
	dtTop,
	dtAnd,
	dtForall,
	dtLE,
	dtIrr,		// \neg\exists R.Self
	dtProj,		// aux vertex with Projection FROM the current node
	dtNN,		// NN-rule was applied
	dtChoose,	// choose-rule

	// ID's
	dtPConcept,	// primitive concept
	dtNConcept,			// non-primitive concept
	dtPSingleton,
	dtNSingleton,
	dtDataType,
	dtDataValue,
	dtDataExpr,		// data type with restrictions
};

	/// check whether given DagTag is a primitive named concept-like entity
inline bool isPNameTag ( DagTag tag ) { return (tag == dtPConcept || tag == dtPSingleton); }
	/// check whether given DagTag is a non-primitive named concept-like entity
inline bool isNNameTag ( DagTag tag ) { return (tag == dtNConcept || tag == dtNSingleton); }
	/// check whether given DagTag is a named concept-like entity
inline bool isCNameTag ( DagTag tag ) { return isPNameTag(tag) || isNNameTag(tag); }

// define complex switch labels
#define dtConcept dtPConcept: case dtNConcept
#define dtSingleton dtPSingleton: case dtNSingleton
#define dtName dtConcept: case dtSingleton
#define dtData dtDataType: case dtDataValue: case dtDataExpr

/// interface for the cache of DLVertex
class DLVertexCache
{
protected:	// members
		/// cache for the positive entry
	const modelCacheInterface* pCache = nullptr;
		/// cache for the negative entry
	const modelCacheInterface* nCache = nullptr;

public:		// interface
		/// d'tor
	virtual ~DLVertexCache() { delete pCache; delete nCache; }

	// cache interface

		/// return cache wrt positive flag
	const modelCacheInterface* getCache ( bool pos ) const { return pos ? pCache : nCache; }
		/// set cache wrt positive flag; note that cache is set up only once
	void setCache ( bool pos, const modelCacheInterface* p )
	{
		if ( pos )
			pCache = p;
		else
			nCache = p;
	}
}; // DLVertexCache

class DLVertexStatistic
{
public:		// types
		/// type for a statistic
	typedef unsigned short int StatType;

protected:	// members
		/// maximal depth, size and frequency of reference of the expression
	StatType stat[10];

public:		// static methods
		/// get access to statistic by the depth of a concept
	static unsigned int getStatIndexDepth ( bool pos ) { return (pos ? 0 : 1); }
		/// get access to statistic by the size of a concept
	static unsigned int getStatIndexSize ( bool pos ) { return (pos ? 2 : 3); }
		/// get access to statistic by the # of branching rules of a concept
	static unsigned int getStatIndexBranch ( bool pos ) { return (pos ? 4 : 5); }
		/// get access to statistic by the # of generating rules of a concept
	static unsigned int getStatIndexGener ( bool pos ) { return (pos ? 6 : 7); }
		/// get access to statistic by the freq of a concept
	static unsigned int getStatIndexFreq ( bool pos ) { return (pos ? 8 : 9); }

public:		// interface
		/// default c'tor
	DLVertexStatistic ( void ) { std::memset ( stat, 0, sizeof(stat) ); }
		/// empty d'tor
	virtual ~DLVertexStatistic() = default;

	// set methods

		/// add-up all stat values at once by explicit values
	void updateStatValues ( StatType d, StatType s, StatType b, StatType g, bool pos )
	{
		stat[getStatIndexSize(pos)] += s;
		stat[getStatIndexBranch(pos)] += b;
		stat[getStatIndexGener(pos)] += g;
		if ( d > stat[getStatIndexDepth(pos)] )
			stat[getStatIndexDepth(pos)] = d;
	}
		/// add-up all values at once by a given vertex
	void updateStatValues ( const DLVertexStatistic& v, bool posV, bool pos )
		{ updateStatValues ( v.getDepth(posV), v.getSize(posV), v.getBranch(posV), v.getGener(posV), pos ); }
		/// increment frequency value
	void incFreqValue ( bool pos ) { ++stat[getStatIndexFreq(pos)]; }

	// get methods

		/// general access to a stat value by index
	StatType getStat ( unsigned int i ) const { return stat[i]; }
		/// general access to a stat value by index
	StatType getDepth ( bool pos ) const { return stat[getStatIndexDepth(pos)]; }
		/// general access to a stat value by index
	StatType getSize ( bool pos ) const { return stat[getStatIndexSize(pos)]; }
		/// general access to a stat value by index
	StatType getBranch ( bool pos ) const { return stat[getStatIndexBranch(pos)]; }
		/// general access to a stat value by index
	StatType getGener ( bool pos ) const { return stat[getStatIndexGener(pos)]; }
		/// general access to a stat value by index
	StatType getFreq ( bool pos ) const { return stat[getStatIndexFreq(pos)]; }
}; // DLVertexStatistic

/// tag of the vertex and bits and code for efficient DFS algorithms
class DLVertexTagDFS
{
protected:	// members
		/// main operation in concept expression
		// WARNING: the Visual Studio C++ compiler treat this as a signed integer,
		// so I've added extra bit to stay in the unsigned field
	DagTag Op : 6;	// 17 types
		/// aux field for DFS in presence of cycles
	bool VisitedPos : 1;
		/// aux field for DFS in presence of cycles
	bool ProcessedPos : 1;
		/// true iff node is involved in cycle
	bool inCyclePos : 1;
		/// aux field for DFS in presence of cycles
	bool VisitedNeg : 1;
		/// aux field for DFS in presence of cycles
	bool ProcessedNeg : 1;
		/// true iff node is involved in cycle
	bool inCycleNeg : 1;
		/// padding
	unsigned unused : 4;

public:		// interface
		/// default c'tor
	explicit DLVertexTagDFS ( DagTag op )
		: Op(op)
		, VisitedPos(false)
		, ProcessedPos(false)
		, inCyclePos(false)
		, VisitedNeg(false)
		, ProcessedNeg(false)
		, inCycleNeg(false)
		{}
		/// empty d'tor
	virtual ~DLVertexTagDFS() = default;

	// tag access

		/// return tag of the CE
	DagTag Type ( void ) const { return Op; }

	// DFS-related method

		/// check whether current Vertex is being visited
	bool isVisited ( bool pos ) const { return (pos ? VisitedPos : VisitedNeg); }
		/// check whether current Vertex is processed
	bool isProcessed ( bool pos ) const { return (pos ? ProcessedPos : ProcessedNeg); }
		/// set that the node is being visited
	void setVisited ( bool pos ) { if ( pos ) VisitedPos = true; else VisitedNeg = true; }
		/// set that the node' DFS processing is completed
	void setProcessed ( bool pos )
	{
		if ( pos )
		{
			ProcessedPos = true;
			VisitedPos = false;
		}
		else
		{
			ProcessedNeg = true;
			VisitedNeg = false;
		}
	}
		/// clear DFS flags
	void clearDFS ( void ) { ProcessedPos = VisitedPos = ProcessedNeg = VisitedNeg = false; }
		/// check whether concept is in cycle
	bool isInCycle ( bool pos ) const { return (pos ? inCyclePos : inCycleNeg); }
		/// set concept is in cycle
	void setInCycle ( bool pos ) { if ( pos ) inCyclePos = true; else inCycleNeg = true; }
}; // DLVertexTagDFS

/// usage of the particular vertex during reasoning
class DLVertexUsage
{
public:		// types
		/// type for a statistic
	typedef unsigned long UsageType;

protected:	// members
		/// usage statistic for pos- and neg occurrences of a vertex
	UsageType posUsage = 0;
	UsageType negUsage = 0;

public:		// interface
		/// empty d'tor
	virtual ~DLVertexUsage() = default;

		/// get access to a usage wrt POS
	UsageType getUsage ( bool pos ) const { return pos ? posUsage : negUsage; }
		/// increment usage of the node
	void incUsage ( bool pos ) { if ( pos ) ++posUsage; else ++negUsage; }
}; // DLVertexUsage

class DLVertexSort
{
protected:	// members
		/// maximal depth, size and frequency of reference of the expression
	mergeableLabel Sort;

public:		// interface
		/// empty d'tor
	virtual ~DLVertexSort() = default;

	// label access methods

		/// get RW access to the label
	mergeableLabel& getSort ( void ) { return Sort; }
		/// get RO access to the label
	const mergeableLabel& getSort ( void ) const { return Sort; }
		/// merge local label to label LABEL
	void merge ( mergeableLabel& label ) { Sort.merge(label); }
}; // DLVertexSort

/// Class for normalised Concept Expressions
class DLVertex
	: public DLVertexCache
	, public DLVertexStatistic
#ifdef RKG_PRINT_DAG_USAGE
	, public DLVertexUsage
#endif
	, public DLVertexTagDFS
#ifdef RKG_USE_SORTED_REASONING
	, public DLVertexSort
#endif
{
protected:	// typedefs
		/// base type for array of BPs
	typedef std::vector<BipolarPointer> BaseType;

public:		// typedefs
		/// RO access to the elements of node
	typedef BaseType::const_iterator const_iterator;
		/// RO access to the elements of node in reverse order
	typedef BaseType::const_reverse_iterator const_reverse_iterator;

protected:	// members
		/// set of arguments (CEs, numbers for NR)
	BaseType Child;
		/// pointer to concept-like entry (for PConcept, etc)
	TNamedEntry* Concept = nullptr;
		/// pointer to role (for E\A, NR)
	const TRole* Role = nullptr;
		/// projection role (used for projection op only)
	const TRole* ProjRole = nullptr;
		/// C if available
	BipolarPointer C = bpINVALID;
		/// n if available
	unsigned int n = 0;

public:		// interface
		/// c'tor for Top/CN/And (before adding any operands)
	explicit DLVertex ( DagTag op )
		: DLVertexTagDFS(op)
		{}
		/// c'tor for Refl/Irr
	DLVertex ( DagTag op, const TRole* R )
		: DLVertexTagDFS(op)
		, Role(R)
		{}
		/// c'tor for CN/DE; C is an operand
	DLVertex ( DagTag op, BipolarPointer c )
		: DLVertexTagDFS(op)
		, C(c)
		{}
		/// c'tor for <= n R_C; and for \A R{n}_C; Note order C, n, R->pointer
	DLVertex ( DagTag op, unsigned int m, const TRole* R, BipolarPointer c )
		: DLVertexTagDFS(op)
		, Role(R)
		, C(c)
		, n(m)
		{}
		/// c'tor for ProjFrom R C ProjR
	DLVertex ( const TRole* R, BipolarPointer c, const TRole* ProjR )
		: DLVertexTagDFS(dtProj)
		, Role(R)
		, ProjRole(ProjR)
		, C(c)
		{}
		/// no copy c'tor
	DLVertex ( const DLVertex& ) = delete;
		/// no assignment
	DLVertex& operator = ( const DLVertex& ) = delete;

		/// compare 2 CEs
	bool operator == ( const DLVertex& v ) const
	{
		return (Type() == v.Type()) &&
			   (Role == v.Role) &&
			   (ProjRole == v.ProjRole) &&
			   (C == v.C) &&
			   (n == v.n) &&
			   (Child == v.Child);
	}
		/// compare 2 CEs
	bool operator != ( const DLVertex& v ) const { return !(*this == v); }
		/// return C for concepts/quantifiers/NR vertices
	BipolarPointer getC ( void ) const { return C; }
		/// return N for the (<= n R) vertex
	unsigned int getNumberLE ( void ) const { return n; }
		/// return N for the (>= n R) vertex
	unsigned int getNumberGE ( void ) const { return n+1; }
		/// return STATE for the (\all R{state}.C) vertex
	unsigned int getState ( void ) const { return n; }

		/// return pointer to the first concept name of the entry
	const_iterator begin ( void ) const { return Child.begin(); }
		/// return pointer after the last concept name of the entry
	const_iterator end ( void ) const { return Child.end(); }

		/// return pointer to the last concept name of the entry; WARNING!! works for AND only
	const_reverse_iterator rbegin ( void ) const { return Child.rbegin(); }
		/// return pointer before the first concept name of the entry; WARNING!! works for AND only
	const_reverse_iterator rend ( void ) const { return Child.rend(); }

		/// return pointer to Role for the Role-like vertices
	const TRole* getRole ( void ) const { return Role; }
		/// return pointer to Projection Role for the Projection vertices
	const TRole* getProjRole ( void ) const { return ProjRole; }
		/// get (RW) TConcept for concept-like fields
	TNamedEntry* getConcept ( void ) { return Concept; }
		/// get (RO) TConcept for concept-like fields
	const TNamedEntry* getConcept ( void ) const { return Concept; }

 		/// set TConcept value to entry
	void setConcept ( TNamedEntry* p ) { Concept = p; }
		/// set a concept (child) to Name-like vertex
	void setChild ( BipolarPointer p ) { C = p; }
		/// adds a child to 'AND' vertex; returns TRUE if contradiction found
	bool addChild ( BipolarPointer p );

	// methods for choosing ordering in the OR fields

		/// whether statistic's gathering should be omitted due to the type of a vertex
	bool omitStat ( bool pos ) const;
		/// sort entry using DAG's compare method
	void sortEntry ( const DLDag& dag );

	// output

		/// get text name for CE tag
	const char* getTagName ( void ) const;
		/// print the whole node
	void Print ( std::ostream& o ) const;

	// save/load interface; implementation is in SaveLoad.cpp

		/// save entry
	void Save ( SaveLoadManager& m ) const;
		/// load entry
	void Load ( SaveLoadManager& m );
};	// DLVertex


/// whether statistic's gathering should be omitted due to the type of a vertex
inline bool
DLVertex :: omitStat ( bool pos ) const
{
	switch ( Type() )
	{
	case dtDataType:
	case dtDataValue:
	case dtDataExpr:
	case dtNN:		// no way to get it in expressions
	case dtChoose:	// same
	case dtBad:
	case dtTop:
		return true;
	case dtPConcept:
	case dtPSingleton:
	case dtProj:
		return !pos;
	default:
		return false;
	}
}

/**
 *	returns true iff corresponding NRs may clash.
 *	Clash may appears for (>= n R) and (<= m R) if n > m.
 *	Since \neg (<= n R) represents (>= (n+1) R), so
 *	comparison became (n+1) > m, or n >= m
 */
inline bool mayClashNR ( unsigned int geNR, unsigned int leNR )
{
	return geNR >= leNR;
}

#endif
