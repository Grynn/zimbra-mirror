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

class LocalConfig(config.Config):
	def load(self):
		self.loaded = True
		self.config = {}

		t1 = time.clock()
		c = commands.commands["localconfig"]
		rc = c.execute();
		if (rc != 0):
			Log.logMsg(1, "Skipping "+c.desc+" update.");
			Log.logMsg(1, str(c));
			return None
		dt = time.clock()-t1
		Log.logMsg(5,"Localconfig fetched in %.2f seconds (%d entries)" % (dt,len(c.output)))

		if (len(c.output) == 0):
			Log.logMsg(2, "Skipping " + c.desc + " No data returned.")
			c.status = 1
			raise Exception, "Skipping " + c.desc + " No data returned."

		self.config = dict([(k,v) for (k,v) in c.output])

		# Set a default for this
		if self["zmmtaconfig_listen_port"] is None:
			self["zmmtaconfig_listen_port"] = "7171"

		dt = time.clock()-t1
		Log.logMsg(5,"Localconfig loaded in %.2f seconds" % dt)
