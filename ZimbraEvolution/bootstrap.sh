#!/bin/sh

/usr/bin/aclocal
/usr/bin/libtoolize
/usr/bin/intltoolize --force
/usr/bin/autoconf
/usr/bin/automake --add-missing
./configure --enable-logging
