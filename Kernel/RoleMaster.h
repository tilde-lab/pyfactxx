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

#ifndef ROLEMASTER_H
#define ROLEMASTER_H

#include "globaldef.h"
#include "tNameSet.h"
#include "eFPPCantRegName.h"
#include "tRole.h"
#include "Taxonomy.h"

class RoleMaster
{
public:		// types
		/// vector of roles
	typedef TRole::TRoleVec TRoleVec;
		/// RW access to roles
	typedef TRoleVec::iterator iterator;
		/// RO access to roles
	typedef TRoleVec::const_iterator const_iterator;

protected:	// members
		/// number of the last registered role
	int newRoleId = 1;

		/// all registered roles
	TRoleVec Roles;
		/// internal empty role (bottom in the taxonomy)
	TRole emptyRole;
		/// internal universal role (top in the taxonomy)
	TRole universalRole;

		/// roles nameset
	TNameSet<TRole> roleNS;
		/// Taxonomy of roles
	Taxonomy* pTax = nullptr;

		/// two halves of disjoint roles axioms
	TRoleVec DJRolesA, DJRolesB;

		/// flag whether to create data roles or not
	bool DataRoles;

		/// flag if it is possible to introduce new names
	bool useUndefinedNames = true;

private:	// methods
		/// constant defining first user role in the RBox
	static unsigned int firstRoleIndex ( void ) { return 2; }

protected:	// methods
		/// register TRole and it's inverse in RoleBox
	void registerRole ( TRole* r );
		/// @return true if P is a role that is registered in the RM
	bool isRegisteredRole ( const TNamedEntry* p ) const
	{
		const TRole* R = dynamic_cast<const TRole*>(p);
		if ( R == nullptr )
			return false;
		unsigned int ind = R->getIndex();
		return ( ind >= firstRoleIndex() &&
				 ind < Roles.size() &&
				 Roles[ind] == p );
	}

		/// add parent for the input role; both roles are not synonyms
	void addRoleParentProper ( TRole* role, TRole* parent ) const;

		/// get number of roles
	size_t size ( void ) const { return Roles.size()/2-1; }

public:		// interface
		/// the only c'tor
	RoleMaster ( bool dataRoles, const std::string& TopRoleName, const std::string& BotRoleName );
		/// no copy c'tor
	RoleMaster ( const RoleMaster& ) = delete;
		/// no assignment
	RoleMaster& operator = ( const RoleMaster& ) = delete;
		/// d'tor (delete taxonomy)
	~RoleMaster() { delete pTax; }

		/// create role entry with given name
	TRole* ensureRoleName ( const std::string& name );

		/// add parent for the input role or role composition; delete ROLE afterwards
	void addRoleParent ( DLTree* role, TRole* parent ) const;
		/// add parent for the input role
	void addRoleParent ( TRole* role, TRole* parent ) const { addRoleParentProper ( resolveSynonym(role), resolveSynonym(parent) ); }
		/// add synonym to existing role
	void addRoleSynonym ( TRole* role, TRole* syn ) const
	{
		// no synonyms
//		role = resolveSynonym(role);
//		syn = resolveSynonym(syn);
		// FIXME!! 1st call can make one of them a synonym of a const
		addRoleParentProper ( resolveSynonym(role), resolveSynonym(syn) );
		addRoleParentProper ( resolveSynonym(syn), resolveSynonym(role) );
	}

		/// register a pair of disjoint roles
	void addDisjointRoles ( TRole* R, TRole* S )
	{
		// object- and data roles are always disjoint
		if ( R->isDataRole() != S->isDataRole() )
			return;
		DJRolesA.push_back(R);
		DJRolesB.push_back(S);
	}

		/// create taxonomy of roles (using the Parent data)
	void initAncDesc ( void );

		/// change the undefined names usage policy
	void setUndefinedNames ( bool val ) { useUndefinedNames = val; }

	// access to roles

		/// @return pointer to a TOP role
	TRole* getTopRole ( void ) { return &universalRole; }
		/// @return const pointer to a TOP role
	const TRole* getTopRole ( void ) const { return &universalRole; }
		/// @return pointer to a BOTTOM role
	TRole* getBotRole ( void ) { return &emptyRole; }
		/// @return const pointer to a BOTTOM role
	const TRole* getBotRole ( void ) const { return &emptyRole; }
		/// RW pointer to the first user-defined role
	iterator begin ( void ) { return Roles.begin()+firstRoleIndex(); }
		/// RW pointer after the last user-defined role
	iterator end ( void ) { return Roles.end(); }
		/// RO pointer to the first user-defined role
	const_iterator begin ( void ) const { return Roles.begin()+firstRoleIndex(); }
		/// RO pointer after the last user-defined role
	const_iterator end ( void ) const { return Roles.end(); }

		/// get access to the taxonomy
	Taxonomy* getTaxonomy ( void ) const { return pTax; }

		/// @return true iff there is a reflexive role
	bool hasReflexiveRoles ( void ) const;
		/// put all reflexive roles to a RR array
	void fillReflexiveRoles ( TRoleVec& RR ) const;

	// output interface

	void Print ( std::ostream& o, const char* type ) const
	{
		if ( size() == 0 )
			return;
		o << type << " Roles (" << size() << "):\n";
		emptyRole.Print(o);
		for ( const TRole* role : *this )
			role->Print(o);
	}
}; // RoleMaster

inline bool
RoleMaster :: hasReflexiveRoles ( void ) const
{
	for  ( const_iterator p = begin(), p_end = end(); p < p_end; ++p )
		if ( (*p)->isReflexive() )
			return true;

	return false;
}

inline void
RoleMaster :: fillReflexiveRoles ( TRoleVec& RR ) const
{
	RR.clear();
	for  ( const_iterator p = begin(), p_end = end(); p < p_end; ++p )
		if ( !(*p)->isSynonym() && (*p)->isReflexive() )
			RR.push_back(*p);
}

#endif
