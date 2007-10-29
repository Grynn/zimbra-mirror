/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
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

package com.zimbra.cs.taglib.tag.voice;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZPhoneAccount;
import com.zimbra.common.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;
import java.util.List;

public class CheckVoiceStatusTag extends ZimbraSimpleTag {
	private String mVar;

	public void setVar(String var) { mVar = var; }

	public void doTag() throws JspException, IOException {
		try {
			ZMailbox mbox = getMailbox();
			List<ZPhoneAccount> accounts = mbox.getAllPhoneAccounts();
			Boolean ok = accounts.size() > 0;
			getJspContext().setAttribute(mVar, ok, PageContext.PAGE_SCOPE);
		} catch (ServiceException e) {
			getJspContext().setAttribute(mVar, Boolean.FALSE, PageContext.PAGE_SCOPE);
			throw new JspTagException(e);
		}
	}
}