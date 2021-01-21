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

#ifndef TDATATYPE_H
#define TDATATYPE_H

#include "tDataEntry.h"
#include "tNECollection.h"

/// class for representing general data type
class TDataType: public TNECollection<TDataEntry>
{
protected:	// members
		/// data type
	TDataEntry* Type;
		/// data type
	std::vector<TDataEntry*> Expr;

protected:	// methods
		/// register data value in the datatype
	void registerNew ( TDataEntry* p ) override { p->setHostType(Type); }

public:		// interface
		/// c'tor: create the TYPE entry
	explicit TDataType ( const std::string& name )
		: TNECollection<TDataEntry>(name)
		{ Type = new TDataEntry(name); }
		/// no copy c'tor
	TDataType ( const TDataType& ) = delete;
		/// no assignment
	TDataType& operator = ( const TDataType& ) = delete;
		/// d'tor: delete data type entry and all the expressions
	~TDataType() override
	{
		for ( auto& p: Expr )
			delete p;
		delete Type;
	}

	// access to the type

		/// get RW access to the type entry (useful for relevance etc)
	TDataEntry* getType ( void ) { return Type; }
		/// get RO access to the type entry
	const TDataEntry* getType ( void ) const { return Type; }

		/// create new expression of the type
	TDataEntry* getExpr ( void )
	{
		TDataEntry* ret = registerElem(new TDataEntry("expr"));
		Expr.push_back(ret);
		return ret;
	}
}; // TDataType

#endif
