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
package com.zimbra.cs.offline.gab;

import com.google.gdata.client.Service.GDataRequest.RequestType;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.util.VersionConflictException;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.common.util.Log;
import com.zimbra.common.service.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

public class SyncRequest {
    private final SyncSession session;
    private final int itemId;
    private final RequestType type;
    private ContactEntry entry;
    private com.google.gdata.util.ServiceException error;

    private static final Log LOG = OfflineLog.gab;

    private static final int MAX_COUNT = 3;
    
    public SyncRequest(SyncSession session, int itemId, RequestType type,
                       ContactEntry entry) {
        this.session = session;
        this.itemId = itemId;
        this.type = type;
        this.entry = entry;
    }

    public int getItemId() { return itemId; }
    public RequestType getType() { return type; }
    public ContactEntry getEntry() { return entry; }

    private boolean isVersionConflict() {
        return error != null && error instanceof VersionConflictException;
    }

    public void execute() throws ServiceException, IOException {
        if (type == RequestType.UPDATE) {
            int count = 0;
            while (count++ < MAX_COUNT && !doExecute() && isVersionConflict()) {
                LOG.debug("Retrying UPDATE request for itemId %d (count = %d)",
                          itemId, count);
                VersionConflictException vce = (VersionConflictException) error;
                entry = getCurrentEntry(vce);
            }
        } else {
            doExecute();
        }
        if (error != null) {
            LOG.debug("%s request failed for item id %d: %s", type, itemId,
                      error.getMessage());
            throw ServiceException.FAILURE(type + " request failed", error);
        }
    }
    
    private boolean doExecute() throws IOException {
        ContactsService cs = session.getContactsService();
        if (session.isTraceEnabled()) {
            LOG.debug("Executing %s request for item id %d:\n%s", type, itemId,
                      session.pp(entry));
        }
        error = null;
        try {
            switch (type) {
            case INSERT:
                entry = cs.insert(session.getContactsFeedUrl(), entry);
                break;
            case UPDATE:
                entry = cs.update(getEditUrl(entry), entry);
                break;
            case DELETE:
                cs.delete(getEditUrl(entry));
                break;
            default:
                throw new AssertionError("Invalid request type: " + type);
            }
        } catch (com.google.gdata.util.ServiceException e) {
            error = e;
            return false;
        }
        return true;
    }

    private ContactEntry getCurrentEntry(VersionConflictException e)
        throws ServiceException, IOException {
        String s = e.getResponseBody();
        if (s == null) {
            throw ServiceException.FAILURE("Missing response body", null);
        }
        return session.parseContactEntry(s);
    }
    
    private static URL getEditUrl(ContactEntry entry) throws MalformedURLException {
        return new URL(entry.getEditLink().getHref());
    }
}
