/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class DragAndDropContact extends AjaxCommonTest  {
	
	public DragAndDropContact() {
		logger.info("New "+ DragAndDropContact.class.getCanonicalName());
		
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Enable user preference checkboxes
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Move a contact item to sub-addressbook",
			groups = { "smoke" })
	public void DragAndDropContact_01() throws HarnessException {
		
		

		//-- Data
		
        // The Addressbook folder
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);

		// The addressbook
		String foldername = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='" + foldername +"' l='"+ root.getId() +"' view='contact'/>" +
					"</CreateFolderRequest>");
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

		// Create a contact item
		String firstName = "First" + ZimbraSeleniumProperties.getUniqueString();
		String lastName = "Last" + ZimbraSeleniumProperties.getUniqueString();
		String email = "email" + ZimbraSeleniumProperties.getUniqueString() + "@domain.com";

		app.zGetActiveAccount().soapSend(
	                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
	                		"<cn >" +
	                			"<a n='firstName'>" + firstName +"</a>" +
	                			"<a n='lastName'>" + lastName +"</a>" +
	                			"<a n='email'>" + email + "</a>" +
                			"</cn>" +
	                "</CreateContactRequest>");
		
		ContactItem contact = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:" + firstName);


		//-- GUI
		
		// Refresh to get the contact into the client
		app.zPageAddressbook.zRefresh();
		
		// DND
		app.zPageAddressbook.zDragAndDrop(
				"css=td#zlif__CNS-main__" + contact.getId() + "__fileas:contains("+ contact.fileAs + ")",
				"css=td#zti__main_Contacts__" + folder.getId() + "_textCell:contains("+ folder.getName() + ")");
       
        
        //-- Verification
        
        //verify contact deleted
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
        ZAssert.assertNotNull(actual, "Verify the contact is deleted from the addressbook");
        ZAssert.assertEquals(actual.getFolderId(), folder.getId(), "Verify the contact is in the Trash folder");
        

         
   	}
	

	
	@Test(	description = "Move a contact item to trash folder by drag and drop",
			groups = { "functional" })
	public void DnDToTrash() throws HarnessException {
		
		

		//-- Data
		
        // The Addressbook and Trash folder
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);


		// Create a contact item
		String firstName = "First" + ZimbraSeleniumProperties.getUniqueString();
		String lastName = "Last" + ZimbraSeleniumProperties.getUniqueString();
		String email = "email" + ZimbraSeleniumProperties.getUniqueString() + "@domain.com";

		app.zGetActiveAccount().soapSend(
	                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
	                		"<cn >" +
	                			"<a n='firstName'>" + firstName +"</a>" +
	                			"<a n='lastName'>" + lastName +"</a>" +
	                			"<a n='email'>" + email + "</a>" +
                			"</cn>" +
	                "</CreateContactRequest>");
		
		ContactItem contact = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:" + firstName);


		//-- GUI
		
		// Refresh to get the contact into the client
		app.zPageAddressbook.zRefresh();
		
		// DND
		app.zPageAddressbook.zDragAndDrop(
				"css=td#zlif__CNS-main__" + contact.getId() + "__fileas:contains("+ contact.fileAs + ")",
				"css=td#zti__main_Contacts__" + trash.getId() + "_textCell:contains("+ trash.getName() + ")");
       
        
        //-- Verification
        
        //verify contact deleted
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #firstname:"+ contact.firstName);
        ZAssert.assertNotNull(actual, "Verify the contact is deleted from the addressbook");
        ZAssert.assertEquals(actual.getFolderId(), trash.getId(), "Verify the contact is in the Trash folder");
        

   	}
	
	
}
