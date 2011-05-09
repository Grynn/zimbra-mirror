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
package com.zimbra.cs.mailbox;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.offline.OfflineLog;

/**
 * A checkpoint consists of two integers, changeId and lastSyncedGroup.
 * changeId tells if the zcs gal changes from the last time checkpointing happened,
 * lastSyncedGroup is the last group number that gal sync synced. If changeId
 * changes, full sync should be performed, we couldn't save any efforts by using
 * checkpointing. - If changeId doesn't change, we use lastSyncedGroup to skip
 * the groups of contacts that already synced in the last gal sync attempt.
 * 
 */
public final class GalSyncCheckpointUtil {

    private static final String OFFLINE_GAL_CHECKPOINT = "offline_sync_checkpoint";
    private static final GalSyncCheckpoint INVALID_CHECKPOINT = new GalSyncCheckpoint(-1, -1);

    private GalSyncCheckpointUtil() {
    }

    private static int getCheckpointChangeId(Mailbox mbox)
            throws ServiceException {
        Metadata md = mbox.getConfig(null, OFFLINE_GAL_CHECKPOINT);
        if (md == null) {
            md = new Metadata();
            md.put(mbox.getAccountId(), INVALID_CHECKPOINT);
            mbox.setConfig(null, OFFLINE_GAL_CHECKPOINT, md);
        }
        String checkpt = md.get(mbox.getAccountId());
        OfflineLog.offline.debug("gal sync checkpoint for mbox "
                + mbox.getAccount().getName() + ": [" + checkpt + "]");
        return Integer.parseInt(checkpt.split(":")[0]);
    }

    private static int getChangeId(String token) {
        return Integer.parseInt(token.split(":")[2]);   //token is in form of timestamp:zcs_gal_account_id:changeId
    }

    static boolean isDbChanged(Mailbox mbox, String token) throws ServiceException {
        int changeId = getChangeId(token);
        return changeId != getCheckpointChangeId(mbox);
    }

    static int getCheckpointGroupNumber(Mailbox mbox) throws ServiceException {
        Metadata md = mbox.getConfig(null, OFFLINE_GAL_CHECKPOINT);
        if (md == null) {
            md = new Metadata();
            md.put(mbox.getAccountId(), INVALID_CHECKPOINT);
            mbox.setConfig(null, OFFLINE_GAL_CHECKPOINT, md);
        }
        String checkpt = md.get(mbox.getAccountId());
        return Integer.parseInt(checkpt.split(":")[1]);
    }

    static void removeSyncCheckpoint(Mailbox mbox) throws ServiceException {
        Metadata md = mbox.getConfig(null, OFFLINE_GAL_CHECKPOINT);
        if (md == null) {
            md = new Metadata();
        }
        md.put(mbox.getAccountId(), INVALID_CHECKPOINT);
        mbox.setConfig(null, OFFLINE_GAL_CHECKPOINT, md);
    }

    static void checkpoint(Mailbox mbox, String token, int groupNum)
            throws ServiceException {
        Metadata md = mbox.getConfig(null, OFFLINE_GAL_CHECKPOINT, true);
        md.put(mbox.getAccountId(), new GalSyncCheckpoint(getChangeId(token), groupNum));
        mbox.setConfig(null, OFFLINE_GAL_CHECKPOINT, md, true);
    }

    private static final class GalSyncCheckpoint {
        int dbChangeId;
        int lastSyncedGroup;

        GalSyncCheckpoint(int change, int group) {
            this.lastSyncedGroup = group;
            this.dbChangeId = change;
        }

        public String toString() {
            return this.dbChangeId + ":" + this.lastSyncedGroup;
        }
    }
}