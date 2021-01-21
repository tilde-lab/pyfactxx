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

#ifndef TSIGNATUREUPDATER_H
#define TSIGNATUREUPDATER_H

#include "tDLExpression.h"
#include "tSignature.h"

/// update the signature by adding all signature elements from the expression
class TExpressionSignatureUpdater: public DLExpressionVisitor
{
protected:	// members
		/// Signature to be filled
	TSignature& sig;

protected:	// methods
		/// helper for concept arguments
	void vC ( const TConceptArg& expr ) { expr.getC()->accept(*this); }
		/// helper for individual arguments
	void vI ( const TIndividualArg& expr ) { expr.getI()->accept(*this); }
		/// helper for object role arguments
	void vOR ( const TObjectRoleArg& expr ) { expr.getOR()->accept(*this); }
		/// helper for object role arguments
	void vDR ( const TDataRoleArg& expr ) { expr.getDR()->accept(*this); }
		/// helper for the named entity
	void vE ( const TNamedEntity& e ) { sig.add(e.getEntity()); }
		/// array helper
	template <typename Argument>
	void processArray ( const TDLNAryExpression<Argument>& expr )
	{
		for ( typename TDLNAryExpression<Argument>::iterator p = expr.begin(), p_end = expr.end(); p != p_end; ++p )
			(*p)->accept(*this);
	}

public:		// interface
		/// init c'tor
	explicit TExpressionSignatureUpdater ( TSignature& s ) : sig(s) {}

public:		// visitor interface
	// concept expressions
	void visit ( const TDLConceptTop& ) override {}
	void visit ( const TDLConceptBottom& ) override {}
	void visit ( const TDLConceptName& expr ) override { vE(expr); }
	void visit ( const TDLConceptNot& expr ) override { vC(expr); }
	void visit ( const TDLConceptAnd& expr ) override { processArray(expr); }
	void visit ( const TDLConceptOr& expr ) override { processArray(expr); }
	void visit ( const TDLConceptOneOf& expr ) override { processArray(expr); }
	void visit ( const TDLConceptObjectSelf& expr ) override { vOR(expr); }
	void visit ( const TDLConceptObjectValue& expr ) override { vOR(expr); vI(expr); }
	void visit ( const TDLConceptObjectExists& expr ) override { vOR(expr); vC(expr); }
	void visit ( const TDLConceptObjectForall& expr ) override { vOR(expr); vC(expr); }
	void visit ( const TDLConceptObjectMinCardinality& expr ) override { vOR(expr); vC(expr); }
	void visit ( const TDLConceptObjectMaxCardinality& expr ) override { vOR(expr); vC(expr); }
	void visit ( const TDLConceptObjectExactCardinality& expr ) override { vOR(expr); vC(expr); }
	void visit ( const TDLConceptDataValue& expr ) override { vDR(expr); }
	void visit ( const TDLConceptDataExists& expr ) override { vDR(expr); }
	void visit ( const TDLConceptDataForall& expr ) override { vDR(expr); }
	void visit ( const TDLConceptDataMinCardinality& expr ) override { vDR(expr); }
	void visit ( const TDLConceptDataMaxCardinality& expr ) override { vDR(expr); }
	void visit ( const TDLConceptDataExactCardinality& expr ) override { vDR(expr); }

	// individual expressions
	void visit ( const TDLIndividualName& expr ) override { vE(expr); }

	// object role expressions
	void visit ( const TDLObjectRoleTop& ) override {}
	void visit ( const TDLObjectRoleBottom& ) override {}
	void visit ( const TDLObjectRoleName& expr ) override { vE(expr); }
	void visit ( const TDLObjectRoleInverse& expr ) override { vOR(expr); }
	void visit ( const TDLObjectRoleChain& expr ) override { processArray(expr); }
	void visit ( const TDLObjectRoleProjectionFrom& expr ) override { vOR(expr); vC(expr); }
	void visit ( const TDLObjectRoleProjectionInto& expr ) override { vOR(expr); vC(expr); }

	// data role expressions
	void visit ( const TDLDataRoleTop& ) override {}
	void visit ( const TDLDataRoleBottom& ) override {}
	void visit ( const TDLDataRoleName& expr ) override { vE(expr); }

	// data expressions
	void visit ( const TDLDataTop& ) override {}
	void visit ( const TDLDataBottom& ) override {}
	void visit ( const TDLDataTypeName& ) override {}
	void visit ( const TDLDataTypeRestriction& ) override {}
	void visit ( const TDLDataValue& ) override {}
	void visit ( const TDLDataNot& ) override {}
	void visit ( const TDLDataAnd& ) override {}
	void visit ( const TDLDataOr& ) override {}
	void visit ( const TDLDataOneOf& ) override {}

	// facets
	void visit ( const TDLFacetMinInclusive& ) override {}
	void visit ( const TDLFacetMinExclusive& ) override {}
	void visit ( const TDLFacetMaxInclusive& ) override {}
	void visit ( const TDLFacetMaxExclusive& ) override {}
}; // TExpressionSignatureUpdater

/// update signature by adding the signature of a given axiom to it
class TSignatureUpdater: public DLAxiomVisitor
{
protected:	// members
		/// helper with expressions
	TExpressionSignatureUpdater Updater;

protected:	// methods
		/// helper for the expression processing
	void v ( const TDLExpression* E ) { E->accept(Updater); }
		/// helper for the [begin,end) interval
	template <typename Iterator>
	void v ( Iterator begin, Iterator end )
	{
		for ( ; begin != end; ++begin )
			v(*begin);
	}

public:		// visitor interface
	void visit ( const TDLAxiomDeclaration& axiom ) override { v(axiom.getDeclaration()); }

	void visit ( const TDLAxiomEquivalentConcepts& axiom ) override { v ( axiom.begin(), axiom.end() ); }
	void visit ( const TDLAxiomDisjointConcepts& axiom ) override { v ( axiom.begin(), axiom.end() ); }
	void visit ( const TDLAxiomDisjointUnion& axiom ) override { v(axiom.getC()); v ( axiom.begin(), axiom.end() ); }
	void visit ( const TDLAxiomEquivalentORoles& axiom ) override { v ( axiom.begin(), axiom.end() ); }
	void visit ( const TDLAxiomEquivalentDRoles& axiom ) override { v ( axiom.begin(), axiom.end() ); }
	void visit ( const TDLAxiomDisjointORoles& axiom ) override { v ( axiom.begin(), axiom.end() ); }
	void visit ( const TDLAxiomDisjointDRoles& axiom ) override { v ( axiom.begin(), axiom.end() ); }
	void visit ( const TDLAxiomSameIndividuals& axiom ) override { v ( axiom.begin(), axiom.end() ); }
	void visit ( const TDLAxiomDifferentIndividuals& axiom ) override { v ( axiom.begin(), axiom.end() ); }
	void visit ( const TDLAxiomFairnessConstraint& axiom ) override { v ( axiom.begin(), axiom.end() ); }

	void visit ( const TDLAxiomRoleInverse& axiom ) override { v(axiom.getRole()); v(axiom.getInvRole()); }
	void visit ( const TDLAxiomORoleSubsumption& axiom ) override { v(axiom.getRole()); v(axiom.getSubRole()); }
	void visit ( const TDLAxiomDRoleSubsumption& axiom ) override { v(axiom.getRole()); v(axiom.getSubRole()); }
	void visit ( const TDLAxiomORoleDomain& axiom ) override { v(axiom.getRole()); v(axiom.getDomain()); }
	void visit ( const TDLAxiomDRoleDomain& axiom ) override { v(axiom.getRole()); v(axiom.getDomain()); }
	void visit ( const TDLAxiomORoleRange& axiom ) override { v(axiom.getRole()); v(axiom.getRange()); }
	void visit ( const TDLAxiomDRoleRange& axiom ) override { v(axiom.getRole()); v(axiom.getRange()); }
	void visit ( const TDLAxiomRoleTransitive& axiom ) override { v(axiom.getRole()); }
	void visit ( const TDLAxiomRoleReflexive& axiom ) override { v(axiom.getRole()); }
	void visit ( const TDLAxiomRoleIrreflexive& axiom ) override { v(axiom.getRole()); }
	void visit ( const TDLAxiomRoleSymmetric& axiom ) override { v(axiom.getRole()); }
	void visit ( const TDLAxiomRoleAsymmetric& axiom ) override { v(axiom.getRole()); }
	void visit ( const TDLAxiomORoleFunctional& axiom ) override { v(axiom.getRole()); }
	void visit ( const TDLAxiomDRoleFunctional& axiom ) override { v(axiom.getRole()); }
	void visit ( const TDLAxiomRoleInverseFunctional& axiom ) override { v(axiom.getRole()); }

	void visit ( const TDLAxiomConceptInclusion& axiom ) override { v(axiom.getSubC()); v(axiom.getSupC()); }
	void visit ( const TDLAxiomInstanceOf& axiom ) override { v(axiom.getIndividual()); v(axiom.getC()); }
	void visit ( const TDLAxiomRelatedTo& axiom ) override { v(axiom.getIndividual()); v(axiom.getRelation()); v(axiom.getRelatedIndividual()); }
	void visit ( const TDLAxiomRelatedToNot& axiom ) override { v(axiom.getIndividual()); v(axiom.getRelation()); v(axiom.getRelatedIndividual()); }
	void visit ( const TDLAxiomValueOf& axiom ) override { v(axiom.getIndividual()); v(axiom.getAttribute()); }
	void visit ( const TDLAxiomValueOfNot& axiom ) override { v(axiom.getIndividual()); v(axiom.getAttribute()); }

public:		// interface
		/// init c'tor
	explicit TSignatureUpdater ( TSignature& sig ) : Updater(sig) {}
}; // TSignatureUpdater

#endif
