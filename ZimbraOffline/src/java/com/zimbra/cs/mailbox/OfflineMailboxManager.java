/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.mailbox;

import java.util.TimerTask;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.Mailbox.MailboxData;
import com.zimbra.cs.offline.Offline;
import com.zimbra.cs.offline.OfflineLog;

public class OfflineMailboxManager extends MailboxManager {

    private SyncTask sSyncTask = null;


    public OfflineMailboxManager() throws ServiceException  {
        super();
    }

    @Override
    public void startup() {
        // wait 5 seconds, then start to sync
        if (sSyncTask != null)
            sSyncTask.cancel();
        sSyncTask = new SyncTask();
        Offline.sTimer.schedule(sSyncTask, 5 * Constants.MILLIS_PER_SECOND, 5 * Constants.MILLIS_PER_SECOND);
    }

    @Override
    public void shutdown() {
        if (sSyncTask != null)
            sSyncTask.cancel();
        sSyncTask = null;
    }

    /** Returns a new {@link OfflineMailbox} object to wrap the given data. */
    @Override
    Mailbox instantiateMailbox(MailboxData data) throws ServiceException {
    	OfflineAccount account = (OfflineAccount)Provisioning.getInstance().get(AccountBy.id, data.accountId);
    	if (account.isLocal()) {
    		return new Mailbox(data);
    	}
        return new OfflineMailbox(data);
    }

    public void sync(OfflineMailbox ombx) throws ServiceException {
        ombx.sync();
    }

    private class SyncTask extends TimerTask {
        @Override
        public void run() {
            for (String acctId : getAccountIds()) {
            	if (acctId.equals(OfflineProvisioning.LOCAL_ACCOUNT_ID))
            		continue;
                try {
                    Mailbox mbox = getMailboxByAccountId(acctId);
                    if (!(mbox instanceof OfflineMailbox)) {
                        OfflineLog.offline.warn("cannot sync: not an OfflineMailbox for account " + mbox.getAccount().getName());
                        continue;
                    }
                    ((OfflineMailbox)mbox).getMailboxSync().runSyncOnSchedule();
                } catch (ServiceException e) {
                    OfflineLog.offline.warn("cannot sync: error fetching mailbox/account for acct id " + acctId, e);
                } catch (Throwable t) {
                	OfflineLog.offline.error("unexpected exception syncing account " + acctId, t);
                }
            }
        }
    }
}
