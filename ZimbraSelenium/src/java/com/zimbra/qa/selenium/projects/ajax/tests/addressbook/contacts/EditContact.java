package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactNew.Field;




public class EditContact extends AjaxCommonTest  {
	public EditContact() {
		logger.info("New "+ EditContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage =  app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;		
		
	}
	
	@Test(	description = "Edit a contact item, click Edit on toolbar",
			groups = { "smoke" })
	public void ClickToolbarEdit() throws HarnessException {
		
		//-- Data
		
		// Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount(), GenerateItemType.Basic);
		
		// The new first name
		String firstname = "new" + ZimbraSeleniumProperties.getUniqueString();
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.getName());
		
		// Click "Edit" from the toolbar
        FormContactNew form = (FormContactNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
		
		// Change the first name
        form.zFillField(Field.FirstName, firstname);
        form.zToolbarPressButton(Button.B_SAVE);
        
        
        
        //-- Verification
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ firstname);
        ZAssert.assertNotNull(actual, "Verify the contact is found");
        ZAssert.assertEquals(actual.firstName, firstname, "Verify the new first name is saved");
        
	}

	
	@Test(	description = "Edit a contact item, Right click then click Edit",
			groups = { "functional" })
	public void ClickContextMenuEdit() throws HarnessException {
		
		//-- Data
		
		// Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount(), GenerateItemType.Basic);
		
		// The new first name
		String firstname = "new" + ZimbraSeleniumProperties.getUniqueString();
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Rigth Click -> "Edit"
        FormContactNew form = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_EDIT, contact.getName());        
		
		// Change the first name
        form.zFillField(Field.FirstName, firstname);
        form.zToolbarPressButton(Button.B_SAVE);
        
        
        
        //-- Verification
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ firstname);
        ZAssert.assertNotNull(actual, "Verify the contact is found");
        ZAssert.assertEquals(actual.firstName, firstname, "Verify the new first name is saved");
        
	}

	@Test(	description = "Edit a contact item, double click the contact",
			groups = { "functional" })
	public void DoubleClickContact() throws HarnessException {
		
		//-- Data
		
		// Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount(), GenerateItemType.Basic);
		
		// The new first name
		String firstname = "new" + ZimbraSeleniumProperties.getUniqueString();
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Double click contact
        FormContactNew form = (FormContactNew) app.zPageAddressbook.zListItem(Action.A_DOUBLECLICK, contact.getName());        
		
		// Change the first name
        form.zFillField(Field.FirstName, firstname);
        form.zToolbarPressButton(Button.B_SAVE);
        
        
        
        //-- Verification
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ firstname);
        ZAssert.assertNotNull(actual, "Verify the contact is found");
        ZAssert.assertEquals(actual.firstName, firstname, "Verify the new first name is saved");
        

        
	}

	@Test(	description = "Cancel Editing a contact by click Close",
			groups = { "functional"})
	public void NoEditClickToolbarClose() throws HarnessException {
		
		//-- Data
		
		// Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount(), GenerateItemType.Basic);
		
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.getName());
		
		// Click "Edit" from the toolbar
        FormContactNew form = (FormContactNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
		
		// Change the first name
        form.zToolbarPressButton(Button.B_CLOSE);
        
        
        
        //-- Verification
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
        ZAssert.assertNotNull(actual, "Verify the contact is found");

	
	}

	@Test(	description = "Cancel an edited contact by click Close, then click No",
			groups = { "functional"})
	public void ClickToolbarCloseThenClickNo() throws HarnessException {
		
		//-- Data
		
		// Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount(), GenerateItemType.Basic);
		
		// The new first name
		String firstname = "new" + ZimbraSeleniumProperties.getUniqueString();
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.getName());
		
		// Click "Edit" from the toolbar
        FormContactNew form = (FormContactNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
		
		// Change the first name
        form.zFillField(Field.FirstName, firstname);
        
        // Click close
        DialogWarning dialog = (DialogWarning) form.zToolbarPressButton(Button.B_CLOSE);
        
        // Make sure the dialog is active
        dialog.zWaitForActive();
        
	    // Click No in popup dialog 
        dialog.zClickButton(Button.B_NO);

        
        //-- Verification
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
        ZAssert.assertNotNull(actual, "Verify the contact is found");

	

	}

	@Test(	description = "Cancel an edited contact by click Close, then click Cancel",
			groups = { "functional"})
	public void ClickToolbarCloseThenClickCancel() throws HarnessException {
		
		//-- Data
		
		// Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount(), GenerateItemType.Basic);
		
		// The new first name
		String firstname = "new" + ZimbraSeleniumProperties.getUniqueString();
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.getName());
		
		// Click "Edit" from the toolbar
        FormContactNew form = (FormContactNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
		
		// Change the first name
        form.zFillField(Field.FirstName, firstname);
        
        // Click close
        DialogWarning dialog = (DialogWarning) form.zToolbarPressButton(Button.B_CLOSE);
        
        // Make sure the dialog is active
        dialog.zWaitForActive();
        
	    // Click No in popup dialog 
        dialog.zClickButton(Button.B_CANCEL);
        
        // Click save
        form.zToolbarPressButton(Button.B_SAVE);

        
        //-- Verification
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ firstname);
        ZAssert.assertNotNull(actual, "Verify the contact is found");

	

     }

	@Test(	description = "Cancel an edited contact by click Close, then click Yes",
			groups = { "functional"})
	public void ClickToolbarCloseThenClickYes() throws HarnessException {
		
		//-- Data
		
		// Create a contact
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount(), GenerateItemType.Basic);
		
		// The new first name
		String firstname = "new" + ZimbraSeleniumProperties.getUniqueString();
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, contact.getName());
		
		// Click "Edit" from the toolbar
        FormContactNew form = (FormContactNew) app.zPageAddressbook.zToolbarPressButton(Button.B_EDIT);
		
		// Change the first name
        form.zFillField(Field.FirstName, firstname);
        
        // Click close
        DialogWarning dialog = (DialogWarning) form.zToolbarPressButton(Button.B_CLOSE);
        
        // Make sure the dialog is active
        dialog.zWaitForActive();
        
	    // Click No in popup dialog 
        dialog.zClickButton(Button.B_YES);
        

        
        //-- Verification
        ContactItem actual = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ firstname);
        ZAssert.assertNotNull(actual, "Verify the contact is found");

	

	}

	
}

