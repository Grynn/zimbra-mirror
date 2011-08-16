package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.search;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

public class ContactSearch extends AjaxCommonTest {
	
	
	public ContactSearch() {
		logger.info("New "+ ContactSearch.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;

	}
	
	@Test( description = "select all, search contact in Addressbook + gal ",
			groups = { "functional" })
	public void searchAll() throws HarnessException {
		// Create a contact via soap 
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
		DisplayAllItemTypesSearchResults resultView = null;
		
		app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_ALL);
		app.zPageSearch.zAddSearchQuery(contactItem.firstName);
		resultView = (DisplayAllItemTypesSearchResults) app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		//TODO :verify result
		//ZAssert.assertTrue(re, "Verify contact " + contactItem.fileAs + " displayed");
		
		String name=ZimbraAccount.AccountA().DisplayName;
		app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_ALL);
		app.zPageSearch.zAddSearchQuery(name);
		resultView = (DisplayAllItemTypesSearchResults) app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
         
        boolean isFound=false;
	      for (ContactItem ci : contacts) {
		    if (ci.fileAs.equals(name)) {
		    	isFound = true;
		    	break;
	  	    }
	      }
			
        ZAssert.assertTrue(isFound, "Verify contact " + name + " displayed");

	}

	@Test(	description = "select contact, search a contact existing in addressbook  ",
			groups = { "functional" })
	public void searchContactInAddressbook() throws HarnessException {
		// Create a contact via soap 
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
 
		app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_CONTACTS);	 		
		app.zPageSearch.zAddSearchQuery(contactItem.firstName);
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		ZAssert.assertTrue(app.zPageAddressbook.zIsContactDisplayed(contactItem), "Verify contact " + contactItem.fileAs + " displayed");
				
		app.zPageSearch.zAddSearchQuery(contactItem.lastName);
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		ZAssert.assertTrue(app.zPageAddressbook.zIsContactDisplayed(contactItem), "Verify contact " + contactItem.fileAs + " displayed");
	
		app.zPageSearch.zAddSearchQuery(contactItem.email.substring(0,contactItem.email.indexOf('@')));
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		ZAssert.assertTrue(app.zPageAddressbook.zIsContactDisplayed(contactItem), "Verify contact " + contactItem.fileAs + " displayed");
	}
	
	@Test(	description = "select contact, search a non-existing contact",
			groups = { "functional" })
	public void searchNonExistContact() throws HarnessException {
		// Create a contact via soap 
		ContactItem contactItem = ContactItem.generateContactItem(GenerateItemType.Basic);
		
		app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_CONTACTS);	 		
		app.zPageSearch.zAddSearchQuery(contactItem.firstName);
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		ZAssert.assertFalse(app.zPageAddressbook.zIsContactDisplayed(contactItem), "Verify contact " + contactItem.fileAs + " not displayed");

	}

	@Test (	description = "select GAL, search a contact in GAL ",
			groups = { "functional" })
	public void searchGAL() throws HarnessException {
		String name=ZimbraAccount.AccountA().DisplayName;
		app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_GAL);			
		app.zPageSearch.zAddSearchQuery(name);
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		
		List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(ContactItem.GAL_IMAGE_CLASS); 
 	           
        boolean isFound=false;
	      for (ContactItem ci : contacts) {
		    if (ci.fileAs.equals(name)) {
		    	isFound = true;
		    	break;
	  	    }
	      }
			
        ZAssert.assertTrue(isFound, "Verify contact " + name + " displayed");


	}
}
