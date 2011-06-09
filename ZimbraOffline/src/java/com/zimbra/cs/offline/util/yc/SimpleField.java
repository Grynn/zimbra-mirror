/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.offline.util.yc;

import org.w3c.dom.Element;

import com.zimbra.cs.offline.util.yc.Fields.Type;

public class SimpleField extends FieldValue {
    private String value;
    private Fields.Type type;
    
    public SimpleField(Type type, String value) {
        this.type = type;
        this.value = value;
    }
    
    public SimpleField(String type) {
        if (Fields.NICKNAME.equals(type)) {
            this.type = Fields.Type.nickname;
        } else if (Fields.EMAIL.equals(type)) {
            this.type = Fields.Type.email;
        } else if (Fields.YAHOOID.equals(type)) {
            this.type = Fields.Type.yahooid;
        } else if (Fields.PHONE.equals(type)) {
            this.type = Fields.Type.phone;
        } else if (Fields.COMPANY.equals(type)) {
            this.type = Fields.Type.company;
        } else if (Fields.GUID.equals(type)) {
            this.type = Fields.Type.guid;
        } else if (Fields.JOBTITLE.equalsIgnoreCase(type)) {
            this.type = Fields.Type.jobTitle;
        } else if (Fields.LINK.equals(type)) {
            this.type = Fields.Type.link;
        } else if (Fields.NOTES.equals(type)) {
            this.type = Fields.Type.notes;
        } else if (Fields.OTHERID.equals(type)) {
            this.type = Fields.Type.otherid;
        }else if (Fields.CUSTOM.equals(type)) {
            this.type = Fields.Type.custom;
        } else {
            throw new IllegalArgumentException("error, Yahoo contacts type " + type + " doesn't exist !");
        }
    }

    public String getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected void appendValues(Element parent) {
        parent.setTextContent(this.value);
    }

    @Override
    public void extractFromXml(Element parent) {
        this.value = parent.getTextContent();
    }
    @Override
    public Type getType() {
        return this.type;
    }
}
