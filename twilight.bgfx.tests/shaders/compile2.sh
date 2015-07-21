#!/bin/bash

shaderc -f fs_cool.sc -o fs_cool.bin --type f --platform linux
shaderc -f vs_cool.sc -o vs_cool.bin --type v --platform linux
