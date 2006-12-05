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
