package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;


import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.items.ContextMenuItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.ContextMenu;
import com.zimbra.qa.selenium.projects.ajax.ui.PagePrint;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.PageAddressbook;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.search.PageAdvancedSearch;

public class ContactContextMenu extends AjaxCommonTest  {
	public ContactContextMenu() {
		logger.info("New "+ ContactContextMenu.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	private ContactItem createSelectAContactItem(String firstName, String lastName, String email, String ... tagIdArray ) throws HarnessException {
		String tagParam ="";
		//default value for file as is last, first
		String fileAs = lastName + ", " + firstName;
	
		if (tagIdArray.length == 1) {
			tagParam = " t='" + tagIdArray[0] + "'";
		}
        app.zGetActiveAccount().soapSend(
                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
                "<cn " + tagParam + " fileAsStr='" + fileAs + "' >" +
                "<a n='firstName'>" + firstName +"</a>" +
                "<a n='lastName'>" + lastName +"</a>" +
                "<a n='email'>" + email + "</a>" +               
                "</cn>" +            
                "</CreateContactRequest>");

        
        ContactItem contactItem = ContactItem.importFromSOAP(app.zGetActiveAccount(), "FIELD[lastname]:" + lastName + "");
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
                 
        return contactItem;		
	
		
	}

	private ContactItem createSelectARandomContactItem(String ... tagIdArray) throws HarnessException {

		String firstName = "first" + ZimbraSeleniumProperties.getUniqueString();		
		String lastName = "last" + ZimbraSeleniumProperties.getUniqueString();
	    String email = "email" +  ZimbraSeleniumProperties.getUniqueString() + "@zimbra.com";
	
	    return createSelectAContactItem(firstName, lastName, email, tagIdArray );
	}
	
	
	@Test(	description = "Right click a contact to show a menu",
			groups = { "smoke" })
	public void ShowContextMenu() throws HarnessException {
		
		ContactItem contactItem = createSelectARandomContactItem();
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		// Select the item
        // Right click to show the menu
        ContextMenu contextMenu= (ContextMenu) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, contactItem.fileAs); // contactItem.fileAs);
      
        
        ArrayList <ContextMenuItem> list = contextMenu.zListGetContextMenuItems(PageAddressbook.CONTEXT_MENU.class);
        
        //verify all items in the context menu list
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_SEARCH),"Verify contact search in context menu");
         ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_NEW_EMAIL),"Verify new email in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_EDIT),"Verify edit contact  in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_FORWARD),"Verify forward email in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_TAG),"Verify tag option in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_DELETE),"Verify delete option in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_MOVE),"Verify move option in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_PRINT),"Verify print option in context menu");

        //Verify all items enabled
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_SEARCH),"Verify contact search is enabled");
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_NEW_EMAIL),"Verify new email is enabled");
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_EDIT),"Verify edit contact is enabled");

        
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_FORWARD),"Verify forward email is disabled");
        
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_TAG),"Verify tag option is enabled");
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_DELETE),"Verify delete option is enabled");
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_MOVE),"Verify move option is enabled");
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_PRINT),"Verify print option is enabled");
   	}
	


	@Test(	description = "Right click then click New Email",
			groups = { "smoke" })
	public void NewEmail() throws HarnessException {
	
		ContactItem contactItem = createSelectARandomContactItem();
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		//Click New Email
        FormMailNew formMailNew = (FormMailNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_NEW, contactItem.fileAs);        
        
        //Verify Form New mail is active
        ZAssert.assertTrue(formMailNew.zIsActive(),"Verify Form New Mail is active");
        
        //Verify contactItem.first contactItem.last displayed in the "To" field
        ZAssert.assertTrue(app.zPageAddressbook.sGetText(FormMailNew.Locators.zBubbleToField).contains(contactItem.firstName + " "  + contactItem.lastName),
        		     "Verify contact email displayed in field To - expected " + contactItem.firstName + " " +  contactItem.lastName +" - was " + app.zPageAddressbook.sGetText(FormMailNew.Locators.zBubbleToField));
        
        //TODO: Verify send email
	}
	

	@Test(	description = "Right click then click Advanced Search",
			groups = { "deprecated" })
	public void AdvancedSearch() throws HarnessException {
	
		ContactItem contactItem = createSelectARandomContactItem();
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		//Click Advanced Search
        PageAdvancedSearch pageAdvancedSearch = (PageAdvancedSearch) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_SEARCHADVANCED, contactItem.fileAs);        
        
        //Verify Advanced Search page is active
        ZAssert.assertTrue(pageAdvancedSearch.zIsActive(),"Verify Advanced Search page is active");
                
        //close pageAdvancedSearch panel
        pageAdvancedSearch.zToolbarPressButton(Button.B_CLOSE);
	}

	@Test(	description = "Right click then click Print",
			groups = { "smoke-not-run-for-now" })	
	public void Print() throws HarnessException {
		ContactItem contactItem = createSelectARandomContactItem();
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

        PagePrint pagePrint = (PagePrint) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_PRINT, contactItem.fileAs);        
                
        //close Print Dialog 
        pagePrint.cancelPrintDialog();
        
        //verify first,last,email displayed in Print View
	    Assert.assertTrue(pagePrint.isContained("css=td[class='contactHeader']", contactItem.lastName + ", " + contactItem.firstName )," expected: " + contactItem.lastName + "," + contactItem.firstName + " not displayed in Print Page" + " was:"  );

	    Assert.assertTrue(pagePrint.isContained("css=td[class='contactOutput']", contactItem.email ), contactItem.firstName + " not displayed in Print Page");
	    
	}


	
	
	@Test(	description = "Right click then  click Find Emails->Sent To contact",
			groups = { "smoke" })
	public void FindEmailsSentToContact() throws HarnessException {

			
	    //Create  email sent to this contacts	
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String lastName = "lastname " + ZimbraSeleniumProperties.getUniqueString();
		
		// Send the message from AccountA to the ZWC user
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ "body" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");
		MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");

		ContactItem contactItem = createSelectAContactItem(app.zGetActiveAccount().getPref("displayName"), lastName, app.zGetActiveAccount().EmailAddress);
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());		
		
		//Click Find Emails->Sent To Contact
        app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_SEARCH, Button.O_SEARCH_MAIL_SENT_TO_CONTACT , contactItem.fileAs);

        
        // Get all the messages in the inbox
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		// TODO: "Verify the message is in the inbox");
                
	}
	
	@Test(	description = "Right click then  click Find Emails->Received From contact",
				groups = { "smoke" })
	public void FindEmailsReceivedFromContact() throws HarnessException {
		
	    //Create  email sent to this contacts	
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String lastName = "lastname " + ZimbraSeleniumProperties.getUniqueString();
		
		// Send the message from AccountB to the ZWC user
		ZimbraAccount.AccountB().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>"+ "body" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		MailItem.importFromSOAP(ZimbraAccount.AccountB(), "subject:("+ subject +")");

		ContactItem contactItem = createSelectAContactItem(app.zGetActiveAccount().getPref("displayName"),lastName, ZimbraAccount.AccountB().EmailAddress);
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		
		
		//Click Find Emails->Received From Contact
        app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_SEARCH, Button.O_SEARCH_MAIL_RECEIVED_FROM_CONTACT, contactItem.fileAs);

        
        // Get all the messages in the inbox
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		// TODO: "Verify the message is in the inbox");
                
	}
}

