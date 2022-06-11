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

import os
import pathlib
from shutil import which
from skbuild import setup
from setuptools import find_namespace_packages, Extension
from setuptools.command.build_ext import build_ext as build_extension
from platform import python_version


root = pathlib.Path(__file__).parent.resolve()
debug = False


class CMakeExtension(Extension, object):

    def __init__(self, name):
        # don't invoke the original build_ext for this special extension
        super(CMakeExtension, self).__init__(name, sources=[])


class build_ext(build_extension, object):

    def run(self):
        # check for cmake
        if which('cmake') is None:
            raise SystemError('CMake is needed to install pyfactxx from source!')
        for ext in self.extensions:
            self.build_cmake(ext)
        super(build_ext, self).run()

    def build_cmake(self, ext):
        build_temp = pathlib.Path(self.build_temp)
        build_temp.mkdir(parents=True, exist_ok=True)
        ext_path = self.get_ext_fullpath(ext.name)
        extdir = pathlib.Path(ext_path)
        extdir.mkdir(parents=True, exist_ok=True)
        # example of cmake args
        config = 'Debug' if self.debug else 'Release'
        cmake_args = [
            f'-DCMAKE_LIBRARY_OUTPUT_DIRECTORY={extdir.parent.absolute().as_posix()}',
            f'-DCMAKE_BUILD_TYPE={config}',
            f'-DPYTHON_VERSION={python_version()}',
            f'-DPYFACTXX_ROOT={root.as_posix()}'
        ]
        # example of build args
        build_args = [
            '--config', config,
            '--', '-j4'
        ]
        os.chdir(str(build_temp))
        self.spawn(['cmake', str(root/'pyfactxx')] + cmake_args)
        if not self.dry_run:
            self.spawn(['cmake', '--build', '.'] + build_args)
        # Troubleshooting: if fail on line above then delete all possible
        # temporary CMake files including "CMakeCache.txt" in top level dir.
        os.chdir(root)

# cythonize pyx file if right version of Cython is found
# pyx_ext = Extension("lib_factxx",
#             sources=["pyfactxx/lib_factxx.pyx"],
#             language="c++",
#             )
# cythonize(pyx_ext, compiler_directives={'language_level' : "3"})

config = 'Debug' if debug else 'Release'
cmake_args = [
    # f'-DCMAKE_LIBRARY_OUTPUT_DIRECTORY={extdir.parent.absolute().as_posix()}',
    f'-DCMAKE_BUILD_TYPE={config}',
    # f'-DPYTHON_VERSION={python_version()}',
    f'-DPYFACTXX_ROOT={root.as_posix()}'
]


setup(
    packages=[
        "pyfactxx", 
        "pyfactxx.coras"],
    package_dir={
        "pyfactxx": "pyfactxx", 
        "pyfactxx.coras": "pyfactxx/coras"},
    cmake_source_dir=str(root/'pyfactxx'),
    cmake_args=cmake_args
    # ext_modules=[CMakeExtension("pyfactxx/lib_factxx"),],
    # cmdclass={
    #     'build_ext': build_ext,
    # }
)
