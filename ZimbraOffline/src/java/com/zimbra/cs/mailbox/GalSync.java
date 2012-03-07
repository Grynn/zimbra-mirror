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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.dom4j.ElementHandler;

import com.google.common.collect.Sets;
import com.zimbra.common.account.Key.AccountBy;
import com.zimbra.common.account.ProvisioningConstants;
import com.zimbra.common.account.ZAttrProvisioning;
import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.Element.XMLElement;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.Pair;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineDomainGal;
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

        prov = OfflineProvisioning.getOfflineInstance();
        initGalChecking();

        timer.schedule(currentTask = new TimerTask() {

            @Override
            public void run() {
                if (ZimbraApplication.getInstance().isShutdown()) {
                    currentTask.cancel();
                    return;
                }
                try {
                    Set<String> domains = prov.listAllDomains();
                    Set<Account> currentAccounts = new HashSet<Account>(prov.getAllZcsAccounts());
                    for (Account account : currentAccounts) {
                        // delay the creation of domain and gal account,
                        // create them only when there is galsync enabled account
                        if (account.isFeatureGalEnabled() && account.isFeatureGalSyncEnabled()) {
                            String domain = ((OfflineAccount) account).getDomain();
                            if (!domains.contains(domain)) {
                                Map<String, Object> domainAttrs = new HashMap<String, Object>();
                                domainAttrs.put(OfflineProvisioning.A_offlineUsingGalAccountId, account.getId());
                                OfflineProvisioning.getOfflineInstance().createDomainGal(domain, domainAttrs);
                                domains = prov.listAllDomains();
                            } else {
                                String[] accountsUsingGal = prov.getDomainGal(domain).getAttachedToGalAccountIds();
                                if (!Arrays.asList(accountsUsingGal).contains(account.getId())) {
                                    if (account.isFeatureGalEnabled() && account.isFeatureGalSyncEnabled()) {
                                        prov.attachAccountToGal(domain, account.getId());
                                    }
                                }
                            }
                        }
                    }

                    checkGal();

                    for (String domain : prov.listAllDomains()) {
                        runTask(domain, false); // sync each domain's gal
                    }
                } catch (Throwable e) { // don't let exceptions kill the timer
                    if (e instanceof OutOfMemoryError)
                        Zimbra.halt("caught out of memory error", e);
                    else if (OfflineSyncManager.getInstance().isServiceActive(false))
                        OfflineLog.offline.warn("caught exception in timer ", e);
                }
            }
        }, 5 * Constants.MILLIS_PER_SECOND, OfflineLC.zdesktop_gal_sync_timer_frequency.longValue());
    }

    private void initGalChecking() {
        try {
            if (prov.listAllDomains().isEmpty()) {
                migrateGal();
            } else {
                checkGal();
            }
        } catch (ServiceException e) {
            OfflineLog.offline.error("offline gal migrating/checking failed", e);
        }
    }

    /**
     * attach newly added account to GAL directory, detach obsolete account from GAL directory 
     * @throws ServiceException
     */
    private void checkGal() throws ServiceException {
        OfflineLog.offline.debug("[offline gal checking] started");
        Set<String> domains = prov.listAllDomains();
        for (String domain : domains) {
            Set<String> attachedAccounts = new HashSet<String>(Arrays.asList(prov.getDomainGal(domain).getAttachedToGalAccountIds()));
            Set<String> domainAccounts = prov.getAllAccountsByDomain(domain);
            Set<String> newlyAddedAccounts = Sets.difference(domainAccounts, attachedAccounts);
            for (String accountId : newlyAddedAccounts) {
                Account account = prov.getAccountById(accountId);
                if (account.isFeatureGalEnabled() && account.isFeatureGalSyncEnabled()) {
                    prov.attachAccountToGal(domain, accountId);
                }
            }
            //do "detach" after "attach" in the hope that we could save one unnecessary gal recreate (say delete the last mailbox of this domain and then created a new one)
            Set<String> obsoleteAccounts = Sets.difference(attachedAccounts, domainAccounts);
            for (String account : obsoleteAccounts) {
                prov.detachAccountFromGal(domain, account);
            }
        }
        OfflineLog.offline.debug("[offline gal checking] finished successful.");
    }

    /**
     * fit existing gal account into directory.
     * @throws ServiceException
     */
    private void migrateGal() throws ServiceException {
        OfflineLog.offline.debug("[offline gal migrating] add exsiting offline gal account into domain gal directory");
        Map<Account, Pair<String, String>> galTokenMap = prov.getGalAccountTokens();
        Set<String> domains = new HashSet<String>();
        for (Account account : galTokenMap.keySet()) {
            String galAccountId = account.getAttr(OfflineConstants.A_offlineGalAccountId);
            String domain = ((OfflineAccount)account).getDomain();
            if (domains.add(domain)) {
                Map<String, Object> domainAttrs = new HashMap<String, Object>();
                domainAttrs.put(OfflineProvisioning.A_offlineUsingGalAccountId, account.getId());
                domainAttrs.put(OfflineConstants.A_offlineGalAccountId, galAccountId);
                prov.createDomainGal(domain, domainAttrs);
                //copy sync token and last refresh timestamp
                Account galAccount = prov.getAccount(galAccountId);
                String syncToken = galTokenMap.get(account).getFirst();
                prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalAccountSyncToken, syncToken);
                prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalAccountLastRefresh, galTokenMap.get(account).getSecond());
                prov.setAccountAttribute(galAccount, ZAttrProvisioning.A_mail, prov.getOfflineGalMailboxName(domain));
                //remove previous values
                Map<String, Object> attrs = new HashMap<String, Object>();
                attrs.put("-" + OfflineProvisioning.A_offlineGalAccountId, galAccountId);
                attrs.put("-" + OfflineProvisioning.A_offlineGalAccountSyncToken, syncToken);
                prov.modifyAttrs(account, attrs);
            } else {
                //delete the original gal mailbox/account as the domain already has gal now
                prov.deleteGalAccount(galAccountId);
                prov.attachAccountToGal(domain, account.getId());
            }
        }
        OfflineLog.offline.debug("[offline gal migrating] finished successful.");
    }

    /**
     * starting point for running a gal sync task for a domain
     */
    public void runTask(String domain, boolean isOnRequest) {
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
            sync(domain, isOnRequest);
        } catch (ServiceException e) {
            OfflineLog.offline.warn("Offline GAL sync task failed: " + e.getMessage());
        } finally {
            isRunning = false;
        }
        return;
    }

    private void sync(String domain, boolean isOnRequest) throws ServiceException {
        OfflineDomainGal domainGal = OfflineProvisioning.getOfflineInstance().getDomainGal(domain);
        OfflineAccount galAccount = ensureGalAccountExists(domainGal);
        OfflineSyncManager syncMan = OfflineSyncManager.getInstance();
        if (syncMan.getSyncStatus(galAccount) == SyncStatus.running) {
            return;
        }

        if (!isOnRequest) {
            if (domainGal.isRetryEnabled()) {
                OfflineLog.offline.info("Offline GAL sync retry for " + domainGal.getDomain());
                try {
                    GalSyncRetry.retry(galAccount, this.retryContactIds);
                } catch (IOException e) {
                    syncMan.processSyncException(galAccount, "", e, galAccount.isDebugTraceEnabled());
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
            OfflineLog.offline.info("Offline GAL sync started for " + domainGal.getDomain());
            syncGal(galAccount, isOnRequest);
            syncMan.syncComplete(galAccount);
            GalSyncCheckpointUtil.removeCheckpoint(MailboxManager.getInstance().getMailboxByAccount(galAccount));
        } catch (Exception e) {
            syncMan.processSyncException(galAccount, "", e, galAccount.isDebugTraceEnabled());
            if (e instanceof ServiceException) {
                OfflineLog.offline.info("gal sync req timeout is set to %d ms.", OfflineLC.zdesktop_gal_sync_request_timeout.intValue());
            }
            OfflineLog.offline.info("Offline GAL sync failed for " + domainGal.getDomain(), e);
        }
    }

    private OfflineAccount ensureGalAccountExists(OfflineDomainGal domainGal) throws ServiceException {
        String galAcctId = domainGal.getGalAccountId();
        OfflineAccount galAcct = null;
        if (StringUtil.isNullOrEmpty(galAcctId)) {
            OfflineLog.offline.warn("domain %s exists with no offlineGalAccountId yet", domainGal.getDomain());
            //check if there is existing but not referenced gal account
            List<Account> domainGalAccounts = prov.getAllGalAccounts(domainGal.getDomain());
            if (!domainGalAccounts.isEmpty()) {
                if (domainGalAccounts.size() > 1) {
                    OfflineLog.offline.warn("has %d gal accounts for domain %s, not referenced. Only need one.", domainGalAccounts.size(), domainGal.getDomain());
                }
                galAcct = (OfflineAccount) domainGalAccounts.get(0);
                prov.assignGalAccountToDomain(domainGal, galAcct);
                OfflineLog.offline.info("existing Offline GAL mailbox " + galAcct.getName() + " assigned to domain");
            } else {
                galAcct = prov.createGalAccount(domainGal);
                prov.assignGalAccountToDomain(domainGal, galAcct);
                OfflineLog.offline.info("Offline GAL mailbox created: " + galAcct.getName() + " and assigned");
            }
        } else {
            galAcct = (OfflineAccount) prov.get(AccountBy.id, galAcctId);
            String[] gals = domainGal.getMultiAttr(OfflineConstants.A_offlineGalAccountId);
            while (galAcct == null && gals != null && gals.length > 0) {
                OfflineLog.offline.warn("Offline Gal account is null in prov: %s, domain: %s", galAcctId, domainGal.getDomain());
                //bug 70395, we saw two entries of offlineGalAccountId for one GAL directory, the obsolete entry needs to be deleted
                OfflineLog.offline.debug("Removing obsolete Gal account Id for domain GAL...");
                Map<String, Object> attrs = new HashMap<String, Object>();
                attrs.put("-" + OfflineConstants.A_offlineGalAccountId, galAcctId);
                OfflineProvisioning.getInstance().modifyAttrs(domainGal, attrs);
                galAcctId = domainGal.getGalAccountId();
                galAcct = (OfflineAccount) prov.get(AccountBy.id, galAcctId);
                gals = domainGal.getMultiAttr(OfflineConstants.A_offlineGalAccountId);
            }
            if (galAcct == null) {
                try {
                    galAcct = prov.createGalAccount(domainGal);
                    prov.assignGalAccountToDomain(domainGal, galAcct);
                    OfflineLog.offline.info("Offline GAL mailbox created: " + galAcct.getName());
                } catch (Exception e) {
                    OfflineLog.offline.debug("Offline Gal mailbox create failed", e);
                }
            }
        }

        return galAcct;
    }

    private void syncGal(OfflineAccount galAccount, boolean isOnRequest) throws ServiceException, IOException {
        String syncToken = galAccount.getAttr(OfflineConstants.A_offlineGalAccountSyncToken, false);
        boolean fullSync = (syncToken == null || syncToken.trim().length() == 0);
        Mailbox galMbox = MailboxManager.getInstance().getMailboxByAccount(galAccount);
        GalSyncSAXHandler handler = new GalSyncSAXHandler(galAccount, galMbox, fullSync);
        ZcsMailbox mbox = null;
        String domain = galAccount.getDomain();
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

            mbox = GalSyncUtil.getGalEnabledZcsMailbox(domain);
            if (mbox == null) {
                OfflineLog.offline.debug("No gal enabled account for domain %s", domain);
                return;
            }
            mbox.sendRequest(req, true, true, OfflineLC.zdesktop_gal_sync_request_timeout.intValue(),
                    SoapProtocol.Soap12, handlers);

            Exception e = handler.getException();
            if (e != null) {
                if (e instanceof ServiceException)
                    throw (ServiceException) e;
                else
                    throw (IOException) e;
            } else if ((token = handler.getToken()) == null) {
                throw ServiceException.FAILURE("gal sync token is null", null);
            }
            GalSyncCheckpointUtil.persistItemIds(galMbox, handler.getItemIds());
        }

        handler.fetchContacts(domain, fullSync, retryContactIds);

        mbox = GalSyncUtil.getGalEnabledZcsMailbox(domain);
        if (mbox == null) {
            OfflineLog.offline.debug("No gal enabled account for domain %s, but full sync finished", domain);
        }
        if (StringUtil.isNullOrEmpty(token)) {
            OfflineLog.offline.warn("gal sync token is null");
            return;
        }
        if (fullSync) { // after a full sync, reset maintenance timer
            prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalAccountLastRefresh,
                Long.toString(System.currentTimeMillis()));
            //we've done a full sync, group populate is implied; as long as remote server is 7xx
            if (mbox != null && mbox.getRemoteServerVersion().isAtLeast7xx()) {
                prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalGroupMembersPopulated, ProvisioningConstants.TRUE);
            }
            if (galAccount.isGalSyncRetryOn()) {
                GalSyncRetry.checkpoint(galAccount, this.retryContactIds);
            }
        } else if (!galAccount.getBooleanAttr(OfflineConstants.A_offlineGalGroupMembersPopulated, false)
                && mbox != null && mbox.getRemoteServerVersion().isAtLeast7xx()) {
            // existing groups have incorrect type=account and don't have member list
            populateGroupMembers(mbox, galAccount);
            prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalGroupMembersPopulated, ProvisioningConstants.TRUE);
        }
        prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalAccountSyncToken, token);
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
     *
     * @param account GAL account
     *
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

    public boolean resetGal(final OfflineAccount galAccount) {
        final String accountName = galAccount.getName();
        final String domain = galAccount.getDomain();
        this.timer.schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    if (isRecentlyFullSynced(galAccount)) {
                        OfflineLog.offline.debug(
                                "reseting gal for account %s -- Skipped because GAL is recently synced", accountName);
                        return;
                    }
                    OfflineLog.offline.debug("reseting gal for domain %s, triggered by account %s -- 1. begin", domain,
                            accountName);

                    prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalAccountSyncToken, ""); // to trigger full gal sync
                    OfflineLog.offline.debug("reseting gal for domain %s -- 2. removed full sync token", domain);

                    GalSyncUtil.removeConfig(MailboxManager.getInstance().getMailboxByAccount(galAccount));
                    OfflineLog.offline.debug("reseting gal for domain %s -- 3. removed config in gal account", domain);

                    prov.deleteDomainGal(domain);
                    OfflineLog.offline.debug("reseting gal for domain %s -- 4. removed domain gal", domain);

                    Map<String, Object> domainAttrs = new HashMap<String, Object>();
                    OfflineDomainGal domainGal = OfflineProvisioning.getOfflineInstance().createDomainGal(domain, domainAttrs);
                    OfflineAccount galAcct = prov.createGalAccount(domainGal);
                    prov.assignGalAccountToDomain(domainGal, galAcct);
                    OfflineLog.offline.info("Offline GAL mailbox created: " + galAcct.getName());
                    checkGal();
                    OfflineLog.offline.debug("reseting gal for domain %s -- 5. created domain gal", domain);

                    OfflineLog.offline.debug("reseting gal for domain %s -- 6. about to resync", domain);
                    runTask(domain, true);
                    OfflineLog.offline.debug("reseting gal for domain %s -- 7. resync is done", domain);
                } catch (Exception e) {
                    OfflineLog.offline.debug("reseting gal for domain %s -- FAILED", domain, e);
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
            Element response = mbox.sendRequest(request, true, true,
                    OfflineLC.zdesktop_gal_sync_request_timeout.intValue(), SoapProtocol.Soap12);
            List<Element> groups = response.listElements(MailConstants.E_CONTACT);
            more = response.getAttributeBool(MailConstants.A_QUERY_MORE);
            DataSource ds = GalSyncUtil.createDataSourceForAccount(galAccount);
            Mailbox galMbox = MailboxManager.getInstance().getMailboxByAccountId(galAccount.getId(), false);
            OperationContext ctxt = new OperationContext(galMbox);
            if (groups != null) {
                for (Element group : groups) {
                    offset++;
                    // lookup each existing entry, make sure it's type=group and has correct members
                    String id = group.getAttribute(AccountConstants.A_ID);
                    int contactId = GalSyncUtil.findContact(id, ds);
                    if (contactId == -1) {
                        // group probably added after galsync completed. will be added on next sync.
                        OfflineLog.offline.debug("contact [" + id
                                + "] does not exist in local gal; assuming it will be synced on next pass");
                        continue;
                    }
                    Contact contact = galMbox.getContactById(ctxt, contactId);
                    Map<String, String> newFields = new HashMap<String, String>();
                    newFields.put(ContactConstants.A_type, OfflineGal.CTYPE_GROUP);
                    ParsedContact pc = new ParsedContact(contact);
                    pc.modify(newFields, null);
                    galMbox.modifyContact(ctxt, contactId, pc);
                }
            }
        }
    }
}
