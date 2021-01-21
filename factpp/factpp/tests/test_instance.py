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

import pytest

@pytest.fixture
def reasoner():
    """
    Get instance of a reasoner.
    """
    return Reasoner()

def test_one_of(reasoner):
    """
    Test `one of` axiom.
    """
    colors = [reasoner.individual(c) for c in ['blue', 'yellow']]

    color = reasoner.concept('Color')
    a_color = reasoner.one_of(*colors)
    reasoner.equal_concepts(color, a_color)

    blue = reasoner.individual('blue')
    assert reasoner.is_instance(blue, color)

# vim: sw=4:et:ai
