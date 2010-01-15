/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.zimlet.ZimletUtil;

public class ZimletConfig extends ZimbraTag {

    private String mZimlet;
    private String mAction;
    private String mVar;
    private String mName;
    private String mScope;

    public void setZimlet(String val) {
        mZimlet = val;
    }

    public String getZimlet() {
        return mZimlet;
    }

    public void setAction(String val) {
        mAction = val;
    }

    public String getAction() {
        return mAction;
    }

    public void setVar(String val) {
        mVar = val;
    }

    public String getVar() {
        return mVar;
    }

    public void setName(String val) {
        mName = val;
    }

    public String getName() {
        return mName;
    }

    public void setScope(String val) {
        mScope = val;
    }

    public String getScope() {
        return mScope;
    }

    public String doListConfig(Account acct, OperationContext octxt) throws ZimbraTagException, ServiceException {
        if (mVar == null) {
            throw ZimbraTagException.MISSING_ATTR("var");
        }
        Map<String,Map> m = new HashMap<String,Map>();
    	HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
    	req.setAttribute(mVar, m);

    	com.zimbra.cs.zimlet.ZimletConfig config = ZimletUtil.getZimletConfig(mZimlet);

    	Map gc, sc, lc;
    	
        if (config == null) {
        	gc = new HashMap();
        	sc = new HashMap();
        	lc = new HashMap();
        } else {
        	gc = config.getGlobalConfig();
        	sc = config.getSiteConfig();
        	lc = config.getSiteConfig();
        }
    	m.put("global", gc);
    	m.put("site", sc);
    	m.put("local", lc);
    	return "";
    }
    
    public String doGetConfig(Account acct, OperationContext octxt) throws ZimbraTagException, ServiceException {
        if (mName == null) {
            throw ZimbraTagException.MISSING_ATTR("name");
        }
        com.zimbra.cs.zimlet.ZimletConfig config = ZimletUtil.getZimletConfig(mZimlet);
        String val;

        if (config == null) {
        	return "zimlet " + mName + "not found";
        }
        // if scope is not defined, search both global and site config.
       	val = config.getSiteConf(mName);
        if (mScope == null && val == null ||
        	mScope != null && mScope.equals("global")) {
        	val = config.getGlobalConf(mName);
        }
        if (val == null) val = "";
        return val;
    }
    
    public String getContentStart(Account acct, OperationContext octxt) throws ZimbraTagException, ServiceException {
        if (mZimlet == null) {
            throw ZimbraTagException.MISSING_ATTR("zimlet");
        }
        if (mAction != null && mAction.equals("list")) {
        	return doListConfig(acct, octxt);
        }
        return doGetConfig(acct, octxt);
    }
}
