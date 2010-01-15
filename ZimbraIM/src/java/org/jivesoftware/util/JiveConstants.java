/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package org.jivesoftware.util;

/**
 * Contains constant values representing various objects in Jive.
 */
public class JiveConstants {

    public static final int SYSTEM = 17;
    public static final int ROSTER = 18;
    public static final int OFFLINE = 19;
    public static final int MUC_ROOM = 23;

    public static final long SECOND = 1000;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    public static final long WEEK = 7 * DAY;

    /**
     * Date/time format for use by SimpleDateFormat. The format conforms to
     * <a href="http://www.jabber.org/jeps/jep-0082.html">JEP-0082</a>, which defines
     * a unified date/time format for XMPP.
     */
    public static final String XMPP_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
}