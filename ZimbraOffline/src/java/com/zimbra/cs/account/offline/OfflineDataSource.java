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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.mailbox.DesktopMailbox;
import com.zimbra.cs.mailbox.Flag;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.datasource.SyncState;

public class OfflineDataSource extends DataSource {
    private KnownService knownService;

    OfflineDataSource(Account acct, DataSource.Type type, String name, String id, Map<String,Object> attrs) {
        super(acct, type, name, id, attrs);
        setServiceName(getAttr(OfflineConstants.A_zimbraDataSourceDomain));
    }

    void setName(String name) {
        mName = name;
    }

    void setServiceName(String serviceName) {
    	knownService = serviceName == null ? null : knownServices.get(serviceName);
    }

    private static class KnownService {
        String name;
        boolean saveToSent;
        KnownFolder[] folders;
    }
    
    private static class KnownFolder {
    	String localPath; //zimbra path
    	String remotePath; //imap path
    	boolean isSyncEnabled;
    }
    
    private static Map<String, KnownService> knownServices = new HashMap<String, KnownService>();
    private static boolean isSyncAllFoldersByDefault = false;

    private static final String PROP_DATASOURCE = "datasource";
    private static final String PROP_SYNCALLFOLDERS = "datasource.syncAllFolders";
    private static final String PROP_DATASOURCE_COUNT = "datasource.count";
    private static final String PROP_SERVICENAME = "serviceName";
    private static final String PROP_KNOWNFOLDER = "knownFolder";
    private static final String PROP_SAVETOSENT = "saveToSent";
    private static final String PROP_KNOWNFOLDER_COUNT = "knownFolder.count";
    private static final String PROP_LOCAL = "local";
    private static final String PROP_REMOTE = "remote";
    private static final String PROP_SYNC = "sync";

    private static final String SERVICE_NAME_YAHOO = "yahoo.com";

    public static void init() throws IOException {
        EProperties props = new EProperties();
        props.load(new FileInputStream(OfflineLC.zdesktop_datasource_properties.value()));

        isSyncAllFoldersByDefault = props.getPropertyAsBoolean(PROP_SYNCALLFOLDERS, false);

        int dsCount = props.getPropertyAsInteger(PROP_DATASOURCE_COUNT, 0);
        for (int i = 0; i < dsCount; ++i) {
            String serviceName = props.getNumberedProperty(PROP_DATASOURCE, i, PROP_SERVICENAME);
            if (serviceName != null && serviceName.length() > 0) {
                int folderCount = props.getNumberedPropertyAsInteger(PROP_DATASOURCE, i, PROP_KNOWNFOLDER_COUNT, 0);
                if (folderCount > 0) {
                    KnownService ks = new KnownService();
                    ks.name = serviceName;
                    ks.saveToSent = "true".equalsIgnoreCase(
                        props.getNumberedProperty(PROP_DATASOURCE, i, PROP_SAVETOSENT, "true"));
                    ks.folders = new KnownFolder[folderCount];
                    for (int j = 0; j < folderCount; ++j) {
                        KnownFolder kf = new KnownFolder();
                        kf.localPath = props.getNumberedProperty(PROP_DATASOURCE, i, PROP_KNOWNFOLDER, j, PROP_LOCAL);
                        kf.localPath = ".ignore".equals(kf.localPath) ? "" : kf.localPath;
                        kf.remotePath = props.getNumberedProperty(PROP_DATASOURCE, i, PROP_KNOWNFOLDER, j, PROP_REMOTE);
                        kf.remotePath = ".ignore".equals(kf.remotePath) ? "" : kf.remotePath;
                        kf.isSyncEnabled = props.getNumberedPropertyAsBoolean(PROP_DATASOURCE, i, PROP_KNOWNFOLDER, j, PROP_SYNC, false);
                        ks.folders[j] = kf;
                    }
                    knownServices.put(serviceName, ks);
                }
            }
        }
    }

    private KnownFolder getKnownFolderByRemotePath(String remotePath) {
        if (knownService != null && knownService.folders != null)
            for (KnownFolder kf : knownService.folders)
                if (remotePath.equals(kf.remotePath))
                    return kf;
        return null;
    }

    private KnownFolder getKnownFolderByLocalPath(String localPath) {
        if (knownService != null && knownService.folders != null)
            for (KnownFolder kf : knownService.folders)
                if (localPath.equals(kf.localPath))
                    return kf;
        return null;
    }

    private boolean isSyncEnabledByDefault(String localPath) {
        if (localPath.equalsIgnoreCase("/Inbox"))
            return true;
        KnownFolder kf = getKnownFolderByLocalPath(localPath);
        return kf == null ? isSyncAllFoldersByDefault || getBooleanAttr(OfflineConstants.A_zimbraDataSourceSyncAllServerFolders, false) : kf.isSyncEnabled;
    }

    @Override
    public String matchKnownLocalPath(String remotePath) {
        KnownFolder kf = getKnownFolderByRemotePath(remotePath);
        return kf == null ? null : kf.localPath;
    }

    @Override
    public String matchKnownRemotePath(String localPath) {
        KnownFolder kf = getKnownFolderByLocalPath(localPath);
        return kf == null ? null : kf.remotePath;
    }

    @Override
    public void initializedLocalFolder(String localPath, boolean isLocallyCreated) {
        try {
            DesktopMailbox mbox = (DesktopMailbox)MailboxManager.getInstance().getMailboxByAccount(getAccount());
            Folder folder = mbox.getFolderByPath(new Mailbox.OperationContext(mbox), localPath);
            if (folder != null) {
                if (folder.getId() == DesktopMailbox.ID_FOLDER_OUTBOX || folder.getId() == DesktopMailbox.ID_FOLDER_ARCHIVE || folder.getId() == DesktopMailbox.ID_FOLDER_FAILURE)
                    return;

                OperationContext context = new Mailbox.OperationContext(mbox);
                mbox.alterTag(context, folder.getId(), MailItem.TYPE_FOLDER, Flag.ID_FLAG_SYNCFOLDER, true);
                mbox.alterTag(context, folder.getId(), MailItem.TYPE_FOLDER, Flag.ID_FLAG_SYNC, isLocallyCreated ? false : isSyncEnabledByDefault(localPath));
            } else
                OfflineLog.offline.warn("local path " + localPath + " not found");
        } catch (ServiceException x) {
            OfflineLog.offline.warn(x);
        }
    }

    @Override
    public boolean isSyncEnabled(String localPath) {
        try {
            Mailbox mbox = getMailbox();
            Folder folder = mbox.getFolderByPath(new Mailbox.OperationContext(mbox), localPath);
            if (folder != null)
                return (folder.getFlagBitmask() & Flag.BITMASK_SYNCFOLDER) != 0 && (folder.getFlagBitmask() & Flag.BITMASK_SYNC) != 0;
            else
                OfflineLog.offline.warn("local path " + localPath + " not found");
        } catch (ServiceException x) {
            OfflineLog.offline.warn(x);
        }
        return isSyncEnabledByDefault(localPath);
    }

    public boolean isSaveToSent() {
        return knownService != null && knownService.saveToSent;
    }

    public boolean isYahoo() {
        return knownService != null && knownService.name.equals(SERVICE_NAME_YAHOO);
    }
    
    private static final int MAX_UID_ENTRIES = 64 * 1024;

    private static final Map<Object, SyncState> sSyncStateMap =
        Collections.synchronizedMap(new LinkedHashMap<Object, SyncState>() {
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > MAX_UID_ENTRIES;
            }
        });

    @Override
    public SyncState getSyncState(int folderId) {
        Object key = key(folderId);
        return key != null ? sSyncStateMap.remove(key) : null;
    }
    
    @Override
    public void putSyncState(int folderId, SyncState ss) {
        Object key = key(folderId);
        if (key != null) {
            sSyncStateMap.put(key, ss);
        }
    }

    @Override
    public void clearSyncState(int folderId) {
        Object key = key(folderId);
        if (key != null) {
            sSyncStateMap.remove(key);
        }
    }

    private Object key(int folderId) {
        try {
            int mailboxId = getMailbox().getId();
            return (long) mailboxId << 32 | ((long) folderId & 0xffffffffL);
        } catch (ServiceException e) {
            return null;
        }
    }

    @Override
    public boolean isOffline() {
        return true;
    }
}
