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
package com.zimbra.cs.taglib.tag.signature;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZSignature;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ForEachSignatureTag extends ZimbraSimpleTag {

    private String mVar;
    private boolean mRefresh;

    public void setVar(String var) { this.mVar = var; }
    public void setRefresh(boolean refresh) { this.mRefresh = refresh; }

    public void doTag() throws JspException, IOException {
        try {
            JspFragment body = getJspBody();
            if (body == null) return;
            JspContext jctxt = getJspContext();
            ZMailbox mbox = getMailbox();
            List<ZSignature> sigs = mbox.getAccountInfo(mRefresh).getSignatures();
            Collections.sort(sigs);
            for (ZSignature sig: sigs) {
                jctxt.setAttribute(mVar, sig);
                body.invoke(null);
            }
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
