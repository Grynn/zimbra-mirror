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

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zimbra.cs.offline.util.Xml;

public class ContactSyncBuildXmlTest {

    @Test
    public void testBuildXml() {
        DocumentBuilder builder = Xml.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element root = doc.createElement("contactsync");
        try {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("yahoo_contacts_server_add.xml");
            Document contactDoc = builder.parse(stream);
            Element contactRoot = contactDoc.getDocumentElement();
            Assert.assertEquals("contactsync", contactRoot.getNodeName());

            Contact contact = new Contact();
            contact.extractFromXml(Xml.getChildren(contactRoot).get(1));

            Element contactEle = contact.toXml(root.getOwnerDocument());
            root.appendChild(contactEle);

            System.out.println(Xml.toString(root));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
