<div id="console" class="ZWizardPage">
    <div class="ZWizardPageTitle">
		<div class='ZFloatInHead'>Click &nbsp; <b><img src='/zimbra/img/startup/ImgLogoff.gif' width=16px height=16px align=top> Setup</a></b> &nbsp; to come back here later.</div>
    	Account Setup
    </div>

<!--
    <% if (error != null) { %>
    <p><font color="red"><%= error %>
    </font></p>
    <% } else if (act != null && act.equals("reset")) { %>
    <p><font color="blue">All local data has been cleared and account will resynchronize with the server.</font></p>
    <% } else { %>
    <p>What do you want to do?</p>
    <% } %>
-->
	<p>Click an account name below to manage it.
	</p>


    <table class="ZWizardTable" cellpadding=5 border=0>
    	<tr><th>Account Name</th><th>Email Address</th><th>Last Accessed</th></tr>
		<tr><td><a href='javascript:Ajax.showPanel("changeAccount_i")'>Main zimbra Account</a></td>
			<td>bob@hosted.zimbra.com</td>
			<td>10/29/07 at 4:45pm</td>
		</tr>
		<tr><td><a href='javascript:Ajax.showPanel("changeService_i")'>Yahoo! Mail</a></td>
			<td>bob@yahoo.com</td>
			<td>10/29/07 at 4:45pm</td>
		</tr>
		<tr><td><a href='javascript:Ajax.showPanel("changeService_i")'>Bob at Gmail</a></td>
			<td>bob@yahoo.com</td>
			<td class='ZSyncErrorNotice'>Error: could not connect with server</td>
		</tr>
    </table>

    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButton">
                <button class='DwtButton' onclick="Ajax.showPanel('accountType')">Set Up Another Account</button>
            </td>
            <td class="ZWizardButton">
                <button class='DwtButton' onclick="Ajax.showPanel('proxy')">Set Up A Proxy Server</button>
            </td>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button class='DwtButton-focused' onclick="OnLogin()">Go to Zimbra Desktop</button>
            </td>
         </tr>
    </table>
</div>
