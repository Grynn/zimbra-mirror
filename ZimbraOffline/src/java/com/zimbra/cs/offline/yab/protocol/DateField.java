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
 * Structure aniversary or birthday YAB field.
 */
public class DateField extends Field {
    private int day;
    private int month;
    private int year;

    public static final String BIRTHDAY = "birthday";
    public static final String ANNIVERSARY = "anniversary";

    private static final String DAY = "day";
    private static final String MONTH = "month";
    private static final String YEAR = "year";

    public static DateField birthday(int day, int month, int year) {
        return new DateField(BIRTHDAY, day, month, year);
    }

    public static DateField anniversary(int day, int month, int year) {
        return new DateField(ANNIVERSARY, day, month, year);
    }
    
    public DateField(String name) {
        super(name);
    }

    public DateField(String name, int day, int month, int year) {
        super(name);
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public Element toXml(Document doc, String tag) {
        Element e = super.toXml(doc, tag);
        if (day != -1) Xml.appendElement(e, DAY, day);
        if (month != -1) Xml.appendElement(e, MONTH, month);
        if (year != -1) Xml.appendElement(e, YEAR, year);
        return e;
    }

    @Override
    protected void parseXml(Element e) {
        super.parseXml(e);
        for (Element child : Xml.getChildren(e)) {
            String tag = child.getTagName();
            if (tag.equals(DAY)) {
                day = Xml.getIntValue(child);
            } else if (tag.equals(MONTH)) {
                month = Xml.getIntValue(child);
            } else if (tag.equals(YEAR)) {
                year = Xml.getIntValue(child);
            } else {
                throw new IllegalArgumentException(
                    "Invalid '" + getName() + "' field child element: " + tag);
            }
        }
    }
}
