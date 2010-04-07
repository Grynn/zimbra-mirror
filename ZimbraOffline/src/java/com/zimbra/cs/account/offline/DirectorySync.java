/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.account.offline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.Element.XMLElement;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AttributeManager;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.account.Identity;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Signature;
import com.zimbra.cs.account.Provisioning.IdentityBy;
import com.zimbra.cs.account.Provisioning.SignatureBy;
import com.zimbra.cs.db.DbMailbox;
import com.zimbra.cs.filter.RuleManager;
import com.zimbra.cs.filter.RuleRewriter;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.service.account.ModifyPrefs;
import com.zimbra.cs.util.Zimbra;
import com.zimbra.cs.util.ZimbraApplication;
import com.zimbra.cs.zclient.ZFilterRules;
import com.zimbra.cs.zclient.ZGetInfoResult;
import com.zimbra.cs.zclient.ZIdentity;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZSignature;

public class DirectorySync {

    private static DirectorySync instance = new DirectorySync();
    private TimerTask currentTask;

    public static DirectorySync getInstance() {
        return instance;
    }

    private Timer timer = new Timer("sync-timer-dir");
    private DirectorySync() {
        timer.schedule(currentTask = new TimerTask() {
            @Override public void run() {
                if (ZimbraApplication.getInstance().isShutdown()) {
                    currentTask.cancel();
                    return;
                }
                try {
                    syncAllAccounts(false);
                } catch (Throwable e) { //don't let exceptions kill the timer
                    if (e instanceof OutOfMemoryError)
                        Zimbra.halt("caught out of memory error", e);
                    else if (OfflineSyncManager.getInstance().isServiceActive())
                        OfflineLog.offline.warn("caught exception in timer ", e);
                }
            }
        },
        5 * Constants.MILLIS_PER_SECOND,
        OfflineLC.zdesktop_sync_timer_frequency.longValue());
    }

    private long mMinSyncInterval = OfflineLC.zdesktop_dirsync_min_delay.longValue();
    private long mFailSyncInterval = OfflineLC.zdesktop_dirsync_fail_delay.longValue();
    private long mAccountPollInterval = OfflineLC.zdesktop_account_poll_interval.longValue();

    private long lastExecutionTime = 0;
    private Map<String, Long> mLastSyncTimes = new HashMap<String, Long>();
    private Map<String, Long> mLastFailTimes = new HashMap<String, Long>();

    private boolean mSyncRunning;

    private boolean lockAccountsToSync() {
        if (!mSyncRunning) {
            synchronized (this) {
                if (!mSyncRunning) {
                    mSyncRunning = true;
                    return true;
                }
            }
        }
        return false;
    }

    private void unlockAccounts() {
        assert mSyncRunning == true;
        mSyncRunning = false;
    }

    void syncAllAccounts(boolean isOnRequest) {
        long now = System.currentTimeMillis();
        if (!isOnRequest && now - lastExecutionTime < mMinSyncInterval)
            return;

        if (lockAccountsToSync()) {
            try {
                OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
                // first, be sure to push the locally-changed accounts
                if (prov.hasDirtyAccounts()) {
                    for (Account acct : prov.listDirtyAccounts()) {
                        long lastFail = mLastFailTimes.get(acct.getId()) == null ? 0 : mLastFailTimes.get(acct.getId());
                        if (now - lastFail > mFailSyncInterval) { //we slow donw dir sync if a failure ever happened
                            if (sync(acct, isOnRequest)) {
                                mLastSyncTimes.put(acct.getId(), now);
                                mLastFailTimes.remove(acct.getId());
                            } else
                                mLastFailTimes.put(acct.getId(), now);
                        }
                    }
                }

                // then, sync the accounts we haven't synced in a while
                // XXX: we should have a cache and iterate over it -- accounts shouldn't change out from under us
                for (Account acct : prov.getAllZcsAccounts()) {
                    long lastSync = mLastSyncTimes.get(acct.getId()) == null ? 0 : mLastSyncTimes.get(acct.getId());
                    long lastFail = mLastFailTimes.get(acct.getId()) == null ? 0 : mLastFailTimes.get(acct.getId());
                    if (now - lastFail > mFailSyncInterval && now - lastSync > mAccountPollInterval) {
                        if (sync(acct, isOnRequest)) {
                            mLastSyncTimes.put(acct.getId(), now);
                            mLastFailTimes.remove(acct.getId());
                        } else
                            mLastFailTimes.put(acct.getId(), now);
                    }
                }

                lastExecutionTime = now;
            } catch (ServiceException e) {
                if (!e.getCode().equals(ServiceException.INTERRUPTED))
                    OfflineLog.offline.warn("error listing accounts to sync", e);
            } catch (Exception t) {
                OfflineLog.offline.error("Unexpected exception syncing directory", t);
            } finally {
                unlockAccounts();
            }
        }
    }

    private boolean sync(Account acct, boolean isOnRequest) throws ServiceException {
        OfflineSyncManager.getInstance().continueOK();
        if (!isOnRequest && !OfflineSyncManager.getInstance().reauthOK(acct)) //don't reauth if just failed not too long ago
            return false;

        OfflineProvisioning prov = (OfflineProvisioning) Provisioning.getInstance();

        // figure out where we need to connect to
        String email = acct.getAttr(Provisioning.A_mail);
        String password = acct.getAttr(OfflineProvisioning.A_offlineRemotePassword);
        String baseUri = acct.getAttr(OfflineProvisioning.A_offlineRemoteServerUri);

        if (email == null || password == null || baseUri == null) {
            OfflineLog.offline.warn("one of email/password/uri not set for account: " + acct.getName());
            return false;
        }

        try {
            // fetch the account data from the remote host
            ZMailbox zmbx = prov.newZMailbox((OfflineAccount)acct, AccountConstants.USER_SERVICE_URI);
            syncAccount(prov, acct, zmbx);
            pushAccount(prov, acct, zmbx);
            if (((OfflineAccount)acct).getRemoteServerVersion().isAtLeast6xx()) {
                syncFilterRules(prov, acct, zmbx);
                syncWhiteBlackList(prov, acct, zmbx);
            }
            else {
                syncRules5xx(prov, acct, zmbx);
            }

            // FIXME: there's a race condition here, as <tt>acct</tt> may have been modified during the push
            prov.markAccountClean(acct);
            return true;
        } catch (Exception e) {
            OfflineSyncManager.getInstance().processSyncException(acct, e, false);
            return false;
        }
    }

    private void syncWhiteBlackList(OfflineProvisioning prov, Account acct, ZMailbox zmbx) throws ServiceException {
        Set<String> modified = acct.getMultiAttrSet(OfflineProvisioning.A_offlineModifiedAttrs);
        if (modified.contains(Provisioning.A_amavisWhitelistSender)
                || modified.contains(Provisioning.A_amavisBlacklistSender)) {
            Element req = zmbx.newRequestElement(AccountConstants.MODIFY_WHITE_BLACK_LIST_REQUEST);
            if (modified.contains(Provisioning.A_amavisWhitelistSender)) {
                String[] addrs = acct.getMultiAttr(Provisioning.A_amavisWhitelistSender);
                addAddrs(req, AccountConstants.E_WHITE_LIST, addrs);
            }
            if (modified.contains(Provisioning.A_amavisBlacklistSender)) {
                String[] addrs = acct.getMultiAttr(Provisioning.A_amavisBlacklistSender);
                addAddrs(req, AccountConstants.E_BLACK_LIST, addrs);
            }
            zmbx.invoke(req);
            OfflineLog.offline.debug("dsync: pushed white/blacklisted senders: %s", acct.getName());
        } else {
            Element req = zmbx.newRequestElement(AccountConstants.GET_WHITE_BLACK_LIST_REQUEST);
            Element resp = zmbx.invoke(req);
            HashMap<String, Object> attrs = new HashMap<String, Object>();
            readAddrs(resp.getOptionalElement(AccountConstants.E_WHITE_LIST),
                      Provisioning.A_amavisWhitelistSender,
                      attrs);
            readAddrs(resp.getOptionalElement(AccountConstants.E_BLACK_LIST),
                      Provisioning.A_amavisBlacklistSender,
                      attrs);
            acct.modify(attrs);
            OfflineLog.offline.debug("dsync: pulled white/blacklisted senders: %s", acct.getName());
        }
    }

    private static void addAddrs(Element req, String list, String[] addrs) {
        Element eList = req.addElement(list);

        for (String addr : addrs)
            eList.addElement(AccountConstants.E_ADDR).setText(addr);
    }

    private static void readAddrs(Element eList, String attrName, HashMap<String, Object> attrs) {
        if (eList == null)
            return;

        // empty list, means delete all
        if (eList.getOptionalElement(AccountConstants.E_ADDR) == null) {
            StringUtil.addToMultiMap(attrs, attrName, "");
            return;
        }

        for (Element eAddr : eList.listElements(AccountConstants.E_ADDR)) {
            String value = eAddr.getText();
            StringUtil.addToMultiMap(attrs, attrName, value);
        }
    }

    private void syncFilterRules(OfflineProvisioning prov, Account acct, ZMailbox zmbx) throws ServiceException {
        Set<String> modified = acct.getMultiAttrSet(OfflineProvisioning.A_offlineModifiedAttrs);
        if (modified.contains(Provisioning.A_zimbraMailSieveScript)) {
            Element xmlRules = RuleManager.getRulesAsXML(XMLElement.mFactory, acct, true);
            ZFilterRules rules = new ZFilterRules(xmlRules);
            zmbx.saveFilterRules(rules);
            OfflineLog.offline.debug("dsync: pushed %d filter rules: %s", rules.getRules().size(), acct.getName());
        } else {
            ZFilterRules rules = zmbx.getFilterRules(true);
            Element e = new XMLElement(MailConstants.SAVE_RULES_REQUEST); //dummy element
            rules.toElement(e);
            try {
                RuleManager.setXMLRules(acct, e.getElement(MailConstants.E_FILTER_RULES), true);
                OfflineLog.offline.debug("dsync: pulled %d filter rules: %s", rules.getRules().size(), acct.getName());
            } catch (ServiceException x) {
                //bug 37422
                OfflineLog.offline.warn("dsync: pulled %d filter rules:\n%s", rules.getRules().size(), e.prettyPrint(), x);
            }
        }
    }
    
    private void syncRules5xx(OfflineProvisioning prov, Account acct, ZMailbox zmbx) throws ServiceException {
        Set<String> modified = acct.getMultiAttrSet(OfflineProvisioning.A_offlineModifiedAttrs);
        if (modified.contains(Provisioning.A_zimbraMailSieveScript)) {
            Element xmlRules = RuleManager.getRulesAsXML(XMLElement.mFactory, acct, false);
            RuleRewriter.sanitizeRules(xmlRules);
            saveRulesTo5xxServer(zmbx, xmlRules);
            OfflineLog.offline.debug("dsync: pushed %d filter rules: %s", xmlRules.listElements().size(), acct.getName());
        } else {
            Element xmlRules = getRulesFrom5xxServer(zmbx);
            RuleRewriter.sanitizeRules(xmlRules);
            try {
                RuleManager.setXMLRules(acct, xmlRules, false);
                OfflineLog.offline.debug("dsync: pulled %d filter rules: %s", xmlRules.listElements().size(), acct.getName());
            } catch (ServiceException x) {
                //bug 37422
                OfflineLog.offline.warn("dsync: pulled %d filter rules:\n%s", xmlRules.listElements().size(), xmlRules.prettyPrint(), x);
            }
        }
    }
    
    private void saveRulesTo5xxServer(ZMailbox zmbx, Element xmlRules) throws ServiceException {
        Element req = zmbx.newRequestElement(MailConstants.SAVE_RULES_REQUEST);
        req.addElement(xmlRules);
        zmbx.invoke(req);
    }
    
    private Element getRulesFrom5xxServer(ZMailbox zmbx) throws ServiceException {
        Element req = zmbx.newRequestElement(MailConstants.GET_RULES_REQUEST);
        Element resp = zmbx.invoke(req);
        return resp.getElement(MailConstants.E_RULES);
    }
    
    private void syncAccount(OfflineProvisioning prov, Account acct, ZMailbox zmbx) throws ServiceException {
        ZGetInfoResult zgi = zmbx.getAccountInfo(false);
        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccount(acct);

        synchronized (DbMailbox.getZimbraSynchronizer(mbox)) {
            synchronized (prov) {
                syncAccount(prov, acct, zgi);
            }
        }
    }

    private void syncAccount(OfflineProvisioning prov, Account acct, ZGetInfoResult zgi) throws ServiceException {
        // make sure we're current
        prov.reload(acct);

        // update the state of the account
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.putAll(zgi.getAttrs());
        attrs.putAll(zgi.getPrefAttrs());

        attrs = diffAttributes(acct, attrs);

        attrs.put(Provisioning.A_zimbraMailQuota, "0"); //legacy account correction in case an old account sync down server quota
        attrs.put(OfflineProvisioning.A_offlineRemoteServerVersion, zgi.getVersion()); //make sure always update if different
        OfflineLog.offline.info("dsync: remote server version: " + zgi.getVersion());

        prov.modifyAttrs(acct, attrs, false, true, false);
        ((OfflineAccount)acct).resetRemoteServerVersion();
        OfflineLog.offline.debug("dsync: synchronized account: " + acct.getName());

        // sync identities from server
        Set<String> identityIds = new HashSet<String>();
        for (ZIdentity zident : zgi.getIdentities()) {
            // create/update identity entries in local database
            syncIdentity(prov, acct, zident);
            identityIds.add(zident.getId());
        }
        for (Identity ident : prov.getAllIdentities(acct)) {
            // delete any non-locally-created identity not in the list
            if (!identityIds.contains(ident.getId()) && !isLocallyCreated(ident)) {
                prov.deleteIdentity(acct, ident.getName(), false);
                OfflineLog.offline.debug("dsync: deleted identity: " + acct.getName() + '/' + ident.getName());
            }
        }

        // sync data sources from server
//        Set<String> dataSourceIds = new HashSet<String>();
//        for (ZDataSource zdsrc : zgi.getDataSources()) {
//            // create/update data source entries in local database
//            syncDataSource(prov, acct, zdsrc);
//            dataSourceIds.add(zdsrc.getId());
//        }
//        for (DataSource dsrc : prov.getAllDataSources(acct)) {
//            // delete any non-locally-created data source not in the list
//            if (!dataSourceIds.contains(dsrc.getId()) && !isLocallyCreated(dsrc)) {
//                prov.deleteDataSource(acct, dsrc.getId(), false);
//                OfflineLog.offline.debug("dsync: deleted data source: " + acct.getName() + '/' + dsrc.getName());
//            }
//        }

        // sync signature server
        Set<String> signatureIds = new HashSet<String>();
        for (ZSignature zsig : zgi.getSignatures()) {
            // create/update data source entries in local database
            syncSignature(prov, acct, zsig);
            signatureIds.add(zsig.getId());
        }
        for (Signature signature : prov.getAllSignatures(acct)) {
            // delete any non-locally-created signature not in the list
            if (!signatureIds.contains(signature.getId()) && !isLocallyCreated(signature)) {
                prov.deleteSignature(acct, signature.getId(), false);
                OfflineLog.offline.debug("dsync: deleted signature: " + acct.getName() + '/' + signature.getName());
            }
        }
    }

    private static boolean isLocallyCreated(Entry e) {
        return e.getMultiAttrSet(OfflineProvisioning.A_offlineModifiedAttrs).contains(OfflineProvisioning.A_offlineDn);
    }
    
    private static Map<String, Object> scrubAttributes(Map<String, Object> attrs) {
        for (Iterator<java.util.Map.Entry<String, Object>> i = attrs.entrySet().iterator(); i.hasNext();) {
            String name = i.next().getKey();
            if (OfflineProvisioning.sOfflineAttributes.contains(name))
                i.remove();
        }
        return attrs;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> diffAttributes(Entry e, Map<String, Object> attrs) {
        // write over all unchanged account attributes
        Set<String> modified = e.getMultiAttrSet(OfflineProvisioning.A_offlineModifiedAttrs);
        Map<String, Object> changes = new HashMap<String, Object>();
        for (Map.Entry<String, Object> zattr : attrs.entrySet()) {
            String key = zattr.getKey();
            if (modified.contains(key) || key.equals(Provisioning.A_zimbraMailHost) || key.equals(Provisioning.A_zimbraMailSieveScript) ||
                    OfflineProvisioning.sOfflineAttributes.contains(key))
                continue;
            Object value = zattr.getValue();
            if (value instanceof List) {
                if (((List) value).size() == 1)
                    value = ((List) value).get(0);
                else if (((List) value).isEmpty())
                    value = null;
            } else if (value instanceof String[]) {
                if (((String[]) value).length == 1)
                    value = ((String[]) value)[0];
                else if (((String[]) value).length == 0)
                    value = null;
            }
            changes.put(zattr.getKey(), value);
        }

        // make sure to detect any deleted attributes
        Set<String> existing = new HashSet<String>(e.getAttrs().keySet());
        existing.removeAll(modified);
        existing.removeAll(changes.keySet());
        for (String key : existing)
            if (!key.startsWith("offline") && !OfflineProvisioning.sOfflineAttributes.contains(key) && !key.equals(Provisioning.A_zimbraMailSieveScript))
                changes.put(key, null);

        return changes;
    }

    void syncIdentity(OfflineProvisioning prov, Account acct, ZIdentity zident) throws ServiceException {
        if (zident.isDefault())
            return;

        String identityId = zident.getId();
        String name = zident.getName();

        Map<String, Object> attrs = zident.getAttrs();

        Identity ident = prov.get(acct, IdentityBy.id, identityId);
        Identity conflict = prov.get(acct, IdentityBy.name, name);

        if (conflict != null && (ident == null || !conflict.getId().equals(ident.getId()))) {
            // handle any naming conflicts by renaming the *local* identity
            // XXX: if the identity has been renamed locally, no need to rename the conflict
            Map<String, Object> resolution = new HashMap<String, Object>(1);
            resolution.put(Provisioning.A_zimbraPrefIdentityName, name + '{' + UUID.randomUUID().toString() + '}');
            prov.modifyIdentity(acct, name, resolution);
            OfflineLog.offline.debug("dsync: detected conflict and renamed identity: " + acct.getName() + '/' + conflict.getName());
        }

        if (ident != null && isLocallyCreated(ident)) {
            // identity is marked as locally created, but it already exists on the server
            Map<String, Object> resolution = new HashMap<String, Object>(1);
            resolution.put('-' + OfflineProvisioning.A_offlineModifiedAttrs, OfflineProvisioning.A_offlineDn);
            prov.modifyIdentity(acct, ident.getName(), resolution, false);
            OfflineLog.offline.debug("dsync: marked identity as non-locally created: " + acct.getName() + '/' + ident.getName());
        }

        if (ident == null) {
            // if we're here and haven't locally deleted the identity, it's a new one and needs to be created
            if (!acct.getMultiAttrSet(OfflineProvisioning.A_offlineDeletedIdentity).contains(identityId)) {
                ident = prov.createIdentity(acct, name, scrubAttributes(attrs), false);
                OfflineLog.offline.debug("dsync: created identity: " + acct.getName() + '/' + ident.getName());
            }
        } else {
            prov.modifyIdentity(acct, ident.getName(), diffAttributes(ident, zident.getAttrs()), false);
            prov.reload(ident);
            OfflineLog.offline.debug("dsync: updated identity: " + acct.getName() + '/' + ident.getName());
        }
    }

    void syncSignature(OfflineProvisioning prov, Account acct, ZSignature zsig) throws ServiceException {
        String signatureId = zsig.getId();
        String name = zsig.getName();
        Map<String, Object> attrs = zsig.getAttrs();

        Signature signature = prov.get(acct, SignatureBy.id, signatureId);
        Signature conflict = prov.get(acct, SignatureBy.name, name);

        if (conflict != null && (signature == null || !conflict.getId().equals(signature.getId()))) {
            // handle any naming conflicts by renaming the *local* signature
            // XXX: if the signature has been renamed locally, no need to rename the conflict
            Map<String, Object> resolution = new HashMap<String, Object>(1);
            resolution.put(Provisioning.A_zimbraSignatureName, name + '{' + UUID.randomUUID().toString() + '}');
            prov.modifySignature(acct, signature.getId(), resolution);
            OfflineLog.offline.debug("dsync: detected conflict and renamed signature: " + acct.getName() + '/' + conflict.getName());
        }

        if (signature != null && isLocallyCreated(signature)) {
            // signature is marked as locally created, but it already exists on the server
            Map<String, Object> resolution = new HashMap<String, Object>(1);
            resolution.put('-' + OfflineProvisioning.A_offlineModifiedAttrs, OfflineProvisioning.A_offlineDn);
            prov.modifySignature(acct, signature.getId(), resolution, false);
            OfflineLog.offline.debug("dsync: marked signature as non-locally created: " + acct.getName() + '/' + signature.getName());
        }

        if (signature == null) {
            if (!acct.getMultiAttrSet(OfflineProvisioning.A_offlineDeletedSignature).contains(signatureId)) {
                // if we're here and haven't locally deleted the signature, it's a new one and needs to be created
                signature = prov.createSignature(acct, name, scrubAttributes(attrs), false);
                OfflineLog.offline.debug("dsync: created signature: " + acct.getName() + '/' + signature.getName());
            }
        } else {
            prov.modifySignature(acct, signature.getId(), diffAttributes(signature, zsig.getAttrs()), false);
            prov.reload(signature);
            OfflineLog.offline.debug("dsync: updated signature: " + acct.getName() + '/' + signature.getName());
        }
    }

//    void syncDataSource(OfflineProvisioning prov, Account acct, ZDataSource zdsrc) throws ServiceException {
//        String dsid = zdsrc.getId();
//        String name = zdsrc.getName();
//
//        Map<String, Object> attrs = zdsrc.getAttrs();
//
//        DataSource dsrc = prov.get(acct, DataSourceBy.id, dsid);
//        DataSource conflict = prov.get(acct, DataSourceBy.name, name);
//
//        if (conflict != null && (dsrc == null || !conflict.getId().equals(dsrc.getId()))) {
//            // handle any naming conflicts by renaming the *local* data source
//            // XXX: if the data source has been renamed locally, no need to rename the conflict
//            Map<String, Object> resolution = new HashMap<String, Object>(1);
//            resolution.put(Provisioning.A_zimbraDataSourceName, name + '{' + UUID.randomUUID().toString() + '}');
//            prov.modifyDataSource(acct, conflict.getId(), resolution);
//            OfflineLog.offline.debug("dsync: detected conflict and renamed data source: " + acct.getName() + '/' + conflict.getName());
//        }
//
//        if (dsrc != null && isLocallyCreated(dsrc)) {
//            // data source is marked as locally created, but it already exists on the server
//            Map<String, Object> resolution = new HashMap<String, Object>(1);
//            resolution.put('-' + OfflineProvisioning.A_offlineModifiedAttrs, OfflineProvisioning.A_offlineDn);
//            prov.modifyDataSource(acct, dsrc.getId(), resolution, false);
//            OfflineLog.offline.debug("dsync: marked data source as non-locally created: " + acct.getName() + '/' + dsrc.getName());
//        }
//
//        if (dsrc == null) {
//            // if we're here and haven't locally deleted the data source, it's a new one and needs to be created
//            if (!acct.getMultiAttrSet(OfflineProvisioning.A_offlineDeletedDataSource).contains(dsid)) {
//                dsrc = prov.createDataSource(acct, zdsrc.getType(), name, attrs, false, false);
//                OfflineLog.offline.debug("dsync: created data source: " + acct.getName() + '/' + dsrc.getName());
//            }
//        } else {
//            prov.modifyDataSource(acct, dsrc.getId(), diffAttributes(dsrc, zdsrc.getAttrs()), false);
//            prov.reload(dsrc);
//            OfflineLog.offline.debug("dsync: updated data source: " + acct.getName() + '/' + dsrc.getName());
//        }
//    }


    private void pushAccount(OfflineProvisioning prov, Account acct, ZMailbox zmbx) throws ServiceException {
        Set<String> modified = acct.getMultiAttrSet(OfflineProvisioning.A_offlineModifiedAttrs);
        if (!modified.isEmpty()) {
            Map<String, Object> attrs = acct.getAttrs();
            Map<String, Object> changes = new HashMap<String, Object>(modified.size());
            for (String pref : modified) {
                // we're only authorized to push changes to user preferences
                if (pref.startsWith(ModifyPrefs.PREF_PREFIX) && !OfflineProvisioning.sOfflineAttributes.contains(pref)
                        && AttributeManager.getInstance().inVersion(pref, ((OfflineAccount)acct).getRemoteServerVersion().toString())) {
                    Object val = attrs.get(pref);
                    if (val == null) {
                        OfflineLog.offline.debug("dpush: attr name=%s has null value", pref);
                        val = "";
                    }
                    changes.put(pref, val);
                } else if (!pref.startsWith("offline") && !OfflineProvisioning.sOfflineAttributes.contains(pref) && !pref.equals(Provisioning.A_zimbraMailSieveScript))
                    OfflineLog.offline.warn("dpush: could not push non-preference attribute: " + pref);
            }
            if (!changes.isEmpty()) {
                zmbx.modifyPrefs(changes);
                OfflineLog.offline.debug("dpush: modified account: " + acct.getName());
            }
        }

        for (Identity ident : prov.getAllIdentities(acct))
            if (!ident.getId().equals(acct.getId()))
                pushIdentity(prov, acct, ident, zmbx);
        for (String identityId : acct.getMultiAttrSet(OfflineProvisioning.A_offlineDeletedIdentity)) {
            zmbx.deleteIdentity(IdentityBy.id, identityId);
            OfflineLog.offline.debug("dpush: deleted identity: " + acct.getName() + '/' + identityId);
        }

//        for (DataSource dsrc : prov.getAllDataSources(acct))
//            pushDataSource(prov, acct, dsrc, zmbx);
//        for (String dsid : acct.getMultiAttrSet(OfflineProvisioning.A_offlineDeletedDataSource)) {
//            zmbx.deleteDataSource(DataSourceBy.id, dsid);
//            OfflineLog.offline.debug("dpush: deleted data source: " + acct.getName() + '/' + dsid);
//        }

        for (Signature signature : prov.getAllSignatures(acct))
            pushSignature(prov, acct, signature, zmbx);
        for (String signatureId : acct.getMultiAttrSet(OfflineProvisioning.A_offlineDeletedSignature)) {
            zmbx.deleteSignature(signatureId);
            OfflineLog.offline.debug("dpush: deleted signature: " + acct.getName() + '/' + signatureId);
        }
    }

    private void pushIdentity(OfflineProvisioning prov, Account acct, Identity ident, ZMailbox zmbx) throws ServiceException {
        // check to see if this identity has been modified since the last sync
        Set<String> modified = ident.getMultiAttrSet(OfflineProvisioning.A_offlineModifiedAttrs);
        if (modified == null || modified.isEmpty())
            return;

        Map<String, Object> attrs = ident.getAttrs();
        attrs.remove(OfflineProvisioning.A_offlineModifiedAttrs);
        scrubAttributes(attrs);
        ZIdentity zident = new ZIdentity(ident.getName(), attrs);

        // create or modify the identity, as requested
        if (isLocallyCreated(ident)) {
            zmbx.createIdentity(zident);
            OfflineLog.offline.debug("dpush: created identity: " + acct.getName() + '/' + ident.getName());
        } else {
            zmbx.modifyIdentity(zident);
            OfflineLog.offline.debug("dpush: modified identity: " + acct.getName() + '/' + ident.getName());
        }

        // clear the set of modified attributes, since we're now in sync
        Map<String, Object> postModify = new HashMap<String, Object>(1);
        postModify.put(OfflineProvisioning.A_offlineModifiedAttrs, null);
        prov.modifyIdentity(acct, ident.getName(), postModify, false);
    }

//    private void pushDataSource(OfflineProvisioning prov, Account acct, DataSource dsrc, ZMailbox zmbx) throws ServiceException {
//        // check to see if this identity has been modified since the last sync
//        Set<String> modified = dsrc.getMultiAttrSet(OfflineProvisioning.A_offlineModifiedAttrs);
//        if (modified == null || modified.isEmpty())
//            return;
//
//        Map<String, Object> attrs = dsrc.getAttrs();
//        attrs.remove(OfflineProvisioning.A_offlineModifiedAttrs);
//        if (dsrc.getType() != DataSource.Type.pop3)
//            throw OfflineServiceException.UNSUPPORTED("cannot push changes to " + dsrc.getType() + " data source: " + acct.getName() + '/' + dsrc.getName());
//        ZDataSource zdsrc = new ZPop3DataSource(dsrc);
//
//        // create or modify the identity, as requested
//        if (isLocallyCreated(dsrc)) {
//            zmbx.createDataSource(zdsrc);
//            OfflineLog.offline.debug("dpush: created data source: " + acct.getName() + '/' + dsrc.getName());
//        } else {
//            zmbx.modifyDataSource(zdsrc);
//            OfflineLog.offline.debug("dpush: modified data source: " + acct.getName() + '/' + dsrc.getName());
//        }
//
//        // clear the set of modified attributes, since we're now in sync
//        Map<String, Object> postModify = new HashMap<String, Object>(1);
//        postModify.put(OfflineProvisioning.A_offlineModifiedAttrs, null);
//        prov.modifyDataSource(acct, dsrc.getName(), postModify, false);
//    }

    private void pushSignature(OfflineProvisioning prov, Account acct, Signature signature, ZMailbox zmbx) throws ServiceException {
        // check to see if this signature has been modified since the last sync
        Set<String> modified = signature.getMultiAttrSet(OfflineProvisioning.A_offlineModifiedAttrs);
        if (modified == null || modified.isEmpty())
            return;

        Map<String, Object> attrs = signature.getAttrs();
        attrs.remove(OfflineProvisioning.A_offlineModifiedAttrs);
        scrubAttributes(attrs);
        String sigType = signature.getAttr(Provisioning.A_zimbraPrefMailSignatureHTML, null) == null ? "text/plain" : "text/html";
        ZSignature zsig = new ZSignature(signature.getId(), signature.getName(), signature.getAttr(Signature.mimeTypeToAttrName(sigType)), sigType);

        // create or modify the signature, as requested
        if (isLocallyCreated(signature)) {
            zmbx.createSignature(zsig);
            OfflineLog.offline.debug("dpush: created signature: " + acct.getName() + '/' + signature.getName());
        } else {
            zmbx.modifySignature(zsig);
            OfflineLog.offline.debug("dpush: modified signature: " + acct.getName() + '/' + signature.getName());
        }

        // clear the set of modified attributes, since we're now in sync
        Map<String, Object> postModify = new HashMap<String, Object>(1);
        postModify.put(OfflineProvisioning.A_offlineModifiedAttrs, null);
        prov.modifySignature(acct, signature.getId(), postModify, false);
    }
}
