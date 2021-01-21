/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2011-2015 Dmitry Tsarkov and The University of Manchester
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

#include "tDag2Interface.h"

/// build concept expression by a vertex V
const TDLConceptExpression*
TDag2Interface :: buildCExpr ( const DLVertex& v )
{
	switch ( v.Type() )
	{
	case dtTop:
		return Manager->Top();
	case dtNConcept:
	case dtPConcept:
		return CName(v.getConcept());
	case dtPSingleton:
	case dtNSingleton:
		return Manager->OneOf(IName(v.getConcept()));
	case dtAnd:
	{
		Manager->newArgList();
		for ( DLVertex::const_iterator p = v.begin(), p_end = v.end(); p != p_end; ++p )
			Manager->addArg(getCExpr(*p));
		return Manager->And();
	}
	case dtForall:
		if ( v.getRole()->isDataRole() )
			return Manager->Forall ( DRName(v.getRole()), getDExpr(v.getC()) );
		else
			return Manager->Forall ( ORName(v.getRole()), getCExpr(v.getC()) );
	case dtLE:
		if ( v.getRole()->isDataRole() )
			return Manager->MaxCardinality ( v.getNumberLE(), DRName(v.getRole()), getDExpr(v.getC()) );
		else
			return Manager->MaxCardinality ( v.getNumberLE(), ORName(v.getRole()), getCExpr(v.getC()) );
	case dtIrr:
		return Manager->Not(Manager->SelfReference(ORName(v.getRole())));
	case dtProj:
	case dtNN:
	case dtChoose:	// these are artificial constructions and shouldn't be visible
		return Manager->Top();
	default:
		fpp_unreachable();
	}
}

/// build data expression by a vertex V
const TDLDataExpression*
TDag2Interface :: buildDExpr ( const DLVertex& v )
{
	switch ( v.Type() )
	{
	case dtTop:
		return Manager->DataTop();
	case dtDataType:
	case dtDataValue:
	case dtDataExpr:	// TODO: no data stuff yet
		return Manager->DataTop();
	case dtAnd:
	{
		Manager->newArgList();
		for ( DLVertex::const_iterator p = v.begin(), p_end = v.end(); p != p_end; ++p )
			Manager->addArg(getDExpr(*p));
		return Manager->DataAnd();
	}
	default:
		fpp_unreachable();
	}
}
