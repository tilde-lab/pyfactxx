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

#ifndef FACT_H
#define FACT_H

// export specification
#if defined(_WIN32)
#	if defined(FPP_BUILD_SHARED)	/* build DLL */
#		define FPP_EXPORT __declspec(dllexport)
#	else /* use DLL */
#		define FPP_EXPORT __declspec(dllimport)
#	endif
#else
#	define FPP_EXPORT
#endif


#ifdef __cplusplus
extern "C" {
#endif

/* type declarations */

#define DECLARE_STRUCT(name) typedef struct name ## _st name
/* FaCT++ kernel */
DECLARE_STRUCT(fact_reasoning_kernel);
/* progress monitor */
DECLARE_STRUCT(fact_progress_monitor);
/* axiom */
DECLARE_STRUCT(fact_axiom);
/* expression */
DECLARE_STRUCT(fact_expression);
/* concept expression */
DECLARE_STRUCT(fact_concept_expression);
/* data- or object-role expression */
DECLARE_STRUCT(fact_role_expression);
/* object role expression */
DECLARE_STRUCT(fact_o_role_expression);
/* complex object role expression */
DECLARE_STRUCT(fact_o_role_complex_expression);
/* data role expression */
DECLARE_STRUCT(fact_d_role_expression);
/* individual expression */
DECLARE_STRUCT(fact_individual_expression);
/* general data expression */
DECLARE_STRUCT(fact_data_expression);
/* data type expression */
DECLARE_STRUCT(fact_data_type_expression);
/* data value expression */
DECLARE_STRUCT(fact_data_value_expression);
/* facet expression */
DECLARE_STRUCT(fact_facet_expression);
/* actor to traverse taxonomy */
DECLARE_STRUCT(fact_actor);

#undef DECLARE_STRUCT

FPP_EXPORT const char *fact_get_version ();

FPP_EXPORT fact_reasoning_kernel *fact_reasoning_kernel_new (void);
FPP_EXPORT void fact_reasoning_kernel_free (fact_reasoning_kernel *);

/*
ifOptionSet* getOptions (  );
const ifOptionSet* getOptions (  );
 */

FPP_EXPORT int fact_is_kb_preprocessed (fact_reasoning_kernel *);
FPP_EXPORT int fact_is_kb_classified (fact_reasoning_kernel *);
FPP_EXPORT int fact_is_kb_realised (fact_reasoning_kernel *);
FPP_EXPORT void fact_set_progress_monitor (fact_reasoning_kernel *, fact_progress_monitor *);

FPP_EXPORT void fact_set_verbose_output (fact_reasoning_kernel *, int value);

FPP_EXPORT void fact_set_top_bottom_role_names (fact_reasoning_kernel *,
		const char *top_b_role_name,
		const char *bot_b_role_name,
		const char *top_d_role_name,
		const char *bot_d_role_name);

FPP_EXPORT void fact_set_operation_timeout (fact_reasoning_kernel *,
		unsigned long timeout);

FPP_EXPORT int fact_new_kb (fact_reasoning_kernel *);
FPP_EXPORT int fact_release_kb (fact_reasoning_kernel *);
FPP_EXPORT int fact_clear_kb (fact_reasoning_kernel *);

FPP_EXPORT fact_axiom *fact_declare (fact_reasoning_kernel *, fact_expression *c);
FPP_EXPORT fact_axiom *fact_implies_concepts (fact_reasoning_kernel *,
		fact_concept_expression *c,
		fact_concept_expression *d);
FPP_EXPORT fact_axiom *fact_equal_concepts (fact_reasoning_kernel *);
FPP_EXPORT fact_axiom *fact_disjoint_concepts (fact_reasoning_kernel *);
FPP_EXPORT fact_axiom *fact_disjoint_union (fact_reasoning_kernel *,
		fact_concept_expression *c);


FPP_EXPORT fact_axiom *fact_set_inverse_roles (fact_reasoning_kernel *,
		fact_o_role_expression *r,
		fact_o_role_expression *s);
FPP_EXPORT fact_axiom *fact_implies_o_roles (fact_reasoning_kernel *,
		fact_o_role_complex_expression *r,
		fact_o_role_expression *s);
FPP_EXPORT fact_axiom *fact_implies_d_roles (fact_reasoning_kernel *,
		fact_d_role_expression *r,
		fact_d_role_expression *s);
FPP_EXPORT fact_axiom *fact_equal_o_roles (fact_reasoning_kernel *);
FPP_EXPORT fact_axiom *fact_equal_d_roles (fact_reasoning_kernel *);
FPP_EXPORT fact_axiom *fact_disjoint_o_roles (fact_reasoning_kernel *);
FPP_EXPORT fact_axiom *fact_disjoint_d_roles (fact_reasoning_kernel *);

FPP_EXPORT fact_axiom* fact_set_o_domain (fact_reasoning_kernel *,
		fact_o_role_expression *r,
		fact_concept_expression *c);
FPP_EXPORT fact_axiom *fact_set_d_domain (fact_reasoning_kernel *,
		fact_d_role_expression *r,
		fact_concept_expression *c);
FPP_EXPORT fact_axiom *fact_set_o_range (fact_reasoning_kernel *,
		fact_o_role_expression *r,
		fact_concept_expression *c);
FPP_EXPORT fact_axiom *fact_set_d_range (fact_reasoning_kernel *,
		fact_d_role_expression *r,
		fact_data_expression *e);

FPP_EXPORT fact_axiom *fact_set_transitive (fact_reasoning_kernel *,
		fact_o_role_expression *r);
FPP_EXPORT fact_axiom *fact_set_reflexive (fact_reasoning_kernel *,
		fact_o_role_expression *r);
FPP_EXPORT fact_axiom *fact_set_irreflexive (fact_reasoning_kernel *,
		fact_o_role_expression *r);
FPP_EXPORT fact_axiom *fact_set_symmetric (fact_reasoning_kernel *,
		fact_o_role_expression *r);
FPP_EXPORT fact_axiom *fact_set_asymmetric (fact_reasoning_kernel *,
		fact_o_role_expression *r);
FPP_EXPORT fact_axiom *fact_set_o_functional (fact_reasoning_kernel *,
		fact_o_role_expression *r);
FPP_EXPORT fact_axiom *fact_set_d_functional (fact_reasoning_kernel *,
		fact_d_role_expression *r);
FPP_EXPORT fact_axiom *fact_set_inverse_functional (fact_reasoning_kernel *,
		fact_o_role_expression *r);

FPP_EXPORT fact_axiom *fact_instance_of (fact_reasoning_kernel *,
		fact_individual_expression *i,
		fact_concept_expression *c);
FPP_EXPORT fact_axiom *fact_related_to (fact_reasoning_kernel *,
		fact_individual_expression *i,
		fact_o_role_expression *r,
		fact_individual_expression *j);
FPP_EXPORT fact_axiom *fact_related_to_not (fact_reasoning_kernel *,
		fact_individual_expression *i,
		fact_o_role_expression *r,
		fact_individual_expression *j);
FPP_EXPORT fact_axiom *fact_value_of (fact_reasoning_kernel *,
		fact_individual_expression *i,
		fact_d_role_expression *a,
		fact_data_value_expression *v);
FPP_EXPORT fact_axiom *fact_value_of_not (fact_reasoning_kernel *,
		fact_individual_expression *i,
		fact_d_role_expression *a,
		fact_data_value_expression *v);
FPP_EXPORT fact_axiom *fact_process_same (fact_reasoning_kernel *);
FPP_EXPORT fact_axiom *fact_process_different (fact_reasoning_kernel *);
FPP_EXPORT fact_axiom *fact_set_fairness_constraint (fact_reasoning_kernel *);

FPP_EXPORT void fact_retract (fact_reasoning_kernel *, fact_axiom *axiom);

FPP_EXPORT int fact_is_kb_consistent (fact_reasoning_kernel *);
FPP_EXPORT void fact_preprocess_kb (fact_reasoning_kernel *);
FPP_EXPORT void fact_classify_kb (fact_reasoning_kernel *);
FPP_EXPORT void fact_realise_kb (fact_reasoning_kernel *);

FPP_EXPORT int fact_is_o_functional (fact_reasoning_kernel *, fact_o_role_expression *r);
FPP_EXPORT int fact_is_d_functional (fact_reasoning_kernel *,
		fact_d_role_expression *r);
FPP_EXPORT int fact_is_inverse_functional (fact_reasoning_kernel *,
		fact_o_role_expression *r);
FPP_EXPORT int fact_is_transitive (fact_reasoning_kernel *, fact_o_role_expression *r);
FPP_EXPORT int fact_is_symmetric (fact_reasoning_kernel *, fact_o_role_expression *r);
FPP_EXPORT int fact_is_asymmetric (fact_reasoning_kernel *, fact_o_role_expression *r);
FPP_EXPORT int fact_is_reflexive (fact_reasoning_kernel *, fact_o_role_expression *r);
FPP_EXPORT int fact_is_irreflexive (fact_reasoning_kernel *, fact_o_role_expression *r);
FPP_EXPORT int fact_is_sub_o_roles (fact_reasoning_kernel *, fact_o_role_expression *r,
		fact_o_role_expression *s);
FPP_EXPORT int fact_is_sub_d_roles (fact_reasoning_kernel *, fact_d_role_expression *r,
		fact_d_role_expression *s);
FPP_EXPORT int fact_is_disjoint_o_roles (fact_reasoning_kernel *,
		fact_o_role_expression *r,
		fact_o_role_expression *s);
FPP_EXPORT int fact_is_disjoint_d_roles (fact_reasoning_kernel *,
		fact_d_role_expression *r,
		fact_d_role_expression *s);
FPP_EXPORT int fact_is_disjoint_roles (fact_reasoning_kernel *);
FPP_EXPORT int fact_is_sub_chain (fact_reasoning_kernel *, fact_o_role_expression *r);

FPP_EXPORT int fact_is_satisfiable (fact_reasoning_kernel *, fact_concept_expression *c);
FPP_EXPORT int fact_is_subsumed_by (fact_reasoning_kernel *, fact_concept_expression *c,
		fact_concept_expression *d);
FPP_EXPORT int fact_is_disjoint (fact_reasoning_kernel *, fact_concept_expression *c,
		fact_concept_expression *d);
FPP_EXPORT int fact_is_equivalent (fact_reasoning_kernel *, fact_concept_expression *c,
		fact_concept_expression *d);

FPP_EXPORT void fact_get_sup_concepts (fact_reasoning_kernel *, fact_concept_expression *c,
		int direct, fact_actor **actor);
FPP_EXPORT void fact_get_sub_concepts (fact_reasoning_kernel *, fact_concept_expression *c,
		int direct, fact_actor **actor);
FPP_EXPORT void fact_get_equivalent_concepts (fact_reasoning_kernel *,
		fact_concept_expression *c,
		fact_actor **actor);
FPP_EXPORT void fact_get_disjoint_concepts (fact_reasoning_kernel *,
		fact_concept_expression *c,
		fact_actor **actor);

FPP_EXPORT void fact_get_sup_roles (fact_reasoning_kernel *, fact_role_expression *r,
		int direct, fact_actor **actor);
FPP_EXPORT void fact_get_sub_roles (fact_reasoning_kernel *, fact_role_expression *r,
		int direct, fact_actor **actor);
FPP_EXPORT void fact_get_equivalent_roles (fact_reasoning_kernel *, fact_role_expression *r,
		fact_actor **actor);
FPP_EXPORT void fact_get_o_role_domain (fact_reasoning_kernel *, fact_o_role_expression *r,
		int direct, fact_actor **actor);
FPP_EXPORT void fact_get_d_role_domain (fact_reasoning_kernel *, fact_d_role_expression *r,
		int direct, fact_actor **actor);
FPP_EXPORT void fact_get_role_range (fact_reasoning_kernel *, fact_o_role_expression *r,
		int direct, fact_actor **actor);
FPP_EXPORT void fact_get_direct_instances (fact_reasoning_kernel *,
		fact_concept_expression *c, fact_actor **actor);
FPP_EXPORT void fact_get_instances (fact_reasoning_kernel *, fact_concept_expression *c,
		fact_actor **actor);
FPP_EXPORT void fact_get_types (fact_reasoning_kernel *, fact_individual_expression *i,
		int direct, fact_actor **actor);
FPP_EXPORT void fact_get_same_as (fact_reasoning_kernel *,
		fact_individual_expression *i, fact_actor **actor);

FPP_EXPORT int fact_is_same_individuals (fact_reasoning_kernel *,
		fact_individual_expression *i,
		fact_individual_expression *j);
FPP_EXPORT int fact_is_instance (fact_reasoning_kernel *,
		fact_individual_expression *i,
		fact_concept_expression *c);
/*
void fact_get_related_roles (fact_reasoning_kernel *,
			     fact_individual_expression *i,
			     int data, int needI,
			     fact_names_vector **result);
 */
FPP_EXPORT const char **fact_get_role_fillers (fact_reasoning_kernel *,
			    fact_individual_expression *i,
			    fact_o_role_expression *r);
FPP_EXPORT int fact_is_related (fact_reasoning_kernel *,
		 fact_individual_expression *i,
		 fact_o_role_expression *r,
		 fact_individual_expression *j);

FPP_EXPORT fact_actor* fact_concept_actor_new();
FPP_EXPORT fact_actor* fact_individual_actor_new();
FPP_EXPORT fact_actor* fact_o_role_actor_new();
FPP_EXPORT fact_actor* fact_d_role_actor_new();
FPP_EXPORT void fact_actor_free(fact_actor*);
/* get 1-d NULL-terminated array of synonyms of the 1st entry(necessary for Equivalents, for example) */
FPP_EXPORT const char** fact_get_synonyms ( fact_actor* );
/* get NULL-terminated 2D array of all required elements of the taxonomy */
FPP_EXPORT const char*** fact_get_elements_2d ( fact_actor* );
/* get NULL-terminated 1D array of all required elements of the taxonomy */
FPP_EXPORT const char** fact_get_elements_1d ( fact_actor* );

/* opens new argument list */
FPP_EXPORT void fact_new_arg_list ( fact_reasoning_kernel *k );
/* add argument _a_rG to the current argument list */
FPP_EXPORT void fact_add_arg ( fact_reasoning_kernel *k,fact_expression* e );

/* create expressions methods */

/* concepts */

/* get _t_o_p concept */
FPP_EXPORT fact_concept_expression* fact_top ( fact_reasoning_kernel *k );
/* get _b_o_t_t_o_m concept */
FPP_EXPORT fact_concept_expression* fact_bottom ( fact_reasoning_kernel *k );
/* get named concept */
FPP_EXPORT fact_concept_expression* fact_concept ( fact_reasoning_kernel *k,const char* name );
/* get negation of a concept c */
FPP_EXPORT fact_concept_expression* fact_not ( fact_reasoning_kernel *k,fact_concept_expression* c );
/* get an n-ary conjunction expression; take the arguments from the last argument list */
FPP_EXPORT fact_concept_expression* fact_and ( fact_reasoning_kernel *k );
/* get an n-ary disjunction expression; take the arguments from the last argument list */
FPP_EXPORT fact_concept_expression* fact_or ( fact_reasoning_kernel *k );
/* get an n-ary one-of expression; take the arguments from the last argument list */
FPP_EXPORT fact_concept_expression* fact_one_of ( fact_reasoning_kernel *k );

/* get self-reference restriction of an object role r */
FPP_EXPORT fact_concept_expression* fact_self_reference ( fact_reasoning_kernel *k,fact_o_role_expression* r );
/* get value restriction wrt an object role r and an individual i */
FPP_EXPORT fact_concept_expression* fact_o_value ( fact_reasoning_kernel *k,fact_o_role_expression* r, fact_individual_expression* i );
/* get existential restriction wrt an object role r and a concept c */
FPP_EXPORT fact_concept_expression* fact_o_exists ( fact_reasoning_kernel *k,fact_o_role_expression* r, fact_concept_expression* c );
/* get universal restriction wrt an object role r and a concept c */
FPP_EXPORT fact_concept_expression* fact_o_forall ( fact_reasoning_kernel *k,fact_o_role_expression* r, fact_concept_expression* c );
/* get min cardinality restriction wrt number _n, an object role r and a concept c */
FPP_EXPORT fact_concept_expression* fact_o_min_cardinality ( fact_reasoning_kernel *k,unsigned int n, fact_o_role_expression* r, fact_concept_expression* c );
/* get max cardinality restriction wrt number _n, an object role r and a concept c */
FPP_EXPORT fact_concept_expression* fact_o_max_cardinality ( fact_reasoning_kernel *k,unsigned int n, fact_o_role_expression* r, fact_concept_expression* c );
/* get exact cardinality restriction wrt number _n, an object role r and a concept c */
FPP_EXPORT fact_concept_expression* fact_o_cardinality ( fact_reasoning_kernel *k,unsigned int n, fact_o_role_expression* r, fact_concept_expression* c );

/* get value restriction wrt a data role r and a data value v */
FPP_EXPORT fact_concept_expression* fact_d_value ( fact_reasoning_kernel *k,fact_d_role_expression* r, fact_data_value_expression* v );
/* get existential restriction wrt a data role r and a data expression e */
FPP_EXPORT fact_concept_expression* fact_d_exists ( fact_reasoning_kernel *k,fact_d_role_expression* r, fact_data_expression* e );
/* get universal restriction wrt a data role r and a data expression e */
FPP_EXPORT fact_concept_expression* fact_d_forall ( fact_reasoning_kernel *k,fact_d_role_expression* r, fact_data_expression* e );
/* get min cardinality restriction wrt number _n, a data role r and a data expression e */
FPP_EXPORT fact_concept_expression* fact_d_min_cardinality ( fact_reasoning_kernel *k,unsigned int n, fact_d_role_expression* r, fact_data_expression* e );
/* get max cardinality restriction wrt number _n, a data role r and a data expression e */
FPP_EXPORT fact_concept_expression* fact_d_max_cardinality ( fact_reasoning_kernel *k,unsigned int n, fact_d_role_expression* r, fact_data_expression* e );
/* get exact cardinality restriction wrt number _n, a data role r and a data expression e */
FPP_EXPORT fact_concept_expression* fact_d_cardinality ( fact_reasoning_kernel *k,unsigned int n, fact_d_role_expression* r, fact_data_expression* e );

/* individuals */

/* get named individual */
FPP_EXPORT fact_individual_expression* fact_individual ( fact_reasoning_kernel *k,const char* name );

/* object roles */

/* get _t_o_p object role */
FPP_EXPORT fact_o_role_expression* fact_object_role_top ( fact_reasoning_kernel *k );
/* get _b_o_t_t_o_m object role */
FPP_EXPORT fact_o_role_expression* fact_object_role_bottom ( fact_reasoning_kernel *k );
/* get named object role */
FPP_EXPORT fact_o_role_expression* fact_object_role ( fact_reasoning_kernel *k,const char* name );
/* get an inverse of a given object role expression r */
FPP_EXPORT fact_o_role_expression* fact_inverse ( fact_reasoning_kernel *k,fact_o_role_expression* r );
/* get a role chain corresponding to _r1 o ... o _rn; take the arguments from the last argument list */
FPP_EXPORT fact_o_role_complex_expression* fact_compose ( fact_reasoning_kernel *k );
/* get a expression corresponding to r projected from c */
FPP_EXPORT fact_o_role_complex_expression* fact_project_from ( fact_reasoning_kernel *k,fact_o_role_expression* r, fact_concept_expression* c );
/* get a expression corresponding to r projected into c */
FPP_EXPORT fact_o_role_complex_expression* fact_project_into ( fact_reasoning_kernel *k,fact_o_role_expression* r, fact_concept_expression* c );

/* data roles */

/* get _t_o_p data role */
FPP_EXPORT fact_d_role_expression* fact_data_role_top ( fact_reasoning_kernel *k );
/* get _b_o_t_t_o_m data role */
FPP_EXPORT fact_d_role_expression* fact_data_role_bottom ( fact_reasoning_kernel *k );
/* get named data role */
FPP_EXPORT fact_d_role_expression* fact_data_role ( fact_reasoning_kernel *k,const char* name );

/* data expressions */

/* get _t_o_p data element */
FPP_EXPORT fact_data_expression* fact_data_top ( fact_reasoning_kernel *k );
/* get _b_o_t_t_o_m data element */
FPP_EXPORT fact_data_expression* fact_data_bottom ( fact_reasoning_kernel *k );

/* get named data type */
FPP_EXPORT fact_data_type_expression* fact_data_type ( fact_reasoning_kernel *k,const char* name );
/* get basic string data type */
FPP_EXPORT fact_data_type_expression* fact_get_str_data_type ( fact_reasoning_kernel *k );
/* get basic integer data type */
FPP_EXPORT fact_data_type_expression* fact_get_int_data_type ( fact_reasoning_kernel *k );
/* get basic floating point data type */
FPP_EXPORT fact_data_type_expression* fact_get_real_data_type ( fact_reasoning_kernel *k );
/* get basic boolean data type */
FPP_EXPORT fact_data_type_expression* fact_get_bool_data_type ( fact_reasoning_kernel *k );
/* get basic date-time data type */
FPP_EXPORT fact_data_type_expression* fact_get_time_data_type ( fact_reasoning_kernel *k );

/* get basic boolean data type */
FPP_EXPORT fact_data_type_expression* fact_restricted_type ( fact_reasoning_kernel *k,fact_data_type_expression* type, fact_facet_expression* facet );

/* get data value with given _v_a_lU_e and _tY_p_e; */
/* _f_iX_m_e!! now change the type to the basic type of the given one */
/* _that is, value of a type positive_integer will be of a type _integer */
FPP_EXPORT fact_data_value_expression* fact_data_value ( fact_reasoning_kernel *k,const char* value, fact_data_type_expression* type );
/* get negation of a data expression e */
FPP_EXPORT fact_data_expression* fact_data_not ( fact_reasoning_kernel *k,fact_data_expression* e );
/* get an n-ary data conjunction expression; take the arguments from the last argument list */
FPP_EXPORT fact_data_expression* fact_data_and ( fact_reasoning_kernel *k );
/* get an n-ary data disjunction expression; take the arguments from the last argument list */
FPP_EXPORT fact_data_expression* fact_data_or ( fact_reasoning_kernel *k );
/* get an n-ary data one-of expression; take the arguments from the last argument list */
FPP_EXPORT fact_data_expression* fact_data_one_of ( fact_reasoning_kernel *k );

/* get min_inclusive facet with a given _v */
FPP_EXPORT fact_facet_expression* fact_facet_min_inclusive ( fact_reasoning_kernel *k,fact_data_value_expression* v );
/* get min_exclusive facet with a given _v */
FPP_EXPORT fact_facet_expression* fact_facet_min_exclusive ( fact_reasoning_kernel *k,fact_data_value_expression* v );
/* get max_inclusive facet with a given _v */
FPP_EXPORT fact_facet_expression* fact_facet_max_inclusive ( fact_reasoning_kernel *k,fact_data_value_expression* v );
/* get max_exclusive facet with a given _v */
FPP_EXPORT fact_facet_expression* fact_facet_max_exclusive ( fact_reasoning_kernel *k,fact_data_value_expression* v );

void fact_kb_set_tracing(fact_reasoning_kernel *k);
void fact_kb_set_dump(fact_reasoning_kernel *k);

#ifdef __cplusplus
}
#endif

#endif
