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

import org.w3c.dom.Element;
import org.w3c.dom.Document;

import java.util.List;
import java.util.ArrayList;

import com.zimbra.cs.offline.yab.Session;

/**
 * Yahoo address book synchronization request.
 */
public class SyncRequest extends Request {
    private List<SyncRequestEvent> events;

    private static final String SYNCHRONIZE = "synchronize";
    private static final String MYREV = "myrev";
    
    public SyncRequest(Session session, int revision) {
        super(session);
        events = new ArrayList<SyncRequestEvent>();
        addParam(MYREV, String.valueOf(revision));
    }

    public void addEvent(SyncRequestEvent event) {
        events.add(event);
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

    @Override
    protected Response parseResponse(Document doc) {
        return SyncResponse.fromXml(doc.getDocumentElement());
    }
}
