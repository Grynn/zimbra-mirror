package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;



import java.util.List;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.ContactGroupItem;
import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

public class SearchContactGroup extends AjaxCommonTest {
	
	
	public SearchContactGroup() {
		logger.info("New "+ SearchContactGroup.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;

	}
	
	
	@Test(	description = "select contact option, search an existed contact group ",
			groups = { "deprecated" })
	public void searchGroupName() throws HarnessException {
		// Create a contact group via Soap 
		ContactGroupItem group = ContactGroupItem.createUsingSOAP(app);
	  
		// search for group name
		app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_CONTACTS);	 		
		app.zPageSearch.zAddSearchQuery(group.groupName);
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
				
	    List<ContactItem> contacts = app.zPageAddressbook.zListGetContacts(); 
        	
        ZAssert.assertTrue(contacts.size()==1, "Verify only 1 contact group displayed");		
        ZAssert.assertEquals(contacts.get(0).fileAs, group.groupName, "Verify contact group (" + group.groupName + ") is displayed");
	
	}

	@Test(	description = "select contact option, search  contact groups with same prefix ",
			groups = { "deprecated" })
	public void searchGroupsWithSameNamePrefix() throws HarnessException {
		// Create a contact group via Soap
		ContactGroupItem group1 = ContactGroupItem.createUsingSOAP(app);
		// Create a contact group via Soap
		ContactGroupItem group2 = ContactGroupItem.createUsingSOAP(app);
	
		// search for group names
		app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_CONTACTS);	 		
		app.zPageSearch.zAddSearchQuery("group");
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		
		// verify all 2 groups are displayed
		ZAssert.assertTrue(app.zPageAddressbook.zIsContactDisplayed(group1), "Verify contact " + group1.fileAs + " displayed");
		ZAssert.assertTrue(app.zPageAddressbook.zIsContactDisplayed(group2), "Verify contact " + group2.fileAs + " displayed");
					
	}


	@Test(	description = "select contact option, search a non-existed contact group ",
			groups = { "deprecated" })
	public void searchNonExistedGroupName() throws HarnessException {
		// Create a contact group via Soap 
		ContactGroupItem group = ContactGroupItem.createUsingSOAP(app);
		  
		// search for group name
		app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_CONTACTS);	 		
		app.zPageSearch.zAddSearchQuery(group.groupName + ZimbraSeleniumProperties.getUniqueString());	
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);		
		ZAssert.assertFalse(app.zPageAddressbook.zIsContactDisplayed(group), "Verify contact " + group.fileAs + " not displayed");
				
	}


	@Test(	description = "select contact option, search for a contact group with group member as keyword search ",
			groups = { "deprecated" })
	public void searchGroupMember() throws HarnessException {
		// Create a contact group via Soap 
		ContactGroupItem group = ContactGroupItem.createUsingSOAP(app);
	  		
		app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_CONTACTS);	 		

		// search for group members
		for (int i=0; i < group.dlist.size(); i++) {
			app.zPageSearch.zAddSearchQuery(group.dlist.get(i).firstName);
			app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
			ZAssert.assertFalse(app.zPageAddressbook.zIsContactDisplayed(group), "Verify contact " + group.fileAs + " displayed");

			app.zPageSearch.zAddSearchQuery(group.dlist.get(i).lastName);
			app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
			ZAssert.assertFalse(app.zPageAddressbook.zIsContactDisplayed(group), "Verify contact " + group.fileAs + " displayed");

			app.zPageSearch.zAddSearchQuery(group.dlist.get(i).company);
			app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
			ZAssert.assertFalse(app.zPageAddressbook.zIsContactDisplayed(group), "Verify contact " + group.fileAs + " displayed");

			app.zPageSearch.zAddSearchQuery(group.dlist.get(i).email.substring(0,group.dlist.get(i).email.indexOf('@')));
			app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
			ZAssert.assertFalse(app.zPageAddressbook.zIsContactDisplayed(group), "Verify contact " + group.fileAs + " displayed");

		
		}
				
	}


	
}
