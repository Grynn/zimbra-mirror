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
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;


import java.awt.event.KeyEvent;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;





public class DeleteContactGroup extends AjaxCommonTest  {
	public DeleteContactGroup() {
		logger.info("New "+ DeleteContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Enable user preference checkboxes
		super.startingAccountPreferences = new HashMap<String , String>() {
			private static final long serialVersionUID = -263733102718446576L;

		{
		    	put("zimbraPrefShowSelectionCheckbox", "TRUE");		         
		   }};				
		
	}
	
	@Test(	description = "Delete a contact group by click Delete button on toolbar",
			groups = { "smoke" })
	public void ClickDeleteOnToolbar() throws HarnessException {

		//--  Data
		
		// The trash folder
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		
		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());
		
        //delete contact group by click Delete button on toolbar
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

        
        //-- Verification
        
        ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group.getName());
        ZAssert.assertNotNull(actual, "Verify the contact group exists");

        // Verify the contact group is in the trash
        ZAssert.assertEquals(actual.getFolderId(), trash.getId(), "Verify the contact group is in the trash");
        
   	}

	@Test(	description = "Delete a contact group by click Delete on Context Menu",
			groups = { "functional" })
	public void ClickDeleteOnContextMenu() throws HarnessException {


		//--  Data
		
		// The trash folder
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		
		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		

		//delete contact group by click Delete on Context menu
        app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_DELETE, group.getName());

        
        //-- Verification
        
        ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group.getName());
        ZAssert.assertNotNull(actual, "Verify the contact group exists");
        
        // Verify the contact group is in the trash
        ZAssert.assertEquals(actual.getFolderId(), trash.getId(), "Verify the contact group is in the trash");
        
           
   	}

	@Test(	description = "Delete a contact group selected by checkbox by click Delete button on toolbar",
			groups = { "functional" })
	public void DeleteContactGroupSelectedWithCheckbox() throws HarnessException {


		//--  Data
		
		// The trash folder
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		
		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, group.getName());
		
        //delete contact group by click Delete button on toolbar
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

        
        //-- Verification
        
        ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group.getName());
        ZAssert.assertNotNull(actual, "Verify the contact group exists");
        
        // Verify the contact group is in the trash
        ZAssert.assertEquals(actual.getFolderId(), trash.getId(), "Verify the contact group is in the trash");
        
           
   	}

	@Test(	description = "Delete a contact group use shortcut Del",
			groups = { "functional" })
	public void UseShortcutDel() throws HarnessException {

		//--  Data
		
		// The trash folder
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		
		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());
		
        //delete contact group by click shortcut Del
		 app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_DELETE);

        
        //-- Verification
        
        ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group.getName());
        ZAssert.assertNotNull(actual, "Verify the contact group exists");

        // Verify the contact group is in the trash
        ZAssert.assertEquals(actual.getFolderId(), trash.getId(), "Verify the contact group is in the trash");
        

   	}
	
	@Test(	description = "Delete a contact group use shortcut backspace",
			groups = { "functional" })
	public void  UseShortcutBackspace() throws HarnessException {

		//--  Data
		
		// The trash folder
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		
		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());
		
        //delete contact group by click shortcut Del
		 app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_BACK_SPACE);

        
        //-- Verification
        
        ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group.getName());
        ZAssert.assertNotNull(actual, "Verify the contact group exists");

        // Verify the contact group is in the trash
        ZAssert.assertEquals(actual.getFolderId(), trash.getId(), "Verify the contact group is in the trash");
        

   	}

	@Test(	description = "Delete multiple contact groups at once",
			groups = { "functional" })
	public void DeleteMultipleContactGroups() throws HarnessException {


		//--  Data
		
		// The trash folder
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		
		// Create a contact group
		ContactGroupItem group1 = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		ContactGroupItem group2 = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		ContactGroupItem group3 = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, group1.getName());
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, group2.getName());
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, group3.getName());
		
        //delete contact group by click Delete button on toolbar
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

        
        //-- Verification
        
        ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group1.getName());
        ZAssert.assertNotNull(actual, "Verify the contact group exists");
        ZAssert.assertEquals(actual.getFolderId(), trash.getId(), "Verify the contact group is in the trash");

        actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group2.getName());
        ZAssert.assertNotNull(actual, "Verify the contact group exists");
        ZAssert.assertEquals(actual.getFolderId(), trash.getId(), "Verify the contact group is in the trash");

        actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group3.getName());
        ZAssert.assertNotNull(actual, "Verify the contact group exists");
        ZAssert.assertEquals(actual.getFolderId(), trash.getId(), "Verify the contact group is in the trash");
        
           

	}
	

	@Test(	description = "Delete contact + contact group at once",
			groups = { "functional" })
	public void DeleteMixOfContactAndGroup() throws HarnessException {


		//--  Data
		
		// The trash folder
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		
		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, group.getName());
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, contact.getName());
		
        //delete contact group by click Delete button on toolbar
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

        
        //-- Verification
        
        ContactGroupItem actual1 = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group.getName());
        ZAssert.assertNotNull(actual1, "Verify the contact group exists");
        ZAssert.assertEquals(actual1.getFolderId(), trash.getId(), "Verify the contact group is in the trash");

        ContactItem actual2 = ContactItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #firstname:"+ contact.firstName);
        ZAssert.assertNotNull(actual2, "Verify the contact exists");
        ZAssert.assertEquals(actual2.getFolderId(), trash.getId(), "Verify the contact is in the trash");

	}
	
	@Test(	description = "Move a contact group to folder Trash by expand Move dropdown then select Trash",
			groups = { "functional" })
	public void MoveToTrashFromMoveDropdownOnToolbar() throws HarnessException {


		//--  Data
		
		// The trash folder
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		
		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());
		
        //delete contact group by click Delete button on toolbar
        app.zPageAddressbook.zToolbarPressPulldown(Button.B_MOVE, trash);

        
        //-- Verification
        
        ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group.getName());
        ZAssert.assertNotNull(actual, "Verify the contact group exists");
        ZAssert.assertEquals(actual.getFolderId(), trash.getId(), "Verify the contact group is in the trash");

 
   	}
	

}
