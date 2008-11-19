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
package com.zimbra.cs.offline.ab.yab;

import com.zimbra.cs.offline.ab.SyncException;

public class RemoteId {
    private final Type type;
    private final int value;

    private enum Type { CONTACT, CATEGORY }

    private static final String SEPARATOR = ":";

    public static RemoteId contactId(int value) {
        return new RemoteId(Type.CONTACT, value);
    }

    public static RemoteId categoryId(int value) {
        return new RemoteId(Type.CATEGORY, value);
    }

    public static RemoteId parse(String s) throws SyncException {
        int i = s.indexOf(SEPARATOR);
        if (i != -1) {
            try {
                String prefix = s.substring(0, i);
                int id = Integer.parseInt(s.substring(i + SEPARATOR.length()));
                if (prefix.equalsIgnoreCase(Type.CONTACT.name())) {
                    return contactId(id);
                } else if (prefix.equalsIgnoreCase(Type.CATEGORY.name())) {
                    return categoryId(id);
                }
            } catch (NumberFormatException e) {
            }
        }
        throw new SyncException("Invalid ID syntax: " + s);
    }
    
    private RemoteId(Type type, int value) {
        assert value > 0 : "Invalid id value: " + value;
        this.type = type;
        this.value = value;
    }

    public int getValue() { return value; }

    public boolean isContact() {
        return type == Type.CONTACT;
    }

    public boolean isCategory() {
        return type == Type.CATEGORY;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == RemoteId.class) {
            RemoteId rid = (RemoteId) obj;
            return type == rid.type && value == rid.value;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return type.hashCode() ^ Integer.valueOf(value).hashCode();
    }

    @Override
    public String toString() {
        return type.name().toLowerCase() + SEPARATOR + String.valueOf(value);
    }
}
