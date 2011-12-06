package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.outofoffice;

import java.util.HashMap;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.DialogOOOAlert;

public class PromptToTurnOffAutoreply extends AjaxCommonTest {
	final String autoreplyMessage = "OOO";

	public PromptToTurnOffAutoreply() {
		logger.info("New " + PromptToTurnOffAutoreply.class.getCanonicalName());

		// test starts in the Briefcase tab
		super.startingPage = app.zPageBriefcase;

		// use an account with OOO auto-reply enabled
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put("zimbraPrefOutOfOfficeReplyEnabled", "TRUE");
				put("zimbraPrefOutOfOfficeReply", autoreplyMessage);
			}
		};
	}

	@Bugs(ids = "51990")
	@Test(description = "Enable auto-reply message - Verify after login  alert dialog promts to turn off auto-reply", groups = { "unctional" })
	public void PromptToTurnOffAutoreply_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		if (ZimbraSeleniumProperties.zimbraGetVersionString().contains("8.0.")) {
			DialogOOOAlert alert = new DialogOOOAlert(app,
					((AppAjaxClient) app).zPageBriefcase);

			ZAssert.assertTrue(alert.zIsActive(),
					"Verify turn off auto-reply alert dialog is displayed");

			alert.zCheckboxSet(true);
			
			alert.zClickButton(Button.B_NO);
		}

		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();

		// Send message to self
		account.soapSend("<SendMsgRequest xmlns='urn:zimbraMail'>" + "<m>"
				+ "<e t='t' a='" + account.EmailAddress + "'/>" + "<su>"
				+ subject + "</su>" + "<mp ct='text/plain'>"
				+ "<content>content"
				+ ZimbraSeleniumProperties.getUniqueString() + "</content>"
				+ "</mp>" + "</m>" + "</SendMsgRequest>");

		// Click on Mail tab
		app.zPageMain.zClickAt("id=zb__App__Mail_title", "0,0");

		// Click Get Mail button to view folder in list
		// app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		SleepUtil.sleepMedium();

		MailItem mailItem = MailItem.importFromSOAP(account, "in:inbox "
				+ autoreplyMessage);

		ZAssert.assertTrue(mailItem.dBodyText.contains(autoreplyMessage),
				"Verify auto-reply message is received");
	}
}
