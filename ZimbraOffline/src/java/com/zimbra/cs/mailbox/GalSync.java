/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010, 2011 Zimbra, Inc.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.dom4j.ElementHandler;

import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.Element.XMLElement;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.common.account.Key.AccountBy;
import com.zimbra.common.account.ProvisioningConstants;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineGal;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.common.OfflineConstants.SyncStatus;
import com.zimbra.cs.util.Zimbra;
import com.zimbra.cs.util.ZimbraApplication;

public class GalSync {
    private static GalSync instance = new GalSync();
    private TimerTask currentTask;
    private boolean isDisabled = false;
    private volatile boolean isRunning = false;
    private OfflineProvisioning prov;

    private List<String> retryContactIds = new ArrayList<String>();

    public static GalSync getInstance() {
        return instance;
    }

    private Timer timer = new Timer("sync-timer-gal");
    private GalSync() {
        if (!OfflineLC.zdesktop_sync_gal.booleanValue()) {
            OfflineLog.offline.debug("Offline GAL sync is disabled in local config (zdesktop_sync_gal = false)");
            isDisabled = true;
            return;
        }

        prov = (OfflineProvisioning)Provisioning.getInstance();

        timer.schedule(currentTask = new TimerTask() {
            @Override public void run() {
                if (ZimbraApplication.getInstance().isShutdown()) {
                    currentTask.cancel();
                    return;
                }
                try {
                    runTask(null, false); // sync all accounts
                } catch (Throwable e) { //don't let exceptions kill the timer
                    if (e instanceof OutOfMemoryError)
                        Zimbra.halt("caught out of memory error", e);
                    else if (OfflineSyncManager.getInstance().isServiceActive(false))
                        OfflineLog.offline.warn("caught exception in timer ", e);
                }
            }
        },
        5 * Constants.MILLIS_PER_SECOND,
        OfflineLC.zdesktop_gal_sync_timer_frequency.longValue());
    }

    /*
     * the only starting point for running gal-sync task. if account == null, sync gal for all zcs accounts
     */
    public void runTask(OfflineAccount targetAccount, boolean isOnRequest) {
        if (isDisabled) {
            OfflineLog.offline.debug("Offline GAL sync is disabled in local config (zdesktop_sync_gal = false)");
            return;
        }

        synchronized (this) {
            if (isRunning) {
                OfflineLog.offline.info("Offline GAL sync is already running");
                return;
            } else {
                isRunning = true;
            }
        }
        try {
            for (Account acct : prov.getAllZcsAccounts()) {
                OfflineAccount account = (OfflineAccount) acct;
                if (targetAccount != null && !account.getId().equals(targetAccount.getId())) {
                    continue;
                }
                ZcsMailbox ombx = (ZcsMailbox) OfflineMailboxManager.getInstance().getMailboxByAccount(account);
                //TODO 1. There is only one GAL account at zcs server, so we only need to sync it once for every zcs account.
                sync(ombx, isOnRequest);
                if (targetAccount != null) {
                    break;
                }
            }
        } catch (ServiceException e) {
            OfflineLog.offline.warn("Offline GAL sync task failed: " + e.getMessage());
        } finally {
            isRunning = false;
        }
        return;
    }

    private void sync(ZcsMailbox ombx, boolean isOnRequest) throws ServiceException {
        OfflineAccount account = (OfflineAccount)ombx.getAccount();
        if (!account.getBooleanAttr(Provisioning.A_zimbraFeatureGalEnabled , false) ||
            !account.getBooleanAttr(Provisioning.A_zimbraFeatureGalSyncEnabled , false)) {
            OfflineLog.offline.debug("Offline GAL sync is disabled: " + ombx.getRemoteUser());
            ensureGalAccountNotExists(account);
            return;
        }
        OfflineAccount galAccount = ensureGalAccountExists(account);
        OfflineSyncManager syncMan = OfflineSyncManager.getInstance();
        if (syncMan.getSyncStatus(galAccount) == SyncStatus.running)
            return;

        String user = ombx.getRemoteUser();
        boolean traceOn = account.isDebugTraceEnabled();
        Mailbox galMbox = MailboxManager.getInstance().getMailboxByAccountId(galAccount.getId(), false);
        
        if (!isOnRequest) {
            if (account.isGalSyncRetryOn()) {
                OfflineLog.offline.info("Offline GAL sync retry for " + user);
                try {
                    GalSyncRetry.retry(ombx, galMbox, this.retryContactIds);
                } catch (IOException e) {
                    syncMan.processSyncException(galAccount, "", e, traceOn);
                }
            }

            if (!syncMan.retryOK(galAccount))
                return;
            long interval = OfflineLC.zdesktop_gal_sync_interval_secs.longValue();
            long last = syncMan.getLastSyncTime(galAccount);
            if (last > 0 && (System.currentTimeMillis() - last) / 1000 < interval) {
                return;
            }
        }

        syncMan.syncStart(galAccount);

        try {
            OfflineLog.offline.info("Offline GAL sync started for " + user);
            syncGal(ombx, galMbox, account, galAccount, traceOn, isOnRequest);
            syncMan.syncComplete(galAccount);
            GalSyncCheckpointUtil.removeCheckpoint(galMbox);
        } catch (Exception e) {
            syncMan.processSyncException(galAccount, "", e, traceOn);
            OfflineLog.offline.info("Offline GAL sync failed for " + user + ": " + e.getMessage());
        }
    }

    private void ensureGalAccountNotExists(OfflineAccount account) throws ServiceException {
        prov.deleteGalAccount(account);
    }

    private OfflineAccount ensureGalAccountExists(OfflineAccount account) throws ServiceException {
        String galAcctId = account.getAttr(OfflineConstants.A_offlineGalAccountId, false);
        OfflineAccount galAcct;
        if (galAcctId == null || galAcctId.length() == 0 || (galAcct = (OfflineAccount)prov.get(AccountBy.id, galAcctId)) == null) {
            galAcct = prov.createGalAccount(account);
            OfflineLog.offline.info("Offline GAL mailbox created: " + galAcct.getName());
        } else { // migration: move existing token from gal account to main account; lastFullSync to LastRefresh
            String token = account.getAttr(OfflineConstants.A_offlineGalAccountSyncToken, false);
            if (token == null) {
                token = galAcct.getAttr(OfflineConstants.A_offlineGalAccountSyncToken, false);
                if (token != null && !token.isEmpty()) {
                    prov.setAccountAttribute(account, OfflineConstants.A_offlineGalAccountSyncToken, token);
                    
                    long fs = galAcct.getLongAttr(OfflineConstants.A_offlineGalAccountLastFullSync, 0);
                    prov.setAccountAttribute(galAcct, OfflineConstants.A_offlineGalAccountLastRefresh, Long.toString(fs));
                }
            }
        }
        return galAcct;
    }

    private void syncGal(ZcsMailbox mbox, Mailbox galMbox, OfflineAccount account, OfflineAccount galAccount, boolean traceEnabled, boolean isOnRequest)
        throws ServiceException, IOException {
        String syncToken = account.getAttr(OfflineConstants.A_offlineGalAccountSyncToken, false);
        boolean fullSync = (syncToken == null || syncToken.length() == 0);

        GalSyncSAXHandler handler =  new GalSyncSAXHandler(mbox, galAccount, fullSync, traceEnabled);
        String token = "";
        if (fullSync && GalSyncCheckpointUtil.hasCheckpoint(galMbox)) {
            handler.loadCheckpointingInfo(galMbox);
            token = handler.getToken();
        } else {
            if (!isOnRequest && !fullSync) { // don't run the potentially lengthy maintenance if it's manual or full
                handler.runMaintenance();
            }

            XMLElement req = new XMLElement(AccountConstants.SYNC_GAL_REQUEST);
            req.addAttribute(AccountConstants.A_ID_ONLY, "true");
            if (!fullSync)
                req.addAttribute(AdminConstants.A_TOKEN, syncToken);

            HashMap<String, ElementHandler> handlers = new HashMap<String, ElementHandler>();
            handlers.put(GalSyncSAXHandler.PATH_RESPONSE, handler);
            handlers.put(GalSyncSAXHandler.PATH_CN, handler);
            handlers.put(GalSyncSAXHandler.PATH_DELETED, handler);

            mbox.sendRequest(req, true, true, OfflineLC.zdesktop_gal_sync_request_timeout.intValue(), SoapProtocol.Soap12, handlers);

            Exception e = handler.getException();
            if (e != null) {
                if (e instanceof ServiceException)
                    throw (ServiceException) e;
                else
                    throw (IOException) e;
            } else if ((token = handler.getToken()) == null) {
                throw ServiceException.FAILURE("unable to search GAL", null);
            }
            GalSyncCheckpointUtil.persistItemIds(galMbox, handler.getItemIds());
        }

        handler.fetchContacts(fullSync, retryContactIds);

        if (fullSync) { // after a full sync, reset maintenance timer
            prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalAccountLastRefresh,
                Long.toString(System.currentTimeMillis()));
            //we've done a full sync, group populate is implied; as long as remote server is 7xx
            if (mbox.getRemoteServerVersion().isAtLeast7xx()) {
                prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalGroupMembersPopulated, ProvisioningConstants.TRUE);
            }
            if (account.isGalSyncRetryOn()) {
                GalSyncRetry.checkpoint(mbox, galMbox, this.retryContactIds);
            }
        } else if (!galAccount.getBooleanAttr(OfflineConstants.A_offlineGalGroupMembersPopulated, false) && mbox.getRemoteServerVersion().isAtLeast7xx()) {
            //existing groups have incorrect type=account and don't have member list
            populateGroupMembers(mbox, galAccount);
            prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalGroupMembersPopulated, ProvisioningConstants.TRUE);
        }
        prov.setAccountAttribute(account, OfflineConstants.A_offlineGalAccountSyncToken, token);
        if (fullSync) {
            OfflineLog.offline.info("Offline GAL full sync completed successfully, token persisted: " + token);
        } else {
            OfflineLog.offline.info("Offline GAL delta sync completed, token persisted: " + token);
        }
    }

    public static boolean isFullSynced(Account account) {
        String syncToken = account.getAttr(OfflineConstants.A_offlineGalAccountSyncToken, false);
        return (syncToken != null && syncToken.length() != 0);
    }

    /**
     * if GAL is full synced within 5 min, consider it recently full synced.
     * @param account The account of mailbox, not GAL account
     * @return whether it's recently full synced
     */
    private static boolean isRecentlyFullSynced(Account account) {
        String lastRefreshStr = account.getAttr(OfflineConstants.A_offlineGalAccountLastRefresh);
        try {
            long lastRefresh = Long.parseLong(lastRefreshStr);
            return (System.currentTimeMillis() - lastRefresh <= 5 * Constants.MILLIS_PER_MINUTE);
        } catch (Exception e) {
        }
        return false;
    }

    public boolean resetGal(final ZcsMailbox mbox, final Mailbox galMbox, final OfflineAccount account,
            final OfflineAccount galAccount) {
        final String accountName = account.getName();
        this.timer.schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    if (isRecentlyFullSynced(galAccount)) {
                        OfflineLog.offline.debug("reseting gal for account %s -- Skipped because GAL is recently synced", accountName);
                        return;
                    }
                    OfflineLog.offline.debug("reseting gal for account %s -- 1. begin", accountName);
                    GalSyncUtil.removeConfig(mbox, galMbox);
                    OfflineLog.offline.debug("reseting gal for account %s -- 2. removed config", accountName);
                    prov.deleteGalAccount(account);
                    OfflineLog.offline.debug("reseting gal for account %s -- 3. deleted its gal", accountName);
                    OfflineLog.offline.debug("reseting gal for account %s -- 4. about to resync", accountName);
                    runTask(account, true);
                    OfflineLog.offline.debug("reseting gal for account %s -- 5. resync is done", accountName);
                } catch (Exception e) {
                    OfflineLog.offline.debug("reseting gal for account %s -- FAILED due to %s", accountName, e.getCause());
                }
            }
        }, 0);
        return true;
    }

    private void populateGroupMembers(ZcsMailbox mbox, OfflineAccount galAccount) throws ServiceException {
        OfflineLog.offline.debug("populating group members for previously created gal contacts");
        int offset = 0;
        boolean more = true;
        while (more) {
            XMLElement request = new XMLElement(AccountConstants.SEARCH_GAL_REQUEST);
            request.addAttribute(AccountConstants.A_TYPE, OfflineGal.CTYPE_GROUP);
            request.addElement(AccountConstants.A_NAME).setText(".");
            request.addAttribute(MailConstants.A_QUERY_OFFSET, offset);
            Element response = mbox.sendRequest(request, true, true, OfflineLC.zdesktop_gal_sync_request_timeout.intValue(), SoapProtocol.Soap12);
            List<Element> groups = response.listElements(MailConstants.E_CONTACT);
            more = response.getAttributeBool(MailConstants.A_QUERY_MORE);
            DataSource ds = GalSyncUtil.createDataSourceForAccount(galAccount);
            Mailbox galMbox = MailboxManager.getInstance().getMailboxByAccountId(galAccount.getId(), false);
            OperationContext ctxt = new OperationContext(galMbox);
            if (groups != null) {
                for (Element group : groups) {
                    offset++;
                    //lookup each existing entry, make sure it's type=group and has correct members
                    String id = group.getAttribute(AccountConstants.A_ID);
                    int contactId = GalSyncUtil.findContact(id, ds);
                    if (contactId == -1) {
                        //group probably added after galsync completed. will be added on next sync.
                        OfflineLog.offline.debug("contact ["+id+"] does not exist in local gal; assuming it will be synced on next pass");
                        continue;
                    }
                    Contact contact = galMbox.getContactById(ctxt, contactId);
                    Map<String,String> newFields = new HashMap<String, String>();
                    newFields.put(ContactConstants.A_type, OfflineGal.CTYPE_GROUP);
                    ParsedContact pc = new ParsedContact(contact);
                    pc.modify(newFields, null);
                    galMbox.modifyContact(ctxt, contactId, pc);
                }
            }
        }
    }
}
