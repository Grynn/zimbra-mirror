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
<%@ attribute name="closeurl" rtexprvalue="true" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>

<c:set var="ids" value="${fn:join(paramValues.id, ',')}"/>
<c:choose>
    <c:when test="${empty ids}">
        <app:status style="Warning"><fmt:message key="actionNoContactSelected"/></app:status>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${!empty param.actionDelete}">
                <zm:getMailbox var="mailbox"/>
                <zm:moveContact  var="result" id="${ids}" folderid="${mailbox.trash.id}"/>
                <app:status>
                    <fmt:message key="actionContactMovedTrash">
                        <fmt:param value="${result.idCount}"/>
                    </fmt:message>
                </app:status>
            </c:when>
            <c:when test="${fn:startsWith(param.actionOp, 't:') or fn:startsWith(param.actionOp, 'u:')}">
                <c:set var="tag" value="${fn:startsWith(param.actionOp, 't')}"/>
                <c:set var="tagid" value="${fn:substring(param.actionOp, 2, -1)}"/>
                <zm:tagContact tagid="${tagid}"var="result" id="${ids}" tag="${tag}"/>
                <app:status>
                    <fmt:message key="${tag ? 'actionContactTag' : 'actionContactUntag'}">
                        <fmt:param value="${result.idCount}"/>
                        <fmt:param value="${zm:getTagName(pageContext, tagid)}"/>
                    </fmt:message>
                </app:status>
            </c:when>
            <c:when test="${fn:startsWith(param.folderId, 'm:')}">
                <c:set var="folderid" value="${fn:substring(param.folderId, 2, -1)}"/>
                <zm:getMailbox var="mailbox"/>
                <zm:moveContact folderid="${folderid}"var="result" id="${ids}"/>
                <app:status>
                    <fmt:message key="actionContactMoved">
                        <fmt:param value="${result.idCount}"/>
                        <fmt:param value="${zm:getFolderName(pageContext, folderid)}"/>
                    </fmt:message>
                </app:status>
            </c:when>
            <c:when test="${!empty param.actionMove}">
                <app:status style="Warning"><fmt:message key="actionNoFolderSelected"/></app:status>
            </c:when>
            <c:otherwise>
                <app:status style="Warning"><fmt:message key="actionNoActionSelected"/></app:status>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
