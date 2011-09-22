package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose.autocomplete;

import java.util.List;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;


public class AutoCompleteContactGroup extends PrefGroupMailByMessageTest {


	private ZimbraAccount Contact1 = null;
	private String Contact1FirstName = null;
	private String Contact1LastName = null;

	private ZimbraAccount Contact2 = null;
	private String Contact2FirstName = null;
	private String Contact2LastName = null;

	private String ContactGroupName = null;

	
	
	public AutoCompleteContactGroup() {
		logger.info("New "+ AutoCompleteContactGroup.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		super.startingAccountPreferences.put("zimbraPrefContactsDisableAutocompleteOnContactGroupMembers", "FALSE");
	
	}
	
	@BeforeMethod(alwaysRun = true)
	/**
	 * Create a contact group
	 */
	public void CreateContactGroup() throws HarnessException {
		
		Contact1 = new ZimbraAccount();
		Contact1.provision();
		Contact1.authenticate();
		Contact1FirstName = "Alexander" + ZimbraSeleniumProperties.getUniqueString();
		Contact1LastName = "Davis" + ZimbraSeleniumProperties.getUniqueString();

		Contact2 = new ZimbraAccount();
		Contact2.provision();
		Contact2.authenticate();
		Contact2FirstName = "Noah" + ZimbraSeleniumProperties.getUniqueString();
		Contact2LastName = "Miller" + ZimbraSeleniumProperties.getUniqueString();

		
		ContactGroupName = "Apple" + ZimbraSeleniumProperties.getUniqueString();
		
		// Create a contact
		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>"
			+		"<cn >"
			+			"<a n='firstName'>"+ Contact1FirstName +"</a>"
			+			"<a n='lastName'>"+ Contact1LastName +"</a>"
			+			"<a n='email'>"+ Contact1.EmailAddress +"</a>"
			+		"</cn>"
			+	"</CreateContactRequest>");
	
		// Create a contact
		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>"
			+		"<cn >"
			+			"<a n='firstName'>"+ Contact2FirstName +"</a>"
			+			"<a n='lastName'>"+ Contact2LastName +"</a>"
			+			"<a n='email'>"+ Contact2.EmailAddress +"</a>"
			+		"</cn>"
			+	"</CreateContactRequest>");
	
	
		// Create a contact group
		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>"
			+		"<cn >"
			+			"<a n='type'>group</a>"
			+			"<a n='fileAs'>8:" + ContactGroupName + "</a>"
			+			"<a n='nickname'>"+ ContactGroupName +"</a>"
			+			"<a n='dlist'>"+ Contact1.EmailAddress + ","+ Contact2.EmailAddress +"</a>"
			+		"</cn>"
			+	"</CreateContactRequest>");

		
		app.zPageMain.zToolbarPressButton(Button.B_REFRESH);

	}

	@Test(	description = "Autocomplete using a Contact Group - Group Name",
			groups = { "functional" })
	public void AutoCompleteContactGroups_01() throws HarnessException {
		
		
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
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, ContactGroupName);
		AutocompleteEntry found = null;
		for (AutocompleteEntry entry : entries) {
			if ( entry.getAddress().contains(ContactGroupName) ) {
				found = entry;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
		mailform.zAutocompleteSelectItem(found);
		
		// Send the message
		mailform.zSubmit();

		
		// Log into the destination account and make sure the message is received
		MailItem received = MailItem.importFromSOAP(Contact1, "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
		received = MailItem.importFromSOAP(Contact2, "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");

	}

//	@Test(	description = "Autocomplete using the Contacts - Partial First Name",
//			groups = { "functional" })
//	public void AutoCompleteContactGroups_02() throws HarnessException {
//		
//		// Create a contact
//		ZimbraAccount contact = new ZimbraAccount();
//		contact.provision();
//		contact.authenticate();
//
//		String firstname = "Michael" + ZimbraSeleniumProperties.getUniqueString();
//		String lastname = "Williams" + ZimbraSeleniumProperties.getUniqueString();
//		
//		app.zGetActiveAccount().soapSend(
//					"<CreateContactRequest xmlns='urn:zimbraMail'>"
//				+		"<cn>"
//				+			"<a n='firstName'>"+ firstname +"</a>"
//				+			"<a n='lastName'>"+ lastname +"</a>"
//				+			"<a n='email'>"+ contact.EmailAddress +"</a>"
//				+		"</cn>"
//				+	"</CreateContactRequest>");
//		
//		app.zPageMain.zToolbarPressButton(Button.B_REFRESH);
//
//		
//		// Message properties
//		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
//		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
//		
//		
//		
//		// Open the new mail form
//		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
//		ZAssert.assertNotNull(mailform, "Verify the new form opened");
//		
//		// Fill out the form with the data
//		mailform.zFillField(Field.Subject, subject);
//		mailform.zFillField(Field.Body, body);
//
//		// Auto complete a name
//		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, firstname.substring(0, 5));
//		AutocompleteEntry found = null;
//		for (AutocompleteEntry entry : entries) {
//			if ( entry.getAddress().contains(contact.EmailAddress) ) {
//				found = entry;
//				break;
//			}
//		}
//		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
//		mailform.zAutocompleteSelectItem(found);
//		
//		// Send the message
//		mailform.zSubmit();
//
//		
//		// Log into the destination account and make sure the message is received
//		MailItem received = MailItem.importFromSOAP(contact, "subject:("+ subject +")");
//		ZAssert.assertNotNull(received, "Verify the message is received correctly");
//		
//	}
//
//	@Test(	description = "Autocomplete using a Contact - Last Name",
//			groups = { "functional" })
//	public void AutoCompleteContactGroups_03() throws HarnessException {
//		
//		// Create a contact
//		ZimbraAccount contact = new ZimbraAccount();
//		contact.provision();
//		contact.authenticate();
//
//		String firstname = "Michael" + ZimbraSeleniumProperties.getUniqueString();
//		String lastname = "Williams" + ZimbraSeleniumProperties.getUniqueString();
//		
//		app.zGetActiveAccount().soapSend(
//					"<CreateContactRequest xmlns='urn:zimbraMail'>"
//				+		"<cn>"
//				+			"<a n='firstName'>"+ firstname +"</a>"
//				+			"<a n='lastName'>"+ lastname +"</a>"
//				+			"<a n='email'>"+ contact.EmailAddress +"</a>"
//				+		"</cn>"
//				+	"</CreateContactRequest>");
//		
//		app.zPageMain.zToolbarPressButton(Button.B_REFRESH);
//
//		
//		// Message properties
//		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
//		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
//		
//		
//		
//		// Open the new mail form
//		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
//		ZAssert.assertNotNull(mailform, "Verify the new form opened");
//		
//		// Fill out the form with the data
//		mailform.zFillField(Field.Subject, subject);
//		mailform.zFillField(Field.Body, body);
//
//		// Auto complete a name
//		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, lastname);
//		AutocompleteEntry found = null;
//		for (AutocompleteEntry entry : entries) {
//			if ( entry.getAddress().contains(contact.EmailAddress) ) {
//				found = entry;
//				break;
//			}
//		}
//		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
//		mailform.zAutocompleteSelectItem(found);
//		
//		// Send the message
//		mailform.zSubmit();
//
//		
//		// Log into the destination account and make sure the message is received
//		MailItem received = MailItem.importFromSOAP(contact, "subject:("+ subject +")");
//		ZAssert.assertNotNull(received, "Verify the message is received correctly");
//		
//	}
//
//	@Test(	description = "Autocomplete using a Contact - Partial Last Name",
//			groups = { "functional" })
//	public void AutoCompleteContactGroups_04() throws HarnessException {
//		
//		// Create a contact
//		ZimbraAccount contact = new ZimbraAccount();
//		contact.provision();
//		contact.authenticate();
//
//		String firstname = "Michael" + ZimbraSeleniumProperties.getUniqueString();
//		String lastname = "Williams" + ZimbraSeleniumProperties.getUniqueString();
//		
//		app.zGetActiveAccount().soapSend(
//					"<CreateContactRequest xmlns='urn:zimbraMail'>"
//				+		"<cn>"
//				+			"<a n='firstName'>"+ firstname +"</a>"
//				+			"<a n='lastName'>"+ lastname +"</a>"
//				+			"<a n='email'>"+ contact.EmailAddress +"</a>"
//				+		"</cn>"
//				+	"</CreateContactRequest>");
//		
//		app.zPageMain.zToolbarPressButton(Button.B_REFRESH);
//
//		
//		// Message properties
//		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
//		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
//		
//		
//		
//		// Open the new mail form
//		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
//		ZAssert.assertNotNull(mailform, "Verify the new form opened");
//		
//		// Fill out the form with the data
//		mailform.zFillField(Field.Subject, subject);
//		mailform.zFillField(Field.Body, body);
//
//		// Auto complete a name
//		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, lastname.substring(0, 5));
//		AutocompleteEntry found = null;
//		for (AutocompleteEntry entry : entries) {
//			if ( entry.getAddress().contains(contact.EmailAddress) ) {
//				found = entry;
//				break;
//			}
//		}
//		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
//		mailform.zAutocompleteSelectItem(found);
//		
//		// Send the message
//		mailform.zSubmit();
//
//		
//		// Log into the destination account and make sure the message is received
//		MailItem received = MailItem.importFromSOAP(contact, "subject:("+ subject +")");
//		ZAssert.assertNotNull(received, "Verify the message is received correctly");
//		
//	}
//
//	@Test(	description = "Autocomplete using a Contact - Full Name",
//			groups = { "functional" })
//	public void AutoCompleteContactGroups_05() throws HarnessException {
//		
//		// Create a contact
//		ZimbraAccount contact = new ZimbraAccount();
//		contact.provision();
//		contact.authenticate();
//
//		String firstname = "Michael" + ZimbraSeleniumProperties.getUniqueString();
//		String lastname = "Williams" + ZimbraSeleniumProperties.getUniqueString();
//		
//		app.zGetActiveAccount().soapSend(
//					"<CreateContactRequest xmlns='urn:zimbraMail'>"
//				+		"<cn>"
//				+			"<a n='firstName'>"+ firstname +"</a>"
//				+			"<a n='lastName'>"+ lastname +"</a>"
//				+			"<a n='email'>"+ contact.EmailAddress +"</a>"
//				+		"</cn>"
//				+	"</CreateContactRequest>");
//		
//		app.zPageMain.zToolbarPressButton(Button.B_REFRESH);
//
//		
//		// Message properties
//		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
//		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
//		
//		
//		
//		// Open the new mail form
//		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
//		ZAssert.assertNotNull(mailform, "Verify the new form opened");
//		
//		// Fill out the form with the data
//		mailform.zFillField(Field.Subject, subject);
//		mailform.zFillField(Field.Body, body);
//
//		// Auto complete a name
//		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, firstname + " " + lastname);
//		AutocompleteEntry found = null;
//		for (AutocompleteEntry entry : entries) {
//			if ( entry.getAddress().contains(contact.EmailAddress) ) {
//				found = entry;
//				break;
//			}
//		}
//		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
//		mailform.zAutocompleteSelectItem(found);
//		
//		// Send the message
//		mailform.zSubmit();
//
//		
//		// Log into the destination account and make sure the message is received
//		MailItem received = MailItem.importFromSOAP(contact, "subject:("+ subject +")");
//		ZAssert.assertNotNull(received, "Verify the message is received correctly");
//		
//	}
//
//	@Test(	description = "Autocomplete using a Contact - First Name and Last Initial",
//			groups = { "functional" })
//	public void AutoCompleteContactGroups_07() throws HarnessException {
//		
//		// Create a contact
//		ZimbraAccount contact = new ZimbraAccount();
//		contact.provision();
//		contact.authenticate();
//
//		String firstname = "Michael" + ZimbraSeleniumProperties.getUniqueString();
//		String lastname = "Williams" + ZimbraSeleniumProperties.getUniqueString();
//		
//		app.zGetActiveAccount().soapSend(
//					"<CreateContactRequest xmlns='urn:zimbraMail'>"
//				+		"<cn>"
//				+			"<a n='firstName'>"+ firstname +"</a>"
//				+			"<a n='lastName'>"+ lastname +"</a>"
//				+			"<a n='email'>"+ contact.EmailAddress +"</a>"
//				+		"</cn>"
//				+	"</CreateContactRequest>");
//		
//		app.zPageMain.zToolbarPressButton(Button.B_REFRESH);
//
//		
//		// Message properties
//		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
//		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
//		
//		
//		
//		// Open the new mail form
//		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
//		ZAssert.assertNotNull(mailform, "Verify the new form opened");
//		
//		// Fill out the form with the data
//		mailform.zFillField(Field.Subject, subject);
//		mailform.zFillField(Field.Body, body);
//
//		// Auto complete a name
//		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, firstname + " " + lastname.substring(0, 1));
//		AutocompleteEntry found = null;
//		for (AutocompleteEntry entry : entries) {
//			if ( entry.getAddress().contains(contact.EmailAddress) ) {
//				found = entry;
//				break;
//			}
//		}
//		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
//		mailform.zAutocompleteSelectItem(found);
//		
//		// Send the message
//		mailform.zSubmit();
//
//		
//		// Log into the destination account and make sure the message is received
//		MailItem received = MailItem.importFromSOAP(contact, "subject:("+ subject +")");
//		ZAssert.assertNotNull(received, "Verify the message is received correctly");
//		
//	}
//
//
//	@Test(	description = "Autocomplete using a Contact - Multiple Matches",
//			groups = { "functional" })
//	public void AutoCompleteContactGroups_08() throws HarnessException {
//		int count = 3;
//		
//		// Message properties
//		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
//		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
//		
//		String firstname = "Jayden" + ZimbraSeleniumProperties.getUniqueString();
//		for (int i = 0; i < count; i++) {
//			
//			// Create a contact
//			ZimbraAccount contact = new ZimbraAccount();
//			contact.provision();
//			contact.authenticate();
//
//			String lastname = "Jones" + ZimbraSeleniumProperties.getUniqueString();
//			
//			app.zGetActiveAccount().soapSend(
//						"<CreateContactRequest xmlns='urn:zimbraMail'>"
//					+		"<cn>"
//					+			"<a n='firstName'>"+ firstname +"</a>"
//					+			"<a n='lastName'>"+ lastname +"</a>"
//					+			"<a n='email'>"+ contact.EmailAddress +"</a>"
//					+		"</cn>"
//					+	"</CreateContactRequest>");
//			
//		}
//		
//		app.zPageMain.zToolbarPressButton(Button.B_REFRESH);
//
//		
//		// Open the new mail form
//		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
//		ZAssert.assertNotNull(mailform, "Verify the new form opened");
//		
//		// Fill out the form with the data
//		mailform.zFillField(Field.Subject, subject);
//		mailform.zFillField(Field.Body, body);
//
//		// Auto complete a name
//		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, firstname);
//		
//		ZAssert.assertEquals(entries.size(), count, "Verify the correct number of results were returned");
//		
//		mailform.zAutocompleteSelectItem(entries.get(1));
//		
//		// Send the message
//		mailform.zSubmit();
//
//		
//	}
//
//	@Test(	description = "Autocomplete using a Contact - No Matches",
//			groups = { "functional" })
//	public void AutoCompleteContactGroups_09() throws HarnessException {
//		
//		// Create a contact
//		ZimbraAccount contact = new ZimbraAccount();
//		contact.provision();
//		contact.authenticate();
//
//		String firstname = "Michael" + ZimbraSeleniumProperties.getUniqueString();
//		String lastname = "Williams" + ZimbraSeleniumProperties.getUniqueString();
//		
//		app.zGetActiveAccount().soapSend(
//					"<CreateContactRequest xmlns='urn:zimbraMail'>"
//				+		"<cn>"
//				+			"<a n='firstName'>"+ firstname +"</a>"
//				+			"<a n='lastName'>"+ lastname +"</a>"
//				+			"<a n='email'>"+ contact.EmailAddress +"</a>"
//				+		"</cn>"
//				+	"</CreateContactRequest>");
//		
//		app.zPageMain.zToolbarPressButton(Button.B_REFRESH);
//
//		
//		// Message properties
//		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
//		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
//		
//		// Open the new mail form
//		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
//		ZAssert.assertNotNull(mailform, "Verify the new form opened");
//		
//		// Fill out the form with the data
//		mailform.zFillField(Field.Subject, subject);
//		mailform.zFillField(Field.Body, body);
//
//		// Auto complete a name
//		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, "nomatchstring");
//		
//		ZAssert.assertEquals(entries.size(), 0, "Verify zero results were returned");
//				
//		DialogWarning dialog = (DialogWarning)mailform.zToolbarPressButton(Button.B_CANCEL);
//		if ( dialog.zIsActive() ) {
//			dialog.zClickButton(Button.B_NO);
//		}
//		
//	}


}
