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
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.*;

import java.util.ArrayList;
import java.util.List;

public class ZSearchResultBean {
    
    private ZSearchResult mResult;
    private ArrayList<ZSearchHitBean> mHits;
    private ArrayList<ZConversationHitBean> mConvHits;
    private ArrayList<ZContactHitBean> mContactHits;
    private ArrayList<ZMessageHitBean> mMessageHits;
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

    public int getConvSize() { return getConvHits().size(); }

    public int getContactSize() { return getContactHits().size(); }    


    public synchronized ZMessageBean getFetchedMessage() {
        for (ZMessageHitBean hit : getMessageHits()) {
            ZMessageBean msg = hit.getMessage();
            if (msg != null) return msg;
        }
        return null;
    }

    /**
     *
     * @return index of first message in result that matched, or -1.
     * 
     */
    public synchronized int getFetchedMessageIndex() {
        List<ZMessageHitBean> hits = getMessageHits();
        int size = hits.size();
        for (int i=0; i < size; i++) {
            if (hits.get(i).getMessage() != null)
                return i;
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
                }
            }
        }
        return mHits;
    }

    public synchronized List<ZConversationHitBean> getConvHits() {
        if (mConvHits == null) {
            mConvHits = new ArrayList<ZConversationHitBean>();
            for (ZSearchHit hit : mResult.getHits()) {
                if (hit instanceof ZConversationHit) {
                    mConvHits.add(new ZConversationHitBean((ZConversationHit)hit));
                }
            }
        }
        return mConvHits;
    }

    public synchronized List<ZContactHitBean> getContactHits() {
        if (mContactHits == null) {
            mContactHits = new ArrayList<ZContactHitBean>();
            for (ZSearchHit hit : mResult.getHits()) {
                if (hit instanceof ZContactHit) {
                    mContactHits.add(new ZContactHitBean((ZContactHit)hit));
                }
            }
        }
        return mContactHits;
    }

    public synchronized List<ZMessageHitBean> getMessageHits() {
        if (mMessageHits == null) {
            mMessageHits = new ArrayList<ZMessageHitBean>();
            for (ZSearchHit hit : mResult.getHits()) {
                if (hit instanceof ZMessageHit) {
                    mMessageHits.add(new ZMessageHitBean((ZMessageHit)hit));
                }
            }
        }
        return mMessageHits;
    }

    /**
     * @return true if there are more search results on the server
     */
    public boolean getHasMore() { return mResult.hasMore(); }  

    public boolean getHasNextPage() { return mResult.hasMore(); }
    public boolean getHasPrevPage() { return mResult.getOffset() > 0; }
    
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
