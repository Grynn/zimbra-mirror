package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZPhoneAccount;
import com.zimbra.cs.zclient.ZPhone;

public class ZPhoneAccountBean {

    private ZPhoneAccount mAccount;

    public ZPhoneAccountBean(ZPhoneAccount account) {
        mAccount = account;
    }

    public ZFolderBean getRootFolder() {
        return new ZFolderBean(mAccount.getRootFolder());
    }

    public ZPhone getPhone() {
        return mAccount.getPhone();
    }
}
