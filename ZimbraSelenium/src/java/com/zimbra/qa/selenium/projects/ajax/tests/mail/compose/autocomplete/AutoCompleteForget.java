package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose.autocomplete;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;


public class AutoCompleteForget extends PrefGroupMailByMessageTest {


	
	
	public AutoCompleteForget() {
		logger.info("New "+ AutoCompleteForget.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		super.startingAccountPreferences.put("zimbraPrefAutoAddAddressEnabled", "FALSE");
	
	}
	
	
	/**
	 * Steps:
	 * 1. Compose a message to a new email address (i.e. non-contact)
	 * 2. Send
	 * 3. Compose a message to the same email address
	 * 4. Forget the auto-complete address
	 * 5. Compose a message to the same email address
	 * 6. Verify the auto-complete does not suggest the address
	 * 
	 * 
	 * @throws HarnessException
	 */
	@Test(	description = "Forget an autocomplete address - invalid email",
			groups = { "functional" })
	public void AutoCompleteForget_01() throws HarnessException {
		
		// Create a contact
		String emailaddress = "foo"+ ZimbraSeleniumProperties.getUniqueString() + "@"+ ZimbraSeleniumProperties.getStringProperty("testdomain", "testdomain.com");

		// Send a message to the invalid email
		
		// Message properties
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();

		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);
		mailform.zFillField(Field.To, emailaddress);
		mailform.zSubmit();

		
		// Open the new mail form
		mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);

		// Auto complete a name
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, emailaddress.substring(0, emailaddress.indexOf('@')));
		AutocompleteEntry found = null;
		for (AutocompleteEntry entry : entries) {
			if ( entry.getAddress().contains(emailaddress) ) {
				found = entry;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
		
		// Forget this address
		mailform.zAutocompleteForgetItem(found);
		
		// Cancel the compose
		DialogWarning dialog = (DialogWarning)mailform.zToolbarPressButton(Button.B_CANCEL);
		if ( dialog.zIsActive() ) {
			dialog.zClickButton(Button.B_NO);
		}

		
		// Open the new mail form
		mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);

		// Auto complete a name
		entries = mailform.zAutocompleteFillField(Field.To, emailaddress.substring(0, emailaddress.indexOf('@')));
		found = null;
		for (AutocompleteEntry entry : entries) {
			if ( entry.getAddress().contains(emailaddress) ) {
				found = entry;
				break;
			}
		}
		ZAssert.assertNull(found, "Verify the autocomplete does not return the same email");

		// Cancel the compose
		dialog = (DialogWarning)mailform.zToolbarPressButton(Button.B_CANCEL);
		if ( dialog.zIsActive() ) {
			dialog.zClickButton(Button.B_NO);
		}

		
	}


}
