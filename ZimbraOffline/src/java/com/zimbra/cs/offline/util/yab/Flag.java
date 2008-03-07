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
 * YAB field flag.
 */
public class Flag {
    private final String name;
    private final boolean value;

    public static final String HOME = "home";
    public static final String WORK = "work";
    public static final String PERSONAL = "personal";
    public static final String MOBILE = "mobile";
    public static final String FAX = "fax";
    public static final String PAGER = "pager";
    public static final String YAHOOPHONE = "yahoophone";
    public static final String AOL = "aol";
    public static final String MSN = "msn";
    public static final String JABBER = "jabber";
    public static final String DOTMAC = "dotmac";
    public static final String ICQ = "icq";
    public static final String GOOGLE = "google";
    public static final String SKYPE = "skype";
    public static final String IRC = "irc";
    public static final String QQ = "qq";
    public static final String LCS = "lcs";
    public static final String EXTERNAL = "external";
    public static final String Y360 = "y360";
    public static final String PHOTO = "photo";
    public static final String BLOG = "blog";
    public static final String IBM = "ibm";

    public Flag(String name, boolean value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public boolean getValue() {
        return value;
    }

    public int hashCode() {
        return name.hashCode() ^ ((Boolean) value).hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != Flag.class) return false;
        Flag flag = (Flag) obj;
        return name.equals(flag.name) && value == flag.value;
    }
}
