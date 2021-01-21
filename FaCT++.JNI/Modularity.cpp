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

// this file contains implementation of modularity-related methods of FaCT++ JNI interface

#include "uk_ac_manchester_cs_factplusplus_FaCTPlusPlus.h"
#include "Kernel.h"
#include "tJNICache.h"

/// translate int values of Java interface into ModuleType enum
inline enum ModuleType
moduleTypeByInt ( jint moduleType )
{
	return moduleType == 0 ? M_BOT : moduleType == 1 ? M_TOP : M_STAR;
}

/// translate int values of Java interface into ModuleMethod enum
inline enum ModuleMethod
moduleMethodByInt ( jint moduleMethod )
{
	return moduleMethod == 0 ? SYN_LOC_STD : moduleMethod == 1 ? SYN_LOC_COUNT : SEM_LOC;
}

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getAtomicDecompositionSize
 * Signature: (ZI)I
 */
JNIEXPORT jint JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getAtomicDecompositionSize
  (JNIEnv * env, jobject obj, jint moduleMethod, jint moduleType)
{
	TRACE_JNI("getAtomicDecompositionSize");
	return getK(env,obj)->getAtomicDecompositionSize ( moduleMethodByInt(moduleMethod), moduleTypeByInt(moduleType) );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getAtomAxioms
 * Signature: (I)[Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getAtomAxioms
  (JNIEnv * env, jobject obj, jint index)
{
	TRACE_JNI("getAtomAxioms");
	TJNICache* J = getJ(env,obj);
	return J->buildArray ( J->K->getAtomAxioms(index), J->AxiomPointer );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getAtomModule
 * Signature: (I)[Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getAtomModule
  (JNIEnv * env, jobject obj, jint index)
{
	TRACE_JNI("getAtomModule");
	TJNICache* J = getJ(env,obj);
	return J->buildArray ( J->K->getAtomModule(index), J->AxiomPointer );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getAtomDependents
 * Signature: (I)[I
 */
JNIEXPORT jintArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getAtomDependents
  (JNIEnv * env, jobject obj, jint index)
{
	TRACE_JNI("getAtomDependents");
	const TOntologyAtom::AtomSet aSet = getK(env,obj)->getAtomDependents(index);
	size_t sz = aSet.size();
	jint* buf = new jint[sz];
	TOntologyAtom::AtomSet::const_iterator p = aSet.begin();
	for ( size_t i = 0; i < sz; ++i, ++p )
		buf[i] = (*p)->getId();
	jint size = (jint)sz;
	jintArray ret = env->NewIntArray(size);
	env->SetIntArrayRegion ( ret, 0, size, buf );
	delete [] buf;
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getLocCheckNumber
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getLocCheckNumber
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("getLocCheckNumber");
	TJNICache* J = getJ(env,obj);
	return J->K->getLocCheckNumber();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getModule
 * Signature: (ZI)[Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getModule
  (JNIEnv * env, jobject obj, jint moduleMethod, jint moduleType)
{
	TRACE_JNI("getModule");
	TJNICache* J = getJ(env,obj);
	return J->buildArray ( J->K->getModule(moduleMethodByInt(moduleMethod),moduleTypeByInt(moduleType)), J->AxiomPointer );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getNonLocal
 * Signature: (ZI)[Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getNonLocal
  (JNIEnv * env, jobject obj, jint moduleMethod, jint moduleType)
{
	TRACE_JNI("getNonLocal");
	TJNICache* J = getJ(env,obj);
	return J->buildArray ( J->K->getNonLocal(moduleMethodByInt(moduleMethod),moduleTypeByInt(moduleType)), J->AxiomPointer );
}

#ifdef __cplusplus
}
#endif
