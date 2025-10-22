cmake_minimum_required(VERSION 3.15)
project(pyfactxx)
set(CMAKE_CXX_STANDARD 11)
include(CMakePrintHelpers)

find_package(PythonExtensions REQUIRED)
find_package(Cython REQUIRED)

add_subdirectory(${PYFACTXX_ROOT}/lib/Kernel ./Kernel)
set(FACT_DIR ${PYFACTXX_ROOT}/lib/FaCT++)
file(GLOB FACT_FILES  "${FACT_DIR}/*.cpp")
# TODO: How to create shared libKernel?
# target_compile_definitions(Kernel PRIVATE BUILD_SHARED_LIBS)
set_property(TARGET Kernel PROPERTY POSITION_INDEPENDENT_CODE ON)
add_cython_target(_factxx lib_factxx.pyx CXX PY3)
add_library(lib_factxx MODULE ${_factxx} ${FACT_FILES} Kernel)
target_include_directories(lib_factxx PRIVATE ${FACT_DIR} Kernel)
target_link_libraries(lib_factxx Kernel)
python_extension_module(lib_factxx)

install(TARGETS lib_factxx LIBRARY DESTINATION src)

set(CPACK_PROJECT_NAME ${PROJECT_NAME})
set(CPACK_PROJECT_VERSION ${PROJECT_VERSION})
include(CPack)
