#!/bin/bash

shaderc -f fs_twl.sc -o fs_twl.bin --type f --platform linux
shaderc -f vs_twl.sc -o vs_twl.bin --type v --platform linux