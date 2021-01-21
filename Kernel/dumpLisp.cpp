/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2003-2015 Dmitry Tsarkov and The University of Manchester
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

#include "dumpLisp.h"

void dumpLisp :: startOp ( diOp Op )
{
	if ( Op == diAnd || Op == diOr )
		incIndent();

	o << "(";

	switch (Op)
	{
	case diErrorOp:
	default:
		fpp_unreachable();
		// concept expressions
	case diNot:
		o << "not";
		break;
	case diAnd:
		o << "and";
		break;
	case diOr:
		o << "or";
		break;
	case diExists:
		o << "some";
		break;
	case diForall:
		o << "all";
		break;
	case diGE:
		o << "atleast";
		break;
	case diLE:
		o << "atmost";
		break;
	}

	contOp(Op);
}

void dumpLisp :: startAx ( diAx Ax )
{
	o << "(";

	switch (Ax)
	{
	case diErrorAx:
	default:
		fpp_unreachable();
		// concept axioms
	case diDefineC:
		o << "defprimconcept";
		break;
	case diImpliesC:
		o << "implies_c";
		break;
	case diEqualsC:
		o << "equal_c";
		break;
		// role axioms
	case diDefineR:
		o << "defprimrole";
		break;
	case diTransitiveR:
		o << "transitive";
		break;
	case diFunctionalR:
		o << "functional";
		break;
	case diImpliesR:
		o << "implies_r";
		break;
	case diEqualsR:
		o << "equal_r";
		break;
	case diDomainR:
		o << "domain";
		break;
	case diRangeR:
		o << "range";
		break;
	};

	contAx(Ax);
}
