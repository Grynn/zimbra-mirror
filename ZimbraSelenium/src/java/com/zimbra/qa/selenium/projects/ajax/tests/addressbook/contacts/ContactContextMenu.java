package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
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
	
	private ContactItem createSelectAContactItem() throws HarnessException {

		String firstName = "first" + ZimbraSeleniumProperties.getUniqueString();		
		String lastName = "last" + ZimbraSeleniumProperties.getUniqueString();
	    String email = "email" +  ZimbraSeleniumProperties.getUniqueString() + "@zimbra.com";
		//default value for file as is last, first
		String fileAs = lastName + ", " + firstName;
	
        app.zGetActiveAccount().soapSend(
                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
                "<cn fileAsStr='" + fileAs + "' >" +
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
	
	
	@Test(	description = "Right click a contact to show a menu",
			groups = { "smoke" })
	public void ShowContextMenu() throws HarnessException {
		
		ContactItem contactItem = createSelectAContactItem();
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
	public void ClickDelete() throws HarnessException {
		
		ContactItem contactItem = createSelectAContactItem();
		
    	    
        //select delete option
        app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_DELETE, contactItem.fileAs);
       
        
        //verify toasted message 1 contact moved to Trash
        ZAssert.assertTrue(ToastedMessage.isContainedText("1 contact moved to Trash"), "Verify toast message '1 contact moved to Trash'");

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
			groups = { "smoke" })
	public void ClickMove() throws HarnessException {
		
		ContactItem contactItem = createSelectAContactItem();
		            
        //select move option
        DialogContactMove dialogContactMove = (DialogContactMove) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_MOVE, contactItem.fileAs);
      
        
        //enter the moved folder
        dialogContactMove.zEnterFolderName("Emailed Contacts");        		
        dialogContactMove.zClickButton(Button.B_OK);
       
        
        //verify toasted message 1 contact moved to "Emailed Contacts"
        ZAssert.assertTrue(ToastedMessage.isContainedText("1 contact moved to \"Emailed Contacts\""), "Verify toast message '1 contact moved to \"Emailed Contacts\"'");

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
	public void ClickEdit() throws HarnessException {
		
		ContactItem contactItem = createSelectAContactItem();
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
        ZAssert.assertTrue(ToastedMessage.isContainedText("Contact Saved"), "Verify toast message 'Contact Saved'");

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
	public void ClickNewEmail() throws HarnessException {
	
		ContactItem contactItem = createSelectAContactItem();

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
	public void ClickAdvancedSearch() throws HarnessException {
	
		ContactItem contactItem = createSelectAContactItem();

		//Click Advanced Search
        PageAdvancedSearch pageAdvancedSearch = (PageAdvancedSearch) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_SEARCHADVANCED, contactItem.fileAs);        
        
        //Verify Advanced Search page is active
        ZAssert.assertTrue(pageAdvancedSearch.zIsActive(),"Verify Advanced Search page is active");
                
	}

	@Test(	description = "Right click then click Print",
			groups = { "smoke-not-run-for-now" })	
	public void ClickPrint() throws HarnessException {
		ContactItem contactItem = createSelectAContactItem();

        PagePrint pagePrint = (PagePrint) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_PRINT, contactItem.fileAs);        
                
        //close Print Dialog 
        pagePrint.cancelPrintDialog();
        
        //verify first,last,email displayed in Print View
	    Assert.assertTrue(pagePrint.isContained("css=td[class='contactHeader']", contactItem.lastName + ", " + contactItem.firstName )," expected: " + contactItem.lastName + "," + contactItem.firstName + " not displayed in Print Page" + " was:"  );

	    Assert.assertTrue(pagePrint.isContained("css=td[class='contactOutput']", contactItem.email ), contactItem.firstName + " not displayed in Print Page");
	    
	}
	
}

