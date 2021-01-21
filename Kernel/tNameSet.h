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

#ifndef TNAMESET_H
#define TNAMESET_H

#include <string>
#include <map>

/// base class for creating Named Entries; template parameter should be derived from TNamedEntry
template <typename T>
class TNameCreator
{
public:		// interface
		/// empty c'tor
	TNameCreator() = default;
		/// empty d'tor
	virtual ~TNameCreator() = default;

		/// create new Named Entry
	virtual T* makeEntry ( const std::string& name ) const { return new T(name); }
}; // TNameCreator


/// Implementation of NameSets by binary trees; template parameter should be derived from TNamedEntry
template <typename T>
class TNameSet
{
protected:	// types
		/// base type
	typedef std::map <std::string, T*> NameTree;

protected:	// members
		/// Base holding all names
	NameTree Base;
		/// creator of new name
	TNameCreator<T>* Creator;

public:		// interface
		/// c'tor (empty)
	TNameSet() : Creator(new TNameCreator<T>) {}
		/// c'tor (with given Name Creating class)
	explicit TNameSet ( TNameCreator<T>* p ) : Creator(p) {}
		/// no copy c'tor
	TNameSet ( const TNameSet& ) = delete;
		/// no assignment
	TNameSet& operator = ( const TNameSet& ) = delete;
		/// d'tor (delete all entries)
	virtual ~TNameSet() { clear(); delete Creator; }

		/// return pointer to existing id or NULL if no such id defined
	T* get ( const std::string& id ) const
	{
		auto p = Base.find(id);
		return p == Base.end() ? nullptr : p->second;
	}
		/// unconditionally add new element with name ID to the set; return new element
	T* add ( const std::string& id )
	{
		T* pne = Creator->makeEntry(id);
		Base[id] = pne;
		return pne;
	}
		/// Insert id to the nameset (if necessary); @return pointer to id structure created by external creator
	T* insert ( const std::string& id )
	{
		T* pne = get(id);
		if ( pne == nullptr )	// no such Id
			pne = add(id);
		return pne;
	}
		/// remove given entry from the set
	void remove ( const std::string& id )
	{
		auto p = Base.find(id);

		if ( p != Base.end () )	// found such Id
		{
			delete p->second;
			Base.erase(p);
		}
	}
		/// clear name set
	void clear ( void )
	{
		for ( auto& p: Base )
			delete p.second;

		Base.clear();
	}
		/// clear the Entry field in all entities
	template <typename U>
	friend void clearEntriesCache ( TNameSet<U>& ns );
		/// get size of a name set
	size_t size ( void ) const { return Base.size(); }
}; // TNameSet

/// clear the Entry field in all entities
/// work only for T derived from TNamedEntity
template <typename T>
void clearEntriesCache ( TNameSet<T>& ns )
{
	for ( auto& bind: ns.Base )
		bind.second->setEntry(nullptr);
}

#endif
