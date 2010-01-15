<%--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
--%>
<%@ tag body-content="empty" %>
<%@ attribute name="folder" rtexprvalue="true" required="true" type="com.zimbra.cs.taglib.bean.ZFolderBean" %>
<%@ attribute name="label" rtexprvalue="true" required="false" %>
<%@ attribute name="base" rtexprvalue="true" required="false" %>
<%@ attribute name="key" rtexprvalue="true" required="false" %>
<%@ attribute name="alwaysBold" rtexprvalue="true" required="false" %>
<%@ attribute name="isShared" rtexprvalue="true" required="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>

<c:if test="${!empty label}"><fmt:message key="${label}" var="label"/></c:if>
<div class="folder<c:if test="${folder.hasUnread or alwaysBold}"> unread</c:if><c:if test="${folder.id eq requestScope.context.selectedId}"> folderSelected</c:if><c:if test="${isShared}"> sharedFolder</c:if>" style='padding-left: ${4+folder.depth*8}px'>
	<a href='${empty base ? "clv" : base}?sfi=${folder.id}'>
		${fn:escapeXml(empty label ? folder.name : label)}
		<c:if test="${folder.hasUnread}"> (${folder.unreadCount})</c:if>
	</a>
</div>
