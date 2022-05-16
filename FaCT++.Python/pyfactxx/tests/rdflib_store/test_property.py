#
# pyfactxx - Python interface to FaCT++ reasoner
#
# Copyright (C) 2016-2017 by Artur Wroblewski <wrobell@riseup.net>
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

from .util import graph, NS

from rdflib import Literal
from rdflib.namespace import FOAF, RDF, RDFS, OWL

def test_property_domain():
    g, reasoner = graph()

    g.add((FOAF.knows, RDF.type, OWL.ObjectProperty))
    g.add((FOAF.knows, RDFS.domain, FOAF.Person))

    r = reasoner.object_role(FOAF.knows)
    value = next(reasoner.get_o_domain(r))
    assert value.name == str(FOAF.Person)

def test_property_range():
    g, reasoner = graph()

    g.add((FOAF.knows, RDF.type, OWL.ObjectProperty))
    g.add((FOAF.knows, RDFS.range, FOAF.Person))

    r = reasoner.object_role(FOAF.knows)
    value = next(reasoner.get_o_range(r))
    assert value.name == str(FOAF.Person)

def test_new_o_property():
    """
    Test adding new object property.
    """
    g, reasoner = graph()

    g.add((NS.P, RDF.type, OWL.ObjectProperty))
    assert NS.P in g.store._properties

def test_o_sub_property():
    """
    Test adding object sub-property.
    """
    g, reasoner = graph()
    parsers = g.store._parsers

    g.add((NS.P1, RDF.type, OWL.ObjectProperty))
    g.add((NS.P2, RDF.type, OWL.ObjectProperty))
    g.add((NS.P1, RDFS.subPropertyOf, NS.P2))

    r1 = reasoner.object_role(NS.P1)
    r2 = reasoner.object_role(NS.P2)
    assert reasoner.is_sub_o_role(r1, r2)

def test_new_d_property():
    """
    Test adding new data property.
    """
    g, reasoner = graph()
    parsers = g.store._parsers

    g.add((NS.P, RDF.type, OWL.DatatypeProperty))
    assert NS.P in g.store._properties

def test_d_sub_property():
    """
    Test adding data sub-property.
    """
    g, reasoner = graph()
    parsers = g.store._parsers

    g.add((NS.P1, RDF.type, OWL.DatatypeProperty))
    g.add((NS.P2, RDF.type, OWL.DatatypeProperty))
    g.add((NS.P1, RDFS.subPropertyOf, NS.P2))

    r1 = reasoner.data_role(NS.P1)
    r2 = reasoner.data_role(NS.P2)
    assert reasoner.is_sub_d_role(r1, r2)

def test_d_property_range():
    """
    Test setting data property range.
    """
    g, reasoner = graph()
    parsers = g.store._parsers

    g.add((NS.P, RDF.type, OWL.DatatypeProperty))
    g.add((NS.P, RDFS.range, RDFS.Literal))

    assert NS.P in g.store._properties

def test_d_property_set_str():
    """
    Test setting data property string value.
    """
    g, reasoner = graph('value_of_str')

    g.add((NS.P, RDF.type, OWL.DatatypeProperty))
    g.add((NS.P, RDFS.range, RDFS.Literal))
    g.add((NS.O, NS.P, Literal('a-value')))

    i = reasoner.individual(NS.O)
    r = reasoner.data_role(NS.P)
    reasoner.value_of_str.assert_called_once_with(i, r, 'a-value')

def test_inverse_role():
    """
    Test setting OWL inverse role.
    """
    g, reasoner = graph()

    g.add((NS.P1, RDF.type, OWL.ObjectProperty))
    g.add((NS.P2, RDF.type, OWL.ObjectProperty))
    g.add((NS.P2, OWL.inverseOf, NS.P1))

    g.add((NS.C, RDF.type, OWL.Class))
    g.add((NS.A1, RDF.type, NS.C))
    g.add((NS.A2, RDF.type, NS.C))
    g.add((NS.B, RDF.type, NS.C))

    g.add((NS.A1, NS.P1, NS.B))
    g.add((NS.A2, NS.P1, NS.B))

    r_inv = reasoner.object_role(NS.P2)
    i = reasoner.individual(NS.B)
    values = reasoner.get_role_fillers(i, r_inv)
    assert [str(NS.A1), str(NS.A2)] == [i.name for i in values]

def test_equivalent_object_properties():
    """
    Test equivalent object properties.
    """
    g, reasoner = graph('equal_o_roles')

    g.add((NS.P1, RDF.type, OWL.ObjectProperty))
    g.add((NS.P2, RDF.type, OWL.ObjectProperty))
    g.add((NS.P1, OWL.equivalentProperty, NS.P2))

    r1 = reasoner.object_role(NS.P1)
    r2 = reasoner.object_role(NS.P2)
    reasoner.equal_o_roles.assert_called_once_with([r1, r2])

def test_equivalent_data_properties():
    """
    Test equivalent data properties.
    """
    g, reasoner = graph('equal_d_roles')

    g.add((NS.P1, RDF.type, OWL.DatatypeProperty))
    g.add((NS.P2, RDF.type, OWL.DatatypeProperty))
    g.add((NS.P1, OWL.equivalentProperty, NS.P2))

    r1 = reasoner.data_role(NS.P1)
    r2 = reasoner.data_role(NS.P2)
    reasoner.equal_d_roles.assert_called_once_with([r1, r2])

# vim: sw=4:et:ai
