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
package com.zimbra.cs.offline.gab;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Name {
    private String prefix;
    private String first;
    private String middle;
    private String last;

    private static final String PREFIX = "[A-Za-z]{2,}\\.";

    public static Name parse(String spec) {
        Name name = new Name();
        List<String> parts = getParts(spec);
        int size = parts.size();
        if (size == 0) return null;
        String part = parts.get(0);
        if (size > 1 && part.matches(PREFIX)) {
            name.prefix = part;
            parts = parts.subList(1, size--);
        }
        name.last = parts.get(--size);
        if (size > 0) {
            name.first = parts.get(0);
            if (size > 1) {
                name.middle = join(parts.subList(1, size));
            }
        }
        return name;
    }

    private static List<String> getParts(String spec) {
        List<String> parts = new ArrayList<String>();
        for (String part : spec.split(" ")) {
            part = part.trim();
            if (part.length() > 0) {
                parts.add(part);
            }
        }
        return parts;
    }

    private static String join(List<String> parts) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = parts.iterator();
        if (it.hasNext()) {
            sb.append(it.next());
            while (it.hasNext()) {
                sb.append(' ').append(it.next());
            }
        }
        return sb.toString();
    }

    public Name() {}
    
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
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

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (prefix != null) sb.append(prefix).append(' ');
        if (first != null) sb.append(first).append(' ');
        if (middle != null) sb.append(middle).append(' ');
        if (last != null) sb.append(last).append(' ');
        return sb.toString().trim();
    }
}
