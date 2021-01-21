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

#ifndef DATATYPECOMPARATOR_H
#define DATATYPECOMPARATOR_H

#include <ctime>
#include <string>
#include <ostream>

#include "fpp_assert.h"
#include "globaldef.h"

/// allows one to compare DataType values wrt appropriate type
class ComparableDT
{
private:	// members
		/// value as a string
	std::string strValue;
		/// value as a number
	long longIntValue = 0;
		/// value as a float
	float floatValue = 0.0;
		/// value as a date
	long timeValue = 0;	// FIXME!! FORNOW
		/// tag of a value
	enum ValueType { UNUSED = 0, INT, STR, FLOAT, TIME } vType = UNUSED;

public:		// interface
		/// create empty dt
	ComparableDT() = default;
		/// create NUMBER's dt
	explicit ComparableDT ( long int value )
		: longIntValue(value)
		, vType(INT)
		{}
		/// create STRING's dt
	explicit ComparableDT ( const char* value )
		: strValue(value)
		, vType(STR)
		{}
		/// create FLOAT's dt
	explicit ComparableDT ( float value )
		: floatValue(value)
		, vType(FLOAT)
		{}
		/// create dateTime's dt; use dummy parameter to distinguish it from INT one
	explicit ComparableDT ( long value, int )
		: timeValue(value)
		, vType(TIME)
		{}
		/// copy c'tor
	ComparableDT ( const ComparableDT& copy ) = default;
		/// assignment
	ComparableDT& operator= ( const ComparableDT& copy ) = default;

		/// get NUMBER
	long int getLongIntValue ( void ) const { return longIntValue; }
		/// get STRING
	const std::string& getStringValue ( void ) const { return strValue; }
		/// get FLOAT
	float getFloatValue ( void ) const { return floatValue; }
		/// get TIME
	long getTimeValue ( void ) const { return timeValue; }
		/// check if the datatype is discrete
	bool hasDiscreteType ( void ) const { return vType == INT || vType == TIME; }
		/// check whether the comparator is inited
	bool inited ( void ) const { return vType != UNUSED; }
		/// check whether the comparator is compatible with another one
	bool compatible ( const ComparableDT& other ) const { return !inited() || !other.inited() || vType == other.vType; }
		/// correct min value if the DT is discrete and EXCL is true; @return new EXCL value
	bool correctMin ( bool excl )
	{
		if ( vType == INT && excl )
		{	// transform (n,} into [n+1,}
			longIntValue++;
			return false;
		}
		if ( vType == TIME && excl )
		{	// transform (n,} into [n+1,}
			timeValue++;
			return false;
		}
		return excl;
	}
		/// correct max value if the DT is discrete and EXCL is true; @return new EXCL value
	bool correctMax ( bool excl )
	{
		if ( vType == INT && excl )
		{	// transform (n,} into [n+1,}
			longIntValue--;
			return false;
		}
		if ( vType == TIME && excl )
		{	// transform (n,} into [n+1,}
			timeValue--;
			return false;
		}
		return excl;
	}

	// compare 2 values

		/// check whether 2 DT entries with the same TYPE are equal
	bool operator == ( const ComparableDT& other ) const
	{
		fpp_assert ( vType == other.vType );	// sanity check
		switch ( vType )
		{
		case INT:	return getLongIntValue() == other.getLongIntValue();
		case STR:	return getStringValue() == other.getStringValue();
		case FLOAT:	return getFloatValue() == other.getFloatValue();
		case TIME:	return getTimeValue() == other.getTimeValue();
		default:	fpp_unreachable();
		}
	}
		/// check whether 2 DT entries with the same TYPE are NOT equal
	bool operator != ( const ComparableDT& other ) const
		{ return !(*this == other); }
		/// check whether 2 DT entries with the same TYPE are in '<' relation
	bool operator < ( const ComparableDT& other ) const
	{
		fpp_assert ( vType == other.vType );	// sanity check
		switch ( vType )
		{
		case INT:	return getLongIntValue() < other.getLongIntValue();
		case STR:	return getStringValue() < other.getStringValue();
		case FLOAT:	return getFloatValue() < other.getFloatValue();
		case TIME:	return getTimeValue() < other.getTimeValue();
		default:	fpp_unreachable();
		}
	}
		/// check whether 2 DT entries with the same TYPE are in '>' relation
	bool operator > ( const ComparableDT& other ) const { return other < *this; }

		/// print the LISP value of a DTC; FIXME!! hack
	std::ostream& printValue ( std::ostream& o ) const
	{
		o << ' ';
		switch ( vType )
		{
		case INT:	o << getLongIntValue(); break;
		case FLOAT:	o << getFloatValue(); break;
		case STR:	o << '"' << getStringValue() << '"'; break;
		case TIME:	o << getTimeValue(); break;
		default:	fpp_unreachable();
		}
		return o;
	}
	friend std::ostream& operator << ( std::ostream& o, const ComparableDT& cdt );
}; // ComparableDT

inline
std::ostream& operator << ( std::ostream& o, const ComparableDT& cdt )
{
	switch ( cdt.vType )
	{
	case ComparableDT::INT:	o << cdt.getLongIntValue(); break;
	case ComparableDT::STR:	o << cdt.getStringValue(); break;
	case ComparableDT::FLOAT:	o << cdt.getFloatValue(); break;
	case ComparableDT::TIME:	o << cdt.getTimeValue(); break;
	default:	fpp_unreachable();
	}
	return o;
}

#endif
