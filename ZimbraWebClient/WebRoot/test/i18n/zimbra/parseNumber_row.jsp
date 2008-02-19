<%@ page import="java.text.*,java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%
	String type = request.getParameter("type");
	Locale locale = (Locale)pageContext.getAttribute("locale", PageContext.REQUEST_SCOPE);

	String pattern = null;
	if ("number".equals(type)) {
		pattern = ((DecimalFormat)NumberFormat.getNumberInstance(locale)).toPattern();
	}
	else if ("currency".equals(type)) {
		pattern = ((DecimalFormat)NumberFormat.getCurrencyInstance(locale)).toPattern();
	}
	else if ("percent".equals(type)) {
		pattern = ((DecimalFormat)NumberFormat.getPercentInstance(locale)).toPattern();
	}

	pageContext.setAttribute("pattern", pattern);
%>

<tr><td>${param.type}</td>
	<td>${pattern}</td>
	<td><fmt:formatNumber var='value' value='${requestScope.number}' pattern="${pattern}" />
		${value}
	</td>
	<td><% try { %>
			<fmt:parseNumber var='value' value="${value}" pattern='${pattern}' />
			<fmt:formatNumber value="${value}" pattern="#,##0.####" />
		<% } catch (Exception e) { %>
			<%=e.getMessage()%>
		<% } %>
	</td>
</tr>
