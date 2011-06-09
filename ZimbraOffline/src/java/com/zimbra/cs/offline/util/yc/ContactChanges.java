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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zimbra.cs.offline.util.Xml;

public class ContactChanges {

    private List<Contact> contactsToAdd = new ArrayList<Contact>();
    private Map<String, List<Fields>> contactsToUpdate = new HashMap<String, List<Fields>>();
    private Set<String> contactsToRemove = new HashSet<String>();

    public void addContactForAdd(Contact contact) {
        this.contactsToAdd.add(contact);
    }

    public void addUpdateFields(String contactId, List<Fields> fields) {
        this.contactsToUpdate.put(contactId, fields);
    }

    public void addContactdIdToRemove(String id) {
        this.contactsToRemove.add(id);
    }

    public String toXml(int clientRev) {
        Document doc = Xml.newDocument();
        Element cont = doc.createElement("contactsync");
        cont.setAttribute("rev", String.valueOf(clientRev));
        for (Contact contact : this.contactsToAdd) {
            cont.appendChild(contact.toXml(cont.getOwnerDocument()));
        }
        for (String id : this.contactsToUpdate.keySet()) {
            List<Fields> fields = this.contactsToUpdate.get(id);
            Element contact = cont.getOwnerDocument().createElement("contacts");
            Xml.appendElement(contact, "op", Action.UPDATE.name().toLowerCase());
            Xml.appendElement(contact, "id", id);
            for (Fields f : fields) {
                if (f != null) {
                    contact.appendChild(f.toXml(contact.getOwnerDocument()));
                }
            }
            cont.appendChild(contact);
        }
        for (String id : this.contactsToRemove) {
            Element contact = cont.getOwnerDocument().createElement("contacts");
            Xml.appendElement(contact, "op", Action.REMOVE.name().toLowerCase());
            Xml.appendElement(contact, "id", id);
            cont.appendChild(contact);
        }
        return Xml.toString(cont);
    }
}
