/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2013-2015 Dmitry Tsarkov and The University of Manchester
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

// incremental reasoning implementation

#include "Kernel.h"
#include "OntologyBasedModularizer.h"
#include "tOntologyPrinterLISP.h"
#include "procTimer.h"
#include "SaveLoadManager.h"	// for saving/restoring ontology

TsProcTimer moduleTimer, subCheckTimer;
int nModule = 0;

/// setup Name2Sig for a given name C
void
ReasoningKernel :: setupSig ( const TNamedEntity* entity, const AxiomVec& Module )
{
	// do nothing if entity doesn't exist
	if ( entity == nullptr )
		return;

	moduleTimer.Start();
	// prepare a place to update
	TSignature sig;
	NameSigMap::iterator insert = Name2Sig.find(entity);
	if ( insert == Name2Sig.end() )
		insert = Name2Sig.insert(std::make_pair(entity,&sig)).first;
	else
		delete insert->second;

	// calculate a module
	sig.add(entity);
	getModExtractor(SYN_LOC_STD)->getModule(Module,sig,M_BOT);
	++nModule;

	// perform update
	insert->second = new TSignature(getModExtractor(SYN_LOC_STD)->getModularizer()->getSignature());

	moduleTimer.Stop();
}

/// build signature for ENTITY and all dependent entities from toProcess; look for modules in Module;
void
ReasoningKernel :: buildSignature ( const TNamedEntity* entity, const AxiomVec& Module, std::set<const TNamedEntity*>& toProcess )
{
	toProcess.erase(entity);
	setupSig ( entity, Module );
	const AxiomVec NewModule = getModExtractor(SYN_LOC_STD)->getModularizer()->getModule();
	if ( Module.size() == NewModule.size() )	// the same module
		return;
	// smaller module: recurse
	for ( const TNamedEntity* e : getModExtractor(SYN_LOC_STD)->getModularizer()->getSignature() )
		if ( toProcess.count(e) > 0 )	// need to process
			buildSignature ( e, NewModule, toProcess );
}

/// initialise the incremental bits on full reload
void
ReasoningKernel :: initIncremental ( void )
{
	delete ModSyn;
	ModSyn = nullptr;
	// fill the module signatures of the concepts
	Name2Sig.clear();
	// found all entities
	std::set<const TNamedEntity*> toProcess;
	for ( TBox::c_const_iterator p = getTBox()->c_begin(), p_end = getTBox()->c_end(); p != p_end; ++p )
		toProcess.insert((*p)->getEntity());
	// process all entries recursively
	while ( !toProcess.empty() )
		buildSignature ( *toProcess.begin(), Ontology.getAxioms(), toProcess );


	getTBox()->setNameSigMap(&Name2Sig);
	// fill in ontology signature
	OntoSig = Ontology.getSignature();
	std::cout << "Init modules (" << nModule << ") time: " << moduleTimer << " sec" << std::endl;
}

void
ReasoningKernel :: doIncremental ( void )
{
	TsProcTimer total;
	total.Start();
	std::cout << "Incremental!\n";
	// re-set the modularizer to use updated ontology
	delete ModSyn;
	ModSyn = nullptr;

	std::set<const TNamedEntity*> MPlus, MMinus;
	std::set<const TNamedEntry*> excluded;

	// detect new- and old- signature elements
	TSignature NewSig = Ontology.getSignature();
	TSignature::BaseType RemovedEntities, AddedEntities;
	std::set_difference(OntoSig.begin(), OntoSig.end(), NewSig.begin(), NewSig.end(), inserter(RemovedEntities, RemovedEntities.begin()));
	std::set_difference(NewSig.begin(), NewSig.end(), OntoSig.begin(), OntoSig.end(), inserter(AddedEntities, AddedEntities.begin()));

	Taxonomy* tax = getCTaxonomy();
//	std::cout << "Original Taxonomy:";
//	tax->print(std::cout);

	// deal with removed concepts
	TSignature::BaseType::iterator e, e_end;
	for ( e = RemovedEntities.begin(), e_end = RemovedEntities.end(); e != e_end; ++e )
		if ( const TConcept* C = dynamic_cast<const TConcept*>((*e)->getEntry()) )
		{
			excluded.insert(C);
			// remove all links
			C->getTaxVertex()->remove();
			// update Name2Sig
			delete Name2Sig[*e];
			Name2Sig.erase(*e);
		}

	// deal with added concepts
	tax->deFinalise();
	for ( e = AddedEntities.begin(), e_end = AddedEntities.end(); e != e_end; ++e )
		if ( const TDLConceptName* cName = dynamic_cast<const TDLConceptName*>(*e) )
		{
			// register the name in TBox
			TreeDeleter TD(this->e(cName));
			TConcept* C = dynamic_cast<TConcept*>(cName->getEntry());
			// create sig for it
			setupSig(cName);
			// init the taxonomy element
			TaxonomyVertex* cur = tax->getCurrent();
			cur->clear();
			cur->setSample(C);
			cur->addNeighbour ( /*upDirection=*/true, tax->getTopVertex() );
			tax->finishCurrentNode();
//			std::cout << "Insert " << C->getName() << std::endl;
		}
	OntoSig = NewSig;

	// fill in M^+ and M^- sets
	TsProcTimer t;
	t.Start();
	LocalityChecker* lc = getModExtractor(SYN_LOC_STD)->getModularizer()->getLocalityChecker();
	TOntology::iterator nb = Ontology.beginUnprocessed(), ne = Ontology.end(), rb = Ontology.beginRetracted(), re = Ontology.endRetracted();
//	TLISPOntologyPrinter pr(std::cout);
//	TOntology::iterator q;
//	for ( q = nb; q != ne; ++q )
//	{
//		std::cout << "Add:";
//		(*q)->accept(pr);
//	}
//	for ( q = rb; q != re; ++q )
//	{
//		std::cout << "Del:";
//		(*q)->accept(pr);
//	}
	for ( NameSigMap::iterator p = Name2Sig.begin(), p_end = Name2Sig.end(); p != p_end; ++p )
	{
		lc->setSignatureValue(*p->second);
		for ( TOntology::iterator notProcessed = nb; notProcessed != ne; ++notProcessed )
			if ( !lc->local(*notProcessed) )
			{
				MPlus.insert(p->first);
//				std::cout << "Non-local NP axiom ";
//				(*notProcessed)->accept(pr);
//				std::cout << " wrt " << p->first->getName() << std::endl;
				break;
			}
		for ( TOntology::iterator retracted = rb; retracted != re; retracted++ )
			if ( !lc->local(*retracted) )
			{
				MMinus.insert(p->first);
				// FIXME!! only concepts for now
				TaxonomyVertex* v = dynamic_cast<const ClassifiableEntry*>(p->first->getEntry())->getTaxVertex();
				if ( v->noNeighbours(true) )
				{
					v->addNeighbour(true,tax->getTopVertex());
					tax->getTopVertex()->addNeighbour(false,v);
				}
//				std::cout << "Non-local RT axiom ";
//				(*retracted)->accept(pr);
//				std::cout << " wrt " << p->first->getName() << std::endl;
				break;
			}
	}
	t.Stop();
	std::cout << "Determine concepts that need reclassification: done in " << t << std::endl;

	// build changed modules
	std::set<const TNamedEntity*> toProcess(MPlus);
	toProcess.insert ( MMinus.begin(), MMinus.end() );
	// process all entries recursively
	while ( !toProcess.empty() )
		buildSignature ( *toProcess.begin(), Ontology.getAxioms(), toProcess );

	tax->finalise();
//	std::cout << "Adjusted Taxonomy:";
//	tax->print(std::cout);

	t.Reset();
	t.Start();
	// save taxonomy
	SaveLoadManager SLManager("Incremental");
	SLManager.prepare(/*input=*/false);
	// FIXME!! for now
	excluded.clear();
	getTBox()->SaveTaxonomy(SLManager,excluded);

	// do actual change
	useIncrementalReasoning = false;
	forceReload();
	pTBox->setNameSigMap(&Name2Sig);
	pTBox->isConsistent();
	useIncrementalReasoning = true;

	// load the taxonomy
	SLManager.prepare(/*input=*/true);
	getTBox()->LoadTaxonomy(SLManager);
	t.Stop();

	std::cout << "Reloading ontology: done in " << t << std::endl;

	tax = getCTaxonomy();
//	std::cout << "Reloaded Taxonomy:";
//	tax->print(std::cout);
//	std::cout.flush();

	subCheckTimer.Start();
	getTBox()->reclassify ( MPlus, MMinus );
	subCheckTimer.Stop();
	Ontology.setProcessed();
	total.Stop();
	std::cout << "Total modularization (" << nModule << ") time: " << moduleTimer << " sec\nTotal reasoning time: " << subCheckTimer
			  << " sec\nTotal reclassification time: " << total << " sec" << std::endl;
}

std::ostream&
operator << ( std::ostream& o, const TSignature& sig )
{
	o << "[";
	for ( const TNamedEntity* entity : sig )
		o << entity->getName() << " ";
	o << "]" << std::endl;
	return o;
}
