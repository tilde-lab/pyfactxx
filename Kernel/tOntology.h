/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2008-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef TONTOLOGY_H
#define TONTOLOGY_H

#include <vector>

#include "globaldef.h"
#include "tDLAxiom.h"
#include "tExpressionManager.h"

/// define ontology as a set of axioms
class TOntology
{
public:		// types
		/// RW iterator over the base type
	typedef AxiomVec::iterator iterator;

protected:	// members
		/// all the axioms
	AxiomVec Axioms;
		/// retracted since last change
	AxiomVec Retracted;
		/// expression manager that builds all the expressions for the axioms
	TExpressionManager EManager;
		/// id to be given to the next axiom
	unsigned int axiomId = 0;
		/// index of the 1st unprocessed axiom
	size_t axiomToProcess = 0;
		/// true iff ontology was changed
	bool changed = false;

public:		// interface
		/// empty c'tor
	TOntology() = default;
		/// d'tor
	~TOntology() { clear(); }

		/// @return true iff the ontology was changed since its last load
	bool isChanged ( void ) const { return changed; }
		/// set the processed marker to the end of the ontology
	void setProcessed ( void ) { axiomToProcess = Axioms.size(); Retracted.clear(); changed = false; }

		/// add given axiom to the ontology
	TDLAxiom* add ( TDLAxiom* p )
	{
		p->setId(++axiomId);
		Axioms.push_back(p);
		changed = true;
		return p;
	}
		/// retract given axiom to the ontology
	void retract ( TDLAxiom* p )
	{
//		if ( p->getId() <= Axioms.size() && Axioms[p->getId()-1] == p )
		{
			changed = true;
			p->setUsed(false);
			Retracted.push_back(p);
		}
	}
		/// mark all the axioms as not in the module
	void clearModuleInfo ( void )
	{
		for ( iterator p = Axioms.begin(), p_end = Axioms.end(); p < p_end; ++p )
			(*p)->setInModule(false);
	}
		/// safe clear the ontology (do not remove axioms)
	void safeClear ( void ) { Axioms.clear(); }
		/// clear axioms (delete all axioms)
	void clearAxioms ( void )
	{
		for ( iterator p = Axioms.begin(), p_end = Axioms.end(); p < p_end; ++p )
			delete *p;
		safeClear();
	}
		/// clear the ontology
	void clear ( void )
	{
		clearAxioms();
		Retracted.clear();
		EManager.clear();
		axiomToProcess = 0;
		changed = false;
	}

		/// get access to an expression manager
	TExpressionManager* getExpressionManager ( void ) { return &EManager; }

	// access to axioms

		/// get RW access to all axioms in the ontology
	AxiomVec& getAxioms ( void ) { return Axioms; }
		/// get RO access to all axioms in the ontology
	const AxiomVec& getAxioms ( void ) const { return Axioms; }

		/// RW begin() for the whole ontology
	iterator begin ( void ) { return Axioms.begin(); }
		/// RW end() for the whole ontology
	iterator end ( void ) { return Axioms.end(); }
		/// RW begin() for the unprocessed part of the ontology
	iterator beginUnprocessed ( void ) { return Axioms.begin()+(long)axiomToProcess; }
		/// RW end() for the processed part of the ontology
	iterator endProcessed ( void ) { return beginUnprocessed(); }
		/// RW begin() for retracted axioms
	iterator beginRetracted ( void ) { return Retracted.begin(); }
		/// RW end() for retracted axioms
	iterator endRetracted ( void ) { return Retracted.end(); }
		/// get access to the I'th axiom
	TDLAxiom* operator [] ( size_t i ) { return Axioms[i]; }

		/// apply an axiom visitor to all used axioms in the ontology
	void visitOntology ( DLAxiomVisitor& visitor )
	{
		for ( TOntology::iterator p = begin(), p_end = end(); p < p_end; ++p )
			if ( likely((*p)->isUsed()) )
				(*p)->accept(visitor);
	}
		/// size of the ontology
	size_t size ( void ) const { return Axioms.size(); }
		/// get signature of all ontology axioms
	TSignature getSignature ( void )
	{
		TSignature sig;
		for ( iterator p = begin(), p_end = end(); p != p_end; ++p )
			if ( likely((*p)->isUsed()) )
				sig.add((*p)->getSignature());
		return sig;
	}
}; // TOntology

#endif
