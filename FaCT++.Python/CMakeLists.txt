cmake_minimum_required(VERSION 3.15)

# This directory is intended to be added from the repository root via:
#   add_subdirectory(FaCT++.Python)

set(CMAKE_VERBOSE_MAKEFILE ON)
if (NOT DEFINED CMAKE_CXX_STANDARD)
    set(CMAKE_CXX_STANDARD 11)
endif()

# Repository root (directory that contains Kernel/, FaCT++/, etc.)
set(PYFACTXX_REPO_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/..")

# Source directory that is packaged as the Python package "pyfactxx"
set(PYFACTXX_PY_PKG_DIR "${CMAKE_CURRENT_SOURCE_DIR}/src/pyfactxx")

# Python + helpers (provides python_add_library and Python::Interpreter)
set(Python_FIND_VIRTUALENV FIRST)
find_package(Python COMPONENTS Interpreter Development.Module REQUIRED)

message(STATUS "Python executable: ${Python_EXECUTABLE}")
message(STATUS "Python version: ${Python_VERSION}")

if (NOT TARGET Kernel)
    add_subdirectory("${PYFACTXX_REPO_ROOT}/Kernel" "${CMAKE_CURRENT_BINARY_DIR}/Kernel")
endif()
set_property(TARGET Kernel PROPERTY POSITION_INDEPENDENT_CODE ON)

# FaCT++ sources (compiled into the extension module)
set(FACT_DIR "${PYFACTXX_REPO_ROOT}/FaCT++")
file(GLOB FACT_FILES CONFIGURE_DEPENDS "${FACT_DIR}/*.cpp")

# Cython input and generated C++ output
set(PYFACTXX_PYX "${PYFACTXX_PY_PKG_DIR}/lib_factxx.pyx")
set(PYFACTXX_CPP "${CMAKE_CURRENT_BINARY_DIR}/lib_factxx.cpp")

add_custom_command(
  OUTPUT "${PYFACTXX_CPP}"
  COMMENT "Generating ${PYFACTXX_CPP} from ${PYFACTXX_PYX}"
  COMMAND Python::Interpreter -m cython --cplus --verbose
          "${PYFACTXX_PYX}"
          --output-file "${PYFACTXX_CPP}"
  DEPENDS "${PYFACTXX_PYX}"
  VERBATIM
)

# Build the Python extension module (with ABI tag / SOABI where applicable)
python_add_library(lib_factxx MODULE WITH_SOABI "${PYFACTXX_CPP}" ${FACT_FILES})
target_include_directories(lib_factxx PRIVATE "${FACT_DIR}")
target_link_libraries(lib_factxx PRIVATE Kernel)

# Install into the Python package directory inside the wheel:
# the compiled extension will end up next to pyfactxx/__init__.py etc.
install(
  TARGETS lib_factxx
  LIBRARY DESTINATION pyfactxx   # Linux/macOS (.so/.dylib)
  RUNTIME DESTINATION pyfactxx   # Windows (.pyd)
)
