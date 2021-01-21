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

#include "globaldef.h"

#ifdef RKG_USE_SORTED_REASONING

#include "RoleMaster.h"
#include "dlDag.h"
#include "logging.h"

/// merge label of given role and all its super-roles
void TRole :: mergeSupersDomain ( void )
{
	for ( const auto& sup: ancestors() )
		domLabel.merge(sup->domLabel);

	// for reflexive role -- merge domain and range labels
	if ( isReflexive() )
		domLabel.merge(getRangeLabel());

	// for R1*R2*...*Rn [= R, merge dom(R) with dom(R1) and ran(R) with ran(Rn)
	for ( std::vector<TRoleVec>::iterator q = subCompositions.begin(), q_end = subCompositions.end(); q != q_end; ++q )
		if ( !q->empty() )
		{
			domLabel.merge((*q->begin())->domLabel);
			getRangeLabel().merge(q->back()->getRangeLabel());
		}
}

/// merge sorts for a given role
void DLDag :: mergeSorts ( TRole* R )
{
	// associate role domain labels
	R->mergeSupersDomain();
	merge ( R->getDomainLabel(), R->getBPDomain() );
	// also associate functional nodes (if any)
	for ( const auto& topf: R->topfuncs() )
		merge ( R->getDomainLabel(), topf->getFunctional() );
}

/// merge sorts for a given vertex
void DLDag :: mergeSorts ( DLVertex& v )
{
	switch ( v.Type() )
	{
	case dtLE:	// set R&D for role
	case dtForall:
		v.merge(const_cast<TRole*>(v.getRole())->getDomainLabel());	// domain(role)=cur
		merge ( const_cast<TRole*>(v.getRole())->getRangeLabel(), v.getC() );
		break;
	case dtProj:	// projection: equate R&D of R and ProjR, and D(R) with C
		v.merge(const_cast<TRole*>(v.getRole())->getDomainLabel());
		v.merge(const_cast<TRole*>(v.getProjRole())->getDomainLabel());
		merge ( const_cast<TRole*>(v.getRole())->getDomainLabel(), v.getC() );
		const_cast<TRole*>(v.getRole())->getRangeLabel().merge(const_cast<TRole*>(v.getProjRole())->getRangeLabel());
		break;
	case dtIrr:	// equate R&D for role
		v.merge(const_cast<TRole*>(v.getRole())->getDomainLabel());
		v.merge(const_cast<TRole*>(v.getRole())->getRangeLabel());
		break;
	case dtAnd:
		for ( DLVertex::const_iterator q = v.begin(), q_end = v.end(); q < q_end; ++q )
			merge ( v.getSort(), *q );
		break;
	case dtNSingleton:
	case dtPSingleton:
	case dtPConcept:
	case dtNConcept:	// merge with description
	case dtChoose:
		merge ( v.getSort(), v.getC() );
		break;

	case dtDataType:	// nothing to do
	case dtDataValue:
	case dtDataExpr:
	case dtNN:
		break;
	case dtTop:
	default:
		fpp_unreachable();
	}
}

/// build the sort system for given TBox
void DLDag :: determineSorts ( RoleMaster& ORM, RoleMaster& DRM )
{
	sortArraySize = Heap.size();

	RoleMaster::iterator p, p_end;
	HeapType::iterator i, i_end = Heap.end();

	// init roles R&D sorts
	for ( p = ORM.begin(), p_end = ORM.end(); p < p_end; ++p )
		if ( !(*p)->isSynonym() )
			mergeSorts(*p);
	for ( p = DRM.begin(), p_end = DRM.end(); p < p_end; ++p )
		if ( !(*p)->isSynonym() )
			mergeSorts(*p);

	for ( i = Heap.begin()+2; i < i_end; ++i )
		mergeSorts(**i);

	unsigned int sum = 0;
	for ( i = Heap.begin()+2; i < i_end; ++i )
	{
		mergeableLabel& lab = (*i)->getSort();
		lab.resolve();
		if ( lab.isSample() )
			++sum;
	}

	for ( p = ORM.begin(), p_end = ORM.end(); p < p_end; ++p )
		if ( !(*p)->isSynonym() )
		{
			mergeableLabel& lab = const_cast<TRole*>(*p)->getDomainLabel();
			lab.resolve();
			if ( lab.isSample() )
				++sum;
		}
	for ( p = DRM.begin(), p_end = DRM.end(); p < p_end; ++p )
		if ( !(*p)->isSynonym() )
		{
			mergeableLabel& lab = const_cast<TRole*>(*p)->getDomainLabel();
			lab.resolve();
			if ( lab.isSample() )
				++sum;
		}

	CHECK_LL_RETURN(llAlways);
	// we added a temp concept here; don't count it
	if ( sum > 0 )
		sum--;

	LL << "\nThere are ";
	if ( sum > 1 )
		LL << sum;
	else
		LL << "no";
	LL << " different sorts in TBox\n";
}

#endif	// RKG_USE_SORTED_REASONING
