/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011 Zimbra, Inc.
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zimbra.common.account.Key;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.MapUtil;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.Mailbox.MailboxData;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.session.Session;
import com.zimbra.cs.session.SoapSession;

public class OfflineMailboxManager extends MailboxManager {

    public static OfflineMailboxManager getOfflineInstance() throws ServiceException {
        return (OfflineMailboxManager) MailboxManager.getInstance();
    }

    public OfflineMailboxManager() throws ServiceException {
        super();
    }

    /** Returns a new {@link ZcsMailbox} object to wrap the given data. */
    @Override
    protected Mailbox instantiateMailbox(MailboxData data) throws ServiceException {
        return DesktopMailbox.newMailbox(data);
    }

    public void notifyAllMailboxes() throws ServiceException {
        for (String acctId : getAccountIds()) {
            OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
            Account acct = prov.get(Key.AccountBy.id, acctId);
            if (acct == null || prov.isGalAccount(acct) || prov.isMountpointAccount(acct))
                continue;

            try {
                Mailbox mbox = getMailboxByAccountId(acctId);
                // loop through mailbox sessions and signal hanging NoOps
                List<Session> sessions = mbox.getListeners(Session.Type.SOAP);
                for (Session session : sessions) {
                    if (session instanceof SoapSession)
                        ((SoapSession) session).forcePush();
                }
            } catch (ServiceException e) {
                OfflineLog.offline.warn("failed to notify mailbox account_id=" + acctId, e);
            } catch (Exception t) {
                OfflineLog.offline.error("unexpected exception notifying mailbox account_id" + acctId, t);
            }
        }
    }

    public synchronized void purgeBadMailboxByAccountId(String accountId) {
        int mailboxId = lookupMailboxId(accountId);
        try {
            MailboxData mbData = new MailboxData();
            mbData.accountId = accountId;
            mbData.id = mailboxId;
            mbData.schemaGroupId = mailboxId;
            Mailbox tempMb = new Mailbox(mbData);
            Account acct = ((OfflineProvisioning) Provisioning.getInstance()).getLocalAccount();
            LocalMailbox mbox = (LocalMailbox) MailboxManager.getInstance().getMailboxByAccount(acct);
            mbox.forceDeleteMailbox(tempMb);
        } catch (Exception e) {
            OfflineLog.offline.error("failed to purge bad mailbox due to exception", e);
        }
    }

    @Override
    protected MailboxMap createCache() {
        return new OfflineMailboxMap(OfflineLC.zdesktop_mailbox_cache.intValue());
    }

    private static class OfflineMailboxMap extends MailboxMap {

        private final Map<Integer, Object> cache;

        OfflineMailboxMap(int size) {
            // only needs to cache a couple of mailboxes and its locks, should be able to hold them all
            this.cache = MapUtil.newLruMap(size);
        }

        @Override
        public void clear() {
            this.cache.clear();
        }

        @Override
        public boolean containsKey(Object key) {
            return this.cache.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return this.cache.containsValue(value);
        }

        @Override
        public Set<Entry<Integer, Object>> entrySet() {
            Set<Entry<Integer, Object>> entries = new HashSet<Entry<Integer, Object>>(size());
            entries.addAll(this.cache.entrySet());
            return entries;
        }

        @Override
        public Object get(Object key) {
            return this.cache.get(key);
        }

        @Override
        public Object get(Object key, boolean trackGC) {
            return this.get(key);
        }

        @Override
        public boolean isEmpty() {
            return this.cache.isEmpty();
        }

        @Override
        public Set<Integer> keySet() {
            return this.cache.keySet();
        }

        @Override
        public Object put(Integer key, Object value) {
            return this.cache.put(key, value);
        }

        @Override
        public void putAll(Map<? extends Integer, ? extends Object> t) {
            this.cache.putAll(t);
        }

        @Override
        public Object remove(Object key) {
            return this.cache.remove(key);
        }

        @Override
        public int size() {
            return this.cache.size();
        }

        @Override
        public Collection<Object> values() {
            List<Object> values = new ArrayList<Object>(size());
            values.addAll(this.cache.values());
            return values;
        }

        @Override
        public String toString() {
            return this.cache.toString();
        }

    }
}
