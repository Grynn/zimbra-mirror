<%@ tag body-content="empty" %>
<%@ attribute name="folder" rtexprvalue="true" required="true" type="com.zimbra.cs.jsp.bean.ZFolderBean" %>
<%@ attribute name="label" rtexprvalue="true" required="false" %>
<%@ attribute name="base" rtexprvalue="true" required="false" %>
<%@ attribute name="key" rtexprvalue="true" required="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>

<c:if test="${!empty label}"><fmt:message key="${label}" var="label"/></c:if>
<div class="folder<c:if test="${folder.hasUnread}"> unread</c:if><c:if test="${folder.id eq requestScope.context.selectedId}"> folderSelected</c:if>" style='padding-left: ${4+folder.depth*8}px'>
    <a href='clv?sfi=${folder.id}'>
        ${fn:escapeXml(folder.name)}
        <c:if test="${folder.hasUnread}"> (${folder.unreadCount})</c:if>
    </a>
</div>
