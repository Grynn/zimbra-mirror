/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline;

import java.util.Timer;

import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.apache.commons.httpclient.params.HttpConnectionParams;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapTransport;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineProvisioning;

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

    	//Set a bunch of HttpClient connection/socket parameters for offline specific tuning
    	
    	int soTimeoutMs = 6000;
    	try {
    		String httpSoTimeout = LC.get("http_so_timeout");
    		if (httpSoTimeout != null) {
    			soTimeoutMs = Integer.parseInt(httpSoTimeout);
    		}
    	} catch (Throwable t) {}
    	DefaultHttpParams.getDefaultParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, soTimeoutMs);
    	
    	int connectionTimeoutMs = 6000;
    	try {
    		String httpConnectionTimeout = LC.get("http_connection_timeout");
    		if (httpConnectionTimeout != null) {
    			connectionTimeoutMs = Integer.parseInt(httpConnectionTimeout);
    		}
    	} catch (Throwable t) {}
    	DefaultHttpParams.getDefaultParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, connectionTimeoutMs);
    }

    public static class OfflineDebugListener implements SoapTransport.DebugListener {
        public void sendSoapMessage(Element envelope)     { OfflineLog.request.debug(getPayload(envelope)); }
        public void receiveSoapMessage(Element envelope)  { OfflineLog.response.debug(getPayload(envelope)); }

        private Element getPayload(Element soap) {
            Element body = soap.getOptionalElement("Body");
            if (body != null && !body.listElements().isEmpty())            return body.listElements().get(0);
            if (body == null && soap.getOptionalElement("Fault") != null)  return soap.getOptionalElement("Fault");
            return soap;
        }
    }

    public static String getServerURI(Account acct, String service) {
        return getServerURI(acct.getAttr(OfflineProvisioning.A_offlineRemoteServerUri), service);
    }

    public static String getServerURI(String baseUri, String service) {
        if (baseUri == null)
            return null;
        else if (baseUri.endsWith("/") && service.startsWith("/"))
            return baseUri + service.substring(1);
        else if (!baseUri.endsWith("/") && !service.startsWith("/"))
            return baseUri + '/' + service;
        else
            return baseUri + service;
    }
}
