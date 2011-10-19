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

	@Test(	description = "Verify the 'forget' link for Contacts",
			groups = { "functional" })
	public void AutoCompleteForget_02() throws HarnessException {
		
		// Create two contacts
		String emailaddress = "admin@zqa-061.eng.vmware.com";
		String firstname = "Michael" + ZimbraSeleniumProperties.getUniqueString();
		String lastname = "Williams" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
					"<CreateContactRequest xmlns='urn:zimbraMail'>"
				+		"<cn>"
				+			"<a n='firstName'>"+ firstname +"</a>"
				+			"<a n='lastName'>"+ lastname +"</a>"
				+			"<a n='email'>"+ emailaddress +"</a>"
				+		"</cn>"
				+	"</CreateContactRequest>");
		
		app.zGetActiveAccount().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>"
			+		"<m>"
			+			"<e t='t' a='"+ emailaddress +"'/>"
			+			"<su>subject"+ ZimbraSeleniumProperties.getUniqueString() +"</su>"
			+			"<mp ct='text/plain'>"
			+				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>"
			+			"</mp>"
			+		"</m>"
			+	"</SendMsgRequest>");

		app.zPageMain.zToolbarPressButton(Button.B_REFRESH);

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
		
		// Forget the item
		mailform.zAutocompleteForgetItem(found);
		
		// Cancel the compose
		DialogWarning dialog = (DialogWarning)mailform.zToolbarPressButton(Button.B_CANCEL);
		if ( dialog.zIsActive() ) {
			dialog.zClickButton(Button.B_NO);
		}

		
	}

	@Test(	description = "Verify the 'forget' link for GAL",
			groups = { "functional" })
	public void AutoCompleteForget_03() throws HarnessException {
		
		// Create a GAL Entry
		ZimbraAccount account = new ZimbraAccount();
		account.setPref("givenName", "Christopher" + ZimbraSeleniumProperties.getUniqueString());
		account.setPref("sn", "White" + ZimbraSeleniumProperties.getUniqueString());
		account.setPref("displayName", account.getPref("givenName") + " " + account.getPref("sn"));
		account.provision();
		account.authenticate();
		
		app.zGetActiveAccount().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>"
			+		"<m>"
			+			"<e t='t' a='"+ account.EmailAddress +"'/>"
			+			"<su>subject"+ ZimbraSeleniumProperties.getUniqueString() +"</su>"
			+			"<mp ct='text/plain'>"
			+				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>"
			+			"</mp>"
			+		"</m>"
			+	"</SendMsgRequest>");

		app.zPageMain.zToolbarPressButton(Button.B_REFRESH);

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
		
		// Auto complete a name
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, account.getPref("givenName"));
		AutocompleteEntry found = null;
		for (AutocompleteEntry entry : entries) {
			if ( entry.getAddress().contains(account.EmailAddress) ) {
				found = entry;
				break;
			}
		}		
		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
		
		// Forget the item
		mailform.zAutocompleteForgetItem(found);
		
		// Cancel the compose
		DialogWarning dialog = (DialogWarning)mailform.zToolbarPressButton(Button.B_CANCEL);
		if ( dialog.zIsActive() ) {
			dialog.zClickButton(Button.B_NO);
		}
		
	}

	
	/**
	 * 1. Create two contacts, Acontact and Bcontact
	 * 2. Send one message to Acontact
	 * 3. Send two messages to Bcontact
	 * 4. Compose new message, verify Bcontact comes before Acontact
	 * 5. Forget Bcontact
	 * 6. Compose new message, verify Acontact comes before Bcontact
	 * @throws HarnessException
	 */
	@Test(	description = "Verify 'forget' functionality resets the ranking order - Contacts",
			groups = { "functional" })
	public void AutoCompleteForget_04() throws HarnessException {
		
		// Create two contacts
		String emailaddress1 = "email"+ ZimbraSeleniumProperties.getUniqueString() + "@"+ ZimbraSeleniumProperties.getStringProperty("testdomain", "testdomain.com");
		String firstname1 = "Paul" + ZimbraSeleniumProperties.getUniqueString();
		String lastname1 = "Harris" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
					"<CreateContactRequest xmlns='urn:zimbraMail'>"
				+		"<cn>"
				+			"<a n='firstName'>"+ firstname1 +"</a>"
				+			"<a n='lastName'>"+ lastname1 +"</a>"
				+			"<a n='email'>"+ emailaddress1 +"</a>"
				+		"</cn>"
				+	"</CreateContactRequest>");
		
		// Send one message
		app.zGetActiveAccount().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>"
			+		"<m>"
			+			"<e t='t' a='"+ emailaddress1 +"'/>"
			+			"<su>subject"+ ZimbraSeleniumProperties.getUniqueString() +"</su>"
			+			"<mp ct='text/plain'>"
			+				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>"
			+			"</mp>"
			+		"</m>"
			+	"</SendMsgRequest>");

		
		// Create a second contact
		String emailaddress2 = "email"+ ZimbraSeleniumProperties.getUniqueString() + "@"+ ZimbraSeleniumProperties.getStringProperty("testdomain", "testdomain.com");
		String firstname2 = "Paul" + ZimbraSeleniumProperties.getUniqueString();
		String lastname2 = "Harris" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
					"<CreateContactRequest xmlns='urn:zimbraMail'>"
				+		"<cn>"
				+			"<a n='firstName'>"+ firstname2 +"</a>"
				+			"<a n='lastName'>"+ lastname2 +"</a>"
				+			"<a n='email'>"+ emailaddress2 +"</a>"
				+		"</cn>"
				+	"</CreateContactRequest>");
		
		// Send two messages
		app.zGetActiveAccount().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>"
			+		"<m>"
			+			"<e t='t' a='"+ emailaddress2 +"'/>"
			+			"<su>subject"+ ZimbraSeleniumProperties.getUniqueString() +"</su>"
			+			"<mp ct='text/plain'>"
			+				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>"
			+			"</mp>"
			+		"</m>"
			+	"</SendMsgRequest>");

		app.zGetActiveAccount().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>"
			+		"<m>"
			+			"<e t='t' a='"+ emailaddress2 +"'/>"
			+			"<su>subject"+ ZimbraSeleniumProperties.getUniqueString() +"</su>"
			+			"<mp ct='text/plain'>"
			+				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>"
			+			"</mp>"
			+		"</m>"
			+	"</SendMsgRequest>");

		
		app.zPageMain.zToolbarPressButton(Button.B_REFRESH);

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

		
		// Auto complete a name
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, "Paul");
		ZAssert.assertNotNull(entries, "Verify the autocomplete entry exists in the returned list");
		
		// Determine the ranking
		int index1 = 0;
		int index2 = 0;
		AutocompleteEntry found2 = null;
		for (int i = 0; i < entries.size(); i++) {
			if ( entries.get(i).getAddress().contains(emailaddress1) ) {
				index1 = i;
			}
			if ( entries.get(i).getAddress().contains(emailaddress2) ) {
				index2 = i;
				found2 = entries.get(i);
			}
		}
		
		ZAssert.assertLessThan(index2, index1, "Verify that Contact2 (two messages) is ranked higher than Contact1 (one message)");
		
		// Forget contact2
		mailform.zAutocompleteForgetItem(found2);
						
		// Cancel the compose
		DialogWarning dialog = (DialogWarning)mailform.zToolbarPressButton(Button.B_CANCEL);
		if ( dialog.zIsActive() ) {
			dialog.zClickButton(Button.B_NO);
		}

		
		
		// Compose again, verify contact1 (one message) is higher than contact2 (forgotten)
		// Open the new mail form
		mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);

		
		// Auto complete a name
		entries = mailform.zAutocompleteFillField(Field.To, "Paul");
		ZAssert.assertNotNull(entries, "Verify the autocomplete entry exists in the returned list");
		
		// Determine the ranking
		index1 = 0;
		index2 = 0;
		found2 = null;
		for (int i = 0; i < entries.size(); i++) {
			if ( entries.get(i).getAddress().contains(emailaddress1) ) {
				index1 = i;
			}
			if ( entries.get(i).getAddress().contains(emailaddress2) ) {
				index2 = i;
				found2 = entries.get(i);
			}
		}
		
		ZAssert.assertLessThan(index1, index2, "Verify that Contact1 (one message) is ranked higher than Contact2 (forgotten)");
		
		// Cancel the compose
		dialog = (DialogWarning)mailform.zToolbarPressButton(Button.B_CANCEL);
		if ( dialog.zIsActive() ) {
			dialog.zClickButton(Button.B_NO);
		}

	}

	/**
	 * 1. Create two GAL accounts, Acontact and Bcontact
	 * 2. Send one message to Acontact
	 * 3. Send two messages to Bcontact
	 * 4. Compose new message, verify Bcontact comes before Acontact
	 * 5. Forget Bcontact
	 * 6. Compose new message, verify Acontact comes before Bcontact
	 * @throws HarnessException
	 */
	@Test(	description = "Verify 'forget' functionality resets the ranking order - GAL",
			groups = { "functional" })
	public void AutoCompleteForget_05() throws HarnessException {
		
		// Create a GAL Entry
		ZimbraAccount account1 = new ZimbraAccount();
		account1.setPref("givenName", "Mark" + ZimbraSeleniumProperties.getUniqueString());
		account1.setPref("sn", "Martin" + ZimbraSeleniumProperties.getUniqueString());
		account1.setPref("displayName", account1.getPref("givenName") + " " + account1.getPref("sn"));
		account1.provision();
		account1.authenticate();
		
		// Send one message
		app.zGetActiveAccount().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>"
			+		"<m>"
			+			"<e t='t' a='"+ account1.EmailAddress +"'/>"
			+			"<su>subject"+ ZimbraSeleniumProperties.getUniqueString() +"</su>"
			+			"<mp ct='text/plain'>"
			+				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>"
			+			"</mp>"
			+		"</m>"
			+	"</SendMsgRequest>");

		ZimbraAccount account2 = new ZimbraAccount();
		account2.setPref("givenName", "Mark" + ZimbraSeleniumProperties.getUniqueString());
		account2.setPref("sn", "Martin" + ZimbraSeleniumProperties.getUniqueString());
		account2.setPref("displayName", account2.getPref("givenName") + " " + account2.getPref("sn"));
		account2.provision();
		account2.authenticate();
		
		// Send two messages
		app.zGetActiveAccount().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>"
			+		"<m>"
			+			"<e t='t' a='"+ account2.EmailAddress +"'/>"
			+			"<su>subject"+ ZimbraSeleniumProperties.getUniqueString() +"</su>"
			+			"<mp ct='text/plain'>"
			+				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>"
			+			"</mp>"
			+		"</m>"
			+	"</SendMsgRequest>");
		
		app.zGetActiveAccount().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>"
			+		"<m>"
			+			"<e t='t' a='"+ account2.EmailAddress +"'/>"
			+			"<su>subject"+ ZimbraSeleniumProperties.getUniqueString() +"</su>"
			+			"<mp ct='text/plain'>"
			+				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>"
			+			"</mp>"
			+		"</m>"
			+	"</SendMsgRequest>");

		app.zPageMain.zToolbarPressButton(Button.B_REFRESH);

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

		
		// Auto complete a name
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, "Mark");
		ZAssert.assertNotNull(entries, "Verify the autocomplete entry exists in the returned list");
		
		// Determine the ranking
		int index1 = 0;
		int index2 = 0;
		AutocompleteEntry found2 = null;
		for (int i = 0; i < entries.size(); i++) {
			if ( entries.get(i).getAddress().contains(account1.EmailAddress) ) {
				index1 = i;
			}
			if ( entries.get(i).getAddress().contains(account2.EmailAddress) ) {
				index2 = i;
				found2 = entries.get(i);
			}
		}
		
		ZAssert.assertLessThan(index2, index1, "Verify that Contact2 (two messages) is ranked higher than Contact1 (one message)");
		
		// Forget contact2
		mailform.zAutocompleteForgetItem(found2);
						
		// Cancel the compose
		DialogWarning dialog = (DialogWarning)mailform.zToolbarPressButton(Button.B_CANCEL);
		if ( dialog.zIsActive() ) {
			dialog.zClickButton(Button.B_NO);
		}

		
		
		// Compose again, verify contact1 (one message) is higher than contact2 (forgotten)
		// Open the new mail form
		mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);

		
		// Auto complete a name
		entries = mailform.zAutocompleteFillField(Field.To, "Mark");
		ZAssert.assertNotNull(entries, "Verify the autocomplete entry exists in the returned list");
		
		// Determine the ranking
		index1 = 0;
		index2 = 0;
		found2 = null;
		for (int i = 0; i < entries.size(); i++) {
			if ( entries.get(i).getAddress().contains(account1.EmailAddress) ) {
				index1 = i;
			}
			if ( entries.get(i).getAddress().contains(account2.EmailAddress) ) {
				index2 = i;
				found2 = entries.get(i);
			}
		}
		
		ZAssert.assertLessThan(index1, index2, "Verify that Contact1 (one message) is ranked higher than Contact2 (forgotten)");
		
		// Cancel the compose
		dialog = (DialogWarning)mailform.zToolbarPressButton(Button.B_CANCEL);
		if ( dialog.zIsActive() ) {
			dialog.zClickButton(Button.B_NO);
		}

	}



}
