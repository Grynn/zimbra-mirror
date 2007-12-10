<div id="welcome" class='ZWizardPage ZWizardPageBig'>
    <div class='ZWizardPageTitle'>
        Welcome to the Zimbra Desktop setup wizard!
    </div>

    <p>Zimbra Desktop allows you to access your email while your computer 
    	is disconnected from the internet.

    </p>

    <p>To use Zimbra Desktop, you must first enter settings for an existing mail account.  </p>

	<p> 	You must be online to setup the account -- if you are not online now, 
    	please quit and launch the application again later when you are connected.
    </p>

<!--
    <p> Note: In order to download your email, we must store email messages and login
        information on your computer. 
        
        To prevent someone from accessing your email, please verify that your computer requires a password for access. 
        <a href="javascript:Ajax.togglePlatformNotice('secureSetup')">How do I do this?</a>
    </p>

    <div id='secureSetup-Win' class='infoBox' style='display:none'>
        <div class='infoTitle'>For maximum security, follow all of the guidelines below.</div>

        <p>On Windows:
        <ol>
            <li> Launch Start -> Control Panel.
            <li> Make sure you have a reasonable password on the account
            <li> Require the password to log in
            <li> Require password to resume from hibernation/standby
            <li> Require password to unlock screen saver
        </ol>
        <a href="javascript:Ajax.togglePlatformNotice('secureSetup')">Done</a>
    </div>

    <div id='secureSetup-Mac' class='infoBox' style='display:none'>
        <div class='infoTitle'>For maximum security, follow all of the guidelines below.</div>
        <ol>
            <li> Launch "System Preferences".
            <li> Choose the "Accounts" icon and ensure you have a reasonable passsword on the account
            <li> Choose the "Security" icon and:
            <ol>
                <li> Check the options:
                    <br>
                    [] Require password to wake this computer from sleep or screen saver
                    <br>
                    [] Log out after [X] minutes of inactivity

                <li> Uncheck the options:
                    <br>
                    [] Disable automatic login
            </ol>
        </ol>

        <a href="javascript:Ajax.togglePlatformNotice('secureSetup')">Done</a>
    </div>
-->    
    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button class='DwtButton-focused' onclick="Ajax.showPanel('accountType')">Set Up an Account</button>
            </td>
    </table>
</div>
