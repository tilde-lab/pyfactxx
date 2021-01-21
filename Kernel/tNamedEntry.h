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

#ifndef TNAMEDENTRY_H
#define TNAMEDENTRY_H

#include <string>
#include <ostream>

#include "flags.h"

class TNamedEntity;
class SaveLoadManager;

class TNamedEntry: public Flags
{
protected:	// members
		/// name of the entry
	std::string extName;
		/// entry identifier
	int extId = 0;
		/// original entity
	const TNamedEntity* entity = nullptr;

public:		// interface
		/// the only c'tor
	explicit TNamedEntry ( const std::string& name )
		: extName (name)		// copy name
		{}
		/// no copy c'tor
	TNamedEntry ( const TNamedEntry& ) = delete;
		/// no assignment
	TNamedEntry& operator = ( const TNamedEntry& ) = delete;

		/// gets name of given entry
	const char* getName ( void ) const { return extName.c_str(); }

		/// set internal ID
	void setId ( int id ) { extId = id; }
		/// get internal ID
	int getId ( void ) const { return extId; }

		/// set entity
	void setEntity ( const TNamedEntity* e ) { entity = e; }
		/// get entity
	const TNamedEntity* getEntity ( void ) const { return entity; }

		/// register a System flag
	FPP_ADD_FLAG(System,0x1);

	// hierarchy interface

		/// register a Top-of-the-hierarchy flag
	FPP_ADD_FLAG(Top,0x1000);
		/// register a Bottom-of-the-hierarchy flag
	FPP_ADD_FLAG(Bottom,0x2000);

	virtual void Print ( std::ostream& o ) const { o << getName (); }

	// save/load interface; implementation is in SaveLoad.cpp

		/// save entry
	virtual void Save ( SaveLoadManager& ) const;
		/// load entry
	virtual void Load ( SaveLoadManager& );
}; // TNamedEntry

#endif
