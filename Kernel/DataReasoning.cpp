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

#include "DataReasoning.h"

//toms code start
bool DataTypeReasoner :: addDataEntry ( BipolarPointer p, const DepSet& dep )
{
	switch ( DLHeap[p].Type() )
	{
	case dtDataType:		// get appropriate type
	{
		DataTypeAppearance* type = getDTAbyType(getDataEntry(p));

		if ( LLM.isWritable(llCDAction) )	// level of logging
			LL << ' ' << (isPositive(p) ? '+' : '-') << getDataEntry(p)->getName();

		return setTypePresence ( type, isPositive(p), dep );
	}
	case dtDataValue:
		return processDataValue ( isPositive(p), getDataEntry(p), dep );
	case dtDataExpr:
		return processDataExpr ( isPositive(p), getDataEntry(p), dep );
	case dtAnd:		// processed by general reasoning
		return false;
	default:
		fpp_unreachable();
	}
}

// ---------- Processing different alternatives

bool
DataTypeAppearance::DepInterval :: checkMinMaxClash ( DepSet& dep ) const
{
	// we are interested in a NEG intervals iff a PType is set
	if ( !Constraints.closed() )
		return false;

	const ComparableDT& Min = Constraints.min;
	const ComparableDT& Max = Constraints.max;

	// normal interval
	if ( Min < Max )
		return false;

	// >?x and <?y leads to clash for y < x
	// >5 and <5, >=5 and <5, >5 and <=5 leads to clash
	if ( Max < Min || Constraints.minExcl || Constraints.maxExcl )
	{
		dep += locDep;
		return true;
	}

	return false;
}

bool
DataTypeAppearance :: addPosInterval ( const TDataInterval& Int, const DepSet& dep )
{
	DTConstraint aux;
	if ( Int.hasMin() )
	{
		Constraints.swap(aux);
		setLocal ( /*min=*/true, /*excl=*/Int.minExcl, Int.min, dep );
		if ( addIntervals ( aux.begin(), aux.end() ) )
			return true;
		aux.clear();
	}
	if ( Int.hasMax() )
	{
		Constraints.swap(aux);
		setLocal ( /*min=*/false, /*excl=*/Int.maxExcl, Int.max, dep );
		if ( addIntervals ( aux.begin(), aux.end() ) )
			return true;
		aux.clear();
	}
	if ( Constraints.empty() )
		return reportClash ( accDep, "C-MM" );
	return false;
}

bool
DataTypeAppearance :: addNegInterval ( const TDataInterval& Int, const DepSet& dep )
{
	// negative interval -- make a copies
	DTConstraint aux;
	Constraints.swap(aux);

	if ( Int.hasMin() )
	{
		setLocal ( /*min=*/false, /*excl=*/!Int.minExcl, Int.min, dep );
		if ( addIntervals ( aux.begin(), aux.end() ) )
			return true;
	}
	if ( Int.hasMax() )
	{
		setLocal ( /*min=*/ true, /*excl=*/!Int.maxExcl, Int.max, dep );
		if ( addIntervals ( aux.begin(), aux.end() ) )
			return true;
	}
	aux.clear();
	if ( Constraints.empty() )
		return reportClash ( accDep, "C-MM" );
	return false;
}

// comparison methods

/// @return true iff there is at least one point that two DTA share
bool
DataTypeAppearance :: operator == ( const DataTypeAppearance& other ) const
{
	if ( Constraints.size() != 1 && other.Constraints.size() != 1 )
		return false;	// FORNOW: just a single interval
	const TDataInterval& i0 = Constraints[0].getDataInterval();
	const TDataInterval& i1 = other.Constraints[0].getDataInterval();
	if ( !i0.closed() || !i1.closed() )	// FORNOW: only closed ones
		return false;
	const ComparableDT& min0 = i0.min;
	const ComparableDT& min1 = i1.min;
	const ComparableDT& max0 = i0.max;
	const ComparableDT& max1 = i1.max;
	if ( max0 < min1 || max1 < min0 )	// no intersection
		return false;
	// here there is an intersection: minx-miny-maxz-maxt
	if ( min0 == max1 && (i0.minExcl || i1.maxExcl) )	// no touch with a border
		return false;
	if ( min1 == max0 && (i1.minExcl || i0.maxExcl) )	// no touch with a border
		return false;
	return true;
}

/// @return true iff there is at least one point in OTHER that there is not in THIS
bool
DataTypeAppearance :: operator < ( const DataTypeAppearance& other ) const
{
	if ( Constraints.size() != 1 && other.Constraints.size() != 1 )
		return false;	// FORNOW: just a single interval
	const TDataInterval& i0 = Constraints[0].getDataInterval();
	const TDataInterval& i1 = other.Constraints[0].getDataInterval();
	if ( !i1.hasMax() )	// always can find larger one
		return true;
	// here i1.max exists
	if ( !i0.hasMin() )	// always can find a smaller one
		return true;
	// here i0.min exists
	if ( i0.min < i1.max )	// {5,} and {,7}
		return true;
	if ( i0.min == i1.max && i0.minExcl && !i1.maxExcl )
		return true;	// (5,} and {,5]
	// note that (6,} and {,5] are not in the < relation
	return false;
}

