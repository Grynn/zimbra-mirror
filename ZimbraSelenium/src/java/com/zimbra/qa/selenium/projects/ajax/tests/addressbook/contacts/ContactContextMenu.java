package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;


import java.util.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;
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
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_ADVANCED_SEARCH),"Verify advanced search in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_NEW_EMAIL),"Verify new email in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_EDIT),"Verify edit contact  in context menu");
        //TODO 
        //ZAssert.assertTrue(list.contains(ContextMenuItem.C_CONTACT_FORWARD),"Verify forward email in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_TAG),"Verify tag option in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_DELETE),"Verify delete option in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_MOVE),"Verify move option in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_PRINT),"Verify print option in context menu");

        //Verify all items enabled
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_SEARCH),"Verify contact search is enabled");
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_ADVANCED_SEARCH),"Verify contact advanced search is enabled");
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_NEW_EMAIL),"Verify new email is enabled");
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_EDIT),"Verify edit contact is enabled");

        //ZAssert.assertTrue(ContextMenuItem.C_CONTACT_FORWARD),"Verify forward email is enabled");

        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_TAG),"Verify tag option is enabled");
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_DELETE),"Verify delete option is enabled");
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_MOVE),"Verify move option is enabled");
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_PRINT),"Verify print option is enabled");
   	}
	

	@Test(	description = "Right click then click delete",
			groups = { "smoke" })
	public void Delete() throws HarnessException {
		
		ContactItem contactItem = createSelectARandomContactItem();
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

        //select delete option
        app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_DELETE, contactItem.fileAs);

        //verify toasted message 1 contact moved to Trash
        Toaster toast = app.zPageMain.zGetToaster();
        String toastMsg = toast.zGetToastMessage();
        ZAssert.assertStringContains(toastMsg, "1 contact moved to Trash", "Verify toast message '1 contact moved to Trash'");

        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        //verify deleted contact not displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(contactItem.fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact fileAs (" + contactItem.fileAs + ") deleted");
                
   	}

	@Test(	description = "Right click then click move",
			groups = { "functional" })
	public void Move() throws HarnessException {
		FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
		
		
		ContactItem contactItem = createSelectARandomContactItem();		
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		            
        //select move option
        DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_MOVE, contactItem.fileAs);
      
        
        //enter the moved folder
        dialogContactMove.zClickTreeFolder(emailedContacts);
        dialogContactMove.zClickButton(Button.B_OK);

        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        //verify moved contact not displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(contactItem.fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact fileAs (" + contactItem.fileAs + ") not displayed");
        
   	}
	@Test(	description = "Right click then click move, also verify toasted message",
			groups = { "smoke" })
	public void MoveVerifyToastedMessage() throws HarnessException {
		FolderItem emailedContacts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.EmailedContacts);
		
				
		ContactItem contactItem = createSelectARandomContactItem();
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		            
        //select move option
		DialogMove dialogContactMove = (DialogMove) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_MOVE, contactItem.fileAs);
      
        
        //enter the moved folder
        dialogContactMove.zClickTreeFolder(emailedContacts);		
        dialogContactMove.zClickButton(Button.B_OK);
       
        
        //verify toasted message 1 contact moved to "Emailed Contacts"
        Toaster toast = app.zPageMain.zGetToaster();
        String toastMsg = toast.zGetToastMessage();

        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        ZAssert.assertStringContains(toastMsg, "1 contact moved to \"Emailed Contacts\"", "Verify toast message '1 contact moved to \"Emailed Contacts\"'");

        //verify moved contact not displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(contactItem.fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact fileAs (" + contactItem.fileAs + ") not displayed");
        
   	}

	@Test(	description = "Right click then click Edit",
			groups = { "smoke" })
	public void Edit() throws HarnessException {
		
		ContactItem contactItem = createSelectARandomContactItem();
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		//Click Edit contact	
        FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, contactItem.fileAs);        
	  	        
		ContactItem newContact = ContactItem.generateContactItem(GenerateItemType.Basic);							
		
		//clear the form, 
		formContactNew.zReset();
		
        // Fill in the form
	    formContactNew.zFill(newContact);
	    
		// Save the contact
        formContactNew.zSubmit();
		
        
        //verify toasted message Contact Saved
        Toaster toast = app.zPageMain.zGetToaster();
        String toastMsg = toast.zGetToastMessage();
        GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
        ZAssert.assertStringContains(toastMsg, "Contact Saved", "Verify toast message 'Contact Saved'");

        //verify new contact item is displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();   
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(newContact.fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertTrue(isFileAsEqual, "Verify contact fileAs (" + contactItem.fileAs + ") existed ");

        
		//verify old contact not displayed
    	isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(contactItem.fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact fileAs (" + contactItem.fileAs + ") deleted");      
        	             
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
        
        //Verify contactItem.email displayed in the "To" field
        ZAssert.assertTrue(app.zPageAddressbook.sGetText(FormMailNew.Locators.zBubbleToField).contains(contactItem.email), "Verify contact email displayed in field To - expected " + contactItem.email + " - was " + app.zPageAddressbook.sGetText(FormMailNew.Locators.zBubbleToField));
        
        //TODO: Verify send email
	}
	

	@Test(	description = "Right click then click Advanced Search",
			groups = { "smoke" })
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

	@Test(	description = "Right click then click Tag Contact->New Tag",
			groups = { "smoke" })	
	public void ClickTagContactNewTag() throws HarnessException {
		ContactItem contactItem = createSelectARandomContactItem();
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();
			
		//click Tag Contact->New Tag	
        DialogTag dialogTag = (DialogTag) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_NEWTAG , contactItem.fileAs);        
    	dialogTag.zSetTagName(tagName);
		dialogTag.zClickButton(Button.B_OK);		

		// Make sure the tag was created on the server (get the tag ID)
		app.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");;
		String tagID = app.zGetActiveAccount().soapSelectValue("//mail:GetTagResponse//mail:tag[@name='"+ tagName +"']", "id");

		// Make sure the tag was applied to the contact
		app.zGetActiveAccount().soapSend(
					"<GetContactsRequest xmlns='urn:zimbraMail'>" +
						"<cn id='"+ contactItem.getId() +"'/>" +
					"</GetContactsRequest>");
		
		String contactTags = app.zGetActiveAccount().soapSelectValue("//mail:GetContactsResponse//mail:cn", "t");
		 
		ZAssert.assertEquals(contactTags, tagID, "Verify the tag appears on the contact id=" +  contactItem.getId());
		
		//verify toasted message '1 contact tagged ...'
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg, "1 contact tagged \"" + tagName + "\"", "Verify toast message '" + "1 contact tagged \"" + tagName + "\"'" );
 
	}

	
	@Test(	description = "Right click then click Tag Contact->Remove Tag",
			groups = { "smoke" })	
	public void ClickTagContactRemoveTag() throws HarnessException {
		
		String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();
			
			// Create a tag via soap
		app.zGetActiveAccount().soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" +
             	"<tag name='"+ tagName +"' color='1' />" +
             "</CreateTagRequest>");
		String tagid = app.zGetActiveAccount().soapSelectValue("//mail:CreateTagResponse/mail:tag", "id");

				
		ContactItem contactItem = createSelectARandomContactItem(tagid);
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

      //click Tag Contact->Remove Tag	
      app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_TAG, Button.O_TAG_REMOVETAG , contactItem.fileAs);
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      // The reason why this is not using app.zPageAddressbook.zSyncDesktopToZcs(); is because
      // 1. The very next step is doing the verification from backend
      // 2. Then the next step is verifying the toast message, if using the zSyncDesktopToZcs(),
      // it will wait for spinner to disappear, which could cause the toast
      // message to disappear before the spinner disappears
      //TODO: remove it as checked in ???????????????????????????????????????????????????????????
      //GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
     	
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail'>" +
		        "<a n='t'/>"+
		 		"<cn id='"+ contactItem.getId() +"'/>" +
				"</GetContactsRequest>");
	     
		
		
		String contactTag = app.zGetActiveAccount().soapSelectValue("//mail:GetContactsResponse//mail:cn", "t");
	
	    ZAssert.assertNull(contactTag, "Verify that the tag is removed from the contact. Expected: null. Actual:" + contactTag);
      
	    //verify toasted message Tag \"" + tagName + "\" removed from 1 contact
	    Toaster toast = app.zPageMain.zGetToaster();
	    String toastMsg = toast.zGetToastMessage();
	    ZAssert.assertStringContains(toastMsg, "Tag \"" + tagName + "\" removed from 1 contact", "Verify toast message Tag \"" + tagName + "\" removed from 1 contact");
 
	
	
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
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");

		ContactItem contactItem = createSelectAContactItem(app.zGetActiveAccount().DisplayName, lastName, app.zGetActiveAccount().EmailAddress);
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());		
		
		//Click Find Emails->Sent To Contact
        app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_SEARCH, Button.O_SEARCH_MAIL_SENT_TO_CONTACT , contactItem.fileAs);

        
        // Get all the messages in the inbox
		List<ConversationItem> messages = app.zPageMail.zListGetConversations();
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

		MailItem mail = MailItem.importFromSOAP(ZimbraAccount.AccountB(), "subject:("+ subject +")");

		ContactItem contactItem = createSelectAContactItem(app.zGetActiveAccount().DisplayName,lastName, ZimbraAccount.AccountB().EmailAddress);
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		
		
		//Click Find Emails->Received From Contact
        app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_SEARCH, Button.O_SEARCH_MAIL_RECEIVED_FROM_CONTACT, contactItem.fileAs);

        
        // Get all the messages in the inbox
		List<ConversationItem> messages = app.zPageMail.zListGetConversations();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		// TODO: "Verify the message is in the inbox");
                
	}
}

