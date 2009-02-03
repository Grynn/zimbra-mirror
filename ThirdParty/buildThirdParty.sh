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
elif [[ $PLAT == "MACOSX"* ]]; then
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

if [[ $PLAT == "MACOSX"* ]]; then
	LIBEXT=dylib
else
	LIBEXT=so
fi

NONMACLIB="libpcre.so libexpat.so libpopt.so"
NONMACHEADER="expat.h popt.h"

if [ x$RELEASE = "xmain" ]; then
	LIBREQ="libncurses.$LIBEXT libz.$LIBEXT"
	HEADERREQ="ncurses.h zlib.h"
else
	LIBREQ="libncurses.$LIBEXT libz.$LIBEXT libltdl.$LIBEXT"
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

if [[ $PLAT != "MACOSX"* ]]; then
	for req in $NONMACLIB
	do
		echo "	Checking $req"
		if [ ! -f "$LIBDIR/$req" ]; then
			echo "Error: $LIBDIR/$req not found"
			exit 1;
		fi
	done
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

if [[ $PLAT != "MACOSX"* ]]; then
	for req in $NONMACHEADER
	do
		echo "	Checking $req"
		if [ ! -f "/usr/include/$req" ]; then
			echo "Error: /usr/include/$req not found"
			exit 1;
		fi
	done
fi

if [ x$PLAT = "xRHEL4" -o x$PLAT = "xRHEL4_64" -o x$PLAT = "xCentOS4" -o x$PLAT = "xCentOS4_64" ]; then
	PCREH="pcre/pcre.h"
else
	PCREH="pcre.h"
fi

echo "	Checking pcre.h"
if [[ $PLAT != "MACOSX"* ]]; then
	if [ ! -f "/usr/include/$PCREH" ]; then
		echo "Error: /usr/include/$PCREH not found"
		exit 1;
	fi
fi

cd ${BUILD_HOME}/$RELEASE/ThirdParty
rm -f make.out 2> /dev/null
make allclean > /dev/null 2>&1
make all 2>&1 | tee -a make.out
