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
package com.zimbra.cs.offline.util;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Various XML utility methods.
 */
public final class Xml {
    public static Element appendText(Element e, Object text) {
        e.appendChild(e.getOwnerDocument().createTextNode(text.toString()));
        return e;
    }

    public static Element createElement(Document doc, String name, Object value) {
        return appendText(doc.createElement(name), value.toString());
    }

    public static Element appendElement(Element e, String name, Object value) {
        e.appendChild(createElement(e.getOwnerDocument(), name, value.toString()));
        return e;
    }

    public static String getTextValue(Element e) {
        NodeList nl = e.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Text) {
                return ((Text) node).getWholeText();
            }
        }
        return null;
    }

    public static int getIntValue(Element e) {
        String s = getTextValue(e);
        return s != null ? parseInt(s) : -1;
    }

    public static int getIntAttribute(Element e, String name) {
        String s = e.getAttribute(name);
        return s != null ? parseInt(s) : -1;
    }

    private static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    public static List<Element> getChildren(Element e) {
        NodeList nl = e.getChildNodes();
        List<Element> nodes = new ArrayList<Element>(nl.getLength());
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                nodes.add((Element) node);
            }
        }
        return nodes;
    }

    public static Document createDocument() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.newDocument();
    }
    
    public static void prettyPrint(Document doc, OutputStream out) throws IOException {
        OutputFormat format = new OutputFormat(doc);
        format.setLineWidth(80);
        format.setIndenting(true);
        format.setIndent(2);
        XMLSerializer serializer = new XMLSerializer(out, format);
        serializer.serialize(doc);
    }
}
