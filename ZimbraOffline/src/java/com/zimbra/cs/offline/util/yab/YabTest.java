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
import com.zimbra.cs.offline.util.yab.Contact;
import com.zimbra.cs.offline.util.yab.NameField;
import com.zimbra.cs.offline.util.yab.SimpleField;
import com.zimbra.cs.offline.util.yab.Yab;
import com.zimbra.cs.offline.util.yab.SyncRequest;
import com.zimbra.cs.offline.util.yab.SyncResponse;
import com.zimbra.cs.offline.util.yab.Session;

public class YabTest extends TestCase {
    private Session session;

    private static final String APPID = "D2hTUBHAkY0IEL5MA7ibTS_1K86E8RErSSaTGn4-";
    private static final String USER = "dacztest";
    private static final String PASS = "test1234";

    private static final String JOHN = "John";
    private static final String LAST = "Doe";
    private static final String YABTEST = "YABTEST";

    static {
        //System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        //System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        //System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "debug");
        //System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");
    }

    public void setUp() throws Exception {
        if (session == null) {
            session = Yab.createSession(APPID, Yab.XML);
            session.authenticate(USER, PASS);
        }
    }

    public void test() throws Exception {
        SyncRequest req = session.createSyncRequest(2);
        SyncResponse res = (SyncResponse) req.send();
    }

    private Contact createContact(String first, String last) {
        Contact contact = new Contact();
        contact.addField(new NameField(first, last));
        contact.addField(SimpleField.email(first + "." + last + "@yahoo.com"));
        contact.addField(SimpleField.custom(YABTEST));
        return contact;
    }
}
