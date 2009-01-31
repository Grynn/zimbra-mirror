#!/bin/bash

BUILD_HOME=/home/public/p4
P4USER=public
P4CLIENT=public-view
P4PASSWD=public1234
export P4USER P4CLIENT P4PASSWD
P4=`which p4`;
RELEASE=$1
SYNC=$2

if [ x$RELEASE = "x" ]; then
	RELEASE=main
fi

if [ x$SYNC = "x" ]; then
	SYNC=no
fi

PLAT=`$BUILD_HOME/$RELEASE/ZimbraBuild/rpmconf/Build/get_plat_tag.sh`;

if [ x$PLAT = "x" ]; then
    echo "Unknown platform, exiting."
    exit 1;
fi

if [ x$PLAT = "xRHEL4" -o x$PLAT = "CentOS4" -o x$PLAT = "xRHEL5" -o x$PLAT = "xCentOS5" -o x$PLAT = "xFC4" -o x$PLAT = "xFC5" -o x$PLAT = "xF7" -o x$PLAT = "xRPL1" -o x$PLAT = "xDEBIAN3.1" ]; then
	export PERLLIB="${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib:${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib/i386-linux-thread-multi"
	export PERL5LIB=${PERLLIB}
elif [ x$PLAT = "xRHEL4_64" -o x$PLAT = "xCentOS4_64" -o x$PLAT = "xRHEL5_64" -o x$PLAT = "xCentOS5_64"  -o x$PLAT = "xSLES10_64" ]; then
	export PERLLIB="${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib:${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib/x86_64-linux-thread-multi"
	export PERL5LIB=${PERLLIB}
elif [ x$PLAT = "xSuSEES9" -o x$PLAT = "xSuSEES10" -o x$PLAT = "xopenSUSE_10.2" -o x$PLAT = "xSuSE10" ]; then
	export PERLLIB="${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib:${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib/i586-linux-thread-multi"
	export PERL5LIB=${PERLLIB}
elif [ x$PLAT = "xDEBIAN4.0" -o x$PLAT = "xUBUNTU6" -o x$PLAT = "xUBUNTU8" ]; then
	export PERLLIB="${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib:${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib/i486-linux-gnu-thread-multi"
	export PERL5LIB=${PERLLIB}
elif [ x$PLAT = "xUBUNTU6_64" -o x$PLAT = "xUBUNTU8_64" ]; then
	export PERLLIB="${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib:${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib/x86_64-linux-gnu-thread-multi"
	export PERL5LIB=${PERLLIB}
elif [ x$PLAT = "xMACOSXx86" -o x$PLAT = "xMACOSX" -o x$PLAT = "xMACOSXx86_10.5" ]; then
	export PERLLIB="${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib:${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib/darwin-thread-multi-2level"
	export PERL5LIB=${PERLLIB}
elif [ x$PLAT = "xMANDRIVA2006" ]; then
	export PERLLIB="${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib:${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib/i386-linux"
	export PERL5LIB=${PERLLIB}
fi

if [ x$PLAT = "xSLES10_64" -o x$PLAT = "xRHEL4_64" -o x$PLAT = "xRHEL5_64" ]; then
	LIBDIR="/usr/lib64"
else
	LIBDIR="/usr/lib"
fi

echo "Resyncing thirdparty source for $RELEASE"
if [ x$SYNC = "xyes" ]; then
	cd ${BUILD_HOME}/$RELEASE/ThirdParty
	$P4 sync ... > /dev/null 
fi

if [ x$SYNC = "xyes" ]; then
	cd ${BUILD_HOME}/$RELEASE/ZimbraBuild
	$P4 sync ... > /dev/null 
fi

mkdir -p ${BUILD_HOME}/$RELEASE/ThirdPartyBuilds/$PLAT

if [ x$SYNC = "xyes" ]; then
	if [ x$RELEASE = "xmain" ]; then
		cd ${BUILD_HOME}/$RELEASE/ThirdPartyBuilds/$PLAT
		$P4 sync ... > /dev/null 
	fi
fi

echo "Removing /opt/zimbra"
if [ -d "/opt/zimbra" ]; then
  rm -rf /opt/zimbra
  mkdir /opt/zimbra
fi

if [ -x "/sbin/ldconfig" ]; then
  /sbin/ldconfig
fi


if [ x$RELEASE = "xmain" ]; then
	LIBREQ="libncurses.so libz.so"
	HEADERREQ="ncurses.h zlib.h"
else
	LIBREQ="libncurses.so libz.so libltdl.so"
	HEADERREQ="ncurses.h ltdl.h zlib.h"
fi

echo "Checking for prerequisite binaries"
for req in autoconf autoheader automake libtool bison flex gcc g++ perl make patch
do
	echo "	Checking $req"
	if [ ! -x "/usr/bin/$req" ]; then
		echo "Error: /usr/bin/$req not found"
		exit 1;
	fi
done

echo "Checking for prerequisite libraries"
for req in $LIBREQ
do
	echo "	Checking $req"
	if [ ! -f "$LIBDIR/$req" ]; then
		echo "Error: $LIBDIR/$req not found"
		exit 1;
	fi
done

if [ x$PLAT = "xMACOSXx86" -o x$PLAT = "xMACOSXx86_10.5" -o x$PLAT = "xMACOSX" ]; then
	echo "	Checking libpcre.a"
	if [ ! -f "/opt/zimbra/lib/libpcre.a" ]; then
		echo "Error: /opt/zimbra/lib/libpcre.a not found"
		exit 1;
	fi
else
	echo "	Checking libpcre.so"
	if [ ! -f "$LIBDIR/libpcre.so" ]; then
		echo "Error: $LIBDIR/libpcre.so not found"
		exit 1;
	fi
fi

echo "Checking for prerequisite headers"
for req in $HEADERREQ
do
	echo "	Checking $req"
	if [ ! -f "/usr/include/$req" ]; then
		echo "Error: /usr/include/$req not found"
		exit 1;
	fi
done

if [ x$PLAT = "xRHEL4" -o x$PLAT = "xRHEL4_64" -o x$PLAT = "xCentOS4" -o x$PLAT = "xCentOS4_64" ]; then
	PCREH="pcre/pcre.h"
else
	PCREH="pcre.h"
fi

echo "	Checking pcre.h"
if [ x$PLAT = "xMACOSXx86" -o x$PLAT = "xMACOSXx86_10.5" -o x$PLAT = "xMACOSX" ]; then
	if [ ! -f "/opt/zimbra/include/pcre.h" ]; then
		echo "Error: /opt/zimbra/include/pcre.h not found"
		exit 1;
	fi
else
	if [ ! -f "/usr/include/$PCREH" ]; then
		echo "Error: /usr/include/$PCREH not found"
		exit 1;
	fi
fi

cd ${BUILD_HOME}/$RELEASE/ThirdParty
rm -f make.out 2> /dev/null
make allclean > /dev/null 2>&1
make all 2>&1 | tee -a make.out
