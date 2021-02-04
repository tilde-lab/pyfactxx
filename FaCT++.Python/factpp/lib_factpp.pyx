#
# factpp - Python interface to FaCT++ reasoner
#
# Copyright (C) 2016-2018 by Artur Wroblewski <wrobell@riseup.net>
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

# distutils: language = c++
# cython: c_string_type=unicode, c_string_encoding=utf8, embedsignature=True, language_level=3

from libcpp cimport bool
from libcpp.string cimport string
from cython.operator cimport dereference, postincrement

cdef extern from "<vector>" namespace "std":
    cdef cppclass vector[T]:
        cppclass iterator:
            T operator*()
            iterator operator++()
            bint operator==(iterator)
            bint operator!=(iterator)

        T& operator[](int)
        int size()
        iterator begin()
        iterator end()

cdef extern from "<iostream>" namespace "std":
    cdef cppclass ostream:
        ostream& write(const char*, int) except +
    
    cdef cppclass istream:
        istream& read(const char*, int) except +

cdef extern from "<iostream>" namespace "std::ios_base":
    cdef cppclass open_mode:
        pass
    cdef open_mode binary

cdef extern from "<fstream>" namespace "std":
    cdef cppclass ofstream(ostream):
        ofstream(const char*) except +
        ofstream(const char*, open_mode) except +
    
    cdef cppclass ifstream(istream):
        ifstream(const char*) except +
        ifstream(const char*, open_mode) except +

cdef extern from 'taxNamEntry.h':
    cdef cppclass ClassifiableEntry:
        ClassifiableEntry() except +
        string getName()
        bool isSystem()
        bool isTop()
        bool isBottom()
        TNamedEntity *getEntity()

cdef bool skip(const ClassifiableEntry *obj):
    return obj.isSystem() or obj.isBottom()

cdef extern from 'tDLAxiom.h':
    cdef cppclass TDLAxiom:
        TDLAxiom() except +

cdef extern from 'tConcept.h':
    cdef cppclass TConcept(ClassifiableEntry):
        TConcept(string) except +
        bool isSingleton()

cdef extern from 'tIndividual.h':
    cdef cppclass TRelatedMap:
        TRelatedMap() except +

    cdef cppclass TIndividual(TConcept):
        TIndividual(string) except +

cdef extern from 'tRole.h':
    cdef cppclass TRole:
        TRole(string) except +

cdef extern from 'tIndividual.h' namespace 'TRelatedMap':
    ctypedef vector[TIndividual*] CIVec

# cdef extern from 'tNamedEntry.h':
#     cdef cppclass TNamedEntry:
#         TNamedEntry(string) except +
#         string getName()

cdef extern from 'tDLExpression.h':
    cdef cppclass TNamedEntity:
        string getName()

    cdef cppclass TDLObjectRoleName:
        TDLObjectRoleName(string) except +
        string getName()

    cdef cppclass TDLExpression:
        pass

    cdef cppclass TDLRoleExpression(TDLExpression):
        pass

    cdef cppclass TDLObjectRoleComplexExpression(TDLRoleExpression):
        pass

    cdef cppclass TDLObjectRoleExpression(TDLObjectRoleComplexExpression):
        pass

    cdef cppclass TDLObjectRoleName(TDLObjectRoleExpression):
        pass

    cdef cppclass TDLDataExpression:
        pass

    cdef cppclass TDLDataValue(TDLDataExpression):
        pass

    cdef cppclass TDLDataTypeExpression(TDLDataExpression):
        pass

    cdef cppclass TDLDataTypeName(TDLDataTypeExpression):
        pass

    cdef cppclass TDLConceptExpression(TDLExpression):
        pass

    cdef cppclass TDLConceptName(TDLConceptExpression):
        string getName()

    cdef cppclass TDLIndividualExpression(TDLExpression):
        pass

    cdef cppclass TDLIndividualName(TDLIndividualExpression):
        TDLIndividualName(string) except +
        string getName()

    cdef cppclass TDLDataRoleExpression(TDLExpression):
        pass

    cdef cppclass TDLDataRoleName(TDLDataRoleExpression):
        string getName()

cdef extern from 'tExpressionManager.h':
    cdef cppclass TExpressionManager:
        TDLConceptName* Concept(string)
        TDLIndividualExpression* Individual(string)
        TDLDataRoleName* DataRole(string)
        TDLObjectRoleName* ObjectRole(string)
        TDLDataExpression* DataTop()
        TDLDataExpression* DataBottom()
        TDLDataTypeName* DataType(string)

        TDLConceptExpression* Top()
        TDLConceptExpression* Bottom()
        TDLConceptExpression* Exists(const TDLObjectRoleExpression*, const TDLConceptExpression*)
        TDLConceptExpression* Exists(const TDLDataRoleExpression*, const TDLDataExpression*)
        TDLObjectRoleExpression* Inverse(const TDLObjectRoleExpression*)
        TDLObjectRoleComplexExpression* Compose()

        TDLConceptExpression* And()
        TDLConceptExpression* OneOf()

        TDLConceptExpression* Cardinality(unsigned int, const TDLDataRoleExpression*, const TDLDataExpression*)
        TDLConceptExpression* MinCardinality(unsigned int, const TDLObjectRoleExpression*, const TDLConceptExpression*)
        TDLConceptExpression* MinCardinality(unsigned int, const TDLDataRoleExpression*, const TDLDataExpression*)
        TDLConceptExpression* MaxCardinality(unsigned int, const TDLObjectRoleExpression*, const TDLConceptExpression*)
        TDLConceptExpression* MaxCardinality(unsigned int, const TDLDataRoleExpression*, const TDLDataExpression*)

        const TDLDataValue* DataValue(string, const TDLDataTypeExpression*);

        void newArgList()
        void addArg(const TDLExpression*)


#cdef extern from 'Kernel.h' namespace 'ReasoningKernel':
#    ctypedef vector[TNamedEntry*] IndividualSet

cdef extern from 'taxVertex.h':
    cdef cppclass TaxonomyVertex:
        TaxonomyVertex() except +
        const ClassifiableEntry* getPrimer()
        vector[TaxonomyVertex*].iterator begin(bool)
        vector[TaxonomyVertex*].iterator end(bool)

cdef extern from 'parser.h':
    cdef cppclass DLLispParser:
        DLLispParser(istream*, ReasoningKernel*) except +
        void Parse()

cdef extern from 'ifOptions.h':
    cdef cppclass ifOptionSet:
        ifOptionSet() except +
        bool setOption(const string, const string)

cdef extern from 'Kernel.h':
    cdef cppclass ReasoningKernel:
        ReasoningKernel() except +
        TExpressionManager* getExpressionManager()
        ifOptionSet* getOptions()

        TDLAxiom* equalORoles()
        TDLAxiom* equalDRoles()
        TDLAxiom* setSymmetric(TDLObjectRoleExpression*)
        TDLAxiom* setTransitive(TDLObjectRoleExpression*)
        TDLAxiom* setIrreflexive(TDLObjectRoleExpression*)
        TDLAxiom* setOFunctional(TDLObjectRoleExpression*)
        TDLAxiom* setDFunctional(TDLDataRoleExpression*)
        TDLAxiom* setInverseFunctional(TDLObjectRoleExpression*)
        TDLAxiom* relatedTo(TDLIndividualExpression*, TDLObjectRoleExpression*, TDLIndividualExpression*)
        TDLAxiom* relatedToNot(TDLIndividualExpression*, TDLObjectRoleComplexExpression*, TDLIndividualExpression*)
        TDLAxiom* instanceOf(TDLIndividualExpression*, TDLConceptExpression* C)
        bool isSameIndividuals(const TDLIndividualExpression*, const TDLIndividualExpression*)
        bool isInstance(TDLIndividualExpression*, TDLConceptExpression* C)
        TDLAxiom* valueOf(TDLIndividualExpression*, TDLDataRoleExpression*, TDLDataValue*)
        TDLAxiom* impliesORoles(TDLObjectRoleComplexExpression*, TDLObjectRoleExpression*)
        TDLAxiom* impliesDRoles(TDLDataRoleExpression*, TDLDataRoleExpression*)
        bool isSubRoles(const TDLObjectRoleExpression*, const TDLObjectRoleExpression*)
        bool isSubRoles(const TDLDataRoleExpression*, const TDLDataRoleExpression*)
        TDLAxiom* setODomain(TDLObjectRoleExpression*, TDLConceptExpression*)
        TDLAxiom* setORange(TDLObjectRoleExpression*, TDLConceptExpression*)
        TDLAxiom* setDDomain(TDLDataRoleExpression*, TDLConceptExpression*)
        TDLAxiom* setDRange(TDLDataRoleExpression*, TDLDataExpression*)
        TDLAxiom* setInverseRoles(TDLObjectRoleExpression*, TDLObjectRoleExpression*)

        TDLAxiom* equalConcepts()
        TDLAxiom* disjointConcepts()
        TDLAxiom* disjointUnion(TDLConceptExpression*)
        TDLAxiom* processSame()
        TDLAxiom* processDifferent()
        TDLAxiom* impliesConcepts(TDLConceptExpression*, TDLConceptExpression*)
        bool isSubsumedBy(TDLConceptExpression*, TDLConceptExpression*)
        #TIndividual* getIndividual(TDLIndividualExpression*, char*)
        #TRole* getRole(TDLObjectRoleExpression*, char*)
        #CIVec& getRelated(TIndividual*, TRole*)
        #void getRoleFillers(TDLIndividualExpression*, TDLObjectRoleExpression*, IndividualSet&)
        CIVec& getRoleFillers(TDLIndividualExpression*, TDLObjectRoleExpression*)
        TaxonomyVertex* setUpCache(TDLConceptExpression*)

        void realiseKB()
        void classifyKB()
        bool isKBConsistent()
        bool isKBPreprocessed()

        void dumpLISP(ostream)

cdef class Expression:
    cdef const TDLExpression *_obj

cdef class IndividualExpr(Expression):
    cdef TDLIndividualExpression *c_obj(self):
        return <TDLIndividualExpression*>self._obj

cdef class Individual(IndividualExpr):
    @property
    def name(self):
        return (<TDLIndividualName*>self.c_obj()).getName()

    def __repr__(self):
        return '<{} object at {}: {}>'.format(self.__class__.__name__, id(self), self.name)

cdef class ConceptExpr(Expression):
    cdef TDLConceptExpression *c_obj(self):
        return <TDLConceptExpression*>self._obj

cdef class Concept(ConceptExpr):
    @property
    def name(self):
        return (<TDLConceptName*>self.c_obj()).getName()

    def __repr__(self):
        return '<{} object at {}: {}>'.format(self.__class__.__name__, id(self), self.name)

cdef class ObjectRoleExpr(Expression):
    cdef TDLObjectRoleExpression *c_obj(self):
        return <TDLObjectRoleExpression*>self._obj

cdef class ObjectRole(ObjectRoleExpr):
    @property
    def name(self):
        return (<TDLObjectRoleName*>self.c_obj()).getName()

    def __repr__(self):
        return '<{} object at {}: {}>'.format(self.__class__.__name__, id(self), self.name)

cdef class DataRoleExpr(Expression):
    cdef TDLDataRoleExpression *c_obj(self):
        return <TDLDataRoleExpression*>self._obj

cdef class DataRole(DataRoleExpr):
    @property
    def name(self):
        return (<TDLDataRoleName*>self.c_obj()).getName()

    def __repr__(self):
        return '<{} object at {}: {}>'.format(self.__class__.__name__, id(self), self.name)

cdef class DataExpr:
    cdef const TDLDataExpression *c_obj

cdef class DataType:
    cdef TDLDataTypeName *c_obj

cdef class Reasoner:
    cdef ReasoningKernel *c_kernel
    cdef TExpressionManager *c_mgr

    cdef readonly DataType type_int
    cdef readonly DataType type_float
    cdef readonly DataType type_str
    cdef readonly DataType type_bool
    cdef readonly DataType type_datetime_long
    cdef readonly dict _cache

    def __cinit__(self):
        self.c_kernel = new ReasoningKernel()
        self.c_mgr = self.c_kernel.getExpressionManager()

    def __init__(self, **kwds):
        self.type_int = self.data_type('http://www.w3.org/2001/XMLSchema#integer')
        self.type_float = self.data_type('http://www.w3.org/2001/XMLSchema#float')
        self.type_str = self.data_type('http://www.w3.org/2001/XMLSchema#string')
        self.type_bool = self.data_type('http://www.w3.org/2001/XMLSchema#boolean')
        self.type_datetime_long = self.data_type('http://www.w3.org/2001/XMLSchema#dateTimeAsLong')
        # set options
        cdef string name
        cdef string value
        for k, v in kwds.items():
            name = k.encode('UTF-8')
            value = v.encode('UTF-8')
            if self.c_kernel.getOptions().setOption(name, value):
                raise ValueError(f'Error setting value {v} for option {k}. \n'
                'Either option does not exist or value is of incorrect type')

        self._cache = {}  # it should be weakref dict

    def parse_lisp(self, str fn):
        cdef ifstream* in_file = new ifstream(fn.encode(), binary)
        cdef DLLispParser *parser = new DLLispParser(in_file, self.c_kernel)
        try:
            parser.Parse()
        finally:
            del in_file

    def __dealloc__(self):
        del self.c_kernel

    cdef _get(self, type T, TDLExpression *c_obj):
        return instance(self._cache, T, c_obj)

    cdef _to_cls_expr(self, const ClassifiableEntry *obj):
        # FIXME: or use obj.getEntity()?
        if obj.isTop():
            return self.concept_top()
        else:
            return self.concept(obj.getName())

    def _find_o_role_items(self, ObjectRoleExpr r, bool top):
        # TODO: duplicate code, see _find_d_role_items

        cdef TDLConceptExpression *start
        cdef TaxonomyVertex *node
        cdef const ClassifiableEntry *obj
        cdef vector[TaxonomyVertex*].iterator it

        start = self.c_mgr.Top() if top else self.c_mgr.Bottom()
        node = self.c_kernel.setUpCache(self.c_mgr.Exists(r.c_obj(), start))
        obj = <const ClassifiableEntry*>node.getPrimer()
        if not skip(obj):
            yield self._to_cls_expr(obj)

        it = node.begin(True)
        while it != node.end(True):
            obj = <const ClassifiableEntry*>dereference(it).getPrimer()
            if skip(obj):
                postincrement(it)
                continue
            yield self._to_cls_expr(obj)
            postincrement(it)

    def _find_d_role_items(self, DataRoleExpr r, bool top):
        # TODO: duplicate code, see _find_o_role_items

        cdef TDLDataExpression *start
        cdef TaxonomyVertex *node
        cdef const ClassifiableEntry *obj
        cdef vector[TaxonomyVertex*].iterator it

        start = self.c_mgr.DataTop() if top else self.c_mgr.DataBottom()
        node = self.c_kernel.setUpCache(self.c_mgr.Exists(r.c_obj(), start))

        it = node.begin(True)
        while it != node.end(True):
            obj = <const ClassifiableEntry*>dereference(it).getPrimer()
            yield self._to_cls_expr(obj)
            postincrement(it)

    def _arg_list(self, items):
        self.c_mgr.newArgList()
        for e in items:
            self.c_mgr.addArg((<Expression>e)._obj)

    #
    # concepts
    #
    def concept(self, name not None):
        return self._get(Concept, self.c_mgr.Concept(name.encode()))

    def concept_top(self):
        return self._get(ConceptExpr, self.c_mgr.Top())

    def concept_bottom(self):
        return self._get(ConceptExpr, self.c_mgr.Bottom())

    def implies_concepts(self, ConceptExpr subcls, ConceptExpr parent):
        """
        Make first class a subclass of the second.

        :param subcls: Derived class (subclass).
        :param parent: Parent class (superclass)
        """
        self.c_kernel.impliesConcepts(subcls.c_obj(), parent.c_obj())

    def is_subsumed_by(self, ConceptExpr c1, ConceptExpr c2):
        return self.c_kernel.isSubsumedBy(c1.c_obj(), c2.c_obj())

    def equal_concepts(self, *classes):
        self._arg_list(classes)
        self.c_kernel.equalConcepts()

    def disjoint_concepts(self, *classes):
        self._arg_list(classes)
        self.c_kernel.disjointConcepts()

    def union_of(self, ConceptExpr c, *classes):
        """
        Concept `c` is union of collection of concepts.

        :param c: The union concept.
        :param classes: Collection of concepts in the union.
        """
        self._arg_list(classes)
        self.c_kernel.disjointUnion(c.c_obj())

    def intersection(self, *classes):
        self._arg_list(classes)
        return self._get(ConceptExpr, self.c_mgr.And())

    #
    # individuals
    #
    def individual(self, name not None):
        return self._get(Individual, self.c_mgr.Individual(name.encode()))

    def instance_of(self, IndividualExpr i, ConceptExpr c):
        self.c_kernel.instanceOf(i.c_obj(), c.c_obj())

    def one_of(self, *instances):
        self._arg_list(instances)
        return self._get(ConceptExpr, self.c_mgr.OneOf())

    def same_individuals(self, instances):
        self._arg_list(instances)
        self.c_kernel.processSame()

    def different_individuals(self, *instances):
        self._arg_list(instances)
        self.c_kernel.processDifferent()

    def is_same_individuals(self, IndividualExpr i1, IndividualExpr i2):
        return self.c_kernel.isSameIndividuals(i1.c_obj(), i2.c_obj())

    def is_instance(self, IndividualExpr i, ConceptExpr c):
        return self.c_kernel.isInstance(i.c_obj(), c.c_obj())

    #
    # object roles
    #
    def object_role(self, name not None):
        return self._get(ObjectRole, self.c_mgr.ObjectRole(name.encode()))

    def set_o_domain(self, ObjectRoleExpr r, ConceptExpr c):
        self.c_kernel.setODomain(r.c_obj(), c.c_obj())

    def get_o_domain(self, ObjectRoleExpr r):
        yield from self._find_o_role_items(r, True)

    def get_o_range(self, ObjectRoleExpr r):
        rev = self._get(ObjectRoleExpr, self.c_mgr.Inverse((r.c_obj())))
        yield from self._find_o_role_items(rev, True)

    def set_o_range(self, ObjectRoleExpr r, ConceptExpr c):
        self.c_kernel.setORange(r.c_obj(), c.c_obj())

    def o_exists(self, ObjectRoleExpr r, ConceptExpr c):
        return self._get(ConceptExpr, self.c_mgr.Exists(r.c_obj(), c.c_obj()))

    def min_o_cardinality(self, unsigned int n, ObjectRoleExpr r, ConceptExpr c):
        return self._get(ConceptExpr, self.c_mgr.MinCardinality(n, r.c_obj(), c.c_obj()))

    def max_o_cardinality(self, unsigned int n, ObjectRoleExpr r, ConceptExpr c):
        return self._get(ConceptExpr, self.c_mgr.MaxCardinality(n, r.c_obj(), c.c_obj()))

    def equal_o_roles(self, *roles):
        self._arg_list(roles)
        self.c_kernel.equalORoles()

    def implies_o_roles(self, ObjectRoleExpr r1, ObjectRoleExpr r2):
        self.c_kernel.impliesORoles(r1.c_obj(), r2.c_obj())

    def is_sub_o_role(self, ObjectRoleExpr r1, ObjectRoleExpr r2):
        return self.c_kernel.isSubRoles(r1.c_obj(), r2.c_obj())

    def set_symmetric(self, ObjectRoleExpr r):
        self.c_kernel.setSymmetric(r.c_obj())

    def set_transitive(self, ObjectRoleExpr r):
        self.c_kernel.setTransitive(r.c_obj())

    def set_irreflexive(self, ObjectRoleExpr r):
        self.c_kernel.setIrreflexive(r.c_obj())

    def set_o_functional(self, ObjectRoleExpr r):
        self.c_kernel.setOFunctional(r.c_obj())

    def set_inverse_functional(self, ObjectRoleExpr r):
        self.c_kernel.setInverseFunctional(r.c_obj())

    def inverse(self, ObjectRoleExpr r):
        return self._get(ObjectRoleExpr, self.c_mgr.Inverse(r.c_obj()))

    def compose(self, *roles):
        """
        Compose chain of roles.

        :param roles: Collection of object roles.
        """
        self._arg_list(roles)
        return self._get(ObjectRoleExpr, self.c_mgr.Compose())

    def set_inverse_roles(self, ObjectRoleExpr r1, ObjectRoleExpr r2):
        self.c_kernel.setInverseRoles(r1.c_obj(), r2.c_obj())

    def related_to(self, IndividualExpr i1, ObjectRoleExpr r, IndividualExpr i2):
        self.c_kernel.relatedTo(i1.c_obj(), r.c_obj(), i2.c_obj())

    def related_to_not(self, IndividualExpr i1, ObjectRoleExpr r, IndividualExpr i2):
        self.c_kernel.relatedToNot(i1.c_obj(), r.c_obj(), i2.c_obj())

    def get_role_fillers(self, IndividualExpr i, ObjectRoleExpr r):
        cdef CIVec data = self.c_kernel.getRoleFillers(i.c_obj(), r.c_obj())

        for k in range(data.size()):
            yield self.individual(data[k].getName())

    def get_instances(self, ConceptExpr c):
        cdef const TConcept *obj
        cdef TaxonomyVertex *node = self.c_kernel.setUpCache(c.c_obj())
        cdef vector[TaxonomyVertex*].iterator it = node.begin(False)
        obj = <const TConcept*>dereference(it).getPrimer()
        if obj.isBottom():
            return

        while it != node.end(False):
            obj = <const TConcept*>dereference(it).getPrimer()
            if obj.isSingleton():
                yield self.individual(obj.getName())
            else:
                yield from self.get_instances(self.concept(obj.getName()))
            postincrement(it)

    #
    # data roles
    #
    def data_role(self, name not None):
        return self._get(DataRole, self.c_mgr.DataRole(name.encode()))

    def data_top(self):
        cdef DataExpr result = DataExpr.__new__(DataExpr)
        result.c_obj = self.c_mgr.DataTop()
        return result

    def get_d_domain(self, DataRoleExpr r):
        yield from self._find_d_role_items(r, True)

    def data_type(self, name not None):
        cdef DataType result = DataType.__new__(DataType)
        result.c_obj = self.c_mgr.DataType(name.encode())
        return result

    def equal_d_roles(self, *roles):
        self._arg_list(roles)
        self.c_kernel.equalDRoles()

    def set_d_domain(self, DataRoleExpr r, ConceptExpr c):
        self.c_kernel.setDDomain(r.c_obj(), c.c_obj())

    def set_d_range(self, DataRoleExpr r, DataType t):
        self.c_kernel.setDRange(r.c_obj(), t.c_obj)

    def d_cardinality(self, unsigned int n, DataRoleExpr r, DataExpr d):
        return self._get(ConceptExpr, self.c_mgr.Cardinality(n, r.c_obj(), d.c_obj))

    def max_d_cardinality(self, unsigned int n, DataRoleExpr r, DataExpr d):
        return self._get(ConceptExpr, self.c_mgr.MaxCardinality(n, r.c_obj(), d.c_obj))

    def implies_d_roles(self, DataRoleExpr r1, DataRoleExpr r2):
        self.c_kernel.impliesDRoles(r1.c_obj(), r2.c_obj())

    def is_sub_d_role(self, DataRoleExpr r1, DataRoleExpr r2):
        return self.c_kernel.isSubRoles(r1.c_obj(), r2.c_obj())

    def set_d_functional(self, DataRoleExpr r):
        self.c_kernel.setDFunctional(r.c_obj())

#   def data_value(self, string v, DataType t):
#       cdef DataExpr result = DataExpr.__new__(DataExpr)
#       result.c_obj = self.c_mgr.DataValue(v, t.c_obj)
#       return result

    def value_of_int(self, IndividualExpr i, DataRoleExpr r, int v):
        value = self.c_mgr.DataValue(str(v).encode(), self.type_int.c_obj)
        self.c_kernel.valueOf(i.c_obj(), r.c_obj(), value)

    def value_of_str(self, IndividualExpr i, DataRoleExpr r, str v):
        value = self.c_mgr.DataValue(v.encode(), self.type_str.c_obj)
        self.c_kernel.valueOf(i.c_obj(), r.c_obj(), value)

    def value_of_float(self, IndividualExpr i, DataRoleExpr r, float v):
        value = self.c_mgr.DataValue(str(v).encode(), self.type_float.c_obj)
        self.c_kernel.valueOf(i.c_obj(), r.c_obj(), value)

    def value_of_bool(self, IndividualExpr i, DataRoleExpr r, bool v):
        value = self.c_mgr.DataValue(str(v).encode(), self.type_bool.c_obj)
        self.c_kernel.valueOf(i.c_obj(), r.c_obj(), value)

    #
    # general
    #
    def realise(self):
        self.c_kernel.realiseKB()

    def classify(self):
        self.c_kernel.classifyKB()

    def is_consistent(self):
        return self.c_kernel.isKBConsistent()

    def is_preprocessed(self):
        return self.c_kernel.isKBPreprocessed()

    def dump(self, str fn):
        cdef ofstream *out = new ofstream(fn.encode(), binary)
        try:
            self.c_kernel.dumpLISP(out[0])
        finally:
            del out

cdef instance(dict cache, type T, const TDLExpression *c_obj):
    cdef Expression result

    key = <long>c_obj
    result = cache.get(key)

    if result is None:
        result = T.__new__(T)
        result._obj = c_obj
        cache[key] = result
    assert result is not None
    return result

# vim: sw=4:et:ai
