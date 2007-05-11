#!/bin/bash -x

#
# Builds a saslauthd with Zimbra authentication support.  We modify
# configure.in which means we have to run autoconf.  I have borrowed
# the steps to run autoconf from the rpm spec.  The cyrus CVS tree's
# SMakefile is probably the source for the rpm spec.
#
src=cyrus-sasl-2.1.21.ZIMBRA
platform=`uname -s`

rm -fr build
mkdir build
cd build
cp -PpR ../${src} ${src}
chmod -R +w ${src}

cd ${src}
rm config/ltconfig config/libtool.m4
if [ -x /usr/bin/libtoolize ]; then
	LIBTOOLIZE=/usr/bin/libtoolize
else
	if [ -x /opt/local/bin/glibtoolize ]; then
		export CPPFLAGS=-DDARWIN
		LIBTOOLIZE=/opt/local/bin/glibtoolize
	else
		echo "Where is libtoolize?"
		exit 1
	fi
fi
$LIBTOOLIZE -f -c
aclocal -I config -I cmulocal
automake -a -c -f
autoheader
autoconf -f

cd saslauthd
rm config/ltconfig
$LIBTOOLIZE -f -c
aclocal -I config -I ../cmulocal -I ../config
automake -a -c -f
autoheader
autoconf -f

cd ..
sed -i.bak 's/-lRSAglue //' configure
if [ $platform = "Darwin" ]; then
# we need to remove all -lxml2 references because mac ld will pick the dylib
# no matter the order of -L options.
sed -i .bak -e 's/-lxml2//g' /opt/zimbra/libxml2/bin/xml2-config
LIBS="/opt/zimbra/libxml2/lib/libxml2.a" CFLAGS="-I/opt/zimbra/libxml2/include/libxml2" ./configure --enable-zimbra --prefix=/opt/zimbra/${src} \
            --with-saslauthd=/opt/zimbra/${src}/state \
            --with-plugindir=/opt/zimbra/${src}/lib/sasl2 \
            --enable-static=no \
            --enable-shared \
            --with-libxml2=/opt/zimbra/libxml2/bin/xml2-config \
			--with-dblib=no \
			--enable-login
else 
LIBS="-lxml2" ./configure --enable-zimbra --prefix=/opt/zimbra/${src} \
            --with-saslauthd=/opt/zimbra/${src}/state \
            --with-plugindir=/opt/zimbra/${src}/lib/sasl2 \
            --with-libxml2=/opt/zimbra/libxml2/bin/xml2-config \
			--with-dblib=no \
			--enable-login
fi
if [ $platform = "Darwin" ]; then
     sed -i .bak -e 's/\_la_LDFLAGS)/_la_LDFLAGS) $(AM_LDFLAGS)/' plugins/Makefile
     make
else
     make
fi
