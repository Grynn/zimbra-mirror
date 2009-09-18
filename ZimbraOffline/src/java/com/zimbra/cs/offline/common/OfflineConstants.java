/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009 Zimbra, Inc.
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
package com.zimbra.cs.offline.common;

import org.dom4j.Namespace;
import org.dom4j.QName;

import com.zimbra.common.util.Constants;

public interface OfflineConstants {

    public static final String NAMESPACE_STR = "urn:zimbraOffline";
    public static final Namespace NAMESPACE = Namespace.get(NAMESPACE_STR);

    public static final QName SYNC_REQUEST = QName.get("SyncRequest", NAMESPACE);
    public static final QName SYNC_RESPONSE = QName.get("SyncResponse", NAMESPACE);
    
    public static final QName CLIENT_EVENT_NOTIFY_REQUEST = QName.get("ClientEventNotifyRequest", NAMESPACE);
    public static final QName CLIENT_EVENT_NOTIFY_RESPONSE = QName.get("ClientEventNotifyResponse", NAMESPACE);
	
    public static final String A_Event = "e";
    public static final String EVENT_UI_LOAD_BEGIN = "ui_load_begin";
    public static final String EVENT_UI_LOAD_END = "ui_load_end";

    public static enum SyncStatus {
        unknown, offline, online, running, authfail, error
    }

    public static final String A_offlineAccountsOrder = "offlineAccountsOrder";
    
    public static final String A_offlineRemoteServerVersion = "offlineRemoteServerVersion";
    public static final String A_offlineRemotePassword = "offlineRemotePassword";
    public static final String A_offlineRemoteServerUri = "offlineRemoteServerUri";
    public static final String A_offlineWebappUri = "offlineWebappUri";
    
    public static final String A_offlineProxyHost = "offlineProxyHost";
    public static final String A_offlineProxyPort = "offlineProxyPort";
    public static final String A_offlineProxyUser = "offlineProxyUser";
    public static final String A_offlineProxyPass = "offlineProxyPass";

    public static final String A_offlineAccountName = "offlineAccountName";
    public static final String A_offlineAccountFlavor = "offlineAccountFlavor"; 

    public static final String A_offlineDataSourceType = "offlineDataSourceType";
    public static final String A_offlineDataSourceName = "offlineDataSourceName";
    
    public static final String A_offlineFeatureSmtpEnabled = "offlineFeatureSmtpEnabled";

    public static final String A_offlineGalAccountId = "offlineGalAccountId";
    public static final String A_offlineGalAccountSyncToken = "offlineGalAccountSyncToken";
    public static final String A_offlineGalAccountLastFullSync = "offlineGalAccountLastFullSync";
    
    public static final String A_offlineSyncFreq = "offlineSyncFreq";
    public static final String A_offlineSyncStatus = "offlineSyncStatus";
    public static final String A_offlineSyncStatusErrorCode = "offlineSyncStatusErrorCode";
    public static final String A_offlineSyncStatusErrorMsg = "offlineSyncStatusErrorMsg";
    public static final String A_offlineSyncStatusException = "offlineSyncStatusException";
    
    public static final String A_offlineLastSync = "offlineLastSync";
    public static final String A_offlineEnableTrace = "offlineEnableTrace";
    
    public static final String A_offlineSslCertAlias = "offlineSslCertAlias";
    public static final String A_offlineAccountSetup = "offlineAccountSetup";
                                                         
    public static final String A_zimbraDataSourceSmtpEnabled = "zimbraDataSourceSmtpEnabled";
    public static final String A_zimbraDataSourceSmtpHost = "zimbraDataSourceSmtpHost";
    public static final String A_zimbraDataSourceSmtpPort = "zimbraDataSourceSmtpPort";
    public static final String A_zimbraDataSourceSmtpConnectionType = "zimbraDataSourceSmtpConnectionType";
    public static final String A_zimbraDataSourceSmtpAuthRequired = "zimbraDataSourceSmtpAuthRequired";
    public static final String A_zimbraDataSourceSmtpAuthUsername = "zimbraDataSourceSmtpAuthUsername";
    public static final String A_zimbraDataSourceSmtpAuthPassword = "zimbraDataSourceSmtpAuthPassword";
    
    public static final String A_zimbraDataSourceUseProxy = "zimbraDataSourceUseProxy";
    public static final String A_zimbraDataSourceProxyHost = "zimbraDataSourceProxyHost";
    public static final String A_zimbraDataSourceProxyPort = "zimbraDataSourceProxyPort";
    
    public static final String A_zimbraDataSourceSyncFreq = "zimbraDataSourceSyncFreq";
    public static final String A_zimbraDataSourceSyncStatus = "zimbraDataSourceSyncStatus";
    public static final String A_zimbraDataSourceSyncStatusErrorCode = "A_zimbraDataSourceSyncStatusErrorCode";
    public static final String A_zimbraDataSourceLastSync = "zimbraDataSourceLastSync";

    public static final String A_zimbraDataSourceContactSyncEnabled = "zimbraDataSourceContactSyncEnabled";
    public static final String A_zimbraDataSourceCalendarSyncEnabled = "zimbraDataSourceCalendarSyncEnabled";
    public static final String A_zimbraDataSourceCalendarFolderId = "zimbraDataSourceCalendarFolderId";
    
    public static final String A_zimbraDataSourceSyncAllServerFolders = "zimbraDataSourceSyncAllServerFolders";
    
    public static final String A_zimbraDataSourceSslCertAlias = "zimbraDataSourceSslCertAlias";
    public static final String A_zimbraDataSourceAccountSetup = "zimbraDataSourceAccountSetup";
    
    public static final long DEFAULT_SYNC_FREQ = 15 * Constants.MILLIS_PER_MINUTE;
    public static final long MIN_SYNC_FREQ = Constants.MILLIS_PER_MINUTE;
    
    public static final String LOCAL_ACCOUNT_ID = "ffffffff-ffff-ffff-ffff-ffffffffffff";    
    public static final String GAL_ACCOUNT_SUFFIX = "__OFFLINE_GAL__";
    public static final String GAL_LDAP_DN = "GAL_LDAP_DN";
    public static final String YMAIL_PARTNER_NAME = "Zimbra";
    public static final String CALDAV_DS = "caldav:";
    public static final String YAB_DS = "yab:";
    public static final String SYNC_SERVER_PREFIX = "offline_sync_server_";
}
