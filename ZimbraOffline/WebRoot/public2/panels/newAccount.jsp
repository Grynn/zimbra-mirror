<div id="newAccount" class="ZWizardPage">
<div class="ZWizardPageTitle">
	Set Up a Zimbra Account
</div>

<% if (error == null) { %>
		<p>Enter information about your Zimbra account below.</p>
<% } else { %>
<p class='ZOfflineError'><%= error %></p>
<% } %>

<form name="new_service" action="<%=LOCALHOST_THIS_URL%>" method="POST">

	<input type="hidden" name="act" value="new">

	<table class="ZWizardForm">
		<tr>
			<td class="ZFieldLabel">Email address:</td>
			<td><input style='width:200px' class="ZField" type="text" id="account" name="account" value="<%=param_account%>">
							<span class='ZHint'>(e.g. john@company.com)</span></td>
		</tr>
			<tr><td></td></tr>
		<tr>
			<td class="ZFieldLabel">Password:</td>
			<td><input style='width:100px' class="ZField" type="password" id="password" name="password" value="<%=param_password%>"></td>
		</tr>

			<tr><td></td></tr>

		<tr>
			<td class="ZFieldLabel">Zimbra Server:</td>
			<td>
				<table cellspacing=0 cellpadding=0>
					<tr><td><input style='width:200px' class="ZField" type="text" id="server_name" name="server_name" value="<%=param_server%>">
							<br><span class='ZHint'>(e.g. mail.company.com)</span></td>
						<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
						<td class="ZFieldLabel">Port:</td>
						<td><nobr><input style='width:50px' class="ZField" type="text" disabled="true" id="server_port" name="server_port" value="<%=param_port%>"
								>&nbsp;&nbsp;<a href="#" onclick="Ajax.enable('server_port');this.style.display='none'">Edit</a>
							<br><span class='ZHint'>(e.g. 80)</span></td>
					</tr>
				</table>
			</td>
		</tr>
			<tr><td></td></tr>
		<tr>
			<td class='ZCheckboxCell'><input type="checkbox" id="server_secured" name="server_secured" onclick="secureCheck();"></td>
			<td class="ZCheckboxLabel">Use an encrypted connection (SSL) when accessing this server</td>
		</tr>

		<tr><td colspan=2><hr></td></tr>
		
		<tr>
			<td class="ZFieldLabel">Synchronize with server every:</td>
			<td>
				<table cellspacing=0 cellpadding=0>
					<tr><td><input style='width:50px' class="ZField" type="text" id="sync_interval" name="sync_interval" value=<%=param_interval%>></td>
						<td>&nbsp;</td>
						<td><select class="ZSelect" id="interval_unit" name="interval_unit">
								<option <%=unit_sec_selected%>>seconds</option>
								<option <%=unit_min_selected%>>minutes</option>
							</select>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

</form>

<p>Press <span class="ZWizardButtonRef">Test Settings</span> to verify these settings</p>

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