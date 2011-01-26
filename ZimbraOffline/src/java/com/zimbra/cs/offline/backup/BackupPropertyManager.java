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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;

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
        String[] acctAttr = OfflineProvisioning.getOfflineInstance().getLocalAccount().getMultiAttr(OfflineProvisioning.A_zimbraPrefOfflineBackupAccountId);
        if (acctAttr.length == 1) {
            return acctAttr[0].split(",");
        } else {
            return acctAttr;
        }
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

    private String getDefaultBackupPath() {
        String defaultBackupPath = null;
        try {
            defaultBackupPath = new File(OfflineLC.zdesktop_backup_dir.value()).getCanonicalPath();
        } catch (IOException e) {
            OfflineLog.offline.error("Unable to set default path to "+OfflineLC.zdesktop_backup_dir.value(), e);
        }
        return defaultBackupPath;
    }

    public void testAndSetDefaultBackupPath() throws ServiceException {
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        Account localAccount = prov.getLocalAccount();
        String backupPath = localAccount.getAttr(OfflineProvisioning.A_zimbraPrefOfflineBackupPath);
        if (backupPath == null) {
            prov.setAccountAttribute(localAccount, OfflineProvisioning.A_zimbraPrefOfflineBackupPath, getDefaultBackupPath());
        } else {
            try {
                validateBackupPath(backupPath);
            } catch (ServiceException se) {
                OfflineLog.offline.warn("Current backup path is invalid; setting to default", se);
                prov.setAccountAttribute(localAccount, OfflineProvisioning.A_zimbraPrefOfflineBackupPath, getDefaultBackupPath());
            }
        }
    }
    
    public void validateBackupPath(String backupPath) throws ServiceException {
        File testDir = new File((String) backupPath);
        if (testDir.getPath().indexOf(LC.zimbra_home.value()) == 0) {
            throw ServiceException.INVALID_REQUEST("Backups path "+testDir+" may not be placed under Zimbra Desktop data dir "+LC.zimbra_home.value(), null);
        }
        if (!testDir.exists()) {
            if (!testDir.mkdirs()) {
                throw ServiceException.INVALID_REQUEST("Directory "+testDir+" does not exist can could not mkdir", null);
            }
        }
        if (!testDir.canRead() || !testDir.canWrite()) {
            throw ServiceException.INVALID_REQUEST("Need read/write permissions on directory "+testDir, null);
        }
    }
}
