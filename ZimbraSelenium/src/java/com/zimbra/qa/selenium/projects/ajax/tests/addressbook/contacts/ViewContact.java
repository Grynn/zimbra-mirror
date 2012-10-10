package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;



import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactNew;


public class ViewContact extends AjaxCommonTest  {
	public ViewContact() {
		logger.info("New "+ ViewContact.class.getCanonicalName());

		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Enable user preference checkboxes
		super.startingAccountPreferences = null;

	}


	//First Last 
	@Test(	description = "View a contact, file as First Last",
			groups = { "functional" })
	public void EditContact_02_FileAsFirstLast() throws HarnessException {		         		

		//-- Data
		
		// Create a contact item
		String firstname = "first"+ ZimbraSeleniumProperties.getUniqueString();
		String lastname = "last"+ ZimbraSeleniumProperties.getUniqueString();
		String email = "email"+ ZimbraSeleniumProperties.getUniqueString() + "@example.com";
		String company = "company"+ ZimbraSeleniumProperties.getUniqueString();
		
		String expected = String.format("%s %s", firstname, lastname);

		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
							"<a n='firstName'>"+ firstname +"</a>" +
							"<a n='lastName'>"+ lastname +"</a>" +
							"<a n='email'>"+ email +"</a>" +
							"<a n='company'>"+ company +"</a>" +
						"</cn>" +
				"</CreateContactRequest>" );


		//-- GUI

		// Refresh the app
		app.zPageAddressbook.zRefresh();

		// Select the contact
		FormContactNew form = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_DOUBLECLICK, firstname);
		
		// Change File As -> Company
		form.zToolbarPressPulldown(Button.B_FILEAS, Button.O_FILEAS_FIRSTLAST);
		
		// Verify that the title bar changes
		String fileas = form.zGetFieldText(FormContactNew.Field.FullName);
		ZAssert.assertEquals(fileas, expected, "Verify the contact edit form title");
		
		form.zToolbarPressButton(Button.B_SAVE);
		
		
		
		//-- Verification
		
		// Verify the contact list shows company
		boolean found = false;
		for (ContactItem c : app.zPageAddressbook.zListGetContacts()) {
			if (c.fileAs.equals(expected)) 
			{
				found = true;
				break;
			}			
		}

		ZAssert.assertTrue(found, "Verify contact (" + expected + ") displayed with First Last ");

		

	}

	//Last, First
	@Test(	description = "View a contact, file as Last, First",
			groups = { "functional" })
	public void EditContact_01_FileAsLastFirst() throws HarnessException {		         		

		//-- Data
		
		// Create a contact item
		String firstname = "first"+ ZimbraSeleniumProperties.getUniqueString();
		String lastname = "last"+ ZimbraSeleniumProperties.getUniqueString();
		String email = "email"+ ZimbraSeleniumProperties.getUniqueString() + "@example.com";
		String company = "company"+ ZimbraSeleniumProperties.getUniqueString();
		
		String expected = String.format("%s, %s", lastname, firstname);

		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
							"<a n='firstName'>"+ firstname +"</a>" +
							"<a n='lastName'>"+ lastname +"</a>" +
							"<a n='email'>"+ email +"</a>" +
							"<a n='company'>"+ company +"</a>" +
						"</cn>" +
				"</CreateContactRequest>" );


		//-- GUI

		// Refresh the app
		app.zPageAddressbook.zRefresh();

		// Select the contact
		FormContactNew form = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_DOUBLECLICK, firstname);
		
		// Change File As -> Company
		form.zToolbarPressPulldown(Button.B_FILEAS, Button.O_FILEAS_LASTFIRST);
		
		// Verify that the title bar changes
		String fileas = form.zGetFieldText(FormContactNew.Field.FullName);
		ZAssert.assertEquals(fileas, expected, "Verify the contact edit form title");
		
		form.zToolbarPressButton(Button.B_SAVE);
		
		
		
		//-- Verification
		
		// Verify the contact list shows company
		boolean found = false;
		for (ContactItem c : app.zPageAddressbook.zListGetContacts()) {
			if (c.fileAs.equals(expected)) 
			{
				found = true;
				break;
			}			
		}

		ZAssert.assertTrue(found, "Verify contact (" + expected + ") displayed with Last, First ");

		

	}

	//Company(Last, First)
	@Test(	description = "View a contact, file as Company(Last, First)",
			groups = { "functional" })
	public void EditContact_06_FileAsCompanyLastFirst() throws HarnessException {		         		

		//-- Data
		
		// Create a contact item
		String firstname = "first"+ ZimbraSeleniumProperties.getUniqueString();
		String lastname = "last"+ ZimbraSeleniumProperties.getUniqueString();
		String email = "email"+ ZimbraSeleniumProperties.getUniqueString() + "@example.com";
		String company = "company"+ ZimbraSeleniumProperties.getUniqueString();
		
		String expected = String.format("%s (%s, %s)", company, lastname, firstname);

		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
							"<a n='firstName'>"+ firstname +"</a>" +
							"<a n='lastName'>"+ lastname +"</a>" +
							"<a n='email'>"+ email +"</a>" +
							"<a n='company'>"+ company +"</a>" +
						"</cn>" +
				"</CreateContactRequest>" );


		//-- GUI

		// Refresh the app
		app.zPageAddressbook.zRefresh();

		// Select the contact
		FormContactNew form = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_DOUBLECLICK, firstname);
		
		// Change File As -> Company
		form.zToolbarPressPulldown(Button.B_FILEAS, Button.O_FILEAS_COMPANYLASTFIRST);
		
		// Verify that the title bar changes
		String fileas = form.zGetFieldText(FormContactNew.Field.FullName);
		ZAssert.assertEquals(fileas, expected, "Verify the contact edit form title");
		
		form.zToolbarPressButton(Button.B_SAVE);
		
		
		
		//-- Verification
		
		// Verify the contact list shows company
		boolean found = false;
		for (ContactItem c : app.zPageAddressbook.zListGetContacts()) {
			if (c.fileAs.equals(expected)) 
			{
				found = true;
				break;
			}			
		}

		ZAssert.assertTrue(found, "Verify contact (" + expected + ") displayed with Company(Last, First) ");

		

	}

	//Company
	@Test(	description = "View a contact, file as Company",
			groups = { "functional" })
	public void EditContact_03_FileAsCompany() throws HarnessException {		         		

		//-- Data
		
		// Create a contact item
		String firstname = "first"+ ZimbraSeleniumProperties.getUniqueString();
		String lastname = "last"+ ZimbraSeleniumProperties.getUniqueString();
		String email = "email"+ ZimbraSeleniumProperties.getUniqueString() + "@example.com";
		String company = "company"+ ZimbraSeleniumProperties.getUniqueString();
		
		String expected = String.format("%s", company);

		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
							"<a n='firstName'>"+ firstname +"</a>" +
							"<a n='lastName'>"+ lastname +"</a>" +
							"<a n='email'>"+ email +"</a>" +
							"<a n='company'>"+ company +"</a>" +
						"</cn>" +
				"</CreateContactRequest>" );


		//-- GUI

		// Refresh the app
		app.zPageAddressbook.zRefresh();

		// Select the contact
		FormContactNew form = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_DOUBLECLICK, firstname);
		
		// Change File As -> Company
		form.zToolbarPressPulldown(Button.B_FILEAS, Button.O_FILEAS_COMPANY);
		
		// Verify that the title bar changes
		String fileas = form.zGetFieldText(FormContactNew.Field.FullName);
		ZAssert.assertEquals(fileas, expected, "Verify the contact edit form title");
		
		form.zToolbarPressButton(Button.B_SAVE);
		
		
		
		//-- Verification
		
		// Verify the contact list shows company
		boolean found = false;
		for (ContactItem c : app.zPageAddressbook.zListGetContacts()) {
			if (c.fileAs.equals(expected)) 
			{
				found = true;
				break;
			}			
		}

		ZAssert.assertTrue(found, "Verify contact (" + expected + ") displayed with company ");

		


	}

	//Last, First (Company)
	@Test(	description = "View a contact, file as Last, First (Company)",
			groups = { "functional" })
	public void EditContact_04_FileAsLastFirstCompany() throws HarnessException {		         		

		//-- Data
		
		// Create a contact item
		String firstname = "first"+ ZimbraSeleniumProperties.getUniqueString();
		String lastname = "last"+ ZimbraSeleniumProperties.getUniqueString();
		String email = "email"+ ZimbraSeleniumProperties.getUniqueString() + "@example.com";
		String company = "company"+ ZimbraSeleniumProperties.getUniqueString();
		
		String expected = String.format("%s, %s (%s)", lastname, firstname, company);
				
		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
							"<a n='firstName'>"+ firstname +"</a>" +
							"<a n='lastName'>"+ lastname +"</a>" +
							"<a n='email'>"+ email +"</a>" +
							"<a n='company'>"+ company +"</a>" +
						"</cn>" +
				"</CreateContactRequest>" );


		//-- GUI

		// Refresh the app
		app.zPageAddressbook.zRefresh();

		// Select the contact
		FormContactNew form = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_DOUBLECLICK, firstname);
		
		// Change File As -> Company
		form.zToolbarPressPulldown(Button.B_FILEAS, Button.O_FILEAS_LASTFIRSTCOMPANY);
		
		// Verify that the title bar changes
		String fileas = form.zGetFieldText(FormContactNew.Field.FullName);
		ZAssert.assertEquals(fileas, expected, "Verify the contact edit form title");
		
		form.zToolbarPressButton(Button.B_SAVE);
		
		
		
		//-- Verification
		
		// Verify the contact list shows company
		boolean found = false;
		for (ContactItem c : app.zPageAddressbook.zListGetContacts()) {
			if (c.fileAs.equals(expected)) 
			{
				found = true;
				break;
			}			
		}

		ZAssert.assertTrue(found, "Verify contact (" + expected + ") displayed with 'Last, First (company)' ");

		


	}


	//First Last (Company)
	@Test(	description = "View a contact, file as First Last (Company)",
			groups = { "functional" })
	public void EditContact_05_FileAsFirstLastCompany() throws HarnessException {		         		

		//-- Data
		
		// Create a contact item
		String firstname = "first"+ ZimbraSeleniumProperties.getUniqueString();
		String lastname = "last"+ ZimbraSeleniumProperties.getUniqueString();
		String email = "email"+ ZimbraSeleniumProperties.getUniqueString() + "@example.com";
		String company = "company"+ ZimbraSeleniumProperties.getUniqueString();
		
		String expected = String.format("%s %s (%s)", firstname, lastname, company);

		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
							"<a n='firstName'>"+ firstname +"</a>" +
							"<a n='lastName'>"+ lastname +"</a>" +
							"<a n='email'>"+ email +"</a>" +
							"<a n='company'>"+ company +"</a>" +
						"</cn>" +
				"</CreateContactRequest>" );


		//-- GUI

		// Refresh the app
		app.zPageAddressbook.zRefresh();

		// Select the contact
		FormContactNew form = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_DOUBLECLICK, firstname);
		
		// Change File As -> Company
		form.zToolbarPressPulldown(Button.B_FILEAS, Button.O_FILEAS_FIRSTLASTCOMPANY);
		
		// Verify that the title bar changes
		String fileas = form.zGetFieldText(FormContactNew.Field.FullName);
		ZAssert.assertEquals(fileas, expected, "Verify the contact edit form title");
		
		form.zToolbarPressButton(Button.B_SAVE);
		
		
		
		//-- Verification
		
		// Verify the contact list shows company
		boolean found = false;
		for (ContactItem c : app.zPageAddressbook.zListGetContacts()) {
			if (c.fileAs.equals(expected)) 
			{
				found = true;
				break;
			}			
		}

		ZAssert.assertTrue(found, "Verify contact (" + expected + ") displayed with First Last (Company) ");

		

	}

	//Company (First Last)
	@Test(	description = "View a contact, file as Company (First Last)",
			groups = { "functional" })
	public void EditContact_07_FileAsCompanyFirstLast() throws HarnessException {		         		

		//-- Data
		
		// Create a contact item
		String firstname = "first"+ ZimbraSeleniumProperties.getUniqueString();
		String lastname = "last"+ ZimbraSeleniumProperties.getUniqueString();
		String email = "email"+ ZimbraSeleniumProperties.getUniqueString() + "@example.com";
		String company = "company"+ ZimbraSeleniumProperties.getUniqueString();
		
		String expected = String.format("%s (%s %s)", company, firstname, lastname);

		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
							"<a n='firstName'>"+ firstname +"</a>" +
							"<a n='lastName'>"+ lastname +"</a>" +
							"<a n='email'>"+ email +"</a>" +
							"<a n='company'>"+ company +"</a>" +
						"</cn>" +
				"</CreateContactRequest>" );


		//-- GUI

		// Refresh the app
		app.zPageAddressbook.zRefresh();

		// Select the contact
		FormContactNew form = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_DOUBLECLICK, firstname);
		
		// Change File As -> Company
		form.zToolbarPressPulldown(Button.B_FILEAS, Button.O_FILEAS_COMPANYFIRSTLAST);
		
		// Verify that the title bar changes
		String fileas = form.zGetFieldText(FormContactNew.Field.FullName);
		ZAssert.assertEquals(fileas, expected, "Verify the contact edit form title");
		
		form.zToolbarPressButton(Button.B_SAVE);
		
		
		
		//-- Verification
		
		// Verify the contact list shows company
		boolean found = false;
		for (ContactItem c : app.zPageAddressbook.zListGetContacts()) {
			if (c.fileAs.equals(expected)) 
			{
				found = true;
				break;
			}			
		}

		ZAssert.assertTrue(found, "Verify contact (" + expected + ") displayed with Company (First Last) ");

		

	}


}

