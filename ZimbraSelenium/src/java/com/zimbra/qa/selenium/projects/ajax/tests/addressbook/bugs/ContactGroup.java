package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.bugs;



import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactGroupNew.Field;


public class ContactGroup extends AjaxCommonTest  {

	public ContactGroup() {
		logger.info("New "+ ContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;		
		
	}
		
	
	/*
	 * http://bugzilla.zimbra.com/show_bug.cgi?id=60652#c0
	 * 
	 * 1)Login to ZWC
	 * 2)Create a new contact group, make sure you have some contacts created initially.
	 * 3)In the Right Panel observe the entries from GAL are self populated but when 
	 *   you select contacts from the "in:" drop down the contacts are not populated.
	 *   It shows "No results found" message.
	 */
	@Test(	description = "Contacts are not populated while creating a new contact group",
			groups = { "functional" })
	public void Bug60652_ContactsGetPopulated() throws HarnessException {
		
		//-- Data
		
		String groupname = "group" + ZimbraSeleniumProperties.getUniqueString();
		
		// Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// open contact group form
		FormContactGroupNew formGroup = (FormContactGroupNew)app.zPageAddressbook.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_CONTACTGROUP);
        
		// Set the group name
		formGroup.zFillField(Field.GroupName, groupname);
			
		// select contacts option
		formGroup.zToolbarPressPulldown(Button.B_CONTACTGROUP_SEARCH_TYPE, Button.O_CONTACTGROUP_SEARCH_CONTACTS);
		
		// Get the displayed list
		ArrayList<ContactItem> ciArray = formGroup.zListGroupRows();
		
		boolean found=false;
		for (ContactItem ci: ciArray) {
			if ( ci.getName().equals(contact.getName()) ) {
				found = true;
				break;
			}
		}
        
		ZAssert.assertTrue(found, "Verify contact " + contact.getName() + " populated");

		// add all to the email list
		formGroup.zToolbarPressButton(Button.B_CONTACTGROUP_ADD_ADDRESS);
		
		formGroup.zToolbarPressButton(Button.B_SAVE);
		
		formGroup.zClick(FormContactGroupNew.Locators.zAddButton);

		
	}

	@Test(	description = "Click Delete Toolbar button in Edit Contact Group form",
			groups = { "functional", "matt" })
	public void Bug62026_ClickDeleteToolbarButtonInEditContactGroupForm() throws HarnessException {

		DistributionListItem dlist = DistributionListItem.createDistributionListItem(app.zGetActiveAccount());
		
		//-- Data
		
		// The trash folder
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Trash);
		
		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Edit the group
		FormContactGroupNew form = (FormContactGroupNew)app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, group.getName());
		
		// In the form, click "Delete"
		form.zToolbarPressButton(Button.B_DELETE);
		
		

		//-- Verification
		
		// Verify the group is in the trash
		ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere "+ group.getName());
		ZAssert.assertNotNull(actual, "Verify the group stil exists");
		
		ZAssert.assertEquals(actual.getFolderId(), trash.getId(), "Verify the group is located in trash");


   	}
	
	@Test( description="create a new contact group from GAL search result",
		   groups=("smoke"))
	public void Bug66623_AddingAGALToAContactGroup() throws HarnessException{
		String email=ZimbraAccount.AccountB().EmailAddress.substring(0,ZimbraAccount.AccountB().EmailAddress.indexOf('@'));
		
		// search for a GAL
		app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_GAL); 		
		app.zPageSearch.zAddSearchQuery(email);	
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);		
					
	 
		//Right click and select New Contact Group
	 	 SimpleFormContactGroupNew simpleFormGroup = (SimpleFormContactGroupNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_CONTACTGROUP, Button.O_NEW_CONTACTGROUP , email);     
	
	 	//Create a contact item
	 	ContactItem contactItem = new ContactItem(email);
	 	contactItem.email = ZimbraAccount.AccountB().EmailAddress;
	 	
	 	//Create contact group 
		 ContactGroupItem newGroup = new ContactGroupItem("group_" + ZimbraSeleniumProperties.getUniqueString().substring(8));
		 
		 //Add the member to the group	 
		 newGroup.addDListMember(contactItem);
	
		 
	 	//fill in group name 
		simpleFormGroup.zFill(newGroup);
		   
		//click Save
		simpleFormGroup.zSubmit(); 
		
		//verify toasted message 'group created'  
        String expectedMsg ="Group Created";
        ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
        		        expectedMsg , "Verify toast message '" + expectedMsg + "'");
    
	    
        

        //click "Contacts" folder
		FolderItem folder= FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Contacts);
	    app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, folder);
		
	    //verify group name is displayed		        
			List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();
			boolean isFileAsEqual=false;
			for (ContactItem ci : contacts) {
				if (ci.fileAs.equals(ci.fileAs)) {
		            isFileAsEqual = true;	
					break;
				}
			}
		ZAssert.assertTrue(isFileAsEqual, "Verify group name (" + newGroup.fileAs + ") displayed");

	    //verify the location is System folder "Contacts"
		ZAssert.assertEquals(app.zPageAddressbook.sGetText("css=td.companyFolder"), SystemFolder.Contacts.getName(), "Verify location (folder) is " + SystemFolder.Contacts.getName());
		
	}
	     
	

}
