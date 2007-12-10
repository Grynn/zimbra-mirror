<!-- NOTE: this page should probably be merged with "newAccount.jsp" as they're both essentially showing the same thing -->

<div id="changeAccount_<%=i%>" class="ZWizardPage">
<div class="ZWizardPageTitle">Change A Zimbra Account</div>

<% if (error != null) { %>
<p class='ZOfflineError'><%= error %></p>
<% } else if (act != null && act.equals("modify")) { %>
<p class='ZOfflineNotice'>Account settings have been updated.</p>
<% } else { %>
<p>What do you want to change?</p>
<% } %>


<form name="update_account_<%=i%>" action="<%=LOCALHOST_THIS_URL%>" method="POST">

        <input type="hidden" name="account" value="<%=name%>">
        <input type="hidden" name="act" value="modify">

        <table class="ZWizardForm">
            <tr>
                <td class="ZFieldLabel">Email address:</td>
                <td colspan=3><input style='width:200px' class="ZField" type="text" id="email" value="<%=name%>" disabled> 
                		<a href="javascript:Ajax.toggle('changeAccountInfo')">How do I change this?</a></td>
            </tr>
			<tr><td colspan='2'>
				<div id='changeAccountInfo' class='infoBox' style='display:none'>
						<div class='infoTitle'>You can only set up a single Zimbra account at a time.</div>
		
						<p>If you want to replace the existing desktop account with another one you must first remove this account.
							<ol>
								<li> Press <span class='ZWizardButtonRef'>Remove Account...</span> below to remove the existing account.
								<li> Once downloaded mailbox messages has been removed, you will be returned to the Account Setup page.
								<li> Press <b>Set Up Another Account</b> on the Account Setup page to create another Zimbra account.
							</ol>
						<br>
						<a href="javascript:Ajax.toggle('changeAccountInfo')">Done</a>
				</div>
		    </td></tr>
            <tr>
                <td class="ZFieldLabel">Password:</td>
                <td colspan=3><input style='width:100px' class="ZField" type="password" id="paswd" name="password" value="********"></td>
            </tr>
			<tr><td></td></tr>
           <tr>
                <td class="ZFieldLabel">Zimbra Server:</td>
                <td>
                	<table cellspacing=0 cellpadding=0>
                		<tr>
							<td><input style='width:200px' class="ZField" type="text" id="servername" name="server_name" value="<%=servername%>">
								<span class='ZHint'>(e.g. mail.company.com)</font></td>
							<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
							<td class="ZFieldLabel">Port:</td>
							<td width=100%><input style='width:50px' disabled="true" class="ZField" type="text" id="port" name="server_port" value="<%=port%>"
									>&nbsp;&nbsp;<a href="#" onclick="Ajax.enable('port');this.style.display='none';">Edit</a>
							<br><span class='ZHint'>(e.g. 80)</font> </td>
						</tr>
					</table>
				</td>
            </tr>

			<tr><td></td></tr>
            <tr>
                <td class='ZCheckboxCell'><input <%=checked%> type="checkbox" id="secured" name="server_secured" onclick="secureCheck();"></td>
                <td colspan=3 class="ZCheckboxLabel">Use an encrypted connection (SSL) when accessing this server</td>
            </tr>


			<tr><td colspan=4><hr></td></tr>

            <tr>
                <td class="ZFieldLabel">Synchronize with server every:</td>
                <td colspan=3><input style='width:50px' class="ZField" type="text" id="syncQuantity" name="sync_interval" value="<%=interval%>">
                    <select class="ZSelect" id="syncUnits" name="interval_unit">
                        <option <%=unit_sec_selected%>>seconds</option>
                        <option <%=unit_min_selected%>>minutes</option>
                    </select>
                </td>
            </tr>
        </table>


</form>

<table class="ZWizardButtonBar">
    <tr>
        <td class="ZWizardButton">
            <button class='DwtButton' onclick="deleteAccountWarning()">Remove Account...</button>
        </td>
        <td class="ZWizardButton">
            <button class='DwtButton' onclick="resetAccountWarning()">Reset Account...</button>
        </td>
        <td class="ZWizardButtonSpacer">
            <div></div>
        </td>
        <td class="ZWizardButton">
            <button class='DwtButton' onclick="Ajax.showPanel('console')">Cancel</button>
        </td>
        <td class="ZWizardButton">
            <button class='DwtButton' onclick="OnModify(update_account_<%=i%>)">Save Changes</button>
        </td>
</table>

</div>