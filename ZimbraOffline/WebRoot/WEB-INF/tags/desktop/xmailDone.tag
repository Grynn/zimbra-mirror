<%@ tag body-content="empty" %>
<%@ attribute name="uri" required="true" %>
<%@ attribute name="name" required="true" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
		    <div class="ZWizardPageTitle">Service Created</div>
		
		    <p>Your mail service "${name}" has been successfully set up.
		    </p>
		
		    <p>Note that the first synchronization takes a while to run,
		        possibly hours if you have a lot of messages in the account.
		        However, you can start using this account right away.
		    </p>
		
		    <table class="ZWizardButtonBar">
		        <tr>
		            <td class="ZWizardButton">
		                <button class='DwtButton' onclick="OnNew()">Set Up Another Account</button>
		            </td>
		            <td class="ZWizardButton">
		                <button class='DwtButton' onclick="OnLogin()">Go to Zimbra Desktop</button>
		            </td>
		            <td class="ZWizardButtonSpacer">
                        <div></div>
                    </td>
                    <td class="ZWizardButton">
                        <button class='DwtButton' onclick="OnOK()">OK</button>
                    </td>
		    </table>
		</div>
    </c:when>

    <c:when test="${bean.modify}">
        <div id="serviceDeleted" class="ZWizardPage">
            <div class="ZWizardPageTitle">Manage Service</div>
        
            <p>Service "${name}" has been updated.</p>
        
            <table class="ZWizardButtonBar">
                <tr>
                    <td class="ZWizardButtonSpacer">
                        <div></div>
                    </td>
                    <td class="ZWizardButton">
                        <button class='DwtButton' onclick="OnOK()">OK</button>
                    </td>
            </table>
        </div>
    </c:when>

    <c:when test="${bean.reset}">
        <div id="serviceDeleted" class="ZWizardPage">
            <div class="ZWizardPageTitle">Manage Service</div>
        
            <p>Service "${name}" has been reset.</p>
        
            <table class="ZWizardButtonBar">
                <tr>
                    <td class="ZWizardButtonSpacer">
                        <div></div>
                    </td>
                    <td class="ZWizardButton">
                        <button class='DwtButton' onclick="OnOK()">OK</button>
                    </td>
            </table>
        </div>
    </c:when>

    <c:when test="${bean.delete}">
		<div id="serviceDeleted" class="ZWizardPage">
		    <div class="ZWizardPageTitle">Manage Service</div>
		
		    <p>Service "${name}" has been deleted.</p>
		
		    <table class="ZWizardButtonBar">
		        <tr>
		            <td class="ZWizardButtonSpacer">
		                <div></div>
		            </td>
		            <td class="ZWizardButton">
		                <button class='DwtButton' onclick="OnOK()">OK</button>
		            </td>
		    </table>
		</div>
    </c:when>
</c:choose>

