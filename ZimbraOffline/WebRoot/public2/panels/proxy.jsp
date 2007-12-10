<div id="proxy" class="ZWizardPage">
<div class="ZWizardPageTitle">
    Network Proxy Settings
</div>

<% if (error != null) { %>
<p><font color="red"><%= error %></font></p>
<% } %>

<p>
    Some corportate firewalls require you to use a proxy servers to connect to the Internet.
    If you are not sure whether this applies to you, please ask your IT department.
</p>

<form name="settings" action="/zimbra/" method="POST">

    <input type="hidden" name="act">

    <table class="ZWizardForm">

        <tr>
            <td class="ZFieldLabel">Proxy server:</td>
            <td>
				<table cellspacing=0 cellpadding=0>
					<tr>
						<td><input style='width:200px' class="ZField" type="text" id="proxyhost" name="proxy_host" value="<%=param_proxy_host%>">
							<br><span class='ZHint'>(e.g. proxy.company.com)</span>
						</td>
						<td>&nbsp;&nbsp;&nbsp;</td>
						<td class="ZFieldLabel">Port:</td>
						<td width=100%><input style='width:50px' class="ZField" type="text" id="proxyport" name="proxy_port" value="<%=param_port%>">
							<!-- &nbsp;&nbsp;<a href="#" onclick="Ajax.enable('server_port');this.style.display='none'">Edit</a> -->
							<br><span class='ZHint'>(e.g. 8888)</span>
						</td>
					</tr>
				</table>
			</td>
        <tr>
            <td class="ZFieldLabel">User Name:</td>
            <td><input style='width:200px' class="ZField" type="text" id="proxyuser" name="proxy_user"
                       value="<%=param_proxy_user%>"> <span class='ZHint'>(if proxy requires authentication)</span>
            </td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Password:</td>
            <td><input style='width:200px' class="ZField" type="text" id="proxypass" name="proxy_pass"
                       value="<%=param_proxy_pass%>"> <span class='ZHint'>(if proxy requires authentication)</span>
            </td>
        </tr>

    </table>

</form>

<table class="ZWizardButtonBar">
    <tr>
        <td class="ZWizardButtonSpacer">
            <div></div>
        </td>
        <td class="ZWizardButton">
            <button class='DwtButton' onclick="Ajax.showPanel('console')()">Cancel</button>
        </td>
        <td class="ZWizardButton">
            <button class='DwtButton-focused' onclick="OnModifySettings()" disabled>Save Changes</button>
        </td>
</table>

</div>
