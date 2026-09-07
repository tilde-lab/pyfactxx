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

"""
Tests of OWL 2 data ranges: datatype restrictions with facets, enumerated
data ranges, boolean combinations of data ranges and the data role
restrictions using them.
"""

import pytest


def bounded_role(reasoner, name='R', low=1, high=230):
    """
    Create a data role with an integer range restricted by an inclusive
    lower and upper bound.
    """
    role = reasoner.data_role(name)
    data_range = reasoner.restricted_type(
        reasoner.type_int,
        [
            reasoner.facet_min_inclusive(reasoner.data_value(low)),
            reasoner.facet_max_inclusive(reasoner.data_value(high)),
        ],
    )
    reasoner.set_d_range(role, data_range)
    return role


def test_data_value_default_type(reasoner):
    """ Test datatype of a data value inferred from the Python value. """
    assert reasoner.data_value(1) is not None
    assert reasoner.data_value(1.5) is not None
    assert reasoner.data_value('cubic') is not None
    assert reasoner.data_value(True) is not None


def test_facet_within_bounds(reasoner):
    """ Test data role value within the bounds of a datatype restriction. """
    role = bounded_role(reasoner)
    i = reasoner.individual('I')
    reasoner.value_of(i, role, reasoner.data_value(100))

    assert reasoner.is_consistent()


def test_facet_out_of_bounds(reasoner):
    """ Test data role value outside the bounds of a datatype restriction. """
    role = bounded_role(reasoner)
    i = reasoner.individual('I')
    reasoner.value_of(i, role, reasoner.data_value(300))

    assert not reasoner.is_consistent()


def test_facet_exclusive_bound(reasoner):
    """ Test data role value at an exclusive bound of a datatype restriction. """
    role = reasoner.data_role('R')
    data_range = reasoner.restricted_type(
        reasoner.type_int, reasoner.facet_min_exclusive(reasoner.data_value(0))
    )
    reasoner.set_d_range(role, data_range)

    i = reasoner.individual('I')
    reasoner.value_of(i, role, reasoner.data_value(0))

    assert not reasoner.is_consistent()


def test_one_of_data_range(reasoner):
    """ Test data role value outside an enumerated data range. """
    role = reasoner.data_role('R')
    reasoner.set_d_range(role, reasoner.data_one_of('cubic', 'hexagonal'))

    i = reasoner.individual('I')
    reasoner.value_of(i, role, reasoner.data_value('trigonal'))

    assert not reasoner.is_consistent()


def test_data_not(reasoner):
    """ Test data role value in a complement of a data range. """
    role = reasoner.data_role('R')
    reasoner.set_d_range(role, reasoner.data_not(reasoner.data_one_of('cubic')))

    i = reasoner.individual('I')
    reasoner.value_of(i, role, reasoner.data_value('cubic'))

    assert not reasoner.is_consistent()


def test_data_or(reasoner):
    """ Test data role value in a union of two datatype restrictions. """
    role = reasoner.data_role('R')
    low = reasoner.restricted_type(
        reasoner.type_int, reasoner.facet_max_exclusive(reasoner.data_value(10))
    )
    high = reasoner.restricted_type(
        reasoner.type_int, reasoner.facet_min_exclusive(reasoner.data_value(100))
    )
    reasoner.set_d_range(role, reasoner.data_or(low, high))

    i = reasoner.individual('I')
    reasoner.value_of(i, role, reasoner.data_value(50))

    assert not reasoner.is_consistent()


def test_data_and(reasoner):
    """ Test data role value in an intersection of two datatype restrictions. """
    role = reasoner.data_role('R')
    low = reasoner.restricted_type(
        reasoner.type_int, reasoner.facet_min_inclusive(reasoner.data_value(1))
    )
    high = reasoner.restricted_type(
        reasoner.type_int, reasoner.facet_max_inclusive(reasoner.data_value(10))
    )
    reasoner.set_d_range(role, reasoner.data_and(low, high))

    i = reasoner.individual('I')
    reasoner.value_of(i, role, reasoner.data_value(5))

    assert reasoner.is_consistent()


def test_bool_value_lexical_form(reasoner):
    """ Test lexical form of a boolean data role value. """
    role = reasoner.data_role('R')
    reasoner.set_d_range(role, reasoner.data_one_of(True))

    i = reasoner.individual('I')
    reasoner.value_of_bool(i, role, False)

    assert not reasoner.is_consistent()


def test_d_value_concept(reasoner):
    """ Test concept of individuals having a data role value. """
    role = reasoner.data_role('R')
    cls = reasoner.concept('CLS')
    reasoner.implies_concepts(
        reasoner.d_value(role, reasoner.data_value('cubic')), cls
    )

    i = reasoner.individual('I')
    reasoner.value_of(i, role, reasoner.data_value('cubic'))

    assert reasoner.is_instance(i, cls)


def test_d_exists(reasoner):
    """ Test existential data role restriction. """
    role = bounded_role(reasoner)
    cls = reasoner.concept('CLS')
    reasoner.implies_concepts(
        reasoner.d_exists(
            role,
            reasoner.restricted_type(
                reasoner.type_int,
                reasoner.facet_min_inclusive(reasoner.data_value(100)),
            ),
        ),
        cls,
    )

    i = reasoner.individual('I')
    reasoner.value_of(i, role, reasoner.data_value(150))

    assert reasoner.is_instance(i, cls)


def test_d_forall(reasoner):
    """ Test universal data role restriction. """
    role = reasoner.data_role('R')
    cls = reasoner.concept('CLS')
    reasoner.implies_concepts(
        cls,
        reasoner.d_forall(
            role,
            reasoner.restricted_type(
                reasoner.type_int,
                reasoner.facet_max_inclusive(reasoner.data_value(10)),
            ),
        ),
    )

    i = reasoner.individual('I')
    reasoner.instance_of(i, cls)
    reasoner.value_of(i, role, reasoner.data_value(50))

    assert not reasoner.is_consistent()


def test_min_d_cardinality(reasoner):
    """ Test minimum cardinality restriction of a data role. """
    role = reasoner.data_role('R')
    reasoner.set_d_functional(role)

    cls = reasoner.concept('CLS')
    reasoner.implies_concepts(
        cls, reasoner.min_d_cardinality(2, role, reasoner.data_top())
    )

    i = reasoner.individual('I')
    reasoner.instance_of(i, cls)

    # a functional data role cannot have two values
    assert not reasoner.is_consistent()

# vim: sw=4:et:ai
