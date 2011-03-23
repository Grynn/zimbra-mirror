package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;


import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


public class MoveContactGroup extends AjaxCommonTest  {
	public MoveContactGroup() {
		logger.info("New "+ MoveContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	@Test(	description = "Move a contact group to different folder",
			groups = { "smoke" })
	public void MoveContactGroup_01() throws HarnessException {
		        
		FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
 		
		// Create a contact group via SOAP 
		ContactGroupItem group = CreateContactGroup.CreateContactGroupViaSoap(app);
	    		       
        // Refresh the view, to pick up the new contact group
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
       
        
        // Select the item
        app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.fileAs);


        //click Move icon 
        DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zToolbarPressButton(Button.B_MOVE);
     
        //enter the moved folder
        dialogContactMove.zClickTreeFolder(emailedContacts);
        dialogContactMove.zClickButton(Button.B_OK);
       
        //verify toasted message 1 contact greoup moved to "Emailed Contacts"
        Toaster toast = app.zPageMain.zGetToaster();
        String toastMsg = toast.zGetToastMessage();
        String expectedMsg = "1 contact group moved to \"Emailed Contacts\"";
        ZAssert.assertStringContains(toastMsg, expectedMsg , "Verify toast message '" + expectedMsg + "'");

        //verify moved contact not displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(group.fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact group fileAs (" + group.fileAs + ") not displayed");
        
 
   
   	}

}

