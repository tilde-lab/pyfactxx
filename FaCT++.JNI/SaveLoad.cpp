/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2013-2015 Dmitry Tsarkov and The University of Manchester
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

// this file contains implementation of saving and restoring of an internal state of FaCT++ JNI interface

#include "uk_ac_manchester_cs_factplusplus_FaCTPlusPlus.h"
#include "Kernel.h"
#include "tJNICache.h"

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    checkSaveLoadContext
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_checkSaveLoadContext
  (JNIEnv * env, jobject obj, jstring str)
{
	TRACE_JNI("checkSaveLoadContext");
	TRACE_STR(env,str);
	JString* pContext = str ? new JString(env,str) : nullptr;
	const char* context = pContext ? (*pContext)() : "";
	bool ret = getK(env,obj)->checkSaveLoadContext(context);
	delete pContext;
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    setSaveLoadContext
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_setSaveLoadContext
  (JNIEnv * env, jobject obj, jstring str)
{
	TRACE_JNI("setSaveLoadContext");
	TRACE_STR(env,str);
	JString* pContext = str ? new JString(env,str) : nullptr;
	const char* context = pContext ? (*pContext)() : "";
	bool ret = getK(env,obj)->setSaveLoadContext(context);
	delete pContext;
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    clearSaveLoadContext
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_clearSaveLoadContext
  (JNIEnv * env, jobject obj, jstring str)
{
	TRACE_JNI("clearSaveLoadContext");
	TRACE_STR(env,str);
	JString* pContext = str ? new JString(env,str) : nullptr;
	const char* context = pContext ? (*pContext)() : "";
	bool ret = getK(env,obj)->clearSaveLoadContext(context);
	delete pContext;
	return ret;
}

#ifdef __cplusplus
}
#endif
