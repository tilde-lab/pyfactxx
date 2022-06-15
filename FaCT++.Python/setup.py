#!/usr/bin/env python
#
# pyfactxx - Python interface to FaCT++ reasoner
#
# Copyright (C) 2016-2018 by Artur Wroblewski <wrobell@riseup.net>
# Copyright (C) 2021-2022 by Ivan Rygaev <ir@tilde.pro>
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

import re
import ast
import pathlib
from skbuild import setup


root = pathlib.Path(__file__).parent.resolve()
DEBUG = False

_version_re = re.compile(r'__version__\s+=\s+(.*)')

with open('pyfactxx/__init__.py', 'rb') as f:
    version = str(ast.literal_eval(_version_re.search(
        f.read().decode('utf-8')).group(1)))

config = 'Debug' if DEBUG else 'Release'
cmake_args = [
    f'-DCMAKE_BUILD_TYPE={config}',
    f'-DPYFACTXX_ROOT={root.as_posix()}'
]

setup(
    version=version,
    packages=[
        "pyfactxx",
        "pyfactxx.coras"],
    package_dir={
        "pyfactxx": "pyfactxx", 
        "pyfactxx.coras": "pyfactxx/coras"},
    cmake_source_dir=str(root/'pyfactxx'),
    cmake_args=cmake_args
)
