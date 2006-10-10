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

import com.zimbra.cs.zclient.ZFolder;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZSearchFolder;
import com.zimbra.cs.zclient.ZSearchParams;
import com.zimbra.cs.zclient.ZTag;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocaleSupport;
import java.io.IOException;

public class ComputeSearchContextTag extends ZimbraSimpleTag {

    private static final int DEFAULT_SEARCH_LIMIT = 25;

    public static final String TYPE_MAIL = "mail";
    public static final String TYPE_CONTACTS = "contacts";

    private static final String QP_SEARCH_CONTEXT = "sc";
    private static final String QP_SEARCH_QUERY = "sq";
    private static final String QP_SEARCH_FOLDER_ID = "sfi";
    private static final String QP_SEARCH_TAG_ID = "sti";
    private static final String QP_SEARCH_OFFSET = "so";
    private static final String QP_SEARCH_LIMIT = "sl";
    private static final String QP_SEARCH_SORT = "ss";
    private static final String QP_SEARCH_USE_CACHE = "su";    
    private static final String QP_SEARCH_TYPES = "st";
    private static final String QP_SEARCH_INDEX = "si";

    private String mVar;
    private String mDefault = TYPE_MAIL;
    private String mTypes;
    private ZMailbox.SearchSortBy mSortBy;
    private boolean mUseCache;
    private int mLimit = DEFAULT_SEARCH_LIMIT;

    public void setVar(String var) { this.mVar = var; }

    public void setUsecache(boolean usecache) {this.mUseCache = usecache; }

    public void setDefault(String def) { this.mDefault = def; }

    public void setTypes(String types) { this.mTypes = types; }

    public void setLimit(int limit) { this.mLimit = limit; } 

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

        int si = getInt(req, QP_SEARCH_INDEX, 0);
        int so = getInt(req, QP_SEARCH_OFFSET, 0);

        int usecache = getInt(req, QP_SEARCH_USE_CACHE, -1);
        if (usecache == 1) mUseCache = true; // otherwise, leave it set to what was passed in...

        String sc = req.getParameter(QP_SEARCH_CONTEXT);

        SearchContext sContext = SearchContext.getSearchContext(pageContext, sc);
        if (sContext != null) {
            pageContext.setAttribute(mVar, sContext, PageContext.REQUEST_SCOPE);

            if (si != -1) sContext.setCurrentItemIndex(si);

            if ((sContext.getSearchResult().getOffset() != so) || !mUseCache) {
                sContext.getParams().setOffset(so);
                sContext.doSearch(mailbox);
            }
            
        } else {
            // if we get here, we don't have a session, or the one we had timed out
            sContext = SearchContext.newSearchContext(pageContext);
            pageContext.setAttribute(mVar, sContext, PageContext.REQUEST_SCOPE);

            determineQuery(pageContext, sContext, req); // TODO: throw exception?
            sContext.setParams(determineParams(sContext, req, so));
            sContext.doSearch(mailbox);
        }

        if (sContext.getCurrentItemIndex() != si) sContext.setCurrentItemIndex(si);
    }

    private ZSearchParams determineParams(SearchContext result, ServletRequest req, int so) throws JspException {
        //String so = req.getParameter(QP_SEARCH_OFFSET);
        ZSearchParams params = new ZSearchParams(result.getQuery());

        params.setOffset(so);

        params.setLimit(mLimit);

        params.setSortBy(mSortBy);

        String st = req.getParameter(QP_SEARCH_TYPES);
        params.setTypes(st != null ? st : mTypes);

        //params.setFetchFirstMessage(mFetch);
        //params.setPeferHtml(mWanthtml);
        //params.setMarkAsRead(mMarkread);
        return params;
    }

    private void determineQuery(PageContext pageContext, SearchContext result, ServletRequest req) throws JspException {
        String sq = req.getParameter(QP_SEARCH_QUERY);
        String sf = req.getParameter(QP_SEARCH_FOLDER_ID);
        String st = req.getParameter(QP_SEARCH_TAG_ID);

        if (mTypes == null) {
            if (mDefault.equals(TYPE_CONTACTS)) {
                mTypes = ZSearchParams.TYPE_CONTACT;
            } else {
                mTypes = ZSearchParams.TYPE_CONVERSATION; // TODO: from pref, or based on view?
            }
        }

        if (mSortBy == null) {
            if (mDefault.equals(TYPE_CONTACTS)) {
                mSortBy = ZMailbox.SearchSortBy.nameAsc;
            } else {
                mSortBy = ZMailbox.SearchSortBy.dateDesc;
            }
        }

        // default to inbox/contacts
        if (sq == null && st == null && sf == null) {
            if (mDefault.equals(TYPE_CONTACTS)) {
                sf = ZFolder.ID_CONTACTS;
            } else {
                sf = ZFolder.ID_INBOX;
            }
        }

        if (sq != null) {
            result.setTitle(sq);
            result.setBackTo(LocaleSupport.getLocalizedMessage(pageContext, "backToSearch"));
            result.setQuery(sq);
            result.setShowMatches(true);
            return;
        } else if (sf != null) {
            ZMailbox mailbox = getMailbox();
            ZFolder folder = mailbox.getFolderById(sf);
            if (folder != null) {
                if (folder instanceof ZSearchFolder) {
                    result.setQuery(((ZSearchFolder)folder).getQuery());
                    result.setShowMatches(true);
                    result.setBackTo(LocaleSupport.getLocalizedMessage(pageContext, "backToSearchFolder", new Object[] {folder.getName()}));
                } else {
                    result.setQuery("in:\"" + folder.getPath() + "\"");
                    result.setBackTo(LocaleSupport.getLocalizedMessage(pageContext, "backToFolder", new Object[] {folder.getName()}));
                }
                result.setTitle(folder.getName());
                result.setSelectedId(folder.getId());
                return;
            }
        } else if (st != null) {
            ZMailbox mailbox = getMailbox();
            ZTag tag = mailbox.getTagById(st);
            if (tag != null) {
                result.setQuery("tag:\"" + tag.getName() + "\"");
                result.setTitle(tag.getName());
                result.setBackTo(LocaleSupport.getLocalizedMessage(pageContext, "backToTag", new Object[] {tag.getName()}));                                    
                result.setSelectedId(tag.getId());
                result.setShowMatches(true);
                return;
            }
        }
        throw new JspTagException("unable to determine query");
    }

 
}
