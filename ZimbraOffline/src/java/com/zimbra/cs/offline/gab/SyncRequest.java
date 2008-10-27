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
import com.google.gdata.data.Link;
import com.google.gdata.util.VersionConflictException;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.common.util.Log;
import com.zimbra.common.service.ServiceException;

import java.io.IOException;
import java.net.URL;

public class SyncRequest {
    private final SyncSession session;
    private final int itemId;
    private final RequestType type;
    private final ContactEntry entry;
    private Link editLink;
    private ContactEntry currentEntry;
    private Throwable error;

    private static final Log LOG = OfflineLog.gab;
    
    public SyncRequest(SyncSession session, int itemId, RequestType type,
                       ContactEntry entry) {
        this.session = session;
        this.itemId = itemId;
        this.type = type;
        this.entry = entry;
        editLink = entry.getEditLink();
    }

    public int getItemId() { return itemId; }
    public RequestType getType() { return type; }
    public ContactEntry getEntry() { return entry; }
    public ContactEntry getCurrentEntry() { return currentEntry; }
    public Throwable getError() { return error; }
    public boolean isSuccess() { return error == null; }

    private boolean isVersionConflict() {
        return error != null && error instanceof VersionConflictException;
    }
    
    public boolean execute() throws ServiceException, IOException {
        while (!doExecute() && isVersionConflict()) {
            // Retry with new edit link if remote contact has changed
            if (type == RequestType.UPDATE) {
                // TODO Merge non-conflicting remote contact changes
                LOG.debug("Detected version conflict during update for item " +
                          "id %d - Overriding remote contact with local", itemId);
            }
            VersionConflictException vce = (VersionConflictException) error;
            editLink = getCurrentEntry(vce).getEditLink();
        }
        if (!isSuccess()) {
            LOG.debug("Contact sync '%s' request failed for itemid = %d",
                      type, itemId, error);
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
    
    private boolean doExecute() throws IOException {
        ContactsService cs = session.getContactsService();
        if (session.isTraceEnabled()) {
            LOG.debug("Executing %s request for entry (itemid = %d):\n%s",
                      type, itemId, session.pp(entry));
        }
        error = null;
        try {
            switch (type) {
            case INSERT:
                currentEntry = cs.insert(session.getContactsFeedUrl(), entry);
                break;
            case UPDATE:
                currentEntry = cs.update(new URL(editLink.getHref()), entry);
                break;
            case DELETE:
                cs.delete(new URL(editLink.getHref()));
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

}
