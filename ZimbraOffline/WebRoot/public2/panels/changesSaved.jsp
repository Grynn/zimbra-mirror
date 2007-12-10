<div id="changesSaved" class="ZWizardPage">
<div class="ZWizardPageTitle">Change Settings</div>

<% if (error != null) { %>
<p><font color="red"><%= error %>
</font></p>
<% } else if (act != null && act.equals("modify")) { %>
<p><font color="blue">Desktop mailbox settings have been updated.</font></p>
<% } else { %>
<p>What do you want to change?</p>
<% } %>


<table class="ZWizardButtonBar">
    <tr>
        <td class="ZWizardButtonSpacer">
            <div></div>
        </td>
        <td class="ZWizardButton">
            <button class='DwtButton' onclick="showManageAccount()">Back</button>
        </td>
        <td class="ZWizardButton">
            <button class='DwtButton' onclick="OnModify()">Save Changes</button>
        </td>
</table>

</div>
