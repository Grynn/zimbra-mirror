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
package com.zimbra.cs.offline.util;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.DesktopMailbox;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.Message;
import com.zimbra.cs.mailbox.SyncExceptionHandler;
import com.zimbra.cs.mailclient.CommandFailedException;

public class OfflineErrorUtil {

    public static void reportError(DesktopMailbox mbox, int itemId, String error, Exception e) {
        String data = "";
        try {
            Message msg = mbox.getMessageById(null, itemId);
            Folder folder = mbox.getFolderById(null, msg.getFolderId());
            data = "Local folder: " + folder.getPath() + "\n";
        } catch (ServiceException ex) {
        }
        if (e instanceof CommandFailedException) {
            String req = ((CommandFailedException) e).getRequest();
            if (req != null) {
                data += "Failed request: " + req;
            }
        }
        try {
            SyncExceptionHandler.saveFailureReport(mbox, itemId, error, data, 0, e);
        } catch (ServiceException x) {
            // Ignore
        }
    }
}
