/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010 Zimbra, Inc.
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
package com.zimbra.cs.service.offline;

import java.util.Map;
import java.util.Set;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.offline.backup.AccountBackupInfo;
import com.zimbra.cs.offline.backup.AccountBackupProducer;
import com.zimbra.cs.offline.backup.BackupInfo;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineBackupEnumService extends DocumentHandler {
    
    @Override
    public Element handle(Element request, Map<String, Object> context)
            throws ServiceException {
        Set<AccountBackupInfo> info = AccountBackupProducer.getInstance().getStoredBackups();
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Element response = zsc.createElement(OfflineConstants.ACCOUNT_BACKUP_ENUM_RESPONSE);
        for (AccountBackupInfo acctInfo : info) {
            Element acctElem = response.addElement(AdminConstants.E_ACCOUNT);
            acctElem.addAttribute(AdminConstants.E_ID, acctInfo.getAccountId());
            for (BackupInfo backup : acctInfo.getBackups()) {
                Element backupElem = acctElem.addElement(OfflineConstants.E_BACKUP);
                backupElem.addAttribute(AdminConstants.A_TIME, backup.getTimestamp());
                backupElem.addAttribute(AdminConstants.A_FILE_SIZE, backup.getFile().length());
            }
        }
        return response;
    }
}
