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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineGal;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.db.DbDataSource;
import com.zimbra.cs.db.DbDataSource.DataSourceItem;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.db.DbMailItem.QueryParams;
import com.zimbra.cs.db.DbPool;
import com.zimbra.cs.db.DbPool.DbConnection;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.common.OfflineConstants;

public class GalSyncSAXHandler implements ElementHandler {
    public static final String PATH_RESPONSE = "/Envelope/Body/SyncGalResponse";
    public static final String PATH_CN = PATH_RESPONSE + "/cn";
    public static final String PATH_DELETED = PATH_RESPONSE + "/deleted";

    private OfflineAccount galAccount;
    private boolean fullSync;
    private Mailbox galMbox;
    private OperationContext context;
    private Exception exception = null;
    private String token = null;
    private int syncFolder;
    private int grpSize = OfflineLC.zdesktop_gal_sync_group_size.intValue();
    private DataSource ds;
    private static OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
    private List<Integer> itemIds = new ArrayList<Integer>();
    private String zcsGalAccountId = "";
    private boolean isItemIdsSorted = false;

    public GalSyncSAXHandler(OfflineAccount galAccount, Mailbox galMbox, boolean fullSync) throws ServiceException {
        this.galAccount = galAccount;
        this.fullSync = fullSync;
        prov = OfflineProvisioning.getOfflineInstance();        
        this.galMbox = galMbox;
        this.context = new OperationContext(galMbox);
        this.ds = GalSyncUtil.createDataSourceForAccount(galAccount);
        this.syncFolder = OfflineGal.getSyncFolder(galMbox, context, fullSync).getId();
        OfflineLog.offline.debug("Offline GAL current sync folder: " + Integer.toString(syncFolder));
    }

    public String getToken() {
        return token;
    }

    public OfflineAccount getGalAccount() {
        return galAccount;
    }

    public Exception getException() {
        return exception;
    }

    public List<Integer> getItemIds() {
        if (!this.isItemIdsSorted) {
            Collections.sort(this.itemIds, Collections.reverseOrder());
            this.isItemIdsSorted = true;
        }
        return this.itemIds;
    }

    public void loadCheckpointingInfo(Mailbox mbox) throws ServiceException {
        this.itemIds = GalSyncCheckpointUtil.retrieveItemIds(galMbox);
        this.zcsGalAccountId = GalSyncCheckpointUtil.getCheckpointGalAccountId(galMbox);
        this.token = GalSyncCheckpointUtil.getCheckpointToken(galMbox);
    }

    @Override
    public void onStart(ElementPath elPath) { // TODO: add trace logging;
        String path = elPath.getPath();
        if (!path.equals(PATH_RESPONSE))
            return;

        org.dom4j.Element row = elPath.getCurrent();
        token = row.attributeValue(AdminConstants.A_TOKEN);
        if (token == null) {
            OfflineLog.offline.debug("Offline GAL parse error: SyncGalResponse has no token attribute");
            unregisterHandlers(elPath);
            return;
        }
    }

    @Override
    public void onEnd(ElementPath elPath) { // TODO: add trace logging;
        String path = elPath.getPath();
        if (!path.equals(PATH_CN) && !path.equals(PATH_DELETED))
            return;

        if (token == null) {
            OfflineLog.offline.debug("Offline GAL parse error: missing SyncGalResponse tag");
            unregisterHandlers(elPath);
            return;
        }

        org.dom4j.Element row = elPath.getCurrent();
        String id = row.attributeValue(AdminConstants.A_ID);
        if (id == null) {
            OfflineLog.offline.debug("Offline GAL parse error: cn has no id attribute");
        } else if (path.equals(PATH_DELETED)) {
            try {
                deleteContact(id);
            } catch (Exception e) {
                handleException(e, elPath);
            }
        } else {
            Iterator<org.dom4j.Element> itr = row.elementIterator();
            if (itr.hasNext()) {
                Map<String, String> map = new HashMap<String, String>();
                map.put(OfflineConstants.GAL_LDAP_DN, id);
                while (itr.hasNext()) {
                    org.dom4j.Element child = (org.dom4j.Element) itr.next();
                    String key = child.attributeValue(AdminConstants.A_N);
                    if (!key.equals("objectClass"))
                        map.put(key, child.getText());
                }

                try {
                    saveUnparsedContact(id, map);
                } catch (Exception e) {
                    handleException(e, elPath);
                }
            } else {
                if (StringUtil.isNullOrEmpty(this.zcsGalAccountId)) {
                    this.zcsGalAccountId = id.split(":")[0];
                }
                itemIds.add(Integer.parseInt(id.split(":")[1]));
            }
        }

        row.detach(); // done with this node - prune it off to save memory
    }

    private void unregisterHandlers(ElementPath elPath) {
        elPath.removeHandler(PATH_CN);
        elPath.removeHandler(PATH_DELETED);
    }

    private void handleException(Exception e, ElementPath elPath) {
        OfflineLog.offline.debug("Offline GAL exception caught", e);
        if (e instanceof ServiceException || e instanceof IOException) {
            exception = e;
        }
        unregisterHandlers(elPath);
    }

    private void deleteContact(String id) throws ServiceException, IOException {
        int iid = GalSyncUtil.findContact(id, ds);
        if (iid > 0) {
            // always delete mapping first, so that in case of crash, unmapped contacts can be cleaned up in runMaintenance()
            DbDataSource.deleteMapping(ds, iid);
            galMbox.delete(context, iid, MailItem.Type.CONTACT);
            OfflineLog.offline.debug("Offline GAL contact deleted: " + Integer.toString(iid) + ", " + id);
        }
    }

    private void removeUnmapped() throws ServiceException {
        List<Integer> folderIds = new ArrayList<Integer>();
        folderIds.add(OfflineGal.getSyncFolder(galMbox, context, false).getId());
        QueryParams params = new QueryParams();
        params.setFolderIds(folderIds);

        DbConnection conn = null;
        Set<Integer> galItemIds = null;
        galMbox.lock.lock();
        try {
            conn = DbPool.getConnection();
            galItemIds = DbMailItem.getIds(galMbox, conn, params, false);
        } finally {
            DbPool.quietClose(conn);
            galMbox.lock.release();
        }
        if (galItemIds == null || galItemIds.size() == 0) {
            return;
        }

        Collection<DataSourceItem> dsItems = DbDataSource.getAllMappings(ds);
        int sz = dsItems.size();
        if (sz < galItemIds.size()) { // proceed only if mapping size is less than number of gal entries
            Set<Integer> dsItemIds = new HashSet<Integer>(sz);
            for (DataSourceItem dsi : dsItems) {
                dsItemIds.add(dsi.itemId);
            }

            try {
                galItemIds.removeAll(dsItemIds);
            } catch (Exception e) {
                OfflineLog.offline.warn("Offline GAL error in calculating set difference: " + e.getMessage());
                return;
            }

            if (galItemIds.size() > 100) {
                prov.setAccountAttribute(this.galAccount, OfflineConstants.A_offlineGalAccountSyncToken, "");
                OfflineLog.offline.warn("Offline GAL too many unmapped items: " + Integer.toString(galItemIds.size())
                        + ", falling back to full sync.");
            } else {
                for (Integer id : galItemIds) {
                    galMbox.delete(context, id.intValue(), MailItem.Type.CONTACT);
                }
                OfflineLog.offline.debug("Offline GAL deleted " + Integer.toString(galItemIds.size())
                        + " unmapped items.");
            }
        }
    }

    public void runMaintenance() {
        long lastRefresh = galAccount.getLongAttr(OfflineConstants.A_offlineGalAccountLastRefresh, 0);
        long interval = OfflineLC.zdesktop_gal_refresh_interval_days.longValue();
        if (lastRefresh > 0 && (System.currentTimeMillis() - lastRefresh) / Constants.MILLIS_PER_DAY < interval) {
            return;
        }

        try {
            OfflineLog.offline.debug("Offline GAL running maintenance");
            removeUnmapped();
            galMbox.optimize(0);
            prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalAccountLastRefresh,
                    Long.toString(System.currentTimeMillis()));
        } catch (ServiceException e) {
            OfflineLog.offline.warn("Offline GAL maintenance error: " + e.getMessage());
        }
    }

    private void saveUnparsedContact(String id, Map<String, String> map) throws ServiceException, IOException {
        boolean success = false;
        try {
            galMbox.beginTransaction("saveUnparsedContact", null);
            GalSyncUtil.fillContactAttrMap(map);
            ParsedContact contact = new ParsedContact(map);
            String logstr = GalSyncUtil.getContactLogStr(contact);
            if (fullSync) {
                GalSyncUtil.createContact(this.galMbox, this.context, this.syncFolder, this.ds, contact, id, logstr);
            } else {
                int itemId = GalSyncUtil.findContact(id, ds);
                if (itemId > 0) {
                    try {
                        galMbox.modifyContact(context, itemId, contact);
                        OfflineLog.offline.debug("Offline GAL contact modified: " + logstr);
                    } catch (MailServiceException.NoSuchItemException e) {
                        OfflineLog.offline.warn("Offline GAL modify error - no such contact: " + logstr + " itemId=" + Integer.toString(itemId));
                    }
                    GalSyncUtil
                            .createContact(this.galMbox, this.context, this.syncFolder, this.ds, contact, id, logstr);
                } else {
                }
            }
            success = true;
        } finally {
            galMbox.endTransaction(success);
        }
    }

    // needs to be called after item ids have been sorted
    private String removeItemIds() {
        StringBuilder builder = new StringBuilder();
        int size = 0;
        while (size < this.grpSize && !this.itemIds.isEmpty()) {
            if (size != 0) {
                builder.append(",");
            }
            builder.append(this.zcsGalAccountId).append(":").append(this.itemIds.remove(this.itemIds.size() - 1));
            size++;
        }
        return builder.toString();
    }

    public void fetchContacts(String domain, boolean isFullSync, List<String> retryContactIds) throws ServiceException,
            IOException {
        while (this.itemIds.size() > 0) {
            if (isFullSync) {
                int lastSyncedItemId = GalSyncCheckpointUtil.getLastSyncedItemId(this.galMbox);
                for (ListIterator<Integer> iter = this.itemIds.listIterator(this.itemIds.size()); iter.hasPrevious();) {
                    int id = iter.previous();
                    if (id <= lastSyncedItemId) {
                        OfflineLog.offline.debug("Offline GAL sync skipped item " + id);
                        iter.remove();
                    } else {
                        break;
                    }
                }
            }
            if (!this.itemIds.isEmpty()) {
                ZcsMailbox mbox = GalSyncUtil.getGalEnabledZcsMailbox(domain);
                if (mbox == null) {
                    OfflineLog.offline.debug("No gal enabled account for domain %s", domain);
                    return;
                }
                GalSyncUtil.fetchContacts(mbox, this.galMbox, this.context, syncFolder, this.removeItemIds(),
                        isFullSync, this.ds, retryContactIds, this.token, this.zcsGalAccountId);
                if (!this.itemIds.isEmpty()) { // remoteItemIds() removes items
                    try {
                        Thread.sleep(OfflineLC.zdesktop_gal_sync_group_interval.longValue());
                    } catch (InterruptedException ie) {
                    }
                }
            }
        }
    }
}
