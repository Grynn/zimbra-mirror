#!/bin/bash
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2006 Zimbra, Inc.
# 
# The contents of this file are subject to the Yahoo! Public License
# Version 1.0 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# 
# ***** END LICENSE BLOCK *****
# 

PROGDIR=`dirname $0`
cd $PROGDIR
PATHDIR=`pwd`
BETA=
CLEAN=no
SYNC=no
PUBLIC=yes
OVERRIDE=no
ZIMBRA=no
MIRROR="http://cpan.yahoo.com/"

usage() {
	echo ""
	echo "Usage: "`basename $0`" [-b] -c [-p] [-s]" >&2
	echo "-b: Use beta software versions"
	echo "-c: Remove contents of /opt/zimbra (clean)"
	echo "-p: Use private CPAN mirror"
	echo "-s: Re-sync source before building"
	exit 2;
}

ask() {
  PROMPT=$1
  DEFAULT=$2
  
  echo ""
  echo -n "$PROMPT [$DEFAULT] "
  read response
  
  if [ -z $response ]; then
    response=$DEFAULT 
  fi
}   

askYN() {
  PROMPT=$1
  DEFAULT=$2

  if [ "x$DEFAULT" = "xyes" -o "x$DEFAULT" = "xYes" -o "x$DEFAULT" = "xy" -o "x$DEFAULT" = "xY" ]; then
    DEFAULT="Y"
  else
    DEFAULT="N"
  fi

  while [ 1 ]; do
    ask "$PROMPT" "$DEFAULT"
    response=`echo $response | tr "[:upper:]" "[:lower:]"`
    if [ -z $response ]; then
      :
    else
      if [ $response = "yes" -o $response = "y" ]; then
        response="yes"
        break
      else
        if [ $response = "no" -o $response = "n" ]; then
          response="no"
          break
        fi
      fi
    fi
    echo "A Yes/No answer is required"
  done
}

askURL() {
  PROMPT=$1
  DEFAULT=$2

  while [ 1 ]; do
    ask "$PROMPT" "$DEFAULT"
    response=`echo $response | tr "[:upper:]" "[:lower:]"`
    if [ -z $response ]; then
      :
    else
      if [[ $response == "http://"* ]]; then
        break
      fi
    fi
    echo "A http:// formed URL is required"
  done
}

parseVersion() {
	VER=$1
	MAJOR=`echo $VER | awk -F. '{print $1}'`
	MINOR=`echo $VER | awk -F. '{print $2}'`
	PATCH=`echo $VER | awk -F. '{print $3}'`
}

if [ $# -lt 1 ]; then
	usage
fi

while [ $# -gt 0 ]; do
	case $1 in
		-b|--beta)
			BETA=1
			shift;
			;;
		-c|--clean)
			CLEAN=yes
			shift;
			;;
		-o|--override)
			OVERRIDE=yes
			shift;
			;;
		-p|--private)
			PUBLIC=no
			shift;
			;;
		-h|--help)
			usage;
			exit 0;
			;;
		-s|--sync)
			SYNC=yes
			shift;
			;;
		-z|--zimbra)
			ZIMBRA=yes
			shift;
			;;
		*)
			echo "Usage: $0 -c [-s]"
			exit 1;
			;;
	esac
done

RELEASE=${PATHDIR%/*}
RELEASE=${RELEASE##*/}

if [ x$CLEAN = x"no" ]; then
	echo "WARNING: You must supply the clean option -c"
	echo "WARNING: This will completely remove the contents of /opt/zimbra from the system"
	exit 1;
fi

if [ x$SYNC = x"yes" ]; then
	P4USER=public
	P4CLIENT=public-view
	P4PASSWD=public1234
	export P4USER P4CLIENT P4PASSWD
	P4=`which p4`;
fi

PLAT=`$PATHDIR/../ZimbraBuild/rpmconf/Build/get_plat_tag.sh`;

if [ x$PLAT = "x" ]; then
	echo "Unknown platform, exiting."
	exit 1;
fi

if [ x$OVERRIDE = x"no" ]; then
	askYN "Proceeding will remove /opt/zimbra.  Do you wish to continue?: " "N"
	if [ $response = "no" ]; then
		echo "Exiting"
		exit 1;
	fi
fi

eval `/usr/bin/perl -V:archname`
export PERLLIB="${PATHDIR}/Perl/zimbramon/lib:${PATHDIR}/Perl/zimbramon/lib/$archname"
export PERL5LIB=${PERLLIB}

if [ x$PLAT = "xSLES10_64" -o x$PLAT = "xSLES11_64" -o x$PLAT = "xRHEL4_64" -o x$PLAT = "xRHEL5_64" -o x$PLAT = "xRHEL6_64" -o x$PLAT = "xF10_64" -o x$PLAT = "xF11_64" ]; then
	LIBDIR="/usr/lib64"
else
	LIBDIR="/usr/lib"
fi

if [ x$SYNC = "xyes" ]; then
	echo "Resyncing thirdparty source for $RELEASE"
fi

if [ x$SYNC = "xyes" ]; then
	cd ${PATHDIR}
	$P4 sync ... > /dev/null 
fi

if [ x$SYNC = "xyes" ]; then
	cd ${PATHDIR}/../ZimbraBuild
	$P4 sync ... > /dev/null 
fi

mkdir -p ${PATHDIR}/../ThirdPartyBuilds/$PLAT

if [ x$SYNC = "xyes" ]; then
	if [ x$RELEASE != "xFRANK" ]; then
		cd ${PATHDIR}/../ThirdPartyBuilds/$PLAT
		$P4 sync ... > /dev/null 
	fi
fi

if [[ $PLAT == "MACOSX"* ]]; then
	LIBEXT=dylib
else
	LIBEXT=so
fi

NONMACLIB="libpcre.so libexpat.so libpopt.so"
NONMACHEADER="expat.h popt.h"

if [[ $PLAT == "UBUNTU"*"64" || $PLAT == "DEBIAN"*"64" ]]; then
	NONMACLIB="libpcre.so libexpat.so libpopt.so libperl.so"
fi

if [ x$RELEASE = "xmain" ]; then
	LIBREQ="libncurses.$LIBEXT libz.$LIBEXT"
	HEADERREQ="ncurses.h zlib.h"
else
	LIBREQ="libncurses.$LIBEXT libz.$LIBEXT libltdl.$LIBEXT"
	HEADERREQ="ncurses.h ltdl.h zlib.h"
fi

echo "Checking for prerequisite binaries"
for req in autoconf autoheader automake libtool bison flex gcc g++ perl make patch wget bzip2 unzip
do
	echo "	Checking $req"
	command=`which $req 2>/dev/null`
	RC=$?
	if [ $RC -ne 0 ]; then
		echo "Error: $req not found"
		exit 1;
	elif [ $RC -eq 0 -a x$req = x"automake" ]; then
		VERSION=$(${command} --version 2>&1 | grep "^automake" | sed -e 's/^automake //' -e 's/(GNU automake) //')
		parseVersion $VERSION
		if [ $MAJOR -eq 1 -a $MINOR -lt 7 ]; then
			echo "Error: Version 1.7.0 or higher of $req is required"
			exit 1;
		fi
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

if [[ $PLAT != "MACOSX"* ]]; then
	echo "	Checking pcre.h"
	if [ ! -f "/usr/include/$PCREH" ]; then
		echo "Error: /usr/include/$PCREH not found"
		exit 1;
	fi
fi

if [ x"$ZIMBRA" = x"no" ]; then
	echo "Cleaning contents of /opt/zimbra"
	if [ -d "/opt/zimbra" ]; then
		rm -rf /opt/zimbra/* 2>/dev/null
		rm -rf /opt/zimbra/.* 2>/dev/null
		mkdir -p /opt/zimbra
	fi
else
	if [ -x "/home/build/scripts/setup-build.sh" ]; then
		sudo /home/build/scripts/setup-build.sh 2>/dev/null
	else
		echo "Error: setup-build.sh missing"
		exit 1;
	fi
fi

touch /opt/zimbra/blah 2>/dev/null
RC=$?

if [ $RC -eq 1 ]; then
	echo "Error: Unable to write to /opt/zimbra"
	exit 1;
else
	rm -f /opt/zimbra/blah
fi

if [ x$PUBLIC = x"yes" ]; then
	askURL "CPAN URL?" "$MIRROR"
	MIRROR=$response
fi

cd ${PATHDIR}
rm -f make.out 2> /dev/null
make allclean > /dev/null 2>&1

if [ x$PUBLIC = x"yes" ]; then
	make all CMIRROR=$MIRROR BETA=$BETA 2>&1 | tee -a make.out
else
	make all 2>&1 | tee -a make.out
fi

mkdir -p $PATHDIR/../logs
cp -f ThirdParty.make.log $PATHDIR/../logs
cp -f Perl/ThirdParty-Perllibs.log $PATHDIR/../logs
exit 0;
