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

import com.zimbra.cs.zclient.ZContact;
import com.zimbra.cs.zclient.ZMailbox.ZSearchGalResult;
import com.zimbra.cs.zclient.ZMailbox.GalEntryType;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class ZSearchGalResultBean {

    private ZSearchGalResult mResult;
    private List<ZContactBean> mContacts;

    public ZSearchGalResultBean(ZSearchGalResult result) {
        mResult = result;
        mContacts = new ArrayList<ZContactBean>(result.getContacts().size());
        for (ZContact contact : result.getContacts()) {
            mContacts.add(new ZContactBean(contact, true));
        }
        Collections.sort(mContacts);
    }

    public int getSize() { return mContacts.size(); }

    public boolean getHasMore() { return mResult.getHasMore(); }

    public GalEntryType getGalEntryType() { return mResult.getGalEntryType(); }

    public String getQuery() { return mResult.getQuery(); }

    public List<ZContactBean> getContacts() {
        return mContacts;
    }

}
