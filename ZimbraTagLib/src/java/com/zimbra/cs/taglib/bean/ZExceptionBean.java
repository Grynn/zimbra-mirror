/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.service.ServiceException;

import javax.servlet.jsp.JspException;

public class ZExceptionBean {

    private ServiceException mException;
    
    public ZExceptionBean(Throwable e) {
        if (e instanceof JspException) {
            while((e instanceof JspException) && (((JspException) e).getRootCause() != null)) {
                e =  ((JspException) e).getRootCause();
            }
        }
        if ((!(e instanceof ServiceException)) && (e.getCause() instanceof ServiceException)) {
            e = e.getCause();
        }

        if (e instanceof ServiceException) {
            mException = (ServiceException) e;
        } else {
            mException = ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    public Exception getException() {
        return mException;
    }

    public String getCode() {
        return mException.getCode();
    }
    
    public String getDisplayMessage() {
        String code = getCode();
        if (code == null) return "ERROR: "+mException.getMessage();
        
        if (code.equals(AccountServiceException.AUTH_FAILED)) {
            return "The username or password is incorrect. Verify that CAPS LOCK is not on, and then retype the current username and password";
        } else {
            return mException.getMessage();
        }
    }
}
