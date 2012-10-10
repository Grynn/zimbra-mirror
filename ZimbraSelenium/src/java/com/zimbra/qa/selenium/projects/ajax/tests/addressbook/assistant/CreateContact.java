package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.assistant;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogAssistant;


public class CreateContact extends AjaxCommonTest {
	
	public CreateContact() {
		logger.info("New "+ CreateContact.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;


	}
	
	@Test(	description = "Create a new conntact using the Zimbra Assistant",
			groups = { "deprecated" })
	public void CreateContact_01() throws HarnessException {
		
		// Create a contact item
		ContactItem contactItem = ContactItem.createContactItem(app.zGetActiveAccount(), GenerateItemType.Basic);
	
		String command = "contact " + contactItem.firstName + " " + contactItem.lastName + " " + contactItem.email;

	
		DialogAssistant assistant = (DialogAssistant)app.zPageAddressbook.zKeyboardShortcut(Shortcut.S_ASSISTANT);
		assistant.zEnterCommand(command);
		assistant.zClickButton(Button.B_OK);
		
	  
	    // verify contact created
		List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
 	           
        boolean isContactItemDisplayed=false;
	    for (ContactItem ci : contacts) {
		    if (ci.fileAs.toLowerCase().contains(contactItem.firstName)) 
			   {
              isContactItemDisplayed=true;
              break;
	  	    }
	      }
			
        ZAssert.assertTrue(isContactItemDisplayed, "Verify contact fileAs (" + contactItem.firstName + " displayed");
        

	}


}
