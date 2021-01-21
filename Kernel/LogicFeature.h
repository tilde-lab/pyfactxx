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

#ifndef LOGICFEATURE_H
#define LOGICFEATURE_H

class TConcept;
class TRole;
class DLVertex;

// contains class for currently used logic's features
class LogicFeatures
{
protected:	// types
	enum lfEnum {
		lfInvalid = 0,
		// role description
		lfTransitiveRoles	= (1 << 0),
		lfRolesSubsumption	= (1 << 1),
		lfDirectRoles		= (1 << 2),
		lfInverseRoles		= (1 << 3),
		lfRangeAndDomain	= (1 << 4),
		lfFunctionalRoles	= (1 << 5),

		// concept description
		lfSomeConstructor	= (1 << 6),
		lfFConstructor		= (1 << 7),
		lfNConstructor		= (1 << 8),
		lfQConstructor		= (1 << 9),
		lfSingleton			= (1 << 10),

		// global description
		lfGeneralAxioms		= (1 << 11),
		lfBothRoles			= (1 << 12),

		// new constructions
		lfSelfRef			= (1 << 13),
		lfTopRole			= (1 << 14),
	};

protected:	// members
		/// all flags in one long
	unsigned long flags = 0;

protected:	// methods
		/// set any flag
	void setX ( lfEnum val ) { flags |= val; }
		/// get value of any flag
	bool getX ( lfEnum val ) const { return (flags & val) != 0; }

public:		// interface
		/// default c'tor
	LogicFeatures() = default;
		/// copy c'tor
	LogicFeatures(const LogicFeatures&) = default;
		/// assignment
	LogicFeatures& operator = (const LogicFeatures&)  = default;
		/// operator add
	LogicFeatures& operator |= ( const LogicFeatures& lf ) { flags |= lf.flags; return *this; }

//	bool hasInverseRole ( void ) const { return getX(lfDirectRoles) && getX(lfInverseRoles); }
	bool hasInverseRole ( void ) const { return getX(lfBothRoles); }
	bool hasRoleHierarchy ( void ) const { return getX(lfRolesSubsumption); }
	bool hasTransitiveRole ( void ) const  { return getX(lfTransitiveRoles); }
	bool hasSomeAll ( void ) const  { return getX(lfSomeConstructor); }
	bool hasFunctionalRestriction ( void ) const { return getX(lfFConstructor) || getX(lfFunctionalRoles); }
	bool hasNumberRestriction ( void ) const { return getX(lfNConstructor); }
	bool hasQNumberRestriction ( void ) const { return getX(lfQConstructor); }
	bool hasSingletons ( void ) const { return getX(lfSingleton); }
	bool hasSelfRef ( void ) const { return getX(lfSelfRef); }
	bool hasTopRole ( void ) const { return getX(lfTopRole); }

	// overall state

		/// check whether no flags are set
	bool empty ( void ) const { return flags == 0; }
		/// get all the flags at once
	unsigned long getAllFlags ( void ) const { return flags; }
		/// set all flags to a given value; @return old value of the flags
	unsigned long setAllFlags ( unsigned long value ) { unsigned long old = flags; flags = value; return old; }
		/// clear all the flags
	void clear ( void ) { setAllFlags(0); }

	// collect statistic from different things

		/// get features from given TConcept
	void fillConceptData ( const TConcept* p );
		/// get features from given TRole
	void fillRoleData ( const TRole* p, bool both );
		/// get features from given DAG entry with given sign
	void fillDAGData ( const DLVertex& v, bool pos );

		/// build bothRoles from single Roles flags
	void mergeRoles ( void )
	{
		if ( getX(lfDirectRoles) && getX(lfInverseRoles) )
			setX(lfBothRoles);
	}
		/// allow user to set presence of inverse roles
	void setInverseRoles ( void ) { setX(lfBothRoles); }

		/// write gathered features
	void writeState ( void ) const;
}; // LogicFeatures

#endif
