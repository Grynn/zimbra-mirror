/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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

import com.zimbra.common.auth.ZAuthToken;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.TimeZone;

public class CalSearchJSONTag extends ZimbraSimpleTag {

    private static final Pattern sSCRIPT = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);

    private String mVar;
    private ZAuthToken mAuthToken;
    private String mItemsPerPage;
    private String mTypes;
    private TimeZone mTimeZone;

    public void setVar(String var) { this.mVar = var; }
    public void setAuthtoken(ZAuthToken authToken) { this.mAuthToken = authToken; }
    public void setItemsperpage(String itemsPerPage) { mItemsPerPage = itemsPerPage; }
    public void setTypes(String types) { mTypes = types; }
    public void setTimezone(TimeZone timezone) { mTimeZone = timezone; }

    public void doTag() throws JspException, IOException {
        try {
            JspContext ctxt = getJspContext();
            PageContext pageContext = (PageContext) ctxt;
            String url = ZJspSession.getSoapURL(pageContext);
            String remoteAddr = ZJspSession.getRemoteAddr(pageContext);
            Element e = ZMailbox.getBootstrapCalSearchJSON(url, remoteAddr, mAuthToken, mItemsPerPage, mTypes);

			// Replace "</script>" with "</scr" + "ipt>" because html parsers recognize the close script tag.
			String json = e.toString();
			String json2 = sSCRIPT.matcher(json).replaceAll("</scr\"+\"ipt>");

			ctxt.setAttribute(mVar, json2,  PageContext.REQUEST_SCOPE);

        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }
}
