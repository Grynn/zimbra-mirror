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

import java.util.Date;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;

public class BackupTimer extends Thread {

    private static BackupTimer instance = null; 
    
    public synchronized static void initialize() {
        if (instance == null) {
            instance = new BackupTimer();
            instance.setDaemon(true); //ZD can exit immediately even if backup is in progress
            instance.start();
        }
    }
    
    public BackupTimer() {
        super("backup-timer");
    }

    public static void updateInterval() throws ServiceException {
        if (instance != null) {
            instance.intervalChanged();
        }
    }
    
    public static void shutdown() {
        if (instance != null) {
            instance.halted = true;
            if (instance.sleeping) {
                synchronized(instance) {
                    instance.notify();
                }
            }
        }
    }
    
    private volatile long interval = Constants.MILLIS_PER_DAY;
    private long lastBackupSuccess = -1;
    private boolean halted = false;
    private boolean sleeping = false;
    private final long clockTolerance = Constants.MILLIS_PER_MINUTE;

    private synchronized void waitForInterval(long lastBackupAttempt) {
        try {
            sleeping = true;
            if (interval <= 0) {
                OfflineLog.offline.info("Auto-Backup Disabled");
                wait(); //wake on property change
            } else {
                long nextTime = interval + lastBackupAttempt;
                OfflineLog.offline.info("Waiting until next backup at "+new Date(nextTime));
                long waitTime = nextTime - System.currentTimeMillis();
                while (waitTime > 0) {
                    wait(waitTime > clockTolerance ? clockTolerance : waitTime);
                    waitTime = nextTime - System.currentTimeMillis();
                } 
            }
        } catch (InterruptedException e) {
        } finally {
            sleeping = false;
        }
    }

    public void intervalChanged() throws ServiceException {
        interval = BackupPropertyManager.getInstance().getInterval();
        synchronized (this) {
            if (sleeping) {
                notify();
            }
        }
    }
    
    @Override
    public void run() {
        try {
            intervalChanged();
            lastBackupSuccess = BackupPropertyManager.getInstance().getLastBackupSuccess(); 
            BackupPropertyManager.getInstance().testAndSetDefaultBackupPath();
        } catch (ServiceException e) {
            OfflineLog.offline.error("Exception while initializing account backup",e);
            return; 
        } 
        //delay initialization; this gets started before UserServlet is available which would cause backup failure
        while (!OfflineSyncManager.getInstance().isServiceUp()) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        }
        //track last attempt in memory so we don't repeatedly attempt backup when its failing`
        long lastBackupAttempt = lastBackupSuccess;
        while (!halted) {
            if (interval > 0) {
                try {
                    if (System.currentTimeMillis() - interval > lastBackupAttempt) {
                        lastBackupAttempt = System.currentTimeMillis();
                        AccountBackupProducer.getInstance().backupAllAccounts();
                        lastBackupSuccess = System.currentTimeMillis();
                        //store last success in directory so its available after ZD restart
                        BackupPropertyManager.getInstance().setLastBackupSuccess(lastBackupSuccess);
                    }
                } catch (Exception e) {
                    OfflineLog.offline.error("Unable to backup accounts due to exception",e);
                }
            }
            waitForInterval(lastBackupAttempt);
        }
    }
}