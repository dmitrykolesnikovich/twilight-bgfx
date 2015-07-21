cmake_minimum_required (VERSION 2.6)
project (twilight-bgfx)

# Detect the arch for the build (can be overriden)
include(${CMAKE_SOURCE_DIR}/arch/detect_arch.cmake)

# Include the arch specific configuration
include(${CMAKE_SOURCE_DIR}/arch/${TARGET_ARCH}.cmake)

set(LIBRARY_OUTPUT_PATH ${CMAKE_SOURCE_DIR}/target/classes)

link_directories(${CMAKE_SOURCE_DIR}/lib/bgfx/${TARGET_ARCH}/lib/)
link_directories(${CMAKE_SOURCE_DIR}/lib/sdl/${TARGET_ARCH}/lib/)

include_directories(
					lib/sdl/${TARGET_ARCH}/include 
					lib/bgfx/${TARGET_ARCH}/include
					lib/stb/
					src/3rdparty/
					src/3rdparty/nanovg/
					lib/bgfx/nanosvg/
					src/include 
					src/java
					src/java/linux)
		 
set(BGFX
        src/jni_cache.cc
        src/BGFX.cc
        src/SDLWindow.cc
        src/SDLDisplay.cc
        src/VertexDecl.cc
        src/TransientVertexBuffer.cc
        src/TransientIndexBuffer.cc
		src/PlatformUtil.cc
		) 
		
set(NANOVG
        src/3rdparty/nanovg/nanovg.cpp
        src/3rdparty/nanovg/nanovg_bgfx.cpp
        src/NanoVG.cc
        src/SVG.cc
		)
  
find_package(PkgConfig REQUIRED)
pkg_check_modules(GTK2 REQUIRED gtk+-2.0)
if(GTK2_FOUND)  
   include_directories(${GTK2_INCLUDE_DIRS})
   link_directories("/usr/lib//x86_64-linux-gnu/")
   set(LINK_LIBRARIES ${GTK2_LIBRARIES} ${LINK_LIBRARIES})
endif()
		
#add_definitions(-DBGFX_CONFIG_MULTITHREADED=0)
 
# pnacl doesn't support shared libraries, must be statically compiled in
if(TARGET_ARCH MATCHES "pnacl")
add_library(twilight-bgfx ${BGFX} ${NANOVG})
else(TARGET_ARCH MATCHES "pnacl")
add_library(twilight-bgfx SHARED ${BGFX} ${NANOVG})
endif(TARGET_ARCH MATCHES "pnacl")
 
target_link_libraries(twilight-bgfx bgfxRelease ${LINK_LIBRARIES})

file(COPY ${CMAKE_SOURCE_DIR}/target/classes/ DESTINATION ../twilight.bgfx/native/${TARGET_ARCH}/ FILES_MATCHING PATTERN libtwilight-bgfx.*)
