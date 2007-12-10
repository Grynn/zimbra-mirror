<div id="manageAccounts" class="ZWizardPage">
    <div class="ZWizardPageTitle">Manage Accounts   &nbsp;&nbsp; NOTE: THIS SCREEN IS NO LONGER NECESSARY</div>


    <% if (error != null) { %>
        <p class='ZOfflineError'><%= error %></p>
    <% }
    
       if (accounts == null || accounts.size() == 0) {
    %>
		<p class='ZOfflineNotice'>Press Add New Account to set up a Zimbra account.</p>
		<br><br>
    <% } else {
            for (int i = 0; i < accounts.size(); ++i) {
                Account account = accounts.get(i);
                String server = account.getAttr(OFFLINE_REMOTE_SERVER);
                String username = account.getName();
    %>
    <div class="ZWizardPageTitle"><%=server%> --&gt; <%=username%></div>


        <table class="ZWizardForm" cellpadding=5 style='margin-left:20px;'>
            <tr>
                <td valign=top>
                    <button class='DwtButton' class='DwtButton' onclick="Ajax.showPanel('changeAccount_<%=i%>')" style='width:140px'>
                        <nobr>Change Account Setup</nobr>
                    </button>
                </td>
                <td>Change account setup (password, check mail interval, etc)</td>
            </tr>
            <tr>
                <td valign=top>
                    <button class='DwtButton' class='DwtButton' onclick="OnReset('<%=username%>', '<%=server%>')" style='width:100%'>
                        <nobr>Reset Desktop Account</nobr>
                    </button>
                </td>
                <td>Clear all local mail data and resynchronize with the server.
                </td>
            </tr>
            <tr>
                <td valign=top>
                    <button class='DwtButton' class='DwtButton' onclick="OnDelete('<%=username%>', '<%=server%>')" style='width:100%'>
                        <nobr>Delete Desktop Account</nobr>
                    </button>
                </td>
                <td>Delete all local mail data and login information.
                    You can still access this account through your web browser.
                    The next time you use Zimbra Desktop, the setup wizard will prompt you to set up another account.
                </td>
            </tr>
        </table>

    <% } %>
    <% } %>

    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button class='DwtButton' class='DwtButton' onclick="showSetupPage()">Back</button>
            </td>

            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button class='DwtButton' class='DwtButton' onclick="Ajax.showPanel('newAccount')">Add New Account</button>
            </td>
    </table>
</div>