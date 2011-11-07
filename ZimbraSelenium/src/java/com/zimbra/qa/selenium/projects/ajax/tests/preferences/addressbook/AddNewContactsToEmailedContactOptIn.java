package com.zimbra.qa.selenium.projects.ajax.tests.preferences.addressbook;

import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.DisplayContact;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;

public class AddNewContactsToEmailedContactOptIn extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public AddNewContactsToEmailedContactOptIn() {
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{				
		 		put("zimbraPrefAutoAddAddressEnabled", "TRUE");
			}
		};
	}

	/**
	 * Test case : Verify select checkbox works (e.g make the option changed to opt-out)
	 * @throws HarnessException
	 */
	@Test(description= " select the checkbox to toggle the opt-in option to opt-out ", groups= {"smoke" })
	public void UnSelectAutoAddAddressCheckbox() throws HarnessException {
		// Verify the status of the checkbox is TRUE
		ZAssert.assertTrue(app.zPagePreferences.zGetCheckboxStatus("zimbraPrefAutoAddAddressEnabled"),
				  "Verify if zimbraPrefAutoAddAddressEnabled is TRUE, the preference box is checked" );			
	
		// Uncheck the box
		app.zPagePreferences.zCheckboxSet("css=input[id$=_AUTO_ADD_ADDRESS]",false);
			
		// Click save
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);		
		
		// Verify the status of the checkbox is FALSE
		ZAssert.assertFalse(app.zPagePreferences.zGetCheckboxStatus("zimbraPrefAutoAddAddressEnabled"),
				  "Verify if zimbraPrefAutoAddAddressEnabled is FALSE, the preference box is unchecked" );			
	}
	/**
	 * Test case : Opt-in Add New Contacts To emailed contact
	 * Verify receivers' addresses of out-going mails automatically added to "Emailed Contacts" folder 
	 * @throws HarnessException
	 */
	@Test(description = " send message to 1 receiver, the address should be added into Emailed Contact", groups = { "smoke" })
	public void SendEmailTo1Receiver() throws HarnessException {
		// Go to Preferences->"Addressbook"
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.AddressBook);

		// Determine the status of the checkbox
		boolean checked = app.zPagePreferences.zGetCheckboxStatus("zimbraPrefAutoAddAddressEnabled");
	
		// Since zimbraPrefAutoAddAddressEnabled is set to TRUE, the checkbox should be checked
		ZAssert.assertTrue(checked, "Verify if zimbraPrefAutoAddAddressEnabled is TRUE, the preference box is checked" );
	
		// Send a message to the account A
		ZimbraAccount.AccountZWC().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
				"<m>" +
				"<e t='t' a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>" +
				"<su>subject"+ ZimbraSeleniumProperties.getUniqueString() + "</su>" +
				"<mp ct='text/plain'>" +
				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() + "</content>" +
				"</mp>" +
				"</m>" +
		        "</SendMsgRequest>");

		//Click Addressbook tab
		app.zPageAddressbook.zNavigateTo();
		
		//Select Emailed Contacts folder
		FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
	    app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, emailedContacts);
   	 
		//Verify accountA contact included in Emailed Contacts folder   	     
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
        String fileAs= ZimbraAccount.AccountA().EmailAddress.substring(0,ZimbraAccount.AccountA().EmailAddress.indexOf("@"));
        
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertTrue(isFileAsEqual, "Verify contact fileAs (" + fileAs + ") displayed in folder Emailed Contacts");
     
        // Select the contact 
		DisplayContact contactView = (DisplayContact) app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, fileAs);
	  
		// Verify contact fileAs + email displayed
		ZAssert.assertStringContains(contactView.zGetContactProperty(DisplayContact.Field.FileAs), fileAs, "Verify contact fileAs (" + fileAs + ") displayed");	
		
	    ZAssert.assertStringContains(contactView.zGetContactProperty(DisplayContact.Field.Email), ZimbraAccount.AccountA().EmailAddress, "Verify contact email (" + ZimbraAccount.AccountA().EmailAddress + ") displayed");	

	}

	
	/**
	 * Test case : Opt-in Add New Contacts To emailed contact
	 * Verify receivers' addresses of out-going mails automatically added to "Emailed Contacts" folder 
	 * @throws HarnessException
	 */
	@Test(description = " send message to 2 receiver, the addresses should be added into Emailed Contact", groups = { "functional" })
	public void SendEmailTo2Receivers() throws HarnessException {
		// Go to Preferences->"Addressbook"
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.AddressBook);

		// Determine the status of the checkbox
		boolean checked = app.zPagePreferences.zGetCheckboxStatus("zimbraPrefAutoAddAddressEnabled");
	
		// Since zimbraPrefAutoAddAddressEnabled is set to TRUE, the checkbox should be checked
		ZAssert.assertTrue(checked, "Verify if zimbraPrefAutoAddAddressEnabled is TRUE, the preference box is checked" );
	
		// Send a message to the account A
		ZimbraAccount.AccountZWC().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
				"<m>" +
				"<e t='t' a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>" +
				"<e t='t' a='"+ ZimbraAccount.AccountB().EmailAddress +"'/>" +
				"<su>subject"+ ZimbraSeleniumProperties.getUniqueString() + "</su>" +
				"<mp ct='text/plain'>" +
				"<content>content"+ ZimbraSeleniumProperties.getUniqueString() + "</content>" +
				"</mp>" +
				"</m>" +
		        "</SendMsgRequest>");

		//Click Addressbook tab
		app.zPageAddressbook.zNavigateTo();
		
		//Select Emailed Contacts folder
		FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
	    app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, emailedContacts);
   	 
		//Verify contacts included in Emailed Contacts folder   	     
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
        String fileAsA= ZimbraAccount.AccountA().EmailAddress.substring(0,ZimbraAccount.AccountA().EmailAddress.indexOf("@"));
        String fileAsB= ZimbraAccount.AccountB().EmailAddress.substring(0,ZimbraAccount.AccountB().EmailAddress.indexOf("@"));
           
		int count=0;
		for (ContactItem ci : contacts) {
			if ((ci.fileAs.equals(fileAsA)) ||
			    (ci.fileAs.equals(fileAsB)))	
				{
				 count++;	            
			}
		}
		
        ZAssert.assertTrue(count==2, "Verify contact fileAs (" + fileAsA + " " + fileAsB +  ") displayed in folder Emailed Contacts");
     
        // Select the contact 
		DisplayContact contactView = (DisplayContact) app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, fileAsA);
	  
		// Verify contact fileAs + email displayed
		ZAssert.assertStringContains(contactView.zGetContactProperty(DisplayContact.Field.FileAs), fileAsA, "Verify contact fileAs (" + fileAsA + ") displayed");	
		
	    ZAssert.assertStringContains(contactView.zGetContactProperty(DisplayContact.Field.Email), ZimbraAccount.AccountA().EmailAddress, "Verify contact email (" + ZimbraAccount.AccountA().EmailAddress + ") displayed");	

        // Select the contact 
         contactView = (DisplayContact) app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, fileAsB);
	  
		// Verify contact fileAs + email displayed
		ZAssert.assertStringContains(contactView.zGetContactProperty(DisplayContact.Field.FileAs), fileAsB, "Verify contact fileAs (" + fileAsA + ") displayed");	
		
	    ZAssert.assertStringContains(contactView.zGetContactProperty(DisplayContact.Field.Email), ZimbraAccount.AccountB().EmailAddress, "Verify contact email (" + ZimbraAccount.AccountB().EmailAddress + ") displayed");	

	}
	

	
}
