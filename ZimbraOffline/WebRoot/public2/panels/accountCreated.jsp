<div id="accountCreated" class="ZWizardPage ZWizardPageBig">
    <div class="ZWizardPageTitle">Zimbra Account Set Up</div>

    <p>Your account "<%= param_account %>" has been successfully set up.
    </p>

    <p>Note that the first synchronization takes a while to run,
    	possibly hours if you have a lot of messages in the account.
    	However, you can start using this account right away.
    </p>
    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButton">
                <button class='DwtButton' onclick="Ajax.showPanel('accountType')">Set Up Another Account</button>
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
