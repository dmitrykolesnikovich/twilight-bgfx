#!/bin/bash

/usr/local/bin/shaderc --Werror --debug --verbose -f fs_twl.sc -o fs_twl.bin --type f --platform linux
/usr/local/bin/shaderc --Werror --debug --verbose -f vs_twl.sc -o vs_twl.bin --type v --platform linux

/usr/local/bin/shaderc --Werror --debug --verbose -f fs_tile.sc -o fs_tile.bin --type f --platform linux
/usr/local/bin/shaderc --Werror --debug --verbose -f vs_tile.sc -o vs_tile.bin --type v --platform linux

/usr/local/bin/shaderc --Werror --debug --verbose -f fs_test.sc -o fs_test.bin --type f --platform linux
/usr/local/bin/shaderc --Werror --debug --verbose -f vs_test.sc -o vs_test.bin --type v --platform linux
