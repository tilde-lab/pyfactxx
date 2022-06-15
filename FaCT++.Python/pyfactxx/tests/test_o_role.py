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

import pytest


def test_equivalent_roles(reasoner):
    """ Test creating equivalent roles. """

    r1 = reasoner.object_role('R1')
    cls = reasoner.concept('CLS')
    reasoner.set_o_domain(r1, cls)

    r2 = reasoner.object_role('R2')
    reasoner.equal_o_roles(r1, r2)

    # precondition
    assert r1 != r2

    # r1 and r2 are equivalent, so they have the same domain
    values = reasoner.get_o_domain(r2)
    assert 'CLS' == next(values).name
    assert next(values, None) is None


def test_sub_property(reasoner):
    """ Test creating sub-property of an object property.
    """
    r = reasoner.object_role('R')
    sub_r = reasoner.object_role('SR')
    reasoner.implies_o_roles(sub_r, r)
    assert reasoner.is_sub_o_role(sub_r, r)


def test_get_o_domain(reasoner):
    """ Test getting object role domain.
    """
    cls = reasoner.concept('CLS')
    r = reasoner.object_role('R')
    reasoner.set_o_domain(r, cls)

    values = reasoner.get_o_domain(r)
    assert 'CLS' == next(values).name
    assert next(values, None) is None


def test_get_o_domain_top(reasoner):
    """ Test getting object role domain when no domain set.
    """
    top = reasoner.concept_top()
    r = reasoner.object_role('R')
    i1 = reasoner.individual('i1')
    i2 = reasoner.individual('i2')
    reasoner.related_to(i1, r, i2)

    values = reasoner.get_o_domain(r)
    assert [top] == list(values)


def test_get_o_domain_complex(reasoner):
    """ Test getting object role domain.

    Example from Tsars ontology.
    """
    cls = reasoner.concept('Person')
    man = reasoner.concept('Man')
    sex = reasoner.concept('Sex')
    de = reasoner.concept('DomainEntity')

    r = reasoner.object_role('hasFather')

    c1 = reasoner.o_exists(r, man)
    c2 = reasoner.o_exists(r, sex)
    reasoner.implies_concepts(cls, c1)
    reasoner.implies_concepts(cls, c2)
    reasoner.implies_concepts(cls, de)

    reasoner.set_o_domain(r, cls)

    values = reasoner.get_o_domain(r)
    assert 'Person' == next(values).name
    assert [de] == list(values)


def test_get_o_range(reasoner):
    """ Test getting object role range. """
    cls = reasoner.concept('CLS')
    r = reasoner.object_role('R')
    reasoner.set_o_range(r, cls)

    values = reasoner.get_o_range(r)
    assert 'CLS' == next(values).name
    assert next(values, None) is None


def test_get_o_domain_range(reasoner):
    """ Test getting object role domain and range.
    """
    r = reasoner.object_role('R')

    c1 = reasoner.concept('C1')
    reasoner.set_o_domain(r, c1)
    c2 = reasoner.concept('C2')
    reasoner.set_o_range(r, c2)

    values = reasoner.get_o_domain(r)
    assert 'C1' == next(values).name
    assert next(values, None) is None

    values = reasoner.get_o_range(r)
    assert 'C2' == next(values).name
    assert next(values, None) is None


@pytest.mark.skip()
def test_inverse_role(reasoner):
    """ Test getting inverse of an object role.
    """
    r = reasoner.object_role('R')

    i1 = reasoner.individual('A1')
    i2 = reasoner.individual('A2')
    i3 = reasoner.individual('B')
    # ([A1, A2], R, B)
    reasoner.related_to(i1, r, i3)
    reasoner.related_to(i2, r, i3)

    r_inv = reasoner.inverse(r)
    values = reasoner.get_role_fillers(i3, r_inv)
    assert ['A1', 'A2'] == [i.name for i in values]


@pytest.mark.skip()
def test_set_inverse_role(reasoner):
    """ Test setting two object roles as inverse.
    """
    r = reasoner.object_role('R1')
    r_inv = reasoner.object_role('R2')

    i1 = reasoner.individual('A1')
    i2 = reasoner.individual('A2')
    i3 = reasoner.individual('B')
    # ([A1, A2], R, B)
    reasoner.related_to(i1, r, i3)
    reasoner.related_to(i2, r, i3)

    reasoner.set_inverse_roles(r, r_inv)

    values = reasoner.get_role_fillers(i3, r_inv)
    assert ['A1', 'A2'] == [i.name for i in values]


def test_relation(reasoner):
    """ Test setting relation between two instances.
    """
    cls = reasoner.concept('CLS')
    r = reasoner.object_role('R')
    reasoner.set_o_functional(r)

    c1 = reasoner.individual('c1')
    c2 = reasoner.individual('c2')

    a_cls = reasoner.one_of(c1, c2)
    reasoner.equal_concepts(cls, a_cls)

    reasoner.related_to(c1, r, c2)

    c3 = reasoner.individual('c3')
    c4 = reasoner.individual('c4')
    reasoner.related_to(c3, r, c4)

    # if c1 == c3, then c2 == c4
    reasoner.same_individuals([c1, c3])
    assert reasoner.is_same_individuals(c2, c4)


def test_negative_relation(reasoner):
    """ Test negative relation. """
    h1 = reasoner.individual('house1')
    h2 = reasoner.individual('house2')
    h3 = reasoner.individual('house3')
    reasoner.different_individuals(h1, h2, h3)

    house = reasoner.concept('House')
    a_house = reasoner.one_of(h1, h2, h3)
    reasoner.equal_concepts(house, a_house)

    is_next_to = reasoner.object_role('is_next_to')
    reasoner.set_symmetric(is_next_to)
    reasoner.set_irreflexive(is_next_to)
    reasoner.set_o_domain(is_next_to, house)
    reasoner.set_o_range(is_next_to, house)

    is_left_to = reasoner.object_role('is_left_to')
    reasoner.implies_o_roles(is_left_to, is_next_to)

    a_house_next = reasoner.max_o_cardinality(2, is_next_to, house)
    reasoner.implies_concepts(house, a_house_next)

    reasoner.related_to(h1, is_left_to, h2)
    reasoner.related_to(h2, is_left_to, h3)
    reasoner.related_to_not(h1, is_next_to, h3)

    some_house = reasoner.individual('some house')
    reasoner.related_to(some_house, is_next_to, h1)

    assert reasoner.is_same_individuals(some_house, h2)


def test_role_chain(reasoner):
    """ Test role chain. """
    father = reasoner.object_role('father')
    grand_father = reasoner.object_role('grand_father')

    c = reasoner.compose(father, father)
    reasoner.implies_o_roles(c, grand_father)

    i1 = reasoner.individual('i1')
    i2 = reasoner.individual('i2')
    i3 = reasoner.individual('i3')

    reasoner.related_to(i1, father, i2)
    reasoner.related_to(i2, father, i3)

    items = reasoner.get_role_fillers(i1, grand_father)
    assert [i3] == list(items)

# vim: sw=4:et:ai
