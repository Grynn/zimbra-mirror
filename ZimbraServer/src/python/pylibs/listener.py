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
import signal
import socket
import SocketServer
import threading
import time

import state

from logmsg import *

class ThreadedRequestHandler(SocketServer.BaseRequestHandler):

	def handle(self):

		data = self.request.recv(2048)
		Log.logMsg(5, "Received %s" % data)
		args = data.split()
		if len(args) == 0:
			response = "ERROR UNKNOWN COMMAND"
		elif args[0] == "STATUS":
			response = "SUCCESS ACTIVE"
		elif args[0] == "REWRITE":
			if len(args) < 2:
				response = "ERROR NO SERVICES LISTED"
			else:
				Log.logMsg (5, "LOCK myState.lAction requested")
				state.State.mState.lAction.acquire() # Don't interrupt the rewrite process
				Log.logMsg (5, "LOCK myState.lAction acquired")
				for arg in args[1:]:
					state.State.mState.forced += 100
					Log.logMsg(3, "Processing rewrite request for %s" % arg)
					state.State.mState.forcedconfig[arg] = arg
				os.kill(os.getpid(),signal.SIGUSR2) # wake up the main thread if it's sleeping
				while state.State.mState.forced:
					Log.logMsg (5, "LOCK myState.lAction wait()")
					state.State.mState.lAction.wait()
					state.State.mState.lAction.release()
					Log.logMsg (5, "LOCK myState.lAction released")
				response = "SUCCESS REWRITES COMPLETE"
		else:
			response = "ERROR UNKNOWN COMMAND"

		Log.logMsg(5, "Sending %s" % response)
		self.request.send(response)

class ThreadedStreamServer(SocketServer.ThreadingMixIn, SocketServer.TCPServer):

	allow_reuse_address = True

	def shutdown(self):
		Log.logMsg(5, "Removing socket %s" % self.server_address)
