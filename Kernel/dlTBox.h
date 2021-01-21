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

#ifndef DLTBOX_H
#define DLTBOX_H

#include <string>
#include <vector>
#include <set>
#include <map>

#include "tConcept.h"
#include "tIndividual.h"
#include "modelCacheSingleton.h"
#include "RoleMaster.h"
#include "LogicFeature.h"
#include "dlDag.h"
#include "ifOptions.h"
#include "PriorityMatrix.h"
#include "tRelated.h"
#include "tNECollection.h"
#include "tAxiomSet.h"
#include "DataTypeCenter.h"
#include "tProgressMonitor.h"
#include "tKBFlags.h"

class DlSatTester;
class Taxonomy;
class DLConceptTaxonomy;
class dumpInterface;
class TSignature;
class SaveLoadManager;

/// enumeration for the reasoner status
enum KBStatus
{
	kbEmpty,		// no axioms loaded yet; not used in TBox
	kbLoading,		// axioms are added to the KB, no preprocessing done
	kbCChecked,		// KB is preprocessed and consistency checked
	kbClassified,	// KB is classified
	kbRealised,		// KB is realised
};

class TBox
{
	friend class DlSatTester;
	friend class NominalReasoner;
	friend class ReasoningKernel;
	friend class TAxiom;	// FIXME!! while TConcept can't get rid of told cycles
	friend class DLConceptTaxonomy;

public:		// type interface
		/// vector of CONCEPT-like elements
	typedef std::vector<TConcept*> ConceptVector;
		/// vector of SINGLETON-like elements
	typedef std::vector<TIndividual*> SingletonVector;
		/// map between names and corresponding module signatures
	typedef std::map<const TNamedEntity*, const TSignature*> NameSigMap;

protected:	// types
		/// type for DISJOINT-like statements
	typedef std::vector<DLTree*> ExpressionArray;
		/// set of concepts together with creation methods
	typedef TNECollection<TConcept> ConceptCollection;
		/// collection of individuals
	typedef TNECollection<TIndividual> IndividualCollection;
		/// type for the array of Related elements
	typedef std::vector<TRelated*> RelatedCollection;
		/// type for a collection of DIFFERENT individuals
	typedef std::vector<SingletonVector> DifferentIndividuals;
		/// type for a A -> C map
	typedef std::map<TConcept*, DLTree*> ConceptDefMap;
		/// type for a set of concepts
	typedef std::set<TConcept*> ConceptSet;

		/// class for simple rules like Ch :- Cb1, Cbi, CbN; all C are primitive named concepts
	class TSimpleRule
	{
	public:		// type interface
			/// type for the rule body
		typedef TBox::ConceptVector TRuleBody;
			/// RW iterator over body
		typedef TRuleBody::iterator iterator;
			/// RO iterator over body
		typedef TRuleBody::const_iterator const_iterator;

	public:		// members
			/// body of the rule
		TRuleBody Body;
			/// head of the rule as a DLTree
		DLTree* tHead;
			/// head of the rule as a BP
		BipolarPointer bpHead = bpINVALID;

	public:		// interface
			/// init c'tor
		TSimpleRule ( const TRuleBody& body, DLTree* head )
			: Body(body)
			, tHead(head)
			{}
			/// no copy c'tor
		TSimpleRule ( const TSimpleRule& ) = delete;
			/// no assignment
		TSimpleRule& operator= ( const TSimpleRule& ) = delete;
			/// empty d'tor
		virtual ~TSimpleRule() { deleteTree(tHead); }
	}; // TSimpleRule
		/// all simple rules in KB
	typedef std::vector<TSimpleRule*> TSimpleRules;
		/// R-C cache for the \forall R.C replacement in GCIs
	typedef std::vector<std::pair<DLTree*,TConcept*> > TRCCache;

protected:	// typedefs
		/// RW concept iterator
	typedef ConceptCollection::iterator c_iterator;
		/// RW individual iterator
	typedef IndividualCollection::iterator i_iterator;
		/// RO ExpressionArray iterator
	typedef ExpressionArray::const_iterator ea_iterator;

public:		// interface
		/// RO concept iterator
	typedef ConceptCollection::const_iterator c_const_iterator;
		/// RO individual iterator
	typedef IndividualCollection::const_iterator i_const_iterator;

protected:	// members
		/// relevance label (to determine logical features)
	TLabeller relevance;
		/// DAG of all expressions
	DLDag DLHeap;

		/// reasoner for TBox-related queries w/o nominals
	DlSatTester* stdReasoner = nullptr;
		/// reasoner for TBox-related queries with nominals
	DlSatTester* nomReasoner = nullptr;

		/// progress monitor
	TProgressMonitor* pMonitor = nullptr;

		/// vectors for Completely defined, Non-CD and Non-primitive concepts
	ConceptVector arrayCD, arrayNoCD, arrayNP;
		/// taxonomy structure of a TBox
	Taxonomy* pTax = nullptr;
		/// classifier
	DLConceptTaxonomy* pTaxCreator = nullptr;
		/// name-signature map
	NameSigMap* pName2Sig = nullptr;
		/// DataType center
	DataTypeCenter DTCenter;
		/// set of reasoning options
	const ifOptionSet* pOptions;
		/// status of the KB
	KBStatus Status = kbLoading;

		/// global KB features
	LogicFeatures KBFeatures;
		/// GCI features
	LogicFeatures GCIFeatures;
		/// nominal cloud features
	LogicFeatures NCFeatures;
		/// aux features
	LogicFeatures auxFeatures;
		/// pointer to current feature (in case of local ones)
	LogicFeatures* curFeature = nullptr;

	// auxiliary concepts for Taxonomy

		/// concept representing Top
	TConcept* pTop;
		/// concept representing Bottom
	TConcept* pBottom;
		/// concept representing temporary one that can not be used anywhere in the ontology
	TConcept* pTemp;
		/// temporary concept that represents query
	TConcept* pQuery = nullptr;

		/// all named concepts
	ConceptCollection Concepts;
		/// all named individuals/nominals
	IndividualCollection Individuals;
		/// "normal" (object) roles
	RoleMaster ORM;
		/// data roles
	RoleMaster DRM;
		/// set of GCIs
	TAxiomSet Axioms;
		/// given individual-individual relations
	RelatedCollection RelatedI;
		/// known disjoint sets of individuals
	DifferentIndividuals Different;
		/// all simple rules in KB
	TSimpleRules SimpleRules;
		/// map to show the possible equivalence between individuals
	std::map<TConcept*, std::pair<TIndividual*,bool> > SameI;
		/// map from A->C for axioms A [= C where A = D is in the TBox
	ConceptDefMap ExtraConceptDefs;

		/// internalisation of a general axioms
	BipolarPointer T_G = bpTOP;
		/// KB flags about GCIs
	TKBFlags GCIs;

		/// cache for the \forall R.C replacements during absorption
	TRCCache RCCache;

		/// maps from concept index to concept itself
	ConceptVector ConceptMap;

		/// number of concepts and individuals; used to set index for modelCache
	unsigned int nC = 0;
		/// number of all distinct roles; used to set index for modelCache
	unsigned int nR = 0;
		/// current aux concept's ID
	unsigned int auxConceptID = 0;
		/// how many times nominals were found during translation to DAG; local to BuildDAG
	unsigned int nNominalReferences = 0;
		/// number of relevant calls to named concepts; local to relevance
	unsigned long nRelevantCCalls = 0;
		/// number of relevant calls to concept expressions; local to relevance
	unsigned long nRelevantBCalls = 0;

		/// searchable stack for the told subsumers
	std::set<TConcept*> CInProcess;
		/// all the synonyms in the told subsumers' cycle
	std::vector<TConcept*> ToldSynonyms;

		/// fairness constraints
	ConceptVector Fairness;

		/// priority matrix for To-Do lists
	ToDoPriorMatrix PriorityMatrix;
		/// single SAT/SUB test timeout in milliseconds
	unsigned long testTimeout = 0;

	//---------------------------------------------------------------------------
	// Reasoner's members: there are many reasoner classes, some members are shared
	//---------------------------------------------------------------------------

		/// flag for switching semantic branching
	bool useSemanticBranching = true;
		/// flag for switching backjumping
	bool useBackjumping = true;
		/// whether or not check blocking status as late as possible
	bool useLazyBlocking = true;
		/// flag for switching between Anywhere and Ancestor blocking
	bool useAnywhereBlocking = true;
		/// flag to use caching during completion tree construction
	bool useNodeCache = true;
		/// how many nodes skip before block; work only with FAIRNESS
	int nSkipBeforeBlock = 0;

	//---------------------------------------------------------------------------
	// User-defined flags
	//---------------------------------------------------------------------------

		/// flag for creating taxonomy
	bool useCompletelyDefined = true;
		/// flag for dumping TBox relevant to query
	bool dumpQuery = false;
		/// whether or not we need classification. Set up in checkQueryNames()
	bool needClassification = true;
		/// shall we prefer C=D axioms to C[=E in definition of concepts
	bool alwaysPreferEquals = true;
		/// use special domains as GCIs
	bool useSpecialDomains = false;
		/// shall verbose output be used
	bool verboseOutput = false;

	//---------------------------------------------------------------------------
	// Internally defined flags
	//---------------------------------------------------------------------------

		/// whether we use sorted reasoning; depends on some simplifications
	bool useSortedReasoning = true;
		/// flag whether TBox is GALEN-like
	bool isLikeGALEN = false;
		/// flag whether TBox is WINE-like
	bool isLikeWINE = false;

		/// whether KB is consistent
	bool Consistent = true;

		/// time spend for preprocessing
	float preprocTime = 0.0;
		/// time spend for consistency checking
	float consistTime = 0.0;

protected:	// methods
		/// init all flags using given set of options
	void readConfig ( const ifOptionSet* Options );
		/// initialise Top and Bottom internal concepts
	void initTopBottom ( void );


//-----------------------------------------------------------------------------
//--		internal iterators
//-----------------------------------------------------------------------------

		/// RW begin() for concepts
	c_iterator c_begin ( void ) { return Concepts.begin(); }
		/// RW end() for concepts
	c_iterator c_end ( void ) { return Concepts.end(); }

		/// RW begin() for individuals
	i_iterator i_begin ( void ) { return Individuals.begin(); }
		/// RW end() for individuals
	i_iterator i_end ( void ) { return Individuals.end(); }

//-----------------------------------------------------------------------------
//--		internal ensure*-like interface
//-----------------------------------------------------------------------------

		/// @return concept by given Named Entry ID
	static TConcept* toConcept ( TNamedEntry* id ) { return static_cast<TConcept*>(id); }
		/// @return concept by given Named Entry ID
	static const TConcept* toConcept ( const TNamedEntry* id ) { return static_cast<const TConcept*>(id); }
		/// @return individual by given Named Entry ID
	static TIndividual* toIndividual ( TNamedEntry* id ) { return static_cast<TIndividual*>(id); }
		/// @return individual by given Named Entry ID
	static const TIndividual* toIndividual ( const TNamedEntry* id ) { return static_cast<const TIndividual*>(id); }

//-----------------------------------------------------------------------------
//--		internal BP-to-concept interface
//-----------------------------------------------------------------------------

		/// set P as a concept corresponding BP
	void setBPforConcept ( BipolarPointer bp, TConcept* p )
	{
		DLHeap[bp].setConcept(p);
		p->pName = bp;
	}

		/// get concept by it's BP (non-const version)
	TDataEntry* getDataEntryByBP ( BipolarPointer bp )
	{
		TDataEntry* p = static_cast<TDataEntry*>(DLHeap[bp].getConcept());
		fpp_assert ( p != nullptr );
		return p;
	}
		/// get concept by it's BP (const version)
	const TDataEntry* getDataEntryByBP ( BipolarPointer bp ) const
	{
		const TDataEntry* p = static_cast<const TDataEntry*>(DLHeap[bp].getConcept());
		fpp_assert ( p != nullptr );
		return p;
	}


//-----------------------------------------------------------------------------
//--		internal concept building interface
//-----------------------------------------------------------------------------

		/// checks if C is defined as C=D and set Synonyms accordingly
	void checkEarlySynonym ( TConcept* C )
	{
		if ( C->isSynonym() )
			return;	// nothing to do
		if ( C->isPrimitive() )
			return;	// couldn't be a synonym
		if ( !isCN(C->Description) )
			return;	// complex expression -- not a synonym(imm.)

		C->setSynonym(getCI(C->Description));
		C->initToldSubsumers();
	}
		/// make concept C non-primitive with definition DESC; @return it's old description
	DLTree* makeNonPrimitive ( TConcept* C, DLTree* desc )
	{
		DLTree* ret = C->makeNonPrimitive(desc);
		checkEarlySynonym(C);
		return ret;
	}

//-----------------------------------------------------------------------------
//--		support for n-ary predicates
//-----------------------------------------------------------------------------

		/// build a construction in the form AND (\neg q_i)
	template <typename Iterator>
	DLTree* buildDisjAux ( Iterator beg, Iterator end )
	{
		DLTree* t = createTop();
		for ( Iterator i = beg; i < end; ++i )
			t = createSNFAnd ( t, createSNFNot(clone(*i)) );
		return t;
	}
		/// process a disjoint set [beg,end) in a usual manner
	template <typename Iterator>
	void processDisjoint ( Iterator beg, Iterator end )
	{
		for ( Iterator i = beg; i < end; ++i )
			addSubsumeAxiom ( *i, buildDisjAux ( i+1, end ) );
	}
//-----------------------------------------------------------------------------
//--		internal DAG building methods
//-----------------------------------------------------------------------------

		/// build a DAG-structure for concepts and axioms
	void buildDAG ( void );

		/// translate concept P (together with definition) to DAG representation
	void addConceptToHeap ( TConcept* p );
		/// register data-related expression in the DAG; @return it's DAG index
	BipolarPointer addDataExprToHeap ( TDataEntry* p );
		/// builds DAG entry by general concept expression
	BipolarPointer tree2dag ( const DLTree* );
		/// create forall node (together with transitive sub-roles entries)
	BipolarPointer forall2dag ( const TRole* R, BipolarPointer C );
		/// create atmost node (together with NN-rule entries)
	BipolarPointer atmost2dag ( unsigned int n, const TRole* R, BipolarPointer C );
		/// create REFLEXIVE node
	BipolarPointer reflexive2dag ( const TRole* R )
	{
		// input check: only simple roles are allowed in the reflexivity construction
		if ( !R->isSimple() )
			throw EFPPNonSimpleRole(R->getName());
		return inverse ( DLHeap.add ( new DLVertex ( dtIrr, R ) ) );
	}
		/// create node for AND expression T
	BipolarPointer and2dag ( const DLTree* t );
		/// add elements of T to and-like vertex V; @return true if clash occurs
	bool fillANDVertex ( DLVertex* v, const DLTree* t );
		/// create forall node for data role
	BipolarPointer dataForall2dag ( const TRole* R, BipolarPointer C )
		{ return DLHeap.add ( new DLVertex ( dtForall, 0, R, C ) ); }
		/// create atmost node for data role
	BipolarPointer dataAtMost2dag ( unsigned int n, const TRole* R, BipolarPointer C )
		{ return DLHeap.add ( new DLVertex ( dtLE, n, R, C ) ); }
		/// @return a pointer to concept representation
	BipolarPointer concept2dag ( TConcept* p )
	{
		if ( p == nullptr )
			return bpINVALID;
		if ( !isValid(p->pName) )
			addConceptToHeap(p);
		return p->resolveId();
	}

//-----------------------------------------------------------------------------
//--		internal parser (input) interface
//-----------------------------------------------------------------------------

		/// set the flag that forbid usage of undefined names for concepts/roles; @return old value
	bool setForbidUndefinedNames ( bool val )
	{
		ORM.setUndefinedNames(!val);
		DRM.setUndefinedNames(!val);
		Individuals.setLocked(val);
		return Concepts.setLocked(val);
	}
		/// tries to apply axiom C [= CN; @return NULL if applicable or new CN
	DLTree* applyAxiomCToCN ( DLTree* C, DLTree* CN );
		/// tries to apply axiom CN [= C; @return NULL if applicable or new CN
	DLTree* applyAxiomCNToC ( DLTree* CN, DLTree* C );
		/// tries to add C = RHS for the concept C; @return true if OK
	bool addNonprimitiveDefinition ( TConcept* C, DLTree* rhs );
		/// tries to add C = RHS for the concept C [= X; @return true if OK
	bool switchToNonprimitive ( TConcept* C, DLTree* rhs );
		/// transform definition C=D' with C [= E into C [= (D' and E) with D [= C
		/// D is usually D', but see addSubsumeForDefined()
	void makeDefinitionPrimitive ( TConcept* C, DLTree* E, DLTree* D )
	{
		C->setPrimitive();	// now we have C [= D'
		C->addDesc(E);		// here C [= (D' and E)
		C->initToldSubsumers();
		// all we need is to add (old C's desc)D [= C
		addSubsumeAxiom ( D, getTree(C) );
	}

	// for complex Concept operations
		/// try to absorb GCI C[=D; if not possible, just record this GCI
	void processGCI ( DLTree* C, DLTree* D ) { Axioms.addAxiom ( C, D ); }

	// recognize Range/Domain restriction in an axiom and transform it into role R&D.
	// return true if transformation was performed
	bool axiomToRangeDomain ( DLTree* l, DLTree* r );

		/// @return true if BP represents a named entry in a DAG
	bool isNamedConcept ( BipolarPointer bp ) const
	{
		DagTag tag = DLHeap[bp].Type();
		return isCNameTag(tag) || tag == dtDataType || tag == dtDataValue;
	}

		/// get aux concept obtained from C=\AR.~D by forall replacement
	TConcept* getRCCache ( const DLTree* C ) const
	{
		for ( TRCCache::const_iterator p = RCCache.begin(), p_end = RCCache.end(); p < p_end; ++p )
			if ( equalTrees ( C, p->first ) )
				return p->second;
		return nullptr;
	}
		/// add CN as a cache entry for C=\AR.~D>
	void setRCCache ( DLTree* C, TConcept* CN ) { RCCache.push_back(std::make_pair(C,CN)); }

		/// check if TBox contains too many GCIs to switch strategy
	bool isGalenLikeTBox ( void ) const { return isLikeGALEN; }
		/// check if TBox contains too many nominals and GCIs to switch strategy
	bool isWineLikeTBox ( void ) const { return isLikeWINE; }

//-----------------------------------------------------------------------------
//--		internal preprocessing methods
//-----------------------------------------------------------------------------

		/// build a roles taxonomy and a DAG
	void Preprocess ( void );
		/// transform C [= D with C = E into GCIs
	void TransformExtraSubsumptions ( void );
		/// absorb all axioms
	void AbsorbAxioms ( void )
	{
		unsigned int nSynonyms = countSynonyms();
		Axioms.absorb();
		if ( countSynonyms() > nSynonyms )
			replaceAllSynonyms();
		if ( Axioms.wasRoleAbsorptionApplied() )
			initToldSubsumers();
	}

		/// pre-process RELATED axioms: resolve synonyms, mark individuals as related
	void preprocessRelated ( void );
		/// determine all sorts in KB (make job only for SORTED_REASONING)
	void determineSorts ( void );

		/// calculate statistic for DAG and Roles
	void CalculateStatistic ( void );
		/// Remove DLTree* from TConcept after DAG is constructed
	void RemoveExtraDescriptions ( void );

		/// init Range and Domain for all roles of RM; sets hasGCI if R&D was found
	void initRangeDomain ( RoleMaster& RM );

		/// set told TOP concept whether necessary
	void initToldSubsumers ( void )
	{
		for ( c_iterator pc = c_begin(); pc != c_end(); ++pc )
			if ( !(*pc)->isSynonym() )
				(*pc)->initToldSubsumers();
		for ( i_iterator pi = i_begin(); pi != i_end(); ++pi )
			if ( !(*pi)->isSynonym() )
				(*pi)->initToldSubsumers();
	}
		/// set told TOP concept whether necessary
	void setToldTop ( void )
	{
		for ( c_iterator pc = c_begin(); pc != c_end(); ++pc )
			(*pc)->setToldTop(pTop);
		for ( i_iterator pi = i_begin(); pi != i_end(); ++pi )
			(*pi)->setToldTop(pTop);
	}
		/// calculate TS depth for all concepts
	void calculateTSDepth ( void )
	{
		for ( c_iterator pc = c_begin(); pc != c_end(); ++pc )
			(*pc)->calculateTSDepth();
		for ( i_iterator pi = i_begin(); pi != i_end(); ++pi )
			(*pi)->calculateTSDepth();
	}

		/// find referential cycles (like A [= (and B C), B [= A) and transform them to synonyms (B=A, A[=C)
	void transformToldCycles ( void );
		/// check if P appears in referential cycle;
		/// @return concept which creates cycle, NULL if no such concept exists.
	TConcept* checkToldCycle ( TConcept* p );
		/// transform i [= C [= j into i=C=j for i,j nominals
	void transformSingletonHierarchy ( void );
		/// make P and all its non-singleton parents synonyms to its singleton parent; @return that singleton
	TIndividual* transformSingletonWithSP ( TConcept* p );
		/// helper to the previous function
	TIndividual* getSPForConcept ( TConcept* p );

		/// @return true if C is referenced in TREE; use PROCESSED to record explored names
	bool isReferenced ( TConcept* C, DLTree* tree, ConceptSet& processed ) const;
		/// @return true if C is referenced in the definition of concept D; use PROCESSED to record explored names
	bool isReferenced ( TConcept* C, TConcept* D, ConceptSet& processed ) const
	{
		// mark D as processed
		processed.insert(D);
		// check the description of D
		if ( D->Description == nullptr )
			return false;
		if ( isReferenced ( C, D->Description, processed ) )
			return true;
		// we are done for primitive concepts
		if ( D->isPrimitive() )
			return false;
		// check if D has an extra description
		auto p = ExtraConceptDefs.find(D);
		if ( p != ExtraConceptDefs.end() )
			return isReferenced ( C, p->second, processed );
		return false;
	}

		/// @return number of synonyms in the KB
	unsigned int countSynonyms ( void ) const
	{
		unsigned int nSynonyms = 0;
		for ( c_const_iterator pc = c_begin(); pc != c_end(); ++pc )
			if ( (*pc)->isSynonym() )
				++nSynonyms;

		for ( i_const_iterator pi = i_begin(); pi != i_end(); ++pi )
			if ( (*pi)->isSynonym() )
				++nSynonyms;
		return nSynonyms;
	}
		/// replace all synonyms in concept descriptions with their definitions
	void replaceAllSynonyms ( void );

		/// init Extra Rule field in concepts given by a vector V with a given INDEX
	inline void
	initRuleFields ( const ConceptVector& v, size_t index ) const
	{
		for ( ConceptVector::const_iterator q = v.begin(), q_end = v.end(); q < q_end; ++q )
			(*q)->addExtraRule(index);
	}
		/// mark all concepts wrt their classification tag
	void fillsClassificationTag ( void )
	{
		for ( c_const_iterator pc = c_begin(); pc != c_end(); ++pc )
			(*pc)->getClassTag();
		for ( i_const_iterator pi = i_begin(); pi != i_end(); ++pi )
			(*pi)->getClassTag();
	}
		/// set new concept index for given C wrt existing nC
	void setConceptIndex ( TConcept* C )
	{
		C->setIndex(nC);
		ConceptMap.push_back(C);
		++nC;
	}
		/// set index on all classifiable entries
	void setAllIndexes ( void );

//-----------------------------------------------------------------------------
//--		internal reasoner-related interface
//-----------------------------------------------------------------------------

		/// @return true iff reasoners were initialised
	bool reasonersInited ( void ) const { return stdReasoner != nullptr; }
		/// get RW reasoner wrt nominal case
	DlSatTester* getReasoner ( void )
	{
		fpp_assert ( curFeature != nullptr );
		if ( curFeature->hasSingletons() )
			return nomReasoner;
		else
			return stdReasoner;
	}
		/// get RO reasoner wrt nominal case
	const DlSatTester* getReasoner ( void ) const
	{
		fpp_assert ( curFeature != nullptr );
		if ( curFeature->hasSingletons() )
			return nomReasoner;
		else
			return stdReasoner;
	}
		/// check whether KB is consistent; @return true if it is
	bool performConsistencyCheck ( void );	// implemented in Reasoner.h

//-----------------------------------------------------------------------------
//--		internal reasoning interface
//-----------------------------------------------------------------------------

		/// init reasoning service: create reasoner(s)
	void initReasoner ( void );				// implemented in Reasoner.h
		/// init taxonomy and classifier
	void initTaxonomy ( void );				// implemented in DLConceptTaxonomy.h
		/// set NameSigMap
	void setNameSigMap ( NameSigMap* p ) { pName2Sig = p; }
		/// creating taxonomy for given TBox; include individuals if necessary
	void createTaxonomy ( bool needIndividuals );
		/// partition all elements in [begin,end) range wtr their tags
	template <typename Iterator>
	unsigned int fillArrays ( Iterator begin, Iterator end )
	{
		unsigned int n = 0;
		for ( Iterator p = begin; p < end; ++p )
		{
			if ( (*p)->isNonClassifiable() )
				continue;
			++n;
			switch ( (*p)->getClassTag() )
			{
				case cttTrueCompletelyDefined:
					arrayCD.push_back(*p);
					break;
				default:
					arrayNoCD.push_back(*p);
					break;
				case cttNonPrimitive:
				case cttHasNonPrimitiveTS:
					arrayNP.push_back(*p);
					break;
			}
		}

		return n;
	}
		/// classify all concepts from given COLLECTION with given CD value
	void classifyConcepts ( const ConceptVector& collection, bool curCompletelyDefined, const char* type );
		/// classify single concept
	void classifyEntry ( TConcept* entry );

//-----------------------------------------------------------------------------
//--		internal cache-related methods
//-----------------------------------------------------------------------------

		/// init const cache for either bpTOP or bpBOTTOM
	void initConstCache ( BipolarPointer p ) { DLHeap.setCache ( p, createConstCache(p) ); }
		/// init [singleton] cache for given concept and polarity
	void initSingletonCache ( const TConcept* p, bool pos )
		{ DLHeap.setCache ( createBiPointer(p->pName,pos), new modelCacheSingleton(createBiPointer(p->index(),pos)) ); }
		/// create cache for ~C where C is a primitive concept (as it is simple)
	void buildSimpleCache ( void );

//-----------------------------------------------------------------------------
//--		internal output helper methods
//-----------------------------------------------------------------------------

	void PrintDagEntry ( std::ostream& o, BipolarPointer p ) const;
		/// print one concept-like entry
	void PrintConcept ( std::ostream& o, const TConcept* p ) const;
		/// print all registered concepts
	void PrintConcepts ( std::ostream& o ) const
	{
		if ( Concepts.size() == 0 )
			return;
		o << "Concepts (" << Concepts.size() << "):\n";
		for ( c_const_iterator pc = c_begin(); pc != c_end(); ++pc )
			PrintConcept(o,*pc);
	}
		/// print all registered individuals
	void PrintIndividuals ( std::ostream& o ) const
	{
		if ( Individuals.size() == 0 )
			return;
		o << "Individuals (" << Individuals.size() << "):\n";
		for ( i_const_iterator pi = i_begin(); pi != i_end(); ++pi )
			PrintConcept(o,*pi);
	}
	void PrintSimpleRules ( std::ostream& o ) const
	{
		if ( SimpleRules.empty() )
			return;
		o << "Simple rules (" << SimpleRules.size() << "):\n";
		for ( TSimpleRules::const_iterator p = SimpleRules.begin(); p < SimpleRules.end(); ++p )
		{
			ConceptVector::const_iterator q = (*p)->Body.begin(), q_end = (*p)->Body.end();
			o << "(" << (*q)->getName();
			while ( ++q < q_end )
				o << ", " << (*q)->getName();
			o << ") => " << (*p)->tHead << "\n";
		}
	}
	void PrintAxioms ( std::ostream& o ) const
	{
		if ( T_G == bpTOP )
			return;
		o << "Axioms:\nT [=";
		PrintDagEntry ( o, T_G );
	}
		/// print KB features to LL
	void printFeatures ( void ) const;

//-----------------------------------------------------------------------------
//--		 save/load support; implementation in SaveLoad.cpp
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//--		internal relevance helper methods
//-----------------------------------------------------------------------------
		/// is given concept relevant wrt current TBox
	bool isRelevant ( const TConcept* p ) const { return p->isRelevant(relevance); }
		/// set given concept relevant wrt current TBox
	void setRelevant1 ( TConcept* p );
		/// set given concept relevant wrt current TBox if not checked yet
	void setRelevant ( TConcept* p ) { if ( !isRelevant(p) ) setRelevant1(p); }

		/// is given role relevant wrt current TBox
	bool isRelevant ( const TRole* p ) const { return p->isRelevant(relevance); }
		/// set given role relevant wrt current TBox
	void setRelevant1 ( TRole* p );
		/// set given role relevant wrt current TBox if not checked yet
	void setRelevant ( TRole* p )
	{
		if ( ( likely(p->getId() != 0) || p->isTop() ) && !isRelevant(p) )
			setRelevant1(p);
	}

		/// set given DAG entry relevant wrt current TBox
	void setRelevant ( BipolarPointer p );

		/// gather information about logical features of relevant concept
	void collectLogicFeature ( const TConcept* p ) const
	{
		if ( curFeature )
			curFeature->fillConceptData(p);
	}
		/// gather information about logical features of relevant role
	void collectLogicFeature ( const TRole* p ) const
	{
		if ( curFeature )	// update features w.r.t. current concept
			curFeature->fillRoleData ( p, isRelevant(p->inverse()) );
	}
		/// gather information about logical features of relevant DAG entry
	void collectLogicFeature ( const DLVertex& v, bool pos ) const
	{
		if ( curFeature )
			curFeature->fillDAGData ( v, pos );
	}
		/// mark all active GCIs relevant
	void markGCIsRelevant ( void ) { setRelevant(T_G); }

//-----------------------------------------------------------------------------
//--		internal relevance interface
//-----------------------------------------------------------------------------
		/// set all TBox content (namely, concepts and GCIs) relevant
	void markAllRelevant ( void )
	{
		for ( c_iterator pc = c_begin(); pc != c_end(); ++pc )
			setRelevant(*pc);
		for ( i_iterator pi = i_begin(); pi != i_end(); ++pi )
			setRelevant(*pi);

		markGCIsRelevant();
	}
		/// mark chosen part of TBox (P, Q and GCIs) relevant
	void calculateRelevant ( TConcept* p, TConcept* q )
	{
		setRelevant(p);
		if ( q != nullptr )
			setRelevant(q);
		markGCIsRelevant();
	}
		/// clear all relevance info
	void clearRelevanceInfo ( void ) { relevance.newLabel(); }
		/// gather relevance statistic for the whole KB
	void gatherRelevanceInfo ( void );
		/// put relevance information to a concept's data
	void setConceptRelevant ( TConcept* p )
	{
		curFeature = &p->posFeatures;
		setRelevant(p->pBody);
		KBFeatures |= p->posFeatures;
		collectLogicFeature(p);
		clearRelevanceInfo();

		// nothing to do for neg-prim concepts
		if ( p->isPrimitive() )
			return;

		curFeature = &p->negFeatures;
		setRelevant(inverse(p->pBody));
		KBFeatures |= p->negFeatures;
		clearRelevanceInfo();
	}
		/// update AUX features with the given one; update roles if necessary
	void updateAuxFeatures ( const LogicFeatures& lf )
	{
		if ( !lf.empty() )
		{
			auxFeatures |= lf;
			auxFeatures.mergeRoles();
		}
	}
		/// prepare features for SAT(P), or SUB(P,Q) test
	void prepareFeatures ( const TConcept* pConcept, const TConcept* qConcept );
		/// clear current features
	void clearFeatures ( void ) { curFeature = nullptr; }

//-----------------------------------------------------------------------------
//--		internal dump output interface
//-----------------------------------------------------------------------------
		/// dump concept-like essence using given dump method
	void dumpConcept ( dumpInterface* dump, const TConcept* p ) const;
		/// dump role-like essence using given dump method
	void dumpRole ( dumpInterface* dump, const TRole* p ) const;
		/// dump general concept expression using given dump method
	void dumpExpression ( dumpInterface* dump, BipolarPointer p ) const;
		/// dump all (relevant) roles
	void dumpAllRoles ( dumpInterface* dump ) const;

//-----------------------------------------------------------------------------
//--		internal save/load interface; implementation in SaveLoad.cpp
//-----------------------------------------------------------------------------

		/// init pointer2int maps
	void initPointerMaps ( SaveLoadManager& m ) const;

public:
		/// init c'tor
	TBox ( const ifOptionSet* Options,
		   const std::string& TopORoleName,
		   const std::string& BotORoleName,
		   const std::string& TopDRoleName,
		   const std::string& BotDRoleName );
		/// no copy c'tor
	TBox ( const TBox& ) = delete;
		/// no assignment
	TBox& operator = ( const TBox& ) = delete;
		/// d'tor
	~TBox();

		/// get RW access to used Role Master
	RoleMaster& getORM ( void ) { return ORM; }
		/// get RO access to used Role Master
	const RoleMaster& getORM ( void ) const { return ORM; }
		/// get RW access to used DataRole Master
	RoleMaster& getDRM ( void ) { return DRM; }
		/// get RO access to used DataRole Master
	const RoleMaster& getDRM ( void ) const { return DRM; }
		/// get RW access to the RoleMaster depending of the R
	RoleMaster& getRM ( const TRole* R ) { return R->isDataRole() ? getDRM() : getORM(); }
		/// get RO access to the RoleMaster depending of the R
	const RoleMaster& getRM ( const TRole* R ) const { return R->isDataRole() ? getDRM() : getORM(); }
		/// get RW access to a DT center
	DataTypeCenter& getDataTypeCenter ( void ) { return DTCenter; }
		/// get RO access to a DT center
	const DataTypeCenter& getDataTypeCenter ( void ) const { return DTCenter; }
		/// get RO access to DAG (needed for KE)
	const DLDag& getDag ( void ) const { return DLHeap; }

		/// set the value of a test timeout in milliseconds to VALUE
	void setTestTimeout ( unsigned long value ) { testTimeout = value; }
		/// (dis-)allow reasoner to use the undefined names in queries
	void setUseUndefinedNames ( bool value ) { Concepts.setAllowFresh(value); }
		/// set flag to use node cache to value VAL
	void setUseNodeCache ( bool val ) { useNodeCache = val; }

//-----------------------------------------------------------------------------
//--		public parser ensure* interface
//-----------------------------------------------------------------------------

		/// return registered concept by given NAME; @return NULL if can't register
	TConcept* getConcept ( const std::string& name ) { return Concepts.get(name); }
		/// return registered individual by given NAME; @return NULL if can't register
	TIndividual* getIndividual ( const std::string& name ) { return Individuals.get(name); }

		/// @return true iff given NAME is a name of a registered individual
	bool isIndividual ( const std::string& name ) const { return Individuals.isRegistered(name); }
		/// @return true iff given ENTRY is a registered individual
	bool isIndividual ( const TNamedEntry* entry ) const { return isIndividual(entry->getName()); }
		/// @return true iff given TREE represents a registered individual
	bool isIndividual ( const DLTree* tree ) const
		{ return (tree->Element().getToken() == INAME && isIndividual(tree->Element().getNE())); }
		/// @return true iff given DLTree represents a data value
	static bool isDataValue ( const DLTree* entry )
	{
		return entry->Element().getToken() == DATAEXPR &&
			static_cast<const TDataEntry*>(entry->Element().getNE())->isDataValue();
	}

		/// get TOP/BOTTOM/CN/IN by the DLTree entry
	TConcept* getCI ( const DLTree* name )
	{
		if ( name->Element() == TOP )
			return pTop;
		if ( name->Element() == BOTTOM )
			return pBottom;

		if ( !isName(name) )
			return nullptr;

		if ( name->Element().getToken() == CNAME )
			return toConcept(name->Element().getNE());
		else
			return toIndividual(name->Element().getNE());
	}
		/// get a DL tree by a given concept-like C
	DLTree* getTree ( TConcept* C ) const
	{
		if ( C == nullptr )
			return nullptr;
		if ( C == pTop )
			return createTop();
		if ( C == pBottom )
			return createBottom();
		return createEntry ( isIndividual(C) ? INAME : CNAME, C );
	}
		/// get fresh concept
	DLTree* getFreshConcept ( void ) const { return createEntry ( CNAME, pTemp ); }

	// n-ary absorption support

		/// get unique aux concept
	TConcept* getAuxConcept ( DLTree* desc = nullptr );
		/// replace RC=(AR:~C) with X such that C [= AR^-:X for fresh X. @return X
	TConcept* replaceForall ( DLTree* RC );
		/// @return true iff C has a cyclic definition, ie is referenced in its own description
	bool isCyclic ( TConcept* C ) const
	{
		ConceptSet processed;
		return isReferenced ( C, C, processed );
	}

//-----------------------------------------------------------------------------
//--		public input axiom interface
//-----------------------------------------------------------------------------

		/// register individual relation <a,b>:R
	void RegisterIndividualRelation ( TNamedEntry* a, TNamedEntry* R, TNamedEntry* b )
	{
		if ( !isIndividual(a) || !isIndividual(b) )
			throw EFaCTPlusPlus("Individual expected in related()");
		RelatedI.push_back ( new
			TRelated ( toIndividual(a),
					   toIndividual(b),
					   static_cast<TRole*>(R) ) );
		RelatedI.push_back ( new
			TRelated ( toIndividual(b),
					   toIndividual(a),
					   static_cast<TRole*>(R)->inverse() ) );
	}

		/// add general subsumption axiom C [= D
	void addSubsumeAxiom ( DLTree* C, DLTree* D );
		/// add axiom CN [= D for concept CN
	void addSubsumeAxiom ( TConcept* C, DLTree* D ) { addSubsumeAxiom ( getTree(C), D ); }
		/// add an axiom CN [= E for defined CN (CN=D already in base)
	void addSubsumeForDefined ( TConcept* C, DLTree* E );
		/// add an axiom LHS = RHS
	void addEqualityAxiom ( DLTree* lhs, DLTree* rhs );

		/// add simple rule RULE to the TBox' rules
	inline
	void addSimpleRule ( TSimpleRule* Rule )
	{
		initRuleFields ( Rule->Body, SimpleRules.size() );
		SimpleRules.push_back(Rule);
	}
		/// add binary simple rule (C0,C1)=>H
	void addBSimpleRule ( TConcept* C0, TConcept* C1, DLTree* H )
	{
		ConceptVector v;
		v.push_back(C0);
		v.push_back(C1);
		addSimpleRule ( new TSimpleRule ( v, H ) );
	}

	// external-set methods for set-of-concept-expressions
	void processEquivalentC ( ea_iterator beg, ea_iterator end );
	void processDisjointC ( ea_iterator beg, ea_iterator end );
	void processEquivalentR ( ea_iterator beg, ea_iterator end );
	void processDisjointR ( ea_iterator beg, ea_iterator end );
	void processSame ( ea_iterator beg, ea_iterator end );
	void processDifferent ( ea_iterator beg, ea_iterator end );

		/// let TBox know that the whole ontology is loaded
	void finishLoading ( void ) { setForbidUndefinedNames(true); }
		/// @return true if KB contains fairness constraints
	bool hasFC ( void ) const { return !Fairness.empty(); }
		/// add concept expression C as a fairness constraint
	void setFairnessConstraint ( ea_iterator beg, ea_iterator end )
	{
		for ( ; beg < end; ++beg )
			if ( isName(*beg) )
			{
				Fairness.push_back(getCI(*beg));
				deleteTree(*beg);
			}
			else
			{
				// build a flag for a FC
				TConcept* fc = getAuxConcept();
				Fairness.push_back(fc);
				// make an axiom: FC = C
				addEqualityAxiom ( getTree(fc), *beg );
			}
		// in presence of fairness constraints use ancestor blocking
		if ( useAnywhereBlocking && hasFC() )
		{
			useAnywhereBlocking = false;
			if ( LLM.isWritable(llAlways) )
				LL << "\nFairness constraints: set useAnywhereBlocking = 0";
		}
	}

//-----------------------------------------------------------------------------
//--		public access interface
//-----------------------------------------------------------------------------

		/// GCI Axioms access
	BipolarPointer getTG ( void ) const { return T_G; }
		/// get simple rule by its INDEX
	const TSimpleRule* getSimpleRule ( size_t index ) const { return SimpleRules[index]; }

		/// check if the relevant part of KB contains inverse roles.
	bool isIRinQuery ( void ) const
	{
		if ( curFeature != nullptr )
			return curFeature->hasInverseRole();
		else
			return KBFeatures.hasInverseRole();
	}
		/// check if the relevant part of KB contains number restrictions.
	bool isNRinQuery ( void ) const
	{
		const LogicFeatures* p = curFeature ? curFeature : &KBFeatures;
		return p->hasFunctionalRestriction() || p->hasNumberRestriction() || p->hasQNumberRestriction();
	}
		/// check if the relevant part of KB contains singletons
	bool testHasNominals ( void ) const
	{
		if ( curFeature != nullptr )
			return curFeature->hasSingletons();
		else
			return KBFeatures.hasSingletons();
	}
		/// check if the relevant part of KB contains top role
	bool testHasTopRole ( void ) const
	{
		if ( curFeature != nullptr )
			return curFeature->hasTopRole();
		else
			return KBFeatures.hasTopRole();
	}
		/// check if Sorted Reasoning is applicable
	bool canUseSortedReasoning ( void ) const
		{ return useSortedReasoning && !GCIs.isGCI() && !GCIs.isReflexive(); }

		/// @return true iff individual C is known to be p-blocked by another one
	bool isBlockedInd ( TConcept* C ) const { return SameI.find(C) != SameI.end(); }
		/// get individual that blocks C; works only for blocked individuals C
	TIndividual* getBlockingInd ( TConcept* C ) const { return SameI.find(C)->second.first; }
		/// @return true iff an individual blocks C deterministically
	bool isBlockingDet ( TConcept* C ) const { return SameI.find(C)->second.second; }

//-----------------------------------------------------------------------------
//--		public iterators
//-----------------------------------------------------------------------------

		/// RO begin() for concepts
	c_const_iterator c_begin ( void ) const { return Concepts.begin(); }
		/// RO end() for concepts
	c_const_iterator c_end ( void ) const { return Concepts.end(); }

		/// RO begin() for individuals
	i_const_iterator i_begin ( void ) const { return Individuals.begin(); }
		/// RO end() for individuals
	i_const_iterator i_end ( void ) const { return Individuals.end(); }

//-----------------------------------------------------------------------------
//--		public reasoning interface
//-----------------------------------------------------------------------------
		/// prepare to reasoning
	void prepareReasoning ( void );
		/// perform classification (assuming KB is consistent)
	void performClassification ( void ) { createTaxonomy ( /*needIndividuals=*/false ); }
		/// perform realisation (assuming KB is consistent)
	void performRealisation ( void ) { createTaxonomy ( /*needIndividuals=*/true ); }
		/// reclassify taxonomy wrt changed sets
	void reclassify ( const std::set<const TNamedEntity*>& MPlus, const std::set<const TNamedEntity*>& MMinus );

		/// get (READ-WRITE) access to internal Taxonomy of concepts
	Taxonomy* getTaxonomy ( void ) { return pTax; }

		/// set given structure as a progress monitor
	void setProgressMonitor ( TProgressMonitor* pMon ) { pMonitor = pMon; }
		/// check that reasoning progress was cancelled by external application
	bool isCancelled ( void ) const { return pMonitor != nullptr && pMonitor->isCancelled(); }
		/// set verbose output (ie, default progress monitor, concept and role taxonomies) wrt given VALUE
	void setVerboseOutput ( bool value ) { verboseOutput = value; }

		/// create (and DAG-ify) query concept via its definition
	TConcept* createQueryConcept ( const DLTree* query );
		/// preprocess query concept: put description into DAG
	void preprocessQueryConcept ( TConcept* query );
		/// classify query concept
	void classifyQueryConcept ( void );
		/// delete all query-related stuff
	void clearQueryConcept ( void ) { DLHeap.removeQuery(); }
		/// @return true if the concept in question is a query concept
	bool isComplexQuery ( const TConcept* queryConcept ) const { return queryConcept == pQuery; }

//-----------------------------------------------------------------------------
//--		public reasoning interface
//-----------------------------------------------------------------------------

		/// get status flag
	KBStatus getStatus ( void ) const { return Status; }
		/// set consistency flag
	void setConsistency ( bool val )
	{
		Status = kbCChecked;
		Consistent = val;
	}
		/// check if the ontology is consistent
	bool isConsistent ( void )
	{
		if ( Status < kbCChecked )
		{
			prepareReasoning();
			if ( Status < kbCChecked && Consistent )	// we can detect inconsistency during preprocessing
				setConsistency(performConsistencyCheck());
		}
		return Consistent;
	}
		/// check if a subsumption C [= D holds
	bool isSubHolds ( const TConcept* C, const TConcept* D );
		/// check if a concept C is satisfiable
	bool isSatisfiable ( const TConcept* C );
		/// check that 2 individuals are the same
	bool isSameIndividuals ( const TIndividual* a, const TIndividual* b );
		/// check if 2 roles are disjoint
	bool isDisjointRoles ( const TRole* R, const TRole* S );
		/// check if the role R is irreflexive
	bool isIrreflexive ( const TRole* R );

		/// fills cache entry for given concept; SUB means that the concept is on the right side of a subsumption test
	const modelCacheInterface* initCache ( const TConcept* pConcept, bool sub = false );

		/// build a completion tree for a concept C (no caching as it breaks the idea of KE). @return the root node
	const DlCompletionTree* buildCompletionTree ( const TConcept* C );

		/// test if 2 concept non-subsumption can be determined by cache merging
	enum modelCacheState testCachedNonSubsumption ( const TConcept* p, const TConcept* q );
		/// test if 2 concept non-subsumption can be determined by sorts checking
	bool testSortedNonSubsumption ( const TConcept* p, const TConcept* q )
	{
		// sorted reasoning doesn't work in presence of GCIs
		if ( !canUseSortedReasoning() )
			return false;
		// doesn't work for the SAT tests
		if ( q == nullptr )
			return false;
		return !DLHeap.haveSameSort ( p->pName, q->pName );
	}
//-----------------------------------------------------------------------------
//--		public output interface
//-----------------------------------------------------------------------------

		/// dump query processing TIME, reasoning statistics and a (preprocessed) TBox
	void writeReasoningResult ( std::ostream& o, float time ) const;
		/// print TBox as a whole
	void Print ( std::ostream& o ) const
	{
		DLHeap.PrintStat(o);
		ORM.Print ( o, "Object" );
		DRM.Print ( o, "Data" );
		PrintConcepts(o);
		PrintIndividuals(o);
		PrintSimpleRules(o);
		PrintAxioms(o);
		DLHeap.Print(o);
	}

		/// create dump of relevant part of query using given method
	void dump ( dumpInterface* dump ) const;

//-----------------------------------------------------------------------------
//--		 save/load interface; implementation in SaveLoad.cpp
//-----------------------------------------------------------------------------

		/// save the KB into the given stream
	void Save ( SaveLoadManager& m );
		/// load the KB from given stream wrt STATUS
	void Load ( SaveLoadManager& m, KBStatus status );
		/// save taxonomy with names (used in the incremental)
	void SaveTaxonomy ( SaveLoadManager& m, const std::set<const TNamedEntry*>& excluded );
		/// load taxonomy with names (used in the incremental)
	void LoadTaxonomy ( SaveLoadManager& m );
}; // TBox

#endif
