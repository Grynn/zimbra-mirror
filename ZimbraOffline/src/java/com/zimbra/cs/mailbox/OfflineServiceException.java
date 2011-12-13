/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

public class OfflineServiceException extends ServiceException {
    private static final long serialVersionUID = -6070768925605337011L;

    public static final String MISCONFIGURED         = "offline.MISCONFIGURED";
    public static final String FOLDER_NOT_EMPTY      = "offline.FOLDER_NOT_EMPTY";
    public static final String UNSUPPORTED_OPERATION = "offline.UNSUPPORTED";
    public static final String UNKNOWN_CLIENT_EVENT = "offline.UNKNOWN_CLIENT_EVENT";
    
    public static final String UNEXPECTED = "offline.UNEXPECTED";
    public static final String AUTH_FAILED = "offline.AUTH_FAILED";
    public static final String OUT_OF_SYNC = "offline.OUT_OF_SYNC";
    public static final String MISSING_GAL_MAILBOX = "offline.MISSING_GAL_MAILBOX";
    public static final String ONLINE_ONLY_OP = "offline.ONLINE_ONLY_OP";
    public static final String MOUNT_INVALID_GRANTEE = "offline.MOUNT_INVALID_GRANTEE";
    
    public static final String CALDAV_LOGIN_FAILED ="offline.CALDAV_LOGIN_FAILED";
    public static final String YCALDAV_NEED_UPGRADE = "offline.YCALDAV_NEED_UPGRADE";
    public static final String GCALDAV_NEED_ENABLE = "offline.GCALDAV_NEED_ENABLE";
    public static final String YCONTACT_NEED_VERIFY = "offline.YCONTACT_NEED_VERIFY";
    public static final String MUST_RESYNC = "offline.MUST_RESYNC";
    public static final String GAL_NOT_READY = "offline.GAL_NOT_READY";

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
    
    public static OfflineServiceException UNKNOWN_CLIENT_EVENT(String event) {
        return new OfflineServiceException("unknown client event: " + event, UNKNOWN_CLIENT_EVENT, SENDERS_FAULT);
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
    
    public static OfflineServiceException MISSING_GAL_MAILBOX(String acctName) {
        return new OfflineServiceException("unable to access GAL mailbox for " + acctName, MISSING_GAL_MAILBOX, RECEIVERS_FAULT);
    }
    
    public static OfflineServiceException ONLINE_ONLY_OP(String op) {
        return new OfflineServiceException("operation only supported when client is online: " + op, ONLINE_ONLY_OP, RECEIVERS_FAULT);
    }
    
    public static OfflineServiceException MOUNT_INVALID_GRANTEE() {
        return new OfflineServiceException("invalid grantee account id", MOUNT_INVALID_GRANTEE, RECEIVERS_FAULT);
    }
    
    public static OfflineServiceException CALDAV_LOGIN_FAILED() {
        return new OfflineServiceException("CalDAV login failed", CALDAV_LOGIN_FAILED, RECEIVERS_FAULT);
    }
    
    public static OfflineServiceException YCALDAV_NEED_UPGRADE() {
        return new OfflineServiceException("must upgrade to all-new yahoo calendar service", YCALDAV_NEED_UPGRADE, RECEIVERS_FAULT); 
    }
    
    public static OfflineServiceException GCALDAV_NEED_ENABLE() {
        return new OfflineServiceException("must enable google calendar service", GCALDAV_NEED_ENABLE, RECEIVERS_FAULT); 
    }
    
    public static OfflineServiceException YCONTACT_NEED_VERIFY() {
        return new OfflineServiceException("must grant access to yahoo contact API", YCONTACT_NEED_VERIFY, RECEIVERS_FAULT);
    }

    public static OfflineServiceException MUST_RESYNC() {
        return new OfflineServiceException("must resync mailbox", MUST_RESYNC, RECEIVERS_FAULT);
    }

    public static OfflineServiceException GAL_NOT_READY() {
        return new OfflineServiceException("GAL is not yet ready", GAL_NOT_READY, RECEIVERS_FAULT);
    }
}

