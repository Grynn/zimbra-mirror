<%@ tag body-content="empty" %>
<%@ attribute name="uri" required="true" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>

<script type="text/javascript">
<!--
function InitScreen() {
    SetPort();
}

function SetPort() {
    if (zd.isDisabled("port")) {
        if (zd.isChecked("ssl")) {
            zd.set("port", "443");
        } else {
            zd.set("port", "80");
        }
    }
}

function OnCancel() {
    window.location = '/zimbra/desktop/console.jsp';
}

function OnSubmit() {
    zd.enable("port");
    zmailManage.submit();
}

function OnReset() {
    if (confirm('All downloaded data will be deleted.  Data on the server will be downloaded again. OK to proceed?')) {
        hidden_form.verb.value = "rst";
        hidden_form.submit();
    }
}

function OnDelete() {
    if (confirm('Service settings and downloaded data will be deleted.  Data on the server will not be affected. OK to proceed?')) {
        hidden_form.verb.value = "del";
        hidden_form.submit();
    }
}
//-->
</script>


<div id="newService" class="ZWizardPage">

<div class="ZWizardPageTitle">
    <div id='settings_hint' class='ZFloatInHead'></div>
    <span id='pageTitle'>Change Zimbra Account Setup</span>
</div>

<form name="hidden_form" action="${uri}" method="POST">
    <input type="hidden" name="verb">
    <input type="hidden" name="accountId" value="${bean.accountId}">
</form>

<c:choose>
    <c:when test="${not empty bean.error}" >
        <p class='ZOfflineError'>${bean.error}</p>
    </c:when>
    <c:when test="${not bean.allValid}" >
        <p class='ZOfflineError'>Please correct missing/invalid input</p>
    </c:when>
    <c:otherwise>
        <p id='instructions'>* Required field<br>
        What do you want to change?</p>
    </c:otherwise>
</c:choose>

<form name="zmailManage" action="${uri}" method="POST">

    <input type="hidden" name="verb" value="mod">
    <input type="hidden" name="accountId" value="${bean.accountId}">
    
    <input type="hidden" name="accountName" value="${bean.accountName}">
    <input type="hidden" name="email" value="${bean.email}">

    <table class="ZWizardForm" style='width:90%'>
        <tr>
            <td class="ZFieldLabel">*Description:</td>
            <td><input style='width:200px' class="ZField" type="text" id="accountName" value="${bean.accountName}" disabled></td>
        </tr>
        <tr id='emailRow'>
            <td class="ZFieldLabel">*Email address:</td>
            <td><input style='width:200px' class="ZField" type="text" id="email" name="email" value="${bean.email}" disabled>
                <span id='email_hint' class='ZHint'>
            </td>
        </tr>
        <tr id='passwordRow'>
            <td class="${zdf:isValid(bean, 'password') ? 'ZFieldLabel' : 'ZFieldError'}">*Password:</td>
            <td><input style='width:100px' class="ZField" type="password" id="password" name="password" value="${bean.password}"></td>
        </tr>

        <tr id='mailServerRow'>
            <td class="${zdf:isValid(bean, 'host') ? 'ZFieldLabel' : 'ZFieldError'}">*Zimbra Server:</td>
            <td>
                <table cellspacing=0 cellpadding=0>
                    <tr>
                        <td><input style='width:200px' class="ZField" type="text" id="host" name="host" value="${bean.host}">
                            <br><span class='ZHint'>(e.g. mail.company.com)</span>
                        </td>
                        <td>&nbsp;&nbsp;&nbsp;</td>
                        <td class="${zdf:isValid(bean, 'port') ? 'ZFieldLabel' : 'ZFieldError'}">*Port:</td>
                        <td width=100%><input style='width:50px' class="ZField" type="text" id="port" name="port" value="${bean.port}" ${bean.defaultPort ? 'disabled' : ''}>
                        <c:if test="${bean.defaultPort}">&nbsp;&nbsp;<a href="#" onclick="zd.enable('port');this.style.display='none'">Edit</a></c:if>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr id='mailSecureRow'>
            <td class='ZCheckboxCell'><input type="checkbox" id="ssl" name="ssl" ${bean.ssl ? 'checked' : ''} onclick="SetPort()"></td>
            <td class="ZCheckboxLabel">Use an encrypted connection (SSL) when accessing this server</td>
        </tr>

        <tr><td colspan=2><hr></td></tr>
        
        <tr>
            <td class="ZFieldLabel">Synchronize with server:</td>
            <td>
                <select class="ZSelect" id="syncFreqSecs" name="syncFreqSecs">
                    <option value="-1" ${bean.syncFreqSecs == -1 ? 'selected' : ''}>manually</option>
                    <option value="0" ${bean.syncFreqSecs == 0 ? 'selected' : ''}>as new mail arrives</option>
                    <option value="60" ${bean.syncFreqSecs == 60 ? 'selected' : ''}>every 1 minute</option>
                    <option value="300" ${bean.syncFreqSecs == 300 ? 'selected' : ''}>every 5 minutes</option>
                    <option value="900" ${bean.syncFreqSecs == 900 ? 'selected' : ''}>every 15 minutes</option>
                    <option value="1800" ${bean.syncFreqSecs == 1800 ? 'selected' : ''}>every 30 minutes</option>
                    <option value="3600" ${bean.syncFreqSecs == 3600 ? 'selected' : ''}>every 1 hour</option>
                    <option value="14400" ${bean.syncFreqSecs == 14400 ? 'selected' : ''}>every 4 hour</option>
                    <option value="43200" ${bean.syncFreqSecs == 43200 ? 'selected' : ''}>every 12 hour</option>
                </select>
            </td>
        </tr>

    </table>

</form>

<p>Press <span class="ZWizardButtonRef">Save Settings</span> to verify these settings.</p>

<table class="ZWizardButtonBar">
    <tr>
        <td class="ZWizardButton">
            <button class='DwtButton' onclick="OnReset()">Reset Data...</button>
        </td>
        <td class="ZWizardButton">
            <button class='DwtButton' onclick="OnDelete()">Remove Account...</button>
        </td>
        <td class="ZWizardButtonSpacer">
            <div></div>
        </td>
        <td class="ZWizardButton">
            <button class='DwtButton' onclick="OnCancel()">Cancel</button>
        </td>
        <td class="ZWizardButton">
            <button class='DwtButton-focused' onclick="OnSubmit()">Save Settings</button>
        </td>
</table>
</div>
