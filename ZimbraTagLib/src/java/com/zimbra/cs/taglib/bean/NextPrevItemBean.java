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
package com.zimbra.cs.taglib.bean;

public class NextPrevItemBean {
    private int mPrevIndex;
    private int mPrevOffset;
    private int mNextIndex;
    private int mNextOffset;
    private boolean mHasNext;
    private boolean mHasPrev;

    public NextPrevItemBean(boolean hasPrev, int prevIndex, int prevOffset, boolean hasNext, int nextIndex, int nextOffset) {
        mHasPrev = hasPrev;
        mPrevIndex = prevIndex;
        mPrevOffset = prevOffset;
        mHasNext = hasNext;
        mNextIndex = nextIndex;
        mNextOffset = nextOffset;
    }

    public boolean getHasPrev() {
        return mHasPrev;
    }

    public int getPrevIndex() {
        return mPrevIndex;
    }

    public int getPrevOffset() {
        return mPrevOffset;
    }

    public boolean getHasNext() {
        return mHasNext;
    }
    
    public int getNextIndex() {
        return mNextIndex;
    }

    public int getNextOffset() {
        return mNextOffset;
    }
}
