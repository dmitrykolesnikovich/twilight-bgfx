set(LINK_LIBRARIES SDL2 iconv "-framework ForceFeedback" "-framework CoreVideo" "-framework CoreAudio" "-framework OpenAL" "-framework AudioUnit" "-framework CoreFoundation" "-framework ApplicationServices" "-framework OpenGL" "-framework IOKit" "-framework CoreServices" "-framework Cocoa" "-framework Carbon")

# OSX bgfx compat
include_directories(${CMAKE_SOURCE_DIR}/lib/bgfx/${TARGET_ARCH}/include/compat/osx)

