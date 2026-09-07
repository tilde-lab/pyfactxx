#
# Coras - OWL reasoning system
#
# Copyright (C) 2018 by Artur Wroblewski <wrobell@riseup.net>
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

"""
OWL parsers to load data from RDFLib graph to FaCT++ reasoner.
"""

import os
import itertools
import logging
from collections import namedtuple
from functools import partial, singledispatch

import rdflib
from rdflib.namespace import RDF, RDFS, OWL, XSD

from . import debug

logger = logging.getLogger(__name__)

CLASS_TYPE = {RDFS.Class, OWL.Class}

# XSD datatypes mapped onto the FaCT++ basic integer and floating point
# datatypes; anything else is handled as a string
INT_TYPE = {
    XSD.integer, XSD.int, XSD.long, XSD.short, XSD.byte,
    XSD.nonNegativeInteger, XSD.positiveInteger,
    XSD.nonPositiveInteger, XSD.negativeInteger,
    XSD.unsignedLong, XSD.unsignedInt, XSD.unsignedShort, XSD.unsignedByte,
}
FLOAT_TYPE = {XSD.decimal, XSD.float, XSD.double}

QUERY_CLASS = [
    (None, RDF.type, OWL.Class),
    (None, RDF.type, RDFS.Class),
    (None, RDF.type, OWL.Restriction),
    (None, RDF.type, OWL.AllDisjointClasses),
    (None, OWL.distinctMembers, None),
]

# instances of special classes
QUERY_INSTANCES = [
    (None, RDF.type, OWL.Thing),
    (None, RDF.type, OWL.NamedIndividual),
]

QUERY_OBJ_PROPERTY = [
    (None, RDF.type, OWL.ObjectProperty),
    (None, RDF.type, OWL.InverseFunctionalProperty),
    (None, RDF.type, OWL.SymmetricProperty),
    (None, RDF.type, OWL.TransitiveProperty),
]

QUERY_AXIOM = [
    (None, RDF.type, OWL.NegativePropertyAssertion),
]

# pairwise individual inequality; owl:AllDifferent and owl:distinctMembers
# are handled with the class queries
QUERY_INDIVIDUAL_AXIOM = [
    (None, OWL.differentFrom, None),
]

QUERY_DATA_PROPERTY = (None, RDF.type, OWL.DatatypeProperty)

Meta = namedtuple('Meta', ['attr', 'type', 'relation'])
MetaAttrQuery = namedtuple('MetaAttrQuery', ['pred', 'obj'])

flatten = itertools.chain.from_iterable

@debug.register
def parse(graph, reasoner):
    parsers = create_parsers(graph, reasoner)
    for q, ctor, meta in parsers:
        items = [s for s, p, o in find_triples(q, graph)]
        parse_items(graph, items, ctor, meta)

    data_properties = set()
    for s, p, o in find_triples((None, None, None), graph):
        if isinstance(o, rdflib.Literal):
            data_properties.add(p)

    parse_items(graph, data_properties, parsers[3][1], parsers[3][2])

def data_value(graph, reasoner, individual, role, literal):
    for cls in {OWL.Class, RDFS.Class, OWL.Restriction, OWL.AllDisjointClasses, RDF.Property, OWL.ObjectProperty, OWL.DatatypeProperty}:
        if list(find_triples((individual, RDF.type, cls), graph)):
            return

    individual = reasoner.individual(individual)

    if not isinstance(literal, rdflib.Literal):
        reasoner.value_of_str(individual, role, '"' + str(literal) + '"')
    else:
        reasoner.value_of(individual, role, data_value_of(reasoner, literal))

def set_data_range(graph, reasoner, role, term):
    data_range = parse_data_range(graph, reasoner, term)
    if data_range is not None:
        reasoner.set_d_range(role, data_range)

def parse_data_range(graph, reasoner, term):
    """
    Translate an OWL 2 data range into a reasoner data range expression.

    Named datatypes, datatype restrictions (`owl:onDatatype` with
    `owl:withRestrictions`), enumerations (`owl:oneOf`), complements
    (`owl:datatypeComplementOf`) and intersections/unions of the above are
    supported.  `None` is returned for the top generic datatype, which is
    left implicit to avoid (pseudo-)inconsistency, and for a data range which
    cannot be translated.

    :param graph: RDFLib graph with the ontology data.
    :param reasoner: Reasoner object.
    :param term: RDF term denoting the data range.
    """
    if isinstance(term, rdflib.Literal):
        return reasoner.data_one_of(data_value_of(reasoner, term))

    if not isinstance(term, rdflib.BNode):
        if term == RDFS.Literal:
            return None
        return datatype_of(reasoner, term)

    fetch = partial(fetch_object, graph, term, f=lambda v: v)

    on_datatype = fetch(pred=OWL.onDatatype)
    if on_datatype is not None:
        base = parse_data_range(graph, reasoner, on_datatype)
        facets = fetch(pred=OWL.withRestrictions)
        if base is None or facets is None:
            return base
        items = [
            facet
            for node in graph.items(facets)
            for facet in parse_facets(graph, reasoner, node)
        ]
        return reasoner.restricted_type(base, items) if items else base

    one_of = fetch(pred=OWL.oneOf)
    if one_of is not None:
        values = [
            data_value_of(reasoner, v) for v in graph.items(one_of)
            if isinstance(v, rdflib.Literal)
        ]
        return reasoner.data_one_of(*values) if values else None

    complement = fetch(pred=OWL.datatypeComplementOf)
    if complement is not None:
        base = parse_data_range(graph, reasoner, complement)
        return None if base is None else reasoner.data_not(base)

    for pred, op in ((OWL.intersectionOf, reasoner.data_and), (OWL.unionOf, reasoner.data_or)):
        items = fetch(pred=pred)
        if items is not None:
            ranges = [parse_data_range(graph, reasoner, v) for v in graph.items(items)]
            ranges = [r for r in ranges if r is not None]
            return op(*ranges) if ranges else None

    logger.debug('data range not translated: {}'.format(term))
    return None

FACET_METHOD = {
    XSD.minInclusive: 'facet_min_inclusive',
    XSD.minExclusive: 'facet_min_exclusive',
    XSD.maxInclusive: 'facet_max_inclusive',
    XSD.maxExclusive: 'facet_max_exclusive',
}

def parse_facets(graph, reasoner, node):
    """
    Translate the facets asserted on a single node of an
    `owl:withRestrictions` list.

    Facets which FaCT++ does not support, for example `xsd:pattern` or the
    length facets, are skipped with a warning, as skipping them weakens the
    data range instead of making it wrong.
    """
    for pred, value in graph.predicate_objects(node):
        method = FACET_METHOD.get(pred)
        if method is None:
            logger.warning('facet not supported: {}'.format(pred))
        else:
            yield getattr(reasoner, method)(data_value_of(reasoner, value))

def datatype_of(reasoner, datatype):
    """
    Translate an XSD datatype into a reasoner datatype.

    The XSD datatypes matching a basic datatype of the reasoner are mapped
    onto it, so that the values of a data property are interpreted as
    numbers, strings or booleans.  Any other datatype is used by its name,
    which leaves its values uninterpreted; the reasoner does not check them,
    but it does not report a clash where there is none either.

    :param reasoner: Reasoner object.
    :param datatype: Datatype IRI or null for an untyped literal.
    """
    if datatype in INT_TYPE:
        return reasoner.type_int
    elif datatype in FLOAT_TYPE:
        return reasoner.type_float
    elif datatype == XSD.boolean:
        return reasoner.type_bool
    elif datatype in (None, XSD.string):
        # a literal without a datatype is a string, see RDF 1.1
        return reasoner.type_str
    else:
        return reasoner.data_type(str(datatype))

def data_value_of(reasoner, literal):
    """
    Translate an RDF literal into a reasoner data value, mapping the XSD
    datatype onto one of the FaCT++ basic datatypes.

    :param reasoner: Reasoner object.
    :param literal: RDFLib literal.
    """
    datatype = getattr(literal, 'datatype', None)
    language = getattr(literal, 'language', None)

    if datatype in INT_TYPE:
        value = int(literal)
    elif datatype in FLOAT_TYPE:
        value = float(literal)
    elif datatype == XSD.boolean:
        value = bool(literal)
    elif language is not None:
        # the string values of the reasoner are read back with the N3 parser,
        # see coras.query; the N3 form keeps the language tag significant
        value = literal.n3()
    elif datatype in (None, XSD.string):
        # the N3 form of the plain literal, so that a string is the same value
        # whether it is typed with xsd:string or untyped, see RDF 1.1
        value = rdflib.Literal(str(literal)).n3()
    else:
        # a value of an uninterpreted datatype is read back with its datatype,
        # so it is stored in its lexical form
        value = str(literal)

    return reasoner.data_value(value, datatype_of(reasoner, datatype))

def set_o_sub_role(reasoner, sub_role, super_role):
    if super_role != rdflib.URIRef('http://www.w3.org/2002/07/owl#topObjectProperty'):
        reasoner.implies_o_roles(sub_role, reasoner.object_role(super_role))

def set_d_sub_role(reasoner, sub_role, super_role):
    if super_role != rdflib.URIRef('http://www.w3.org/2002/07/owl#topDataProperty'):
        reasoner.implies_d_roles(sub_role, reasoner.data_role(super_role))

def create_parsers(graph, reasoner):
    sq = lambda pred: MetaAttrQuery(pred, None)
    tq = lambda obj: MetaAttrQuery(RDF.type, obj)

    top = reasoner.concept_top()
    thing = reasoner.concept(OWL.Thing)
    ni = reasoner.concept(OWL.NamedIndividual)
    reasoner.equal_concepts(top, thing)
    reasoner.equal_concepts(top, ni)

    p_list_cls = partial(parse_list, graph, reasoner.concept)
    p_list_obj = partial(parse_list, graph, reasoner.individual)
    p_list_obj_prop = partial(parse_list, graph, reasoner.object_role)

    p_diff_classes = partial(parse_members, reasoner.disjoint_concepts, reasoner.concept)
    p_diff_individuals = partial(parse_members, reasoner.different_individuals, reasoner.individual)

    p_union_of = partial(parse_union_of, reasoner)
    p_one_of = partial(parse_one_of, reasoner)
    p_intersection = partial(parse_intersection, reasoner)
    p_distinct_members = partial(parse_distinct_members, reasoner)
    p_complement_of = partial(parse_complement_of, reasoner)
    p_all_different = partial(p_diff_individuals, graph, reasoner)
    p_disjoint_cls = partial(p_diff_classes, graph, reasoner)
    p_restriction = partial(parse_restriction, graph, reasoner)

    p_property_chain = partial(parse_property_chain, reasoner)
    p_negative_assert_obj_property = partial(parse_negative_assert_obj_property, graph, reasoner)

    cls_meta = Meta(
        (
            (sq(RDFS.subClassOf), reasoner.implies_concepts, reasoner.concept),
            (sq(OWL.disjointWith), reasoner.disjoint_concepts, reasoner.concept),
            (tq(OWL.AllDisjointClasses), p_disjoint_cls, None),
            (sq(OWL.equivalentClass), reasoner.equal_concepts, reasoner.concept),
            (sq(OWL.unionOf), p_union_of, p_list_cls),
            (sq(OWL.oneOf), p_one_of, p_list_obj),
            (sq(OWL.intersectionOf), p_intersection, p_list_cls),
            (sq(OWL.distinctMembers), p_distinct_members, p_list_obj),
            (sq(OWL.complementOf), p_complement_of, reasoner.concept),
            (tq(OWL.AllDifferent), p_all_different, None),
            (tq(OWL.Restriction), p_restriction, None),
        ),
        (reasoner.instance_of, reasoner.individual),
        [],
    )

    inst_meta = Meta(
        (
            (tq(OWL.Thing), reasoner.instance_of, reasoner.concept),
            (tq(OWL.NamedIndividual), reasoner.instance_of, reasoner.concept),
        ),
        [],
        [],
    )

    obj_p_meta = Meta(
        (
            (tq(OWL.FunctionalProperty), reasoner.set_o_functional, None),
            (tq(OWL.InverseFunctionalProperty), reasoner.set_inverse_functional, None),
            (tq(OWL.SymmetricProperty), reasoner.set_symmetric, None),
            (tq(OWL.TransitiveProperty), reasoner.set_transitive, None),
            (tq(OWL.IrreflexiveProperty), reasoner.set_irreflexive, None),
            (sq(RDFS.domain), reasoner.set_o_domain, reasoner.concept),
            (sq(RDFS.range), reasoner.set_o_range, reasoner.concept),
            (sq(RDFS.subPropertyOf), lambda sub_role, super_role: set_o_sub_role(reasoner, sub_role, super_role), lambda x: x),
            (sq(OWL.equivalentProperty), reasoner.equal_o_roles, reasoner.object_role),
            (sq(OWL.inverseOf), reasoner.set_inverse_roles, reasoner.object_role),
            (sq(OWL.propertyChainAxiom), p_property_chain, p_list_obj_prop),
        ),
        [],
        (reasoner.related_to, reasoner.individual, reasoner.individual),
    )

    data_p_meta = Meta(
        (
            (tq(OWL.FunctionalProperty), reasoner.set_d_functional, None),
            (sq(RDFS.domain), reasoner.set_d_domain, reasoner.concept),
            (sq(RDFS.range), lambda role, range: set_data_range(graph, reasoner, role, range), lambda term: term),
            (sq(RDFS.subPropertyOf), lambda sub_role, super_role: set_d_sub_role(reasoner, sub_role, super_role), lambda x: x),
            (sq(OWL.equivalentProperty), reasoner.equal_d_roles, reasoner.data_role),
        ),
        [],
        (lambda individual, role, value: data_value(graph, reasoner, individual, role, value), lambda ind: ind, lambda obj: obj),
    )

    axiom_meta = Meta(
        [
            (tq(OWL.NegativePropertyAssertion), p_negative_assert_obj_property, None),
        ],
        [],
        [],
    )

    ind_axiom_meta = Meta(
        (
            (sq(OWL.differentFrom), reasoner.different_individuals, reasoner.individual),
        ),
        [],
        [],
    )

    parsers = (
        (QUERY_CLASS, reasoner.concept, cls_meta),
        (QUERY_INSTANCES, reasoner.individual, inst_meta),
        (QUERY_OBJ_PROPERTY, reasoner.object_role, obj_p_meta),
        (QUERY_DATA_PROPERTY, reasoner.data_role, data_p_meta),
        (QUERY_AXIOM, lambda v: v, axiom_meta),
        (QUERY_INDIVIDUAL_AXIOM, reasoner.individual, ind_axiom_meta),
    )
    return parsers

def parse_items(graph, items, ctor, meta):
    # declare each item in the reasoner; this might seem unnecessary, but
    # see declaration consistency in OWL 2
    for s in items:
        ctor(s)

    for q, f_meta, f_obj in meta.attr:
        # for each item, set its metadata in the reasoner
        triples = query_subjects(graph, items, q.pred, q.obj)
        parse_meta(triples, f_meta, ctor, f_obj)

    if meta.type:
        # for each item
        #
        #   X -> (X, RDF.type, OWL.Class) -> then parse (a, RDF.type, X)
        parse_type(graph, items, meta.type[0], meta.type[1], ctor)

    if meta.relation:
        # when each item is a predicate
        parse_rel(graph, ctor, items, *meta.relation)

def parse_rel(graph, f_pred, predicates, f_meta, f_sub, f_obj):
    triples = query_predicates(graph, None, predicates, None)
    for s, p, o in triples:
        f_meta(f_sub(s), f_pred(p), f_obj(o))

def parse_type(graph, objects, f_meta, f_sub, f_obj):
    # to avoid classes of an ontology to be instances of RDF/OWL class
    objects = (o for o in objects if o not in CLASS_TYPE)
    triples = query_objects(graph, None, RDF.type, objects)
    parse_meta(triples, f_meta, f_sub, f_obj)

def parse_meta(triples, f_meta, f_sub, f_obj):
    if f_obj:
        parse_meta_binary(triples, f_meta, f_sub, f_obj)
    else:
        parse_meta_unary(triples, f_meta, f_sub)

def parse_meta_unary(triples, f_meta, f_sub):
    for s, _, _ in triples:
        if __debug__:
            logger.debug('parse: {} {}'.format(f_name(f_meta), s))
        f_meta(f_sub(s))

def parse_meta_binary(triples, f_meta, f_sub, f_obj):
    for s, _, o in triples:
        if __debug__:
            logger.debug('parse: {} {} {}'.format(f_name(f_meta), s, o))
        f_meta(f_sub(s), f_obj(o))

def parse_list(graph, f, start):
    return (f(v) for v in graph.items(start))

def parse_union_of(reasoner, cls, items):
    c = reasoner.union(*items)
    reasoner.equal_concepts(cls, c)

def parse_one_of(reasoner, cls, items):
    c = reasoner.one_of(*items)
    reasoner.equal_concepts(cls, c)

def parse_intersection(reasoner, cls, items):
    c = reasoner.intersection(*items)
    reasoner.equal_concepts(cls, c)

def parse_distinct_members(reasoner, cls, items):
    reasoner.different_individuals(*items)

def parse_complement_of(reasoner, cls, other_cls):
    c = reasoner.complement_of(other_cls)
    reasoner.equal_concepts(cls, c)

def parse_members(f_axiom, f_ctor, graph, reasoner, cls):
    from rdflib import BNode
    fetch = partial(fetch_object, graph, BNode(cls.name))

    start = fetch(OWL.members, lambda v: v)
    if start is None:
        start = fetch(OWL.distinctMembers, lambda v: v)
    assert start is not None

    items = parse_list(graph, f_ctor, start)
    f_axiom(*items)

def parse_property_chain(reasoner, prop, items):
    chain = reasoner.compose(*items)
    reasoner.implies_o_roles(chain, prop)

def parse_restriction(graph, reasoner, cls):
    # FIXME: pass bnode directly
    from rdflib import BNode
    b = BNode(cls.name)
    on_property = fetch_object(graph, b, OWL.onProperty, lambda v: v)
    assert on_property is not None

    if is_data_property(graph, b, on_property):
        parse_d_restriction(graph, reasoner, cls, b, reasoner.data_role(on_property))
        return

    prop = reasoner.object_role(on_property)

    inv_prop = fetch_object(graph, BNode(prop.name), OWL.inverseOf, reasoner.object_role)

    if inv_prop is not None:
        reasoner.set_inverse_roles(prop, inv_prop);

    parse_cardinality(graph, reasoner, cls, b, prop)
    parse_q_cardinality(graph, reasoner, cls, b, prop)
    parse_has_value(graph, reasoner, cls, b, prop)
    parse_some_values_from(graph, reasoner, cls, b, prop)
    parse_all_values_from(graph, reasoner, cls, b, prop)

def is_data_property(graph, restriction, prop):
    """
    Determine whether a property restriction restricts a data property.

    The property declaration is used when present.  Otherwise the shape of
    the restriction decides: a literal value, a data range filler or an
    `owl:onDataRange` qualifier all imply a data property.

    :param graph: RDFLib graph with the ontology data.
    :param restriction: Node of the property restriction.
    :param prop: Property being restricted.
    """
    if (prop, RDF.type, OWL.DatatypeProperty) in graph:
        return True
    elif (prop, RDF.type, OWL.ObjectProperty) in graph:
        return False
    elif (restriction, OWL.onDataRange, None) in graph:
        return True

    value = next(graph.objects(restriction, OWL.hasValue), None)
    if isinstance(value, rdflib.Literal):
        return True

    for pred in (OWL.someValuesFrom, OWL.allValuesFrom):
        filler = next(graph.objects(restriction, pred), None)
        if isinstance(filler, rdflib.URIRef):
            if filler.startswith(str(XSD)) or filler == RDFS.Literal:
                return True
        elif isinstance(filler, rdflib.BNode):
            if any(
                (filler, p, None) in graph
                for p in (OWL.onDatatype, OWL.datatypeComplementOf)
            ):
                return True
    return False

D_CARDINALITY_METHOD = (
    (OWL.cardinality, OWL.qualifiedCardinality, 'd_cardinality'),
    (OWL.minCardinality, OWL.minQualifiedCardinality, 'min_d_cardinality'),
    (OWL.maxCardinality, OWL.maxQualifiedCardinality, 'max_d_cardinality'),
)

def parse_d_restriction(graph, reasoner, cls, b, prop):
    """
    Translate a property restriction of a data property, i.e. a data value,
    a data range or a cardinality restriction of the property.

    :param graph: RDFLib graph with the ontology data.
    :param reasoner: Reasoner object.
    :param cls: Concept of the restriction.
    :param b: Node of the property restriction.
    :param prop: Data role being restricted.
    """
    top = reasoner.data_top()

    for pred, q_pred, method in D_CARDINALITY_METHOD:
        card = fetch_object(graph, b, pred, int)
        if card is not None:
            if __debug__:
                logger.debug('data {}: {} {}: {}'.format(pred, cls.name, prop.name, card))
            c = getattr(reasoner, method)(card, prop, top)
            reasoner.equal_concepts(cls, c)

        card = fetch_object(graph, b, q_pred, int)
        if card is not None:
            data_range = fetch_object(graph, b, OWL.onDataRange, lambda v: v)
            d = None if data_range is None else parse_data_range(graph, reasoner, data_range)
            if __debug__:
                logger.debug('data {}: {} {}: {}'.format(q_pred, cls.name, prop.name, card))
            c = getattr(reasoner, method)(card, prop, top if d is None else d)
            reasoner.equal_concepts(cls, c)

    value = fetch_object(graph, b, OWL.hasValue, lambda v: v)
    if isinstance(value, rdflib.Literal):
        if __debug__:
            logger.debug('data has value: {} {}: {}'.format(cls.name, prop.name, value))
        c = reasoner.d_value(prop, data_value_of(reasoner, value))
        reasoner.equal_concepts(cls, c)

    for pred, method in ((OWL.someValuesFrom, 'd_exists'), (OWL.allValuesFrom, 'd_forall')):
        data_range = fetch_object(graph, b, pred, lambda v: v)
        if data_range is not None:
            d = parse_data_range(graph, reasoner, data_range)
            if d is None:
                logger.debug(
                    'data range of {} dropped for {}'.format(pred, cls.name)
                )
            else:
                if __debug__:
                    logger.debug(
                        'data {}: {} {}: {}'.format(pred, cls.name, prop.name, data_range)
                    )
                c = getattr(reasoner, method)(prop, d)
                reasoner.equal_concepts(cls, c)

def parse_negative_assert_obj_property(graph, reasoner, axiom):
    prop = fetch_object(graph, axiom, OWL.assertionProperty, reasoner.object_role)
    i1 = fetch_object(graph, axiom, OWL.sourceIndividual, reasoner.individual)
    i2 = fetch_object(graph, axiom, OWL.targetIndividual, reasoner.individual)
    assert prop is not None
    assert i1 is not None
    assert i2 is not None
    reasoner.related_to_not(i1, prop, i2)

def parse_cardinality(graph, reasoner, cls, b, prop):

    card = fetch_object(graph, b, OWL.cardinality, int)
    if card:
        if __debug__:
            logger.debug(
                'exact cardinality: {} {}: {}'
                .format(cls.name, prop, card)
            )
        c = reasoner.o_cardinality(card, prop, reasoner.concept_top())
        reasoner.equal_concepts(cls, c)

    card = fetch_object(graph, b, OWL.minCardinality, int)
    if card:
        if __debug__:
            logger.debug(
                'min cardinality: {} {}: {}'
                .format(cls.name, prop, card)
            )
        c = reasoner.min_o_cardinality(card, prop, reasoner.concept_top())
        reasoner.equal_concepts(cls, c)

    card = fetch_object(graph, b, OWL.maxCardinality, int)
    if card:
        if __debug__:
            logger.debug(
                'max cardinality: {} {}: {}'
                .format(cls.name, prop, card)
            )
        c = reasoner.max_o_cardinality(card, prop, reasoner.concept_top())
        reasoner.equal_concepts(cls, c)

def parse_q_cardinality(graph, reasoner, cls, b, prop):
    on_cls = fetch_object(graph, b, OWL.onClass, reasoner.concept)

    card = fetch_object(graph, b, OWL.qualifiedCardinality, int)
    if card and on_cls:
        if __debug__:
            logger.debug(
                'exact qual cardinality: {} {}: {} {}'
                .format(cls.name, prop, card, on_cls.name)
            )
        c = reasoner.o_cardinality(card, prop, on_cls)
        reasoner.equal_concepts(cls, c)

    card = fetch_object(graph, b, OWL.minQualifiedCardinality, int)
    if card and on_cls:
        if __debug__:
            logger.debug(
                'min qual cardinality: {} {}: {} {}'
                .format(cls.name, prop, card, on_cls.name)
            )
        c = reasoner.min_o_cardinality(card, prop, on_cls)
        reasoner.equal_concepts(cls, c)

    card = fetch_object(graph, b, OWL.maxQualifiedCardinality, int)
    if card and on_cls:
        if __debug__:
            logger.debug(
                'max qual cardinality: {} {}: {} {}'
                .format(cls.name, prop, card, on_cls.name)
            )
        c = reasoner.max_o_cardinality(card, prop, on_cls)
        reasoner.equal_concepts(cls, c)

def parse_has_value(graph, reasoner, cls, b, prop):
    v_ind = fetch_object(graph, b, OWL.hasValue, lambda x: x)
    if v_ind and isinstance(v_ind, rdflib.URIRef):
        v_ind = reasoner.individual(v_ind)
        if __debug__:
            logger.debug(
                'has value: {} {}: {}'
                .format(cls.name, prop, v_ind.name)
            )
        c = reasoner.o_value(prop, v_ind)
        reasoner.equal_concepts(cls, c)

def parse_some_values_from(graph, reasoner, cls, b, prop):
    v_cls = fetch_object(graph, b, OWL.someValuesFrom, reasoner.concept)
    if v_cls:
        if __debug__:
            logger.debug(
                'some values from: {} {}: {}'
                .format(cls.name, prop, v_cls.name)
            )
        c = reasoner.o_exists(prop, v_cls)
        reasoner.equal_concepts(cls, c)

def parse_all_values_from(graph, reasoner, cls, b, prop):
    v_cls = fetch_object(graph, b, OWL.allValuesFrom, reasoner.concept)
    if v_cls:
        if __debug__:
            logger.debug(
                'all values from: {} {}: {}'
                .format(cls.name, prop, v_cls.name)
            )
        c = reasoner.o_forall(prop, v_cls)
        reasoner.equal_concepts(cls, c)

def fetch_object(graph, sub, pred, f):
    r = next(graph.objects(sub, pred), None)
    return r if r is None else f(r)

@singledispatch
def query(q):
    raise NotImplementedError('Unknown query type: {}'.format(q))

@query.register(list)
def _(items, graph):
    items = flatten(query(q, graph) for q in items)
    yield from set(items)

@query.register(tuple)
def _(q, graph):
    yield from graph.triples(q)

@debug.triples
def find_triples(q, graph):
    return query(q, graph)

@debug.triples
def query_subjects(graph, subjects, pred, obj):
    q = [(s, pred, obj) for s in subjects]
    return query(q, graph)

@debug.triples
def query_predicates(graph, sub, predicates, obj):
    q = [(sub, p, obj) for p in predicates]
    return query(q, graph)

@debug.triples
def query_objects(graph, sub, pred, objects):
    q = [(sub, pred, o) for o in objects]
    return query(q, graph)

def f_name(f):
    name = getattr(f, '__qualname__', None)
    if name is None and hasattr(f, 'func'):
        name = getattr(f.func, '__qualname__', None)
    if name is None:
        name = str(f)
    return name

# vim: sw=4:et:ai
