<%@ tag body-content="empty" %>
<%@ attribute name="selected" rtexprvalue="true" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<table width=100% cellpadding=0 cellspacing=0 class=Tabs>
    <tr>
        <td class='Tab ${selected=='mail' ? 'TabSelected' :'TabNormal'}'>
            <a href="<c:url value="/mail/clv"/>" accesskey="m">
                <app:img src="MailApp.gif"/>
                <span><fmt:message key="mail"/></span>
            </a>
        </td>
        <td class='TabSpacer'>
            <td class='Tab ${selected=='contacts' ? 'TabSelected' :'TabNormal'}'>
                <a href="<c:url value="/mail/contacts"/>" accesskey="c"><app:img src="contacts/ContactsFolder.gif"/><span><fmt:message
                        key="contacts"/></span></a>
            </td>
        <td class='TabSpacer'>
        <%--
        <td class='Tab ${selected=='calendar' ? ' TabSelected' :' TabNormal'}'>
            <app:img src="CalendarApp.gif"/>
            <span><fmt:message key="calendar"/></span>
        </td>
        --%>
        <td class='TabFiller'>
            &nbsp;
        </td>
        <td align=right>
            <zm:getMailbox var="mailbox"/>
            <c:set var="max" value="${mailbox.attrs.zimbraMailQuota[0]}"/>
            <fmt:message key="quotaUsage">
                <fmt:param value="${zm:displaySize(mailbox.size)}"/>
                <fmt:param value="${max==0 ? zm:m(pageContext, 'unlimited') : zm:displaySize(max)}"/>
            </fmt:message>
        </td>
    </tr>
</table>
