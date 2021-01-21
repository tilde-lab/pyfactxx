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
import time

reasoner = factpp.Reasoner()

start = time.time()
individuals = [
    reasoner.individual('i-{}'.format(i))
    for i in range(10 ** 4)
]
role = reasoner.object_role('R')

reasoner.set_symmetric(role)
reasoner.set_transitive(role)

items = [iter(individuals)] * 4
for i1, i2, i3, i4 in zip(*items):
    reasoner.related_to(i1, role, i2)
    reasoner.related_to(i2, role, i3)
    reasoner.related_to(i3, role, i4)

print('setup done within {:.4f}s'.format(time.time() - start))

reasoner.realise()
print('realised after {:.4f}s'.format(time.time() - start))

values = reasoner.get_role_fillers(individuals[-1], role)
for v in values:
    print(v.name)

print('total exec time {:.4f}s'.format(time.time() - start))

# vim: sw=4:et:ai
