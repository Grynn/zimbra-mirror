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

class Config:
	def __init__(self):
		self.loaded = False
		self.config = {}

	def __setitem__(self,key,val):
		self.config[key] = val
		return self.config[key]

	def __getitem__(self,key):
		if key in self.config:
			val = self.config[key]
			if isinstance (val, basestring):
				return val
			else:
				return " ".join(val)
		else:
			return None

	def __contains__(self,key):
		return key in self.config

	def load(self):
		self.loaded = True
		self.config = {}

	def dump(self):
		for k in sorted(self.config.iterkeys()):
			print "%s = %s" % (k, self[k])
