<%@ tag body-content="empty" %>
<%@ attribute name="folder" rtexprvalue="true" required="true" type="com.zimbra.cs.jsp.bean.ZFolderBean" %>
<%@ attribute name="label" rtexprvalue="true" required="false" %>
<%@ attribute name="icon" rtexprvalue="true" required="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>

<c:if test="${!empty label}"><fmt:message key="${label}" var="label"/></c:if>
<tr><td class='Folder<c:if test="${folder.id eq requestScope.context.selectedId}"> Selected</c:if>'
        style='padding-left: ${10+folder.depth*8}px'>
    <a href='clv?sfi=${folder.id}'>
        <app:img src="${empty icon ? 'SearchFolder.gif' : icon}"/>
        <span>${fn:escapeXml(empty label ? folder.name : label)}</span>
    </a>
</td></tr>
