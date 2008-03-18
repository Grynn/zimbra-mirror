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
import com.zimbra.cs.offline.util.Xml;

public class SyncResponseEvent {
    private Type type;
    private Contact contact;
    private int lastModifiedTime = -1;

    public static enum Type {
        ADDRESS_BOOK_RESET, ADD_CONTACT, UPDATE_CONTACT, REMOVE_CONTACT
    }

    public static SyncResponseEvent fromXml(Element e) {
        return new SyncResponseEvent().parseXml(e);
    }

    private SyncResponseEvent() {}

    public Type getType() {
        return type;
    }

    public Contact getContact() {
        return contact;
    }

    public int getContactId() {
        return contact.getId();
    }
    
    public int getLastModifiedTime() {
        return lastModifiedTime;
    }
    
    public SyncResponseEvent parseXml(Element e) {
        type = getType(e.getTagName());
        lastModifiedTime = Xml.getIntAttribute(e, "lmt");
        switch (type) {
        case ADD_CONTACT: case UPDATE_CONTACT: case REMOVE_CONTACT:
            contact = Contact.fromXml(e);
        }
        return this;
    }

    private Type getType(String tag) {
        if (tag.equals("address-book-reset")) {
            return Type.ADDRESS_BOOK_RESET;
        } else if (tag.equals("add-contact")) {
            return Type.ADD_CONTACT;
        } else if (tag.equals("update-contact")) {
            return Type.UPDATE_CONTACT;
        } else if (tag.equals("remove-contact")) {
            return Type.REMOVE_CONTACT;
        } else {
            throw new IllegalArgumentException(
                "Not a sync response event element: " + tag);
        }
    }
}
