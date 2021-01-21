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

from .._factpp import Reasoner

def test_diff_concepts():
    reasoner = Reasoner()

    c1 = reasoner.concept('C1')
    c2 = reasoner.concept('C2')
    assert c1 is not c2

def test_same_concepts():
    reasoner = Reasoner()

    c1 = reasoner.concept('C')
    c2 = reasoner.concept('C')
    assert c1 is c2

def test_diff_individuals():
    reasoner = Reasoner()

    i1 = reasoner.individual('i1')
    i2 = reasoner.individual('i2')
    assert i1 is not i2

def test_same_individuals():
    reasoner = Reasoner()

    i1 = reasoner.individual('i')
    i2 = reasoner.individual('i')
    assert i1 is i2

def test_diff_object_roles():
    reasoner = Reasoner()

    r1 = reasoner.object_role('r1')
    r2 = reasoner.object_role('r2')
    assert r1 is not r2

def test_same_object_roles():
    reasoner = Reasoner()

    r1 = reasoner.object_role('r')
    r2 = reasoner.object_role('r')
    assert r1 is r2

def test_diff_data_roles():
    reasoner = Reasoner()

    r1 = reasoner.data_role('r1')
    r2 = reasoner.data_role('r2')
    assert r1 is not r2

def test_same_data_roles():
    reasoner = Reasoner()

    r1 = reasoner.data_role('r')
    r2 = reasoner.data_role('r')
    assert r1 is r2

# vim: sw=4:et:ai
