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
import threading
import time

class MiscConfig(config.Config):
	def load(self):
		self.loaded = True
		self.config = {}

		t1 = time.clock()
		th = []
		for cm in commands.miscCommands:
			th.append(threading.Thread(target=MiscConfig.doCommand,args=(self,cm),name=cm))
		
		[t.start() for t in th]
		[t.join() for t in th]
		dt = time.clock()-t1
		Log.logMsg(5,"Miscconfig loaded in %.2f seconds" % dt)


	def doCommand(self, cm):
		c = commands.commands[cm]
		rc = c.execute();
		if (rc != 0):
			Log.logMsg(1, "Skipping "+c.desc+" update.");
			Log.logMsg(1, str(c));
			return None

		# lines = c.output.splitlines()

		# if no output was returned we have a potential avoid stopping all services
		if (len(c.output) == 0):
			Log.logMsg(2, "Skipping " + c.desc + " No data returned.")
			c.status = 1
			return

		self[c.name] = ' '.join(c.output)
		Log.logMsg(5, "%s=%s" % (cm, self[cm]));
		# v = ' '.join(lines)
		# self[cm] = self[cm] and (self[cm] + " " + v) or v

