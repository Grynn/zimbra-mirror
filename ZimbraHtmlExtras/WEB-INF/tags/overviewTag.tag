<%@ tag body-content="empty" %>
<%@ attribute name="tag" rtexprvalue="true" required="true" type="com.zimbra.cs.jsp.bean.ZTagBean" %>
<%@ attribute name="label" rtexprvalue="true" required="false" %>
<%@ attribute name="icon" rtexprvalue="true" required="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>

<tr><td class='Folder ${tag.hasUnread ? ' Unread':''}<c:if test="${tag.id eq requestScope.context.selectedId}"> Selected</c:if>'>
    <a href='clv?sti=${tag.id}'>
        <app:img src="${tag.image}"/>
 <span>
 <c:out value="${tag.name}"/>
 <c:if test="${tag.hasUnread}"> (${tag.unreadCount}) </c:if>
 </span>
    </a>
</td></tr>
 