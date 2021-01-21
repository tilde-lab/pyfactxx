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

from factpp import Reasoner

def test_create_data_role():
    """
    Test creating data role.
    """
    reasoner = Reasoner()

    r = reasoner.data_role('R')
    assert 'R' == r.name

def test_equivalent_roles():
    """
    Test creating equivalent data roles.
    """
    reasoner = Reasoner()

    r1 = reasoner.data_role('R1')
    cls = reasoner.concept('CLS')
    reasoner.set_d_domain(r1, cls)

    r2 = reasoner.data_role('R2')
    reasoner.equal_d_roles(r1, r2)

    # precondition
    assert r1 != r2

    # r1 and r2 are equivalent, so they have the same domain
    values = reasoner.get_d_domain(r2)
    assert 'CLS' == next(values).name
    assert next(values, None) is None

def test_sub_property():
    """
    Test creating sub-property of a data property.
    """
    reasoner = Reasoner()

    r = reasoner.data_role('R')
    sub_r = reasoner.data_role('SR')
    reasoner.implies_d_roles(sub_r, r)
    assert reasoner.is_sub_d_role(sub_r, r)

def test_get_d_domain():
    """
    Test getting data role domain.
    """
    reasoner = Reasoner()

    cls = reasoner.concept('CLS')
    r = reasoner.data_role('R')
    reasoner.set_d_domain(r, cls)

    values = reasoner.get_d_domain(r)
    assert 'CLS' == next(values).name
    assert next(values, None) is None

def test_get_d_domain_top():
    """
    Test getting data role domain when no domain set.
    """
    reasoner = Reasoner()
    top = reasoner.concept_top()
    r = reasoner.data_role('R')
    i = reasoner.individual('i')
    reasoner.value_of_int(i, r, 1)

    values = reasoner.get_d_domain(r)
    assert [top] == list(values)

# vim: sw=4:et:ai
