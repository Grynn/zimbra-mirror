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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style type="text/css">
	TR { vertical-align: top; }
	TH { text-align: left; }
	TD { text-align: right; }
</style>

<c:set var="number" value="${1234.5678}" scope="request" />

<h2>${number}</h2>
<li>parse to output: <fmt:parseNumber value='${number}' pattern="###0.####" /></li>

<c:set var="locale" value="${pageContext.request.locale}" scope="request" />
<jsp:include page="parseNumber_table.jsp" />

<% pageContext.setAttribute("locale", new Locale("ja"), PageContext.REQUEST_SCOPE); %>
<jsp:include page="parseNumber_table.jsp" />
