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
import org.w3c.dom.Document;

import com.zimbra.cs.offline.util.Xml;

/**
 * Structured YAB name field.
 */
public class NameField extends Field {
    private String first;
    private String middle;
    private String last;
    private String prefix;
    private String suffix;
    private String firstSound;
    private String lastSound;

    public static final String NAME = "name";

    private static final String FIRST = "first";
    private static final String MIDDLE = "middle";
    private static final String LAST = "last";
    private static final String PREFIX = "prefix";
    private static final String SUFFIX = "suffix";
    private static final String FIRST_SOUND = "first-sound";
    private static final String LAST_SOUND = "last-sound";
    
    public NameField() {
        super(NAME);
    }

    public NameField(String first, String last) {
        this();
        this.first = first;
        this.last = last;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getMiddle() {
        return middle;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getFirstSound() {
        return firstSound;
    }

    public void setFirstSound(String firstSound) {
        this.firstSound = firstSound;
    }

    public String getLastSound() {
        return lastSound;
    }

    public void setLastSound(String lastSound) {
        this.lastSound = lastSound;
    }

    @Override
    public Element toXml(Document doc, String tag) {
        Element e = super.toXml(doc, tag);
        if (first != null) Xml.appendElement(e, FIRST, first);
        if (middle != null) Xml.appendElement(e, MIDDLE, middle);
        if (last != null) Xml.appendElement(e, LAST, last);
        if (prefix != null) Xml.appendElement(e, PREFIX, prefix);
        if (suffix != null) Xml.appendElement(e, SUFFIX, suffix);
        if (firstSound != null) Xml.appendElement(e, FIRST_SOUND, firstSound);
        if (lastSound != null) Xml.appendElement(e, LAST_SOUND, lastSound);
        return e;
    }

    @Override
    protected void parseXml(Element e) {
        super.parseXml(e);
        for (Element child : Xml.getChildren(e)) {
            String tag = child.getTagName();
            if (tag.equals(FIRST)) {
                first = Xml.getTextValue(child);
            } else if (tag.equals(MIDDLE)) {
                middle = Xml.getTextValue(child);
            } else if (tag.equals(LAST)) {
                last = Xml.getTextValue(child);
            } else if (tag.equals(PREFIX)) {
                prefix = Xml.getTextValue(child);
            } else if (tag.equals(SUFFIX)) {
                suffix = Xml.getTextValue(child);
            } else if (tag.equals(FIRST_SOUND)) {
                firstSound = Xml.getTextValue(child);
            } else if (tag.equals(LAST_SOUND)) {
                lastSound = Xml.getTextValue(child);
            } else {
                throw new IllegalArgumentException(
                    "Invalid 'name' field child element: " + tag);
            }
        }
    }
}
