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

#ifndef SEMANTICLOCALITYCHECKER_H
#define SEMANTICLOCALITYCHECKER_H

#include "LocalityChecker.h"
#include "Kernel.h"

/// semantic locality checker for DL axioms
class SemanticLocalityChecker: public LocalityChecker
{
protected:	// members
		/// Reasoner to detect the tautology
	ReasoningKernel Kernel;
		/// Expression manager of a kernel
	TExpressionManager* pEM;
		/// map between axioms and concept expressions
	std::map<const TDLAxiom*, const TDLConceptExpression*> ExprMap;

protected:	// methods
		/// @return expression necessary to build query for a given type of an axiom; @return NULL if none necessary
	const TDLConceptExpression* getExpr ( const TDLAxiom* axiom )
	{
		if ( const TDLAxiomRelatedTo* art = dynamic_cast<const TDLAxiomRelatedTo*>(axiom) )
			return pEM->Value ( art->getRelation(), art->getRelatedIndividual() );
		if ( const TDLAxiomValueOf* avo = dynamic_cast<const TDLAxiomValueOf*>(axiom) )
			return pEM->Value ( avo->getAttribute(), avo->getValue() );
		if ( const TDLAxiomORoleDomain* ord = dynamic_cast<const TDLAxiomORoleDomain*>(axiom) )
			return pEM->Exists ( ord->getRole(), pEM->Top() );
		if ( const TDLAxiomORoleRange* orr = dynamic_cast<const TDLAxiomORoleRange*>(axiom) )
			return pEM->Exists ( orr->getRole(), pEM->Not(orr->getRange()) );
		if ( const TDLAxiomDRoleDomain* drd = dynamic_cast<const TDLAxiomDRoleDomain*>(axiom) )
			return pEM->Exists ( drd->getRole(), pEM->DataTop() );
		if ( const TDLAxiomDRoleRange* drr = dynamic_cast<const TDLAxiomDRoleRange*>(axiom) )
			return pEM->Exists ( drr->getRole(), pEM->DataNot(drr->getRange()) );
		if ( const TDLAxiomRelatedToNot* rtn = dynamic_cast<const TDLAxiomRelatedToNot*>(axiom) )
			return pEM->Not ( pEM->Value ( rtn->getRelation(), rtn->getRelatedIndividual() ) );
		if ( const TDLAxiomValueOfNot* von = dynamic_cast<const TDLAxiomValueOfNot*>(axiom) )
			return pEM->Not ( pEM->Value ( von->getAttribute(), von->getValue() ) );
		// everything else doesn't require expression to be build
		return nullptr;
	}

public:		// interface
		/// init c'tor
	explicit SemanticLocalityChecker ( const TSignature* sig ) : LocalityChecker(sig)
	{
		pEM = Kernel.getExpressionManager();
		// for tests we will need TB names to be from the OWL 2 namespace
		pEM->setTopBottomRoles(
			"http://www.w3.org/2002/07/owl#topObjectProperty",
			"http://www.w3.org/2002/07/owl#bottomObjectProperty",
			"http://www.w3.org/2002/07/owl#topDataProperty",
			"http://www.w3.org/2002/07/owl#bottomDataProperty");
	}

		/// init kernel with the ontology signature and init expression map
	void preprocessOntology ( const AxiomVec& Axioms ) override
	{
		TSignature s;
		ExprMap.clear();
		for ( TDLAxiom* axiom : Axioms )
		{
			ExprMap[axiom] = getExpr(axiom);
			s.add(axiom->getSignature());
		}

		Kernel.clearKB();
		// register all the objects in the ontology signature
		for (const TNamedEntity* entity : s)
			Kernel.declare(dynamic_cast<const TDLExpression*>(entity));
		// prepare the reasoner to check tautologies
		Kernel.realiseKB();
		// after TBox appears there, set signature to translate
		Kernel.setSignature(getSignature());
		// disallow usage of the expression cache as same expressions will lead to different translations
		Kernel.setIgnoreExprCache(true);
	}

public:		// visitor interface
	void visit ( const TDLAxiomDeclaration& ) override { isLocal = true; }

	void visit ( const TDLAxiomEquivalentConcepts& axiom ) override
	{
		isLocal = false;
		TDLAxiomEquivalentConcepts::iterator p = axiom.begin(), p_end = axiom.end();
		const TDLConceptExpression* C = *p;
		while (  ++p != p_end )
			if ( !Kernel.isEquivalent ( C, *p ) )
				return;
		isLocal = true;
	}
	void visit ( const TDLAxiomDisjointConcepts& axiom ) override
	{
		isLocal = false;
		for ( TDLAxiomDisjointConcepts::iterator p = axiom.begin(), q, p_end = axiom.end(); p != p_end; ++p )
			for ( q = p+1; q != p_end; ++q )
				if ( !Kernel.isDisjoint ( *p, *q ) )
					return;
		isLocal = true;
	}
	void visit ( const TDLAxiomDisjointUnion& axiom ) override
	{
		isLocal = false;
		// check A = (or C1... Cn)
		TDLAxiomDisjointUnion::iterator p, q, p_end = axiom.end();
		pEM->newArgList();
		for ( p = axiom.begin(); p != p_end; ++p )
			pEM->addArg(*p);
		if ( !Kernel.isEquivalent ( axiom.getC(), pEM->Or() ) )
			return;
		// check disjoint(C1... Cn)
		for ( p = axiom.begin(); p != p_end; ++p )
			for ( q = p+1; q != p_end; ++q )
				if ( !Kernel.isDisjoint ( *p, *q ) )
					return;
		isLocal = true;
	}
	void visit ( const TDLAxiomEquivalentORoles& axiom ) override
	{
		isLocal = false;
		TDLAxiomEquivalentORoles::iterator p = axiom.begin(), p_end = axiom.end();
		const TDLObjectRoleExpression* R = *p;
		while (  ++p != p_end )
			if ( !(Kernel.isSubRoles ( R, *p ) && Kernel.isSubRoles ( *p, R )) )
				return;
		isLocal = true;
	}
		// tautology if all the subsumptions Ri [= Rj holds
	void visit ( const TDLAxiomEquivalentDRoles& axiom ) override
	{
		isLocal = false;
		TDLAxiomEquivalentDRoles::iterator p = axiom.begin(), p_end = axiom.end();
		const TDLDataRoleExpression* R = *p;
		while (  ++p != p_end )
			if ( !(Kernel.isSubRoles ( R, *p ) && Kernel.isSubRoles ( *p, R )) )
				return;
		isLocal = true;
	}
	void visit ( const TDLAxiomDisjointORoles& axiom ) override
	{
		pEM->newArgList();
		for (const auto* arg : axiom)
			pEM->addArg(arg);
		isLocal = Kernel.isDisjointRoles();
	}
	void visit ( const TDLAxiomDisjointDRoles& axiom ) override
	{
		pEM->newArgList();
		for (const auto* arg : axiom)
			pEM->addArg(arg);
		isLocal = Kernel.isDisjointRoles();
	}
		// never local
	void visit ( const TDLAxiomSameIndividuals& ) override { isLocal = false; }
		// never local
	void visit ( const TDLAxiomDifferentIndividuals& ) override { isLocal = false; }
		/// there is no such axiom in OWL API, but I hope nobody would use Fairness here
	void visit ( const TDLAxiomFairnessConstraint& ) override { isLocal = true; }

		// R = inverse(S) is tautology iff R [= S- and S [= R-
	void visit ( const TDLAxiomRoleInverse& axiom ) override
	{
		isLocal = Kernel.isSubRoles ( axiom.getRole(), pEM->Inverse(axiom.getInvRole()) ) &&
				Kernel.isSubRoles ( axiom.getInvRole(), pEM->Inverse(axiom.getRole()) );
	}
	void visit ( const TDLAxiomORoleSubsumption& axiom ) override
	{
		// check whether the LHS is a role chain
		if ( const TDLObjectRoleChain* Chain = dynamic_cast<const TDLObjectRoleChain*>(axiom.getSubRole()) )
		{
			pEM->newArgList();
			for (const auto* arg : *Chain)
				pEM->addArg(arg);
			isLocal = Kernel.isSubChain(axiom.getRole());
		}
		// check whether the LHS is a plain role or inverse
		else if ( const TDLObjectRoleExpression* Sub = dynamic_cast<const TDLObjectRoleExpression*>(axiom.getSubRole()) )
			isLocal = Kernel.isSubRoles ( Sub, axiom.getRole() );
		else	// here we have a projection expression. FIXME!! for now
			isLocal = true;
	}
	void visit ( const TDLAxiomDRoleSubsumption& axiom ) override { isLocal = Kernel.isSubRoles ( axiom.getSubRole(), axiom.getRole() ); }
		// Domain(R) = C is tautology iff ER.Top [= C
	void visit ( const TDLAxiomORoleDomain& axiom ) override { isLocal = Kernel.isSubsumedBy ( ExprMap[&axiom], axiom.getDomain() ); }
	void visit ( const TDLAxiomDRoleDomain& axiom ) override { isLocal = Kernel.isSubsumedBy ( ExprMap[&axiom], axiom.getDomain() ); }
		// Range(R) = C is tautology iff ER.~C is unsatisfiable
	void visit ( const TDLAxiomORoleRange& axiom ) override { isLocal = !Kernel.isSatisfiable(ExprMap[&axiom]); }
	void visit ( const TDLAxiomDRoleRange& axiom ) override { isLocal = !Kernel.isSatisfiable(ExprMap[&axiom]); }
	void visit ( const TDLAxiomRoleTransitive& axiom ) override { isLocal = Kernel.isTransitive(axiom.getRole()); }
	void visit ( const TDLAxiomRoleReflexive& axiom ) override { isLocal = Kernel.isReflexive(axiom.getRole()); }
	void visit ( const TDLAxiomRoleIrreflexive& axiom ) override { isLocal = Kernel.isIrreflexive(axiom.getRole()); }
	void visit ( const TDLAxiomRoleSymmetric& axiom ) override { isLocal = Kernel.isSymmetric(axiom.getRole()); }
	void visit ( const TDLAxiomRoleAsymmetric& axiom ) override { isLocal = Kernel.isAsymmetric(axiom.getRole()); }
	void visit ( const TDLAxiomORoleFunctional& axiom ) override { isLocal = Kernel.isFunctional(axiom.getRole()); }
	void visit ( const TDLAxiomDRoleFunctional& axiom ) override { isLocal = Kernel.isFunctional(axiom.getRole()); }
	void visit ( const TDLAxiomRoleInverseFunctional& axiom ) override { isLocal = Kernel.isInverseFunctional(axiom.getRole()); }

	void visit ( const TDLAxiomConceptInclusion& axiom ) override { isLocal = Kernel.isSubsumedBy ( axiom.getSubC(), axiom.getSupC() ); }
		// for top locality, this might be local
	void visit ( const TDLAxiomInstanceOf& axiom ) override { isLocal = Kernel.isInstance ( axiom.getIndividual(), axiom.getC() ); }
		// R(i,j) holds if {i} [= \ER.{j}
	void visit ( const TDLAxiomRelatedTo& axiom ) override { isLocal = Kernel.isInstance ( axiom.getIndividual(), ExprMap[&axiom] ); }
		///!R(i,j) holds if {i} [= \AR.!{j}=!\ER.{j}
	void visit ( const TDLAxiomRelatedToNot& axiom ) override { isLocal = Kernel.isInstance ( axiom.getIndividual(), ExprMap[&axiom] ); }
		// R(i,v) holds if {i} [= \ER.{v}
	void visit ( const TDLAxiomValueOf& axiom ) override { isLocal = Kernel.isInstance ( axiom.getIndividual(), ExprMap[&axiom] ); }
		// !R(i,v) holds if {i} [= !\ER.{v}
	void visit ( const TDLAxiomValueOfNot& axiom ) override { isLocal = Kernel.isInstance ( axiom.getIndividual(), ExprMap[&axiom] ); }
}; // SemanticLocalityChecker

#endif
