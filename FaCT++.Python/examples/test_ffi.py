#
# pyfactxx - Python interface to FaCT++ reasoner
#
# Copyright (C) 2016 by Artur Wroblewski <wrobell@riseup.net>
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

from pyfactxx.lib_factxx import ffi, lib

def print_array(data):
    i = 0
    print('[')
    while True:
        v = data[i]
        if v == ffi.NULL:
            break

        s = ffi.string(data[i][0]).decode()
        print('[' + s + ']')
        i += 1
    print(']')

k = lib.fact_reasoning_kernel_new()

c = lib.fact_concept(k, b'C');
d = lib.fact_concept(k, b'D');
r = lib.fact_object_role(k, b'R');

top = lib.fact_top(k)
some = lib.fact_o_exists(k, r, top);
lib.fact_implies_concepts(k, c, some);
lib.fact_implies_concepts(k, some, d);

if lib.fact_is_subsumed_by(k, c, d):
    print('yes!\n')
else:
    print('no...\n')

actor = lib.fact_concept_actor_new();
p_actor = ffi.new('fact_actor **', actor)
lib.fact_get_sup_concepts(k, c, 0, p_actor)
names = lib.fact_get_elements_2d(actor)
print_array(names)
lib.fact_actor_free(actor)

print()

o_top = lib.fact_object_role_top(k)
o_top_c = ffi.cast('fact_role_expression *', o_top)
actor = lib.fact_o_role_actor_new()
p_actor = ffi.new('fact_actor **', actor)
lib.fact_get_sub_roles(k, o_top_c, 0, p_actor)
names = lib.fact_get_elements_2d(actor)
print_array(names)
lib.fact_actor_free(actor)

lib.fact_reasoning_kernel_free(k)

# vim: sw=4:et:ai
