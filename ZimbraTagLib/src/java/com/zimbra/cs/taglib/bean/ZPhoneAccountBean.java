/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
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

import com.zimbra.cs.zclient.ZPhoneAccount;
import com.zimbra.cs.zclient.ZPhone;
import com.zimbra.common.service.ServiceException;

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

    public ZCallFeaturesBean getCallFeatures() throws ServiceException {
        return new ZCallFeaturesBean(mAccount.getCallFeatures(), false);
    }

	public boolean getHasVoiceMail() {
		return mAccount.getHasVoiceMail();
	}
}
