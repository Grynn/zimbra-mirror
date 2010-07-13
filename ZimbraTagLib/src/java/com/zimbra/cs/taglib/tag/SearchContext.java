/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.*;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZSearchParams;
import com.zimbra.cs.zclient.ZSearchPagerResult;
import com.zimbra.cs.zclient.ZPhoneAccount;
import com.zimbra.common.util.MapUtil;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

public class SearchContext {

    private static long sNextSearchContext = 1;

    private static final int MAX_QUERY_CACHE = 10;

    private String mTitle; // title to put in html page
    private String mBackTo; // text to use for "back to..."
    private String mShortBackTo; // text to use for "back to..."
    private String mSelectedId; // id of item in overview tree that is selected
    private String mQuery; // computed search query that we will run
    private String mSq; // from sq= attr
    private String mSfi; // from sfi = attr
    private String mSti; // from sti = attr
    private String mSt; // from st = attr
    private String mSs; // from ss = attr
    private String mTypes; // search types
    
    private ZFolderBean mFolderBean;
    private ZTagBean mTagBean;
	private ZPhoneAccountBean mPhoneAccount;

	public String getSq() { return mSq; }
    public void setSq(String sq) { mSq = sq; }

    public String getSfi() { return mSfi; }
    public void setSfi(String sfi) { mSfi = sfi; }

    public String getSti() { return mSti;}
    public void setSti(String sti) { mSti = sti; }

    public String getSt() { return mSt;}
    public void setSt(String st) { mSt = st; }

    public String getSs() { return mSs;}
    public void setSs(String ss) { mSs = ss; }

    public ZFolderBean getFolder() { return mFolderBean; }
    public void setFolder(ZFolderBean folder) { mFolderBean = folder; }

    public String getTypes() { return mTypes; }
    public void setTypes(String types) { mTypes = types; }

    public boolean getIsConversationSearch() { return ZSearchParams.TYPE_CONVERSATION.equals(mTypes); }
    public boolean getIsMessageSearch() { return ZSearchParams.TYPE_MESSAGE.equals(mTypes); }
    public boolean getIsContactSearch() { return ZSearchParams.TYPE_CONTACT.equals(mTypes); }
    public boolean getIsGALSearch() { return ZSearchParams.TYPE_GAL.equals(mTypes); }
    public boolean getIsTaskSearch() { return ZSearchParams.TYPE_TASK.equals(mTypes); }    
    public boolean getIsBriefcaseSearch() { return ZSearchParams.TYPE_BRIEFCASE.equals(mTypes); }
    public boolean getIsWikiSearch() { return ZSearchParams.TYPE_WIKI.equals(mTypes); }

    public boolean getIsVoiceMailSearch() { return ZSearchParams.TYPE_VOICE_MAIL.equals(mTypes); }    
    public boolean getIsCallSearch() { return ZSearchParams.TYPE_CALL.equals(mTypes); }

	public ZPhoneAccountBean getPhoneAccount() { return mPhoneAccount; }
	public void setPhoneAccount(ZPhoneAccount account) {
		mPhoneAccount = account == null ? null : new ZPhoneAccountBean(account);
	}

    public ZTagBean getTag() { return mTagBean; }
    public void setTag(ZTagBean tag) { mTagBean = tag; }

    public boolean getIsFolderSearch() { return mFolderBean != null && !mFolderBean.getIsSearchFolder(); }
    public boolean getIsSearchFolderSearch() { return mFolderBean != null && mFolderBean.getIsSearchFolder(); }    
    public boolean getIsTagSearch() { return mTagBean != null; }

    private ZSearchParams mParams;
    private ZSearchResultBean mResult;
    private boolean mShowMatches;

    private int mItemIndex; // index into search results
    private String mId; // my search context id

    private static synchronized String nextSearchContext() {
        return Long.toString(sNextSearchContext++);
    }

    public static SearchContext getSearchContext(PageContext ctxt, String searchContext) {
        if (searchContext == null || searchContext.length() == 0)
            return null;
        Map cache = getSearchContextCache(ctxt);
        return (SearchContext) cache.get(searchContext);
    }

    public static SearchContext newSearchContext(PageContext ctxt) {
        Map cache = getSearchContextCache(ctxt);
        SearchContext sc = new SearchContext(nextSearchContext());
        cache.put(sc.getId(), sc);
        return sc;
    }

    private static Map getSearchContextCache(PageContext ctxt) {
        Map cache = (Map) ctxt.getAttribute("SearchTag.queryCache", PageContext.SESSION_SCOPE);
        if (cache == null) {
            cache = MapUtil.newLruMap(MAX_QUERY_CACHE);
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
    
    public ZSearchResultBean getSearchResult() {
        
        return mResult;
    }
    //private void setSearchResult(ZSearchResultBean result) { mResult = result; }

    public String getTitle() { return mTitle; }
    public void setTitle(String title) { mTitle = title; }

    public String getBackTo() { return mBackTo; }
    public void setBackTo(String backto) { mBackTo = backto; }

    public String getShortBackTo() { return mShortBackTo; }
    public void setShortBackTo(String backto) { mShortBackTo = backto; }

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
        if (mResult == null || mResult.getHits() == null)
            return null;
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

    public synchronized void doSearch(ZMailbox mailbox, boolean useCache, boolean useCursor) throws JspTagException {
        try {
            int offset = mParams.getOffset();
            int requestedPage = offset/mParams.getLimit();
            mParams.setOffset(0);
            ZSearchPagerResult pager = mailbox.search(mParams, requestedPage, useCache, useCursor);
            if (pager.getActualPage() != pager.getRequestedPage())
                offset = pager.getActualPage()*mParams.getLimit();
            mParams.setOffset(offset);
            mResult = new ZSearchResultBean(pager.getResult(), mParams);
            mItemIndex = 0;
        } catch (ServiceException e) {
            throw new JspTagException("search failed", e);
        }
    }
}

