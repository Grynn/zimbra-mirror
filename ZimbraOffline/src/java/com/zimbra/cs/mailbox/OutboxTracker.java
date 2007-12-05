package com.zimbra.cs.mailbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;

class OutboxTracker {
	
    /**
     * Outgoing messages in Outbox.  This is an in memory cache so that we don't have to run derby queries all the time
     * 
     * key: mailbox id
     * value: a map of item-id -> last time tried and failed (0 means never tried)
     */
    private static Map<Integer, Map<Integer, Long>> sOutboxMessageMap = Collections.synchronizedMap(new HashMap<Integer, Map<Integer, Long>>());
    
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
			sOutboxMessageMap.get(mbox.getId()).put(itemId, System.currentTimeMillis());
		}
    }
	
    static void remove(Mailbox mbox, int itemId) {
    	synchronized (sOutboxMessageMap) {
    		sOutboxMessageMap.get(mbox.getId()).remove(itemId);
		}
    }

    private static void refresh(Mailbox mbox) throws ServiceException {
    	int[] pendingSends = mbox.listItemIds(new OperationContext(mbox), MailItem.TYPE_MESSAGE, OfflineMailbox.ID_FOLDER_OUTBOX);
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
