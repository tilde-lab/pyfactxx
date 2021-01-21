/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2003-2015 Dmitry Tsarkov and The University of Manchester
Copyright (C) 2015-2017 Dmitry Tsarkov

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
*/

// methods to work with Atomic Decomposition

#include <fstream>

#include "AtomicDecomposer.h"
#include "tOntologyPrinterLISP.h"	// AD prints
#include "procTimer.h"
#include "cppi.h"

// defined in FaCT.cpp
extern std::ofstream Out;

// print axioms of an atom
static void
printAtomAxioms ( const TOntologyAtom::AxiomSet& Axioms )
{
	static TLISPOntologyPrinter LP(Out);
	// do cycle via set to keep the order
	typedef std::set<TDLAxiom*> AxSet;
	const AxSet M ( Axioms.begin(), Axioms.end() );
	for ( const TDLAxiom* axiom : M )
		axiom->accept(LP);
}

/// print dependencies of an atom
static void
printAtomDeps ( const TOntologyAtom::AtomSet& Dep )
{
	if ( unlikely(Dep.empty()) )
		Out << "Ground";
	else
		Out << "Depends on:";
	for ( const TOntologyAtom* atom : Dep )
		Out << " " << atom->getId();
	Out << "\n";
}

/// print the atom with an index INDEX of the AD
static void
printADAtom ( const TOntologyAtom* atom )
{
	const TOntologyAtom::AxiomSet& Axioms = atom->getAtomAxioms();
	Out << "Atom " << atom->getId() << " (size " << Axioms.size() << ", module size " << atom->getModule().size() << "):\n";
	printAtomAxioms(Axioms);
	printAtomDeps(atom->getDepAtoms());
}

/// @return all the axioms in the AD
static size_t
sizeAD ( AOStructure* AOS )
{
	size_t ret = 0;
	for ( const TOntologyAtom* atom : *AOS )
		ret += atom->getAtomAxioms().size();
	return ret;
}

void
CreateAD ( TOntology* Ontology, ModuleMethod moduleMethod )
{
	std::cerr << "\n";
	// do the atomic decomposition
	TsProcTimer timer;
	timer.Start();
	TModularizer mod(moduleMethod);
	AtomicDecomposer* AD = new AtomicDecomposer(&mod);
	AD->setProgressIndicator(new CPPI());
	AOStructure* AOS = AD->getAOS ( Ontology, M_BOT );
	timer.Stop();
	Out << "Atomic structure built in " << timer << " seconds\n";
	size_t sz = sizeAD(AOS);
	Out << "Atomic structure (" << sz << " axioms in " << AOS->size() << " atoms; " << Ontology->size()-sz << " tautologies):\n";
	for ( const TOntologyAtom* atom : *AOS )
		printADAtom(atom);
	delete AD;
}
