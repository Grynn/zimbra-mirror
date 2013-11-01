#
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2010, 2011, 2012, 2013 Zimbra Software, LLC.
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.4 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# ***** END LICENSE BLOCK *****
#


from logmsg import *
import commands
import config
import re
import time

class ServerConfig(config.Config):
	def getServices(self, key=None):
		if key is not None:
			if key == "mailboxd":
				key = "mailbox"
			Log.logMsg(5, "Checking service %s in services %s (%s)" % (key, self.serviceconfig, key in self.serviceconfig.keys()))
			return key in self.serviceconfig
		return self.serviceconfig.iterkeys()
                
	def load(self, hostname):
		if (hostname is None):
			raise Exception, "Hostname required"
		self.loaded = True

		t1 = time.clock()
		c = commands.commands["gs"]
		rc = c.execute((hostname,));
		if (rc != 0):
			Log.logMsg(1, "Skipping %s update." % c.desc);
			Log.logMsg(1, str(c));
			return None

		# if no output was returned we have a potential avoid stopping all services
		if (len(c.output) == 0):
			Log.logMsg(2, "Skipping %s - No data returned." % c.desc)
			c.status = 1
			return None

		self.config = dict([(e.getKey(), e.getValue()) for e in sorted(c.output, key=lambda x: x.getKey())])

		if self["zimbraSSLExcludeCipherSuites"] is not None:
			v = self["zimbraSSLExcludeCipherSuites"]
			v = str(v)
			self["zimbraSSLExcludeCipherSuites"] = ' '.join(sorted(v.split(), key=str.lower))
			self["zimbraSSLExcludeCipherSuitesXML"] = '\n'.join([''.join(('<Item>',val,'</Item>')) for val in self["zimbraSSLExcludeCipherSuites"].split()])

		if self["zimbraMtaMyNetworks"] is not None:
			self["zimbraMtaMyNetworksPerLine"] = '\n'.join([''.join((val,'')) for val in self["zimbraMtaMyNetworks"].split()])

		if self["zimbraServiceEnabled"] is not None:
			for v in self["zimbraServiceEnabled"].split():
				self.serviceconfig[v] = "zimbraServiceEnabled"
				if (v == "mailbox"):
					self.serviceconfig["mailboxd"] = "zimbraServiceEnabled"
				elif (v == "mta"):
					self.serviceconfig["sasl"] = "zimbraServiceEnabled"

		if self["zimbraMtaRestriction"] is not None:
			# Remove all the reject_rbl_client lines from MTA restriction and put the values in RBLs
			q = re.sub(r'reject_rbl_client\s+\S+\s+','',self["zimbraMtaRestriction"])
			p = re.findall(r'reject_rbl_client\s+(\S+)',self["zimbraMtaRestriction"])
			self["zimbraMtaRestriction"] = q
			self["zimbraMtaRestrictionRBLs"] = ', '.join(p)
			# Remove all the reject_rhsbl_client lines from MTA restriction and put the values in RBLs
			q = re.sub(r'reject_rhsbl_client\s+\S+\s+','',self["zimbraMtaRestriction"])
			p = re.findall(r'reject_rhsbl_client\s+(\S+)',self["zimbraMtaRestriction"])
			self["zimbraMtaRestriction"] = q
			self["zimbraMtaRestrictionRHSBLCs"] = ', '.join(p)
			# Remove all the reject_rhsbl_sender lines from MTA restriction and put the values in RBLs
			q = re.sub(r'reject_rhsbl_sender\s+\S+\s+','',self["zimbraMtaRestriction"])
			p = re.findall(r'reject_rhsbl_sender\s+(\S+)',self["zimbraMtaRestriction"])
			self["zimbraMtaRestriction"] = q
			self["zimbraMtaRestrictionRHSBLSs"] = ', '.join(p)
			# Remove all the reject_rhsbl_reverse_client lines from MTA restriction and put the values in RBLs
			q = re.sub(r'reject_rhsbl_reverse_client\s+\S+\s+','',self["zimbraMtaRestriction"])
			p = re.findall(r'reject_rhsbl_reverse_client\s+(\S+)',self["zimbraMtaRestriction"])
			self["zimbraMtaRestriction"] = q
			self["zimbraMtaRestrictionRHSBLRCs"] = ', '.join(p)

		if self["zimbraIPMode"] is not None:
			self["zimbraIPv4BindAddress"] = "127.0.0.1"
			v = self["zimbraIPMode"]
			v = str(v)
			v = v.lower()
			if v == "ipv4":
				self["zimbraUnboundBindAddress"] = "127.0.0.1"
				self["zimbraLocalBindAddress"] = "127.0.0.1"
				self["zimbraPostconfProtocol"] = "ipv4"
				self["zimbraAmavisListenSockets"] = "'10024','10026','10032'"
				self["zimbraInetMode"] = "inet"
				if self["zimbraMilterBindAddress"] is None:
					self["zimbraMilterBindAddress"] = "127.0.0.1"
			if v == "ipv6":
				self["zimbraUnboundBindAddress"] = "::1"
				self["zimbraLocalBindAddress"] = "::1"
				self["zimbraPostconfProtocol"] = "ipv6"
				self["zimbraAmavisListenSockets"] = "'[::1]:10024','[::1]:10026','[::1]:10032'"
				self["zimbraInetMode"] = "inet6"
				if self["zimbraMilterBindAddress"] is None:
					self["zimbraMilterBindAddress"] = "[::1]"
			if v == "both":
				self["zimbraUnboundBindAddress"] = "127.0.0.1 ::1"
				self["zimbraLocalBindAddress"] = "::1"
				self["zimbraPostconfProtocol"] = "all"
				self["zimbraAmavisListenSockets"] = "'10024','10026','10032','[::1]:10024','[::1]:10026','[::1]:10032'"
				self["zimbraInetMode"] = "inet6"
				if self["zimbraMilterBindAddress"] is None:
					self["zimbraMilterBindAddress"] = "[::1]"

		milter = None
		if (self["zimbraMilterServerEnabled"] == "TRUE"):
			milter = "inet:%s:%s" % (self["zimbraMilterBindAddress"],self["zimbraMilterBindPort"])

		if self["zimbraMtaSmtpdMilters"] is not None and milter is not None:
			self["zimbraMtaSmtpdMilters"] = "%s, %s" % (self["zimbraMtaSmtpdMilters"], milter)
		elif self["zimbraMtaSmtpdMilters"] is None and milter is not None:
			self["zimbraMtaSmtpdMilters"] = milter

		if self["zimbraMtaAliasMaps"] is not None:
			v = self["zimbraMtaAliasMaps"]
			v = str(v)
			self["zimbraMtaAliasMaps"] = ', '.join(v.split())

		if self["zimbraMtaHeaderChecks"] is not None:
			v = self["zimbraMtaHeaderChecks"]
			v = str(v)
			self["zimbraMtaHeaderChecks"] = ', '.join(v.split())

		if self["zimbraMtaImportEnvironment"] is not None:
			v = self["zimbraMtaImportEnvironment"]
			v = str(v)
			self["zimbraMtaImportEnvironment"] = ', '.join(v.split())

		if self["zimbraMtaLmtpConnectionCacheDestinations"] is not None:
			v = self["zimbraMtaLmtpConnectionCacheDestinations"]
			v = str(v)
			self["zimbraMtaLmtpConnectionCacheDestinations"] = ', '.join(v.split())

		if self["zimbraMtaLmtpHostLookup"] is not None:
			v = self["zimbraMtaLmtpHostLookup"]
			v = str(v)
			self["zimbraMtaLmtpHostLookup"] = ', '.join(v.split())

		if self["zimbraMtaSmtpSaslMechanismFilter"] is not None:
			v = self["zimbraMtaSmtpSaslMechanismFilter"]
			v = str(v)
			self["zimbraMtaSmtpSaslMechanismFilter"] = ', '.join(v.split())

		if self["zimbraMtaSmtpSaslPasswordMaps"] is not None:
			v = self["zimbraMtaSmtpSaslPasswordMaps"]
			v = str(v)
			self["zimbraMtaSmtpSaslPasswordMaps"] = ', '.join(v.split())

		if self["zimbraMtaNotifyClasses"] is not None:
			v = self["zimbraMtaNotifyClasses"]
			v = str(v)
			self["zimbraMtaNotifyClasses"] = ', '.join(v.split())

		if self["zimbraMtaPropagateUnmatchedExtensions"] is not None:
			v = self["zimbraMtaPropagateUnmatchedExtensions"]
			v = str(v)
			self["zimbraMtaPropagateUnmatchedExtensions"] = ', '.join(v.split())

		if self["zimbraMtaSenderCanonicalMaps"] is not None:
			v = self["zimbraMtaSenderCanonicalMaps"]
			v = str(v)
			self["zimbraMtaSenderCanonicalMaps"] = ', '.join(v.split())

		if self["zimbraMtaSmtpdSaslSecurityOptions"] is not None:
			v = self["zimbraMtaSmtpdSaslSecurityOptions"]
			v = str(v)
			self["zimbraMtaSmtpdSaslSecurityOptions"] = ', '.join(v.split())

		if self["zimbraMtaSmtpSaslSecurityOptions"] is not None:
			v = self["zimbraMtaSmtpSaslSecurityOptions"]
			v = str(v)
			self["zimbraMtaSmtpSaslSecurityOptions"] = ', '.join(v.split())

		if self["zimbraMtaSmtpdSaslTlsSecurityOptions"] is not None:
			v = self["zimbraMtaSmtpdSaslTlsSecurityOptions"]
			v = str(v)
			self["zimbraMtaSmtpdSaslTlsSecurityOptions"] = ', '.join(v.split())

		if self["zimbraMtaTransportMaps"] is not None:
			v = self["zimbraMtaTransportMaps"]
			v = str(v)
			self["zimbraMtaTransportMaps"] = ', '.join(v.split())

		if self["zimbraMtaVirtualAliasDomains"] is not None:
			v = self["zimbraMtaVirtualAliasDomains"]
			v = str(v)
			self["zimbraMtaVirtualAliasDomains"] = ', '.join(v.split())

		if self["zimbraMtaVirtualAliasMaps"] is not None:
			v = self["zimbraMtaVirtualAliasMaps"]
			v = str(v)
			self["zimbraMtaVirtualAliasMaps"] = ', '.join(v.split())

		if self["zimbraMtaVirtualMailboxDomains"] is not None:
			v = self["zimbraMtaVirtualMailboxDomains"]
			v = str(v)
			self["zimbraMtaVirtualMailboxDomains"] = ', '.join(v.split())

		if self["zimbraMtaVirtualMailboxMaps"] is not None:
			v = self["zimbraMtaVirtualMailboxMaps"]
			v = str(v)
			self["zimbraMtaVirtualMailboxMaps"] = ', '.join(v.split())

		dt = time.clock()-t1
		Log.logMsg(5,"Serverconfig loaded in %.2f seconds" % dt)
