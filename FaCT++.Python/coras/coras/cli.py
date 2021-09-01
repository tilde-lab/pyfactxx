#
# Coras - OWL reasoning system
#
# Copyright (C) 2018 by Artur Wroblewski <wrobell@riseup.net>
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

import os.path

FILE_FORMAT = {
    'rdf': 'xml',
    'n3': 'n3',
}

def load_and_parse(crs, *input):
    for fn in input:
        format = FILE_FORMAT.get(os.path.splitext(fn)[1][1:])
        if not format:
            print('coras-load: unknown file format for file {}'.format(fn))
            sys.exit(1)
        crs.load(fn, format=format)

    crs.parse()

# vim: sw=4:et:ai
