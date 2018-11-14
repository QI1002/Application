# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

# refer the android studio 3 + openCV teach link website => https://www.jianshu.com/p/6e16c0429044
cmake_minimum_required(VERSION 3.4.1)

set( OpenCV_DIR /home/qi/gpu/opencv-3.4.3/android-sdk/sdk/native/jni )

find_package(OpenCV REQUIRED )
if(OpenCV_FOUND)
    include_directories(${OpenCV_INCLUDE_DIRS})
    message(STATUS "OpenCV library status:")
    message(STATUS "    version: ${OpenCV_VERSION}")
    message(STATUS "    libraries: ${OpenCV_LIBS}")
    message(STATUS "    include path: ${OpenCV_INCLUDE_DIRS}")
else(OpenCV_FOUND)
    message(FATAL_ERROR "OpenCV library not found")
endif(OpenCV_FOUND)

add_library( native-lib
        SHARED
        src/main/cpp/native-lib.cpp)

target_link_libraries( native-lib
        ${OpenCV_LIBS}
        log
        jnigraphics)