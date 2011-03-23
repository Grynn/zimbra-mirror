package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;


import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;


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

		String domain = "@zimbra.com";
		String groupName =  ZimbraSeleniumProperties.getUniqueString();
        String emailAddress1 = ZimbraSeleniumProperties.getUniqueString() + domain;
        String emailAddress2 = ZimbraSeleniumProperties.getUniqueString() + domain;
        
        // Create a contact group 
		ContactGroupItem group = new ContactGroupItem(groupName);
	    
		group.addDListMember(emailAddress1);
		group.addDListMember(emailAddress2);
	
        app.zGetActiveAccount().soapSend(
                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
                "<cn fileAsStr='" + groupName + "' >" +
                "<a n='type'>group</a>" +
                "<a n='nickname'>" + groupName +"</a>" +
                "<a n='dlist'>" + emailAddress1 + "," + emailAddress2 + "</a>" +
                "</cn>" +
                "</CreateContactRequest>");

        app.zGetActiveAccount().soapSelectNode("//mail:CreateContactResponse", 1);
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
        
        // Select the item
        app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, groupName);


        //delete contact group
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);
       
        
        //verify toasted message 1 contact group moved to Trash
        Toaster toast = app.zPageMain.zGetToaster();
        String toastMsg = toast.zGetToastMessage();
        String expectedMsg = "1 contact group moved to Trash";
        ZAssert.assertStringContains(toastMsg, expectedMsg , "Verify toast message '" + expectedMsg + "'");

        //verify deleted contact group not displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(groupName)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact group " + groupName + " deleted");
        
 
   
   	}

}
