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
