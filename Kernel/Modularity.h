/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2011-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef MODULARITY_H
#define MODULARITY_H

#include <queue>

// uncomment the next line to use AD to speed up modularisation
#define RKG_USE_AD_IN_MODULE_EXTRACTION

#include "tOntology.h"
#include "SigIndex.h"
#include "LocalityChecker.h"

#include "ModuleType.h"

#ifdef RKG_USE_AD_IN_MODULE_EXTRACTION
#	include "tOntologyAtom.h"
#endif

/// class to create modules of an ontology wrt module type
class TModularizer
{
protected:	// types
		/// RO iterator over axiom vector
	typedef AxiomVec::const_iterator const_iterator;

protected:	// members
		/// shared signature signature
	TSignature sig;
		/// internal syntactic locality checker
	LocalityChecker* Checker;
		/// module as a list of axioms
	AxiomVec Module;
		/// pointer to a sig index; if not NULL then use optimized algo
	SigIndex sigIndex;
		/// queue of unprocessed entities
	std::queue<const TNamedEntity*> WorkQueue;
		/// number of locality check calls
	unsigned long long nChecks = 0;
		/// number of non-local axioms
	unsigned long long nNonLocal = 0;
		/// true if no atoms are processed ATM
	bool noAtomsProcessing = true;

protected:	// methods
		/// update SIG wrt the axiom signature
	void addAxiomSig ( const TSignature& axiomSig )
	{
		for ( const TNamedEntity* entity : axiomSig )
			if ( !sig.contains(entity) )	// new one
			{
				WorkQueue.push(entity);
				sig.add(entity);
			}
	}
		/// add an axiom to a module
	void addAxiomToModule ( TDLAxiom* axiom )
	{
		axiom->setInModule(true);
		Module.push_back(axiom);
		// update the signature
		addAxiomSig(axiom->getSignature());
	}
		/// @return true iff an AXiom is non-local
	bool isNonLocal ( const TDLAxiom* ax )
	{
		++nChecks;
		if ( Checker->local(ax) )
			return false;
		++nNonLocal;
		return true;
	}

		/// add an axiom if it is non-local (or in noCheck is true)
	void addNonLocal ( TDLAxiom* ax, bool noCheck )
	{
		if ( unlikely(noCheck) || unlikely(isNonLocal(ax)) )
		{
			addAxiomToModule(ax);

#		ifdef RKG_USE_AD_IN_MODULE_EXTRACTION
			if ( noAtomsProcessing && ax->getAtom() != nullptr )
			{
				noAtomsProcessing = false;
				addNonLocal ( ax->getAtom()->getModule(), /*noCheck=*/true );
				noAtomsProcessing = true;
			}
#		endif
		}
	}
		/// add all the non-local axioms from given axiom-set AxSet
	void addNonLocal ( const AxiomVec& AxSet, bool noCheck )
	{
		for ( TDLAxiom* axiom : AxSet )
			if ( !axiom->isInModule() && axiom->isInSS() ) // in the given range but not in module yet
				addNonLocal ( axiom, noCheck );
	}
		/// build a module traversing axioms by a signature
	void extractModuleQueue ( void )
	{
		// init queue with a sig
		for ( const TNamedEntity* entity : sig )
			WorkQueue.push(entity);
		// add all the axioms that are non-local wrt given value of a top-locality
		addNonLocal ( sigIndex.getNonLocal(sig.topCLocal()), /*noCheck=*/true );
		// main cycle
		while ( !WorkQueue.empty() )
		{
			const TNamedEntity* entity = WorkQueue.front();
			WorkQueue.pop();
			// for all the axioms that contains entity in their signature
			addNonLocal ( sigIndex.getAxioms(entity), /*noCheck=*/false );
		}
	}
		/// extract module wrt presence of a sig index
	void extractModule ( const_iterator begin, const_iterator end )
	{
		size_t size = (size_t)(end-begin);
		Module.clear();
		Module.reserve(size);
		// clear the module flag in the input
		const_iterator p;
		for ( p = begin; p != end; ++p )
			(*p)->setInModule(false);
		for ( p = begin; p != end; ++p )
			if ( (*p)->isUsed() )
				(*p)->setInSS(true);
		extractModuleQueue();
		for ( p = begin; p != end; ++p )
			(*p)->setInSS(false);
	}

public:		// interface
		/// init c'tor
	explicit TModularizer ( ModuleMethod moduleMethod )
		: Checker(createLocalityChecker(moduleMethod,&sig))
		, sigIndex(Checker)
		{}
		// d'tor
	~TModularizer() { delete Checker; }

		/// allow the checker to preprocess an ontology if necessary
	void preprocessOntology ( const AxiomVec& vec )
	{
		Checker->preprocessOntology(vec);
		sigIndex.clear();
		sigIndex.preprocessOntology(vec);
		nChecks += 2*vec.size();
	}
		/// extract module wrt SIGNATURE and TYPE from the set of axioms [BEGIN,END)
	void extract ( const_iterator begin, const_iterator end, const TSignature& signature, ModuleType type )
	{
		bool topLocality = (type == M_TOP);

		sig = signature;
		sig.setLocality(topLocality);
 		extractModule ( begin, end );

		if ( type != M_STAR )
			return;

		// here there is a star: do the cycle until stabilization
		size_t size;
		AxiomVec oldModule;
		do
		{
			size = Module.size();
			oldModule.swap(Module);
			topLocality = !topLocality;

			sig = signature;
			sig.setLocality(topLocality);
	 		extractModule ( oldModule.begin(), oldModule.end() );
		} while ( size != Module.size() );
	}
		/// extract module wrt SIGNATURE and TYPE from the axiom vector VEC
	void extract ( const AxiomVec& Vec, const TSignature& signature, ModuleType type )
		{ extract ( Vec.begin(), Vec.end(), signature, type ); }
		/// extract module wrt SIGNATURE and TYPE from O
	void extract ( const TOntology& O, const TSignature& signature, ModuleType type )
		{ extract ( O.getAxioms(), signature, type ); }
		/// @return true iff the axiom AX is a tautology wrt given type
	bool isTautology ( TDLAxiom* ax, ModuleType type )
	{
		bool topLocality = (type == M_TOP);
		sig = ax->getSignature();
		sig.setLocality(topLocality);
		// axiom is a tautology if it is local wrt its own signature
 		bool toReturn = Checker->local(ax);
 		if ( likely ( type != M_STAR || !toReturn ) )
 			return toReturn;
 		// here it is STAR case and AX is local wrt BOT
 		sig.setLocality(!topLocality);
 		return Checker->local(ax);
	}

		/// get RW access to the sigIndex (mainly to (un-)register axioms on the fly)
	SigIndex* getSigIndex ( void ) { return &sigIndex; }

		/// get the last computed module
	const AxiomVec& getModule ( void ) const { return Module; }
		/// get access to a signature
	const TSignature& getSignature ( void ) const { return sig; }
		/// get access to the Locality checker
	LocalityChecker* getLocalityChecker ( void ) { return Checker; }
		/// get number of checks made
	unsigned long long getNChecks ( void ) const { return nChecks; }
		/// get number of axioms that were local
	unsigned long long getNNonLocal ( void ) const { return nNonLocal; }
}; // TModularizer

#endif
