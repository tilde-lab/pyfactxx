/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2010-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef TEXPRESSIONPRINTERLISP_H
#define TEXPRESSIONPRINTERLISP_H

#include <ostream>

#include "tDLExpression.h"

class TLISPExpressionPrinter: public DLExpressionVisitor
{
protected:	// members
		/// main stream
	std::ostream& o;
		/// define str-str map
	typedef std::map<std::string, std::string> SSMap;
		/// map between OWL datatype names and FaCT++ ones
	SSMap DTNames;
		/// helper class for brackets
	class BR
	{
	protected:
		std::ostream& o;
	public:
		BR ( std::ostream& o_, const char* command ) : o(o_) { o << " (" << command; }
		~BR() { o << ")"; }
	}; // BR
protected:	// methods
		/// array helper
	template <typename Argument>
	void printArray ( const TDLNAryExpression<Argument>& expr )
	{
		for ( typename TDLNAryExpression<Argument>::iterator p = expr.begin(), p_end = expr.end(); p != p_end; ++p )
			(*p)->accept(*this);
	}
		/// datatype helper to get a LISP datatype name by a OWL one
	const char* getDTName ( const char* owlName ) const
	{
		SSMap::const_iterator p = DTNames.find(owlName);
		if ( p != DTNames.end() )	// known name
			return p->second.c_str();
		return owlName;
	}

public:		// interface
		/// init c'tor
	explicit TLISPExpressionPrinter ( std::ostream& o_ ) : o(o_)
	{
		DTNames["http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral"] = "string";
		DTNames["http://www.w3.org/2001/XMLSchema#string"] = "string";
		DTNames["http://www.w3.org/2001/XMLSchema#anyURI"] = "string";

		DTNames["http://www.w3.org/2001/XMLSchema#integer"] = "number";
		DTNames["http://www.w3.org/2001/XMLSchema#int"] = "number";

		DTNames["http://www.w3.org/2001/XMLSchema#float"] = "real";
		DTNames["http://www.w3.org/2001/XMLSchema#double"] = "real";
		DTNames["http://www.w3.org/2001/XMLSchema#real"] = "real";
	}

public:		// visitor interface
	// concept expressions
	void visit ( const TDLConceptTop& ) override { o << " *TOP*"; }
	void visit ( const TDLConceptBottom& ) override { o << " *BOTTOM*"; }
	void visit ( const TDLConceptName& expr ) override { o << " " << expr.getName(); }
	void visit ( const TDLConceptNot& expr ) override { BR b(o,"not"); expr.getC()->accept(*this); }
	void visit ( const TDLConceptAnd& expr ) override { BR b(o,"and"); printArray(expr); }
	void visit ( const TDLConceptOr& expr ) override { BR b(o,"or"); printArray(expr); }
	void visit ( const TDLConceptOneOf& expr ) override { BR b(o,"one-of"); printArray(expr); }
	void visit ( const TDLConceptObjectSelf& expr ) override { BR b(o,"self-ref"); expr.getOR()->accept(*this); }
	void visit ( const TDLConceptObjectValue& expr ) override { BR b(o,"some"); expr.getOR()->accept(*this); BR i(o,"one-of"); expr.getI()->accept(*this); }
	void visit ( const TDLConceptObjectExists& expr ) override { BR b(o,"some"); expr.getOR()->accept(*this); expr.getC()->accept(*this); }
	void visit ( const TDLConceptObjectForall& expr ) override { BR b(o,"all"); expr.getOR()->accept(*this); expr.getC()->accept(*this); }
	void visit ( const TDLConceptObjectMinCardinality& expr ) override
		{ BR b(o,"atleast"); o << " " << expr.getNumber(); expr.getOR()->accept(*this); expr.getC()->accept(*this); }
	void visit ( const TDLConceptObjectMaxCardinality& expr ) override
		{ BR b(o,"atmost"); o << " " << expr.getNumber(); expr.getOR()->accept(*this); expr.getC()->accept(*this); }
	void visit ( const TDLConceptObjectExactCardinality& expr ) override
	{
		BR a(o,"and");
		{ BR b(o,"atleast"); o << " " << expr.getNumber(); expr.getOR()->accept(*this); expr.getC()->accept(*this); }
		{ BR b(o,"atmost"); o << " " << expr.getNumber(); expr.getOR()->accept(*this); expr.getC()->accept(*this); }
	}
	void visit ( const TDLConceptDataValue& expr ) override { BR b(o,"some"); expr.getDR()->accept(*this); expr.getExpr()->accept(*this); }
	void visit ( const TDLConceptDataExists& expr ) override { BR b(o,"some"); expr.getDR()->accept(*this); expr.getExpr()->accept(*this); }
	void visit ( const TDLConceptDataForall& expr ) override { BR b(o,"all"); expr.getDR()->accept(*this); expr.getExpr()->accept(*this); }
	void visit ( const TDLConceptDataMinCardinality& expr ) override
		{ BR b(o,"atleast"); o << " " << expr.getNumber(); expr.getDR()->accept(*this); expr.getExpr()->accept(*this); }
	void visit ( const TDLConceptDataMaxCardinality& expr ) override
		{ BR b(o,"atmost"); o << " " << expr.getNumber(); expr.getDR()->accept(*this); expr.getExpr()->accept(*this); }
	void visit ( const TDLConceptDataExactCardinality& expr ) override
	{
		BR a(o,"and");
		{ BR b(o,"atleast"); o << " " << expr.getNumber(); expr.getDR()->accept(*this); expr.getExpr()->accept(*this); }
		{ BR b(o,"atmost"); o << " " << expr.getNumber(); expr.getDR()->accept(*this); expr.getExpr()->accept(*this); }
	}

	// individual expressions
	void visit ( const TDLIndividualName& expr ) override { o << " " << expr.getName(); }

	// object role expressions
	void visit ( const TDLObjectRoleTop& ) override { o << " *UROLE*"; }
	void visit ( const TDLObjectRoleBottom& ) override { o << " *EROLE*"; }
	void visit ( const TDLObjectRoleName& expr ) override { o << " " << expr.getName(); }
	void visit ( const TDLObjectRoleInverse& expr ) override { BR b(o,"inv"); expr.getOR()->accept(*this); }
	void visit ( const TDLObjectRoleChain& expr ) override { BR b(o,"compose"); printArray(expr); }
	void visit ( const TDLObjectRoleProjectionFrom& expr ) override
		{ BR b(o,"project_from"); expr.getOR()->accept(*this); expr.getC()->accept(*this); }
	void visit ( const TDLObjectRoleProjectionInto& expr ) override
		{ BR b(o,"project_into"); expr.getOR()->accept(*this); expr.getC()->accept(*this); }

	// data role expressions
	void visit ( const TDLDataRoleTop& ) override { o << " *UDROLE*";  }
	void visit ( const TDLDataRoleBottom& ) override { o << " *EDROLE*"; }
	void visit ( const TDLDataRoleName& expr ) override { o << " " << expr.getName(); }

	// data expressions
	void visit ( const TDLDataTop& ) override { o << " *TOP*"; }
	void visit ( const TDLDataBottom& ) override { o << " *BOTTOM*"; }
	void visit ( const TDLDataTypeName& expr ) override { o << " (" << getDTName(expr.getName()) << ")"; }
		// no need to use a type of a restriction here, as all contains in constants
	void visit ( const TDLDataTypeRestriction& expr ) override { BR b(o,"and"); printArray(expr); }
	void visit ( const TDLDataValue& expr ) override
		{ o << " (" << getDTName(getBasicDataType(const_cast<TDLDataTypeExpression*>(expr.getExpr()))->getName()) << " " << expr.getName() << ")"; }
	void visit ( const TDLDataNot& expr ) override { BR b(o,"not"); expr.getExpr()->accept(*this); }
	void visit ( const TDLDataAnd& expr ) override { BR b(o,"and"); printArray(expr); }
	void visit ( const TDLDataOr& expr ) override { BR b(o,"or"); printArray(expr); }
	void visit ( const TDLDataOneOf& expr ) override { BR b(o,"d-one-of"); printArray(expr); }

	// facets
	void visit ( const TDLFacetMinInclusive& expr ) override { BR b(o,"ge"); expr.getExpr()->accept(*this); }
	void visit ( const TDLFacetMinExclusive& expr ) override { BR b(o,"gt"); expr.getExpr()->accept(*this); }
	void visit ( const TDLFacetMaxInclusive& expr ) override { BR b(o,"le"); expr.getExpr()->accept(*this); }
	void visit ( const TDLFacetMaxExclusive& expr ) override { BR b(o,"lt"); expr.getExpr()->accept(*this); }
}; // TLISPExpressionPrinter

#endif
