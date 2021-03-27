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

from rdflib import Graph, Literal, BNode
from rdflib.namespace import FOAF, RDF, RDFS

import factpp.rdflib_interface

g = Graph(store=factpp.rdflib_interface.Store())
reasoner = g.store._reasoner

p1 = BNode()
p2 = BNode()
p3 = BNode()

for p in [p1, p2, p3]:
    g.add((p, RDF.type, FOAF.Person))

g.add((FOAF.knows, RDFS.domain, FOAF.Person))
g.add((FOAF.knows, RDFS.range, FOAF.Person))

g.add((p1, FOAF.name, Literal('P 1')))
g.add((p2, FOAF.name, Literal('P 2')))
g.add((p3, FOAF.name, Literal('P 3')))
g.add((p1, FOAF.knows, p2))
g.add((p2, FOAF.knows, p3))

result = g.query("""
PREFIX foaf: <http://xmlns.com/foaf/0.1/>
SELECT ?n1 ?n2
WHERE {
    ?p1 foaf:knows ?p2.
    ?p1 foaf:name ?n1.
    ?p2 foaf:name ?n2.
}
""")

for r in result:
    print('result "{}" knows "{}"'.format(r[0], r[1]))


# vim: sw=4:et:ai
