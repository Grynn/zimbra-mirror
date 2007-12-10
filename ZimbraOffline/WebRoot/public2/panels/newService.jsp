<div id="newService" class="ZWizardPage">

<div class="ZWizardPageTitle">
	<div id='settings_hint' class='ZFloatInHead'></div>
	<span id='pageTitle'>POP/IMAP Account Setup</span>
</div>


<% if (error == null) { %>
<p id='instructions'>Enter all of the configuration information below.<br>
						If you are not sure of some of the settings, contact your systems administrator
						or Internet Service Provider.
</p>
<% } else { %>
<p class='ZOfflineError'><%= error %></p>
<% } %>

<form name="new_service" action="<%=LOCALHOST_THIS_URL%>" method="POST">

	<input type="hidden" name="act" value="new">

	<table class="ZWizardForm" style='width:90%'>
		<tr>
			<td class="ZFieldLabel">Description:</td>
			<td><input style='width:200px' class="ZField" type="text" id="service" name="service" value="<%=param_service%>"
					onkeypress='Ajax.syncIdsOnTimer(this, "folder_name_display")'
			>
						<span id='service_hint' class='ZHint'>(e.g. My ISP)</span></td>
		</tr>
		<tr><td class="ZFieldLabel">Your full name:</td>
			<td><input style='width:200px' class="ZField" type="text" id="from_display" name="from_display" value="<%=param_from_display%>"
					onkeypress='Ajax.syncIdsOnTimer(this, "replyto_display")'
			></td>
		</tr>
		<tr id='emailRow'>
			<td class="ZFieldLabel">Email address:</td>
			<td><input style='width:200px' class="ZField" type="text" id="email" name="email" value="<%=param_email%>"
					onkeypress='Ajax.syncIdsOnTimer(this, "username", "smtp_user", "replyto")'
				>
					<span id='email_hint' class='ZHint'>
			</td>
		</tr>

		<tr id='accountTypeRow'>
			<td class="ZFieldLabel">Account Type:</td>
			<td>
				<table cellspacing=0 cellpadding=0><tr>
					<td class='ZRadioCell'><input type=radio id='protocol_pop' name="protocol_name" value="pop3" <%=pop3_checked%>
												onclick='togglePopSettings(true)'
											></td>
					<td class="ZFieldLabel"><label class="ZRadioLabel" for='protocol_pop'>POP3</label></td>
					<td>&nbsp;&nbsp;&nbsp;</td>
					<td class='ZRadioCell'><input type=radio id='protocol_imap' name="protocol_name" value="imap" <%=imap_checked%>
												onclick='togglePopSettings(false)'
											></td>
					<td class="ZFieldLabel"><label class="ZRadioLabel" for='protocol_imap'>IMAP4</label></td>
				</tr></table>
			</td>
		</tr>
		
		<tr id='receivingMailRow'><td colspan=2><div class='ZOfflineHeader'>Receiving Mail</div></td></tr>

		
		<tr id='usernameRow'>
			<td class="ZFieldLabel">User Name:</td>
			<td><input style='width:200px' class="ZField" type="text" id="username" name="username" value="<%=param_username%>"
						onkeypress='Ajax.markElementAsManuallyChanged(this)'
					>
					<span id='username_hint' class='ZHint'></span>			
			</td>
		</tr>
		<tr id='passwordRow'>
			<td class="ZFieldLabel">Password:</td>
			<td><input style='width:100px' class="ZField" type="password" id="password" name="password" value="<%=param_password%>"
					onkeypress='Ajax.syncIdsOnTimer(this, "smtp_pass")'
			></td>
		</tr>


		<tr id='mailServerRow'>
			<td class="ZFieldLabel">Incoming Mail Server:</td>
			<td>
				<table cellspacing=0 cellpadding=0>
					<tr>
						<td><input style='width:200px' class="ZField" type="text" id="server_host" name="server_host" value="<%=param_host%>">
						</td>
						<td>&nbsp;&nbsp;&nbsp;</td>
						<td class="ZFieldLabel">Port:</td>
						<td width=100%><input style='width:50px' class="ZField" disabled='true' type="text" id="server_port" name="server_port" value="<%=param_port%>">&nbsp;&nbsp;<a href="#" onclick="Ajax.enable('server_port');this.style.display='none'">Edit</a>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr id='mailSecureRow'>
			<td class='ZCheckboxCell'><input type="checkbox" id="server_secure" name="server_secure" <%=ssl_checked%>></td>
			<td class="ZCheckboxLabel">Use an encrypted connection (SSL) when accessing this server</td>
		</tr>

		<tr id='sendingMailRow'><td colspan=2><div class='ZOfflineHeader'>Sending Mail</div></td></tr>
		
		<tr id='smtpServerRow'>
			<td class="ZFieldLabel">Outgoing (SMTP) Mail Server:</td>
			<td>
				<table cellspacing=0 cellpadding=0>
					<tr>
						<td><input style='width:200px' class="ZField" type="text" id="smtp_host" name="smtp_host" value="<%=param_smtp_host%>">
						</td>
						<td>&nbsp;&nbsp;&nbsp;</td>
						<td class="ZFieldLabel">Port:</td>
						<td width=100%><input style='width:50px' class="ZField" disabled='true' type="text" id="smtp_port" name="smtp_port" value="<%=param_smtp_port%>">&nbsp;&nbsp;<a href="#" onclick="Ajax.enable('smtp_port');this.style.display='none'">Edit</a>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr id='smtpSecureRow'>
			<td class='ZCheckboxCell'><input type="checkbox" id="smtp_secure" name="smtp_secure" <%=smtp_ssl_checked%>></td>
			<td class="ZCheckboxLabel">Use an encrypted connection (SSL) when accessing this server</td>
		</tr>
		<tr id='smtpAuthRow'>
			<td class='ZCheckboxCell'><input type="checkbox" id="smtp_auth" name="smtp_auth" <%=smtp_auth_checked%>
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
						<td><input style='width:200px' class="ZField" type="text" id="smtp_user" name="smtp_user" value="<%=param_smtp_user%>"
								onkeypress='Ajax.markElementAsManuallyChanged(this)'
						></td>
					</tr>
					<tr>
						<td class="ZFieldLabel">Password:</td>
						<td><input style='width:100px' class="ZField" type="password" id="smtp_pass" name="smtp_pass" value="<%=param_smtp_pass%>"
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
						<td><input style='width:200px' class="ZField" type="text" id="replyto_display" name="replyto_display" value="<%=param_replyto_display%>"
								onkeypress='Ajax.markElementAsManuallyChanged(this)'
						></td>
						<td><input style='width:200px' class="ZField" type="text" id="replyto" name="replyto" value="<%=param_replyto%>"
								onkeypress='Ajax.markElementAsManuallyChanged(this)'
						></td>
					</tr>
				</table>
			</td>
		</tr>		


		<tr><td colspan=2><div class='ZOfflineHeader'>Downloading Mail</div></td></tr>

		
		<tr>
			<td class="ZFieldLabel">Get new mail every:</td>
			<td><input style='width:50px' class="ZField" type="text" id="sync_interval" name="sync_interval" value=<%=param_interval%>>
				<select class="ZSelect" id="interval_unit" name="interval_unit">
					<option <%=unit_min_selected%>>minutes</option>
					<option <%=unit_sec_selected%>>seconds</option>
				</select>
			</td>
		</tr>

		<tr id='pop_settings_folder_row'><td class="ZFieldLabel">Download Messages to:</td>
			<td>
				<table cellspacing=0 cellpadding=0>
					<tr>
						<td class='ZRadioCell'><input type=radio id='pop_folder_new' name="pop_folder" value="new" <%=pop_to_new_checked%>></td>
						<td class="ZFieldLabelLeft"><label class="ZRadioLabel" for='pop_folder_new'>Folder: <span id='folder_name_display' style='font-weight:bold'>FOLDERNAME</span></label></td>
					</tr>
					<tr>
						<td class='ZRadioCell'><input type=radio id='pop_folder_inbox' name="pop_folder" value="inbox" <%=pop_to_inbox_checked%>></td>
						<td class="ZFieldLabelLeft"><label class="ZRadioLabel" for='pop_folder_inbox'>Inbox</label></td>
					</tr>

					<tr><td colspan=2>&nbsp;</td></tr>

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
			<button class='DwtButton' onclick="Ajax.showPanel('accountType')">Use a Different Account Type</button>
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
