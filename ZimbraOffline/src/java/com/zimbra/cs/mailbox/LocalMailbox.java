/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.ChangeTrackingMailbox.TracelessContext;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.redolog.op.CreateFolder;

public class LocalMailbox extends DesktopMailbox {
    public LocalMailbox(MailboxData data) throws ServiceException {
        super(data);
    }
    
    @Override synchronized void ensureSystemFolderExists() throws ServiceException {
        super.ensureSystemFolderExists();
        try {
            getFolderById(ID_FOLDER_NOTIFICATIONS);
        } catch (NoSuchItemException e) {
            CreateFolder redo = new CreateFolder(getId(), NOTIFICATIONS_PATH,
                ID_FOLDER_USER_ROOT, Folder.FOLDER_IS_IMMUTABLE,
                MailItem.TYPE_UNKNOWN, 0, MailItem.DEFAULT_COLOR_RGB, null);
            
            redo.setFolderId(ID_FOLDER_NOTIFICATIONS);
            redo.start(System.currentTimeMillis());
            createFolder(new TracelessContext(redo), NOTIFICATIONS_PATH,
                ID_FOLDER_USER_ROOT, Folder.FOLDER_IS_IMMUTABLE,
                MailItem.TYPE_UNKNOWN, 0, MailItem.DEFAULT_COLOR_RGB, null);
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
                    accountId, ID_FOLDER_USER_ROOT,
                    MailItem.TYPE_UNKNOWN, 0, MailItem.DEFAULT_COLOR_RGB);
            }
        }
    }

    @Override protected synchronized void initialize() throws ServiceException {
        super.initialize();
        getCachedItem(ID_FOLDER_CALENDAR).setColor(new MailItem.Color((byte)8));
        Folder.create(ID_FOLDER_NOTIFICATIONS, this,
            getFolderById(ID_FOLDER_USER_ROOT), NOTIFICATIONS_PATH,
            Folder.FOLDER_IS_IMMUTABLE, MailItem.TYPE_UNKNOWN, 0,
            MailItem.DEFAULT_COLOR_RGB, null, null);
    }

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
}
