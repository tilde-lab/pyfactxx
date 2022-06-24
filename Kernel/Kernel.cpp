/* This file is part of the FaCT++ DL reasoner
Copyright (C) 2003-2015 Dmitry Tsarkov and The University of Manchester
Copyright (C) 2015-2017 Dmitry Tsarkov
Copyright (C) 2021-2022 Ivan Rygaev and Tilde MI

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

#include "Kernel.h"
#include "tOntologyLoader.h"
#include "tOntologyPrinterLISP.h"
#include "AtomicDecomposer.h"
#include "OntologyBasedModularizer.h"
#include "eFPPSaveLoad.h"
#include "SaveLoadManager.h"
#include "TripleGatherer.h"

const char* ReasoningKernel :: Version = "1.8.0-SNAPSHOT";
const char* ReasoningKernel :: SupportedDL = "SROIQ(D)";
const char* ReasoningKernel :: Copyright =
	"Copyright (C) Dmitry Tsarkov, Ivan Rygaev, 2002-2022";
const char* ReasoningKernel :: ReleaseDate = "31 March 2022";

// print the FaCT++ information only once
static bool KernelFirstRun = true;

// debug related individual/values switch
//#define FPP_DEBUG_PRINT_RELATED_PROGRESS

ReasoningKernel :: ReasoningKernel ( void )
{
	// Intro
	if ( KernelFirstRun )
	{
		std::cerr << "FaCT++.Kernel: Reasoner for the " << SupportedDL << " Description Logic, " << 8*sizeof(void*) << "-bit\n"
				  << Copyright << ". Version " << Version << " (" << ReleaseDate << ")\n";
		KernelFirstRun = false;
	}

	// init option set (fill with options):
	if ( initOptions () )
		throw EFaCTPlusPlus("FaCT++ kernel: Cannot init options");
}

/// d'tor
ReasoningKernel :: ~ReasoningKernel()
{
	clearTBox();
	deleteTree(cachedQueryTree);
	delete pMonitor;
	delete pSLManager;
	for ( NameSigMap::iterator p = Name2Sig.begin(), p_end = Name2Sig.end(); p != p_end; ++p )
		delete p->second;
}

/// clear TBox and related structures; keep ontology in place
void
ReasoningKernel :: clearTBox ( void )
{
	delete pTBox;
	pTBox = nullptr;
	delete pET;
	pET = nullptr;
	delete KE;
	KE = nullptr;
	delete AD;
	AD = nullptr;
	delete ModSem;
	ModSem = nullptr;
	delete ModSyn;
	ModSyn = nullptr;
	delete ModSynCount;
	ModSynCount = nullptr;
	// during preprocessing the TBox names were cached. clear that cache now.
	getExpressionManager()->clearNameCache();
}

bool
ReasoningKernel :: needForceReload ( void ) const
{
	// if no TBox known -- reload
	if ( pTBox == nullptr )
		return true;
	// if ontology wasn't change -- no need to reload
	if ( !Ontology.isChanged() )
		return false;
	// no incremental required -- nothing to do
	if ( !useIncrementalReasoning )
		return true;
	return false;
}

/// force the re-classification of the changed ontology
void
ReasoningKernel :: forceReload ( void )
{
	// reset TBox
	clearTBox();
	newKB();

	// Protege (as the only user of non-trivial monitors with reload) does not accept multiple usage of a monitor
	// so switch it off after the 1st usage
	pMonitor = nullptr;

	// (re)load ontology
	TOntologyLoader OntologyLoader(*getTBox());
	OntologyLoader.visitOntology(Ontology);

	if ( dumpOntology )
		dumpLISP(std::cout);
	if ( useIncrementalReasoning )
		initIncremental();

	// after loading ontology became processed completely
	Ontology.setProcessed();
}

void ReasoningKernel :: dumpLISP ( std::ostream& o_ ) {
	TLISPOntologyPrinter OntologyPrinter(o_);
	// First print all the declaration (as datarole declarations should be before usages)
	OntologyPrinter.setPrintFlags(/*declarations=*/true, /*axioms=*/false);
	Ontology.visitOntology(OntologyPrinter);
	// Then print logical axioms
	OntologyPrinter.setPrintFlags(/*declarations=*/false, /*axioms=*/true);
	Ontology.visitOntology(OntologyPrinter);
}


//-------------------------------------------------
// Prepare reasoning/query
//-------------------------------------------------

void
ReasoningKernel :: ClassifyOrLoad ( bool needIndividuals )
{
	if ( pSLManager != nullptr )	// try to load the taxonomy
	{
		if ( pSLManager->existsContent() )
		{	// previous version exists
			try
			{
				Load();	// loaded => nothing to do
				return;
			}
			catch ( const EFPPSaveLoad& )
			{
				// fail to load -- fall through to the real action
			}
		}
	}
	// perform the real classification
	if ( needIndividuals )
		pTBox->performRealisation();
	else
		pTBox->performClassification();

	// save the result if necessary
	if ( pSLManager != nullptr )
		Save();
}

void
ReasoningKernel :: processKB ( KBStatus status )
{
	fpp_assert ( status >= kbCChecked );

	// check whether reasoning was failed
	if ( reasoningFailed )
		throw EFaCTPlusPlus("Can't answer queries due to previous errors");

	KBStatus curStatus = getStatus();

	if ( curStatus >= status )
	{	// nothing to do; but make sure that we are consistent
		if ( !isKBConsistent() )
			throw EFPPInconsistentKB();
		return;
	}

	// here curStatus < kbRealised, and status >= kbChecked
	if ( curStatus == kbEmpty || curStatus == kbLoading )
	{	// load and preprocess KB -- here might be failures
		reasoningFailed = true;

		// load the axioms from the ontology to the TBox
		if ( needForceReload() )
			forceReload();
		else	// just do incremental classification and exit
		{
			doIncremental();
			reasoningFailed = false;
			return;
		}

		// do the preprocessing and consistency check
		pTBox->isConsistent();

		// if there were no exception thrown -- clear the failure status
		reasoningFailed = false;

		// if the consistency check is all we need -- return
		if ( status == kbCChecked )
			return;
	}

	// here we need to do classification or realisation

	if ( !pTBox->isConsistent() )	// nothing to do for inconsistent ontologies
		return;

	ClassifyOrLoad(status == kbRealised);
}

//-----------------------------------------------------------------------------
//--		query caching support
//-----------------------------------------------------------------------------

/// classify query; cache is ready at the point. NAMED means whether concept is just a name
void
ReasoningKernel :: classifyQuery ( void )
{
	// make sure KB is classified
	classifyKB();

	// ... and the cache entry is properly cleared
	fpp_assert ( cachedVertex == nullptr );

	// classify general query expression
	bool complexQuery = getTBox()->isComplexQuery(cachedConcept);
	if ( complexQuery )
		getTBox()->classifyQueryConcept();

	// now cached concept is classified
	cachedVertex = cachedConcept->getTaxVertex();

	if ( unlikely(cachedVertex == nullptr) )	// fresh concept
	{
		fpp_assert (!complexQuery);
		cachedVertex = getCTaxonomy()->getFreshVertex(cachedConcept);
	}

	// setup proper cache level
	cacheLevel = csClassified;
}

void
ReasoningKernel :: setUpSatCache ( DLTree* query )
{
	// if KB was changed since it was classified,
	// we should catch it before
	fpp_assert ( !Ontology.isChanged() );

	// check if the query is already cached
	if ( checkQueryCache(query) )
	{	// the level should be the same
		fpp_assert ( cacheLevel == csSat );
		// nothing to do
		deleteTree(query);
		return;
	}

	// clear currently cached query
	clearQueryCache();

	// setup concept to be queried
	setQueryConcept(query);

	// everything is fine -- set up cache now
	setQueryCache(query);
	cacheLevel = csSat;
}

void
ReasoningKernel :: setUpCache ( TConceptExpr* query, cacheStatus level )
{
	// if KB was changed since it was classified,
	// we should catch it before
	fpp_assert ( !Ontology.isChanged() );

	// check if the query is already cached
	if ( checkQueryCache(query) )
	{
		fpp_assert ( cacheLevel != csEmpty );
		// query cached with the same or lower level -- nothing to do
		if ( level <= cacheLevel )
			return;
		// the only other option is: cache level is SAT but CLASSIFIED is requested
		fpp_assert ( level == csClassified );
		fpp_assert ( cacheLevel == csSat );
		// classify query and return
		classifyQuery();
		return;
	}

	// clear currently cached query
	clearQueryCache();

	// setup concept to be queried
	setQueryConcept(TreeDeleter(e(query)));

	// everything is fine -- set up cache now
	setQueryCache(query);
	cacheLevel = csSat;

	if ( level == csClassified )
		classifyQuery();
}

//-------------------------------------------------
// concept subsumption query implementation
//-------------------------------------------------

/// class for exploring concept taxonomy to find super classes
class SupConceptActor: public WalkerInterface
{
protected:
	const ClassifiableEntry* pe;
	void entry ( const ClassifiableEntry* q ) { if ( pe == q ) throw std::exception(); }
public:
	explicit SupConceptActor ( ClassifiableEntry* q ) : pe(q) {}
	bool apply ( const TaxonomyVertex& v ) override
	{
		entry(v.getPrimer());
		for ( const auto& synonym: v.synonyms() )
			entry(synonym);
		return true;
	}
}; // SupConceptActor

/// @return true iff C [= D holds
bool
ReasoningKernel :: checkSub ( TConcept* C, TConcept* D )
{
	// check whether a concept is fresh
	if ( unlikely(!isValid(D->pName)) )	// D is fresh
	{
		if ( unlikely(!isValid(C->pName)) )	// C is fresh
			return C == D;	// 2 fresh concepts subsumes one another iff they are the same
		else	// C is known
			return !getTBox()->isSatisfiable(C);	// C [= D iff C=\bottom
	}
	else	// D is known
		if ( unlikely(!isValid(C->pName)) )	// C is fresh
			// C [= D iff D = \top, or ~D = \bottom
			return !checkSatTree(createSNFNot(getTBox()->getTree(C)));
	// here C and D are known (not fresh)
	// check the obvious ones
	if ( unlikely(D->isTop()) || unlikely(C->isBottom()) )
		return true;
	if ( getStatus() < kbClassified )	// unclassified => do via SAT test
		return getTBox()->isSubHolds ( C, D );
	// classified => do the taxonomy traversal
	SupConceptActor actor(D);
	Taxonomy* tax = getCTaxonomy();
	try { tax->getRelativesInfo</*needCurrent=*/true, /*onlyDirect=*/false, /*upDirection=*/true> ( C->getTaxVertex(), actor ); return false; }
	catch (...) { tax->clearVisited(); return true; }
}

//-------------------------------------------------
// all-disjoint query implementation
//-------------------------------------------------

bool
ReasoningKernel :: isDisjointRoles ( void )
{
	// grab all roles from the arg-list
	typedef const std::vector<const TDLExpression*> TExprVec;
	typedef std::vector<const TRole*> TRoleVec;
	TExprVec Disj = getExpressionManager()->getArgList();
	TRoleVec Roles;
	Roles.reserve(Disj.size());
	unsigned int nTopRoles = 0;

	for ( auto* arg : Disj )
	{
		if ( TORoleExpr* ORole = dynamic_cast<TORoleExpr*>(arg) )
		{
			TRole* R = getRole ( ORole, "Role expression expected in isDisjointRoles()" );
			if ( R->isBottom() )
				continue;		// empty role is disjoint with everything
			if ( R->isTop() )
				++nTopRoles;	// count universal roles
			else
				Roles.push_back(R);
		}
		else if ( TDRoleExpr* DRole = dynamic_cast<TDRoleExpr*>(arg) )
		{
			TRole* R = getRole ( DRole, "Role expression expected in isDisjointRoles()" );
			if ( R->isBottom() )
				continue;		// empty role is disjoint with everything
			if ( R->isTop() )
				++nTopRoles;	// count universal roles
			else
				Roles.push_back(R);
		}
		else
			throw EFaCTPlusPlus ( "Role expression expected in isDisjointRoles()" );
	}

	// deal with top-roles
	if ( nTopRoles > 0 )
	{
		if ( nTopRoles > 1 || !Roles.empty() )
			return false;	// universal role is not disjoint with anything but the bottom role
		else
			return true;
	}

	// test pair-wise disjointness
	TRoleVec::const_iterator q = Roles.begin(), q_end = Roles.end(), s;
	for ( ; q != q_end; ++q )
		for ( s = q+1; s != q_end; ++s )
			if ( !getTBox()->isDisjointRoles(*q,*s) )
				return false;

	return true;
}

//-------------------------------------------------
// related individuals implementation
//-------------------------------------------------

class RIActor: public WalkerInterface
{
protected:
	ReasoningKernel::CIVec acc;

		/// process single entry in a vertex label
	bool tryEntry ( const ClassifiableEntry* p )
	{
		// check the applicability
		if ( p->isSystem() || !static_cast<const TConcept*>(p)->isSingleton() )
			return false;

		// print the concept
		acc.push_back(static_cast<const TIndividual*>(p));
		return true;
	}

public:
	RIActor() = default;

	bool apply ( const TaxonomyVertex& v ) override
	{
		bool ret = tryEntry(v.getPrimer());

		for ( const auto& synonym: v.synonyms() )
			ret |= tryEntry(synonym);

		return ret;
	}
	void clear ( void ) { acc.clear(); }
	const ReasoningKernel::CIVec& getAcc ( void ) const { return acc; }
}; // RIActor

ReasoningKernel::CIVec
ReasoningKernel :: buildRelatedCache ( TIndividual* I, const TRole* R )
{
#ifdef FPP_DEBUG_PRINT_RELATED_PROGRESS
	std::cout << "Related for " << I->getName() << " via property " << R->getName() << "\n";
#endif

	// for synonyms: use the representative's cache
	if ( R->isSynonym() )
		return getRelated ( I, resolveSynonym(R) );

	// FIXME!! return an empty set for data roles
	if ( R->isDataRole() )
		return CIVec();

	// empty role has no fillers
	if ( R->isBottom() )
		return CIVec();

	// now fills the query
	RIActor actor;
	// ask for instances of \exists R^-.{i}
	TORoleExpr* InvR = R->getId() > 0
		? getExpressionManager()->Inverse(getExpressionManager()->ObjectRole(R->getName()))
		: getExpressionManager()->ObjectRole(R->inverse()->getName());
	TConceptExpr* query =
		R->isTop() ? getExpressionManager()->Top() : 	// universal role has all the named individuals as a filler
		getExpressionManager()->Value ( InvR, getExpressionManager()->Individual(I->getName()) );
	getInstances ( query, actor );
	return actor.getAcc();
}

/// @return in Rs all (DATA)-roles R s.t. (I,x):R; add inverses if NEEDI is true
void
ReasoningKernel :: getRelatedRoles ( const TIndividualExpr* I, NamesVector& Rs, bool data, bool needI )
{
	realiseKB();	// ensure KB is ready to answer the query
	Rs.clear();

	TIndividual* i = getIndividual ( I, "individual name expected in the getRelatedRoles()" );
	for ( const auto& R: (data ? getDRM() : getORM()) )
		if ( ( R->getId() > 0 || needI ) && !getRelated(i,R).empty() )
			Rs.push_back(R);
}

void
ReasoningKernel :: getRoleFillers ( const TIndividualExpr* I, const TORoleExpr* R, IndividualSet& Result )
{
	CIVec vec = getRoleFillers( I, R );
	for ( CIVec::iterator p = vec.begin(), p_end = vec.end(); p < p_end; ++p )
		Result.push_back(const_cast<TIndividual*>(*p));
}
const ReasoningKernel::CIVec&
ReasoningKernel :: getRoleFillers ( const TIndividualExpr* I, const TORoleExpr* R)
{
	realiseKB();    // ensure KB is ready to answer the query
	return getRelated ( getIndividual ( I, "Individual name expected in the getRoleFillers()" ),
						getRole ( R, "Role expression expected in the getRoleFillers()" ) );
}


/// @return true iff R(I,J) holds
bool
ReasoningKernel :: isRelated ( const TIndividualExpr* I, const TORoleExpr* R, const TIndividualExpr* J )
{
	realiseKB();	// ensure KB is ready to answer the query
	TIndividual* i = getIndividual ( I, "Individual name expected in the isRelated()" );
	TRole* r = getRole ( R, "Role expression expected in the isRelated()" );
	if ( r->isDataRole() )
		return false;	// FIXME!! not implemented

	TIndividual* j = getIndividual ( J, "Individual name expected in the isRelated()" );
	CIVec vec = getRelated ( i, r );
	for ( CIVec::iterator p = vec.begin(), p_end = vec.end(); p < p_end; ++p )
		if ( j == (*p) )
			return true;

	return false;
}

/// @return true iff A(I,V) holds
bool
ReasoningKernel :: isRelated ( const TIndividualExpr* I, const TDRoleExpr* A, const TDataValueExpr* V )
{
	TDLConceptDataValue exists(A, V);
	return isInstance(I, &exists);
}

TDLConceptExpression* ReasoningKernel::parseConcept(const std::string& name)
{
    auto* em = getExpressionManager();

    if (name == OWL_THING)
        return em->Top();
    else if (name == OWL_NOTHING)
        return em->Bottom();
    else
        return em->Concept(name);
}

const TConcept* ReasoningKernel::getConcept(TDLIndividualName* i_exp, TDLConceptExpression* c_exp)
{
    auto* em = getExpressionManager();

    if (strlen(i_exp->getName()) == 0)
        return nullptr;
    else if (c_exp == em->Top())
        return static_cast<const TConcept*>(getTBox()->getTaxonomy()->getTopVertex()->getPrimer());
    else if (c_exp == em->Bottom())
        return static_cast<const TConcept*>(getTBox()->getTaxonomy()->getBottomVertex()->getPrimer());
    else if (i_exp->getEntry() != nullptr)
        return static_cast<const TConcept*>(i_exp->getEntry());
    else
        return static_cast<const TConcept*>(static_cast<TDLConceptName*>(c_exp)->getEntry());
}

const TRole* ReasoningKernel::getRole(TDLObjectRoleExpression* o_exp, TDLDataRoleExpression* d_exp)
{
    auto* em = getExpressionManager();
    auto& orm = getORM();
    auto& drm = getDRM();


    if (o_exp == em->ObjectRoleTop())
    {
        return orm.getTopRole();
    }
    else if (o_exp == em->ObjectRoleBottom())
    {
        return orm.getBotRole();
    }
    else if (d_exp == em->DataRoleTop())
    {
        return drm.getTopRole();
    }
    else if (d_exp == em->DataRoleBottom())
    {
        return drm.getBotRole();
    }
    else
    {
        auto* o_name = static_cast<TDLObjectRoleName*>(o_exp);
        auto* d_name = static_cast<TDLDataRoleName*>(d_exp);

        if (strlen(o_name->getName()) == 0 || isSpecialRole(o_name->getName()))
            return nullptr;
        else if (o_name->getEntry() != nullptr)
            return static_cast<TRole*>(o_name->getEntry());
        else
            return static_cast<TRole*>(d_name->getEntry());
    }
}

TDLObjectRoleExpression* ReasoningKernel::parseObjectRole(const std::string& name)
{
    auto* em = getExpressionManager();

    if (name == OWL_TOP_OBJECT_PROPERTY)
        return em->ObjectRoleTop();
    else if (name == OWL_BOTTOM_OBJECT_PROPERTY)
        return em->ObjectRoleBottom();
    else
        return em->ObjectRole(name);
}

TDLDataRoleExpression* ReasoningKernel::parseDataRole(const std::string& name)
{
    auto* em = getExpressionManager();

    if (name == OWL_TOP_DATA_PROPERTY)
        return em->DataRoleTop();
    else if (name == OWL_BOTTOM_DATA_PROPERTY)
        return em->DataRoleBottom();
    else
        return em->DataRole(name);
}

void ReasoningKernel::getTriples(const std::string& q_subj_name, const std::string& q_role_name, const std::string& q_obj_name, std::set<std::vector<std::string>>& triples)
{
    TBox* tBox = getTBox();
    auto* em = getExpressionManager();

    TDLIndividualName* q_subj_i_exp = em->Individual(q_subj_name);
    TDLConceptExpression* q_subj_c_exp = parseConcept(q_subj_name);
    TDLObjectRoleExpression* q_subj_o_role_exp = parseObjectRole(q_subj_name);
    TDLDataRoleExpression* q_subj_d_role_exp = parseDataRole(q_subj_name);

    TDLObjectRoleName* q_o_role_exp = em->ObjectRole(q_role_name);
    TDLDataRoleName* q_d_role_exp = em->DataRole(q_role_name);

    TDLIndividualName* q_obj_i_exp = em->Individual(q_obj_name);
    TDLConceptExpression* q_obj_c_exp = parseConcept(q_obj_name);
    TDLObjectRoleExpression* q_obj_o_role_exp = parseObjectRole(q_obj_name);
    TDLDataRoleExpression* q_obj_d_role_exp = parseDataRole(q_obj_name);

    const TConcept* q_subj = getConcept(q_subj_i_exp, q_subj_c_exp);
    const TRole* q_subj_role = getRole(q_subj_o_role_exp, q_subj_d_role_exp);
    const TRole* q_role = getRole(q_o_role_exp, q_d_role_exp);
    const TConcept* q_obj = getConcept(q_obj_i_exp, q_obj_c_exp);
    const TRole* q_obj_role = getRole(q_obj_o_role_exp, q_obj_d_role_exp);

    // If subject or role is not found in the ontology, return an empty set
    if (!q_subj_name.empty() && q_subj == nullptr && q_subj_role == nullptr || !q_role_name.empty() && !isSpecialRole(q_role_name) && q_role == nullptr)
        return;

    if (q_subj_name.empty())
    {
        // Subject is not specified in the query

        // If role and object are specified in the query process special roles separately
        if (q_role_name == RDF_TYPE && q_obj != nullptr)
        {
            // All instances of a specific class

            if (!q_obj->isSingleton())
            {
                TripleGatherer gatherer(&triples, false, RDF_TYPE, q_obj_name.c_str(), [](const ClassifiableEntry* entry)
                {
                    return static_cast<const TConcept*>(entry)->isSingleton();
                });

                getInstances(q_obj_c_exp, gatherer);
            }
            else
            {
                return;
            }
        }
        else if (q_role_name == RDFS_SUBCLASS_OF && q_obj != nullptr)
        {
            // All subclasses of a specific class

            if (!q_obj->isSingleton())
            {
                TripleGatherer gatherer(&triples, false, RDFS_SUBCLASS_OF, q_obj_name.c_str(), [q_obj](const ClassifiableEntry* entry)
                {
                    const TConcept* concept = static_cast<const TConcept*>(entry);
                    return entry != q_obj && !concept->isSingleton() && !concept->isBottom();
                });

                getSubConcepts(q_obj_c_exp, false, gatherer, true);
            }
            else
            {
                return;
            }
        }
        else if (q_role_name == OWL_SAME_AS && q_obj != nullptr)
        {
            // All equivalent individuals to a specific individual

            if (q_obj->isSingleton())
            {
                TripleGatherer gatherer(&triples, false, OWL_SAME_AS, q_obj_name.c_str(), [q_obj](const ClassifiableEntry* entry)
                {
                    return entry != q_obj && static_cast<const TConcept*>(entry)->isSingleton();
                });

                getSameAs(q_obj_i_exp, gatherer);
            }
            else
            {
                return;
            }
        }
        else if (q_role_name == OWL_EQUIVALENT_CLASS && q_obj != nullptr)
        {
            // All equivalent classes to a specific class

            if (!q_obj->isSingleton())
            {
                TripleGatherer gatherer(&triples, false, OWL_EQUIVALENT_CLASS, q_obj_name.c_str(), [q_obj](const ClassifiableEntry* entry)
                {
                    return entry != q_obj && !static_cast<const TConcept*>(entry)->isSingleton();
                });

                getEquivalentConcepts(q_obj_c_exp, gatherer);
            }
            else
            {
                return;
            }
        }
        /*else if (q_role_name == OWL_DISJOINT_WITH)
        {
            // All disjoint classes with a specific class

            if (!q_obj->isSingleton())
            {
                TripleGatherer gatherer(&triples, false, OWL_DISJOINT_WITH, q_obj_name.c_str(), [](const ClassifiableEntry* entry)
                {
                    const TConcept* concept = static_cast<const TConcept*>(entry);
                    return !concept->isSingleton() && !concept->isSystem() && !concept->isBottom();
                });

                getDisjointConcepts(q_obj_c_exp, gatherer);
            }
            else
            {
                return;
            }
        }*/
        else if (q_role_name == RDFS_SUBPROPERTY_OF && q_obj_role != nullptr)
        {
            // All subproperties of a specific property

            TripleGatherer gatherer(&triples, false, RDFS_SUBPROPERTY_OF, q_obj_name.c_str(), [q_obj_role](const ClassifiableEntry* entry)
            {
                return entry != q_obj_role && static_cast<const TNamedEntry*>(entry)->getId() >= 0;
            });

            if (q_obj_role->isDataRole())
                getSubRoles(q_obj_d_role_exp, false, gatherer);
            else
                getSubRoles(q_obj_o_role_exp, false, gatherer);
        }
        else if (q_role_name == OWL_EQUIVALENT_PROPERTY && q_obj_role != nullptr)
        {
            // All equivalent properties to a specific property

            TripleGatherer gatherer(&triples, false, OWL_EQUIVALENT_PROPERTY, q_obj_name.c_str(), [q_obj_role](const ClassifiableEntry* entry)
            {
                return entry != q_obj_role && static_cast<const TNamedEntry*>(entry)->getId() >= 0;
            });

            if (q_obj_role->isDataRole())
                getEquivalentRoles(q_obj_d_role_exp, gatherer);
            else
                getEquivalentRoles(q_obj_o_role_exp, gatherer);
        }
        else if (q_role_name == OWL_INVERSE_OF && q_obj_role != nullptr)
        {
            // Inverse property

            if (!q_obj_role->isDataRole())
            {
                const auto* inverse_role = q_obj_role->inverse();

                if (inverse_role != nullptr && inverse_role->getId() >= 0)
                    pushTriple(triples, toRdf(inverse_role->getName()), OWL_INVERSE_OF, q_obj_name.c_str());
            }
        }
        else
        {
            // Otherwise take the list of all individuals, classes and roles and run the same query for each individual, class or role separately

            for (auto p_concept = tBox->c_begin(); p_concept < tBox->c_end(); p_concept++)
            {
                const TConcept* concept = *p_concept;

                if (!concept->isTop() && !concept->isBottom())
                    getTriples(concept->getName(), q_role_name, q_obj_name, triples);
            }

            for (auto p_ind = tBox->i_begin(); p_ind < tBox->i_end(); p_ind++)
            {
                const TIndividual* ind = *p_ind;
                getTriples(ind->getName(), q_role_name, q_obj_name, triples);
            }

            const auto& orm = tBox->getORM();

            for (auto p_role = orm.begin(); p_role < orm.end(); p_role++)
            {
                const TRole* role = *p_role;
                getTriples(role->getName(), q_role_name, q_obj_name, triples);
            }

            const auto& drm = tBox->getDRM();

            for (auto p_role = drm.begin(); p_role < drm.end(); p_role++)
            {
                const TRole* role = *p_role;
                getTriples(role->getName(), q_role_name, q_obj_name, triples);
            }
        }
    }
    else if (q_subj != nullptr)
    {
        // Subject is specified in the query and it is not a role

        if (q_subj->isSingleton())
        {
            // Query subject is an individual

            const TIndividual* q_subj_ind = static_cast<const TIndividual*>(q_subj);

            NamesVector roles;

            // Fill required role list (either with all roles or with a single role from the query)
            if (q_role_name.empty())
                getRelatedRoles(q_subj_i_exp, roles, false, false);
            else if (!isSpecialRole(q_role_name) && !q_role->isDataRole())
                roles.push_back(q_role);

            // If types are needed
            if (q_role_name.empty() || q_role_name == RDF_TYPE)
            {
                if (q_obj_name.empty())
                {
                    // All types
                    TripleGatherer gatherer(&triples, true, RDF_TYPE, q_subj_name.c_str(), [](const ClassifiableEntry* entry)
                    {
                        return !static_cast<const TConcept*>(entry)->isSingleton();
                    });

                    getTypes(q_subj_i_exp, false, gatherer);
                }
                else if (q_obj != nullptr && !q_obj->isSingleton())
                {
                    // One specific type from the query
                    if (isInstance(q_subj_i_exp, q_obj_c_exp))
                        pushTriple(triples, q_subj_name.c_str(), RDF_TYPE, q_obj_name.c_str());
                }
            }

            // If synonyms are needed
            if (q_subj != nullptr && q_role_name.empty() || q_role_name == OWL_SAME_AS)
            {
                if (q_obj_name.empty())
                {
                    // All synonyms
                    TripleGatherer gatherer(&triples, true, OWL_SAME_AS, q_subj_name.c_str(), [q_subj](const ClassifiableEntry* entry)
                    {
                        return entry != q_subj && static_cast<const TConcept*>(entry)->isSingleton();
                    });

                    getSameAs(q_subj_i_exp, gatherer);
                }
                else if (q_obj != nullptr && q_obj->isSingleton() && q_obj != q_subj)
                {
                    // A single synonym to a specific individual from the query
                    if (isSameIndividuals(q_subj_i_exp, q_obj_i_exp))
                        pushTriple(triples, q_subj_name.c_str(), OWL_SAME_AS, q_obj_name.c_str());
                }
            }

            // Gather object role values
            for (const TNamedEntry* named_entry : roles)
            {
                const TRole* role = static_cast<const TRole*>(named_entry);

                const TORoleExpr* role_exp = getExpressionManager()->ObjectRole(role->getName());

                if (q_obj_name.empty())
                {
                    // All values
                    for (const TIndividual* obj : getRoleFillers(q_subj_i_exp, role_exp))
                        pushTriple(triples, q_subj->getName(), role->getName(), obj->getName());
                }
                else if (q_obj != nullptr && q_obj->isSingleton())
                {
                    // A specific value from the query
                    if (isRelated(q_subj_i_exp, role_exp, q_obj_i_exp))
                        pushTriple(triples, q_subj->getName(), role->getName(), q_obj->getName());
                }
            }

            // Gather data role values
            if (q_role_name.empty())
            {
                // All data roles
                const TDataValueMap* dataValueMap = q_subj_ind->getDataValueMap();

                for (const auto& pair : dataValueMap->getAllDataValues())
                {
                    const TRole* role = pair.first;
                    const TDataValueMap::DEVec& values = pair.second;

                    for (const TDataEntry* value : values)
                    {
                        if (q_obj_name.empty() || value->getName() == q_obj_name)
                            pushTriple(triples, q_subj->getName(), role->getName(), value->getName());
                    }
                }
            }
            else if (q_subj_ind->hasDataValueCache(q_role))
            {
                // A specific data role from the query
                for (const TDataEntry* value : q_subj_ind->getDataValueCache(q_role))
                {
                    if (q_obj_name.empty() || value->getName() == q_obj_name)
                        pushTriple(triples, q_subj->getName(), q_role->getName(), value->getName());
                }
            }
        }
        else
        {
            // Query subject is a class

            if ((q_role_name.empty() || q_role_name == RDF_TYPE) && (q_obj_name.empty() || q_obj_name == OWL_CLASS))
                pushTriple(triples, q_subj_name.c_str(), RDF_TYPE, OWL_CLASS);

            // If superclasses are needed
            if (!q_subj->isBottom() && (q_role_name.empty() || q_role_name == RDFS_SUBCLASS_OF))
            {
                if (q_obj_name.empty())
                {
                    // All superclasses
                    TripleGatherer gatherer(&triples, true, RDFS_SUBCLASS_OF, q_subj_name.c_str(), [q_subj](const ClassifiableEntry* entry)
                    {
                        return entry != q_subj && !static_cast<const TConcept*>(entry)->isSingleton();
                    });

                    getSupConcepts(q_subj_c_exp, false, gatherer, true);
                }
                else if (q_obj != nullptr && !q_obj->isSingleton())
                {
                    // A specific superclass from the query
                    if (q_obj != q_subj && isSubsumedBy(q_subj_c_exp, q_obj_c_exp))
                        pushTriple(triples, q_subj_name.c_str(), RDFS_SUBCLASS_OF, q_obj_name.c_str());
                }
            }

            // If synonyms are needed
            if (q_subj != nullptr && q_role_name.empty() || q_role_name == OWL_EQUIVALENT_CLASS)
            {
                if (q_obj_name.empty())
                {
                    // All synonyms
                    TripleGatherer gatherer(&triples, true, OWL_EQUIVALENT_CLASS, q_subj_name.c_str(), [q_subj](const ClassifiableEntry* entry)
                    {
                        return entry != q_subj && !static_cast<const TConcept*>(entry)->isSingleton();
                    });

                    getEquivalentConcepts(q_subj_c_exp, gatherer);
                }
                else if (q_obj != nullptr && !q_obj->isSingleton() && q_obj != q_subj)
                {
                    // A synonyms to a specific class from the query
                    if (isEquivalent(q_subj_c_exp, q_obj_c_exp))
                        pushTriple(triples, q_subj_name.c_str(), OWL_EQUIVALENT_CLASS, q_obj_name.c_str());
                }
            }

            // If disjoints are needed
            /*if (q_subj != nullptr && q_role_name.empty() || q_role_name == OWL_DISJOINT_WITH)
            {
                if (q_obj_name.empty())
                {
                    // All disjoint classes
                    TripleGatherer gatherer(&triples, true, OWL_DISJOINT_WITH, q_subj_name.c_str(), [](const ClassifiableEntry* entry)
                    {
                        const TConcept* concept = static_cast<const TConcept*>(entry);
                        return !concept->isSingleton() && !concept->isSystem() && !concept->isBottom();
                    });

                    getDisjointConcepts(q_subj_c_exp, gatherer);
                }
                else if (q_obj != nullptr && !q_obj->isSingleton())
                {
                    // A specific disjoint class from the query
                    if (isDisjoint(q_subj_c_exp, q_obj_c_exp))
                        pushTriple(triples, q_subj_name.c_str(), OWL_DISJOINT_WITH, q_obj_name.c_str());
                }
            }*/
        }
    }
    else if (q_subj_role != nullptr)
    {
        // Subject is specified in the query and it is a role

        if (q_subj_role->isDataRole())
        {
            // Data role properties

            if (q_role_name.empty() || q_role_name == RDF_TYPE)
            {
                if (q_obj_name.empty() || q_obj_name == OWL_DATATYPE_PROPERTY)
                    pushTriple(triples, q_subj_name.c_str(), RDF_TYPE, OWL_DATATYPE_PROPERTY);

                if (isFunctional(q_subj_d_role_exp) && (q_obj_name.empty() || q_obj_name == OWL_FUNCTIONAL_PROPERTY))
                    pushTriple(triples, q_subj_name.c_str(), RDF_TYPE, OWL_FUNCTIONAL_PROPERTY);
            }

            if (q_role_name.empty() || q_role_name == RDFS_DOMAIN)
            {
                TripleGatherer gatherer(&triples, true, RDFS_DOMAIN, q_subj_name.c_str(), [q_obj_name, q_obj](const ClassifiableEntry* entry)
                {
                    return !entry->isSystem() && (q_obj_name.empty() || entry == q_obj);
                });

                getDRoleDomain(q_subj_d_role_exp, true, gatherer);
            }

            if (q_role_name.empty() || q_role_name == RDFS_SUBPROPERTY_OF)
            {
                if (q_obj_role != nullptr)
                {
                    if (q_obj_role->isDataRole() && isSubRoles(q_subj_d_role_exp, q_obj_d_role_exp))
                        pushTriple(triples, q_subj_name.c_str(), RDFS_SUBPROPERTY_OF, q_obj_name.c_str());
                }
                else if (q_obj_name.empty())
                {
                    TripleGatherer gatherer(&triples, true, RDFS_SUBPROPERTY_OF, q_subj_name.c_str(), [q_subj_role](const ClassifiableEntry* entry)
                    {
                        return entry != q_subj_role;
                    });

                    getSupRoles(q_subj_d_role_exp, false, gatherer);
                }
            }

            if (q_role_name.empty() || q_role_name == OWL_EQUIVALENT_PROPERTY)
            {
                TripleGatherer gatherer(&triples, true, OWL_EQUIVALENT_PROPERTY, q_subj_name.c_str(), [q_subj_role, q_obj_name, q_obj_role](const ClassifiableEntry* entry)
                {
                    return entry != q_subj_role && (q_obj_name.empty() || entry == q_obj_role);
                });

                getEquivalentRoles(q_subj_d_role_exp, gatherer);
            }
        }
        else
        {
            // Object role properties

            if (q_role_name.empty() || q_role_name == RDF_TYPE)
            {
                if (q_obj_name.empty() || q_obj_name == OWL_OBJECT_PROPERTY)
                    pushTriple(triples, q_subj_name.c_str(), RDF_TYPE, OWL_OBJECT_PROPERTY);

                if (isFunctional(q_subj_o_role_exp) && (q_obj_name.empty() || q_obj_name == OWL_FUNCTIONAL_PROPERTY))
                    pushTriple(triples, q_subj_name.c_str(), RDF_TYPE, OWL_FUNCTIONAL_PROPERTY);

                if (isInverseFunctional(q_subj_o_role_exp) && (q_obj_name.empty() || q_obj_name == OWL_INVERSE_FUNCTIONAL_PROPERTY))
                    pushTriple(triples, q_subj_name.c_str(), RDF_TYPE, OWL_INVERSE_FUNCTIONAL_PROPERTY);

                if (isSymmetric(q_subj_o_role_exp) && (q_obj_name.empty() || q_obj_name == OWL_SYMMETRIC_PROPERTY))
                    pushTriple(triples, q_subj_name.c_str(), RDF_TYPE, OWL_SYMMETRIC_PROPERTY);

                if (isAsymmetric(q_subj_o_role_exp) && (q_obj_name.empty() || q_obj_name == OWL_ASYMMETRIC_PROPERTY))
                    pushTriple(triples, q_subj_name.c_str(), RDF_TYPE, OWL_ASYMMETRIC_PROPERTY);

                if (isTransitive(q_subj_o_role_exp) && (q_obj_name.empty() || q_obj_name == OWL_TRANSITIVE_PROPERTY))
                    pushTriple(triples, q_subj_name.c_str(), RDF_TYPE, OWL_TRANSITIVE_PROPERTY);

                if (isReflexive(q_subj_o_role_exp) && (q_obj_name.empty() || q_obj_name == OWL_REFLEXIVE_PROPERTY))
                    pushTriple(triples, q_subj_name.c_str(), RDF_TYPE, OWL_REFLEXIVE_PROPERTY);

                if (isIrreflexive(q_subj_o_role_exp) && (q_obj_name.empty() || q_obj_name == OWL_IRREFLEXIVE_PROPERTY))
                    pushTriple(triples, q_subj_name.c_str(), RDF_TYPE, OWL_IRREFLEXIVE_PROPERTY);
            }

            if (q_role_name.empty() || q_role_name == RDFS_DOMAIN)
            {
                TripleGatherer gatherer(&triples, true, RDFS_DOMAIN, q_subj_name.c_str(), [q_subj, q_obj_name, q_obj](const ClassifiableEntry* entry)
                {
                    return !entry->isSystem() && (q_obj_name.empty() || entry == q_obj);
                });

                getORoleDomain(q_subj_o_role_exp, true, gatherer);
            }
            
            if (q_role_name.empty() || q_role_name == RDFS_RANGE)
            {
                TripleGatherer gatherer(&triples, true, RDFS_RANGE, q_subj_name.c_str(), [q_subj, q_obj_name, q_obj](const ClassifiableEntry* entry)
                {
                    return !entry->isSystem() && (q_obj_name.empty() || entry == q_obj);
                });

                getRoleRange(q_subj_o_role_exp, true, gatherer);
            }

            if (q_role_name.empty() || q_role_name == RDFS_SUBPROPERTY_OF)
            {
                if (q_obj_role != nullptr)
                {
                    if (!q_obj_role->isDataRole() && isSubRoles(q_subj_o_role_exp, q_obj_o_role_exp))
                        pushTriple(triples, q_subj_name.c_str(), RDFS_SUBPROPERTY_OF, q_obj_name.c_str());
                }
                else if (q_obj_name.empty())
                {
                    TripleGatherer gatherer(&triples, true, RDFS_SUBPROPERTY_OF, q_subj_name.c_str(), [q_subj_role](const ClassifiableEntry* entry)
                    {
                        return entry != q_subj_role && static_cast<const TNamedEntry*>(entry)->getId() >= 0;
                    });

                    getSupRoles(q_subj_o_role_exp, false, gatherer);
                }
            }

            if (q_role_name.empty() || q_role_name == OWL_EQUIVALENT_PROPERTY)
            {
                TripleGatherer gatherer(&triples, true, OWL_EQUIVALENT_PROPERTY, q_subj_name.c_str(), [q_subj_role, q_obj_name, q_obj_role](const ClassifiableEntry* entry)
                {
                    return entry != q_subj_role && static_cast<const TNamedEntry*>(entry)->getId() >= 0 && (q_obj_name.empty() || entry == q_obj_role);
                });

                getEquivalentRoles(q_subj_o_role_exp, gatherer);
            }

            if (q_role_name.empty() || q_role_name == OWL_INVERSE_OF)
            {
                const auto* inverse_role = q_subj_role->inverse();

                if (inverse_role != nullptr && inverse_role->getId() >= 0 && (q_obj_name.empty() || q_obj_role == inverse_role))
                    pushTriple(triples, q_subj_name.c_str(), OWL_INVERSE_OF, toRdf(inverse_role->getName()));
            }

        }
    }
}

//----------------------------------------------------------------------------------
// atomic decomposition queries
//----------------------------------------------------------------------------------

	/// create new atomic decomposition of the loaded ontology using TYPE. @return size of the AD
size_t
ReasoningKernel :: getAtomicDecompositionSize ( ModuleMethod moduleMethod, ModuleType moduleType )
{
	// init AD field
	if ( unlikely(AD != nullptr) )
		delete AD;

	AD = new AtomicDecomposer(getModExtractor(moduleMethod)->getModularizer());
	return AD->getAOS ( &Ontology, moduleType )->size();
}
	/// get a set of axioms that corresponds to the atom with the id INDEX
const TOntologyAtom::AxiomSet&
ReasoningKernel :: getAtomAxioms ( unsigned int index ) const
{
	return (*AD->getAOS())[index]->getAtomAxioms();
}
	/// get a set of axioms that corresponds to the module of the atom with the id INDEX
const TOntologyAtom::AxiomSet&
ReasoningKernel :: getAtomModule ( unsigned int index ) const
{
	return (*AD->getAOS())[index]->getModule();
}
	/// get a set of atoms on which atom with index INDEX depends
const TOntologyAtom::AtomSet&
ReasoningKernel :: getAtomDependents ( unsigned int index ) const
{
	return (*AD->getAOS())[index]->getDepAtoms();
}

	/// get a number of locality checks performed for creating an AD
unsigned long long
ReasoningKernel :: getLocCheckNumber ( void ) const
{
	return AD->getLocCheckNumber();
}

OntologyBasedModularizer*
ReasoningKernel :: getModExtractor ( ModuleMethod moduleMethod )
{
	// check whether we need init
	OntologyBasedModularizer*& pMod = getModPointer(moduleMethod);
	if ( unlikely(pMod == nullptr) )
		pMod = new OntologyBasedModularizer ( getOntology(), moduleMethod );
	return pMod;
}

/// get a set of axioms that corresponds to the atom with the id INDEX
const AxiomVec&
ReasoningKernel :: getModule ( ModuleMethod moduleMethod, ModuleType moduleType )
{
	// init signature
	TSignature Sig;
	// NB: we don't care about locality type here as modularizer will change it
	Sig.setLocality(false);
	for ( const TDLExpression* expr : getExpressionManager()->getArgList() )
		if ( const TNamedEntity* entity = dynamic_cast<const TNamedEntity*>(expr) )
			Sig.add(entity);
	return getModExtractor(moduleMethod)->getModule ( Sig, moduleType );
}

/// get a set of axioms that corresponds to the atom with the id INDEX
const AxiomVec&
ReasoningKernel :: getNonLocal ( ModuleMethod moduleMethod, ModuleType moduleType )
{
	// init signature
	TSignature Sig;
	Sig.setLocality(moduleType == M_TOP);	// true for TOP, false for BOT/STAR
	for ( const TDLExpression* expr : getExpressionManager()->getArgList() )
		if ( const TNamedEntity* entity = dynamic_cast<const TNamedEntity*>(expr) )
			Sig.add(entity);

//	TLISPOntologyPrinter lp(std::cout);
	// do check
	LocalityChecker* LC = getModExtractor(moduleMethod)->getModularizer()->getLocalityChecker();
	LC->setSignatureValue(Sig);
	Result.clear();
	for ( TDLAxiom* axiom : getOntology() )
	{
//		std::cout << "Checking locality of ";
//		axiom->accept(lp);
		if ( !LC->local(axiom) )
		{
//			std::cout << "AX non-local" << std::endl;
			Result.push_back(axiom);
		}
//		else
//			std::cout << "AX local" << std::endl;
	}
	return Result;
}

//----------------------------------------------------------------------------------
// save/load interface
//----------------------------------------------------------------------------------

/// check whether  @return true if a file with reasoner state with a given NAME exists.
bool
ReasoningKernel :: checkSaveLoadContext ( const std::string& name ) const
{
	return SaveLoadManager(name).existsContent();
}

/// set a save/load file to a given NAME
bool
ReasoningKernel :: setSaveLoadContext ( const std::string& name )
{
	delete pSLManager;
	pSLManager = new SaveLoadManager(name);
	return pSLManager->existsContent();
}

/// clear a cache for a given name
bool
ReasoningKernel :: clearSaveLoadContext ( const std::string& name ) const
{
	if ( checkSaveLoadContext(name) )
	{
		SaveLoadManager(name).clearContent();
		return true;
	}
	return false;
}

//******************************************
//* Initialization
//******************************************

bool ReasoningKernel :: initOptions ( void )
{
	// register all possible options used in FaCT++ Kernel

	// options for TBox

	// register "dumpQuery" option -- 11-08-04
	if ( KernelOptions.RegisterOption (
		"dumpQuery",
		"Option 'dumpQuery' dumps sub-TBox relevant to given satisfiability/subsumption query.",
		ifOption::iotBool,
		"false"
		) )
		return true;

	// register "absorptionFlags" option (04/05/2005)
	if ( KernelOptions.RegisterOption (
		"absorptionFlags",
		"Option 'absorptionFlags' sets up absorption process for general axioms. "
		"It text field of arbitrary length; every symbol means the absorption action: "
		"(B)ottom Absorption), (T)op absorption, (E)quivalent concepts replacement, (C)oncept absorption, "
		"(N)egated concept absorption, (F)orall expression replacement, Simple (f)orall expression replacement, "
		"(R)ole absorption, (S)plit",
		ifOption::iotText,
		"BTEfCFSR"
		) )
		return true;

	// register "alwaysPreferEquals" option (26/01/2006)
	if ( KernelOptions.RegisterOption (
		"alwaysPreferEquals",
		"Option 'alwaysPreferEquals' allows user to enforce usage of C=D definition instead of C[=D "
		"during absorption, even if implication appeared earlier in stream of axioms.",
		ifOption::iotBool,
		"true"
		) )
		return true;

	// register "useSpecialDomains" option (25/10/2011)
	if ( KernelOptions.RegisterOption (
		"useSpecialDomains",
		"Option 'useSpecialDomains' (development) controls the special processing of R&D for non-simple roles. "
		"Should always be set to true.",
		ifOption::iotBool,
		"true"
		) )
		return true;

	// options for DLDag

	// register "orSortSub" option (20/12/2004)
	if ( KernelOptions.RegisterOption (
		"orSortSub",
		"Option 'orSortSub' define the sorting order of OR vertices in the DAG used in subsumption tests. "
		"Option has form of string 'Mop', where 'M' is a sort field (could be 'D' for depth, 'S' for size, 'F' "
		"for frequency, and '0' for no sorting), 'o' is a order field (could be 'a' for ascending and 'd' "
		"for descending mode), and 'p' is a preference field (could be 'p' for preferring non-generating "
		"rules and 'n' for not doing so).",
		ifOption::iotText,
		"0"
		) )
		return true;

	// register "orSortSat" option (20/12/2004)
	if ( KernelOptions.RegisterOption (
		"orSortSat",
		"Option 'orSortSat' define the sorting order of OR vertices in the DAG used in satisfiability tests "
		"(used mostly in caching). Option has form of string 'Mop', see orSortSub for details.",
		ifOption::iotText,
		"0"
		) )
		return true;

	// options for ToDoTable

	// register "IAOEFLG" option
	if ( KernelOptions.RegisterOption (
		"IAOEFLG",
		"Option 'IAOEFLG' define the priorities of different operations in TODO list. Possible values are "
		"7-digit strings with ony possible digit are 0-6. The digits on the places 1, 2, ..., 7 are for "
		"priority of Id, And, Or, Exists, Forall, LE and GE operations respectively. The smaller number means "
		"the higher priority. All other constructions (TOP, BOTTOM, etc) has priority 0.",
		ifOption::iotText,
		"1263005"
		) )
		return true;

	// options for Reasoner

	// register "useSemanticBranching" option
	if ( KernelOptions.RegisterOption (
		"useSemanticBranching",
		"Option 'useSemanticBranching' switch semantic branching on and off. The usage of semantic branching "
		"usually leads to faster reasoning, but sometime could give small overhead.",
		ifOption::iotBool,
		"true"
		) )
		return true;

	// register "useBackjumping" option
	if ( KernelOptions.RegisterOption (
		"useBackjumping",
		"Option 'useBackjumping' switch backjumping on and off. The usage of backjumping "
		"usually leads to much faster reasoning.",
		ifOption::iotBool,
		"true"
		) )
		return true;

	// register "testTimeout" option -- 21/08/09
	if ( KernelOptions.RegisterOption (
		"testTimeout",
		"Option 'testTimeout' sets timeout for a single reasoning test in milliseconds.",
		ifOption::iotInt,
		"0"
		) )
		return true;

	// options for Blocking

	// register "useLazyBlocking" option -- 08-03-04
	if ( KernelOptions.RegisterOption (
		"useLazyBlocking",
		"Option 'useLazyBlocking' makes checking of blocking status as small as possible. This greatly "
		"increase speed of reasoning.",
		ifOption::iotBool,
		"true"
		) )
		return true;

	// register "useAnywhereBlocking" option (18/08/2008)
	if ( KernelOptions.RegisterOption (
		"useAnywhereBlocking",
		"Option 'useAnywhereBlocking' allow user to choose between Anywhere and Ancestor blocking.",
		ifOption::iotBool,
		"true"
		) )
		return true;

	// register "skipBeforeBlock" option (28/02/2009)
	if ( KernelOptions.RegisterOption (
		"skipBeforeBlock",
		"Internal use only. Option 'skipBeforeBlock' allow user to skip given number of nodes before make a block.",
		ifOption::iotInt,
		"0"
		) )
		return true;

	// options for Taxonomy

	// register "useCompletelyDefined" option
	if ( KernelOptions.RegisterOption (
		"useCompletelyDefined",
		"Option 'useCompletelyDefined' leads to simpler Taxonomy creation if TBox contains no non-primitive "
		"concepts. Unfortunately, it is quite rare case.",
		ifOption::iotBool,
		"true"
		) )
		return true;

	// options for kernel

	// register "checkAD" option (24/02/2012)
	if ( KernelOptions.RegisterOption (
		"checkAD",
		"Option 'checkAD' forces FaCT++ to create the AD and exit instead of performing classification",
		ifOption::iotBool,
		"false"
		) )
		return true;

	// register "dumpOntology" option (28/03/2013)
	if ( KernelOptions.RegisterOption (
		"dumpOntology",
		"Option 'dumpOntology' dumps the ontology loaded into the reasoner in a LISP-like format",
		ifOption::iotBool,
		"false"
		) )
		return true;

	// register "useIncrementalReasoning" option (21/06/2013)
	if ( KernelOptions.RegisterOption (
		"useIncrementalReasoning",
		"Option 'useIncrementalReasoning' (development) allows one to reason efficiently about small changes in the ontology.",
		ifOption::iotBool,
		"false"
		) )
		return true;

	// register "allowUndefinedNames" option (03/11/2013)
	if ( KernelOptions.RegisterOption (
		"allowUndefinedNames",
		"Option 'allowUndefinedNames' describes the policy of undefined names.",
		ifOption::iotBool,
		"true"
		) )
		return true;

    // register "precacheRelated" option -- 16/11/2021
    if (KernelOptions.RegisterOption(
        "precacheRelated",
        "Option 'precacheRelated' allows filling cache of individual relations during the consistency check, "
        "which allows for much faster (but sometimes incomplete) queries.",
        ifOption::iotBool,
        "true"
    ))
        return true;

    // all was registered OK
	return false;
}

