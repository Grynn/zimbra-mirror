<%--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2008, 2009, 2010 Zimbra, Inc.
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
<%@ attribute name="var" rtexprvalue="false" required="true" type="java.lang.String" %>
<%@ attribute name="value" rtexprvalue="true" required="true" type="java.lang.String" %>
<%@ attribute name="index" rtexprvalue="true" required="false" %>
<%@ attribute name="context" rtexprvalue="true" required="true" type="com.zimbra.cs.taglib.tag.SearchContext" %>
<%@ attribute name="usecache" rtexprvalue="true" required="false"  %>
<%@ attribute name="refresh" rtexprvalue="true" required="false"  %>
<%@ variable name-from-attribute="var" alias='urlVar' scope="AT_BEGIN" variable-class="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>

<c:choose>
    <c:when test="${empty context}">
        <c:url value="${value}" var="urlVar">
            <c:if test="${not refresh}">
                <c:if test="${usecache && (empty dynattrs.su && dynattrs.su!='')}"><c:param name='su' value='1'/></c:if>
                <c:if test="${empty dynattrs.si && dynattrs.si!=''}"><c:param name='si' value='${empty index ? param.si : index}'/></c:if>
                <c:if test="${!empty param.so && (empty dynattrs.so && dynattrs.so!='')}"><c:param name='so' value='${param.so}'/></c:if>
                <c:if test="${!empty param.sc && (empty dynattrs.sc && dynattrs.sc!='')}"><c:param name='sc' value='${param.sc}'/></c:if>
            </c:if>
            <c:if test="${!empty param.sq && (empty dynattrs.sq && dynattrs.sq!='')}"><c:param name='sq' value='${param.sq}'/></c:if>
            <c:if test="${!empty param.sfi && (empty dynattrs.sfi && dynattrs.sfi!='')}"><c:param name='sfi' value='${param.sfi}'/></c:if>
            <c:if test="${!empty param.sti && (empty dynattrs.sti && dynattrs.sti!='')}"><c:param name='sti' value='${param.sti}'/></c:if>
            <c:if test="${!empty param.st && (empty dynattrs.st && dynattrs.st!='')}"><c:param name='st' value='${param.st}'/></c:if>
            <c:if test="${!empty param.ss && (empty dynattrs.ss && dynattrs.ss!='')}"><c:param name='ss' value='${param.ss}'/></c:if>
            <c:forEach items="${dynattrs}" var="a">
                <c:if test="${!empty a.key && !empty a.value}">
                    <c:param name='${a.key}' value='${a.value}'/>
                </c:if>
            </c:forEach>
        </c:url>
    </c:when>
    <c:otherwise>
        <c:url value="${value}" var="urlVar">
            <c:if test="${not refresh}">
                <c:if test="${usecache && (empty dynattrs.su && dynattrs.su!='')}"><c:param name='su' value='1'/></c:if>
                <c:if test="${empty dynattrs.si && dynattrs.si!=''}"><c:param name='si' value='${empty index ? context.currentItemIndex : index}'/></c:if>
                <c:if test="${empty dynattrs.so && dynattrs.so!=''}"><c:param name='so' value='${context.searchResult.offset}'/></c:if>
                <c:if test="${!empty context && (empty dynattrs.sc && dynattrs.sc!='')}"><c:param name='sc' value='${context.id}'/></c:if>
            </c:if>
            <c:if test="${!empty context.sq && (empty dynattrs.sq && dynattrs.sq!='')}"><c:param name='sq' value='${context.sq}'/></c:if>
            <c:if test="${!empty context.sfi && (empty dynattrs.sfi && dynattrs.sfi!='')}"><c:param name='sfi' value='${context.sfi}'/></c:if>
            <c:if test="${!empty context.sti && (empty dynattrs.sti && dynattrs.sti!='')}"><c:param name='sti' value='${context.sti}'/></c:if>
            <c:if test="${!empty context.st && (empty dynattrs.st && dynattrs.st!='')}"><c:param name='st' value='${context.st}'/></c:if>
            <c:if test="${!empty context.ss && (empty dynattrs.ss && dynattrs.ss!='')}"><c:param name='ss' value='${context.ss}'/></c:if>
            <c:forEach items="${dynattrs}" var="a">
                <c:if test="${!empty a.key && !empty a.value}">
                    <c:param name='${a.key}' value='${a.value}'/>
                </c:if>
            </c:forEach>
        </c:url>
    </c:otherwise>
</c:choose>

