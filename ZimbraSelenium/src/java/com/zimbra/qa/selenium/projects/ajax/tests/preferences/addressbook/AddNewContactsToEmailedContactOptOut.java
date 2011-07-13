package com.zimbra.qa.selenium.projects.ajax.tests.preferences.addressbook;

import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.DisplayContact;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;

public class AddNewContactsToEmailedContactOptOut extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public AddNewContactsToEmailedContactOptOut() {
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{				
				put("zimbraPrefAutoAddAddressEnabled", "FALSE");
			}
		};
	}

	
	/**
	 * Test case : Opt-out Add New Contacts To emailed contact
	 * Verify receiver' addresses of out-going mails not added to "Emailed Contacts" folder automatically 
	 * @throws HarnessException
	 */
	@Test(description = " send message to 1 receiver, the address should not be added into Emailed Contact", groups = { "smoke" })
	public void SendEmailTo1Receiver() throws HarnessException {
		// Go to "Addressbook"
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.AddressBook);

		// Determine the status of the checkbox
		boolean checked = app.zPagePreferences.zGetCheckboxStatus("zimbraPrefAutoAddAddressEnabled");
	
		// Since zimbraPrefAutoAddAddressEnabled is set to FALSE, the checkbox should not be checked
		ZAssert.assertFalse(checked, "Verify if zimbraPrefAutoAddAddressEnabled is TRUE, the preference box is checked" );
	
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
   	 
		//Verify accountA contact not included in Emailed Contacts folder           
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
        String fileAs= ZimbraAccount.AccountA().EmailAddress.substring(0,ZimbraAccount.AccountA().EmailAddress.indexOf("@"));
        
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact fileAs (" + fileAs + ") not displayed in folder Emailed Contacts");     
	}
	
	
}
