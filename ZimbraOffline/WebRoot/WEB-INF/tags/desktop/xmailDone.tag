<%@ tag body-content="empty" %>
<%@ attribute name="uri" required="true" %>
<%@ attribute name="name" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>

<fmt:setBundle basename="/desktop/ZdMsg" scope="request"/>

<script type="text/javascript">
<!--
function InitScreen() {
}

function OnOK() {
    window.location = "/zimbra/desktop/console.jsp";
}

function OnNew() {
    window.location = "/zimbra/desktop/new.jsp";
}

function OnLogin() {
    window.location = "/zimbra/desktop/login.jsp";
}
//-->
</script>

<c:choose>
    <c:when test="${bean.add}">
		<div id="serviceCreated" class="ZWizardPage ZWizardPageBig">
		    <div class="ZWizardPageTitle"><fmt:message key='ServiceCreated'/></div>
		<span class="padding">
		    <p><fmt:message key='ServiceAdded'><fmt:param>${name}</fmt:param></fmt:message>
		    </p>
		
		    <p><fmt:message key='ServiceAddedNote'/>
		    </p>
</span>		
		    <table class="ZWizardButtonBar" width="100%">
		        <tr>
		            <td class="ZWizardButton">
		                <button class='DwtButton' onclick="OnNew()"><fmt:message key='SetupAnotherAcct'/></button>
		            </td>
		            <td class="ZWizardButton" width="1%">
		                <button class='DwtButton' onclick="OnLogin()"><fmt:message key='GotoDesktop'/></button>
		            </td>
		            <td class="ZWizardButtonSpacer">
                        <div></div>
                    </td>
                    <td class="ZWizardButton" width="1%">
                        <button class='DwtButton' onclick="OnOK()"><fmt:message key='OK'/></button>
                    </td>
		    </table>
		</div>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${bean.delete}">
                <c:set var="key" value="ServiceDeleted"/>
            </c:when>
            <c:when test="${bean.export}">
                <c:set var="key" value="ServiceExported"/>
            </c:when>
            <c:when test="${bean.import}">
                <c:set var="key" value="ServiceImported"/>
            </c:when>
            <c:when test="${bean.modify}">
                <c:set var="key" value="ServiceUpdated"/>
            </c:when>
            <c:when test="${bean.reset}">
                <c:set var="key" value="ServiceReset"/>
            </c:when>
        </c:choose>

        <div id="serviceDeleted" class="ZWizardPage">
            <div class="ZWizardPageTitle"><fmt:message key='ManageService'/></div>
      		<span class="padding">
            <p><fmt:message key="${key}"><fmt:param>${name}</fmt:param></fmt:message></p>
			</span>        
            <table class="ZWizardButtonBar" width="100%">
                <tr>
                    <td class="ZWizardButtonSpacer">
                        <div></div>
                    </td>
                    <td class="ZWizardButton" width="1%">
                        <button class='DwtButton' onclick="OnOK()"><fmt:message key='OK'/></button>
                    </td>
            </table>
        </div>
    </c:otherwise>
</c:choose>

