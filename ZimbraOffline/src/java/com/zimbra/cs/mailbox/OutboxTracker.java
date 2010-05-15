/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.mailbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;

class OutboxTracker {
    /*
     * Outgoing messages in Outbox.  This is an in memory cache so that we don't have to run derby queries all the time
     * 
     * key: mailbox id
     * value: a map of item-id -> last time triesd and failed (0 means never tried)
     */
    private static final Map<Long, Map<Integer, Long>> sOutboxMessageMap = Collections.synchronizedMap(new HashMap<Long, Map<Integer, Long>>());

    static void invalidate(Mailbox mbox) {
        synchronized (sOutboxMessageMap) {
            sOutboxMessageMap.remove(mbox.getId());
        }
    }

    static Iterator<Integer> iterator(Mailbox mbox, long retryDelay) throws ServiceException {
        Map<Integer, Long> outboxMap = null;
        synchronized (sOutboxMessageMap) {
            outboxMap = sOutboxMessageMap.get(mbox.getId());
            if (outboxMap == null) {
                refresh(mbox);
                outboxMap = sOutboxMessageMap.get(mbox.getId());
            }
        }
        List<Integer> msgList = new ArrayList<Integer>();
        long now = System.currentTimeMillis();
        for (Map.Entry<Integer, Long> e : outboxMap.entrySet()) {
            if (now - e.getValue() > retryDelay)
                msgList.add(e.getKey());
        }
        Collections.sort(msgList, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });
        return msgList.iterator();
    }

    static void recordFailure(Mailbox mbox, int itemId) {
        synchronized (sOutboxMessageMap) {
            Map<Integer, Long> outboxMap = sOutboxMessageMap.get(mbox.getId());
            if (outboxMap != null)
                outboxMap.put(itemId, System.currentTimeMillis());
        }
    }

    static void remove(Mailbox mbox, int itemId) {
        synchronized (sOutboxMessageMap) {
            Map<Integer, Long> outboxMap = sOutboxMessageMap.get(mbox.getId());
            if (outboxMap != null)
                outboxMap.remove(itemId);
        }
    }

    private static void refresh(Mailbox mbox) throws ServiceException {
        List<Integer> pendingSends = mbox.listItemIds(new OperationContext(mbox), MailItem.TYPE_MESSAGE, ZcsMailbox.ID_FOLDER_OUTBOX);
        synchronized (sOutboxMessageMap) {
            Map<Integer, Long> oldMap = sOutboxMessageMap.get(mbox.getId());
            Map<Integer, Long> newMap = new HashMap<Integer, Long>();
            for (int id : pendingSends) {
                newMap.put(id, (oldMap == null ? 0L : (oldMap.get(id) == null ? 0L : oldMap.get(id))));
            }
            sOutboxMessageMap.put(mbox.getId(), newMap);
        }
    }
}
