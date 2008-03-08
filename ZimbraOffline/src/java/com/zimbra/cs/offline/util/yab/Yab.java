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

/**
 * Yahoo Address book access.
 */
public class Yab {
    public static final String BASE_URI = "http://address.yahooapis.com/v1";
    public static final String DTD = "http://l.yimg.com/us.yimg.com/lib/pim/r/abook/xml/2/pheasant.dtd";
    public static final String XML = "xml";
    public static final String JSON = "json";

    public static final Boolean DEBUG = true; // Boolean.getBoolean("zimbra.yab.debug");
    
    public static Session createSession(String appId, String format) {
        return new Session(appId, format);
    }

    public static Session createSession(String appId) {
        return new Session(appId, XML);
    }

    public static void debug(String format, Object... args) {
        if (DEBUG) {
            System.out.printf("[DEBUG] " + format, args);
            System.out.println();
        }
    }
}
