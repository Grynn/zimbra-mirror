<div id="accountType" class="ZWizardPage">
    <div class="ZWizardPageTitle">What type of account do you want to set up?</div>


    <table cellpadding=10 style='margin-left:20px;'>
        <tr>
            <td valign=top width=200px>
                <button class='DwtButton' onclick="showAccountsPage()" style='width:100%'>
                    <nobr>Zimbra Account</nobr>
                </button>
            </td>
            <td>Set up an account on a Zimbra mail server.
            </td>
        </tr>
        <tr>
            <td valign=top>
                <button class='DwtButton-disabled' onclick="alert('Not yet working')" style='width:100%' disabled>
                    <nobr>Zimbra Test Drive</nobr>
                </button>
            </td>
            <td>(Coming Soon) Set up a two-week Zimbra Test Drive Account.
            </td>
        </tr>
        <tr>
            <td valign=top>
                <button class='DwtButton' onclick="showYahooPage()" style='width:100%'>
                    <nobr>Yahoo! Mail Plus Account</nobr>
                </button>
            </td>
            <td>Note: you need a <a href="javascript:Ajax.toggle('YahooMailPlus')">Yahoo! Mail Plus account</a>
            	for this to work.
            </td>
        </tr>
		<tr id='YahooMailPlus' style='display:none'>
			<td colspan=2>
				<div class='infoBox' style='margin:0px 30px 0px 30px;'>
					<div class='infoTitle'>About Yahoo! Mail Plus</div>
			
					<p>Blah blah Y!Mail Plus blah blah.  <a href=http://www.yahoo.com target=_blank>Go there</a></p>
					
					<a href="javascript:Ajax.toggle('YahooMailPlus')">Done</a>
				</div>
			</td>
		</tr>

        <tr>
            <td valign=top>
                <button class='DwtButton' onclick="showGmailPage()" style='width:100%'>
                    <nobr>Gmail Account</nobr>
                </button>
            </td>
            <td>Note: your Gmail account <a href="javascript:Ajax.toggle('Gmail')">must allow IMAP access</a>.</td>
        </tr>

		<tr id='Gmail' style='display:none'>
			<td colspan=2>
				<div class='infoBox' style='margin:0px 30px 0px 30px;'>
					<div class='infoTitle'>To allow IMAP access from your Gmail account</div>
			
						<ol>
							<li>Log in to your <a href=http://gmail.com target=_blank>Gmail account</a>.
							<li>Click <b>Settings</b> at the top of any Gmail page.
							<li>Click <b>Forwarding and POP/IMAP</b>.
							<li>Select <b>Enable IMAP</b>.
							<li>Click <b>Save Changes</b>.
							<li>Close Gmail, come back to this screen and click the <b>Gmail Account</b> button to above.
						</ol>

					<a href="javascript:Ajax.toggle('Gmail')">Done</a>
				</div>
			</td>		
		</tr>

        <tr>
            <td valign=top>
                <button class='DwtButton' onclick="showHotmailPage()" style='width:100%'>
                    <nobr>Windows Live Hotmail Plus Account</nobr>
                </button>
            </td>
            <td>Note: you must have a <a href="javascript:Ajax.toggle('Hotmail')">Windows Live Hotmail Plus account</a> for this to work.</td>            		
            </td>
        </tr>

		<tr id='Hotmail' style='display:none'>
			<td colspan=2>
				<div class='infoBox' style='margin:0px 30px 0px 30px;'>
					<div class='infoTitle'>Hotmail info</div>
			
						Blah blah blah.

					<a href="javascript:Ajax.toggle('Hotmail')">Done</a>
				</div>
			</td>		
		</tr>

        <tr>
            <td valign=top>
                <button class='DwtButton' onclick="showServicesPage()" style='width:100%'>
                    <nobr>Other POP/IMAP Account</nobr>
                </button>
            </td>
            <td>Set up an account on an other POP or IMAP mail server,
            		for example, an account from your Internet Service Provider.
            </td>
        </tr>
    </table>

    <table class="ZWizardButtonBar">
        <tr>
            <td class="ZWizardButtonSpacer">
                <div></div>
            </td>
            <td class="ZWizardButton">
                <button class='DwtButton' onclick="Ajax.showPanel('console')">Cancel</button>
            </td>
    </table>
</div>
