/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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

import com.zimbra.cs.zclient.ZPhone;
import com.zimbra.common.service.ServiceException;

public class ZPhoneBean {

    public static final String INVALID_PHNUM_OWN_PHONE_NUMBER = ZPhone.INVALID_PHNUM_OWN_PHONE_NUMBER;
    public static final String INVALID_PHNUM_INTERNATIONAL_NUMBER = ZPhone.INVALID_PHNUM_INTERNATIONAL_NUMBER;
    public static final String INVALID_PHNUM_BAD_NPA = ZPhone.INVALID_PHNUM_BAD_NPA;
    public static final String INVALID_PHNUM_BAD_LINE = ZPhone.INVALID_PHNUM_BAD_LINE;
    public static final String INVALID_PHNUM_EMERGENCY_ASSISTANCE = ZPhone.INVALID_PHNUM_EMERGENCY_ASSISTANCE;
    public static final String INVALID_PHNUM_DIRECTORY_ASSISTANCE = ZPhone.INVALID_PHNUM_DIRECTORY_ASSISTANCE;
    public static final String INVALID_PHNUM_BAD_FORMAT = ZPhone.INVALID_PHNUM_BAD_FORMAT;
    public static final String VALID = ZPhone.VALID;

    private ZPhone mPhone;

    public ZPhoneBean(ZPhone phone) {
        mPhone = phone;
    }
    public ZPhoneBean(String name) throws ServiceException {
	this(new ZPhone(name));
    }

    public String getName() {
	return mPhone.getName();
    }

    public String getDisplay() {
	return mPhone.getDisplay();
    }
    
    public String getValidity() {
	return mPhone.getValidity();
    }
}
