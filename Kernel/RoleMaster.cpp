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

#include "RoleMaster.h"
#include "eFPPInconsistentKB.h"
#include "TaxonomyCreator.h"

RoleMaster :: RoleMaster ( bool dataRoles, const std::string& TopRoleName, const std::string& BotRoleName )
	: emptyRole(BotRoleName == "" ? "emptyRole" : BotRoleName)
	, universalRole(TopRoleName == "" ? "universalRole" : TopRoleName)
	, roleNS()
	, DataRoles(dataRoles)
{
	// no zero-named roles allowed
	Roles.push_back(nullptr);
	Roles.push_back(nullptr);
	// setup empty role
	emptyRole.setId(0);
	emptyRole.setInverse(&emptyRole);
	emptyRole.setDataRole(dataRoles);
	emptyRole.setBPDomain(bpBOTTOM);
	emptyRole.setBottom();
	// setup universal role
	universalRole.setId(0);
	universalRole.setInverse(&universalRole);
	universalRole.setDataRole(dataRoles);
	universalRole.setBPDomain(bpTOP);
	universalRole.setTop();
	// FIXME!! now it is not transitive => simple
	const_cast<RoleAutomaton&>(universalRole.getAutomaton()).setCompleted();
}

/// register TRole and it's inverse in RoleBox
void
RoleMaster :: registerRole ( TRole* r )
{
	fpp_assert ( r != nullptr && r->Inverse == nullptr );	// sanity check
	fpp_assert ( r->getId() == 0 );	// only call it for the new roles

	if ( DataRoles )
		r->setDataRole();

	Roles.push_back (r);
	r->setId (newRoleId);

	// create new role which would be inverse of R
	std::string iname ("-");
	iname += r->getName();
	TRole* ri = new TRole(iname);

	// set up inverse
	r->setInverse(ri);
	ri->setInverse(r);

	Roles.push_back (ri);
	ri->setId (-newRoleId);
	++newRoleId;
}

TRole*
RoleMaster :: ensureRoleName ( const std::string& name )
{
	// check for the Top/Bottom names
	if ( name == emptyRole.getName() )
		return &emptyRole;
	if ( name == universalRole.getName() )
		return &universalRole;

	// new name from NS
	TRole* p = roleNS.insert(name);
	// check what happens
	if ( p == nullptr )			// role registration attempt failed
		throw EFPPCantRegName ( name, DataRoles ? "data role" : "role" );

	if ( isRegisteredRole(p) )	// registered role
		return p;
	if ( p->getId() != 0 ||		// not registered but has non-null ID
		 !useUndefinedNames )	// new names are disallowed
		throw EFPPCantRegName ( name, DataRoles ? "data role" : "role" );

	registerRole(p);
	return p;
}

// inverse the role composition
DLTree* inverseComposition ( const DLTree* tree )
{
	if ( tree->Element() == RCOMPOSITION )
		return new DLTree ( TLexeme(RCOMPOSITION),
							inverseComposition(tree->Right()),
							inverseComposition(tree->Left()) );
	else
		return createEntry ( RNAME, resolveRole(tree)->inverse() );
}

void
RoleMaster :: addRoleParent ( DLTree* tree, TRole* parent ) const
{
	if ( !tree )	// nothing to do
		return;
	if ( tree->Element() == RCOMPOSITION )
	{
		parent->addComposition(tree);
		DLTree* inv = inverseComposition(tree);
		parent->inverse()->addComposition(inv);
		deleteTree(inv);
	}
	else if ( tree->Element() == PROJINTO )
	{
		// here -R->C became -PARENT->
		// encode this as PROJFROM(R-,PROJINTO(PARENT-,C)),
		// added to the range of R
		TRole* R = resolveRole(tree->Left());
		// can't do anything ATM for the data roles
		if ( R->isDataRole() )
			throw EFaCTPlusPlus("Projection into not implemented for the data role");
		DLTree* C = clone(tree->Right());
		DLTree* InvP = createEntry ( RNAME, parent->inverse() );
		DLTree* InvR = createEntry ( RNAME, R->inverse() );
		// C = PROJINTO(PARENT-,C)
		C = new DLTree ( TLexeme(PROJINTO), InvP, C );
		// C = PROJFROM(R-,PROJINTO(PARENT-,C))
		C = new DLTree ( TLexeme(PROJFROM), InvR, C );
		R->setRange(C);
	}
	else if ( tree->Element() == PROJFROM )
	{
		// here C-R-> became -PARENT->
		// encode this as PROJFROM(R,PROJINTO(PARENT,C)),
		// added to the domain of R
		TRole* R = resolveRole(tree->Left());
		DLTree* C = clone(tree->Right());
		DLTree* P = createEntry ( RNAME, parent );
		// C = PROJINTO(PARENT,C)
		C = new DLTree ( TLexeme(PROJINTO), P, C );
		// C = PROJFROM(R,PROJINTO(PARENT,C))
		C = new DLTree ( TLexeme(PROJFROM), clone(tree->Left()), C );
		R->setDomain(C);
	}
	else
		addRoleParent ( resolveRole(tree), parent );
	deleteTree(tree);
}

/// add parent for the input role
void
RoleMaster :: addRoleParentProper ( TRole* role, TRole* parent ) const
{
	fpp_assert ( !role->isSynonym() && !parent->isSynonym() );

	if ( role == parent )	// nothing to do
		return;

	if ( role->isDataRole() != parent->isDataRole() )
		throw EFaCTPlusPlus("Mixed object and data roles in role subsumption axiom");

	// check the inconsistency case *UROLE* [= *EROLE*
	if ( unlikely(role->isTop() && parent->isBottom()) )
		throw EFPPInconsistentKB();

	// *UROLE* [= R means R (and R-) are synonym of *UROLE*
	if ( unlikely(role->isTop()) )
	{
		parent->setSynonym(role);
		parent->inverse()->setSynonym(role);
		return;
	}

	// R [= *EROLE* means R (and R-) are synonyms of *EROLE*
	if ( unlikely(parent->isBottom()) )
	{
		role->setSynonym(parent);
		role->inverse()->setSynonym(parent);
		return;
	}

	role->addParent(parent);
	role->inverse()->addParent(parent->inverse());
}

void RoleMaster :: initAncDesc ( void )
{
	iterator p, p_begin = begin(), p_end = end();
	size_t nRoles = Roles.size();

	// stage 0.1: eliminate told cycles
	for ( p = p_begin; p != p_end; ++p )
		(*p)->eliminateToldCycles();	// not VERY efficient: quadratic vs (possible) linear

	// setting up all synonyms
	for ( p = p_begin; p != p_end; ++p )
		if ( (*p)->isSynonym() )
		{
			(*p)->canonicaliseSynonym();
			(*p)->addFeaturesToSynonym();
		}

	// change all parents that are synonyms to their primers
	for ( p = p_begin; p != p_end; ++p )
		if ( !(*p)->isSynonym() )
			(*p)->removeSynonymsFromParents();

	// here TOP-role has no children yet, so it's safe to complete the automaton
	universalRole.completeAutomaton(nRoles);

	// make all roles w/o told subsumers have Role TOP instead
	for ( p = p_begin; p < p_end; ++p )
		if ( !(*p)->isSynonym() && !(*p)->hasToldSubsumers() )
			(*p)->addParent(&universalRole);

	// stage 2: perform classification

	// create roles taxonomy
	pTax = new Taxonomy ( &universalRole, &emptyRole );
	TaxonomyCreator TaxCreator(pTax);
	TaxCreator.setCompletelyDefined(true);

	for ( p = p_begin; p != p_end; ++p )
		if ( !(*p)->isClassified() )
			TaxCreator.classifyEntry(*p);

	// stage 3: fills ancestor/descendants using taxonomy
	for ( p = p_begin; p != p_end; ++p )
		if ( !(*p)->isSynonym() )
			(*p)->initADbyTaxonomy ( pTax, nRoles );

	// complete role automaton's info
	for ( p = p_begin; p != p_end; ++p )
		if ( !(*p)->isSynonym() )
			(*p)->completeAutomaton(nRoles);

	// now all usual roles has their own automata, set up Bottom's automata
	emptyRole.completeAutomaton(nRoles);

	// prepare taxonomy to the real usage
	pTax->finalise();

	// stage 3.5: apply Disjoint axioms to roles; check and correct disjoints in hierarchy
	if ( !DJRolesA.empty() )
	{
		for ( iterator q = DJRolesA.begin(), q_end = DJRolesA.end(), r = DJRolesB.begin();
			  q != q_end; ++q, ++r )
		{
			TRole* R = resolveSynonym(*q);
			TRole* S = resolveSynonym(*r);
			R->addDisjointRole(S);
			S->addDisjointRole(R);
			R->inverse()->addDisjointRole(S->inverse());
			S->inverse()->addDisjointRole(R->inverse());
		}

		for ( p = p_begin; p != p_end; ++p )
			if ( !(*p)->isSynonym() && (*p)->isDisjoint() )
				(*p)->checkHierarchicalDisjoint();
	}

	// stage 4: init other fields for the roles. The whole hierarchy is known here
	for ( p = p_begin; p != p_end; ++p )
		if ( !(*p)->isSynonym() )
			(*p)->postProcess();

	// the last stage: check whether all roles are consistent
	for ( p = p_begin; p != p_end; ++p )
		if ( !(*p)->isSynonym() )
			(*p)->consistent();
}
