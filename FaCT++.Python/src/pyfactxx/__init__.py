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

from importlib.metadata import PackageNotFoundError, version

from .coras import Coras
from .lib_factxx import Reasoner  # pylint: disable=no-name-in-module

try:
    # The single source of truth for the package version is
    # pyproject.toml's [project] version; this import reads it from the
    # installed distribution metadata (setuptools/scikit-build-core write
    # it at build time), so the two can never drift apart again.
    __version__ = version("pyfactxx")
except PackageNotFoundError:  # running from a source tree, not installed
    __version__ = "unknown"

# vim: sw=4:et:ai