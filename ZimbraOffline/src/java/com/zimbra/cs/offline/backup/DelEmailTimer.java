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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.InitialSync;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.SyncMailbox;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.common.OfflineConstants.SyncMsgOptions;
import com.zimbra.cs.util.ZimbraApplication;

public class DelEmailTimer extends TimerTask {

    private static Timer syncMaildeltimer;
    private static DelEmailTimer syncMaildelcurrentTask;
    private static final int FIFTEEN_MIN = 15;

    public synchronized static void initialize() {
        if (syncMaildelcurrentTask == null) {
            syncMaildelcurrentTask = new DelEmailTimer();
            syncMaildeltimer = new Timer("delEmail");
            Calendar threadRunTime = GregorianCalendar.getInstance();
            threadRunTime.add(Calendar.MINUTE, FIFTEEN_MIN);
            syncMaildeltimer.scheduleAtFixedRate(syncMaildelcurrentTask, threadRunTime.getTime(), Constants.MILLIS_PER_DAY);
        }
    }

    public static void shutdown() {
        if (syncMaildelcurrentTask != null) {
                synchronized(syncMaildelcurrentTask) {
                    syncMaildelcurrentTask.cancel();
                }
        }
        syncMaildelcurrentTask = null;
    }

    public void run() {
        if (ZimbraApplication.getInstance().isShutdown()) {
            shutdown();
            return;
        }

        try {
            for (Account account : OfflineProvisioning.getOfflineInstance().getAllZcsAccounts()) {
                if (((OfflineAccount) account).getBooleanAttr(OfflineConstants.A_offlineEnableExpireOldEmails, true)) {
                    expireOldMsgs(account);
                }
            }
        } catch (ServiceException e) {
            OfflineLog.offline.info("Error while extracting ZCS accounts" + e);
        }
    }

    private void expireOldMsgs(Account account) {
        long cutoffTime = 0L;
        try {
            switch (SyncMsgOptions.getOption(account.getAttr(OfflineConstants.A_offlinesyncEmailDate))) {
            case SYNCTORELATIVEDATE :
                //task is performed only if the sync is set to a relative date
                cutoffTime = Long.parseLong(InitialSync.convertRelativeDatetoLong(account.getAttr(OfflineConstants.A_offlinesyncRelativeDate) ,
                        account.getAttr(OfflineConstants.A_offlinesyncFieldName)));
                if (cutoffTime > 0) {
                    OfflineLog.offline.info("deleting messages from %s older than %d", account.getName(), cutoffTime);
                    Folder root = null;
                    Set<Folder> visible = null;
                    try {
                        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccount(account);
                        OperationContext octxtOwner = new OperationContext(mbox);
                        root = mbox.getFolderById(octxtOwner, Mailbox.ID_FOLDER_ROOT);
                        ((SyncMailbox)mbox).deleteMsginFolder(cutoffTime, root, visible);
                    } catch (ServiceException e) {
                        OfflineLog.offline.info("error in (%s) while deleting stale messages", account.getName());
                    }
                    OfflineLog.offline.info("message in (%s) are deleted", account.getName());
                }
                break;
            }
        } catch (NumberFormatException x) {
            OfflineLog.offline.warn("unable to parse syncEmailDate", x);
        }
    }
}