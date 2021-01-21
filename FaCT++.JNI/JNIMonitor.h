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

#ifndef JNIMONITOR_H
#define JNIMONITOR_H

#include "tProgressMonitor.h"
#include "JNISupport.h"

class JNIProgressMonitor: public TProgressMonitor
{
protected:
	JNIEnv* env;
	jobject javaMonitor;
	jmethodID sCS, nC, sF, iC;
public:
		/// c'tor: remember object and fill in methods to call
	JNIProgressMonitor ( JNIEnv* Env, jobject obj )
		: TProgressMonitor()
		, env(Env)
	{
		javaMonitor = env->NewGlobalRef(obj);
		jclass cls = env->GetObjectClass(obj);
		if ( cls == nullptr )
			Throw ( env, "Can't get class of ProgressMonitor object" );
		sCS = env->GetMethodID ( cls, "setClassificationStarted", "(I)V" );
		if ( sCS == nullptr )
			Throw ( env, "Can't get method setClassificationStarted" );
		nC = env->GetMethodID ( cls, "nextClass", "()V" );
		if ( nC == nullptr )
			Throw ( env, "Can't get method nextClass" );
		sF = env->GetMethodID ( cls, "setFinished", "()V" );
		if ( sF == nullptr )
			Throw ( env, "Can't get method setFinished" );
		iC = env->GetMethodID ( cls, "isCancelled", "()Z" );
		if ( iC == nullptr )
			Throw ( env, "Can't get method isCancelled" );
	}
		/// d'tor: allow JRE to delete object
	~JNIProgressMonitor() override { env->DeleteGlobalRef(javaMonitor); }

		/// informs about beginning of classification with number of concepts to be classified
	void setClassificationStarted ( unsigned int nConcepts ) override
		{ env->CallVoidMethod ( javaMonitor, sCS, nConcepts ); }
		/// informs about beginning of classification of a given CONCEPT
	void nextClass ( void ) override { env->CallVoidMethod ( javaMonitor, nC ); }
		/// informs that the reasoning is done
	void setFinished ( void ) override { env->CallVoidMethod ( javaMonitor, sF ); }
		/// @return true iff reasoner have to be stopped
	bool isCancelled ( void ) override { return env->CallBooleanMethod ( javaMonitor, iC ); }
}; // JNIProgressMonitor

#endif
