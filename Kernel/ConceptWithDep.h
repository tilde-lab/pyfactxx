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

#ifndef CONCEPTWITHDEP_H
#define CONCEPTWITHDEP_H

#include "BiPointer.h"
#include "DepSet.h"

/// Concept with dependence: bipolar pointer to concept and a set of dependencies
class ConceptWDep
{
protected:	// members
		/// "pointer" to a concept in DAG
	BipolarPointer Concept = bpINVALID;
		/// dep-set for a concept
	DepSet depSet;

public:		// methods
		/// empty c'tor
	ConceptWDep() = default;
		/// c'tor with empty dep-set
	explicit ConceptWDep ( BipolarPointer p ) : Concept(p) {}
		/// usual c'tor
	ConceptWDep ( BipolarPointer p, const DepSet& dep ) : Concept(p), depSet(dep) {}
		/// copy c'tor
	ConceptWDep ( const ConceptWDep& c ) = default;
		/// copy c'tor with additional dep-set
	ConceptWDep ( const ConceptWDep& c, const DepSet& dep ) : Concept(c.Concept), depSet(c.depSet) { depSet.add(dep); }

	// comparison

	bool operator == ( const ConceptWDep& ce ) const { return Concept == ce.Concept; }
	bool operator != ( const ConceptWDep& ce ) const { return Concept != ce.Concept; }
	bool operator == ( BipolarPointer p ) const { return Concept == p; }
	bool operator != ( BipolarPointer p ) const { return Concept != p; }

	// access to elements

		/// get bp-part
	BipolarPointer bp ( void ) const { return Concept; }
		/// get dep-set part
	const DepSet& getDep ( void ) const { return depSet; }

		/// create CWD with inverted body and the same dep-set
	ConceptWDep inverse ( void ) const { return ConceptWDep ( ::inverse(Concept), getDep () ); }
		/// add dep-set to a CWD
	void addDep ( const DepSet& d ) { depSet.add(d); }

		/// print concept and a dep-set
	template <typename O>
	friend O& operator << ( O& o, const ConceptWDep& c ) { o << c.Concept << c.getDep(); return o; }
}; // ConceptWDep

/// create an inverse of a given CWD with the same dep-set
inline ConceptWDep inverse ( const ConceptWDep& C ) { return ConceptWDep ( inverse(C.bp()), C.getDep() ); }

#endif // CONCEPTWITHDEP_H
