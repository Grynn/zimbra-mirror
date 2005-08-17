#!/bin/bash -x

#
# Builds a saslauthd with Liquid authentication support.  We modify
# configure.in which means we have to run autoconf.  I have borrowed
# the steps to run autoconf from the rpm spec.  The cyrus CVS tree's
# SMakefile is probably the source for the rpm spec.
#
src=cyrus-sasl-2.1.21.LIQUID

rm -fr build
mkdir build
cd build
cp -a ../${src} ${src}
chmod -R +w ${src}

cd ${src}
rm config/ltconfig config/libtool.m4
libtoolize -f -c
aclocal -I config -I cmulocal
automake -a -c -f
autoheader
autoconf -f

cd saslauthd
rm config/ltconfig
libtoolize -f -c
aclocal -I config -I ../cmulocal -I ../config
automake -a -c -f
autoheader
autoconf -f

cd ..
./configure --enable-liquid --prefix=/opt/liquid/${src} \
            --with-saslauthd=/opt/liquid/${src}/state \
            --with-plugindir=/opt/liquid/${src}/lib/sasl2 \
	    --enable-login
make
