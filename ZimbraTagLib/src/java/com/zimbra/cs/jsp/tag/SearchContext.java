/*
 * Created by IntelliJ IDEA.
 * User: schemers
 * Date: Oct 3, 2006
 * Time: 10:17:38 AM
 */
package com.zimbra.cs.jsp.tag;

import com.zimbra.cs.jsp.bean.ZSearchHitBean;
import com.zimbra.cs.jsp.bean.ZSearchResultBean;
import com.zimbra.cs.service.ServiceException;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZSearchParams;
import com.zimbra.cs.zclient.ZSearchResult;
import org.apache.commons.collections.map.LRUMap;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

public class SearchContext {

    private static long sNextSearchContext = 1;

    private static final int MAX_QUERY_CACHE = 20;

    private String mTitle; // title to put in html page
    private String mBackTo; // text to use for "back to..."
    private String mSelectedId; // id of item in overview tree that is selected
    private String mQuery; // computed search query that we will run
    private ZSearchParams mParams;
    private ZSearchResultBean mResult;
    private boolean mShowMatches;

    private int mItemIndex; // index into search results
    private String mId; // my search context id

    private static synchronized String nextSearchContext() {
        return Long.toString(sNextSearchContext++);
    }

    public static SearchContext getSearchContext(PageContext ctxt, String searchContext) {
        LRUMap cache = getSearchContextCache(ctxt);
        return (SearchContext) cache.get(searchContext);
    }

    public static SearchContext newSearchContext(PageContext ctxt) {
        LRUMap cache = getSearchContextCache(ctxt);
        SearchContext sc = new SearchContext(nextSearchContext());
        cache.put(sc.getId(), sc);
        return sc;
    }

    private static LRUMap getSearchContextCache(PageContext ctxt) {
        LRUMap cache = (LRUMap) ctxt.getAttribute("SearchTag.queryCache", PageContext.SESSION_SCOPE);
        if (cache == null) {
            cache = new LRUMap(MAX_QUERY_CACHE);
            ctxt.setAttribute("SearchTag.queryCache", cache, PageContext.SESSION_SCOPE);
        }

        return cache;
    }

    private SearchContext(String id) {
        mId = id;
    }

    public String getId() { return mId; }

    public boolean getShowMatches() { return mShowMatches; }
    public void setShowMatches(boolean matches)  { mShowMatches = matches; }
    
    public ZSearchResultBean getSearchResult() { return mResult;}
    //private void setSearchResult(ZSearchResultBean result) { mResult = result; }

    public String getTitle() { return mTitle; }
    public void setTitle(String title) { mTitle = title; }

    public String getBackTo() { return mBackTo; }
    public void setBackTo(String backto) { mBackTo = backto; }

    public String getSelectedId() { return mSelectedId; }
    public void setSelectedId(String selectedId) { mSelectedId = selectedId; }

    public String getQuery() { return mQuery; }
    public void setQuery(String query) { mQuery = query; }

    public ZSearchParams getParams() { return mParams; }
    public void setParams(ZSearchParams params) { mParams = params; }

    public boolean getHasPrevItem() {
        return (mResult.getOffset() > 0) || (mItemIndex > 0);
    }

    public boolean getHasNextItem() {
        return (mItemIndex < mResult.getHits().size()-1) || mResult.getHasMore();    
    }
    
    public ZSearchHitBean getCurrentItem() {
        int size = mResult.getHits().size();
        if (mItemIndex >= 0 && mItemIndex < size)
            return mResult.getHits().get(mItemIndex);
        else
            return null;    
    }

    public int getCurrentItemIndex() {
        return mItemIndex;
    }

    public void setCurrentItemIndex(int index) {
        if (index >= 0 && mResult != null && index < mResult.getHits().size())
            mItemIndex = index;
    }

    public synchronized void doSearch(ZMailbox mailbox) throws JspTagException {
        try {
            mResult = new ZSearchResultBean(mailbox.search(mParams), mParams);
            mItemIndex = 0;
        } catch (ServiceException e) {
            throw new JspTagException("search failed", e);
        }
    }
}

