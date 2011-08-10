package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.search;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.util.HarnessException;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class ContactSearch extends AjaxCommonTest {
	
	
	public ContactSearch() {
		logger.info("New "+ ContactSearch.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;

	}
	
	@Test( description = "select all, search contact + gal ",
			groups = { "functional" })
	public void searchAll() throws HarnessException {
		app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_ALL);
		app.zPageSearch.zAddSearchQuery("first");
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
	    //TODO verify contact displayed
		
		app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_ALL);
		app.zPageSearch.zAddSearchQuery("admin");
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		//TODO verify admin displayed
	}

	@Test(	description = "select contact, search contact in addressbook  ",
			groups = { "functional" })
	public void searchContactInAddressbook() throws HarnessException {
		// Create a contact item
		ContactItem contactItem = ContactItem.generateContactItem(GenerateItemType.Basic);
	
		app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_CONTACTS);	  
		app.zPageSearch.zAddSearchQuery(contactItem.firstName);
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		
		//TODO verify contact displayed
		app.zPageSearch.zAddSearchQuery(contactItem.lastName);
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		
		//TODO verify contact displayed
		app.zPageSearch.zAddSearchQuery(contactItem.email);
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		
		//TODO verify contact displayed
	}
	
	@Test (	description = "select GAL, search contact in GAL ",
			groups = { "functional" })
	public void searchGAL() throws HarnessException {
		app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_GAL);
		app.zPageSearch.zAddSearchQuery("admin");
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		
		//TODO: verify admin displayed
	}
}
