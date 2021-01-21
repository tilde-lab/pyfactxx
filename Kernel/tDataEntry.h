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

#ifndef TDATAENTRY_H
#define TDATAENTRY_H

#include "tNamedEntry.h"
#include "BiPointer.h"
#include "tLabeller.h"
#include "DataTypeComparator.h"

// if min() and max() are defined, then they conflicts with a TDI members
#undef min
#undef max

/// class for representing general data restriction
class TDataInterval
{
public:		// members
		/// left border of the interval
	ComparableDT min;
		/// right border of the interval
	ComparableDT max;
		/// type of the left border
	bool minExcl;
		/// type of the right border
	bool maxExcl;

public:		// interface

		/// clear an interval
	void clear ( void ) { min = max = ComparableDT(); }

		/// check if min value range have been set
	bool hasMin ( void ) const { return min.inited(); }
		/// check if max value range have been set
	bool hasMax ( void ) const { return max.inited(); }
		/// no constraints
	bool empty ( void ) const { return !hasMin() && !hasMax(); }
		/// closed interval
	bool closed ( void ) const { return hasMin() && hasMax(); }

		/// update MIN border of an interval with VALUE wrt EXCL
	bool updateMin ( bool excl, const ComparableDT& value )
	{
		if ( hasMin() )	// another min value: check if we need update
		{
			// constraint is >= or >
			if ( min > value )		// was: {7,}; now: {5,}: no update needed
				return false;
			if ( min == value &&	// was: (5,}; now: [5,}: no update needed
				 minExcl && !excl )
				return false;
			// fallthrough: update is necessary for everything else
		}

		min = value;
		minExcl = min.correctMin(excl);
		return true;
	}
		/// update MAX border of an interval with VALUE wrt EXCL
	bool updateMax ( bool excl, const ComparableDT& value )
	{
		if ( hasMax() )	// another max value: check if we need update
		{
			// constraint is <= or <
			if ( max < value )		// was: {,5}; now: {,7}: no update needed
				return false;
			if ( max == value &&	// was: {,5); now: {,5]: no update needed
				 maxExcl && !excl )
				return false;
			// fallthrough: update is necessary for everything else
		}

		max = value;
		maxExcl = max.correctMax(excl);
		return true;
	}
		/// update given border of an interval with VALUE wrt EXCL
	bool update ( bool min, bool excl, const ComparableDT& value )
		{ return min ? updateMin ( excl, value ) : updateMax ( excl, value ); }
		/// update wrt another interval
	bool update ( const TDataInterval& Int )
	{
		bool ret = false;
		if ( Int.hasMin() )
			ret |= updateMin ( Int.minExcl, Int.min );
		if ( Int.hasMax() )
			ret |= updateMax ( Int.maxExcl, Int.max );
		return ret;
	}
		/// @return true iff all the data is consistent wrt given TYPE
	bool consistent ( const ComparableDT& dtype ) const
	{
		if ( hasMin() && !min.compatible(dtype) )
			return false;
		if ( hasMax() && !max.compatible(dtype) )
			return false;
		return true;
	}

		/// print an interval
	void Print ( std::ostream& o ) const
	{
		if ( hasMin() )
			o << (minExcl ? '(' : '[') << min;
		else
			o << '{';
		o << ',';
		if ( hasMax() )
			o << max << (maxExcl ? ')' : ']');
		else
			o << '}';
	}
		/// print an interval as a LISP; FIXME!! hack
	void printLISP ( std::ostream& o, const char* type ) const
	{
		if ( hasMin() && hasMax() )
			o << "(and ";
		if ( hasMin() )
		{
			o << "(g" << (minExcl ? 't' : 'e') << " (" << type;
			min.printValue(o);
			o << "))";
		}
		if ( hasMax() )
		{
			o << "(l" << (maxExcl ? 't' : 'e') << " (" << type;
			max.printValue(o);
			o << "))";
		}
		if ( hasMin() && hasMax() )
			o << ")";
	}
}; // TDataInterval

/// class for representing general data entry ("name" of data type or data value)
class TDataEntry: public TNamedEntry
{
private:	// members
		/// label to use in relevant-only checks
	TLabeller::LabelType rel;

protected:	// members
		/// corresponding type (Type has NULL in the field)
	const TDataEntry* Type;
		/// DAG index of the entry
	BipolarPointer pName;
		/// ComparableDT, used only for values
	ComparableDT comp;
		/// restriction to the entry
	TDataInterval Constraints;

protected:	// methods
		/// set COMP for the (typed) data value
	void setComp ( const std::string& typeName )
	{
		// FIXME!! do the thing properly; unify the usage of DT names
		if ( typeName == "string" )
			comp = ComparableDT(getName());
		else if ( typeName == "number" )
			comp = ComparableDT(atol(getName()));
		else if ( typeName == "real" )
			comp = ComparableDT((float)atof(getName()));
		else if ( typeName == "bool" )	// FIXME!! dirty hack
			comp = ComparableDT(getName());
		else if ( typeName == "time" )
			comp = ComparableDT ( atol(getName()), 0 );
		else	// no more types available
			fpp_unreachable();
	}

public:		// interface
		/// create data entry with given name
	explicit TDataEntry ( const std::string& name )
		: TNamedEntry(name)
		, Type(nullptr)
		, pName(bpINVALID)
		, comp()
		{}
		/// no copy c'tor
	TDataEntry ( const TDataEntry& ) = delete;
		/// no assignment
	TDataEntry& operator = ( const TDataEntry& ) = delete;

	// type/value part

		/// check if data entry represents basic data type
	bool isBasicDataType ( void ) const { return Type == nullptr && Constraints.empty(); }
		/// check if data entry represents restricted data type
	bool isRestrictedDataType ( void ) const { return !Constraints.empty(); }
		/// check if data entry represents data value
	bool isDataValue ( void ) const { return Type != nullptr && Constraints.empty(); }

		/// set host data type for the data value
	void setHostType ( const TDataEntry* type ) { Type = type; setComp(type->getName()); }
		/// get host type
	const TDataEntry* getType ( void ) const { return Type; }

		/// get comparable variant of DE
	const ComparableDT& getComp ( void ) const { return comp; }

	// facet part

		/// get RW access to constraints of the DE
	TDataInterval* getFacet ( void ) { return &Constraints; }
		/// get RO access to constraints of the DE
	const TDataInterval* getFacet ( void ) const { return &Constraints; }

	// relevance part

		/// is given concept relevant to given Labeller's state
	bool isRelevant ( const TLabeller& lab ) const { return lab.isLabelled(rel); }
		/// make given concept relevant to given Labeller's state
	void setRelevant ( const TLabeller& lab ) { lab.set(rel); }

	// BP part

		/// get pointer to DAG entry corresponding to the data entry
	BipolarPointer getBP ( void ) const { return pName; }
		/// set DAG index of the data entry
	void setBP ( BipolarPointer p ) { pName = p; }

		// printing LISP FIXME!!
	void printLISP ( std::ostream& o ) const
	{
		o << ' ';
		if ( isBasicDataType() )
			o << "(" << getName() << ")";
		else if ( isDataValue() )
		{
			o << "(" << getType()->getName();
			comp.printValue(o);
			o << ")";
		}
		else if ( isRestrictedDataType() )
			Constraints.printLISP ( o, getType()->getName() );
		else
			fpp_unreachable();
	}
}; // TDataEntry

inline std::ostream&
operator << ( std::ostream& o, const TDataInterval& c )
{
	c.Print(o);
	return o;
}

#endif
