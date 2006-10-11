<%@ tag body-content="empty" %>

<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<zm:getMailbox var="mailbox"/>

<div class=Tree>
    <table width=100% cellpadding=0 cellspacing=0>
        <tr><th><fmt:message key="folders"/></th></tr>

        <app:overviewFolder folder="${mailbox.inbox}" key="i" label="inbox" icon="Inbox.gif"/>
        <app:overviewFolder folder="${mailbox.sent}" key="s" label="sent" icon="SentFolder.gif"/>
        <app:overviewFolder folder="${mailbox.drafts}" key="d" label="drafts" icon="DraftFolder.gif"/>
        <app:overviewFolder folder="${mailbox.spam}" key="u" label="junk" icon="SpamFolder.gif"/>
        <app:overviewFolder folder="${mailbox.trash}" key="t" label="trash" icon="Trash.gif"/>

        <tr><td>&nbsp;</td></tr>

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
        <tr><td>&nbsp;</td></tr>
    </table>
</div>
