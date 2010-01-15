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
<%@ tag body-content="empty" dynamic-attributes="dynattrs" %>
<%@ attribute name="searchResult" rtexprvalue="true" required="true" type="com.zimbra.cs.taglib.bean.ZSearchResultBean"%>
<%@ attribute name="max" rtexprvalue="true" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>

<c:set var="first" value="${searchResult.size eq 0 ? 0 : searchResult.offset+1}"/>
<c:set var="last" value="${searchResult.offset+searchResult.size}"/>    
<b>${first}<c:if test="${first ne last}"> - ${last}</c:if></b>
<c:if test="${!empty max}"> of ${max}</c:if>
<c:if test="${empty max and !searchResult.hasMore}">&nbsp;<fmt:message key="of"/>&nbsp;<b>${last}</b> </c:if>
<c:if test="${!searchResult.hasMore}">&nbsp;</c:if>
