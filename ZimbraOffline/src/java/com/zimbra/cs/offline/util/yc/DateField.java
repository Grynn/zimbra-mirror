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

import com.zimbra.cs.offline.util.Xml;
import com.zimbra.cs.offline.util.yc.Fields.Type;

public abstract class DateField extends FieldValue {
    private String day = "";
    private String month = "";
    private String year = "";

    private static final String DAY = "day";
    private static final String MONTH = "month";
    private static final String YEAR = "year";
    
    public DateField() {}
    
    public DateField(String date) {
        try {
            String[] ymd = date.split("-");
            this.year = ymd[0];
            this.month = ymd[1];
            this.day = ymd[2];    
        } catch (Exception e) {
        }
    }

    public void extractFromXml(Element e) {
        for (Element child : Xml.getChildren(e)) {
            if (DateField.DAY.equals(child.getNodeName())) {
                this.day = child.getTextContent();
            } else if (DateField.MONTH.equals(child.getNodeName())) {
                this.month = child.getTextContent();
            } else if (DateField.YEAR.equals(child.getNodeName())) {
                this.year = child.getTextContent();
            }
        }
    }

    @Override
    public String toString() {
        try {
            int monthValue = Integer.parseInt(month);
            int dayValue = Integer.parseInt(day);
            return String.format("%s-%02d-%02d", year, monthValue, dayValue);    
        } catch (NumberFormatException e) {
            return "";
        }
    }

    @Override
    protected void appendValues(Element parent) {
        Xml.appendElement(parent, DateField.DAY, this.day);
        Xml.appendElement(parent, DateField.MONTH, this.month);
        Xml.appendElement(parent, DateField.YEAR, this.year);
    }

    public static class BirthdayField extends DateField {

        public BirthdayField() {}
        
        public BirthdayField(String date) {
            super(date);
        }

        @Override
        public Type getType() {
            return Fields.Type.birthday;
        }
    }
    
    public static class AnniversaryField extends DateField {

        public AnniversaryField() {}
        
        public AnniversaryField(String date) {
            super(date);
        }

        @Override
        public Type getType() {
            return Fields.Type.anniversary;
        }
    }
}
