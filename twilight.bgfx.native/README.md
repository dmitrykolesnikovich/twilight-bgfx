
# Building for specific platform:
cmake -DTARGET_ARCH=os-arch .

e.g. 

cmake -DTARGET_ARCH=linux-x64 .

# pnacl example
cmake -DTARGET_ARCH=pnacl -DLOTUS_NACL_SDK_HOME=/path/to/nacl_sdk/pepper_41/ .
