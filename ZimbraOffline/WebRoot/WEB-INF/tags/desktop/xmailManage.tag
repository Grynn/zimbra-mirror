<%@ tag body-content="empty" %>
<%@ attribute name="uri" required="true" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>

<script type="text/javascript">
<!--
function InitScreen() {
    <c:if test="${not bean.pop}">
        Ajax.hide("popSettingsRow");
    </c:if>

    if (!Ajax.isChecked("smtpAuth")) {
        Ajax.hide("smtpAuthSettingsRow");
    }
}

function OnCancel() {
    window.location = '/zimbra/desktop/console.jsp';
}

function OnSubmit() {
    xmailManage.submit();
}

function OnReset() {
    if (confirm('All downloaded data will be deleted.  Data on the server will be downloaded again. OK to proceed?')) {
        hidden_form.verb.value = "rst";
        hidden_form.submit();
    }
}

function OnDelete() {
    if (confirm('Service "' + service + '" information and downloaded data will be deleted.  Data on the server will not be affected. OK to proceed?')) {
        hidden_form.verb.value = "del";
        hidden_form.submit();
    }
}
//-->
</script>


<div id="newService" class="ZWizardPage">

<div class="ZWizardPageTitle">
    <div id='settings_hint' class='ZFloatInHead'></div>
    <span id='pageTitle'>Change POP/IMAP Account Setup</span>
</div>

<form name="hidden_form" action="${uri}" method="POST">
    <input type="hidden" name="verb">
    <input type="hidden" name="accountId" value="${bean.accountId}">
    <input type="hidden" name="dataSourceName" value="${bean.dataSourceName}">
</form>

<c:choose>
    <c:when test="${not empty bean.error}" >
        <p class='ZOfflineError'>${bean.error}</p>
    </c:when>
    <c:when test="${not bean.allValid}" >
        <p class='ZOfflineError'>* Please correct missing/invalid input</p>
    </c:when>
    <c:otherwise>
        <p id='instructions'>* Required field<br>What do you want to change?</p>
    </c:otherwise>
</c:choose>

<form name="xmailManage" action="${uri}" method="POST">

    <input type="hidden" name="verb" value="mod">
    <input type="hidden" name="accountId" value="${bean.accountId}">
    
    <input type="hidden" name="protocol" value="${bean.pop ? 'pop' : 'imap'}">
    <input type="hidden" name="dataSourceName" value="${bean.dataSourceName}">
    <input type="hidden" name="username" value="${bean.username}">
    <input type="hidden" name="email" value="${bean.email}">

    <table class="ZWizardForm" style='width:90%'>
        <tr id='accountTypeRow'>
            <td class="ZFieldLabel">*Account Type:</td>
            <td><input style='width:200px' class="ZField" type="text" id="protocol" value="${bean.pop ? 'POP3' : 'IMAP4'}" disabled></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">*Description:</td>
            <td><input style='width:200px' class="ZField" type="text" id="dataSourceName" value="${bean.dataSourceName}" disabled></td>
        </tr>
        <tr id='usernameRow'>
            <td class="ZFieldLabel">*User Name:</td>
            <td><input style='width:200px' class="ZField" type="text" id="username" value="${bean.username}" disabled></td>
        </tr>
        <tr id='passwordRow'>
            <td class="${zdf:isValid(bean, 'password') ? 'ZFieldLabel' : 'ZFieldError'}">*Password:</td>
            <td><input style='width:100px' class="ZField" type="password" id="password" name="password" value="${bean.password}"
                    onkeypress='Ajax.syncIdsOnTimer(this, "smtpPassword")'>
            </td>
        </tr>
        
        <tr><td class="ZFieldLabel">Your full name:</td>
            <td><input style='width:200px' class="ZField" type="text" id="fromDisplay" name="fromDisplay" value="${bean.fromDisplay}"></td>
        </tr>
        <tr id='emailRow'>
            <td class="ZFieldLabel">*Email address:</td>
            <td><input style='width:200px' class="ZField" type="text" id="email" name="email" value="${bean.email}" disabled>
                <span id='email_hint' class='ZHint'>
            </td>
        </tr>
        
        <tr id='receivingMailRow'><td colspan=2><div class='ZOfflineHeader'>Receiving Mail</div></td></tr>
        
        <tr id='mailServerRow'>
            <td class="${zdf:isValid(bean, 'host') ? 'ZFieldLabel' : 'ZFieldError'}">*Incoming Mail Server:</td>
            <td>
                <table cellspacing=0 cellpadding=0>
                    <tr>
                        <td><input style='width:200px' class="ZField" type="text" id="host" name="host" value="${bean.host}">
                        </td>
                        <td>&nbsp;&nbsp;&nbsp;</td>
                        <td class="${zdf:isValid(bean, 'port') ? 'ZFieldLabel' : 'ZFieldError'}">*Port:</td>
                        <td width=100%><input style='width:50px' class="ZField" type="text" id="port" name="port" value="${bean.port}">
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr id='mailSecureRow'>
            <td class='ZCheckboxCell'><input type="checkbox" id="ssl" name="ssl" ${bean.ssl ? 'checked' : ''}></td>
            <td class="ZCheckboxLabel">Use an encrypted connection (SSL) when accessing this server</td>
        </tr>

        <tr id='sendingMailRow'><td colspan=2><div class='ZOfflineHeader'>Sending Mail</div></td></tr>
        
        <tr id='smtpServerRow'>
            <td class="${zdf:isValid(bean, 'smtpHost') ? 'ZFieldLabel' : 'ZFieldError'}">*Outgoing (SMTP) Mail Server:</td>
            <td>
                <table cellspacing=0 cellpadding=0>
                    <tr>
                        <td><input style='width:200px' class="ZField" type="text" id=smtpHost name="smtpHost" value="${bean.smtpHost}">
                        </td>
                        <td>&nbsp;&nbsp;&nbsp;</td>
                        <td class="${zdf:isValid(bean, 'smtpPort') ? 'ZFieldLabel' : 'ZFieldError'}">*Port:</td>
                        <td width=100%><input style='width:50px' class="ZField" type="text" id="smtpPort" name="smtpPort" value="${bean.smtpPort}">
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr id='smtpSecureRow'>
            <td class='ZCheckboxCell'><input type="checkbox" id="smtpSsl" name="smtpSsl" ${bean.smtpSsl ? 'checked' : ''}></td>
            <td class="ZCheckboxLabel">Use an encrypted connection (SSL) when accessing this server</td>
        </tr>
        <tr id='smtpAuthRow'>
            <td class='ZCheckboxCell'><input type="checkbox" id="smtpAuth" name="smtpAuth" ${bean.smtpAuth ? 'checked' : ''}
                                        onclick='Ajax.toggle("smtpAuthSettingsRow", this.checked)'
                                    ></td>
            <td class="ZCheckboxLabel">Username and password required for sending mail</td>
        </tr>
        <tr id='smtpAuthSettingsRow'>
            <td></td>
            <td>
                <table>
                    <tr>
                        <td class="${zdf:isValid(bean, 'smtpUsername') ? 'ZFieldLabel' : 'ZFieldError'}">*User Name:</td>
                        <td><input style='width:200px' class="ZField" type="text" id="smtpUsername" name="smtpUsername" value="${bean.smtpUsername}"></td>
                    </tr>
                    <tr>
                        <td class="${zdf:isValid(bean, 'smtpPassword') ? 'ZFieldLabel' : 'ZFieldError'}">*Password:</td>
                        <td><input style='width:100px' class="ZField" type="password" id="smtpPassword" name="smtpPassword" value="${bean.smtpPassword}"></td>
                    </tr>
                </table>
            </td>
        </tr>

        <tr id='replyToRow'>
            <td class="ZFieldLabel">Reply-to:</td>
            <td>
                <table>
                    <tr>
                        <td>Name:</td>
                        <td>Email Address:</td>
                    </tr>
                    <tr>
                        <td><input style='width:200px' class="ZField" type="text" id="replyToDisplay" name="replyToDisplay" value="${bean.replyToDisplay}"
                                onkeypress='Ajax.markElementAsManuallyChanged(this)'
                        ></td>
                        <td><input style='width:200px' class="ZField" type="text" id="replyTo" name="replyTo" value="${bean.replyTo}"
                                onkeypress='Ajax.markElementAsManuallyChanged(this)'
                        ></td>
                    </tr>
                </table>
            </td>
        </tr>       


        <tr><td colspan=2><div class='ZOfflineHeader'>Downloading Mail</div></td></tr>

        <tr>
            <td class="ZFieldLabel">*Get new mail:</td>
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
        
        <tr id='popSettingsRow'>
            <td style='text-align:right'><input type="checkbox" id="leave_on_server" name="leave_on_server" disabled></td>
            <td class="ZCheckboxLabel ZHint">Delete messages on the server after downloading them</td>
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
