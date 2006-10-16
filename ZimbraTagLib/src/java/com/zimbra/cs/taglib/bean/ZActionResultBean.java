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
