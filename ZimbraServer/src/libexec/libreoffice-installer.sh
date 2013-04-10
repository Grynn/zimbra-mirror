#!/bin/bash
#
# ***** BEGIN LICENSE BLOCK *****
# Copyright (C) 2012, 2013 VMware, Inc.  All Rights Reserved.
# This product is protected by copyright and intellectual property laws in the
# United States and other countries as well as by international treaties.
# VMware products are covered by one or more patents listed at http://www.vmware.com/go/patents.
# ***** END LICENSE BLOCK *****
#
#
export PATH="/bin:/usr/bin:/sbin:/usr/sbin";

WGET=$(which wget)
TAR=$(which tar)
HEAD=$(which head)
AWK=$(which awk)
XARGS=$(which xargs)
BASENAME=$(which basename)

ARG=$1
shift

if [ x"$ARG" = "xhelp" -o x"$ARG" = "x-help" -o x"$ARG" = "x-h" -o x"$ARG" = "x--help" ]; then
  usage
fi

PKGTYPE=RPM

#LIBREOFFICE_TAR=$ARG
LIBREOFFICE_TAR=${LIBREOFFICE_TAR:=/tmp/LibO.tar.gz}

if [ $PKGTYPE = "RPM" ]; then
  RPM=$(which rpm)
  PKGCMD="$RPM -U --force --quiet --nodeps"
  #LIBREOFFICE_URL=http://downloadarchive.documentfoundation.org/libreoffice/old/3.5.6.2/rpm/x86_64/LibO_3.5.6rc2_Linux_x86-64_install-rpm_en-US.tar.gz
  LIBREOFFICE_URL=http://downloadarchive.documentfoundation.org/libreoffice/old/4.0.2.2/rpm/x86_64/LibreOffice_4.0.2.2_Linux_x86-64_rpm.tar.gz
else
  DPKG=$(which dpkg)
  APTGET=$(which apt-get)
  PKGCMD="$DPKG -i"
  #LIBREOFFICE_URL=http://downloadarchive.documentfoundation.org/libreoffice/old/3.5.6.2/deb/x86_64/LibO_3.5.6rc2_Linux_x86-64_install-deb_en-US.tar.gz
  LIBREOFFICE_URL=http://downloadarchive.documentfoundation.org/libreoffice/old/4.0.2.2/deb/x86_64/LibreOffice_4.0.2.2_Linux_x86-64_deb.tar.gz
fi

usage() {
  echo "$0 [LibreOffice tar file]"
  exit
}

checkPrereqs() {

  if [ $PKGTYPE = "RPM" -a x"${RPM}" = "x" ]; then
    echo "Unable to locate rpm command."
    exit 1
  fi
  if [ $PKGTYPE = "DEB" -a x"${DPKG}" = "x" ]; then
    echo "Unable to locate dpkg command."
    exit 1
  fi
  if [ x"${TAR}" = "x" ]; then
    echo "Unable to locate tar command."
    exit 1
  fi
  if [ x"${WGET}" = "x" ]; then
    echo "Unable to locate wget command."
    exit 1
  fi
  if [ x"${HEAD}" = "x" ]; then
    echo "Unable to locate head command."
    exit 1
  fi
  if [ x"${AWK}" = "x" ]; then
    echo "Unable to locate awk command."
    exit 1
  fi
  if [ x"${XARGS}" = "x" ]; then
    echo "Unable to locate xargs command."
    exit 1
  fi
  if [ x"${BASENAME}" = "x" ]; then
    echo "Unable to locate basename command."
    exit 1
  fi
}


checkPrereqs

if [ x"$ARG" = "x" ]; then
  echo -n "Downloading ${LIBREOFFICE_URL}..."
  $WGET -o /tmp/libreoffice.download.txt -O ${LIBREOFFICE_TAR} ${LIBREOFFICE_URL}
  if [ $? != 0 ]; then
    echo "failed."
    cat /tmp/libreoffice.download.txt
    exit 1
  else
    echo "done."
  fi
fi

if [ -z "${LIBREOFFICE_TAR}" ]; then
  echo "${LIBREOFFICE_TAR} doesn't exist or is empty."
  exit 1
else
  LIBREOFFICE_DIR=`$TAR tzvf ${LIBREOFFICE_TAR} | ${HEAD} -1 | ${AWK} '{print $NF}' | ${XARGS} ${BASENAME}`
  if [ x"${LIBREOFFICE_DIR}" != "x" ]; then
    echo -n "Extracting ${LIBREOFFICE_TAR}..."
    $TAR xzf ${LIBREOFFICE_TAR}
    echo "done"
  else
    echo "Couldn't determine tarfile directory structure."
    exit 1
  fi
fi

if [ $PKGTYPE = "RPM" ]; then
  if [ ! -d "${LIBREOFFICE_DIR}/RPMS" ]; then
    echo "Unabled to locate rpm packages."
    exit 1
  else
    LIBREOFFICE_PKGS=${LIBREOFFICE_DIR}/RPMS/*.rpm
  fi
fi

if [ $PKGTYPE = "DEB" ]; then
  if [ ! -d "${LIBREOFFICE_DIR}/DEBS" ]; then
    echo "Unabled to locate deb packages."; exit 1
  else
    LIBREOFFICE_PKGS=${LIBREOFFICE_DIR}/DEBS/*.deb
  fi
fi

echo -n "Uninstalling LibreOffice pkgs..."
echo "Uninstalling LibreOffice pkgs..." > /tmp/pkginstall.txt 2>&1

if [ $PKGTYPE = "RPM" ]; then
  $RPM -qa | grep libreoffice | xargs rpm -ev --nodeps >> /tmp/pkginstall.txt 2>&1
  $RPM -qa | grep libobasis | xargs rpm -ev --nodeps >> /tmp/pkginstall.txt 2>&1
fi

if [ $PKGTYPE = "DEB" ]; then
  $APTGET remove --purge --assume-yes 'libreoffice*' >> /tmp/pkginstall.txt 2>&1
  if [ $? = 0 ]; then
    $APTGET clean >> /tmp/pkginstall.txt 2>&1
  fi
  if [ $? = 0 ]; then
    $APTGET autoremove >> /tmp/pkginstall.txt 2>&1
  fi
fi

if [ $? = 0 ]; then
  echo "done."
else
  echo "failed."
  cat /tmp/pkginstall.txt
  exit
fi

#$RM ${LIBREOFFICE_DIR}/RPMS/libobasis*-gnome-integration
echo -n "Installing LibreOffice pkgs..."
echo "Installing LibreOffice pkgs..." >> /tmp/pkginstall.txt 2>&1

$PKGCMD ${LIBREOFFICE_PKGS} >> /tmp/pkginstall.txt 2>&1
if [ $? = 0 ]; then
  echo "done."
else
  echo "failed."
  echo "You many need to install dependancies."
  cat /tmp/pkginstall.txt
fi

su -l zimbra -c "/opt/zimbra/bin/zmlocalconfig -e oo_linux_install_path=/opt/libreoffice4.0/program/soffice"
