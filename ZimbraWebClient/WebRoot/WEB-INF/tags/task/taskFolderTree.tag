<%--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
--%>
<%@ tag body-content="empty" %>
<%@ attribute name="editmode" rtexprvalue="true" required="false" %>
<%@ attribute name="keys" rtexprvalue="true" required="true" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlclient" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>

<zm:getMailbox var="mailbox"/>

<jsp:useBean id="expanded" scope="session" class="java.util.HashMap" />
<c:set var="expanded" value="${sessionScope.expanded.tasks ne 'collapse'}"/>

<div class="Tree">
    <table width="100%" cellpadding="0" cellspacing="0">
        <tr>
            <c:url var="toggleUrl" value="/h/search">
                <c:param name="st" value="task"/>
                 <c:param name="${expanded ? 'collapse' : 'expand'}" value="tasks"/>
             </c:url>
             <th style='width:20px'><a href="${fn:escapeXml(toggleUrl)}"><app:img altkey="${ expanded ? 'ALT_TREE_EXPANDED' : 'ALT_TREE_COLLAPSED'}" src="${ expanded ? 'startup/ImgNodeExpanded.png' : 'startup/ImgNodeCollapsed.png'}"/></a></th>
            <th class='Header'><fmt:message key="tasks"/></th>
            <th nowrap="nowrap" align='right' class='ZhTreeEdit'>
                <c:url value="/h/mtasks" var="mabUrl">
                    <c:if test="${not empty param.sfi}">
                        <c:param name="sfi" value="${param.sfi}"/>
                    </c:if>
                </c:url>
                <a id="MTASKS" href="${mabUrl}" ><fmt:message key="TREE_EDIT"/></a>
            </th>
        </tr>
        <c:if test="${expanded}">
            <app:taskFolder folder="${mailbox.tasks}"/>

            <%--
                Display the children of Tasks folder, if any. Folders with unknown view also get listed. 
            --%>
            <zm:forEachFolder var="folder" skiproot="${true}" skipsystem="${false}" expanded="${sessionScope.expanded}" skiptrash="${true}" parentid="${mailbox.tasks.id}">
                <c:if test="${!folder.isSearchFolder and !folder.isSystemFolder and (folder.isNullView or folder.isUnknownView or folder.isTaskView)}">
                    <app:taskFolder folder="${folder}"/>
                </c:if>
            </zm:forEachFolder>

            <%--
                Rest of the task lists, do not display folders with unknown view here.
            --%>
            <zm:forEachFolder var="folder" skiproot="${true}" skipsystem="${true}" expanded="${sessionScope.expanded}" skiptrash="${true}">
                <c:if test="${!folder.isSearchFolder and folder.isTaskView}">
                        <app:taskFolder folder="${folder}"/>
                </c:if>
            </zm:forEachFolder>

        </c:if>
    </table>
</div>
