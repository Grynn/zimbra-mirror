/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZMailbox.ZActionResult;

public class ZActionResultBean {

    private ZActionResult mResult;
    private String[] mIds;
    private int mIdCount = -1;

    public ZActionResultBean(ZActionResult result) {
        mResult = result;
    }

    public synchronized String[] getIds() {
        if (mIds == null) mIds = mResult.getIdsAsArray();
        return mIds;
    }

    public synchronized int getIdCount() {

        if (mIdCount == -1) {
            if (mIds != null) {
                mIdCount = mIds.length;
            } else {
                String ids = mResult.getIds();
                int len = ids.length();
                if (ids == null || len == 0) return 0;
                mIdCount = 1;
                for (int i=0; i < len; i++) {
                    if (ids.charAt(i) == ',') mIdCount++;
                }
            }
        }
        return mIdCount;
    }
}
