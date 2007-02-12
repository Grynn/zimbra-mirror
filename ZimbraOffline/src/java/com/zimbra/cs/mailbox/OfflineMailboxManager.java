/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * The Original Code is: Zimbra Network
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.mailbox;

import java.util.TimerTask;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.mailbox.Mailbox.MailboxData;
import com.zimbra.cs.mailbox.OfflineMailbox.SyncState;
import com.zimbra.cs.offline.Offline;
import com.zimbra.cs.offline.OfflineLog;

public class OfflineMailboxManager extends MailboxManager {

    /** Default interval between client-initiated sync requests.  Can be overridden by setting the
     * {@link com.zimbra.cs.account.offline.OfflineProvisioning#A_offlineSyncInterval} attribute
     *  on the Account. */
    static final long DEFAULT_SYNC_INTERVAL = 2 * Constants.MILLIS_PER_MINUTE;

    private static SyncTask sSyncTask = null;


    public OfflineMailboxManager() throws ServiceException  {
        super();

        // wait 5 seconds, then start to sync
        if (sSyncTask != null)
            sSyncTask.cancel();
        sSyncTask = new SyncTask();
        Offline.sTimer.schedule(sSyncTask, 5 * Constants.MILLIS_PER_SECOND, 5 * Constants.MILLIS_PER_SECOND);
    }

    /** Returns a new {@link OfflineMailbox} object to wrap the given data. */
    @Override
    Mailbox instantiateMailbox(MailboxData data) throws ServiceException {
        return new OfflineMailbox(data);
    }

    public void sync(OfflineMailbox ombx) throws ServiceException {
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
                } catch (Exception e) {
                    OfflineLog.offline.warn("cannot sync: error fetching mailbox/account for acct id " + acctId, e);
                }
            }
        }

        void sync(OfflineMailbox ombx) throws ServiceException {
            String username = ombx.getRemoteUser();

            try {
                SyncState state = ombx.getSyncState();
                if (state == SyncState.INITIAL) {
                    // FIXME: wiping the mailbox when detecting interrupted initial sync is bad
                    String acctId = ombx.getAccountId();
                    ombx.deleteMailbox();
                    Mailbox mbox = getMailboxByAccountId(acctId);
                    if (!(mbox instanceof OfflineMailbox)) {
                        OfflineLog.offline.warn("cannot sync: not an OfflineMailbox for account " + username);
                        return;
                    }
                    ombx = (OfflineMailbox) mbox;
                    state = ombx.getSyncState();
                }
                if (state == SyncState.BLANK) {
                    InitialSync.sync(ombx);
                } else if (state == SyncState.INITIAL) {
//                  InitialSync.resume(ombx);
                    OfflineLog.offline.warn("detected interrupted initial sync; cannot recover at present: " + username);
                    return;
                }
                DeltaSync.sync(ombx);
                if (PushChanges.sync(ombx))
                    DeltaSync.sync(ombx);

                ombx.setLastSyncTime(System.currentTimeMillis());
            } catch (ServiceException e) {
                if (e.getCode().equals(ServiceException.PROXY_ERROR)) {
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
                OfflineLog.offline.error("uncaught exception during sync for account " + username, e);
            }
        }
    }
}
