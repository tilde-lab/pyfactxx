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

"""
RDFLib store unit tests utility functions.
"""

from rdflib import Graph
from rdflib.namespace import Namespace

import pyfactxx.rdflib_interface

from unittest import mock

NS = Namespace('http://test.com/ns#')

class MockProxy:
    def __init__(self, reasoner, method):
        self.reasoner = reasoner
        self.method = method
        self.mock = mock.MagicMock()

    def __getattr__(self, attr):
        if attr == self.method:
            return self.mock
        else:
            return getattr(self.reasoner, attr)

def graph(mock=None):
    store = pyfactxx.rdflib_interface.Store()
    if mock:
        store._reasoner = MockProxy(store._reasoner, mock)
        # reset parsers after mock is created
        store._create_parsers()
    g = Graph(store=store)
    return g, g.store._reasoner

# vim: sw=4:et:ai
