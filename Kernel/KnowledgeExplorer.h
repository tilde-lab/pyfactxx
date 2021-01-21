/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2011-2015 Dmitry Tsarkov and The University of Manchester
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

#ifndef KNOWLEDGEEXPLORER_H
#define KNOWLEDGEEXPLORER_H

#include "taxNamEntry.h"
#include "tDag2Interface.h"

// forward declarations
class TBox;
class DlCompletionTree;

/// class to perform a knowledge exploration
class KnowledgeExplorer
{
public:		// types
		/// type for the node in the completion graph
	typedef const DlCompletionTree TCGNode;
		/// type for the node vector
	typedef std::vector<TCGNode*> TCGNodeVec;
		/// type for a set of role expressions (used in KE to return stuff)
	typedef std::set<const TDLRoleExpression*> TCGRoleSet;
		/// type for a vector of data/concept expressions (used in KE to return stuff)
	typedef std::vector<const TDLExpression*> TCGItemVec;

protected:	// classes
		/// class that maps named entity to a set of ENTITIES
	template <typename Entity>
	class EE2Map
	{
	public:		// type interface
			/// set of entities
		typedef std::set<const Entity*> ESet;
			/// RO iterator to the ESet
		typedef typename ESet::const_iterator iterator;
	protected:	// members
			/// the map itself
		std::map<const TNamedEntity*, ESet> Base;
	public:		// interface
			/// @return true if the entry does not have correspondent entity
		bool check ( const TNamedEntry* e ) const;
			/// add an entity corresponding E to a map corresponding to E0
		void add ( const TNamedEntry* e0, const TNamedEntry* e )
		{
			// check for artificial constructions
			if ( check(e) || check(e0) )
				return;
			const TNamedEntity* E0 = e0->getEntity(), *E = e->getEntity();
			Base[E0].insert(dynamic_cast<const Entity*>(E));
		}
			/// get the set corresponding to the entity E
		const ESet& get ( const TNamedEntity* e ) { return Base[e]; }
			/// get the begin iterator of a set corresponding to the entity E
		iterator begin ( const TNamedEntity* e ) { return Base[e].begin(); }
			/// get the end iterator of a set corresponding to the entity E
		iterator end ( const TNamedEntity* e ) { return Base[e].end(); }
	}; // EE2Map

protected:	// members
		/// map concept into set of its synonyms
	EE2Map<TDLConceptName> Cs;
		/// map individual into set of its synonyms
	EE2Map<TDLIndividualName> Is;
		/// map object role to the set of its super-roles (self included)
	EE2Map<TDLObjectRoleName> ORs;
		/// map data role to the set of its super-roles (self included)
	EE2Map<TDLDataRoleName> DRs;
		/// dag-2-interface translator used in knowledge exploration
	TDag2Interface D2I;
		/// node vector to return
	TCGNodeVec Nodes;
		/// role set to return
	TCGRoleSet Roles;
		/// concept vector to return
	TCGItemVec Concepts;

protected:	// methods
		/// adds an entity as a synonym to a map MAP
	template <typename Entity>
	void addE ( EE2Map<Entity>& map, const ClassifiableEntry* entry )
	{
		map.add(entry,entry);
		if ( entry->isSynonym() )
			map.add ( entry->getSynonym(), entry );
	}
		/// add concept-like expression E (possibly with synonyms) to CONCEPTS
	void addC ( const TDLExpression* e );

public:		// interface
		/// init c'tor
	KnowledgeExplorer ( const TBox* box, TExpressionManager* pEM );

		/// @return the set of data neighbours of a NODE
	const TCGRoleSet& getDataRoles ( const TCGNode* node, bool onlyDet );
		/// @return the set of object neighbours of a NODE
	const TCGRoleSet& getObjectRoles ( const TCGNode* node, bool onlyDet, bool needIncoming );
		/// @return the set of neighbours of a NODE via role ROLE
	const TCGNodeVec& getNeighbours ( const TCGNode* node, const TRole* role );
		/// @return the set of all the expressions from the NODE label
	const TCGItemVec& getLabel ( const TCGNode* node, bool onlyDet );
		/// @return blocker of a blocked node NODE or NULL if node is not blocked
	const TCGNode* getBlocker ( const TCGNode* node ) const;
}; // KnowledgeExplorer

#endif
