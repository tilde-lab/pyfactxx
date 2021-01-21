/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2011-2015 Dmitry Tsarkov and The University of Manchester
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

// inform interface that we are building library here
#define FPP_BUILD_SHARED

#include "fact.h"
#include "Kernel.h"
#include "Actor.h"

/// class for acting with a taxonomy at a C level
class CActor: public Actor
{
protected:	// methods
		/// build the NULL-terminated array of names of entries
	const char** buildArray ( const Array1D& vec ) const
	{
		const char** ret = new const char*[vec.size()+1];
		for ( size_t i = 0; i < vec.size(); ++i )
			ret[i] = vec[i]->getName();
		ret[vec.size()] = nullptr;
		return ret;
	}

public:		// interface
		/// empty c'tor
	CActor() = default;

	// return values
		/// get 1-d NULL-terminated array of synonyms of the 1st entry(necessary for Equivalents, for example)
	const char** getSynonyms ( void ) const
	{
		if ( found.empty() )
			return buildArray(Array1D());
		Array2D acc;
		getFoundData(acc);
		return buildArray(acc[0]);
	}
		/// get NULL-terminated 2D array of all required elements of the taxonomy
	const char*** getElements2D ( void ) const
	{
		Array2D acc;
		getFoundData(acc);
		const char*** ret = new const char**[acc.size()+1];
		for ( size_t i = 0; i < acc.size(); ++i )
			ret[i] = buildArray(acc[i]);
		ret[acc.size()] = nullptr;
		return ret;
	}
		/// get NULL-terminated 1D array of all required elements of the taxonomy
	const char** getElements1D ( void ) const
	{
		Array1D vec;
		getFoundData(vec);
		return buildArray(vec);
	}
}; // CActor

// type declarations

#define DECLARE_STRUCT(name,type)	\
struct name ## _st { type* p; explicit name ## _st(type* q) : p(q) {} }
// FaCT++ kernel
DECLARE_STRUCT(fact_reasoning_kernel,ReasoningKernel);
// progress monitor
DECLARE_STRUCT(fact_progress_monitor,TProgressMonitor);
// axiom
DECLARE_STRUCT(fact_axiom,TDLAxiom);
// expression
DECLARE_STRUCT(fact_expression,ReasoningKernel::TExpr);
// concept expression
DECLARE_STRUCT(fact_concept_expression,ReasoningKernel::TConceptExpr);
// data- or object-role expression
DECLARE_STRUCT(fact_role_expression,ReasoningKernel::TRoleExpr);
// object role expression
DECLARE_STRUCT(fact_o_role_expression,ReasoningKernel::TORoleExpr);
// complex object role expression
DECLARE_STRUCT(fact_o_role_complex_expression,ReasoningKernel::TORoleComplexExpr);
// data role expression
DECLARE_STRUCT(fact_d_role_expression,ReasoningKernel::TDRoleExpr);
// individual expression
DECLARE_STRUCT(fact_individual_expression,ReasoningKernel::TIndividualExpr);
// general data expression
DECLARE_STRUCT(fact_data_expression,ReasoningKernel::TDataExpr);
// data type expression
DECLARE_STRUCT(fact_data_type_expression,ReasoningKernel::TDataTypeExpr);
// data value expression
DECLARE_STRUCT(fact_data_value_expression,ReasoningKernel::TDataValueExpr);
// facet expression
DECLARE_STRUCT(fact_facet_expression,ReasoningKernel::TFacetExpr);
// actor to traverse taxonomy
DECLARE_STRUCT(fact_actor,CActor);

const char *fact_get_version ()
{
	return ReasoningKernel::getVersion();
}

fact_reasoning_kernel *fact_reasoning_kernel_new ()
{
	fact_reasoning_kernel *ret = new fact_reasoning_kernel_st(new ReasoningKernel());
	/* need this to work properly with top/bot roles */
	ret->p->setTopBottomRoleNames("*UROLE*","*EROLE*","*UDROLE*","*EDROLE*");
	return ret;
}
void fact_reasoning_kernel_free (fact_reasoning_kernel *k)
{
	delete k->p;
	delete k;
}

/*
ifOptionSet* getOptions (  );
const ifOptionSet* getOptions (  );
 */

int fact_is_kb_preprocessed (fact_reasoning_kernel *k)
{
	return k->p->isKBPreprocessed();
}
int fact_is_kb_classified (fact_reasoning_kernel *k)
{
	return k->p->isKBRealised();
}
int fact_is_kb_realised (fact_reasoning_kernel *k)
{
	return k->p->isKBRealised();
}
void fact_set_progress_monitor (fact_reasoning_kernel *k, fact_progress_monitor *m)
{
	return k->p->setProgressMonitor(m->p);
}

void fact_set_verbose_output (fact_reasoning_kernel *k, int value)
{
	k->p->setVerboseOutput(value);
}

void fact_set_top_bottom_role_names (fact_reasoning_kernel *k,
		const char *top_o_role_name,
		const char *bot_o_role_name,
		const char *top_d_role_name,
		const char *bot_d_role_name)
{
	k->p->setTopBottomRoleNames(top_o_role_name,bot_o_role_name,top_d_role_name,bot_d_role_name);
}

void fact_set_operation_timeout (fact_reasoning_kernel *k,
		unsigned long timeout)
{
	k->p->setOperationTimeout(timeout);
}

int fact_new_kb (fact_reasoning_kernel *k)
{
	return k->p->newKB();
}
int fact_release_kb (fact_reasoning_kernel *k)
{
	return k->p->releaseKB();
}
int fact_clear_kb (fact_reasoning_kernel *k)
{
	return k->p->clearKB();
}

fact_axiom *fact_declare (fact_reasoning_kernel *k, fact_expression *c)
{
	return new fact_axiom_st(k->p->declare(c->p));
}
fact_axiom *fact_implies_concepts (fact_reasoning_kernel *k,
		fact_concept_expression *c,
		fact_concept_expression *d)
{
	return new fact_axiom_st(k->p->impliesConcepts(c->p,d->p));
}
fact_axiom *fact_equal_concepts (fact_reasoning_kernel *k)
{
	return new fact_axiom_st(k->p->equalConcepts());
}
fact_axiom *fact_disjoint_concepts (fact_reasoning_kernel *k)
{
	return new fact_axiom_st(k->p->disjointConcepts());
}
fact_axiom *fact_disjoint_union (fact_reasoning_kernel *k,
		fact_concept_expression *C)
{
	return new fact_axiom_st(k->p->disjointUnion(C->p));
}


fact_axiom *fact_set_inverse_roles (fact_reasoning_kernel *k,
		fact_o_role_expression *r,
		fact_o_role_expression *s)
{
	return new fact_axiom_st(k->p->setInverseRoles(r->p,s->p));
}
fact_axiom *fact_implies_o_roles (fact_reasoning_kernel *k,
		fact_o_role_complex_expression *r,
		fact_o_role_expression *s)
{
	return new fact_axiom_st(k->p->impliesORoles(r->p,s->p));
}
fact_axiom *fact_implies_d_roles (fact_reasoning_kernel *k,
		fact_d_role_expression *r,
		fact_d_role_expression *s)
{
	return new fact_axiom_st(k->p->impliesDRoles(r->p,s->p));
}
fact_axiom *fact_equal_o_roles (fact_reasoning_kernel *k)
{
	return new fact_axiom_st(k->p->equalORoles());
}
fact_axiom *fact_equal_d_roles (fact_reasoning_kernel *k)
{
	return new fact_axiom_st(k->p->equalDRoles());
}
fact_axiom *fact_disjoint_o_roles (fact_reasoning_kernel *k)
{
	return new fact_axiom_st(k->p->disjointORoles());
}
fact_axiom *fact_disjoint_d_roles (fact_reasoning_kernel *k)
{
	return new fact_axiom_st(k->p->disjointDRoles());
}

fact_axiom* fact_set_o_domain (fact_reasoning_kernel *k,
		fact_o_role_expression *r,
		fact_concept_expression *c)
{
	return new fact_axiom_st(k->p->setODomain(r->p,c->p));
}
fact_axiom *fact_set_d_domain (fact_reasoning_kernel *k,
		fact_d_role_expression *r,
		fact_concept_expression *c)
{
	return new fact_axiom_st(k->p->setDDomain(r->p,c->p));
}
fact_axiom *fact_set_o_range (fact_reasoning_kernel *k,
		fact_o_role_expression *r,
		fact_concept_expression *c)
{
	return new fact_axiom_st(k->p->setORange(r->p,c->p));
}
fact_axiom *fact_set_d_range (fact_reasoning_kernel *k,
		fact_d_role_expression *r,
		fact_data_expression *e)
{
	return new fact_axiom_st(k->p->setDRange(r->p,e->p));
}

fact_axiom *fact_set_transitive (fact_reasoning_kernel *k,
		fact_o_role_expression *r)
{
	return new fact_axiom_st(k->p->setTransitive(r->p));
}
fact_axiom *fact_set_reflexive (fact_reasoning_kernel *k,
		fact_o_role_expression *r)
{
	return new fact_axiom_st(k->p->setReflexive(r->p));
}
fact_axiom *fact_set_irreflexive (fact_reasoning_kernel *k,
		fact_o_role_expression *r)
{
	return new fact_axiom_st(k->p->setIrreflexive(r->p));
}
fact_axiom *fact_set_symmetric (fact_reasoning_kernel *k,
		fact_o_role_expression *r)
{
	return new fact_axiom_st(k->p->setSymmetric(r->p));
}
fact_axiom *fact_set_asymmetric (fact_reasoning_kernel *k,
		fact_o_role_expression *r)
{
	return new fact_axiom_st(k->p->setAsymmetric(r->p));
}
fact_axiom *fact_set_o_functional (fact_reasoning_kernel *k,
		fact_o_role_expression *r)
{
	return new fact_axiom_st(k->p->setOFunctional(r->p));
}
fact_axiom *fact_set_d_functional (fact_reasoning_kernel *k,
		fact_d_role_expression *r)
{
	return new fact_axiom_st(k->p->setDFunctional(r->p));
}
fact_axiom *fact_set_inverse_functional (fact_reasoning_kernel *k,
		fact_o_role_expression *r)
{
	return new fact_axiom_st(k->p->setInverseFunctional(r->p));
}

fact_axiom *fact_instance_of (fact_reasoning_kernel *k,
		fact_individual_expression *i,
		fact_concept_expression *c)
{
	return new fact_axiom_st(k->p->instanceOf(i->p,c->p));
}
fact_axiom *fact_related_to (fact_reasoning_kernel *k,
		fact_individual_expression *i,
		fact_o_role_expression *r,
		fact_individual_expression *j)
{
	return new fact_axiom_st(k->p->relatedTo(i->p,r->p,j->p));
}
fact_axiom *fact_related_to_not (fact_reasoning_kernel *k,
		fact_individual_expression *i,
		fact_o_role_expression *r,
		fact_individual_expression *j)
{
	return new fact_axiom_st(k->p->relatedToNot(i->p,r->p,j->p));
}
fact_axiom *fact_value_of (fact_reasoning_kernel *k,
		fact_individual_expression *i,
		fact_d_role_expression *a,
		fact_data_value_expression *v)
{
	return new fact_axiom_st(k->p->valueOf(i->p,a->p,v->p));
}
fact_axiom *fact_value_of_not (fact_reasoning_kernel *k,
		fact_individual_expression *i,
		fact_d_role_expression *a,
		fact_data_value_expression *v)
{
	return new fact_axiom_st(k->p->valueOfNot(i->p,a->p,v->p));
}
fact_axiom *fact_process_same (fact_reasoning_kernel *k)
{
	return new fact_axiom_st(k->p->processSame());
}
fact_axiom *fact_process_different (fact_reasoning_kernel *k)
{
	return new fact_axiom_st(k->p->processDifferent());
}
fact_axiom *fact_set_fairness_constraint (fact_reasoning_kernel *k)
{
	return new fact_axiom_st(k->p->setFairnessConstraint());
}

void fact_retract (fact_reasoning_kernel *k, fact_axiom *axiom)
{
	k->p->retract(axiom->p);
}

int fact_is_kb_consistent (fact_reasoning_kernel *k)
{
	return k->p->isKBConsistent();
}
void fact_preprocess_kb (fact_reasoning_kernel *k)
{
	k->p->preprocessKB();
}
void fact_classify_kb (fact_reasoning_kernel *k)
{
	k->p->classifyKB();
}
void fact_realise_kb (fact_reasoning_kernel *k)
{
	k->p->realiseKB();
}

int fact_is_o_functional (fact_reasoning_kernel *k, fact_o_role_expression *r)
{
	return k->p->isFunctional(r->p);
}
int fact_is_d_functional (fact_reasoning_kernel *k,
		fact_d_role_expression *r)
{
	return k->p->isFunctional(r->p);
}
int fact_is_inverse_functional (fact_reasoning_kernel *k,
		fact_o_role_expression *r)
{
	return k->p->isInverseFunctional(r->p);
}
int fact_is_transitive (fact_reasoning_kernel *k, fact_o_role_expression *r)
{
	return k->p->isTransitive(r->p);
}
int fact_is_symmetric (fact_reasoning_kernel *k, fact_o_role_expression *r)
{
	return k->p->isSymmetric(r->p);
}
int fact_is_asymmetric (fact_reasoning_kernel *k, fact_o_role_expression *r)
{
	return k->p->isAsymmetric(r->p);
}
int fact_is_reflexive (fact_reasoning_kernel *k, fact_o_role_expression *r)
{
	return k->p->isReflexive(r->p);
}
int fact_is_irreflexive (fact_reasoning_kernel *k, fact_o_role_expression *r)
{
	return k->p->isIrreflexive(r->p);
}
int fact_is_sub_o_roles (fact_reasoning_kernel *k, fact_o_role_expression *r,
		fact_o_role_expression *s)
{
	return k->p->isSubRoles(r->p,s->p);
}
int fact_is_sub_d_roles (fact_reasoning_kernel *k, fact_d_role_expression *r,
		fact_d_role_expression *s)
{
	return k->p->isSubRoles(r->p,s->p);
}
int fact_is_disjoint_o_roles (fact_reasoning_kernel *k,
		fact_o_role_expression *r,
		fact_o_role_expression *s)
{
	return k->p->isDisjointRoles(r->p,s->p);
}
int fact_is_disjoint_d_roles (fact_reasoning_kernel *k,
		fact_d_role_expression *r,
		fact_d_role_expression *s)
{
	return k->p->isDisjointRoles(r->p,s->p);
}
int fact_is_disjoint_roles (fact_reasoning_kernel *k)
{
	return k->p->isDisjointRoles();
}
int fact_is_sub_chain (fact_reasoning_kernel *k, fact_o_role_expression *r)
{
	return k->p->isSubChain(r->p);
}

int fact_is_satisfiable (fact_reasoning_kernel *k, fact_concept_expression *c)
{
	return k->p->isSatisfiable(c->p);
}
int fact_is_subsumed_by (fact_reasoning_kernel *k, fact_concept_expression *c,
		fact_concept_expression *d)
{
	return k->p->isSubsumedBy(c->p,d->p);
}
int fact_is_disjoint (fact_reasoning_kernel *k, fact_concept_expression *c,
		fact_concept_expression *d)
{
	return k->p->isDisjoint(c->p,d->p);
}
int fact_is_equivalent (fact_reasoning_kernel *k, fact_concept_expression *c,
		fact_concept_expression *d)
{
	return k->p->isEquivalent(c->p,d->p);
}

void fact_get_sup_concepts (fact_reasoning_kernel *k, fact_concept_expression *c,
		int direct, fact_actor **actor)
{
	k->p->getSupConcepts(c->p,direct,*(*actor)->p);
}
void fact_get_sub_concepts (fact_reasoning_kernel *k, fact_concept_expression *c,
		int direct, fact_actor **actor)
{
	k->p->getSubConcepts(c->p,direct,*(*actor)->p);
}
void fact_get_equivalent_concepts (fact_reasoning_kernel *k,
		fact_concept_expression *c,
		fact_actor **actor)
{
	k->p->getEquivalentConcepts(c->p,*(*actor)->p);
}
void fact_get_disjoint_concepts (fact_reasoning_kernel *k,
		fact_concept_expression *c,
		fact_actor **actor)
{
	k->p->getDisjointConcepts(c->p,*(*actor)->p);
}

void fact_get_sup_roles (fact_reasoning_kernel *k, fact_role_expression *r,
		int direct,
		fact_actor **actor)
{
	k->p->getSupRoles(r->p,direct,*(*actor)->p);
}
void fact_get_sub_roles (fact_reasoning_kernel *k, fact_role_expression *r,
		int direct, fact_actor **actor)
{
	k->p->getSubRoles(r->p,direct,*(*actor)->p);
}
void fact_get_equivalent_roles (fact_reasoning_kernel *k, fact_role_expression *r,
		fact_actor **actor)
{
	k->p->getEquivalentRoles(r->p,*(*actor)->p);
}
void fact_get_o_role_domain (fact_reasoning_kernel *k, fact_o_role_expression *r,
		int direct, fact_actor **actor)
{
	k->p->getORoleDomain(r->p,direct,*(*actor)->p);
}
void fact_get_d_role_domain (fact_reasoning_kernel *k, fact_d_role_expression *r,
		int direct, fact_actor **actor)
{
	k->p->getDRoleDomain(r->p,direct,*(*actor)->p);
}
void fact_get_role_range (fact_reasoning_kernel *k, fact_o_role_expression *r,
		int direct, fact_actor **actor)
{
	k->p->getRoleRange(r->p,direct,*(*actor)->p);
}
void fact_get_direct_instances (fact_reasoning_kernel *k,
		fact_concept_expression *c, fact_actor **actor)
{
	k->p->getDirectInstances(c->p,*(*actor)->p);
}
void fact_get_instances (fact_reasoning_kernel *k, fact_concept_expression *c,
		fact_actor **actor)
{
	k->p->getInstances(c->p,*(*actor)->p);
}
void fact_get_types (fact_reasoning_kernel *k, fact_individual_expression *i,
		int direct, fact_actor **actor)
{
	k->p->getTypes(i->p,direct,*(*actor)->p);
}
void fact_get_same_as (fact_reasoning_kernel *k,
		fact_individual_expression *i, fact_actor **actor)
{
	k->p->getSameAs(i->p,*(*actor)->p);
}

int fact_is_same_individuals (fact_reasoning_kernel *k,
		fact_individual_expression *i,
		fact_individual_expression *j)
{
	return k->p->isSameIndividuals(i->p,j->p);
}
int fact_is_instance (fact_reasoning_kernel *k,
		fact_individual_expression *i,
		fact_concept_expression *c)
{
	return k->p->isInstance(i->p,c->p);
}
int fact_is_related (fact_reasoning_kernel *k,
		fact_individual_expression *i,
		fact_o_role_expression *r,
		fact_individual_expression *j)
{
	return k->p->isRelated(i->p,r->p,j->p);
}
fact_actor* fact_concept_actor_new()
{
	fact_actor* ret = new fact_actor(new CActor());
	ret->p->needConcepts();
	return ret;
}
fact_actor* fact_individual_actor_new()
{
	fact_actor* ret = new fact_actor(new CActor());
	ret->p->needIndividuals();
	return ret;
}
fact_actor* fact_o_role_actor_new()
{
	fact_actor* ret = new fact_actor(new CActor());
	ret->p->needObjectRoles();
	return ret;
}
fact_actor* fact_d_role_actor_new()
{
	fact_actor* ret = new fact_actor(new CActor());
	ret->p->needDataRoles();
	return ret;
}
void fact_actor_free ( fact_actor* actor )
{
	delete actor->p;
	delete actor;
}
/// get 1-d NULL-terminated array of synonyms of the 1st entry(necessary for Equivalents, for example)
const char** fact_get_synonyms ( fact_actor* actor )
{
	return actor->p->getSynonyms();
}
/// get NULL-terminated 2D array of all required elements of the taxonomy
const char*** fact_get_elements_2d ( fact_actor* actor )
{
	return actor->p->getElements2D();
}
/// get NULL-terminated 1D array of all required elements of the taxonomy
const char** fact_get_elements_1d ( fact_actor* actor )
{
	return actor->p->getElements1D();
}

/// opens new argument list
void fact_new_arg_list ( fact_reasoning_kernel *k )
{
	k->p->getExpressionManager()->newArgList();
}
/// add argument E to the current argument list
void fact_add_arg ( fact_reasoning_kernel *k,fact_expression* e )
{
	k->p->getExpressionManager()->addArg(e->p);
}

// create expressions methods

// concepts

/// get TOP concept
fact_concept_expression* fact_top ( fact_reasoning_kernel *k )
{
	return new fact_concept_expression(k->p->getExpressionManager()->Top());
}
/// get BOTTOM concept
fact_concept_expression* fact_bottom ( fact_reasoning_kernel *k )
{
	return new fact_concept_expression(k->p->getExpressionManager()->Bottom());
}
/// get named concept
fact_concept_expression* fact_concept ( fact_reasoning_kernel *k,const char* name )
{
	return new fact_concept_expression(k->p->getExpressionManager()->Concept(name));
}
/// get negation of a concept c
fact_concept_expression* fact_not ( fact_reasoning_kernel *k,fact_concept_expression* c )
{
	return new fact_concept_expression(k->p->getExpressionManager()->Not(c->p));
}
/// get an n-ary conjunction expression; take the arguments from the last argument list
fact_concept_expression* fact_and ( fact_reasoning_kernel *k )
{
	return new fact_concept_expression(k->p->getExpressionManager()->And());
}
/// get an n-ary disjunction expression; take the arguments from the last argument list
fact_concept_expression* fact_or ( fact_reasoning_kernel *k )
{
	return new fact_concept_expression(k->p->getExpressionManager()->Or());
}
/// get an n-ary one-of expression; take the arguments from the last argument list
fact_concept_expression* fact_one_of ( fact_reasoning_kernel *k )
{
	return new fact_concept_expression(k->p->getExpressionManager()->OneOf());
}

/// get self-reference restriction of an object role r
fact_concept_expression* fact_self_reference ( fact_reasoning_kernel *k,fact_o_role_expression* r )
{
	return new fact_concept_expression(k->p->getExpressionManager()->SelfReference(r->p));
}
/// get value restriction wrt an object role r and an individual i
fact_concept_expression* fact_o_value ( fact_reasoning_kernel *k,fact_o_role_expression* r, fact_individual_expression* i )
{
	return new fact_concept_expression(k->p->getExpressionManager()->Value(r->p,i->p));
}
/// get existential restriction wrt an object role r and a concept c
fact_concept_expression* fact_o_exists ( fact_reasoning_kernel *k,fact_o_role_expression* r, fact_concept_expression* c )
{
	return new fact_concept_expression(k->p->getExpressionManager()->Exists(r->p,c->p));
}
/// get universal restriction wrt an object role r and a concept c
fact_concept_expression* fact_o_forall ( fact_reasoning_kernel *k,fact_o_role_expression* r, fact_concept_expression* c )
{
	return new fact_concept_expression(k->p->getExpressionManager()->Forall(r->p,c->p));
}
/// get min cardinality restriction wrt number _n, an object role r and a concept c
fact_concept_expression* fact_o_min_cardinality ( fact_reasoning_kernel *k,unsigned int n, fact_o_role_expression* r, fact_concept_expression* c )
{
	return new fact_concept_expression(k->p->getExpressionManager()->MinCardinality(n,r->p,c->p));
}
/// get max cardinality restriction wrt number _n, an object role r and a concept c
fact_concept_expression* fact_o_max_cardinality ( fact_reasoning_kernel *k,unsigned int n, fact_o_role_expression* r, fact_concept_expression* c )
{
	return new fact_concept_expression(k->p->getExpressionManager()->MaxCardinality(n,r->p,c->p));
}
/// get exact cardinality restriction wrt number _n, an object role r and a concept c
fact_concept_expression* fact_o_cardinality ( fact_reasoning_kernel *k,unsigned int n, fact_o_role_expression* r, fact_concept_expression* c )
{
	return new fact_concept_expression(k->p->getExpressionManager()->Cardinality(n,r->p,c->p));
}

/// get value restriction wrt a data role r and a data value v
fact_concept_expression* fact_d_value ( fact_reasoning_kernel *k,fact_d_role_expression* r, fact_data_value_expression* v )
{
	return new fact_concept_expression(k->p->getExpressionManager()->Value(r->p,v->p));
}
/// get existential restriction wrt a data role r and a data expression e
fact_concept_expression* fact_d_exists ( fact_reasoning_kernel *k,fact_d_role_expression* r, fact_data_expression* e )
{
	return new fact_concept_expression(k->p->getExpressionManager()->Exists(r->p,e->p));
}
/// get universal restriction wrt a data role r and a data expression e
fact_concept_expression* fact_d_forall ( fact_reasoning_kernel *k,fact_d_role_expression* r, fact_data_expression* e )
{
	return new fact_concept_expression(k->p->getExpressionManager()->Forall(r->p,e->p));
}
/// get min cardinality restriction wrt number _n, a data role r and a data expression e
fact_concept_expression* fact_d_min_cardinality ( fact_reasoning_kernel *k,unsigned int n, fact_d_role_expression* r, fact_data_expression* e )
{
	return new fact_concept_expression(k->p->getExpressionManager()->MinCardinality(n,r->p,e->p));
}
/// get max cardinality restriction wrt number _n, a data role r and a data expression e
fact_concept_expression* fact_d_max_cardinality ( fact_reasoning_kernel *k,unsigned int n, fact_d_role_expression* r, fact_data_expression* e )
{
	return new fact_concept_expression(k->p->getExpressionManager()->MaxCardinality(n,r->p,e->p));
}
/// get exact cardinality restriction wrt number _n, a data role r and a data expression e
fact_concept_expression* fact_d_cardinality ( fact_reasoning_kernel *k,unsigned int n, fact_d_role_expression* r, fact_data_expression* e )
{
	return new fact_concept_expression(k->p->getExpressionManager()->Cardinality(n,r->p,e->p));
}

// individuals

/// get named individual
fact_individual_expression* fact_individual ( fact_reasoning_kernel *k,const char* name )
{
	return new fact_individual_expression(k->p->getExpressionManager()->Individual(name));
}

// object roles

/// get TOP object role
fact_o_role_expression* fact_object_role_top ( fact_reasoning_kernel *k )
{
	return new fact_o_role_expression(k->p->getExpressionManager()->ObjectRoleTop());
}
/// get BOTTOM object role
fact_o_role_expression* fact_object_role_bottom ( fact_reasoning_kernel *k )
{
	return new fact_o_role_expression(k->p->getExpressionManager()->ObjectRoleBottom());
}
/// get named object role
fact_o_role_expression* fact_object_role ( fact_reasoning_kernel *k,const char* name )
{
	return new fact_o_role_expression(k->p->getExpressionManager()->ObjectRole(name));
}
/// get an inverse of a given object role expression r
fact_o_role_expression* fact_inverse ( fact_reasoning_kernel *k,fact_o_role_expression* r )
{
	return new fact_o_role_expression(k->p->getExpressionManager()->Inverse(r->p));
}
/// get a role chain corresponding to _r1 o ... o _rn; take the arguments from the last argument list
fact_o_role_complex_expression* fact_compose ( fact_reasoning_kernel *k )
{
	return new fact_o_role_complex_expression(k->p->getExpressionManager()->Compose());
}
/// get a expression corresponding to r projected from c
fact_o_role_complex_expression* fact_project_from ( fact_reasoning_kernel *k,fact_o_role_expression* r, fact_concept_expression* c )
{
	return new fact_o_role_complex_expression(k->p->getExpressionManager()->ProjectFrom(r->p,c->p));
}
/// get a expression corresponding to r projected into c
fact_o_role_complex_expression* fact_project_into ( fact_reasoning_kernel *k,fact_o_role_expression* r, fact_concept_expression* c )
{
	return new fact_o_role_complex_expression(k->p->getExpressionManager()->ProjectInto(r->p,c->p));
}

// data roles

/// get TOP data role
fact_d_role_expression* fact_data_role_top ( fact_reasoning_kernel *k )
{
	return new fact_d_role_expression(k->p->getExpressionManager()->DataRoleTop());
}
/// get BOTTOM data role
fact_d_role_expression* fact_data_role_bottom ( fact_reasoning_kernel *k )
{
	return new fact_d_role_expression(k->p->getExpressionManager()->DataRoleBottom());
}
/// get named data role
fact_d_role_expression* fact_data_role ( fact_reasoning_kernel *k,const char* name )
{
	return new fact_d_role_expression(k->p->getExpressionManager()->DataRole(name));
}

// data expressions

/// get TOP data element
fact_data_expression* fact_data_top ( fact_reasoning_kernel *k )
{
	return new fact_data_expression(k->p->getExpressionManager()->DataTop());
}
/// get BOTTOM data element
fact_data_expression* fact_data_bottom ( fact_reasoning_kernel *k )
{
	return new fact_data_expression(k->p->getExpressionManager()->DataBottom());
}

/// get named data type
fact_data_type_expression* fact_data_type ( fact_reasoning_kernel *k,const char* name )
{
	return new fact_data_type_expression(k->p->getExpressionManager()->DataType(name));
}
/// get basic string data type
fact_data_type_expression* fact_get_str_data_type ( fact_reasoning_kernel *k )
{
	return new fact_data_type_expression(k->p->getExpressionManager()->getStrDataType());
}
/// get basic integer data type
fact_data_type_expression* fact_get_int_data_type ( fact_reasoning_kernel *k )
{
	return new fact_data_type_expression(k->p->getExpressionManager()->getIntDataType());
}
/// get basic floating point data type
fact_data_type_expression* fact_get_real_data_type ( fact_reasoning_kernel *k )
{
	return new fact_data_type_expression(k->p->getExpressionManager()->getRealDataType());
}
/// get basic boolean data type
fact_data_type_expression* fact_get_bool_data_type ( fact_reasoning_kernel *k )
{
	return new fact_data_type_expression(k->p->getExpressionManager()->getBoolDataType());
}
/// get basic date-time data type
fact_data_type_expression* fact_get_time_data_type ( fact_reasoning_kernel *k )
{
	return new fact_data_type_expression(k->p->getExpressionManager()->getTimeDataType());
}

/// get basic boolean data type
fact_data_type_expression* fact_restricted_type ( fact_reasoning_kernel *k,fact_data_type_expression* type, fact_facet_expression* facet )
{
	return new fact_data_type_expression(k->p->getExpressionManager()->RestrictedType(type->p,facet->p));
}

/// get data value with given VALUE and TYPE;
fact_data_value_expression* fact_data_value ( fact_reasoning_kernel *k,const char* value, fact_data_type_expression* type )
{
	return new fact_data_value_expression(k->p->getExpressionManager()->DataValue(value,type->p));
}
/// get negation of a data expression e
fact_data_expression* fact_data_not ( fact_reasoning_kernel *k,fact_data_expression* e )
{
	return new fact_data_expression(k->p->getExpressionManager()->DataNot(e->p));
}
/// get an n-ary data conjunction expression; take the arguments from the last argument list
fact_data_expression* fact_data_and ( fact_reasoning_kernel *k )
{
	return new fact_data_expression(k->p->getExpressionManager()->DataAnd());
}
/// get an n-ary data disjunction expression; take the arguments from the last argument list
fact_data_expression* fact_data_or ( fact_reasoning_kernel *k )
{
	return new fact_data_expression(k->p->getExpressionManager()->DataOr());
}
/// get an n-ary data one-of expression; take the arguments from the last argument list
fact_data_expression* fact_data_one_of ( fact_reasoning_kernel *k )
{
	return new fact_data_expression(k->p->getExpressionManager()->DataOneOf());
}
/// get min_inclusive facet with a given _v
fact_facet_expression* fact_facet_min_inclusive ( fact_reasoning_kernel *k,fact_data_value_expression* v )
{
	return new fact_facet_expression(k->p->getExpressionManager()->FacetMinInclusive(v->p));
}
/// get min_exclusive facet with a given _v
fact_facet_expression* fact_facet_min_exclusive ( fact_reasoning_kernel *k,fact_data_value_expression* v )
{
	return new fact_facet_expression(k->p->getExpressionManager()->FacetMinExclusive(v->p));
}
/// get max_inclusive facet with a given _v
fact_facet_expression* fact_facet_max_inclusive ( fact_reasoning_kernel *k,fact_data_value_expression* v )
{
	return new fact_facet_expression(k->p->getExpressionManager()->FacetMaxInclusive(v->p));
}
/// get max_exclusive facet with a given _v
fact_facet_expression* fact_facet_max_exclusive ( fact_reasoning_kernel *k,fact_data_value_expression* v )
{
	return new fact_facet_expression(k->p->getExpressionManager()->FacetMaxExclusive(v->p));
}
void fact_kb_set_tracing(fact_reasoning_kernel *k)
{
    k->p->needTracing();
}

void fact_kb_set_dump(fact_reasoning_kernel *k)
{
    k->p->setDumpOntology(true);
}
const char **fact_get_role_fillers(fact_reasoning_kernel *k, fact_individual_expression *i, fact_o_role_expression *r) {
    ReasoningKernel::IndividualSet s = ReasoningKernel::IndividualSet();
    k->p->getRoleFillers(i->p, r->p, s);

    const char** result = new const char*[s.size() + 1];
    for (size_t i = 0; i < s.size(); ++i)
        result[i] = s[i]->getName();
    result[s.size()] = NULL;
    return result;
}
