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
package com.zimbra.cs.offline;

import java.util.Timer;

import com.zimbra.common.localconfig.LC;

public class Offline {

    public static Timer sTimer = new Timer("Timer-Offline-Main", true);
    
    static {
    	//If we don't set this, DNS resolution is by default cached forever.  If one starts the offline server from one
    	//location and then moves to another network, the sync target address may change due to different route.
    	//This way it's set to
    	int ttl = 10;
    	try {
    		String dnsCacheTtl = LC.get("dns_cache_ttl");
    		if (dnsCacheTtl != null) {
    			ttl = Integer.parseInt(dnsCacheTtl);
    		}
    	} catch (Throwable t) {}
    	java.security.Security.setProperty("networkaddress.cache.ttl" , Integer.toString(ttl));
    }
}
