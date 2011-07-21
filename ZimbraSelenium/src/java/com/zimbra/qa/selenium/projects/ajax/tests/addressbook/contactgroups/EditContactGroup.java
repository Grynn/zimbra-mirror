package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.ExecuteHarnessMain;
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
			if (ci.fileAs.equals(newGroup.groupName)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertTrue(isFileAsEqual, "Verify contact fileAs (" + newGroup.groupName + ") existed ");

        
		//verify old contact group not displayed
    	isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(group.groupName)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact fileAs (" + group.groupName + ") deleted");
      	
	}
	
	
	@Test(	description = "Edit a contact group by click Edit on Toolbar button",
			groups = { "smoke"})
	public void ClickToolbarEdit() throws HarnessException {
		
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
		
		//Click Edit on Toolbar button	
        FormContactGroupNew formContactGroupNew = (FormContactGroupNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
    
        //Edit the contact group 
        EditGroup(formContactGroupNew, group);
		        	    
	}

	@Test(	description = "Edit a contact group by click Edit Group on Context Menu ",
			groups = { "functional"})
	public void ClickContextMenuEditGroup() throws HarnessException {
		
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
		
		//Right-Click, then Edit Group on Context Menu 	
        FormContactGroupNew formContactGroupNew = (FormContactGroupNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, group.fileAs);        
    	
        //Edit the contact group
        EditGroup(formContactGroupNew, group);
		        	    
	}

	@Test(	description = "Edit a contact group by double click on the contact group  ",
			groups = { "functional"})
	public void DoubleClickContactGroup() throws HarnessException {
		
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
		
		//Double-click the contact group 	
        FormContactGroupNew formContactGroupNew = (FormContactGroupNew) app.zPageAddressbook.zListItem(Action.A_DOUBLECLICK, group.fileAs);        
    	
        //Edit the contact group
        EditGroup(formContactGroupNew, group);
		        	    
	}

} 

