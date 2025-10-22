project(FaCT++)
cmake_minimum_required(VERSION 3.15)

set(SOURCE_FILES
	AD.cpp
	comparser.h
	comscanner.h
	FaCT.cpp
	lispgrammar.h
	parser.cpp
	parser.h
	scanner.cpp
	scanner.h
)

# Executable
add_executable(FaCT++ ${SOURCE_FILES})
target_link_libraries(FaCT++ LINK_PUBLIC Kernel)
