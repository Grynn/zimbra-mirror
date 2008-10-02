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

import junit.framework.TestCase;
import junit.framework.Assert;
import com.zimbra.cs.util.yauth.RawAuthManager;
import com.zimbra.cs.util.yauth.FileTokenStore;
import com.zimbra.cs.util.yauth.TokenStore;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.junit.Before;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public class YabTest {
    private Session session;

    private static final String APPID = "D2hTUBHAkY0IEL5MA7ibTS_1K86E8RErSSaTGn4-";
    private static final String USER = "dacztest";
    private static final String PASS = "test123";

    private static final NameField NAME = new NameField("John", "Doe");
    private static final SimpleField EMAIL = SimpleField.email("john@foo.com", Flag.HOME);

    private static final File TOKENS = new File("/tmp/tokens");

    static {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        Yab.enableDebug();
    }
    
    static {
        //System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        //System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        //System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "debug");
        //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");
    }

    @Before
    public void setUp() throws Exception {
        if (session == null) {
            TokenStore ts = new FileTokenStore(TOKENS);
            if (!ts.hasToken(APPID, USER)) {
                ts.newToken(APPID, USER, PASS);
            }
            RawAuthManager ram = new RawAuthManager(ts);
            session = Yab.createSession(ram.newAuthenticator(APPID, USER, PASS));
        }
    }                     

    @Test
    public void testSyncRequest() throws Exception {
        SyncRequest req = session.createSyncRequest(0);
        SyncResponse res = (SyncResponse) req.send();
    }

    @Test
    public void testSynchronize() throws Exception {
        // Get initial revision id and contacts
        SyncRequest req = session.createSyncRequest(0);
        SyncResponse res = (SyncResponse) req.send();
        int cid = findContact(res.getAddedContacts(), NAME);
        // Add new contact
        req = session.createSyncRequest(res.getRevision());
        Contact contact = new Contact();
        contact.addField(NAME);
        if (cid != -1) {
            req.addEvent(SyncRequestEvent.removeContact(cid));
        }
        req.addEvent(SyncRequestEvent.addContact(contact));
        res = (SyncResponse) req.send();
        checkResults(res);
        if (cid != -1) {
            Assert.assertEquals(1, res.getRemovedContacts().size());
        }
        Assert.assertEquals(1, res.getAddedContacts().size());
        Assert.assertEquals(0, res.getUpdatedContacts().size());
    }

    private static void checkResults(SyncResponse res) {
        for (Result result : res.getResults()) {
            Assert.assertNotNull(result);
            Assert.assertFalse(result.isError());
        }
    }

    private static int findContact(List<Contact> contacts, NameField name) {
        for (Contact contact : contacts) {
            for (Field field : contact.getFields()) {
                if (field.isName()) {
                    NameField nf = (NameField) field;
                    if (name.getFirst().equalsIgnoreCase(nf.getFirst()) &&
                        name.getLast().equalsIgnoreCase(nf.getLast())) {
                        return contact.getId();
                    }
                }
            }
        }
        return -1;
    }
    
    public static void main(String... args) throws Exception {
        YabTest test = new YabTest();
        test.setUp();
        test.testSyncRequest();
    }
}
