/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2006-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef DLVHASH_H
#define DLVHASH_H

#include <map>
#include <list>
#include "dlVertex.h"
#include "tRole.h"

/// naive and simple hash table for DL Vertices
class dlVHashTable
{
protected:	// types
		/// hashed IDs of the DL vertices
	typedef std::list<BipolarPointer> HashLeaf;
		/// hash table by itself
	typedef std::map<BipolarPointer, HashLeaf> HashTable;

protected:	// members
		/// host DAG that contains actual nodes;
	const DLDag& host;
		/// HT for nodes
	HashTable Table;

protected:	// methods
		/// get a (very simple) hash of the vertex
	static BipolarPointer hash ( const DLVertex& v )
	{
		BipolarPointer sum = 0;
		if ( v.getRole() != nullptr )
			sum += v.getRole()->getId();
		if ( v.getProjRole() != nullptr )
			sum += v.getProjRole()->getId();
		sum += v.getC();
		sum += v.getNumberLE();
		for ( const auto& arg: v )
			sum += arg;
		return sum;
	}

		/// insert new POSition into a given LEAF
	static void insert ( HashLeaf& leaf, BipolarPointer pos ) { leaf.push_back(pos); }
		/// locate vertex V in a given LEAF
	BipolarPointer locate ( const HashLeaf& leaf, const DLVertex& v ) const;

public:		// interface
		/// empty c'tor
	explicit dlVHashTable ( const DLDag& dag ) : host(dag) {}

		/// add an element (given by a POSition) to hash
	void addElement ( BipolarPointer pos );
		/// locate given vertex in the hash
	BipolarPointer locate ( const DLVertex& v ) const;
}; // dlVHashTable

#endif
