<!--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
-->
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
