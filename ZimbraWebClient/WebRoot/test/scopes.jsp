<%@ page session="false" %>
<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%!
	static String escape(Object o) {
		if (o == null) return "<i>null</i>";
		return String.valueOf(o).replaceAll("&", "&amp;").replaceAll("<", "&lt;");
	}
%>
<h3>Page</h3>
<%{
	int scope = PageContext.PAGE_SCOPE;
	Enumeration e = pageContext.getAttributeNamesInScope(scope);
	while (e.hasMoreElements()) {
		String aname = (String)e.nextElement();
		Object avalue = pageContext.getAttribute(aname, scope);
		%><li><%=escape(aname)%> = <%=escape(avalue)%></li><%
	}
}%>
<h3>Request</h3>
<%{
	int scope = PageContext.REQUEST_SCOPE;
	Enumeration e = pageContext.getAttributeNamesInScope(scope);
	while (e.hasMoreElements()) {
		String aname = (String)e.nextElement();
		Object avalue = pageContext.getAttribute(aname, scope);
		%><li><%=escape(aname)%> = <%=escape(avalue)%></li><%
	}
}%>
<h3>Session: <%=request.getSession(false)!=null?request.getSession(false):"<i>no session</i>"%></h3>
<%try {
	int scope = PageContext.SESSION_SCOPE;
	Enumeration e = pageContext.getAttributeNamesInScope(scope);
	while (e.hasMoreElements()) {
		String aname = (String)e.nextElement();
		Object avalue = pageContext.getAttribute(aname, scope);
		%><li><%=escape(aname)%> = <%=escape(avalue)%></li><%
	}
} catch (Exception e) {
	%><b>Error: <%=e.getMessage()%></b><%
}%>
<h3>Application</h3>
<%{
	int scope = PageContext.APPLICATION_SCOPE;
	Enumeration e = pageContext.getAttributeNamesInScope(scope);
	while (e.hasMoreElements()) {
		String aname = (String)e.nextElement();
		com.zimbra.common.util.ZimbraLog.webclient.debug("+++ aname: "+aname);
		try {
			Object avalue = pageContext.getAttribute(aname, scope);
			com.zimbra.common.util.ZimbraLog.webclient.debug("   avalue: "+avalue);
			%><li><%=escape(aname)%> = <%=escape(avalue)%></li><%
		}
		catch (Exception ex) {
			%><li><%=escape(aname)%> = <b>error:</b> <%=ex%></li><%
		}
	}
}%>
<%--
--%>