<%@ tag body-content="empty" %>
<%@ attribute name="uri" required="true" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>

<script type="text/javascript">
<!--
function InitScreen() {
}

function OnPickType() {
    window.location = '/zimbra/desktop/new.jsp';
}

function OnCancel() {
    window.location = '/zimbra/desktop/console.jsp';
}

function OnSubmit() {
    beforeSubmit();
    ymailNew.submit();
}

function beforeSubmit() {
    disableButtons();
    zd.set("whattodo", "<img src='/zimbra/img/animated/Imgwait_16.gif' align='absmiddle'></img> <span class='ZOfflineNotice'>Processing...</span>");
}

function disableButtons() {
    zd.disable("typeButton");
    zd.disable("cancelButton");
    zd.disable("saveButton");
}
//-->
</script>


<div id="newService" class="ZWizardPage">

<div class="ZWizardPageTitle">
    <div id='settings_hint' class='ZFloatInHead'></div>
    <span id='pageTitle'>Yahoo! Mail Plus Account Setup</span>
    
</div>

<c:choose>
    <c:when test="${not empty bean.error}" >
        <p class='ZOfflineError'>${bean.error}</p>
    </c:when>
    <c:when test="${not bean.allValid}" >
        <p class='ZOfflineError'>Please correct missing/invalid input</p>
    </c:when>
    <c:otherwise>
        <p id='instructions'>* Required field<br>
        Settings to access your Yahoo! Mail Plus mailbox</p>
    </c:otherwise>
</c:choose>

<form name="ymailNew" action="${uri}" method="POST">

    <input type="hidden" name="verb" value="add">

    <table class="ZWizardForm" style='width:90%'>
        <tr>
            <td class="${zdf:isValid(bean, 'dataSourceName') ? 'ZFieldLabel' : 'ZFieldError'}">*Description:</td>
            <td><input style='width:200px' class="ZField" type="text" id="dataSourceName" name="dataSourceName" value="${bean.dataSourceName}">
                        <span id='service_hint' class='ZHint'>(e.g. My Email)</span></td>
        </tr>
        <tr><td class="ZFieldLabel">Your full name:</td>
            <td><input style='width:200px' class="ZField" type="text" id="fromDisplay" name="fromDisplay" value="${bean.fromDisplay}"></td>
        </tr>
        <tr id='emailRow'>
           <td class="${zdf:isValid(bean, 'username') ? 'ZFieldLabel' : 'ZFieldError'}">*Email address:</td>
            <td><input style='width:100px' class="ZField" type="text" id="username" name="username" value="${bean.username}"><span class="ZFieldLabel">@yahoo.com</span></td>
        </tr>
        <tr id='passwordRow'>
            <td class="${zdf:isValid(bean, 'password') ? 'ZFieldLabel' : 'ZFieldError'}">*Password:</td>
            <td><input style='width:100px' class="ZField" type="password" id="password" name="password" value="${bean.password}"></td>
        </tr>       

        <tr><td colspan=2><div class='ZOfflineHeader'>Downloading Mail</div></td></tr>
        
        <tr>
            <td class="ZFieldLabel">Get new mail:</td>
            <td>
                <select class="ZSelect" id="syncFreqSecs" name="syncFreqSecs">
                    <option value="-1" ${bean.syncFreqSecs == -1 ? 'selected' : ''}>manually</option>
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

<p><span id="whattodo">Press <span class="ZWizardButtonRef">Save Settings</span> to verify these settings.</span></p>

<table class="ZWizardButtonBar">
    <tr>
        <td class="ZWizardButton">
            <button id="typeButton" class='DwtButton' onclick="OnPickType()">Use a Different Account Type</button>
        </td>
        <td class="ZWizardButtonSpacer">
            <div></div>
        </td>
        <td class="ZWizardButton">
            <button id="cancelButton" class='DwtButton' onclick="OnCancel()">Cancel</button>
        </td>
        <td class="ZWizardButton">
            <button id="saveButton" class='DwtButton-focused' onclick="OnSubmit()">Save Settings</button>
        </td>
</table>
</div>
