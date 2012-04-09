/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 Zimbra, Inc.
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
package com.zimbra.soap.mail;

import java.util.List;

import com.sun.xml.ws.developer.WSBindingProvider;
import com.zimbra.soap.Utility;

import generated.zcsclient.mail.testDiffDocumentRequest;
import generated.zcsclient.mail.testDiffDocumentResponse;
import generated.zcsclient.mail.testDiffDocumentVersionSpec;
import generated.zcsclient.mail.testDispositionAndText;
import generated.zcsclient.mail.testDocumentInfo;
import generated.zcsclient.mail.testDocumentSpec;
import generated.zcsclient.mail.testIdEmailName;
import generated.zcsclient.mail.testIdVersionName;
import generated.zcsclient.mail.testListDocumentRevisionsRequest;
import generated.zcsclient.mail.testListDocumentRevisionsResponse;
import generated.zcsclient.mail.testListDocumentRevisionsSpec;
import generated.zcsclient.mail.testSaveDocumentRequest;
import generated.zcsclient.mail.testSaveDocumentResponse;
import generated.zcsclient.ws.service.ZcsPortType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class WSDLDocumentTest {

    private static ZcsPortType mailSvcEIF = null;

    private final static String testAcctDomain = "wsdldoc.example.test";
    private final static String testAcct = "wsdl1@" + testAcctDomain;

    @BeforeClass
    public static void init() throws Exception {
        Utility.setUpToAcceptAllHttpsServerCerts();
        mailSvcEIF = Utility.getZcsSvcEIF();
        oneTimeTearDown();
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // one-time cleanup code
        try {
            Utility.deleteAccountIfExists(testAcct);
            Utility.deleteDomainIfExists(testAcctDomain);
        } catch (Exception ex) {
            System.err.println("Exception " + ex.toString() + " thrown inside oneTimeTearDown");
        }
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Exercise SaveDocument/ListDocumentRevisions/DiffDocument
     */
    @Test
    public void document() throws Exception {
        final String docName = "My Document name";
        Utility.ensureAccountExists(testAcct);
        Utility.addSoapAcctAuthHeaderForAcct((WSBindingProvider)mailSvcEIF, testAcct);
        testSaveDocumentRequest req = new testSaveDocumentRequest();
        testDocumentSpec docSpec = new testDocumentSpec();
        docSpec.setCt("text/plain");
        docSpec.setContent("My Document Content");
        docSpec.setName(docName);
        req.setDoc(docSpec);
        testSaveDocumentResponse resp = mailSvcEIF.saveDocumentRequest(req);
        Assert.assertNotNull("SaveDocumentResponse object", resp);
        testIdVersionName docInfo = resp.getDoc();
        Assert.assertNotNull("SaveDocumentResponse/doc object", docInfo);
        Assert.assertEquals("Saved Document name", docName, docInfo.getName());
        Assert.assertEquals("Saved Document first version", 1, docInfo.getVer());
        String docId = docInfo.getId();
        Assert.assertNotNull("SaveDocumentResponse/doc/id object", docId);
        docSpec = new testDocumentSpec();
        docSpec.setDesc("Description for Rev 2");
        docSpec.setId(docId);
        docSpec.setVer(1);
        docSpec.setContent("My Changed Document Content");
        req.setDoc(docSpec);
        resp = mailSvcEIF.saveDocumentRequest(req);
        Assert.assertNotNull("SaveDocumentResponse object", resp);
        docInfo = resp.getDoc();
        Assert.assertNotNull("SaveDocumentResponse/doc object", docInfo);
        Assert.assertEquals("Saved Document name", docName, docInfo.getName());
        Assert.assertEquals("Saved Document second version", 2, docInfo.getVer());
        docId = docInfo.getId();
        Assert.assertNotNull("SaveDocumentResponse/doc/id object", docId);
        testListDocumentRevisionsRequest revsReq = new testListDocumentRevisionsRequest();
        testListDocumentRevisionsSpec revsSpec = new testListDocumentRevisionsSpec();
        revsSpec.setId(docId);
        revsSpec.setCount(200);
        revsSpec.setVer(2);
        revsReq.setDoc(revsSpec);
        testListDocumentRevisionsResponse revsResp = mailSvcEIF.listDocumentRevisionsRequest(revsReq);
        Assert.assertNotNull("ListDocumentRevisionsResponse object", revsResp);
        List<testDocumentInfo> revs = revsResp.getDoc();
        Assert.assertEquals("ListDocumentRevisionsResponse num docs", 2, revs.size());
        List<testIdEmailName> users = revsResp.getUser();
        Assert.assertEquals("ListDocumentRevisionsResponse num users", 1, users.size());
        testDiffDocumentRequest diffReq = new testDiffDocumentRequest();
        testDiffDocumentVersionSpec diffSpec = new testDiffDocumentVersionSpec();
        diffSpec.setId(docId);
        diffSpec.setV1(1);
        diffSpec.setV2(2);
        diffReq.setDoc(diffSpec);
        testDiffDocumentResponse diffResp = mailSvcEIF.diffDocumentRequest(diffReq);
        Assert.assertNotNull("DiffDocumentResponse object", diffResp);
        List<testDispositionAndText> chunks = diffResp.getChunk();
        Assert.assertEquals("DiffDocumentResponse num chunks", 2, chunks.size());
    }
}
