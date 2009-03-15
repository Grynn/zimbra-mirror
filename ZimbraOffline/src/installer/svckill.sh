#!/bin/sh
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2008 Zimbra, Inc.
# 
# The contents of this file are subject to the Yahoo! Public License
# Version 1.0 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# 
# ***** END LICENSE BLOCK *****
# 

timeout=20

while [ $timeout -gt 0 ]; do
  pid=`ps -fe | grep java | grep 'Launcher start com.zimbra.cs.offline.start.Main' | awk '{print $2}'`
  if [ -z $pid ]; then
    exit 0
  fi
  sleep 1
  timeout=$[timeout-1]
done

kill -9 $pid
