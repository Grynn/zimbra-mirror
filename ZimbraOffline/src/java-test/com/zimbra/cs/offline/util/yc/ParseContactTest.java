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

public class ParseContactTest {

    @Test
    public void testAddContact() {
        try {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("yahoo_contacts_server_add.xml");
            DocumentBuilder docBuilder = Xml.newDocumentBuilder();
            Document doc = docBuilder.parse(stream);
            Element root = doc.getDocumentElement();
            Assert.assertEquals("contactsync", root.getNodeName());

            Contact contact = new Contact();
            contact.extractFromXml(Xml.getChildren(root).get(1));
            Assert.assertEquals(13, contact.getAllFields().size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testDeleteContact() {
        try {
            InputStream stream = this.getClass().getClassLoader()
                    .getResourceAsStream("yahoo_contacts_server_remove.xml");
            DocumentBuilder docBuilder = Xml.newDocumentBuilder();
            Document doc = docBuilder.parse(stream);
            Element root = doc.getDocumentElement();
            Assert.assertEquals("contactsync", root.getNodeName());

            Contact contact = new Contact();
            contact.extractFromXml(Xml.getChildren(root).get(1));
            Assert.assertEquals("remove", contact.getOp().name().toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testUpdateContact() {
        try {
            InputStream stream = this.getClass().getClassLoader()
                    .getResourceAsStream("yahoo_contacts_server_update.xml");
            DocumentBuilder docBuilder = Xml.newDocumentBuilder();
            Document doc = docBuilder.parse(stream);
            Element root = doc.getDocumentElement();
            Assert.assertEquals("contactsync", root.getNodeName());

            Contact contact = new Contact();
            contact.extractFromXml(Xml.getChildren(root).get(1));
            Assert.assertEquals("update", contact.getOp().name().toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testParseDbXml() {
        try {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("yahoo_contacts_dummy_saved.xml");
            DocumentBuilder docBuilder = Xml.newDocumentBuilder();
            Document doc = docBuilder.parse(stream);
            Element root = doc.getDocumentElement();

            Contact contact = new Contact();
            contact.extractFromXml(root);
            Assert.assertEquals("add", contact.getOp().name().toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
