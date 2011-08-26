package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.search;

import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.items.*;

import com.zimbra.qa.selenium.framework.ui.*;

import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.search.PageAllItemTypes;

public class ContactSearch extends AjaxCommonTest {
	
	
	public ContactSearch() {
		logger.info("New "+ ContactSearch.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;

	}
	
	@Test( description = "select all, search existed contact in Addressbook ",
			groups = { "functional" })
	public void SelectAllItemTypesSearchExistedContac() throws HarnessException {
		// Search a contact item 		
		ContactItem contactItem = app.zPageAddressbook.createUsingSOAPSelectContact(app, Action.A_LEFTCLICK);
		PageAllItemTypes resultPage = null;
		
		app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_ALL);
		app.zPageSearch.zAddSearchQuery(contactItem.firstName);
		resultPage = (PageAllItemTypes) app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		
		ArrayList<AllItemTypesItem> items = resultPage.zListItems();		        
        boolean isFound=false;
	      for (AllItemTypesItem item : items) {
		    if (item.from.equals(contactItem.fileAs)) {
		    	isFound = true;
		    	break;
	  	    }
	      }
			
        ZAssert.assertTrue(isFound, "Verify contact " + contactItem.fileAs + " displayed");
	}
	
	@Test( description = "select all, search gal contact ",
			groups = { "functional" })
	public void SelectAllItemTypesSearchGALContact() throws HarnessException {
	
		// Search a GAL item
		String name=ZimbraAccount.AccountA().DisplayName;
		app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_ALL);
		app.zPageSearch.zAddSearchQuery(name);
		PageAllItemTypes resultPage = (PageAllItemTypes) app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		
		ArrayList<AllItemTypesItem> items = resultPage.zListItems();		        

        boolean isFound=false;
	      for (AllItemTypesItem item : items) {
		    if (item.from.equals(ZimbraAccount.AccountA().DisplayName)) {
		    	isFound = true;
		    	break;
	  	    }
	      }
  
        ZAssert.assertTrue(isFound, "Verify GAL contact " + ZimbraAccount.AccountA().DisplayName + " displayed");

	}

	@Test(	description = "select contact, search a contact existing in addressbook  ",
			groups = { "functional" })
	public void searchExistedContact() throws HarnessException {
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
