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
import com.zimbra.cs.offline.util.Xml;

import java.util.List;

public class Bucket {
    private int id;
    private int count;
    private Contact start;
    private Contact end;

    public static final String TAG = "bucket";
    
    public static Bucket fromXml(Element e) {
        return new Bucket().parseXml(e);
    }
    
    private Bucket() {}

    public int getId() { return id; }
    public int getContactCount() { return count; }
    public Contact getStartContact() { return start; }
    public Contact getEndContact() { return end; }
    
    private Bucket parseXml(Element e) {
        if (!e.getTagName().equals(TAG)) {
            throw new IllegalArgumentException(
                "Not a '" + TAG + "' element: " + e.getTagName());
        }
        id = Xml.getIntAttribute(e, "id");
        count = Xml.getIntAttribute(e, "contact-count");
        List<Element> children = Xml.getChildren(e);
        if (children.size() != 2) {
            throw new IllegalArgumentException("Invalid '" + TAG + "' element");
        }
        start = Contact.fromXml(children.get(0));
        end = Contact.fromXml(children.get(1));
        return this;
    }
}
