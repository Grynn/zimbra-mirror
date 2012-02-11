/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010, 2011 Zimbra, Inc.
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

import java.util.HashSet;
import java.util.Set;

import com.zimbra.common.account.Key.AccountBy;
import com.zimbra.common.mailbox.Color;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.UUIDUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.db.DbOfflineMailbox;
import com.zimbra.cs.db.DbPool.DbConnection;
import com.zimbra.cs.mailbox.ChangeTrackingMailbox.TracelessContext;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.redolog.op.CreateFolder;
import com.zimbra.cs.redolog.op.DeleteMailbox;

public class LocalMailbox extends DesktopMailbox {
    public LocalMailbox(MailboxData data) {
        super(data);
    }

    @Override
    void ensureSystemFolderExists() throws ServiceException {
        lock.lock();
        try {
            super.ensureSystemFolderExists();
            try {
                getFolderById(ID_FOLDER_NOTIFICATIONS);
            } catch (NoSuchItemException e) {
                CreateFolder redo = new CreateFolder(getId(), NOTIFICATIONS_PATH,
                    ID_FOLDER_USER_ROOT, Folder.FOLDER_IS_IMMUTABLE,
                    MailItem.Type.UNKNOWN, 0, MailItem.DEFAULT_COLOR_RGB, null);

                redo.setFolderIdAndUuid(ID_FOLDER_NOTIFICATIONS, UUIDUtil.generateUUID());
                redo.start(System.currentTimeMillis());
                createFolder(new TracelessContext(redo), NOTIFICATIONS_PATH,
                    ID_FOLDER_USER_ROOT, Folder.FOLDER_IS_IMMUTABLE,
                    MailItem.Type.UNKNOWN, 0, MailItem.DEFAULT_COLOR_RGB, null);
            }
            OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
            for (String accountId : prov.getAllAccountIds()) {
                Account acct = prov.get(AccountBy.id, accountId);
                if (acct == null || prov.isGalAccount(acct) || prov.isMountpointAccount(acct))
                    continue;
                try {
                    getFolderByName(null, ID_FOLDER_NOTIFICATIONS, accountId);
                } catch (NoSuchItemException e) {
                    createMountpoint(null, ID_FOLDER_NOTIFICATIONS, accountId,
                        accountId, ID_FOLDER_USER_ROOT, null,
                        MailItem.Type.UNKNOWN, 0, MailItem.DEFAULT_COLOR_RGB, false);
                }
            }
        } finally {
            lock.release();
        }
    }

    @Override
    protected void initialize() throws ServiceException {
        lock.lock();
        try {
            super.initialize();
            getCachedItem(ID_FOLDER_CALENDAR).setColor(new Color((byte)8));
            Folder.create(ID_FOLDER_NOTIFICATIONS, UUIDUtil.generateUUID(), this,
                getFolderById(ID_FOLDER_USER_ROOT), NOTIFICATIONS_PATH,
                Folder.FOLDER_IS_IMMUTABLE, MailItem.Type.UNKNOWN, 0,
                MailItem.DEFAULT_COLOR_RGB, null, null);
        } finally {
            lock.release();
        }
    }

    @Override
    Set<Folder> getAccessibleFolders(short rights) throws ServiceException {
        Set<Folder> accessable = super.getAccessibleFolders(rights);
        Set<Folder> visible = new HashSet<Folder>();

        for (Folder folder : accessable == null ? getFolderById(
            ID_FOLDER_ROOT).getSubfolderHierarchy() : accessable) {
            if (folder.getId() != ID_FOLDER_DRAFTS &&
                folder.getId() != ID_FOLDER_INBOX &&
                folder.getId() != ID_FOLDER_SPAM &&
                folder.getId() != ID_FOLDER_SENT &&
                !(folder instanceof Mountpoint))
                visible.add(folder);
        }
        return visible;
    }

    public void forceDeleteMailbox(Mailbox mbox) throws ServiceException {
        DeleteMailbox redoRecorder = new DeleteMailbox(mbox.getId());
        boolean success = false;
        try {
            beginTransaction("deleteMailbox", null, redoRecorder);
            redoRecorder.log();

            try {
                // remove all the relevant entries from the database
                DbConnection conn = getOperationConnection();
                ZimbraLog.mailbox.info("attempting to remove the zimbra.mailbox row for id "+mbox.getId());
                DbOfflineMailbox.forceDeleteMailbox(conn, mbox.getId());
                success = true;
            } finally {
                // commit the DB transaction before touching the store!  (also ends the operation)
                endTransaction(success);
            }

            if (success) {
                // remove all traces of the mailbox from the Mailbox cache
                //   (so anyone asking for the Mailbox gets NO_SUCH_MBOX or creates a fresh new empty one with a different id)
                MailboxManager.getInstance().markMailboxDeleted(mbox);
            }
        } finally {
            if (success) {
                redoRecorder.commit();
            } else {
                redoRecorder.abort();
            }
        }
    }

    @Override
    public boolean isImmutableSystemFolder(int folderId) {
        if (folderId == ID_FOLDER_NOTIFICATIONS) {
            return true;
        } else {
            return false;
        }
    }

}
