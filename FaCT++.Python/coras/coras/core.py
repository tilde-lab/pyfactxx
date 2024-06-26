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

import os.path
import logging

import pyfactxx
import rdflib

from .parser import parse
from .query import QueryStore

logger = logging.getLogger(__name__)

FILE_FORMAT = {
    'rdf': 'xml',
    'owl': 'xml',
    'n3': 'n3',
    'ttl': 'turtle'
}

class Coras:
    def __init__(self, **kwargs):
        self._reasoner = pyfactxx.Reasoner(**kwargs)
        self._graph = rdflib.ConjunctiveGraph()
        self._parse_graph = rdflib.ConjunctiveGraph()

        store = QueryStore(self._graph, self._reasoner)
        self._query_graph = rdflib.ConjunctiveGraph(store=store)

    @property
    def reasoner(self):
        return self._reasoner

    def load(self, f, format=None):
        """
        Load an ontology from a file.

        :param f: File object with an ontology data.
        :param format: Format of the data.
        """
        
        if format is None:
            format = FILE_FORMAT.get(os.path.splitext(f)[1][1:], 'xml')
            
        self._parse_graph.parse(f, format=format)
        
    def add_graph(self, graph):
        self._parse_graph += graph
        
    def add_triple(self, triple):
        self._parse_graph.add(triple)
        
    def parse(self):
        logger.debug('parse graph')
        parse(self._parse_graph, self._reasoner)
        self._graph += self._parse_graph
        self._parse_graph = rdflib.ConjunctiveGraph()

    def load_and_parse(self, *input):
        for fn in input:
            self.load(fn)
        self.parse()

    def realise(self):
        logger.debug('reasoner classification')
        self._reasoner.classify()
        logger.debug('reasoner realisation')
        self._reasoner.realise()
        logger.debug('reasoner realisation done')

    def query(self, *args, **kw):
        processed = set()

        need_asserted = 'scope' not in kw or kw['scope'] in ('asserted', 'both')
        need_inferred = 'scope' not in kw or kw['scope'] in ('inferred', 'both')

        if 'scope' in kw:
            del kw['scope']

        for item in self._graph.query(*args, **kw):
            processed.add(item)
            if need_asserted:
                yield item

        if need_inferred and self._reasoner.is_realised():
            for item in self._query_graph.query(*args, **kw):
                if item not in processed:
                    yield item

def as_labels(graph, items):
    """
    Replace URI values in a query result with RDF labels.

    :param graph: RDFLib graph having label information.
    :param items: Query results to be processed.
    """
    items = (tuple(as_label(graph, v) for v in row) for row in items)
    yield from items

def as_label(graph, value):
    """
    Replace URI value with a RDF label.

    :param graph: RDFLib graph having label information.
    :param value: URI value to be replaced.
    """
    if isinstance(value, rdflib.Literal):
        label = value
    else:
        labels = graph.objects(rdflib.URIRef(value), rdflib.RDFS.label)
        label = next(labels, value)
    return label


# vim: sw=4:et:ai
