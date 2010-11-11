/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.offline.jsp;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.offline.common.OfflineConstants;

public class PageBean {
    private Locale clientLocale = new Locale("en");
    private static Locale serverLocale = null; 
    private static final String APP_VERSION = LC.get("zdesktop_relabel") + LC.get("zdesktop_version") + " (build " + LC.get("zdesktop_buildid") + ")";

    private String getMsgFromBundle(String key) {
        ResourceBundle bundle = ResourceBundle.getBundle("/messages/ZdMsg", clientLocale);
        String msg;
        
        try {
            msg = bundle.getString(key);
        } catch (MissingResourceException x) {
            msg = null;
        }
        return msg;
    }

    protected String getMessage(String key) {
        String msg = getMsgFromBundle(key);
        
        return msg == null ? "??" + key + "??" : msg; 
    }

    protected String getMessage(String key, boolean keyAsDefault) {
        return keyAsDefault ? getMessage(key) : getMsgFromBundle(key);
    }

    protected String getMessage(String key, Object[] params) {
        return MessageFormat.format(getMessage(key), params);
    }

    protected String getMessage(String key, Object[] params, boolean keyAsDefault) {
        if (keyAsDefault)
            return getMessage(key, params);
        String msg = getMsgFromBundle(key);
        return msg == null ? null : MessageFormat.format(msg, params);
    }

    public PageBean() {}

    public String getAppVersion() { return APP_VERSION; }

    public String getLoginUsername() {
        try {
            return JspProvStub.getInstance().getLoginAccountName();
        } catch (ServiceException x) {
            return JspConstants.LOCAL_ACCOUNT;
        }
    }

    public void setLocale(Locale locale) throws ServiceException {
        clientLocale = locale;
        updateServerLocale(locale);
    }

    public String getSkin() {
        try {
            return JspProvStub.getInstance().getOfflineAccount(
                OfflineConstants.LOCAL_ACCOUNT_ID).getAttr(Provisioning.A_zimbraPrefSkin);
        } catch (ServiceException x) {
            return "twilight";
        }
    }

    public static synchronized void updateServerLocale(Locale locale) throws ServiceException {
        if (serverLocale == null || !serverLocale.equals(locale)) { 
            Map<String, Object> attrs = new HashMap<String, Object>();
            attrs.put(Provisioning.A_zimbraPrefLocale, locale.toString());
            JspProvStub stub = JspProvStub.getInstance();
            stub.modifyOfflineAccount(OfflineConstants.LOCAL_ACCOUNT_ID, attrs);
            serverLocale = (Locale)locale.clone();						
        }
    }

    public static String getBaseUri() {
        return "http://127.0.0.1:" + LC.zimbra_admin_service_port.value();
    }

    public static String getLocalConfig(String key) {
        return LC.get(key);
    }

    private static final String[] knownPassThroughFlags = new String[] {"dev","notifydebug","scripterrors"};

    public static String addAuthToken(String url, HttpServletRequest request) {
        String at = LC.get("zdesktop_installation_key");
        
        if (at != null && !at.startsWith("@"))
            url += (url.indexOf('?') < 0 ? "?" : "&") + "at=" + at;
        for (String flagKey : knownPassThroughFlags) {
            String flagVal = request.getParameter(flagKey);
            if (flagVal != null && flagVal.length() > 0) {
                url += (url.indexOf('?') < 0 ? "?" : "&") + flagKey + "=" + flagVal;
            }
        }
        return url;
    }

    public static boolean checkAuthToken(HttpServletRequest request) {
        String key = LC.get("zdesktop_installation_key");
        if (key == null || key.startsWith("@"))
            return true;            
        String at = request.getParameter("at");
        return at != null && at.equals(key);
    }

    public static boolean isPrism(String userAgent) {
        return userAgent != null && userAgent.indexOf("Prism") >= 0;
    }
}
