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
			Log.logMsg(5, "Checking service %s in services %s (%s)" % (key, self["zimbraServiceEnabled"], key in self["zimbraServiceEnabled"]))
			return key in self["zimbraServiceEnabled"]
		return self["zimbraServiceEnabled"].split(' ')

	def load(self, hostname):
		if (hostname is None):
			raise Exception, "Hostname required"
		self.loaded = True
		self.config = {}
		self.serviceconfig = {}

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
			self["zimbraSSLExcludeCipherSuitesXML"] = '\n'.join([''.join(('<Item>',val,'</Item>')) for val in self["zimbraSSLExcludeCipherSuites"].split()])

		if self["zimbraServiceEnabled"] is not None:
			for v in self["zimbraServiceEnabled"].split():
				self.serviceconfig[v] = "zimbraServiceEnabled"
				if (v == "mailbox"):
					self.serviceconfig["mailboxd"] = "zimbraServiceEnabled"
				elif (v == "mta"):
					self.serviceconfig["sasl"] = "zimbraServiceEnabled"

		milter = None
		if (self["zimbraMilterServerEnabled"] == "TRUE"):
			if self["zimbraMilterBindAddress"] is None:
				self["zimbraMilterBindAddress"] = "127.0.0.1"
			milter = "inet:%s:%s" % (self["zimbraMilterBindAddress"],self["zimbraMilterBindPort"])

		if self["zimbraMtaSmtpdMilters"] is not None and milter is not None:
			self["zimbraMtaSmtpdMilters"] = "%s, %s" % (self["zimbraMtaSmtpdMilters"], milter)
		elif self["zimbraMtaSmtpdMilters"] is None and milter is not None:
			self["zimbraMtaSmtpdMilters"] = milter

		dt = time.clock()-t1
		Log.logMsg(5,"Serverconfig loaded in %.2f seconds" % dt)
