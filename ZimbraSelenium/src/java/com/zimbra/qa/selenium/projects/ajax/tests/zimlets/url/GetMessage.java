package com.zimbra.qa.selenium.projects.ajax.tests.zimlets.url;

import java.io.File;
import java.util.*;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail.Field;


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
	
	@Test(	description = "Receive a mail with a basic URL",
			groups = { "smoke" })
	public void GetMessage_01() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String url = "http://www.vmware.com";
		String body = "text " + System.getProperty("line.separator") + url + System.getProperty("line.separator") + "text"+ ZimbraSeleniumProperties.getUniqueString() + System.getProperty("line.separator") ;
		
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
		HtmlElement bodyElement = display.zGetMailPropertyAsHtml(Field.Body);
		
		// Verify that the phone zimlet has been applied
		// <a href="http://www.vmware.com" target="_blank">http://www.vmware.com</a>
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url +"']", null, (String)null, 1);
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url +"']", "target", "_blank", 1);
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url +"']", null, url, 1);

	}


	@Test(	description = "Receive a mail with two URLs in body",
			groups = { "functional" })
	public void GetMessage_02() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String url1 = "http://www.vmware.com";
		String url2 = "http://www.google.com";
		String body = "url1: " + url1 + " url2: "+ url2;
		
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
		HtmlElement bodyElement = display.zGetMailPropertyAsHtml(Field.Body);
		
		// Verify that the phone zimlet has been applied
		// <a href="callto:1-877-486-9273" onclick="window.top.Com_Zimbra_Phone.unsetOnbeforeunload()">1-877-486-9273</a>
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url1 +"']", null, (String)null, 1);
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url1 +"']", "target", "_blank", 1);
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url1 +"']", null, url1, 1);
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url2 +"']", null, (String)null, 1);
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url2 +"']", "target", "_blank", 1);
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url2 +"']", null, url2, 1);

	}

	

	@Test(	description = "Validate the url zimlet matches valid URLs",
			groups = { "functional" })
	public void GetMessage_03() throws HarnessException {

		final String subject = "subject12955323015009";
		final String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/url01/valid_url.txt";
		final String url1 = "http://www.vmware.com";
		final String url2 = "https://www.vmware.com";
		
		// Inject the example message
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mime));

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Get all the messages in the inbox
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Wait for a bit so the zimlet can take affect
		SleepUtil.sleep(5000);
		
		// Get the HTML of the body
		HtmlElement bodyElement = display.zGetMailPropertyAsHtml(Field.Body);
		
		// Verify that the phone zimlet has been applied
		// <a href="callto:1-877-486-9273" onclick="window.top.Com_Zimbra_Phone.unsetOnbeforeunload()">1-877-486-9273</a>
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url1 +"']", null, (String)null, 1);
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url1 +"']", "target", "_blank", 1);
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url1 +"']", null, url1, 1);
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url2 +"']", null, (String)null, 1);
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url2 +"']", "target", "_blank", 1);
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url2 +"']", null, url2, 1);


	}


	@Test(	description = "Validate the url zimlet does not match invalid URLs",
			groups = { "functional" })
	public void GetMessage_04() throws HarnessException {

		final String subject = "subject12976223025009";
		final String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/url01/invalid_url.txt";
		
		// Inject the example message
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mime));

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Get all the messages in the inbox
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Wait for a bit so the zimlet can take affect
		SleepUtil.sleep(5000);
		
		// Get the HTML of the body
		HtmlElement bodyElement = display.zGetMailPropertyAsHtml(Field.Body);
		
		// TODO:
		// Not sure which URL's to add to the mime
		// Need to mine bugs for URLs that give issues
		// Add URLS to the sample mime and add verification points here.
		logger.info(bodyElement.prettyPrint());
		
	}


	@Test(	description = "Receive a mail with a url in subject",
			groups = { "functional" })
	public void GetMessage_05() throws HarnessException {
		
		// Create the message data to be sent
		String url = "http://www.vmware.com";
		String subject = "subject " + url;
		
		// Send the message from AccountA to the ZWC user
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Get all the messages in the inbox
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Wait for a bit so the zimlet can take affect
		SleepUtil.sleep(5000);
		
		// Find the subject and the phone span
		String locator = "css=span[id$='_com_zimbra_url']";
		
		ZAssert.assertTrue(display.sIsElementPresent(locator), "Verify the phone zimlet applies to the subject");
		ZAssert.assertEquals(display.sGetText(locator), url, "Verify the phone zimlet highlights the phone number");
		

	}

	@Bugs(ids = "29018,67927")
	@Test(	description = "Receive a mail with a URL in angled brackets",
			groups = { "functional" })
	public void GetMessage_06() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String url = "http://www.vmware.com";
		String body = "text &lt;" + url + "&gt; text "+ ZimbraSeleniumProperties.getUniqueString();
				
		
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
		HtmlElement bodyElement = display.zGetMailPropertyAsHtml(Field.Body);
		
		// Verify that the phone zimlet has been applied
		// <a href="http://www.vmware.com" target="_blank">http://www.vmware.com</a>
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url +"']", null, (String)null, 1);
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url +"']", "target", "_blank", 1);
		HtmlElement.evaluate(bodyElement, "//a[@href='"+ url +"']", null, url, 1);

	}

}
