/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009 Zimbra, Inc.
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
package com.zimbra.cs.taglib.tag;

import com.zimbra.common.auth.ZAuthToken;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.taglib.bean.ZMailboxBean;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.TimeZone;

public class GetItemInfoJSONTag extends ZimbraSimpleTag {

    private static final Pattern sSCRIPT = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);

    private String mVar;
    private String mId;
    private ZMailboxBean mMailbox;
    

    public void setVar(String var) { this.mVar = var; }
    public void setId(String id) { this.mId = id; }
    public void setBox(ZMailboxBean mailbox) { this.mMailbox = mailbox; }

    public void doTag() throws JspException, IOException {
        try {
            JspContext ctxt = getJspContext();
            ZMailbox mbox = mMailbox != null ? mMailbox.getMailbox() :  getMailbox();
            Element e = mbox.getItemInfoJSON(mId);

			// Replace "</script>" with "</scr" + "ipt>" because html parsers recognize the close script tag.
			String json = e.toString();
			String json2 = sSCRIPT.matcher(json).replaceAll("</scr\"+\"ipt>");

			ctxt.setAttribute(mVar, json2,  PageContext.REQUEST_SCOPE);

        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }
}
