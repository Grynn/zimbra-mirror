package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;
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
	public static ContactGroupItem CreateContactGroupViaSoap(AppAjaxClient app) throws HarnessException {

		String domain = "@zimbra.com";
		String groupName =  "group_" + ZimbraSeleniumProperties.getUniqueString();
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
        
        return group;
	}
	
	@Test(	description = "Create a basic contact group",
			groups = { "smoke" })
	public void CreateContactGroup_01() throws HarnessException {			
		FormContactGroupNew formGroup = (FormContactGroupNew)app.zPageAddressbook.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_CONTACTGROUP);

		String domain = "@zimbra.com";
		String groupName =  ZimbraSeleniumProperties.getUniqueString();
        String emailAddress1 = ZimbraSeleniumProperties.getUniqueString() + domain;
        String emailAddress2 = ZimbraSeleniumProperties.getUniqueString() + domain;
        
        // Create a contact group Item
		ContactGroupItem group = new ContactGroupItem(groupName);
	    
		group.addDListMember(emailAddress1);
		group.addDListMember(emailAddress2);
		
		//fill in group name and email addresses
		formGroup.zFill(group);
	   
		//click Save
		formGroup.zSubmit(); 
	
		//verify toasted message 'group created'  
        Toaster toast = app.zPageMain.zGetToaster();
        String toastMsg = toast.zGetToastMessage();
        ZAssert.assertStringContains(toastMsg, "Group Created", "Verify toast message 'Group Created'");

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
