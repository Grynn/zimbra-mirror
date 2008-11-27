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

import com.sun.mail.smtp.SMTPTransport;
import com.zimbra.common.auth.ZAuthToken;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.RemoteServiceException;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.CustomTrustManager;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.SystemUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.*;
import com.zimbra.cs.account.NamedEntry.Visitor;
import com.zimbra.cs.account.accesscontrol.RightCommand;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.db.DbOfflineDirectory;
import com.zimbra.cs.mailbox.LocalJMSession;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.mime.MimeTypeInfo;
import com.zimbra.cs.offline.Offline;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.OfflineCalDavDataImport;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.util.OfflineUtil;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.servlet.ZimbraServlet;
import com.zimbra.cs.zclient.ZGetInfoResult;
import com.zimbra.cs.zclient.ZIdentity;
import com.zimbra.cs.zclient.ZMailbox;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.util.*;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.auth.login.LoginException;

public class OfflineProvisioning extends Provisioning implements OfflineConstants {

    public static final String A_offlineDn = "offlineDn";
    public static final String A_offlineModifiedAttrs = "offlineModifiedAttrs";
    public static final String A_offlineDeletedIdentity = "offlineDeletedIdentity";
    public static final String A_offlineDeletedDataSource = "offlineDeletedDataSource";
    public static final String A_offlineDeletedSignature = "offlineDeletedSignature";
    public static final String A_offlineMountpointProxyAccountId = "offlineMountpointProxyAccountId";
    public static final String A_offlineMountpointAccountIds = "offlineMountpointAccountIds";
    public static final String A_zimbraPrefMailtoHandlerEnabled = "zimbraPrefMailtoHandlerEnabled";
    public static final String A_zimbraPrefMailtoAccountId = "zimbraPrefMailtoAccountId";
    public static final String A_zimbraPrefMailToasterEnabled = "zimbraPrefMailToasterEnabled";
    public static final String A_zimbraPrefCalendarToasterEnabled = "zimbraPrefCalendarToasterEnabled";

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

    
    private final OfflineConfig mLocalConfig;
    private final Server mLocalServer;
    private final Cos mDefaultCos;
    private final List<MimeTypeInfo> mMimeTypes;
    private final Map<String, Zimlet> mZimlets;
    private final NamedEntryCache<Account> mAccountCache;
    private final Map<String, Server> mSyncServerCache; 

    private boolean mHasDirtyAccounts = true;

    public OfflineProvisioning() {
        mLocalConfig  = OfflineConfig.instantiate(this);
        mLocalServer  = OfflineLocalServer.instantiate(mLocalConfig, this);
        mDefaultCos   = OfflineCos.instantiate(this);
        mMimeTypes    = OfflineMimeType.instantiateAll();
        mZimlets      = OfflineZimlet.instantiateAll(this);
        mSyncServerCache = new HashMap<String, Server>();
        mAccountCache = new NamedEntryCache<Account>(LC.ldap_cache_account_maxsize.intValue(), LC.ldap_cache_account_maxage.intValue() * Constants.MILLIS_PER_MINUTE);
    }
    
    public ZMailbox newZMailbox(OfflineAccount account, String serviceUri) throws ServiceException {
    	ZMailbox.Options options;
    	String uri = Offline.getServerURI(account, serviceUri);
    	ZAuthToken authToken = OfflineSyncManager.getInstance().lookupAuthToken(account);
    	if (authToken != null) {
    		options = new ZMailbox.Options(authToken, uri);
    	} else {
    		options = new ZMailbox.Options(account.getAttr(Provisioning.A_mail), AccountBy.name, account.getAttr(A_offlineRemotePassword), uri);
    	}
        options.setDebugListener(new Offline.OfflineDebugListener(account));
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
        options.setDebugListener(new Offline.OfflineDebugListener());
    	
    	return newZMailbox(options, proxyHost, proxyPort, proxyUser, proxyPass);
    }
    
    private ZMailbox newZMailbox(ZMailbox.Options options, String proxyHost, int proxyPort, String proxyUser, String proxyPass) throws ServiceException {
    	options.setRequestProtocol(SoapProtocol.Soap12);
    	options.setResponseProtocol(SoapProtocol.Soap12);
        options.setProxy(proxyHost, proxyPort, proxyUser, proxyPass);
        options.setNoSession(true);
        options.setUserAgent(OfflineLC.zdesktop_name.value(), OfflineLC.getFullVersion());
        options.setTimeout(OfflineLC.zdesktop_request_timeout.intValue());
        options.setRetryCount(1);
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
        modifyAttrs(e, attrs, checkImmutable, allowCallback, e instanceof Account && isSyncAccount((Account)e));
    }

    @SuppressWarnings("unchecked")
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

        if (allowCallback && e instanceof Account && isLocalAccount((Account)e) && attrs.containsKey(A_offlineAccountsOrder))
        	((Map<String, Object>)attrs).put(A_offlineAccountsOrder, promoteAccount((String)attrs.get(A_offlineAccountsOrder)));
        
        // only tracking changes on account entries
        markChanged &= e instanceof OfflineAccount;

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
    
    /*
     * Special way to set a single Account attribute that doesn't mark the account dirty.
     */
    public void setAccountAttribute(Account account, String key, Object value) throws ServiceException {
    	Map<String, Object> attrs = new HashMap<String, Object>(1);
    	attrs.put(key, value);
    	modifyAttrs(account, attrs, false, true, false);
    }
    
    /*
     * Special way to set a single attribute of a DataSource that doesn't mark the account dirty.
     */
    public void setDataSourceAttribute(DataSource ds, String key, Object value) throws ServiceException {
    	Map<String, Object> attrs = new HashMap<String, Object>(1);
    	attrs.put(key, value);
    	modifyDataSource(ds.getAccount(), ds.getId(), attrs, false);
    }
    
    private void checkForSSLCertAlias(Map<String, ? extends Object> attrs) throws ServiceException {
    	String sslCertAlias = (String)attrs.remove(OfflineConstants.A_offlineSslCertAlias);
    	if (sslCertAlias != null) {
    		try {
    			CustomTrustManager.getInstance().acceptCertificates(sslCertAlias);
    		} catch (GeneralSecurityException x) {
    			throw RemoteServiceException.SSLCERT_NOT_ACCEPTED(x.getMessage(), x);
    		}
    	}
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
                String newPassword = (String)change.getValue();
                hasChange |= !password.equals(newPassword);
                password = newPassword;
            } else if (name.equalsIgnoreCase(A_offlineRemoteServerUri)) {
                String newBaseUri = (String)change.getValue();
                hasChange |= !baseUri.equals(newBaseUri);
                baseUri = newBaseUri;
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
        
        checkForSSLCertAlias(changes);

        // fetch the mailbox; this will throw an exception if the username/password/URI are incorrect
        ZMailbox.Options options = new ZMailbox.Options(acct.getAttr(Provisioning.A_mail), AccountBy.name, password, Offline.getServerURI(baseUri, ZimbraServlet.USER_SERVICE_URI));
        newZMailbox(options, proxyHost, proxyPort, proxyUser, proxyPass);
    }

    @Override
    public synchronized void reload(Entry e) throws ServiceException {
        EntryType etype = EntryType.typeForEntry(e);
        if (etype == null)
            throw OfflineServiceException.UNSUPPORTED("reload(" + e.getClass().getSimpleName() + ")");

        Map<String,Object> attrs;
        if (etype == EntryType.IDENTITY && e instanceof OfflineIdentity) {
            attrs = DbOfflineDirectory.readDirectoryLeaf(etype, ((OfflineIdentity) e).getAccount(), A_zimbraId, e.getAttr(A_zimbraPrefIdentityId));
            ((OfflineIdentity) e).setName(e.getAttr(A_zimbraPrefIdentityName));
        } else if (etype == EntryType.DATASOURCE && e instanceof OfflineDataSource) {
            attrs = DbOfflineDirectory.readDirectoryLeaf(etype, ((OfflineDataSource) e).getAccount(), A_zimbraId, e.getAttr(A_zimbraDataSourceId));
            ((OfflineDataSource) e).setName(e.getAttr(A_zimbraDataSourceName));
            ((OfflineDataSource) e).setServiceName(e.getAttr(OfflineConstants.A_zimbraDataSourceDomain));
        } else if (etype == EntryType.SIGNATURE && e instanceof OfflineSignature) {
            attrs = DbOfflineDirectory.readDirectoryLeaf(etype, ((OfflineSignature) e).getAccount(), A_zimbraId, e.getAttr(A_zimbraSignatureId));
            ((OfflineSignature) e).setName(e.getAttr(A_zimbraSignatureName));
        } else if (etype == EntryType.CONFIG) {
        	attrs = OfflineConfig.instantiate(this).getAttrs();
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
    public synchronized GlobalGrant getGlobalGrant() throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("getGlobalGrant");
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

    static final Set<String> sOfflineAttributes = new HashSet<String>(Arrays.asList( 
            A_zimbraId,
            A_mail,
            A_uid,
            A_objectClass,
            A_zimbraMailHost,
            A_displayName,
            A_sn,
            A_zimbraAccountStatus,
            A_zimbraPrefSkin,
            A_zimbraZimletAvailableZimlets,
            A_zimbraPrefClientType,
            A_zimbraPrefLabel,
            A_zimbraPrefMailPollingInterval,
            A_zimbraChildAccount,
            A_zimbraPrefChildVisibleAccount,
            A_zimbraPrefMailtoHandlerEnabled,
            A_zimbraPrefMailtoAccountId,
            A_zimbraJunkMessagesIndexingEnabled,
            A_zimbraPrefMailToasterEnabled,
            A_zimbraPrefCalendarToasterEnabled,
            A_zimbraMailQuota
    ));

    @Override
    public synchronized Account createAccount(String emailAddress, String password, Map<String, Object> attrs) throws ServiceException {
    	String dsName = (String)attrs.get(A_offlineDataSourceName);
    	Account account;
    	if (dsName != null) {
    		account = createDataSourceAccount(dsName, emailAddress, password, attrs);
    	} else {
    		account = createSyncAccount(emailAddress, password, attrs);
    	}
    	fixAccountsOrder(true);
    	return account;
    }
    
    private OfflineAccount.Version MIN_ZCS_VER = new OfflineAccount.Version("5.0");
    
    private synchronized Account createSyncAccount(String emailAddress, String password, Map<String, Object> attrs) throws ServiceException {    
        if (attrs == null || !(attrs.get(A_offlineRemoteServerUri) instanceof String))
            throw ServiceException.FAILURE("need single offlineRemoteServerUri when creating account: " + emailAddress, null);

        String parts[] = emailAddress.split("@");
        if (parts.length != 2)
            throw ServiceException.INVALID_REQUEST("must be valid email address: " + emailAddress, null);
        String uid = parts[0];

        checkForSSLCertAlias(attrs);
        
        ZGetInfoResult zgi = newZMailbox(emailAddress, (String)attrs.get(A_offlineRemotePassword), attrs, ZimbraServlet.USER_SERVICE_URI).getAccountInfo(false);
        OfflineLog.offline.info("Remote Zimbra Server Version: " + zgi.getVersion());
        OfflineAccount.Version remoteVersion = new OfflineAccount.Version(zgi.getVersion());
        if (!remoteVersion.isAtLeast(MIN_ZCS_VER))
        	throw ServiceException.FAILURE("Remote server version " + remoteVersion + ", ZCS 5.0 or later required", null);
        
        attrs.put(A_offlineRemoteServerVersion, zgi.getVersion());

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
        if (!(attrs.get(A_cn) instanceof String))
            attrs.put(A_cn, attrs.get(A_displayName) instanceof String ? (String) attrs.get(A_displayName) : uid);
        if (!(attrs.get(A_sn) instanceof String))
            attrs.put(A_sn, uid);
        if (!(attrs.get(A_zimbraAccountStatus) instanceof String))
            attrs.put(A_zimbraAccountStatus, ACCOUNT_STATUS_ACTIVE);

        attrs.remove(A_zimbraIsAdminAccount);
        attrs.remove(A_zimbraIsDomainAdminAccount);
        
        String[] skins = mLocalConfig.getMultiAttr(Provisioning.A_zimbraInstalledSkin);
        attrs.put(A_zimbraPrefSkin, skins == null || skins.length == 0 ? "yahoo" : skins[0]);
        
        attrs.put(A_zimbraPrefMailPollingInterval, OfflineLC.zdesktop_client_poll_interval.value());
        
        attrs.put(A_zimbraPrefClientType, "advanced");
        attrs.put(A_zimbraFeatureSharingEnabled, TRUE);
        
        attrs.remove(A_zimbraChildAccount);
        attrs.remove(A_zimbraPrefChildVisibleAccount);
        
        attrs.put(A_zimbraJunkMessagesIndexingEnabled, TRUE);
        
        attrs.put(A_zimbraMailQuota, "0");

        Account account = createAccountInternal(emailAddress, zgi.getId(), attrs, true);
        try {
            // create identity entries in database
            for (ZIdentity zident : zgi.getIdentities())
                DirectorySync.getInstance().syncIdentity(this, account, zident);
            // create data source entries in database
//            for (ZDataSource zdsrc : zgi.getDataSources())
//                DirectorySync.getInstance().syncDataSource(this, account, zdsrc);
        } catch (ServiceException e) {
            OfflineLog.offline.error("error initializing account " + emailAddress, e);
            Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(account.getId(), false);
            if (mbox != null) {
                mbox.deleteMailbox();
            }
            mAccountCache.remove(account);
            deleteAccount(zgi.getId());
            throw e;
        }
        return account;
    }

    public synchronized List<Account> getAllSyncAccounts() throws ServiceException {
        List<Account> accounts = getAllAccounts();
        for (Iterator<Account> i = accounts.iterator(); i.hasNext();) {
            if (!isSyncAccount(i.next()))
                i.remove();
        }
        return accounts;
    }

    public boolean isSyncAccount(Account account) {
        return account.getAttr(A_offlineRemoteServerUri, null) != null;
    }

    private void testDataSource(OfflineDataSource ds) throws ServiceException {
        DataSourceManager.test(ds);
        
        // No need to test Live/YMail SOAP access, since successful IMAP/POP3
        // connection implies that SOAP access will succeed with same auth
        // credentials.
        if (ds.needsSmtpAuth()) {
        	OfflineLog.offline.info("SMTP Testing: %s", ds);
            try {
                SMTPTransport smtp = (SMTPTransport)(LocalJMSession.getSession(ds).getTransport());
                smtp.connect();
                smtp.issueCommand("MAIL FROM:<" + ds.getEmailAddress() + ">", 250);
                smtp.issueCommand("RSET", 250);
                smtp.close();
                OfflineLog.offline.info("SMTP Test Succeeded: %s", ds);
            } catch (Exception e) {
                if (e instanceof AuthenticationFailedException)
                    throw RemoteServiceException.SMTP_AUTH_FAILURE(e.getMessage(), e);
                else if (e instanceof MessagingException && e.getMessage() != null && e.getMessage().startsWith("530"))
                    throw RemoteServiceException.SMTP_AUTH_REQUIRED(e.getMessage(), e);
                
                Throwable t = SystemUtil.getInnermostException(e);
                if (t == null)
                	t = e;
                if (t instanceof SSLPeerUnverifiedException)
                	throw RemoteServiceException.SSLCERT_MISMATCH(t.getMessage(), t);
                else if (t instanceof CertificateException)
                	throw RemoteServiceException.SSLCERT_ERROR(t.getMessage(), t);
                else if (t instanceof SSLHandshakeException)
                	throw RemoteServiceException.SSL_HANDSHAKE(t.getMessage(), t);
                
                throw ServiceException.FAILURE("SMTP connect failure", e);
            }
        }
    }

    private void testCalDav(String localPart, String domain, String password) throws ServiceException {
        String username = null;
        boolean isYmail = false;
      
        if (domain.equals("yahoo.com") || domain.equals("ymail.com") || domain.equals("rocketmail.com")) {
            if (domain.equals("yahoo.com"))
                username = localPart;
            domain = "yahoo.com";
            isYmail = true;
        } else if (!domain.equals("gmail.com")) {
            return;
        }
        if (username == null)
            username = localPart + "@" + domain;
        
        int status;
        try {
            OfflineLog.offline.debug("testing offline caldav access: username=" + username + " service=" + domain);
            status = OfflineCalDavDataImport.loginTest(username, password, domain);
            if (status == 502 && isYmail) {
                OfflineLog.offline.debug("must upgrade to all-new yahoo calendar servcie: username=" + username + " service=" + domain);
                throw OfflineServiceException.YCALDAV_NEED_UPGRADE();
            } else if (status == 404 && domain.equals("gmail.com")) {
                OfflineLog.offline.debug("google calendar servcie not enabled: username=" + username + " service=" + domain);
                throw OfflineServiceException.GCALDAV_NEED_ENABLE();
            } else if (status != 200) {
                OfflineLog.offline.debug("caldav login failed: username=" + username + " service=" + domain + " status=" + Integer.toString(status));
                throw OfflineServiceException.CALDAV_LOGIN_FAILED();
            }
            OfflineLog.offline.debug("caldav access test passed for " + username);
        } catch (IOException e) {
            throw ServiceException.FAILURE("IO error in CalDav login test", e);
        }
    }
    
    private synchronized Account createDataSourceAccount(String dsName, String emailAddress, String _password, Map<String, Object> dsAttrs) throws ServiceException {
        validEmailAddress(emailAddress);
        emailAddress = emailAddress.toLowerCase().trim();
        String parts[] = emailAddress.split("@");
        if (parts.length != 2)
            throw ServiceException.INVALID_REQUEST("must be valid email address: "+emailAddress, null);

        String localPart = parts[0];
        String domain = parts[1];
        domain = IDNUtil.toAsciiDomainName(domain);
        emailAddress = localPart + "@" + domain;

        //first we need to verify datasource
    	String accountLabel = (String)dsAttrs.remove(A_zimbraPrefLabel);
    	dsAttrs.remove(A_offlineDataSourceName);
    	String dsType = (String)dsAttrs.remove(A_offlineDataSourceType);
    	DataSource.Type type = DataSource.Type.valueOf(dsType);
        String dsid = UUID.randomUUID().toString();
    	dsAttrs.put(A_zimbraDataSourceId, dsid);
        String password = (String) dsAttrs.remove(A_zimbraDataSourcePassword);
    	dsAttrs.put(A_zimbraDataSourcePassword, DataSource.encryptData(dsid, password));
    	String smtpPassword = (String) dsAttrs.get(A_zimbraDataSourceSmtpAuthPassword);
    	if (smtpPassword != null)
    		dsAttrs.put(A_zimbraDataSourceSmtpAuthPassword, DataSource.encryptData(dsid, smtpPassword));
    	
    	checkForSSLCertAlias(dsAttrs);

        OfflineDataSource testDs = new OfflineDataSource(getLocalAccount(), type, dsName, dsid, dsAttrs, this);
        testDataSource(testDs);

        String syncCal = (String) dsAttrs.get(OfflineConstants.A_zimbraDataSourceCalendarSyncEnabled);
        if (syncCal != null && syncCal.equals(Provisioning.TRUE))
            testCalDav(localPart, domain, password);
        
    	String accountId = UUID.randomUUID().toString();

        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(A_offlineDataSourceName, dsName);
        attrs.put(A_zimbraPrefLabel, accountLabel);
        String displayName = (String)dsAttrs.get(A_zimbraPrefFromDisplay);
        if (displayName != null)
        	attrs.put(A_zimbraPrefFromDisplay, displayName);
        
        attrs.put(A_objectClass, new String[] { "organizationalPerson", "zimbraAccount" } );
        attrs.put(A_zimbraMailHost, "localhost");
        attrs.put(A_uid, localPart);
        attrs.put(A_mail, emailAddress);
        attrs.put(A_zimbraId, accountId);
        attrs.put(A_cn, localPart);
        attrs.put(A_sn, localPart);
        attrs.put(A_displayName, dsName);
        attrs.put(A_zimbraAccountStatus, ACCOUNT_STATUS_ACTIVE);

        setDefaultAccountAttributes(attrs);
        
        if (testDs.isYahoo()) {
        	attrs.put(A_zimbraPrefSkin, "yahoo");
        }

        Account account = createAccountInternal(emailAddress, accountId, attrs, false);
        OfflineDataSource ds = null;
        try {
        	ds = (OfflineDataSource) createDataSource(account, type, dsName, dsAttrs, true, false);
        } catch (Throwable t) {
        	OfflineLog.offline.warn("failed creating datasource: " + dsName, t);
        	deleteAccount(account.getId());
        }
        try {
            MailboxManager.getInstance().getMailboxByAccount(account);
        } catch (ServiceException e) {
            OfflineLog.offline.error("error initializing account " + emailAddress, e);
            if (ds != null) {
                deleteDataSource(account, ds.getId());
            }
            mAccountCache.remove(account);
            deleteAccount(accountId);
            throw e;
        }
        
        return account;
    }

    public synchronized List<Account> getAllDataSourceAccounts() throws ServiceException {
        List<Account> accounts = getAllAccounts();
        for (Iterator<Account> i = accounts.iterator(); i.hasNext();) {
            if (!isDataSourceAccount(i.next()))
                i.remove();
        }
        return accounts;
    }

    public static boolean isDataSourceAccount(Account account) {
        return account.getAttr(A_offlineDataSourceName, null) != null;
    }

    public static String getDataSourceName(Account account) {
        return account.getAttr(A_offlineDataSourceName, null);
    }

    public synchronized DataSource getDataSource(Account account) throws ServiceException {
        if (!isDataSourceAccount(account))
            return null;
        return get(account, DataSourceBy.name, getDataSourceName(account));
    }

    public synchronized List<DataSource> getAllDataSources() throws ServiceException {
        List<Account> accounts = getAllDataSourceAccounts();
        List<DataSource> dataSources = new ArrayList<DataSource>(accounts.size());
        for (Account account : accounts)
            dataSources.add(getDataSource(account));
        return dataSources;
    }

    public synchronized OfflineAccount createGalAccount(OfflineAccount mainAcct) throws ServiceException {
        Map<String, Object> attrs = new HashMap<String, Object>();

        String id = UUID.randomUUID().toString();
        String name = mainAcct.getName() + OfflineConstants.GAL_ACCOUNT_SUFFIX;
        
        attrs.put(A_objectClass, new String[] { "organizationalPerson", "zimbraAccount" } );
        attrs.put(A_zimbraMailHost, "localhost");
        attrs.put(A_uid, id);
        attrs.put(A_mail, name);
        attrs.put(A_zimbraId, id);
        attrs.put(A_cn, id);
        attrs.put(A_sn, id);
        attrs.put(A_zimbraAccountStatus, ACCOUNT_STATUS_ACTIVE);
        attrs.put(A_offlineGalAccountSyncToken, "");
        attrs.put(A_offlineGalAccountLastFullSync, "0");

        setDefaultAccountAttributes(attrs);

        OfflineAccount galAcct = (OfflineAccount)createAccountInternal(name, id, attrs, true);
        setAccountAttribute(mainAcct, OfflineConstants.A_offlineGalAccountId, galAcct.getId());
        return galAcct;
    }

    private void setMountpointAccountId(OfflineAccount account, String mptAcctId) throws ServiceException {
        String ids = account.getAttr(A_offlineMountpointAccountIds, null);
        String newIds = null;
        if (ids == null)
            newIds = mptAcctId;
        else if (ids.indexOf(mptAcctId) == -1)
            newIds = ids + "," + mptAcctId;
        if (newIds != null)
            setAccountAttribute(account, A_offlineMountpointAccountIds, newIds);
    }
    
    public void checkMountpointAccount(OfflineAccount mptAcct, String reqAcctId) throws ServiceException {
        if (!isMountpointAccount(mptAcct))
            throw OfflineServiceException.MOUNT_EXISTING_ACCT();
        String pxyAcctId = mptAcct.getAttr(A_offlineMountpointProxyAccountId);
        if (!pxyAcctId.equals(reqAcctId))
            throw OfflineServiceException.MOUNT_OP_UNSUPPORTED(); 
    }
    
    public synchronized OfflineAccount createMountpointAccount(String name, String id, OfflineAccount account, boolean check) throws ServiceException {
        OfflineAccount mptAcct = (OfflineAccount)get(Provisioning.AccountBy.id, id);
        if (mptAcct != null) {
            if (check)
                checkMountpointAccount(mptAcct, account.getId());
        } else {
            Map<String, Object> attrs = new HashMap<String, Object>();
            attrs.put(A_objectClass, new String[] { "organizationalPerson", "zimbraAccount" } );       
            attrs.put(A_zimbraMailHost, OfflineConstants.SYNC_SERVER_PREFIX + account.getAttr(OfflineConstants.A_offlineRemoteServerUri));
            attrs.put(A_uid, id);
            attrs.put(A_mail, name);
            attrs.put(A_zimbraId, id);
            attrs.put(A_cn, id);
            attrs.put(A_sn, id);
            attrs.put(A_zimbraAccountStatus, ACCOUNT_STATUS_ACTIVE);
            attrs.put(A_offlineMountpointProxyAccountId, account.getId());

            setDefaultAccountAttributes(attrs);

            mptAcct = (OfflineAccount)createAccountInternal(name, id, attrs, false, true);
        }
          
        setMountpointAccountId(account, mptAcct.getId());
        return mptAcct;
    }
    
    private static final String LOCAL_ACCOUNT_UID = "local";
    private static final String LOCAL_ACCOUNT_NAME = LOCAL_ACCOUNT_UID + "@host.local";
    private static final String LOCAL_ACCOUNT_DISPLAYNAME = "Loading...";

    private synchronized Account createLocalAccount() throws ServiceException {
        Map<String, Object> attrs = new HashMap<String, Object>();

        attrs.put(A_objectClass, new String[] { "organizationalPerson", "zimbraAccount" } );
        attrs.put(A_zimbraMailHost, "localhost");
        attrs.put(A_uid, LOCAL_ACCOUNT_UID);
        attrs.put(A_mail, LOCAL_ACCOUNT_NAME);
        attrs.put(A_zimbraId, LOCAL_ACCOUNT_ID);
        attrs.put(A_cn, LOCAL_ACCOUNT_UID);
        attrs.put(A_sn, LOCAL_ACCOUNT_UID);
        attrs.put(A_displayName, LOCAL_ACCOUNT_DISPLAYNAME);
        attrs.put(A_zimbraPrefFromDisplay, LOCAL_ACCOUNT_DISPLAYNAME);
        attrs.put(A_zimbraAccountStatus, ACCOUNT_STATUS_ACTIVE);

        setDefaultAccountAttributes(attrs);

        return createAccountInternal(LOCAL_ACCOUNT_NAME, LOCAL_ACCOUNT_ID, attrs, true);
    }

    public synchronized Account getLocalAccount() throws ServiceException {
    	Account account = get(AccountBy.id, LOCAL_ACCOUNT_ID);
    	if (account != null)
    		return account;
    	return createLocalAccount();
    }

    public boolean isGalAccount(Account account) {
        return account.getAttr(A_offlineGalAccountSyncToken, null) != null;
    }
    
    public boolean isMountpointAccount(Account account) {
        return account.getAttr(A_offlineMountpointProxyAccountId, null) != null;
    }
    
    public boolean isLocalAccount(Account account) {
    	return account.getId().equals(LOCAL_ACCOUNT_ID);
    }

    private Account createAccountInternal(String emailAddress, String accountId, Map<String, Object> attrs, boolean initMailbox) throws ServiceException {
        return createAccountInternal(emailAddress, accountId, attrs, initMailbox, false);
    }
    
    private synchronized Account createAccountInternal(String emailAddress, String accountId, Map<String, Object> attrs, boolean initMailbox, boolean skipAttrMgr) throws ServiceException {
        Map<String,Object> immutable = new HashMap<String, Object>();
        for (String attr : AttributeManager.getInstance().getImmutableAttrs())
            if (attrs.containsKey(attr))
                immutable.put(attr, attrs.remove(attr));

        Map<String, Object> context = null;
        if (!skipAttrMgr) {
            context = new HashMap<String, Object>();
            AttributeManager.getInstance().preModify(attrs, null, context, true, true);
        }

        attrs.putAll(immutable);

        // create account entry in database
        DbOfflineDirectory.createDirectoryEntry(EntryType.ACCOUNT, emailAddress, attrs, false);

        Account acct = new OfflineAccount(emailAddress, accountId, attrs, mDefaultCos.getAccountDefaults(), accountId.equals(LOCAL_ACCOUNT_ID) ? null : getLocalAccount(), this);
        mAccountCache.put(acct);

        if (!skipAttrMgr)
            AttributeManager.getInstance().postModify(attrs, acct, context, true);

        if (initMailbox)
	        try {
	            MailboxManager.getInstance().getMailboxByAccount(acct);
	        } catch (ServiceException e) {
	            OfflineLog.offline.error("error initializing account " + emailAddress, e);
	            mAccountCache.remove(acct);
	            deleteAccount(accountId);
	            throw e;
	        }

	    cachedaccountIds = null;    
	    
        return acct;
    }
    
    private static final String A_zimbraMailIdleSessionTimeout = "zimbraMailIdleSessionTimeout";
    private static final String A_zimbraPrefCalendarAlwaysShowMiniCal = "zimbraPrefCalendarAlwaysShowMiniCal";
    private static final String A_zimbraPrefCalendarApptReminderWarningTime = "zimbraPrefCalendarApptReminderWarningTime";
    private static final String A_zimbraPrefComposeInNewWindow = "zimbraPrefComposeInNewWindow";
    private static final String A_zimbraPrefContactsInitialView = "zimbraPrefContactsInitialView";
    private static final String A_zimbraPrefGalAutoCompleteEnabled = "zimbraPrefGalAutoCompleteEnabled";
    private static final String A_zimbraPrefHtmlEditorDefaultFontColor = "zimbraPrefHtmlEditorDefaultFontColor";
    private static final String A_zimbraPrefHtmlEditorDefaultFontFamily = "zimbraPrefHtmlEditorDefaultFontFamily";
    private static final String A_zimbraPrefHtmlEditorDefaultFontSize = "zimbraPrefHtmlEditorDefaultFontSize";
    
    private void setDefaultAccountAttributes(Map<String, Object> attrs) {
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
        addToMap(attrs, A_zimbraFeatureGalSyncEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureGalEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureGroupCalendarEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureHtmlComposeEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureIMEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureIdentitiesEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureInitialSearchPreferenceEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureInstantNotify, TRUE);
        addToMap(attrs, A_zimbraFeatureMailEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureMailForwardingEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureMailPriorityEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureNewMailNotificationEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureNotebookEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureOutOfOfficeReplyEnabled, TRUE);
        addToMap(attrs, A_zimbraFeaturePop3DataSourceEnabled, TRUE);
        addToMap(attrs, A_zimbraFeaturePortalEnabled, FALSE);
        addToMap(attrs, A_zimbraFeatureSavedSearchesEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureSharingEnabled, FALSE);
        addToMap(attrs, A_zimbraFeatureSkinChangeEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureTaggingEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureTasksEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureViewInHtmlEnabled, TRUE);
        addToMap(attrs, A_zimbraFeatureVoiceEnabled, FALSE);
        
        addToMap(attrs, A_zimbraJunkMessagesIndexingEnabled, TRUE); //always enable junk index
        
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
        addToMap(attrs, A_zimbraPrefMailInitialSearch, "in:inbox");
        addToMap(attrs, A_zimbraPrefMailItemsPerPage, "50");
        addToMap(attrs, A_zimbraPrefMailPollingInterval, OfflineLC.zdesktop_client_poll_interval.value());
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

        String[] skins = mLocalConfig.getMultiAttr(Provisioning.A_zimbraInstalledSkin);
        attrs.put(A_zimbraPrefSkin, skins == null || skins.length == 0 ? "yahoo" : skins[0]);
        
        attrs.put(A_zimbraPrefClientType, "advanced");
        attrs.put(A_zimbraFeatureSharingEnabled, FALSE);
        
        addToMap(attrs, A_zimbraIsAdminAccount, TRUE);
        addToMap(attrs, A_zimbraIsDomainAdminAccount, TRUE);
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
        deleteGalAccount(zimbraId);
        deleteMountpointAccounts(zimbraId);
        deleteOfflineAccount(zimbraId);
        
        cachedaccountIds = null;
    }
    
    private synchronized void deleteOfflineAccount(String zimbraId) throws ServiceException {
        DbOfflineDirectory.deleteDirectoryEntry(EntryType.ACCOUNT, zimbraId);

        Account acct = mAccountCache.getById(zimbraId);
        if (acct != null)
            mAccountCache.remove(acct);
        
        fixAccountsOrder(true);        
    }

    public synchronized void deleteGalAccount(String mainAcctId) throws ServiceException {
        OfflineAccount mainAcct = (OfflineAccount)get(AccountBy.id, mainAcctId);
        if (mainAcct != null)
            deleteGalAccount(mainAcct);
    }
        
    public synchronized void deleteGalAccount(OfflineAccount mainAcct) throws ServiceException {
        String galAcctId = mainAcct.getAttr(OfflineConstants.A_offlineGalAccountId, false);        
        if (galAcctId == null || galAcctId.length() == 0)
            return;
        setAccountAttribute(mainAcct, OfflineConstants.A_offlineGalAccountId, "");

        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(galAcctId, false);      
        if (mbox != null)
            mbox.deleteMailbox(); 
        OfflineAccount galAcct = (OfflineAccount)get(AccountBy.id, galAcctId);
        if (galAcct != null)
            deleteOfflineAccount(galAcctId);
    }
    
    private void deleteMountpointAccounts(String mainAcctId) throws ServiceException {
        OfflineAccount mainAcct = (OfflineAccount)get(AccountBy.id, mainAcctId);
        String ids;
        if (mainAcct == null || (ids = mainAcct.getAttr(A_offlineMountpointAccountIds, null)) == null)
            return;       
        
        String[] idList = ids.split(",");
        for (String id : idList) {
            Account acct = get(Provisioning.AccountBy.id, id);        
            if (acct != null && isMountpointAccount(acct))
                deleteOfflineAccount(id);
        }
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
            if ((acct = mAccountCache.getById(key)) == null)
            	attrs = DbOfflineDirectory.readDirectoryEntry(EntryType.ACCOUNT, A_zimbraId, key);
        } else if (keyType == AccountBy.name) {
        	if (key.equals(LOCAL_ACCOUNT_NAME))
        		return getLocalAccount();
            if ((acct = mAccountCache.getByName(key)) == null)
            	attrs = DbOfflineDirectory.readDirectoryEntry(EntryType.ACCOUNT, A_offlineDn, key);
        } else if (keyType == AccountBy.adminName) {
            if ((acct = mAccountCache.getByName(key)) == null && key.equals(LC.zimbra_ldap_user.value())) {
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
        
        if (acct != null)
        	attrs = acct.getAttrs();
        if (attrs == null)
        	return null;

        String name = (String)attrs.get(A_mail);
        if (name == null)
        	return null;
        
        //There are attributes we don't persist into DB.  This is where we add them:
    	attrs.put(OfflineConstants.A_offlineSyncStatus, OfflineSyncManager.getInstance().getSyncStatus(name).toString());    	
    	
    	if (acct != null) {
    		acct.setAttrs(attrs);
    	} else {
        	acct = new OfflineAccount(name, (String) attrs.get(A_zimbraId), attrs, mDefaultCos.getAccountDefaults(),
        			keyType == AccountBy.id && key.equals(LOCAL_ACCOUNT_ID) ? null : getLocalAccount(), this);
            mAccountCache.put(acct);
        }

    	// mountpoint remote account
    	String pxyAcctId;
    	if ((pxyAcctId = (String)attrs.get(A_offlineMountpointProxyAccountId)) != null) {
    	    Server server;
    	    synchronized (mSyncServerCache) {
    	        server = mSyncServerCache.get((String)attrs.get(A_zimbraMailHost));
    	    }
    	    if (server == null)
    	        loadRemoteSyncServer(pxyAcctId, acct.getId());
    	}
    	
        return acct;
    }

    @Override
    public synchronized List<NamedEntry> searchAccounts(String query, String[] returnAttrs, String sortAttr, boolean sortAscending, int flags) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    private List<String> cachedaccountIds;

    public List<Account> getAllAccounts() throws ServiceException {
        List<Account> accts = new ArrayList<Account>();
        synchronized (this) {
        	cachedaccountIds = cachedaccountIds != null ? cachedaccountIds : DbOfflineDirectory.listAllDirectoryEntries(EntryType.ACCOUNT);
            for (String zimbraId : cachedaccountIds) {
                Account acct = get(AccountBy.id, zimbraId);
                if (acct != null && !isLocalAccount(acct) && !isGalAccount(acct) && !isMountpointAccount(acct)) {
                	MailboxManager.getInstance().getMailboxByAccount(acct);
                    accts.add(acct);
                }
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
	            if (acct != null && isSyncAccount(acct))
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
    public synchronized void authAccount(Account acct, String password, String proto, Map<String, Object> context) throws ServiceException {
	authAccount(acct, password, proto);
    }

    @Override
    public synchronized void preAuthAccount(Account acct, String accountName, String accountBy, long timestamp, long expires, String preAuth, Map<String, Object> authCtxt) throws ServiceException {
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
    public synchronized Cos copyCos(String srcCosId, String destCosName) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("copyCos");
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
        if (key.startsWith(OfflineConstants.SYNC_SERVER_PREFIX)) {
            synchronized(mSyncServerCache) {
                return mSyncServerCache.get(key);
            }
        } else if (keyType == ServerBy.id)
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
        Zimlet zimlet = new OfflineZimlet(name, (String) attrs.get(A_zimbraId), attrs, this);
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
    public synchronized void getAllAccounts(Domain d, Server s, NamedEntry.Visitor visitor) throws ServiceException {
        if (s == null || s.getName().equalsIgnoreCase(mLocalServer.getName()))
            getAllAccounts(d, visitor);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized List getAllCalendarResources(Domain d) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void getAllCalendarResources(Domain d, Server s, Visitor visitor) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void getAllCalendarResources(Domain d, Visitor visitor) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized List getAllDistributionLists(Domain d) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("getAllDistributionLists");
    }

    @Override
    public synchronized List<NamedEntry> searchAccounts(Domain d, String query, String[] returnAttrs, String sortAttr, boolean sortAscending, int flags) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    public List<NamedEntry> searchDirectory(SearchOptions options) throws ServiceException {
    	//HACK: we were throwing UnsupportedOperationException, but DeleteAccount now does a searchDirectory to prevent from deleting
    	//domain wiki accounts.  Hence the hack to always return empty.
        return new ArrayList<NamedEntry>();
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
    @SuppressWarnings("unchecked")
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
        return createIdentity(account, name, attrs, isSyncAccount(account));
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
        Identity identity = new OfflineIdentity(account, name, attrs, this);
        mHasDirtyAccounts |= markChanged;

        AttributeManager.getInstance().postModify(attrs, identity, context, true);
        return identity;
    }

    @Override
    public synchronized void deleteIdentity(Account account, String name) throws ServiceException {
        deleteIdentity(account, name, isSyncAccount(account));
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
        modifyIdentity(account, name, attrs, isSyncAccount(account));
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
            if (key.equalsIgnoreCase(account.getId()))
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

        return new OfflineIdentity(account, (String) attrs.get(A_zimbraPrefIdentityName), attrs, this);
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
    	return createSignature(account, signatureName, attrs, isSyncAccount(account));
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
        modifySignature(account, signatureId, attrs, isSyncAccount(account));
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
        deleteSignature(account, signatureId, isSyncAccount(account));
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

        return new OfflineSignature(account, attrs, this);
    }
    
    private Map<String, List<DataSource>> cachedDataSources = new HashMap<String, List<DataSource>>();
    
    @Override
    public synchronized DataSource createDataSource(Account account, DataSource.Type type, String name, Map<String, Object> attrs) throws ServiceException {
        return createDataSource(account, type, name, attrs, false, isSyncAccount(account));
    }

    @Override
    public synchronized DataSource createDataSource(Account account, DataSource.Type type, String name, Map<String, Object> attrs, boolean passwdAlreadyEncrypted) throws ServiceException {
        return createDataSource(account, type, name, attrs, passwdAlreadyEncrypted, isSyncAccount(account));
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
        if (!passwdAlreadyEncrypted)
            attrs.put(A_zimbraDataSourcePassword, DataSource.encryptData(dsid, (String) attrs.get(A_zimbraDataSourcePassword)));
        if (markChanged)
            attrs.put(A_offlineModifiedAttrs, A_offlineDn);

        if (isDataSourceAccount(account))
		    attrs.put(A_zimbraDataSourceEnabled, TRUE);

        //testDataSource(new OfflineDataSource(account, type, name, dsid, attrs));
        
        Map<String,Object> immutable = new HashMap<String, Object>();
        for (String attr : AttributeManager.getInstance().getImmutableAttrs())
            if (attrs.containsKey(attr))
                immutable.put(attr, attrs.remove(attr));

        Map<String, Object> context = new HashMap<String, Object>();
        AttributeManager.getInstance().preModify(attrs, null, context, true, true);

        attrs.putAll(immutable);
        
        DbOfflineDirectory.createDirectoryLeaf(EntryType.DATASOURCE, account, name, dsid, attrs, markChanged);
        DataSource ds = new OfflineDataSource(account, type, name, dsid, attrs, this);
        mHasDirtyAccounts |= markChanged;

        AttributeManager.getInstance().postModify(attrs, ds, context, true);
        
        cachedDataSources.remove(account.getId());
        
        return ds;
    }

    @Override
    public synchronized void deleteDataSource(Account account, String dataSourceId) throws ServiceException {
        deleteDataSource(account, dataSourceId, isSyncAccount(account));
    }

    synchronized void deleteDataSource(Account account, String dataSourceId, boolean markChanged) throws ServiceException {
        DataSource dsrc = get(account, DataSourceBy.id, dataSourceId);
        if (dsrc == null)
            return;

        DbOfflineDirectory.deleteDirectoryLeaf(EntryType.DATASOURCE, account, dsrc.getId(), markChanged);
        reload(account);
        mHasDirtyAccounts |= markChanged;
        
        cachedDataSources.remove(account.getId());
    }

    @Override
    public synchronized List<DataSource> getAllDataSources(Account account) throws ServiceException {
    	List<DataSource> sources = cachedDataSources.get(account.getId());
    	if (sources == null) {
    		List<String> names = DbOfflineDirectory.listAllDirectoryLeaves(EntryType.DATASOURCE, account);
    		sources = new ArrayList<DataSource>(names.size());
    		for (String name : names)
                sources.add(get(account, DataSourceBy.name, name));
            sort(sources);
    		cachedDataSources.put(account.getId(), sources);
    	}
    	for (DataSource ds : sources)
    		ds.getAttrs(false).put(OfflineConstants.A_zimbraDataSourceSyncStatus, OfflineSyncManager.getInstance().getSyncStatus(ds.getName()).toString());
    	return sources;
    }

    private static void sort(List<DataSource> sources) {
        Collections.sort(sources, new Comparator<DataSource>() {
            public int compare(DataSource ds1, DataSource ds2) {
                return syncOrder(ds1) - syncOrder(ds2);
            }
        });
    }

    private static int syncOrder(DataSource ds) {
        switch (ds.getType()) {
        case yab:
            return 1;
        case caldav:
            return 2;
        default:
            return 3;
        }
    }
    
    @Override
    public void modifyDataSource(Account account, String dataSourceId, Map<String, Object> attrs) throws ServiceException {
        modifyDataSource(account, dataSourceId, attrs, isSyncAccount(account));
    }

    void modifyDataSource(Account account, String dataSourceId, Map<String, Object> attrs, boolean markChanged) throws ServiceException {
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
        
        if (attrs.get(A_zimbraDataSourceSmtpAuthPassword) instanceof String)
        	attrs.put(A_zimbraDataSourceSmtpAuthPassword, DataSource.encryptData(dataSourceId, (String) attrs.get(A_zimbraDataSourceSmtpAuthPassword)));

        if (isDataSourceAccount(account) && attrs.get(A_zimbraDataSourceHost) != null) {
        	boolean isTestNeeded = false;
        	if (!ds.getHost().equals(attrs.get(A_zimbraDataSourceHost)) ||
        			!ds.getPort().toString().equals(attrs.get(A_zimbraDataSourcePort)) ||
        			!ds.getAttr(A_zimbraDataSourceConnectionType).equals(attrs.get(A_zimbraDataSourceConnectionType)))
        		isTestNeeded = true;
        	
        	String password = (String)attrs.get(A_zimbraDataSourcePassword);
        	if (password == null) {
        		password = ds.getAttr(A_zimbraDataSourcePassword);
        		attrs.put(A_zimbraDataSourcePassword, password);
        	} else if (!isTestNeeded && !password.equals(ds.getAttr(A_zimbraDataSourcePassword))) {
        		isTestNeeded = true;
        	}
        	String domain = ds.getAttr(OfflineConstants.A_zimbraDataSourceDomain);
        	if (ds.getType() != DataSource.Type.live && !"yahoo.com".equals(domain)) {
	        	if (!isTestNeeded && (!ds.getAttr(A_zimbraDataSourceSmtpHost).equals(attrs.get(A_zimbraDataSourceSmtpHost)) ||
	        		!ds.getAttr(A_zimbraDataSourceSmtpPort).equals(attrs.get(A_zimbraDataSourceSmtpPort)) ||
	        		!ds.getAttr(A_zimbraDataSourceSmtpConnectionType).equals(attrs.get(A_zimbraDataSourceSmtpConnectionType)) ||
	        		!ds.getAttr(A_zimbraDataSourceSmtpAuthRequired).equals(attrs.get(A_zimbraDataSourceSmtpAuthRequired))))
	        		isTestNeeded = true;
	        	
	        	if (!isTestNeeded && ds.getBooleanAttr(A_zimbraDataSourceSmtpAuthRequired, false) &&
	        		!ds.getAttr(A_zimbraDataSourceSmtpAuthUsername).equals(attrs.get(A_zimbraDataSourceSmtpAuthUsername)))
	        		isTestNeeded = true;
	        	
	        	String smtpPassword = (String)attrs.get(A_zimbraDataSourceSmtpAuthPassword);
	        	if (smtpPassword == null) {
	        		smtpPassword = ds.getAttr(A_zimbraDataSourceSmtpAuthPassword, null);
	        		if (smtpPassword != null)
	        			attrs.put(A_zimbraDataSourceSmtpAuthPassword, smtpPassword);
	        	} else if (!isTestNeeded && !smtpPassword.equals(ds.getAttr(A_zimbraDataSourceSmtpAuthPassword, null)))
	        		isTestNeeded = true;
        	}
        	
            if (isTestNeeded) {
            	checkForSSLCertAlias(attrs);
            	
                if ("yahoo.com".equals(domain)) {
                    // Clear auth token so that it will be regenerated during test...
                    OfflineYAuth.removeToken(ds);
                }
                testDataSource(new OfflineDataSource(account, ds.getType(), ds.getName(), ds.getId(), attrs, this));
            }
    	
            attrs.put(A_zimbraDataSourceEnabled, TRUE);
        }

        Map<String, Object> context = new HashMap<String, Object>();

        synchronized (this) {
            AttributeManager.getInstance().preModify(attrs, ds, context, false, true, true);

            DbOfflineDirectory.modifyDirectoryLeaf(EntryType.DATASOURCE, account, A_zimbraId, dataSourceId, attrs, markChanged, newName);
            reload(ds);
            mHasDirtyAccounts |= markChanged;

            AttributeManager.getInstance().postModify(attrs, ds, context, false, true);
        }
    }

    @Override
    public synchronized DataSource get(Account account, DataSourceBy keyType, String key) throws ServiceException {
    	List<DataSource> cached = cachedDataSources.get(account.getId());
    	if (cached != null) {
    		for (DataSource ds : cached) {
    	        if (keyType == DataSourceBy.name && ds.getName().equals(key) ||
    	        		keyType == DataSourceBy.id && ds.getId().equals(key))
    	            return ds;
    		}
    		return null;
    	}
    	
        Map<String,Object> attrs = null;
        if (keyType == DataSourceBy.name) {
            attrs = DbOfflineDirectory.readDirectoryLeaf(EntryType.DATASOURCE, account, A_offlineDn, key);
        } else if (keyType == DataSourceBy.id) {
            attrs = DbOfflineDirectory.readDirectoryLeaf(EntryType.DATASOURCE, account, A_zimbraId, key);
        }
        if (attrs == null)
            return null;
        
        String name = (String)attrs.get(A_zimbraDataSourceName);
        if (name == null)
        	return null;

        DataSource.Type type = DataSource.Type.fromString((String) attrs.get(A_offlineDataSourceType));
        return new OfflineDataSource(account, type, name, (String) attrs.get(A_zimbraDataSourceId), attrs, this);
    }
    
    @Override
    public XMPPComponent createXMPPComponent(String name, Domain domain, Server server, Map<String, Object> attrs) throws ServiceException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<XMPPComponent> getAllXMPPComponents() throws ServiceException {
        throw ServiceException.FAILURE("unsupported", null);
    }
    
    @Override
    public XMPPComponent get(XMPPComponentBy keyType, String key) throws ServiceException {
        throw ServiceException.FAILURE("unsupported", null);
    }
    
    @Override
    public void deleteXMPPComponent(XMPPComponent comp) throws ServiceException {
        throw ServiceException.FAILURE("unsupported", null);
    }
    
    @Override
    public RightCommand.ACL getGrants(String targetType, TargetBy targetBy, String target) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("getGrants");
    }
    
    @Override
    public void grantRight(String targetType, TargetBy targetBy, String target,
             String granteeType, GranteeBy granteeBy, String grantee,
             String right, boolean deny) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("grantRight");
    }
    
    @Override
    public void revokeRight(String targetType, TargetBy targetBy, String target,
              String granteeType, GranteeBy granteeBy, String grantee,
              String right, boolean deny) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("revokeRight");
    }


    @Override
    public void flushCache(CacheEntryType type, CacheEntry[] entries) throws ServiceException {
        throw OfflineServiceException.UNSUPPORTED("flushCache");
    }
    
    private String promoteAccount(String accountId) throws ServiceException {
    	String[] order = fixAccountsOrder(false);
    	for (int i = 0; i < order.length; ++i) {
            if (order[i].equals(accountId) && i > 0) {
	        while (i > 0)
                    order[i] = order[i-- - 1];
                order[0] = accountId;
                break;
            }
    	}
    	return StringUtil.join(",", order);
    }
    
    //append unknown and remove missing
    private String[] fixAccountsOrder(boolean commit) throws ServiceException {
    	Account localAccount = getLocalAccount();
    	String oldOrderStr = localAccount.getAttr(A_offlineAccountsOrder, "");
    	String[] oldOrder = oldOrderStr.length() > 0 ? oldOrderStr.split(",") : new String[0];

    	List<Account> accounts = getAllAccounts();
    	String[] newOrder = new String[accounts.size()];
    	for (int i = 0; i < newOrder.length; ++i) {
    		newOrder[i] = accounts.get(i).getId();
    	}
    	
    	OfflineUtil.fixItemOrder(oldOrder, newOrder);
    	String newOrderStr = newOrder.length > 0 ? StringUtil.join(",", newOrder) : "";

    	if (commit && !newOrderStr.equals(oldOrderStr)) {
    		Map<String, Object> attrs = new HashMap<String, Object>(1);
    		attrs.put(A_offlineAccountsOrder, newOrderStr);
    		modifyAttrs(localAccount, attrs, false, false, false);
    	}
        return newOrder;
    }
    
    protected void loadRemoteSyncServer(String acctId, String mptAcctId) throws ServiceException {
        Account account = get(AccountBy.id, acctId);
        if (account == null)
            throw AccountServiceException.NO_SUCH_ACCOUNT(acctId);
        
        String uri = account.getAttr(OfflineConstants.A_offlineRemoteServerUri);
        if (uri == null) {
            OfflineLog.offline.warn("offline account missing RemoteServerUri attr: " + account.getName());
            throw AccountServiceException.INVALID_ATTR_VALUE(OfflineConstants.A_offlineRemoteServerUri + " is null", null);            
        }        
        String key = OfflineConstants.SYNC_SERVER_PREFIX + uri;

        boolean ssl = uri.startsWith("https://");            
        String port = ssl ? "443" : "80";
        String host = uri;
        int dslash = uri.indexOf("//");
        if (dslash > 0)
            host = uri.substring(dslash + 2);
        int colon = host.indexOf(':');
        if (colon > 0) {
            port = host.substring(colon + 1);
            host = host.substring(0, colon);
        }

        Map<String, Object> attrs = new HashMap<String, Object>(12);
        attrs.put(Provisioning.A_objectClass, "zimbraServer");
        attrs.put(Provisioning.A_cn, host);
        attrs.put(Provisioning.A_zimbraServiceHostname, host);
        attrs.put(Provisioning.A_zimbraSmtpHostname, host);
        attrs.put(Provisioning.A_zimbraId, account.getId());
        attrs.put("zimbraServiceEnabled", "mailbox");
        attrs.put("zimbraServiceInstalled", "mailbox");
        if (ssl)
            attrs.put(Provisioning.A_zimbraMailSSLPort, port);
        else
            attrs.put(Provisioning.A_zimbraMailPort, port);
        attrs.put(Provisioning.A_zimbraAdminPort, port);
        attrs.put(Provisioning.A_zimbraMailMode, ssl ? "https" : "http");

        Server server = new Server(key, key, attrs, null, this);
        synchronized(mSyncServerCache) {
            mSyncServerCache.put(key, server);
        }
        
        setMountpointAccountId((OfflineAccount)account, mptAcctId);
    }
    
    @Override
    public boolean isOfflineProxyServer(Server server) {
        return server.getName().startsWith(OfflineConstants.SYNC_SERVER_PREFIX);
    }
    
    @Override
    public String getProxyAuthToken(String acctId) throws ServiceException {
        Account account = get(AccountBy.id, acctId);
        if (isSyncAccount(account) || isMountpointAccount(account)) {
            String id = isMountpointAccount(account) ? account.getAttr(A_offlineMountpointProxyAccountId) : acctId;
            OfflineMailbox ombx = (OfflineMailbox)MailboxManager.getInstance().getMailboxByAccountId(id, false);
            return ombx.getAuthToken().getValue();
        } else {
            return null;
        }
    }
}
