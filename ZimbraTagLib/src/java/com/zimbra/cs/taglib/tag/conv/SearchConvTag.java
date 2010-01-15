/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
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
package com.zimbra.cs.taglib.tag.conv;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZSearchResultBean;
import com.zimbra.cs.taglib.tag.SearchContext;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZMailbox.Fetch;
import com.zimbra.cs.zclient.ZSearchPagerResult;
import com.zimbra.cs.zclient.ZSearchParams;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class SearchConvTag extends ZimbraSimpleTag {

    private static final int DEFAULT_CONV_SEARCH_LIMIT = 10;

    private String mVar;
    private String mId;
    private SearchContext mContext;
    private int mLimit = DEFAULT_CONV_SEARCH_LIMIT;
    private boolean mWanthtml;
    private boolean mWantHtmlSet;
    private boolean mMarkread;
    private Fetch mFetch;
    private ZMailbox.SearchSortBy mSortBy = ZMailbox.SearchSortBy.dateDesc;

    public void setVar(String var) { this.mVar = var; }

    public void setContext(SearchContext context) {this.mContext = context; }

    public void setId(String id) {this.mId = id; }

    public void setFetch(String fetch) throws ServiceException { this.mFetch = Fetch.fromString(fetch); }

    public void setMarkread(boolean markread) { this.mMarkread = markread; }

    public void setSort(String sortBy) throws ServiceException {
        if (sortBy != null && sortBy.length() > 0)
            this.mSortBy = ZMailbox.SearchSortBy.fromString(sortBy);
    }
    
    public void setLimit(int limit) { this.mLimit = limit; }

    public void setWanthtml(boolean wanthtml) {
        this.mWanthtml = wanthtml;
        this.mWantHtmlSet = true;
    }

    private static final String QP_CONV_SEARCH_OFFSET = "cso";

    private int getInt(ServletRequest req, String name, int def) {
        try {
            String value = req.getParameter(name);
            if (value != null) return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // ignore
        }
        return def;
    }

    public void doTag() throws JspException, IOException {
        ZMailbox mailbox = getMailbox();

        PageContext pageContext = (PageContext) getJspContext();
        ServletRequest req = pageContext.getRequest();

        if (mId == null) return;

        try {
            ZSearchParams params =  new ZSearchParams(mContext.getParams());
            params.setRecipientMode(false);
            params.setConvId(mId);
            params.setOffset(getInt(req, QP_CONV_SEARCH_OFFSET, 0));
            params.setLimit(mLimit); // TODO: prefs
            params.setFetch(mFetch);
            params.setPeferHtml(mWantHtmlSet ? mWanthtml : mailbox.getPrefs().getMessageViewHtmlPreferred());
            params.setMarkAsRead(mMarkread);
            params.setSortBy(mSortBy);

            int offset = params.getOffset();
            int requestedPage = offset/params.getLimit();
            params.setOffset(0);
            ZSearchPagerResult pager = mailbox.searchConversation(mId, params, requestedPage, true, false);
            if (pager.getActualPage() != pager.getRequestedPage())
                offset = pager.getActualPage()*params.getLimit();
            params.setOffset(offset);
            ZSearchResultBean result = new ZSearchResultBean(pager.getResult(), params);

            pageContext.setAttribute(mVar, result, PageContext.REQUEST_SCOPE);

        } catch (ServiceException e) {
            throw new JspTagException("search failed", e);
        }
    }
}
