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

import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.common.OfflineConstants;

public class OfflineDataSource extends DataSource {
	
	private String serviceName;
	
    OfflineDataSource(Account acct, DataSource.Type type, String name, String id, Map<String,Object> attrs) {
        super(acct, type, name, id, attrs);
        serviceName = getAttr(OfflineConstants.A_zimbraDataSourceDomain);
    }

    void setName(String name) {
        mName = name;
    }

    void setServiceName(String serviceName) {
    	this.serviceName = serviceName;
    }
    
    private static class KnownFolder {
    	String localPath; //zimbra path
    	String remotePath; //imap path
    	boolean autosync;
    }
    
    private static Map<String, KnownFolder[]> knownFolderMapping = new HashMap<String, KnownFolder[]>();
    
    private static class EProperties extends Properties {
    	
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
    private static final String PROP_DATASOURCE_COUNT = "datasource.count";
    private static final String PROP_SERVICENAME = "serviceName";
    private static final String PROP_KNOWNFOLDER = "knownFolder";
    private static final String PROP_KNOWNFOLDER_COUNT = "knownFolder.count";
    private static final String PROP_LOCAL = "local";
    private static final String PROP_REMOTE = "remote";
    private static final String PROP_AUTOSYNC = "autosync";
    
    public static void init() throws FileNotFoundException, IOException {
    	EProperties props = new EProperties();
    	props.load(new FileInputStream(OfflineLC.zdesktop_datasource_properties.value()));
    	
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
	    				kf.remotePath = props.getNumberedProperty(PROP_DATASOURCE, i, PROP_KNOWNFOLDER, j, PROP_REMOTE);
	    				kf.autosync = props.getNumberedPropertyAsBoolean(PROP_DATASOURCE, i, PROP_KNOWNFOLDER, j, PROP_AUTOSYNC, false);
	    				knownFolders[j] = kf;
	    			}
	    			knownFolderMapping.put(serviceName, knownFolders);
    			}
    		}
    	}
    }
    
	@Override
	public String matchKnownLocalPath(String remotePath) {
		KnownFolder[] knownFolders = knownFolderMapping.get(serviceName);
		if (knownFolders == null)
			return null;
		for (KnownFolder kf : knownFolders)
			if (remotePath.equalsIgnoreCase(kf.remotePath))
				return kf.localPath;
		return null;
	}

	@Override
	public String matchKnownRemotePath(String localPath) {
		KnownFolder[] knownFolders = knownFolderMapping.get(serviceName);
		if (knownFolders == null)
			return null;
		for (KnownFolder kf : knownFolders)
			if (localPath.equalsIgnoreCase(kf.localPath))
				return kf.remotePath;
		return null;
	}
}
