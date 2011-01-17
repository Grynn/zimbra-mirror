/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.offline.OfflineLog;

public class StatusTracker {
    ConcurrentMap<String, Process> inProgress = new ConcurrentHashMap<String, Process>();
    
    public static enum Process {BACKUP, RESTORE};
    
    synchronized boolean markAccountInProgress(String accountId, Process p) throws ServiceException {
        if (isAccountInProgress(accountId, p)) {
            return false;
        }
        inProgress.put(accountId, p);
        return true;
    }
    
    void markAccountDone(String accountId) {
        inProgress.remove(accountId);
    }
    
    boolean isAccountInProgress(String accountId, Process p) throws ServiceException {
        Process current = inProgress.get(accountId);
        if (current != null) {
            OfflineLog.offline.warn("Account already in "+current+" process");
            return true;
        } else {
            return false;
        }
    }
}
