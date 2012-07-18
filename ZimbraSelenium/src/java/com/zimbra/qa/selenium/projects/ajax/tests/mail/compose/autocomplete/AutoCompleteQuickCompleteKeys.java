package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose.autocomplete;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.AutocompleteEntry;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;


public class AutoCompleteQuickCompleteKeys extends PrefGroupMailByMessageTest {

	private String FirstName = null;
	private String LastName = null;
	private ZimbraAccount SampleAccount = null;
	
	
	public AutoCompleteQuickCompleteKeys() throws HarnessException {
		logger.info("New "+ AutoCompleteQuickCompleteKeys.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		super.startingAccountPreferences.put("zimbraPrefGalAutoCompleteEnabled", "TRUE");
		super.startingAccountPreferences.put("zimbraPrefAutoCompleteQuickCompletionOnComma", "TRUE");

		FirstName = "James" + ZimbraSeleniumProperties.getUniqueString();
		LastName = "Smith" + ZimbraSeleniumProperties.getUniqueString();
		SampleAccount = new ZimbraAccount();
		SampleAccount.setPref("displayName", FirstName + " " + LastName);
		SampleAccount.provision();
		SampleAccount.authenticate();

	}
	
	@Test(	description = "Type comma (',') to automatically accept autocomplete",
			groups = { "functional" })
	public void AutoCompleteQuickCompleteKeys_01() throws HarnessException {
		
		// Message properties
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);

		// Auto complete a name
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, FirstName);
		ZAssert.assertGreaterThan(entries.size(), 0, "Verify some results are returned");
		//app.zPageMail.zKeyboardTypeString(",");
		//workaround
		SleepUtil.sleepSmall();
		mailform.sKeyDown("css=div>input[id^=zv__COMPOSE][id$=_to_control]", "\\188");
		
		// Send the message
		mailform.zSubmit();

		
		// Log into the destination account and make sure the message is received
		MailItem received = MailItem.importFromSOAP(SampleAccount, "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
	}

	@Test(	description = "Type semicolon (';') to automatically accept autocomplete",
			groups = { "functional" })
	public void AutoCompleteQuickCompleteKeys_02() throws HarnessException {
		
		// Message properties
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);

		// Auto complete a name
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, FirstName);
		ZAssert.assertGreaterThan(entries.size(), 0, "Verify some results are returned");
		//app.zPageMail.zKeyboardTypeString(";");
		//workaround
		SleepUtil.sleepSmall();
		mailform.sKeyDown("css=div>input[id^=zv__COMPOSE][id$=_to_control]", "\\59");
		
		// Send the message
		mailform.zSubmit();

		
		// Log into the destination account and make sure the message is received
		MailItem received = MailItem.importFromSOAP(SampleAccount, "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
	}

	@Test(	description = "Type tab ('	') to automatically accept autocomplete",
			groups = { "functional" })
	public void AutoCompleteQuickCompleteKeys_03() throws HarnessException {
		
		// Message properties
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);

		// Auto complete a name
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, FirstName);
		ZAssert.assertGreaterThan(entries.size(), 0, "Verify some results are returned");
		//app.zPageMail.zKeyboardTypeString("	"); // Whitespace is 'tab' character
		//workaround
		SleepUtil.sleepSmall();
		mailform.sKeyDown("css=div>input[id^=zv__COMPOSE][id$=_to_control]", "\\9");
		
		// Send the message
		mailform.zSubmit();

		
		// Log into the destination account and make sure the message is received
		MailItem received = MailItem.importFromSOAP(SampleAccount, "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
	}


}
