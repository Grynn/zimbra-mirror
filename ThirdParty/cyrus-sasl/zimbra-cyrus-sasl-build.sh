#!/bin/bash -x

#
# Builds a saslauthd with Zimbra authentication support.  We modify
# configure.in which means we have to run autoconf.  I have borrowed
# the steps to run autoconf from the rpm spec.  The cyrus CVS tree's
# SMakefile is probably the source for the rpm spec.
#
package=cyrus-sasl
release=2.1.22
patchlevel=3
cyrus_version=${release}.${patchlevel}
src=${package}-${cyrus_version}
platform=`uname -s`

cyrus_root=`pwd`
p4_root=`cd ${cyrus_root}/../..; pwd`
build_platform=`sh ${p4_root}/ZimbraBuild/rpmconf/Build/get_plat_tag.sh`

heimdal_version=1.0.1
openssl_version=0.9.8e
curl_version=7.17.0
xml2_version=2.6.29

openssl_lib_dir=/opt/zimbra/openssl-${openssl_version}/lib
heimdal_lib_dir=/opt/zimbra/heimdal-${heimdal_version}/lib
cyrus_lib_dir=/opt/zimbra/cyrus-sasl-${cyrus_version}/lib
curl_lib_dir=/opt/zimbra/curl-${curl_version}/lib

rm -fr build
mkdir build
cd build
tar xfz ../src/cyrus-sasl-2.1.22.tar.gz  -C .
chmod -R +w ${package}-${release}
mv ${package}-${release} ${src}

cd ${src}
patch -g0 -p1 < ../../sasl-link-order.patch
patch -g0 -p1 < ../../sasl-darwin.patch
patch -g0 -p1 < ../../sasl-auth-zimbra.patch
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
# fix linking against OpenSSL
sed -i.obak -e 's|${with_openssl}/$CMU_LIB_SUBDIR|${with_openssl}/lib|' -e 's|${with_openssl}/lib $andrew_runpath_switch${with_openssl}/$CMU_LIB_SUBDIR|${with_openssl}/lib|' configure
sed -i.obak -e 's|${with_openssl}/$CMU_LIB_SUBDIR|${with_openssl}/lib|' -e 's|${with_openssl}/lib $andrew_runpath_switch${with_openssl}/$CMU_LIB_SUBDIR|${with_openssl}/lib|' saslauthd/configure

sed -i.bak 's/-lRSAglue //' configure
if [ $platform = "Darwin" ]; then
# we need to remove all -lxml2 references because mac ld will pick the dylib
# no matter the order of -L options.
sed -i .bak -e 's/-lxml2//g' /opt/zimbra/libxml2/bin/xml2-config
LIBS="/opt/zimbra/libxml2/lib/libxml2.a" CFLAGS="-D_REENTRANT -g -O2 -I/opt/zimbra/libxml2/include/libxml2" ./configure --enable-zimbra --prefix=/opt/zimbra/${src} \
            --with-saslauthd=/opt/zimbra/${src}/state \
            --with-plugindir=/opt/zimbra/${src}/lib/sasl2 \
            --enable-static=no \
            --enable-shared \
            --with-dblib=no \
            --with-openssl=/opt/zimbra/openssl-${openssl_version} \
            --with-libcurl=/opt/zimbra/curl-${curl_version}/bin/curl-config \
            --with-gss_impl=heimdal \
            --enable-gssapi=/opt/zimbra/heimdal-${heimdal_version} \
            --enable-login
else 
CFLAGS="-D_REENTRANT -g -O2" ./configure --enable-zimbra --prefix=/opt/zimbra/${src} \
            --with-saslauthd=/opt/zimbra/${src}/state \
            --with-plugindir=/opt/zimbra/${src}/lib/sasl2 \
            --with-dblib=no \
            --with-openssl=/opt/zimbra/openssl-${openssl_version} \
            --with-libcurl=/opt/zimbra/curl-${curl_version}/bin/curl-config \
            --with-gss_impl=heimdal \
	    --with-libxml2=/opt/zimbra/libxml2-${xml2_version}/bin/xml2-config \
            --enable-gssapi=/opt/zimbra/heimdal-${heimdal_version} \
            --enable-login
fi
if [ $platform = "Darwin" ]; then
     sed -i .bak -e 's/\_la_LDFLAGS)/_la_LDFLAGS) $(AM_LDFLAGS)/' plugins/Makefile
elif [ $build_platform = "F7" -o $build_platform -o "DEBIAN4.0" ]; then
     sed -i.bak -e 's/\_la_LDFLAGS)/_la_LDFLAGS) $(AM_LDFLAGS)/' plugins/Makefile
fi
LD_RUN_PATH="${openssl_lib_dir}:${heimdal_lib_dir}:${cyrus_lib_dir}:${curl_lib_dir}" make
