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

import java.util.List;
import java.util.ArrayList;

public class AddRequest extends Request {
    private List<Contact> contacts;

    public static final String ACTION = "addContacts";
    public static final String TAG = "add-request";
    
    public AddRequest(Session session) {
        super(session);
        contacts = new ArrayList<Contact>();
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    @Override
    protected String getAction() {
        return ACTION;
    }

    @Override
    public Element toXml(Document doc) {
        if (contacts.size() == 0) {
            throw new IllegalStateException(
                "AddRequest must contain at least one contact");
        }
        Element e = doc.createElement(TAG);
        for (Contact contact : contacts) {
            e.appendChild(contact.toXml(doc, "contact"));
        }
        return e;
    }

    @Override
    protected Response parseResponse(Document doc) {
        return AddResponse.fromXml(doc.getDocumentElement());
    }
}
