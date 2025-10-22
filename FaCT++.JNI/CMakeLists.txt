cmake_minimum_required(VERSION 3.15)

# We are using JNI and need to #include <jni.h>
find_package(JNI REQUIRED)

set(SOURCE_FILES
	Axioms.cpp
	Expressions.cpp
	FaCTPlusPlus.cpp
	JNIActor.h
	JNIMonitor.h
	JNISupport.h
	KnowledgeExploration.cpp
	MemoryStat.cpp
	MemoryStat.h
	Modularity.cpp
	Queries.cpp
	SaveLoad.cpp
	tJNICache.h
	uk_ac_manchester_cs_factplusplus_FaCTPlusPlus.h
)

# JNI Library should be declared as MODULE
add_library(FaCTPlusPlusJNI MODULE ${SOURCE_FILES})
target_link_libraries(FaCTPlusPlusJNI LINK_PUBLIC Kernel)
# Include search path for jni.h and jni_md.h
include_directories(
	${JAVA_INCLUDE_PATH}
	${JAVA_INCLUDE_PATH2}
)

# allow library users to find headers
target_include_directories(FaCTPlusPlusJNI PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
