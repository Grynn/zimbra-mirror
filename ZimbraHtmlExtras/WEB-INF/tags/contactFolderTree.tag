<%@ tag body-content="empty" %>

<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<zm:getMailbox var="mailbox"/>

<div class=Tree>
    <table width=100% cellpadding=0 cellspacing=0>
        <tr><th>Address Books</th></tr>

        <app:overviewFolder base="contacts" folder="${mailbox.contacts}" label="contacts"
                            icon="contacts/ContactsFolder.gif"/>
        <app:overviewFolder base="contacts" folder="${mailbox.autoContacts}" label="emailedContacts"
                            icon="contacts/ContactsFolder.gif"/>

        <zm:forEachFolder var="folder">
            <c:if test="${!folder.isSystemFolder and folder.isContactView and !folder.isSearchFolder}">
                <c:set var="icon"
                       value="${folder.isMountPoint ? 'contacts/SharedContactsFolder.gif' : 'contacts/ContactsFolder.gif'}"/>
                <app:overviewFolder base="contacts" folder="${folder}" icon="${icon}"/>
            </c:if>
        </zm:forEachFolder>
        <tr><td>&nbsp;</td></tr>
    </table>
</div>
