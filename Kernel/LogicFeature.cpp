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

#include "LogicFeature.h"
#include "logging.h"
#include "dlVertex.h"
#include "tConcept.h"
#include "tRole.h"

void LogicFeatures :: fillConceptData ( const TConcept* p )
{
	if ( p->isSingleton() )
		setX(lfSingleton);
}

void LogicFeatures :: fillRoleData ( const TRole* p, bool both )
{
	if ( unlikely(p->isTop()) )
	{
		if ( !p->isDataRole() )
			setX(lfTopRole);
		return;
	}

	if ( p->getId() > 0 )	// direct role
		setX(lfDirectRoles);
	else
		setX(lfInverseRoles);

	// inverse roles check
	if ( both )
		setX(lfBothRoles);

	// transitivity check
	if ( p->isTransitive() )
		setX(lfTransitiveRoles);

	// subsumption check
	if ( p->hasToldSubsumers() )
		setX(lfRolesSubsumption);

	// functionality check
	if ( p->isFunctional() )
		setX(lfFunctionalRoles);

	// R&D check
	if ( p->getBPDomain() != bpTOP || p->getBPRange() != bpTOP )
		setX(lfRangeAndDomain);
}

void LogicFeatures :: fillDAGData ( const DLVertex& v, bool )
{
	switch ( v.Type () )
	{
	case dtForall:
		setX(lfSomeConstructor);
		break;

	case dtLE:
		setX(lfNConstructor);
		if ( v.getC() != bpTOP )
			setX(lfQConstructor);

		break;

	case dtPSingleton:
	case dtNSingleton:
		setX(lfSingleton);
		break;

	case dtIrr:
		setX(lfSelfRef);
		break;

	default:	// any other vertex -- nothing to do
		break;
	}
}

void LogicFeatures :: writeState ( void ) const
{
	CHECK_LL_RETURN(llAlways);
	LL << "\nLoaded KB used DL with following features:\nKB contains ";
	if ( !hasInverseRole () )
		LL << "NO ";
	LL << "inverse role(s)\nKB contains ";
	if ( !hasRoleHierarchy () )
		LL << "NO ";
	LL << "role hierarchy\nKB contains ";
	if ( !hasTransitiveRole () )
		LL << "NO ";
	LL << "transitive role(s)\nKB contains ";
	if ( !hasTopRole () )
		LL << "NO ";
	LL << "top role expressions\nKB contains ";
	if ( !hasSomeAll () )
		LL << "NO ";
	LL << "quantifier(s)\nKB contains ";
	if ( !hasFunctionalRestriction () )
		LL << "NO ";
	LL << "functional restriction(s)\nKB contains ";
	if ( !hasNumberRestriction () )
		LL << "NO ";
	else if ( hasQNumberRestriction () )
		LL << "qualified ";
	LL << "number restriction(s)\nKB contains ";
	if ( !hasSingletons() )
		LL << "NO ";
	LL << "nominal(s)\n";
}
