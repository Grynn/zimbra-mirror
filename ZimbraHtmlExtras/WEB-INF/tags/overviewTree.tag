<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>


<b class='folder'><a href="javascript:;">Compose Mail</a></b>

<p/>

<app:folderTree/>

<p/>

<b class='folder'><a href="<c:url value="/mail/contacts"/>">Contacts</a></b>

<p/>

<app:searchFolderTree/>

<p/>

<app:tagTree/>

