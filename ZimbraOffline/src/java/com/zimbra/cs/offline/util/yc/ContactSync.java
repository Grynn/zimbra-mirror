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

import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zimbra.cs.offline.util.Xml;

/**
 * http://developer.yahoo.com/social/rest_api_guide/syncing.html
 * 
 * The returned data is in the contactsync element within the response body. The
 * data includes an array of categories and an array of Contact Objects. Because
 * category lists are short, the entire category list is returned every time,
 * not just the differences.
 * 
 */
public class ContactSync extends Entity implements YContactSyncResult {

    private static DocumentBuilder docBuilder = Xml.newDocumentBuilder();
    private static final String CONTACTSYNC = "contactsync";
    private int clientRev;
    private int yahooRev;
    private List<Category> categories = Collections.emptyList();
    private List<Contact> contacts = Collections.emptyList();

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public int getClientRev() {
        return clientRev;
    }

    public void setClientRev(int clientRev) {
        this.clientRev = clientRev;
    }

    public String getYahooRev() {
        return String.valueOf(yahooRev);
    }

    public void setYahooRev(int yahooRev) {
        this.yahooRev = yahooRev;
    }

    /**
     * pass null
     */
    @Override
    public Element toXml(Document doc1) {
        Document doc = docBuilder.newDocument();
        Element e = doc.createElement(CONTACTSYNC);
        for (Contact contact : this.contacts) {
            Xml.appendElement(e, Contact.TAG_NAME, contact.toXml(e.getOwnerDocument()));
        }
        return e;
    }

    public static String toXml(List<Element> contacts, String rev) {
        Document doc = docBuilder.newDocument();
        Element e = doc.createElement(CONTACTSYNC);
        e.setAttribute("rev", rev);
        for (Element contact : contacts) {
            Xml.appendElement(e, "contacts", contact);
        }
        return Xml.toString(doc);
    }

    @Override
    public boolean isPushResult() {
        return false;
    }
}
