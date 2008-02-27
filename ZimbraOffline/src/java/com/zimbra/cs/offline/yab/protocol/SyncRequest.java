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
package com.zimbra.cs.offline.yab.protocol;

import org.apache.commons.httpclient.NameValuePair;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import java.util.List;
import java.util.ArrayList;

import com.zimbra.cs.offline.util.Xml;
import com.zimbra.cs.offline.yab.RawAuth;

/**
 * Yahoo address book synchronization request.
 */
public class SyncRequest extends Request {
    private final int revision;
    private List<SyncRequestEvent> events;

    private static final String SYNCHRONIZE = "synchronize";
    private static final String MYREV = "myrev";
    
    public SyncRequest(RawAuth auth, String format, int revision) {
        super(auth, format);
        this.revision = revision;
        events = new ArrayList<SyncRequestEvent>();
    }

    public void addEvent(SyncRequestEvent event) {
        events.add(event);
    }

    @Override
    protected NameValuePair[] getAdditionalParams() {
        return new NameValuePair[] {
            new NameValuePair(MYREV, String.valueOf(revision))
        };
    }

    @Override
    protected String getAction() {
        return SYNCHRONIZE;
    }

    @Override
    public Element toXml(Document doc) {
        Element e = doc.createElement("sync-request");
        for (SyncRequestEvent event : events) {
            e.appendChild(event.toXml(doc));
        }
        return e;
    }

    public static void main(String[] args) throws Exception {
        SyncRequest req = new SyncRequest(null, XML, 1234);
        Contact contact = new Contact();
        contact.addField(new NameField("David", "Connelly"));
        contact.addField(DateField.birthday(3, 1, 1965));
        contact.addField(SimpleField.nickname("dac"));
        contact.addField(SimpleField.email("dac@zimbra.com", Flag.WORK));
        contact.addField(SimpleField.email("dconnelly@gmail.com", Flag.HOME));
        contact.addField(SimpleField.notes("My contact information"));
        req.addEvent(SyncRequestEvent.addContact(contact));
        Document doc = Xml.createDocument();
        doc.appendChild(req.toXml(doc));
        Xml.prettyPrint(doc, System.out);
    }
}
