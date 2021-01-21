#!/usr/bin/env python
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


import os
from setuptools import setup, Extension
from setuptools.command.build_ext import build_ext as build_extension
from platform import python_version
from Cython.Build import cythonize
import pathlib

class CMakeExtension(Extension, object):

    def __init__(self, name):
        # don't invoke the original build_ext for this special extension
        super(CMakeExtension, self).__init__(name, sources=[])


class build_ext(build_extension, object):

    def run(self):
        for ext in self.extensions:
            self.build_cmake(ext)
        super(build_ext, self).run()

    def build_cmake(self, ext):
        root = str(pathlib.Path().absolute())
        build_temp = pathlib.Path(self.build_temp)
        build_temp.mkdir(parents=True, exist_ok=True)
        ext_path = self.get_ext_fullpath(ext.name)
        extdir = pathlib.Path(ext_path)
        extdir.mkdir(parents=True, exist_ok=True)
        # example of cmake args
        config = 'Debug' if self.debug else 'Release'
        cmake_args = [
            '-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=' + str(extdir.parent.absolute()),
            '-DCMAKE_BUILD_TYPE=' + config,
            '-DPYTHON_VERSION=' + python_version(),
            '-DFACTPP_ROOT=' + root
        ]
        # example of build args
        build_args = [
            '--config', config,
            '--', '-j4'
        ]
        os.chdir(str(build_temp))
        self.spawn(['cmake', os.path.join(root, 'factpp')] + cmake_args)
        if not self.dry_run:
            self.spawn(['cmake', '--build', '.'] + build_args)
        # Troubleshooting: if fail on line above then delete all possible
        # temporary CMake files including "CMakeCache.txt" in top level dir.
        os.chdir(root)

# cythonize pyx file if right version of Cython is found
pyx_ext = Extension("lib_factpp",
            sources=["factpp/lib_factpp.pyx"],
            language="c++",
            )
cythonize(pyx_ext, compiler_directives={'language_level' : "3"})

setup(
    name="factpp",
    version="0.0.1",
    description="Python bindings to FaCT++ library",
    author="Artur Wroblewski",
    author_email="wrobell@riseup.net",
    url="",
    download_url="",
    packages=["factpp"],
    package_dir={"factpp": "factpp"},
    ext_modules=[CMakeExtension("factpp/lib_factpp"),],
    cmdclass={
        'build_ext': build_ext,
    }
)