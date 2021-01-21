/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2006-2015 Dmitry Tsarkov and The University of Manchester
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

// this file contains implementation of DL axiom-related methods of FaCT++ JNI interface

#include "uk_ac_manchester_cs_factplusplus_FaCTPlusPlus.h"
#include "Kernel.h"
#include "tJNICache.h"

#ifdef __cplusplus
extern "C" {
#endif

//-------------------------------------------------------------
// Concept/role/individual axioms (TELL language)
//-------------------------------------------------------------

#define PROCESS_QUERY(Action,Name)				\
	do { TRACE_JNI(Name);						\
	TJNICache* J = getJ(env,obj);				\
	try { return J->Axiom(Action); }			\
	catch ( const EFPPInconsistentKB& )			\
	{ ThrowICO(J->env); }						\
	catch ( const EFPPNonSimpleRole& nsr )		\
	{ ThrowNSR ( J->env, nsr.getRoleName() ); }	\
	catch ( const EFPPCycleInRIA& cir )			\
	{ ThrowRIC ( J->env, cir.getRoleName() ); }	\
	catch ( const EFaCTPlusPlus& fpp )			\
	{ Throw ( J->env, fpp.what() ); }			\
	catch ( const std::exception& ex )			\
	{ Throw ( J->env, ex.what() ); }			\
		return nullptr;  } while (false)
//	Throw ( env, "FaCT++ Kernel: error during " Name " processing" )

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellClassDeclaration
 * Signature: (Luk/ac/manchester/cs/factplusplus/ClassPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellClassDeclaration
  (JNIEnv * env, jobject obj, jobject arg)
{
	PROCESS_QUERY ( J->K->declare(getConceptExpr(env,arg)), "tellClassDeclaration" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellObjectPropertyDeclaration
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellObjectPropertyDeclaration
  (JNIEnv * env, jobject obj, jobject arg)
{
	PROCESS_QUERY ( getK(env,obj)->declare(getORoleExpr(env,arg)), "tellObjectPropertyDeclaration" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellDataPropertyDeclaration
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellDataPropertyDeclaration
  (JNIEnv * env, jobject obj, jobject arg)
{
	PROCESS_QUERY ( getK(env,obj)->declare(getDRoleExpr(env,arg)), "tellDataPropertyDeclaration" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellIndividualDeclaration
 * Signature: (Luk/ac/manchester/cs/factplusplus/IndividualPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellIndividualDeclaration
  (JNIEnv * env, jobject obj, jobject arg)
{
	PROCESS_QUERY ( getK(env,obj)->declare(getIndividualExpr(env,arg)), "tellIndividualDeclaration" );
}


/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellDatatypeDeclaration
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataTypePointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellDatatypeDeclaration
  (JNIEnv * env, jobject obj, jobject arg)
{
	PROCESS_QUERY ( getK(env,obj)->declare(getDataTypeExpr(env,arg)), "tellDatatypeDeclaration" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellSubClassOf
 * Signature: (Luk/ac/manchester/cs/factplusplus/ClassPointer;Luk/ac/manchester/cs/factplusplus/ClassPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellSubClassOf
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	PROCESS_QUERY ( getK(env,obj)->impliesConcepts ( getConceptExpr(env,arg1), getConceptExpr(env,arg2) ), "tellSubClassOf" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellEquivalentClass
 * Signature: ()Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellEquivalentClass
  (JNIEnv * env, jobject obj)
{
	PROCESS_QUERY ( getK(env,obj)->equalConcepts(), "tellEquivalentClasses" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellDisjointClasses
 * Signature: ()Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellDisjointClasses
  (JNIEnv * env, jobject obj)
{
	PROCESS_QUERY ( getK(env,obj)->disjointConcepts(), "tellDisjointClasses" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellDisjointUnion
 * Signature: (Luk/ac/manchester/cs/factplusplus/ClassPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellDisjointUnion
  (JNIEnv * env, jobject obj, jobject arg)
{
	PROCESS_QUERY ( getK(env,obj)->disjointUnion(getConceptExpr(env,arg)), "tellDisjointUnion" );
}


/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellHasKey
 * Signature: (Luk/ac/manchester/cs/factplusplus/ClassPointer;Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellHasKey
  (JNIEnv * env, jobject obj ATTR_UNUSED, jobject, jobject, jobject)
{
	TRACE_JNI("tellHasKey");
	Throw ( env, "FaCT++ Kernel: unsupported operation 'tellHasKey'" );
	return nullptr;
}


/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellSubObjectProperties
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellSubObjectProperties
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	PROCESS_QUERY ( getK(env,obj)->impliesORoles ( getORoleComplexExpr(env,arg1), getORoleExpr(env,arg2) ), "tellSubObjectProperties" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellEquivalentObjectProperties
 * Signature: ()Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellEquivalentObjectProperties
  (JNIEnv * env, jobject obj)
{
	PROCESS_QUERY ( getK(env,obj)->equalORoles(), "tellEquivalentObjectProperties" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellInverseProperties
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellInverseProperties
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	PROCESS_QUERY ( getK(env,obj)->setInverseRoles ( getORoleExpr(env,arg1), getORoleExpr(env,arg2) ), "tellInverseProperties" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellObjectPropertyRange
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Luk/ac/manchester/cs/factplusplus/ClassPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellObjectPropertyRange
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	PROCESS_QUERY ( getK(env,obj)->setORange ( getORoleExpr(env,arg1), getConceptExpr(env,arg2) ), "tellObjectPropertyRange" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellDataPropertyRange
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;Luk/ac/manchester/cs/factplusplus/DataTypePointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellDataPropertyRange
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	PROCESS_QUERY ( getK(env,obj)->setDRange ( getDRoleExpr(env,arg1), getDataExpr(env,arg2) ), "tellDataPropertyRange" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellObjectPropertyDomain
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Luk/ac/manchester/cs/factplusplus/ClassPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellObjectPropertyDomain
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	PROCESS_QUERY ( getK(env,obj)->setODomain ( getORoleExpr(env,arg1), getConceptExpr(env,arg2) ), "tellObjectPropertyDomain" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellDataPropertyDomain
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;Luk/ac/manchester/cs/factplusplus/ClassPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellDataPropertyDomain
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	PROCESS_QUERY ( getK(env,obj)->setDDomain ( getDRoleExpr(env,arg1), getConceptExpr(env,arg2) ), "tellDataPropertyDomain" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellDisjointObjectProperties
 * Signature: ()Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellDisjointObjectProperties
  (JNIEnv * env, jobject obj)
{
	PROCESS_QUERY ( getK(env,obj)->disjointORoles(), "tellDisjointObjectProperties" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellFunctionalObjectProperty
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellFunctionalObjectProperty
  (JNIEnv * env, jobject obj, jobject arg)
{
	PROCESS_QUERY ( getK(env,obj)->setOFunctional(getORoleExpr(env,arg)), "tellFunctionalObjectProperty" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellInverseFunctionalObjectProperty
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellInverseFunctionalObjectProperty
  (JNIEnv * env, jobject obj, jobject arg)
{
	PROCESS_QUERY ( getK(env,obj)->setInverseFunctional(getORoleExpr(env,arg)), "tellInverseFunctionalObjectProperty" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellSymmetricObjectProperty
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellSymmetricObjectProperty
  (JNIEnv * env, jobject obj, jobject arg)
{
	PROCESS_QUERY ( getK(env,obj)->setSymmetric(getORoleExpr(env,arg)), "tellSymmetricObjectProperty" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellAsymmetricObjectProperty
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellAsymmetricObjectProperty
  (JNIEnv * env, jobject obj, jobject arg)
{
	PROCESS_QUERY ( getK(env,obj)->setAsymmetric(getORoleExpr(env,arg)), "tellAsymmetricObjectProperty" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellReflexiveObjectProperty
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellReflexiveObjectProperty
  (JNIEnv * env, jobject obj, jobject arg)
{
	PROCESS_QUERY ( getK(env,obj)->setReflexive(getORoleExpr(env,arg)), "tellReflexiveObjectProperty" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellIrreflexiveObjectProperty
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellIrreflexiveObjectProperty
  (JNIEnv * env, jobject obj, jobject arg)
{
	PROCESS_QUERY ( getK(env,obj)->setIrreflexive(getORoleExpr(env,arg)), "tellIrreflexiveObjectProperty" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellTransitiveObjectProperty
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellTransitiveObjectProperty
  (JNIEnv * env, jobject obj, jobject arg)
{
	PROCESS_QUERY ( getK(env,obj)->setTransitive(getORoleExpr(env,arg)), "tellTransitiveObjectProperty" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellSubDataProperties
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellSubDataProperties
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	PROCESS_QUERY ( getK(env,obj)->impliesDRoles ( getDRoleExpr(env,arg1), getDRoleExpr(env,arg2) ), "tellSubDataProperties" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellEquivalentDataProperties
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellEquivalentDataProperties
  (JNIEnv * env, jobject obj)
{
	PROCESS_QUERY ( getK(env,obj)->equalDRoles(), "tellEquivalentDataProperties" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellDisjointDataProperties
 * Signature: ()Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellDisjointDataProperties
  (JNIEnv * env, jobject obj)
{
	PROCESS_QUERY ( getK(env,obj)->disjointDRoles(), "tellDisjointDataProperties" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellFunctionalDataProperty
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellFunctionalDataProperty
  (JNIEnv * env, jobject obj, jobject arg)
{
	PROCESS_QUERY ( getK(env,obj)->setDFunctional(getDRoleExpr(env,arg)), "tellFunctionalDataProperty" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellIndividualType
 * Signature: (Luk/ac/manchester/cs/factplusplus/IndividualPointer;Luk/ac/manchester/cs/factplusplus/ClassPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellIndividualType
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	PROCESS_QUERY ( getK(env,obj)->instanceOf ( getIndividualExpr(env,arg1), getConceptExpr(env,arg2) ), "tellIndividualType" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellRelatedIndividuals
 * Signature: (Luk/ac/manchester/cs/factplusplus/IndividualPointer;Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Luk/ac/manchester/cs/factplusplus/IndividualPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellRelatedIndividuals
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2, jobject arg3)
{
	PROCESS_QUERY ( getK(env,obj)->relatedTo ( getIndividualExpr(env,arg1), getORoleExpr(env,arg2), getIndividualExpr(env,arg3) ), "tellRelatedIndividuals" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellNotRelatedIndividuals
 * Signature: (Luk/ac/manchester/cs/factplusplus/IndividualPointer;Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Luk/ac/manchester/cs/factplusplus/IndividualPointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellNotRelatedIndividuals
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2, jobject arg3)
{
	PROCESS_QUERY ( getK(env,obj)->relatedToNot ( getIndividualExpr(env,arg1), getORoleExpr(env,arg2), getIndividualExpr(env,arg3) ), "tellNotRelatedIndividuals" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellRelatedIndividualValue
 * Signature: (Luk/ac/manchester/cs/factplusplus/IndividualPointer;Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;Luk/ac/manchester/cs/factplusplus/DataValuePointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellRelatedIndividualValue
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2, jobject arg3)
{
	PROCESS_QUERY ( getK(env,obj)->valueOf ( getIndividualExpr(env,arg1), getDRoleExpr(env,arg2), getDataValueExpr(env,arg3) ), "tellRelatedIndividualValue" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellNotRelatedIndividualValue
 * Signature: (Luk/ac/manchester/cs/factplusplus/IndividualPointer;Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;Luk/ac/manchester/cs/factplusplus/DataValuePointer;)Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellNotRelatedIndividualValue
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2, jobject arg3)
{
	PROCESS_QUERY ( getK(env,obj)->valueOfNot ( getIndividualExpr(env,arg1), getDRoleExpr(env,arg2), getDataValueExpr(env,arg3) ), "tellNotRelatedIndividualValue" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellSameIndividuals
 * Signature: ()Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellSameIndividuals
  (JNIEnv * env, jobject obj)
{
	PROCESS_QUERY ( getK(env,obj)->processSame(), "tellSameIndividuals" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    tellDifferentIndividuals
 * Signature: ()Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_tellDifferentIndividuals
  (JNIEnv * env, jobject obj)
{
	PROCESS_QUERY ( getK(env,obj)->processDifferent(), "tellDifferentIndividuals" );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    retract
 * Signature: (Luk/ac/manchester/cs/factplusplus/AxiomPointer;)V
 */
JNIEXPORT void JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_retract
  (JNIEnv * env, jobject obj, jobject axiom)
{
	TRACE_JNI("retract");
	getK(env,obj)->retract(getAxiom(env,axiom));
}

#undef PROCESS_QUERY

#ifdef __cplusplus
}
#endif
