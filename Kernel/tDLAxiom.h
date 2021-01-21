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

#ifndef TDLAXIOM_H
#define TDLAXIOM_H

#include "tDLExpression.h"

// forward declaration for all axiom classes: necessary for the visitor pattern
class TDLAxiomDeclaration;

class TDLAxiomEquivalentConcepts;
class TDLAxiomDisjointConcepts;
class TDLAxiomDisjointUnion;
class TDLAxiomEquivalentORoles;
class TDLAxiomEquivalentDRoles;
class TDLAxiomDisjointORoles;
class TDLAxiomDisjointDRoles;
class TDLAxiomSameIndividuals;
class TDLAxiomDifferentIndividuals;
class TDLAxiomFairnessConstraint;

class TDLAxiomRoleInverse;
class TDLAxiomORoleSubsumption;
class TDLAxiomDRoleSubsumption;
class TDLAxiomORoleDomain;
class TDLAxiomDRoleDomain;
class TDLAxiomORoleRange;
class TDLAxiomDRoleRange;
class TDLAxiomRoleTransitive;
class TDLAxiomRoleReflexive;
class TDLAxiomRoleIrreflexive;
class TDLAxiomRoleSymmetric;
class TDLAxiomRoleAsymmetric;
class TDLAxiomORoleFunctional;
class TDLAxiomDRoleFunctional;
class TDLAxiomRoleInverseFunctional;

class TDLAxiomConceptInclusion;
class TDLAxiomInstanceOf;
class TDLAxiomRelatedTo;
class TDLAxiomRelatedToNot;
class TDLAxiomValueOf;
class TDLAxiomValueOfNot;

/// general visitor for DL axioms
class DLAxiomVisitor
{
public:		// visitor interface
	virtual void visit ( const TDLAxiomDeclaration& axiom ) = 0;

	virtual void visit ( const TDLAxiomEquivalentConcepts& axiom ) = 0;
	virtual void visit ( const TDLAxiomDisjointConcepts& axiom ) = 0;
	virtual void visit ( const TDLAxiomDisjointUnion& axiom ) = 0;
	virtual void visit ( const TDLAxiomEquivalentORoles& axiom ) = 0;
	virtual void visit ( const TDLAxiomEquivalentDRoles& axiom ) = 0;
	virtual void visit ( const TDLAxiomDisjointORoles& axiom ) = 0;
	virtual void visit ( const TDLAxiomDisjointDRoles& axiom ) = 0;
	virtual void visit ( const TDLAxiomSameIndividuals& axiom ) = 0;
	virtual void visit ( const TDLAxiomDifferentIndividuals& axiom ) = 0;
	virtual void visit ( const TDLAxiomFairnessConstraint& axiom ) = 0;

	virtual void visit ( const TDLAxiomRoleInverse& axiom ) = 0;
	virtual void visit ( const TDLAxiomORoleSubsumption& axiom ) = 0;
	virtual void visit ( const TDLAxiomDRoleSubsumption& axiom ) = 0;
	virtual void visit ( const TDLAxiomORoleDomain& axiom ) = 0;
	virtual void visit ( const TDLAxiomDRoleDomain& axiom ) = 0;
	virtual void visit ( const TDLAxiomORoleRange& axiom ) = 0;
	virtual void visit ( const TDLAxiomDRoleRange& axiom ) = 0;
	virtual void visit ( const TDLAxiomRoleTransitive& axiom ) = 0;
	virtual void visit ( const TDLAxiomRoleReflexive& axiom ) = 0;
	virtual void visit ( const TDLAxiomRoleIrreflexive& axiom ) = 0;
	virtual void visit ( const TDLAxiomRoleSymmetric& axiom ) = 0;
	virtual void visit ( const TDLAxiomRoleAsymmetric& axiom ) = 0;
	virtual void visit ( const TDLAxiomORoleFunctional& axiom ) = 0;
	virtual void visit ( const TDLAxiomDRoleFunctional& axiom ) = 0;
	virtual void visit ( const TDLAxiomRoleInverseFunctional& axiom ) = 0;

	virtual void visit ( const TDLAxiomConceptInclusion& axiom ) = 0;
	virtual void visit ( const TDLAxiomInstanceOf& axiom ) = 0;
	virtual void visit ( const TDLAxiomRelatedTo& axiom ) = 0;
	virtual void visit ( const TDLAxiomRelatedToNot& axiom ) = 0;
	virtual void visit ( const TDLAxiomValueOf& axiom ) = 0;
	virtual void visit ( const TDLAxiomValueOfNot& axiom ) = 0;

	virtual ~DLAxiomVisitor() = default;
}; // DLAxiomVisitor

/// empty implementation of DL axioms visitor
class DLAxiomVisitorEmpty: public DLAxiomVisitor
{
public:		// visitor interface
	void visit ( const TDLAxiomDeclaration& ) override {}

	void visit ( const TDLAxiomEquivalentConcepts& ) override {}
	void visit ( const TDLAxiomDisjointConcepts& ) override {}
	void visit ( const TDLAxiomDisjointUnion& ) override {}
	void visit ( const TDLAxiomEquivalentORoles& ) override {}
	void visit ( const TDLAxiomEquivalentDRoles& ) override {}
	void visit ( const TDLAxiomDisjointORoles& ) override {}
	void visit ( const TDLAxiomDisjointDRoles& ) override {}
	void visit ( const TDLAxiomSameIndividuals& ) override {}
	void visit ( const TDLAxiomDifferentIndividuals& ) override {}
	void visit ( const TDLAxiomFairnessConstraint& ) override {}

	void visit ( const TDLAxiomRoleInverse& ) override {}
	void visit ( const TDLAxiomORoleSubsumption& ) override {}
	void visit ( const TDLAxiomDRoleSubsumption& ) override {}
	void visit ( const TDLAxiomORoleDomain& ) override {}
	void visit ( const TDLAxiomDRoleDomain& ) override {}
	void visit ( const TDLAxiomORoleRange& ) override {}
	void visit ( const TDLAxiomDRoleRange& ) override {}
	void visit ( const TDLAxiomRoleTransitive& ) override {}
	void visit ( const TDLAxiomRoleReflexive& ) override {}
	void visit ( const TDLAxiomRoleIrreflexive& ) override {}
	void visit ( const TDLAxiomRoleSymmetric& ) override {}
	void visit ( const TDLAxiomRoleAsymmetric& ) override {}
	void visit ( const TDLAxiomORoleFunctional& ) override {}
	void visit ( const TDLAxiomDRoleFunctional& ) override {}
	void visit ( const TDLAxiomRoleInverseFunctional& ) override {}

	void visit ( const TDLAxiomConceptInclusion& ) override {}
	void visit ( const TDLAxiomInstanceOf& ) override {}
	void visit ( const TDLAxiomRelatedTo& ) override {}
	void visit ( const TDLAxiomRelatedToNot& ) override {}
	void visit ( const TDLAxiomValueOf& ) override {}
	void visit ( const TDLAxiomValueOfNot& ) override {}
}; // DLAxiomVisitor

/// signature of an axiom
class TSignature;
/// atom that the axiom belongs to
class TOntologyAtom;

/// base class for the DL axiom, which include T-, A- and RBox ones
class TDLAxiom
{
protected:	// members
		/// id of the axiom
	unsigned int id = 0;
		/// signature (built lazily on demand)
	TSignature* sig = nullptr;
		/// atom of the ontology (build lazily on demand)
	const TOntologyAtom* Atom = nullptr;
		/// flag to show whether it is used (to support retraction)
	bool used = true;
		/// flag to show whether or not the axiom is in the module
	bool inModule = false;
		/// flag to show whether or not the axiom is in the search space for the optimised modularization algorithm
	bool inSearchSpace = false;

protected:	// methods
		/// build signature of an axiom
	void buildSignature ( void );

public:		// interface
		/// empty c'tor
	TDLAxiom() = default;
		/// d'tor: delete signature if it was created
	virtual ~TDLAxiom();

	// id management

		/// set the id
	void setId ( unsigned int Id ) { id = Id; }
		/// get the id
	unsigned int getId ( void ) const { return id; }

	// used management

		/// set the used flag
	void setUsed ( bool Used ) { used = Used; }
		/// get the value of the used flag
	bool isUsed ( void ) const { return used; }

	// modularity management

		/// set the inModule flag
	void setInModule ( bool flag ) { inModule = flag; }
		/// get the value of the inModule flag
	bool isInModule ( void ) const { return inModule; }
		/// set the isSearchSpace flag
	void setInSS ( bool flag ) { inSearchSpace = flag; }
		/// get the value of the isSearchSpace flag
	bool isInSS ( void ) const { return inSearchSpace; }

	// signature access

	const TSignature& getSignature ( void )
	{
		if ( sig == nullptr )	// 1st access: build it
			buildSignature();
		return *sig;
	}

	// ontological atomic structure management

		/// set atom to which the axiom belongs
	void setAtom ( const TOntologyAtom* atom ) { Atom = atom; }
		/// get the atom an axiom belongs to
	const TOntologyAtom* getAtom ( void ) const { return Atom; }

	// visitor interface

		/// accept method for the visitor pattern
	virtual void accept ( DLAxiomVisitor& visitor ) const = 0;
}; // TDLAxiom

//------------------------------------------------------------------
///	globally visible axiom vector
//------------------------------------------------------------------
typedef std::vector<TDLAxiom*> AxiomVec;

//------------------------------------------------------------------
///	general declaration axiom
//------------------------------------------------------------------
class TDLAxiomDeclaration: public TDLAxiom
{
protected:	// members
	const TDLExpression* D;

public:		// interface
		/// c'tor: create an axiom
	explicit TDLAxiomDeclaration ( const TDLExpression* d ) : TDLAxiom(), D(d) {}

		/// access
	const TDLExpression* getDeclaration ( void ) const { return D; }
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomIndividual

//------------------------------------------------------------------
//	n-ary axioms
//------------------------------------------------------------------

//------------------------------------------------------------------
///	Concept equivalence axiom
//------------------------------------------------------------------
class TDLAxiomEquivalentConcepts: public TDLAxiom, public TDLNAryExpression<TDLConceptExpression>
{
public:		// interface
		/// c'tor: create an axiom for C1 = ... = Cn
	explicit TDLAxiomEquivalentConcepts ( const ExpressionArray& v )
		: TDLAxiom()
		, TDLNAryExpression<TDLConceptExpression>("concept expression","equivalent concepts")
		{ add(v); }
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomEquivalentConcepts

//------------------------------------------------------------------
///	Concept disjointness axiom
//------------------------------------------------------------------
class TDLAxiomDisjointConcepts: public TDLAxiom, public TDLNAryExpression<TDLConceptExpression>
{
public:		// interface
		/// c'tor: create an axiom for C1 != ... != Cn
	explicit TDLAxiomDisjointConcepts ( const ExpressionArray& v )
		: TDLAxiom()
		, TDLNAryExpression<TDLConceptExpression>("concept expression","disjoint concepts")
		{ add(v); }
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomDisjointConcepts

//------------------------------------------------------------------
///	Disjoint Union axiom
//------------------------------------------------------------------
class TDLAxiomDisjointUnion: public TDLAxiom, public TDLNAryExpression<TDLConceptExpression>
{
protected:	// members
	const TDLConceptExpression* C;

public:		// interface
		/// c'tor: create an axiom
	TDLAxiomDisjointUnion ( const TDLConceptExpression* c, const ExpressionArray& v )
		: TDLAxiom()
		, TDLNAryExpression<TDLConceptExpression>("concept expression","disjoint union")
		, C(c)
		{ add(v); }
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }

		/// access
	const TDLConceptExpression* getC ( void ) const { return C; }
}; // TDLAxiomInstanceOf

//------------------------------------------------------------------
///	Object Role equivalence axiom
//------------------------------------------------------------------
class TDLAxiomEquivalentORoles: public TDLAxiom, public TDLNAryExpression<TDLObjectRoleExpression>
{
public:		// interface
		/// c'tor: create an axiom for OR1 = ... = ORn
	explicit TDLAxiomEquivalentORoles ( const ExpressionArray& v )
		: TDLAxiom()
		, TDLNAryExpression<TDLObjectRoleExpression>("object role expression","equivalent roles")
		{ add(v); }
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomEquivalentORoles

//------------------------------------------------------------------
///	Data Role equivalence axiom
//------------------------------------------------------------------
class TDLAxiomEquivalentDRoles: public TDLAxiom, public TDLNAryExpression<TDLDataRoleExpression>
{
public:		// interface
		/// c'tor: create an axiom for DR1 = ... = DRn
	explicit TDLAxiomEquivalentDRoles ( const ExpressionArray& v )
		: TDLAxiom()
		, TDLNAryExpression<TDLDataRoleExpression>("data role expression","equivalent roles")
		{ add(v); }
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomEquivalentDRoles

//------------------------------------------------------------------
///	Object Role disjointness axiom
//------------------------------------------------------------------
class TDLAxiomDisjointORoles: public TDLAxiom, public TDLNAryExpression<TDLObjectRoleExpression>
{
public:		// interface
		/// c'tor: create an axiom for OR1 != ... != ORn
	explicit TDLAxiomDisjointORoles ( const ExpressionArray& v )
		: TDLAxiom()
		, TDLNAryExpression<TDLObjectRoleExpression>("object role expression","disjoint roles")
		{ add(v); }
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomDisjointORoles

//------------------------------------------------------------------
///	Data Role disjointness axiom
//------------------------------------------------------------------
class TDLAxiomDisjointDRoles: public TDLAxiom, public TDLNAryExpression<TDLDataRoleExpression>
{
public:		// interface
		/// c'tor: create an axiom for DR1 != ... != DRn
	explicit TDLAxiomDisjointDRoles ( const ExpressionArray& v )
		: TDLAxiom()
		, TDLNAryExpression<TDLDataRoleExpression>("data role expression","disjoint roles")
		{ add(v); }
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomDisjointDRoles

//------------------------------------------------------------------
///	Same individuals axiom
//------------------------------------------------------------------
class TDLAxiomSameIndividuals: public TDLAxiom, public TDLNAryExpression<TDLIndividualExpression>
{
public:		// interface
		/// c'tor: create an axiom for i1 = ... = in
	explicit TDLAxiomSameIndividuals ( const ExpressionArray& v )
		: TDLAxiom()
		, TDLNAryExpression<TDLIndividualExpression>("individual expression","same individuals")
		{ add(v); }
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomSameIndividuals

//------------------------------------------------------------------
///	Different individuals axiom
//------------------------------------------------------------------
class TDLAxiomDifferentIndividuals: public TDLAxiom, public TDLNAryExpression<TDLIndividualExpression>
{
public:		// interface
		/// c'tor: create an axiom for i1 != ... != in
	explicit TDLAxiomDifferentIndividuals ( const ExpressionArray& v )
		: TDLAxiom()
		, TDLNAryExpression<TDLIndividualExpression>("individual expression","different individuals")
		{ add(v); }
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomDifferentIndividuals

//------------------------------------------------------------------
///	Fairness constraint axiom
//------------------------------------------------------------------
class TDLAxiomFairnessConstraint: public TDLAxiom, public TDLNAryExpression<TDLConceptExpression>
{
public:		// interface
		/// c'tor: create an axiom for Fair(C1), ... Fair(Cn) constraints
	explicit TDLAxiomFairnessConstraint ( const ExpressionArray& v )
		: TDLAxiom()
		, TDLNAryExpression<TDLConceptExpression>("concept expression","fairness")
		{ add(v); }
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomFairnessConstraint

//------------------------------------------------------------------
//	Role-related axiom
//------------------------------------------------------------------

//------------------------------------------------------------------
///	General axiom that contains a single object role
//------------------------------------------------------------------
class TDLAxiomSingleORole: public TDLAxiom
{
protected:	// members
	const TDLObjectRoleExpression* Role;

public:		// interface
		/// c'tor: create an axiom
	explicit TDLAxiomSingleORole ( const TDLObjectRoleExpression* role )
		: TDLAxiom()
		, Role(role)
		{}

		/// access to role
	const TDLObjectRoleExpression* getRole ( void ) const { return Role; }
}; // TDLAxiomSingleORole

//------------------------------------------------------------------
///	General axiom that contains a single data role
//------------------------------------------------------------------
class TDLAxiomSingleDRole: public TDLAxiom
{
protected:	// members
	const TDLDataRoleExpression* Role;

public:		// interface
		/// c'tor: create an axiom
	explicit TDLAxiomSingleDRole ( const TDLDataRoleExpression* role )
		: TDLAxiom()
		, Role(role)
		{}

		/// access to role
	const TDLDataRoleExpression* getRole ( void ) const { return Role; }
}; // TDLAxiomSingleDRole

//------------------------------------------------------------------
///	Role inverse axiom
//------------------------------------------------------------------
class TDLAxiomRoleInverse: public TDLAxiomSingleORole
{
protected:	// members
	const TDLObjectRoleExpression* InvRole;

public:		// interface
		/// c'tor: create an axiom
	TDLAxiomRoleInverse ( const TDLObjectRoleExpression* dirRole, const TDLObjectRoleExpression* invRole )
		: TDLAxiomSingleORole(dirRole)
		, InvRole(invRole)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }

		/// access to role
	const TDLObjectRoleExpression* getInvRole ( void ) const { return InvRole; }
}; // TDLAxiomRoleInverse

//------------------------------------------------------------------
///	Object Role subsumption axiom
//------------------------------------------------------------------
class TDLAxiomORoleSubsumption: public TDLAxiomSingleORole
{
protected:	// members
	const TDLObjectRoleComplexExpression* SubRole;

public:		// interface
		/// c'tor: create an axiom
	TDLAxiomORoleSubsumption ( const TDLObjectRoleComplexExpression* subRole, const TDLObjectRoleExpression* supRole )
		: TDLAxiomSingleORole(supRole)
		, SubRole(subRole)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }

		/// access to role
	const TDLObjectRoleComplexExpression* getSubRole ( void ) const { return SubRole; }
}; // TDLAxiomORoleSubsumption

//------------------------------------------------------------------
///	Data Role subsumption axiom
//------------------------------------------------------------------
class TDLAxiomDRoleSubsumption: public TDLAxiomSingleDRole
{
protected:	// members
	const TDLDataRoleExpression* SubRole;

public:		// interface
		/// c'tor: create an axiom
	TDLAxiomDRoleSubsumption ( const TDLDataRoleExpression* subRole, const TDLDataRoleExpression* supRole )
		: TDLAxiomSingleDRole(supRole)
		, SubRole(subRole)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }

		/// access to role
	const TDLDataRoleExpression* getSubRole ( void ) const { return SubRole; }
}; // TDLAxiomDRoleSubsumption

//------------------------------------------------------------------
///	Object Role domain axiom
//------------------------------------------------------------------
class TDLAxiomORoleDomain: public TDLAxiomSingleORole
{
protected:	// members
	const TDLConceptExpression* Domain;

public:		// interface
		/// c'tor: create an axiom
	TDLAxiomORoleDomain ( const TDLObjectRoleExpression* role, const TDLConceptExpression* domain )
		: TDLAxiomSingleORole(role)
		, Domain(domain)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }

		/// access
	const TDLConceptExpression* getDomain ( void ) const { return Domain; }
}; // TDLAxiomORoleDomain

//------------------------------------------------------------------
///	Data Role domain axiom
//------------------------------------------------------------------
class TDLAxiomDRoleDomain: public TDLAxiomSingleDRole
{
protected:	// members
	const TDLConceptExpression* Domain;

public:		// interface
		/// c'tor: create an axiom
	TDLAxiomDRoleDomain ( const TDLDataRoleExpression* role, const TDLConceptExpression* domain )
		: TDLAxiomSingleDRole(role)
		, Domain(domain)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }

		/// access
	const TDLConceptExpression* getDomain ( void ) const { return Domain; }
}; // TDLAxiomDRoleDomain

//------------------------------------------------------------------
///	Object Role range axiom
//------------------------------------------------------------------
class TDLAxiomORoleRange: public TDLAxiomSingleORole
{
protected:	// members
	const TDLConceptExpression* Range;

public:		// interface
		/// c'tor: create an axiom
	TDLAxiomORoleRange ( const TDLObjectRoleExpression* role, const TDLConceptExpression* range )
		: TDLAxiomSingleORole(role)
		, Range(range)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }

		/// access
	const TDLConceptExpression* getRange ( void ) const { return Range; }
}; // TDLAxiomORoleRange

//------------------------------------------------------------------
///	Data Role range axiom
//------------------------------------------------------------------
class TDLAxiomDRoleRange: public TDLAxiomSingleDRole
{
protected:	// members
	const TDLDataExpression* Range;

public:		// interface
		/// c'tor: create an axiom
	TDLAxiomDRoleRange ( const TDLDataRoleExpression* role, const TDLDataExpression* range )
		: TDLAxiomSingleDRole(role)
		, Range(range)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }

		/// access
	const TDLDataExpression* getRange ( void ) const { return Range; }
}; // TDLAxiomDRoleRange

//------------------------------------------------------------------
///	Role transitivity axiom
//------------------------------------------------------------------
class TDLAxiomRoleTransitive: public TDLAxiomSingleORole
{
public:		// interface
		/// c'tor: create an axiom
	explicit TDLAxiomRoleTransitive ( const TDLObjectRoleExpression* role )
		: TDLAxiomSingleORole(role)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomRoleTransitive

//------------------------------------------------------------------
///	Role reflexivity axiom
//------------------------------------------------------------------
class TDLAxiomRoleReflexive: public TDLAxiomSingleORole
{
public:		// interface
		/// c'tor: create an axiom
	explicit TDLAxiomRoleReflexive ( const TDLObjectRoleExpression* role )
		: TDLAxiomSingleORole(role)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomRoleReflexive

//------------------------------------------------------------------
///	Role irreflexivity axiom
//------------------------------------------------------------------
class TDLAxiomRoleIrreflexive: public TDLAxiomSingleORole
{
public:		// interface
		/// c'tor: create an axiom
	explicit TDLAxiomRoleIrreflexive ( const TDLObjectRoleExpression* role )
		: TDLAxiomSingleORole(role)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomRoleIrreflexive

//------------------------------------------------------------------
///	Role symmetry axiom
//------------------------------------------------------------------
class TDLAxiomRoleSymmetric: public TDLAxiomSingleORole
{
public:		// interface
		/// c'tor: create an axiom
	explicit TDLAxiomRoleSymmetric ( const TDLObjectRoleExpression* role )
		: TDLAxiomSingleORole(role)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomRoleSymmetric

//------------------------------------------------------------------
///	Role asymmetry axiom
//------------------------------------------------------------------
class TDLAxiomRoleAsymmetric: public TDLAxiomSingleORole
{
public:		// interface
		/// c'tor: create an axiom
	explicit TDLAxiomRoleAsymmetric ( const TDLObjectRoleExpression* role )
		: TDLAxiomSingleORole(role)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomRoleAsymmetric

//------------------------------------------------------------------
///	Object Role functionality axiom
//------------------------------------------------------------------
class TDLAxiomORoleFunctional: public TDLAxiomSingleORole
{
public:		// interface
		/// c'tor: create an axiom
	explicit TDLAxiomORoleFunctional ( const TDLObjectRoleExpression* role )
		: TDLAxiomSingleORole(role)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomORoleFunctional

//------------------------------------------------------------------
///	Data Role functionality axiom
//------------------------------------------------------------------
class TDLAxiomDRoleFunctional: public TDLAxiomSingleDRole
{
public:		// interface
		/// c'tor: create an axiom
	explicit TDLAxiomDRoleFunctional ( const TDLDataRoleExpression* role )
		: TDLAxiomSingleDRole(role)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomDRoleFunctional

//------------------------------------------------------------------
///	Role inverse functionality axiom
//------------------------------------------------------------------
class TDLAxiomRoleInverseFunctional: public TDLAxiomSingleORole
{
public:		// interface
		/// c'tor: create an axiom
	explicit TDLAxiomRoleInverseFunctional ( const TDLObjectRoleExpression* role )
		: TDLAxiomSingleORole(role)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }
}; // TDLAxiomRoleInverseFunctional


//------------------------------------------------------------------
//	Concept/individual axioms
//------------------------------------------------------------------

//------------------------------------------------------------------
///	Concept inclusion axiom
//------------------------------------------------------------------
class TDLAxiomConceptInclusion: public TDLAxiom
{
protected:	// members
	const TDLConceptExpression* Sub;
	const TDLConceptExpression* Sup;

public:		// interface
		/// c'tor: create an axiom
	TDLAxiomConceptInclusion ( const TDLConceptExpression* sub, const TDLConceptExpression* sup ) : TDLAxiom(), Sub(sub), Sup(sup) {}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }

		/// access
	const TDLConceptExpression* getSubC ( void ) const { return Sub; }
		/// access
	const TDLConceptExpression* getSupC ( void ) const { return Sup; }
}; // TDLAxiomConceptInclusion

//------------------------------------------------------------------
///	general individual-based axiom
//------------------------------------------------------------------
class TDLAxiomIndividual: public TDLAxiom
{
protected:	// members
	const TDLIndividualExpression* I;

public:		// interface
		/// c'tor: create an axiom
	explicit TDLAxiomIndividual ( const TDLIndividualExpression* i ) : TDLAxiom(), I(i) {}

		/// access
	const TDLIndividualExpression* getIndividual ( void ) const { return I; }
}; // TDLAxiomIndividual

//------------------------------------------------------------------
///	Instance axiom
//------------------------------------------------------------------
class TDLAxiomInstanceOf: public TDLAxiomIndividual
{
protected:	// members
	const TDLConceptExpression* C;

public:		// interface
		/// c'tor: create an axiom
	TDLAxiomInstanceOf ( const TDLIndividualExpression* i, const TDLConceptExpression* c ) : TDLAxiomIndividual(i), C(c) {}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }

		/// access
	const TDLConceptExpression* getC ( void ) const { return C; }
}; // TDLAxiomInstanceOf

//------------------------------------------------------------------
///	Related To axiom
//------------------------------------------------------------------
class TDLAxiomRelatedTo: public TDLAxiomIndividual
{
protected:	// members
	const TDLObjectRoleExpression* R;
	const TDLIndividualExpression* J;

public:		// interface
		/// c'tor: create an axiom
	TDLAxiomRelatedTo ( const TDLIndividualExpression* i, const TDLObjectRoleExpression* r, const TDLIndividualExpression* j )
		: TDLAxiomIndividual(i)
		, R(r)
		, J(j)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }

		/// access
	const TDLObjectRoleExpression* getRelation ( void ) const { return R; }
		/// access
	const TDLIndividualExpression* getRelatedIndividual ( void ) const { return J; }
}; // TDLAxiomRelatedTo

//------------------------------------------------------------------
///	Related To Not axiom
//------------------------------------------------------------------
class TDLAxiomRelatedToNot: public TDLAxiomIndividual
{
protected:	// members
	const TDLObjectRoleExpression* R;
	const TDLIndividualExpression* J;

public:		// interface
		/// c'tor: create an axiom
	TDLAxiomRelatedToNot ( const TDLIndividualExpression* i, const TDLObjectRoleExpression* r, const TDLIndividualExpression* j )
		: TDLAxiomIndividual(i)
		, R(r)
		, J(j)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }

		/// access
	const TDLObjectRoleExpression* getRelation ( void ) const { return R; }
		/// access
	const TDLIndividualExpression* getRelatedIndividual ( void ) const { return J; }
}; // TDLAxiomRelatedToNot

//------------------------------------------------------------------
///	Value Of axiom
//------------------------------------------------------------------
class TDLAxiomValueOf: public TDLAxiomIndividual
{
protected:	// members
	const TDLDataRoleExpression* A;
	const TDLDataValue* V;

public:		// interface
		/// c'tor: create an axiom
	TDLAxiomValueOf ( const TDLIndividualExpression* i, const TDLDataRoleExpression* a, const TDLDataValue* v )
		: TDLAxiomIndividual(i)
		, A(a)
		, V(v)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }

		/// access to role
	const TDLDataRoleExpression* getAttribute ( void ) const { return A; }
		/// access to value
	const TDLDataValue* getValue ( void ) const { return V; }
}; // TDLAxiomValueOf

//------------------------------------------------------------------
///	Related To Not axiom
//------------------------------------------------------------------
class TDLAxiomValueOfNot: public TDLAxiomIndividual
{
protected:	// members
	const TDLDataRoleExpression* A;
	const TDLDataValue* V;

public:		// interface
		/// c'tor: create an axiom
	TDLAxiomValueOfNot ( const TDLIndividualExpression* i, const TDLDataRoleExpression* a, const TDLDataValue* v )
		: TDLAxiomIndividual(i)
		, A(a)
		, V(v)
		{}
		/// accept method for the visitor pattern
	void accept ( DLAxiomVisitor& visitor ) const override { visitor.visit(*this); }

		/// access to role
	const TDLDataRoleExpression* getAttribute ( void ) const { return A; }
		/// access to value
	const TDLDataValue* getValue ( void ) const { return V; }
}; // TDLAxiomValueOfNot


#endif
