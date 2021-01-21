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

#ifndef TDAG2INTERFACE_H
#define TDAG2INTERFACE_H

#include "tDLExpression.h"
#include "tExpressionManager.h"
#include "dlDag.h"

/// class to translate DAG entities into the TDL* expressions
class TDag2Interface
{
protected:	// members
		/// DAG to be translated
	const DLDag& Dag;
		/// expression manager
	TExpressionManager* Manager;
		/// vector of cached concept expressions
	std::vector<const TDLConceptExpression*> TransC;
		/// vector of cached data expressions
	std::vector<const TDLDataExpression*> TransD;

protected:	// methods
		/// build concept expression by a vertex V
	const TDLConceptExpression* buildCExpr ( const DLVertex& v );
		/// build data expression by a vertex V
	const TDLDataExpression* buildDExpr ( const DLVertex& v );
		/// create concept name by named entry
	static const TDLConceptName* CName ( const TNamedEntry* p ) { return dynamic_cast<const TDLConceptName*>(p->getEntity()); }
		/// create individual name by named entry
	static const TDLIndividualName* IName ( const TNamedEntry* p ) { return dynamic_cast<const TDLIndividualName*>(p->getEntity()); }
		/// create object role name by named entry
	static const TDLObjectRoleName* ORName ( const TNamedEntry* p ) { return dynamic_cast<const TDLObjectRoleName*>(p->getEntity()); }
		/// create data role name by named entry
	static const TDLDataRoleName* DRName ( const TNamedEntry* p ) { return dynamic_cast<const TDLDataRoleName*>(p->getEntity()); }

public:		// interface
		/// init c'tor
	TDag2Interface ( const DLDag& dag, TExpressionManager* manager )
		: Dag(dag)
		, Manager(manager)
		, TransC(dag.size(),nullptr)
		, TransD(dag.size(),nullptr)
		{}

		/// make sure that size of expression cache is the same as the size of a DAG
	void ensureDagSize ( void )
	{
		size_t ds = Dag.size(), ts = TransC.size();
		if ( likely(ds == ts) )
			return;
		TransC.resize(ds);
		TransD.resize(ds);
		if ( unlikely(ds>ts) )
			for ( ; ts != ds; ++ts )
			{
				TransC[ts] = nullptr;
				TransD[ts] = nullptr;
			}
	}
		/// get concept expression corresponding index of vertex
	const TDLConceptExpression* getCExpr ( BipolarPointer p )
	{
		if ( isNegative(p) )
			return Manager->Not(getCExpr(inverse(p)));
		unsigned int i = (unsigned int)p;
		if ( TransC[i] == nullptr )
			TransC[i] = buildCExpr(Dag[p]);
		return TransC[i];
	}
		/// get data expression corresponding index of vertex
	const TDLDataExpression* getDExpr ( BipolarPointer p )
	{
		if ( isNegative(p) )
			return Manager->DataNot(getDExpr(inverse(p)));
		unsigned int i = (unsigned int)p;
		if ( TransD[i] == nullptr )
			TransD[i] = buildDExpr(Dag[p]);
		return TransD[i];
	}
		/// get expression corresponding index of vertex given the DATA flag
	const TDLExpression* getExpr ( BipolarPointer p, bool data )
	{
		if ( data )
			return getDExpr(p);
		else
			return getCExpr(p);
	}
}; // TDag2Interface

#endif
