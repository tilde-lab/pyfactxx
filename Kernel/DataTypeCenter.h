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

#ifndef DATATYPECENTER_H
#define DATATYPECENTER_H

#include "tDataType.h"
#include "dltree.h"
#include "tDataTypeBool.h"

class DataTypeReasoner;

class DataTypeCenter
{
public:		// interface
		/// vector of DATATYPEs
	typedef std::vector<TDataType*> TypesVector;

		/// iterator (RW) to access types
	typedef TypesVector::iterator iterator;
		/// iterator (RO) to access types
	typedef TypesVector::const_iterator const_iterator;

protected:	// members
		/// vector of registered data types; initially contains unrestricted NUMBER and STRING
	TypesVector Types;

protected:	// methods
		/// register new data type
	void RegisterDataType ( TDataType* p ) { Types.push_back(p); }

	// access to datatypes

		/// get type corresponding to Numbers
	TDataType* getNumberDataType ( void ) const { return *begin(); }
		/// get type corresponding to Strings
	TDataType* getStringDataType ( void ) const { return *(begin()+1); }
		/// get type corresponding to Real
	TDataType* getRealDataType ( void ) const { return *(begin()+2); }
		/// get type corresponding to Bool
	TDataType* getBoolDataType ( void ) const { return *(begin()+3); }
		/// get type corresponding to date-time
	TDataType* getTimeDataType ( void ) const { return *(begin()+4); }
		/// get type by name
	TDataType* getTypeByName ( const std::string& name ) const;

		/// get type corresponding to given sample
	TDataType* getTypeBySample ( const TDataEntry* sample ) const
	{
		const TDataEntry* type = sample->isBasicDataType() ? sample : sample->getType();

		for ( const_iterator p = begin(), p_end = end(); p < p_end; ++p )
			if ( type == (*p)->getType() )
				return *p;

		return nullptr;
	}

	// DLTree wrapping interface

		/// get DLTree by a given TDE
	static DLTree* wrap ( const TDataEntry* t ) { return createEntry ( DATAEXPR, const_cast<TDataEntry*>(t) ); }
		/// get TDE by a given DLTree
	static TDataEntry* unwrap ( const DLTree* t ) { return static_cast<TDataEntry*>(t->Element().getNE()); }

public:		// interface
		// c'tor: create NUMBER and STRING datatypes
	DataTypeCenter ( void )
	{
		// register primitive DataTypes
		RegisterDataType(new TDataType("number"));
		RegisterDataType(new TDataType("string"));
		RegisterDataType(new TDataType("real"));
		RegisterDataType(new TDataTypeBool());
		RegisterDataType(new TDataType("time"));
		// fresh DT that doesn't appear in KB. FIXME!! make it as TOP later on
		RegisterDataType(new TDataType(" "));
	}
		/// d'tor: delete all datatypes
	~DataTypeCenter();

	// iterators

		/// begin (RW)
	iterator begin ( void ) { return Types.begin(); }
		/// end (RW)
	iterator end ( void ) { return Types.end(); }
		/// begin (RO)
	const_iterator begin ( void ) const { return Types.begin(); }
		/// end (RO)
	const_iterator end ( void ) const { return Types.end(); }

	// DLTree interface

		/// get NUMBER DT that can be used in TBox
	DLTree* getNumberType ( void ) const { return wrap(getNumberDataType()->getType()); }
		/// get STRING DT that can be used in TBox
	DLTree* getStringType ( void ) const { return wrap(getStringDataType()->getType()); }
		/// get REAL DT that can be used in TBox
	DLTree* getRealType ( void ) const { return wrap(getRealDataType()->getType()); }
		/// get BOOL DT that can be used in TBox
	DLTree* getBoolType ( void ) const { return wrap(getBoolDataType()->getType()); }
		/// get TIME DT that can be used in TBox
	DLTree* getTimeType ( void ) const { return wrap(getTimeDataType()->getType()); }
		/// get fresh DT that can be used in TBox
	DLTree* getFreshDataType ( void ) const { return wrap((*(begin()+5))->getType()); }

		/// return registered data value by given NAME of a Type, given by SAMPLE
	DLTree* getDataValue ( const std::string& name, const DLTree* sample ) const
	{
		TDataType* type = isConst(sample) ?
			getStringDataType() :	// data top
			getTypeBySample(unwrap(sample));

		return wrap(type->get(name));	// may throw
	}
		/// get datatype by its name
	DLTree* getDataType ( const std::string& name )
		{ return wrap(getTypeByName(name)->getType()); }
		/// define datatype by NAME as equal to given EXPR. FIXME!! stub for JNI for now
	DLTree* getDataType ( const std::string&, DLTree* expr )
		{ return expr; }

		/// facet for >=/>/</<=
	DLTree* getIntervalFacetExpr ( DLTree* val, bool min, bool excl )
	{
		/// get value as an DTE
		TDataEntry* value = unwrap(val);
		// create new (unnamed) expression
		TDataEntry* ret = getTypeBySample(value)->getExpr();
		// apply appropriate facet to it
		ret->getFacet()->update ( min, excl, value->getComp() );
		deleteTree(val);
		return wrap(ret);
	}

	// reasoning interface

		/// init DT reasoner
	void initDataTypeReasoner ( DataTypeReasoner& DTReasoner ) const;
		/// lock/unlock types for additional elements
	void setLocked ( bool val );
}; // DataTypeCenter

#endif
