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
package com.zimbra.qa.selenium.projects.touch.tests.addressbook.contacts;


import java.awt.event.KeyEvent;
import java.util.HashMap;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;

import com.zimbra.qa.selenium.projects.touch.core.TouchCommonTest;
import com.zimbra.qa.selenium.projects.touch.ui.addressbook.MoveContactView;



public class MoveContact extends TouchCommonTest  {
	
	
	
	public MoveContact() {
		logger.info("New "+ MoveContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;
			
		
	}
	
	@Test(	description = "Move a contact item to EmailedContacts Address Book",
			groups = { "sanity" })
	public void MoveContact() throws HarnessException {

		//-- Data
		
		// fetch info of EmailedContacts AddressBook
		FolderItem emailedcontacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
		
		
		// create a contact item
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
		SleepUtil.sleepSmall();
		
		// select the contact from Contacts AddressBook
		String nameInList = contact.lastName + ", " + contact.firstName;
		String locator = "css=div[id^='ext-contactslistview'] div[class='zcs-contactList-name']:contains('"+nameInList+"')";
		app.zPageAddressbook.zClick(locator);
		
        // choose delete button from action menu
		MoveContactView mcv = (MoveContactView)app.zPageAddressbook.zToolbarPressPulldown(Button.B_ACTIONS,Button.B_MOVE);
        
		// choose target AddressBook which you move the contact to
        mcv.zTreeItem(Action.A_LEFTCLICK, emailedcontacts);
		
        
        //-- Verification
        
        //verify contact deleted from Contacts AddressBook
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "in:contacts AND #firstname:"+ contact.firstName);
        ZAssert.assertNull(actual, "Verify the contact is deleted from the addressbook");
        
        // Verify the contact in EmailedContacts AddressBook 
        actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "in:\"Emailed Contacts\" AND #firstname:"+ contact.firstName);
        ZAssert.assertNotNull(actual, "Verify the contact is in the trash");
        
   	}	
	
}
