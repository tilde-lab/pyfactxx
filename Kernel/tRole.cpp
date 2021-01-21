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

#include <set>
#include <fstream>
#include <algorithm>

#include "tRole.h"
#include "Taxonomy.h"

void TRole :: fillsComposition ( TRoleVec& Composition, const DLTree* tree ) const
{
	if ( tree->Element() == RCOMPOSITION )
	{
		fillsComposition ( Composition, tree->Left() );
		fillsComposition ( Composition, tree->Right() );
	}
	else
		Composition.push_back(resolveRole(tree));
}

/// copy role information (like transitivity, functionality, R&D etc) to synonym
void TRole :: addFeaturesToSynonym ( void )
{
	if ( !isSynonym() )
		return;

	TRole* syn = resolveSynonym(this);

	// don't copy parents: they are already copied during ToldSubsumers processing

	// copy functionality
	if ( isFunctional() && !syn->isFunctional() )
		syn->setFunctional();

	// copy transitivity
	if ( isTransitive() )
		syn->setTransitive ();

	// copy reflexivity
	if ( isReflexive() )
		syn->setReflexive();

	// copy data type
	if ( isDataRole() )
		syn->setDataRole();

	// copy R&D
	if ( pDomain != nullptr )
		syn->setDomain (clone(pDomain));

	// copy disjoint
	if ( isDisjoint() )
		syn->Disjoint.insert ( Disjoint.begin(), Disjoint.end() );

	// copy subCompositions
	syn->subCompositions.insert ( syn->subCompositions.end(),
								  subCompositions.begin(),
								  subCompositions.end() );

	// syn should be the only parent for synonym
	toldSubsumers.clear();
	addParent(syn);
}

// compare 2 TRoles wrt order of synonyms
class TRoleCompare
{
public:
	bool operator() ( TRole* p, TRole* q ) const
	{
		int n = p->getId(), m = q->getId();
		if ( n > 0 && m < 0 )
			return true;
		if ( n < 0 && m > 0 )
			return false;
		return abs(n) < abs(m);
	}
}; // TRoleCompare

TRole* TRole :: eliminateToldCycles ( TRoleSet& RInProcess, TRoleVec& ToldSynonyms )
{
	// skip synonyms
	if ( isSynonym() )
		return nullptr;

	// if we found a cycle...
	if ( RInProcess.find(this) != RInProcess.end() )
	{
		ToldSynonyms.push_back(this);
		return this;
	}

	TRole* ret = nullptr;

	// start processing role
	RInProcess.insert(this);

	// ensure that parents does not contain synonyms
	removeSynonymsFromParents();

	// not involved in cycle -- check all told subsumers
	for ( auto r: told() )
		// if cycle was detected
		if ( (ret = static_cast<TRole*>(r)->eliminateToldCycles ( RInProcess, ToldSynonyms )) != nullptr )
		{
			if ( ret == this )
			{
				std::sort ( ToldSynonyms.begin(), ToldSynonyms.end(), TRoleCompare() );
				// now first element is representative; save it as RET
				ret = *ToldSynonyms.begin();

				// make all others synonyms of RET
				for ( std::vector<TRole*>::iterator
						p = ToldSynonyms.begin()+1, p_end = ToldSynonyms.end(); p < p_end; ++p )
				{
					(*p)->setSynonym(ret);
					ret->addParents((*p)->told());
				}

				ToldSynonyms.clear();
				// restart search for the representative
				RInProcess.erase(this);
				return ret->eliminateToldCycles ( RInProcess, ToldSynonyms );
			}
			else	// some role inside a cycle: save it and return
			{
				ToldSynonyms.push_back(this);
				break;
			}
		}

	// finish processing role
	RInProcess.erase(this);
	return ret;
}

void TRole :: Print ( std::ostream& o ) const
{
	o << "Role \"" << getName() << "\"(" << getId() << ")";

//FIXME!! while it's not necessary
//		o << " [" << r.nUsageFreq << "]";

	// transitivity
	if ( isTransitive() )
		o << "T";

	// reflexivity
	if ( isReflexive() )
		o << "R";

	// functionality
	if ( isTopFunc() )
		o << "t";
	if ( isFunctional() )
		o << "F";

	// data role
	if ( isDataRole() )
		o << "D";

	if ( isSynonym() )
	{
		o << " = \"" << getSynonym()->getName() << "\"\n";
		return;
	}

	if ( !toldSubsumers.empty() )
	{
		ClassifiableEntry::linkSet::const_iterator q = toldSubsumers.begin();

		o << " parents={\"" << (*q)->getName();

		for ( ++q; q != toldSubsumers.end(); ++q )
			o << "\", \"" << (*q)->getName();

		o << "\"}";
	}

	if ( !Disjoint.empty() )
	{
		TRoleSet::const_iterator p = Disjoint.begin(), p_end = Disjoint.end();

		o << " disjoint with {\"" << (*p)->getName();

		for ( ++p; p != p_end; ++p )
			o << "\", \"" << (*p)->getName();

		o << "\"}";
	}

	// range/domain
	if ( getTDomain() != nullptr )
		o << " Domain=(" << getBPDomain() << ")=" << getTDomain();
	if ( getTRange() != nullptr )
		o << " Range=(" << getBPRange() << ")=" << getTRange();

	if ( !isDataRole() )
	{
		o << "\nAutomaton (size " << A.size() << "): " << ( A.isISafe() ? "I" : "i" ) << ( A.isOSafe() ? "O" : "o" );
		A.Print(o);
	}
	o << "\n";
}

// actor to fill vector by traversing taxonomy in a proper direction
class AddRoleActor: public WalkerInterface
{
protected:
	TRole::TRoleVec& rset;
public:
	explicit AddRoleActor ( TRole::TRoleVec& v ) : rset(v) {}
	bool apply ( const TaxonomyVertex& v ) override
	{
		if ( v.getPrimer()->getId() == 0 )
			return false;
		rset.push_back(const_cast<TRole*>(static_cast<const TRole*>(v.getPrimer())));
		return true;
	}
}; // AddRoleActor

/// init ancestors and descendants using Taxonomy
void
TRole :: initADbyTaxonomy ( Taxonomy* pTax, size_t nRoles )
{
	fpp_assert ( isClassified() );	// safety check
	fpp_assert ( Ancestor.empty() && Descendant.empty() );

	// Note that Top/Bottom are not connected to taxonomy yet.

	// fills ancestors by the taxonomy
	AddRoleActor anc(Ancestor);
	pTax->getRelativesInfo</*needCurrent=*/false, /*onlyDirect=*/false, /*upDirection=*/true> ( getTaxVertex(), anc );
	// fills descendants by the taxonomy
	AddRoleActor desc(Descendant);
	pTax->getRelativesInfo</*needCurrent=*/false, /*onlyDirect=*/false, /*upDirection=*/false> ( getTaxVertex(), desc );

	// resize maps for fast access
	DJRoles.resize(nRoles);
	AncMap.resize(nRoles);
	// init map for fast Anc/Desc access
	addAncestorsToBitMap(AncMap);
}

void TRole :: postProcess ( void )
{
	// set Topmost-Functional field
	initTopFunc();
	// init DJ roles map
	if ( isDisjoint() )
		initDJMap();
}

/// check if the role is topmost-functional (internal-use only)
// not very efficient, but good enough
bool TRole :: isRealTopFunc ( void ) const
{
	if ( !isFunctional() )	// all REAL top-funcs have their self-ref in TopFunc already
		return false;
	// if any of the parent is self-proclaimed top-func -- this one is not top-func
	for ( const auto& sup: ancestors() )
		if ( sup->isTopFunc() )
			return false;

	// functional role with no functional parents is top-most functional
	return true;
}

/// set up TopFunc member (internal-use only)
// not very efficient, but good enough
void TRole :: initTopFunc ( void )
{
	if ( isRealTopFunc() )	// TF already set up -- nothing to do
		return;

	if ( isTopFunc() )		// self-proclaimed TF but not real -- need to be updated
		TopFunc.clear();

	// register all real TFs
	for ( const auto& sup: ancestors() )
		if ( sup->isRealTopFunc() )
			TopFunc.push_back(sup);

	if ( !TopFunc.empty() )
		Functionality.setValue(true);
}

// disjoint-related implementation

/// check (and correct) case whether R != S for R [= S
void TRole :: checkHierarchicalDisjoint ( TRole* R )
{
	// if element is disjoint with itself -- the role is empty
	if ( Disjoint.count(R) )
	{
		setDomain(createBottom());
		Disjoint.clear();
		return;
	}

	// check whether a sub-role is disjoint with the given one
	for ( auto& sub: R->descendants() )
		if ( Disjoint.count(sub) )
		{
			sub->setDomain(createBottom());
			Disjoint.erase(sub);
			sub->Disjoint.clear();
		}
}

/// init map of all disjoint roles (N is a size of a bitmap)
void TRole :: initDJMap ( void )
{
	// role R is disjoint with every role S' [= S such that R != S
	for ( const TRole* role : Disjoint )
		DJRoles[role->getIndex()] = true;
}

// automaton-related implementation

void
TRole :: preprocessComposition ( TRoleVec& RS )
{
	bool same = false;
	size_t last = RS.size()-1;
	size_t i = 0;	// current element of the composition

	for ( TRoleVec::iterator p = RS.begin(), p_end = RS.end(); p != p_end; ++p, ++i )
	{
		TRole* R = resolveSynonym(*p);

		if ( R->isBottom() )	// empty role in composition -- nothing to do
		{
			RS.clear();
			return;
		}
		if ( R == this )	// found R in composition
		{
			if ( i != 0 && i != last )		// R in the middle of the composition
				throw EFPPCycleInRIA(getName());
			if ( same )	// second one
			{
				if ( last == 1 )	// transitivity
				{
					RS.clear();
					setTransitive();
					return;
				}
				else					// wrong (undecidable) axiom
					throw EFPPCycleInRIA(getName());
			}
			else		// first one
				same = true;
		}

		*p = R;	// replace possible synonyms
	}
}

/// complete role automaton
void TRole :: completeAutomaton ( TRoleSet& RInProcess )
{
	// check whether RA is already complete
	if ( A.isCompleted() )
		return;

	// if we found a cycle...
	if ( RInProcess.find(this) != RInProcess.end() )
		throw EFPPCycleInRIA(getName());

	// start processing role
	RInProcess.insert(this);

	// make sure that all sub-roles already have completed automata
	for ( auto& sub: descendants() )
		sub->completeAutomaton(RInProcess);

	// add automata for complex role inclusions
	for ( auto& subComp: subCompositions )
		addSubCompositionAutomaton ( subComp, RInProcess );

	// check for the transitivity
	if ( isTransitive() )
		A.addTransitionSafe ( A.final(), RATransition(A.initial()) );

	// here automaton is complete
	A.setCompleted();

	if ( likely(!isBottom()) )	// FIXME!! for now; need better Top/Bot synonyms processing
	for ( auto& p: told() )
	{
		TRole* R = resolveSynonym(static_cast<TRole*>(p));
		if ( R->isTop() )	// do not propagate anything to a top-role
			continue;
		R->addSubRoleAutomaton(this);
		if ( hasSpecialDomain() )
			R->SpecialDomain = true;
	}

	// finish processing role
	RInProcess.erase(this);
}

/// add automaton for a role composition
void
TRole :: addSubCompositionAutomaton ( TRoleVec& RS, TRoleSet& RInProcess )
{
	// first preprocess the role chain
	preprocessComposition(RS);

	if ( RS.empty() )	// fallout from transitivity axiom
		return;

	// here we need a special treatment for R&D
	SpecialDomain = true;

	// tune iterators and states
	auto p = RS.begin(), p_last = RS.end() - 1;
	RAState from = A.initial(), to = A.final();

	if ( RS.front() == this )
	{
		++p;
		from = A.final();
	}
	else if ( RS.back() == this )
	{
		--p_last;
		to = A.initial();
	}

	// make sure the role chain contain at least one element
	fpp_assert ( p <= p_last );

	// create a chain
	bool oSafe = false;	// we couldn't assume that the current role automaton is i- or o-safe
	A.initChain(from);
	for ( ; p != p_last; ++p )
		oSafe = A.addToChain ( completeAutomatonByRole ( *p, RInProcess ), oSafe );

	// add the last automaton to chain
	A.addToChain ( completeAutomatonByRole ( *p, RInProcess ), oSafe, to );
}
