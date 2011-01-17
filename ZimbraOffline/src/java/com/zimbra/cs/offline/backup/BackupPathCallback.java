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

import java.io.File;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.AttributeCallback;
import com.zimbra.cs.account.Entry;

/**
 * Backup Attribute callback. Validates that the specified directory is reabable and writeable 
 *
 */
public class BackupPathCallback extends AttributeCallback {

    @SuppressWarnings("unchecked")
    @Override
    public void postModify(Map context, String attrName, Entry entry,
            boolean isCreate) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void preModify(Map context, String attrName, Object attrValue,
            Map attrsToModify, Entry entry, boolean isCreate)
            throws ServiceException {
        if (!(attrValue instanceof String)) {
            throw ServiceException.INVALID_REQUEST(attrName+" must be a String", null);
        }
        File testDir = new File((String)attrValue);
        if (!testDir.exists()) {
            if (!testDir.mkdirs()) {
                throw ServiceException.INVALID_REQUEST("Directory "+testDir+" does not exist can could not mkdir", null);
            }
        }
        if (!testDir.canRead() || !testDir.canWrite()) {
            throw ServiceException.INVALID_REQUEST("Need read/write permissions on directory "+testDir, null);
        }
    }
}
