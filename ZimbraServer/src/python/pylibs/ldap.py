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

TODO this doesn't work, the jython ldap implementation doesn't support ldapi access.  
TODO we use the external perl libraries instead.

import conf

from logmsg import *
from java.util import Hashtable
import sys
# Hack for standalone jython bug
sys.packageManager.makeJavaPackage("javax.naming", "*", None)
sys.packageManager.makeJavaPackage("javax.naming.directory", "*", None)
from javax.naming import Context
from javax.naming.directory import InitialDirContext

# (Key, DN, requires_master)
keymap = {
	"ldap_common_loglevel"		:	("olcLogLevel",		"cn=config", False),
	"ldap_common_threads"		:	("olcThreads",		"cn=config", False),
	"ldap_common_toolthreads"	:	("olcToolThreads",	"cn=config", False),
	"ldap_common_require_tls"	:	("olcSecurity",		"cn=config", False),
	"ldap_common_writetimeout"	:	("olcWriteTimeout",	"cn=config", False),

	"ldap_db_checkpoint"		:	("olcDbCheckpoint",		"olcDatabase={3}mdb,cn=config", False),
	"ldap_db_maxsize"			:	("olcDbMaxsize",			"olcDatabase={3}mdb,cn=config", False),

	"ldap_accesslog_checkpoint"		:	("olcDbCheckpoint",		"olcDatabase={2}mdb,cn=config", True),
	"ldap_accesslog_maxsize"			:	("olcDbMaxsize",			"olcDatabase={2}mdb,cn=config", True),

	"ldap_overlay_syncprov_checkpoint"	:	("olcSpCheckpoint",	"olcOverlay={0}syncprov,olcDatabase={3}mdb,cn=config", True),

	"ldap_overlay_accesslog_logpurge"	:	("olcAccessLogPurge", "olcOverlay={1}accesslog,olcDatabase={3}mdb,cn=config", True)
}

class Ldap:

	cf = None
	mLdapContext = None
	master = False

	@classmethod
	def initLdap(cls, c=None):
		Log.logMsg(4, "Initializing ldap")
		if c:
			cls.cf = c
			cls.createLdapContext()
		else:
			raise Exception("Ldap not initialized")

	@classmethod
	def createLdapContext(cls):
		Log.logMsg(5, "Creating ldap context")
		if cls.cf is None:
			raise Exception("Ldap not initialized")

		env=Hashtable()
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory")
		env.put(Context.PROVIDER_URL, 'ldapi:///opt/zimbra/openldap/var/run/ldapi/') 
		env.put(Context.SECURITY_AUTHENTICATION, "simple")
		env.put(Context.SECURITY_PRINCIPAL, "cn=config")
		env.put(Context.SECURITY_CREDENTIALS, cls.cf.ldap_root_password)
		cls.mLdapContext = InitialDirContext(env)

		if cls.cf.ldap_is_master:
			#s = SearchControls()
			#s.setSearchScope(SearchControls.OBJECT_SCOPE)
			Log.logMsg(5, "Creating ldap context")
			ats = BasicAttributes("objectClass", None)
			atr = ['1.1']
			results = cls.mLdapContext.search("cn=accesslog", ats, atr)
			if results.hasMore():
				Log.logMsg(5, "Ldap config is master")
				cls.master = True

	@classmethod
	def getLdapContext(cls):
		if cls.mLdapContext is None:
			cls.createLdapContext()
		return cls.mLdapContext

	@classmethod
	def modify_attribute(cls, key, value):
		(attr, dn, xform) = Ldap.lookupKey(key, cls.master)
		if attr is not None:
			v = xform % (value,)
			Log.logMsg(4, "Setting %s to %s" % (key, v))
			myAttrs = BasicAttributes(attr, v, True)
			cls.ctx.modifyAttributes(dn, DirContext.REPLACE_ATTRIBUTE, myAttrs)

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
