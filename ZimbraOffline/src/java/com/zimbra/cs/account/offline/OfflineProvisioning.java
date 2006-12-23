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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.UUID;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.*;
import com.zimbra.cs.account.NamedEntry.Visitor;
import com.zimbra.cs.db.DbOfflineDirectory;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OfflineMailboxManager;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.mailbox.calendar.ICalTimeZone;
import com.zimbra.cs.mime.MimeTypeInfo;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.servlet.ZimbraServlet;
import com.zimbra.cs.zclient.ZDataSource;
import com.zimbra.cs.zclient.ZGetInfoResult;
import com.zimbra.cs.zclient.ZIdentity;
import com.zimbra.cs.zclient.ZMailbox;

public class OfflineProvisioning extends Provisioning {

    public static final String A_offlineDn = "offlineDn";
    public static final String A_offlineModifiedAttrs = "offlineModifiedAttrs";
    public static final String A_offlineDeletedIdentity = "offlineDeletedIdentity";
    public static final String A_offlineDeletedDataSource = "offlineDeletedDataSource";
    public static final String A_offlineRemotePassword = "offlineRemotePassword";
    public static final String A_offlineRemoteServerUri = "offlineRemoteServerUri";
    public static final String A_offlineDataSourceType = "offlineDataSourceType";


    public enum EntryType {
        ACCOUNT("acct"), DATASOURCE("dsrc", true), IDENTITY("idnt", true), COS("cos"), CONFIG("conf"), ZIMLET("zmlt");

        private String mAbbr;
        private boolean mLeafEntry;

        private EntryType(String abbr)                { mAbbr = abbr; }
        private EntryType(String abbr, boolean leaf)  { mAbbr = abbr;  mLeafEntry = leaf; }

        public boolean isLeafEntry()  { return mLeafEntry; }
        public String toString()      { return mAbbr; }

        public static EntryType typeForEntry(Entry e) {
            if (e instanceof Account)          return ACCOUNT;
            else if (e instanceof Identity)    return IDENTITY;
            else if (e instanceof DataSource)  return DATASOURCE;
            else if (e instanceof Cos)         return COS;
            else if (e instanceof Config)      return CONFIG;
            else if (e instanceof Zimlet)      return ZIMLET;
            else                               return null;
        }
    }


    private static final long MINIMUM_SYNC_INTERVAL = 15 * Constants.MILLIS_PER_SECOND;
    private static final long SYNC_TIMER_INTERVAL = 1 * Constants.MILLIS_PER_MINUTE;
    private static final long ACCOUNT_POLL_INTERVAL = 1 * Constants.MILLIS_PER_HOUR;
    private static DirectorySyncTask sSyncTask = null;
    static final Object sDirectorySynchronizer = new Object();

    private class DirectorySyncTask extends TimerTask {
        private boolean inProgress = false;
        private long lastExecutionTime = 0;
        private Map<String, Long> mLastSyncTimes = new HashMap<String, Long>();

        @Override
        public void run() {
            long now = System.currentTimeMillis();
            if (inProgress || now - lastExecutionTime < MINIMUM_SYNC_INTERVAL)
                return;

            synchronized (sDirectorySynchronizer) {
                inProgress = true;
                try {
                    // first, be sure to push the locally-changed accounts
                    if (hasDirtyAccounts()) {
                        for (Account acct : listDirtyAccounts()) {
                            if (DirectorySync.sync(acct))
                                mLastSyncTimes.put(acct.getId(), now);
                        }
                    }
    
                    // then, sync the accounts we haven't synced in a while
                    // XXX: we should have a cache and iterate over it -- accounts shouldn't change out from under us
                    for (Account acct : getAllAccounts()) {
                        long lastSync = mLastSyncTimes.get(acct.getId()) == null ? 0 : mLastSyncTimes.get(acct.getId());
                        if (now - lastSync > ACCOUNT_POLL_INTERVAL)
                            if (DirectorySync.sync(acct))
                                mLastSyncTimes.put(acct.getId(), now);
                    }
                } catch (ServiceException e) {
                    OfflineLog.offline.warn("error listing accounts to sync", e);
                } finally {
                    inProgress = false;
                }
            }
        }
    }


    private final OfflineConfig mLocalConfig;
    private final Server mLocalServer;
    private final Cos mDefaultCos;
    private final List<MimeTypeInfo> mMimeTypes;
    private final Map<String, Zimlet> mZimlets;
    private final NamedEntryCache<Account> mAccountCache;

    private boolean mHasDirtyAccounts = true;

    public OfflineProvisioning() {
        mLocalConfig  = OfflineConfig.instantiate();
        mLocalServer  = OfflineLocalServer.instantiate(mLocalConfig);
        mDefaultCos   = OfflineCos.instantiate();
        mMimeTypes    = OfflineMimeType.instantiateAll();
        mZimlets      = OfflineZimlet.instantiateAll();
        mAccountCache = new NamedEntryCache<Account>(LC.ldap_cache_account_maxsize.intValue(), LC.ldap_cache_account_maxage.intValue() * Constants.MILLIS_PER_MINUTE);

        if (sSyncTask != null)
            sSyncTask.cancel();
        sSyncTask = new DirectorySyncTask();
        OfflineMailboxManager.mTimer.schedule(sSyncTask, 5 * Constants.MILLIS_PER_SECOND, SYNC_TIMER_INTERVAL);
    }


    @Override
    public synchronized void modifyAttrs(Entry e, Map<String, ? extends Object> attrs, boolean checkImmutable) throws ServiceException {
        modifyAttrs(e, attrs, checkImmutable, true);
    }

    @Override
    public synchronized void modifyAttrs(Entry e, Map<String, ? extends Object> attrs, boolean checkImmutable, boolean allowCallback) throws ServiceException {
        modifyAttrs(e, attrs, checkImmutable, allowCallback, e instanceof Account);
    }

    synchronized void modifyAttrs(Entry e, Map<String, ? extends Object> attrs, boolean checkImmutable, boolean allowCallback, boolean markChanged) throws ServiceException {
        EntryType etype = EntryType.typeForEntry(e);
        if (etype == null)
            throw OfflineServiceException.UNSUPPORTED("modifyAttrs(" + e.getClass().getSimpleName() + ")");
        else if (etype == EntryType.IDENTITY)
            throw ServiceException.INVALID_REQUEST("must use Provisioning.modifyIdentity() instead", null);
        else if (etype == EntryType.DATASOURCE)
            throw ServiceException.INVALID_REQUEST("must use Provisioning.modifyDataSource() instead", null);

        // only tracking changes on account entries
        markChanged &= e instanceof Account;

        boolean settingModified = !markChanged && attrs.containsKey(A_offlineModifiedAttrs);
        Object modified = attrs.remove(A_offlineModifiedAttrs);
        HashMap context = new HashMap();
        AttributeManager.getInstance().preModify(attrs, e, context, false, checkImmutable, allowCallback);
        if (settingModified) {
            Map<String, Object> replacement = new HashMap<String, Object>(attrs.size() + 1);
            replacement.putAll(attrs);
            replacement.put(A_offlineModifiedAttrs, modified);
            attrs = replacement;
        }

        if (markChanged) {
            List<String> modattrs = new ArrayList<String>();
            for (String attr : attrs.keySet()) {
                if (attr.startsWith("-") || attr.startsWith("+"))
                    attr = attr.substring(1);
                if (!modattrs.contains(attr) && !attr.equalsIgnoreCase(A_offlineDn) && !attr.equalsIgnoreCase(A_offlineModifiedAttrs))
                    modattrs.add(attr);
            }
            if (!modattrs.isEmpty()) {
                Map<String, Object> replacement = new HashMap<String, Object>(attrs.size() + 1);
                replacement.putAll(attrs);
                replacement.put(A_offlineModifiedAttrs, modattrs.toArray(new String[modattrs.size()]));
                attrs = replacement;
            }
        }

        if (etype == EntryType.CONFIG) {
            DbOfflineDirectory.modifyDirectoryEntry(etype, A_offlineDn, "config", attrs, false);
        } else {
            DbOfflineDirectory.modifyDirectoryEntry(etype, A_zimbraId, e.getAttr(A_zimbraId), attrs, markChanged);
            mHasDirtyAccounts |= markChanged;
        }
        reload(e);
        AttributeManager.getInstance().postModify(attrs, e, context, false, allowCallback);
    }

    @Override
    public synchronized void reload(Entry e) throws ServiceException {
        EntryType etype = EntryType.typeForEntry(e);
        if (etype == null)
            throw OfflineServiceException.UNSUPPORTED("reload(" + e.getClass().getSimpleName() + ")");

        Map<String,Object> attrs = null;
        if (etype == EntryType.IDENTITY && e instanceof OfflineIdentity) {
            attrs = DbOfflineDirectory.readDirectoryLeaf(etype, ((OfflineIdentity) e).getAccount(), A_zimbraId, e.getAttr(A_zimbraPrefIdentityId));
            ((OfflineIdentity) e).setName(e.getAttr(A_zimbraPrefIdentityName));
        } else if (etype == EntryType.DATASOURCE && e instanceof OfflineDataSource) {
            attrs = DbOfflineDirectory.readDirectoryLeaf(etype, ((OfflineDataSource) e).getAccount(), A_zimbraId, e.getAttr(A_zimbraDataSourceId));
            ((OfflineDataSource) e).setName(e.getAttr(A_zimbraDataSourceName));
        } else if (etype == EntryType.CONFIG) {
            attrs = DbOfflineDirectory.readDirectoryEntry(etype, A_offlineDn, "config");
        } else {
            attrs = DbOfflineDirectory.readDirectoryEntry(etype, A_zimbraId, e.getAttr(A_zimbraId));
        }
        if (attrs == null)
            throw AccountServiceException.NO_SUCH_ACCOUNT(e.getAttr(A_mail));
        e.setAttrs(attrs);
    }

    @Override
    public synchronized ICalTimeZone getTimeZone(Account acct) throws ServiceException {
        return acct.getTimeZone();
    }

    @Override
    public synchronized boolean inDistributionList(Account acct, String zimbraId) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("inDistributionList");
    }

    @Override
    public synchronized Set<String> getDistributionLists(Account acct) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("getDistributionLists");
    }

    @Override
    public synchronized List<DistributionList> getDistributionLists(Account acct, boolean directOnly, Map<String, String> via) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("getDistributionLists");
    }

    @Override
    public synchronized List<DistributionList> getDistributionLists(DistributionList list, boolean directOnly, Map<String, String> via) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("getDistributionLists");
    }

    @Override
    public synchronized boolean healthCheck() {
        try {
            DbOfflineDirectory.readDirectoryEntry(EntryType.CONFIG, A_offlineDn, "config");
            return true;
        } catch (ServiceException e) {
            OfflineLog.offline.info("health check failed", e);
            return false;
        }
    }

    @Override
    public synchronized Config getConfig() {
        return mLocalConfig;
    }

    @Override
    public synchronized MimeTypeInfo getMimeType(String name) {
        for (MimeTypeInfo mtinfo : mMimeTypes) {
            if (mtinfo.getType().equalsIgnoreCase(name))
                return mtinfo;
        }
        return null;
    }

    @Override
    public synchronized MimeTypeInfo getMimeTypeByExtension(String ext) {
        for (MimeTypeInfo mtinfo : mMimeTypes) {
            for (String filext : mtinfo.getFileExtensions())
                if (filext.equalsIgnoreCase(ext))
                    return mtinfo;
        }
        return null;
    }

    @Override
    public synchronized List<Zimlet> getObjectTypes() {
        return listAllZimlets();
    }

    static final Set<String> sOfflineAttributes = new HashSet<String>(Arrays.asList(new String[] { 
        A_zimbraId, A_mail, A_uid, A_objectClass, A_zimbraMailHost, A_displayName, A_sn, A_zimbraAccountStatus
    }));

    @Override
    public synchronized Account createAccount(String emailAddress, String password, Map<String, Object> attrs) throws ServiceException {
        if (attrs == null || !(attrs.get(A_offlineRemoteServerUri) instanceof String))
            throw ServiceException.FAILURE("need single offlineRemoteServerUri when creating account: " + emailAddress, null);
        String uri = (String) attrs.get(A_offlineRemoteServerUri);

        String parts[] = emailAddress.split("@");
        if (parts.length != 2)
            throw ServiceException.INVALID_REQUEST("must be valid email address: " + emailAddress, null);
        String uid = parts[0];

        ZMailbox.Options options = new ZMailbox.Options(emailAddress, AccountBy.name, password, uri + ZimbraServlet.USER_SERVICE_URI);
        options.setNoSession(true);
        ZGetInfoResult zgi = ZMailbox.getMailbox(options).getAccountInfo(false);

        for (Map.Entry<String,List<String>> zattr : zgi.getAttrs().entrySet())
            for (String value : zattr.getValue())
                addToMap(attrs, zattr.getKey(), value);
        for (Map.Entry<String,List<String>> zpref : zgi.getPrefAttrs().entrySet())
            for (String value : zpref.getValue())
                addToMap(attrs, zpref.getKey(), value);
        attrs.put(A_zimbraId, zgi.getId());
        attrs.put(A_mail, emailAddress);
        attrs.put(A_uid, uid);
        attrs.put(A_objectClass, new String[] { "organizationalPerson", "zimbraAccount" } );
        attrs.put(A_zimbraMailHost, "localhost");
        attrs.put(A_offlineRemotePassword, password);
        if (!(attrs.get(A_cn) instanceof String))
            attrs.put(A_cn, attrs.get(A_displayName) instanceof String ? (String) attrs.get(A_displayName) : uid);
        if (!(attrs.get(A_sn) instanceof String))
            attrs.put(A_sn, uid);
        if (!(attrs.get(A_zimbraAccountStatus) instanceof String))
            attrs.put(A_zimbraAccountStatus, ACCOUNT_STATUS_ACTIVE);

        attrs.remove(A_zimbraIsAdminAccount);
        attrs.remove(A_zimbraIsDomainAdminAccount);

        synchronized (this) {
            // create account entry in database
            DbOfflineDirectory.createDirectoryEntry(EntryType.ACCOUNT, emailAddress, attrs, false);
            Account acct = new Account(emailAddress, zgi.getId(), attrs, mDefaultCos.getAccountDefaults());
            mAccountCache.put(acct);

            try {
                // create identity entries in database
                for (ZIdentity zident : zgi.getIdentities())
                    DirectorySync.syncIdentity(this, acct, zident);
                // create data source entries in database
                for (ZDataSource zdsrc : zgi.getDataSources())
                    DirectorySync.syncDataSource(this, acct, zdsrc);
                // fault in the mailbox so it's picked up by the sync loop
                MailboxManager.getInstance().getMailboxByAccount(acct);
            } catch (ServiceException e) {
                OfflineLog.offline.error("error initializing account " + emailAddress, e);
                mAccountCache.remove(acct);
                deleteAccount(zgi.getId());
                throw e;
            }

            return acct;
        }
    }

    public static void addToMap(Map<String,Object> attrs, String key, String value) {
        Object existing = attrs.get(key);
        if (existing == null) {
            attrs.put(key, value);
        } else if (existing instanceof String) {
            attrs.put(key, new String[] { (String) existing, value } );
        } else {
            String[] before = (String[]) existing, after = new String[before.length+1];
            System.arraycopy(before, 0, after, 0, before.length);
            after[after.length-1] = value;
            attrs.put(key, after);
        }
    }

    @Override
    public synchronized void deleteAccount(String zimbraId) throws ServiceException {
        DbOfflineDirectory.deleteDirectoryEntry(EntryType.ACCOUNT, zimbraId);

        Account acct = mAccountCache.getById(zimbraId);
        if (acct != null)
            mAccountCache.remove(acct);
    }

    @Override
    public synchronized void renameAccount(String zimbraId, String newName) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("renameAccount");
    }

    @Override
    public synchronized Account get(AccountBy keyType, String key) throws ServiceException {
        Account acct = null;
        Map<String,Object> attrs = null;
        if (keyType == AccountBy.id) {
            if ((acct = mAccountCache.getById(key)) != null)
                return acct;
            attrs = DbOfflineDirectory.readDirectoryEntry(EntryType.ACCOUNT, A_zimbraId, key);
        } else if (keyType == AccountBy.name) {
            if ((acct = mAccountCache.getByName(key)) != null)
                return acct;
            attrs = DbOfflineDirectory.readDirectoryEntry(EntryType.ACCOUNT, A_offlineDn, key);
        } else if (keyType == AccountBy.adminName) {
            if ((acct = mAccountCache.getByName(key)) != null)
                return acct;
            if (key.equals(LC.zimbra_ldap_user.value())) {
                attrs = new HashMap<String,Object>(7);
                attrs.put(A_mail, key);
                attrs.put(A_cn, key);
                attrs.put(A_sn, key);
                attrs.put(A_zimbraId, UUID.randomUUID().toString());
                attrs.put(A_zimbraAccountStatus, ACCOUNT_STATUS_ACTIVE);
                attrs.put(A_offlineRemotePassword, LC.zimbra_ldap_password.value());
                attrs.put(A_zimbraIsAdminAccount, TRUE);
            }
        }
        if (attrs == null)
            return null;

        acct = new Account((String) attrs.get(A_mail), (String) attrs.get(A_zimbraId), attrs, mDefaultCos.getAccountDefaults());
        mAccountCache.put(acct);
        return acct;
    }

    @Override
    public synchronized List<NamedEntry> searchAccounts(String query, String[] returnAttrs, String sortAttr, boolean sortAscending, int flags) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    public synchronized List<Account> getAllAccounts() throws ServiceException {
        List<Account> accts = new ArrayList<Account>();
        for (String zimbraId : DbOfflineDirectory.listAllDirectoryEntries(EntryType.ACCOUNT)) {
            Account acct = get(AccountBy.id, zimbraId);
            if (acct != null)
                accts.add(acct);
        }
        return accts;
    }

    @Override
    public synchronized List<Account> getAllAdminAccounts() throws ServiceException {
        List<Account> admins = new ArrayList<Account>(1);
        Account acct = get(AccountBy.adminName, LC.zimbra_ldap_user.value());
        if (acct != null)
            admins.add(acct);
        return admins;
    }

    synchronized boolean hasDirtyAccounts() {
        return mHasDirtyAccounts;
    }

    synchronized List<Account> listDirtyAccounts() throws ServiceException {
        List<Account> dirty = new ArrayList<Account>();
        for (String zimbraId : DbOfflineDirectory.listAllDirtyEntries(EntryType.ACCOUNT)) {
            Account acct = get(AccountBy.id, zimbraId);
            if (acct != null)
                dirty.add(acct);
        }
        mHasDirtyAccounts = !dirty.isEmpty();
        return dirty;
    }

    synchronized void markAccountClean(Account acct) throws ServiceException {
        DbOfflineDirectory.markEntryClean(EntryType.ACCOUNT, acct);
        reload(acct);
    }

    @Override
    public synchronized void setCOS(Account acct, Cos cos) throws ServiceException {
        if (cos != mDefaultCos)
            throw OfflineServiceException.UNSUPPORTED("setCOS");
    }

    @Override
    public synchronized void modifyAccountStatus(Account acct, String newStatus) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("modifyAccountStatus");
    }

    @Override
    public synchronized void authAccount(Account acct, String password, String proto) throws ServiceException {
        try {
            if (password == null || password.equals(""))
                throw AccountServiceException.AUTH_FAILED(acct.getName() + " (empty password)");
            if (!password.equals(acct.getAttr(A_offlineRemotePassword)))
                throw AccountServiceException.AUTH_FAILED(acct.getName());
            ZimbraLog.security.info(ZimbraLog.encodeAttrs(new String[] {"cmd", "Auth", "account", acct.getName(), "protocol", proto}));
        } catch (ServiceException e) {
            ZimbraLog.security.warn(ZimbraLog.encodeAttrs(new String[] {"cmd", "Auth", "account", acct.getName(), "protocol", proto, "error", e.getMessage()}));             
            throw e;
        }
    }

    @Override
    public synchronized void preAuthAccount(Account acct, String accountName, String accountBy, long timestamp, long expires, String preAuth) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("preAuthAccount");
    }

    @Override
    public synchronized void changePassword(Account acct, String currentPassword, String newPassword) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void setPassword(Account acct, String newPassword) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void addAlias(Account acct, String alias) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("addAlias");
    }

    @Override
    public synchronized void removeAlias(Account acct, String alias) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("removeAlias");
    }

    @Override
    public synchronized Domain createDomain(String name, Map<String, Object> attrs) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("createDomain");
    }

    @Override
    public synchronized Domain get(DomainBy keyType, String key) {
        return null;
    }

    @Override
    public synchronized List<Domain> getAllDomains() {
        return Collections.emptyList();
    }

    @Override
    public synchronized void deleteDomain(String zimbraId) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("deleteDomain");
    }

    @Override
    public synchronized Cos createCos(String name, Map<String, Object> attrs) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("createCos");
    }

    @Override
    public synchronized void renameCos(String zimbraId, String newName) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("renameCos");
    }

    @Override
    public synchronized Cos get(CosBy keyType, String key) throws ServiceException {
        if (keyType == CosBy.id)
            return mDefaultCos.getId().equalsIgnoreCase(key) ? mDefaultCos : null;
        else if (keyType == CosBy.name)
            return mDefaultCos.getName().equalsIgnoreCase(key) ? mDefaultCos : null;
        else
            throw ServiceException.FAILURE("unsupported CosBy value: " + keyType, null);
    }

    @Override
    public synchronized List<Cos> getAllCos() {
        List<Cos> coses = new ArrayList<Cos>(1);
        coses.add(mDefaultCos);
        return coses;
    }

    @Override
    public synchronized void deleteCos(String zimbraId) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("deleteCos");
    }

    @Override
    public synchronized Server getLocalServer() {
        return mLocalServer;
    }

    @Override
    public synchronized Server createServer(String name, Map<String, Object> attrs) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("createServer");
    }

    @Override
    public synchronized Server get(ServerBy keyType, String key) throws ServiceException {
        if (keyType == ServerBy.id)
            return mLocalServer.getId().equalsIgnoreCase(key) ? mLocalServer : null;
        else if (keyType == ServerBy.name)
            return mLocalServer.getName().equalsIgnoreCase(key) ? mLocalServer : null;
        else if (keyType == ServerBy.serviceHostname)
            return mLocalServer.getAttr(A_zimbraServiceHostname, "localhost").equalsIgnoreCase(key) ? mLocalServer : null;
        else
            throw ServiceException.FAILURE("unsupported ServerBy value: " + keyType, null);
    }

    @Override
    public synchronized List<Server> getAllServers() {
        List<Server> servers = new ArrayList<Server>(1);
        servers.add(mLocalServer);
        return servers;
    }

    @Override
    public synchronized List<Server> getAllServers(String service) {
        if ("mailbox".equalsIgnoreCase(service))
            return getAllServers();
        return Collections.emptyList();
    }

    @Override
    public synchronized void deleteServer(String zimbraId) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("deleteServer");
    }

    @Override
    public synchronized DistributionList createDistributionList(String listAddress, Map<String, Object> listAttrs) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("createDistributionList");
    }

    @Override
    public synchronized DistributionList get(DistributionListBy keyType, String key) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("get(DistributionList)");
    }

    @Override
    public synchronized void deleteDistributionList(String zimbraId) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("deleteDistributionList");
    }

    @Override
    public synchronized void addAlias(DistributionList dl, String alias) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("addAlias");
    }

    @Override
    public synchronized void removeAlias(DistributionList dl, String alias) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("removeAlias");
    }

    @Override
    public synchronized void renameDistributionList(String zimbraId, String newName) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("renameDistributionList");
    }

    @Override
    public synchronized Zimlet getZimlet(String name) {
        return mZimlets.get(name.toLowerCase());
    }

    @Override
    public synchronized List<Zimlet> listAllZimlets() {
        // FIXME: not thread-safe wrt zimlet deletes/creates
        return new ArrayList<Zimlet>(mZimlets.values());
    }

    @Override
    public synchronized Zimlet createZimlet(String name, Map<String, Object> attrs) throws ServiceException {
        name = name.toLowerCase();

        HashMap attrManagerContext = new HashMap();
        AttributeManager.getInstance().preModify(attrs, null, attrManagerContext, true, true);
        if (!(attrs.get(A_zimbraId) instanceof String))
            attrs.put(A_zimbraId, UUID.randomUUID().toString());
        attrs.put(A_cn, name);
        attrs.put(A_objectClass, "zimbraZimletEntry");
        attrs.put(A_zimbraZimletEnabled, FALSE);
        attrs.put(A_zimbraZimletIndexingEnabled, attrs.containsKey(A_zimbraZimletKeyword) ? TRUE : FALSE);

        DbOfflineDirectory.createDirectoryEntry(EntryType.ZIMLET, name, attrs, false);
        Zimlet zimlet = new OfflineZimlet(name, (String) attrs.get(A_zimbraId), attrs);
        mZimlets.put(name, zimlet);
        AttributeManager.getInstance().postModify(attrs, zimlet, attrManagerContext, true);
        return zimlet;
    }

    @Override
    public synchronized void deleteZimlet(String name) throws ServiceException {
        name = name.toLowerCase();

        Zimlet zimlet = mZimlets.get(name);
        if (zimlet == null)
            return;
        DbOfflineDirectory.deleteDirectoryEntry(EntryType.ZIMLET, zimlet.getId());
        mZimlets.remove(name);
    }

    @Override
    public synchronized CalendarResource createCalendarResource(String emailAddress, String password, Map<String, Object> attrs) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("createCalendarResource");
    }

    @Override
    public synchronized void deleteCalendarResource(String zimbraId) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("deleteCalendarResource");
    }

    @Override
    public synchronized void renameCalendarResource(String zimbraId, String newName) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("renamerCalendarResource");
    }

    @Override
    public synchronized CalendarResource get(CalendarResourceBy keyType, String key) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List<NamedEntry> searchCalendarResources(EntrySearchFilter filter, String[] returnAttrs, String sortAttr, boolean sortAscending) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List<Account> getAllAccounts(Domain d) throws ServiceException {
        if (d == null || d.getAttr(A_zimbraDomainName) == null)
            throw ServiceException.INVALID_REQUEST("null Domain or missing domain name", null);
        List<Account> accts = new ArrayList<Account>();

        List<String> ids = DbOfflineDirectory.searchDirectoryEntries(EntryType.ACCOUNT, A_offlineDn, '%' + d.getAttr(A_zimbraDomainName));
        for (String id : ids) {
            Account acct = get(AccountBy.id, id);
            if (acct != null)
                accts.add(acct);
        }
        return accts;
    }

    @Override
    public synchronized void getAllAccounts(Domain d, Visitor visitor) throws ServiceException {
        for (Account acct : getAllAccounts(d))
            visitor.visit(acct);
    }

    @Override
    public synchronized List getAllCalendarResources(Domain d) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void getAllCalendarResources(Domain d, Visitor visitor) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List getAllDistributionLists(Domain d) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("getAllDistributionLists");
    }

    @Override
    public synchronized List<NamedEntry> searchAccounts(Domain d, String query, String[] returnAttrs, String sortAttr, boolean sortAscending, int flags) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized SearchGalResult searchGal(Domain d, String query, GAL_SEARCH_TYPE type, String token) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized SearchGalResult autoCompleteGal(Domain d, String query, GAL_SEARCH_TYPE type, int limit) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized List searchCalendarResources(Domain d, EntrySearchFilter filter, String[] returnAttrs, String sortAttr, boolean sortAscending) throws ServiceException {
        return Collections.emptyList();
    }

    @Override
    public synchronized void addMembers(DistributionList list, String[] members) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("addMembers");
    }

    @Override
    public synchronized void removeMembers(DistributionList list, String[] member) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("removeMembers");
    }

    private void validateIdentityAttrs(Map<String, Object> attrs) throws ServiceException {
        Set<String> validAttrs = AttributeManager.getInstance().getLowerCaseAttrsInClass(AttributeClass.identity);
        validAttrs.add(A_objectClass.toLowerCase());
        validAttrs.add(A_offlineModifiedAttrs.toLowerCase());

        for (String key : attrs.keySet()) {
            if (key.startsWith("+") || key.startsWith("-"))
                key = key.substring(1);
            if (!validAttrs.contains(key.toLowerCase()))
                throw ServiceException.INVALID_REQUEST("unable to modify attr: " + key, null);
        }
    }

    @Override
    public synchronized Identity createIdentity(Account account, String name, Map<String, Object> attrs) throws ServiceException {
        return createIdentity(account, name, attrs, true);
    }

    synchronized Identity createIdentity(Account account, String name, Map<String, Object> attrs, boolean markChanged) throws ServiceException {
        if (name.equalsIgnoreCase(DEFAULT_IDENTITY_NAME))
            throw AccountServiceException.IDENTITY_EXISTS(name);

        List<Identity> existing = getAllIdentities(account);
        if (existing.size() >= account.getLongAttr(A_zimbraIdentityMaxNumEntries, 20))
            throw AccountServiceException.TOO_MANY_IDENTITIES();

        attrs.remove(A_offlineModifiedAttrs);
        validateIdentityAttrs(attrs);
        HashMap attrManagerContext = new HashMap();
        AttributeManager.getInstance().preModify(attrs, null, attrManagerContext, true, true);

        if (!(attrs.get(A_zimbraPrefIdentityId) instanceof String))
            attrs.put(A_zimbraPrefIdentityId, UUID.randomUUID().toString());
        String identId = (String) attrs.get(A_zimbraPrefIdentityId);
        attrs.put(A_zimbraPrefIdentityName, name);
        attrs.put(A_objectClass, "zimbraIdentity");
        if (markChanged)
            attrs.put(A_offlineModifiedAttrs, A_offlineDn);

        DbOfflineDirectory.createDirectoryLeafEntry(EntryType.IDENTITY, account, name, identId, attrs, markChanged);
        Identity identity = new OfflineIdentity(account, name, attrs);
        AttributeManager.getInstance().postModify(attrs, identity, attrManagerContext, true);
        return identity;
    }

    @Override
    public synchronized void deleteIdentity(Account account, String name) throws ServiceException {
        deleteIdentity(account, name, true);
    }

    synchronized void deleteIdentity(Account account, String name, boolean markChanged) throws ServiceException {
        if (name.equalsIgnoreCase(DEFAULT_IDENTITY_NAME))
            throw ServiceException.INVALID_REQUEST("can't delete default identity", null);

        Identity ident = get(account, IdentityBy.name, name);
        if (ident == null)
            return;

        DbOfflineDirectory.deleteDirectoryLeaf(EntryType.IDENTITY, account, ident.getId(), markChanged);
        reload(account);
    }

    @Override
    public synchronized List<Identity> getAllIdentities(Account account) throws ServiceException {
        List<String> names = DbOfflineDirectory.listAllDirectoryLeaves(EntryType.IDENTITY, account);

        List<Identity> identities = new ArrayList<Identity>(names.size() + 1);
        identities.add(getDefaultIdentity(account));
        for (String name : names)
            identities.add(get(account, IdentityBy.name, name));
        return identities;
    }

    @Override
    public synchronized void modifyIdentity(Account account, String name, Map<String, Object> attrs) throws ServiceException {
        modifyIdentity(account, name, attrs, true);
    }

    synchronized void modifyIdentity(Account account, String name, Map<String, Object> attrs, boolean markChanged) throws ServiceException {
        validateIdentityAttrs(attrs);
        if (name.equalsIgnoreCase(DEFAULT_IDENTITY_NAME)) {
            modifyAttrs(account, attrs, false, true, markChanged);
            return;
        }

        Identity identity = get(account, IdentityBy.name, name);
        if (identity == null)
            throw AccountServiceException.NO_SUCH_IDENTITY(name);

        boolean settingModified = !markChanged && attrs.containsKey(A_offlineModifiedAttrs);
        Object modified = attrs.remove(A_offlineModifiedAttrs);
        HashMap context = new HashMap();
        AttributeManager.getInstance().preModify(attrs, identity, context, false, true, true);
        if (settingModified)
            attrs.put(A_offlineModifiedAttrs, modified);

        if (markChanged) {
            List<String> modattrs = new ArrayList<String>();
            for (String attr : attrs.keySet()) {
                if (attr.startsWith("-") || attr.startsWith("+"))
                    attr = attr.substring(1);
                if (!modattrs.contains(attr) && !attr.equalsIgnoreCase(A_offlineDn) && !attr.equalsIgnoreCase(A_offlineModifiedAttrs))
                    modattrs.add(attr);
            }
            if (!modattrs.isEmpty())
                attrs.put('+' + A_offlineModifiedAttrs, modattrs.toArray(new String[modattrs.size()]));
        }

        String newName = (String) attrs.get(A_zimbraPrefIdentityName);
        if (newName == null)
            newName = (String) attrs.get('+' + A_zimbraPrefIdentityName);

        DbOfflineDirectory.modifyDirectoryLeaf(EntryType.IDENTITY, account, A_offlineDn, name, attrs, markChanged, newName);
        reload(identity);
        AttributeManager.getInstance().postModify(attrs, identity, context, false, true);
    }

    @Override
    public synchronized Identity get(Account account, IdentityBy keyType, String key) throws ServiceException {
        Map<String,Object> attrs = null;
        if (keyType == IdentityBy.name) {
            if (key.equalsIgnoreCase(DEFAULT_IDENTITY_NAME))
                return getDefaultIdentity(account);
            attrs = DbOfflineDirectory.readDirectoryLeaf(EntryType.IDENTITY, account, A_offlineDn, key);
        } else if (keyType == IdentityBy.id) {
            if (key.equalsIgnoreCase(account.getAttr(A_zimbraPrefIdentityId)))
                return getDefaultIdentity(account);
            attrs = DbOfflineDirectory.readDirectoryLeaf(EntryType.IDENTITY, account, A_zimbraId, key);
        }
        if (attrs == null)
            return null;

        return new OfflineIdentity(account, (String) attrs.get(A_zimbraPrefIdentityName), attrs);
    }

    @Override
    public synchronized DataSource createDataSource(Account account, DataSource.Type type, String name, Map<String, Object> attrs) throws ServiceException {
        return createDataSource(account, type, name, attrs, false, true);
    }

    @Override
    public DataSource createDataSource(Account account, DataSource.Type type, String name, Map<String, Object> attrs, boolean passwdAlreadyEncrypted) throws ServiceException {
        return createDataSource(account, type, name, attrs, passwdAlreadyEncrypted, true);
    }

    synchronized DataSource createDataSource(Account account, DataSource.Type type, String name, Map<String, Object> attrs, boolean passwdAlreadyEncrypted, boolean markChanged)
    throws ServiceException {
        List<DataSource> existing = getAllDataSources(account);
        if (existing.size() >= account.getLongAttr(A_zimbraDataSourceMaxNumEntries, 20))
            throw AccountServiceException.TOO_MANY_DATA_SOURCES();

        attrs.remove(A_offlineModifiedAttrs);
        HashMap attrManagerContext = new HashMap();
        AttributeManager.getInstance().preModify(attrs, null, attrManagerContext, true, true);

        if (!(attrs.get(A_zimbraDataSourceId) instanceof String))
            attrs.put(A_zimbraDataSourceId, UUID.randomUUID().toString());
        String dsid = (String) attrs.get(A_zimbraDataSourceId);
        attrs.put(A_zimbraDataSourceName, name); // must be the same
        attrs.put(A_offlineDataSourceType, type.toString());
        attrs.put(A_objectClass, "zimbraDataSource");
        if (attrs.get(A_zimbraDataSourcePassword) instanceof String)
            attrs.put(A_zimbraDataSourcePassword, DataSource.encryptData(dsid, (String) attrs.get(A_zimbraDataSourcePassword)));
        if (markChanged)
            attrs.put(A_offlineModifiedAttrs, A_offlineDn);

        DbOfflineDirectory.createDirectoryLeafEntry(EntryType.DATASOURCE, account, name, dsid, attrs, markChanged);
        DataSource dsrc = new OfflineDataSource(account, type, name, dsid, attrs);
        AttributeManager.getInstance().postModify(attrs, dsrc, attrManagerContext, true);
        return dsrc;
    }

    @Override
    public synchronized void deleteDataSource(Account account, String dataSourceId) throws ServiceException {
        deleteDataSource(account, dataSourceId, true);
    }

    synchronized void deleteDataSource(Account account, String dataSourceId, boolean markChanged) throws ServiceException {
        DataSource dsrc = get(account, DataSourceBy.id, dataSourceId);
        if (dsrc == null)
            return;

        DbOfflineDirectory.deleteDirectoryLeaf(EntryType.DATASOURCE, account, dsrc.getId(), markChanged);
        reload(account);
    }

    @Override
    public synchronized List<DataSource> getAllDataSources(Account account) throws ServiceException {
        List<String> names = DbOfflineDirectory.listAllDirectoryLeaves(EntryType.DATASOURCE, account);
        if (names.isEmpty())
            return Collections.emptyList();

        List<DataSource> sources = new ArrayList<DataSource>(names.size());
        for (String name : names)
            sources.add(get(account, DataSourceBy.name, name));
        return sources;
    }

    @Override
    public synchronized void modifyDataSource(Account account, String dataSourceId, Map<String, Object> attrs) throws ServiceException {
        modifyDataSource(account, dataSourceId, attrs, true);
    }

    synchronized void modifyDataSource(Account account, String dataSourceId, Map<String, Object> attrs, boolean markChanged) throws ServiceException {
        DataSource dsrc = get(account, DataSourceBy.id, dataSourceId);
        if (dsrc == null)
            throw AccountServiceException.NO_SUCH_DATA_SOURCE(dataSourceId);

        boolean settingModified = !markChanged && attrs.containsKey(A_offlineModifiedAttrs);
        Object modified = attrs.remove(A_offlineModifiedAttrs);
        HashMap context = new HashMap();
        AttributeManager.getInstance().preModify(attrs, dsrc, context, false, true, true);
        if (settingModified)
            attrs.put(A_offlineModifiedAttrs, modified);

        if (markChanged) {
            List<String> modattrs = new ArrayList<String>();
            for (String attr : attrs.keySet()) {
                if (attr.startsWith("-") || attr.startsWith("+"))
                    attr = attr.substring(1);
                if (!modattrs.contains(attr) && !attr.equalsIgnoreCase(A_offlineDn) && !attr.equalsIgnoreCase(A_offlineModifiedAttrs))
                    modattrs.add(attr);
            }
            if (!modattrs.isEmpty())
                attrs.put('+' + A_offlineModifiedAttrs, modattrs.toArray(new String[modattrs.size()]));
        }

        String newName = (String) attrs.get(A_zimbraDataSourceName);
        if (newName == null)
            newName = (String) attrs.get('+' + A_zimbraDataSourceName);

        DbOfflineDirectory.modifyDirectoryLeaf(EntryType.DATASOURCE, account, A_zimbraId, dataSourceId, attrs, markChanged, newName);
        reload(dsrc);
        AttributeManager.getInstance().postModify(attrs, dsrc, context, false, true);
    }

    @Override
    public synchronized DataSource get(Account account, DataSourceBy keyType, String key) throws ServiceException {
        Map<String,Object> attrs = null;
        if (keyType == DataSourceBy.name) {
            attrs = DbOfflineDirectory.readDirectoryLeaf(EntryType.DATASOURCE, account, A_offlineDn, key);
        } else if (keyType == DataSourceBy.id) {
            attrs = DbOfflineDirectory.readDirectoryLeaf(EntryType.DATASOURCE, account, A_zimbraId, key);
        }
        if (attrs == null)
            return null;

        DataSource.Type type = DataSource.Type.fromString((String) attrs.get(A_offlineDataSourceType));
        return new OfflineDataSource(account, type, (String) attrs.get(A_zimbraDataSourceName), (String) attrs.get(A_zimbraDataSourceId), attrs);
    }
}
