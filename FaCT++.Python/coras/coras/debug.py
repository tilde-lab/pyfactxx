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

import os
import functools
import itertools
import logging
from copy import deepcopy
from rdflib.namespace import DC, RDF, RDFS, OWL, Namespace

logger = logging.getLogger(__name__)

DEBUG_ENABLED = int(os.environ.get('CORAS_PARSER_DEBUG', 0))

VS = Namespace('http://www.w3.org/2003/06/sw-vocab-status/ns#')
META = {
    DC.description,
    DC.title,
    (RDF.type, RDF.Property),
    RDFS.comment,
    RDFS.label,
    RDFS.isDefinedBy,
    OWL.versionInfo,
    (RDF.type, OWL.AnnotationProperty),
    (RDF.type, OWL.Ontology),
    VS.term_status,
}

def register(f):
    @functools.wraps(f)
    def func(graph, *args):
        start_debug(graph)
        result = f(graph, *args)
        end_debug()
        return result
    return func

def start_debug(graph):
    global GRAPH
    GRAPH = deepcopy(graph)

def triples(f):
    global GRAPH

    @functools.wraps(f)
    def func(*args):
        triples = f(*args)

        triples, to_remove = itertools.tee(triples, 2)
        for t in to_remove:
            GRAPH.remove(t)

        return triples

    return func

def end_debug():
    global GRAPH

    for s, p, o in GRAPH:
        prefix = 'meta' if p in META or (p, o) in META else 'unsupported'
        logger.debug('{}: {}, {}, {}'.format(prefix, s, p, o))
    del GRAPH

# vim: sw=4:et:ai
