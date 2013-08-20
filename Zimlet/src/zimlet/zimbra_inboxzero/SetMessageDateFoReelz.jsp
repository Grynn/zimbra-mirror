<!--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2010, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
-->
<%@ page import="java.util.*" %>
<%@ page import="com.zimbra.cs.account.*" %>
<%@ page import="com.zimbra.cs.mailbox.*" %>
<%@ page import="com.zimbra.common.service.ServiceException" %>
<%
int itemId = getParameterInt(request, "itemId", -1);
long date = getParameterLong(request, "date", -1L);
if (itemId != -1 && date != -1) {
	try {
		AuthToken authToken = AuthToken.getAuthToken(getCookie(request, "ZM_AUTH_TOKEN"));
		Account account = AccessManager.getInstance().getAccount(authToken);
		Mailbox mbox = MailboxManager.getInstance().getMailboxByAccount(account);

		OperationContext octxt = new OperationContext(mbox);
		mbox.setDate(octxt, itemId, MailItem.TYPE_MESSAGE, date);
		%>Success<%
	}
	catch (ServiceException e) {
		%>Error: <font color=red>error: <%=e%></font><%
	}
}
%>
<%!
static int getParameterInt(HttpServletRequest request, String name, int defaultValue) {
    try {
        return Integer.parseInt(request.getParameter(name));
    }
    catch (NumberFormatException e) {
        return defaultValue;
    }
}
static long getParameterLong(HttpServletRequest request, String name, long defaultValue) {
    try {
        return Long.parseLong(request.getParameter(name));
    }
    catch (NumberFormatException e) {
        return defaultValue;
    }
}
static String getCookie(HttpServletRequest request, String name) {
	for (Cookie cookie : request.getCookies()) {
		if (cookie.getName().equals(name)) {
			return cookie.getValue();
		}
	}
	return null;
}
%>