package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;


import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;



public class DeleteContactGroup extends AjaxCommonTest  {
	public DeleteContactGroup() {
		logger.info("New "+ DeleteContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	@Test(	description = "Delete a contact group",
			groups = { "smoke" })
	public void DeleteContactGroup_01() throws HarnessException {

        
        // Create a contact group 
		ContactGroupItem group = CreateContactGroup.CreateContactGroupViaSoap(app);
	            
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
        
        // Select the item
        app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.groupName);


        //delete contact group
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);
       
        
        //verify toasted message 1 contact group moved to Trash
        String expectedMsg = "1 contact group moved to Trash";
        ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
		        expectedMsg , "Verify toast message '" + expectedMsg + "'");

        //verify deleted contact group not displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(group.groupName)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact group " + group.groupName + " deleted");
        
 
   
   	}

}
