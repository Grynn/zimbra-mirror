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
import org.w3c.dom.Document;
import com.zimbra.cs.offline.util.Xml;

/**
 * Category reference by name or id.
 */
public class Category {
    private String name;
    private int id = -1;

    private static final String CATID = "catid";

    public static Category fromXml(Element e) {
        Category cat = new Category();
        cat.parseXml(e);
        return cat;
    }
    
    public Category() {}

    public Category(String name) {
        this.name = name;
    }

    public Category(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }

    public Element toXml(Document doc, String tag) {
        Element e = doc.createElement(tag);
        if (id != -1) {
            e.setAttribute(CATID, String.valueOf(id));
        }
        if (name != null) {
            Xml.appendText(e, name);
        }
        return e;
    }

    private void parseXml(Element e) {
        id = Xml.getIntAttribute(e, CATID);
        name = Xml.getTextValue(e);
    }
}
