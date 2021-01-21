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

// this file contains implementation of DL query-related methods of FaCT++ JNI interface

#include "uk_ac_manchester_cs_factplusplus_FaCTPlusPlus.h"
#include "Kernel.h"
#include "tJNICache.h"
#include "JNIActor.h"
#include "eFPPTimeout.h"
#include "MemoryStat.h"

#ifdef __cplusplus
extern "C" {
#endif

//-------------------------------------------------------------
// minimal query language (ASK languages)
//-------------------------------------------------------------

#define ASK_START do { try {
#define ASK_END }								\
	catch ( const EFPPInconsistentKB& )			\
	{ ThrowICO(env); }							\
	catch ( const EFPPNonSimpleRole& nsr )		\
	{ ThrowNSR ( env, nsr.getRoleName() ); }	\
	catch ( const EFPPCycleInRIA& cir )			\
	{ ThrowRIC ( env, cir.getRoleName() ); }	\
	catch ( const EFPPTimeout& )				\
	{ ThrowTO(env); }							\
	catch ( const EFaCTPlusPlus& fpp )			\
	{ Throw ( env, fpp.what() ); }				\
	catch ( const std::exception& ex )			\
	{ Throw ( env, ex.what() ); }  } while (false)

#define PROCESS_QUERY(Action) ASK_START Action; ASK_END
#define PROCESS_SIMPLE_QUERY(Action) ASK_START TJNICache* J = getJ(env,obj); Action; ASK_END
/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isKBConsistent
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isKBConsistent
  (JNIEnv * env, jobject obj)
{
	MemoryStatistics MS("Consistency Checking");
	TRACE_JNI("isKBConsistent");
	bool ret = false;
	PROCESS_QUERY ( ret=getK(env,obj)->isKBConsistent() );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    classify
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_classify
  (JNIEnv * env, jobject obj)
{
	MemoryStatistics MS("Classification");
	TRACE_JNI("classify");
	PROCESS_QUERY ( getK(env,obj)->classifyKB() );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    realise
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_realise
  (JNIEnv * env, jobject obj)
{
	MemoryStatistics MS("Realization");
	TRACE_JNI("realise");
	PROCESS_QUERY ( getK(env,obj)->realiseKB() );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isRealised
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isRealised
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("isRealised");
	return getK(env,obj)->isKBRealised();
}


/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isClassSatisfiable
 * Signature: (Luk/ac/manchester/cs/factplusplus/ClassPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isClassSatisfiable
  (JNIEnv * env, jobject obj, jobject arg)
{
	MemoryStatistics MS("isClassSatisfiable");
	TRACE_JNI("isClassSatisfiable");
	TRACE_ARG(env,obj,arg);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isSatisfiable(getROConceptExpr(env,arg)) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isClassSubsumedBy
 * Signature: (Luk/ac/manchester/cs/factplusplus/ClassPointer;Luk/ac/manchester/cs/factplusplus/ClassPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isClassSubsumedBy
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	MemoryStatistics MS("isClassSubsumedBy");
	TRACE_JNI("isClassSubsumedBy");
	TRACE_ARG(env,obj,arg1);
	TRACE_ARG(env,obj,arg2);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isSubsumedBy ( getROConceptExpr(env,arg1), getROConceptExpr(env,arg2) ) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isClassEquivalentTo
 * Signature: (Luk/ac/manchester/cs/factplusplus/ClassPointer;Luk/ac/manchester/cs/factplusplus/ClassPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isClassEquivalentTo
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	TRACE_JNI("isClassEquivalentTo");
	TRACE_ARG(env,obj,arg1);
	TRACE_ARG(env,obj,arg2);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isEquivalent ( getROConceptExpr(env,arg1), getROConceptExpr(env,arg2) ) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isClassDisjointWith
 * Signature: (Luk/ac/manchester/cs/factplusplus/ClassPointer;Luk/ac/manchester/cs/factplusplus/ClassPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isClassDisjointWith
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	TRACE_JNI("isClassDisjointWith");
	TRACE_ARG(env,obj,arg1);
	TRACE_ARG(env,obj,arg2);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isDisjoint ( getROConceptExpr(env,arg1), getROConceptExpr(env,arg2) ) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askSubClasses
 * Signature: (Luk/ac/manchester/cs/factplusplus/ClassPointer;Z)[[Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askSubClasses
  (JNIEnv * env, jobject obj, jobject arg, jboolean direct)
{
	MemoryStatistics MS("getSubClasses");
	TRACE_JNI("askSubClasses");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	JTaxonomyActor<ClassPolicy> actor(J);
	const TConceptExpr* p = getROConceptExpr(env,arg);
	PROCESS_QUERY ( J->K->getSubConcepts(p,direct,actor) );
	return actor.getElements();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askSuperClasses
 * Signature: (Luk/ac/manchester/cs/factplusplus/ClassPointer;Z)[[Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askSuperClasses
  (JNIEnv * env, jobject obj, jobject arg, jboolean direct)
{
	MemoryStatistics MS("getSuperClasses");
	TRACE_JNI("askSuperClasses");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	JTaxonomyActor<ClassPolicy> actor(J);
	const TConceptExpr* p = getROConceptExpr(env,arg);
	PROCESS_QUERY ( J->K->getSupConcepts(p,direct,actor) );
	return actor.getElements();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askEquivalentClasses
 * Signature: (Luk/ac/manchester/cs/factplusplus/ClassPointer;)[Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askEquivalentClasses
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("askEquivalentClasses");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	JTaxonomyActor<ClassPolicy> actor(J);
	PROCESS_QUERY ( J->K->getEquivalentConcepts ( getROConceptExpr(env,arg), actor ) );
	return actor.getSynonyms();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askDisjointClasses
 * Signature: (Luk/ac/manchester/cs/factplusplus/ClassPointer;)[[Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askDisjointClasses
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("askDisjointClasses");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	JTaxonomyActor<ClassPolicy> actor(J);
	const TConceptExpr* p = getROConceptExpr(env,arg);
	PROCESS_QUERY ( J->K->getDisjointConcepts(p,actor) );
	return actor.getElements();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askSuperObjectProperties
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Z)[[Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askSuperObjectProperties
  (JNIEnv * env, jobject obj, jobject arg, jboolean direct)
{
	TRACE_JNI("askSuperObjectProperties");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	JTaxonomyActor<ObjectPropertyPolicy> actor(J);
	const TORoleExpr* p = getROORoleExpr(env,arg);
	PROCESS_QUERY ( J->K->getSupRoles(p,direct,actor) );
	return actor.getElements();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askSubObjectProperties
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Z)[[Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askSubObjectProperties
  (JNIEnv * env, jobject obj, jobject arg, jboolean direct)
{
	TRACE_JNI("askSubObjectProperties");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	JTaxonomyActor<ObjectPropertyPolicy> actor(J);
	const TORoleExpr* p = getROORoleExpr(env,arg);
	PROCESS_QUERY ( J->K->getSubRoles(p,direct,actor) );
	return actor.getElements();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askEquivalentObjectProperties
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)[Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askEquivalentObjectProperties
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("askEquivalentObjectProperties");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	JTaxonomyActor<ObjectPropertyPolicy> actor(J);
	PROCESS_QUERY ( J->K->getEquivalentRoles ( getROORoleExpr(env,arg), actor ) );
	return actor.getSynonyms();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askObjectPropertyDomain
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Z)[[Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askObjectPropertyDomain
  (JNIEnv * env, jobject obj, jobject arg, jboolean direct)
{
	TRACE_JNI("askObjectPropertyDomain");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	JTaxonomyActor<ClassPolicy> actor(J);
	PROCESS_QUERY ( J->K->getORoleDomain ( getROORoleExpr(env,arg), direct, actor ) );
	return actor.getElements();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askObjectPropertyRange
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Z)[[Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askObjectPropertyRange
  (JNIEnv * env, jobject obj, jobject arg, jboolean direct)
{
	TRACE_JNI("askObjectPropertyRange");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	JTaxonomyActor<ClassPolicy> actor(J);
	PROCESS_QUERY ( J->K->getRoleRange ( getROORoleExpr(env,arg), direct, actor ) );
	return actor.getElements();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isObjectPropertyFunctional
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isObjectPropertyFunctional
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("isObjectPropertyFunctional");
	TRACE_ARG(env,obj,arg);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isFunctional(getROORoleExpr(env,arg)) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isObjectPropertyInverseFunctional
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isObjectPropertyInverseFunctional
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("isObjectPropertyInverseFunctional");
	TRACE_ARG(env,obj,arg);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isInverseFunctional(getROORoleExpr(env,arg)) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isObjectPropertySymmetric
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isObjectPropertySymmetric
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("isObjectPropertySymmetric");
	TRACE_ARG(env,obj,arg);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isSymmetric(getROORoleExpr(env,arg)) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isObjectPropertyAsymmetric
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isObjectPropertyAsymmetric
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("isObjectPropertyAsymmetric");
	TRACE_ARG(env,obj,arg);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isAsymmetric(getROORoleExpr(env,arg)) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isObjectPropertyTransitive
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isObjectPropertyTransitive
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("isObjectPropertyTransitive");
	TRACE_ARG(env,obj,arg);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isTransitive(getROORoleExpr(env,arg)) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isObjectPropertyReflexive
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isObjectPropertyReflexive
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("isObjectPropertyReflexive");
	TRACE_ARG(env,obj,arg);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isReflexive(getROORoleExpr(env,arg)) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isObjectPropertyIrreflexive
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isObjectPropertyIrreflexive
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("isObjectPropertyIrreflexive");
	TRACE_ARG(env,obj,arg);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isIrreflexive(getROORoleExpr(env,arg)) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isObjectSubPropertyOf
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isObjectSubPropertyOf
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	TRACE_JNI("isObjectSubPropertyOf");
	TRACE_ARG(env,obj,arg1);
	TRACE_ARG(env,obj,arg2);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isSubRoles ( getROORoleExpr(env,arg1), getROORoleExpr(env,arg2) ) );
	return ret;
}
/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isObjectPropertyDisjointWith
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isObjectPropertyDisjointWith
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	TRACE_JNI("isObjectPropertyDisjointWith");
	TRACE_ARG(env,obj,arg1);
	TRACE_ARG(env,obj,arg2);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isDisjointRoles ( getROORoleExpr(env,arg1), getROORoleExpr(env,arg2) ) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isSubPropertyChainOf
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isSubPropertyChainOf
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("isSubPropertyChainOf");
	TRACE_ARG(env,obj,arg);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isSubChain(getROORoleExpr(env,arg)) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    arePropertiesDisjoint
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_arePropertiesDisjoint
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("arePropertiesDisjoint");
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isDisjointRoles() );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askSuperDataProperties
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;Z)[[Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askSuperDataProperties
  (JNIEnv * env, jobject obj, jobject arg, jboolean direct)
{
	TRACE_JNI("askSuperDataProperties");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	JTaxonomyActor<DataPropertyPolicy> actor(J);
	const TDRoleExpr* p = getRODRoleExpr(env,arg);
	PROCESS_QUERY ( J->K->getSupRoles(p,direct,actor) );
	return actor.getElements();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askSubDataProperties
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;Z)[[Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askSubDataProperties
  (JNIEnv * env, jobject obj, jobject arg, jboolean direct)
{
	TRACE_JNI("askSubDataProperties");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	JTaxonomyActor<DataPropertyPolicy> actor(J);
	const TDRoleExpr* p = getRODRoleExpr(env,arg);
	PROCESS_QUERY ( J->K->getSubRoles(p,direct,actor) );
	return actor.getElements();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askEquivalentDataProperties
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;)[Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askEquivalentDataProperties
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("askEquivalentDataProperties");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	JTaxonomyActor<DataPropertyPolicy> actor(J);
	PROCESS_QUERY ( J->K->getEquivalentRoles ( getRODRoleExpr(env,arg), actor ) );
	return actor.getSynonyms();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askDataPropertyDomain
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;Z)[[Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askDataPropertyDomain
  (JNIEnv * env, jobject obj, jobject arg, jboolean direct)
{
	TRACE_JNI("askDataPropertyDomain");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	JTaxonomyActor<ClassPolicy> actor(J);
	PROCESS_QUERY ( J->K->getDRoleDomain ( getRODRoleExpr(env,arg), direct, actor ) );
	return actor.getElements();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isDataPropertyFunctional
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isDataPropertyFunctional
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("isDataPropertyFunctional");
	TRACE_ARG(env,obj,arg);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isFunctional(getRODRoleExpr(env,arg)) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isDataSubPropertyOf
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isDataSubPropertyOf
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	TRACE_JNI("isDataSubPropertyOf");
	TRACE_ARG(env,obj,arg1);
	TRACE_ARG(env,obj,arg2);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isSubRoles ( getRODRoleExpr(env,arg1), getRODRoleExpr(env,arg2) ) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isObjectPropertyDisjointWith
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isDataPropertyDisjointWith
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	TRACE_JNI("isDataPropertyDisjointWith");
	TRACE_ARG(env,obj,arg1);
	TRACE_ARG(env,obj,arg2);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isDisjointRoles ( getRODRoleExpr(env,arg1), getRODRoleExpr(env,arg2) ) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askIndividualTypes
 * Signature: (Luk/ac/manchester/cs/factplusplus/IndividualPointer;Z)[[Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askIndividualTypes
  (JNIEnv * env, jobject obj, jobject arg, jboolean direct)
{
	TRACE_JNI("askIndividualTypes");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	JTaxonomyActor<ClassPolicy> actor(J);
	const TIndividualExpr* p = getROIndividualExpr(env,arg);
	PROCESS_QUERY ( J->K->getTypes(p,direct,actor) );
	return actor.getElements();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askObjectProperties
 * Signature: (Luk/ac/manchester/cs/factplusplus/IndividualPointer;)[Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askObjectProperties
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("askObjectProperties");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	ReasoningKernel::NamesVector Rs;
	PROCESS_QUERY ( J->K->getRelatedRoles ( getROIndividualExpr(env,arg), Rs, /*data=*/false, /*needI=*/false ) );
	std::vector<TExpr*> acc;
	for ( ReasoningKernel::NamesVector::const_iterator p = Rs.begin(), p_end = Rs.end(); p < p_end; ++p )
		acc.push_back(J->getOName((*p)->getName()));
	return J->buildArray ( acc, J->ObjectPropertyPointer );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askRelatedIndividuals
 * Signature: (Luk/ac/manchester/cs/factplusplus/IndividualPointer;Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)[Luk/ac/manchester/cs/factplusplus/IndividualPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askRelatedIndividuals
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	TRACE_JNI("askRelatedIndividuals");
	TRACE_ARG(env,obj,arg1);
	TRACE_ARG(env,obj,arg2);
	TJNICache* J = getJ(env,obj);
	ReasoningKernel::NamesVector Js;
	PROCESS_QUERY ( J->K->getRoleFillers ( getROIndividualExpr(env,arg1), getROORoleExpr(env,arg2), Js ) );
	std::vector<TExpr*> acc;
	for ( ReasoningKernel::NamesVector::const_iterator p = Js.begin(), p_end = Js.end(); p < p_end; ++p )
		acc.push_back(J->getIName((*p)->getName()));
	return J->buildArray ( acc, J->IndividualPointer );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askDataProperties
 * Signature: (Luk/ac/manchester/cs/factplusplus/IndividualPointer;)[Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askDataProperties
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("askDataProperties");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	ReasoningKernel::NamesVector Rs;
	PROCESS_QUERY ( J->K->getRelatedRoles ( getROIndividualExpr(env,arg), Rs, /*data=*/true, /*needI=*/false ) );
	std::vector<TExpr*> acc;
	for ( ReasoningKernel::NamesVector::const_iterator p = Rs.begin(), p_end = Rs.end(); p < p_end; ++p )
		acc.push_back(J->getDName((*p)->getName()));
	return J->buildArray ( acc, J->DataPropertyPointer );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askRelatedValues
 * Signature: (Luk/ac/manchester/cs/factplusplus/IndividualPointer;Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;)[Luk/ac/manchester/cs/factplusplus/DataValuePointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askRelatedValues
  (JNIEnv * env, jobject obj ATTR_UNUSED, jobject arg1 ATTR_UNUSED, jobject arg2 ATTR_UNUSED)
{
	TRACE_JNI("askRelatedValues");
	TRACE_ARG(env,obj,arg1);
	TRACE_ARG(env,obj,arg2);
	Throw ( env, "FaCT++ Kernel: unsupported operation 'askRelatedValues'" );
	return nullptr;
#if 0
	ReasoningKernel::NamesVector Js;
	PROCESS_SIMPLE_QUERY ( J->K->getRoleFillers ( getROIndividualExpr(env,arg1), getRODRoleExpr(env,arg2), Js ) );
	std::vector<TExpr*> acc;
	for ( ReasoningKernel::NamesVector::const_iterator p = Js.begin(), p_end = Js.end(); p < p_end; ++p )
		acc.push_back(new TExpr(TLexeme(DATAEXPR,const_cast<TNamedEntry*>(*p))));
	return J->buildArray ( acc, J->DataValuePointer );
#endif
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    hasDataPropertyRelationship
 * Signature: (Luk/ac/manchester/cs/factplusplus/IndividualPointer;Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;Luk/ac/manchester/cs/factplusplus/DataValuePointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_hasDataPropertyRelationship
  (JNIEnv * env, jobject obj ATTR_UNUSED, jobject arg1 ATTR_UNUSED, jobject arg2 ATTR_UNUSED, jobject arg3 ATTR_UNUSED)
{
	TRACE_JNI("hasDataPropertyRelationship");
	TRACE_ARG(env,obj,arg1);
	TRACE_ARG(env,obj,arg2);
	TRACE_ARG(env,obj,arg3);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isRelated ( getROIndividualExpr(env,arg1), getRODRoleExpr(env,arg2), getRODataValueExpr(env,arg3) ) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    hasObjectPropertyRelationship
 * Signature: (Luk/ac/manchester/cs/factplusplus/IndividualPointer;Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Luk/ac/manchester/cs/factplusplus/IndividualPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_hasObjectPropertyRelationship
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2, jobject arg3)
{
	TRACE_JNI("hasObjectPropertyRelationship");
	TRACE_ARG(env,obj,arg1);
	TRACE_ARG(env,obj,arg2);
	TRACE_ARG(env,obj,arg3);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isRelated ( getROIndividualExpr(env,arg1), getROORoleExpr(env,arg2), getROIndividualExpr(env,arg3) ) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isInstanceOf
 * Signature: (Luk/ac/manchester/cs/factplusplus/IndividualPointer;Luk/ac/manchester/cs/factplusplus/ClassPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isInstanceOf
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	TRACE_JNI("isInstanceOf");
	TRACE_ARG(env,obj,arg1);
	TRACE_ARG(env,obj,arg2);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isInstance ( getROIndividualExpr(env,arg1), getROConceptExpr(env,arg2) ) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askInstances
 * Signature: (Luk/ac/manchester/cs/factplusplus/ClassPointer;Z)[Luk/ac/manchester/cs/factplusplus/IndividualPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askInstances
  (JNIEnv * env, jobject obj, jobject arg, jboolean direct)
{
	TRACE_JNI("askInstances");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	JTaxonomyActor<IndividualPolicy</*plain=*/true> > actor(J);
	const TConceptExpr* p = getROConceptExpr(env,arg);
	PROCESS_QUERY ( direct ? J->K->getDirectInstances(p,actor) : J->K->getInstances(p,actor) );
	return actor.getElements();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askInstancesGrouped
 * Signature: (Luk/ac/manchester/cs/factplusplus/ClassPointer;Z)[[Luk/ac/manchester/cs/factplusplus/IndividualPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askInstancesGrouped
  (JNIEnv * env, jobject obj, jobject arg, jboolean direct)
{
	TRACE_JNI("askInstancesGrouped");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	JTaxonomyActor<IndividualPolicy</*plain=*/false> > actor(J);
	const TConceptExpr* p = getROConceptExpr(env,arg);
	PROCESS_QUERY ( direct ? J->K->getDirectInstances(p,actor) : J->K->getInstances(p,actor) );
	return actor.getElements();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    askSameAs
 * Signature: (Luk/ac/manchester/cs/factplusplus/IndividualPointer;)[Luk/ac/manchester/cs/factplusplus/IndividualPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_askSameAs
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("askSameAs");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	JTaxonomyActor<IndividualPolicy</*plain=*/true> > actor(J);
	PROCESS_QUERY ( J->K->getSameAs ( getROIndividualExpr(env,arg), actor ) );
	return actor.getSynonyms();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    isSameAs
 * Signature: (Luk/ac/manchester/cs/factplusplus/IndividualPointer;Luk/ac/manchester/cs/factplusplus/IndividualPointer;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_isSameAs
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	TRACE_JNI("isSameAs");
	TRACE_ARG(env,obj,arg1);
	TRACE_ARG(env,obj,arg2);
	bool ret = false;
	PROCESS_SIMPLE_QUERY ( ret=J->K->isSameIndividuals ( getROIndividualExpr(env,arg1), getROIndividualExpr(env,arg2) ) );
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataRelatedIndividuals
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;I)[Luk/ac/manchester/cs/factplusplus/IndividualPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataRelatedIndividuals
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2, jint op)
{
	TRACE_JNI("getDataRelatedIndividuals");
	TRACE_ARG(env,obj,arg1);
	TRACE_ARG(env,obj,arg2);
	TJNICache* J = getJ(env,obj);
	ReasoningKernel::NamesVector Js;
	PROCESS_QUERY ( J->K->getDataRelatedIndividuals ( getRODRoleExpr(env,arg1), getRODRoleExpr(env,arg2), op, Js ) );
	std::vector<TExpr*> acc;
	for ( ReasoningKernel::NamesVector::const_iterator p = Js.begin(), p_end = Js.end(); p < p_end; ++p )
		acc.push_back(J->getIName((*p)->getName()));
	return J->buildArray ( acc, J->IndividualPointer );

}


#undef PROCESS_QUERY
#undef PROCESS_SIMPLE_QUERY

#ifdef __cplusplus
}
#endif
