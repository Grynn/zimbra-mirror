/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.*;
import com.zimbra.cs.zclient.ZSearchResult.ZConversationSummary;

import java.util.ArrayList;
import java.util.List;

public class ZSearchResultBean {
    
    private ZSearchResult mResult;
    private ArrayList<ZSearchHitBean> mHits;
    private int mLimit;
    private int mOffset;
    private int mPrevOffset;
    private int mNextOffset;
    
    public ZSearchResultBean(ZSearchResult result, ZSearchParams params) {
        mResult = result;
        mLimit = params.getLimit();
        mOffset = params.getOffset();
        mPrevOffset = mLimit > mOffset ? 0 : mOffset - mLimit;
        mNextOffset = mOffset + mLimit;
    }

    public int getSize() { return mResult.getHits().size(); }

    public ZConversationSummary getConversationSummary() { return mResult.getConversationSummary(); }
    
    public synchronized ZMessageBean getFetchedMessage() {
        for (ZSearchHitBean hit : getHits()) {
            if (hit.getIsMessage()) {
                ZMessageBean msg = hit.getMessageHit().getMessage();
                if (msg != null) return msg;
            }
        }
        return null;
    }

    /**
     *
     * @return index of first message in result that matched, or -1.
     * 
     */
    public synchronized int getFetchedMessageIndex() {
        int i = 0;
        for (ZSearchHitBean hit : getHits()) {
            if (hit.getIsMessage()) {
                ZMessageBean msg = hit.getMessageHit().getMessage();
                if (msg != null) return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * @return ZSearchHit objects from search
     */
    public synchronized List<ZSearchHitBean> getHits() {
        if (mHits == null) {
            mHits = new ArrayList<ZSearchHitBean>();
            for (ZSearchHit hit : mResult.getHits()) {
                if (hit instanceof ZConversationHit) {
                    mHits.add(new ZConversationHitBean((ZConversationHit)hit));
                } else if (hit instanceof ZMessageHit) {
                    mHits.add(new ZMessageHitBean((ZMessageHit)hit));
                } else if (hit instanceof ZContactHit) {
                    mHits.add(new ZContactHitBean((ZContactHit)hit));
                } else if (hit instanceof ZVoiceMailItemHit) {
                    mHits.add(new ZVoiceMailItemHitBean((ZVoiceMailItemHit)hit));
                } else if (hit instanceof ZCallHit) {
                    mHits.add(new ZCallHitBean((ZCallHit)hit));
                } else if (hit instanceof ZTaskHit) {
                    mHits.add(new ZTaskHitBean((ZTaskHit)hit));
                } else if (hit instanceof ZDocumentHit) {
                    mHits.add(new ZDocumentHitBean((ZDocumentHit)hit));
                }

            }
        }
        return mHits;
    }

    /**
     * @return true if there are more search results on the server
     */
    public boolean getHasMore() { return mResult.hasMore(); }  

    public boolean getHasNextPage() { return mResult.hasMore(); }
    public boolean getHasPrevPage() { return mOffset > 0; }
    
    /**
     * @return the sort by value
     */
    public String getSortBy() { return mResult.getSortBy(); }

    /**
     * @return offset used for the search.
     */
    public int getOffset() { return mOffset; }

    public int getPrevOffset() { return mPrevOffset; }
    
    public int getNextOffset() { return mNextOffset; }

    /** @return limit used with search */
    public int getLimit() { return mLimit; }
}
