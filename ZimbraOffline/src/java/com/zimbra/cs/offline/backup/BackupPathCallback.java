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

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.AttributeCallback;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.account.callback.CallbackContext;

/**
 * Backup Attribute callback. Validates that the specified directory is readable and writable 
 *
 */
public class BackupPathCallback extends AttributeCallback {

    @SuppressWarnings("unchecked")
    @Override
    public void postModify(CallbackContext context, String attrName, Entry entry) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void preModify(CallbackContext context, String attrName, Object attrValue,
            Map attrsToModify, Entry entry)
            throws ServiceException {
        if (!(attrValue instanceof String)) {
            throw ServiceException.INVALID_REQUEST(attrName+" must be a String", null);
        }
        BackupPropertyManager.getInstance().validateBackupPath((String) attrValue);
    }
}
