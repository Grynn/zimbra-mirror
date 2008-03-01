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
package com.zimbra.cs.offline.yab;

import com.zimbra.cs.offline.yab.auth.RawAuth;
import com.zimbra.cs.offline.yab.auth.Auth;
import com.zimbra.cs.offline.yab.auth.AuthenticationException;
import com.zimbra.cs.offline.yab.protocol.SearchRequest;
import com.zimbra.cs.offline.yab.protocol.AddRequest;
import com.zimbra.cs.offline.yab.protocol.SyncRequest;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
    private String appId;
    private String format;
    private Auth auth;
    private HttpClient httpClient;
    private DocumentBuilder docBuilder;
    private Transformer transformer;
    private ByteArrayOutputStream baos;

    public Session(String appId, String format) {
        if (!Yab.XML.equals(format)) {
            throw new IllegalArgumentException(
                "Unsupported data format: " + format);
        }
        this.appId = appId;
        this.format = format;
        httpClient = new HttpClient();
        docBuilder = createDocumentBuilder();
        transformer = createTransformer();
        baos = new ByteArrayOutputStream(4096);
    }
    
    public void authenticate(String user, String pass)
            throws AuthenticationException, IOException {
        auth = RawAuth.authenticate(appId, user, pass);
    }

    public SearchRequest createSearchRequest(String params) {
        checkAuthenticated();
        SearchRequest req = new SearchRequest(this);
        req.addParams(params);
        return req;
    }

    public SearchRequest createSearchRequest() {
        checkAuthenticated();
        return new SearchRequest(this);
    }

    public AddRequest createAddRequest() {
        checkAuthenticated();
        return new AddRequest(this);
    }

    public SyncRequest createSyncRequest(int revision) {
        checkAuthenticated();
        return new SyncRequest(this, revision);
    }
    
    public String getAppId() {
        return appId;
    }

    public String getFormat() {
        return format;
    }

    public Auth getAuth() {
        return auth;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public RequestEntity getRequestEntity(Document doc) {
        baos.reset();
        try {
            transformer.transform(new DOMSource(doc), new StreamResult(baos));
        } catch (TransformerException e) {
            throw new IllegalStateException("Could not serialize document", e);
        }
        return new ByteArrayRequestEntity(baos.toByteArray());
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

    private void checkAuthenticated() {
        if (auth == null) {
            throw new IllegalStateException("Not authenticated");
        }
    }

    private static DocumentBuilder createDocumentBuilder() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setEntityResolver(new EntityResolver() {
                public InputSource resolveEntity(String pid, String sid) {
                    return new InputSource(new StringReader(""));
                }
            });
            return db;
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Unable to create DocumentBuilder", e);
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
