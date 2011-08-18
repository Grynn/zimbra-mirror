package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.trustedaddresses;

import java.io.File;
import java.util.HashMap;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.LmtpInject;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.trustedaddresses.DisplayTrustedAddress;

public class TrustedEmailAddrMsgView extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public TrustedEmailAddrMsgView() throws HarnessException {
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {
			{
				put("zimbraPrefGroupMailBy", "message");
				put("zimbraPrefMessageViewHtmlPreferred", "TRUE");
				put("zimbraPrefMailTrustedSenderList", "admintest@testdoamin.com");
			}
		};
	}

	/**
	 * TestCase : Trusted Email address with message view
	 * 1.Set Email address in Preference/Mail/Trusted Addresses
	 * 	Verify email addr through soap- GetPrefsRequest
	 * 2.In message View Inject mail from same email id with external image
	 * 3.Verify To,From,Subject through soap 
	 * 4.Click on same mail
	 * 5.Yellow color Warning Msg Info bar should not present for trusted eamil address.
	 * @throws HarnessException
	 */
	@Test(description = "Verify Display Image link in Trusted Addresses for message view", groups = { "smoke" })
	public void TrustedEmailAddrMsgView_01() throws HarnessException {

		final String subject = "TestTrustedAddress";
		final String from = "admintest@testdoamin.com";
		final String to = "admin@testdoamin.com";
		final String mimeFolder = ZimbraSeleniumProperties.getBaseDirectory()
		+ "/data/public/mime/ExternalImg.txt";

		// Verify Email id through soap GetPrefsRequest
		String PrefMailTrustedAddr = ZimbraAccount.AccountZWC().getPreference(
				"zimbraPrefMailTrustedSenderList");
		ZAssert.assertTrue(PrefMailTrustedAddr
				.equals("admintest@testdoamin.com"),
				"Verify Email address is present /Pref/TrustedAddr");

		// Inject the external image message(s)
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFolder));

		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(),subject);

		ZAssert.assertNotNull(mail, "Verify message is received");
		ZAssert.assertEquals(from, mail.dFromRecipient.dEmailAddress,"Verify the from matches");
		ZAssert.assertEquals(to, mail.dToRecipients.get(0).dEmailAddress,"Verify the to address");
				
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		DisplayTrustedAddress actual = new DisplayTrustedAddress(app);

		// Verify Warning info bar with other links

		ZAssert
		.assertFalse(actual.zHasWDDLinks("message"),
		"Verify Warning icon ,Display Image and Domain link  does not present");

	}

}
