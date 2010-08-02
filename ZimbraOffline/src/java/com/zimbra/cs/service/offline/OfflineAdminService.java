/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

/*
 * Created on Jul 30, 2010
 */
package com.zimbra.cs.service.offline;

import com.zimbra.common.soap.AdminConstants;
import com.zimbra.cs.service.admin.AdminService;
import com.zimbra.soap.DocumentDispatcher;

public class OfflineAdminService extends AdminService {

    @Override
    public void registerHandlers(DocumentDispatcher dispatcher) {
        super.registerHandlers(dispatcher);
        dispatcher.registerHandler(AdminConstants.DELETE_MAILBOX_REQUEST, new OfflineDeleteMailbox());
        dispatcher.registerHandler(AdminConstants.DELETE_ACCOUNT_REQUEST, new OfflineDeleteAccount());
    }

}
