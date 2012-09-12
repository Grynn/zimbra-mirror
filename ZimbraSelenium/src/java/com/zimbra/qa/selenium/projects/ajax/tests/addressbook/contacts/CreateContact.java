package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.AbsToaster.Locators;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;


//TODO: add more in ContactItem.java

public class CreateContact extends AjaxCommonTest  {

	public CreateContact() {
		logger.info("New "+ CreateContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;		
		
	}
	
	

	//can be used for other classes such as DeleteContact, MoveContact
	public static ContactItem createBasicContact(AppAjaxClient app, FormContactNew formContactNew)throws HarnessException {
							
		// Create a contact Item
		ContactItem contactItem = ContactItem.generateContactItem(GenerateItemType.Basic);
	
		//verify form contact new page is displayed
		ZAssert.assertTrue(formContactNew.zIsActive(),"Verify new contact form is displayed");
		
        // Fill in the form
	formContactNew.zFill(contactItem);
	    
	// Save the contact
        formContactNew.zSubmit();
		
        //verify toasted message 'contact created'  
        //ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(), "Contact Created", "Verify toast message 'Contact Created'");
        Toaster toaster = app.zPageMain.zGetToaster();
	toaster.zWaitForElementVisible(Locators.ToastTextLocatorCSS);
	SleepUtil.sleepSmall();
	String toastMsg = toaster.sGetText(Locators.ToastTextLocatorCSS);
	String expectedMsg ="Contact Created";
	        
	ZAssert.assertStringContains(toastMsg, expectedMsg , "Verify toast message '" + expectedMsg + "'");

        //verify contact "file as" is displayed
        ZAssert.assertTrue(app.zPageAddressbook.zIsContactDisplayed(contactItem), "Verify contact fileAs (" + contactItem.fileAs + ") displayed ");

	    //verify location is System folder "Contacts"
		ZAssert.assertEquals(app.zPageAddressbook.sGetText("css=td.companyFolder"), SystemFolder.Contacts.getName(), "Verify location (folder) is " + SystemFolder.Contacts.getName());

		return contactItem;
	}
	
	private DialogWarning clickCancel(ContactItem contactItem, FormContactNew formContactNew) throws HarnessException {
			
        // Fill in the form
	    formContactNew.zFill(contactItem);
	
	    // Click Cancel
	    DialogWarning dialogWarning = (DialogWarning) app.zPageAddressbook.zToolbarPressButton(Button.B_CANCEL);
	    
	    //Verify title Warning and content "Do you want to save changes?"
	    String text="Warning";
	    ZAssert.assertEquals(text,dialogWarning.zGetWarningTitle()," Verify title is " + text);
	    text = "Do you want to save changes?";
	    ZAssert.assertEquals(text,dialogWarning.zGetWarningContent()," Verify content is " + text);
	
	    return dialogWarning;
	}
	
	@Test(	description = "Create a basic contact item by click New in page Addressbook ",
			groups = { "sanity" })
	public void ClickNew() throws HarnessException {				
		FormContactNew formContactNew = (FormContactNew)app.zPageAddressbook.zToolbarPressButton(Button.B_NEW);

		createBasicContact(app, formContactNew);		
	}
	
	@Test(	description = "Create a basic contact item by use PullDown Menu->Contacts",
			groups = { "functional" })
	public void CreateContactFromPulldownMenu() throws HarnessException {				
		FormContactNew formContactNew = (FormContactNew)app.zPageAddressbook.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_CONTACT);
		
		createBasicContact(app, formContactNew);		
	}
	
	@Test(	description = "Cancel creating a contact item - Click Yes",
			groups = { "functional" })
	public void CancelCreateContactClickYes() throws HarnessException {				
		FormContactNew formContactNew = (FormContactNew)app.zPageAddressbook.zToolbarPressButton(Button.B_NEW);
		
		// Create a contact Item
		ContactItem contactItem = ContactItem.generateContactItem(GenerateItemType.Basic);
	
		DialogWarning dialogWarning = clickCancel(contactItem,formContactNew);
	    		
	    // Click Yes in popup dialog 
	    dialogWarning.zClickButton(Button.B_YES);
	    	    
	    //verify toasted message 'contact created'  
        Toaster toast = app.zPageMain.zGetToaster();
        String toastMsg = toast.zGetToastMessage();
        ZAssert.assertStringContains(toastMsg, "Contact Created", "Verify toast message 'Contact Created'");
  
	    // Verify contact  created  	    
        ZAssert.assertTrue(app.zPageAddressbook.zIsContactDisplayed(contactItem), "Verify contact fileAs (" + contactItem.fileAs + ") displayed ");


	}

	@Test(	description = "Cancel creating a contact item - Click No",
			groups = { "functional" })
	public void CancelCreateContactClickNo() throws HarnessException {				
		FormContactNew formContactNew = (FormContactNew)app.zPageAddressbook.zToolbarPressButton(Button.B_NEW);
		
		// Create a contact Item
		ContactItem contactItem = ContactItem.generateContactItem(GenerateItemType.Basic);
	
		DialogWarning dialogWarning = clickCancel(contactItem,formContactNew);
	    
	    // Click No in popup dialog 
	    dialogWarning.zClickButton(Button.B_NO);

	    // Verify contact not created
        ZAssert.assertFalse(app.zPageAddressbook.zIsContactDisplayed(contactItem), "Verify contact fileAs (" + contactItem.fileAs + ") not displayed ");


	}

	@Test(	description = "Cancel creating a contact item - Click Cancel",
			groups = { "functional" })
	public void CancelCreateContactClickCancel() throws HarnessException {		
		FormContactNew formContactNew = (FormContactNew)app.zPageAddressbook.zToolbarPressButton(Button.B_NEW);
		
		// Create a contact Item
		ContactItem contactItem = ContactItem.generateContactItem(GenerateItemType.Basic);
	
		DialogWarning dialogWarning = clickCancel(contactItem,formContactNew);
    
	    // Click Cancel in popup dialog 
	    dialogWarning.zClickButton(Button.B_CANCEL);
	    	    
	    // Verify page not redirect
	    // or form contact new page is displayed
		ZAssert.assertTrue(formContactNew.zIsActive(),"Verify new contact form is displayed");
		
		//Verify firstname , lastname  not changed
        ZAssert.assertEquals(app.zPageAddressbook.sGetValue(FormContactNew.getLocator(FormContactNew.Locators.zFirstEditField)),contactItem.firstName, "Verify contact firstname (" + contactItem.firstName + ") not changed ");
        ZAssert.assertEquals(app.zPageAddressbook.sGetValue(FormContactNew.getLocator(FormContactNew.Locators.zLastEditField)),contactItem.lastName, "Verify contact lastname (" + contactItem.lastName + ") not changed ");


	}

	@Test(	description = "create a contact item with full attribute",
			groups = { "smoke" })
	public void CreateContactWithAllAttributes() throws HarnessException {		
		FormContactNew formContactNew = (FormContactNew)app.zPageAddressbook.zToolbarPressButton(Button.B_NEW);
		
		// Create a contact Item
		ContactItem contactItem = ContactItem.generateContactItem(GenerateItemType.AllAttributes);
	
		 //Verify form contact new page is displayed
		ZAssert.assertTrue(formContactNew.zIsActive(),"Verify new contact form is displayed");
		
		// show all hidden field for names:
		formContactNew.zDisplayHiddenName();
		
		// fill items
		formContactNew.zFill(contactItem);
		
		// Save the contact
        formContactNew.zSubmit();
		
        //verify toasted message 'contact created'  
        ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(), "Contact Created", "Verify toast message 'Contact Created'");

        
		//verify contact "file as" is displayed
        ZAssert.assertTrue(app.zPageAddressbook.zIsContactDisplayed(contactItem), "Verify contact fileAs (" + contactItem.fileAs + ") displayed ");

	}

}
