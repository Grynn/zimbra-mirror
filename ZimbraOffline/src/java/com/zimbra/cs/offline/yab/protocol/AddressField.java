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
 * Structured YAB address field.
 */
public class AddressField extends Field {
    private String street;
    private String city;
    private String state;
    private String zip;
    private String country;

    public static final String ADDRESS = "address";

    private static final String STREET = "street";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String ZIP = "zip";
    private static final String COUNTRY = "country";
    
    public AddressField() {
        super(ADDRESS);
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public Element toXml(Document doc, String tag) {
        Element e = super.toXml(doc, tag);
        if (street != null) Xml.appendElement(e, STREET, street);
        if (city != null) Xml.appendElement(e, CITY, city);
        if (state != null) Xml.appendElement(e, STATE, state);
        if (zip != null) Xml.appendElement(e, ZIP, zip);
        if (country != null) Xml.appendElement(e, COUNTRY, country);
        return e;
    }

    @Override
    protected void parseXml(Element e) {
        for (Element child : Xml.getChildren(e)) {
            String tag = child.getTagName();
            if (tag.equals(STREET)) {
                street = Xml.getTextValue(child);
            } else if (tag.equals(CITY)) {
                city = Xml.getTextValue(child);
            } else if (tag.equals(STATE)) {
                state = Xml.getTextValue(child);
            } else if (tag.equals(ZIP)) {
                zip = Xml.getTextValue(child);
            } else if (tag.equals(COUNTRY)) {
                country = Xml.getTextValue(child);
            } else {
                throw new IllegalArgumentException(
                    "Invalid 'address' field child element: " + tag);
            }
        }
    }
}
