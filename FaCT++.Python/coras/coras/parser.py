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
from rdflib.namespace import RDF, RDFS, OWL

from . import debug

logger = logging.getLogger(__name__)

CLASS_TYPE = {RDFS.Class, OWL.Class}

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
            (sq(RDFS.subPropertyOf), reasoner.implies_o_roles, reasoner.object_role),
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
            (sq(RDFS.range), reasoner.set_d_range, lambda _: reasoner.type_str),
            (sq(RDFS.subPropertyOf), reasoner.implies_d_roles, reasoner.data_role),
            (sq(OWL.equivalentProperty), reasoner.equal_d_roles, reasoner.data_role),
        ),
        [],
        (reasoner.value_of_str, reasoner.individual, lambda obj: str(obj)),
    )

    axiom_meta = Meta(
        [
            (tq(OWL.NegativePropertyAssertion), p_negative_assert_obj_property, None),
        ],
        [],
        [],
    )

    parsers = (
        (QUERY_CLASS, reasoner.concept, cls_meta),
        (QUERY_INSTANCES, reasoner.individual, inst_meta),
        (QUERY_OBJ_PROPERTY, reasoner.object_role, obj_p_meta),
        (QUERY_DATA_PROPERTY, reasoner.data_role, data_p_meta),
        (QUERY_AXIOM, lambda v: v, axiom_meta),
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
    reasoner.union_of(cls, *items)

def parse_one_of(reasoner, cls, items):
    c = reasoner.one_of(*items)
    reasoner.equal_concepts(cls, c)

def parse_intersection(reasoner, cls, items):
    c = reasoner.intersection(*items)
    reasoner.equal_concepts(cls, c)

def parse_distinct_members(reasoner, cls, items):
    reasoner.different_individuals(*items)

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
    prop = fetch_object(graph, b, OWL.onProperty, reasoner.object_role)
    assert prop is not None

    parse_q_cardinality(graph, reasoner, cls, b, prop)
    parse_some_values_from(graph, reasoner, cls, b, prop)

def parse_negative_assert_obj_property(graph, reasoner, axiom):
    prop = fetch_object(graph, axiom, OWL.assertionProperty, reasoner.object_role)
    i1 = fetch_object(graph, axiom, OWL.sourceIndividual, reasoner.individual)
    i2 = fetch_object(graph, axiom, OWL.targetIndividual, reasoner.individual)
    assert prop is not None
    assert i1 is not None
    assert i2 is not None
    reasoner.related_to_not(i1, prop, i2)

def parse_q_cardinality(graph, reasoner, cls, b, prop):
    on_cls = fetch_object(graph, b, OWL.onClass, reasoner.concept)

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
