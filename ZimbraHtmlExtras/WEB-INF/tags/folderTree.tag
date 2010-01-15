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

<zm:getMailbox var="mailbox"/>

<app:overviewFolder folder="${mailbox.inbox}" key="i" label="inbox"/>
<app:overviewFolder folder="${mailbox.sent}" key="s" label="sent"/>
<app:overviewFolder folder="${mailbox.drafts}" key="d" label="drafts"/>
<app:overviewFolder folder="${mailbox.spam}" key="u" label="spam"/>
<app:overviewFolder folder="${mailbox.trash}" key="t" label="trash"/>

<p/>

<zm:forEachFolder var="folder">
	<c:if test="${!folder.isSystemFolder and (folder.isNullView or folder.isMessageView or folder.isConversationView)}">
		<c:if test="${!folder.isSearchFolder}">
			<app:overviewFolder folder="${folder}"/>
		</c:if>
		<c:if test="${folder.isSearchFolder and folder.depth gt 0}">
			<app:overviewSearchFolder folder="${folder}"/>
		</c:if>
	</c:if>
</zm:forEachFolder>
