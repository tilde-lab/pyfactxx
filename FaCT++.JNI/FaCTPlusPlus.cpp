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

#include <iostream>

#include "uk_ac_manchester_cs_factplusplus_FaCTPlusPlus.h"
#include "Kernel.h"
#include "tJNICache.h"
#include "JNIMonitor.h"
#include "configure.h"
#include "MemoryStat.h"

#ifdef __cplusplus
extern "C" {
#endif

//-------------------------------------------------------------
// Different fields/method IDs and their setup
//-------------------------------------------------------------

/// field for Kernel's ID
jfieldID KernelFID;

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    initMethodsFieldsIDs
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_initMethodsFieldsIDs
  (JNIEnv * env, jclass cls)
{
	KernelFID = env->GetFieldID ( cls, "KernelId", "J" );

	if ( KernelFID == nullptr )
	{
		Throw ( env, "Can't get 'KernelId' field" );
		return;
	}
	MemoryStatistics MS("init JNI");
}

//-------------------------------------------------------------
// Kernel management (like newKB/curKB/releaseKB)
//-------------------------------------------------------------

/// try to load configuration from FILE; @return false if successful
bool loadConfiguration ( ReasoningKernel* K, const char* file )
{
	Configuration Config;

	if ( Config.Load(file) )
		return true;
	if ( K->getOptions()->initByConfigure ( Config, "Tuning" ) )
		return true;
	std::cerr << "Using options from file " << file << "\n";
	return false;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    initKernel
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_initKernel
  (JNIEnv * env, jobject obj)
{
	MemoryStatistics MS("Create Reasoner");
	// create new kernel and save it in an FaCTPlusPlus object
	ReasoningKernel* Kernel = new ReasoningKernel();
	env->SetLongField ( obj, KernelFID, (jlong)Kernel );

	// try to load configuration
	if ( loadConfiguration ( Kernel, ".fpp-options" ) )
		loadConfiguration ( Kernel, "fpp-options.txt" );

	// init dumping of the ontology
	Kernel->setDumpOntology(Kernel->getOptions()->getBool("dumpOntology"));
	// init incremental reasoning
	Kernel->setUseIncrementalReasoning(Kernel->getOptions()->getBool("useIncrementalReasoning"));

	// setup JNI cache
	TJNICache* J = new TJNICache(env);
	Kernel->setJNICache(J);
	J->K = Kernel;
	J->EM = Kernel->getExpressionManager();

	TRACE_JNI("initKernel");

	if ( USE_LOGGING )
	{
		// initialize LeveLogger
		//LLM.initLogger ( 20, "reasoning.log" );
	}
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    deleteKernel
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_deleteKernel
  (JNIEnv * env, jobject obj)
{
	MemoryStatistics MS("Delete Reasoner");
	TRACE_JNI("deleteKernel");
	ReasoningKernel* Kernel = getK(env,obj);
	delete Kernel->getJNICache();
	delete Kernel;
	// set to NULL
	env->SetLongField ( obj, KernelFID, 0 );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    clearKernel
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_clearKernel
  (JNIEnv * env, jobject obj)
{
	MemoryStatistics MS("Clear Reasoner");
	TRACE_JNI("clearKernel");
	getK(env,obj)->clearKB();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    setTopBottomPropertyNames
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_setTopBottomPropertyNames
  (JNIEnv * env, jobject obj, jstring ton, jstring bon, jstring tdn, jstring bdn)
{
	TRACE_JNI("setTopBottomPropertyNames");
	TRACE_STR(env,ton);
	TRACE_STR(env,bon);
	TRACE_STR(env,tdn);
	TRACE_STR(env,bdn);
	JString topObjectName(env,ton);
	JString botObjectName(env,bon);
	JString topDataName(env,tdn);
	JString botDataName(env,bdn);
	getK(env,obj)->setTopBottomRoleNames ( topObjectName(), botObjectName(), topDataName(), botDataName() );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    setOperationTimeout
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_setOperationTimeout
(JNIEnv * env, jobject obj, jlong delay)
{
	TRACE_JNI("setOperationTimeout");
	getK(env,obj)->setOperationTimeout(delay > 0 ? static_cast<unsigned long>(delay) : 0);
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    setFreshEntityPolicy
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_setFreshEntityPolicy
  (JNIEnv * env, jobject obj, jboolean value)
{
	TRACE_JNI("setFreshEntityPolicy");
	getK(env,obj)->setUseUndefinedNames(value);
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    setProgressMonitor
 * Signature: (Luk/ac/manchester/cs/factplusplus/FaCTPlusPlusProgressMonitor;)V
 */
JNIEXPORT void JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_setProgressMonitor
  (JNIEnv * env, jobject obj, jobject monitor)
{
	TRACE_JNI("setProgressMonitor");
	getK(env,obj)->setProgressMonitor ( new JNIProgressMonitor ( env, monitor ) );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    startChanges
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_startChanges
  (JNIEnv *, jobject)
{
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    endChanges
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_endChanges
  (JNIEnv * env ATTR_UNUSED, jobject obj ATTR_UNUSED)
{
	TRACE_JNI("endChanges");
	// do nothing
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    needTracing
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_needTracing
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("needTracing");
	getK(env,obj)->needTracing();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getTrace
 * Signature: ()[Luk/ac/manchester/cs/factplusplus/AxiomPointer;
 */
JNIEXPORT jobjectArray JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getTrace
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("getTrace");
	TJNICache* J = getJ(env,obj);
	return J->buildArray ( J->K->getTrace(), J->AxiomPointer );
}

#ifdef __cplusplus
}
#endif
