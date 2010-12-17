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
package com.zimbra.cs.offline.backup;

import java.util.HashMap;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineProvisioning;

public class BackupPropertyManager {
    
    private static BackupPropertyManager instance = null;
    
    public synchronized static BackupPropertyManager getInstance() {
        if (instance == null) {
            instance = new BackupPropertyManager();
        }
        return instance;
    }
    
    private long getLongAttr(String attrKey) throws ServiceException {
        Account localAccount = OfflineProvisioning.getOfflineInstance().getLocalAccount();
        String backupStr = localAccount.getAttr(attrKey);
        if (backupStr != null && backupStr.length() > 0) {
            return Long.parseLong(backupStr);
        } else {
            return -1;
        }
    }

    /**
     * Retrieve backup interval from directory
     * @throws ServiceException
     */
    public long getInterval() throws ServiceException {
        return getLongAttr(OfflineProvisioning.A_zimbraPrefOfflineBackupInterval);
    }

    /**
     * Retrieve timestamp of last successful backup 
     * @throws ServiceException
     */
    public long getLastBackupSuccess() throws ServiceException {
        return getLongAttr(OfflineProvisioning.A_offlineBackupLastSuccess);
    }
    
    /**
     * Set the timestamp of last successful backup
     * @throws ServiceException
     */
    public void setLastBackupSuccess(long lastBackupSuccess) throws ServiceException {
        Account localAccount = OfflineProvisioning.getOfflineInstance().getLocalAccount();
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put(OfflineProvisioning.A_offlineBackupLastSuccess, lastBackupSuccess+"");
        Provisioning.getInstance().modifyAttrs(localAccount, attrs, true, true);

    }
    
    /**
     * Get the account Ids enabled for backup 
     * @throws ServiceException
     */
    public String[] getBackupAccounts() throws ServiceException {
        return OfflineProvisioning.getOfflineInstance().getLocalAccount().getMultiAttr(OfflineProvisioning.A_zimbraPrefOfflineBackupAccountId);
    }
    
    /**
     * Get the output path for backups 
     * @throws ServiceException
     */
    public String getBackupPath() throws ServiceException {
        Account localAccount = OfflineProvisioning.getOfflineInstance().getLocalAccount();
        String path = localAccount.getAttr(OfflineProvisioning.A_zimbraPrefOfflineBackupPath);
        if (path == null || path.length() < 1) {
            throw ServiceException.FAILURE("Account backup output path not set; please configure "+OfflineProvisioning.A_zimbraPrefOfflineBackupPath, null);
        } else {
            return path;
        }
    }
    
    /**
     * Get the number of backups to keep
     * Always returns a minimum value of 1, otherwise we would delete the backup we just made
     * @throws ServiceException
     */
    public int getBackupsToKeep() throws ServiceException {
        Account localAccount = OfflineProvisioning.getOfflineInstance().getLocalAccount();
        String keepStr = localAccount.getAttr(OfflineProvisioning.A_zimbraPrefOfflineBackupKeep);
        int val = 1;
        if (keepStr != null && keepStr.length() > 0) {
            val = Integer.parseInt(keepStr);
        }
        return (val > 0 ? val : 1);
    }
}
