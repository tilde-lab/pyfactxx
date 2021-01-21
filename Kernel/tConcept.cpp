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

#include "tConcept.h"
#include "tRole.h"

void TConcept :: clear ( void )
{
	// TNamedEntry clean
	setId(0);
	// ClassifiableEntry clean
	taxVertex = nullptr;
	toldSubsumers.clear();
	setCompletelyDefined(false);
	pSynonym = nullptr;
	// TConcept clean
	removeDescription();
	setPrimitive();
	pName = pBody = bpINVALID;
}

void TConcept :: addDesc ( DLTree* Desc )
{
	fpp_assert (!isNonPrimitive());	// safety check

	// FIXME!! check about reverse order
	Description = createSNFAnd ( Desc, Description );
}

/// calculate value of classification TAG based on told subsumers. WARNING: no TS cycles allowed
CTTag TConcept :: determineClassTag ( void )
{
	// for synonyms -- set tag as a primer's one
	if ( isSynonym() )
		return resolveSynonym(this)->getClassTag();

	// check if it is non-primitive
	if ( isNonPrimitive() )
		return cttNonPrimitive;

	// no told subsumers
	if ( !hasToldSubsumers() )
		return cttOrphan;

	// now need to check all the told subsumers
	bool hasLCD = false;
	bool hasOther = false;
	bool hasNP = false;

	for ( auto& p: told() )
		switch ( static_cast<TConcept*>(p)->getClassTag() )
		{
		case cttTrueCompletelyDefined:
			break;
		case cttOrphan:
		case cttLikeCompletelyDefined:
			hasLCD = true;
			break;
		case cttRegular:
			hasOther = true;
			break;
		case cttHasNonPrimitiveTS:
		case cttNonPrimitive:
			hasNP = true;
			break;
		default:
			fpp_unreachable();
		}

	// there are non-primitive TS
	if ( hasNP )
		return cttHasNonPrimitiveTS;

	// has something different from CD-like ones (and not CD)
	if ( hasOther || !isCompletelyDefined() )
		return cttRegular;

	// no more 'other' concepts here, and the CD-like structure
	if ( hasLCD )
		return cttLikeCompletelyDefined;

	return cttTrueCompletelyDefined;
}

bool
TConcept :: hasSelfInDesc ( const DLTree* t ) const
{
	if ( t == nullptr )
		return false;

	switch ( t->Element().getToken() )
	{
		case NAME:	// if ID contains synonym of P
		{
			const ClassifiableEntry* name = static_cast<const ClassifiableEntry*>(t->Element().getNE());
			return resolveSynonym(name) == this;
		}
		case AND:
			return hasSelfInDesc(t->Left()) || hasSelfInDesc(t->Right());
		case NOT:	// a [= (not a) -> a [= BOTTOM; a [= (a or b) -> a [= TOP
			switch ( t->Left()->Element().getToken() )
			{
			case AND:
			case NAME:
				return hasSelfInDesc(t->Left());
			default:
				return false;
		}
		default:
			return false;
	}
}

DLTree*
TConcept :: replaceSelfWithConst ( const DLTree* t ) const
{
	if ( t == nullptr )
		return nullptr;

	switch ( t->Element().getToken() )
	{
	case NAME:	// if ID contains synonym of P
	{
		const ClassifiableEntry* name = static_cast<const ClassifiableEntry*>(t->Element().getNE());
		if ( resolveSynonym(name) == this )
			return createTop();
		else
			break;
	}
	case AND:	// a [= (and a b) -> a [= b
		return createSNFAnd ( replaceSelfWithConst(t->Left()), replaceSelfWithConst(t->Right()) );
	case NOT:	// a [= (not a) -> a [= BOTTOM; a [= (a or b) -> a [= TOP
		switch ( t->Left()->Element().getToken() )
		{
		case AND:
		case NAME:
			return createSNFNot(replaceSelfWithConst(t->Left()));
		default:
			break;
		}
	default:
		break;
	}
	return clone(t);
}

/// init told subsumers of the concept by given DESCription; @return TRUE iff concept is CD
bool TConcept :: initToldSubsumers ( const DLTree* desc, RoleSSet& RolesProcessed )
{
	// no description => nothing to do (and yes, it is told)
	if ( desc == nullptr )
		return true;

	switch ( desc->Element().getToken() )
	{
	case TOP:	// the 1st node
		return true;
	case NAME:	// it is a concept ID
		return addToldSubsumer(static_cast<TConcept*>(desc->Element().getNE()));
	case AND:	// add TS from BOTH parts of AND
		return initToldSubsumers ( desc->Left(), RolesProcessed ) & initToldSubsumers ( desc->Right(), RolesProcessed );
	case NOT:	// Domains from \ER.C and (>= n R.C) are told concepts
	{
		const TLexeme& cur = desc->Left()->Element();

		if ( cur.getToken() == FORALL || cur.getToken() == LE )
			SearchTSbyRoleAndSupers ( resolveRole(desc->Left()->Left()), RolesProcessed );

		return false;
	}
	case SELF:	// Domains and Range from participating role
	{
		const TRole* R = resolveRole(desc->Left());
		SearchTSbyRoleAndSupers ( R, RolesProcessed );
		SearchTSbyRoleAndSupers ( R->inverse(), RolesProcessed );
		return false;
	}
	default:	// not told one
		return false;
	}
}

void TConcept :: SearchTSbyRole ( const TRole* R, RoleSSet& RolesProcessed )
{
	const DLTree* Domain = R->getTDomain();
	if ( Domain == nullptr || isConst(Domain) )
		return;

	// don't process the same role twice
	if ( RolesProcessed.find(R) != RolesProcessed.end() )
		return;

	// mark role as processed; usually it's the only role, so set hint as a begin()
	RolesProcessed.insert ( RolesProcessed.begin(), R );

	// init TS by the domain of role
	initToldSubsumers ( Domain, RolesProcessed );	// don't bother about result
}

void TConcept :: SearchTSbyRoleAndSupers ( const TRole* r, RoleSSet& RolesProcessed )
{
	SearchTSbyRole ( r, RolesProcessed );

	// do the same for all super-roles if necessary
	// FIXME!! need to do the same for DomSupers (like SoR [= R)
	for ( const auto& sup: r->ancestors() )
		SearchTSbyRole ( sup, RolesProcessed );
}

unsigned int TConcept :: calculateTSDepth ( void )
{
	if ( tsDepth > 0 )
		return tsDepth;

	unsigned int max = 0;

	for ( auto& p: told() )
	{
		unsigned int cur = static_cast<TConcept*>(p)->calculateTSDepth();
		if ( max < cur )
			max = cur;
	}

	return (tsDepth = max+1);
}
