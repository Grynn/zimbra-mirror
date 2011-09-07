package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;
import java.util.List;

import org.testng.annotations.Test;


import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;

import com.zimbra.qa.selenium.framework.ui.*;

import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;



public class EditContactGroup extends AjaxCommonTest  {
	public EditContactGroup() {
		logger.info("New "+ EditContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage =  app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;		
		
	}
   
	private DialogWarning EditClickClose(ContactGroupItem newGroup, FormContactGroupNew formContactGroupNew) throws HarnessException {
		
		//clear the form, 
		formContactGroupNew.zReset();
		
        // Fill in the form
	    formContactGroupNew.zFill(newGroup);
	    
	        
    	// Click Close
	    DialogWarning dialogWarning = (DialogWarning) app.zPageAddressbook.zToolbarPressButton(Button.B_CANCEL);
	    
	    //Verify title Warning and content "Do you want to save changes?"
	    String text="Warning";
	    ZAssert.assertEquals(text,dialogWarning.zGetWarningTitle()," Verify title is " + text);
	    text = "Do you want to save changes?";
	    ZAssert.assertEquals(text,dialogWarning.zGetWarningContent()," Verify content is " + text);
	
	    return dialogWarning;
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

	@Test(	description = "Cancel Editing a contact group by click Close",
			groups = { "functional"})
	public void NoEditClickToolbarClose() throws HarnessException {
		
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
		
		//Click Edit on Toolbar button	
        app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
  
		//Click Close on Toolbar button	
        app.zPageAddressbook.zToolbarPressButton(Button.B_CLOSE);
             	
        // Select the contact group
		DisplayContactGroup groupView = (DisplayContactGroup) app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.fileAs);
	  
		ZAssert.assertStringContains(groupView.zGetContactProperty(DisplayContactGroup.Field.Company), group.fileAs  , "Verify contact group email (" + group.fileAs + ") displayed");	
		
		for (int i=0; i<group.dlist.size(); i++) {
	       ZAssert.assertStringContains(groupView.zGetContactProperty(DisplayContactGroup.Field.Email), group.dlist.get(i).email, "Verify contact group email (" + group.dlist.get(i) + ") displayed");	
		}         
	}

	@Test(	description = "Cancel an edited contact group by click Close, then click No",
			groups = { "functional"})
	public void ClickToolbarCloseThenClickNo() throws HarnessException {
		
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
		
	    //generate the new contact group
		ContactGroupItem newGroup = ContactGroupItem.generateContactItem(GenerateItemType.Basic);
				
		//Right-Click, then Edit Group on Context Menu 	
        FormContactGroupNew formContactGroupNew = (FormContactGroupNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, group.fileAs);        
  
        //Click Close on Toolbar button	
	    DialogWarning dialogWarning = EditClickClose(newGroup,formContactGroupNew);
	    
	    // Click No in popup dialog 
	    dialogWarning.zClickButton(Button.B_NO);
	    
	    // Select the contact group
		DisplayContactGroup groupView = (DisplayContactGroup) app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.fileAs);
	  
		ZAssert.assertStringContains(groupView.zGetContactProperty(DisplayContactGroup.Field.Company), group.fileAs  , "Verify contact group email (" + group.fileAs + ") displayed");	
		
		for (int i=0; i<group.dlist.size(); i++) {
	       ZAssert.assertStringContains(groupView.zGetContactProperty(DisplayContactGroup.Field.Email), group.dlist.get(i).email, "Verify contact group email (" + group.dlist.get(i) + ") displayed");	
		}     
	}

	@Test(	description = "Cancel an edited contact by click Close, then click Cancel",
			groups = { "functional"})
	public void ClickToolbarCloseThenClickCancel() throws HarnessException {
		
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
		
	    //generate the new contact group
		ContactGroupItem newGroup = ContactGroupItem.generateContactItem(GenerateItemType.Basic);
				
		//Right-Click, then Edit Group on Context Menu 	
        FormContactGroupNew formContactGroupNew = (FormContactGroupNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, group.fileAs);        
  
        //Click Close on Toolbar button	
	    DialogWarning dialogWarning = EditClickClose(newGroup,formContactGroupNew);
       
	    //Click Cancel in popup dialog 
	    dialogWarning.zClickButton(Button.B_CANCEL);
	 
	    //Verify form contact is active
	    ZAssert.assertTrue(formContactGroupNew.zIsActive(),"Verify contact form is active");   
     }

	@Test(	description = "Cancel an edited contact by click Close, then click Yes",
			groups = { "functional"})
	public void ClickToolbarCloseThenClickYes() throws HarnessException {
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app, Action.A_LEFTCLICK);
		
	    //generate the new contact group
		ContactGroupItem newGroup = ContactGroupItem.generateContactItem(GenerateItemType.Basic);
				
		//Right-Click, then Edit Group on Context Menu 	
        FormContactGroupNew formContactGroupNew = (FormContactGroupNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, group.fileAs);        
  
        //Click Close on Toolbar button	
	    DialogWarning dialogWarning = EditClickClose(newGroup,formContactGroupNew);
   
	    // Click Yes in popup dialog 
	    dialogWarning.zClickButton(Button.B_YES);

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
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact fileAs (" + group.groupName + ") not displayed");

        // Select the new group
		DisplayContactGroup contactView = (DisplayContactGroup) app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, newGroup.fileAs);
	  
		ZAssert.assertNotNull(contactView," Verify contact " + newGroup + " is displayed");
	}

} 

