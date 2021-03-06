/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.client.ZPhoneAccount;
import com.zimbra.client.ZPhone;
import com.zimbra.common.service.ServiceException;

public class ZPhoneAccountBean {

    private ZPhoneAccount account;

    public ZPhoneAccountBean(ZPhoneAccount account) {
        this.account = account;
    }

    public ZFolderBean getRootFolder() {
        return new ZFolderBean(account.getRootFolder());
    }

    public ZPhone getPhone() {
        return account.getPhone();
    }

    public ZCallFeaturesBean getCallFeatures() throws ServiceException {
        return new ZCallFeaturesBean(account.getCallFeatures(), false);
    }

	public boolean getHasVoiceMail() {
		return account.getHasVoiceMail();
	}

    public String getPhoneType() {
        return account.getPhoneType();
    }

}
