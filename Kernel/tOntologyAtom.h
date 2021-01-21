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

#ifndef TONTOLOGYATOM_H
#define TONTOLOGYATOM_H

#include <set>

#include "globaldef.h"
#include "tDLAxiom.h"

/// representation of the ontology atom
class TOntologyAtom
{
protected:	// internal types
		/// type to compare 2 atoms
	struct AtomLess
	{
		bool operator()(const TOntologyAtom* a1, const TOntologyAtom* a2) const
			{ return a1->getId() < a2->getId(); }
	};

public:		// typedefs
		/// set of axioms
	typedef AxiomVec AxiomSet;
		/// set of atoms
	typedef std::set<TOntologyAtom*, AtomLess> AtomSet;

protected:	// members
		/// set of axioms in the atom
	AxiomSet AtomAxioms;
		/// set of axioms in the module (Atom's ideal)
	AxiomSet ModuleAxioms;
		/// set of atoms current one depends on
	AtomSet DepAtoms;
		/// set of all atoms current one depends on
	AtomSet AllDepAtoms;
		/// unique atom's identifier
	size_t Id = 0;

protected:	// methods
		/// remove all atoms in AllDepAtoms from DepAtoms
	void filterDep ( void )
	{
		for ( TOntologyAtom* atom : AllDepAtoms )
			DepAtoms.erase(atom);
	}
		/// build all dep atoms; filter them from DepAtoms
	void buildAllDepAtoms ( AtomSet& checked )
	{
		// first gather all dep atoms from all known dep atoms
		for ( TOntologyAtom* atom : DepAtoms )
		{
			const AtomSet& Dep = atom->getAllDepAtoms(checked);
			AllDepAtoms.insert ( Dep.begin(), Dep.end() );
		}
		// now filter them out from known dep atoms
		filterDep();
		// add direct deps to all deps
		AllDepAtoms.insert ( DepAtoms.begin(), DepAtoms.end() );
		// now the atom is checked
		checked.insert(this);
	}

public:		// interface
		/// set the module axioms
	void setModule ( const AxiomSet& module ) { ModuleAxioms = module; }
		/// add axiom AX to an atom
	void addAxiom ( TDLAxiom* ax )
	{
		AtomAxioms.push_back(ax);
		ax->setAtom(this);
	}
		/// add atom to the dependency set
	void addDepAtom ( TOntologyAtom* atom )
	{
		if ( likely(atom != nullptr) && atom != this )
			DepAtoms.insert(atom);
	}
		/// get all the atoms the current one depends on; build this set if necessary
	const AtomSet& getAllDepAtoms ( AtomSet& checked )
	{
		if ( checked.count(this) == 0 )	// not build yet
			buildAllDepAtoms(checked);
		return AllDepAtoms;
	}

	// access to axioms

		/// get all the atom's axioms
	const AxiomSet& getAtomAxioms ( void ) const { return AtomAxioms; }
		/// get all the module axioms
	const AxiomSet& getModule ( void ) const { return ModuleAxioms; }
		/// get atoms a given one depends on
	const AtomSet& getDepAtoms ( void ) const { return DepAtoms; }

		/// get the value of the id
    size_t getId() const { return Id; }
    	/// set the value of the id to ID
    void setId ( size_t id ) { Id = id; }
}; // TOntologyAtom

#endif
