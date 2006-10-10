/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
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
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.jsp.tag;

import com.zimbra.cs.jsp.bean.ZSearchResultBean;
import com.zimbra.cs.jsp.bean.ZConversationHitBean;
import com.zimbra.cs.zclient.*;
import com.zimbra.cs.service.ServiceException;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class SearchConvTag extends ZimbraSimpleTag {

    private static final int DEFAULT_CONV_SEARCH_LIMIT = 10;

    private String mVar;
    private ZConversationHitBean mHit;
    private SearchContext mContext;
    private int mLimit = DEFAULT_CONV_SEARCH_LIMIT;
    private boolean mWanthtml;
    private boolean mMarkread;
    private boolean mFetchfirst;

    public void setVar(String var) { this.mVar = var; }

    public void setContext(SearchContext context) {this.mContext = context; }

    public void setConv(ZConversationHitBean hit) {this.mHit = hit; }

    public void setFetchfirst(boolean fetchfirst) { this.mFetchfirst = fetchfirst; }

    public void setMarkread(boolean markread) { this.mMarkread = markread; }

    public void setLimit(int limit) { this.mLimit = limit; }
    public void setWanthtml(boolean wanthtml) { this.mWanthtml = wanthtml; }

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

        if (mHit == null) return;

        String convId = mHit.getId();
        try {
            ZSearchParams params =  new ZSearchParams(mContext.getParams());
            params.setOffset(getInt(req, QP_CONV_SEARCH_OFFSET, 0));
            params.setLimit(mLimit); // TODO: prefs
            params.setFetchFirstMessage(mFetchfirst);
            params.setPeferHtml(mWanthtml);
            params.setMarkAsRead(mMarkread);
            
            ZSearchResult searchResults = mailbox.searchConversation(convId, params);

            ZSearchResultBean result = new ZSearchResultBean(searchResults, params);

            pageContext.setAttribute(mVar, result, PageContext.REQUEST_SCOPE);

        } catch (ServiceException e) {
            throw new JspTagException("search failed", e);
        }
    }
}
