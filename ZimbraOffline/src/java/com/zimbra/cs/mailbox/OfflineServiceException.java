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

import com.zimbra.common.service.ServiceException;

public class OfflineServiceException extends ServiceException {
    private static final long serialVersionUID = -6070768925605337011L;

    public static final String MISCONFIGURED         = "offline.MISCONFIGURED";
    public static final String FOLDER_NOT_EMPTY      = "offline.FOLDER_NOT_EMPTY";
    public static final String UNSUPPORTED_OPERATION = "offline.UNSUPPORTED";
    
    public static final String UNEXPECTED = "offline.UNEXPECTED";
    public static final String AUTH_FAILED = "offline.AUTH_FAILED";
    public static final String OUT_OF_SYNC = "offline.OUT_OF_SYNC";

    public static final String ITEM_ID = "itemId";


    private OfflineServiceException(String message, String code, boolean isReceiversFault, Argument... args) {
        super(message, code, isReceiversFault, args);
    }


    public static OfflineServiceException MISCONFIGURED(String error) {
        return new OfflineServiceException("configuration error: " + error, MISCONFIGURED, RECEIVERS_FAULT);
    }

    public static OfflineServiceException FOLDER_NOT_EMPTY(int id) {
        return new OfflineServiceException("cannot delete non-empty folder: "+ id, FOLDER_NOT_EMPTY, RECEIVERS_FAULT, new Argument(ITEM_ID, id, Argument.Type.IID));
    }

    public static OfflineServiceException UNSUPPORTED(String op) {
        return new OfflineServiceException("operation not supported by offline client: " + op, UNSUPPORTED_OPERATION, RECEIVERS_FAULT);
    }
    
    public static OfflineServiceException UNEXPECTED(String error) {
    	return new OfflineServiceException("unexpected failure: " + error, UNEXPECTED, RECEIVERS_FAULT);
    }
    
    public static OfflineServiceException AUTH_FAILED(String username, String message) {
    	return new OfflineServiceException("authentication failed for " + username + ": " + message, AUTH_FAILED, RECEIVERS_FAULT);
    }
    
    public static OfflineServiceException OUT_OF_SYNC() {
    	return new OfflineServiceException("out of sync", OUT_OF_SYNC, RECEIVERS_FAULT);
    }
}
