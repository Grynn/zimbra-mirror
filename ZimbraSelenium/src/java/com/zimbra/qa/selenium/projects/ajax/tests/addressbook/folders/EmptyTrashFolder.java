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
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.folders;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;


public class EmptyTrashFolder extends AjaxCommonTest {

	public EmptyTrashFolder() {
		logger.info("New "+ EmptyTrashFolder.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;
		
		// Enable user preference checkboxes
		super.startingAccountPreferences = null;
			
	}
	
	@Test(	description = "Delete a contact, group, and folder permanently by Empty Trash folder on context menu",
			groups = { "smoke" })
	public void ClickOK() throws HarnessException {

		//-- Data
		
		// The trash folder
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		
		
		// Create a contact group via Soap
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		
		// Move to trash
		app.zGetActiveAccount().soapSend(
				"<ItemActionRequest xmlns='urn:zimbraMail'>" +
						"<action op='move' id='"+ group.getId() +"' l='"+ trash.getId() +"'/>" +
				"</ItemActionRequest>");
		  
		
		// Create a contact via Soap
	    ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());
	    
	    // Move to trash
		app.zGetActiveAccount().soapSend(
				"<ItemActionRequest xmlns='urn:zimbraMail'>" +
						"<action op='move' id='"+ contact.getId() +"' l='"+ trash.getId() +"'/>" +
				"</ItemActionRequest>");

		
		// Create a new folder in trash
		String name = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='"+ name + "' view='contact' l='"+ trash.getId() +"'/>" +
				"</CreateFolderRequest>");



		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Now open empty trash dialog
		DialogWarning dialogWarning = (DialogWarning) app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_FOLDER_EMPTY ,trash);
	
		// Click OK
		dialogWarning.zClickButton(Button.B_OK);
		
		
		
		
		//-- Verification
		
		// Verify items are permanently deleted
		
        // Verify Trash folder is empty
        ContactItem actualContact = ContactItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #firstname:"+ contact.firstName);
        ZAssert.assertNull(actualContact, "Verify the contact is deleted");
        
        ContactGroupItem actualGroup = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere "+ group.getName());
        ZAssert.assertNull(actualGroup, "Verify the contact group is deleted");
        
        FolderItem actualAddressbook = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
        ZAssert.assertNull(actualAddressbook, "Verify the addressbook is deleted");

        
		
	}
	
	
	
	@Test(	description = "Cancel Empty Trash folder option",
			groups = { "functional" })
	public void ClickCancel() throws HarnessException {

		//-- Data
		
		// The trash folder
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		
		
		// Create a contact group via Soap
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		
		// Move to trash
		app.zGetActiveAccount().soapSend(
				"<ItemActionRequest xmlns='urn:zimbraMail'>" +
						"<action op='move' id='"+ group.getId() +"' l='"+ trash.getId() +"'/>" +
				"</ItemActionRequest>");
		  
		
		// Create a contact via Soap
	    ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());
	    
	    // Move to trash
		app.zGetActiveAccount().soapSend(
				"<ItemActionRequest xmlns='urn:zimbraMail'>" +
						"<action op='move' id='"+ contact.getId() +"' l='"+ trash.getId() +"'/>" +
				"</ItemActionRequest>");

		
		// Create a new folder in trash
		String name = "ab"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='"+ name + "' view='contact' l='"+ trash.getId() +"'/>" +
				"</CreateFolderRequest>");



		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Now open empty trash dialog
		DialogWarning dialogWarning = (DialogWarning) app.zTreeContacts.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_FOLDER_EMPTY ,trash);
	
		// Click OK
		dialogWarning.zClickButton(Button.B_CANCEL);
		
		
		
		
		//-- Verification
		
		// Verify items are permanently deleted
		
        // Verify Trash folder is empty
        ContactItem actualContact = ContactItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #firstname:"+ contact.firstName);
        ZAssert.assertNotNull(actualContact, "Verify the contact is not deleted");
        
        ContactGroupItem actualGroup = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere "+ group.getName());
        ZAssert.assertNotNull(actualGroup, "Verify the contact group is not deleted");
        
        FolderItem actualAddressbook = FolderItem.importFromSOAP(app.zGetActiveAccount(), name);
        ZAssert.assertNotNull(actualAddressbook, "Verify the addressbook is not deleted");

        
		
	
	}




}
