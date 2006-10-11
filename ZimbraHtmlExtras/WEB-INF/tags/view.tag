<%@ tag body-content="scriptless" %>
<%@ attribute name="selected" rtexprvalue="true" required="false" %>
<%@ attribute name="folders" rtexprvalue="true" required="false" %>
<%@ attribute name="searches" rtexprvalue="true" required="false" %>
<%@ attribute name="contacts" rtexprvalue="true" required="false" %>
<%@ attribute name="calendars" rtexprvalue="true" required="false" %>
<%@ attribute name="ads" rtexprvalue="true" required="false" %>
<%@ attribute name="tags" rtexprvalue="true" required="false" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<table width=100% cellpadding="0" cellspacing="0">
    <tr>
        <td valign=middle align=center class='Overview'>
            <a href="http://www.zimbra.com" target=_new><app:img src="AppBanner.png" border="0"
                                                                 alt="ZCS by Zimbra"/></a>
        </td>
        <td valign=top class='TopContent'>
            <jsp:directive.include file="/WEB-INF/fragments/top.jspf"/>
            <app:appTabs selected='${selected}'/>
        </td>
    </tr>
    <tr>
        <td valign=top class='Overview'>
            <app:overviewTree contacts="${contacts}" tags="${tags}" searches="${searches}" folders="${folders}"/>
        </td>
<c:set var="adsOn" value="${(!empty ads) and ((!empty param.ads) or sessionScope.adsOn)}"/>
<c:if test="${adsOn}" >
        <td valign='top'>
            <table width=100% cellpadding="0" cellspacing="0">
                <tr>
</c:if>
                    <td valign='top'>
                        <div class='MainContent'>
                            <jsp:doBody/>
                        </div>
                        <jsp:directive.include file="/WEB-INF/fragments/footer.jspf"/>
                    </td>
<c:if test="${adsOn}" >
                    <td valign='top' style='border-top: 1px solid #98adbe; width: 180px;'>
                       <app:ads content="${ads}"/>
                    </td>
                </tr>
            </table>
        </td>
</c:if>        
    </tr>
</table>
