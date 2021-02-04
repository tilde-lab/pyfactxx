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
top_data = reasoner.data_top()

cls_a = reasoner.concept('CLS-A')
c = reasoner.individual('C')
reasoner.instance_of(c, cls_a)

r = reasoner.data_role('R')
reasoner.set_d_domain(r, cls_a)
reasoner.set_d_range(r, reasoner.type_int)

restriction_max_one = reasoner.max_d_cardinality(1, r, top_data)
reasoner.implies_concepts(cls_a, restriction_max_one)

reasoner.value_of_int(c, r, 1)
print('consistent after first value:', reasoner.is_consistent())

reasoner.value_of_int(c, r, 2)
print('consistent after 2nd value:', reasoner.is_consistent())

# vim: sw=4:et:ai
