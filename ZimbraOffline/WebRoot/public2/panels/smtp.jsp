<div id="smtp" class="ZWizardPage">
<div class="ZWizardPageTitle">
    Default SMTP Settings
</div>

<% if (error != null) { %>
<p><font color="red"><%= error %></font></p>
<% } %>

<p>Configure the SMTP server for sending outgoing messages</p>

<form name="smtp_form" action="<%=LOCALHOST_THIS_URL%>" method="POST">

    <input type="hidden" name="act" value="smtp">

    <table class="ZWizardForm">
        <tr>
            <td class="ZFieldLabel">SMTP host:</td>
            <td><input style='width:200px' class="ZField" type="text" id="smtp_host" name="smtp_host"
                       value="<%=param_smtp_host%>"> <span class='ZHint'>(e.g. smtp.company.com)</span></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">SMTP port:</td>
            <td><input style='width:50px' class="ZField" type="text" id="smtp_port" name="smtp_port"
                       value="<%=param_smtp_port%>"> <span class='ZHint'>(e.g. 25)</span></td>
        </tr>
        <tr>
            <td class="ZFieldLable">Use an encrypted connection (SSL) when accessing this server</td>
            <td><input class="ZField" type="checkbox" id="smtp_secure" name="smtp_secure" <%=smtp_ssl_checked%>></td>
        </tr>
        <tr>
            <td class="ZFieldLable">SMTP authentication required:</td>
            <td><input class="ZField" type="checkbox" id="smtp_auth" name="smtp_auth" <%=smtp_auth_checked%>></td>
        </tr>

        <tr>
            <td class="ZFieldLabel">Authentication username:</td>
            <td><input style='width:200px' class="ZField" type="text" id="smtp_user" name="smtp_user"
                       value="<%=param_smtp_user%>"></td>
        </tr>
        <tr>
            <td class="ZFieldLabel">Authentication password:</td>
            <td><input style='width:100px' class="ZField" type="password" id="smtp_user" name="smtp_pass"
                       value="<%=param_smtp_pass%>"></td>
        </tr>
    </table>

</form>

<table class="ZWizardButtonBar">
    <tr>
        <td class="ZWizardButtonSpacer">
            <div></div>
        </td>
        <td class="ZWizardButton">
            <button class='DwtButton' onclick="Ajax.showPanel('manageServices')">Back</button>
        </td>
        <td class="ZWizardButton">
            <button class='DwtButton' onclick="OnSmtp()">Save</button>
        </td>
</table>
</div>
