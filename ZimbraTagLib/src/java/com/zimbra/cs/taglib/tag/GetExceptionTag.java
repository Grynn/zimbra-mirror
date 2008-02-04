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
package com.zimbra.cs.taglib.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import com.zimbra.cs.taglib.bean.ZExceptionBean;
import com.zimbra.cs.taglib.bean.ZTagLibException;
import com.zimbra.cs.zclient.ZClientException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.service.ServiceException;

public class GetExceptionTag extends ZimbraSimpleTag {
    
    private String mVar;
    private Exception mException;
    
    public void setVar(String var) { this.mVar = var; }
    
    public void setException(Exception e) { this.mException = e; }
    
    public void doTag() throws JspException, IOException {
        ZExceptionBean eb = new ZExceptionBean(mException);
        Exception e = eb.getException();
        if (e != null) {
            if ((!(e instanceof ServiceException)) || (e instanceof ZTagLibException) || (e instanceof ZClientException))
                ZimbraLog.webclient.warn("local exception", e);
        }
        getJspContext().setAttribute(mVar, eb,  PageContext.PAGE_SCOPE);
    }
}
