<%@ tag body-content="empty" %>
<%@ attribute name="uri" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>

<fmt:setBundle basename="/desktop/ZdMsg" scope="request"/>

<c:set var='onDeleteWarn'><fmt:message key='OnDeleteWarn'/></c:set>

<script type="text/javascript">
<!--
function InitScreen() {
    zd.hide("syncSettingsRow");
    zd.hide("popSettingsRow");

	<c:choose>
	    <c:when test="${bean.pop}" >
	        zd.show("popSettingsRow");
	    </c:when>
	    <c:when test="${bean.imap || bean.live}">
	        zd.show("syncSettingsRow");
	    </c:when>
	</c:choose>

	<c:if test="${not bean.live && not bean.ymail}">
    if (!zd.isChecked("smtpAuth")) {
        zd.hide("smtpAuthSettingsRow");
    }
	</c:if>
}

function OnCancel() {
    window.location = '/zimbra/desktop/console.jsp';
}

function OnSubmit() {
    beforeSubmit();
    xmailManage.submit();
}

function OnDelete() {
    if (confirm("${onDeleteWarn}")) {
        beforeSubmit();
        document.hidden_form.verb.value = "del";
        document.hidden_form.submit();
    }
}

function OnManage() {
    document.hidden_form.action = '/zimbra/desktop/manageData.jsp';
    document.hidden_form.submit();
}

function OnSwitch(ds) {
    if (ds == 'caldav') {
        document.caldav_form.submit();
    }
}

function beforeSubmit() {
    disableButtons();
    zd.set("whattodo", <fmt:message key='Processing'/>");
    zd.enable("password");
    if (!${bean.live} && !${bean.ymail}) {
        zd.enable("smtpPassword");
    }
}

function disableButtons() {
    zd.disable("cancelButton");
    //zd.disable("deleteButton");
    zd.disable("manageButton");
    zd.disable("saveButton");
}

function passOnEdit(id) {
    zd.enable(id);
    passObj = document.getElementById(id);
    passObj.value='';
    passObj.focus();
}

//-->
</script>


<div id="newService" class="ZWizardPage">

<div class="ZWizardPageTitle">
    <div id='settings_hint' class='ZFloatInHead'></div>
    <span id='pageTitle'>
    <c:choose>
    <c:when test="${bean.live}" >
        <h2 align="center"><fmt:message key='LiveChgSetup'/></h2><hr>
    </c:when>
    <c:when test="${bean.ymail}" >
         <h2 align="center"><fmt:message key='YMPChgSetup'/></h2><hr>
    </c:when>
    <c:otherwise>
        <h2 align="center"><fmt:message key='OtherChgSetup'/></h2><hr>
    </c:otherwise>
    </c:choose>
    </span>
</div>

<form name="caldav_form" action="/zimbra/desktop/caldav.jsp" method="POST">
    <input type="hidden" name="accountId" value="${bean.accountId}">
    <input type="hidden" name="accountName" value="${bean.accountName}">
    <input type="hidden" name="displayName" value="${bean.fromDisplay}">
    <input type="hidden" name="mailUsername" value="${bean.email}">
    <input type="hidden" name="accntType" value="OtherAcct">
</form>

<form name="hidden_form" action="${uri}" method="POST">
    <input type="hidden" name="accountId" value="${bean.accountId}">
    <input type="hidden" name="accountName" value="${bean.accountName}">
    <input type="hidden" name="email" value="${bean.email}">
    <input type="hidden" name="verb">
    <input type="hidden" name="accntType" value="OtherAcct">
</form>

<span class="padding">

<form name="xmailManage" action="${uri}" method="POST">
    <input type="hidden" name="accountId" value="${bean.accountId}">
    <input type="hidden" name="accountName" value="${bean.accountName}">
    <input type="hidden" name="email" value="${bean.email}">
    <input type="hidden" name="verb" value="mod">
    <input type="hidden" name="accntType" value="OtherAcct">

    <c:if test="${not empty bean.domain}">
        <input type="hidden" name="domain" value="${bean.domain}">
    </c:if>
    
    <input type="hidden" name="protocol" value="${bean.pop ? 'pop3' : bean.imap ? 'imap' : 'live'}">
    <input type="hidden" name="username" value="${bean.username}">

    <table cellpadding="10">
        <c:if test="${not bean.live and not bean.ymail}">    
	        <tr id='accountTypeRow'>
	            <td class="ZFieldLabel"><fmt:message key='AccountType'/></td>
	            <td><input class="ZField" type="text" id="protocol" value="${bean.pop ? 'POP3' : 'IMAP4'}" disabled></td>
	        </tr>
	    </c:if>
	    
        <tr>
            <td class="ZFieldLabel"><fmt:message key='Description'/></td>
            <td><input class="ZField" type="text" id="accountName" value="${bean.accountName}" disabled></td>
        </tr>
        <tr id='usernameRow'>
            <td class="ZFieldLabel"><fmt:message key='UserName'/></td>
            <td><input class="ZField" type="text" id="username" value="${bean.username}" disabled></td>
        </tr>
        <tr id='passwordRow'>
            <td class="${zdf:isValid(bean, 'password') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='Password'/></td>
            <td><input  class="ZField" type="password" id="password" name="password" value="${bean.password}"
                    onkeypress='zd.syncIdsOnTimer(this, "smtpPassword")' ${zdf:isValid(bean, 'password') ? 'disabled' : ''}>
                <c:if test="${zdf:isValid(bean, 'password')}"><a href="#" onclick="passOnEdit('password');this.style.display='none'"><fmt:message key='Edit'/></a></c:if>
            </td>
        </tr>
        
        <tr><td class="ZFieldLabel"><fmt:message key='FullName'/></td>
            <td><input  class="ZField" type="text" id="fromDisplay" name="fromDisplay" value="${bean.fromDisplay}"></td>
        </tr>
        <tr id='emailRow'>
            <td class="ZFieldLabel"><fmt:message key='EmailAddr'/></td>
            <td><input  class="ZField" type="text" id="email" name="email" value="${bean.email}" disabled>
                <span id='email_hint' class='ZHint'>
            </td>
        </tr>
        
        <c:choose>
        <c:when test="${bean.live || bean.ymail}">
            <input type="hidden" name="host" value="${bean.host}">
            <input type="hidden" name="port" value="${bean.port}">
            <input type="hidden" name="ssl" value="${bean.ssl ? 'true' : 'false'}">
        </c:when>
        <c:otherwise>
        
	        <tr id='receivingMailRow'><td colspan=2><b><fmt:message key='ReceivingMail'/></b><hr></td></tr>
	        
	        <tr id='mailServerRow'>
	            <td class="${zdf:isValid(bean, 'host') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='InMailServer'/></td>
	            <td>
	                <table cellspacing=0 cellpadding=0>
	                    <tr>
	                        <td><input style='width:240px' class="ZField" type="text" id="host" name="host" value="${bean.host}">
	                        </td>
	                        <td>&nbsp;&nbsp;&nbsp;</td>
	                        <td class="${zdf:isValid(bean, 'port') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='Port'/></td>
	                        <td width=100%><input style='width:50px' class="ZField" type="text" id="port" name="port" value="${bean.port}">
	                        </td>
	                    </tr>
	                </table>
	            </td>
	        </tr>
	        <tr id='mailSecureRow'><td class="ZFieldLabel"></td>
	            <td><input type="checkbox" id="ssl" name="ssl" ${bean.ssl ? 'checked' : ''}> <fmt:message key='UseSSL'/></td>
	            
	        </tr>
	
	        <tr id='sendingMailRow'><td colspan=2><b><fmt:message key='SendingMail'/></b><hr></td></tr>
        
	        <tr id='smtpServerRow'>
	            <td class="${zdf:isValid(bean, 'smtpHost') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='OutMailServer'/></td>
	            <td>
	                <table cellspacing=0 cellpadding=0>
	                    <tr>
	                        <td><input style='width:240px' class="ZField" type="text" id=smtpHost name="smtpHost" value="${bean.smtpHost}">
	                        </td>
	                        <td>&nbsp;&nbsp;&nbsp;</td>
	                        <td class="${zdf:isValid(bean, 'smtpPort') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='Port'/></td>
	                        <td width=100%><input style='width:50px' class="ZField" type="text" id="smtpPort" name="smtpPort" value="${bean.smtpPort}">
	                        </td>
	                    </tr>
	                </table>
	            </td>
	        </tr>
	        <tr id='smtpSecureRow'><td class="ZFieldLabel"></td>
	            <td><input type="checkbox" id="smtpSsl" name="smtpSsl" ${bean.smtpSsl ? 'checked' : ''}> <fmt:message key='UseSSL'/></td>
	            
	        </tr>
	        <tr id='smtpAuthRow'> <td class="ZCheckboxLabel"></td>
	            <td><input type="checkbox" id="smtpAuth" name="smtpAuth" ${bean.smtpAuth ? 'checked' : ''}
	                                        onclick='zd.toggle("smtpAuthSettingsRow", this.checked)'
	                                    > <fmt:message key='UsrPassForSend'/></td>
	           
	        </tr>
	        <tr id='smtpAuthSettingsRow'>
	            <td class="ZFieldLabel"></td>
	            <td>
	                <table>
	                    <tr>
	                        <td class="${zdf:isValid(bean, 'smtpUsername') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='UserName'/></td>
	                        <td><input style='width:240px' class="ZField" type="text" id="smtpUsername" name="smtpUsername" value="${bean.smtpUsername}"></td>
	                    </tr>
	                    <tr>
	                        <td class="${zdf:isValid(bean, 'smtpPassword') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='Password'/></td>
	                        <td>
                               <input style='width:240px' class="ZField" type="password" id="smtpPassword" name="smtpPassword" value="${bean.smtpPassword}"
                                 ${zdf:isValid(bean, 'smtpPassword') ? 'disabled' : ''}>
                               <c:if test="${zdf:isValid(bean, 'smtpPassword')}">
                                 <a href="#" onclick="passOnEdit('smtpPassword');this.style.display='none'"><fmt:message key='Edit'/></a>
                               </c:if>
	                        </td>
	                    </tr>
	                </table>
	            </td>
	        </tr>

	        <tr id='replyToRow'>
	            <td class="ZFieldLabel"><fmt:message key='ReplyTo'/></td>
	            <td>
	                <table>
	                    <tr>
	                        <td><fmt:message key='Name'/></td>
	                        <td><input style='width:240px' class="ZField" type="text" id="replyToDisplay" name="replyToDisplay" value="${bean.replyToDisplay}"
	                                onkeypress='zd.markElementAsManuallyChanged(this)'
	                        ></td>
	                       
	                    </tr>
	                    <tr>
	                         <td><fmt:message key='EmailAddress'/></td>
	                        <td><input style='width:240px' class="ZField" type="text" id="replyTo" name="replyTo" value="${bean.replyTo}"
	                                onkeypress='zd.markElementAsManuallyChanged(this)'
	                        ></td>
	                    </tr>
	                </table>
	            </td>
	        </tr>       
        
        </c:otherwise>
        </c:choose>

        <tr><td colspan=2><b><fmt:message key='SendAndReceive'/></b><hr></td></tr>

        <tr>
            <td class="ZFieldLabel"><fmt:message key='OtherSyncFrequency'/></td>
            <td>
                <select class="ZSelectSmall" id="syncFreqSecs" name="syncFreqSecs">
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
        
        <tr id='syncSettingsRow'><td class="ZFieldLabel"></td>
            <td><input type="checkbox" id="syncAllServerFolders" name="syncAllServerFolders" ${bean.syncAllServerFolders ? 'checked' : ''}> <fmt:message key='SyncAllFolders'/></td>
            
        </tr>
        
        <tr id='popSettingsRow'> <td class="ZFeildLabel"></td>
            <td><input type="checkbox" id="leaveOnServer" name="leaveOnServer" ${bean.leaveOnServer ? 'checked' : ''}> <fmt:message key='LeaveOnServer'/></td>
           
        </tr>

        <c:if test="${bean.contactSyncSupported}">
            <tr>
                <td style='text-align:right'><input type="checkbox" id="contactSyncEnabled" name="contactSyncEnabled" ${bean.contactSyncEnabled ? 'checked' : ''}></td>
                <td class="ZCheckboxLabel"><fmt:message key='ContactSyncEnabled'/></td>
            </tr>
        </c:if>
        <c:if test="${bean.contactSyncSupported}">
            <tr>
                <td style='text-align:right'><input type="checkbox" id="calendarSyncEnabled" name="calendarSyncEnabled" ${bean.calendarSyncEnabled ? 'checked' : ''}></td>
                <td class="ZCheckboxLabel"><fmt:message key='CalendarSyncEnabled'/></td>
            </tr>
        </c:if>
        <tr>
            <td style='text-align:right'><input type="checkbox" id="debugTraceEnabled" name="debugTraceEnabled" ${bean.debugTraceEnabled ? 'checked' : ''}></td>
            <td class="ZCheckboxLabel"><fmt:message key='EnableTrace'/></td>
        </tr>
    </table>

</form>
</span>
<div align="right">
            <a href="#" id="manageButton" class='DwtButton' onclick="OnManage()"><fmt:message key='ManageData'/></a>
       
</div>
</div>
