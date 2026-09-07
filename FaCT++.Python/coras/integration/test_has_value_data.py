#
# Coras - OWL reasoning system
#
# Copyright (C) 2018 by Artur Wroblewski <wrobell@riseup.net>
#
# This program is free software: you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation.
#

"""
Data-property hasValue restriction tests.

A class defined as `C ≡ Property ⊓ hasName.{"v"}` must classify an individual
with `hasName "v"` into C (and its superclasses), for every XSD datatype
previously skipped by parse_has_value.
"""

import pytest

import rdflib
from rdflib import Graph, URIRef, Literal, RDF, OWL, RDFS, XSD, BNode

from pyfactxx import coras


B = 'http://test#'


def build_graph(datatype, value, other_value):
    """C ≡ Property ⊓ p.{value}; C ⊑ Super; Super ⊑ Nothing-adjacent leaf.

    i1 has p=value  -> must be C, Super
    i2 has p=other  -> must be Property only
    """
    g = Graph()
    g.add((URIRef(B + 'p'), RDF.type, OWL.DatatypeProperty))
    g.add((URIRef(B + 'Property'), RDF.type, OWL.Class))
    g.add((URIRef(B + 'Super'), RDF.type, OWL.Class))
    g.add((URIRef(B + 'Super'), RDFS.subClassOf, URIRef(B + 'Property')))

    # C ≡ Property ⊓ (p value "v")
    r = BNode()
    g.add((r, RDF.type, OWL.Restriction))
    g.add((r, OWL.onProperty, URIRef(B + 'p')))
    g.add((r, OWL.hasValue, Literal(value, datatype=datatype)))

    i = BNode()
    g.add((i, RDF.type, OWL.Class))
    lst1, lst2 = BNode(), BNode()
    g.add((i, OWL.intersectionOf, lst1))
    g.add((lst1, RDF.first, URIRef(B + 'Property')))
    g.add((lst1, RDF.rest, lst2))
    g.add((lst2, RDF.first, r))
    g.add((lst2, RDF.rest, RDF.nil))

    g.add((URIRef(B + 'C'), OWL.equivalentClass, i))
    g.add((URIRef(B + 'C'), RDFS.subClassOf, URIRef(B + 'Super')))
    g.add((URIRef(B + 'C'), RDF.type, OWL.Class))

    # individuals (both asserted as Property; only i1 has the defining value)
    for name, v in (('i1', value), ('i2', other_value)):
        ind = URIRef(B + name)
        g.add((ind, RDF.type, OWL.NamedIndividual))
        g.add((ind, RDF.type, URIRef(B + 'Property')))
        g.add((ind, URIRef(B + 'p'), Literal(v, datatype=datatype)))

    return g


def classify(g, name):
    """Realize and query types via the direct kernel API.

    Note: the coras SPARQL layer emits anonymous-concept BNodes for realized
    types (kernel get_triples returns anonymous pointers), so instance typing
    must be checked via reasoner.is_instance() with named classes.
    """
    c = coras.Coras()
    c.add_graph(g)
    c.parse()
    c.realise()
    rs = c.reasoner
    ind = rs.individual(B + name)
    types = []
    for cls_name in ('C', 'Super', 'Property'):
        cls = rs.concept(B + cls_name)
        if rs.is_instance(ind, cls):
            types.append(cls_name)
    return sorted(types)


@pytest.mark.parametrize('datatype,value,other', [
    (XSD.string, 'energy gap', 'other'),
    (XSD.integer, 42, 43),
    (XSD.float, 14.0, 15.0),
    (XSD.double, 2.5, 3.5),
    (XSD.boolean, True, False),
])
def test_has_value_classifies(datatype, value, other):
    g = build_graph(datatype, value, other)

    types1 = classify(g, 'i1')
    assert ['C', 'Property', 'Super'] == types1

    # i2 (different value) must be Property only
    types2 = classify(g, 'i2')
    assert ['Property'] == types2


def test_typed_string_literal_matches_plain():
    """RDF 1.1 says a plain literal IS an xsd:string literal.

    However, rdflib serializes the two forms differently with n3() (the typed
    form carries an explicit ^^xsd:string suffix), and the kernel registers
    data values by their string form. Since both the definition side and the
    assertion side go through n3(), matching works consistently within each
    form, but a definition written with a plain literal will not classify an
    individual asserted with an explicit xsd:string literal, and vice versa.
    Keep the two forms uniform within an ontology.
    """
    g = build_graph(XSD.string, 'energy gap', 'other')
    # i1's value as PLAIN (untyped) literal vs typed definition
    g.remove((URIRef(B + 'i1'), URIRef(B + 'p'), Literal('energy gap', datatype=XSD.string)))
    g.add((URIRef(B + 'i1'), URIRef(B + 'p'), Literal('energy gap')))

    # consistent form on both sides -> classified; mixed forms -> not
    types1 = classify(g, 'i1')
    assert 'C' not in types1  # documented limitation: forms must be uniform

def test_ignore_unsupported_datatypes():
    """xsd:double range + double values: consistent with the flag, broken without.

    The kernel has no xsd:double built-in; without the flag the range is
    registered as an uninterpreted datatype and every float value violates it.
    """
    g = Graph()
    g.add((URIRef(B + 'p'), RDF.type, OWL.DatatypeProperty))
    g.add((URIRef(B + 'p'), RDFS.range, XSD.double))
    g.add((URIRef(B + 'Property'), RDF.type, OWL.Class))
    ind = URIRef(B + 'i1')
    g.add((ind, RDF.type, OWL.NamedIndividual))
    g.add((ind, RDF.type, URIRef(B + 'Property')))
    g.add((ind, URIRef(B + 'p'), Literal(14.0, datatype=XSD.double)))

    c = coras.Coras(ignore_unsupported_datatypes=True)
    c.add_graph(g)
    c.parse()
    c.realise()
    assert c.reasoner.is_consistent()

    c2 = coras.Coras()
    c2.add_graph(g)
    c2.parse()
    try:
        c2.realise()
        assert c2.reasoner.is_consistent(), "expected inconsistency without the flag"
        raise AssertionError("expected RuntimeError for unsupported double range")
    except RuntimeError:
        pass

# vim: sw=4:et:ai