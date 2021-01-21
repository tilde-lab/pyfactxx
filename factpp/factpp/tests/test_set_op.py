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

def test_intersection_subclass():
    """
    Test intersection of classes.

    From OWL 2 primer::

        SubClassOf(
          :Grandfather
          ObjectIntersectionOf(:Man :Parent)
        )
    """
    reasoner = Reasoner()

    cls_p = reasoner.concept('Parent')
    cls_m = reasoner.concept('Man')
    cls_g = reasoner.concept('Grandfather')

    cls = reasoner.intersection(cls_p, cls_m)
    reasoner.implies_concepts(cls_g, cls)

    i = reasoner.individual('John')
    reasoner.instance_of(i, cls_g)
    reasoner.realise()

    assert reasoner.is_instance(i, cls_m)
    assert reasoner.is_instance(i, cls_p)


def test_intersection_eq():
    """
    Test intersection of classes.

    From OWL 2 primer::

        EquivalentClasses(
          :Mother
          ObjectIntersectionOf(:Woman :Parent)
        )
    """
    reasoner = Reasoner()

    cls_p = reasoner.concept('Parent')
    cls_w = reasoner.concept('Woman')
    cls_m = reasoner.concept('Mother')

    cls = reasoner.intersection(cls_p, cls_m)
    reasoner.equal_concepts(cls, cls_m)

    a = reasoner.individual('Alice')
    reasoner.instance_of(a, cls_m)
    reasoner.realise()

    # is a woman, but not a parent yet
    assert not reasoner.is_instance(a, cls_w)

    # becomes a parent, so mother as well
    reasoner.instance_of(a, cls_p)
    reasoner.realise()
    assert reasoner.is_instance(a, cls_m)

# vim: sw=4:et:ai
