/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2005-2015 Dmitry Tsarkov and The University of Manchester
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

#include <cmath>
#include <iostream>

#include "dlTBox.h"

void TBox :: setRelevant ( BipolarPointer p )
{
	fpp_assert ( isValid(p) );

	if ( p == bpTOP || p == bpBOTTOM )
		return;

	const DLVertex& v = DLHeap[p];
	bool pos = isPositive(p);

	++nRelevantBCalls;
	collectLogicFeature(v,pos);

	switch ( v.Type() )
	{
	case dtDataType:	// types and values are not relevant
	case dtDataValue:
	case dtDataExpr:
	case dtNN:			// not appear in any expression => not relevant
		break;

	case dtPConcept:	// negated primitive entries -- does nothing
	case dtPSingleton:
//		if ( !pos )
//			break;

	// fall through
	case dtNConcept:	// named concepts
	case dtNSingleton:
		setRelevant(const_cast<TConcept*>(static_cast<const TConcept*>(v.getConcept())));
		break;

	case dtForall:
	case dtLE:
		setRelevant(const_cast<TRole*>(v.getRole()));
		setRelevant (v.getC());
		break;

	case dtProj:	// no need to set (inverse) roles as it doesn't really matter
	case dtChoose:
		setRelevant(v.getC());
		break;

	case dtIrr:
		setRelevant(const_cast<TRole*>(v.getRole()));
		break;

	case dtAnd:
		for ( DLVertex::const_iterator q = v.begin(); q != v.end(); ++q )
			setRelevant(*q);
		break;

	default:
		std::cerr << "Error setting relevant vertex of type " << v.getTagName() << "(" << v.Type () << ")";
		fpp_unreachable();
	}
}

void TBox :: setRelevant1 ( TConcept* p )
{
	++nRelevantCCalls;
	p->setRelevant(relevance);
	collectLogicFeature(p);
	setRelevant (p->pBody);
}

void TBox :: setRelevant1 ( TRole* p )
{
	p->setRelevant(relevance);
	collectLogicFeature(p);

	// Range and Domain are also relevant
	setRelevant ( p->getBPDomain() );
	setRelevant ( p->getBPRange() );

	// all super-roles are also relevant
	for ( auto& sup: p->ancestors() )
		setRelevant(sup);
}

void TBox :: gatherRelevanceInfo ( void )
{
	nRelevantCCalls = 0;
	nRelevantBCalls = 0;

	// gather GCIs features
	curFeature = &GCIFeatures;
	markGCIsRelevant();
	clearRelevanceInfo();
	KBFeatures |= GCIFeatures;

	// fills in nominal cloud relevance info
	NCFeatures = GCIFeatures;

	// set up relevance info
	for ( i_iterator pi = i_begin(); pi != i_end(); ++pi )
	{
		setConceptRelevant(*pi);
		NCFeatures |= (*pi)->posFeatures;
	}

	// correct NC inverse role information
	if ( NCFeatures.hasSomeAll() && !RelatedI.empty() )
		NCFeatures.setInverseRoles();

	for ( c_iterator pc = c_begin(); pc != c_end(); ++pc )
		setConceptRelevant(*pc);
	auto cSize = ( c_end() - c_begin() ) + ( i_end() - i_begin() );
	size_t bSize = DLHeap.size()-2;

	curFeature = nullptr;

	float cRatio, bRatio = 0, logCSize = 1, logBSize = 1, sqCSize = 1, sqBSize = 1;
	if ( cSize > 10 )
	{
		cRatio = ((float)nRelevantCCalls)/cSize;
		sqCSize = sqrtf((float)cSize);
		if ( cSize > 1 )
			logCSize = logf((float)cSize);
	}
	if ( bSize > 20 )
	{
		bRatio = ((float)nRelevantBCalls)/bSize;
		sqBSize = sqrtf((float)bSize);
		if ( bSize > 1 )
			logBSize = logf((float)bSize);
	}

	if (0)	// relevance stat
	{
	if ( LLM.isWritable(llAlways) && cSize > 10 )
		LL << "There were made " << nRelevantCCalls << " relevance C calls for "
		   << cSize << " concepts\nRC ratio=" << cRatio << ", ratio/logSize="
		   << cRatio/logCSize << ", ratio/sqSize=" << cRatio/sqCSize << ", ratio/size="
		   << cRatio/cSize<< "\n";
	if ( LLM.isWritable(llAlways) && bSize > 20 )
		LL << "There were made " << nRelevantBCalls << " relevance B calls for "
		   << bSize << " nodes\nRB ratio=" << bRatio << ", ratio/logSize="
		   << bRatio/logBSize << ", ratio/sqSize=" << bRatio/sqBSize << ", ratio/size="
		   << bRatio/bSize << "\n";
	}

	// set up GALEN-like flag; based on r/n^{3/2}, add r/n^2<1
	isLikeGALEN = (bRatio > sqBSize*20) && (bRatio < bSize);

	// switch off sorted reasoning iff top role appears
	if ( KBFeatures.hasTopRole() )
		useSortedReasoning = false;
}
