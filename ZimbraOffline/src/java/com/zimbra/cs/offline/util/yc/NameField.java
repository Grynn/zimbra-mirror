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

import com.google.common.base.Objects;
import com.zimbra.cs.offline.util.Xml;
import com.zimbra.cs.offline.util.yc.Fields.Type;

public final class NameField extends FieldValue {
    private String first;
    private String middle;
    private String last;
    private String prefix;
    private String suffix;
    private String firstSound;
    private String lastSound;

    private static final String GIVENNAME = "givenName";
    private static final String MIDDLE = "middleName";
    private static final String FAMILYNAME = "familyName";
    private static final String PREFIX = "prefix";
    private static final String SUFFIX = "suffix";
    private static final String FIRST_SOUND = "givenNameSound";
    private static final String LAST_SOUND = "familyNameSound";

    public String getFirst() {
        return first;
    }

    public String getMiddle() {
        return middle;
    }

    public String getLast() {
        return last;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getFirstSound() {
        return firstSound;
    }

    public String getLastSound() {
        return lastSound;
    }
    
    public void setFirst(String first) {
        this.first = first;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setFirstSound(String firstSound) {
        this.firstSound = firstSound;
    }

    public void setLastSound(String lastSound) {
        this.lastSound = lastSound;
    }

    @Override
    protected void appendValues(Element parent) {
        if (first != null)
            Xml.appendElement(parent, GIVENNAME, first);
        if (middle != null)
            Xml.appendElement(parent, MIDDLE, middle);
        if (last != null)
            Xml.appendElement(parent, FAMILYNAME, last);
        if (prefix != null)
            Xml.appendElement(parent, PREFIX, prefix);
        if (suffix != null)
            Xml.appendElement(parent, SUFFIX, suffix);
        if (firstSound != null)
            Xml.appendElement(parent, FIRST_SOUND, firstSound);
        if (lastSound != null)
            Xml.appendElement(parent, LAST_SOUND, lastSound);
    }

    @Override
    public void extractFromXml(Element e) {
        for (Element child : Xml.getChildren(e)) {
            String tag = child.getTagName();
            if (tag.equals(GIVENNAME)) {
                first = child.getTextContent();
            } else if (tag.equals(MIDDLE)) {
                middle = child.getTextContent();
            } else if (tag.equals(FAMILYNAME)) {
                last = child.getTextContent();
            } else if (tag.equals(PREFIX)) {
                prefix = child.getTextContent();
            } else if (tag.equals(SUFFIX)) {
                suffix = child.getTextContent();
            } else if (tag.equals(FIRST_SOUND)) {
                firstSound = child.getTextContent();
            } else if (tag.equals(LAST_SOUND)) {
                lastSound = child.getTextContent();
            }
        }
    }

    @Override
    public Type getType() {
        return Fields.Type.name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NameField)) {
            return false;
        }
        return this.hashCode() == ((NameField) obj).hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.first, this.last, this.middle, this.prefix, this.suffix);
    }
}
