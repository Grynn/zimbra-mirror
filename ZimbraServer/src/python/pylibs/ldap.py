#
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
#
# The contents of this file are subject to the Zimbra Public License
# Version 1.3 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
#
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# ***** END LICENSE BLOCK *****
#

import conf

import com.zimbra.cs.ldap.LdapClient;
import com.zimbra.cs.ldap.LdapUsage;
import com.zimbra.cs.ldap.ZAttributes;
import com.zimbra.cs.ldap.ZLdapContext;
import com.zimbra.cs.ldap.ZLdapFilter;
import com.zimbra.cs.ldap.ZLdapFilterFactory;
import com.zimbra.cs.ldap.ZMutableEntry;
import com.zimbra.cs.ldap.ZSearchControls;
import com.zimbra.cs.ldap.ZSearchResultEntry;
import com.zimbra.cs.ldap.ZSearchResultEnumeration;
import com.zimbra.cs.ldap.ZSearchScope;
import com.zimbra.cs.ldap.LdapException.LdapSizeLimitExceededException;
import com.zimbra.cs.ldap.LdapServerConfig.GenericLdapConfig;
import com.zimbra.cs.ldap.ZLdapFilterFactory.FilterId;
from logmsg import *

# (Key, DN, requires_master)
keymap = {
	"ldap_common_loglevel"			:	("olcLogLevel",		"cn=config", False),
	"ldap_common_threads"			:	("olcThreads",		"cn=config", False),
	"ldap_common_toolthreads"		:	("olcToolThreads",	"cn=config", False),
	"ldap_common_require_tls"		:	("olcSecurity",		"cn=config", False),
	"ldap_common_writetimeout"		:	("olcWriteTimeout",	"cn=config", False),

	"ldap_db_checkpoint"			:	("olcDbCheckpoint",		"olcDatabase={3}mdb,cn=config", False),
	"ldap_db_maxsize"			:	("olcDbMaxsize",			"olcDatabase={3}mdb,cn=config", False),

	"ldap_accesslog_checkpoint"		:	("olcDbCheckpoint",		"olcDatabase={2}mdb,cn=config", True),
	"ldap_accesslog_maxsize"		:	("olcDbMaxsize",			"olcDatabase={2}mdb,cn=config", True),

	"ldap_overlay_syncprov_checkpoint"	:	("olcSpCheckpoint",	"olcOverlay={0}syncprov,olcDatabase={3}mdb,cn=config", True),

	"ldap_overlay_accesslog_logpurge"	:	("olcAccessLogPurge", "olcOverlay={1}accesslog,olcDatabase={3}mdb,cn=config", True)
}

class Ldap:

	cf = None
	mLdapConfig = None
	master = False

	@classmethod
	def initLdap(cls, c=None):
		Log.logMsg(4, "Initializing ldap")
		if c:
			cls.cf = c
		else:
			raise Exception("Ldap not initialized")

	@classmethod
	def createLdapConfig(cls):
		Log.logMsg(5, "Creating ldap context")
		if cls.cf is None:
			raise Exception("Ldap not initialized")

		ldapUrl = "ldapi:///"
		bindDN = "cn=config"
		if cls.cf.ldap_starttls_required=="false":
			startTLSEnabled = False
		else:
			startTLSEnabled = True
		
		bindPassword = cls.cf.ldap_root_password
		cls.mLdapConfig = GenericLdapConfig(ldapUrl, startTLSEnabled, bindDN, bindPassword)

		if cls.cf.ldap_is_master:
			Log.logMsg(5, "Creating ldap context")
			atbase = "cn=accesslog"
			atfilter = "(objectClass=*)"
			atreturn = ['1.1']
			mLdapContext = LdapClient.getContext(cls.mLdapConfig, LdapUsage.SEARCH)
			zfilter = ZLdapFilterFactory.getInstance().fromFilterString(FilterId.ZMCONFIGD, atfilter)
			searchControls = ZSearchControls.createSearchControls(ZSearchScope.SEARCH_SCOPE_BASE, ZSearchControls.SIZE_UNLIMITED, atreturn);
			ne = mLdapContext.searchDir(base, zFilter, searchControls);
			if ne.hasMore():
				Log.logMsg(5, "Ldap config is master")
				cls.master = True
			LdapClient.closeContext(mLdapContext)

	@classmethod
	def getLdapContext(cls):
		if cls.mLdapConfig is None:
			cls.createLdapConfig()
		return cls.mLdapContext

	@classmethod
	def modify_attribute(cls, key, value):
		(attr, dn, xform) = Ldap.lookupKey(key, cls.master)
		if attr is not None:
			v = xform % (value,)
			Log.logMsg(4, "Setting %s to %s" % (key, v))
			mLdapContext = LdapClient.getContext(cls.mLdapConfig, LdapUsage.MOD)
			mEntry = LdapClient.createMutableEntry()
			mEntry.setAttr(attr, v)
			mLdapContext.replaceAttributes(dn, mEntry.getAttributes())
			LdapClient.closeContext(mLdapContext)

	@classmethod
	def lookupKey(cls, key, master):
		if key in keymap:
			(attr, dn, requires_master) = keymap[key]
			if re.match("ldap_db_", key) and not cls.master:
				dn = "olcDatabase={2}mdb,cn=config"
				
			xform = "%s"
			if key == "ldap_common_require_tls":
				xform = "ssf=%s"
			if requires_master and not cls.master:
				Log.logMsg(2, "LDAP: Trying to modify key: %s when not a master" % (key,))
				return (None, None, None)
			else:
				Log.logMsg(5, "Found key %s and dn %s for %s (%s)" % (attr, dn, key, cls.master))
				return (attr, dn, xform)
		else:
			Log.logMsg(1, "UNKNOWN KEY %s" % (key,))

		raise Exception("Key error")
