<%@ tag body-content="empty" %>
<%@ attribute name="uri" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>

<fmt:setBundle basename="/desktop/ZdMsg" scope="request"/>

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
    zd.set("whattodo", "<span class='ZOfflineNotice'><fmt:message key='Processing'/></span>");
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
    <span id='pageTitle'><fmt:message key='YMPSetupTitle'/></span>
    
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
        <fmt:message key='YMPSettings'/></p>
    </c:otherwise>
</c:choose>

<form name="ymailNew" action="${uri}" method="POST">

    <input type="hidden" name="verb" value="add">

    <table class="ZWizardForm" style='width:90%'>
        <tr>
            <td class="${zdf:isValid(bean, 'accountName') ? 'ZFieldLabel' : 'ZFieldError'}">*<fmt:message key='Description'/>:</td>
            <td><input style='width:200px' class="ZField" type="text" id="accountName" name="accountName" value="${bean.accountName}">
                        <span id='service_hint' class='ZHint'><fmt:message key='DescHint'/></span></td>
        </tr>
        <tr><td class="ZFieldLabel"><fmt:message key='FullName'/>:</td>
            <td><input style='width:200px' class="ZField" type="text" id="fromDisplay" name="fromDisplay" value="${bean.fromDisplay}"></td>
        </tr>
        <tr id='emailRow'>
           <td class="${zdf:isValid(bean, 'email') ? 'ZFieldLabel' : 'ZFieldError'}">*<fmt:message key='EmailAddr'/>:</td>
            <td><input style='width:200px' class="ZField" type="text" id="email" name="email" value="${bean.email}">
            </td>
        </tr>
        <tr id='passwordRow'>
            <td class="${zdf:isValid(bean, 'password') ? 'ZFieldLabel' : 'ZFieldError'}">*<fmt:message key='Password'/>:</td>
            <td><input style='width:100px' class="ZField" type="password" id="password" name="password" value="${bean.password}"></td>
        </tr>       

        <tr><td colspan=2><div class='ZOfflineHeader'><fmt:message key='DownloadingMail'/></div></td></tr>
        
        <tr>
            <td class="ZFieldLabel"><fmt:message key='SyncFrequency'/>:</td>
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
        
        <tr>
            <td style='text-align:right'><input type="checkbox" id="syncAllServerFolders" name="syncAllServerFolders" ${bean.syncAllServerFolders ? 'checked' : ''}></td>
            <td class="ZCheckboxLabel"><fmt:message key='SyncAllFolders'/></td>
        </tr>

        <tr>
             <td style='text-align:right'><input type="checkbox" id="contactSyncEnabled" name="contactSyncEnabled" checked></td>
             <td class="ZCheckboxLabel"><fmt:message key='ContactSyncEnabled'/></td>
         </tr>

        <tr>
            <td style='text-align:right'><input type="checkbox" id="syncCalendar" name="syncCalendar" checked></td>
            <td class="ZCheckboxLabel"><fmt:message key='YMPSyncCal'/></td>
        </tr>

    </table>

</form>

<p><span id="whattodo"><fmt:message key='PressToVerify'><fmt:param><span class="ZWizardButtonRef"><fmt:message key='SaveSettings'/></span></fmt:param></fmt:message></span></p>
</span>
<table class="ZWizardButtonBar" width="100%">
    <tr>
        <td class="ZWizardButton">
            <button id="typeButton" class='DwtButton' onclick="OnPickType()"><fmt:message key='UseDiffType'/></button>
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
