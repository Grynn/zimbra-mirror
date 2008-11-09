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
package com.zimbra.cs.offline.ab.gab;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;

public class Address {
    private String street;
    private String city;
    private String state;
    private String zip;
    private String country;

    private static final String EOL = "\r\n";

    private static final Pattern STATE_ZIP =
        Pattern.compile("([a-zA-Z ]+) +([0-9][^ ]*)");
    
    public static Address parse(String spec) {
        Address addr = new Address();
        List<String> parts = getParts(spec);
        if (parts.isEmpty()) return null;
        int size = parts.size();
        if (size > 1 && addr.parseStateZip(parts.get(size - 2))) {
            addr.country = parts.get(--size);
            parts = parts.subList(0, --size);
        } else if (addr.parseStateZip(parts.get(size - 1))) {
            parts = parts.subList(0, --size);
        } else {
            addr.state = parts.get(--size);
            parts = parts.subList(0, size);
        }
        if (size > 1) {
            String part = parts.get(size - 1);
            if (!part.matches("[0-9].*")) {
                addr.city = part;
                parts = parts.subList(0, --size);
            }
        }
        if (size > 0) {
            addr.street = join(parts);
        }
        return addr;
    }

    private boolean parseStateZip(String part) {
        Matcher m = STATE_ZIP.matcher(part);
        if (m.matches()) {
            state = m.group(1);
            zip = m.group(2);
            return true;
        }
        return false;
    }
    
    private static String join(List<String> parts) {
        StringBuilder sb = new StringBuilder();
        if (parts.isEmpty()) {
            return null;
        }
        sb.append(parts.get(0));
        for (int i = 1; i < parts.size(); i++) {
            sb.append(", ").append(parts.get(i));
        }
        return sb.toString();
    }
    
    private static List<String> getParts(String spec) {
        List<String> parts = new ArrayList<String>();
        for (String part : spec.split("[,\\n\\r]+")) {
            part = part.trim();
            if (part.length() > 0) {
                parts.add(part);
            }
        }
        return parts;
    }
    
    public Address() {}
    
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

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (street != null) sb.append(street).append(EOL);
        if (city != null) {
            sb.append(city).append(state != null ? ", " : EOL);
        }
        if (state != null) {
            sb.append(state).append(zip != null ? "  " : EOL);
        }
        if (zip != null) sb.append(zip).append(EOL);
        if (country != null) sb.append(country);
        return sb.toString().trim();
    }

    private static String quote(String s) {
        return s != null ? '"' + s + '"' : "null";
    }
}
