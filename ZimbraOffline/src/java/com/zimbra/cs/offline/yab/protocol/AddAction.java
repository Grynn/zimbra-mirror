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
package com.zimbra.cs.offline.yab.protocol;

import org.w3c.dom.Element;

public enum AddAction {
    ADD, MERGE;

    public static AddAction fromXml(Element e) {
        String s = e.getAttribute("add-action");
        if ("add".equals(s)) return ADD;
        if ("merge".equals(s)) return MERGE;
        throw new IllegalArgumentException("Invalid 'add-action' value: " + s);
    }
}
