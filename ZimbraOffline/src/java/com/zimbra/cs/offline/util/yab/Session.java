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

import com.zimbra.cs.util.yauth.Auth;
import com.zimbra.cs.util.yauth.Authenticator;
import com.zimbra.cs.offline.util.Xml;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Yahoo address book session information.
 */
public class Session {
    private final String format;
    private final Authenticator authenticator;
    private final HttpClient httpClient;
    private final DocumentBuilder docBuilder;
    private final Transformer transformer;
    private final ByteArrayOutputStream baos;

    public Session(String format, Authenticator authenticator) {
        if (!Yab.XML.equals(format)) {
            throw new IllegalArgumentException(
                "Unsupported data format: " + format);
        }
        this.format = format;
        this.authenticator = authenticator;
        httpClient = new HttpClient();
        docBuilder = Xml.newDocumentBuilder();
        transformer = createTransformer();
        baos = new ByteArrayOutputStream(4096);
    }

    public Session(Authenticator authenticator) {
        this(Yab.XML, authenticator);
    }

    public List<Contact> search(String params)
        throws IOException {
        SearchResponse res = (SearchResponse) createSearchRequest(params).send();
        return res.getContacts();
    }
    
    public SearchRequest createSearchRequest(String... params) {
        SearchRequest req = new SearchRequest(this);
        req.addParams(params);
        return req;
    }

    public SearchRequest createSearchRequest() {
        return new SearchRequest(this);
    }

    public AddRequest createAddRequest() {
        return new AddRequest(this);
    }

    public SyncRequest createSyncRequest(int revision) {
        return new SyncRequest(this, revision);
    }
    
    public String getFormat() {
        return format;
    }

    public Auth authenticate() throws IOException {
        return authenticator.authenticate();
    }
    
    public HttpClient getHttpClient() {
        return httpClient;
    }

    public String toString(Node node) {
        try {
            return transform(node).toString("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unsupported encoding UTF-8");
        }
    }
    
    public RequestEntity getRequestEntity(Document doc) {
        return new ByteArrayRequestEntity(transform(doc).toByteArray());
    }

    private ByteArrayOutputStream transform(Node node) {
        baos.reset();
        try {
            transformer.transform(new DOMSource(node), new StreamResult(baos));
        } catch (TransformerException e) {
            throw new IllegalStateException("Could not serialize document", e);
        }
        return baos;
    }

    public Document createDocument() {
        return docBuilder.newDocument();
    }

    public Document parseDocument(InputStream is) throws IOException {
        try {
            return docBuilder.parse(is);
        } catch (SAXException e) {
            throw (IOException)
                new IOException("Unable to parse XML").initCause(e);
        }
    }

    public Document parseDocument(String s) throws IOException {
        try {
            return docBuilder.parse(new InputSource(new StringReader(s)));
        } catch (SAXException e) {
            throw (IOException)
                new IOException("Unable to parse XML").initCause(e);
        }
    }

    private static Transformer createTransformer() {
        try {
            return TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new IllegalStateException("Unable to create Transformer", e);
        }
    }
}
