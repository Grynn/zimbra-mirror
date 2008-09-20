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

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.cs.mailbox.MetadataList;
import com.zimbra.cs.offline.util.yab.Contact;
import com.zimbra.cs.offline.util.yab.Session;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.w3c.dom.Document;

public class SyncState {
    private final SyncSession sync;
    private int revision;  // YAB revision number
    private int sequence;  // Zimbra mailbox last change id
    private Map<Integer, Integer> contactIdByItemId = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> itemIdByContactId = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> categoryIdByItemId = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> itemIdByCategoryId = new HashMap<Integer, Integer>();
    private Map<Integer, String> contactsByItemId = new HashMap<Integer, String>();

    private static final String YAB = "YAB";
    private static final String REV = "REV";
    private static final String SEQ = "SEQ";
    private static final String CIDS = "CIDS";
    private static final String CATIDS = "CATIDS";
    private static final String CONTACTS = "CONTACTS";

    private static final Log LOG = OfflineLog.yab;

    public static SyncState load(SyncSession sync)
        throws ServiceException {
        return new SyncState(sync).load();
    }

    private SyncState(SyncSession sync) {
        this.sync = sync;
    }

    private SyncState load() throws ServiceException {
        Mailbox mbox = sync.getMailbox();
        Metadata md = mbox.getConfig(sync.getContext(), YAB);
        if (md == null) return this;
        revision = (int) md.getLong(REV);
        sequence = (int) md.getLong(SEQ);
        loadIds(md.getList(CIDS), contactIdByItemId, itemIdByContactId);
        loadIds(md.getList(CATIDS), categoryIdByItemId, itemIdByCategoryId);
        loadContacts(md.getList(CONTACTS));
        LOG.debug("Loaded sync state: %s", this);
        return this;
    }

    private static void loadIds(MetadataList ml,
                                Map<Integer, Integer> remoteByLocalId,
                                Map<Integer, Integer> localByRemoteId) {
        if (ml == null) return;
        assert ml.size() % 2 == 0;
        for (Iterator it = ml.asList().iterator(); it.hasNext(); ) {
            Integer localId = ((Long) it.next()).intValue();
            Integer remoteId = ((Long) it.next()).intValue();
            remoteByLocalId.put(localId, remoteId);
            localByRemoteId.put(remoteId, localId);
        }
    }

    private void loadContacts(MetadataList ml) throws ServiceException {
        contactsByItemId = new HashMap<Integer, String>(ml.size() / 2);
        for (int i = 0; i < ml.size(); ) {
            int cid = Integer.parseInt(ml.get(i++));
            String contact = ml.get(i++);
            contactsByItemId.put(cid, contact);
        }
    }
    
    public void save() throws ServiceException {
        Metadata md = new Metadata();
        md.put(REV, revision);
        md.put(SEQ, sequence);
        md.put(CIDS, saveIds(contactIdByItemId));
        md.put(CATIDS, saveIds(categoryIdByItemId));
        md.put(CONTACTS, saveContacts());
        sync.getMailbox().setConfig(sync.getContext(), YAB, md);
        LOG.debug("Saved sync state: %s", this);
    }

    private static List<Long> saveIds(Map<Integer, Integer> remoteByLocalId) {
        List<Long> ids = new ArrayList<Long>(remoteByLocalId.size() * 2);
        for (Map.Entry<Integer, Integer> e : remoteByLocalId.entrySet()) {
            ids.add(e.getKey().longValue());
            ids.add(e.getValue().longValue());
        }
        return ids;
    }

    private MetadataList saveContacts() {
        MetadataList ml = new MetadataList();
        for (Map.Entry<Integer, String> me : contactsByItemId.entrySet()) {
            ml.add(me.getKey().toString());
            ml.add(me.getValue());
        }
        return ml;
    }

    public void delete() throws ServiceException {
        sync.getMailbox().setConfig(sync.getContext(), YAB, null);
        clear();
    }

    public void clear() {
        revision = 0;
        sequence = 0;
        contactIdByItemId.clear();
        itemIdByContactId.clear();
        categoryIdByItemId.clear();
        itemIdByCategoryId.clear();
        contactsByItemId.clear();
    }

    public int getRevision() { return revision; }
    public int getSequence() { return sequence; }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getContactId(int itemId) {
        return getInt(contactIdByItemId, itemId);
    }

    public int getContactItemId(int contactId) {
        return getInt(itemIdByContactId, contactId);
    }

    public int getCategoryId(int itemId) {
        return getInt(categoryIdByItemId, itemId);
    }

    public int getCategoryItemId(int categoryId) {
        return getInt(itemIdByCategoryId, categoryId);
    }

    private static int getInt(Map<Integer, Integer> map, int key) {
        Integer value = map.get(key);
        return value != null ? value : -1;
    }

    public void addContact(int itemId, int contactId) {
        if (getContactId(itemId) != -1) {
            throw new IllegalArgumentException("Duplicate contact id");
        }
        contactIdByItemId.put(itemId, contactId);
        itemIdByContactId.put(contactId, itemId);
        LOG.debug("Added contact for itemId=%d, cid=%d", itemId, contactId);
    }


    public void removeContact(int itemId) {
        Integer cid = contactIdByItemId.remove(itemId);
        if (cid != null) {
            itemIdByContactId.remove(cid);
        }
    }

    public void addCategory(int itemId, int categoryId) {
        categoryIdByItemId.put(itemId, categoryId);
        itemIdByCategoryId.put(categoryId, itemId);
    }

    public void removeCategory(int itemId) {
        Integer catid = categoryIdByItemId.remove(itemId);
        if (catid != null) {
            itemIdByCategoryId.remove(catid);
        }
    }

    public String toString() {
        return String.format("[rev=%d,seq=%d,contacts=%d,categories=%d]",
            revision, sequence, contactIdByItemId.size(), categoryIdByItemId.size());
    }

    // TODO Remove following methods once we store contact in per-item metadata

    public Contact getContact(int itemId) throws ServiceException {
        String s = contactsByItemId.get(itemId);
        LOG.debug("Loading contact for itemId = %s:\n", s);
        if (s != null) {
            try {
                Document doc = sync.getSession().parseDocument(s);
                return Contact.fromXml(doc.getDocumentElement());
            } catch (Exception e) {
                throw ServiceException.FAILURE(
                    "Unable to parse contact for cid " + itemId, null);
            }
        }
        return null;
    }

    public void updateContact(Contact contact) throws SyncException {
        int cid = contact.getId();
        int itemId = getContactItemId(cid);
        if (itemId == -1) {
            throw new IllegalArgumentException("Unknown contact id " + cid);
        }
        String data = serialize(contact);
        contactsByItemId.put(itemId, data);
        LOG.debug("Saved contact data for itemId=%d:\n%s", itemId, data);
    }

    private String serialize(Contact contact) {
        Session session = sync.getSession();
        Document doc = session.createDocument();
        return session.toString(contact.toXml(doc));

    }
}
