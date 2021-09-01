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

import csv
import os.path
from functools import partial

import pytest

import coras
from coras.cli import load_and_parse

TESTS = [
    ('foaf', '01-foaf.sq', ['foaf.rdf', 'tbl-foaf-card.n3'], '01-result.csv'),
    ('foaf', '02-foaf.sq', ['foaf.rdf', 'tbl-foaf-card.n3'], '02-result.csv'),
    ('foaf', '03-foaf.sq', ['foaf.rdf', '03-foaf-ex.n3'], '03-result.csv'),
    ('zebra', '01-zebra.sq', ['zebra.n3'], '01-result.csv'),
    ('zebra', '02-zebra.sq', ['zebra.n3'], '02-result.csv'),
    ('tsars', '01-tsars.sq', ['tsars.rdf'], '01-result.csv'),
    ('tsars', '02-tsars.sq', ['tsars.rdf'], '02-result.csv'),
]

@pytest.mark.parametrize(
    'prefix,query,ontologies,expected',
    TESTS,
)
def test_it(prefix, query, ontologies, expected):
    crs = coras.Coras()

    f_path = partial(os.path.join, 'integration', prefix)
    f_open = lambda fn: open(f_path(fn))

    files = (f_path(fn) for fn in ontologies)
    load_and_parse(crs, *files)
    crs.realise()

    with f_open(expected) as f:
        expected = list(csv.reader(f))

    with f_open(query) as f:
        sq = f.read()

    result = crs.query(sq)
    result = [[str(s) for s in row] for row in result]

    assert sorted(expected) == sorted(result)

# vim: sw=4:et:ai
