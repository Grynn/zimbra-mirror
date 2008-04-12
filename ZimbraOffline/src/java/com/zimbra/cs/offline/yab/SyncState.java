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
import com.zimbra.common.service.ServiceException;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

public class SyncState {
    private int revision;  // YAB revision number
    private int sequence;  // Zimbra mailbox last change id
    private Map<Integer, Integer> localRemoteCidMap = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> remoteLocalCidMap = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> localRemoteCatidMap = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> remoteLocalCatidMap = new HashMap<Integer, Integer>();

    private static final String YAB = "YAB";
    private static final String REV = "REV";
    private static final String SEQ = "SEQ";
    private static final String CIDS = "CIDS";
    private static final String CATIDS = "CATIDS";

    public static SyncState load(Mailbox mbox) throws ServiceException {
        return new SyncState().loadState(mbox);
    }

    private SyncState loadState(Mailbox mbox) throws ServiceException {
        Metadata md = mbox.getConfig(new Mailbox.OperationContext(mbox), YAB);
        if (md == null) return this;
        revision = (int) md.getLong(REV);
        sequence = (int) md.getLong(SEQ);
        loadIds(md.getList(CIDS), localRemoteCidMap, remoteLocalCidMap);
        loadIds(md.getList(CATIDS), localRemoteCatidMap, remoteLocalCatidMap);
        return this;
    }

    public int getRevision() { return revision; }
    
    public int getSequence() { return sequence; }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
    
    public int getCid(int contactId) {
        return getInt(localRemoteCidMap, contactId);
    }

    public int getContactId(int cid) {
        return getInt(remoteLocalCidMap, cid);
    }

    public int getCatid(int categoryId) {
        return getInt(localRemoteCatidMap, categoryId);
    }

    public int getCategoryId(int catid) {
        return getInt(remoteLocalCatidMap, catid);
    }

    private static int getInt(Map<Integer, Integer> map, int key) {
        Integer value = map.get(key);
        return value != null ? value : -1;
    }

    public List<Integer> getCids(Collection<Integer> contactIds) {
        List<Integer> cids = new ArrayList<Integer>(contactIds.size());
        for (int contactId : contactIds) {
            int cid = getCid(contactId);
            if (cid != -1) cids.add(cid);
        }
        return cids;
    }
    
    public void addContact(int contactId, int cid) {
        localRemoteCidMap.put(contactId, cid);
        remoteLocalCidMap.put(cid, contactId);
    }

    public void addCategory(int categoryId, int catid) throws SyncException {
        localRemoteCatidMap.put(categoryId, catid);
        remoteLocalCatidMap.put(catid, categoryId);
    }

    public void removeContact(int contactId) {
        Integer cid = localRemoteCidMap.remove(contactId);
        if (cid != null) {
            remoteLocalCidMap.remove(cid);
        }
    }

    public void remoteCategory(int categoryId) {
        Integer catid = localRemoteCatidMap.remove(categoryId);
        if (catid != null) {
            remoteLocalCatidMap.remove(catid);
        }
    }
    
    public void save() throws ServiceException {
        Metadata md = new Metadata();
        md.put(REV, revision);
        md.put(SEQ, sequence);
        if (!localRemoteCidMap.isEmpty()) {
            md.put(CIDS, idList(localRemoteCidMap));
        }
        if (!localRemoteCatidMap.isEmpty()) {
            md.put(CATIDS, idList(localRemoteCatidMap));
        }
    }
    
    private static void loadIds(MetadataList ids,
                                Map<Integer, Integer> localRemoteMap,
                                Map<Integer, Integer> remoteLocalMap) {
        if (ids == null) return;
        assert ids.size() % 2 == 0;
        for (Iterator it = ids.asList().iterator(); it.hasNext(); ) {
            Integer local = ((Long) it.next()).intValue();
            Integer remote = ((Long) it.next()).intValue();
            localRemoteMap.put(local, remote);
            remoteLocalMap.put(remote, local);
        }
    }

    private static List<Long> idList(Map<Integer, Integer> localRemoteMap) {
        List<Long> ids = new ArrayList<Long>(localRemoteMap.size() * 2);
        for (Map.Entry<Integer, Integer> e : localRemoteMap.entrySet()) {
            ids.add(e.getKey().longValue());
            ids.add(e.getValue().longValue());
        }
        return ids;
    }
}
