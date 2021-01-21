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

import factpp

reasoner = factpp.Reasoner()

classes = [
    reasoner.concept('A'),
    reasoner.concept('B'),
    reasoner.concept('C'),
]

reasoner.disjoint_concepts(classes)

a = reasoner.individual('a')
b = reasoner.individual('b')
c = reasoner.individual('c')
reasoner.instance_of(a, classes[0])

print('a is A, consistency:', reasoner.is_consistent())

# inconsistent, b is both instance of class B and C, but B and C are
# disjoint
reasoner.instance_of(b, classes[1])
reasoner.instance_of(b, classes[2])
print('b is B and b is C, consistency:', reasoner.is_consistent())

# vim: sw=4:et:ai
