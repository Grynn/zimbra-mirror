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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.zimbra.cs.offline.util.Xml;

/**
 * YAB field type.
 */
public abstract class Field {
    private String name;
    private int id = -1;
    private List<Flag> flags;

    public static final String FID = "fid";

    public static Field fromXml(Element e) {
        Field field = newField(e.getTagName());
        field.parseXml(e);
        return field;
    }

    private static Field newField(String name) {
        if (name.equals(NameField.NAME)) {
            return new NameField();
        } else if (name.equals(AddressField.ADDRESS)) {
            return new AddressField();
        } else if (name.equals(DateField.BIRTHDAY) ||
                   name.equals(DateField.ANNIVERSARY)) {
            return new DateField(name);
        } else {
            return new SimpleField(name);
        }
    }
    
    protected Field() {}

    protected Field(String name) {
        this.name = name;
    }

    public boolean isName() { return this instanceof NameField; }
    public boolean isDate() { return this instanceof DateField; }
    public boolean isAddress() { return this instanceof AddressField; }
    public boolean isSimple() { return this instanceof SimpleField; }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public void addFlag(Flag flag) {
        if (flags == null) {
            flags = new ArrayList<Flag>();
        }
        flags.add(flag);
    }

    public void setFlags(String... flags) {
        for (String flag : flags) setFlag(flag);
    }

    public void unsetFlags(String... flags) {
        for (String flag : flags) unsetFlag(flag);
    }

    public void setFlag(String flag) {
        addFlag(new Flag(flag, true));
    }

    public void unsetFlag(String flag) {
        addFlag(new Flag(flag, false));
    }
    
    public boolean isFlagSet(String flag) {
        Flag f = getFlag(flag);
        return f != null ? f.getValue() : null;
    }

    public boolean isFlagUnset(String flag) {
        Flag f = getFlag(flag);
        return f != null ? !f.getValue() : null;
    }

    public Flag getFlag(String name) {
        if (flags == null) return null;
        for (Flag flag : flags) {
            if (flag.getName().equals(name)) {
                return flag;
            }
        }
        return null;
    }
    
    public List<Flag> getFlags() {
        return flags;
    }

    public boolean isHome() {
        return isFlagSet(Flag.HOME);
    }

    public boolean isWork() {
        return isFlagSet(Flag.WORK);
    }

    public Element toXml(Document doc, String tag) {
        Element e = doc.createElement(tag);
        if (id != -1) {
            e.setAttribute(FID, String.valueOf(id));
        }
        if (flags != null) {
            for (Flag flag : flags) {
                e.setAttribute(name, String.valueOf(flag.getValue()));
            }
        }
        return e;
    }

    protected void parseXml(Element e) {
        name = e.getTagName();
        id = Xml.getIntAttribute(e, FID);
        flags = parseFlags(e);
    }

    private static List<Flag> parseFlags(Element e) {
        NamedNodeMap attrs = e.getAttributes();
        if (attrs == null) return null;
        List<Flag> flags = new ArrayList<Flag>();
        for (int i = 0; i < attrs.getLength(); i++) {
            Attr attr = (Attr) attrs.item(i);
            String s = attr.getValue();
            if ("true".equals(s) || "false".equals(s)) {
                flags.add(new Flag(attr.getName(), Boolean.parseBoolean(s)));
            }
        }
        return flags;
    }
}
