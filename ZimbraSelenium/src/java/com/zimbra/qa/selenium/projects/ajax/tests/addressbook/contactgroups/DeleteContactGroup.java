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
	
	private void VerifyContactGroupDeleted(ContactGroupItem group) throws HarnessException {
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
		  
		
	@Test(	description = "Delete a contact group by click Delete button on toolbar",
			groups = { "smoke" })
	public void DeleteContactGroup_01() throws HarnessException {

		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app);
	  
    
        //delete contact group by click Delete button on toolbar
        app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);
       
        //verify contact group deleted
        VerifyContactGroupDeleted(group);
           
   	}

	@Test(	description = "Delete a contact group by click Delete on Context Menu",
			groups = { "functional" })
	public void DeleteContactGroup_02() throws HarnessException {

		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app);
	  
    
        //delete contact group by click Delete on Context menu
        app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_DELETE, group.fileAs);
       
        //verify contact group deleted
        VerifyContactGroupDeleted(group);
           
   	}

}
