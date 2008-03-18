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

    public int getCid(int contactId) {
        return localRemoteCidMap.get(contactId);
    }

    public int getContactId(int cid) {
        return remoteLocalCidMap.get(cid);
    }

    public int getCatid(int categoryId) {
        return localRemoteCatidMap.get(categoryId);
    }

    public int getCategoryId(int catid) {
        return remoteLocalCatidMap.get(catid);
    }

    public int[] getCids(int[] contactIds) {
        int[] cids = new int[contactIds.length];
        for (int i = 0; i < contactIds.length; i++) {
            cids[i] = getCid(contactIds[i]);
        }
        return cids;
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
