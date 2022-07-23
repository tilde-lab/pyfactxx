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
Query engine.
"""

import re
import logging

import rdflib.store
from pyfactxx.lib_factxx import ObjectRoleExpr, DataRoleExpr
from rdflib import RDF, OWL

from .util import dispatch

logger = logging.getLogger(__name__)

class QueryStore(rdflib.store.Store):
    def __init__(self, tgraph, reasoner):
        self._triples_graph = tgraph
        self._reasoner = reasoner
        self._uri_pattern = re.compile(r'\w+://')

        # TODO: research the settings below; set all of them to true to
        # allow loading N3 files at the moment
        self.context_aware = True
        self.formula_aware = True

    def get_term(self, node_name, datatype=''):
        if datatype == 'default':
            return rdflib.util.from_n3(node_name)
        elif datatype != '':
            return rdflib.Literal(node_name, datatype=datatype)
        elif self._uri_pattern.match(node_name):
            return rdflib.URIRef(node_name)
        else:
            return rdflib.BNode(node_name)

    def triples(self, pattern, context=None):
        s, p, o = pattern

        #ref_s = None if s is None else self._reasoner.individual(s)
        #ref_p = None if p is None else self._reasoner.object_role(p)
        #ref_o = None if o is None else self._reasoner.individual(o)

        ref_s = "" if s is None else s
        ref_p = "" if p is None else p
        ref_o = "" if o is None else o

        for subj, role, obj, datatype in  self._reasoner.get_triples(ref_s, ref_p, ref_o):
            yield ((self.get_term(subj), self.get_term(role), self.get_term(obj, datatype)), context)
            
    def __len__(self, context=None):
        return len(list(self.triples((None, None, None), context)))

    def role_triples_(self, s, ref_p, context):
        ref_s = self._reasoner.individual(s)
        objects = self._reasoner.get_role_fillers(ref_s, ref_p)
        return (((s, None, o), context) for o in objects)

    def role_triples(self, s, p, context):
        ref_p, _, fetch_values = self._property_type(p)
        values = fetch_values(s, p)
        return (((s, p, v), context) for v in values)

    def remove(self, triple, context=None):
        logger.warning('removal of triples not supported yet')

    @dispatch
    def _get_domains(self, p):
        raise NotImplementedError('Unknown type of property: {}'.format(p))

    @_get_domains.register(ObjectRoleExpr)
    def _(self, p):
        yield from self._reasoner.get_o_domain(p)

    @_get_domains.register(DataRoleExpr)
    def _(self, p):
        yield from self._reasoner.get_d_domain(p)

    def _get_instances(self, classes):
        get = self._reasoner.get_instances
        instances = {i for c in classes for i in get(c)}
        yield from (rdflib.URIRef(i.name) for i in instances)

    @dispatch
    def _fetch_objects(self, p, s):
        raise NotImplementedError('Unknown type of property: {}'.format(p))

    @_fetch_objects.register(ObjectRoleExpr)
    def _(self, p, s):
        if __debug__:
            logger.debug('fetching objects for ({}, {})'.format(s, p))
        objects = self._reasoner.get_role_fillers(s, p)
        yield from (o.name for o in objects)

    @_fetch_objects.register(DataRoleExpr)
    def _(self, p, s):
        if __debug__:
            logger.debug('fetching objects for ({}, {})'.format(s, p))
        s = rdflib.URIRef(s.name)
        p = rdflib.URIRef(p.name)
        yield from self._triples_graph.objects(s, p)

    def _detect_property(self, p):
        assert p is not None

        reasoner = self._reasoner
        if self._is_datatype_property(p):
            return reasoner.data_role(p)
        elif self._is_object_property(p):
            return reasoner.object_role(p)
        else:
            return None

    def _is_datatype_property(self, p):
        q = (p, RDF.type, OWL.DatatypeProperty)
        return q in self._triples_graph

    def _is_object_property(self, p):
        q = (p, RDF.type, OWL.ObjectProperty)
        return q in self._triples_graph

# vim: sw=4:et:ai
