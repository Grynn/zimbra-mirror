<%@ tag body-content="empty" %>
<%@ attribute name="folders" rtexprvalue="true" required="false" %>
<%@ attribute name="searches" rtexprvalue="true" required="false" %>
<%@ attribute name="contacts" rtexprvalue="true" required="false" %>
<%@ attribute name="calendars" rtexprvalue="true" required="false" %>
<%@ attribute name="tags" rtexprvalue="true" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>

<zm:getMailbox var="mailbox"/>
<c:if test="${folders}"><app:folderTree/></c:if>
<c:if test="${contacts}"><app:contactFolderTree/></c:if>
<c:if test="${searches}"><app:searchFolderTree/></c:if>
<c:if test="${tags}"><app:tagTree/></c:if>
