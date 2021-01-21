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

// extended data range support

#include "Kernel.h"
#include "dlCompletionTree.h"
#include "DataReasoning.h"

static bool
checkDataRelation ( const DataTypeReasoner& Op1, const DataTypeReasoner& Op2, int op )
{
	switch (op)
	{
	case 0:	// =
		return Op1 == Op2;
	case 1:	// !=
		return !(Op1 == Op2);
	case 2:	// <
		return Op1 < Op2;
	case 3: // <=
		return Op1 < Op2 || Op1 == Op2;
	case 4:	// >
		return Op2 < Op1;
	case 5: // >=
		return Op2 < Op1 || Op2 == Op1;
	default:
		throw new EFaCTPlusPlus("Illegal operation in checkDataRelation()");
	}
}

static bool
fillDTReasoner ( DataTypeReasoner& DTR, const DlCompletionTree* node )
{
	DTR.clear();
	// data node may contain only "simple" concepts in there
	for ( DlCompletionTree::const_label_iterator p = node->beginl_sc(), p_end = node->endl_sc(); p != p_end; ++p )
		if ( DTR.addDataEntry ( p->bp(), p->getDep() ) )	// clash found
			return true;
	return false;
}

/// set RESULT into set of instances of A such that they do have data roles R and S
void
ReasoningKernel :: getDataRelatedIndividuals ( TDRoleExpr* R, TDRoleExpr* S, int op, IndividualSet& Result )
{
	preprocessKB();	// ensure KB is ready to answer the query
	Result.clear();
	const TRole* r = getRole ( R, "Role expression expected in the getDataRelatedIndividuals()" );
	const TRole* s = getRole ( S, "Role expression expected in the getDataRelatedIndividuals()" );

	// prepare DT reasoners
	const DLDag& dag = getTBox()->getDag();
	const DataTypeCenter& dtc = getTBox()->getDataTypeCenter();

	DataTypeReasoner Op1(dag);
	DataTypeReasoner Op2(dag);
	dtc.initDataTypeReasoner(Op1);
	dtc.initDataTypeReasoner(Op2);

	// vector of individuals
	typedef TDLNAryExpression<TDLIndividualExpression> IndVec;
	IndVec Individuals ("individual expression","data related individuals");
	Individuals.add(getExpressionManager()->getArgList());
	for ( const TIndividualExpr* individual : Individuals )
	{
		const TIndividual* ind = getIndividual ( individual, "individual name expected in getDataRelatedIndividuals()" );
		const DlCompletionTree* vR = nullptr;
		const DlCompletionTree* vS = nullptr;
		for ( DlCompletionTree::const_edge_iterator p = ind->node->begin(), p_end = ind->node->end(); p != p_end; ++p )
		{
			const DlCompletionTreeArc* edge = *p;
			if ( edge->isNeighbour(r) )
				vR = edge->getArcEnd();
			else if ( edge->isNeighbour(s) )
				vS = edge->getArcEnd();
			if ( vR && vS )
			{
				if ( fillDTReasoner ( Op1, vR ) )
					break;
				if ( fillDTReasoner ( Op2, vS ) )
					break;
				if ( checkDataRelation ( Op1, Op2, op ) )
					Result.push_back(ind);
				break;
			}
		}
	}
}

