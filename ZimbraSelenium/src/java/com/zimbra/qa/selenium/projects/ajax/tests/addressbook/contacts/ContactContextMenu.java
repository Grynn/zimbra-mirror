package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;


import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;

public class ContactContextMenu extends AjaxCommonTest  {
	public ContactContextMenu() {
		logger.info("New "+ ContactContextMenu.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	private ContactItem createSelectAContactItem() throws HarnessException {

		String firstName = "first" + ZimbraSeleniumProperties.getUniqueString();		
		String lastName = "last" + ZimbraSeleniumProperties.getUniqueString();
	    String email = "email" +  ZimbraSeleniumProperties.getUniqueString() + "@zimbra.com";
		//default value for file as is last, first
		String fileAs = lastName + ", " + firstName;
	
        app.zGetActiveAccount().soapSend(
                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
                "<cn fileAsStr='" + fileAs + "' >" +
                "<a n='firstName'>" + firstName +"</a>" +
                "<a n='lastName'>" + lastName +"</a>" +
                "<a n='email'>" + email + "</a>" +               
                "</cn>" +            
                "</CreateContactRequest>");

        
        ContactItem contactItem = ContactItem.importFromSOAP(app.zGetActiveAccount(), "FIELD[lastname]:" + lastName + "");
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
                 
        return contactItem;		
	}
	
	
	@Test(	description = "Right click a contact to show a menu",
			groups = { "smoke" })
	public void ShowContextMenu() throws HarnessException {
		
		ContactItem contactItem = createSelectAContactItem();
		// Select the item
        // Right click to show the menu
        ContextMenu contextMenu= (ContextMenu) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, contactItem.fileAs); // contactItem.fileAs);
        SleepUtil.sleepMedium();
        
        List <ContextMenuItem> list = contextMenu.zListGetContextMenuItems(ContextMenu.zContacts);
        
        //verify all items in the context menu list
        ZAssert.assertTrue(list.contains(ContextMenuItem.C_CONTACT_SEARCH),"Verify contact search in context menu");
        ZAssert.assertTrue(list.contains(ContextMenuItem.C_CONTACT_ADVANCED_SEARCH),"Verify advanced search in context menu");
        ZAssert.assertTrue(list.contains(ContextMenuItem.C_CONTACT_NEW_EMAIL),"Verify new email in context menu");
        ZAssert.assertTrue(list.contains(ContextMenuItem.C_CONTACT_EDIT),"Verify edit contact  in context menu");
        //TODO 
        //ZAssert.assertTrue(list.contains(ContextMenuItem.C_CONTACT_FORWARD),"Verify forward email in context menu");
        ZAssert.assertTrue(list.contains(ContextMenuItem.C_CONTACT_TAG),"Verify tag option in context menu");
        ZAssert.assertTrue(list.contains(ContextMenuItem.C_CONTACT_DELETE),"Verify delete option in context menu");
        ZAssert.assertTrue(list.contains(ContextMenuItem.C_CONTACT_MOVE),"Verify move option in context menu");
        ZAssert.assertTrue(list.contains(ContextMenuItem.C_CONTACT_PRINT),"Verify print option in context menu");
        
   	}
	

	@Test(	description = "Right click then click delete",
			groups = { "smoke" })
	public void ClickDeleteOnContextMenu() throws HarnessException {
		
		ContactItem contactItem = createSelectAContactItem();
		
    	// Right click on the item to show the menu
        app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, contactItem.fileAs); // contactItem.fileAs);
        SleepUtil.sleepMedium();
            
        //select delete option
        app.zPageAddressbook.zContextMenu(ContextMenuItem.C_CONTACT_DELETE);
        SleepUtil.sleepSmall();
        
        //verify toasted message 1 contact moved to Trash
        ZAssert.assertStringContains(app.zPageAddressbook.sGetText("xpath=//div[@id='z_toast_text']"), "1 contact moved to Trash", "Verify toast message '1 contact moved to Trash'");

        //verify deleted contact not displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(contactItem.fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact fileAs (" + contactItem.fileAs + ") deleted");
                
   	}

	@Test(	description = "Right click then click move",
			groups = { "smoke" })
	public void ClickMoveOnContextMenu() throws HarnessException {
		
		ContactItem contactItem = createSelectAContactItem();
		
    	// Right click on the item to show the menu
        app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, contactItem.fileAs); // contactItem.fileAs);
        SleepUtil.sleepMedium();
            
        //select move option
        DialogContactMove dialogContactMove = (DialogContactMove) app.zPageAddressbook.zContextMenu(ContextMenuItem.C_CONTACT_MOVE);
        SleepUtil.sleepSmall();
        
        //enter the moved folder
        dialogContactMove.zEnterFolderName("Emailed Contacts");        		
        dialogContactMove.zClickButton(Button.B_OK);
        SleepUtil.sleepSmall();
        
        //verify toasted message 1 contact moved to "Emailed Contacts"
        ZAssert.assertStringContains(app.zPageAddressbook.sGetText("xpath=//div[@id='z_toast_text']"), "1 contact moved to \"Emailed Contacts\"", "Verify toast message '1 contact moved to \"Emailed Contacts\"'");

        //verify moved contact not displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(contactItem.fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact fileAs (" + contactItem.fileAs + ") not displayed");
        
   	}
    //TODO: select other options
}

