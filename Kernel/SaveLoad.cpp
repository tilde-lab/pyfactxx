/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2008-2015 Dmitry Tsarkov and The University of Manchester
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

//-------------------------------------------------------
//-- Saving/restoring internal state of the FaCT++
//-------------------------------------------------------

#include "Kernel.h"
#include "ReasonerNom.h"	// for initReasoner()
#include "SaveLoadManager.h"

const char* ReasoningKernel :: InternalStateFileHeader = "FaCT++InternalStateDump1.0";

const int bytesInInt = sizeof(int);

#if 0
// FIXME!! try to avoid recursion later on
static inline
void saveUIntAux ( ostream& o, unsigned int n, const int rest )
{
	if ( rest > 1 )
		saveUIntAux ( o, n/256, rest-1 );
	m.o() << (unsigned char)n%256;
}

static inline
void saveUInt ( ostream& o, unsigned int n )
{
	saveUIntAux ( o, n, bytesInInt );
}

static inline
void saveSInt ( ostream& o, int n ) { saveUInt(o,n); }

static inline
unsigned int loadUInt ( istream& i )
{
	static unsigned int ret = 0;
	unsigned char byte;
	for ( int j = bytesInInt-1; j >= 0; --j )
	{
		i >> byte;
		ret *= 256;
		ret += byte;
	}
	return ret;
}

static inline
int loadSInt ( istream& i ) { return (int)loadUInt(i); }
#endif	// 0

//----------------------------------------------------------
//-- Implementation of the Kernel methods (Kernel.h)
//----------------------------------------------------------

void
ReasoningKernel :: Save ( SaveLoadManager& m )
{
	TsProcTimer t;
	t.Start();
	m.checkStream();
	SaveHeader(m);
	m.checkStream();
	SaveOptions(m);
	m.checkStream();
	SaveKB(m);
	m.checkStream();
	SaveIncremental(m);
	m.checkStream();
	t.Stop();
	std::cout << "Reasoner internal state saved in " << t << " sec" << std::endl;
}

void
ReasoningKernel :: Save ( void )
{
	fpp_assert ( pSLManager != nullptr );
	pSLManager->prepare(/*input=*/false);
	Save(*pSLManager);
}

void
ReasoningKernel :: Load ( SaveLoadManager& m )
{
	TsProcTimer t;
	t.Start();
	m.checkStream();
//	releaseKB();	// we'll start a new one if necessary
	LoadHeader(m);
	m.checkStream();
	LoadOptions(m);
	m.checkStream();
	LoadKB(m);
	m.checkStream();
	LoadIncremental(m);
	m.checkStream();
	t.Stop();
	std::cout << "Reasoner internal state loaded in " << t << " sec" << std::endl;
}

void
ReasoningKernel :: Load ( void )
{
	fpp_assert ( pSLManager != nullptr );
	pSLManager->prepare(/*input=*/true);
	Load(*pSLManager);
}

//-- save/load header (Kernel.h)

void
ReasoningKernel :: SaveHeader ( SaveLoadManager& m ) const
{
	m.o() << InternalStateFileHeader << "\n" << Version << "\n" << bytesInInt << "\n";
}

void
ReasoningKernel :: LoadHeader ( SaveLoadManager& m )
{
	std::string str;
	m.i() >> str;
	if ( str != InternalStateFileHeader )
		throw EFPPSaveLoad("Incompatible save/load header");
	m.i() >> str;
	// FIXME!! we don't check version equivalence for now
//	if ( str != Version )
//		return true;
	int n;
	m.i() >> n;
	if ( n != bytesInInt )
		throw EFPPSaveLoad("Saved file differ in word size");
}

//-- save/load options (Kernel.h)

void
ReasoningKernel :: SaveOptions ( SaveLoadManager& m ) const
{
	m.o() << "Options\n";
}

void
ReasoningKernel :: LoadOptions ( SaveLoadManager& m )
{
	std::string options;
	m.i() >> options;
}

//-- save/load KB (Kernel.h)

void
ReasoningKernel :: SaveKB ( SaveLoadManager& m )
{
	m.saveUInt((unsigned int)getStatus());
	switch ( getStatus() )
	{
	case kbEmpty:	// nothing to do
		return;
	case kbLoading:
		throw EFPPSaveLoad("Can't save internal state of the unclassified reasoner");
	default:
		getTBox()->Save(m);
		break;
	}
}

void
ReasoningKernel :: LoadKB ( SaveLoadManager& m )
{
	KBStatus status = (KBStatus)m.loadUInt();
//	initCacheAndFlags();	// will be done
	// no classification => no need to monitor
	pMonitor = nullptr;
	if ( status == kbEmpty )
		return;
//	newKB();
	getTBox()->Load(m,status);
}

//----------------------------------------------------------
//-- Helpers: Save/Load of class TNECollection
//----------------------------------------------------------

/// Save all the objects in the collection
template <typename T>
static void
SaveTNECollection ( const TNECollection<T>& collection, SaveLoadManager& m, const std::set<const TNamedEntry*>& excluded )
{
	typename TNECollection<T>::const_iterator p, p_beg = collection.begin(), p_end = collection.end();
	// get the max length of the identifier in the collection
	size_t maxLength = 0, curLength, size = 0;

	for ( p = p_beg; p < p_end; ++p )
	{
		if ( excluded.count(*p) == 0 )
		{
			++size;
			if ( maxLength < (curLength = strlen((*p)->getName())) )
				maxLength = curLength;
		}
	}

	// save number of entries and max length of the entry
	m.saveUInt(size);
	m.saveUInt(maxLength);

	// save names of all entries
	for ( p = p_beg; p < p_end; ++p )
	{
		// register all entries in the global map
		m.registerE(*p);
		if ( excluded.count(*p) == 0 )
			m.o() << (*p)->getName() << "\n";
	}

	// save the entries itself
//	for ( p = p_beg; p < p_end; ++p )
//		(*p)->Save(o);
}
/// Load all the objects into the collection
template <typename T>
static void
LoadTNECollection ( TNECollection<T>& collection, SaveLoadManager& m )
{
	// sanity check: Load shall be done for the empty collection and only once
//	fpp_assert ( size() == 0 );

	unsigned int collSize, maxLength;
	collSize = m.loadUInt();
	maxLength = m.loadUInt();
	++maxLength;
	char* name = new char[maxLength];

	// register all the named entries
	for ( unsigned int j = 0; j < collSize; ++j )
	{
		m.i().getline ( name, maxLength, '\n' );
		m.registerE(collection.get(name));
	}

	delete [] name;

	// load all the named entries
//	for ( iterator p = begin(); p < end(); ++p )
//		(*p)->Load(i);
}

//----------------------------------------------------------
//-- Helpers: Save/Load of class RoleMaster
//----------------------------------------------------------

static void
SaveRoleMaster ( const RoleMaster& RM, SaveLoadManager& m )
{
	RoleMaster::const_iterator p, p_beg = RM.begin(), p_end = RM.end();
	// get the max length of the identifier in the collection
	size_t maxLength = 0, curLength, size = 0;

	for ( p = p_beg; p != p_end; p += 2, size++ )
		if ( maxLength < (curLength = strlen((*p)->getName())) )
			maxLength = curLength;

	// save number of entries and max length of the entry
	m.saveUInt(size);
	m.saveUInt(maxLength);

	// register const entries in the global map
	m.registerE(RM.getBotRole());
	m.registerE(RM.getTopRole());

	// save names of all (non-inverse) entries
	for ( p = p_beg; p != p_end; p += 2 )
	{
		TRole* R = *p;
		m.registerE(R);
		m.registerE(R->inverse());
		m.o() << R->getName() << "\n";
	}

//	// save the entries itself
//	for ( p = p_beg; p < p_end; ++p )
//		(*p)->Save(o);
//
//	// save the rest of the RM
//	o << "\nRT";
//	pTax->Save(o);
}

static void
LoadRoleMaster ( RoleMaster& RM, SaveLoadManager& m )
{
	// sanity check: Load shall be done for the empty collection and only once
//	fpp_assert ( size() == 0 );

	unsigned int RMSize, maxLength;
	RMSize = m.loadUInt();
	maxLength = m.loadUInt();
	++maxLength;
	char* name = new char[maxLength];

	// register const entries in the global map
	m.registerE(RM.getBotRole());
	m.registerE(RM.getTopRole());

	// register all the named entries
	for ( unsigned int j = 0; j < RMSize; ++j )
	{
		m.i().getline ( name, maxLength, '\n' );
		TRole* R = RM.ensureRoleName(name);
		m.registerE(R);
		m.registerE(R->inverse());
	}

	delete [] name;

//	// load all the named entries
//	for ( iterator p = begin(); p < end(); ++p )
//		(*p)->Load(i);
//
//	// load the rest of the RM
//	expectChar(i,'R');
//	expectChar(i,'T');
//	pTax = new Taxonomy ( &universalRole, &emptyRole );
//	pTax->Load(i);
//	useUndefinedNames = false;	// no names
}

//----------------------------------------------------------
//-- Helpers: Save/Load of class TDataType
//----------------------------------------------------------

static void
SaveDataType ( const TDataType* dt, SaveLoadManager& m )
{
	// register the DT itself
	m.registerE(dt->getType());
	// register all the data values excluding expressions
	typedef std::set<const TNamedEntry*> TNESet;
	TNESet expressions;
	TNECollection<TDataEntry>::const_iterator p, p_beg = dt->begin(), p_end = dt->end();
	for ( p = p_beg; p != p_end; ++p )
		if ( (*p)->isRestrictedDataType() )
			expressions.insert(*p);
	SaveTNECollection ( *dt, m, expressions );
	// for expressions: register facets in the same order
	for ( p = p_beg; p != p_end; ++p )
		if ( (*p)->isRestrictedDataType() )
		{
			const TDataEntry* de = static_cast<const TDataEntry*>(*p);
			m.registerE(de);
		}
}

static void
LoadDataType ( TDataType* dt, SaveLoadManager& m )
{
	// register the DT itself
	m.registerE(dt->getType());
	// register all the data values
	LoadTNECollection ( *dt, m );
	// for expressions: register facets
	for ( TNECollection<TDataEntry>::const_iterator p = dt->begin(), p_end = dt->end(); p != p_end; ++p )
		if ( (*p)->isRestrictedDataType() )
			m.registerE(*p);
}


//----------------------------------------------------------
//-- Implementation of the DLDag methods (dlDag.h)
//----------------------------------------------------------

static void
SaveDLDag ( const DLDag& dag, SaveLoadManager& m )
{
	m.saveUInt(dag.size());
	m.o() << "\n";
	// skip fake vertex and TOP
	for ( unsigned int i = 2; i < dag.size(); ++i )
		dag[i].Save(m);
}

void
LoadDLDag ( DLDag& dag, SaveLoadManager& m )
{
	unsigned int j, size;
	size = m.loadUInt();
	for ( j = 2; j < size; ++j )
	{
		DagTag tag = static_cast<DagTag>(m.loadUInt());
		DLVertex* v = new DLVertex(tag);
		v->Load(m);
		dag.directAdd(v);
	}

	// only reasoning now -- no cache
	dag.setFinalSize();
}

/// @return true if the DAG in the SL structure is the same that is loaded
static bool
VerifyDag ( const DLDag& dag, SaveLoadManager& m )
{
	unsigned int j, size;
	size = m.loadUInt();

	if ( size != dag.size() )
	{
		std::cout << "DAG verification fail: size " << size << ", expected " << dag.size() << "\n";
		return false;
	}

	for ( j = 2; j < size; ++j )
	{
		DagTag tag = static_cast<DagTag>(m.loadUInt());
		DLVertex* v = new DLVertex(tag);
		v->Load(m);
		if ( *v != dag[j] )
		{
			std::cout << "DAG verification fail: dag entry at " << j << " is ";
			v->Print(std::cout);
			std::cout << ", expected ";
			dag[j].Print(std::cout);
			std::cout << "\n";
			delete v;
			return false;
		}
		delete v;
	}

	return true;
}

// we need a tag to save/load appropriate cache
enum class ModelCacheType: unsigned int { mctConst, mctSingleton, mctIan };

static void
SaveSingleCache ( SaveLoadManager& m, BipolarPointer bp, const modelCacheInterface* cache )
{
	if ( cache == nullptr )
		return;
	m.saveSInt(bp);
	// check all alternatives, starting from the most possible ones
	if ( auto cacheIan = dynamic_cast<const modelCacheIan*>(cache) )
	{
		m.saveUInt(static_cast<unsigned int>(ModelCacheType::mctIan));
		cacheIan->Save(m);
	}
	else if ( auto cacheSingleton = dynamic_cast<const modelCacheSingleton*>(cache) )
	{
		m.saveUInt(static_cast<unsigned int>(ModelCacheType::mctSingleton));
		m.saveSInt(cacheSingleton->getValue());
	}
	else if ( dynamic_cast<const modelCacheConst*>(cache) )
	{
		m.saveUInt(static_cast<unsigned int>(ModelCacheType::mctConst));
		m.saveUInt(cache->getState() == csValid);
	}
	else
		fpp_unreachable();

	m.o() << "\n";
}

static const modelCacheInterface*
LoadSingleCache ( SaveLoadManager& m )
{
	ModelCacheType state = static_cast<ModelCacheType>(m.loadUInt());
	switch ( state )
	{
	case ModelCacheType::mctConst:
		return new modelCacheConst ( m.loadUInt() != 0 );
	case ModelCacheType::mctSingleton:
		return new modelCacheSingleton(m.loadSInt());
	case ModelCacheType::mctIan:
	{
		bool hasNominals = m.loadUInt() > 0;
		unsigned int nC = m.loadUInt();
		unsigned int nR = m.loadUInt();
		modelCacheIan* cache = new modelCacheIan ( hasNominals, nC, nR );
		cache->Load(m);
		return cache;
	}

	default:
		fpp_unreachable();
	}
}

static void
SaveDagCache ( const DLDag& dag, SaveLoadManager& m )
{
	m.o() << "\nDC";	// dag cache
	for ( unsigned int i = 2; i < dag.size(); ++i )
	{
		const DLVertex& v = dag[(int)i];
		SaveSingleCache ( m, createBiPointer(i,true), v.getCache(true) );
		SaveSingleCache ( m, createBiPointer(i,false), v.getCache(false) );
	}
	m.saveUInt(0);
}

static void
LoadDagCache ( DLDag& dag, SaveLoadManager& m )
{
	m.expectChar('D');
	m.expectChar('C');
	while ( BipolarPointer bp = m.loadSInt() )
		dag.setCache ( bp, LoadSingleCache(m) );
}


//----------------------------------------------------------
//-- Implementation of the TBox methods (dlTBox.h)
//----------------------------------------------------------

void
TBox :: initPointerMaps ( SaveLoadManager& m ) const
{
	m.clearPointerMaps();
	m.registerE(pBottom);
	m.registerE(pTop);
	m.registerE(pTemp);
	m.registerE(pQuery);
}

void
TBox :: Save ( SaveLoadManager& m )
{
	initPointerMaps(m);
	m.o() << "\nDT";
	for ( DataTypeCenter::const_iterator p = DTCenter.begin(), p_end = DTCenter.end(); p != p_end; ++p )
		SaveDataType(*p,m);
	m.o() << "\nC";
	std::set<const TNamedEntry*> empty;
	SaveTNECollection(Concepts,m,empty);
	m.o() << "\nI";
	SaveTNECollection(Individuals,m,empty);
	m.o() << "\nOR";
	SaveRoleMaster(ORM,m);
	m.o() << "\nDR";
	SaveRoleMaster(DRM,m);
	m.o() << "\nD";
	DLHeap.removeQuery();
	SaveDLDag(DLHeap,m);
	if ( Status > kbCChecked )
	{
		m.o() << "\nCT";
		pTax->Save(m,empty);
	}
	SaveDagCache(DLHeap,m);
}

void
TBox :: Load ( SaveLoadManager& m, KBStatus status )
{
	Status = status;
	initPointerMaps(m);
	m.expectChar('D');
	m.expectChar('T');
	for ( DataTypeCenter::iterator p = DTCenter.begin(), p_end = DTCenter.end(); p != p_end; ++p )
		LoadDataType(*p,m);
	m.expectChar('C');
	LoadTNECollection(Concepts,m);
	m.expectChar('I');
	LoadTNECollection(Individuals,m);
	m.expectChar('O');
	m.expectChar('R');
	LoadRoleMaster(ORM,m);
	m.expectChar('D');
	m.expectChar('R');
	LoadRoleMaster(DRM,m);
	m.expectChar('D');
	DLHeap.setSubOrder();
//	LoadDLDag(DLHeap,m);
	if ( !VerifyDag(DLHeap,m) )
		throw EFPPSaveLoad("DAG verification failed");
//	initReasoner();
	if ( Status > kbCChecked )
	{
		initTaxonomy();
		pTaxCreator->setBottomUp(GCIs);
		m.expectChar('C');
		m.expectChar('T');
		pTax->Load(m);
	}
	LoadDagCache(DLHeap,m);
}

void
TBox :: SaveTaxonomy ( SaveLoadManager& m, const std::set<const TNamedEntry*>& excluded )
{
	initPointerMaps(m);
	m.o() << "\nC";
	SaveTNECollection(Concepts,m,excluded);
	m.o() << "\nI";
	SaveTNECollection(Individuals,m,excluded);
	m.o() << "\nCT";
	pTax->Save(m,excluded);
}

void
TBox :: LoadTaxonomy ( SaveLoadManager& m )
{
	initPointerMaps(m);
	m.expectChar('C');
	LoadTNECollection(Concepts,m);
	m.expectChar('I');
	LoadTNECollection(Individuals,m);
	initTaxonomy();
	pTaxCreator->setBottomUp(GCIs);
	m.expectChar('C');
	m.expectChar('T');
	pTax->Load(m);
}

//----------------------------------------------------------
//-- Save/Load incremental structures (Kernel.h)
//----------------------------------------------------------

void
ReasoningKernel :: SaveIncremental ( SaveLoadManager& m ) const
{
	if ( !useIncrementalReasoning )
		return;
	m.o() << "\nQ";
	m.saveUInt(Name2Sig.size());
	for ( NameSigMap::const_iterator p = Name2Sig.begin(), p_end = Name2Sig.end(); p != p_end; ++p )
	{
		m.savePointer(p->first);
		m.saveUInt(p->second->size());

		for ( const TNamedEntity* entity : *p->second )
			m.savePointer(entity);
	}
}

void
ReasoningKernel :: LoadIncremental ( SaveLoadManager& m )
{
	if ( !useIncrementalReasoning )
		return;
	m.expectChar('Q');
	Name2Sig.clear();
	unsigned int size = m.loadUInt();
	for ( unsigned int j = 0; j < size; j++ )
	{
		TNamedEntity* entity = m.loadEntity();
		unsigned int sigSize = m.loadUInt();
		TSignature* sig = new TSignature();
		for ( unsigned int k = 0; k < sigSize; k++ )
			sig->add(m.loadEntity());
		Name2Sig[entity] = sig;
	}
}

//----------------------------------------------------------
//-- Implementation of the TNamedEntry methods (tNamedEntry.h)
//----------------------------------------------------------

void
TNamedEntry :: Save ( SaveLoadManager& m ) const
{
	m.saveUInt(getAllFlags());
}

void
TNamedEntry :: Load ( SaveLoadManager& m )
{
	setAllFlags(m.loadUInt());
}

//----------------------------------------------------------
//-- Implementation of the TConcept methods (tConcept.h)
//----------------------------------------------------------

void
TConcept :: Save ( SaveLoadManager& m ) const
{
	ClassifiableEntry::Save(m);
	m.saveUInt((unsigned int)classTag);
	m.saveUInt(tsDepth);
	m.saveSInt(pName);
	m.saveSInt(pBody);
	m.saveUInt(posFeatures.getAllFlags());
	m.saveUInt(negFeatures.getAllFlags());
//	ERSet.Save(m);
}

void
TConcept :: Load ( SaveLoadManager& m )
{
	ClassifiableEntry::Load(m);
	classTag = CTTag(m.loadUInt());
	tsDepth = m.loadUInt();
	pName = m.loadSInt();
	pBody = m.loadSInt();
	posFeatures.setAllFlags(m.loadUInt());
	negFeatures.setAllFlags(m.loadUInt());
//	ERSet.Load(m);
}

//----------------------------------------------------------
//-- Implementation of the TIndividual methods (tIndividual.h)
//----------------------------------------------------------

void
TIndividual :: Save ( SaveLoadManager& m ) const
{
	TConcept::Save(m);
//	RelatedIndex.Save(m);
}

void
TIndividual :: Load ( SaveLoadManager& m )
{
	TConcept::Load(m);
//	RelatedIndex.Load(m);
}

//----------------------------------------------------------
//-- Implementation of the TRole methods (tRole.h)
//----------------------------------------------------------

void
TRole :: Save ( SaveLoadManager& m ) const
{
	ClassifiableEntry::Save(m);
	// FIXME!! think about automaton
}

void
TRole :: Load ( SaveLoadManager& m )
{
	ClassifiableEntry::Load(m);
	// FIXME!! think about automaton
}

//----------------------------------------------------------
//-- Implementation of the TaxonomyVertex methods (taxVertex.h)
//----------------------------------------------------------

void
TaxonomyVertex :: SaveLabel ( SaveLoadManager& m ) const
{
	m.savePointer(sample);
	m.saveUInt(Synonyms.size());
	for ( const auto& synonym: synonyms() )
		m.savePointer(synonym);
	m.o() << "\n";
}

void
TaxonomyVertex :: LoadLabel ( SaveLoadManager& m )
{
	// note that sample is already loaded
	unsigned int size = m.loadUInt();
	for ( unsigned int j = 0; j < size; ++j )
		addSynonym(static_cast<ClassifiableEntry*>(m.loadEntry()));
}

void
TaxonomyVertex :: SaveNeighbours ( SaveLoadManager& m ) const
{
	const_iterator p, p_end;
	m.saveUInt(neigh(true).size());
	for ( p = begin(true), p_end = end(true); p != p_end; ++p )
		m.savePointer(*p);
	m.saveUInt(neigh(false).size());
	for ( p = begin(false), p_end = end(false); p != p_end; ++p )
		m.savePointer(*p);
	m.o() << "\n";
}

void
TaxonomyVertex :: LoadNeighbours ( SaveLoadManager& m )
{
	unsigned int j, size;
	size = m.loadUInt();
	for ( j = 0; j < size; ++j )
		addNeighbour ( true, m.loadVertex() );
	size = m.loadUInt();
	for ( j = 0; j < size; ++j )
		addNeighbour ( false, m.loadVertex() );
}

//----------------------------------------------------------
//-- Implementation of the Taxonomy methods (Taxonomy.h)
//----------------------------------------------------------

void
Taxonomy :: Save ( SaveLoadManager& m, const std::set<const TNamedEntry*>& excluded ATTR_UNUSED ) const
{
	TaxVertexVec::const_iterator p, p_beg = Graph.begin(), p_end = Graph.end();
	for ( p = p_beg; p != p_end; ++p )
		m.registerV(*p);

	// save number of taxonomy elements
	m.saveUInt(Graph.size()/*-excluded.size()*/);
	m.o() << "\n";

	// save labels for all vertices of the taxonomy
	for ( p = p_beg; p != p_end; ++p )
//		if ( excluded.count((*p)->getPrimer()) == 0 )
			(*p)->SaveLabel(m);

	// save the taxonomies hierarchy
	for ( p = p_beg; p != p_end; ++p )
//		if ( excluded.count((*p)->getPrimer()) == 0 )
			(*p)->SaveNeighbours(m);
}

void
Taxonomy :: Load ( SaveLoadManager& m )
{
	unsigned int size = m.loadUInt();
	Graph.clear();	// both TOP and BOTTOM elements would be load;

	// create all the vertices and load their labels
	for ( unsigned int j = 0; j < size; ++j )
	{
		ClassifiableEntry* p = static_cast<ClassifiableEntry*>(m.loadEntry());
		TaxonomyVertex* v = new TaxonomyVertex(p);
		Graph.push_back(v);
		v->LoadLabel(m);
		m.registerV(v);
	}

	// load the hierarchy
	for ( TaxVertexVec::iterator p = Graph.begin(), p_end = Graph.end(); p < p_end; ++p )
		(*p)->LoadNeighbours(m);
}

//----------------------------------------------------------
//-- Implementation of the modelCacheIan methods (modelCacheIan.h)
//----------------------------------------------------------

static void
SaveIndexSet ( SaveLoadManager& m, const TSetAsTree& Set )
{
	m.saveUInt(Set.size());
	for ( TSetAsTree::const_iterator p = Set.begin(), p_end = Set.end(); p != p_end; ++p )
		m.saveUInt(*p);
}

static void
LoadIndexSet ( SaveLoadManager& m, TSetAsTree& Set )
{
	unsigned int n = m.loadUInt();
	for ( unsigned int i = 0; i < n; i++ )
		Set.insert(m.loadUInt());
}

void
modelCacheIan :: Save ( SaveLoadManager& m ) const
{
	// header: hasNominals, nC, nR
	m.saveUInt(hasNominalNode);
	m.saveUInt(posDConcepts.maxSize());
	m.saveUInt(existsRoles.maxSize());
	// the body that will be loaded
	SaveIndexSet(m,posDConcepts);
	SaveIndexSet(m,posNConcepts);
	SaveIndexSet(m,negDConcepts);
	SaveIndexSet(m,negNConcepts);
#ifdef RKG_USE_SIMPLE_RULES
	SaveIndexSet(o,extraDConcepts);
	SaveIndexSet(o,extraNConcepts);
#endif
	SaveIndexSet(m,existsRoles);
	SaveIndexSet(m,forallRoles);
	SaveIndexSet(m,funcRoles);
	m.saveUInt(curState);
}

void
modelCacheIan :: Load ( SaveLoadManager& m )
{
	// note that nominals, nC and nR already read, and all sets are created
	LoadIndexSet(m,posDConcepts);
	LoadIndexSet(m,posNConcepts);
	LoadIndexSet(m,negDConcepts);
	LoadIndexSet(m,negNConcepts);
#ifdef RKG_USE_SIMPLE_RULES
	LoadIndexSet(m,extraDConcepts);
	LoadIndexSet(m,extraNConcepts);
#endif
	LoadIndexSet(m,existsRoles);
	LoadIndexSet(m,forallRoles);
	LoadIndexSet(m,funcRoles);
	curState = (modelCacheState) m.loadUInt();
}

//----------------------------------------------------------
//-- Implementation of the DLVertex methods (dlVertex.h)
//----------------------------------------------------------

void
DLVertex :: Save ( SaveLoadManager& m ) const
{
	m.saveUInt(static_cast<unsigned int>(Type()));

	switch ( Type() )
	{
	case dtBad:
	case dtTop:		// can't be S/L
	default:
		fpp_unreachable();
		break;

	case dtAnd:
		m.saveUInt(Child.size());
		for ( const_iterator p = begin(); p != end(); ++p )
			m.saveSInt(*p);
		break;

	case dtLE:
		m.savePointer(Role);
		m.saveSInt(getC());
		m.saveUInt(getNumberLE());
		break;

	case dtForall:	// n here is for the automaton state
		m.savePointer(Role);
		m.saveSInt(getC());
		m.saveUInt(getNumberLE());
		break;

	case dtIrr:
		m.savePointer(Role);
		break;

	case dtPConcept:
	case dtNConcept:
	case dtPSingleton:
	case dtNSingleton:
		m.savePointer(Concept);
		m.saveSInt(getC());
		break;

	case dtProj:
		m.saveSInt(getC());
		m.savePointer(Role);
		m.savePointer(ProjRole);
		break;

	case dtNN:	// nothing to do
		break;

	case dtDataType:
	case dtDataValue:
	case dtDataExpr:
		m.savePointer(Concept);
		m.saveSInt(getC());
		break;
	}
	m.o() << "\n";
}

void
DLVertex :: Load ( SaveLoadManager& m )
{
	// now OP is already loaded
	switch ( Type() )
	{
	case dtBad:
	case dtTop:		// can't be S/L
	default:
		fpp_unreachable();
		break;

	case dtAnd:
	{
		unsigned int size = m.loadUInt();
		for ( unsigned int j = 0; j < size; ++j )
			Child.push_back(m.loadSInt());
		break;
	}

	case dtLE:
		Role = static_cast<const TRole*>(m.loadEntry());
		setChild(m.loadSInt());
		n = m.loadUInt();
		break;

	case dtForall:
		Role = static_cast<const TRole*>(m.loadEntry());
		setChild(m.loadSInt());
		n = m.loadUInt();
		break;

	case dtIrr:
		Role = static_cast<const TRole*>(m.loadEntry());
		break;

	case dtPConcept:
	case dtNConcept:
	case dtPSingleton:
	case dtNSingleton:
		setConcept(m.loadEntry());
		setChild(m.loadSInt());
		break;

	case dtProj:
		setChild(m.loadSInt());
		Role = static_cast<const TRole*>(m.loadEntry());
		ProjRole = static_cast<const TRole*>(m.loadEntry());
		break;

	case dtNN:	// nothing to do
		break;

	case dtDataType:
	case dtDataValue:
	case dtDataExpr:
		setConcept(m.loadEntry());
		setChild(m.loadSInt());
		break;
	}
}
