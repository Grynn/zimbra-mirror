/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.bp;

import com.zimbra.common.service.ServiceException;

/**
 * Created by IntelliJ IDEA.
 * User: ccao
 * Date: Oct 8, 2008
 * Time: 4:03:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class BulkProvisionException extends ServiceException {
    public static final String BP_TOO_MANY_ACCOUNTS = "bulkprovision.BP_TOO_MANY_ACCOUNTS";

    private BulkProvisionException(String message, String code, boolean isReceiversFault) {
        super(message, code, isReceiversFault);
    }

     public static BulkProvisionException BP_TOO_MANY_ACCOUNTS (String desc) {
        return new BulkProvisionException("too many accounts: " + desc, BP_TOO_MANY_ACCOUNTS, SENDERS_FAULT);
    }
}
