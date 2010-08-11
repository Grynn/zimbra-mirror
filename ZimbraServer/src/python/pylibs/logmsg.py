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

lvlmap = {
	5	: logging.DEBUG,
	4	: logging.INFO,
	3	: logging.WARNING,
	2	: logging.ERROR,
	1	: logging.CRITICAL,
	0	: logging.FATAL,
	}

class Log:
	loghandler = None
	sysloghandler = None

	@classmethod
	def initLogging(cls, c = None):
		if c:
			cls.cf = c
			if cls.cf.loglevel > 5:
				cls.cf.loglevel = 5
		else:
			cls.cf = conf.Config()

		fmt = logging.Formatter("%(asctime)s %(name)s %(levelname)s [%(process)d-%(threadName)s] %(message)s")
		sfmt = logging.Formatter("%(name)s %(levelname)s [%(process)d-%(threadName)s] %(message)s")

		cls.logger = logging.getLogger('zmconfigd')
		cls.logger.setLevel(logging.DEBUG)
		if (cls.loghandler):
			cls.logger.removeHandler(cls.loghandler)
		cls.loghandler = logging.handlers.RotatingFileHandler("/opt/zimbra/log/zmconfigd.log",maxBytes=10000000,backupCount=5)
		cls.loghandler.setFormatter(fmt)
		cls.loghandler.setLevel(lvlmap[cls.cf.loglevel])
		cls.logger.addHandler(cls.loghandler)

		if (cls.sysloghandler):
			cls.logger.removeHandler(cls.sysloghandler)
		cls.sysloghandler = logging.handlers.SysLogHandler(('localhost',514),logging.handlers.SysLogHandler.LOG_LOCAL0)
		cls.sysloghandler.setFormatter(sfmt)
		cls.sysloghandler.setLevel(logging.CRITICAL)

		cls.logger.addHandler(cls.sysloghandler)

	@classmethod
	def logMsg(cls, lvl, msg):

		if lvl > 5:
			lvl = 5
		msg = re.sub(r"\s|\n", " ", msg)
		# print "Logging at %d (%d)" % (lvl, lvlmap[lvl])
		cls.logger.log( lvlmap[lvl], msg) 

		if lvl == 0:
			cls.logger.log( lvlmap[lvl], "%s: shutting down" % (cls.cf.progname,) )
			os._exit(1)

Log.initLogging()
