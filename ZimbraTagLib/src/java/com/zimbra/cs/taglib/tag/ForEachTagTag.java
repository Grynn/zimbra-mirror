/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.taglib.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZTagBean;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZTag;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;
import java.util.List;

public class ForEachTagTag extends ZimbraSimpleTag {
    
    private String mVar;
    
    public void setVar(String var) { this.mVar = var; }
    
    public void doTag() throws JspException, IOException {
        try {
            JspFragment body = getJspBody();
            if (body == null) return;
            JspContext jctxt = getJspContext();
            ZMailbox mbox = getMailbox();
            List<ZTag> tags = mbox.getAllTags();
            for (ZTag tag: tags) {
                jctxt.setAttribute(mVar, new ZTagBean(tag));
                body.invoke(null);
            }
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
