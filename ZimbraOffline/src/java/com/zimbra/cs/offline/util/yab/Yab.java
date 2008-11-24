/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline.util.yab;

import com.zimbra.cs.util.yauth.Auth;
import com.zimbra.cs.util.yauth.Authenticator;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.common.util.Log;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * Yahoo Address book access.
 */
public class Yab {
    public static final String DTD = "http://l.yimg.com/us.yimg.com/lib/pim/r/abook/xml/2/pheasant.dtd";

    public static final Log LOG = OfflineLog.yab;
    
    public static final String BASE_URI = "http://address.yahooapis.com/v1";

    public static final String XML = "xml";
    public static final String JSON = "json";
    
    public static Session createSession(Authenticator auth) {
        return new Session(auth);
    }

    public static boolean isDebug() {
        return LOG.isDebugEnabled();
    }

    public static void debug(String fmt, Object... args) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format(fmt, args));
        }
    }
}
