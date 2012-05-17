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

import os
import conf
import sys
import logging
import time
import logging
import logging.handlers
import re
import threading

from org.productivity.java.syslog4j import Syslog
from org.productivity.java.syslog4j import SyslogIF
from org.productivity.java.syslog4j import SyslogConstants
from org.productivity.java.syslog4j.impl.unix import UnixSyslog
from org.productivity.java.syslog4j.impl.unix.socket import UnixSocketSyslogConfig

class Log:
	zmconfigdSyslogInstance = UnixSocketSyslogConfig(SyslogConstants.FACILITY_LOCAL0, "/dev/log")
	zmsyslog = Syslog.createInstance("zmSyslog",zmconfigdSyslogInstance)
	zmsyslog.getConfig().setLocalName("zmconfigd[%d]:" % os.getpid())

	@classmethod
	def initLogging(cls, c = None):
		if c:
			cls.cf = c
			if cls.cf.loglevel > 5:
				cls.cf.loglevel = 5
		else:
			cls.cf = conf.Config()

	@classmethod
	def logMsg(cls, lvl, msg):

		if lvl > 5:
			lvl = 5
		msg = re.sub(r"\s|\n", " ", msg)

		if lvl <= cls.cf.loglevel:
			Log.zmsyslog.log(lvl, msg)

		if lvl == 0:
			Log.zmsyslog.log(2, "%s: shutting down" % (cls.cf.progname,) )
			os._exit(1)

Log.initLogging()
