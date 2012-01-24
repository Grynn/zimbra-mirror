package com.zimbra.qa.selenium.projects.ajax.tests.zimlets.date;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

import org.testng.annotations.*;

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
	
	@Test(	description = "Receive a mail with a basic date",
			groups = { "smoke" })
	public void GetMessage_01() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String date = "12/25/2014";
		String body = "text " + date + " text";
		
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
		//
		// <span id="OBJ_PREFIX_DWT36_com_zimbra_date" class="Object">
		//  <span id="OBJ_PREFIX_DWT37_com_zimbra_date" class="Object">
		//    12/25/2014
		//  </span>
		// </span>
		//
		HtmlElement.evaluate(bodyElement, "//span//span//span", "id", Pattern.compile(".*_com_zimbra_date"), 1);
		HtmlElement.evaluate(bodyElement, "//span//span//span", null, date, 1);

	}


	@Test(	description = "Receive a mail with two dates in body",
			groups = { "functional" })
	public void GetMessage_02() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String date1 = "12/25/2014";
		String date2 = "1/1/2015";
		String body = "date1: " + date1 + " date2: "+ date2;
		
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
		
		// Verify that the date zimlet has been applied
		HtmlElement.evaluate(bodyElement, "//span//span//span", null, date1, 1);
		HtmlElement.evaluate(bodyElement, "//span//span//span", null, date2, 1);

	}

	

	@Test(	description = "Validate the date zimlet matches valid dates",
			groups = { "functional" })
	public void GetMessage_03() throws HarnessException {

		final String subject = "subject12912323015009";
		final String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/date01/en_us_valid_dates.txt";
		
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
		
		// See the mime file
		List<String> dates = new ArrayList<String>();
		dates.add("1/1/2014");
		dates.add("1/31/2014");
		dates.add("10/1/2014");
		dates.add("10/31/2014");

		for (String date : dates) {
			// Verify that the phone zimlet has been applied
			HtmlElement.evaluate(bodyElement, "//span//span//span", null, date, 1);
		}


	}


	@Test(	description = "Validate the date zimlet does not match invalid dates",
			groups = { "functional" })
	public void GetMessage_04() throws HarnessException {

		final String subject = "subject1293323025009";
		final String mime = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/date01/en_us_invalid_dates.txt";
		
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
		

		// No spans should be present in the mime
		HtmlElement.evaluate(bodyElement, "//span//span", null, (String)null, 0);
		
	}


	@Test(	description = "Receive a mail with a date in subject",
			groups = { "functional" })
	public void GetMessage_05() throws HarnessException {
		
		// Create the message data to be sent
		String date = "12/25/2016";
		String subject = "subject " + date;
		
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
		String locator = "css=span[id$='_com_zimbra_date']";
		
		ZAssert.assertTrue(display.sIsElementPresent(locator), "Verify the date zimlet applies to the subject");
		ZAssert.assertEquals(display.sGetText(locator), date, "Verify the date zimlet highlights the date");
		

	}


}
