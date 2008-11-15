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
package com.zimbra.cs.offline;

import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.apache.commons.httpclient.params.HttpConnectionParams;

import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapTransport;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;

public class Offline {
    static {
    	//If we don't set this, DNS resolution is by default cached forever.  If one starts the offline server from one
    	//location and then moves to another network, the sync target address may change due to different route.
    	java.security.Security.setProperty("networkaddress.cache.ttl" , OfflineLC.dns_cache_ttl.value());

    	//Set a couple of HttpClient connection/socket parameters for offline specific tuning
    	DefaultHttpParams.getDefaultParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, OfflineLC.http_so_timeout.intValue());
    	DefaultHttpParams.getDefaultParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, OfflineLC.http_connection_timeout.intValue());
    	
    	System.setProperty("http.agent", OfflineLC.zdesktop_name.value() + " " + OfflineLC.getFullVersion()); //for httpclient
    }

    public static class OfflineDebugListener implements SoapTransport.DebugListener {
    	OfflineAccount account;
    	
    	public OfflineDebugListener() {}
    	public OfflineDebugListener(OfflineAccount account) { this.account = account; }
    	
        public void sendSoapMessage(Element envelope) {
        	if (account == null || account.isDebugTraceEnabled())
        		OfflineLog.request.debug(getPayload(envelope));
        }
        public void receiveSoapMessage(Element envelope) {
        	if (account == null || account.isDebugTraceEnabled())
        		OfflineLog.response.debug(getPayload(envelope));
        }

        private Element getPayload(Element soap) {
            Element body = soap.getOptionalElement("Body");
            if (body != null && !body.listElements().isEmpty()) {
                Element elt = body.listElements().get(0);
                if (elt.getName().equals(AccountConstants.AUTH_REQUEST.getName())) {
                    elt = elt.clone();
                    Element eltPswd = elt.getOptionalElement(AccountConstants.E_PASSWORD);
                    if (eltPswd != null)
                        eltPswd.setText("*");
                }
                return elt;
            }
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
