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


def test_instances_query(reasoner):
    """Test getting instances"""
    cls = reasoner.concept('CLS')
    for v in 'abc':
        i = reasoner.individual(v)
        reasoner.instance_of(i, cls)

    reasoner.realise()

    names = [i.name for i in reasoner.get_instances(cls)]
    assert ['a', 'b', 'c'] == names


def test_instances_query_from_object_role_domain(reasoner):
    """Test getting instances of a class, which is a domain of an object role.
    """
    cls = reasoner.concept('CLS')
    r = reasoner.object_role('R')
    reasoner.set_o_domain(r, cls)

    for v in 'abc':
        i = reasoner.individual(v)
        reasoner.instance_of(i, cls)

    reasoner.realise()

    # get role domain
    value = next(reasoner.get_o_domain(r))

    # get instances of the domain
    names = [i.name for i in reasoner.get_instances(value)]
    assert ['a', 'b', 'c'] == names


def test_instances_query_of_superclass(reasoner):
    """Test getting instances of a superclass.
    """
    person = reasoner.concept('Person')
    child = reasoner.concept('Child')
    reasoner.implies_concepts(child, person)

    a = reasoner.individual('a')
    reasoner.instance_of(a, child)

    reasoner.realise()
    items = reasoner.get_instances(person)

    assert ['a'] == [i.name for i in items]


def test_instances_query_of_class_hierarchy(reasoner):
    """Test getting instances of a class hierarchy.
    """
    person = reasoner.concept('Person')
    child = reasoner.concept('Child')
    reasoner.implies_concepts(child, person)

    p1 = reasoner.individual('p1')
    p2 = reasoner.individual('p2')
    reasoner.instance_of(p1, person)
    reasoner.instance_of(p2, person)

    c1 = reasoner.individual('c1')
    c2 = reasoner.individual('c2')
    reasoner.instance_of(c1, child)
    reasoner.instance_of(c2, child)

    reasoner.realise()
    items = reasoner.get_instances(person)

    assert ['c1', 'c2', 'p1', 'p2'] == [i.name for i in items]


def test_instances_query_of_class_hierarchy_last_empty(reasoner):
    """Test getting instances of a class hierarchy with last class having no
    instances.
    """
    person = reasoner.concept('Person')
    child = reasoner.concept('Child')
    baby = reasoner.concept('Baby')
    reasoner.implies_concepts(child, person)
    reasoner.implies_concepts(baby, child)

    p1 = reasoner.individual('p1')
    p2 = reasoner.individual('p2')
    reasoner.instance_of(p1, person)
    reasoner.instance_of(p2, person)

    c1 = reasoner.individual('c1')
    c2 = reasoner.individual('c2')
    reasoner.instance_of(c1, child)
    reasoner.instance_of(c2, child)

    reasoner.realise()
    items = reasoner.get_instances(person)

    assert ['c1', 'c2', 'p1', 'p2'] == [i.name for i in items]

# vim: sw=4:et:ai
