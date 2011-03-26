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
	
	private void EditGroup(FormContactGroupNew formContactGroupNew , ContactGroupItem group) throws HarnessException {
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
	
	
	@Test(	description = "Edit a contact group by click Edit on Toolbar button",
			groups = { "smoke"})
	public void EditContactGroup_01() throws HarnessException {
		
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app);
		
		//Click Edit on Toolbar button	
        FormContactGroupNew formContactGroupNew = (FormContactGroupNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
    
        //Edit a contact group 
        EditGroup(formContactGroupNew, group);
		        	    
	}

	@Test(	description = "Edit a contact group by click Edit Group on Context Menu ",
			groups = { "functional"})
	public void EditContactGroup_02() throws HarnessException {
		
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app);
		
		//Click Right-Click, then Edit Group on Context Menu 	
        FormContactGroupNew formContactGroupNew = (FormContactGroupNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, group.fileAs);        
    	
        //Edit a contact group
        EditGroup(formContactGroupNew, group);
		        	    
	}
}

