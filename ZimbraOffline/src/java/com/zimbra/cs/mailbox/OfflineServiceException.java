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

import com.zimbra.common.service.ServiceException;

public class OfflineServiceException extends ServiceException {
    private static final long serialVersionUID = -6070768925605337011L;

    public static final String MISCONFIGURED         = "offline.MISCONFIGURED";
    public static final String FOLDER_NOT_EMPTY      = "offline.FOLDER_NOT_EMPTY";
    public static final String UNSUPPORTED_OPERATION = "offline.UNSUPPORTED";

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
}
