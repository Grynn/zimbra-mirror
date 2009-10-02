package com.zimbra.cs.service.versioncheck;

import com.zimbra.common.service.ServiceException;


public class VersionCheckException extends ServiceException {
	public static final String INVALID_VC_RESPONSE     = "versioncheck.INVALID_VC_RESPONSE";
   
	private VersionCheckException(String message, String code, boolean isReceiversFault, Throwable cause) {
        super(message, code, isReceiversFault, cause);
    }

    public static VersionCheckException INVALID_VC_RESPONSE(String msg, Throwable cause) {
        return new VersionCheckException("Received invalid response from the update script: " + msg, INVALID_VC_RESPONSE, SENDERS_FAULT, cause);
    }	
}
