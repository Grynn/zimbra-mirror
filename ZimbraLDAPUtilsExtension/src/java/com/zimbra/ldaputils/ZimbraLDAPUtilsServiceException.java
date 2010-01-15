/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
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

/*
 * Created on Jun 1, 2004
 *
 */
package com.zimbra.ldaputils;

import com.zimbra.common.service.ServiceException;


/**
 * @author schemers
 * 
 */
@SuppressWarnings("serial")
public class ZimbraLDAPUtilsServiceException extends ServiceException {

    public static final String DN_EXISTS  = "zimblraldaputils.DN_EXISTS";
    
    private ZimbraLDAPUtilsServiceException(String message, String code, boolean isReceiversFault, Throwable cause) {
        super(message, code, isReceiversFault, cause);
    }
    
    public static ZimbraLDAPUtilsServiceException DN_EXISTS(String dn) {
    	return new ZimbraLDAPUtilsServiceException("dn already exists: "+dn, DN_EXISTS, SENDERS_FAULT, null);
    }
}
