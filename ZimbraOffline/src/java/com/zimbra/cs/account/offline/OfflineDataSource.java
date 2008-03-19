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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.mailbox.Flag;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.common.OfflineConstants;

public class OfflineDataSource extends DataSource {
	
	private KnownFolder[] knownFolders;
	
    OfflineDataSource(Account acct, DataSource.Type type, String name, String id, Map<String,Object> attrs) {
        super(acct, type, name, id, attrs);
        setServiceName(getAttr(OfflineConstants.A_zimbraDataSourceDomain));
    }

    void setName(String name) {
        mName = name;
    }

    void setServiceName(String serviceName) {
    	knownFolders = serviceName == null ? null : knownFolderMapping.get(serviceName);
    }
    
    private static class KnownFolder {
    	String localPath; //zimbra path
    	String remotePath; //imap path
    	boolean isSyncEnabled;
    }
    
    private static Map<String, KnownFolder[]> knownFolderMapping = new HashMap<String, KnownFolder[]>();
    private static boolean isSyncAllFoldersByDefault = false;
    
    private static class EProperties extends Properties {
    	
		private static final long serialVersionUID = -8135956477865965194L;

		@Override
		public String getProperty(String key, String defaultValue) {
			String val = super.getProperty(key, defaultValue);
			return val == null ? null : val.trim();
		}

		@Override
		public String getProperty(String key) {
			String val = super.getProperty(key);
			return val == null ? null : val.trim();
		}

		public int getPropertyAsInteger(String key, int defaultValue) {
			String val = getProperty(key);
			if (val == null || val.length() == 0)
				return defaultValue;
			try {
				return Integer.parseInt(val);
			} catch (NumberFormatException x) {
				return defaultValue;
			}
		}
		
		public boolean getPropertyAsBoolean(String key, boolean defaultValue) {
			String val = getProperty(key);
			if (val == null || val.length() == 0)
				return defaultValue;
			return Boolean.parseBoolean(val);
		}
		
		public String getNumberedProperty(String prefix, int number, String suffix) {
			return getProperty(prefix + '.' + number + '.' + suffix);
		}
		
		public int getNumberedPropertyAsInteger(String prefix, int number, String suffix, int defaultValue) {
			String val = getNumberedProperty(prefix, number, suffix);
			if (val == null || val.length() == 0)
				return defaultValue;
			try {
				return Integer.parseInt(val);
			} catch (NumberFormatException x) {
				return defaultValue;
			}
		}
		
		public String getNumberedProperty(String prefix, int n1, String midfix, int n2, String suffix) {
			return getProperty(prefix + '.' + n1 + '.' + midfix + '.' + n2 + '.' + suffix);
		}
		
		public boolean getNumberedPropertyAsBoolean(String prefix, int n1, String midfix, int n2, String suffix, boolean defaultValue) {
			String val = getProperty(prefix + '.' + n1 + '.' + midfix + '.' + n2 + '.' + suffix);
			if (val == null || val.length() == 0)
				return defaultValue;
			return Boolean.parseBoolean(val);
		}
    }
    
    private static final String PROP_DATASOURCE = "datasource";
    private static final String PROP_SYNCALLFOLDERS = "datasource.syncAllFolders";
    private static final String PROP_DATASOURCE_COUNT = "datasource.count";
    private static final String PROP_SERVICENAME = "serviceName";
    private static final String PROP_KNOWNFOLDER = "knownFolder";
    private static final String PROP_KNOWNFOLDER_COUNT = "knownFolder.count";
    private static final String PROP_LOCAL = "local";
    private static final String PROP_REMOTE = "remote";
    private static final String PROP_SYNC = "sync";
    
    public static void init() throws FileNotFoundException, IOException {
    	EProperties props = new EProperties();
    	props.load(new FileInputStream(OfflineLC.zdesktop_datasource_properties.value()));
    	
    	isSyncAllFoldersByDefault = props.getPropertyAsBoolean(PROP_SYNCALLFOLDERS, false);
    	
    	int dsCount = props.getPropertyAsInteger(PROP_DATASOURCE_COUNT, 0);
    	for (int i = 0; i < dsCount; ++i) {
    		String serviceName = props.getNumberedProperty(PROP_DATASOURCE, i, PROP_SERVICENAME);
    		if (serviceName != null && serviceName.length() > 0) {
    			int folderCount = props.getNumberedPropertyAsInteger(PROP_DATASOURCE, i, PROP_KNOWNFOLDER_COUNT, 0);
    			if (folderCount > 0) {
	    			KnownFolder[] knownFolders = new KnownFolder[folderCount];
	    			for (int j = 0; j < folderCount; ++j) {
	    				KnownFolder kf = new KnownFolder();
	    				kf.localPath = props.getNumberedProperty(PROP_DATASOURCE, i, PROP_KNOWNFOLDER, j, PROP_LOCAL);
	    				kf.localPath = ".ignore".equals(kf.localPath) ? "" : kf.localPath;
	    				kf.remotePath = props.getNumberedProperty(PROP_DATASOURCE, i, PROP_KNOWNFOLDER, j, PROP_REMOTE);
	    				kf.remotePath = ".ignore".equals(kf.remotePath) ? "" : kf.remotePath;
	    				kf.isSyncEnabled = props.getNumberedPropertyAsBoolean(PROP_DATASOURCE, i, PROP_KNOWNFOLDER, j, PROP_SYNC, false);
	    				knownFolders[j] = kf;
	    			}
	    			knownFolderMapping.put(serviceName, knownFolders);
    			}
    		}
    	}
    }
    
    private KnownFolder getKnownFolderByRemotePath(String remotePath) {
    	if (knownFolders != null)
    		for (KnownFolder kf : knownFolders)
    			if (remotePath.equals(kf.remotePath))
    				return kf;
    	return null;
    }
    
    private KnownFolder getKnownFolderByLocalPath(String localPath) {
    	if (knownFolders != null)
    		for (KnownFolder kf : knownFolders)
    			if (localPath.equals(kf.localPath))
    				return kf;
    	return null;
    }
    
	private boolean isSyncEnabledByDefault(String localPath) {
		if (localPath.equalsIgnoreCase("/Inbox"))
			return true;
		KnownFolder kf = getKnownFolderByLocalPath(localPath);
		return kf == null ? isSyncAllFoldersByDefault : kf.isSyncEnabled;
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
	public void initializedLocalFolder(String localPath) {
		try {
			Mailbox mbox = MailboxManager.getInstance().getMailboxByAccount(getAccount());
			Folder folder = mbox.getFolderByPath(new Mailbox.OperationContext(mbox), localPath);
			if (folder != null) {
				OperationContext context = new Mailbox.OperationContext(mbox); 
				mbox.alterTag(context, folder.getId(), MailItem.TYPE_FOLDER, Flag.ID_FLAG_SYNCFOLDER, true);
				mbox.alterTag(context, folder.getId(), MailItem.TYPE_FOLDER, Flag.ID_FLAG_SYNC, isSyncEnabledByDefault(localPath));
			} else
				OfflineLog.offline.warn("local path " + localPath + " not found");
		} catch (ServiceException x) {
			OfflineLog.offline.warn(x);
		}
	}

	@Override
	public boolean isSyncEnabled(String localPath) {
		try {
			Mailbox mbox = MailboxManager.getInstance().getMailboxByAccount(getAccount());
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
}
