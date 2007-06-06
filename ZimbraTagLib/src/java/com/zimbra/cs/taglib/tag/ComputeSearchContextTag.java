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
package com.zimbra.cs.taglib.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.taglib.bean.ZFolderBean;
import com.zimbra.cs.taglib.bean.ZTagBean;
import com.zimbra.cs.zclient.ZFolder;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZSearchFolder;
import com.zimbra.cs.zclient.ZSearchParams;
import com.zimbra.cs.zclient.ZTag;
import com.zimbra.cs.zclient.ZPhoneAccount;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocaleSupport;
import java.io.IOException;
import java.util.List;

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
    private String mTypes;
    private ZMailbox.SearchSortBy mSortBy;
    private boolean mUseCache;
    private int mLimit = -1;

    public void setVar(String var) { this.mVar = var; }

    public void setUsecache(boolean usecache) {this.mUseCache = usecache; }

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
        try {
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

                if ((sContext.getSearchResult() == null || sContext.getParams().getOffset() != so) || !mUseCache) {
                    sContext.getParams().setOffset(so);
                }
            } else {
                // if we get here, we don't have a session, or the one we had timed out
                sContext = SearchContext.newSearchContext(pageContext);
                pageContext.setAttribute(mVar, sContext, PageContext.REQUEST_SCOPE);

                determineQuery(pageContext, sContext, req, mailbox); // TODO: throw exception?
                sContext.setParams(determineParams(sContext, req, so, mailbox));
                mUseCache = false; // always ignore cache on new search context. TODO: optimize?
            }
            sContext.doSearch(mailbox, mUseCache, !ZJspSession.getSearchUseOffset(pageContext));
            if (sContext.getCurrentItemIndex() != si) sContext.setCurrentItemIndex(si);
        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }

    private ZSearchParams determineParams(SearchContext result, ServletRequest trareq, int so, ZMailbox mailbox) throws ServiceException {
        //String so = req.getParameter(QP_SEARCH_OFFSET);
        ZSearchParams params = new ZSearchParams(result.getQuery());

        params.setOffset(so);
        if (result.getFolder() != null && (result.getFolder().getIsDrafts() || result.getFolder().getIsSent()))
            params.setRecipientMode(true);

        params.setSortBy(mSortBy);
        params.setTypes(mTypes);

        if (mLimit == -1) {
            mLimit = (int) (ZSearchParams.TYPE_CONTACT.equals(mTypes) ?
                    mailbox.getPrefs().getContactsPerPage() :
                    mailbox.getPrefs().getMailItemsPerPage());
            if (mLimit == -1)
                mLimit = DEFAULT_SEARCH_LIMIT;
        }
        params.setLimit(mLimit);
        //params.setFetchFirstMessage(mFetch);
        //params.setPeferHtml(mWanthtml);
        //params.setMarkAsRead(mMarkread);
        return params;
    }

    private void determineQuery(PageContext pageContext, SearchContext result, ServletRequest req, ZMailbox mailbox) throws JspException, ServiceException {
        String sq = req.getParameter(QP_SEARCH_QUERY);
        String sfi = req.getParameter(QP_SEARCH_FOLDER_ID);
        String sti = req.getParameter(QP_SEARCH_TAG_ID);
        String st = req.getParameter(QP_SEARCH_TYPES);
        String ss = req.getParameter(QP_SEARCH_SORT);

        result.setSq(sq);
        result.setSfi(sfi);
        result.setSti(sti);
        result.setSs(ss);

        if (mTypes == null)
            mTypes = (st != null) ?
                    st :
                    mailbox.getFeatures().getConversations() ?
                            (mailbox.getPrefs().getGroupByMessage() ? ZSearchParams.TYPE_MESSAGE : ZSearchParams.TYPE_CONVERSATION) :
                            ZSearchParams.TYPE_MESSAGE;

        result.setSt(mTypes);

        if (ss != null) {
            mSortBy = ZMailbox.SearchSortBy.fromString(ss);
        }

        if (mSortBy == null)
            mSortBy = ZSearchParams.TYPE_CONTACT.equals(mTypes) ?
                    ZMailbox.SearchSortBy.nameAsc :
                    ZMailbox.SearchSortBy.dateDesc;

        // default to inbox/contacts
        if (sq == null && sti == null && sfi == null) {
            if (ZSearchParams.TYPE_CONTACT.equals(mTypes))
                sfi = ZFolder.ID_CONTACTS;
            else if (!ZSearchParams.TYPE_VOICE_MAIL.equals(mTypes)) {
                if (mailbox.getFeatures().getInitialSearchPreference()) {
                    sq = mailbox.getPrefs().getMailInitialSearch();
                    if (sq != null && sq.equalsIgnoreCase("in:inbox")) {
                        sq = null; // make it act like a folder search
                    }
                }
                if (sq == null || sq.length() == 0)
                    sfi = ZFolder.ID_INBOX;
            }
        }

        result.setTypes(mTypes);
        
        if (sq != null) {
            result.setTitle(sq);
            result.setBackTo(LocaleSupport.getLocalizedMessage(pageContext, "backToSearch"));
            result.setQuery(sq);
            result.setShowMatches(true);
            return;
        } else if (sfi != null) {
            ZFolder folder = mailbox.getFolderById(sfi);
            if (folder != null) {
                if (folder instanceof ZSearchFolder) {
                    result.setQuery(((ZSearchFolder)folder).getQuery());
                    result.setShowMatches(true);
                    result.setBackTo(LocaleSupport.getLocalizedMessage(pageContext, "backToSearchFolder", new Object[] {folder.getName()}));
                } else {
                    result.setQuery("in:\"" + folder.getRootRelativePath() + "\"");
                    result.setBackTo(LocaleSupport.getLocalizedMessage(pageContext, "backToFolder", new Object[] {folder.getName()}));
                }
                result.setFolder(new ZFolderBean(folder));
                result.setTitle(folder.getName());
                result.setSelectedId(folder.getId());
                return;
            }
        } else if (sti != null) {
            ZTag tag = mailbox.getTagById(sti);
            if (tag != null) {
                result.setQuery("tag:\"" + tag.getName() + "\"");
                result.setTitle(tag.getName());
                result.setBackTo(LocaleSupport.getLocalizedMessage(pageContext, "backToTag", new Object[] {tag.getName()}));                                    
                result.setSelectedId(tag.getId());
                result.setTag(new ZTagBean(tag));                
                result.setShowMatches(true);
                return;
            }
        } else if (ZSearchParams.TYPE_VOICE_MAIL.equals(st)) {
            List<ZPhoneAccount> accounts = mailbox.getAllPhoneAccounts();
            if (accounts.size() > 0) {
                result.setQuery("phone:" + accounts.get(0).getPhone().getName());
                return;                
            }
        }
        throw new JspTagException("unable to determine query");
    }

 
}
