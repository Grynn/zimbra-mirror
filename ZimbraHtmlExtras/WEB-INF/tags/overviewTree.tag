<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<b class='folder'><a href="javascript:;"><fmt:message key="composeMail"/></a></b><p/>

<app:folderTree/><p/>

<b class='folder'><a href="<c:url value="/mail/contacts"/>"><fmt:message key="contacts"/></a></b><p/>

<app:searchFolderTree/><p/>

<app:tagTree/>
