/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * The Original Code is: Zimbra Network
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.account.offline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.account.Identity;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.Provisioning.DataSourceBy;
import com.zimbra.cs.account.Provisioning.IdentityBy;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.offline.Offline;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.service.account.ModifyPrefs;
import com.zimbra.cs.servlet.ZimbraServlet;
import com.zimbra.cs.zclient.ZDataSource;
import com.zimbra.cs.zclient.ZGetInfoResult;
import com.zimbra.cs.zclient.ZIdentity;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZPop3DataSource;

public class DirectorySync {

    public static boolean sync(String zimbraId) throws ServiceException {
        // get the local Account for the user
        Account acct = Provisioning.getInstance().get(AccountBy.id, zimbraId);
        if (acct == null) {
            OfflineLog.offline.warn("could not fetch account for zimbraId " + zimbraId);
            return false;
        }

        return sync(acct);
    }

    public static boolean sync(Account acct) {
        OfflineProvisioning prov = (OfflineProvisioning) Provisioning.getInstance();

        // figure out where we need to connect to
        String email = acct.getAttr(Provisioning.A_mail);
        String password = acct.getAttr(OfflineProvisioning.A_offlineRemotePassword);
        String uri = acct.getAttr(OfflineProvisioning.A_offlineRemoteServerUri);
        if (email == null || password == null || uri == null) {
            OfflineLog.offline.warn("one of email/password/uri not set for account: " + acct.getName());
            return false;
        }

        try {
            // fetch the account data from the remote host
            ZMailbox.Options options = new ZMailbox.Options(email, AccountBy.name, password, uri + ZimbraServlet.USER_SERVICE_URI);
            options.setNoSession(true);
            options.setRetryCount(1);
            options.setDebugListener(new Offline.OfflineDebugListener());
            ZMailbox zmbx = ZMailbox.getMailbox(options);
    
            syncAccount(prov, acct, zmbx);
            pushAccount(prov, acct, zmbx);

            return true;
        } catch (ServiceException e) {
            if (e.getCode().equals(ServiceException.PROXY_ERROR)) {
                Throwable cause = e.getCause();
                if (cause instanceof java.net.NoRouteToHostException)
                    OfflineLog.offline.debug("java.net.NoRouteToHostException: offline and unreachable account " + acct.getId(), e);
                else if (cause instanceof org.apache.commons.httpclient.ConnectTimeoutException)
                    OfflineLog.offline.debug("org.apache.commons.httpclient.ConnectTimeoutException: no connect after " + OfflineMailbox.SERVER_REQUEST_TIMEOUT_SECS + " seconds for account " + acct.getId(), e);
                else if (cause instanceof java.net.SocketTimeoutException)
                    OfflineLog.offline.info("java.net.SocketTimeoutException: read timed out after " + OfflineMailbox.SERVER_REQUEST_TIMEOUT_SECS + " seconds for account " + acct.getId(), e);
                else
                    OfflineLog.offline.warn("error communicating with account " + acct.getId(), e);
            } else {
                OfflineLog.offline.warn("failed to sync account " + acct.getId(), e);
            }
            return false;
        }
    }


    static void syncAccount(OfflineProvisioning prov, Account acct, ZMailbox zmbx) throws ServiceException {
        ZGetInfoResult zgi = zmbx.getAccountInfo(false);

        synchronized (prov) {
            // make sure we're current
            prov.reload(acct);

            // update the state of the account
            Map<String, Object> attrs = new HashMap<String, Object>();
            attrs.putAll(zgi.getAttrs());
            attrs.putAll(zgi.getPrefAttrs());
            prov.modifyAttrs(acct, diffAttributes(acct, attrs), false, true, false);
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
            Set<String> dataSourceIds = new HashSet<String>();
            for (ZDataSource zdsrc : zgi.getDataSources()) {
                // create/update data source entries in local database
                syncDataSource(prov, acct, zdsrc);
                dataSourceIds.add(zdsrc.getId());
            }
            for (DataSource dsrc : prov.getAllDataSources(acct)) {
                // delete any non-locally-created data source not in the list
                if (!dataSourceIds.contains(dsrc.getId()) && !isLocallyCreated(dsrc)) {
                    prov.deleteDataSource(acct, dsrc.getId(), false);
                    OfflineLog.offline.debug("dsync: deleted data source: " + acct.getName() + '/' + dsrc.getName());
                }
            }
        }
    }

    private static boolean isLocallyCreated(Entry e) {
        return e.getMultiAttrSet(OfflineProvisioning.A_offlineModifiedAttrs).contains(OfflineProvisioning.A_offlineDn);
    }

    private static Map<String, Object> diffAttributes(Entry e, Map<String, Object> attrs) {
        // write over all unchanged account attributes
        Set<String> modified = e.getMultiAttrSet(OfflineProvisioning.A_offlineModifiedAttrs);
        Map<String, Object> changes = new HashMap<String, Object>();
        for (Map.Entry<String, Object> zattr : attrs.entrySet()) {
            String key = zattr.getKey();
            if (modified.contains(key) || key.equals(Provisioning.A_zimbraMailHost))
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
            if (!key.startsWith("offline") && !OfflineProvisioning.sOfflineAttributes.contains(key))
                changes.put(key, null);

        return changes;
    }

    static void syncIdentity(OfflineProvisioning prov, Account acct, ZIdentity zident) throws ServiceException {
        String identityId = zident.getId();
        String name = zident.getName();
        if (name.equalsIgnoreCase(Provisioning.DEFAULT_IDENTITY_NAME))
            return;

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
                ident = prov.createIdentity(acct, name, attrs, false);
                OfflineLog.offline.debug("dsync: created identity: " + acct.getName() + '/' + ident.getName());
            }
        } else {
            prov.modifyIdentity(acct, ident.getName(), diffAttributes(ident, zident.getAttrs()), false);
            prov.reload(ident);
            OfflineLog.offline.debug("dsync: updated identity: " + acct.getName() + '/' + ident.getName());
        }
    }

    static void syncDataSource(OfflineProvisioning prov, Account acct, ZDataSource zdsrc) throws ServiceException {
        String dsid = zdsrc.getId();
        String name = zdsrc.getName();

        Map<String, Object> attrs = zdsrc.getAttrs();

        DataSource dsrc = prov.get(acct, DataSourceBy.id, dsid);
        DataSource conflict = prov.get(acct, DataSourceBy.name, name);

        if (conflict != null && (dsrc == null || !conflict.getId().equals(dsrc.getId()))) {
            // handle any naming conflicts by renaming the *local* data source
            // XXX: if the data source has been renamed locally, no need to rename the conflict
            Map<String, Object> resolution = new HashMap<String, Object>(1);
            resolution.put(Provisioning.A_zimbraDataSourceName, name + '{' + UUID.randomUUID().toString() + '}');
            prov.modifyDataSource(acct, conflict.getId(), resolution);
            OfflineLog.offline.debug("dsync: detected conflict and renamed data source: " + acct.getName() + '/' + conflict.getName());
        }

        if (dsrc != null && isLocallyCreated(dsrc)) {
            // data source is marked as locally created, but it already exists on the server
            Map<String, Object> resolution = new HashMap<String, Object>(1);
            resolution.put('-' + OfflineProvisioning.A_offlineModifiedAttrs, OfflineProvisioning.A_offlineDn);
            prov.modifyDataSource(acct, dsrc.getId(), resolution, false);
            OfflineLog.offline.debug("dsync: marked data source as non-locally created: " + acct.getName() + '/' + dsrc.getName());
        }

        if (dsrc == null) {
            // if we're here and haven't locally deleted the data source, it's a new one and needs to be created
            if (!acct.getMultiAttrSet(OfflineProvisioning.A_offlineDeletedDataSource).contains(dsid)) {
                dsrc = prov.createDataSource(acct, zdsrc.getType(), name, attrs, false, false);
                OfflineLog.offline.debug("dsync: created data source: " + acct.getName() + '/' + dsrc.getName());
            }
        } else {
            prov.modifyDataSource(acct, dsrc.getId(), diffAttributes(dsrc, zdsrc.getAttrs()), false);
            prov.reload(dsrc);
            OfflineLog.offline.debug("dsync: updated data source: " + acct.getName() + '/' + dsrc.getName());
        }
    }


    static void pushAccount(OfflineProvisioning prov, Account acct, ZMailbox zmbx) throws ServiceException {
        Set<String> modified = acct.getMultiAttrSet(OfflineProvisioning.A_offlineModifiedAttrs);
        if (!modified.isEmpty()) {
            Map<String, Object> attrs = acct.getAttrs();
            Map<String, Object> changes = new HashMap<String, Object>(modified.size());
            for (String pref : modified) {
                // we're only authorized to push changes to user preferences
                if (pref.startsWith(ModifyPrefs.PREF_PREFIX))
                    changes.put(pref, attrs.get(pref));
                else if (!pref.startsWith("offline"))
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

        for (DataSource dsrc : prov.getAllDataSources(acct))
            pushDataSource(prov, acct, dsrc, zmbx);
        for (String dsid : acct.getMultiAttrSet(OfflineProvisioning.A_offlineDeletedDataSource)) {
            zmbx.deleteDataSource(DataSourceBy.id, dsid);
            OfflineLog.offline.debug("dpush: deleted data source: " + acct.getName() + '/' + dsid);
        }

        // FIXME: there's a race condition here, as <tt>acct</tt> may have been modified during the push
        prov.markAccountClean(acct);
    }

    private static void pushIdentity(OfflineProvisioning prov, Account acct, Identity ident, ZMailbox zmbx) throws ServiceException {
        // check to see if this identity has been modified since the last sync
        Set<String> modified = ident.getMultiAttrSet(OfflineProvisioning.A_offlineModifiedAttrs);
        if (modified == null || modified.isEmpty())
            return;

        Map<String, Object> attrs = ident.getAttrs();
        attrs.remove(OfflineProvisioning.A_offlineModifiedAttrs);
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

    private static void pushDataSource(OfflineProvisioning prov, Account acct, DataSource dsrc, ZMailbox zmbx) throws ServiceException {
        // check to see if this identity has been modified since the last sync
        Set<String> modified = dsrc.getMultiAttrSet(OfflineProvisioning.A_offlineModifiedAttrs);
        if (modified == null || modified.isEmpty())
            return;

        Map<String, Object> attrs = dsrc.getAttrs();
        attrs.remove(OfflineProvisioning.A_offlineModifiedAttrs);
        if (dsrc.getType() != DataSource.Type.pop3)
            throw OfflineServiceException.UNSUPPORTED("cannot push changes to " + dsrc.getType() + " data source: " + acct.getName() + '/' + dsrc.getName());
        ZDataSource zdsrc = new ZPop3DataSource(dsrc);

        // create or modify the identity, as requested
        if (isLocallyCreated(dsrc)) {
            zmbx.createDataSource(zdsrc);
            OfflineLog.offline.debug("dpush: created data source: " + acct.getName() + '/' + dsrc.getName());
        } else {
            zmbx.modifyDataSource(zdsrc);
            OfflineLog.offline.debug("dpush: modified data source: " + acct.getName() + '/' + dsrc.getName());
        }

        // clear the set of modified attributes, since we're now in sync
        Map<String, Object> postModify = new HashMap<String, Object>(1);
        postModify.put(OfflineProvisioning.A_offlineModifiedAttrs, null);
        prov.modifyDataSource(acct, dsrc.getName(), postModify, false);
    }
}
