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

cls_a = reasoner.concept('CLS-A')
cls_b = reasoner.concept('CLS-B')
reasoner.disjoint_concepts(cls_a, cls_b)

r = reasoner.object_role('R')
reasoner.set_o_domain(r, cls_a)
reasoner.set_o_range(r, cls_b)

c = reasoner.individual('C')
reasoner.instance_of(c, cls_a)

restriction_max_one_cls_b = reasoner.max_o_cardinality(1, r, cls_b)
reasoner.implies_concepts(cls_a, restriction_max_one_cls_b)

d = reasoner.individual('D')
reasoner.instance_of(d, cls_b)
reasoner.related_to(c, r, d)
print('consistent after 1st instance:', reasoner.is_consistent())

# add another individual to class C, making ontology inconsistent
x = reasoner.individual('X')
reasoner.instance_of(x, cls_b)
reasoner.related_to(c, r, x)
reasoner.different_individuals(d, x)
print('consistent after 2nd instance:', reasoner.is_consistent())

# vim: sw=4:et:ai
