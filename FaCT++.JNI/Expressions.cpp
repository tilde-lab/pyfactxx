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

// this file contains implementation of DL expression-related methods of FaCT++ JNI interface

#include <sstream>

#include "uk_ac_manchester_cs_factplusplus_FaCTPlusPlus.h"
#include "Kernel.h"
#include "tJNICache.h"

#ifdef __cplusplus
extern "C" {
#endif

//-------------------------------------------------------------
// Concept/role/datatype language
//-------------------------------------------------------------

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getThing
 * Signature: ()Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getThing
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("getThing");
	TJNICache* J = getJ(env,obj);
	return J->Class(J->EM->Top());
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getNothing
 * Signature: ()Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getNothing
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("getNothing");
	TJNICache* J = getJ(env,obj);
	return J->Class(J->EM->Bottom());
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getNamedClass
 * Signature: (Ljava/lang/String;)Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getNamedClass
  (JNIEnv * env, jobject obj, jstring str)
{
	TRACE_JNI("getNamedClass");
	TRACE_STR(env,str);
	TJNICache* J = getJ(env,obj);
	JString name(env,str);
	jobject ret = nullptr;
	try
	{
		ret = J->Class(J->getCName(name()));
	}
	catch (const EFPPCantRegName&)
	{
		Throw ( env, "FaCT++ Kernel: Can not register new class name" );
	}
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getTopObjectProperty
 * Signature: ()Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getTopObjectProperty
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("getTopObjectProperty");
	TJNICache* J = getJ(env,obj);
	return J->ObjectProperty(J->getOName("http://www.w3.org/2002/07/owl#topObjectProperty"));
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getBottomObjectProperty
 * Signature: ()Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getBottomObjectProperty
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("getBottomObjectProperty");
	TJNICache* J = getJ(env,obj);
	return J->ObjectProperty(J->getOName("http://www.w3.org/2002/07/owl#bottomObjectProperty"));
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getObjectProperty
 * Signature: (Ljava/lang/String;)Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getObjectProperty
  (JNIEnv * env, jobject obj, jstring str)
{
	TRACE_JNI("getObjectProperty");
	TRACE_STR(env,str);
	TJNICache* J = getJ(env,obj);
	JString name(env,str);
	jobject ret = nullptr;
	try
	{
		ret = J->ObjectProperty(J->getOName(name()));
	}
	catch (const EFPPCantRegName&)
	{
		Throw ( env, "FaCT++ Kernel: Can not register new object property name" );
	}
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getTopDataProperty
 * Signature: ()Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getTopDataProperty
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("getTopDataProperty");
	TJNICache* J = getJ(env,obj);
	return J->DataProperty(J->getDName("http://www.w3.org/2002/07/owl#topDataProperty"));
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getBottomDataProperty
 * Signature: ()Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getBottomDataProperty
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("getBottomDataProperty");
	TJNICache* J = getJ(env,obj);
	return J->DataProperty(J->getDName("http://www.w3.org/2002/07/owl#bottomDataProperty"));
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataProperty
 * Signature: (Ljava/lang/String;)Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataProperty
  (JNIEnv * env, jobject obj, jstring str)
{
	TRACE_JNI("getDataProperty");
	TRACE_STR(env,str);
	TJNICache* J = getJ(env,obj);
	JString name(env,str);
	jobject ret = nullptr;
	try
	{
		ret = J->DataProperty(J->getDName(name()));
	}
	catch (const EFPPCantRegName&)
	{
		Throw ( env, "FaCT++ Kernel: Can not register new data property name" );
	}
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getIndividual
 * Signature: (Ljava/lang/String;)Luk/ac/manchester/cs/factplusplus/IndividualPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getIndividual
  (JNIEnv * env, jobject obj, jstring str)
{
	TRACE_JNI("getIndividual");
	TRACE_STR(env,str);
	TJNICache* J = getJ(env,obj);
	JString name(env,str);
	jobject ret = nullptr;
	try
	{
		ret = J->Individual(J->getIName(name()));
	}
	catch (const EFPPCantRegName&)
	{
		Throw ( env, "FaCT++ Kernel: Can not register new individual name" );
	}
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getBuiltInDataType
 * Signature: (Ljava/lang/String;)Luk/ac/manchester/cs/factplusplus/DataTypePointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getBuiltInDataType
  (JNIEnv * env, jobject obj, jstring str)
{
	TRACE_JNI("getBuiltInDataType");
	TRACE_STR(env,str);
	TJNICache* J = getJ(env,obj);
	JString name(env,str);
	std::string DTName(name());
	if ( DTName == "http://www.w3.org/2000/01/rdf-schema#Literal" ||
		 DTName == "http://www.w3.org/2000/01/rdf-schema#anySimpleType" ||
		 DTName == "http://www.w3.org/2001/XMLSchema#anyType" ||
		 DTName == "http://www.w3.org/2001/XMLSchema#anySimpleType" )
		return J->DataType(J->EM->DataTop());

	if ( DTName == "http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral" ||
		 DTName == "http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral" ||
		 DTName == "http://www.w3.org/2001/XMLSchema#string" ||
		 DTName == "http://www.w3.org/2001/XMLSchema#anyURI" ||
		 DTName == "http://www.w3.org/2001/XMLSchema#ID" )
		return J->DataType(J->EM->getStrDataType());

	if ( DTName == "http://www.w3.org/2001/XMLSchema#integer" ||
		 DTName == "http://www.w3.org/2001/XMLSchema#int" ||
		 DTName == "http://www.w3.org/2001/XMLSchema#long" ||
		 DTName == "http://www.w3.org/2001/XMLSchema#nonNegativeInteger" ||
		 DTName == "http://www.w3.org/2001/XMLSchema#positiveInteger" ||
		 DTName == "http://www.w3.org/2001/XMLSchema#negativeInteger" ||
		 DTName == "http://www.w3.org/2001/XMLSchema#short" ||
		 DTName == "http://www.w3.org/2001/XMLSchema#byte" )
		return J->DataType(J->EM->getIntDataType());

	if ( DTName == "http://www.w3.org/2001/XMLSchema#float" ||
		 DTName == "http://www.w3.org/2001/XMLSchema#double" ||
		 DTName == "http://www.w3.org/2002/07/owl#real" ||
		 DTName == "http://www.w3.org/2001/XMLSchema#decimal" )
		return J->DataType(J->EM->getRealDataType());

	if ( DTName == "http://www.w3.org/2001/XMLSchema#boolean" )
		return J->DataType(J->EM->getBoolDataType());

	if ( DTName == "http://www.w3.org/2001/XMLSchema#dateTimeAsLong" )
		return J->DataType(J->EM->getTimeDataType());

	std::stringstream err;
	err << "Unsupported datatype '" << DTName << "'";
	Throw ( env, err.str().c_str() );
	return nullptr;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataSubType
 * Signature: (Ljava/lang/String;Luk/ac/manchester/cs/factplusplus/DataTypeExpressionPointer;)Luk/ac/manchester/cs/factplusplus/DataTypeExpressionPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataSubType
  (JNIEnv * env, jobject obj ATTR_UNUSED, jstring str ATTR_UNUSED, jobject type ATTR_UNUSED)
{
	TRACE_JNI("getDataSubType");
	TRACE_STR(env,str);
//	TJNICache* J = getJ(env,obj);
	JString name(env,str);
	Throw ( env, "FaCT++ Kernel: unsupported operation 'getDataSubType'" );
	jobject ret = nullptr;
#if 0
	try
	{
		ret = DataTypeExpression ( env, getK(env,obj)->getDataTypeCenter().
								   getDataType ( name(), getDataExpr(env,type) ) );
	}
	catch (const EFPPCantRegName&)
	{
		Throw ( env, "FaCT++ Kernel: Can not register new data type" );
	}
#endif
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataTop
 * Signature: ()Luk/ac/manchester/cs/factplusplus/DataTypePointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataTop
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("getDataTop");
	TJNICache* J = getJ(env,obj);
	return J->DataType(J->EM->DataTop());
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataEnumeration
 * Signature: ()Luk/ac/manchester/cs/factplusplus/DataTypeExpressionPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataEnumeration
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("getDataEnumeration");
	TJNICache* J = getJ(env,obj);
	return J->DataTypeExpression(J->EM->DataOneOf());
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getRestrictedDataType
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataTypeExpressionPointer;Luk/ac/manchester/cs/factplusplus/DataTypeFacet;)Luk/ac/manchester/cs/factplusplus/DataTypeExpressionPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getRestrictedDataType
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	TRACE_JNI("getRestrictedDataType");
	TJNICache* J = getJ(env,obj);
	return J->DataTypeExpression ( J->EM->RestrictedType ( getDataTypeExpr(env,arg1), getFacetExpr(env,arg2) ) );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getLength
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataValuePointer;)Luk/ac/manchester/cs/factplusplus/DataTypeFacet;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getLength
  (JNIEnv * env, jobject, jobject)
{
	Throw ( env, "FaCT++ Kernel: unsupported facet 'getLength'" );
	return nullptr;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getMinLength
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataValuePointer;)Luk/ac/manchester/cs/factplusplus/DataTypeFacet;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getMinLength
  (JNIEnv * env, jobject, jobject)
{
	Throw ( env, "FaCT++ Kernel: unsupported facet 'getMinLength'" );
	return nullptr;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getMaxLength
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataValuePointer;)Luk/ac/manchester/cs/factplusplus/DataTypeFacet;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getMaxLength
  (JNIEnv * env, jobject, jobject)
{
	Throw ( env, "FaCT++ Kernel: unsupported facet 'getMaxLength'" );
	return nullptr;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getPattern
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataValuePointer;)Luk/ac/manchester/cs/factplusplus/DataTypeFacet;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getPattern
  (JNIEnv * env, jobject, jobject)
{
	Throw ( env, "FaCT++ Kernel: unsupported facet 'getPattern'" );
	return nullptr;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getMinExclusiveFacet
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataValuePointer;)Luk/ac/manchester/cs/factplusplus/DataTypeFacet;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getMinExclusiveFacet
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("getMinExclusiveFacet");
	TJNICache* J = getJ(env,obj);
	return J->Facet(J->EM->FacetMinExclusive(getDataValueExpr(env,arg)));
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getMaxExclusiveFacet
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataValuePointer;)Luk/ac/manchester/cs/factplusplus/DataTypeFacet;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getMaxExclusiveFacet
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("getMaxExclusiveFacet");
	TJNICache* J = getJ(env,obj);
	return J->Facet(J->EM->FacetMaxExclusive(getDataValueExpr(env,arg)));
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getMinInclusiveFacet
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataValuePointer;)Luk/ac/manchester/cs/factplusplus/DataTypeFacet;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getMinInclusiveFacet
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("getMinInclusiveFacet");
	TJNICache* J = getJ(env,obj);
	return J->Facet(J->EM->FacetMinInclusive(getDataValueExpr(env,arg)));
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getMaxInclusiveFacet
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataValuePointer;)Luk/ac/manchester/cs/factplusplus/DataTypeFacet;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getMaxInclusiveFacet
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("getMaxInclusiveFacet");
	TJNICache* J = getJ(env,obj);
	return J->Facet(J->EM->FacetMaxInclusive(getDataValueExpr(env,arg)));
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getTotalDigitsFacet
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataValuePointer;)Luk/ac/manchester/cs/factplusplus/DataTypeFacet;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getTotalDigitsFacet
  (JNIEnv * env, jobject, jobject)
{
	Throw ( env, "FaCT++ Kernel: unsupported facet 'getTotalDigitsFacet'" );
	return nullptr;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getFractionDigitsFacet
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataValuePointer;)Luk/ac/manchester/cs/factplusplus/DataTypeFacet;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getFractionDigitsFacet
  (JNIEnv * env, jobject obj ATTR_UNUSED, jobject)
{
	TRACE_JNI("getFractionDigitsFacet");
	Throw ( env, "FaCT++ Kernel: unsupported facet 'getFractionDigitsFacet'" );
	return nullptr;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataNot
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataTypeExpressionPointer;)Luk/ac/manchester/cs/factplusplus/DataTypeExpressionPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataNot
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("getDataNot");
	TJNICache* J = getJ(env,obj);
	return J->DataTypeExpression(J->EM->DataNot(getDataExpr(env,arg)));
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataIntersectionOf
 * Signature: ()Luk/ac/manchester/cs/factplusplus/DataTypeExpressionPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataIntersectionOf
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("getDataIntersectionOf");
	TJNICache* J = getJ(env,obj);
	return J->DataTypeExpression(J->EM->DataAnd());
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataUnionOf
 * Signature: ()Luk/ac/manchester/cs/factplusplus/DataTypeExpressionPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataUnionOf
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("getDataUnionOf");
	TJNICache* J = getJ(env,obj);
	return J->DataTypeExpression(J->EM->DataOr());
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataValue
 * Signature: (Ljava/lang/String;Luk/ac/manchester/cs/factplusplus/DataTypePointer;)Luk/ac/manchester/cs/factplusplus/DataValuePointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataValue__Ljava_lang_String_2Luk_ac_manchester_cs_factplusplus_DataTypePointer_2
  (JNIEnv * env, jobject obj, jstring str, jobject type)
{
	TRACE_JNI("getDataValue");
	TRACE_STR(env,str);
	TJNICache* J = getJ(env,obj);
	JString name(env,str);
	jobject ret = nullptr;
	try
	{
		ret = J->DataValue ( J->EM->DataValue ( name(), getDataTypeExpr(env,type) ) );
	}
	catch (const EFPPCantRegName&)
	{
		Throw ( env, "FaCT++ Kernel: Can not register new data value" );
	}
	return ret;
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getConceptAnd
 * Signature: ()Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getConceptAnd
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("getConceptAnd");
	TJNICache* J = getJ(env,obj);
	return J->Class(J->EM->And());
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getConceptOr
 * Signature: ()Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getConceptOr
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("getConceptOr");
	TJNICache* J = getJ(env,obj);
	return J->Class(J->EM->Or());
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getConceptNot
 * Signature: (Luk/ac/manchester/cs/factplusplus/ClassPointer;)Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getConceptNot
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("getConceptNot");
	TJNICache* J = getJ(env,obj);
	return J->Class(J->EM->Not(getConceptExpr(env,arg)));
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getObjectSome
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Luk/ac/manchester/cs/factplusplus/ClassPointer;)Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getObjectSome
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	TRACE_JNI("getObjectSome");
	TJNICache* J = getJ(env,obj);
	return J->Class ( J->EM->Exists ( getORoleExpr(env,arg1), getConceptExpr(env,arg2) ) );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getObjectAll
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Luk/ac/manchester/cs/factplusplus/ClassPointer;)Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getObjectAll
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	TRACE_JNI("getObjectAll");
	TJNICache* J = getJ(env,obj);
	return J->Class ( J->EM->Forall ( getORoleExpr(env,arg1), getConceptExpr(env,arg2) ) );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getObjectValue
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Luk/ac/manchester/cs/factplusplus/IndividualPointer;)Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getObjectValue
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	TRACE_JNI("getObjectValue");
	TJNICache* J = getJ(env,obj);
	return J->Class ( J->EM->Value ( getORoleExpr(env,arg1), getIndividualExpr(env,arg2) ) );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataSome
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;Luk/ac/manchester/cs/factplusplus/DataTypeExpressionPointer;)Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataSome
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	TRACE_JNI("getDataSome");
	TJNICache* J = getJ(env,obj);
	return J->Class ( J->EM->Exists ( getDRoleExpr(env,arg1), getDataExpr(env,arg2) ) );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataAll
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;Luk/ac/manchester/cs/factplusplus/DataTypeExpressionPointer;)Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataAll
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	TRACE_JNI("getDataAll");
	TJNICache* J = getJ(env,obj);
	return J->Class ( J->EM->Forall ( getDRoleExpr(env,arg1), getDataExpr(env,arg2) ) );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataValue
 * Signature: (Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;Luk/ac/manchester/cs/factplusplus/DataValuePointer;)Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataValue__Luk_ac_manchester_cs_factplusplus_DataPropertyPointer_2Luk_ac_manchester_cs_factplusplus_DataValuePointer_2
  (JNIEnv * env, jobject obj, jobject arg1, jobject arg2)
{
	TRACE_JNI("getDataValue");
	TJNICache* J = getJ(env,obj);
	return J->Class ( J->EM->Value ( getDRoleExpr(env,arg1), getDataValueExpr(env,arg2) ) );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getObjectAtLeast
 * Signature: (ILuk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Luk/ac/manchester/cs/factplusplus/ClassPointer;)Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getObjectAtLeast
  (JNIEnv * env, jobject obj, jint n, jobject arg1, jobject arg2)
{
	TRACE_JNI("getObjectAtLeast");
	TJNICache* J = getJ(env,obj);
	return J->Class ( J->EM->MinCardinality ( (unsigned int)n, getORoleExpr(env,arg1), getConceptExpr(env,arg2) ) );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getObjectExact
 * Signature: (ILuk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Luk/ac/manchester/cs/factplusplus/ClassPointer;)Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getObjectExact
  (JNIEnv * env, jobject obj, jint n, jobject arg1, jobject arg2)
{
	TRACE_JNI("getObjectExact");
	TJNICache* J = getJ(env,obj);
	return J->Class ( J->EM->Cardinality ( (unsigned int)n, getORoleExpr(env,arg1), getConceptExpr(env,arg2) ) );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getObjectAtMost
 * Signature: (ILuk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;Luk/ac/manchester/cs/factplusplus/ClassPointer;)Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getObjectAtMost
  (JNIEnv * env, jobject obj, jint n, jobject arg1, jobject arg2)
{
	TRACE_JNI("getObjectAtMost");
	TJNICache* J = getJ(env,obj);
	return J->Class ( J->EM->MaxCardinality ( (unsigned int)n, getORoleExpr(env,arg1), getConceptExpr(env,arg2) ) );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataAtLeast
 * Signature: (ILuk/ac/manchester/cs/factplusplus/DataPropertyPointer;Luk/ac/manchester/cs/factplusplus/DataTypePointer;)Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataAtLeast
  (JNIEnv * env, jobject obj, jint n, jobject arg1, jobject arg2)
{
	TRACE_JNI("getDataAtLeast");
	TJNICache* J = getJ(env,obj);
	return J->Class ( J->EM->MinCardinality ( (unsigned int)n, getDRoleExpr(env,arg1), getDataExpr(env,arg2) ) );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataExact
 * Signature: (ILuk/ac/manchester/cs/factplusplus/DataPropertyPointer;Luk/ac/manchester/cs/factplusplus/DataTypePointer;)Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataExact
  (JNIEnv * env, jobject obj, jint n, jobject arg1, jobject arg2)
{
	TRACE_JNI("getDataExact");
	TJNICache* J = getJ(env,obj);
	return J->Class ( J->EM->Cardinality ( (unsigned int)n, getDRoleExpr(env,arg1), getDataExpr(env,arg2) ) );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataAtMost
 * Signature: (ILuk/ac/manchester/cs/factplusplus/DataPropertyPointer;Luk/ac/manchester/cs/factplusplus/DataTypePointer;)Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataAtMost
  (JNIEnv * env, jobject obj, jint n, jobject arg1, jobject arg2)
{
	TRACE_JNI("getDataAtMost");
	TJNICache* J = getJ(env,obj);
	return J->Class ( J->EM->MaxCardinality ( (unsigned int)n, getDRoleExpr(env,arg1), getDataExpr(env,arg2) ) );
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getInverseProperty
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getInverseProperty
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("getInverseProperty");
	TJNICache* J = getJ(env,obj);
	return J->ObjectProperty(J->EM->Inverse(getORoleExpr(env,arg)));
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getPropertyComposition
 * Signature: ()Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getPropertyComposition
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("getPropertyComposition");
	TJNICache* J = getJ(env,obj);
	return J->ObjectComplex(J->EM->Compose());
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getDataPropertyKey
 * Signature: ()Luk/ac/manchester/cs/factplusplus/DataPropertyPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getDataPropertyKey
  (JNIEnv * env, jobject obj ATTR_UNUSED)
{
	TRACE_JNI("getDataPropertyKey");
	Throw ( env, "FaCT++ Kernel: unsupported operation 'getDataPropertyKey'" );
	return nullptr;
}


/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getObjectPropertyKey
 * Signature: ()Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getObjectPropertyKey
  (JNIEnv * env, jobject obj ATTR_UNUSED)
{
	TRACE_JNI("getObjectPropertyKey");
	Throw ( env, "FaCT++ Kernel: unsupported operation 'getObjectPropertyKey'" );
	return nullptr;
}


/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getOneOf
 * Signature: ()Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getOneOf
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("getOneOf");
	TJNICache* J = getJ(env,obj);
	return J->Class(J->EM->OneOf());
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    getSelf
 * Signature: (Luk/ac/manchester/cs/factplusplus/ObjectPropertyPointer;)Luk/ac/manchester/cs/factplusplus/ClassPointer;
 */
JNIEXPORT jobject JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_getSelf
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("getSelf");
	TJNICache* J = getJ(env,obj);
	return J->Class(J->EM->SelfReference(getORoleExpr(env,arg)));
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    initArgList
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_initArgList
  (JNIEnv * env, jobject obj)
{
	TRACE_JNI("initArgList");
	TJNICache* J = getJ(env,obj);
	J->EM->newArgList();
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    addArg
 * Signature: (Luk/ac/manchester/cs/factplusplus/Pointer;)V
 */
JNIEXPORT void JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_addArg
  (JNIEnv * env, jobject obj, jobject arg)
{
	TRACE_JNI("addArg");
	TRACE_ARG(env,obj,arg);
	TJNICache* J = getJ(env,obj);
	J->EM->addArg(getExpr(env,arg));
}

/*
 * Class:     uk_ac_manchester_cs_factplusplus_FaCTPlusPlus
 * Method:    closeArgList
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_uk_ac_manchester_cs_factplusplus_FaCTPlusPlus_closeArgList
  (JNIEnv *, jobject)
{
}

#ifdef __cplusplus
}
#endif
