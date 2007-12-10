<div id="changeService_i" class="ZWizardPage">

<div class="ZWizardPageTitle">
	<div id='settings_hint' class='ZFloatInHead'></div>
	<span id='pageTitle'>Change Email Account Setup</span>
</div>


<% if (error != null) { %>
	<p class='ZOfflineError'><%= error %></p>
<% } else if (act != null && act.equals("modify")) { %>
	<p class='ZOfflineNotice'>Service settings have been updated.</p>
<% } else { %>
	<p>What do you want to change?</p>
<% } %>


<form name="update_account_<%=i%>" action="<%=LOCALHOST_THIS_URL%>" method="POST">

    <input type="hidden" name="act" value="modify">
    <input type="hidden" name="service" value="<%=service%>">
    <input type="hidden" name="username" value="<%=username%>">
    <input type="hidden" name="protocol_name" value="<%=dsType.toString()%>">
    <input type="hidden" name="pop_folder" value="<%=folderId == Mailbox.ID_FOLDER_INBOX ? "inbox" : "new"%>">

	<table class="ZWizardForm" style='width:90%'>
		<tr id='accountTypeRow'>
			<td class="ZFieldLabel">Account Type:</td>
			<td><input style='width:200px' class="ZField" type="text" disabled value='<%=pop3Checked ? "POP3" : "IMAP4" %>'>
<!--
				<table cellspacing=0 cellpadding=0><tr>
					<td class='ZRadioCell'><input type=radio id='protocol_pop' name="mod_protocol_name" value="pop3" <%=pop3Checked%> disabled
												onclick='togglePopSettings(true)'
											></td>
					<td class="ZFieldLabel"><label class="ZRadioLabel" for='mod_protocol_pop'>POP3</label></td>
					<td>&nbsp;&nbsp;&nbsp;</td>
					<td class='ZRadioCell'><input type=radio id='protocol_imap' name="mod_protocol_name" value="imap" <%=imapChecked%> disabled
												onclick='togglePopSettings(false)'
											></td>
					<td class="ZFieldLabel"><label class="ZRadioLabel" for='mod_protocol_imap'>IMAP4</label></td>
				</tr></table>
-->
			</td>
		</tr>
		

		<tr>
			<td class="ZFieldLabel">Description:</td>
			<td><input style='width:200px' class="ZField" type="text" disabled name="service" value="<%=service%>"
					onkeypress='Ajax.syncIdsOnTimer(this, "folder_name_display")'
			>
						<span id='service_hint' class='ZHint'></span></td>
		</tr>
		<tr id='usernameRow'>
			<td class="ZFieldLabel">User Name:</td>
			<td><input style='width:200px' class="ZField" type="text" disabled name="username" value="<%=username%>"
						onkeypress='Ajax.markElementAsManuallyChanged(this)'
					>
					<span id='username_hint' class='ZHint'></span>			
			</td>
		</tr>
		<tr id='passwordRow'>
			<td class="ZFieldLabel">Password:</td>
			<td><input style='width:100px' class="ZField" type="password" id="password" name="password" value="********"
					onkeypress='Ajax.syncIdsOnTimer(this, "smtp_pass")'
			></td>
		</tr>
		<tr><td class="ZFieldLabel">Your full name:</td>
			<td><input style='width:200px' class="ZField" type="text" id="from_display" name="from_display" value="<%=fromDisplay%>"
					onkeypress='Ajax.syncIdsOnTimer(this, "replyto_display")'
			></td>
		</tr>
		<tr id='emailRow'>
			<td class="ZFieldLabel">Email address:</td>
			<td><input style='width:200px' class="ZField" type="text" id="email" name="email" value="<%=email%>"
					onkeypress='Ajax.syncIdsOnTimer(this, "replyto")'
				>
					<span id='email_hint' class='ZHint'>
			</td>
		</tr>

		<tr id='receivingMailRow'><td colspan=2><div class='ZOfflineHeader'>Receiving Mail</div></td></tr>

		


		<tr id='mailServerRow'>
			<td class="ZFieldLabel">Incoming Mail Server:</td>
			<td>
				<table cellspacing=0 cellpadding=0>
					<tr>
						<td><input style='width:200px' class="ZField" type="text" id="mod_server_host" name="server_host" value="<%=serverHost%>">
						</td>
						<td>&nbsp;&nbsp;&nbsp;</td>
						<td class="ZFieldLabel">Port:</td>
						<td width=100%><input style='width:50px' class="ZField" disabled='true' type="text" id="mod_server_port" name="server_port" value="<%=serverPort%>"
											>&nbsp;&nbsp;<a href="#" onclick="Ajax.enable('mod_server_port');this.style.display='none'">Edit</a>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr id='mailSecureRow'>
			<td class='ZCheckboxCell'><input type="checkbox" id="mod_server_secure" name="server_secure" <%=sslChecked%>></td>
			<td class="ZCheckboxLabel">Use an encrypted connection (SSL) when accessing this server</td>
		</tr>

		<tr id='sendingMailRow'><td colspan=2><div class='ZOfflineHeader'>Sending Mail</div></td></tr>
		
		<tr id='smtpServerRow'>
			<td class="ZFieldLabel">Outgoing (SMTP) Mail Server:</td>
			<td>
				<table cellspacing=0 cellpadding=0>
					<tr>
						<td><input style='width:200px' class="ZField" type="text" id="mod_smtp_host" name="smtp_host" value="<%=smtpHost%>">
						</td>
						<td>&nbsp;&nbsp;&nbsp;</td>
						<td class="ZFieldLabel">Port:</td>
						<td width=100%><input style='width:50px' class="ZField" disabled='true' type="text" id="mod_smtp_port" name="smtp_port" value="<%=smtpPort%>"
									>&nbsp;&nbsp;<a href="#" onclick="Ajax.enable('mod_smtp_port');this.style.display='none'">Edit</a>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr id='smtpSecureRow'>
			<td class='ZCheckboxCell'><input type="checkbox" id="mod_smtp_secure" name="smtp_secure" <%=smtpSslChecked%>></td>
			<td class="ZCheckboxLabel">Use an encrypted connection (SSL) when accessing this server</td>
		</tr>
		<tr id='smtpAuthRow'>
			<td class='ZCheckboxCell'><input type="checkbox" id="mod_smtp_auth" name="smtp_auth" <%=smtpAuthChecked%>
										onclick='toggleSmtpAuthSettings(this.checked)'
									></td>
			<td class="ZCheckboxLabel">Username and password required for sending mail</td>
		</tr>
		<tr id='smtp_auth_settings_row'>
			<td></td>
			<td>
				<table>
					<tr>
						<td class="ZFieldLabel">User Name:</td>
						<td><input style='width:200px' class="ZField" type="text" id="smtp_user" name="smtp_user" value="<%=smtpUser%>"
								onkeypress='Ajax.markElementAsManuallyChanged(this)'
						></td>
					</tr>
					<tr>
						<td class="ZFieldLabel">Password:</td>
						<td><input style='width:100px' class="ZField" type="password" id="smtp_pass" name="smtp_pass" value="<%=smtpPass%>"
								onkeypress='Ajax.markElementAsManuallyChanged(this)'
						></td>
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
						<td><input style='width:200px' class="ZField" type="text" id="replyto_display" name="replyto_display" value="<%=replytoDisplay%>"
								onkeypress='Ajax.markElementAsManuallyChanged(this)'
						></td>
						<td><input style='width:200px' class="ZField" type="text" id="replyto" name="replyto" value="<%=replyto%>"
								onkeypress='Ajax.markElementAsManuallyChanged(this)'
						></td>
					</tr>
				</table>
			</td>
		</tr>		


		<tr><td colspan=2><div class='ZOfflineHeader'>Downloading Mail</div></td></tr>

		
		<tr>
			<td class="ZFieldLabel">Get new mail every:</td>
			<td><input style='width:50px' class="ZField" type="text" id="syncQuantity" name="sync_interval" value=<%=interval%>>
				<select class="ZSelect" id="syncUnits" name="interval_unit">
					<option <%=unit_min_selected%>>minutes</option>
					<option <%=unit_sec_selected%>>seconds</option>
				</select>
			</td>
		</tr>

		<tr id='pop_settings_folder_row'>
			<td class="ZFieldLabel"><% if (pop3Checked) { %> Download Messages to: <% } %></td>
			<td><input style='width:200px' class="ZField" type="text" disabled value='<%=popToNewChecked ? "Folder: "+service : "Inbox"%>'>
<!--			
				<table cellspacing=0 cellpadding=0>
					<tr>
						<td class='ZRadioCell'><input type=radio id='pop_folder_new' name="pop_folder" value="new" <%=popToNewChecked%> disabled></td>
						<td class="ZFieldLabelLeft"><label class="ZRadioLabel" for='pop_folder_new'>Folder: <span id='folder_name_display' style='font-weight:bold'>FOLDERNAME</span></label></td>
					</tr>
					<tr>
						<td class='ZRadioCell'><input type=radio id='pop_folder_inbox' name="pop_folder" value="inbox" <%=popToInboxChecked%> disabled></td>
						<td class="ZFieldLabelLeft"><label class="ZRadioLabel" for='pop_folder_inbox'>Inbox</label></td>
					</tr>

					<tr><td colspan=2>&nbsp;</td></tr>
-->
					<tr id='pop_settings_delete_row'>
						<td style='text-align:right'><input type="checkbox" id="leave_on_server" name="leave_on_server" disabled></td>
						<td class="ZCheckboxLabel ZHint">Delete messages on the server after downloading them</td>
					</tr>

				</table>
			</td>
		</tr>

	</table>

</form>

<p>Press <span class="ZWizardButtonRef">Test Settings</span> to verify these settings.</p>

<table class="ZWizardButtonBar">
	<tr>
        <td class="ZWizardButton">
            <button class='DwtButton' onclick="deleteAccountWarning()">Remove Account...</button>
        </td>
		<td class="ZWizardButtonSpacer">
			<div></div>
		</td>
		<td class="ZWizardButton">
			<button class='DwtButton' onclick="Ajax.showPanel('console')">Cancel</button>
		</td>
		<td class="ZWizardButton">
			<button class='DwtButton-focused' onclick="OnNew()">Test Settings</button>
		</td>
</table>
</div>
