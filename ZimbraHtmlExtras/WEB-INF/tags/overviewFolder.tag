<%@ tag body-content="empty" %>
<%@ attribute name="folder" rtexprvalue="true" required="true" type="com.zimbra.cs.jsp.bean.ZFolderBean" %>
<%@ attribute name="label" rtexprvalue="true" required="false" %>
<%@ attribute name="icon" rtexprvalue="true" required="false" %>
<%@ attribute name="base" rtexprvalue="true" required="false" %>
<%@ attribute name="key" rtexprvalue="true" required="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>

<c:if test="${!empty label}"><fmt:message key="${label}" var="label"/></c:if>
<tr>
    <td class='Folder<c:if test="${folder.hasUnread}"> Unread</c:if><c:if test="${folder.id eq requestScope.context.selectedId}"> Selected</c:if>'
        style='padding-left: ${10+folder.depth*8}px'>
        <a href='${empty base ? "clv" : base}?sfi=${folder.id}' <c:if test="${!empty key}">accesskey="${key}" </c:if> >
            <app:img src="${empty icon ? 'Folder.gif' : icon}"/>
            <span>${fn:escapeXml(empty label ? folder.name : label)} <c:if test="${folder.hasUnread}">
                (${folder.unreadCount}) </c:if></span>
        </a>

    </td></tr>

