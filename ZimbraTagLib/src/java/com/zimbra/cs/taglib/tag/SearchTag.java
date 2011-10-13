/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011 Zimbra, Inc.
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

import com.google.common.collect.MapMaker;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZSearchResultBean;
import com.zimbra.client.ZMailbox;
import com.zimbra.client.ZMailbox.Fetch;
import com.zimbra.client.ZSearchParams;
import com.zimbra.client.ZSearchResult;
import com.zimbra.soap.type.SearchSortBy;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;

public final class SearchTag extends ZimbraSimpleTag {

    private static final AtomicLong ID_GEN = new AtomicLong(1L);
    private static final int DEFAULT_SEARCH_LIMIT = 25;
    private static final int CACHE_SIZE = 20;
    private static final String CACHE_ATTR = "SearchTag.cache";

    private String mVar;
    private long mSearchContext;
    private int mLimit = DEFAULT_SEARCH_LIMIT;
    private int mOffset;
    private String mConvId = null;
    private String mTypes = ZSearchParams.TYPE_CONVERSATION;
    private String mQuery = "in:inbox";
    private SearchSortBy mSortBy = SearchSortBy.dateDesc;
    private boolean mWanthtml;
    private boolean mWantHtmlSet;
    private boolean mMarkread;
    private long mStart;
    private long mEnd;
    private String mFolderId;
    private TimeZone mTimeZone;
    private Fetch mFetch;
    private String mField = null;

    public void setVar(String var) { this.mVar = var; }

    public void setTypes(String types) { this.mTypes = types; }

    public void setQuery(String query) {
        if (query == null || query.equals("")) query = "in:inbox";
            this.mQuery = query;
    }

    public void setSort(String sortBy) throws ServiceException { this.mSortBy = SearchSortBy.fromString(sortBy); }

    public void setConv(String convId) { this.mConvId = convId; }

    public void setLimit(int limit) { this.mLimit = limit; }

    public void setOffset(int offset) { this.mOffset = offset; }

    public void setMarkread(boolean markread) { this.mMarkread = markread; }

    public void setField(String field) { this.mField = field; }

    public void setStart(long start) { this.mStart = start; }

    public void setEnd(long end) { this.mEnd = end; }

    public void setFolderid(String folderId) { this.mFolderId = folderId; }

    public void setTimezone(TimeZone timeZone) { this.mTimeZone = timeZone; }

    public void setWanthtml(boolean wanthtml) {
        this.mWanthtml = wanthtml;
        this.mWantHtmlSet = true;
    }

    public void setFetch(String fetch) throws ServiceException { this.mFetch = Fetch.fromString(fetch); }

    public void setSearchContext(long context) { this.mSearchContext = context; }

    @Override
    public void doTag() throws JspException, IOException {
        PageContext jctxt = (PageContext) getJspContext();
        try {
            ZMailbox mbox = getMailbox();

            if (mFolderId != null && mFolderId.length() > 0) {
                StringBuilder newQuery = new StringBuilder();
                newQuery.append("(");
                for (String fid : mFolderId.split(",")) {
                    if (newQuery.length() > 1) newQuery.append(" or ");
                    newQuery.append("inid:").append(fid);
                }
                newQuery.append(")");
                if (mQuery != null && mQuery.length() > 0) {
                    newQuery.append("AND (").append(mQuery).append(")");
                }
                mQuery = newQuery.toString();
            }
            ZSearchParams params = new ZSearchParams(mQuery);
            params.setOffset(mOffset);
            params.setLimit(mLimit);
            params.setSortBy(mSortBy);
            params.setTypes(mTypes);
            params.setFetch(mFetch);
            params.setPeferHtml(mWantHtmlSet ? mWanthtml : mbox.getPrefs().getMessageViewHtmlPreferred());
            params.setMarkAsRead(mMarkread);
            params.setField(mField);
            if (mStart != 0) params.setCalExpandInstStart(mStart);
            if (mEnd != 0) params.setCalExpandInstEnd(mEnd);
            if (mTimeZone != null) params.setTimeZone(mTimeZone);

            ZSearchResult searchResults = mConvId == null ? mbox.search(params) : mbox.searchConversation(mConvId, params);

            if (mSearchContext != 0) {
                SearchContext sc = getSearchContextCache(jctxt).get(mSearchContext);
                if (sc == null) mSearchContext = 0;
                else sc.setSearchResult(searchResults);
            }

            if (mOffset == 0 || mSearchContext == 0) {
                mSearchContext = putSearchContext(jctxt, searchResults);
            }

            jctxt.setAttribute(mVar, new ZSearchResultBean(searchResults, params),  PageContext.PAGE_SCOPE);

        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }

    private static Map<Long, SearchContext> getSearchContextCache(PageContext ctxt) {
        synchronized (ctxt.getSession()) {
            @SuppressWarnings("unchecked")
            Map<Long, SearchContext> cache = (Map<Long, SearchContext>) ctxt.getAttribute(CACHE_ATTR,
                    PageContext.SESSION_SCOPE);
            if (cache == null) {
                cache = new MapMaker().concurrencyLevel(1).maximumSize(CACHE_SIZE).makeMap();
                ctxt.setAttribute(CACHE_ATTR, cache, PageContext.SESSION_SCOPE);
            }
            return cache;
        }
    }

    private static long putSearchContext(PageContext ctxt, ZSearchResult result) {
        long id = ID_GEN.getAndIncrement();
        Map<Long, SearchContext> cache = getSearchContextCache(ctxt);
        SearchContext sc = new SearchContext();
        sc.setSearchResult(result);
        cache.put(id, sc);
        return id;
    }

    public static final class SearchContext {
        private ZSearchResult mResult;

        public ZSearchResult getSearchResult() {
            return mResult;
        }

        public void setSearchResult(ZSearchResult result) {
            mResult = result;
        }
    }

}
