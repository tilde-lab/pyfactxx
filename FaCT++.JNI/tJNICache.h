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

#ifndef TJNICACHE_H
#define TJNICACHE_H

#include "JNISupport.h"

//------------------------------------------------------
// Keeps class names and field IDs for different Java classes in FaCT++ interface
//------------------------------------------------------

/// keep class, Node field and c'tor of an interface class
class TClassFieldMethodIDs
{
protected:	// members
		/// full qualifier for the array
	const char* ArrayClassName;

public:		// members
		/// class name
	jclass ClassID = nullptr;
		/// array class type
	jclass ArrayClassID = nullptr;
		/// c'tor type
	jmethodID CtorID = nullptr;
		/// 'node' field
	jfieldID NodeFID = nullptr;

public:		// interface
		/// c'tor: init class name
	explicit TClassFieldMethodIDs ( const char* arrayClassName )
		: ArrayClassName(arrayClassName)
		{}
		/// init values by class name
	void init ( JNIEnv* env )
	{
		jclass id = env->FindClass(ArrayClassName+1);
		if ( id == nullptr )
		{
			Throw ( env, "Can't get class for Pointer" );
			return;
		}
		ClassID = reinterpret_cast<jclass>(env->NewGlobalRef(id));

		id = env->FindClass(ArrayClassName);
		if ( id == nullptr )
		{
			Throw ( env, "Can't get class for [Pointer" );
			return;
		}
		ArrayClassID = reinterpret_cast<jclass>(env->NewGlobalRef(id));

		CtorID = env->GetMethodID ( ClassID, "<init>", "()V" );
		if ( CtorID == nullptr )
		{
			Throw ( env, "Can't get c'tor for Pointer" );
			return;
		}

		NodeFID = env->GetFieldID ( ClassID, "node", "J" );
		if ( NodeFID == nullptr )
		{
			Throw ( env, "Can't get 'node' field" );
			return;
		}
	}
	void fini ( JNIEnv* env )
	{
		env->DeleteGlobalRef(ClassID);
		env->DeleteGlobalRef(ArrayClassID);
	}
}; // TClassFieldMethodIDs

/// Keep all the classes cache together with ENV
class TJNICache
{
public:		// members
		/// copy of an ENV to work with a given kernel
	JNIEnv* env;
		/// link to the owned kernel
	ReasoningKernel* K = nullptr;
		/// attached Expression Manager
	TExpressionManager* EM = nullptr;
		/// cached IDs for known classes
	TClassFieldMethodIDs
		ClassPointer,
		IndividualPointer,
		ObjectPropertyPointer,
		DataPropertyPointer,
		DataTypePointer,
		DataTypeExpressionPointer,
		DataValuePointer,
		DataTypeFacet,
		NodePointer,
		AxiomPointer;

protected:	// methods
		/// init all the IDs
	void init ( void )
	{
		ClassPointer.init(env);
		IndividualPointer.init(env);
		ObjectPropertyPointer.init(env);
		DataPropertyPointer.init(env);
		DataTypePointer.init(env);
		DataTypeExpressionPointer.init(env);
		DataValuePointer.init(env);
		DataTypeFacet.init(env);
		NodePointer.init(env);
		AxiomPointer.init(env);
	}
		/// finalise all the IDs
	void fini ( void )
	{
		ClassPointer.fini(env);
		IndividualPointer.fini(env);
		ObjectPropertyPointer.fini(env);
		DataPropertyPointer.fini(env);
		DataTypePointer.fini(env);
		DataTypeExpressionPointer.fini(env);
		DataValuePointer.fini(env);
		DataTypeFacet.fini(env);
		NodePointer.fini(env);
		AxiomPointer.fini(env);
	}

		/// get an object out of an arbitrary pointer
	jobject retObject ( const void* pointer, const TClassFieldMethodIDs& ID )
	{
		if ( unlikely(pointer == nullptr) )
		{
			Throw ( env, "Incorrect operand by FaCT++ Kernel" );
			return nullptr;
		}

		// create an object to return
		jobject obj = env->NewObject ( ID.ClassID, ID.CtorID );

		if ( unlikely(obj == nullptr) )
			Throw ( env, "Can't create Pointer object" );
		else	// set the return value
			env->SetLongField ( obj, ID.NodeFID, (jlong)pointer );

		return obj;
	}

public:		// members
		/// ctor: init all the IDs
	explicit TJNICache ( JNIEnv* e )
		: env(e)
		, ClassPointer("[Luk/ac/manchester/cs/factplusplus/ClassPointer;")
		, IndividualPointer("[Luk/ac/manchester/cs/factplusplus/IndividualPointer;")
		, ObjectPropertyPointer("[Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;")
		, DataPropertyPointer("[Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;")
		, DataTypePointer("[Luk/ac/manchester/cs/factplusplus/DataTypePointer;")
		, DataTypeExpressionPointer("[Luk/ac/manchester/cs/factplusplus/DataTypeExpressionPointer;")
		, DataValuePointer("[Luk/ac/manchester/cs/factplusplus/DataValuePointer;")
		, DataTypeFacet("[Luk/ac/manchester/cs/factplusplus/DataTypeFacet;")
		, NodePointer("[Luk/ac/manchester/cs/factplusplus/NodePointer;")
		, AxiomPointer("[Luk/ac/manchester/cs/factplusplus/AxiomPointer;")
		{ init(); }
		/// d'tor: release all names
	~TJNICache() { /*fini();*/ }

		/// switch the env to a new one E
	void reset ( JNIEnv* e )
	{
//		fini();
		env = e;
		init();
	}

		/// object for class expression
	jobject Class ( TConceptExpr* expr ) { return retObject ( expr, ClassPointer ); }
		/// object for individual expression
	jobject Individual ( TIndividualExpr* expr ) { return retObject ( expr, IndividualPointer ); }
	jobject ObjectProperty ( TORoleExpr* expr ) { return retObject ( expr, ObjectPropertyPointer ); }
	jobject ObjectComplex ( TORoleComplexExpr* expr ) { return retObject ( expr, ObjectPropertyPointer ); }
	jobject DataProperty ( TDRoleExpr* expr ) { return retObject ( expr, DataPropertyPointer ); }
	jobject DataType ( TDataExpr* expr ) { return retObject ( expr, DataTypePointer ); }
	jobject DataTypeExpression ( TDataExpr* expr ) { return retObject ( expr, DataTypeExpressionPointer ); }
	jobject DataValue ( TDataValueExpr* expr ) { return retObject ( expr, DataValuePointer ); }
	jobject Facet ( TFacetExpr* expr ) { return retObject ( expr, DataTypeFacet ); }
	jobject Node ( TCGNode* node ) { return retObject ( node, NodePointer ); }
	jobject Axiom ( TDLAxiom* axiom ) { return retObject ( axiom, AxiomPointer ); }

		/// create vector of Java objects defined by ID from given VEC
	template <typename T>
	jobjectArray buildArray ( const std::vector<T*>& vec, const TClassFieldMethodIDs& ID )
	{
		jobjectArray ret = env->NewObjectArray ( (jsize)vec.size(), ID.ClassID, nullptr );
		for ( unsigned int i = 0; i < vec.size(); ++i )
			env->SetObjectArrayElement ( ret, (jsize)i, retObject ( vec[i], ID ) );
		return ret;
	}

	// get expressions for the names in the unified way

		/// get expression for the class name
	TConceptExpr* getCName ( const std::string& name ) { return EM->Concept(name); }
		/// get expression for the individual name
	TIndividualExpr* getIName ( const std::string& name ) { return EM->Individual(name); }
		/// get expression for the object property name
	TORoleExpr* getOName ( const std::string& name ) { return EM->ObjectRole(name); }
		/// get expression for the data property name
	TDRoleExpr* getDName ( const std::string& name ) { return EM->DataRole(name); }
}; // TJNICache

/// get JNI cache by the env and obj
inline
TJNICache* getJ ( JNIEnv* env, jobject obj )
{
	TJNICache* J = getK(env,obj)->getJNICache();
	if ( unlikely(J->env != env) )
		J->reset(env);
	return J;
}

#endif
