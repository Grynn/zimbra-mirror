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

import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Session;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.datasource.imap.ImapSync;
import com.zimbra.cs.mailbox.DataSourceMailbox;
import com.zimbra.cs.mailbox.DesktopMailbox;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.LocalJMSession;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Message;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.SyncExceptionHandler;
import com.zimbra.cs.mailclient.CommandFailedException;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.offline.util.ymail.YMailClient;

public class OfflineDataSource extends DataSource {
    private DataSourceConfig.Service knownService;
    private OfflineDataSource contactSyncDataSource;
    private OfflineDataSource calendarSyncDataSource;

    OfflineDataSource(Account acct, DataSource.Type type, String name, String id, Map<String,Object> attrs, Provisioning prov) {
        super(acct, type, name, id, attrs, prov);
        setServiceName(getAttr(Provisioning.A_zimbraDataSourceDomain));
    }

    void setName(String name) {
        mName = name;
    }

    void setServiceName(String serviceName) {
    	knownService = serviceName == null ? null : config.getService(serviceName);
    }

    public OfflineDataSource getContactSyncDataSource() throws ServiceException {
        if (isGmail() || isYahoo()) {
            if (contactSyncDataSource == null) {
                contactSyncDataSource = newChildDataSource(Type.contacts);
            }
        }
        return contactSyncDataSource;
    }

    public OfflineDataSource getCalendarSyncDataSource() throws ServiceException {
        if (isGmail() || isYahoo()) {
            if (calendarSyncDataSource == null) {
                calendarSyncDataSource = newChildDataSource(Type.caldav);
            }
        }
        return calendarSyncDataSource;
    }

    private OfflineDataSource newChildDataSource(Type type) throws ServiceException {
        String pass = getDecryptedPassword();
        String suffix = "-" + type.name();
        Map<String, Object> attrs = new HashMap<String, Object>(getRawAttrs());
        String id = getId() + suffix;
        attrs.put(Provisioning.A_zimbraDataSourcePassword, encryptData(id, pass));
        return new OfflineDataSource(
            getAccount(), type, getName() + suffix, id, attrs, getProvisioning());
    }

    public DataSourceConfig.Service getKnownService() {
        return knownService;
    }

    public static DataSourceConfig getDataSourceConfig() {
        return config;
    }
    
    private static final String SERVICE_NAME_LIVE = "hotmail.com";
    private static final String SERVICE_NAME_YAHOO = "yahoo.com";
    private static final String SERVICE_NAME_GMAIL = "gmail.com";

    private static DataSourceConfig config;

    public static void init() throws IOException {
        File file = new File(OfflineLC.zdesktop_datasource_config.value());
        
        config = DataSourceConfig.read(file);
        for (DataSourceConfig.Service service : config.getServices()) {
            OfflineLog.offline.debug(
                "Loaded %d folder mappings for service '%s'",
                service.getFolders().size(), service.getName());
        }
        OfflineLog.offline.info("Loaded datasource configuration from '%s'", file);
    }

    private DataSourceConfig.Folder getKnownFolderByRemotePath(String remotePath) {
        return knownService != null ? knownService.getFolderByRemotePath(remotePath) : null;
    }

    private DataSourceConfig.Folder getKnownFolderByLocalPath(String localPath) {
        return knownService != null ? knownService.getFolderByLocalPath(localPath) : null;
    }

    public boolean isSyncEnabledByDefault(String localPath) {
        if (localPath.equalsIgnoreCase("/Inbox"))
            return true;
        DataSourceConfig.Folder kf = getKnownFolderByLocalPath(localPath);
        if (kf != null) return kf.isSync();
        return config.isSyncAllFolders() ||
            getBooleanAttr(OfflineConstants.A_zimbraDataSourceSyncAllServerFolders, false);
    }

    @Override
    public String mapRemoteToLocalPath(String remotePath) {
        DataSourceConfig.Folder kf = getKnownFolderByRemotePath(remotePath);
        return kf != null ? kf.getLocalPath() : null;
    }

    @Override
    public String mapLocalToRemotePath(String localPath) {
        DataSourceConfig.Folder kf = getKnownFolderByLocalPath(localPath);
        return kf != null ? kf.getRemotePath() : null;
    }

    @Override
    public boolean ignoreRemotePath(String remotePath) {
        DataSourceConfig.Folder kf = getKnownFolderByRemotePath(remotePath);
        if (kf != null) return kf.isIgnore();
        // Also ignore remote path that would conflict with known local path
        String localPath = "/" + remotePath;
        return getKnownFolderByLocalPath(localPath) != null;
    }

    @Override
    public boolean isSyncInboxOnly() {
        return !getBooleanAttr(OfflineConstants.A_zimbraDataSourceSyncAllServerFolders, false);
    }

    @Override
    public boolean isSyncEnabled(String localPath) {
        if (isSyncInboxOnly())
            return localPath.equalsIgnoreCase("/Inbox");
        try {
            DataSourceManager dsm = DataSourceManager.getInstance();
            Mailbox mbox = dsm.getMailbox(this);
            Folder folder = mbox.getFolderByPath(new OperationContext(mbox), localPath);
            if (folder != null)
                return dsm.isSyncEnabled(this, folder);
            else
                OfflineLog.offline.warn("local path " + localPath + " not found");
        } catch (ServiceException x) {
            OfflineLog.offline.warn(x);
        }
        return isSyncEnabledByDefault(localPath);
    }

    @Override
    public boolean isSaveToSent() {
        return getType() == Type.pop3 || knownService == null || knownService.isSaveToSent();
    }

    public boolean isLive() {
        return knownService != null && knownService.getName().equals(SERVICE_NAME_LIVE);
    }
    
    public boolean isYahoo() {
        return knownService != null && knownService.getName().equals(SERVICE_NAME_YAHOO);
    }
    
    public boolean isGmail() {
        return knownService != null && knownService.getName().equals(SERVICE_NAME_GMAIL);
    }
    
    @Override
    public void reportError(int itemId, String error, Exception e) {
        String data = "";
        try {
            // If this is a message, then indicate folder name
            Mailbox mbox = getMailbox();
            Message msg = mbox.getMessageById(null, itemId);
            Folder folder = mbox.getFolderById(null, msg.getFolderId());
            data = "Local folder: " + folder.getPath() + "\n";
        } catch (ServiceException ex) {
        }
        if (e instanceof CommandFailedException) {
            String req = ((CommandFailedException) e).getRequest();
            if (req != null) {
                data += "Failed request: " + req;
            }
        }
        try {
            SyncExceptionHandler.saveFailureReport((DesktopMailbox) getMailbox(), itemId, error, data, 0, e);
        } catch (ServiceException x) {
            // Ignore
        }
    }

    @Override
    public boolean isOffline() {
        return true;
    }

    @Override
    public long getSyncFrequency() {
        return getTimeInterval(OfflineProvisioning.A_zimbraDataSourceSyncFreq,
            OfflineConstants.DEFAULT_SYNC_FREQ);
    }

    @Override
    public boolean checkPendingMessages() throws ServiceException {
        DataSourceMailbox mbox = (DataSourceMailbox) getMailbox();
        
        return mbox.getFolderById(null, DesktopMailbox.ID_FOLDER_OUTBOX).getSize() > 0 &&
               mbox.sendPendingMessages(true) > 0;
    }
    
    public boolean isEmail() {
    	return getType() == Type.imap || getType() == Type.pop3;
    }
    
    public boolean isSmtpEnabled() {
        return getBooleanAttr(OfflineProvisioning.A_zimbraDataSourceSmtpEnabled, false);
    }

    public boolean needsSmtpAuth() {
        return isEmail() && isSmtpEnabled() && !isLive() && !isYahoo();
    }

    public boolean isCalendarSyncEnabled() {
        return getBooleanAttr(OfflineProvisioning.A_zimbraDataSourceCalendarSyncEnabled, false);
    }

    public boolean isContactSyncEnabled() {
        return getBooleanAttr(OfflineProvisioning.A_zimbraDataSourceContactSyncEnabled, false);
    }

    public void setContactSyncEnabled(boolean enabled) throws ServiceException {
        OfflineProvisioning op = (OfflineProvisioning) Provisioning.getInstance();
        op.setDataSourceAttribute(
            this, OfflineProvisioning.A_zimbraDataSourceContactSyncEnabled,
            enabled ? Provisioning.TRUE : Provisioning.FALSE);
    }

    public void setCalendarSyncEnabled(boolean enabled) throws ServiceException {
        OfflineProvisioning op = (OfflineProvisioning) Provisioning.getInstance();
        op.setDataSourceAttribute(
            this, OfflineProvisioning.A_zimbraDataSourceCalendarSyncEnabled,
            enabled ? Provisioning.TRUE : Provisioning.FALSE);
    }
    
    @Override
    public boolean isDebugTraceEnabled() {
        if (super.isDebugTraceEnabled())
            return true;
        boolean accountDebugTrace = false;
        try {
            accountDebugTrace = ((OfflineAccount)getAccount()).isDebugTraceEnabled();
        } catch (ServiceException x) {}
        return  accountDebugTrace;
    }

    /*
     * Returns true if the Yahoo email address associated with the specified
     * data source refers to a YMail small biz account, in which case we use
     * SMTP rather than Cascade to send the message.
     */
    public boolean isYBizmail() {
        if (!isYahoo()) return false;
        try {
            YMailClient ymc = new YMailClient(OfflineYAuth.authenticate(this));
            ymc.setTrace(isDebugTraceEnabled()); 
            return ymc.isBizMail(getEmailAddress());
        } catch (Exception e) {
            OfflineLog.ymail.warn(
                "Unable to get UserData for address: " + getEmailAddress(), e);
        }
        return false;
    }

    public Session getYBizmailSession() throws ServiceException {
        return LocalJMSession.getSession(
            OfflineLC.zdesktop_ybizmail_smtp_host.value(),
            OfflineLC.zdesktop_ybizmail_smtp_port.intValue(),
            true, // isAuthRequired
            getUsername(),
            getDecryptedPassword(),
            true, // useSSL
            false,// useProxy
            null, // proxyHost
            0,    // proxyPort
            isDebugTraceEnabled());
    }

    @Override
    public boolean isSyncNeeded() throws ServiceException {
        return getType() == Type.imap && ImapSync.isSyncNeeded(this);
    }

    @Override
    public void mailboxDeleted() {
        if (getType() == Type.imap) {
            ImapSync.reset(this.getId());
        }
    }
}

