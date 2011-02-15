package com.zimbra.qa.selenium.projects.ajax.tests.zimlets.phone;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;


public class GetMessage extends AjaxCommonTest {

	
	@SuppressWarnings("serial")
	public GetMessage() {
		logger.info("New "+ GetMessage.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Basic settings
		super.startingAccountPreferences = new HashMap<String, String>() {{
		    put("zimbraPrefGroupMailBy", "message");
		}};



	}
	
	@Test(	description = "Receive a mail with a basic US Phone number",
			groups = { "functional" })
	public void GetMessage_01() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String phonenumber = "1-877-486-9273";
		String body = "text " + System.getProperty("line.separator") + phonenumber + System.getProperty("line.separator") + "text"+ ZimbraSeleniumProperties.getUniqueString() + System.getProperty("line.separator") ;
		
		// Send the message from AccountA to the ZWC user
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ body +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Get all the messages in the inbox
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Wait for a bit so the zimlet can take affect
		SleepUtil.sleep(5000);
		
		// Get the HTML of the body
		HtmlElement bodyElement = display.zGetMailBodyIframe();
		
		// Verify that the phone zimlet has been applied
		// <a href="callto:1-877-486-9273" onclick="window.top.Com_Zimbra_Phone.unsetOnbeforeunload()">1-877-486-9273</a>
		HtmlElement.evaluate(bodyElement, "//a[@href='callto:1-877-486-9273']", null, (String)null, 1);
		HtmlElement.evaluate(bodyElement, "//a[@href='callto:1-877-486-9273']", "onclick", "window.top.Com_Zimbra_Phone.unsetOnbeforeunload()", 1);
		HtmlElement.evaluate(bodyElement, "//a[@href='callto:1-877-486-9273']", null, "1-877-486-9273", 1);

	}


}
