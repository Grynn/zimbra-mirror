/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline.ab.gab;

import com.google.gdata.client.GoogleService.AccountDeletedException;
import com.google.gdata.client.GoogleService.AccountDisabledException;
import com.google.gdata.client.GoogleService.CaptchaRequiredException;
import com.google.gdata.client.GoogleService.InvalidCredentialsException;
import com.google.gdata.client.GoogleService.NotVerifiedException;
import com.google.gdata.client.GoogleService.ServiceUnavailableException;
import com.google.gdata.client.GoogleService.SessionExpiredException;
import com.google.gdata.client.GoogleService.TermsNotAgreedException;
import com.zimbra.common.service.ServiceException;

@SuppressWarnings("serial")
public class GDataServiceException extends ServiceException {
	
	public static final String ACCOUNT_DELETED     = "gdata.ACCOUNT_DELETED";
	public static final String ACCOUNT_DISABLED    = "gdata.ACCOUNT_DISABLED";
	public static final String CAPTCHA_REQUIRED    = "gdata.CAPTCHA_REQUIRED";
	public static final String INVALID_CREDENTIALS = "gdata.INVALID_CREDENTIALS";
	public static final String NOT_VERIFIED        = "gdata.NOT_VERIFIED";
	public static final String SERVICE_UNAVAILABLE = "gdata.SERVICE_UNAVAILABLE";
	public static final String SESSION_EXPIRED     = "gdata.SESSION_EXPIRED";
	public static final String TERMS_NOT_AGREED    = "gdata.TERMS_NOT_AGREED";
	
    private GDataServiceException(String message, String code, boolean isReceiversFault) {
        super(message, code, isReceiversFault);
    }
    
    private GDataServiceException(String message, String code, boolean isReceiversFault, Throwable cause) {
        super(message, code, isReceiversFault, cause);
    }

    public static String getErrorCode(com.google.gdata.util.ServiceException x) {
    	if (x instanceof AccountDeletedException)
    		return ACCOUNT_DELETED;
    	if (x instanceof AccountDisabledException)
    		return ACCOUNT_DISABLED;
    	if (x instanceof CaptchaRequiredException)
    		return CAPTCHA_REQUIRED;
    	if (x instanceof InvalidCredentialsException)
    		return INVALID_CREDENTIALS;
    	if (x instanceof NotVerifiedException)
    		return NOT_VERIFIED;
    	if (x instanceof ServiceUnavailableException)
    		return SERVICE_UNAVAILABLE;
    	if (x instanceof SessionExpiredException)
    		return SESSION_EXPIRED;
    	if (x instanceof TermsNotAgreedException)
    		return TERMS_NOT_AGREED;
    	return null;
    }
    
    public static void doFailures(com.google.gdata.util.ServiceException x) throws GDataServiceException {
    	String code = getErrorCode(x);
    	if (code != null)
    		throw new GDataServiceException(x.getMessage(), code, false, x);
    }
}
