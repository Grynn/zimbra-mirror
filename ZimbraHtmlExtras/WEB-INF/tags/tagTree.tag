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
<%@ tag body-content="empty" %>

<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div style='width:140px' class="niftyLabel">
	<b class="rtopNiftyLabel">
		<b class="r1"></b>
		<b class="r2"></b>
		<b class="r3"></b>
		<b class="r4"></b>
	</b>
	<span style='cursor:pointer;'>
		&nbsp;<app:img src="opentriangle.gif" width='11' height='11'/> <fmt:message key="labels"/><br>
		<table border=0 cellpadding=3 cellspacing=0 width=100%><tr><td>
			<table border=0 cellpadding=2 cellspacing=2 width=100% bgcolor="#FFFFFF"><tr><td>
				<zm:forEachTag var="tag">
					<%-- <c:if test="${tag.id eq requestScope.context.selectedId}"> Selected</c:if>'> --%>
					<div class='labelContent ${tag.hasUnread ? ' unread' : ''}'>
						<a href='clv?sti=${tag.id}'>
							<c:out value="${tag.name}"/>
							<c:if test="${tag.hasUnread}"> (${tag.unreadCount})</c:if>
						</a>
					</div>
				</zm:forEachTag>
			</td></tr></table>
			<div class="labelContent labelEdit"><a href="javascript:;"><fmt:message key="editLabels"/></a></div>
		</td></tr></table>
	</span>
	<b class="rbottomNiftyLabel">
		<b class="r4"></b>
		<b class="r3"></b>
		<b class="r2"></b>
		<b class="r1"></b>
	</b>
</div>