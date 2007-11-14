#!/bin/bash

BUILD_HOME=/home/public/p4/zcs
P4=`which p4`;
RELEASE=$1

if [ x$RELEASE = "x" ]; then
	RELEASE=main
fi

PLAT=`$BUILD_HOME/$RELEASE/ZimbraBuild/rpmconf/Build/get_plat_tag.sh`;

if [ x$PLAT = "x" ]; then
    echo "Unknown platform, exiting."
    exit
fi

if [ x$PLAT = "xRHEL4" -o x$PLAT = "CentOS4" -o x$PLAT = "xRHEL5" -o x$PLAT = "xCentOs5" -o x$PLAT = "xFC4" -o x$PLAT = "xFC5" -o x$PLAT = "xF7" -o x$PLAT = "xRPL1" -o x$PLAT = "xDEBIAN3.1" ]; then
	export PERLLIB="${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib:${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib/i386-linux-thread-multi"
	export PERL5LIB=${PERLLIB}
elif [ x$PLAT = "xRHEL4_64" -o x$PLAT = "xCentOS4_64" -o x$PLAT = "xRHEL5_64" -o x$PLAT = "xCentOS5_64"  -o x$PLAT = "xSLES10_64" ]; then
	export PERLLIB="${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib:${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib/x86_64-linux-thread-multi"
	export PERL5LIB=${PERLLIB}
elif [ x$PLAT = "xSuSEES9" -o x$PLAT = "xSuSEES10" -o x$PLAT = "xopenSUSE_10.2" -o x$PLAT = "xSuSE10" ]; then
	export PERLLIB="${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib:${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib/i586-linux-thread-multi"
	export PERL5LIB=${PERLLIB}
elif [ x$PLAT = "xDEBIAN4.0" -o x$PLAT = "xUBUNTU6" ]; then
	export PERLLIB="${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib:${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib/i486-linux-gnu-thread-multi"
	export PERL5LIB=${PERLLIB}
elif [ x$PLAT = "xMACOSXx86" -o x$PLAT = "xMACOSX" ]; then
	export PERLLIB="${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib:${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib/darwin-thread-multi-2level"
	export PERL5LIB=${PERLLIB}
elif [ x$PLAT = "xMANDRIVA2006" ]; then
	export PERLLIB="${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib:${BUILD_HOME}/$RELEASE/ThirdParty/Perl/zimbramon/lib/i386-linux"
	export PERL5LIB=${PERLLIB}
fi

echo "Resyncing thirdparty source for $RELEASE"
cd ${BUILD_HOME}/$RELEASE/ThirdParty
$P4 sync ... > /dev/null 
cd ${BUILD_HOME}/$RELEASE/ZimbraBuild
$P4 sync ... > /dev/null 

if [ x$RELEASE = "xmain" ]; then
  cd ${BUILD_HOME}/$RELEASE/ThirdPartyBuilds/$PLAT
  $P4 sync ... > /dev/null 
fi

echo "Removing /opt/zimbra"
if [ -d "/opt/zimbra" ]; then
  rm -rf /opt/zimbra
  mkdir /opt/zimbra
fi

if [ -x "/sbin/ldconfig" ]; then
  /sbin/ldconfig
fi


cd ${BUILD_HOME}/$RELEASE/ThirdParty 
rm -f make.out 2> /dev/null
make allclean > /dev/null 2>&1
make all 2>&1 | tee -a make.out
