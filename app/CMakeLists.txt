cmake_minimum_required(VERSION 3.4.1)

set(CURRENT_DIR ${CMAKE_SOURCE_DIR})
message("CURRENT_DIR:" ${CMAKE_SOURCE_DIR})

add_library(
    coo
    SHARED
    ${CURRENT_DIR}/src/main/cpp/android_jni.cpp
#    ${CURRENT_DIR}/src/main/cpp/native_crash_cache.cpp
)

# Specifies a path to native header files.
include_directories(${CURRENT_DIR}/src/main/cpp/include)

find_library(
    log-lib
    log
)

target_link_libraries(
    coo
    ${log-lib}
)

