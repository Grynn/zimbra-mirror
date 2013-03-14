package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose.autocomplete;

import java.util.List;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;


public class AutoCompleteSearchTerms extends PrefGroupMailByMessageTest {

	
	
	public AutoCompleteSearchTerms() throws HarnessException {
		logger.info("New "+ AutoCompleteGAL.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		super.startingAccountPreferences.put("zimbraPrefGalAutoCompleteEnabled", "TRUE");
	
	}
	
	@Bugs(	ids = "46718")
	@Test(	description = "Autocomplete using stop word - IT",
			groups = { "functional" })
	public void AutCompleteSearchTerms_01() throws HarnessException {
		
		// See bug 46718
		String StopWordsFirstName = "It" + ZimbraSeleniumProperties.getUniqueString();
		String StopWordsLastName = "Be" + ZimbraSeleniumProperties.getUniqueString();


		ZimbraAccount StopWordsAccount = new ZimbraAccount();
		StopWordsAccount.setPref("givenName", StopWordsFirstName);
		StopWordsAccount.setPref("sn", StopWordsLastName);
		StopWordsAccount.setPref("displayName", StopWordsFirstName + " " + StopWordsLastName);
		StopWordsAccount.provision();
		StopWordsAccount.authenticate();


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
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, "It");
		AutocompleteEntry found = null;
		for (AutocompleteEntry entry : entries) {
			if ( entry.getAddress().contains(StopWordsAccount.EmailAddress) ) {
				found = entry;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
		mailform.zAutocompleteSelectItem(found);
		
		// Send the message
		mailform.zSubmit();

		
		// Log into the destination account and make sure the message is received
		MailItem received = MailItem.importFromSOAP(StopWordsAccount, "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
	}


	@Bugs(	ids = "46950")
	@Test(	description = "Autocomplete using search term - And",
			groups = { "functional" })
	public void AutCompleteSearchTerms_02() throws HarnessException {
		
		// See bug 46950
		String QueryWordsFirstName = "Andrew" + ZimbraSeleniumProperties.getUniqueString();
		String QueryWordsLastName = "Subject" + ZimbraSeleniumProperties.getUniqueString();

		ZimbraAccount QueryWordsAccount = new ZimbraAccount();
		QueryWordsAccount.setPref("givenName", QueryWordsFirstName);
		QueryWordsAccount.setPref("sn", QueryWordsLastName);
		QueryWordsAccount.setPref("displayName", QueryWordsFirstName + " " + QueryWordsLastName);
		QueryWordsAccount.provision();
		QueryWordsAccount.authenticate();


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
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, "And");
		AutocompleteEntry found = null;
		for (AutocompleteEntry entry : entries) {
			if ( entry.getAddress().contains(QueryWordsAccount.EmailAddress) ) {
				found = entry;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
		mailform.zAutocompleteSelectItem(found);
		
		// Send the message
		mailform.zSubmit();

		
		// Log into the destination account and make sure the message is received
		MailItem received = MailItem.importFromSOAP(QueryWordsAccount, "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
	}


}
