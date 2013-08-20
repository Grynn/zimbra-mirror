/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
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
package com.zimbra.cs.account.offline;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element.XMLElement;
import com.zimbra.cs.account.soap.SoapProvisioning;
import com.zimbra.cs.offline.common.OfflineConstants;

public class OfflineSoapProvisioning extends SoapProvisioning {

    public void resetGal(String accountId) throws ServiceException {
        XMLElement req = new XMLElement(OfflineConstants.RESET_GAL_ACCOUNT_REQUEST);
        req.addElement(AdminConstants.E_ID).setText(accountId);
        invoke(req);
    }
}
