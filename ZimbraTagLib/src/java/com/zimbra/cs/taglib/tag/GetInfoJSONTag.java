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
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.zclient.ZClientException;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.taglib.tag.TagUtil.JsonDebugListener;
import com.zimbra.client.ZFolder;
import com.zimbra.client.ZMailbox;

public class GetInfoJSONTag extends ZimbraSimpleTag {

    private String mVar;
    private ZAuthToken mAuthToken;
    private String mItemsPerPage;
    private String mTypes;
    private boolean mDoSearch;
    private String mFolderPath;
    private String mSortBy;

    public void setVar(String var) { this.mVar = var; }
    public void setAuthtoken(ZAuthToken authToken) { this.mAuthToken = authToken; }
    public void setDosearch(boolean doSearch) { mDoSearch = doSearch; }
    public void setItemsperpage(String itemsPerPage) { mItemsPerPage = itemsPerPage; }
    public void setTypes(String types) { mTypes = types; }
    public void setFolderpath(String folderPath) {mFolderPath = folderPath; }
    public void setSortby(String sortBy) {mSortBy = sortBy; }
    public void doTag() throws JspException {
        try {
            JspContext ctxt = getJspContext();
            PageContext pageContext = (PageContext) ctxt;
            String url = ZJspSession.getSoapURL(pageContext);
            String remoteAddr = ZJspSession.getRemoteAddr(pageContext);
            if (mFolderPath!=null && !mFolderPath.isEmpty()) {
                ZMailbox mbox = this.getMailbox();
                String folderId = this.getFolderFromPath(mbox, mFolderPath);
                if (folderId !=null && !folderId.isEmpty()) {
                    mSortBy = this.getSortByAttr(folderId, mSortBy);
                }
            }
            Element e = getBootstrapJSON(url, remoteAddr, mAuthToken, mDoSearch, mItemsPerPage, mTypes, mSortBy);
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
     * @param sortBy how to sort search
     * @return top-level JSON respsonse
     * @throws ServiceException on error
     */
    public static Element getBootstrapJSON(String url, String remoteAddr, ZAuthToken authToken, boolean doSearch, String itemsPerPage, String searchTypes, String sortBy) throws ServiceException {
        JsonDebugListener debug = new JsonDebugListener();
        SoapTransport transport = TagUtil.newJsonTransport(url, remoteAddr, authToken, debug);

        try {
            Element batch = Element.create(SoapProtocol.SoapJS, ZimbraNamespace.E_BATCH_REQUEST);
            Element getInfoRequest = batch.addElement(AccountConstants.GET_INFO_REQUEST);
			getInfoRequest.addAttribute("rights", "createDistList");
            if (doSearch) {
                Element search = batch.addElement(MailConstants.SEARCH_REQUEST);
                if (itemsPerPage != null && itemsPerPage.length() > 0)
                    search.addAttribute(MailConstants.A_QUERY_LIMIT, itemsPerPage);
                if (searchTypes != null && searchTypes.length() > 0)
                    search.addAttribute(MailConstants.A_SEARCH_TYPES, searchTypes);
                if (sortBy != null && !sortBy.isEmpty()) {
                    search.addAttribute(MailConstants.A_SORTBY, sortBy);
                }
            }
            transport.invoke(batch);
            return debug.getEnvelope();
        } catch (IOException e) {
            throw ZClientException.IO_ERROR("invoke "+e.getMessage(), e);
        }
    }

    /**
     * Get folderId based on path
     * @param folderPath for example inbox/Archived
     * @return folderId the folder id
     * @throws ServiceExcpetion on error
     */
    private String getFolderFromPath(ZMailbox mbox, String folderPath) throws ServiceException {
        String folderId = "";
        if (folderPath != null) {
            ZFolder rootFolder = mbox.getUserRoot();
            ZFolder folder = rootFolder.getSubFolderByPath(mFolderPath);
            if (folder != null) {
                folderId = folder.getId();
            }
        }
        if (ZimbraLog.webclient.isDebugEnabled()) {
            ZimbraLog.webclient.debug("folderPath = " + folderPath);
            ZimbraLog.webclient.debug("folderId = " + folderId);
		}
        return folderId;
    }

    /**
     * Extacts the sortBy attribute for the specified folder
     * @param folderId  folder Id
     * @param sortOrderPref a String with the pattern folderId:sortBy,folderId:sortBy
     * @return sortBy sort by string
     */
    private String getSortByAttr(String folderId, String sortOrderPref) {
        String sortBy = "";
        if (folderId !=null & !folderId.isEmpty() && sortOrderPref != null && !sortOrderPref.isEmpty()) {
            String[] folders = sortOrderPref.split(",");
            for (int i=0; i<folders.length; i++) {
                if (folders[i].startsWith(folderId + ":")) {
                    int index = folders[i].indexOf(":");
                    sortBy = folders[i].substring(index+1);
                    break;
                }
            }
        }
        if (ZimbraLog.webclient.isDebugEnabled()) {
            ZimbraLog.webclient.debug("sortOrderPref = " + sortOrderPref);
            ZimbraLog.webclient.debug("sortBy = " + sortBy);
		}
        return sortBy;
    }
}
