#!/bin/sh 
# 
# ***** BEGIN LICENSE BLOCK *****
# Version: MPL 1.1
# 
# The contents of this file are subject to the Mozilla Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
# 
# The Original Code is: Zimbra Collaboration Suite Server.
# 
# The Initial Developer of the Original Code is Zimbra, Inc.
# Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 
# This may not be there, but we don't want to break the zimbramta package
# if it's installed.

ROOTGROUP=root

PLAT=`/bin/sh /opt/zimbra/bin/get_plat_tag.sh`

if [ "X$PLAT" = "XMACOSX" ]; then
	ROOTGROUP=wheel
fi

# NOT /opt/zimbra/store, /opt/zimbra/backup takes forever
chown -R zimbra:zimbra /opt/zimbra/a* /opt/zimbra/[c-ot-z]* /opt/zimbra/s[a-su-z]* /opt/zimbra/perdition-1.17


chown -R root:$ROOTGROUP /opt/zimbra/libexec
chown -R root:$ROOTGROUP /opt/zimbra/bin
chown -R root:$ROOTGROUP /opt/zimbra/lib
if [ -d /opt/zimbra/jdk1.5.0_05 ]; then
	chown -R root:$ROOTGROUP /opt/zimbra/jdk1.5.0_05
fi

chown root:$ROOTGROUP /opt/zimbra
chmod 755 /opt/zimbra

chmod 755 /opt/zimbra/libexec/*

chmod 755 /opt/zimbra/bin/*

if [ ! -d /opt/zimbra/tomcat/conf ]; then
	mkdir -p /opt/zimbra/tomcat/conf
	chown zimbra:zimbra /opt/zimbra/tomcat/conf
fi

if [ -L /opt/zimbra/postfix ]; then

	#chown -R root:$ROOTGROUP /opt/zimbra/conf/*
	if [ ! -d /opt/zimbra/postfix/spool ]; then
		mkdir -p /opt/zimbra/postfix/spool
	fi
	chown -fR root:$ROOTGROUP /opt/zimbra/postfix*
	chown -fR postfix:postfix /opt/zimbra/postfix/spool
	chown -fR root:postfix /opt/zimbra/postfix/conf
	chown -f root /opt/zimbra/postfix/spool

	chmod 777 /opt/zimbra/postfix/conf
	chmod -fR 644 /opt/zimbra/postfix/conf/*
	chmod -f 755 /opt/zimbra/postfix/conf/postfix-script
	chmod -f 755 /opt/zimbra/postfix/conf/post-install

	# Postfix specific permissions
	if [ -d /opt/zimbra/postfix/spool/public ]; then
		chgrp -f postdrop /opt/zimbra/postfix/spool/public
	fi
	if [ -d /opt/zimbra/postfix/spool/maildrop ]; then
		chgrp -f postdrop /opt/zimbra/postfix/spool/maildrop
	fi
	if [ -d /opt/zimbra/postfix/sbin ]; then
		chgrp -f postdrop /opt/zimbra/postfix/sbin/postqueue
		chgrp -f postdrop /opt/zimbra/postfix/sbin/postdrop
		chmod -f g+s /opt/zimbra/postfix/sbin/postqueue
		chmod -f g+s /opt/zimbra/postfix/sbin/postdrop
	fi

fi

if [ -d /opt/zimbra/cyrus-sasl-2.1.21.ZIMBRA ]; then
	chown root:zimbra /opt/zimbra/cyrus-sasl-2.1.21.ZIMBRA
	mkdir -p /opt/zimbra/cyrus-sasl-2.1.21.ZIMBRA/state
	chown zimbra:zimbra /opt/zimbra/cyrus-sasl-2.1.21.ZIMBRA/state
fi

if [ -d /opt/zimbra/clamav-0.88 ]; then
	chown zimbra:zimbra /opt/zimbra/clamav-0.88
fi

if [ -f /opt/zimbra/openldap/libexec/slapd ]; then
	chown root:$ROOTGROUP /opt/zimbra/openldap/libexec/slapd
	chmod 755 /opt/zimbra/openldap/libexec/slapd
fi

if [ -f /opt/zimbra/libexec/ZmSetup.app/Contents/MacOS/ZmSetup ]; then
	chown root:$ROOTGROUP /opt/zimbra/libexec/ZmSetup.app/Contents/MacOS/ZmSetup
	chmod 544 /opt/zimbra/libexec/ZmSetup.app/Contents/MacOS/ZmSetup
fi

