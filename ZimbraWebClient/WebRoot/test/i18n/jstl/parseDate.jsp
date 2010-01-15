<!--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
-->
<%@ page import="java.util.*" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<style type="text/css">
	TR { vertical-align: top; }
	TH { text-align: left; }
</style>

<jsp:useBean id="now" class='java.util.Date' scope="request" />

<h2>${requestScope.now}</h2>
<li>parse to output: <fmt:parseDate value='200601016' pattern="yyyyMMdd" /></li>

<jsp:include page="parseDate_section.jsp">
	<jsp:param name="timeZone" value="PST" />
	<jsp:param name="locale" value="${pageContext.request.locale}" />
</jsp:include>

<jsp:include page="parseDate_section.jsp">
	<jsp:param name="timeZone" value="EST" />
	<jsp:param name="locale" value="${pageContext.request.locale}" />
</jsp:include>

<fmt:setLocale value="ja" />
<jsp:include page="parseDate_section.jsp">
	<jsp:param name="timeZone" value="EST" />
	<jsp:param name="locale" value="ja" />
</jsp:include>
