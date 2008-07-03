#!/bin/bash
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2005 Zimbra, Inc.
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


accounts=`zmprov gaa`

for a in $accounts; do
    mh=`zmprov ga $a | grep '^zimbraMailHost' | awk -F: '{ print $2; }'`
    cmd="zmprov ma $a zimbraMailHost $mh"
    if [ "x$1" = "x-f" ]; then
        echo "Running: $cmd"
        $cmd
    else
        echo "WillRun: $cmd"
    fi
done
