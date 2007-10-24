/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
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
import java.util.UUID;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.*;
import com.zimbra.cs.account.NamedEntry.Visitor;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.db.DbOfflineDirectory;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;
import com.zimbra.cs.mime.MimeTypeInfo;
import com.zimbra.cs.offline.Offline;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
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
    public static final String A_offlineDeletedSignature = "offlineDeletedSignature";

    public static final String A_offlineRemoteServerVersion = "offlineRemoteServerVersion";
    public static final String A_offlineRemotePassword = "offlineRemotePassword";
    public static final String A_offlineRemoteServerUri = "offlineRemoteServerUri";
    
    public static final String A_offlineProxyHost = "offlineProxyHost";
    public static final String A_offlineProxyPort = "offlineProxyPort";
    public static final String A_offlineProxyUser = "offlineProxyUser";
    public static final String A_offlineProxyPass = "offlineProxyPass";

    public static final String A_offlineSyncInterval = "offlineSyncInterval";
    public static final String A_offlineDataSourceType = "offlineDataSourceType";
    
    public static final String A_zimbraDataSourceSmtpHost = "zimbraDataSourceSmtpHost";
    public static final String A_zimbraDataSourceSmtpPort = "zimbraDataSourceSmtpPort";
    public static final String A_zimbraDataSourceSmtpConnectionType = "zimbraDataSourceSmtpConnectionType";
    public static final String A_zimbraDataSourceSmtpAuthRequired = "zimbraDataSourceSmtpAuthRequired";
    public static final String A_zimbraDataSourceSmtpAuthUsername = "zimbraDataSourceSmtpAuthUsername";
    public static final String A_zimbraDataSourceSmtpAuthPassword = "zimbraDataSourceSmtpAuthPassword";
    
    public static final String A_zimbraDataSourceUseProxy = "zimbraDataSourceUseProxy";
    public static final String A_zimbraDataSourceProxyHost = "zimbraDataSourceProxyHost";
    public static final String A_zimbraDataSourceProxyPort = "zimbraDataSourceProxyPort";

    public enum EntryType {
        ACCOUNT("acct"), DATASOURCE("dsrc", true), IDENTITY("idnt", true), SIGNATURE("sig", true), COS("cos"), CONFIG("conf"), ZIMLET("zmlt");

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
            else if (e instanceof Signature)   return SIGNATURE;
            else if (e instanceof Cos)         return COS;
            else if (e instanceof Config)      return CONFIG;
            else if (e instanceof Zimlet)      return ZIMLET;
            else                               return null;
        }
    }
    
    public synchronized static OfflineProvisioning getOfflineInstance() {
    	return (OfflineProvisioning)getInstance();
    }
    
    private static String appId = OfflineLC.zdesktop_app_id.value();
    
    private static String encryptData(String clear) throws ServiceException {
    	if (appId == null)
    		return clear;
    	return DataSource.encryptData(appId, clear);
    }
    
    private static String decryptData(String crypt) throws ServiceException {
    	if (appId == null)
    		return crypt;
    	return DataSource.decryptData(appId, crypt);
    }

    static final Object sDirectorySynchronizer = new Object();
    
    long mMinSyncInterval = OfflineLC.zdesktop_dirsync_min_delay.longValue();
    long mFailSyncInterval = OfflineLC.zdesktop_dirsync_fail_delay.longValue();
    long mAccountPollInterval = OfflineLC.zdesktop_account_poll_interval.longValue();

    private boolean inProgress = false;
    private long lastExecutionTime = 0;
    private Map<String, Long> mLastSyncTimes = new HashMap<String, Long>();
    private Map<String, Long> mLastFailTimes = new HashMap<String, Long>();
    
    public void syncAllAccounts(boolean isOnRequest) {
    	if (inProgress)
    		return;
    	
        long now = System.currentTimeMillis();
        if (!isOnRequest && now - lastExecutionTime < mMinSyncInterval)
            return;

        synchronized (sDirectorySynchronizer) {
            inProgress = true;
            try {
                // first, be sure to push the locally-changed accounts
                if (hasDirtyAccounts()) {
                    for (Account acct : listDirtyAccounts()) {
                    	long lastFail = mLastFailTimes.get(acct.getId()) == null ? 0 : mLastFailTimes.get(acct.getId());
                    	if (now - lastFail > mFailSyncInterval) { //we slow donw dir sync if a failure ever happened
	                        if (DirectorySync.sync(acct, isOnRequest)) {
	                            mLastSyncTimes.put(acct.getId(), now);
	                        	mLastFailTimes.remove(acct.getId());
	                        } else
	                        	mLastFailTimes.put(acct.getId(), now);
                    	}
                    }
                }

                // then, sync the accounts we haven't synced in a while
                // XXX: we should have a cache and iterate over it -- accounts shouldn't change out from under us
                for (Account acct : getAllAccounts()) {
                    long lastSync = mLastSyncTimes.get(acct.getId()) == null ? 0 : mLastSyncTimes.get(acct.getId());
                    long lastFail = mLastFailTimes.get(acct.getId()) == null ? 0 : mLastFailTimes.get(acct.getId());
                    if (now - lastFail > mFailSyncInterval && now - lastSync > mAccountPollInterval) {
                    	if (DirectorySync.sync(acct, isOnRequest)) {
	                        mLastSyncTimes.put(acct.getId(), now);
                    		mLastFailTimes.remove(acct.getId());
                    	} else
                    		mLastFailTimes.put(acct.getId(), now);
                    }
                }

                lastExecutionTime = now;
            } catch (ServiceException e) {
                OfflineLog.offline.warn("error listing accounts to sync", e);
            } catch (Throwable t) {
            	OfflineLog.offline.error("Unexpected exception syncing directory", t);
            } finally {
                inProgress = false;
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
    }
    
    public ZMailbox newZMailbox(OfflineAccount account, String serviceUri) throws ServiceException {
    	ZMailbox.Options options = null;
    	String uri = Offline.getServerURI(account, serviceUri);
    	String authToken = OfflineSyncManager.getInstance().lookupAuthToken(account);
    	if (authToken != null) {
    		options = new ZMailbox.Options(authToken, uri);
    	} else {
    		options = new ZMailbox.Options(account.getAttr(Provisioning.A_mail), AccountBy.name, account.getAttr(A_offlineRemotePassword), uri);
    	}
    	return newZMailbox(options, account.getProxyHost(), account.getProxyPort(), account.getProxyUser(), account.getProxyPass());
    }
    
    public ZMailbox newZMailbox(String email, String password, Map<String, Object> attrs, String serviceUri) throws ServiceException {
    	String proxyHost = (String)attrs.get(A_offlineProxyHost);
    	int proxyPort = 0;
    	String portStr = (String)attrs.get(A_offlineProxyPort);
    	if (portStr != null) {
	    	try {
	    		proxyPort = Integer.parseInt(portStr);
	    	} catch (NumberFormatException x) {}
    	}
    	String proxyUser = (String)attrs.get(A_offlineProxyUser);
    	String proxyPass = (String)attrs.get(A_offlineProxyPass);
    	
    	String uri = Offline.getServerURI((String)attrs.get(A_offlineRemoteServerUri), serviceUri);
    	ZMailbox.Options options = new ZMailbox.Options(email, AccountBy.name, password, uri);
    	
    	return newZMailbox(options, proxyHost, proxyPort, proxyUser, proxyPass);
    }
    
    private ZMailbox newZMailbox(ZMailbox.Options options, String proxyHost, int proxyPort, String proxyUser, String proxyPass) throws ServiceException {
        options.setProxy(proxyHost, proxyPort, proxyUser, proxyPass);
        options.setNoSession(true);
        options.setUserAgent(OfflineLC.zdesktop_name.value(), OfflineLC.zdesktop_version.value());
        options.setTimeout(OfflineLC.zdesktop_request_timeout.intValue());
        options.setRetryCount(1);
        options.setDebugListener(new Offline.OfflineDebugListener());
        ZMailbox zmbox = ZMailbox.getMailbox(options);
        if (options.getAuthToken() == null) //it was auth by password
        	OfflineSyncManager.getInstance().authSuccess(options.getAccount(), options.getPassword(), zmbox.getAuthResult().getAuthToken(), zmbox.getAuthResult().getExpires());
        return zmbox;
    }

    @Override
    public synchronized void modifyAttrs(Entry e, Map<String, ? extends Object> attrs, boolean checkImmutable) throws ServiceException {
        modifyAttrs(e, attrs, checkImmutable, true);
    }

    @Override
    public synchronized void modifyAttrs(Entry e, Map<String, ? extends Object> attrs, boolean checkImmutable, boolean allowCallback) throws ServiceException {
        modifyAttrs(e, attrs, checkImmutable, allowCallback, e instanceof Account && !isLocalAccount((Account)e));
    }

    synchronized void modifyAttrs(Entry e, Map<String, ? extends Object> attrs, boolean checkImmutable, boolean allowCallback, boolean markChanged) throws ServiceException {
        EntryType etype = EntryType.typeForEntry(e);
        if (etype == null)
            throw OfflineServiceException.UNSUPPORTED("modifyAttrs(" + e.getClass().getSimpleName() + ")");
        else if (etype == EntryType.IDENTITY)
            throw ServiceException.INVALID_REQUEST("must use Provisioning.modifyIdentity() instead", null);
        else if (etype == EntryType.DATASOURCE)
            throw ServiceException.INVALID_REQUEST("must use Provisioning.modifyDataSource() instead", null);
        else if (etype == EntryType.SIGNATURE)
        	throw ServiceException.INVALID_REQUEST("must use Provisioning.modifySignature() instead", null);

        // only tracking changes on account entries
        markChanged &= e instanceof Account;

        if (markChanged) {
            attrs.remove(A_offlineModifiedAttrs);

            List<String> modattrs = new ArrayList<String>();
            for (String attr : attrs.keySet()) {
                if (attr.startsWith("-") || attr.startsWith("+"))
                    attr = attr.substring(1);
                if (!modattrs.contains(attr) && !attr.toLowerCase().startsWith("offline") && !OfflineProvisioning.sOfflineAttributes.contains(attr))
                    modattrs.add(attr);
            }
            if (!modattrs.isEmpty()) {
                Map<String, Object> replacement = new HashMap<String, Object>(attrs.size() + 1);
                replacement.putAll(attrs);
                replacement.put('+' + A_offlineModifiedAttrs, modattrs.toArray(new String[modattrs.size()]));
                attrs = replacement;
            }
        }

        Map<String, Object> context = new HashMap<String, Object>();
        AttributeManager.getInstance().preModify(attrs, e, context, false, checkImmutable, allowCallback);

        if (etype == EntryType.ACCOUNT)
            revalidateRemoteLogin((OfflineAccount)e, attrs);

        if (etype == EntryType.CONFIG) {
            DbOfflineDirectory.modifyDirectoryEntry(etype, A_offlineDn, "config", attrs, false);
        } else {
            DbOfflineDirectory.modifyDirectoryEntry(etype, A_zimbraId, e.getAttr(A_zimbraId), attrs, markChanged);
            mHasDirtyAccounts |= markChanged;
        }
        reload(e);

        AttributeManager.getInstance().postModify(attrs, e, context, false, allowCallback);
    }

    private void revalidateRemoteLogin(OfflineAccount acct, Map<String, ? extends Object> changes) throws ServiceException {
        String password = acct.getAttr(A_offlineRemotePassword);
        String baseUri = acct.getAttr(A_offlineRemoteServerUri);
        
        String proxyHost = acct.getProxyHost();
        int proxyPort = acct.getProxyPort();
        String proxyUser = acct.getProxyUser();
        String proxyPass = acct.getProxyPass();
        
        boolean hasChange = false;
        for (Map.Entry<String, ? extends Object> change : changes.entrySet()) {
            String name = change.getKey();
            if (name.startsWith("-"))
                continue;
            else if (name.startsWith("+"))
                name = name.substring(1);

            if (name.equalsIgnoreCase(A_offlineRemotePassword)) {
                password = (String)change.getValue();
                hasChange = true;
            } else if (name.equalsIgnoreCase(A_offlineRemoteServerUri)) {
                baseUri = (String)change.getValue();
                hasChange = true;
            } else if (name.equalsIgnoreCase(A_offlineProxyHost)) {
                proxyHost = (String)change.getValue();
                hasChange = true;
	        } else if (name.equalsIgnoreCase(A_offlineProxyPort)) {
	        	proxyPort = 0;
	            String portStr = (String)change.getValue();
	            if (portStr != null && portStr.length() > 0) {
	            	try {
	            		proxyPort = Integer.parseInt(portStr);
	            	} catch (NumberFormatException x) {}
	            }
	            hasChange = true;
		    } else if (name.equalsIgnoreCase(A_offlineProxyUser)) {
		        proxyUser = (String)change.getValue();
		        hasChange = true;
			} else if (name.equalsIgnoreCase(A_offlineProxyPass)) {
			    proxyPass = (String)change.getValue();
			    hasChange = true;
			}
        }
        
        if (!hasChange) return;

        // fetch the mailbox; this will throw an exception if the username/password/URI are incorrect
        ZMailbox.Options options = new ZMailbox.Options(acct.getAttr(Provisioning.A_mail), AccountBy.name, password, Offline.getServerURI(baseUri, ZimbraServlet.USER_SERVICE_URI));
        newZMailbox(options, proxyHost, proxyPort, proxyUser, proxyPass);
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
        } else if (etype == EntryType.SIGNATURE && e instanceof OfflineSignature) {
            attrs = DbOfflineDirectory.readDirectoryLeaf(etype, ((OfflineSignature) e).getAccount(), A_zimbraId, e.getAttr(A_zimbraSignatureId));
            ((OfflineSignature) e).setName(e.getAttr(A_zimbraSignatureName));
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
    public synchronized List<MimeTypeInfo> getMimeTypes(String name) {
        List<MimeTypeInfo> mimeTypes = new ArrayList<MimeTypeInfo>();
        for (MimeTypeInfo mtinfo : mMimeTypes) {
            for (String type : mtinfo.getMimeTypes()) {
                if (type.equalsIgnoreCase(name))
                    mimeTypes.add(mtinfo);
            }
        }
        return mimeTypes;
    }

    @Override
    public synchronized List<MimeTypeInfo> getAllMimeTypes() {
        return mMimeTypes;
    }

    @Override
    public synchronized List<Zimlet> getObjectTypes() {
        return listAllZimlets();
    }

    static final Set<String> sOfflineAttributes = new HashSet<String>(Arrays.asList(new String[] { 
            A_zimbraId, A_mail, A_uid, A_objectClass, A_zimbraMailHost, A_displayName, A_sn, A_zimbraAccountStatus, A_zimbraPrefSkin, A_zimbraPrefClientType
    }));

    @Override
    public synchronized Account createAccount(String emailAddress, String password, Map<String, Object> attrs) throws ServiceException {
        if (attrs == null || !(attrs.get(A_offlineRemoteServerUri) instanceof String))
            throw ServiceException.FAILURE("need single offlineRemoteServerUri when creating account: " + emailAddress, null);

        String parts[] = emailAddress.split("@");
        if (parts.length != 2)
            throw ServiceException.INVALID_REQUEST("must be valid email address: " + emailAddress, null);
        String uid = parts[0];

        ZGetInfoResult zgi = newZMailbox(emailAddress, password, attrs, ZimbraServlet.USER_SERVICE_URI).getAccountInfo(false);
        
        attrs.put(A_offlineRemoteServerVersion, zgi.getVersion());
        OfflineLog.offline.info("Remote Zimbra Server Version: " + zgi.getVersion());

        for (Map.Entry<String,List<String>> zattr : zgi.getAttrs().entrySet())
            for (String value : zattr.getValue())
                addToMap(attrs, zattr.getKey(), value);
        for (Map.Entry<String,List<String>> zpref : zgi.getPrefAttrs().entrySet())
            for (String value : zpref.getValue())
                addToMap(attrs, zpref.getKey(), value);
        attrs.put(A_objectClass, new String[] { "organizationalPerson", "zimbraAccount" } );
        attrs.put(A_zimbraMailHost, "localhost");
        attrs.put(A_uid, uid);
        attrs.put(A_mail, emailAddress);
        attrs.put(A_zimbraId, zgi.getId());
        attrs.put(A_offlineRemotePassword, password);
        if (!(attrs.get(A_cn) instanceof String))
            attrs.put(A_cn, attrs.get(A_displayName) instanceof String ? (String) attrs.get(A_displayName) : uid);
        if (!(attrs.get(A_sn) instanceof String))
            attrs.put(A_sn, uid);
        if (!(attrs.get(A_zimbraAccountStatus) instanceof String))
            attrs.put(A_zimbraAccountStatus, ACCOUNT_STATUS_ACTIVE);

        attrs.remove(A_zimbraIsAdminAccount);
        attrs.remove(A_zimbraIsDomainAdminAccount);

        String[] skins = mLocalConfig.getMultiAttr(Provisioning.A_zimbraInstalledSkin);
        attrs.put(A_zimbraPrefSkin, skins == null || skins.length == 0 ? "sand" : skins[0]);
        
        attrs.put(A_zimbraPrefClientType, "advanced");

        Map<String,Object> immutable = new HashMap<String, Object>();
        for (String attr : AttributeManager.getInstance().getImmutableAttrs())
            if (attrs.containsKey(attr))
                immutable.put(attr, attrs.remove(attr));

        Map<String, Object> context = new HashMap<String, Object>();
        AttributeManager.getInstance().preModify(attrs, null, context, true, true);

        attrs.putAll(immutable);

        synchronized (this) {
            // create account entry in database
            DbOfflineDirectory.createDirectoryEntry(EntryType.ACCOUNT, emailAddress, attrs, false);
            Account acct = new OfflineAccount(emailAddress, zgi.getId(), attrs, mDefaultCos.getAccountDefaults());
            mAccountCache.put(acct);

            AttributeManager.getInstance().postModify(attrs, acct, context, true);

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
    
    public static final String LOCAL_ACCOUNT_UID = "local";
    public static final String LOCAL_ACCOUNT_NAME = LOCAL_ACCOUNT_UID + "@host.local";
    public static final String LOCAL_ACCOUNT_ID = "ffffffff-ffff-ffff-ffff-ffffffffffff";
    
    public static final String LOCAL_ACCOUNT_DISPLAYNAME = "My Mailbox";
    
    public synchronized Account getLocalAccount() throws ServiceException {
    	Account account = get(AccountBy.id, LOCAL_ACCOUNT_ID);
    	if (account != null)
    		return account;
    	return createLocalAccount();
    }
    
    public boolean isLocalAccount(Account account) {
    	return account.getId().equals(LOCAL_ACCOUNT_ID);
    }
    
    public static final String A_zimbraMailIdleSessionTimeout = "zimbraMailIdleSessionTimeout";
    public static final String A_zimbraPrefCalendarAlwaysShowMiniCal = "zimbraPrefCalendarAlwaysShowMiniCal";
    public static final String A_zimbraPrefCalendarApptReminderWarningTime = "zimbraPrefCalendarApptReminderWarningTime";
    public static final String A_zimbraPrefComposeInNewWindow = "zimbraPrefComposeInNewWindow";
    public static final String A_zimbraPrefContactsInitialView = "zimbraPrefContactsInitialView";
    public static final String A_zimbraPrefGalAutoCompleteEnabled = "zimbraPrefGalAutoCompleteEnabled";
    public static final String A_zimbraPrefHtmlEditorDefaultFontColor = "zimbraPrefHtmlEditorDefaultFontColor";
    public static final String A_zimbraPrefHtmlEditorDefaultFontFamily = "zimbraPrefHtmlEditorDefaultFontFamily";
    public static final String A_zimbraPrefHtmlEditorDefaultFontSize = "zimbraPrefHtmlEditorDefaultFontSize";
    
    public synchronized Account createLocalAccount() throws ServiceException {
    	Map<String, Object> attrs = new HashMap<String, Object>();

        attrs.put(A_objectClass, new String[] { "organizationalPerson", "zimbraAccount" } );
        attrs.put(A_zimbraMailHost, "localhost");
        attrs.put(A_uid, LOCAL_ACCOUNT_UID);
        attrs.put(A_mail, LOCAL_ACCOUNT_NAME);
        attrs.put(A_zimbraId, LOCAL_ACCOUNT_ID);
        attrs.put(A_cn, LOCAL_ACCOUNT_UID);
        attrs.put(A_sn, LOCAL_ACCOUNT_UID);
        attrs.put(A_displayName, LOCAL_ACCOUNT_DISPLAYNAME);
        attrs.put(A_zimbraAccountStatus, ACCOUNT_STATUS_ACTIVE);

        addToMap(attrs, A_zimbraAllowAnyFromAddress, TRUE);
        addToMap(attrs, A_zimbraAttachmentsBlocked, FALSE);
        addToMap(attrs, A_zimbraContactMaxNumEntries, "0");
        
        addToMap(attrs, A_zimbraFeatureAdvancedSearchEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureBriefcasesEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureCalendarEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureChangePasswordEnabled, FALSE);
        addToMap(attrs, A_zimbraFeatureContactsEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureConversationsEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureFiltersEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureFlaggingEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureGalAutoCompleteEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureGalEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureGroupCalendarEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureHtmlComposeEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureIMEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureIdentitiesEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureInitialSearchPreferenceEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureInstantNotify, TRUE);
        addToMap(attrs, A_zimbraFeatureMailEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureMailForwardingEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureNewMailNotificationEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureNotebookEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureOutOfOfficeReplyEnabled, TRUE);
        addToMap(attrs, A_zimbraFeaturePop3DataSourceEnabled, TRUE);
        addToMap(attrs, A_zimbraFeaturePortalEnabled, FALSE);
        addToMap(attrs, A_zimbraFeatureSavedSearchesEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureSharingEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureSkinChangeEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureTaggingEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureTasksEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureViewInHtmlEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureVoiceEnabled, FALSE);
        
        addToMap(attrs, A_zimbraLocale, "en_US");
        
        addToMap(attrs, A_zimbraMailIdleSessionTimeout, "0"); 
        addToMap(attrs, A_zimbraMailMessageLifetime, "0"); 
        addToMap(attrs, A_zimbraMailMinPollingInterval, "2m");
        addToMap(attrs, A_zimbraMailQuota, "0");
        addToMap(attrs, A_zimbraMailSpamLifetime, "30d");
        addToMap(attrs, A_zimbraMailTrashLifetime, "30d");
        
        addToMap(attrs, A_zimbraPasswordMaxLength, "64");
        addToMap(attrs, A_zimbraPasswordMinLength, "6");
        addToMap(attrs, A_zimbraPasswordMinLowerCaseChars, "0");
        addToMap(attrs, A_zimbraPasswordMinNumericChars, "0");
        addToMap(attrs, A_zimbraPasswordMinPunctuationChars, "0");
        addToMap(attrs, A_zimbraPasswordMinUpperCaseChars, "0");
        
        //addToMap(attrs, A_zimbraPortalName, "velodrome2");
        
        addToMap(attrs, A_zimbraPrefAutoAddAddressEnabled, TRUE);
        addToMap(attrs, A_zimbraPrefCalendarAlwaysShowMiniCal, FALSE);
        addToMap(attrs, A_zimbraPrefCalendarApptReminderWarningTime, "5");
        addToMap(attrs, A_zimbraPrefCalendarFirstDayOfWeek, "0");
        addToMap(attrs, A_zimbraPrefCalendarInitialView, "workWeek");
        addToMap(attrs, A_zimbraPrefCalendarNotifyDelegatedChanges, FALSE);
        addToMap(attrs, A_zimbraPrefCalendarUseQuickAdd, TRUE);
        addToMap(attrs, A_zimbraPrefComposeFormat, "text");
        addToMap(attrs, A_zimbraPrefComposeInNewWindow, FALSE);
        addToMap(attrs, A_zimbraPrefContactsInitialView, "list");
        addToMap(attrs, A_zimbraPrefContactsPerPage, "25");
        addToMap(attrs, A_zimbraPrefDedupeMessagesSentToSelf, "dedupeNone");
        addToMap(attrs, A_zimbraPrefForwardIncludeOriginalText, "includeBody");
        addToMap(attrs, A_zimbraPrefForwardReplyInOriginalFormat, FALSE);
        addToMap(attrs, A_zimbraPrefForwardReplyPrefixChar, ">");
        addToMap(attrs, A_zimbraPrefFromDisplay, LOCAL_ACCOUNT_NAME);
        addToMap(attrs, A_zimbraPrefGalAutoCompleteEnabled, FALSE);
        addToMap(attrs, A_zimbraPrefGroupMailBy, "conversation");
        addToMap(attrs, A_zimbraPrefHtmlEditorDefaultFontColor, "#000000");
        addToMap(attrs, A_zimbraPrefHtmlEditorDefaultFontFamily, "Arial");
        addToMap(attrs, A_zimbraPrefHtmlEditorDefaultFontSize, "10pt");
        addToMap(attrs, A_zimbraPrefIMAutoLogin, TRUE);
        addToMap(attrs, A_zimbraPrefImapSearchFoldersEnabled, TRUE);
        addToMap(attrs, A_zimbraPrefInboxUnreadLifetime, "0");
        addToMap(attrs, A_zimbraPrefIncludeSpamInSearch, FALSE);
        addToMap(attrs, A_zimbraPrefIncludeTrashInSearch, FALSE);
        addToMap(attrs, A_zimbraPrefLocale, "en_US");
        addToMap(attrs, A_zimbraPrefMailInitialSearch, "in:inbox");
        addToMap(attrs, A_zimbraPrefMailItemsPerPage, "50");
        addToMap(attrs, A_zimbraPrefMailPollingInterval, "60");
        addToMap(attrs, A_zimbraPrefMailSignatureEnabled, FALSE);
        addToMap(attrs, A_zimbraPrefMailSignatureStyle, "outlook");
        addToMap(attrs, A_zimbraPrefMessageViewHtmlPreferred, TRUE);
        addToMap(attrs, A_zimbraPrefReadingPaneEnabled, TRUE);
        addToMap(attrs, A_zimbraPrefReplyIncludeOriginalText, "includeBody");
        addToMap(attrs, A_zimbraPrefSaveToSent, TRUE);
        addToMap(attrs, A_zimbraPrefSentLifetime, "0");
        addToMap(attrs, A_zimbraPrefSentMailFolder, "sent");
        addToMap(attrs, A_zimbraPrefShowFragments, TRUE);
        addToMap(attrs, A_zimbraPrefShowSearchString, TRUE);
        addToMap(attrs, A_zimbraPrefUseKeyboardShortcuts, TRUE);
        addToMap(attrs, A_zimbraPrefUseRfc2231, FALSE);
        addToMap(attrs, A_zimbraPrefUseTimeZoneListInCalendar, FALSE);
        
        addToMap(attrs, A_zimbraZimletAvailableZimlets, "com_zimbra_date");
        addToMap(attrs, A_zimbraZimletAvailableZimlets, "com_zimbra_email");
        addToMap(attrs, A_zimbraZimletAvailableZimlets, "com_zimbra_html");
        addToMap(attrs, A_zimbraZimletAvailableZimlets, "com_zimbra_phone");
        addToMap(attrs, A_zimbraZimletAvailableZimlets, "com_zimbra_search");
        addToMap(attrs, A_zimbraZimletAvailableZimlets, "com_zimbra_url");
        
        String[] skins = mLocalConfig.getMultiAttr(Provisioning.A_zimbraInstalledSkin);
        attrs.put(A_zimbraPrefSkin, skins == null || skins.length == 0 ? "sand" : skins[0]);
        
        attrs.put(A_zimbraPrefClientType, "advanced");
        
        addToMap(attrs, A_zimbraIsAdminAccount, TRUE);
        addToMap(attrs, A_zimbraIsDomainAdminAccount, TRUE);
        
        Map<String,Object> immutable = new HashMap<String, Object>();
        for (String attr : AttributeManager.getInstance().getImmutableAttrs())
            if (attrs.containsKey(attr))
                immutable.put(attr, attrs.remove(attr));

        Map<String, Object> context = new HashMap<String, Object>();
        AttributeManager.getInstance().preModify(attrs, null, context, true, true);

        attrs.putAll(immutable);

        synchronized (this) {
            // create account entry in database
            DbOfflineDirectory.createDirectoryEntry(EntryType.ACCOUNT, LOCAL_ACCOUNT_NAME, attrs, false);
            Account acct = new OfflineAccount(LOCAL_ACCOUNT_NAME, LOCAL_ACCOUNT_ID, attrs, mDefaultCos.getAccountDefaults());
            mAccountCache.put(acct);

            AttributeManager.getInstance().postModify(attrs, acct, context, true);

            try {
                Mailbox mbox = MailboxManager.getInstance().getMailboxByAccount(acct);
            } catch (ServiceException e) {
                OfflineLog.offline.error("error initializing account " + LOCAL_ACCOUNT_NAME, e);
                mAccountCache.remove(acct);
                deleteAccount(LOCAL_ACCOUNT_ID);
                throw e;
            }

            return acct;
        }
    }
    
    public static String getSanitizedValue(String key, String value) throws ServiceException {
    	if (value == null) {
    		return null;
    	}
    	if (key.equalsIgnoreCase(A_offlineRemotePassword)) {
    		return encryptData(value);
    	}
    	return value;
    }

    public static void addToMap(Map<String,Object> attrs, String key, String value) {
    	if (value != null && key.equalsIgnoreCase(A_offlineRemotePassword)) {
    		try {
    			value = decryptData(value);
    		} catch (ServiceException x) {
    			OfflineLog.offline.warn("Can't decrypt remote password");
    		}
    	}
    	
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
        	if (key.equals(LOCAL_ACCOUNT_NAME))
        		return getLocalAccount();
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

        acct = new OfflineAccount((String) attrs.get(A_mail), (String) attrs.get(A_zimbraId), attrs, mDefaultCos.getAccountDefaults());
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
        	if (!zimbraId.equals(LOCAL_ACCOUNT_ID)) {
	            Account acct = get(AccountBy.id, zimbraId);
	            if (acct != null)
	                accts.add(acct);
        	}
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
        	if (!zimbraId.equals(LOCAL_ACCOUNT_ID)) {
	            Account acct = get(AccountBy.id, zimbraId);
	            if (acct != null && !((OfflineAccount)acct).isLocal())
	                dirty.add(acct);
        	}
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
    	//allow login without valid password
    	ZimbraLog.security.info(ZimbraLog.encodeAttrs(new String[] {"cmd", "Auth", "account", acct.getName(), "protocol", proto}));
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
    public synchronized void checkPasswordStrength(Account acct, String password) throws ServiceException {
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
        return null;
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
        if (service == null || service.equalsIgnoreCase("mailbox"))
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

        Map<String, Object> context = new HashMap<String, Object>();
        AttributeManager.getInstance().preModify(attrs, null, context, true, true);
        if (!(attrs.get(A_zimbraId) instanceof String))
            attrs.put(A_zimbraId, UUID.randomUUID().toString());
        attrs.put(A_cn, name);
        attrs.put(A_objectClass, "zimbraZimletEntry");
        attrs.put(A_zimbraZimletEnabled, FALSE);
        attrs.put(A_zimbraZimletIndexingEnabled, attrs.containsKey(A_zimbraZimletKeyword) ? TRUE : FALSE);

        DbOfflineDirectory.createDirectoryEntry(EntryType.ZIMLET, name, attrs, false);
        Zimlet zimlet = new OfflineZimlet(name, (String) attrs.get(A_zimbraId), attrs);
        mZimlets.put(name, zimlet);
        AttributeManager.getInstance().postModify(attrs, zimlet, context, true);
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
        if (d == null || d.getAttr(A_zimbraDomainName) == null) {
        	return getAllAccounts();
        }
        
        List<Account> accts = new ArrayList<Account>();
        List<String> ids = DbOfflineDirectory.searchDirectoryEntries(EntryType.ACCOUNT, A_offlineDn, "%@" + d.getAttr(A_zimbraDomainName));
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

    public List<NamedEntry> searchDirectory(SearchOptions options) throws ServiceException {
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

        for (String key : attrs.keySet()) {
            if (key.startsWith("+") || key.startsWith("-"))
                key = key.substring(1);
            if (!validAttrs.contains(key.toLowerCase()))
                throw ServiceException.INVALID_REQUEST("unable to modify attr: " + key, null);
        }
    }

    @Override
    public synchronized Identity createIdentity(Account account, String name, Map<String, Object> attrs) throws ServiceException {
        return createIdentity(account, name, attrs, !isLocalAccount(account));
    }

    synchronized Identity createIdentity(Account account, String name, Map<String, Object> attrs, boolean markChanged) throws ServiceException {
        if (name.equalsIgnoreCase(DEFAULT_IDENTITY_NAME))
            throw AccountServiceException.IDENTITY_EXISTS(name);

        List<Identity> existing = getAllIdentities(account);
        if (existing.size() >= account.getLongAttr(A_zimbraIdentityMaxNumEntries, 20))
            throw AccountServiceException.TOO_MANY_IDENTITIES();

        attrs.remove(A_offlineModifiedAttrs);

        if (!(attrs.get(A_zimbraPrefIdentityId) instanceof String))
            attrs.put(A_zimbraPrefIdentityId, UUID.randomUUID().toString());
        String identId = (String) attrs.get(A_zimbraPrefIdentityId);
        attrs.put(A_zimbraPrefIdentityName, name);
        attrs.put(A_objectClass, "zimbraIdentity");
        if (markChanged)
            attrs.put(A_offlineModifiedAttrs, A_offlineDn);

        Map<String,Object> immutable = new HashMap<String, Object>();
        for (String attr : AttributeManager.getInstance().getImmutableAttrs())
            if (attrs.containsKey(attr))
                immutable.put(attr, attrs.remove(attr));

        Map<String, Object> context = new HashMap<String, Object>();
        AttributeManager.getInstance().preModify(attrs, null, context, true, true);

        attrs.putAll(immutable);

        DbOfflineDirectory.createDirectoryLeaf(EntryType.IDENTITY, account, name, identId, attrs, markChanged);
        Identity identity = new OfflineIdentity(account, name, attrs);
        mHasDirtyAccounts |= markChanged;

        AttributeManager.getInstance().postModify(attrs, identity, context, true);
        return identity;
    }

    @Override
    public synchronized void deleteIdentity(Account account, String name) throws ServiceException {
        deleteIdentity(account, name, !isLocalAccount(account));
    }

    synchronized void deleteIdentity(Account account, String name, boolean markChanged) throws ServiceException {
        if (name.equalsIgnoreCase(DEFAULT_IDENTITY_NAME))
            throw ServiceException.INVALID_REQUEST("can't delete default identity", null);

        Identity ident = get(account, IdentityBy.name, name);
        if (ident == null)
            return;

        DbOfflineDirectory.deleteDirectoryLeaf(EntryType.IDENTITY, account, ident.getId(), markChanged);
        reload(account);
        mHasDirtyAccounts |= markChanged;
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
        modifyIdentity(account, name, attrs, !isLocalAccount(account));
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

        if (markChanged) {
            attrs.remove(A_offlineModifiedAttrs);

            List<String> modattrs = new ArrayList<String>();
            for (String attr : attrs.keySet()) {
                if (attr.startsWith("-") || attr.startsWith("+"))
                    attr = attr.substring(1);
                if (!modattrs.contains(attr) && !attr.toLowerCase().startsWith("offline") && !OfflineProvisioning.sOfflineAttributes.contains(attr))
                    modattrs.add(attr);
            }
            if (!modattrs.isEmpty())
                attrs.put('+' + A_offlineModifiedAttrs, modattrs.toArray(new String[modattrs.size()]));
        }

        String newName = (String) attrs.get(A_zimbraPrefIdentityName);
        if (newName == null)
            newName = (String) attrs.get('+' + A_zimbraPrefIdentityName);

        Map<String, Object> context = new HashMap<String, Object>();
        AttributeManager.getInstance().preModify(attrs, identity, context, false, true, true);

        DbOfflineDirectory.modifyDirectoryLeaf(EntryType.IDENTITY, account, A_offlineDn, name, attrs, markChanged, newName);
        reload(identity);
        mHasDirtyAccounts |= markChanged;

        AttributeManager.getInstance().postModify(attrs, identity, context, false, true);
    }

    @Override
    public synchronized Identity get(Account account, IdentityBy keyType, String key) throws ServiceException {
    	if (key == null) return null;
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
        if (attrs == null && keyType == IdentityBy.id) {
    		//HACK: try DataSource as well
    		DataSource ds = get(account, DataSourceBy.id, key);
    		if (ds != null) {
    			attrs = ds.getAttrs();
    			attrs.put(A_zimbraPrefIdentityId, ds.getId());
    			attrs.put(A_zimbraPrefIdentityName, ds.getName());
    		}
        }
        if (attrs == null)
        	return null;

        return new OfflineIdentity(account, (String) attrs.get(A_zimbraPrefIdentityName), attrs);
    }

    private static void validateSignatureAttrs(Map<String, Object> attrs) throws ServiceException {
        Set<String> validAttrs = AttributeManager.getInstance().getLowerCaseAttrsInClass(AttributeClass.signature);
        validAttrs.add(A_objectClass.toLowerCase());

        for (String key : attrs.keySet()) {
            if (key.startsWith("+") || key.startsWith("-"))
                key = key.substring(1);
            if (!validAttrs.contains(key.toLowerCase()) && !key.equalsIgnoreCase(A_offlineModifiedAttrs))
                throw ServiceException.INVALID_REQUEST("unable to modify attr: " + key, null);
        }
    }
    
    @Override
    public synchronized Signature createSignature(Account account, String signatureName, Map<String, Object> attrs) throws ServiceException {
    	return createSignature(account, signatureName, attrs, !isLocalAccount(account));
    }
    
    synchronized Signature createSignature(Account account, String signatureName, Map<String, Object> attrs, boolean markChanged) throws ServiceException {
        validateSignatureAttrs(attrs);
        
        boolean setAsDefault = false;
        List<Signature> existing = getAllSignatures(account);
        int numSigs = existing.size();
        if (numSigs >= account.getLongAttr(A_zimbraSignatureMaxNumEntries, 20))
            throw AccountServiceException.TOO_MANY_SIGNATURES();
        else if (numSigs == 0)
            setAsDefault = true;
        
        String signatureId = (String)attrs.get(Provisioning.A_zimbraSignatureId);
        if (signatureId == null) {
            signatureId = UUID.randomUUID().toString();
            attrs.put(Provisioning.A_zimbraSignatureId, signatureId);
        }
        attrs.put(Provisioning.A_zimbraSignatureName, signatureName);
        attrs.put(A_objectClass, "zimbraSignature");
        
        if (markChanged)
            attrs.put(A_offlineModifiedAttrs, A_offlineDn);

        Map<String,Object> immutable = new HashMap<String, Object>();
        for (String attr : AttributeManager.getInstance().getImmutableAttrs())
            if (attrs.containsKey(attr))
                immutable.put(attr, attrs.remove(attr));

        Map<String, Object> context = new HashMap<String, Object>();
        AttributeManager.getInstance().preModify(attrs, null, context, true, true);

        attrs.putAll(immutable);

        DbOfflineDirectory.createDirectoryLeaf(EntryType.SIGNATURE, account, signatureName, signatureId, attrs, markChanged);
        Signature signature = get(account, SignatureBy.id, signatureId);
        mHasDirtyAccounts |= markChanged;

        AttributeManager.getInstance().postModify(attrs, signature, context, true);
        
        if (setAsDefault && markChanged) {
        	setDefaultSignature(account, signatureId);
        }
        
        return signature;
    }
    
    private void setDefaultSignature(Account acct, String signatureId) throws ServiceException {
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(Provisioning.A_zimbraPrefDefaultSignatureId, signatureId);
        modifyAttrs(acct, attrs, false, true, false); //always let server set default
    }
    
    private String getDefaultSignature(Account acct) {
        return acct.getAttr(Provisioning.A_zimbraPrefDefaultSignatureId);
    }
    
    private void removeDefaultSignature(Account acct) throws ServiceException {
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put('-' + Provisioning.A_zimbraPrefDefaultSignatureId, null);
        modifyAttrs(acct, attrs, false, true, false); //always let server set default
    }
    
    @Override
    public synchronized void modifySignature(Account account, String signatureId, Map<String, Object> attrs) throws ServiceException {
        modifySignature(account, signatureId, attrs, !isLocalAccount(account));
    }
    
    synchronized void modifySignature(Account account, String signatureId, Map<String, Object> attrs, boolean markChanged) throws ServiceException {
        validateSignatureAttrs(attrs);

    	Signature signature = get(account, SignatureBy.id, signatureId);
        if (signature == null)
            throw AccountServiceException.NO_SUCH_SIGNATURE(signatureId);
        
        if (markChanged) {
            attrs.remove(A_offlineModifiedAttrs);

            List<String> modattrs = new ArrayList<String>();
            for (String attr : attrs.keySet()) {
                if (attr.startsWith("-") || attr.startsWith("+"))
                    attr = attr.substring(1);
                if (!modattrs.contains(attr) && !attr.toLowerCase().startsWith("offline") && !OfflineProvisioning.sOfflineAttributes.contains(attr))
                    modattrs.add(attr);
            }
            if (!modattrs.isEmpty())
                attrs.put('+' + A_offlineModifiedAttrs, modattrs.toArray(new String[modattrs.size()]));
        }

        String newName = (String) attrs.get(A_zimbraSignatureName);
        if (newName!= null) {
        	if (newName.equals(signature.getName())) {
        		newName = null; //no need to update
        	} else if (newName.length() == 0) {
        		throw ServiceException.INVALID_REQUEST("empty signature name is not allowed", null); //can't be empty
        	}
        }

        Map<String, Object> context = new HashMap<String, Object>();
        AttributeManager.getInstance().preModify(attrs, signature, context, false, true);

        DbOfflineDirectory.modifyDirectoryLeaf(EntryType.SIGNATURE, account, Provisioning.A_zimbraId, signatureId, attrs, markChanged, newName);
        reload(signature);
        mHasDirtyAccounts |= markChanged;

        AttributeManager.getInstance().postModify(attrs, signature, context, false, true);
    }
    
    @Override
    public synchronized void deleteSignature(Account account, String signatureId) throws ServiceException {
        deleteSignature(account, signatureId, !isLocalAccount(account));
    }
    
    synchronized void deleteSignature(Account account, String signatureId, boolean markChanged) throws ServiceException {
    	Signature signature = get(account, SignatureBy.id, signatureId);
        if (signature == null) return;

        DbOfflineDirectory.deleteDirectoryLeaf(EntryType.SIGNATURE, account, signatureId, markChanged);
        reload(account);
        mHasDirtyAccounts |= markChanged;
        
        if (signatureId.equals(getDefaultSignature(account))) {
        	List<String> names = DbOfflineDirectory.listAllDirectoryLeaves(EntryType.SIGNATURE, account);
        	if (markChanged) {
	        	if (names.size() > 0) {
	        		setDefaultSignature(account, names.get(0)); //just randomly set to whatever comes next
	        	} else {
	        		removeDefaultSignature(account);
	        	}
        	}
        }
    }
    
    @Override
    public synchronized List<Signature> getAllSignatures(Account account) throws ServiceException {
        List<String> names = DbOfflineDirectory.listAllDirectoryLeaves(EntryType.SIGNATURE, account);
        List<Signature> signatures = new ArrayList<Signature>(names.size());
        for (String name : names)
        	signatures.add(get(account, SignatureBy.name, name));
        return signatures;
    }
    
    @Override
    public synchronized Signature get(Account account, SignatureBy keyType, String key) throws ServiceException {
    	if (key == null) return null;
        Map<String,Object> attrs = null;
        if (keyType == SignatureBy.name) {
            attrs = DbOfflineDirectory.readDirectoryLeaf(EntryType.SIGNATURE, account, A_offlineDn, key);
        } else if (keyType == SignatureBy.id) {
            attrs = DbOfflineDirectory.readDirectoryLeaf(EntryType.SIGNATURE, account, A_zimbraId, key);
        }
        if (attrs == null)
            return null;

        return new OfflineSignature(account, attrs);
    }
    
    @Override
    public synchronized DataSource createDataSource(Account account, DataSource.Type type, String name, Map<String, Object> attrs) throws ServiceException {
        return createDataSource(account, type, name, attrs, false, !isLocalAccount(account));
    }

    @Override
    public DataSource createDataSource(Account account, DataSource.Type type, String name, Map<String, Object> attrs, boolean passwdAlreadyEncrypted) throws ServiceException {
        return createDataSource(account, type, name, attrs, passwdAlreadyEncrypted, !isLocalAccount(account));
    }

    synchronized DataSource createDataSource(Account account, DataSource.Type type, String name, Map<String, Object> attrs, boolean passwdAlreadyEncrypted, boolean markChanged)
    throws ServiceException {
        List<DataSource> existing = getAllDataSources(account);
        if (existing.size() >= account.getLongAttr(A_zimbraDataSourceMaxNumEntries, 20))
            throw AccountServiceException.TOO_MANY_DATA_SOURCES();
        
        attrs.remove(A_offlineModifiedAttrs);

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

        if (isLocalAccount(account)) {
            DataSource testDs = new OfflineDataSource(account, type, name, dsid, attrs);
            String error = DataSourceManager.test(testDs);
            if (error != null)
            	throw ServiceException.FAILURE(error, null);
        	
	        String folderId = (String)attrs.get(A_zimbraDataSourceFolderId);
	        if (folderId == null) {
		        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccount(account);
		        OperationContext context = new OperationContext(mbox);
		        //Folder importedRoot = mbox.getFolderByPath(context, LocalMailbox.IMPORT_ROOT_PATH);
		        String newFolderName = Folder.normalizeItemName(name);
		        synchronized (mbox) {
		        	Folder newFolder = null;
		        	try {
		        		newFolder = mbox.createFolder(context, newFolderName, Mailbox.ID_FOLDER_USER_ROOT, MailItem.TYPE_UNKNOWN, 0, (byte)0, null);
		        	} catch (MailServiceException x) {
		        		if (x.getCode().equals(MailServiceException.ALREADY_EXISTS)) {
		        	        String uuid = '{' + UUID.randomUUID().toString() + '}';
		        	        if (newFolderName.length() + uuid.length() > MailItem.MAX_NAME_LENGTH)
		        	            newFolderName = newFolderName.substring(0, MailItem.MAX_NAME_LENGTH - uuid.length()) + uuid;
		        	        else
		        	            newFolderName += uuid;
		        	        newFolder = mbox.createFolder(context, newFolderName, Mailbox.ID_FOLDER_USER_ROOT, MailItem.TYPE_UNKNOWN, 0, (byte)0, null);
		        		} else
		        			throw x;
		        	}
		        	attrs.put(A_zimbraDataSourceFolderId, Integer.toString(newFolder.getId()));
		        }
	        }
        }

        Map<String,Object> immutable = new HashMap<String, Object>();
        for (String attr : AttributeManager.getInstance().getImmutableAttrs())
            if (attrs.containsKey(attr))
                immutable.put(attr, attrs.remove(attr));

        Map<String, Object> context = new HashMap<String, Object>();
        AttributeManager.getInstance().preModify(attrs, null, context, true, true);

        attrs.putAll(immutable);

        DbOfflineDirectory.createDirectoryLeaf(EntryType.DATASOURCE, account, name, dsid, attrs, markChanged);
        DataSource ds = new OfflineDataSource(account, type, name, dsid, attrs);
        mHasDirtyAccounts |= markChanged;

        AttributeManager.getInstance().postModify(attrs, ds, context, true);
        return ds;
    }

    @Override
    public synchronized void deleteDataSource(Account account, String dataSourceId) throws ServiceException {
        deleteDataSource(account, dataSourceId, !isLocalAccount(account));
    }

    synchronized void deleteDataSource(Account account, String dataSourceId, boolean markChanged) throws ServiceException {
        DataSource dsrc = get(account, DataSourceBy.id, dataSourceId);
        if (dsrc == null)
            return;

        DbOfflineDirectory.deleteDirectoryLeaf(EntryType.DATASOURCE, account, dsrc.getId(), markChanged);
        reload(account);
        mHasDirtyAccounts |= markChanged;
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
        modifyDataSource(account, dataSourceId, attrs, !isLocalAccount(account));
    }

    synchronized void modifyDataSource(Account account, String dataSourceId, Map<String, Object> attrs, boolean markChanged) throws ServiceException {
        DataSource ds = get(account, DataSourceBy.id, dataSourceId);
        if (ds == null)
            throw AccountServiceException.NO_SUCH_DATA_SOURCE(dataSourceId);

        if (markChanged) {
            attrs.remove(A_offlineModifiedAttrs);

            List<String> modattrs = new ArrayList<String>();
            for (String attr : attrs.keySet()) {
                if (attr.startsWith("-") || attr.startsWith("+"))
                    attr = attr.substring(1);
                if (!modattrs.contains(attr) && !attr.toLowerCase().startsWith("offline") && !OfflineProvisioning.sOfflineAttributes.contains(attr))
                    modattrs.add(attr);
            }
            if (!modattrs.isEmpty())
                attrs.put('+' + A_offlineModifiedAttrs, modattrs.toArray(new String[modattrs.size()]));
        }

        String newName = (String) attrs.get(A_zimbraDataSourceName);
        if (newName == null)
            newName = (String) attrs.get('+' + A_zimbraDataSourceName);

        if (attrs.get(A_zimbraDataSourcePassword) instanceof String)
            attrs.put(A_zimbraDataSourcePassword, DataSource.encryptData(dataSourceId, (String) attrs.get(A_zimbraDataSourcePassword)));

        if (isLocalAccount(account)) {
	        DataSource testDs = new OfflineDataSource(account, ds.getType(), ds.getName(), ds.getId(), attrs);
	        String error = DataSourceManager.test(testDs);
	        if (error != null)
	        	throw ServiceException.FAILURE(error, null);
        }
        
        Map<String, Object> context = new HashMap<String, Object>();
        AttributeManager.getInstance().preModify(attrs, ds, context, false, true, true);

        DbOfflineDirectory.modifyDirectoryLeaf(EntryType.DATASOURCE, account, A_zimbraId, dataSourceId, attrs, markChanged, newName);
        reload(ds);
        mHasDirtyAccounts |= markChanged;

        AttributeManager.getInstance().postModify(attrs, ds, context, false, true);
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
    
    @Override
    public void flushCache(CacheEntryType type, CacheEntry[] entries) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("flushCache");
    }
}
