<%@ tag body-content="empty" %>

<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<zm:getMailbox var="mailbox"/>

<app:overviewFolder folder="${mailbox.inbox}" key="i" label="inbox"/>
<app:overviewFolder folder="${mailbox.sent}" key="s" label="sent"/>
<app:overviewFolder folder="${mailbox.drafts}" key="d" label="drafts"/>
<app:overviewFolder folder="${mailbox.spam}" key="u" label="junk"/>
<app:overviewFolder folder="${mailbox.trash}" key="t" label="trash"/>

<p/>

<zm:forEachFolder var="folder">
	<c:if test="${!folder.isSystemFolder and (folder.isNullView or folder.isMessageView or folder.isConversationView)}">
		<c:if test="${!folder.isSearchFolder}">
			<app:overviewFolder folder="${folder}"/>
		</c:if>
		<c:if test="${folder.isSearchFolder and folder.depth gt 0}">
			<app:overviewSearchFolder folder="${folder}"/>
		</c:if>
	</c:if>
</zm:forEachFolder>
