/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2012-2015 Dmitry Tsarkov and The University of Manchester
Copyright (C) 2015-2017 Dmitry Tsarkov

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/

#ifndef ONTOLOGYBASEDMODULARIZER_H
#define ONTOLOGYBASEDMODULARIZER_H

#include "Modularity.h"
#include "tOntology.h"

class OntologyBasedModularizer
{
protected:	// members
		/// ontology to work with
	const TOntology& Ontology;
		/// pointer to a modularizer
	TModularizer* Modularizer;

public:		// interface
		/// init c'tor
	OntologyBasedModularizer ( const TOntology& ontology, ModuleMethod moduleMethod )
		: Ontology(ontology)
	{
		Modularizer = new TModularizer(moduleMethod);
		Modularizer->preprocessOntology(Ontology.getAxioms());
	}
		/// d'tor
	~OntologyBasedModularizer() { delete Modularizer; }

		/// get module
	const AxiomVec& getModule ( const AxiomVec& From, const TSignature& sig, ModuleType type )
	{
		Modularizer->extract ( From, sig, type );
		return Modularizer->getModule();
	}
		/// get module
	const AxiomVec& getModule ( const TSignature& sig, ModuleType type )
		{ return getModule ( Ontology.getAxioms(), sig, type ); }
		/// get access to a modularizer
	TModularizer* getModularizer ( void ) { return Modularizer; }
}; // OntologyBasedModularizer

#endif
