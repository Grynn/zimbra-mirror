package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;

import com.zimbra.qa.selenium.framework.ui.*;

import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;



public class EditContactGroup extends AjaxCommonTest  {
	public EditContactGroup() {
		logger.info("New "+ EditContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage =  app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;		
		
	}
	
	@Test(	description = "Edit a contact group",
			groups = { "smoke"})
	public void EditContactGroup_01() throws HarnessException {
		
		  // Create a contact group via Soap
		ContactGroupItem group = CreateContactGroup.CreateContactGroupViaSoap(app);
	            
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
        
        // Select the item
        app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.fileAs);
		
		//Click Edit on Toolbar button	
        FormContactGroupNew formContactGroupNew = (FormContactGroupNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
	        
		ContactGroupItem newGroup = ContactGroupItem.generateContactItem(GenerateItemType.Basic);
							
		
		//clear the form, 
		formContactGroupNew.zReset();
		
        // Fill in the form
	    formContactGroupNew.zFill(newGroup);
	    
		// Save the contact
        formContactGroupNew.zSubmit();
		
        
        //verify toasted message Group Saved 
        String expectedMsg ="Group Saved";
        ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
        		        expectedMsg , "Verify toast message '" + expectedMsg + "'");
    
        //verify new contact group item is displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();   
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(newGroup.fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertTrue(isFileAsEqual, "Verify contact fileAs (" + group.fileAs + ") existed ");

        
		//verify old contact group not displayed
    	isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(group.fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact fileAs (" + group.fileAs + ") deleted");
      
        	    
	}

}

