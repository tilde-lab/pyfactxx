/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2003-2015 Dmitry Tsarkov and The University of Manchester
Copyright (C) 2015-2017 Dmitry Tsarkov

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
*/

#ifndef PARSER_H
#define PARSER_H

#include "scanner.h"
#define GenericToken LispToken
#include "comparser.h"
#include "Kernel.h"

/// class for parsing LISP-like ontologies
class DLLispParser: public CommonParser<TsScanner>
{
protected:	// typedefs
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

protected:	// members
		/// Kernel to be filled
	ReasoningKernel* Kernel;
		/// expression manager to be used
	TExpressionManager* EManager;
		/// set of known data role names
	std::set<std::string> DataRoles;

protected:	// methods
		/// error by given exception
	void errorByException ( const EFPPCantRegName& ex ) const { parseError(ex.what()); }

		/// @return concept-like Id of just scanned name
	TConceptExpr* getConcept ( void )
	{
		TConceptExpr* ret = EManager->Concept(scan.GetName());
		NextLex();
		return ret;
	}
		/// @return singleton Id of just scanned name
	TIndividualExpr* getSingleton ( void )
	{
		TIndividualExpr* ret = EManager->Individual(scan.GetName());
		NextLex();
		return ret;
	}
		/// @return data- or object role build from just scanned name
	TRoleExpr* getRole ( void )
	{
		TRoleExpr* ret;
		if ( DataRoles.find(scan.GetName()) != DataRoles.end() )
			ret = EManager->DataRole(scan.GetName());	// found data role
		else	// object role
			ret = EManager->ObjectRole(scan.GetName());
		NextLex();
		return ret;
	}
		/// @return data role build from just scanned name
	TDRoleExpr* getDataRole ( void )
	{
		DataRoles.insert(scan.GetName());
		TDRoleExpr* ret = EManager->DataRole(scan.GetName());
		NextLex();
		return ret;
	}
		/// @return object role build from just scanned name
	TORoleExpr* getObjectRole ( void )
	{
		TORoleExpr* ret = EManager->ObjectRole(scan.GetName());
		NextLex();
		return ret;
	}
		/// @return datavalue of a data type TYPE with an Id of a just scanned name
	TDataValueExpr* getDTValue ( TDataTypeExpr* type )
	{
		TDataValueExpr* ret = EManager->DataValue ( scan.GetName(), type );
		NextLex();
		return ret;
	}

		/// check whether expression R is data role
	bool isDataRole ( const TRoleExpr* R ) const { return dynamic_cast<const TDRoleExpr*>(R) != nullptr; }
		/// generate object role axiom between R and S according to the operation TAG
	void tellRoleAxiom ( LispToken tag, TORoleExpr* R, TORoleExpr* S );
		/// generate data role axiom between R and S according to the operation TAG
	void tellRoleAxiom ( LispToken tag, TDRoleExpr* R, TDRoleExpr* S );
		/// get role expression, ie (data)role or its inverse
	TRoleExpr* getRoleExpression ( void );
		/// get object role expression, ie object role, OR constant or their inverse
	TORoleExpr* getORoleExpression ( void );
		/// get data role expression, ie data role or DR constant
	TDRoleExpr* getDRoleExpression ( void );
		/// get simple role expression or role projection or chain
	TRoleExpr* getComplexRoleExpression ( void );
		/// parse simple DL command
	void parseCommand ( void );
		/// parse role arguments if defprimrole command
	void parseRoleArguments ( TORoleExpr* role );
		/// parse list of concept expressions (in disjoint-like commands)
	void parseConceptList ( bool singletonsOnly );
		/// get concept-like expression for simple variants
	TConceptExpr* getConceptExpression ( void );
		/// get concept-like expression for complex constructors
	TConceptExpr* getComplexConceptExpression ( void );
		/// get data expression
	TDataExpr* getDataExpression ( void );

public:		// interface
		/// the only c'tor
	DLLispParser ( std::istream* in, ReasoningKernel* kernel )
		: CommonParser<TsScanner>(in)
		, Kernel (kernel)
		, EManager(kernel->getExpressionManager())
	{
		// locally register Top/Bottom data properties
		DataRoles.insert("*UDROLE*");
		DataRoles.insert("*EDROLE*");
	}

		/// main parsing method
	void Parse ( void );
};	// DLLispParser

#endif
