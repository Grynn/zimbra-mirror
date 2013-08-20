/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
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
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;


import java.awt.event.KeyEvent;
import java.util.HashMap;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class DeleteContact extends AjaxCommonTest  {
	
	
	
	public DeleteContact() {
		logger.info("New "+ DeleteContact.class.getCanonicalName());
		
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Enable user preference checkboxes
		super.startingAccountPreferences = new HashMap<String , String>() {
			private static final long serialVersionUID = -8102550098554063084L;

		{
		    	put("zimbraPrefShowSelectionCheckbox", "TRUE");		         
		   }};			
		
	}
	
	@Test(	description = "Delete a contact item",
			groups = { "smoke" })
	public void ClickDeleteOnToolbar() throws HarnessException {

		//-- Data
		
		// Create a contact item
		ContactItem contact = new ContactItem();
		contact.firstName = "First" + ZimbraSeleniumProperties.getUniqueString();
		contact.lastName = "Last" + ZimbraSeleniumProperties.getUniqueString();
		contact.email = "email" + ZimbraSeleniumProperties.getUniqueString() + "@domain.com";

		app.zGetActiveAccount().soapSend(
	                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
	                		"<cn >" +
	                			"<a n='firstName'>" + contact.firstName +"</a>" +
	                			"<a n='lastName'>" + contact.lastName +"</a>" +
	                			"<a n='email'>" + contact.email + "</a>" +
                			"</cn>" +
	                "</CreateContactRequest>");


		//-- GUI
		
		// Refresh to get the contact into the client
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.firstName);
		
        // delete contact
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);
       
        
        //-- Verification
        
        //verify contact deleted
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
        ZAssert.assertNull(actual, "Verify the contact is deleted from the addressbook");
        
        // Verify contact in trash
        actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #firstname:"+ contact.firstName);
        ZAssert.assertNotNull(actual, "Verify the contact is in the trash");
        
   	}

	@Test(	description = "Delete a contact item selected with checkbox",
			groups = { "functional" })
	public void DeleteContactSelectedWithCheckbox() throws HarnessException {

		//-- Data
		
		// Create a contact item
		ContactItem contact = new ContactItem();
		contact.firstName = "First" + ZimbraSeleniumProperties.getUniqueString();
		contact.lastName = "Last" + ZimbraSeleniumProperties.getUniqueString();
		contact.email = "email" + ZimbraSeleniumProperties.getUniqueString() + "@domain.com";

		app.zGetActiveAccount().soapSend(
	                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
	                		"<cn >" +
	                			"<a n='firstName'>" + contact.firstName +"</a>" +
	                			"<a n='lastName'>" + contact.lastName +"</a>" +
	                			"<a n='email'>" + contact.email + "</a>" +
                			"</cn>" +
	                "</CreateContactRequest>");


		//-- GUI
		
		// Refresh to get the contact into the client
		app.zPageAddressbook.zRefresh();
		
		// Select the contact's checkbox
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, contact.firstName);
		
        // delete contact
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);
       
        
        //-- Verification
        
        //verify contact deleted
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
        ZAssert.assertNull(actual, "Verify the contact is deleted from the addressbook");
        
   	}

	
	@DataProvider(name = "DataProviderDeleteKeys")
	public Object[][] DataProviderDeleteKeys() {
	  return new Object[][] {
	    new Object[] { "VK_DELETE", KeyEvent.VK_DELETE },
	    new Object[] { "VK_BACK_SPACE", KeyEvent.VK_BACK_SPACE },
	  };
	}

	@Test(	description = "Delete a contact item using keyboard short cut Del",
			groups = { "functional" },
			dataProvider = "DataProviderDeleteKeys")
	public void UseShortcut(String name, int keyEvent) throws HarnessException {
        
        

		//-- Data
		
		// Create a contact item
		ContactItem contact = new ContactItem();
		contact.firstName = "First" + ZimbraSeleniumProperties.getUniqueString();
		contact.lastName = "Last" + ZimbraSeleniumProperties.getUniqueString();
		contact.email = "email" + ZimbraSeleniumProperties.getUniqueString() + "@domain.com";

		app.zGetActiveAccount().soapSend(
	                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
	                		"<cn >" +
	                			"<a n='firstName'>" + contact.firstName +"</a>" +
	                			"<a n='lastName'>" + contact.lastName +"</a>" +
	                			"<a n='email'>" + contact.email + "</a>" +
                			"</cn>" +
	                "</CreateContactRequest>");


		//-- GUI
		
		// Refresh to get the contact into the client
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.firstName);
		
		// Delete the contact
		logger.info("Typing shortcut key "+ name + " KeyEvent: "+ keyEvent);
		app.zPageMail.zKeyboardKeyEvent(keyEvent);
       
        
        //-- Verification
        
        //verify contact deleted
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
        ZAssert.assertNull(actual, "Verify the contact is deleted from the addressbook");
        

   	}
	
	

	@Test(	description = "Right click then click delete",
			groups = { "smoke" })
	public void DeleteFromContextMenu() throws HarnessException {
		

		//-- Data
		
		// Create a contact item
		ContactItem contact = new ContactItem();
		contact.firstName = "First" + ZimbraSeleniumProperties.getUniqueString();
		contact.lastName = "Last" + ZimbraSeleniumProperties.getUniqueString();
		contact.email = "email" + ZimbraSeleniumProperties.getUniqueString() + "@domain.com";
		contact.fileAs = contact.lastName + ", " + contact.firstName;

		app.zGetActiveAccount().soapSend(
	                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
	                		"<cn >" +
	                			"<a n='firstName'>" + contact.firstName +"</a>" +
	                			"<a n='lastName'>" + contact.lastName +"</a>" +
	                			"<a n='email'>" + contact.email + "</a>" +
                			"</cn>" +
	                "</CreateContactRequest>");


		//-- GUI
		
		// Refresh to get the contact into the client
		app.zPageAddressbook.zRefresh();
		
		//select delete option
        app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_DELETE, contact.fileAs);
       
        
        //-- Verification
        
        //verify contact deleted
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
        ZAssert.assertNull(actual, "Verify the contact is deleted from the addressbook");
        


   	}

	@Test(	description = "Delete multiple contact items",
			groups = { "functional" })
	public void DeleteMultipleContacts() throws HarnessException {

    
		

		//-- Data
		
		// Create a contact items
		ContactItem contact1 = new ContactItem();
		contact1.firstName = "First" + ZimbraSeleniumProperties.getUniqueString();
		contact1.lastName = "Last" + ZimbraSeleniumProperties.getUniqueString();
		contact1.email = "email" + ZimbraSeleniumProperties.getUniqueString() + "@domain.com";
		contact1.fileAs = contact1.lastName + ", " + contact1.firstName;

		app.zGetActiveAccount().soapSend(
	                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
	                		"<cn >" +
	                			"<a n='firstName'>" + contact1.firstName +"</a>" +
	                			"<a n='lastName'>" + contact1.lastName +"</a>" +
	                			"<a n='email'>" + contact1.email + "</a>" +
                			"</cn>" +
	                "</CreateContactRequest>");

		ContactItem contact2 = new ContactItem();
		contact2.firstName = "First" + ZimbraSeleniumProperties.getUniqueString();
		contact2.lastName = "Last" + ZimbraSeleniumProperties.getUniqueString();
		contact2.email = "email" + ZimbraSeleniumProperties.getUniqueString() + "@domain.com";
		contact2.fileAs = contact2.lastName + ", " + contact2.firstName;

		app.zGetActiveAccount().soapSend(
	                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
	                		"<cn >" +
	                			"<a n='firstName'>" + contact2.firstName +"</a>" +
	                			"<a n='lastName'>" + contact2.lastName +"</a>" +
	                			"<a n='email'>" + contact2.email + "</a>" +
                			"</cn>" +
	                "</CreateContactRequest>");

		ContactItem contact3 = new ContactItem();
		contact3.firstName = "First" + ZimbraSeleniumProperties.getUniqueString();
		contact3.lastName = "Last" + ZimbraSeleniumProperties.getUniqueString();
		contact3.email = "email" + ZimbraSeleniumProperties.getUniqueString() + "@domain.com";
		contact3.fileAs = contact3.lastName + ", " + contact3.firstName;

		app.zGetActiveAccount().soapSend(
	                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
	                		"<cn >" +
	                			"<a n='firstName'>" + contact3.firstName +"</a>" +
	                			"<a n='lastName'>" + contact3.lastName +"</a>" +
	                			"<a n='email'>" + contact3.email + "</a>" +
                			"</cn>" +
	                "</CreateContactRequest>");


		//-- GUI
		
		// Refresh to get the contact into the client
		app.zPageAddressbook.zRefresh();
		
	    // Select the item
	    app.zPageAddressbook.zListItem(Action.A_CHECKBOX, contact1.fileAs);
	    app.zPageAddressbook.zListItem(Action.A_CHECKBOX, contact2.fileAs);
	    app.zPageAddressbook.zListItem(Action.A_CHECKBOX, contact3.fileAs);
	   
		//delete 3 contacts
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);
       
        
        //-- Verification
        
        //verify contacts deleted
        ContactItem actual1 = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact1.firstName);
        ZAssert.assertNull(actual1, "Verify the contact is deleted from the addressbook");
        
        ContactItem actual2 = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact1.firstName);
        ZAssert.assertNull(actual2, "Verify the contact is deleted from the addressbook");
        
        ContactItem actual3 = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact1.firstName);
        ZAssert.assertNull(actual3, "Verify the contact is deleted from the addressbook");



   	}
	
	
	
	
	
}
