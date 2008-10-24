<%@ tag body-content="empty" %>
<%@ attribute name="uri" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>

<fmt:setBundle basename="/desktop/ZdMsg" scope="request"/>

<script type="text/javascript">
<!--
function InitScreen() {
    zd.hide("syncSettingsRow");
    zd.hide("popSettingsRow");
    if (zd.isChecked("protocol_pop")) {
        zd.show("popSettingsRow");
    } else if (zd.isChecked("protocol_imap")) {
        zd.show("syncSettingsRow");
    }
    
    if (!zd.isChecked("smtpAuth")) {
        zd.hide("smtpAuthSettingsRow");
    }
    InitSmtpPort();
}

function onSelectPop() {
    zd.show("popSettingsRow");
    zd.hide("syncSettingsRow");
    SetPort();
}

function onSelectImap() {
    zd.hide("popSettingsRow");
    zd.show("syncSettingsRow");
    SetPort();
}

function SetPort() {
    if (zd.isDisabled("port")) {
        if (zd.isChecked("protocol_pop")) {
            if (zd.isChecked("ssl")) {
                zd.set("port", "995");
            } else {
                zd.set("port", "110");
            }
        } else if (zd.isChecked("protocol_imap")) {
            if (zd.isChecked("ssl")) {
                zd.set("port", "993");
            } else {
                zd.set("port", "143");
            }
        }
    }
}

function InitSmtpPort() {
    if (zd.isValueEqual("smtpPort", "")) {
        SetSmtpPort();
    }
}

function SetSmtpPort() {
    if (zd.isDisabled("smtpPort")) {
        if (zd.isChecked("smtpSsl")) {
            zd.set("smtpPort", "465");
        } else {
            zd.set("smtpPort", "25");
        }
    }
}

function OnPickType() {
    window.location = '/zimbra/desktop/new.jsp';
}

function OnCancel() {
    window.location = '/zimbra/desktop/console.jsp';
}

function OnSubmit() {
    beforeSubmit();
    zd.enable("port");
    zd.enable("smtpPort");
    xmailNew.submit();
}

function beforeSubmit() {
    disableButtons();
    zd.set("whattodo", "<span class='ZOfflineNotice'><fmt:message key='Processing'/></span>");
}

function disableButtons() {
    //zd.disable("typeButton");
    zd.disable("cancelButton");
    zd.disable("saveButton");
}
//-->
</script>



    <form name="xmailNew" action="${uri}" method="POST">

		<input type="hidden" name="verb" value="add">
        <input type="hidden" name="accntType" value="OtherAcct">
        
        <table cellpadding="5">
			<tr>
				<td class="${zdf:isValid(bean, 'accountName') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='Description'/></td>
				<td>
					<input class="ZField" type="text" id="accountName" name="accountName" value="${bean.accountName}"> 
					<span id='service_hint' class='ZHint'><fmt:message key='DescHint2'/></span>
				</td>
			</tr>

			<tr>
				<td class="ZFieldLabel"><fmt:message key='FullName'/></td>
				<td><input class="ZField" type="text" id="fromDisplay" name="fromDisplay" value="${bean.fromDisplay}"></td>
			</tr>

			<tr id='emailRow'>
				<td class="${zdf:isValid(bean, 'email') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='EmailAddr'/></td>
				<td><input class="ZField" type="text" id="email" name="email" value="${bean.email}" onkeypress='zd.syncIdsOnTimer(this, "username", "smtpUsername")'></td>
			</tr>

			<tr id='accountTypeRow'>
				<td class="${zdf:isValid(bean, 'protocol') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='AccountType'/></td>
				<td>
					<table cellspacing=0 cellpadding=0><tr>
						<td>
							<input type=radio id='protocol_imap' name="protocol" value="imap" ${bean.imap ? 'checked' : ''} onclick='onSelectImap()'></td>
						<td>		
						<label class="ZRadioLabel" for='protocol_imap'>IMAP4</label></td>

						<td>
							<input type=radio id='protocol_pop' name="protocol" value="pop3" ${bean.pop ? 'checked' : ''} onclick='onSelectPop()'></td>

						<td><label class="ZRadioLabel" for='protocol_pop'>POP3</label></td>
					</tr></table>
				</td>
			</tr>

			<tr id='receivingMailRow'><td colspan=2><b><fmt:message key='ReceivingMail'/></b><hr></td></tr>

			<tr id='usernameRow'>
				<td class="${zdf:isValid(bean, 'username') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='UserName'/></td>
				<td><input class="ZField" type="text" id="username" name="username" value="${bean.username}" onkeypress='zd.markElementAsManuallyChanged(this);zd.syncIdsOnTimer(this, "smtpUsername")'></td>
			</tr>

			<tr id='passwordRow'>
				<td class="${zdf:isValid(bean, 'password') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='Password'/></td>
				<td><input class="ZField" type="password" id="password" name="password" value="${bean.password}" onkeypress='zd.syncIdsOnTimer(this, "smtpPassword")'></td>
			</tr>

			<tr id='mailServerRow'>
				<td class="${zdf:isValid(bean, 'host') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='InMailServer'/></td>
				<td>
					<table cellspacing=0 cellpadding=0>
						<tr>
							<td><input style='width:240px' class="ZField" type="text" id="host" name="host" value="${bean.host}"></td>
							<td>&nbsp;&nbsp;&nbsp;</td>
							<td class="${zdf:isValid(bean, 'port') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='Port'/></td>
							<td width=100%><input style='width:50px' class="ZField" disabled='true' type="text" id="port" name="port" value="${bean.port}">&nbsp;&nbsp;<a href="#" onclick="zd.enable('port');this.style.display='none'"><fmt:message key='Edit'/></a></td>
						</tr>
					</table>
				</td>
			</tr>

			<tr id='mailSecureRow'><td class="ZFieldLabel"></td>
				<td>
				<table cellpadding="0" cellspacing="0" border="0"><tr>
				<td><input type="checkbox" id="ssl" name="ssl" ${bean.ssl ? 'checked' : ''} onclick="SetPort()"></td><td><fmt:message key='UseSSL'/>
				</td></tr></table>
				</td>

			</tr>

			<tr id='sendingMailRow'><td colspan=2><b><fmt:message key='SendingMail'/></b><hr></td></tr>

			<tr id='smtpServerRow'>
				<td class="${zdf:isValid(bean, 'smtpHost') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='OutMailServer'/></td>
				<td>
					<table cellspacing=0 cellpadding=0>
						<tr>
							<td><input style='width:240px' class="ZField" type="text" id=smtpHost name="smtpHost" value="${bean.smtpHost}"></td>
							<td>&nbsp;&nbsp;&nbsp;</td>
							<td class="${zdf:isValid(bean, 'smtpPort') ? 'ZFieldLabel' : 'ZFieldError'}"><fmt:message key='Port'/></td>
							<td width=100%><input style='width:50px' class="ZField" disabled='true' type="text" id="smtpPort" name="smtpPort" value="${bean.smtpPort}">&nbsp;&nbsp;<a href="#" onclick="zd.enable('smtpPort');this.style.display='none'"><fmt:message key='Edit'/></a></td>
						</tr>
					</table>
				</td>
			</tr>

			<tr id='smtpSecureRow'><td class="ZFieldLabel"></td>
				<td><table cellpadding="0" cellspacing="0" border="0"><tr>
				<td><input type="checkbox" id="smtpSsl" name="smtpSsl" ${bean.smtpSsl ? 'checked' : ''} onclick="SetSmtpPort()"></td><td><fmt:message key='UseSSL'/>
				</td></tr></table>
				</td>

			</tr>

			<tr id='smtpAuthRow'><td class="ZCheckboxLabel"></td>
				<td>
				<table cellpadding="0" cellspacing="0" border="0"><tr>
				<td><input type="checkbox" id="smtpAuth" name="smtpAuth" ${bean.smtpAuth ? 'checked' : ''} onclick='zd.toggle("smtpAuthSettingsRow", this.checked)'></td><td><fmt:message key='UsrPassForSend'/>
				</td></tr></table>
				</td>
			</tr>

			<tr id='smtpAuthSettingsRow'>
				<td class="ZFieldLabel"></td>
				<td>
					<table>
						<tr>
							<td class="${zdf:isValid(bean, 'smtpUsername') ? 'ZLabel' : 'ZFieldError'}"><fmt:message key='UserName'/></td>
							<td><input style='width:240px' class="ZField" type="text" id="smtpUsername" name="smtpUsername" value="${bean.smtpUsername}" onkeypress='zd.markElementAsManuallyChanged(this)'></td>
						</tr>
						<tr>
							<td class="${zdf:isValid(bean, 'smtpPassword') ? 'ZLabel' : 'ZFieldError'}"><fmt:message key='Password'/></td>
							<td><input style='width:240px' class="ZField" type="password" id="smtpPassword" name="smtpPassword" value="${bean.smtpPassword}" onkeypress='zd.markElementAsManuallyChanged(this)'></td>
						</tr>
					</table>
				</td>
			</tr>

			<tr id='replyToRow'>
				<td class="ZFieldLabel"><fmt:message key='ReplyTo'/></td>
				<td>
					<table>
						<tr>
							<td align="right"><fmt:message key='Name'/></td>
							<td align="right"><input style='width:240px' class="ZField" type="text" id="replyToDisplay" name="replyToDisplay" value="${bean.replyToDisplay}" onkeypress='zd.markElementAsManuallyChanged(this)'></td>

						</tr>
						<tr>
						<td><fmt:message key='EmailAddress'/></td>

							<td><input style='width:240px' class="ZField" type="text" id="replyTo" name="replyTo" value="${bean.replyTo}" onkeypress='zd.markElementAsManuallyChanged(this)'></td>
						</tr>
					</table>
				</td>
			</tr>

			<tr><td colspan=2><b><fmt:message key='DownloadingMail'/></b><hr></td></tr>

			<tr>
	            <td class="ZFieldLabel"><fmt:message key='SyncFrequency'/></td>
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

			<tr id='popSettingsRow'><td class="ZFieldLabel"></td>
				<td><input type="checkbox" id="leaveOnServer" name="leaveOnServer" ${bean.leaveOnServer ? 'checked' : ''}> <fmt:message key='LeaveOnServer'/></td>

			</tr>
		</table>
	</form>
