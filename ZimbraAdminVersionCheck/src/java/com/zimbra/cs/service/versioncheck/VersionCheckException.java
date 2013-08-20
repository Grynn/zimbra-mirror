/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.service.versioncheck;

import com.zimbra.common.service.ServiceException;


public class VersionCheckException extends ServiceException {
	public static final String INVALID_VC_RESPONSE     = "versioncheck.INVALID_VC_RESPONSE";
	public static final String FAILED_TO_GET_RESPONSE     = "versioncheck.FAILED_TO_GET_RESPONSE";
	public static final String EMPTY_VC_RESPONSE = "versioncheck.EMPTY_VC_RESPONSE";
   
	private VersionCheckException(String message, String code, boolean isReceiversFault, Throwable cause) {
        super(message, code, isReceiversFault, cause);
    }

    public static VersionCheckException INVALID_VC_RESPONSE(String msg, Throwable cause) {
        return new VersionCheckException("Received invalid response from the update script: " + msg, INVALID_VC_RESPONSE, SENDERS_FAULT, cause);
    }	
    
    public static VersionCheckException FAILED_TO_GET_RESPONSE(String msg, Throwable cause) {
        return new VersionCheckException("Failed to get version updates from " + msg, FAILED_TO_GET_RESPONSE, SENDERS_FAULT, cause);
    }
    
    public static VersionCheckException EMPTY_VC_RESPONSE(Throwable cause) {
        return new VersionCheckException("Response from update script is empty", EMPTY_VC_RESPONSE, SENDERS_FAULT, cause);
    }
}
