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