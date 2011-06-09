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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.zimbra.cs.offline.util.Xml;

public class SyncResponse extends Response {

    private static DocumentBuilder docBuilder = Xml.newDocumentBuilder();

    public SyncResponse(int retCode, String resp) {
        super(retCode, resp);
    }

    public boolean extract(ContactSync cont) throws YContactException {
        Document doc;
        Element root;

        try {
            doc = docBuilder.parse(new InputSource(new StringReader(getResp())));
            root = doc.getDocumentElement();
        } catch (Exception e) {
            throw new YContactException("parsing response error", "", false, e, null);
        }

        int yahooRev = Integer.parseInt(root.getAttribute("yahoo:rev"));
        if (yahooRev == cont.getClientRev()) {
            return false;
        }
        cont.setYahooRev(yahooRev);
        cont.setClientRev(Integer.parseInt(root.getAttribute("yahoo:clientrev")));

        // parse contacts
        List<Element> children = Xml.getChildren(root);
        List<Contact> contacts = new ArrayList<Contact>();
        for (Element e : children) {
            if (Contact.TAG_NAME.equals(e.getNodeName())) {
                com.zimbra.cs.offline.util.yc.Contact contact = new com.zimbra.cs.offline.util.yc.Contact();
                contact.extractFromXml(e);
                contacts.add(contact);
            }
        }
        if (!contacts.isEmpty()) {
            cont.setContacts(contacts);
        }
        return true;
    }

}
