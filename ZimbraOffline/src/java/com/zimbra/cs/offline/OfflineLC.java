package com.zimbra.cs.offline;

import com.zimbra.common.localconfig.KnownKey;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.util.BuildInfo;

public class OfflineLC {
	
	public static final KnownKey zdesktop_app_id;
	public static final KnownKey zdesktop_version;
    public static final KnownKey zdesktop_skins;
    public static final KnownKey zdesktop_derby_log;
    
    public static final KnownKey zdesktop_dirsync_freq;
    public static final KnownKey zdesktop_dirsync_min_delay;
    public static final KnownKey zdesktop_account_poll_interval;
    
    public static final KnownKey zdesktop_retry_limit;
    
    public static final KnownKey zdesktop_sync_batch_size;
    
    public static final KnownKey http_so_timeout;
    public static final KnownKey http_connection_timeout;
    public static final KnownKey dns_cache_ttl;
    public static final KnownKey auth_token_lifetime;

    static void init() {
        // This method is there to guarantee static initializer of this
        // class is run.
    }

    static {
    	zdesktop_app_id = new KnownKey("zdesktop_app_id");

        zdesktop_version = new KnownKey("zdesktop_version");
        zdesktop_version.setDefault("ZCS " + BuildInfo.VERSION);
        zdesktop_version.setDoc("Version number of the Zimbra Desktop software.");

	    zdesktop_skins = new KnownKey("zdesktop_skins");
	    zdesktop_skins.setDefault("sand");
	    zdesktop_skins.setDoc("Comma delimited list of installed skins.");
	    
	    zdesktop_derby_log = new KnownKey("zdesktop_derby_log");
	    zdesktop_derby_log.setDefault("false");
	    zdesktop_derby_log.setDoc("Whether to enable derby debug logging. Default false");
	    
	    zdesktop_dirsync_freq = new KnownKey("zdesktop_dirsync_freq");
	    zdesktop_dirsync_freq.setDefault(Long.toString(Constants.MILLIS_PER_MINUTE));
	    zdesktop_dirsync_freq.setDoc("Directory sync task schedule frequency in milliseconds. Default 60000 (1 minute).");
    	
	    zdesktop_dirsync_min_delay = new KnownKey("zdesktop_dirsync_min_delay");
	    zdesktop_dirsync_min_delay.setDefault(Long.toString(15 * Constants.MILLIS_PER_SECOND));
	    zdesktop_dirsync_min_delay.setDoc("Minimum delay in milliseconds between two directory sync executions. Default 15000 (15 seconds)");
	    
	    zdesktop_account_poll_interval = new KnownKey("zdesktop_account_poll_interval");
	    zdesktop_account_poll_interval.setDefault(Long.toString(Constants.MILLIS_PER_HOUR));
	    zdesktop_account_poll_interval.setDoc("Minimum delay in milliseconds between two directory sync executions for the same account. Default 3600000 (1 hour).");
	    
	    zdesktop_retry_limit = new KnownKey("zdesktop_retry_limit");
	    zdesktop_retry_limit.setDefault("2");
	    zdesktop_retry_limit.setDoc("Number of times to retry if sync fails. Default 2.");
	    
	    zdesktop_sync_batch_size = new KnownKey("zdesktop_sync_batch_size");
	    zdesktop_sync_batch_size.setDefault("100");
	    zdesktop_sync_batch_size.setDoc("Max number of messages to download in each transaction. Default 100.");
	    
	    auth_token_lifetime = new KnownKey("auth_token_lifetime");
	    auth_token_lifetime.setDefault("31536000"); //365 * 24 * 3600
	    auth_token_lifetime.setDoc("Number of seconds before auth token expires. Default 31536000 (1 year).");
	    
	    dns_cache_ttl = new KnownKey("dns_cache_ttl");
	    dns_cache_ttl.setDefault("10");
	    dns_cache_ttl.setDoc("Number of seconds a resolved address stays valid");

	    http_so_timeout = new KnownKey("http_so_timeout");
	    http_so_timeout.setDefault("6000");
	    http_so_timeout.setDoc("Socket timeout (SO_TIMEOUT) in milliseconds while waiting for data. A value of zero means no timeout. Default 6000 (6 seconds).");

	    http_connection_timeout = new KnownKey("http_connection_timeout");
	    http_connection_timeout.setDefault("6000");
	    http_connection_timeout.setDoc("Timeout in milliseconds while waiting for connection to establish. A value of zero means no timeout. Default 6000 (6 seconds).");
    }
}
