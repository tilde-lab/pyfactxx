#
# pyfactxx - Python interface to FaCT++ reasoner
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

"""
Tests of parsing OWL 2 data ranges, data property restrictions and
individual inequality from RDF data.
"""

import io

import pytest

from pyfactxx import coras

PREFIX = """
@prefix owl: <http://www.w3.org/2002/07/owl#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix : <http://example.org/> .
"""

# data property with a range restricted to xsd:integer[>= 1, <= 230]
FACET_RANGE = PREFIX + """
:number a owl:DatatypeProperty ;
    rdfs:range [
        a rdfs:Datatype ;
        owl:onDatatype xsd:integer ;
        owl:withRestrictions (
            [ xsd:minInclusive "1"^^xsd:integer ]
            [ xsd:maxInclusive "230"^^xsd:integer ]
        )
    ] .
"""

# class of entries having a specific value of a data property
HAS_VALUE = PREFIX + """
:system a owl:DatatypeProperty .

:Cubic a owl:Class ;
    owl:equivalentClass [
        a owl:Restriction ;
        owl:onProperty :system ;
        owl:hasValue "cubic"^^xsd:string
    ] .
"""

# class of entries having a data property value within a data range
SOME_VALUES_FROM = PREFIX + """
:number a owl:DatatypeProperty .

:Large a owl:Class ;
    owl:equivalentClass [
        a owl:Restriction ;
        owl:onProperty :number ;
        owl:someValuesFrom [
            a rdfs:Datatype ;
            owl:onDatatype xsd:integer ;
            owl:withRestrictions ( [ xsd:minInclusive "100"^^xsd:integer ] )
        ]
    ] .
"""


def parse(*data):
    """
    Parse ontology data in the Turtle format and return the Coras object.
    """
    crs = coras.Coras()
    for item in data:
        crs.load(io.StringIO(item), format='turtle')
    crs.parse()
    return crs


def test_facet_range_within_bounds():
    """ Test data property value within the bounds of its range. """
    crs = parse(FACET_RANGE, PREFIX + ':e1 :number "100"^^xsd:integer .')
    assert crs.reasoner.is_consistent()


def test_facet_range_out_of_bounds():
    """ Test data property value outside the bounds of its range. """
    crs = parse(FACET_RANGE, PREFIX + ':e1 :number "300"^^xsd:integer .')
    assert not crs.reasoner.is_consistent()


def test_facet_range_not_supported():
    """ Test data property range with an unsupported facet. """
    data = PREFIX + """
    :label a owl:DatatypeProperty ;
        rdfs:range [
            a rdfs:Datatype ;
            owl:onDatatype xsd:string ;
            owl:withRestrictions ( [ xsd:minLength "2"^^xsd:integer ] )
        ] .
    :e1 :label "a"^^xsd:string .
    """
    # the unsupported facet is skipped, which weakens the data range
    crs = parse(data)
    assert crs.reasoner.is_consistent()


def test_named_datatype_range():
    """ Test data property with a range of a named XSD datatype. """
    data = PREFIX + """
    :value a owl:DatatypeProperty ; rdfs:range xsd:double .
    :e1 :value "225.0"^^xsd:double .
    """
    # the range is a number, so a numeric value is within it
    crs = parse(data)
    assert crs.reasoner.is_consistent()


def test_named_datatype_range_clash():
    """ Test data property value of a datatype outside the property range. """
    data = PREFIX + """
    :value a owl:DatatypeProperty ; rdfs:range xsd:double .
    :e1 :value "cubic"^^xsd:string .
    """
    crs = parse(data)
    assert not crs.reasoner.is_consistent()


def test_unsupported_datatype_range():
    """ Test data property with a range of an uninterpreted datatype. """
    data = PREFIX + """
    :when a owl:DatatypeProperty ; rdfs:range xsd:date .
    :e1 :when "2018-12-31"^^xsd:date .
    :e2 :when "cubic"^^xsd:string .
    """
    # a datatype which the reasoner does not interpret is used by its name
    # and its values are not checked, but neither is a clash reported where
    # there is none
    crs = parse(data)
    assert crs.reasoner.is_consistent()


def test_data_has_value():
    """ Test class equivalent to a data property value restriction. """
    crs = parse(HAS_VALUE, PREFIX + ':e1 :system "cubic"^^xsd:string .')
    reasoner = crs.reasoner
    i = reasoner.individual('http://example.org/e1')
    cls = reasoner.concept('http://example.org/Cubic')

    assert reasoner.is_instance(i, cls)


def test_data_some_values_from():
    """ Test class equivalent to a data range restriction. """
    crs = parse(SOME_VALUES_FROM, PREFIX + ':e1 :number "150"^^xsd:integer .')
    reasoner = crs.reasoner
    i = reasoner.individual('http://example.org/e1')
    cls = reasoner.concept('http://example.org/Large')

    assert reasoner.is_instance(i, cls)


def test_data_some_values_from_out_of_range():
    """ Test data property value outside a data range restriction. """
    crs = parse(SOME_VALUES_FROM, PREFIX + ':e1 :number "10"^^xsd:integer .')
    reasoner = crs.reasoner
    i = reasoner.individual('http://example.org/e1')
    cls = reasoner.concept('http://example.org/Large')

    assert not reasoner.is_instance(i, cls)


def test_data_all_values_from():
    """ Test universal data range restriction of a data property. """
    data = PREFIX + """
    :number a owl:DatatypeProperty .
    :Small a owl:Class ;
        rdfs:subClassOf [
            a owl:Restriction ;
            owl:onProperty :number ;
            owl:allValuesFrom [
                a rdfs:Datatype ;
                owl:onDatatype xsd:integer ;
                owl:withRestrictions ( [ xsd:maxInclusive "10"^^xsd:integer ] )
            ]
        ] .
    :e1 a :Small ; :number "50"^^xsd:integer .
    """
    crs = parse(data)
    assert not crs.reasoner.is_consistent()


def test_data_max_cardinality():
    """ Test maximum cardinality restriction of a data property. """
    data = PREFIX + """
    :number a owl:DatatypeProperty .
    :Entry a owl:Class ;
        rdfs:subClassOf [
            a owl:Restriction ;
            owl:onProperty :number ;
            owl:maxCardinality "1"^^xsd:nonNegativeInteger
        ] .
    :e1 a :Entry ; :number "1"^^xsd:integer, "2"^^xsd:integer .
    """
    crs = parse(data)
    assert not crs.reasoner.is_consistent()


MAX_CARDINALITY = PREFIX + """
:has a owl:ObjectProperty .

:Entry a owl:Class ;
    rdfs:subClassOf [
        a owl:Restriction ;
        owl:onProperty :has ;
        owl:maxCardinality "1"^^xsd:nonNegativeInteger
    ] .

:e1 a :Entry ; :has :v1, :v2 .
"""

def test_different_from():
    """
    Test individual inequality asserted with owl:differentFrom against a
    maximum cardinality restriction.
    """
    data = PREFIX + ':v1 owl:differentFrom :v2 .'
    crs = parse(MAX_CARDINALITY, data)
    assert not crs.reasoner.is_consistent()

def test_different_from_absent():
    """
    Test maximum cardinality restriction without individual inequality.
    """
    # without the inequality the two values can be the same individual
    crs = parse(MAX_CARDINALITY)
    assert crs.reasoner.is_consistent()


def test_different_from_functional_role():
    """ Test individual inequality against a functional object property. """
    data = PREFIX + """
    :has a owl:FunctionalProperty, owl:ObjectProperty .
    :e1 :has :v1, :v2 .
    :v1 owl:differentFrom :v2 .
    """
    crs = parse(data)
    assert not crs.reasoner.is_consistent()

# vim: sw=4:et:ai
