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

#include "dlDag.h"

#include "logging.h"
#include "tDataEntry.h"
#include "tConcept.h"

DLDag :: DLDag ( const ifOptionSet* Options )
	: indexAnd(*this)
	, indexAll(*this)
	, indexLE(*this)
{
	Heap.push_back ( new DLVertex (dtBad) );	// empty vertex -- bpINVALID
	Heap.push_back ( new DLVertex (dtTop) );

	readConfig ( Options );
}

DLDag :: ~DLDag()
{
	for ( HeapType::iterator p = Heap.begin(), p_end = Heap.end(); p < p_end; ++p )
		delete *p;
}

void
DLDag :: removeQuery ( void )
{
	for ( size_t i = size()-1; i >= finalDagSize; --i )
	{
		DLVertex* v = Heap[i];
		switch ( v->Type() )
		{
		case dtData:
			static_cast<TDataEntry*>(v->getConcept())->setBP(bpINVALID);
			break;
		case dtConcept:
			static_cast<TConcept*>(v->getConcept())->clear();
			break;
		default:
			break;
		}
		delete v;
	}
	Heap.resize(finalDagSize);
}

void DLDag :: readConfig ( const ifOptionSet* Options )
{
	fpp_assert ( Options != nullptr );	// safety check

	orSortSat = Options->getText ( "orSortSat" ).c_str();
	orSortSub = Options->getText ( "orSortSub" ).c_str();

	if ( !isCorrectOption(orSortSat) || !isCorrectOption(orSortSub) )
		throw EFaCTPlusPlus ( "DAG: wrong OR sorting options" );
}

/// set defaults of OR orderings
void
DLDag :: setOrderDefaults ( const char* defSat, const char* defSub )
{
	// defaults should be correct
	fpp_assert ( isCorrectOption(defSat) && isCorrectOption(defSub) );

	if ( LLM.isWritable(llAlways) )
		LL << "orSortSat: initial=" << orSortSat << ", default=" << defSat;
	if ( orSortSat[0] == '0' )
		orSortSat = defSat;
	if ( LLM.isWritable(llAlways) )
		LL << ", used=" << orSortSat << "\n"
		   << "orSortSub: initial=" << orSortSub << ", default=" << defSub;
	if ( orSortSub[0] == '0' )
		orSortSub = defSub;
	if ( LLM.isWritable(llAlways) )
		LL << ", used=" << orSortSub << "\n";
}

/// set OR sort flags based on given option string
void DLDag :: setOrderOptions ( const char* opt )
{
	// 0x means not to use OR sort
	if ( opt[0] == '0' )
		return;

	sortAscend = (opt[1] == 'a');
	preferNonGen = (opt[2] == 'p');

	// all statistics use negative version (as it is used in disjunctions)
	iSort = opt[0] == 'S' ? DLVertex::getStatIndexSize(false)
		  : opt[0] == 'D' ? DLVertex::getStatIndexDepth(false)
		  : opt[0] == 'B' ? DLVertex::getStatIndexBranch(false)
		  : opt[0] == 'G' ? DLVertex::getStatIndexGener(false)
		  				  : DLVertex::getStatIndexFreq(false);

	Recompute();
}

void
DLDag :: computeVertexStat ( BipolarPointer p )
{
	DLVertex& v = (*this)[p];
	bool pos = isPositive(p);

	// this vertex is already processed
	if ( v.isProcessed(pos) )
		return;

	// in case of cycle: mark concept as such
	if ( v.isVisited(pos) )
	{
		v.setInCycle(pos);
		return;
	}

	v.setVisited(pos);

	// ensure that the statistic is gather for all sub-concepts of the expression
	switch ( v.Type() )
	{
	case dtAnd:	// check all the conjuncts
		for ( DLVertex::const_iterator q = v.begin(), q_end = v.end(); q < q_end; ++q )
			computeVertexStat ( *q, pos );
		break;
	case dtProj:
		if ( !pos )		// ~Proj -- nothing to do
			break;
		// fallthrough
	case dtName:
	case dtForall:
	case dtChoose:
	case dtLE:	// check a single referenced concept
		computeVertexStat ( v.getC(), pos );
		break;
	default:	// nothing to do
		break;
	}

	v.setProcessed(pos);

	// here all the necessary statistics is gathered -- use it in the init
	updateVertexStat(p);
}

void
DLDag :: updateVertexStat ( BipolarPointer p )
{
	DLVertex& v = (*this)[p];
	bool pos = isPositive(p);

	DLVertex::StatType d = 0, s = 1, b = 0, g = 0;

	if ( !v.omitStat(pos) )
	{
		if ( isValid(v.getC()) )
			updateVertexStat ( v, v.getC(), pos );
		else
			for ( DLVertex::const_iterator q = v.begin(), q_end = v.end(); q < q_end; ++q )
				updateVertexStat ( v, *q, pos );
	}

	// correct values wrt POS
	d = v.getDepth(pos);
	switch ( v.Type() )
	{
	case dtAnd:
		if ( !pos )
			++b;	// OR is branching
		break;
	case dtForall:
		++d;		// increase depth
		if ( !pos )
			++g;	// SOME is generating
		break;
	case dtLE:
		++d;		// increase depth
		if ( !pos )
			++g;	// >= is generating
		else if ( v.getNumberLE() != 1 )
			++b;	// <= is branching
		break;
	case dtProj:
		if ( pos )
			++b;	// projection sometimes involves branching
		break;
	default:
		break;
	}

	v.updateStatValues ( d, s, b, g, pos );
}

/// gather vertex freq statistics
void
DLDag :: computeVertexFreq ( BipolarPointer p )
{
	DLVertex& v = (*this)[p];
	bool pos = isPositive(p);

	if ( v.isVisited(pos) )	// avoid cycles
		return;

	v.incFreqValue(pos);	// increment frequency of current vertex
	v.setVisited(pos);

	if ( v.omitStat(pos) )	// negation of primitive concept-like
		return;

	// increment frequency of all sub-vertices
	if ( isValid(v.getC()) )
		computeVertexFreq ( v.getC(), pos );
	else
		for ( DLVertex::const_iterator q = v.begin(), q_end = v.end(); q != q_end; ++q )
			computeVertexFreq ( *q, pos );
}

void
DLDag :: gatherStatistic ( void )
{
	// gather main statistics for disjunctions
	for ( StatVector::iterator p = listAnds.begin(), p_end = listAnds.end(); p < p_end; ++p )
		computeVertexStat(inverse(*p));

	// if necessary -- gather frequency
	if ( orSortSat[0] != 'F' && orSortSub[0] != 'F' )
		return;

	clearDFS();

	for ( BipolarPointer i = (BipolarPointer)size()-1; i > 1; --i )
	{
		if ( isCNameTag((*this)[i].Type()) )
			computeVertexFreq(i);
	}
}

//---------------------------------------------------
// change order of ADD elements with respect to Freq
//---------------------------------------------------

/// return true if p1 is less than p2 using chosen sort order

// the overall sorted entry structure looks like
//   fffA..M..Zlll if sortAscend set, and
//   fffZ..M..Alll if sortAscend cleared.
// here 's' means "always first" entries, like neg-primconcepts,
//  and 'l' means "always last" entries, like looped concepts

// note that the statistics is given for disjunctions,
// so inverted (neg) values are taken into account
bool DLDag :: less ( BipolarPointer p1, BipolarPointer p2 ) const
{
#	ifdef ENABLE_CHECKING
		fpp_assert ( isValid(p1) && isValid(p2) );
#	endif

	// idea: any positive entry should go first
	if ( preferNonGen )
	{
		if ( isNegative(p1) && isPositive(p2) )
			return true;
		if ( isPositive(p1) && isNegative(p2) )
			return false;
	}

	const DLVertex& v1 = (*this)[p1];
	const DLVertex& v2 = (*this)[p2];
/*
	// prefer non-cyclical
	if ( !v1.isInCycle(false) && v2.isInCycle(false) )
		return true;
	if ( !v2.isInCycle(false) && v1.isInCycle(false) )
		return false;
*/
	DLVertex::StatType key1 = v1.getStat(iSort);
	DLVertex::StatType key2 = v2.getStat(iSort);

	// return "less" wrt sortAscend
	if ( sortAscend )
		return key1 < key2;
	else
		return key2 < key1;
}

#ifdef RKG_PRINT_DAG_USAGE
/// print usage of DAG
void DLDag :: PrintDAGUsage ( std::ostream& o ) const
{
	unsigned int n = 0;	// number of no-used DAG entries
	unsigned int total = Heap.size()*2-2;	// number of total DAG entries

	for ( HeapType::const_iterator i = Heap.begin(), i_end = Heap.end(); i < i_end; ++i )
	{
		if ( (*i)->getUsage(true) == 0 )	// positive and...
			++n;
		if ( (*i)->getUsage(false) == 0 )	// negative ones
			++n;
	}

	o << "There are " << n << " unused DAG entries (" << (unsigned long)(n*100/total)
	  << "% of " << total << " total)\n";
}
#endif
