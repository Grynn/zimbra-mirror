<%@ tag body-content="empty" %>
<%@ attribute name="selected" rtexprvalue="true" required="false" %>
<%@ attribute name="folders" rtexprvalue="true" required="false" %>
<%@ attribute name="searches" rtexprvalue="true" required="false" %>
<%@ attribute name="contacts" rtexprvalue="true" required="false" %>
<%@ attribute name="tags" rtexprvalue="true" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="folder ${selected eq 'compose' ? 'folderSelected' : 'unread'}">
	<a href="/mail/compose"><fmt:message key="composeMail"/></a>
</div><p/>

<c:if test="${folders}"><app:folderTree/></c:if><p/>
<c:if test="${contacts}"><app:contactFolderTree selected="${selected}"/><p/></c:if>
<c:if test="${searches}"><app:searchFolderTree/><p/></c:if>
<c:if test="${tags}"><app:tagTree/></c:if>
