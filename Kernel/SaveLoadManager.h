/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2013-2015 Dmitry Tsarkov and The University of Manchester
Copyright (C) 2015-2017 Dmitry Tsarkov

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*/

#ifndef SAVELOADMANAGER_H
#define SAVELOADMANAGER_H

#include <string>
#include <iostream>
#include <vector>
#include <map>

#include "globaldef.h"
#include "eFPPSaveLoad.h"

class TNamedEntity;
class TNamedEntry;
class TaxonomyVertex;

class SaveLoadManager
{
protected:	// types
		// maps pointers and numbers
	template <typename T>
	class PointerMap
	{
	protected:	// types
			/// map int->pointer type
		typedef std::vector<const T*> I2PMap;
			/// map pointer->int type
		typedef std::map<const T*, unsigned int> P2IMap;

	protected:	// members
			/// map i -> pointer
		I2PMap i2p;
			/// map pointer -> i
		P2IMap p2i;
			/// ID of the last recorded NE
		unsigned int last = 0;

	protected:	// methods
			/// @return true if given pointer present in the map
		bool in ( const T* p ) const { return p2i.find(p) != p2i.end(); }
			/// @return true if given index present in the map
		bool in ( unsigned int i ) const { return i < last; }
			/// @throw an exception if P is not registered
		void ensure ( const T* p ) const { if ( !in(p) ) throw EFPPSaveLoad("Cannot save unregistered pointer"); }
			/// @throw an exception if I is not registered
		void ensure ( unsigned int i ) const { if ( !in(i) ) throw EFPPSaveLoad("Cannot load unregistered index"); }

	public:		// interface
			/// clear the maps
		void clear ( void )
		{
			i2p.clear();
			p2i.clear();
			last = 0;
		}

		// populate the map

			/// add an entry
		void add ( T* p )
		{
			if ( in(p) )
				return;
			i2p.push_back(p);
			p2i[p] = last++;
		}

		// access to the mapped element

			/// get the NE by index I
		T* getP ( unsigned int i ) { ensure(i); return const_cast<T*>(i2p[i]); }
			/// get the index by NE P
		unsigned int getI ( const T* p ) { ensure(p); return p2i[p]; }
	}; // PointerMap

protected:	// members
		/// name of S/L dir
	std::string dirname;
		/// file name
	std::string filename;
		/// input stream pointer
	std::istream* ip = nullptr;
		/// output stream pointer
	std::ostream* op = nullptr;

		// uint <-> named entity map for the current taxonomy
	PointerMap<TNamedEntity> eMap;
		// uint <-> named entry map for the current taxonomy
	PointerMap<TNamedEntry> neMap;
		// uint <-> TaxonomyVertex map to update the taxonomy
	PointerMap<TaxonomyVertex> tvMap;

public:		// methods
		/// init c'tor: remember the S/L name
	explicit SaveLoadManager ( const std::string& name ) : dirname(name) { filename = name+".fpp.state"; }
		/// empty d'tor
	~SaveLoadManager()
	{
		delete ip;
		delete op;
	}

	// context information

		/// @return true if there is some S/L content
	bool existsContent ( void ) const;
		/// clear all the content corresponding to the manager
	void clearContent ( void ) const;

	// set up stream

		/// prepare stream according to INPUT value
	void prepare ( bool input );
		/// get an input stream
	std::istream& i ( void ) { return *ip; }
		/// get an output stream
	std::ostream& o ( void ) { return *op; }
		/// check whether stream is in a good shape
	void checkStream ( void ) const
	{
		if ( ip && unlikely(!ip->good()) )
			throw EFPPSaveLoad ( filename, /*save=*/false);
		if ( op && unlikely(!op->good()) )
			throw EFPPSaveLoad ( filename, /*save=*/true);
	}

	// save/load primitives

		/// load a single char from input, throw an exception if it is not a given one
	inline void expectChar ( const char C )
	{
		char c;
		i() >> c;
		if ( c != C )
			throw EFPPSaveLoad(C);
	}

	// save/load integers

		/// save unsigned integer
	inline void saveUInt ( unsigned int n ) { o() << "(" << n << ")"; }
		/// save signed integer
	inline void saveSInt ( int n ) { o() << "(" << n << ")"; }
		/// load unsigned integer
	inline unsigned int loadUInt ( void )
	{
		unsigned int ret;
		expectChar('(');
		i() >> ret;
		expectChar(')');
		return ret;
	}
		/// load signed integer
	inline int loadSInt ( void )
	{
		int ret;
		expectChar('(');
		i() >> ret;
		expectChar(')');
		return ret;
	}

	// pointer <-> int related methods

		/// clear all maps
	void clearPointerMaps ( void )
	{
		neMap.clear();
		eMap.clear();
		tvMap.clear();
	}
		/// register named entry together with entity (if available)
	void registerE ( const TNamedEntry* p );
		/// register taxonomy vertex
	void registerV ( TaxonomyVertex* v ) { tvMap.add(v); }

		/// save Entry pointer
	void savePointer ( const TNamedEntry* p ) { saveUInt(neMap.getI(p)); }
		/// save Entity pointer
	void savePointer ( const TNamedEntity* p ) { saveUInt(eMap.getI(const_cast<TNamedEntity*>(p))); }
		/// save Vertex pointer
	void savePointer ( const TaxonomyVertex* p ) { saveUInt(tvMap.getI(const_cast<TaxonomyVertex*>(p))); }

		/// load Entry pointer
	TNamedEntry* loadEntry ( void ) { return neMap.getP(loadUInt()); }
		/// load Entity pointer
	TNamedEntity* loadEntity ( void ) { return eMap.getP(loadUInt()); }
		/// load Vertex pointer
	TaxonomyVertex* loadVertex ( void ) { return tvMap.getP(loadUInt()); }
}; // SaveLoadManager

#endif
