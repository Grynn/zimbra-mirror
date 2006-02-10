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
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;
import com.zimbra.cs.service.ServiceException;
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

        if (config != null) {
        	m.put("global", config.getGlobalConfig());
        	m.put("site", config.getSiteConfig());
        	m.put("local", config.getSiteConfig());
        }
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
