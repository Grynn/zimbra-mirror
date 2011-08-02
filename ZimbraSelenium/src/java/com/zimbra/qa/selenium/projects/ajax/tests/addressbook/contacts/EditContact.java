package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;
import java.util.List;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;



public class EditContact extends AjaxCommonTest  {
	public EditContact() {
		logger.info("New "+ EditContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage =  app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;		
		
	}
	
	private DialogWarning EditClickClose( ContactItem newContact, FormContactNew formContactNew) throws HarnessException {
		
		//clear the form, 
		formContactNew.zReset();
		
        // Fill in the form
	    formContactNew.zFill(newContact);
   
	    // Click Close 
	    DialogWarning dialogWarning = (DialogWarning) app.zPageAddressbook.zToolbarPressButton(Button.B_CANCEL);
	    
	    //Verify title Warning and content "Do you want to save changes?"
	    String text="Warning";
	    ZAssert.assertEquals(text,dialogWarning.zGetWarningTitle()," Verify title is " + text);
	    text = "Do you want to save changes?";
	    ZAssert.assertEquals(text,dialogWarning.zGetWarningContent()," Verify content is " + text);
	
	    return dialogWarning;
	}
	
	private void EditAndVerify(FormContactNew formContactNew, ContactItem contactItem) 
	     throws HarnessException
	   {
        //generate the new contact
		ContactItem newContact = ContactItem.generateContactItem(GenerateItemType.Basic);

		//clear the form, 
		formContactNew.zReset();
		
        // Fill in the form
	    formContactNew.zFill(newContact);
	    
		// Save the contact
        formContactNew.zSubmit();
		
        
        //verify toasted message Contact Saved
        String expectedMsg ="Contact Saved";
        ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
        		        expectedMsg , "Verify toast message '" + expectedMsg + "'");
  
        
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
	
	@Test(	description = "Edit a contact item, click Edit on toolbar",
			groups = { "smoke"})
	public void ClickToolbarEdit() throws HarnessException {
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
		
		//Click Edit contact	
        FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
	        
		//Edit contact				
		EditAndVerify(formContactNew, contactItem);		
	}

	
	@Test(	description = "Edit a contact item, Right click then click Edit",
			groups = { "functional" })
	public void ClickContextMenuEdit() throws HarnessException {
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
		
		//Click Edit contact	
        FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, contactItem.fileAs);        

        //Edit contact					  	        		
		EditAndVerify(formContactNew, contactItem);
        	             
       }

	@Test(	description = "Edit a contact item, double click the contact",
			groups = { "functional" })
	public void DoubleClickContact() throws HarnessException {
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
		
		//Click Edit contact	
        FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, contactItem.fileAs);        
	  	        								
		//Edit contact
		EditAndVerify(formContactNew, contactItem);
        	             
       }

	@Test(	description = "Cancel Editing a contact by click Close",
			groups = { "functional"})
	public void NoEditClickToolbarClose() throws HarnessException {
		
	    ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
		
		//Click Edit 	
        app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
	
		//Click Close on Toolbar button	
        app.zPageAddressbook.zToolbarPressButton(Button.B_CLOSE);
             	
        // Select the contact 
		DisplayContact contactView = (DisplayContact) app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contactItem.fileAs);
	  
		ZAssert.assertNotNull(contactView," Verify contact " + contactItem + " is displayed");
	}

	@Test(	description = "Cancel an edited contact by click Close, then click No",
			groups = { "functional"})
	public void ClickToolbarCloseThenClickNo() throws HarnessException {
		
	    ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
		
	    //generate the new contact
		ContactItem newContact = ContactItem.generateContactItem(GenerateItemType.Basic);

		//Open edit contact form	
        FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, contactItem.fileAs);        

        //Click Close on Toolbar button	
	    DialogWarning dialogWarning = EditClickClose(newContact,formContactNew);
	    
	    // Click No in popup dialog 
	    dialogWarning.zClickButton(Button.B_NO);
	    
        // Select the contact 
		DisplayContact contactView = (DisplayContact) app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contactItem.fileAs);
	  
		ZAssert.assertNotNull(contactView," Verify contact " + contactItem + " is displayed");
	}

	@Test(	description = "Cancel an edited contact by click Close, then click Cancel",
			groups = { "functional"})
	public void ClickToolbarCloseThenClickCancel() throws HarnessException {
		
	    ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
		
	    //generate the new contact
		ContactItem newContact = ContactItem.generateContactItem(GenerateItemType.Basic);

		//Open edit contact form	
        FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, contactItem.fileAs);        

        //Click Close on Toolbar button	
	    DialogWarning dialogWarning = EditClickClose(newContact,formContactNew);
	    
	    //Click Cancel in popup dialog 
	    dialogWarning.zClickButton(Button.B_CANCEL);
	 
	    //Verify form contact is active
	    ZAssert.assertTrue(formContactNew.zIsActive(),"Verify contact form is active");   
     }

	@Test(	description = "Cancel an edited contact by click Close, then click Yes",
			groups = { "functional"})
	public void ClickToolbarCloseThenClickYes() throws HarnessException {
		
	    ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
		
	    //generate the new contact
		ContactItem newContact = ContactItem.generateContactItem(GenerateItemType.Basic);

		//Open edit contact form	
        FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, contactItem.fileAs);        

        //Click Close on Toolbar button	
	    DialogWarning dialogWarning = EditClickClose(newContact,formContactNew);
	    
	    // Click Yes in popup dialog 
	    dialogWarning.zClickButton(Button.B_YES);

        //verify toasted message Contact Saved
        String expectedMsg ="Contact Saved";
        ZAssert.assertStringContains(app.zPageMain.zGetToaster().zGetToastMessage(),
        		        expectedMsg , "Verify toast message '" + expectedMsg + "'");
  
        
        //verify old contact item is not displayed
        List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts();   
 	           
		boolean isFileAsEqual=false;
		for (ContactItem ci : contacts) {
			if (ci.fileAs.equals(contactItem.fileAs)) {
	            isFileAsEqual = true;	 
				break;
			}
		}
		
        ZAssert.assertFalse(isFileAsEqual, "Verify contact fileAs (" + contactItem.fileAs + ") not displayed");

        // Select the new contact 
		DisplayContact contactView = (DisplayContact) app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, newContact.fileAs);
	  
		ZAssert.assertNotNull(contactView," Verify contact " + newContact + " is displayed");
	}

	
}

