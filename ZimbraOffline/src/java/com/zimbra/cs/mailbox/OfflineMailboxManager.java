/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.mailbox;

import java.util.TimerTask;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.mailbox.Mailbox.MailboxData;
import com.zimbra.cs.mailbox.OfflineMailbox.SyncProgress;
import com.zimbra.cs.mailbox.OfflineMailbox.SyncState;
import com.zimbra.cs.offline.Offline;
import com.zimbra.cs.offline.OfflineLog;

public class OfflineMailboxManager extends MailboxManager {

    /** Default interval between client-initiated sync requests.  Can be overridden by setting the
     * {@link com.zimbra.cs.account.offline.OfflineProvisioning#A_offlineSyncInterval} attribute
     *  on the Account. */
    static final long DEFAULT_SYNC_INTERVAL = 2 * Constants.MILLIS_PER_MINUTE;

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
        return new OfflineMailbox(data);
    }

    public void sync(OfflineMailbox ombx) throws ServiceException {
        if (sSyncTask != null)
            sSyncTask.sync(ombx);
    }


    private class SyncTask extends TimerTask {
        @Override
        public void run() {
            for (String acctId : getAccountIds()) {
                try {
                    Mailbox mbox = getMailboxByAccountId(acctId);
                    if (!(mbox instanceof OfflineMailbox)) {
                        OfflineLog.offline.warn("cannot sync: not an OfflineMailbox for account " + mbox.getAccount().getName());
                        continue;
                    }

                    // do we need to sync this mailbox yet?
                    OfflineMailbox ombx = (OfflineMailbox) mbox;
                    if (ombx.getSyncFrequency() + ombx.getLastSyncTime() <= System.currentTimeMillis())
                        sync(ombx);
                } catch (ServiceException e) {
                    OfflineLog.offline.warn("cannot sync: error fetching mailbox/account for acct id " + acctId, e);
                } catch (Throwable t) {
                	OfflineLog.offline.error("Unexpected exception syncing account " + acctId, t);
                }
            }
        }

        void sync(OfflineMailbox ombx) throws ServiceException {        	
            String username = ombx.getRemoteUser();
            if (ombx.lockMailboxToSync()) { //don't want to start another sync when one is already in progress
	            try {
	                SyncProgress progress = ombx.getSyncProgress();
	                if (progress == SyncProgress.RESET) {
	                    String acctId = ombx.getAccountId();
	                    ombx.deleteMailbox();
	                    Mailbox mbox = getMailboxByAccountId(acctId);
	                    if (!(mbox instanceof OfflineMailbox)) {
	                        OfflineLog.offline.warn("cannot sync: not an OfflineMailbox for account " + username);
	                        return;
	                    }
	                    ombx = (OfflineMailbox) mbox;
	                    progress = ombx.getSyncProgress();
	                }
	
	                if (progress == SyncProgress.BLANK)
	                    InitialSync.sync(ombx);
	                else if (progress == SyncProgress.INITIAL)
	                    InitialSync.resume(ombx);
	
	                DeltaSync.sync(ombx);
	                if (PushChanges.sync(ombx))
	                    DeltaSync.sync(ombx);
	
	                ombx.setLastSyncTime(System.currentTimeMillis());
	                ombx.setSyncState(SyncState.ONLINE);
	            } catch (ServiceException e) {
	                if (e.getCode().equals(ServiceException.PROXY_ERROR)) {
	                    ombx.setSyncState(SyncState.OFFLINE);
	                    Throwable cause = e.getCause();
	                    if (cause instanceof java.net.NoRouteToHostException)
	                        OfflineLog.offline.debug("java.net.NoRouteToHostException: offline and unreachable account " + username, e);
	                    else if (cause instanceof org.apache.commons.httpclient.ConnectTimeoutException)
	                        OfflineLog.offline.debug("org.apache.commons.httpclient.ConnectTimeoutException: no connect after " + OfflineMailbox.SERVER_REQUEST_TIMEOUT_SECS + " seconds for account " + username, e);
	                    else if (cause instanceof java.net.SocketTimeoutException)
	                        OfflineLog.offline.info("java.net.SocketTimeoutException: read timed out after " + OfflineMailbox.SERVER_REQUEST_TIMEOUT_SECS + " seconds for account " + username, e);
	                    else
	                        OfflineLog.offline.warn("error communicating with account " + username, e);
	                } else {
	                    OfflineLog.offline.error("failed to sync account " + username, e);
	                }
	            } catch (Exception e) {
	                ombx.setSyncState(SyncState.ERROR);
	                OfflineLog.offline.error("uncaught exception during sync for account " + username, e);
	            } finally {
	            	ombx.unlockMailbox();
	            }
            } else {
            	OfflineLog.offline.debug("sync already in progress");
            }
        }
    }
}
