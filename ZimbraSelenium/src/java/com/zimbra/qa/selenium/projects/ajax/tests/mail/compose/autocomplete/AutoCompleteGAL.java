/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose.autocomplete;

import java.util.*;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;


public class AutoCompleteGAL extends PrefGroupMailByMessageTest {

	private String FirstName = "James" + ZimbraSeleniumProperties.getUniqueString();
	private String LastName = "Smith" + ZimbraSeleniumProperties.getUniqueString();
	private ZimbraAccount SampleAccount = null;
	
	
	public AutoCompleteGAL() throws HarnessException {
		logger.info("New "+ AutoCompleteGAL.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		super.startingAccountPreferences.put("zimbraPrefGalAutoCompleteEnabled", "TRUE");
	
	}
	
	@Test(	description = "Autocomplete using the GAL - First Name",
			groups = { "functional" })
	public void AutoCompleteGAL_01() throws HarnessException {
		
		// Message properties
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		if ( SampleAccount == null ) {
			SampleAccount = new ZimbraAccount();
			SampleAccount.DisplayName = FirstName + " " + LastName;
			SampleAccount.provision();
			SampleAccount.authenticate();
		}
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);

		// Auto complete a name
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, FirstName);
		AutocompleteEntry found = null;
		for (AutocompleteEntry entry : entries) {
			if ( entry.getAddress().contains(SampleAccount.EmailAddress) ) {
				found = entry;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
		mailform.zAutocompleteSelectItem(found);
		
		// Send the message
		mailform.zSubmit();

		
		// Log into the destination account and make sure the message is received
		MailItem received = MailItem.importFromSOAP(SampleAccount, "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
	}

	@Test(	description = "Autocomplete using the GAL - Partial First Name",
			groups = { "functional" })
	public void AutoCompleteGAL_02() throws HarnessException {
		
		// Message properties
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		if ( SampleAccount == null ) {
			SampleAccount = new ZimbraAccount();
			SampleAccount.DisplayName = FirstName + " " + LastName;
			SampleAccount.provision();
			SampleAccount.authenticate();
		}
		

		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);

		// Auto complete a name
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, FirstName.substring(0, 5));
		AutocompleteEntry found = null;
		for (AutocompleteEntry entry : entries) {
			if ( entry.getAddress().contains(SampleAccount.EmailAddress) ) {
				found = entry;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
		mailform.zAutocompleteSelectItem(found);
		
		// Send the message
		mailform.zSubmit();

		
		// Log into the destination account and make sure the message is received
		MailItem received = MailItem.importFromSOAP(SampleAccount, "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
	}

	@Test(	description = "Autocomplete using the GAL - Last Name",
			groups = { "functional" })
	public void AutoCompleteGAL_03() throws HarnessException {
		
		// Message properties
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		if ( SampleAccount == null ) {
			SampleAccount = new ZimbraAccount();
			SampleAccount.DisplayName = FirstName + " " + LastName;
			SampleAccount.provision();
			SampleAccount.authenticate();
		}
		

		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);

		// Auto complete a name
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, LastName);
		AutocompleteEntry found = null;
		for (AutocompleteEntry entry : entries) {
			if ( entry.getAddress().contains(SampleAccount.EmailAddress) ) {
				found = entry;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
		mailform.zAutocompleteSelectItem(found);
		
		// Send the message
		mailform.zSubmit();

		
		// Log into the destination account and make sure the message is received
		MailItem received = MailItem.importFromSOAP(SampleAccount, "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
	}

	@Test(	description = "Autocomplete using the GAL - Partial Last Name",
			groups = { "functional" })
	public void AutoCompleteGAL_04() throws HarnessException {
		
		// Message properties
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		if ( SampleAccount == null ) {
			SampleAccount = new ZimbraAccount();
			SampleAccount.DisplayName = FirstName + " " + LastName;
			SampleAccount.provision();
			SampleAccount.authenticate();
		}
		

		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);

		// Auto complete a name
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, LastName.substring(0, 5));
		AutocompleteEntry found = null;
		for (AutocompleteEntry entry : entries) {
			if ( entry.getAddress().contains(SampleAccount.EmailAddress) ) {
				found = entry;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
		mailform.zAutocompleteSelectItem(found);
		
		// Send the message
		mailform.zSubmit();

		
		// Log into the destination account and make sure the message is received
		MailItem received = MailItem.importFromSOAP(SampleAccount, "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
	}

	@Test(	description = "Autocomplete using the GAL - Full Name",
			groups = { "functional" })
	public void AutoCompleteGAL_05() throws HarnessException {
		
		// Message properties
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		if ( SampleAccount == null ) {
			SampleAccount = new ZimbraAccount();
			SampleAccount.DisplayName = FirstName + " " + LastName;
			SampleAccount.provision();
			SampleAccount.authenticate();
		}
		

		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);

		// Auto complete a name
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, SampleAccount.DisplayName);
		AutocompleteEntry found = null;
		for (AutocompleteEntry entry : entries) {
			if ( entry.getAddress().contains(SampleAccount.EmailAddress) ) {
				found = entry;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
		mailform.zAutocompleteSelectItem(found);
		
		// Send the message
		mailform.zSubmit();

		
		// Log into the destination account and make sure the message is received
		MailItem received = MailItem.importFromSOAP(SampleAccount, "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
	}

	@Test(	description = "Autocomplete using the GAL - First Name and Last Initial",
			groups = { "functional" })
	public void AutoCompleteGAL_07() throws HarnessException {
		
		// Message properties
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		if ( SampleAccount == null ) {
			SampleAccount = new ZimbraAccount();
			SampleAccount.DisplayName = FirstName + " " + LastName;
			SampleAccount.provision();
			SampleAccount.authenticate();
		}
		

		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);

		// Auto complete a name
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, FirstName + " " + LastName.substring(0, 1));
		AutocompleteEntry found = null;
		for (AutocompleteEntry entry : entries) {
			if ( entry.getAddress().contains(SampleAccount.EmailAddress) ) {
				found = entry;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
		mailform.zAutocompleteSelectItem(found);
		
		// Send the message
		mailform.zSubmit();

		
		// Log into the destination account and make sure the message is received
		MailItem received = MailItem.importFromSOAP(SampleAccount, "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
	}


	@Test(	description = "Autocomplete using the GAL - Multiple Matches",
			groups = { "functional" })
	public void AutoCompleteGAL_08() throws HarnessException {
		int count = 3;
		
		// Message properties
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		String firstname = "Ethan" + ZimbraSeleniumProperties.getUniqueString();
		for (int i = 0; i < count; i++) {
			ZimbraAccount account = new ZimbraAccount();
			account.DisplayName = firstname + " Johnson" + ZimbraSeleniumProperties.getUniqueString();
			account.provision();
			account.authenticate();
		}
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);

		// Auto complete a name
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, firstname);
		
		ZAssert.assertEquals(entries.size(), count, "Verify the correct number of results were returned");
		
		mailform.zAutocompleteSelectItem(entries.get(1));
		
		// Send the message
		mailform.zSubmit();

		
	}

	@Test(	description = "Autocomplete using the GAL - No Matches",
			groups = { "functional" })
	public void AutoCompleteGAL_09() throws HarnessException {
		
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
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, "nomatchstring");
		
		ZAssert.assertEquals(entries.size(), 0, "Verify zero results were returned");
				
		DialogWarning dialog = (DialogWarning)mailform.zToolbarPressButton(Button.B_CANCEL);
		if ( dialog.zIsActive() ) {
			dialog.zClickButton(Button.B_NO);
		}
		
	}

	@Bugs(	ids = "45815")
	@Test(	description = "Autocomplete using the GAL - Apostrophe character",
			groups = { "functional" })
	public void AutoCompleteGAL_10() throws HarnessException {
		
		final String givenName = "Thomas" + ZimbraSeleniumProperties.getUniqueString();
		final String sn = "O'Connor" + ZimbraSeleniumProperties.getUniqueString();
		final String displayName = givenName + " " + sn;
		
		// Create a GAL Entry
		ZimbraAccount account = new ZimbraAccount();
		Map<String,String> attrs = new HashMap<String, String>() {
			private static final long serialVersionUID = -939087202049217526L;
			{
				put("givenName", givenName);
				put("sn", sn);
				put("displayName", displayName);
			}};
		account.setAccountPreferences(attrs);
		account.provision();
		account.authenticate();
		
		app.zPageMain.zToolbarPressButton(Button.B_REFRESH);

		
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
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, "O'");
		AutocompleteEntry found = null;
		for (AutocompleteEntry entry : entries) {
			if ( entry.getAddress().contains(account.EmailAddress) ) {
				found = entry;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
		mailform.zAutocompleteSelectItem(found);
		
		// Send the message
		mailform.zSubmit();

		
		// Log into the destination account and make sure the message is received
		MailItem received = MailItem.importFromSOAP(account, "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
	}

	@Bugs(ids = "47045")
	@Test(	description = "Autocomplete including a period/dot '.' in the string",
			groups = { "functional" })
	public void AutoCompleteGAL_Bug47045A() throws HarnessException {
		

		if ( SampleAccount == null ) {
			SampleAccount = new ZimbraAccount();
			SampleAccount.DisplayName = FirstName + " " + LastName;
			SampleAccount.provision();
			SampleAccount.authenticate();
		}
		

		// Message properties
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);

		// Auto complete a name based on email address such as "user@domain."
		String value = SampleAccount.EmailAddress.substring(0, SampleAccount.EmailAddress.indexOf('.') + 1);
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, value);
		AutocompleteEntry found = null;
		for (AutocompleteEntry entry : entries) {
			if ( entry.getAddress().contains(SampleAccount.EmailAddress) ) {
				found = entry;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
		mailform.zAutocompleteSelectItem(found);
		
		// Send the message
		mailform.zSubmit();

		
		// Log into the destination account and make sure the message is received
		MailItem received = MailItem.importFromSOAP(SampleAccount, "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
	}

	@Bugs(ids = "47045")
	@Test(	description = "Autocomplete including a period/dot '.' in the string",
			groups = { "functional" })
	public void AutoCompleteGAL_Bug47045B() throws HarnessException {
		

		if ( SampleAccount == null ) {
			SampleAccount = new ZimbraAccount();
			SampleAccount.DisplayName = FirstName + " " + LastName;
			SampleAccount.provision();
			SampleAccount.authenticate();
		}
		

		// Message properties
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);

		// Auto complete a name based on email address such as "user@domain.c"
		String value = SampleAccount.EmailAddress.substring(0, SampleAccount.EmailAddress.indexOf('.') + 2);
		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, value);
		AutocompleteEntry found = null;
		for (AutocompleteEntry entry : entries) {
			if ( entry.getAddress().contains(SampleAccount.EmailAddress) ) {
				found = entry;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the autocomplete entry exists in the returned list");
		mailform.zAutocompleteSelectItem(found);
		
		// Send the message
		mailform.zSubmit();

		
		// Log into the destination account and make sure the message is received
		MailItem received = MailItem.importFromSOAP(SampleAccount, "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received correctly");
		
	}

	@Bugs(ids = "40959, 65081")
	@Test(	description = "Autocomplete on 'mike m' should not return all 'mike' names, only those with last name starting with 'm'",
			groups = { "functional" })
	public void AutoCompleteGAL_Bug40959() throws HarnessException {
		
		final String givenName1 = "Mike";
		final String sn1 = "Carter" + ZimbraSeleniumProperties.getUniqueString();
		final String displayName1 = givenName1 + " " + sn1;
		Map<String,String> attrs1 = new HashMap<String, String>() {
			private static final long serialVersionUID = -919087202049217526L;
			{
				put("givenName", givenName1);
				put("sn", sn1);
				put("displayName", displayName1);
			}};

		final String givenName2 = "Mike";
		final String sn2 = "Mitchell" + ZimbraSeleniumProperties.getUniqueString();
		final String displayName2 = givenName2 + " " + sn2;
		Map<String,String> attrs2 = new HashMap<String, String>() {
			private static final long serialVersionUID = -939077202049217526L;
			{
				put("givenName", givenName2);
				put("sn", sn2);
				put("displayName", displayName2);
			}};

		final String givenName3 = "Mike";
		final String sn3 = "Murphy" + ZimbraSeleniumProperties.getUniqueString();
		final String displayName3 = givenName3 + " " + sn3;
		Map<String,String> attrs3 = new HashMap<String, String>() {
			private static final long serialVersionUID = -939077202049216526L;
			{
				put("givenName", givenName3);
				put("sn", sn3);
				put("displayName", displayName3);
			}};

		// Create 3 Mikes
		ZimbraAccount account1 = new ZimbraAccount();
		account1.setAccountPreferences(attrs1);
		account1.provision();
		account1.authenticate();

		ZimbraAccount account2 = new ZimbraAccount();
		account2.setAccountPreferences(attrs2);
		account2.provision();
		account2.authenticate();

		ZimbraAccount account3 = new ZimbraAccount();
		account3.setAccountPreferences(attrs3);
		account3.provision();
		account3.authenticate();

		
		// Message properties
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);

		List<AutocompleteEntry> entries = mailform.zAutocompleteFillField(Field.To, "Mike M");
		AutocompleteEntry found1 = null;
		AutocompleteEntry found2 = null;
		AutocompleteEntry found3 = null;
		for (AutocompleteEntry entry : entries) {
			if ( entry.getAddress().contains(account1.EmailAddress) ) {
				found1 = entry;
			}
			if ( entry.getAddress().contains(account2.EmailAddress) ) {
				found2 = entry;
			}
			if ( entry.getAddress().contains(account3.EmailAddress) ) {
				found3 = entry;
			}
		}
		
		ZAssert.assertNull(found1, "Verify 'mike m' does not match "+ account1.DisplayName);
		ZAssert.assertNotNull(found2, "Verify 'mike m' does match "+ account2.DisplayName);
		ZAssert.assertNotNull(found3, "Verify 'mike m' does match "+ account3.DisplayName);
		
		// Cancel the compose
		DialogWarning dialog = (DialogWarning)mailform.zToolbarPressButton(Button.B_CANCEL);
		if ( dialog.zIsActive() ) {
			dialog.zClickButton(Button.B_NO);
		}

	}


}
