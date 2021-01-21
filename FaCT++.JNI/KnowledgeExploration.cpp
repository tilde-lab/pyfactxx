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

// this file contains implementation of knowledge exploration methods of FaCT++ JNI interface

#include "uk_ac_manchester_cs_factplusplus_FaCTPlusPlus.h"
#include "Kernel.h"
#include "tJNICache.h"

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    buildCompletionTree
 * Signature: (Luk/ac/manchester/cs/factplusplus/ClassPointer;)Luk/ac/manchester/cs/factplusplus/NodePointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_buildCompletionTree
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("buildCompletionTree");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	return J->Node(J->K->buildCompletionTree(getROConceptExpr(env,arg)));
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getObjectNeighbours
 * Signature: (Luk/ac/manchester/cs/factplusplus/NodePointer;Z)[Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getObjectNeighbours__Luk_ac_manchester_cs_factplusplus_NodePointer_2Z
  (JNIEnv * env, jobject obj, jobject arg, jboolean flag)
{
	TRACE_JNI("getObjectNeighbours");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	ReasoningKernel::TCGRoleSet Set;
	J->K->getObjectRoles ( getRONode(env,arg), Set, /*onlyDet=*/flag, /*needIncoming=*/false );
	std::vector<TORoleExpr*> ret;
	for (const TDLRoleExpression* role : Set)
		ret.push_back(dynamic_cast<TORoleExpr*>(role));
	return J->buildArray ( ret, J->ObjectPropertyPointer );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataNeighbours
 * Signature: (Luk/ac/manchester/cs/factplusplus/NodePointer;Z)[Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataNeighbours__Luk_ac_manchester_cs_factplusplus_NodePointer_2Z
  (JNIEnv * env, jobject obj, jobject arg, jboolean flag)
{
	TRACE_JNI("getDataNeighbours");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	ReasoningKernel::TCGRoleSet Set;
	J->K->getDataRoles ( getRONode(env,arg), Set, /*onlyDet=*/flag );
	std::vector<TDRoleExpr*> ret;
	for (const TDLRoleExpression* role : Set)
		ret.push_back(dynamic_cast<TDRoleExpr*>(role));
	return J->buildArray ( ret, J->DataPropertyPointer );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getObjectNeighbours
 * Signature: (Luk/ac/manchester/cs/factplusplus/NodePointer;Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)[Luk/ac/manchester/cs/factplusplus/NodePointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getObjectNeighbours__Luk_ac_manchester_cs_factplusplus_NodePointer_2Luk_ac_manchester_cs_factplusplus_ObjectPropertyPointer_2
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2 )
{
	TRACE_JNI("getObjectNeighbours");
	TRACE_ARG(env,obj,arg1);
	TRACE_ARG(env,obj,arg2);
	TJNICache* J = getJ(env,obj);
	ReasoningKernel::TCGNodeVec Vec;
	J->K->getNeighbours ( getRONode(env,arg1), getROORoleExpr(env,arg2), Vec );
	return J->buildArray ( Vec, J->NodePointer );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataNeighbours
 * Signature: (Luk/ac/manchester/cs/factplusplus/NodePointer;Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;)[Luk/ac/manchester/cs/factplusplus/NodePointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataNeighbours__Luk_ac_manchester_cs_factplusplus_NodePointer_2Luk_ac_manchester_cs_factplusplus_DataPropertyPointer_2
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2 )
{
	TRACE_JNI("getDataNeighbours");
	TRACE_ARG(env,obj,arg1);
	TRACE_ARG(env,obj,arg2);
	TJNICache* J = getJ(env,obj);
	ReasoningKernel::TCGNodeVec Vec;
	J->K->getNeighbours ( getRONode(env,arg1), getRODRoleExpr(env,arg2), Vec );
	return J->buildArray ( Vec, J->NodePointer );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getObjectLabel
 * Signature: (Luk/ac/manchester/cs/factplusplus/NodePointer;Z)[Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getObjectLabel
  (JNIEnv * env, jobject obj, jobject arg, jboolean flag)
{
	TRACE_JNI("getObjectLabel");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	ReasoningKernel::TCGItemVec Vec;
	J->K->getLabel ( getRONode(env,arg), Vec, /*onlyDet=*/flag );
	return J->buildArray ( Vec, J->ClassPointer );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataLabel
 * Signature: (Luk/ac/manchester/cs/factplusplus/NodePointer;Z)[Luk/ac/manchester/cs/factplusplus/DataTypePointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataLabel
  (JNIEnv * env, jobject obj, jobject arg, jboolean flag)
{
	TRACE_JNI("getDataLabel");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	ReasoningKernel::TCGItemVec Vec;
	J->K->getLabel ( getRONode(env,arg), Vec, /*onlyDet=*/flag );
	return J->buildArray ( Vec, J->DataTypeExpressionPointer );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getBlocker
 * Signature: (Luk/ac/manchester/cs/factplusplus/NodePointer;)Luk/ac/manchester/cs/factplusplus/NodePointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getBlocker
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("getBlocker");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	return J->Node(J->K->getBlocker(getRONode(env,arg)));
}

#ifdef __cplusplus
}
#endif
