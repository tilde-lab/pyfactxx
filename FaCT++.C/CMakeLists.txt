cmake_minimum_required(VERSION 2.8.11)

set(SOURCE_FILES
	fact.cpp
	fact.h
)

add_library(fact SHARED ${SOURCE_FILES})
target_link_libraries(fact LINK_PUBLIC Kernel)

add_executable(fact_test test.c)
target_link_libraries(fact_test LINK_PUBLIC Kernel fact)

# allow library users to find headers
target_include_directories(fact PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
