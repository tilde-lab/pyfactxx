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

#ifndef DUMPINTERFACE_H
#define DUMPINTERFACE_H

#include <ostream>

#include "tNamedEntry.h"
#include "dltree.h"

class TConcept;
class TRole;

/// enumeration of dump interface concept operations
enum diOp
{		// concept expressions
	diNot,
	diAnd,
	diOr,
	diExists,
	diForall,
	diGE,
	diLE,
		// role expressions
	diInv,
		// individual expressions
	diOneOf,
		// wrong operation
	diErrorOp,
		// end of the enum
	diLastOp = diErrorOp
}; // diOp

/// enumeration of dump interface axioms
enum diAx
{		// wrong axiom
	diErrorAx = diLastOp,
		// concept axioms
	diDefineC,
	diImpliesC,
	diEqualsC,
	diDisjointC,
		// role axioms
	diDefineR,
	diTransitiveR,
	diFunctionalR,
	diImpliesR,
	diEqualsR,
	diDomainR,
	diRangeR,
		// individual axioms
	diInstanceOf,
}; // diAx

/// general interface for dumping ontology to a proper format
class dumpInterface
{
protected:	// members
		/// output stream
	std::ostream& o;
		/// indentation level
	unsigned int indent = 0;
		/// print every axiom on a single line (need for sorting, for example)
	bool oneliner = false;

protected:	// methods
		/// write necessary amount of TABs
	void skipIndent ( void );
		/// increase indentation level
	void incIndent ( void );
		/// decrease indentation level
	void decIndent ( void );

public:		// interface
		/// the only c'tor -- empty
	explicit dumpInterface ( std::ostream& oo ) : o(oo) {}
		/// empty d'tor
	virtual ~dumpInterface() = default;

		/// set ONELINER flag; @return previous value
	bool useIndentation ( bool val )
	{
		bool ret = oneliner;
		oneliner = val;
		return ret;
	}

	// global prologue/epilogue
	virtual void prologue ( void ) {}
	virtual void epilogue ( void ) {}

	// general concept expression
	virtual void dumpTop ( void ) {}
	virtual void dumpBottom ( void ) {}
	virtual void dumpNumber ( unsigned int ) {}

	virtual void startOp ( diOp ) {}
		/// start operation >=/<= with number
	virtual void startOp ( diOp, unsigned int ) {}
	virtual void contOp ( diOp ) {}
	virtual void finishOp ( diOp ) {}

	virtual void startAx ( diAx ) {}
	virtual void contAx ( diAx ) {}
	virtual void finishAx ( diAx ) {}

		/// obtain name by the named entry
	virtual void dumpName ( const TNamedEntry* p ) { o << p->getName(); }
		/// dump concept atom (as used in expression)
	virtual void dumpConcept ( const TConcept* ) {}
		/// dump role atom (as used in expression)
	virtual void dumpRole ( const TRole* ) {}
}; // dumpInterface

inline void dumpInterface :: skipIndent ( void )
{
	if ( oneliner )
		return;
	o << "\n";
	for ( unsigned int i = indent; i > 0; --i )
		o << "  ";
}

inline void dumpInterface :: incIndent ( void )
{
	skipIndent();
	++indent;	// operands of AND-like
}

inline void dumpInterface :: decIndent ( void )
{
	--indent;
	skipIndent();
}

	// dump given concept expression
void dumpCExpression ( dumpInterface* dump, const DLTree* C );
	// dump given role expression
void dumpRExpression ( dumpInterface* dump, const DLTree* R );

#endif
