/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineGal;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;

public final class GalSyncRetry {

    private Map<Mailbox, MailboxGalSyncRetry> mboxRetryMap = new ConcurrentHashMap<Mailbox, MailboxGalSyncRetry>();

    private GalSyncRetry() {}

    private static final class LazyHolder {
        static GalSyncRetry instance = new GalSyncRetry();
    }

    private synchronized MailboxGalSyncRetry getRetry(OfflineAccount galAccount) throws ServiceException {
        Mailbox galMbox = MailboxManager.getInstance().getMailboxByAccount(galAccount);
        if (!this.mboxRetryMap.containsKey(galMbox)) {
            try {
                this.mboxRetryMap.put(galMbox, new MailboxGalSyncRetry(galMbox));
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
        return this.mboxRetryMap.get(galMbox);
    }

    private synchronized MailboxGalSyncRetry getExistingRetry(Mailbox galMbox) {
        return this.mboxRetryMap.get(galMbox);
    }

    public static void checkpoint(OfflineAccount galAccount, List<String> retryContactIds)
    throws ServiceException {
        LazyHolder.instance.getRetry(galAccount).checkpoint(retryContactIds);
    }

    public static void retry(OfflineAccount galAccount, List<String> retryContactIds)
    throws ServiceException, IOException {
        LazyHolder.instance.getRetry(galAccount).retry(retryContactIds);
    }

    public static boolean remove(Mailbox galMbox) throws ServiceException {
        MailboxGalSyncRetry retry = LazyHolder.instance.getExistingRetry(galMbox);
        if (retry != null) {
            retry.remove();
            return true;
        }
        return false;
    }

    private static final class MailboxGalSyncRetry {

        private static String OfflineGalSyncRetry = "offline_gal_retry";
        private Set<String> retryIds = new HashSet<String>();

        private Mailbox galMbox;
        private Metadata md;
        private long lastRetry;
        private OperationContext context;
        private int syncFolder;
        private DataSource ds;

        MailboxGalSyncRetry(Mailbox galMbox) throws ServiceException {
            this.galMbox = galMbox;
            this.context = new OperationContext(this.galMbox);
            this.syncFolder = OfflineGal.getSyncFolder(this.galMbox, this.context, false).getId();
            this.ds = OfflineProvisioning.getOfflineInstance().getDataSource(galMbox.getAccount());
            
            this.md = this.galMbox.getConfig(null, OfflineGalSyncRetry);
            if (this.md == null) {
                this.md = new Metadata();
                this.md.put(this.galMbox.getAccountId(), "");
                this.galMbox.setConfig(null, OfflineGalSyncRetry, md);
                this.lastRetry = System.currentTimeMillis();
            } else {
                String retryIds = this.md.get(this.galMbox.getAccountId());
                if (!StringUtil.isNullOrEmpty(retryIds)) {
                    this.retryIds.addAll(Arrays.asList(retryIds.split(",")));
                }
                this.lastRetry = 0;
            }
        }

        private void addRetryIds(List<String> ids) {
            this.retryIds.addAll(ids);
        }

        private String getRetryItems() {
            StringBuilder builder = new StringBuilder();
            boolean isFirst = true;
            for (String id : this.retryIds) {
                if (!isFirst) {
                    builder.append(",");
                }
                isFirst = false;
                builder.append(id);
            }
            return builder.toString();
        }

        private void clearRetryItems() {
            this.retryIds.clear();
        }

        private boolean needsRetry() {
            boolean result = (this.retryIds.size() != 0);
            if (result && this.lastRetry == 0) {
                this.lastRetry = System.currentTimeMillis();
            }
            return result;
        }

        private boolean needsRetryNow() {
            return (((System.currentTimeMillis() - this.lastRetry) / 1000)
                    > OfflineLC.zdesktop_gal_sync_retry_interval_secs.longValue());
        }

        void retry(List<String> retryIds) throws ServiceException, IOException {
            if (needsRetry()) {
                if (needsRetryNow()) {
                    if (!retryIds.isEmpty()) {
                        OfflineLog.offline.info("Offline GAL sync retry " + retryIds.size() + " items");
                        String galAcctId = retryIds.get(0).split(":")[0];
                        GalSyncUtil.fetchContacts(galMbox, context, syncFolder, getRetryItems(),
                                false, ds, retryIds, "", galAcctId);
                        clearRetryItems();
                        addRetryIds(retryIds);
                        checkpoint();
                        this.lastRetry = System.currentTimeMillis();
                        OfflineLog.offline.info("Offline GAL sync retry finished");
                    } else {
                        OfflineLog.offline.info("Offline GAL sync retry passed, no retry items");
                    }
                } else {
                    OfflineLog.offline.info("Offline GAL sync retry skipped, not yet time to do it");
                }
            } else {
                OfflineLog.offline.info("Offline GAL sync retry bypassed, no retry items");
            }
        }

        void checkpoint(List<String> ids) throws ServiceException {
            addRetryIds(ids);
            checkpoint();
        }

        void checkpoint() throws ServiceException {
            this.md.put(this.galMbox.getAccountId(), getRetryItems());
            this.galMbox.setConfig(null, OfflineGalSyncRetry, this.md);
        }

        void remove() throws ServiceException {
            this.md = null;
            this.galMbox.setConfig(null, OfflineGalSyncRetry, this.md);
        }

        public String toString() {
            return getRetryItems();
        }
    }
}
