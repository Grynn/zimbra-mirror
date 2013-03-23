#!/bin/sh
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Web Client
# Copyright (C) 2012 VMware, Inc.
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.3 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# 
# ***** END LICENSE BLOCK *****
# 

##############################################################################
#
#
# zmChkPkgDups.sh [startup|all]
#
# This scripts checks for any duplicate file entries in the package definitions. 
# startup		This option looks at only the files which are loaded at startup.
# all			This option looks at all the package definitions
#
# TODO:
#	- Add a exclusion list for some of the packages
#	- Add options for other package combinations.
##############################################################################

if [ $# != 1 ];
then
  echo "Usage: zmChkPkgDups.sh [startup|all]";
  echo "startup		- Look for duplicates only in startup packages";
  echo "all		- Look for duplicates in all packages";
  exit 1;
fi


if [ $1 = "startup" ];
then
    pkgfiles=`ls ../WebRoot/js/zimbraMail/package/Calendar.js ../WebRoot/js/zimbraMail/package/CalendarCore.js ../WebRoot/js/zimbraMail/package/MailCore.js  ../WebRoot/js/zimbraMail/package/Mail.js ../WebRoot/js/zimbraMail/package/Startup*.js ../WebRoot/js/zimbraMail/package/Extras.js ../WebRoot/js/zimbraMail/package/Share.js ../WebRoot/js/zimbraMail/package/TasksCore.js ../WebRoot/js/zimbraMail/package/ContactsCore.js ../WebRoot/js/zimbraMail/package/Contacts.js ../WebRoot/js/zimbraMail/package/Zimlet.js`;
fi

if [ $1 = "all" ];
then
    pkgfiles=`find ../WebRoot/js/zimbraMail/package ../WebRoot/js/zimbra/package ../WebRoot/js/ajax/package -type f`;

fi

dups=`awk 'FNR==1{print ""}1' $pkgfiles | grep "^AjxPackage.require" | sort | uniq -d | cut -d\" -f2`

for entry in $dups
do
  echo "$entry is present in following packages"
  grep -s -l "\"$entry\"" $pkgfiles;
  echo ""
done
