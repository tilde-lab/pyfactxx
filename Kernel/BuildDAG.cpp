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

// This file contains methods for creating DAG representation of KB

#include "dlTBox.h"

void TBox :: buildDAG ( void )
{
	nNominalReferences = 0;

	// init concept indexing
	nC = 1;	// start with 1 to make index 0 an indicator of "not processed"
	ConceptMap.push_back(nullptr);

	// make fresh concept and datatype
	concept2dag(pTemp);
	DLTree* freshDT = DTCenter.getFreshDataType();
	addDataExprToHeap ( static_cast<TDataEntry*>(freshDT->Element().getNE()) );
	deleteTree(freshDT);

	for ( c_const_iterator pc = c_begin(); pc != c_end(); ++pc )
		concept2dag(*pc);
	for ( i_const_iterator pi = i_begin(); pi != i_end(); ++pi )
		concept2dag(*pi);

	// init heads of simple rules
	for ( TSimpleRules::iterator q = SimpleRules.begin(), q_end = SimpleRules.end(); q < q_end; ++q )
		(*q)->bpHead = tree2dag((*q)->tHead);

	// builds Roles range and domain
	initRangeDomain(ORM);
	initRangeDomain(DRM);

	RoleMaster::iterator p, p_end;

	// build all GCIs
	DLTree* GCI = Axioms.getGCI();

	// add special domains to the GCIs
	if ( likely(useSpecialDomains) )
		for ( p = ORM.begin(), p_end = ORM.end(); p < p_end; ++p )
			if ( !(*p)->isSynonym() && (*p)->hasSpecialDomain() )
				GCI = createSNFAnd ( GCI, clone((*p)->getTSpecialDomain()) );

	// take chains that lead to Bot role into account
	if ( !ORM.getBotRole()->isSimple() )
		GCI = createSNFAnd ( GCI,
				new DLTree ( TLexeme(FORALL), createRole(ORM.getBotRole()), createBottom() ) );

	T_G = tree2dag(GCI);
	deleteTree(GCI);

	// mark GCI flags
	GCIs.setGCI(T_G != bpTOP);
	GCIs.setReflexive(ORM.hasReflexiveRoles());

	// builds functional labels for roles
	for ( p = ORM.begin(), p_end = ORM.end(); p < p_end; ++p )
		if ( !(*p)->isSynonym() && (*p)->isTopFunc() )
			(*p)->setFunctional ( atmost2dag ( 1, *p, bpTOP ) );
	for ( p = DRM.begin(), p_end = DRM.end(); p < p_end; ++p )
		if ( !(*p)->isSynonym() && (*p)->isTopFunc() )
			(*p)->setFunctional ( atmost2dag ( 1, *p, bpTOP ) );

	// check the type of the ontology
	if ( nNominalReferences > 0 )
	{
		unsigned int nInd = (unsigned int) (i_end() - i_begin());
		if ( nInd > 100 && nNominalReferences > nInd )
			isLikeWINE = true;
	}

	// here DAG is complete; set its final size
	DLHeap.setFinalSize();
}

void TBox :: initRangeDomain ( RoleMaster& RM )
{
	for ( auto& R: RM )
		if ( !R->isSynonym() )
		{
			// add R&D from super-roles (do it AFTER axioms are transformed into R&D)
			if ( RKG_UPDATE_RND_FROM_SUPERROLES )
				R->collectDomainFromSupers();

			DLTree* dom = R->getTDomain();
			BipolarPointer bp = bpTOP;
			if ( dom )
			{
				bp = tree2dag(dom);
				GCIs.setRnD();
			}

			R->setBPDomain(bp);

			// special domain for R is AR.Range
			R->initSpecialDomain();
			if ( R->hasSpecialDomain() )
				R->setSpecialDomain(tree2dag(R->getTSpecialDomain()));
		}
}

/// register data expression in the DAG
BipolarPointer TBox :: addDataExprToHeap ( TDataEntry* p )
{
	if ( isValid(p->getBP()) )	// already registered value
		return p->getBP();

	// determine the type of an entry
	DagTag dt = p->isBasicDataType() ? dtDataType : p->isDataValue() ? dtDataValue : dtDataExpr;
	BipolarPointer hostBP = bpTOP;

	// register host type first (if any)
	if ( p->getType() != nullptr )
		hostBP = addDataExprToHeap(const_cast<TDataEntry*>(p->getType()));

	// create new DAG entry for the data value
	DLVertex* ver = new DLVertex ( dt, hostBP );
	ver->setConcept(p);
	p->setBP(DLHeap.directAdd(ver));

	return p->getBP();
}

void TBox :: addConceptToHeap ( TConcept* pConcept )
{
	// choose proper tag by concept
	DagTag tag = pConcept->isPrimitive() ?
		(pConcept->isSingleton() ? dtPSingleton : dtPConcept):
		(pConcept->isSingleton() ? dtNSingleton : dtNConcept);

	// NSingleton is a nominal
	if ( tag == dtNSingleton && !pConcept->isSynonym() )
		static_cast<TIndividual*>(pConcept)->setNominal();

	// new concept's addition
	DLVertex* ver = new DLVertex(tag);
	ver->setConcept(pConcept);
	pConcept->pName = DLHeap.directAdd(ver);

	BipolarPointer desc = bpTOP;

	// translate body of a concept
	if ( pConcept->Description != nullptr )	// complex concept
		desc = tree2dag(pConcept->Description);
	else			// only primitive concepts here
		fpp_assert ( pConcept->isPrimitive() );

	// update concept's entry
	pConcept->pBody = desc;
	ver->setChild(desc);
	if ( !pConcept->isSynonym() && pConcept->index() == 0 )
		setConceptIndex(pConcept);
}

BipolarPointer TBox :: tree2dag ( const DLTree* t )
{
	if ( t == nullptr )
		return bpINVALID;	// invalid value

	const TLexeme& cur = t->Element();
	BipolarPointer ret = bpINVALID;

	switch ( cur.getToken() )
	{
	case BOTTOM:	// it is just !top
		ret = bpBOTTOM;
		break;
	case TOP:		// the 1st node
		ret = bpTOP;
		break;
	case DATAEXPR:	// data-related expression
		ret = addDataExprToHeap ( static_cast<TDataEntry*>(cur.getNE()) );
		break;
	case CNAME:		// concept name
		ret = concept2dag(toConcept(cur.getNE()));
		break;
	case INAME:		// individual name
	{
		++nNominalReferences;	// definitely a nominal
		TIndividual* ind = toIndividual(cur.getNE());
		ind->setNominal();
		ret = concept2dag(ind);
		break;
	}

	case NOT:
		ret = inverse ( tree2dag ( t->Left() ) );
		break;
	case AND:
		ret = and2dag(t);
		break;
	case FORALL:
		ret = forall2dag ( resolveRole(t->Left()), tree2dag(t->Right()) );
		break;
	case SELF:
		ret = reflexive2dag(resolveRole(t->Left()));
		break;
	case LE:
		ret = atmost2dag ( cur.getData(), resolveRole(t->Left()), tree2dag(t->Right()) );
		break;
	case PROJFROM:	// note: no PROJINTO as already unified
		ret = DLHeap.directAdd ( new DLVertex ( resolveRole(t->Left()), tree2dag(t->Right()->Right()), resolveRole(t->Right()->Left()) ) );
		break;
	default:
		fpp_assert ( isSNF(t) );	// safety check
		fpp_unreachable();			// extra safety check ;)
	}

	return ret;
}

/// fills AND-like vertex V with an AND-like expression T; process result
BipolarPointer
TBox :: and2dag ( const DLTree* t )
{
	BipolarPointer ret = bpBOTTOM;
	DLVertex* v = new DLVertex(dtAnd);

	if ( fillANDVertex ( v, t ) )	// clash found
		delete v;
	else	// AND vertex
		switch ( v->end() - v->begin() )
		{
		case 0:	// and(TOP) = TOP
			delete v;
			return bpTOP;
		case 1:	// and(C) = C
			ret = *v->begin();
			delete v;
			break;
		default:
			ret = DLHeap.add(v);
			break;
		}

	return ret;
}

BipolarPointer TBox :: forall2dag ( const TRole* R, BipolarPointer C )
{
	if ( R->isDataRole() )
		return dataForall2dag(R,C);

	// create \all R.C == \all R{0}.C
	BipolarPointer ret = DLHeap.add ( new DLVertex ( dtForall, 0, R, C ) );

	if ( R->isSimple() )	// don't care about the rest
		return ret;

	// check if the concept is not last
	if ( !DLHeap.isLast(ret) )
		return ret;		// all sub-roles were added before

	// have appropriate concepts for all the automaton states
	for ( unsigned int i = 1; i < R->getAutomaton().size(); ++i )
		DLHeap.directAddAndCache ( new DLVertex ( dtForall, i, R, C ) );

	return ret;
}

BipolarPointer TBox :: atmost2dag ( unsigned int n, const TRole* R, BipolarPointer C )
{
	// input check: only simple roles are allowed in the (non-trivial) NR
	if ( !R->isSimple() )
		throw EFPPNonSimpleRole(R->getName());

	if ( R->isDataRole() )
		return dataAtMost2dag(n,R,C);

	if ( unlikely ( C == bpBOTTOM ) )	// can happen as A & ~A
		return bpTOP;

	BipolarPointer ret = DLHeap.add ( new DLVertex ( dtLE, n, R, C ) );

	// check if the concept is not last
	if ( !DLHeap.isLast(ret) )
		return ret;		// all elements were added before

	// create entries for the transitive sub-roles
	for ( unsigned int m = n-1; m > 0; --m )
		DLHeap.directAddAndCache ( new DLVertex ( dtLE, m, R, C ) );

	// create a blocker for the NN-rule
	DLHeap.directAddAndCache(new DLVertex(dtNN));

	return ret;
}

bool TBox :: fillANDVertex ( DLVertex* v, const DLTree* t )
{
	if ( t->Element().getToken() == AND )
		return fillANDVertex ( v, t->Left() ) || fillANDVertex ( v, t->Right() );
	else
		return v->addChild ( tree2dag(t) );
}
