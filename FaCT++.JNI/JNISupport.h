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

#ifndef JNISUPPORT_H
#define JNISUPPORT_H

#include <jni.h>

// switch tracing on
//#define JNI_TRACING

#ifdef ENABLE_CHECKING
#	define JNI_TRACING
#endif

#ifdef JNI_TRACING
#	include <iostream>
#	define TRACE_JNI(func) std::cerr << "JNI " << env << " Kernel " << getK(env,obj) << " Call " << func << "\n"
#	define TRACE_ARG(env,obj,arg) do {	\
		std::cerr << " arg ";			\
		TExpr* expr=getROExpr(env,arg);	\
		const TNamedEntity* ne = dynamic_cast<const TNamedEntity*>(expr); \
		if ( ne != nullptr ) std::cerr << ne->getName();	\
		std::cerr << "\n"; } while (false)
#	define TRACE_STR(env,str) std::cerr << " string arg " << JString(env,str)() << "\n"
#else
#	define TRACE_JNI(func) (void)NULL
#	define TRACE_ARG(env,obj,arg) (void)NULL
#	define TRACE_STR(env,str) (void)NULL
#endif

//-------------------------------------------------------------
// Expression typedefs
//-------------------------------------------------------------

	/// general expression
typedef ReasoningKernel::TExpr TExpr;
	/// concept expression
typedef ReasoningKernel::TConceptExpr TConceptExpr;
	/// individual expression
typedef ReasoningKernel::TIndividualExpr TIndividualExpr;
	/// role expression
typedef ReasoningKernel::TRoleExpr TRoleExpr;
	/// object role complex expression (including role chains and projections)
typedef ReasoningKernel::TORoleComplexExpr TORoleComplexExpr;
	/// object role expression
typedef ReasoningKernel::TORoleExpr TORoleExpr;
	/// data role expression
typedef ReasoningKernel::TDRoleExpr TDRoleExpr;
	/// data expression
typedef ReasoningKernel::TDataExpr TDataExpr;
	/// data type expression
typedef ReasoningKernel::TDataTypeExpr TDataTypeExpr;
	/// data value expression
typedef ReasoningKernel::TDataValueExpr TDataValueExpr;
	/// data facet expression
typedef const TDLFacetExpression TFacetExpr;
	/// completion tree node
typedef const ReasoningKernel::TCGNode TCGNode;

//-------------------------------------------------------------
// Support functions
//-------------------------------------------------------------

// class for easy dealing with Java strings
class JString
{
private:
	JNIEnv* env;
	jstring str;
	const char* buf;

public:
	JString ( JNIEnv* e, jstring s ) : env(e), str(s) { buf = env->GetStringUTFChars(str, nullptr); }
	// prevent copy
	JString ( const JString& ) = delete;
	JString& operator = ( const JString& ) = delete;
	~JString() { env->ReleaseStringUTFChars(str, buf); }
	const char* operator() ( void ) const { return buf; }
}; // JString

/// throw exception with a given signature
inline
void ThrowExc ( JNIEnv * env, const char* reason, const char* className )
{
	jclass cls = env->FindClass(className);
	env->ThrowNew ( cls, reason );
}

/// throw exception with an empty c'tor and a given signature
inline
void ThrowExc ( JNIEnv * env, const char* className )
{
	jclass cls = env->FindClass(className);
	jmethodID CtorID = env->GetMethodID ( cls, "<init>", "()V" );
	jobject obj = env->NewObject ( cls, CtorID );
	env->Throw((jthrowable)obj);
}

/// throw general Java exception
inline
void ThrowGen ( JNIEnv* env, const char* reason )
{
	ThrowExc ( env, reason, "Ljava/lang/Exception;" );
}

/// throw general FaCT++ exception
inline
void Throw ( JNIEnv* env, const char* reason )
{
	ThrowExc ( env, reason, "Lorg/semanticweb/owlapi/reasoner/ReasonerInternalException;" );
}

/// throw Inconsistent Ontology exception
inline
void ThrowICO ( JNIEnv* env )
{
	ThrowExc ( env, "Lorg/semanticweb/owlapi/reasoner/InconsistentOntologyException;" );
}

/// throw CR for non-simple role exception
inline
void ThrowNSR ( JNIEnv* env, const char* reason )
{
	std::string msg ("Non-simple object property '");
	msg += reason;
	msg += "' is used as a simple one";
	ThrowExc ( env, msg.c_str(), "Lorg/semanticweb/owlapi/reasoner/OWLReasonerRuntimeException;");

// not correct because does not have enough information
//	jclass ceNotProfile = env->FindClass("Lorg/semanticweb/owlapi/reasoner/ClassExpressionNotInProfileException;");
//	if ( ceNotProfile == 0 )
//	{
//		Throw ( env, "Can't get class for Pointer" );
//		return ;
//	}
//
//	jmethodID CtorID = env->GetMethodID ( ceNotProfile, "<init>", "(Lorg/semanticweb/owlapi/model/OWLClassExpression;Lorg/semanticweb/owlapi/profiles/OWLProfile;)V" );
//
//	// create an object to return
//	jobject obj = env->NewObject ( ceNotProfile, CtorID, nullptr, nullptr );
//	env->Throw((jthrowable)obj);
}

/// throw Role Inclusion Cycle exception
inline
void ThrowRIC ( JNIEnv* env, const char* reason )
{
	ThrowExc ( env, reason, "Lorg/semanticweb/owlapi/reasoner/AxiomNotInProfileException;" );
}

/// throw Role Inclusion Cycle exception
inline
void ThrowTO ( JNIEnv* env )
{
	ThrowExc ( env, "Lorg/semanticweb/owlapi/reasoner/TimeOutException;" );
}

/// field for Kernel's ID
extern "C" jfieldID KernelFID;

/// get Kernel local to given object
// as a side effect sets up curKernel
inline
ReasoningKernel* getK ( JNIEnv * env, jobject obj )
{
	jlong id = env->GetLongField ( obj, KernelFID );

	// ID corresponds to a pointer -- should not be NULL
	if ( unlikely(id == 0) )
		Throw ( env, "Uninitialized FaCT++ kernel found" );

	return (ReasoningKernel*)id;
}

// helper for getTree which extracts a JLONG from a given object
inline
jlong getPointer ( JNIEnv * env, jobject obj )
{
	jclass classThis = env->GetObjectClass(obj);

	if ( unlikely(classThis == 0) )
	{
		Throw ( env, "Can't get class of 'this'" );
		return 0;
	}

	jfieldID fid = env->GetFieldID ( classThis, "node", "J" );

	if ( unlikely(fid == 0) )
	{
		Throw ( env, "Can't get 'node' field" );
		return 0;
	}

	return env->GetLongField ( obj, fid );
}

// macro to expand into the accessor function that transforms pointer into appropriate type
#define ACCESSOR(Name)	\
inline T ## Name* get ## Name ( JNIEnv * env, jobject obj ) {	\
	return dynamic_cast<T ## Name*>((TExpr*)getPointer(env,obj)); }

// accessors for different expression types
ACCESSOR(Expr)
ACCESSOR(ConceptExpr)
ACCESSOR(IndividualExpr)
ACCESSOR(RoleExpr)
ACCESSOR(ORoleComplexExpr)
ACCESSOR(ORoleExpr)
ACCESSOR(DRoleExpr)
ACCESSOR(DataExpr)
ACCESSOR(DataValueExpr)
ACCESSOR(FacetExpr)

// ACCESSOR(DataTypeExpr) -- doesn't work as DTE is not a const typedef
inline TDataTypeExpr* getDataTypeExpr ( JNIEnv * env, jobject obj )
	{ return const_cast<TDataTypeExpr*>(dynamic_cast<const TDataTypeExpr*>((TExpr*)getPointer(env,obj))); }

#undef ACCESSOR

// macro to expand into the RO accessor function that transforms pointer into appropriate type
#define ACCESSOR(Name)	\
inline const T ## Name* getRO ## Name ( JNIEnv * env, jobject obj ) {	\
	return dynamic_cast<const T ## Name*>((const TExpr*)getPointer(env,obj)); }

// accessors for different expression types
ACCESSOR(Expr)
ACCESSOR(ConceptExpr)
ACCESSOR(IndividualExpr)
ACCESSOR(RoleExpr)
ACCESSOR(ORoleComplexExpr)
ACCESSOR(ORoleExpr)
ACCESSOR(DRoleExpr)
ACCESSOR(DataExpr)
ACCESSOR(DataTypeExpr)
ACCESSOR(DataValueExpr)
ACCESSOR(FacetExpr)

// ACCESSOR(NodeExpr) -- doesn't work as the type is not a TExpr's descendant
inline TCGNode* getRONode ( JNIEnv * env, jobject obj )
	{ return (TCGNode*)getPointer(env,obj); }

#undef ACCESSOR

inline
TDLAxiom* getAxiom ( JNIEnv * env, jobject obj )
{
	return (TDLAxiom*)getPointer(env,obj);
}

inline
TCGNode* getNode ( JNIEnv * env, jobject obj )
{
	return (TCGNode*)getPointer(env,obj);
}

#endif
