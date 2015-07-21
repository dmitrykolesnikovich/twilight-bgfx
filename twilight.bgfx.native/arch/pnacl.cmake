if (DEFINED LOTUS_NACL_SDK_HOME)
    set(LOTUS_NACL_SDK_HOME ${LOTUS_NACL_SDK_HOME})
else (DEFINED LOTUS_NACL_SDK_HOME)
    set(LOTUS_NACL_SDK_HOME /opt/nacl_sdk)
endif(DEFINED LOTUS_NACL_SDK_HOME)

# Default GCC toolchain
set(CMAKE_C_COMPILER ${LOTUS_NACL_SDK_HOME}/toolchain/linux_pnacl/bin/ccl-clang)
set(CMAKE_CXX_COMPILER ${LOTUS_NACL_SDK_HOME}/toolchain/linux_pnacl/bin/pnacl-clang++)
set(CMAKE_CXX_FLAGS "-g -Wall -c -Wno-long-long -pthread -O2")

message("[TOOLCHAIN SETTINGS] -- Arch: ${TARGET_ARCH}, Toolchain: PNaCl Clang")

include_directories(lib/bgfx/pnacl/include/compat/nacl)
include_directories(${LOTUS_NACL_SDK_HOME}/include)
