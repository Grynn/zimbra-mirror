<div id="manageServices" class="ZWizardPage">
    <div class="ZWizardPageTitle">Manage Services  &nbsp;&nbsp; NOTE: THIS SCREEN IS NO LONGER NECESSARY</div>



    <% if (error != null) { %>
        <p><font color="red"><%= error %></font></p>
    <% } else { %>
        <p>&nbsp;</p>
    <% } %>


    <%
        if (dataSources != null && dataSources.size() > 0) {
            for (int i = 0; i < dataSources.size(); ++i) {
                DataSource ds = dataSources.get(i);
                String service = ds.getName();
                String username = ds.getUsername();
    %>
    <div class="ZWizardPageTitle"><%=service%> --&gt; <%=username%></div>


        <table class="ZWizardForm" cellpadding=5 style='margin-left:20px;'>
            <tr>
                <td valign=top>
                    <button class='DwtButton' onclick="showChangeScreen(<%=i%>)" style='width:140px'>
                        <nobr>Change Service Setup</nobr>
                    </button>
                </td>
                <td>Change service setup (password, check mail interval, etc)</td>
            </tr>
            <tr>
                <td valign=top>
                    <button class='DwtButton' onclick="OnDelete('<%=service%>')" style='width:100%'>
                        <nobr>Delete Service</nobr>
                    </button>
                </td>
                <td>Delete service setup information. Your already downloaded data as well as data on the server will
                    not be affected.
                </td>
            </tr>
        </table>

    <% } %>
    <% } else { %>

    <p>No service has been provisioned</p>

    <% } %>

    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button class='DwtButton' onclick="showSetupPage()">Back</button>
            </td>


            <!-- td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button class='DwtButton' onclick="showSmtpScreen()">Config Default SMTP</button>
            </td-->

            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button class='DwtButton' onclick="showNewScreen()">Add New Service</button>
            </td>
    </table>
</div>
