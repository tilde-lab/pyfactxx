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

#include <iostream>

#include "dumpInterface.h"
#include "dlTBox.h"

//-------------------------------------------------------------------------------
// General dumping methods -- dumping relevant parts of ontology
//-------------------------------------------------------------------------------
void TBox :: dump ( dumpInterface* dump ) const
{
	dump->prologue();

	// dump all (relevant) roles
	dumpAllRoles(dump);

	// dump all (relevant) concepts
	for ( c_const_iterator pc = c_begin(); pc != c_end(); ++pc )
		if ( isRelevant(*pc) )
			dumpConcept( dump, *pc );

	for ( i_const_iterator pi = i_begin(); pi != i_end(); ++pi )
		if ( isRelevant(*pi) )
			dumpConcept( dump, *pi );

	// dump GCIs
	if ( getTG() != bpTOP )
	{
		dump->startAx (diImpliesC);
		dump->dumpTop();
		dump->contAx (diImpliesC);
		dumpExpression ( dump, getTG() );
		dump->finishAx (diImpliesC);
	}

	dump->epilogue();
}

void TBox :: dumpConcept ( dumpInterface* dump, const TConcept* p ) const
{
	// dump defConcept
	dump->startAx (diDefineC);
	dump->dumpConcept(p);
	dump->finishAx (diDefineC);

	// dump "p [= def"
	if ( p->pBody != bpTOP )
	{
		diAx Ax = p->isNonPrimitive() ? diEqualsC : diImpliesC;
		dump->startAx (Ax);
		dump->dumpConcept(p);
		dump->contAx (Ax);
		dumpExpression ( dump, p->pBody );
		dump->finishAx (Ax);
	}
}

void TBox :: dumpRole ( dumpInterface* dump, const TRole* p ) const
{
	// dump defRole and parents
	if ( p->getId() > 0 || !isRelevant(p->inverse()) )
	{
		const TRole* q = ( p->getId()>0 ? p : p->inverse() );

		dump->startAx (diDefineR);
		dump->dumpRole(q);
		dump->finishAx (diDefineR);

		// dump parents
		for ( const auto& parent: q->told() )
		{
			dump->startAx (diImpliesR);
			dump->dumpRole(q);
			dump->contAx (diImpliesR);
			dump->dumpRole(static_cast<const TRole*>(parent));
			dump->finishAx (diImpliesR);
		}
	}

	// dump transitivity
	if ( p->isTransitive() )
	{
		dump->startAx (diTransitiveR);
		dump->dumpRole(p);
		dump->finishAx (diTransitiveR);
	}

	// dump functionality (for topmost-functional roles only)
	if ( p->isTopFunc() )
	{
		dump->startAx (diFunctionalR);
		dump->dumpRole(p);
		dump->finishAx (diFunctionalR);
	}

	// dump "domain"
	if ( p->getBPDomain() != bpTOP )
	{
		dump->startAx (diDomainR);
		dump->dumpRole(p);
		dump->contAx (diDomainR);
		dumpExpression ( dump, p->getBPDomain() );
		dump->finishAx (diDomainR);
	}

	// dump "range"
	if ( p->getBPRange() != bpTOP )
	{
		dump->startAx (diRangeR);
		dump->dumpRole(p);
		dump->contAx (diRangeR);
		dumpExpression ( dump, p->getBPRange() );
		dump->finishAx (diRangeR);
	}
}

void TBox :: dumpExpression ( dumpInterface* dump, BipolarPointer p ) const
{
	fpp_assert ( isValid(p) );

	if ( p == bpTOP )
		return dump->dumpTop();
	if ( p == bpBOTTOM )
		return dump->dumpBottom();

	// checks inversion
	if ( isNegative(p) )
	{
		dump->startOp (diNot);
		dumpExpression ( dump, inverse(p) );
		return dump->finishOp (diNot);
	}

	const DLVertex& v = DLHeap[p];

	switch ( v.Type() )
	{
	case dtTop:
		return dump->dumpTop();

	case dtName:
		return dump->dumpConcept(static_cast<const TConcept*>(v.getConcept()));

	case dtAnd:
		dump->startOp (diAnd);
		for ( DLVertex::const_iterator q = v.begin(); q != v.end(); ++q )
		{
			if ( q != v.begin() )
				dump->contOp (diAnd);
			dumpExpression ( dump, *q );
		}
		dump->finishOp (diAnd);
		return;

	case dtForall:
		dump->startOp (diForall);
		dump->dumpRole (v.getRole());
		dump->contOp(diForall);
		dumpExpression ( dump, v.getC() );
		dump->finishOp (diForall);
		return;

	case dtLE:
		dump->startOp ( diLE, v.getNumberLE() );
		dump->dumpRole (v.getRole());
		dump->contOp(diLE);
		dumpExpression ( dump, v.getC() );
		dump->finishOp (diLE);
		return;

	default:
		std::cerr << "Error dumping vertex of type " << v.getTagName() << "(" << v.Type () << ")";
		fpp_unreachable();
	}
}

void TBox :: dumpAllRoles ( dumpInterface* dump ) const
{
	RoleMaster::const_iterator p;
	for ( p = ORM.begin(); p != ORM.end(); ++p )
		if ( isRelevant(*p) )
		{
			fpp_assert ( !(*p)->isSynonym() );
			dumpRole ( dump, *p );
		}
	for ( p = DRM.begin(); p != DRM.end(); ++p )
		if ( isRelevant(*p) )
		{
			fpp_assert ( !(*p)->isSynonym() );
			dumpRole ( dump, *p );
		}
}

	// dump given concept expression
void dumpCExpression ( dumpInterface* dump, const DLTree* C )
{
	if ( C == nullptr )
		return;

	Token t = C->Element().getToken();
	diOp tag;

	switch (t)
	{
	case TOP:
		return dump->dumpTop();
	case BOTTOM:
		return dump->dumpBottom();
	case CNAME:
	case INAME:
		return dump->dumpConcept(static_cast<TConcept*>(C->Element().getNE()));
	case NOT:
		dump->startOp(diNot);
		dumpCExpression ( dump, C->Left() );
		return dump->finishOp(diNot);
	case AND:
	case OR:
		tag = t == AND ? diAnd : diOr;
		dump->startOp(tag);
		dumpCExpression ( dump, C->Left() );
		dump->contOp(tag);
		dumpCExpression ( dump, C->Right() );
		return dump->finishOp(tag);

	case EXISTS:
	case FORALL:
	case LE:
	case GE:
		tag = t == EXISTS ? diExists : t == FORALL ? diForall : t == GE ? diGE : diLE;
		if ( t == GE || t == LE )
			dump->startOp ( tag, C->Element().getData() );
		else
			dump->startOp(tag);
		dumpRExpression ( dump, C->Left() );
		dump->contOp(tag);
		dumpCExpression ( dump, C->Right() );
		return dump->finishOp(tag);
	default:
		fpp_unreachable();
	}
}
	// dump given role expression
void dumpRExpression ( dumpInterface* dump, const DLTree* R )
{
	if ( R == nullptr )
		return;

	switch ( R->Element().getToken() )
	{
	case RNAME:
	case DNAME:
		return dump->dumpRole(static_cast<TRole*>(R->Element().getNE()));
	case NOT:
	case INV:
		dump->startOp(diInv);
		dumpRExpression ( dump, R->Left() );
		return dump->finishOp(diInv);
	default:
		fpp_unreachable();
	}
}
