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

#ifndef TNECOLLECTION_H
#define TNECOLLECTION_H

#include <vector>
#include <set>

#include "tNamedEntry.h"
#include "tNameSet.h"
#include "eFPPCantRegName.h"

/** class for collect TNamedEntry'es together. Template parameter should be
	inherited from TNamedEntry. Implemented as vector of T*,
	with Base[i]->getId() == i.
**/
template <typename T>
class TNECollection
{
protected:	// typedefs
	typedef std::vector<T*> BaseType;

public:		// typedefs
	typedef typename BaseType::iterator iterator;
	typedef typename BaseType::const_iterator const_iterator;

protected:	// members
		/// vector of elements
	BaseType Base;
		/// nameset to hold the elements
	TNameSet<T> NameSet;
		/// name of the type
	std::string TypeName;
		/// flag to lock the nameset (ie, prohibit to add new names there)
	bool locked = false;
		/// if true, allow fresh entities even when locked. treat them as System in this case
	bool allowFresh = false;

protected:	// methods
		/// virtual method for additional tuning of newly created element
	virtual void registerNew ( T* ) {}
		/// register new element in a collection; return this element
	T* registerElem ( T* p )
	{
		p->setId((int)Base.size());
		Base.push_back(p);
		registerNew(p);
		return p;
	}

public:		// interface
		/// c'tor: clear 0-th element
	explicit TNECollection ( const std::string& name )
		: TypeName(name)
		{ Base.push_back(nullptr); }
		/// empty d'tor: all elements will be deleted in other place
	virtual ~TNECollection() = default;

	// locked interface

		/// check if collection is locked
	bool isLocked ( void ) const { return locked; }
		/// set LOCKED value to a VAL; @return old value of LOCKED
	bool setLocked ( bool val ) { bool old = locked; locked = val; return old; }
		/// set FRESH value to a VAL; @return the old value
	bool setAllowFresh ( bool val ) { bool old = allowFresh; allowFresh = val; return old; }

	// add/remove elements

		/// check if entry with a NAME is registered in given collection
	bool isRegistered ( const std::string& name ) const { return NameSet.get(name) != nullptr; }
		/// get entry by NAME from the collection; register it if necessary
	T* get ( const std::string& name )
	{
		T* p = NameSet.get(name);

		// check if name is already defined
		if ( p != nullptr )
			return p;

		// check if it is possible to insert name
		if ( isLocked() && !allowFresh )
			throw EFPPCantRegName ( name, TypeName );

		// create name in name set, and register it
		p = registerElem(NameSet.add(name));

		// if fresh entity -- mark it System
		if ( isLocked() )
		{
			p->setSystem();
			if ( auto ce = dynamic_cast<ClassifiableEntry*>(p) )
				ce->setNonClassifiable();
		}
		return p;
	}
		/// remove given entry from the collection; @return true iff it was NOT the last entry.
	bool Remove ( T* p )
	{
		if ( !isRegistered(p->getName()) )	// not in a name-set: just delete it
		{
			delete p;
			return false;
		}
		// we might delete vars in order (6,7), so the resize should be done to 6
		if ( p->getId() > 0 && Base.size() > (size_t)p->getId() )
			Base.resize(p->getId());
		NameSet.remove(p->getName());
		return false;
	}

	// access to elements

	iterator begin ( void ) { return Base.begin()+1; }
	const_iterator begin ( void ) const { return Base.begin()+1; }

	iterator end ( void ) { return Base.end(); }
	const_iterator end ( void ) const { return Base.end(); }

	size_t size ( void ) const { return Base.size()-1; }
}; // TNECollection

#endif
