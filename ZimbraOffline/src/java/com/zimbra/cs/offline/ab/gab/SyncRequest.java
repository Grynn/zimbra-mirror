/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.offline.ab.gab;

import com.google.gdata.client.Service.GDataRequest.RequestType;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.ContactEntry;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.common.util.Log;
import com.zimbra.common.service.ServiceException;

import java.io.IOException;

public class SyncRequest {
    private final SyncSession session;
    private final GabService service;
    private final RequestType type;
    private final int itemId;
    private BaseEntry entry;
    private byte[] photoData;
    private String photoType;

    private static final Log LOG = OfflineLog.gab;

    public static SyncRequest insert(SyncSession session, int itemId, BaseEntry entry) {
        return new SyncRequest(session, RequestType.INSERT, itemId, entry);
    }

    public static SyncRequest update(SyncSession session, int itemId, BaseEntry entry) {
        return new SyncRequest(session, RequestType.UPDATE, itemId, entry);
    }

    public static SyncRequest delete(SyncSession session, int itemId, BaseEntry entry) {
        return new SyncRequest(session, RequestType.DELETE, itemId, entry);
    }
    
    private SyncRequest(SyncSession session, RequestType type, int itemId, BaseEntry entry) {
        this.session = session;
        this.service = session.getGabService();
        this.type = type;
        this.itemId = itemId;
        this.entry = entry;
    }

    public void setPhoto(byte[] data, String type) {
        photoData = data;
        photoType = type;
    }
    
    public RequestType getType() { return type; }
    public int getItemId() { return itemId; }
    public BaseEntry getEntry() { return entry; }

    public boolean isInsert() { return type == RequestType.INSERT; }
    public boolean isUpdate() { return type == RequestType.UPDATE; }
    public boolean isDelete() { return type == RequestType.DELETE; }
    
    public boolean isGroup() {
        return entry != null && entry.getClass() == ContactGroupEntry.class;
    }

    public boolean isContact() {
        return entry != null && entry.getClass() == ContactEntry.class;
    }

    public void execute() throws ServiceException, IOException {
        if (session.isTraceEnabled()) {
            LOG.debug("Executing %s request for itemid %d:\n%s", type, itemId,
                      service.pp(entry));
        }
        try {
            switch (type) {
            case INSERT:
                entry = service.insert(entry);
                if (photoData != null && isContact()) {
                    service.addPhoto((ContactEntry) entry, photoData, photoType);
                }
                LOG.debug("Finished executing INSERT request for itemid %d: updated = %s",
                          itemId, entry.getUpdated());
                break;
            case UPDATE:
                entry = service.update(entry);
                if (isContact()) {
                    ContactEntry ce = (ContactEntry) entry;
                    if (photoData != null) {
                        service.addPhoto(ce, photoData, photoType);
                    } else {
                        service.deletePhoto(ce);
                    }
                }
                LOG.debug("Finished executing UPDATE request for itemid %d: updated = %s",
                          itemId, entry.getUpdated());
                break;
            case DELETE:
                service.delete(entry);
            }
        } catch (com.google.gdata.util.ServiceException e) {
            LOG.debug("%s request failed for item id %d: %s", type, itemId, e.getMessage());
            throw ServiceException.FAILURE(type + " request failed", e);
        }
    }
}
