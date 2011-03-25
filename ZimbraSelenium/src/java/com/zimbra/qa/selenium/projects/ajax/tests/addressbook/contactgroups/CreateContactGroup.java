package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;


public class CreateContactGroup extends AjaxCommonTest  {

	public CreateContactGroup() {
		logger.info("New "+ CreateContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;		
		
	}
	
	
	//To be used by other test cases
	public static ContactGroupItem CreateContactGroupViaSoap(AppAjaxClient app, String ... tagIdArray ) throws HarnessException {
		String tagParam ="";
		if (tagIdArray.length == 1) {
			tagParam = " t='" + tagIdArray[0] + "'";
		}

        // Create a contact group 
		ContactGroupItem group = ContactGroupItem.generateContactItem(GenerateItemType.Basic);
	
        app.zGetActiveAccount().soapSend(
                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
                "<cn " + tagParam + " fileAsStr='" + group.groupName + "' >" +
                "<a n='type'>group</a>" +
                "<a n='nickname'>" + group.groupName +"</a>" +
                "<a n='dlist'>" + group.getDList() + "</a>" +
                "</cn>" +
                "</CreateContactRequest>");

        return group;
	}
	
	@Test(	description = "Create a basic contact group",
			groups = { "sanity" })
	public void CreateContactGroup_01() throws HarnessException {			

	    // Create a contact group 
		ContactGroupItem group = ContactGroupItem.generateContactItem(GenerateItemType.Basic);
	
		FormContactGroupNew formGroup = (FormContactGroupNew)app.zPageAddressbook.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_CONTACTGROUP);
    
		//fill in group name and email addresses
		formGroup.zFill(group);
	   
		//click Save
		formGroup.zSubmit(); 
	
		//verify toasted message 'group created'  
        String expectedMsg ="Group Created";
        ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
        		        expectedMsg , "Verify toast message '" + expectedMsg + "'");
    
	      //verify contact "file as" is displayed
		List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(group.fileAs)) {
	            isFileAsEqual = true;	
				break;
			}
		}
	
		//verify group name is displayed		
        ZAssert.assertTrue(isFileAsEqual, "Verify contact fileAs (" + group.fileAs + ") existed ");

	    //TODO verify group members are displayed
			
	}

}
