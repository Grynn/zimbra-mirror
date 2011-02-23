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
package com.zimbra.cs.mailbox;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;
import java.util.EnumSet;

import org.dom4j.ElementHandler;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element.XMLElement;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.offline.common.OfflineConstants.SyncStatus;
import com.zimbra.cs.util.Zimbra;
import com.zimbra.cs.util.ZimbraApplication;

public class GalSync {
    private static GalSync instance = new GalSync();
    private TimerTask currentTask;
    private boolean isDisabled = false;
    private boolean isRunning = false;
    private OfflineProvisioning prov;
    
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
                    else if (OfflineSyncManager.getInstance().isServiceActive())
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

        if (!isOnRequest) {
            if (!syncMan.retryOK(galAccount))
                return;
            long interval = OfflineLC.zdesktop_gal_sync_interval_secs.longValue();
            long last = syncMan.getLastSyncTime(galAccount);
            if (last > 0 && (System.currentTimeMillis() - last) / 1000 < interval)
                return;
        }

        syncMan.syncStart(galAccount);
        String user = ombx.getRemoteUser();
        boolean traceOn = account.isDebugTraceEnabled();
        try {
            OfflineLog.offline.info("Offline GAL sync started for " + user);
            syncGal(ombx, account, galAccount, traceOn, isOnRequest);
            syncMan.syncComplete(galAccount);
            OfflineLog.offline.info("Offline GAL sync completed successfully for " + user);
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

    private void syncGal(ZcsMailbox mbox, OfflineAccount account, OfflineAccount galAccount, boolean traceEnabled, boolean isOnRequest)
        throws ServiceException, IOException {
        String syncToken = account.getAttr(OfflineConstants.A_offlineGalAccountSyncToken, false);        
        boolean fullSync = (syncToken == null || syncToken.length() == 0);
        
        GalSyncSAXHandler handler =  new GalSyncSAXHandler(mbox, galAccount, fullSync, traceEnabled);
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
        String token;
        if (e != null) {
            if (e instanceof ServiceException)
                throw (ServiceException) e;
            else
                throw (IOException) e;
        } else if ((token = handler.getToken()) == null) {
            throw ServiceException.FAILURE("unable to search GAL", null);
        }

        while (handler.getGroupCount() > 0) {
            handler.fetchContacts();
            if (handler.getGroupCount() > 0) {
                try {
                    Thread.sleep(OfflineLC.zdesktop_gal_sync_group_interval.longValue());
                } catch (InterruptedException ie) {}
            }
        }

        if (fullSync) { // after a full sync, reset maintenance timer
            prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalAccountLastRefresh,
                Long.toString(System.currentTimeMillis()));
        }
        prov.setAccountAttribute(account, OfflineConstants.A_offlineGalAccountSyncToken, token);
    }
}
