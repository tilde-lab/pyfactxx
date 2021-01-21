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

#ifndef LOCALITYCHECKER_H
#define LOCALITYCHECKER_H

#include "tSignature.h"
#include "ModuleMethod.h"
#include "tDLAxiom.h"

/// helper class to set signature and locality class
class SigAccessor
{
protected:	// members
		/// signature of a module
	const TSignature* sig;

public:		// interface
		/// init c'tor
	explicit SigAccessor ( const TSignature* s ) : sig(s) {}
		/// empty d'tor
	virtual ~SigAccessor() = default;

	// locality flags

		/// @return true iff concepts not in the signature are treated as TOPs
	bool topCLocal ( void ) const { return sig->topCLocal(); }
		/// @return true iff concepts not in the signature are treated as BOTTOMs
	bool botCLocal ( void ) const { return !topCLocal(); }
		/// @return true iff roles not in the signature are treated as TOPs
	bool topRLocal ( void ) const { return sig->topRLocal(); }
		/// @return true iff roles not in the signature are treated as BOTTOMs
	bool botRLocal ( void ) const { return !topRLocal(); }

	// signature-based calls
		/// @return the signature
	const TSignature* getSignature ( void ) const { return sig; }
		/// @return true iff SIGnature does NOT contain given entity
	bool nc ( const TNamedEntity* entity ) const { return !sig->contains(entity); }
}; // SigAccessor

/// base class for checking locality of a DL axiom
class LocalityChecker: protected SigAccessor, public DLAxiomVisitor
{
protected:	// members
		/// remember the axiom locality value here
	bool isLocal = true;

public:		// interface
		/// init c'tor
	explicit LocalityChecker ( const TSignature* s ) : SigAccessor(s) {}

		/// @return true iff an AXIOM is local wrt signature
	bool local ( const TDLAxiom* axiom )
	{
		axiom->accept(*this);
		return isLocal;
	}
		/// fake method to match the semantic checker's interface
	virtual void preprocessOntology ( const AxiomVec& ) {}
		/// set a new value of a signature (without changing a locality parameters)
	void setSignatureValue ( const TSignature& Sig )
	{
		TSignature* curSig = const_cast<TSignature*>(sig);
//		bool topC = topCLocal(), topR = topRLocal();
		*curSig = Sig;
//		curSig->setLocality ( topC, topR );
	}
}; // LocalityChecker

/// @return a locality checker corresponding to a given method
extern LocalityChecker* createLocalityChecker ( ModuleMethod moduleMethod, TSignature* pSig );

#endif
