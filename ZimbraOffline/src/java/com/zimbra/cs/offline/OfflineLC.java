/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
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
package com.zimbra.cs.offline;

import java.io.File;

import com.zimbra.common.localconfig.KnownKey;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.util.BuildInfo;

public class OfflineLC {
	
	public static final KnownKey zdesktop_app_id;
	public static final KnownKey zdesktop_name;
	public static final KnownKey zdesktop_version;
    public static final KnownKey zdesktop_skins;
    public static final KnownKey zdesktop_derby_properties;
    
    public static final KnownKey zdesktop_sync_timer_frequency;
    public static final KnownKey zdesktop_dirsync_min_delay;
    public static final KnownKey zdesktop_dirsync_fail_delay;
    public static final KnownKey zdesktop_account_poll_interval;
    public static final KnownKey zdesktop_reauth_delay;
    public static final KnownKey zdesktop_retry_delay_min;
    public static final KnownKey zdesktop_retry_delay_max;
    
    public static final KnownKey zdesktop_client_poll_interval;
    
    public static final KnownKey zdesktop_retry_limit;
    public static final KnownKey zdesktop_enable_push;
    
    public static final KnownKey zdesktop_sync_batch_size;
    public static final KnownKey zdesktop_sync_zip_level;
    
    public static final KnownKey zdesktop_sync_messages;
    public static final KnownKey zdesktop_sync_contacts;
    public static final KnownKey zdesktop_sync_appointments;
    public static final KnownKey zdesktop_sync_chats;
    
    public static final KnownKey zdesktop_sync_skip_idlist;
    
    public static final KnownKey zdesktop_request_timeout;
    public static final KnownKey http_so_timeout;
    public static final KnownKey http_connection_timeout;
    public static final KnownKey dns_cache_ttl;
    
    public static final KnownKey auth_token_lifetime;
    
    public static final KnownKey zdesktop_datasource_properties;
    
    public static final KnownKey zdesktop_membuf_limit;
    
    public static final KnownKey zdesktop_yauth_appid;
    public static final KnownKey zdesktop_yauth_baseuri;
    public static final KnownKey zdesktop_yab_baseuri;

    static void init() {
        // This method is there to guarantee static initializer of this
        // class is run.
    }

    static {
    	zdesktop_app_id = new KnownKey("zdesktop_app_id");

        zdesktop_name = new KnownKey("zdesktop_name");
        zdesktop_name.setDefault("Zimbra Desktop");
        zdesktop_name.setDoc("UserAgent name of the Zimbra Desktop software.");
    	
        zdesktop_version = new KnownKey("zdesktop_version");
        zdesktop_version.setDefault("ZCS " + BuildInfo.FULL_VERSION);
        zdesktop_version.setDoc("Version number of the Zimbra Desktop software.");

	    zdesktop_skins = new KnownKey("zdesktop_skins");
	    zdesktop_skins.setDefault("sand");
	    zdesktop_skins.setDoc("Comma delimited list of installed skins.");
	    
	    zdesktop_derby_properties = new KnownKey("zdesktop_derby_properties");
	    zdesktop_derby_properties.setDefault("${zimbra_home}" + File.separator + "conf" + File.separator + "derby.properties");
	    zdesktop_derby_properties.setDoc("Path to derby configuration properties file.");
    	
	    zdesktop_sync_timer_frequency = new KnownKey("zdesktop_sync_timer_frequency");
	    zdesktop_sync_timer_frequency.setDefault(Long.toString(5 * Constants.MILLIS_PER_SECOND));
	    zdesktop_sync_timer_frequency.setDoc("Main sync loop timer firing frequency. Default 5000 (5 seconds)");    
	    
	    zdesktop_dirsync_min_delay = new KnownKey("zdesktop_dirsync_min_delay");
	    zdesktop_dirsync_min_delay.setDefault(Long.toString(15 * Constants.MILLIS_PER_SECOND));
	    zdesktop_dirsync_min_delay.setDoc("Minimum delay in milliseconds between two directory sync executions. Default 15000 (15 seconds)");
    	
	    zdesktop_dirsync_fail_delay = new KnownKey("zdesktop_dirsync_fail_delay");
	    zdesktop_dirsync_fail_delay.setDefault(Long.toString(15 * Constants.MILLIS_PER_MINUTE));
	    zdesktop_dirsync_fail_delay.setDoc("Minimum delay in milliseconds after a directory sync failure. Default 900000 (15 minutes)");
	    
	    zdesktop_account_poll_interval = new KnownKey("zdesktop_account_poll_interval");
	    zdesktop_account_poll_interval.setDefault(Long.toString(Constants.MILLIS_PER_HOUR));
	    zdesktop_account_poll_interval.setDoc("Minimum delay in milliseconds between two directory sync executions for the same account. Default 3600000 (1 hour).");
	    
	    zdesktop_client_poll_interval = new KnownKey("zdesktop_client_poll_interval");
	    zdesktop_client_poll_interval.setDefault("60");
	    zdesktop_client_poll_interval.setDoc("How often Ajax client should poll for updates. Default 15 (seconds).");
	    
	    zdesktop_reauth_delay = new KnownKey("zdesktop_reauth_delay");
	    zdesktop_reauth_delay.setDefault(Long.toString(Constants.MILLIS_PER_HOUR));
	    zdesktop_reauth_delay.setDoc("Minimum delay in milliseconds to reauth after auth failure. Default 3600000 (1 hour).");
	    
	    zdesktop_retry_delay_min = new KnownKey("zdesktop_retry_delay_min");
	    zdesktop_retry_delay_min.setDefault(Long.toString(Constants.MILLIS_PER_MINUTE));
	    zdesktop_retry_delay_min.setDoc("Minimum delay in milliseconds to retry after becoming offline or encountering error. Default 60000 (1 minute).");
	    
	    zdesktop_retry_delay_max = new KnownKey("zdesktop_retry_delay_max");
	    zdesktop_retry_delay_max.setDefault(Long.toString(10 * Constants.MILLIS_PER_MINUTE));
	    zdesktop_retry_delay_max.setDoc("Maximum delay in milliseconds to retry after becoming offline or encountering error. Default 600000 (10 minutes).");
	    
	    zdesktop_retry_limit = new KnownKey("zdesktop_retry_limit");
	    zdesktop_retry_limit.setDefault("2");
	    zdesktop_retry_limit.setDoc("Number of times to retry if sync fails. Default 2.");
	    
	    zdesktop_enable_push = new KnownKey("zdesktop_enable_push");
	    zdesktop_enable_push.setDefault("true");
	    zdesktop_enable_push.setDoc("Whether to use server push notification. Default true.");
	    
	    zdesktop_sync_batch_size = new KnownKey("zdesktop_sync_batch_size");
	    zdesktop_sync_batch_size.setDefault("100");
	    zdesktop_sync_batch_size.setDoc("Max number of messages to download in each transaction. Default 100.");
	    
	    zdesktop_sync_zip_level = new KnownKey("zdesktop_sync_zip_level");
	    zdesktop_sync_zip_level.setDefault("0");
	    zdesktop_sync_zip_level.setDoc("Zip compression level for batch message sync. Default 0 (NO_COMPRESSION).");
	    
	    zdesktop_sync_messages = new KnownKey("zdesktop_sync_messages");
	    zdesktop_sync_messages.setDefault("true");
	    zdesktop_sync_messages.setDoc("Whether to sync messages. Default true");
	    
	    zdesktop_sync_contacts = new KnownKey("zdesktop_sync_contacts");
	    zdesktop_sync_contacts.setDefault("true");
	    zdesktop_sync_contacts.setDoc("Whether to sync contacts. Default true");
	    
	    zdesktop_sync_appointments = new KnownKey("zdesktop_sync_appointments");
	    zdesktop_sync_appointments.setDefault("true");
	    zdesktop_sync_appointments.setDoc("Whether to sync appointments. Default true");
	    
	    zdesktop_sync_chats = new KnownKey("zdesktop_sync_chats");
	    zdesktop_sync_chats.setDefault("true");
	    zdesktop_sync_chats.setDoc("Whether to sync chats. Default true");
	    
	    zdesktop_sync_skip_idlist = new KnownKey("zdesktop_sync_skip_idlist");
	    zdesktop_sync_skip_idlist.setDefault("");
	    zdesktop_sync_skip_idlist.setDoc("Comma delimited list of item IDs to skip during sync.  Default empty.");
	    
	    auth_token_lifetime = new KnownKey("auth_token_lifetime");
	    auth_token_lifetime.setDefault("31536000"); //365 * 24 * 3600
	    auth_token_lifetime.setDoc("Number of seconds before auth token expires. Default 31536000 (1 year).");
	    
	    dns_cache_ttl = new KnownKey("dns_cache_ttl");
	    dns_cache_ttl.setDefault("10");
	    dns_cache_ttl.setDoc("Number of seconds a resolved address stays valid");

	    zdesktop_request_timeout = new KnownKey("zdesktop_request_timeout");
	    zdesktop_request_timeout.setDefault("15000");
	    zdesktop_request_timeout.setDoc("HTTP request timeout in milliseconds while waiting for response. A value of zero means no timeout. Default 15000 (15 seconds).");
	    
	    http_so_timeout = new KnownKey("http_so_timeout");
	    http_so_timeout.setDefault("15000");
	    http_so_timeout.setDoc("Socket timeout (SO_TIMEOUT) in milliseconds while waiting for data. A value of zero means no timeout. Default 15000 (15 seconds).");

	    http_connection_timeout = new KnownKey("http_connection_timeout");
	    http_connection_timeout.setDefault("15000");
	    http_connection_timeout.setDoc("Timeout in milliseconds while waiting for connection to establish. A value of zero means no timeout. Default 15000 (15 seconds).");
	    
	    zdesktop_datasource_properties = new KnownKey("zdesktop_datasource_properties");
	    zdesktop_datasource_properties.setDefault("${zimbra_home}" + File.separator + "conf" + File.separator + "datasource.properties");
	    zdesktop_datasource_properties.setDoc("Path to datasource configuration properties file.");
	    
	    zdesktop_membuf_limit = new KnownKey("zdesktop_membuf_limit");
	    zdesktop_membuf_limit.setDefault("4194304"); //4 * 1024 * 1024
	    zdesktop_membuf_limit.setDoc("Number of bytes to hold in memory before start disk streaming during message sync.");
	    
	    zdesktop_yauth_appid = new KnownKey("zdesktop_yauth_appid");
	    zdesktop_yauth_appid.setDefault("D2hTUBHAkY0IEL5MA7ibTS_1K86E8RErSSaTGn4-");
	    zdesktop_yauth_appid.setDoc("appid for yauth with rw access to ab");
	    
	    zdesktop_yauth_baseuri = new KnownKey("zdesktop_yauth_baseuri");
	    zdesktop_yauth_baseuri.setDefault("https://login.yahoo.com/WSLogin/V1");
	    zdesktop_yauth_baseuri.setDoc("base uri for yauth");
	    
	    zdesktop_yab_baseuri = new KnownKey("zdesktop_yab_baseuri");
	    zdesktop_yab_baseuri.setDefault("http://address.yahooapis.com/v1");
	    zdesktop_yab_baseuri.setDoc("base uri for yab");
    }
}
