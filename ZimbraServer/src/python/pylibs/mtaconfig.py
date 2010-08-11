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
import re
import time

class Section():
	def __init__(self, name=None):
		self.name		= name
		self.changed = False
		self.config = {
						"depends"    : {},
						"rewrites"   : {},
						"restarts"   : {},
						"ldap"       : {},
						"configkeys" : {},
						"requiredvars" : {},
						"postconf"   : {},
					}

	def depends(self, key=None, val=None):
		if val is not None:
			self.config["depends"][key] = val
		if key is not None:
			if key in self.config["depends"]:
				return self.config["depends"][key]
			return None
		return self.config["depends"]

	def rewrites(self, key=None, val=None, mode=None):
		if val is not None:
			self.config["rewrites"][key] = (val,mode)
		if key is not None:
			if key in self.config["rewrites"]:
				return self.config["rewrites"][key]
			return None
		return self.config["rewrites"]

	def restarts(self, service=None, val=None):
		if val is not None:
			self.config["restarts"][service] = val
		if service is not None:
			if service in self.config["restarts"]:
				return self.config["restarts"][service]
			return None
		return self.config["restarts"]

	def requiredvars(self, var=None, val=None):
		if val is not None:
			self.config["requiredvars"][var] = val
		if var is not None:
			if var in self.config["requiredvars"]:
				return self.config["requiredvars"][var]
			return None
		return self.config["requiredvars"]

	def postconf(self, key=None, val=None):
		if val is not None:
			self.config["postconf"][key] = val
		if key is not None:
			if key in self.config["postconf"]:
				return self.config["postconf"][key]
			return None
		return self.config["postconf"]

	def ldap(self, key=None, val=None):
		if val is not None:
			self.config["ldap"][key] = val
		if key is not None:
			if key in self.config["ldap"]:
				return self.config["ldap"][key]
			return None
		return self.config["ldap"]

class MtaConfig():
	def __init__(self):
		self.sections = {}
		self.sectionMap = {
				"amavis"     : "mta",
				"sasl"       : "mta",
				"webxml"     : "mailbox",
				"nginx"      : "imapproxy",
				}

	def getSection(self,name):
		return self.sections[name]

	def getSections(self):
		return self.sections

	def addSection(self, section):
		self.sections[section.name] = section

	def getServiceMap(self, sname):
		if self.sectionMap.has_key(sname):
			return self.sectionMap[sname]
		return sname

	def load(self, cf, state):
		self.loaded = True
		self.config = {}

		t1 = time.clock()
		lines = open(cf,'r').readlines()
			
		if (len(lines) == 0):
			raise Exception, "Empty config file cf"

		i = 0
		while i < len(lines):
			if lines[i] == "":
				continue

			# No strip() required for zero-arg split()
			fields = lines[i].split()

			section = Section()

			if re.match(r"SECTION", lines[i]):
				section.name = fields[1]
				servicemap = self.getServiceMap(section.name)

			i += 1

			# the previous version continued to add the section to the list, with
			# no data; this resulted in the forced run of proxyconfgen even when imapproxy
			# was disabled.  Probably a bug, not replicating.
			if not state.checkConditional("SERVICE", servicemap):
				Log.logMsg(4, "Service %s is not enabled.  Skipping %s" % (servicemap, section.name))
				while i < len(lines) and (not re.match(r"SECTION", lines[i])):
					i += 1
				continue

			if len(fields) > 2 and fields[2] == "DEPENDS":
				for f in fields[2:]:
					section.depends(f, True)
				
			# Process the entire section
			while i < len(lines) and (not re.match(r"SECTION", lines[i])):
				if lines[i].strip() == "":
					i+=1
					continue

				fields = lines[i].split()
				ln = lines[i].strip()
				if re.match(r"REWRITE", ln):
					Log.logMsg(5, "Adding file rewrite %s to section %s" % (fields[1], section.name))
					if len(fields) > 3:
						section.rewrites(fields[1], fields[2], fields[4])
					else:
						section.rewrites(fields[1], fields[2])
				elif re.match(r"RESTART", ln):
					for service in fields[1:]:
						Log.logMsg(5, "Adding service %s to restarts in section %s" % (service, section.name)); 
						section.restarts(service, True)
				elif re.match(r"VAR|LOCAL", ln):
					Log.logMsg(5, "Adding %s to required vars:  processing %s" % (fields[1], ln));
					section.requiredvars(fields[1], fields[0])
				elif re.match(r"POSTCONF", ln):
					if len(fields) > 2:
						if (re.match(r"VAR|LOCAL|FILE", fields[2])):
							val = state.lookUpConfig(fields[2], fields[3])
							section.requiredvars(fields[3], fields[2])
							if val is not None:
								if (str(val).upper() == "TRUE"):
									val = "yes"
								if (str(val).upper() == "FALSE"):
									val = "no"
							else:
								val = ""
							Log.logMsg(5, "Adding to postconf commands: \'%s\' %s=\'%s\'" % (ln, fields[1], val))
							section.postconf(fields[1],val)
						else:
							section.postconf(fields[1],fields[2])
					else:
						section.postconf(fields[1],"")
				elif re.match(r"PROXYGEN", ln):
					# ignore this; proxygen hardcoded in the imapproxy section logic
					pass
				elif re.match(r"LDAP", ln):
					if (re.match(r"LOCAL", fields[2])):
						val = state.lookUpConfig(fields[2], fields[3])
						Log.logMsg(5, "Adding to ldap commands: \'%s\' %s=\'%s\'" % (ln, fields[1], val))
						section.ldap(fields[1],val)
				elif re.match(r"if", ln):
					if state.checkConditional(fields[1], fields[2]):
						Log.logMsg(5, "checkConditional %s %s is true" % (fields[1], fields[2]));
						i += 1
						while i < len(lines) and (not re.match(r"fi", lines[i].strip())):
							if lines[i].strip() == "":
								i += 1
								continue
							fields = lines[i].split()
							ln = lines[i].strip()
							if re.match(r"POSTCONF", ln):
								if len(fields) > 2:
									if (re.match(r"VAR|LOCAL|FILE", fields[2])):
										val = state.lookUpConfig(fields[2], fields[3])
										section.requiredvars(fields[3], fields[2])
										Log.logMsg(5, "Adding to postconf commands: \'%s\' %s=\'%s\'" % (ln, fields[1], val))
										section.postconf(fields[1],val)
									else:
										section.postconf(fields[1],fields[2])
								else:
									section.postconf(fields[1],"")
							elif re.match(r"VAR|LOCAL", ln):
								Log.logMsg(5, "Adding %s to required vars:  processing %s" % (fields[1], ln));
								section.requiredvars(fields[1], fields[0])
							else:
								Log.logMsg(2, "Error processing line %s" % (lines[i],));
							i += 1
					else:
						Log.logMsg(5, "checkConditional %s %s is false: \'%s\'" % (fields[1], fields[2], lines[i]));
						while i < len(lines) and (not re.match(r"fi", lines[i].strip())):
							Log.logMsg(5, "Skipping line=\'%s\'" % (lines[i],));
							i += 1
				elif re.match(r"fi", ln):
					Log.logMsg(5, "endof conditional reached");
					continue
				else:
					Log.logMsg(2, "Unknown line format %s" % (lines[i],));

				i+=1
			
			self.addSection(section)

		dt = time.clock()-t1
		Log.logMsg(5,"zmmta.cf loaded in %.2f seconds" % dt)
