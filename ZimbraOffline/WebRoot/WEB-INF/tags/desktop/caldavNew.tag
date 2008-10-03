<%@ tag body-content="empty" %>
<%@ attribute name="uri" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>

<fmt:setBundle basename="/desktop/ZdMsg" scope="request"/>

<script type="text/javascript">
<!--
function InitScreen() {
    zd.hide("descriptionRow");
}

function OnCancel() {
    window.location = '/zimbra/desktop/console.jsp';
}

function OnSwitch(app) {
    if (app == 'email') {
        document.hidden_form.action = '/zimbra/desktop/xmail.jsp';
        document.hidden_form.submit();
    }
}

function OnClickToggle(arg) {
    if (arg) {
        zd.disable("email");
    } else {
        zd.enable("email");
    }
}

function OnSubmit() {
    beforeSubmit();
    if (!${bean.loaded}) {
        document.caldavNew.verb.value = "add";
    }
    caldavNew.submit();
}

function beforeSubmit() {
    disableButtons();
    zd.set("whattodo", "<span class='ZOfflineNotice'><fmt:message key='Processing'/></span>");
}

function disableButtons() {
    zd.disable("emailButton");
    zd.disable("cancelButton");
    zd.disable("saveButton");
}
//-->
</script>


<div id="newService" class="ZWizardPage">

<div class="ZWizardPageTitle">
    <div id='settings_hint' class='ZFloatInHead'></div>
    <span id='pageTitle'><fmt:message key='CalDavSetupTitle'/></span>
    
</div>
        <span class="padding">
<c:choose>
    <c:when test="${not empty bean.error}" >
        <p class='ZOfflineError'>${bean.error}</p>
    </c:when>
    <c:when test="${not bean.allValid}" >
        <p class='ZOfflineError'><fmt:message key='PlsCorrectInput'/></p>
    </c:when>
    <c:otherwise>
        <p id='instructions'>* <fmt:message key='RequiredField'/><br>
        <fmt:message key='CalDavSettings'/></p>
    </c:otherwise>
</c:choose>

<form name="hidden_form" action="/zimbra/desktop/xmail.jsp" method="POST">
    <input type="hidden" name="accountId" value="${bean.accountId}">
</form>

<form name="caldavNew" action="${uri}" method="POST">

    <input type="hidden" name="accountId" value="${bean.accountId}">
    <input type="hidden" name="verb" value="mod">

    <table class="ZWizardForm" style='width:90%'>
        <tr id='descriptionRow'>
            <td class="${zdf:isValid(bean, 'name') ? 'ZFieldLabel' : 'ZFieldError'}">*<fmt:message key='Description'/>:</td>
            <td><input style='width:200px' class="ZField" type="text" id="name" name="name" value="${bean.name}">
                        <span id='service_hint' class='ZHint'><fmt:message key='DescHint'/></span></td>
        </tr>
        <tr><td class="ZFieldLabel"><fmt:message key='FullName'/>:</td>
            <td><input style='width:200px' class="ZField" type="text" id="displayName" name="displayName" value="${bean.displayName}"></td>
        </tr>
        <tr id='urlRow'>
            <td class="${zdf:isValid(bean, 'principalUrl') ? 'ZFieldLabel' : 'ZFieldError'}">*<fmt:message key='CalDavUrl'/>:</td>
            <td><input style='width:200px' class="ZField" type="text" id="principalUrl" name="principalUrl" value="${bean.principalUrl}">
            </td>
        </tr>
        <tr id='emailRow'>
            <td class="${zdf:isValid(bean, 'email') ? 'ZFieldLabel' : 'ZFieldError'}">*<fmt:message key='EmailAddr'/>:</td>
            <td><input style='width:200px' class="ZField" type="text" id="email" name="email" value="${bean.email}" ${bean.useLoginFromEmail ? 'disabled' : ''}></td>
        </tr>
        <tr id='passwordRow'>
            <td class="${zdf:isValid(bean, 'password') ? 'ZFieldLabel' : 'ZFieldError'}">*<fmt:message key='Password'/>:</td>
            <td><input style='width:100px' class="ZField" type="password" id="password" name="password" value="${bean.password}" }></td>
        </tr>
        
        <tr id='useLoginFromEmailCheckboxRow'>
            <td class='ZCheckboxCell'><input type="checkbox" id="useLoginFromEmail" name="useLoginFromEmail" ${bean.useLoginFromEmail ? 'checked' : ''} onclick='OnClickToggle(this.checked)'></td>
            <td class="ZCheckboxLabel"><fmt:message key='UseLoginFromEmail'/></td>
        </tr>

        <tr>
            <td class="ZFieldLabel"><fmt:message key='CalDavSyncFrequency'/>:</td>
            <td>
                <select class="ZSelect" id="syncFreqSecs" name="syncFreqSecs">
                    <option value="-1" ${bean.syncFreqSecs == -1 ? 'selected' : ''}><fmt:message key='SyncManually'/></option>
                    <option value="60" ${bean.syncFreqSecs == 60 ? 'selected' : ''}><fmt:message key='SyncEveryMin'/></option>
                    <option value="300" ${bean.syncFreqSecs == 300 ? 'selected' : ''}><fmt:message key='SyncEvery5'/></option>
                    <option value="900" ${bean.syncFreqSecs == 900 ? 'selected' : ''}><fmt:message key='SyncEvery15'/></option>
                    <option value="1800" ${bean.syncFreqSecs == 1800 ? 'selected' : ''}><fmt:message key='SyncEvery30'/></option>
                    <option value="3600" ${bean.syncFreqSecs == 3600 ? 'selected' : ''}><fmt:message key='SyncEvery1Hr'/></option>
                    <option value="14400" ${bean.syncFreqSecs == 14400 ? 'selected' : ''}><fmt:message key='SyncEvery4Hr'/></option>
                    <option value="43200" ${bean.syncFreqSecs == 43200 ? 'selected' : ''}><fmt:message key='SyncEvery12Hr'/></option>
                </select>
            </td>
        </tr>
    </table>

</form>

<p><span id="whattodo"><fmt:message key='PressToVerify'><fmt:param><span class="ZWizardButtonRef"><fmt:message key='SaveSettings'/></span></fmt:param></fmt:message></span></p>
</span>
<table class="ZWizardButtonBar" width="100%">
    <tr>
        <td class="ZWizardButton">
            <button id="emailButton" class='DwtButton' onclick="OnSwitch('email')"><fmt:message key='ViewEmailSettings'/></button>
        </td>
        <td class="ZWizardButtonSpacer">
            <div></div>
        </td>
        <td class="ZWizardButton" width="1%">
            <button id="cancelButton" class='DwtButton' onclick="OnCancel()"><fmt:message key='Cancel'/></button>
        </td>
        <td class="ZWizardButton" width="1%">
            <button id="saveButton" class='DwtButton-focused' onclick="OnSubmit()"><fmt:message key='SaveSettings'/></button>
        </td>
</table>
</div>
