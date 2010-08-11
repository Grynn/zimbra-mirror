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


import localconfig
import commands
import globalconfig
import miscconfig
import serverconfig
import mtaconfig
from logmsg import *
import config
import shutil
import tempfile
import re
import threading
import time
import traceback

class State:
	
	lConfig = threading.Lock()
	lAction = threading.Condition()
	mState = None

	falseSet = (None,"no",0,"")

	startorder = {
		"ldap"      : 0,
		"logger"    : 10,
		"convertd"  : 20,
		"mailbox"   : 30,
		"memcached" : 40,
		"imapproxy" : 50,
		"antispam"  : 60,
		"antivirus" : 70,
		"amavis"    : 75,
		"archiving" : 80,
		"snmp"      : 90,
		"spell"     : 100,
		"mta"       : 110,
		"stats"     : 120,
		}

	def __init__(self):
		self.baseDir		  = "/opt/zimbra"
		self.pidFile		  = "/opt/zimbra/log/zmconfigd.pid"
		self.hostname		  = None
		self.firstRun         = True
		self.forced           = False
		self.localconfig      = localconfig.LocalConfig()
		self.globalconfig     = globalconfig.GlobalConfig()
		self.miscconfig       = miscconfig.MiscConfig()
		self.serverconfig     = serverconfig.ServerConfig()
		self.forcedconfig     = {}
		self.fileCache 		  = {}
		self.watchdog         = {
							  }
		self.changedkeys	  = {}
		self.lastVals		  = {}
		self.previous         = {
								"rewrites"   : {},
								"config"     : {},
								# "restarts"   : {}, Don't need this, I think
								"postconf"   : {},
								"services"   : {},
								"ldap"       : {},
								"proxygen"   : False # 0|1
							  }
		self.current          = {
								"rewrites"   : {},
								"config"     : {},
								"restarts"   : {},
								"postconf"   : {},
								"services"   : {},
								"ldap"       : {},
								"proxygen"   : False # 0|1
							  }

		self.mtaconfig         = mtaconfig.MtaConfig()

	def isFalseValue(self,val):
		return val in self.falseSet

	def isTrueValue(self,val):
		return not self.isFalseValue(val)

	def delWatchdog(self, process):
		try:
			del self.watchdog[process]
		except:
			return

	def getWatchdog(self, service, state=None):
		if state is not None:
			self.watchdog[service] = state
		try:
			return self.watchdog[service]
		except Exception, e:
			return None

	def proxygen(self, val=None):
		if val is not None:
			self.current["proxygen"] = val
		return self.current["proxygen"]

	def delRestart(self, service):
		try:
			del self.current["restarts"][service]
		except Exception:
			return

	def delLdap(self, service):
		try:
			del self.current["ldap"][service]
		except Exception:
			return

	def clearPostconf(self):
		try:
			self.current["postconf"] = {}
		except Exception:
			return

	def delPostconf(self, service):
		try:
			del self.current["postconf"][service]
		except Exception:
			return

	def delRewrite(self, service):
		try:
			del self.current["rewrites"][service]
		except Exception:
			return

	def curRewrites(self, service=None, state=None):
		# state should be a tuple (val, mode)
		if service is not None:
			if state is not None:
				Log.logMsg(5, "Adding rewrite %s" % (service,))
				self.current["rewrites"][service] = state
			try:
				return self.current["rewrites"][service]
			except Exception, e:
				return None
		return self.current["rewrites"]

	def curRestarts(self, service=None, state=None):
		if service is not None:
			if state is not None:
				self.current["restarts"][service] = state
			try:
				return self.current["restarts"][service]
			except Exception, e:
				return None
		return self.current["restarts"]

	def curLdap(self, key=None, val=None):
		if key is not None:
			if val is not None:
				Log.logMsg(5, "Adding ldap %s = %s" % (key, val))
				self.current["ldap"][key] = val
			try:
				return self.current["ldap"][key]
			except Exception, e:
				return None
		return self.current["ldap"]

	def curPostconf(self, key=None, val=None):
		if key is not None:
			if val is not None:
				if val == True:
					val = "yes"
				elif val == False:
					val = "no"
				Log.logMsg(5, "Adding postconf %s = %s" % (key, val))
				self.current["postconf"][key] = val
			try:
				return self.current["postconf"][key]
			except Exception, e:
				return None
		return self.current["postconf"]

	def curServices(self, service=None, state=None):
		if service is not None:
			if state is not None:
				self.current["services"][service] = state
			try:
				return self.current["services"][service]
			except Exception, e:
				return None
		return self.current["services"]

	def prevServices(self, service=None, state=None):
		if service is not None:
			if state is not None:
				self.previous["services"][service] = state
			try:
				return self.previous["services"][service]
			except Exception, e:
				return None
		return self.previous["services"]

	def getAllConfigs(self, cf=None):
		t1 = time.clock()

		# These loading commands really aren't reentrant safe, so we'll need to make sure
		# we only access them from here.  Any rewrite request via interrupt shouldn't require
		# a config reload, anyway.
		Log.logMsg (5, "LOCK myState.lConfig requested")
		State.lConfig.acquire()
		Log.logMsg (5, "LOCK myState.lConfig acquired")
		commands.Command.resetProvisioning("config")
		commands.Command.resetProvisioning("server")
		commands.Command.resetProvisioning("local")
		self.fileCache = {}
		lc = threading.Thread(target=State.getLocalConfig,args=(self,cf),name="lc")
		gc = threading.Thread(target=State.getGlobalConfig,args=(self,),name="gc")
		mc = threading.Thread(target=State.getMiscConfig,args=(self,),name="mc")
		sc = threading.Thread(target=State.getServerConfig,args=(self,),name="sc")
		lc.start()
		gc.start()
		mc.start()
		sc.start()
		lc.join()
		gc.join()
		mc.join()
		sc.join()
		State.lConfig.release()
		Log.logMsg (5, "LOCK myState.lConfig released")

		dt = time.clock() - t1
		Log.logMsg(3, "All configs fetched in %.2f seconds" % (dt,))

	def getLocalConfig(self, cf = None):
		self.localconfig.load()
		self.hostname = self.localconfig["zimbra_server_hostname"]
		if cf:
			cf.setVals(self)

	def getGlobalConfig(self):
		self.globalconfig.load()

	def getMiscConfig(self):
		self.miscconfig.load()

	def getServerConfig(self):
		self.serverconfig.load(self.hostname)

	def getMtaConfig(self,cf=None):
		self.mtaconfig.load(cf,self)

	def lookUpConfig(self, type, key):
		if re.match(r"!",key):
			return self.checkConditional(type, key)
		Log.logMsg(5, "Looking up key=%s with type=%s" % (key, type))
		
		value = None

		if type == "VAR":
			if key in self.globalconfig:
				value = self.globalconfig[key]
			if key in self.miscconfig:
				value = self.miscconfig[key]
			if key in self.serverconfig:
				value = self.serverconfig[key]
		elif type == "LOCAL":
			value = self.localconfig[key]
		elif type == "FILE":
			# Do this all in memory
			if not key in self.fileCache:
				tmpfile = os.path.join(self.baseDir,"conf", key)
				lines = [self.transform(l).strip() for l in open(tmpfile,'r').readlines() if self.transform(l).strip()]
				value = ', '.join(lines)
				self.fileCache[key] = value
				Log.logMsg(5, "Loaded %s = %s" % (key, value))
			else:
				value = self.fileCache[key]
				Log.logMsg(5, "Loaded from cache %s = %s" % (key, value))
		elif type == "SERVICE":
			value = self.serverconfig.getServices(key)
		else:
			Log.logMsg(2, "Unknown config type %s for key %s" % (type, key))

		Log.logMsg(5, "Looked up key=%s with type=%s value=%s" % (key, type, value))
		return value

	def checkConditional(self, type, key):
		negate = False
		Log.logMsg(5, "Conditional Entry: key=%s type=%s negate=%s" % (key, type, negate))
		if re.match(r'!', key):
			negate = True
		key = re.sub(r'^!','',key)
		Log.logMsg(5, "Conditional After Negate Check: key=%s type=%s negate=%s" % (key, type, negate))
		value = self.lookUpConfig(type, key)
		Log.logMsg(5, "Conditional After lookUpConfig: key=%s val=%s type=%s negate=%s" % (key, value, type, negate))
		Log.logMsg(5, "Checking conditional for negate=%s type=%s %s=%s" % (negate, type, key, value))
		if (not value or re.match(r"no|false|0+",str(value),re.I)):
			rvalue = False
		else:
			rvalue = True
		if negate:
			rvalue = not rvalue
		Log.logMsg(5, "Checking conditional for negate=%s type=%s %s=%s return=%s" % (negate, type, key, value, rvalue))
		return rvalue

	def isrunning(self):
		cpid = self.getpid()
		if cpid:
			# python throws if pid does't exist, jython returns -1
			try:
				if (os.kill(cpid,0) != -1):
					Log.logMsg(1, "zmconfigd already running at %d" % (cpid,)) 
					return True
			except:
				return False
		return False

	def getpid(self):
		pf = self.pidFile
		try:
			cpid = open(pf).readline().strip()
		except Exception, e:
			return 0
		return int(cpid)

	def writepid(self):
		try:
			pid = str(os.getpid())
			Log.logMsg(4, "Writing %s to %s" % (pid, self.pidFile))
			file(self.pidFile,'w+').write("%s\n" % (pid,))
		except Exception, e:
			[Log.logMsg(1,t) for t in traceback.format_tb(sys.exc_info()[2])]
			Log.logMsg (0, "writepid() failed: %s" % (e,))

	def resetChangedKeys(self, section):
		self.changedkeys[section] = []

	def changedKeys(self, section, key=None):
		if not section in self.changedkeys:
			self.resetChangedKeys(section)
		if key is not None:
			self.changedkeys[section].append(key)
		return self.changedkeys[section]

	def delVal(self, section, type, key):
		try:
			del self.lastVals[section][type][key]
		except Exception:
			return

	def lastVal(self, section, type, key, val=None):
		Log.logMsg(5,"Entering lastVal %s %s %s %s" % (section, type, key, val))
		if not section in self.lastVals:
			self.lastVals[section] = {}
		if not type in self.lastVals[section]:
			self.lastVals[section][type] = {}
		if val is not None:
			self.lastVals[section][type][key] = val
		if key in self.lastVals[section][type]:
			Log.logMsg(5,"returning lastVal %s %s %s %s" % (section, type, key, self.lastVals[section][type][key]))
			return self.lastVals[section][type][key]
		return None

	def compareKeys(self):
		for sn in self.mtaconfig.getSections():
			section = self.mtaconfig.getSection(sn)
			Log.logMsg(5, "Checking keys for %s" % (section.name,))
			if len(self.forcedconfig):
				Log.logMsg(4, "Checking for forced keys %s" % (section.name,))
				if not section.name in self.forcedconfig:
					continue
			
			section.changed = False
			self.resetChangedKeys(section.name)

			for key in section.requiredvars():
				type = section.requiredvars(key)
				prev = self.lastVal(section.name,type,key)
				val = self.lookUpConfig(type, key)
				Log.logMsg(5, "Checking %s=%s" % (key,val))
				if val is not None:
					if prev != val:
						if not self.firstRun:
							Log.logMsg(3, "Var %s changed from \'%s\' -> \'%s\'" % (key, prev, val))
						self.lastVal(section.name, type, key, val)
						self.changedKeys(section.name,key)
						section.changed = True
				else:
					Log.logMsg(5, "Required key is not defined %s=\'%s\'" % (key, val))
					if prev is not None:
						if not self.firstRun:
							Log.logMsg(3, "Var %s changed from \'%s\' to no longer defined." % (key, prev))
						self.delVal(section.name, type, key)
						section.changed = True
		
		stoppedservices = 0
		totalservices = 0
		for service in self.curServices():
			totalservices += 1
			if not self.lookUpConfig("SERVICE", service):
				stoppedservices += 1
				Log.logMsg(2, "service %s was disabled need to stop" % (service,))
				self.curRestarts(service,0)


  		if (stoppedservices == totalservices) and (totalservices > 1):
			raise Exception, "All services detected disabled."

  		for service in self.serverconfig.getServices():
			if self.curServices(service) is None:
				if self.firstRun:
					if (self.serverconfig.getServices(service)):
						self.curServices(service,"running")
					else:
						self.curServices(service,"stopped")
				else:
					Log.logMsg(2, "service %s was enabled need to start" % (service,))
					self.curRestarts(service, 1)


	def compileActions(self):
		for sn in self.mtaconfig.getSections():
			section = self.mtaconfig.getSection(sn)
			Log.logMsg(5, "compiling actions for %s" % (section.name,))
			if len(self.forcedconfig):
				Log.logMsg(4, "Checking for forced keys %s" % (section.name,))
				if not section.name in self.forcedconfig:
					continue
			
			if self.firstRun or section.changed or section.name in self.forcedconfig:
				Log.logMsg(5, "Section %s changed compiling rewrites" % (section.name,))
				for rewrite in section.rewrites():
					self.curRewrites(rewrite, section.rewrites(rewrite))

				Log.logMsg(5, "Section %s changed compiling postconf" % (section.name,))
				for postconf in section.postconf():
					self.curPostconf(postconf, section.postconf(postconf))

				if section.name == "imapproxy":
					Log.logMsg(5, "Section %s changed compiling proxygen" % (section.name,))
					self.proxygen(True)

				Log.logMsg(5, "Section %s changed compiling ldap" % (section.name,))
				for ldap in section.ldap():
					self.curLdap(ldap, section.ldap(ldap))

				if not self.forced and not self.firstRun: # no restarts on forced rewrites
					Log.logMsg(5, "Section %s changed compiling restarts" % (section.name,))
					for restart in section.restarts():
						if self.lookUpConfig("SERVICE", restart):
							Log.logMsg(5, "Adding restart %s" % (restart,))
							self.curRestarts(restart, -1)
						else:
							Log.logMsg(5, "Adding stop %s" % (restart,))
							self.curRestarts(restart, 0)
			else:
				Log.logMsg(4, "Section %s did not change skipping" % (section.name,));


	def doProxygen(self):
		c = commands.commands["proxygen"]
		try:
			rc = c.execute((self.hostname,))
		except Exception, e:
			rc = 1
			[Log.logMsg(1,t) for t in traceback.format_tb(sys.exc_info()[2])]
			Log.logMsg(1, "Proxy configuration failed (%s)" % (e,))
		return rc

	def doRewrites(self):
		for (rewrite,(val,mode)) in self.curRewrites().items():
			# (val, mode) = self.curRewrites(rewrite)
			if not self.rewriteConfig(rewrite, val, mode):
				self.delRewrite(rewrite)

	def doPostconf(self):
		if self.curPostconf():
			c = commands.commands["postconf"]
			s = ["%s='%s'" % (postconf, val) for (postconf, val) in self.curPostconf().items()]
			try:
				rc = c.execute((" ".join(s),))
			except Exception, e:
				return rc
			self.clearPostconf()
			return rc
		return 0

	def runProxygen(self):
		if self.proxygen():
			if not self.doProxygen():
				self.proxygen(False)

	def runLdap(self):
		master = conf.Config.mConfig.ldap_is_master
		if master == "true":
			master = 1
		else:
			master = 0
		pw = conf.Config.mConfig.ldap_root_password
		for (ldap,val) in self.curLdap().items():
			if not self.doLdap(ldap, val, master, pw):
				self.delLdap(ldap)

	def doActions(self):

		t1 = time.clock()
		th = []
		# Proxygen takes longest, do it first
		th.append(threading.Thread(target=State.runProxygen,args=(self,),name="proxygen"))
		th.append(threading.Thread(target=State.doRewrites,args=(self,),name="rewrites"))
		th.append(threading.Thread(target=State.doPostconf,args=(self,),name="postconf"))
		th.append(threading.Thread(target=State.runLdap,args=(self,),name="ldap"))

		[t.start() for t in th]
		[t.join() for t in th]

		# Don't thread these
		while self.curRestarts().items():	# Loop to pick up any dependencies.
			for (restart, val) in sorted (self.curRestarts().items(), key=lambda x: State.startorder[x[0]]):
				if not self.controlProcess(restart, val):
					self.delRestart(restart)

		dt = time.clock()-t1
		Log.logMsg(3, "All action threads completed in %.2f sec" % dt)

	def doLdap(self, key, val, master, pw):
		Log.logMsg(4, "Setting ldap %s=%s" % (key, val))
		c = commands.commands["ldaphelper"]
		rc = 0
		try:
			rc = c.execute((master, pw, key, val))
			[Log.logMsg(5,t) for t in c.output.splitlines()]
		except Exception, e:
			[Log.logMsg(1,t) for t in traceback.format_tb(sys.exc_info()[2])]
			Log.logMsg(1, "LDAP FAILURE (%s)" % e)
		return rc

	def processIsRunning(self,process):
		return (not self.controlProcess(process, 2))

	def processIsNotRunning(self,process):
		return (not self.processIsRunning(process))

	def controlProcess(self, process, action_value):
		process = process.lower()
		rc = 0
		if not action_value in (-1, 0, 1, 2):
			Log.logMsg(1,"controlProcess %s (%s)" % (process, str(action_value)))
			Log.logMsg(1, "State must be in -1,0,1,2")
			return
		if not process in commands.commands:
			Log.logMsg(1, "Command not defined for %s" % (process,));
			return
		action = ["restart","stop","start","status"][action_value+1]
		lvl = 3
		if action == "status":
			lvl = 4

		Log.logMsg(lvl,"controlProcess %s %s (%d)" % (process, action, action_value))

		# return if it's already running and we are trying to start it.

		if action == "start" and self.processIsRunning(process):
			self.curServices(process,"running")
			Log.logMsg(lvl,"%s was already running adding to current state." % (process,))
			self.compileDependencyRestarts(process);
			return 0

		# if we initiate a stop/restart remove the service from watchdog
		# list of services available for restarts.  This avoids a restart
		# loop if they are slow to startup.
		if action in ["stop", "restart"] and self.getWatchdog(process):
			self.delWatchdog(process)

		# Postfix, unique to the end.
		if process == "mta" and action == "restart":
			action = "reload"

		rewrite = ""
		if action in ["stop","status"]:
			rewrite = "norewrite"

		Log.logMsg(lvl,"CONTROL %s: %s %s %s" % (process, commands.exe[process.upper()], action, rewrite) )
		if action != "status":
			# log at lvl 1 to make sure it gets into syslog
			Log.logMsg(1, "%s %s initiated from zmconfigd" % (process, action))

		pargs = ' '.join([action,rewrite])
		try:
			rc = commands.commands[process].execute((pargs,))
		except Exception, e:
			Log.logMsg(1,"Exception in %s: (%s)" % (commands.exe[process.upper()], e) )

		if rc == 0:
			if action == "stop":
				Log.logMsg(2, "%s was stopped removing from current state" % (process,));
				self.compileDependencyRestarts(process)
				if self.curServices(process):
					del(self.current["services"][process])
			elif action == "start":
				Log.logMsg(2, "%s was started adding to current state" % (process,));
				self.compileDependencyRestarts(process)
				self.curServices(process, "started")
		elif action != "status":
			Log.logMsg(2, "Failed to %s %s rc=%d" % (action, process, rc))

		return rc

	def rewriteConfig(self, fr, to, mode=None):
		t1 = time.clock()
		mode = mode or '0440'
		# This converts what may or may not be a string to an int
		mode = eval(str(mode))

		# Automatically handles absolute paths
		fr = os.path.join(self.baseDir,fr)
		to = os.path.join(self.baseDir,to)
		Log.logMsg(5, "Rewriting %s -> %s (%o)" % (fr, to, mode));

		try:
			(fh, tmpfile) = tempfile.mkstemp(dir="/tmp")

			f = fh.asOutputStream()
			for line in open(fr).readlines():
				f.write(self.transform(line))

			f.close()
			os.chmod(tmpfile, mode)
			# Can't find an atomic clobber move in jython
			if os.path.exists(to):
				os.unlink(to)
			shutil.move(tmpfile, to)
			dt = time.clock() - t1
			Log.logMsg(3, "Rewrote: %s with mode %o (%.2f sec)" % (to, mode, dt))
		except Exception, e:
			[Log.logMsg(1,t) for t in traceback.format_tb(sys.exc_info()[2])]
			Log.logMsg(1, "Rewrite failed: %s (%s)" % (e))
			raise e

		return False

	def xformLocalConfig(self, match):
		sr = match.group(1)
		func = None
		key = sr
		if re.search(r' ',sr):
			(func, key) = sr.split(' ',1)
		Log.logMsg(5, "xformLocalConfig %s" % (sr,))
		val = self.localconfig[key]
		if func:
			if func == "SPLIT":
				val = val.split(' ',1)[0]
			elif func == "PERDITION_LDAP_SPLIT":
				# This appears to be legacy
				# we need first arg plus just host names from remaining args
				val = ' '.join((val.split()[0],' '.join(re.findall(r"ldap.?://(\S*):\d+",val))))
				Log.logMsg(5, "PERDITION_LDAP_SPLIT: %s" % (val,))

		if val is None:
			val = ""
		return val

	# We support parsing for the zmprov -l functions.
	# Normal parsing uses gcf
	# Functions supported:
	#  (un)comment(args) - replace with comment char "#" if true (or value exists)
	#   binary(args) - 0 for false, 1 for true
	#   range (var low high) - replace with percent of range
	#   freq (var total) - replace with total / var  (var is period in total)
	#   contains (var string) - 
	#    for MV attribs, set to string if string is in the attrib
	#   contains (var string, replacement) - 
	#    for MV attribs, set to replacement if string is in the attrib
	#   list (var separator)
	#    Works like perl join, for multivalued attrib, joins with join value
	#    used to create csv or regexes
	#   truefalse
	#   explode
	#
	# args supported:
	#  SERVER:key - use command gs with zimbra_server_hostname, get value of key
	#

	def xformConfig(self, match):
		sr = match.group(1)
		val = None

		if re.match(r"comment", sr):
			[(cmd,key)] = re.findall(r"comment ([^:]+):(\S+)",sr)
			Log.logMsg(5, "comment before lookup key=%s cmd=%s sr=%s" % (key, cmd, sr))

			parts = key.split(',',2)
			valset = self.falseSet
			commentstr = '#'
			if (len(parts) > 1):
				key = parts[0]
				commentstr = parts[1]
			if (len(parts) > 2):
				valset = parts[2].split(',')
			val = self.lookUpConfig(cmd, key)
			Log.logMsg(5, "comment after lookup key=%s val=%s cmd=%s sr=%s" % (key, val, cmd, sr))
			# Negative test because we're testing against self.falseSet
			if not val in valset:
				val = ""
			else:
				val = commentstr
			Log.logMsg(5, "comment after rep key=%s val=%s cmd=%s" % (key, val, cmd))

		elif re.match(r"uncomment", sr):
			[(cmd,key)] = re.findall(r"uncomment ([^:]+):(\S+)",sr)
			Log.logMsg(5, "uncomment before lookup key=%s cmd=%s sr=%s" % (key, cmd, sr))

			parts = key.split(',',2)
			valset = self.falseSet
			commentstr = '#'
			if (len(parts) > 1):
				key = parts[0]
				commentstr = parts[1]
			if (len(parts) > 2):
				valset = parts[2].split(',')
			val = self.lookUpConfig(cmd, key)
			Log.logMsg(5, "uncomment after lookup key=%s val=%s cmd=%s sr=%s" % (key, val, cmd, sr))
			if (len(parts) > 2):
				if val in valset:
					val = ""
				else:
					val = commentstr
			else:
				if self.isTrueValue(val):
					val = ""
				else:
					val = commentstr
			Log.logMsg(5, "uncomment after rep key=%s val=%s cmd=%s" % (key, val, cmd))

		elif re.match(r"binary", sr):
			[(cmd,key)] = re.findall(r"binary ([^:]+):(\S+)",sr)
			val = 0
			if self.isTrueValue(self.lookUpConfig(cmd, key)):
				val = 1

		elif re.match(r"truefalse", sr):
			[(cmd,key)] = re.findall(r"truefalse ([^:]+):(\S+)",sr)
			Log.logMsg(5, "%s %s %s" % (cmd, key, val));
			val = "false"
			if self.isTrueValue(self.lookUpConfig(cmd, key)):
				val = "true"
			Log.logMsg(5, "%s %s %s" % (cmd, key, val));

		elif re.match(r"range", sr):
			[(cmd,key,lo,hi)] = re.findall(r"range ([^:]+):(\S+)\s+(\S+)\s+(\S+)",sr)
			val = int(self.lookUpConfig(cmd,key))
			val = ((val/100.00) * (int(hi) - int(lo))) + int(lo)

		elif re.match(r"list", sr):
			fields = sr.split(' ',2)
			(type,key) = fields[1].split(':')
			val = self.lookUpConfig(type, key)
			if val:
				val = fields[2].join(val.split())
			else:
				val = ""

		elif re.match(r"contains", sr):
			f = sr.split(',',1)
			st = f[0]
			if len(f) > 1:
				replace = f[1]
			else:
				replace = ""
			fields = st.split(' ',2)
			(type,key) = fields[1].split(':')
			val = self.lookUpConfig(type, key)
			replace = replace or fields[2]
			if fields[2] in val:
				val = replace
			else: 
				val = ""

		elif re.match(r"freq", sr):
			[(cmd,key,total)] = re.findall(r"freq ([^:]+):(\S+)\s+(\S+)",sr)
			val = self.lookUpConfig(cmd,key)
			per = re.sub(r"\d+","", val)
			val = re.sub(r"\D","", val)
			val = int(val)
			total = int(total)

			if per == "m":
				val = val / 60
			elif per == "s":
				val = val / 3600
			elif per == "d":
				val = val * 24
			
			if val:
				val = int(total/val)
			else:
				val = total

			if val < 1 and total > 1:
				val = 1

		elif re.match(r"explode", sr):
			[(base,cmd,key)] = re.findall(r"explode (.*) ([^:]+):(\w+)",sr)
			Log.logMsg (5, "Explode %s" % (sr,))
			vals = self.lookUpConfig(cmd, key)
			val = []
			if vals:
				for v in vals.split():
					qr = "%s:%s" % (cmd,key)
					Log.logMsg(5, "Substituting %s in %s with %s" % (qr,sr,v));
					Log.logMsg(5, "Quoted string=%s" % (qr,));
					Log.logMsg(5, "sline=%s" % (match.group(0)));
					val.append("%s %s" % (base, v))
					Log.logMsg(5, "Final string=%s" % (val[-1],))
				val = '\n'.join(val)
			else:
				val = ''
		else:
			val = self.lookUpConfig("VAR", sr)
			if val is None:
				val = self.lookUpConfig("LOCAL", sr)

		# Requires a string return for re.sub()
		if val is None:
			val = ""
		return str(val)

	def transform(self, line):
		line = re.sub(r"@@([^@]+)@@", self.xformLocalConfig, line)
		line = re.sub(r"%%([^%]+)%%", self.xformConfig, line)
		return line

	def compileDependencyRestarts(self, name):
		section = self.mtaconfig.getSection(name)
		for depend in section.depends():
			if self.lookUpConfig("SERVICE", depend) or depend == "amavis":
				Log.logMsg(3, "Adding restart for dependency %s" % (depend,));
				self.curRestarts(depend, -1)
