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
package com.zimbra.cs.taglib.tag;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import com.zimbra.common.auth.ZAuthToken;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.soap.SoapTransport;
import com.zimbra.common.soap.ZimbraNamespace;
import com.zimbra.common.zclient.ZClientException;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.taglib.tag.TagUtil.JsonDebugListener;

public class GetInfoJSONTag extends ZimbraSimpleTag {

    private String mVar;
    private ZAuthToken mAuthToken;
    private String mItemsPerPage;
    private String mTypes;
    private boolean mDoSearch;

    public void setVar(String var) { this.mVar = var; }
    public void setAuthtoken(ZAuthToken authToken) { this.mAuthToken = authToken; }
    public void setDosearch(boolean doSearch) { mDoSearch = doSearch; }
    public void setItemsperpage(String itemsPerPage) { mItemsPerPage = itemsPerPage; }
    public void setTypes(String types) { mTypes = types; }
    
    public void doTag() throws JspException {
        try {
            JspContext ctxt = getJspContext();
            PageContext pageContext = (PageContext) ctxt;
            String url = ZJspSession.getSoapURL(pageContext);
            String remoteAddr = ZJspSession.getRemoteAddr(pageContext);
            Element e = getBootstrapJSON(url, remoteAddr, mAuthToken, mDoSearch, mItemsPerPage, mTypes);
            ctxt.setAttribute(mVar, e.toString(),  PageContext.REQUEST_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }
    
    /**
     * used when bootstrapping AJAX client.
     *
     * @param url url to connect to
     * @param authToken auth token to use
     * @param itemsPerPage number of search items to return
     * @param doSearch whether or not to also do the intial search
     * @param searchTypes what to search for
     * @return top-level JSON respsonse
     * @throws ServiceException on error
     */
    public static Element getBootstrapJSON(String url, String remoteAddr, ZAuthToken authToken, boolean doSearch, String itemsPerPage, String searchTypes) throws ServiceException {
        JsonDebugListener debug = new JsonDebugListener();
        SoapTransport transport = TagUtil.newJsonTransport(url, remoteAddr, authToken, debug);
        
        try {
            Element batch = Element.create(SoapProtocol.SoapJS, ZimbraNamespace.E_BATCH_REQUEST);
            batch.addElement(AccountConstants.GET_INFO_REQUEST);
            if (doSearch) {
                Element search = batch.addElement(MailConstants.SEARCH_REQUEST);
                if (itemsPerPage != null && itemsPerPage.length() > 0)
                    search.addAttribute(MailConstants.A_QUERY_LIMIT, itemsPerPage);
                if (searchTypes != null && searchTypes.length() > 0)
                    search.addAttribute(MailConstants.A_SEARCH_TYPES, searchTypes);
            }
            transport.invoke(batch);
            return debug.getEnvelope();
        } catch (IOException e) {
            throw ZClientException.IO_ERROR("invoke "+e.getMessage(), e);
        }
    }

}
