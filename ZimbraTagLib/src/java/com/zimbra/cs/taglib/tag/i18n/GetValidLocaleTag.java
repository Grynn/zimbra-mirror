/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.tag.i18n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import com.zimbra.common.auth.ZAuthToken;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.Element.XMLElement;
import com.zimbra.common.soap.SoapHttpTransport;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;


public class GetValidLocaleTag extends ZimbraSimpleTag {


	private String mVar;
    private String mLocale;
	
    public void setVar(String var) { this.mVar = var; }
	public void setLocale(String locale) { this.mLocale = locale; }


    // simple tag methods

    public void doTag() throws JspException, IOException {
        JspContext ctxt = getJspContext();
        if (this.mLocale == null) {
            ctxt.setAttribute(mVar, false,  PageContext.REQUEST_SCOPE);
            return;
        }
        ZAuthToken authToken = ZJspSession.getAuthToken((PageContext)ctxt);
        SoapHttpTransport transport = null;
        try {
            String soapUri = ZJspSession.getSoapURL((PageContext)ctxt);
        	transport = new SoapHttpTransport(soapUri);
     		transport.setAuthToken(authToken);
        	XMLElement req = new XMLElement(AccountConstants.GET_AVAILABLE_LOCALES_REQUEST);
            Element resp = transport.invokeWithoutSession(req);
            List<String> locales = new ArrayList<String>();
            for (Element locale : resp.listElements(AccountConstants.E_LOCALE)) {
                String id = locale.getAttribute(AccountConstants.A_ID, null);
                if (id != null)
                	locales.add(id);
            }
            Collections.sort(locales);
            boolean isValid = false;
            for(String s : locales) {
                if (this.mLocale.toLowerCase().startsWith(s.toLowerCase())) {
                    isValid = true;
                    break;
                }
            }
            ctxt.setAttribute(mVar, isValid,  PageContext.REQUEST_SCOPE);
        }
        catch(ServiceException e) {
            throw new JspTagException(e.getMessage(), e);   
        } finally {
            if (transport != null)
                transport.shutdown();
        }
    }

}

