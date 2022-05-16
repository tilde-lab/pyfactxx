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

import pyfactxx

reasoner = pyfactxx.Reasoner()

top_data = reasoner.data_top()

cls = reasoner.concept('A')

role = reasoner.data_role('R')
reasoner.set_d_domain(role, cls)
reasoner.set_d_range(role, reasoner.type_float)

rt = reasoner.d_cardinality(1, role, top_data)
reasoner.implies_concepts(cls, rt)

# a is instance of A due to use of R
a = reasoner.individual('a')
reasoner.value_of_float(a, role, 1)

print('a added, consistent:', reasoner.is_consistent())
print('a instance of A:', reasoner.is_instance(a, cls))

# vim: sw=4:et:ai

