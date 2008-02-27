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

public class SyncRequestEvent {
    private final Type type;
    private final Object param;
    
    public static enum Type {
        ADD_CONTACT, UPDATE_CONTACT, REMOVE_CONTACT,
        ADD_CATEGORY, RENAME_CATEGORY, REMOVE_CATEGORY
    }

    public static SyncRequestEvent addContact(Contact contact) {
        return new SyncRequestEvent(Type.ADD_CONTACT, contact);
    }

    public static SyncRequestEvent updateContact(ContactChange change) {
        return new SyncRequestEvent(Type.UPDATE_CONTACT, change);
    }

    public static SyncRequestEvent removeContact(int id) {
        return new SyncRequestEvent(Type.REMOVE_CONTACT, new Contact(id));
    }

    public static SyncRequestEvent addCategory(String name) {
        return new SyncRequestEvent(Type.ADD_CATEGORY, new Category(name));
    }

    public static SyncRequestEvent renameCategory(Category from, String name) {
        return new SyncRequestEvent(Type.RENAME_CATEGORY,
            new Category[] { from, new Category(name) });
    }
    
    public static SyncRequestEvent removeCategory(Category category) {
        return new SyncRequestEvent(Type.REMOVE_CATEGORY, category);
    }

    private SyncRequestEvent(Type type, Object param) {
        this.type = type;
        this.param = param;
    }

    private Contact getContact() {
        return (Contact) param;
    }

    private ContactChange getContactChange() {
        return (ContactChange) param;
    }

    private Category getCategory() {
        return (Category) param;
    }

    private Category getFromCategory() {
        return ((Category[]) param)[0];
    }

    private Category getToCategory() {
        return ((Category[]) param)[1];
    }

    public Element toXml(Document doc) {
        switch (type) {
        case ADD_CONTACT:
            return getContact().asContactChange().toXml(doc, "add-contact");
        case UPDATE_CONTACT:
            return getContactChange().toXml(doc, "update-contact");
        case REMOVE_CONTACT:
            return getContact().toXml(doc, "remove-contact");
        case ADD_CATEGORY:
            return getCategory().toXml(doc, "add-category");
        case RENAME_CATEGORY:
            Element e = doc.createElement("rename-category");
            e.appendChild(getFromCategory().toXml(doc, "old-category"));
            e.appendChild(getToCategory().toXml(doc, "new-category"));
            return e;
        case REMOVE_CATEGORY:
            return getCategory().toXml(doc, "remove-category");
        }
        return null;
    }
}
