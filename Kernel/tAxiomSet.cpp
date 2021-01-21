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

#include "tAxiomSet.h"

#include "logging.h"
#include "dlTBox.h"

/// d'tor
TAxiomSet :: ~TAxiomSet()
{
	for ( auto& axiom: Accum )
		delete axiom;
}

bool
TAxiomSet :: split ( const TAxiom* p )
{
	AxiomCollection Splitted, Kept, Unneeded;
	if ( !p->split(Splitted) )	// nothing to split
		return false;

	bool fail = false;
	for ( auto& axiom: Splitted )
	{
		// axiom is a copy of a processed one: fail to do split
		if ( axiom->isCyclic() )
		{
			fail = true;
			break;
		}
		// axiom is a copy of a new one: skip it
		if ( copyOfExisting(axiom) )
			Unneeded.push_back(axiom);
		else	// new axiom: keep it
			Kept.push_back(axiom);
	}
	// if fail to split: delete all the axioms
	if ( fail )
	{
		for ( auto& axiom: Splitted )
			delete axiom;
		return false;
	}
	// no failure: delete all the unneeded axioms, add all kept ones
	for ( auto& axiom: Unneeded )
		delete axiom;
	for ( auto& axiom: Kept )
		insertGCI(axiom);
	return true;
}

size_t TAxiomSet :: absorb ( void )
{
	// absorbed- and unabsorbable GCIs
	AxiomCollection Absorbed, GCIs;

	// we will change Accum (via split rule), so indexing and compare with size
	for ( size_t curAxiom = 0; curAxiom < Accum.size(); ++curAxiom )
	{
#	ifdef RKG_DEBUG_ABSORPTION
		std::cout << "\nProcessing (" << curAxiom << "):";
#	endif
		TAxiom* ax = Accum[curAxiom];
		if ( absorbGCI(ax) )
			Absorbed.push_back(ax);
		else
			GCIs.push_back(ax);
	}

	// clear absorbed and remove them from Accum
	for ( auto& axiom: Absorbed )
		delete axiom;
	Accum.swap(GCIs);

#ifdef RKG_DEBUG_ABSORPTION
	std::cout << "\nAbsorption done with " << Accum.size() << " GCIs left\n";
#endif
	PrintStatistics();
	return size();
}

bool TAxiomSet :: absorbGCI ( const TAxiom* p )
{
	Stat::SAbsAction();

	for ( const auto& action: ActionVector )
		if ( (this->*action)(p) )
			return true;

#ifdef RKG_DEBUG_ABSORPTION
	std::cout << " keep as GCI";
#endif

	return false;
}

bool TAxiomSet :: initAbsorptionFlags ( const std::string& flags )
{
	ActionVector.clear();
	for ( const auto& ch: flags )
		switch ( ch )
		{
		case 'B': ActionVector.push_back(&TAxiomSet::absorbIntoBottom); break;
		case 'T': ActionVector.push_back(&TAxiomSet::absorbIntoTop); break;
		case 'E': ActionVector.push_back(&TAxiomSet::simplifyCN); break;
		case 'C': ActionVector.push_back(&TAxiomSet::absorbIntoConcept); break;
		case 'N': ActionVector.push_back(&TAxiomSet::absorbIntoNegConcept); break;
		case 'f': ActionVector.push_back(&TAxiomSet::simplifySForall); break;
		case 'F': ActionVector.push_back(&TAxiomSet::simplifyForall); break;
		case 'R': ActionVector.push_back(&TAxiomSet::absorbIntoDomain); break;
		case 'S': ActionVector.push_back(&TAxiomSet::split); break;
		default: return true;
		}

	if ( LLM.isWritable(llAlways) )
		LL << "Init absorption order as " << flags << "\n";

	return false;
}

void TAxiomSet :: PrintStatistics ( void ) const
{
	if ( Stat::SAbsAction::objects_created == 0 || !LLM.isWritable(llAlways) )
		return;

	LL << "\nAbsorption dealt with "
	   << Stat::SAbsInput::objects_created << " input axioms\nThere were made "
	   << Stat::SAbsAction::objects_created << " absorption actions, of which:";
	if ( Stat::SAbsRepCN::objects_created )
		LL << "\n\t" << Stat::SAbsRepCN::objects_created << " concept name replacements";
	if ( Stat::SAbsRepForall::objects_created )
		LL << "\n\t" << Stat::SAbsRepForall::objects_created << " universals replacements";
	if ( Stat::SAbsSplit::objects_created )
		LL << "\n\t" << Stat::SAbsSplit::objects_created << " conjunction splits";
	if ( Stat::SAbsBApply::objects_created )
		LL << "\n\t" << Stat::SAbsBApply::objects_created << " BOTTOM absorptions";
	if ( Stat::SAbsTApply::objects_created )
		LL << "\n\t" << Stat::SAbsTApply::objects_created << " TOP absorptions";
	if ( Stat::SAbsCApply::objects_created )
		LL << "\n\t" << Stat::SAbsCApply::objects_created << " concept absorption with "
		   << Stat::SAbsCAttempt::objects_created << " possibilities";
	if ( Stat::SAbsNApply::objects_created )
		LL << "\n\t" << Stat::SAbsNApply::objects_created << " negated concept absorption with "
		   << Stat::SAbsNAttempt::objects_created << " possibilities";
	if ( Stat::SAbsRApply::objects_created )
		LL << "\n\t" << Stat::SAbsRApply::objects_created << " role domain absorption with "
		   << Stat::SAbsRAttempt::objects_created << " possibilities";
	if ( !Accum.empty() )
		LL << "\nThere are " << Accum.size() << " GCIs left";
}
