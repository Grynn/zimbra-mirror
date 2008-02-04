/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006 Zimbra, Inc.
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
package com.zimbra.cs.taglib.bean;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ExceptionToString;

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
            mException = ZTagLibException.TAG_EXCEPTION(e.getMessage(), e);
        }
    }

    public Exception getException() {
        return mException;
    }

    public String getCode() {
        return mException.getCode();
    }


    public String getId() {
        return mException.getId();
    }
    
    public String getStackStrace() {
       return ExceptionToString.ToString(mException);
    }
}
