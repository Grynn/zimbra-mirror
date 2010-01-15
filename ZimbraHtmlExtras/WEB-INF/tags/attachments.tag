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
<%@ attribute name="message" rtexprvalue="true" required="true" type="com.zimbra.cs.taglib.bean.ZMessageBean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>

<div class="msgAttachContainer">
<c:forEach var="part" items="${message.attachments}">
	<c:if test="${part.isMssage}">
		<zm:getMessage var="partMessage" id="${message.id}" part="${part.partName}"/>
		<c:set var="body" value="${partMessage.body}"/>
		<c:if test="${body.contentType eq 'text/html'}">
			${body.content}
		</c:if>
		<c:if test="${!(body.contentType eq 'text/html')}">
			${body.textContentAsHtml}
		</c:if>
		<br><br>
		<app:attachments message="${partMessage}"/>
	</c:if>
</c:forEach>

<c:forEach var="part" items="${message.attachments}">
	<c:if test="${!part.isMssage}">
		<c:set var="pname" value="${part.displayName}"/>
		<c:if test="${empty pname}">
			<fmt:message key="unknownContentType" var="pname"><fmt:param value="${part.contentType}"/></fmt:message>
		</c:if>
		<c:set var="url" value="/home/~/?id=${message.id}&part=${part.partName}&auth=co"/>

		<table border=0 cellpadding=1 cellspacing=1>
		<tr>
			<td>
				<c:choose>
					<c:when test="${part.isImage}">
						<img class='msgAttachImage' src="${url}" alt="${part.displayName}"/>
					</c:when>
					<c:otherwise>
						<app:img src="${part.image}" alt="${part.displayName}" title="${part.contentType}"/>
					</c:otherwise>
				</c:choose>
			</td>
			<td width=7></td>
			<td>
				<b>${fn:escapeXml(pname)}</b><br />
				${part.displaySize}
				<a target="_blank" href="${url}&disp=i"><fmt:message key="view"/></a>
				<a href="${url}&disp=a"><fmt:message key="download"/></a>
			</td>
		</tr>
		</table>
	</c:if>
</c:forEach>
</div>
