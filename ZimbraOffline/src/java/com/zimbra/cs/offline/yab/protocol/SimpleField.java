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
 * Simple, unstructured YAB field.
 */
public class SimpleField extends Field {
    private String value;

    public static final String NICKNAME = "nickname";
    public static final String EMAIL = "email";
    public static final String YAHOOID = "yahooid";
    public static final String OTHERID = "otherid";
    public static final String PHONE = "phone";
    public static final String JOBTITLE = "jobtitle";
    public static final String COMPANY = "company";
    public static final String NOTES = "notes";
    public static final String LINK = "link";
    public static final String CUSTOM = "custom";

    public static SimpleField nickname(String name) {
        return new SimpleField(NICKNAME, name);
    }

    public static SimpleField email(String email, String... flags) {
        return new SimpleField(EMAIL, email, flags);
    }

    public static SimpleField yahooid(String yid, String... flags) {
        return new SimpleField(YAHOOID, yid, flags);
    }

    public static SimpleField otherid(String yid, String... flags) {
        return new SimpleField(OTHERID, yid, flags);
    }

    public static SimpleField phone(String phone, String... flags) {
        return new SimpleField(PHONE, phone, flags);
    }

    public static SimpleField jobtitle(String phone, String... flags) {
        return new SimpleField(JOBTITLE, phone, flags);
    }

    public static SimpleField company(String company) {
        return new SimpleField(COMPANY, company);
    }

    public static SimpleField notes(String notes) {
        return new SimpleField(NOTES, notes);
    }

    public static SimpleField custom(String phone, String... flags) {
        return new SimpleField(CUSTOM, phone, flags);
    }

    public SimpleField(String name) {
        super(name);
    }

    public SimpleField(String name, String value) {
        super(name);
        this.value = value;
    }

    public SimpleField(String name, String value, String... flags) {
        this(name, value);
        setFlags(flags);
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }

    @Override
    public Element toXml(Document doc, String tag) {
        Element e = super.toXml(doc, tag);
        Xml.appendText(e, value);
        return e;
    }

    @Override
    protected void parseXml(Element e) {
        super.parseXml(e);
        value = Xml.getTextValue(e);
    }
}
