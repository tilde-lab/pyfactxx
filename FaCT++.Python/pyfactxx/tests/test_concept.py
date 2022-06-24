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


def test_subclass(reasoner):
    """
    Test subclasses.
    """
    cls_a = reasoner.concept('A')
    cls_b = reasoner.concept('B')

    reasoner.implies_concepts(cls_a, cls_b)
    assert reasoner.is_subsumed_by(cls_a, cls_b)


def test_subclass_instances(reasoner):
    """
    Test instances of subclasses.
    """
    child = reasoner.concept('child')
    person = reasoner.concept('person')

    a = reasoner.individual('a')
    reasoner.instance_of(a, child)

    # test precondition
    assert not reasoner.is_instance(a, person)

    reasoner.implies_concepts(child, person)
    assert reasoner.is_instance(a, person)


def test_equal_concepts(reasoner):
    """
    Test equal concepts.
    """
    cls_a = reasoner.concept('A')
    cls_b = reasoner.concept('B')

    a = reasoner.individual('a')
    reasoner.instance_of(a, cls_a)

    reasoner.equal_concepts(cls_a, cls_b)

    assert reasoner.is_instance(a, cls_b)


def test_union_of(reasoner):
    """
    Test union of concepts.
    """
    cls_a = reasoner.concept('A')
    cls_b = reasoner.concept('B')
    cls_c = reasoner.concept('C')

    a = reasoner.individual('a')
    reasoner.instance_of(a, cls_a)

    reasoner.union_of(cls_c, cls_a, cls_b)

    assert not reasoner.is_instance(a, cls_b)
    assert reasoner.is_instance(a, cls_c)

# vim: sw=4:et:ai
