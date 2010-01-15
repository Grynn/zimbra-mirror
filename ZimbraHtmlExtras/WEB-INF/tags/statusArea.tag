<%--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
--%>
<%@ tag body-content="scriptless" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<zm:getMailbox var="mailbox"/>

<tr>
	<td colspan=100 align=middle>
		<table border=0 cellpadding=0 cellspacing=0><tr><td>
		<c:if test="${empty requestScope.statusMessage}">
			<div style='visibility:hidden'>
		</c:if>
		<c:if test="${!empty requestScope.statusMessage}">
			<div class="niftyError">
		</c:if>
			<b class="rtopNiftyError">
				<b class="r1"></b>
				<b class="r2"></b>
				<b class="r3"></b>
				<b class="r4"></b>
			</b>
			<c:if test="${empty requestScope.statusMessage}">
				&nbsp;
			</c:if>
			<c:if test="${!empty requestScope.statusMessage}">
				&nbsp;<span class='${requestScope.statusClass} unread'>${requestScope.statusMessage}</span>&nbsp;
			</c:if>
			<b class="rbottomNiftyError">
				<b class="r4"></b>
				<b class="r3"></b>
				<b class="r2"></b>
				<b class="r1"></b>
			</b>
		</div>
		</td></tr></table>
	</td>
</tr>
