package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;



import java.util.*;
import java.util.Map.Entry;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.PageAddressbook;


public class GetContacts extends AjaxCommonTest  {
	public GetContacts() {
		logger.info("New "+ GetContacts.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Enable user preference checkboxes
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Click Alphabetbar button All: Verify contact started with digit and A-Z listed ",
			groups = { "functional" })
	public void GetContact_01_All_Button() throws HarnessException {
		
		String lastname;
		
		//-- Data
		
		// Create three contact

		lastname = "B" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
							"<a n='firstName'>first"+ ZimbraSeleniumProperties.getUniqueString() +"</a>" +
							"<a n='lastName'>"+ lastname +"</a>" +
							"<a n='email'>email@domain.com</a>" +
						"</cn>" +
				"</CreateContactRequest>" );
   		ContactItem contact1 = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#lastname:"+ lastname);
   		

   		lastname = "5" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
						"<a n='firstName'>first"+ ZimbraSeleniumProperties.getUniqueString() +"</a>" +
							"<a n='lastName'>"+ lastname +"</a>" +
							"<a n='email'>email@domain.com</a>" +
						"</cn>" +
				"</CreateContactRequest>" );
   		ContactItem contact2 = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#lastname:"+ lastname);
   		

   		lastname = "b" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
						"<a n='firstName'>first"+ ZimbraSeleniumProperties.getUniqueString() +"</a>" +
							"<a n='lastName'>"+ lastname +"</a>" +
							"<a n='email'>email@domain.com</a>" +
						"</cn>" +
				"</CreateContactRequest>" );
   		ContactItem contact3 = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#lastname:"+ lastname);
   		


   		//-- GUI
   		
   		app.zPageAddressbook.zRefresh();
   		
        //click All       
		app.zPageAddressbook.zToolbarPressButton(Button.B_AB_ALL);
					
		
		//-- Verification
		
		// Verify group name and members displayed
		List<ContactItem> items = app.zPageAddressbook.zListGetContacts();
		
		boolean found1 = false;
		boolean found2 = false;
		boolean found3 = false;
		
		for (ContactItem item : items) {
			
			if ( item.getName().equals(contact1.getName()) ) {
				found1 = true;
			}
			if ( item.getName().equals(contact2.getName()) ) {
				found2 = true;
			}
			if ( item.getName().equals(contact3.getName()) ) {
				found3 = true;
			}

		}
		
		ZAssert.assertTrue(found1, "Verify contact starting with B is listed");
		ZAssert.assertTrue(found2, "Verify contact starting with 5 is listed");
		ZAssert.assertTrue(found3, "Verify contact starting with b is listed");
	}

	@Test(	description = "Click Alphabetbar button 123: Verify contact started with digit listed and A-Z not-listed ",
			groups = { "functional" })
	public void GetContact_03_123_Button() throws HarnessException {
		
		String lastname;
		
		//-- Data
		
		// Create three contact

		lastname = "B" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
						"<a n='firstName'>first"+ ZimbraSeleniumProperties.getUniqueString() +"</a>" +
							"<a n='lastName'>"+ lastname +"</a>" +
							"<a n='email'>email@domain.com</a>" +
						"</cn>" +
				"</CreateContactRequest>" );
   		ContactItem contact1 = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#lastname:"+ lastname);
   		

   		lastname = "5" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
						"<a n='firstName'>first"+ ZimbraSeleniumProperties.getUniqueString() +"</a>" +
							"<a n='lastName'>"+ lastname +"</a>" +
							"<a n='email'>email@domain.com</a>" +
						"</cn>" +
				"</CreateContactRequest>" );
   		ContactItem contact2 = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#lastname:"+ lastname);
   		

   		lastname = "b" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
						"<a n='firstName'>first"+ ZimbraSeleniumProperties.getUniqueString() +"</a>" +
							"<a n='lastName'>"+ lastname +"</a>" +
							"<a n='email'>email@domain.com</a>" +
						"</cn>" +
				"</CreateContactRequest>" );
   		ContactItem contact3 = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#lastname:"+ lastname);
   		


   		//-- GUI
   		
   		app.zPageAddressbook.zRefresh();
   		
        //click All       
		app.zPageAddressbook.zToolbarPressButton(Button.B_AB_123);
					
		
		//-- Verification
		
		// Verify group name and members displayed
		List<ContactItem> items = app.zPageAddressbook.zListGetContacts();
		
		boolean found1 = false;
		boolean found2 = false;
		boolean found3 = false;
		
		for (ContactItem item : items) {
			
			if ( item.getName().equals(contact1.getName()) ) {
				found1 = true;
			}
			if ( item.getName().equals(contact2.getName()) ) {
				found2 = true;
			}
			if ( item.getName().equals(contact3.getName()) ) {
				found3 = true;
			}

		}
		
		ZAssert.assertFalse(found1, "Verify contact starting with B is not listed");
		ZAssert.assertTrue(found2, "Verify contact starting with 5 is listed");
		ZAssert.assertFalse(found3, "Verify contact starting with b is not listed");
	}

	@Test(	description = "Click Alphabetbar button B: Verify only contact started with B|b is listed ",
			groups = { "functional" })
	public void GetContact_02_B_Button() throws HarnessException {
		
		String lastname;
		
		//-- Data
		
		// Create three contact

		lastname = "B" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
						"<a n='firstName'>first"+ ZimbraSeleniumProperties.getUniqueString() +"</a>" +
							"<a n='lastName'>"+ lastname +"</a>" +
							"<a n='email'>email@domain.com</a>" +
						"</cn>" +
				"</CreateContactRequest>" );
   		ContactItem contact1 = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#lastname:"+ lastname);
   		

   		lastname = "5" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
						"<a n='firstName'>first"+ ZimbraSeleniumProperties.getUniqueString() +"</a>" +
							"<a n='lastName'>"+ lastname +"</a>" +
							"<a n='email'>email@domain.com</a>" +
						"</cn>" +
				"</CreateContactRequest>" );
   		ContactItem contact2 = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#lastname:"+ lastname);
   		

   		lastname = "b" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateContactRequest xmlns='urn:zimbraMail'>" +
						"<cn >" +
						"<a n='firstName'>first"+ ZimbraSeleniumProperties.getUniqueString() +"</a>" +
							"<a n='lastName'>"+ lastname +"</a>" +
							"<a n='email'>email@domain.com</a>" +
						"</cn>" +
				"</CreateContactRequest>" );
   		ContactItem contact3 = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#lastname:"+ lastname);
   		


   		//-- GUI
   		
   		app.zPageAddressbook.zRefresh();
   		
        //click All       
		app.zPageAddressbook.zToolbarPressButton(Button.B_AB_B);
					
		
		//-- Verification
		
		// Verify group name and members displayed
		List<ContactItem> items = app.zPageAddressbook.zListGetContacts();
		
		boolean found1 = false;
		boolean found2 = false;
		boolean found3 = false;
		
		for (ContactItem item : items) {
			
			if ( item.getName().equals(contact1.getName()) ) {
				found1 = true;
			}
			if ( item.getName().equals(contact2.getName()) ) {
				found2 = true;
			}
			if ( item.getName().equals(contact3.getName()) ) {
				found3 = true;
			}

		}
		
		ZAssert.assertTrue(found1, "Verify contact starting with B is listed");
		ZAssert.assertFalse(found2, "Verify contact starting with 5 is not listed");
		ZAssert.assertTrue(found3, "Verify contact starting with b is listed");
	}

	
	@Test(	description = "Click all Alphabetbar buttons: Verify only contact started with the alphabet is listed ",
			groups = { "functional" })
	public void GetContact_04_Iterate_Buttons() throws HarnessException {
		
		// TODO: INTL ... this test case might breaks all INTL locales
		
		
		//-- Data
		
		// A map of buttons to ContactGroupItem
		HashMap<Button, ContactItem> contacts = new HashMap<Button, ContactItem>();

		// Create contact groups with each letter

		for ( Entry<Character, Button> entry : PageAddressbook.buttons.entrySet() ) {
			
			Character c = entry.getKey();
			Button b = entry.getValue();

			
			String lastname = c + ZimbraSeleniumProperties.getUniqueString();
	   		app.zGetActiveAccount().soapSend(
					"<CreateContactRequest xmlns='urn:zimbraMail'>" +
							"<cn >" +
							"<a n='firstName'>first"+ ZimbraSeleniumProperties.getUniqueString() +"</a>" +
								"<a n='lastName'>"+ lastname +"</a>" +
								"<a n='email'>email@domain.com</a>" +
							"</cn>" +
					"</CreateContactRequest>" );
	   		ContactItem contact = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#lastname:"+ lastname);

	   		contacts.put(b, contact);
	   		
		}
		
		
		//-- GUI
		
		// refresh
		app.zPageAddressbook.zRefresh();
		

		
		//-- Verification
		
		for ( Entry<Button, ContactItem> entry : contacts.entrySet() ) {
			
			Button b = entry.getKey();
			ContactItem c = entry.getValue();
			
			// Click each button
			app.zPageAddressbook.zToolbarPressButton(b);
			
			// Verify the group is listed
			boolean found = false;
			for (ContactItem i : app.zPageAddressbook.zListGetContacts()) {
				if ( i.getName().equals(c.getName()) ) {
					found = true;
				}
			}

			ZAssert.assertTrue(found, "Verify contact "+ c.getName() +" is listed");
			
		}
		
	
	}   
	
	


}

