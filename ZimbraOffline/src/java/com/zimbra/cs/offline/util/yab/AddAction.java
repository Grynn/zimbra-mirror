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

import org.w3c.dom.Element;

public enum AddAction {
    ADD("add"), MERGE("merge");

    private static final String TAG = "add-action";
    
    private String attr;

    private AddAction(String addr) { this.attr = addr; }

    public static AddAction fromXml(Element e) {
        String s = e.getAttribute(TAG);
        if (s == null || s.equals("")) return null;
        if (ADD.attr.equals(s)) return ADD;
        if (MERGE.attr.equals(s)) return MERGE;
        throw new IllegalArgumentException("Invalid 'add-action' value: " + s);
    }

    public void setAttribute(Element e) {
        e.setAttribute(TAG, attr);
    }
}
