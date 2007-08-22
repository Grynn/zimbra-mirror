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
 * Portions created by Zimbra are Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;
import com.zimbra.cs.zimlet.ZimletProperty;
import com.zimbra.cs.zimlet.ZimletUserProperties;

public class Property extends ZimbraTag {

    private String mZimlet;
    private String mVar;
    private String mName;
    private String mAction;
    private String mValue;

    public void setZimlet(String zimlet) {
        mZimlet = zimlet;
    }

    public String getZimlet() {
        return mZimlet;
    }

    public void setVar(String val) {
        mVar = val;
    }

    public String getVar() {
        return mVar;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setAction(String action) {
        mAction = action;
    }

    public String getAction() {
        return mAction;
    }

    public void setValue(String value) {
        mValue = value;
    }

    public String getValue() {
        return mValue;
    }

    private String doListProperty(Account acct, OperationContext octxt) throws ZimbraTagException, ServiceException {
        if (mVar == null) {
            throw ZimbraTagException.MISSING_ATTR("var");
        }
        ZimletUserProperties props = ZimletUserProperties.getProperties(acct);

    	Iterator iter = props.getAllProperties().iterator();
    	Map<String,String> m = new HashMap<String,String>();
    	while (iter.hasNext()) {
    		ZimletProperty zp = (ZimletProperty) iter.next();
    		if (zp.getZimletName().equals(mZimlet)) {
                m.put(zp.getKey(), zp.getValue());
            }
    	}
    	HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
    	req.setAttribute(mVar, m);
        return "";
    }

    private String doGetProperty(Account acct, OperationContext octxt) throws ZimbraTagException, ServiceException {
        if (mName == null) {
            throw ZimbraTagException.MISSING_ATTR("name");
        }
        ZimletUserProperties props = ZimletUserProperties.getProperties(acct);

        StringBuffer ret = new StringBuffer("undefined");
    	Iterator iter = props.getAllProperties().iterator();
    	while (iter.hasNext()) {
    		ZimletProperty zp = (ZimletProperty) iter.next();
    		if (zp.getZimletName().equals(mZimlet) &&
    			zp.getKey().equals(mName)) {
                return zp.getValue();
            }
    	}
        return ret.toString();
    }

    private String doSetProperty(Account acct, OperationContext octxt) throws ZimbraTagException, ServiceException {
        if (mName == null) {
            throw ZimbraTagException.MISSING_ATTR("name");
        }
        if (mValue == null) {
            throw ZimbraTagException.MISSING_ATTR("value");
        }
        ZimletUserProperties props = ZimletUserProperties.getProperties(acct);
        props.setProperty(mZimlet, mName, mValue);
        props.saveProperties(acct);
        return "";
    }

    public String getContentStart(Account acct, OperationContext octxt) throws ZimbraTagException, ServiceException {
        if (mZimlet == null) {
            throw ZimbraTagException.MISSING_ATTR("zimlet");
        }
        if (mAction != null && mAction.equals("set")) {
            return doSetProperty(acct, octxt);
        } else if (mAction != null && mAction.equals("list")) {
            return doListProperty(acct, octxt);
        }
        return doGetProperty(acct, octxt);
    }
}
