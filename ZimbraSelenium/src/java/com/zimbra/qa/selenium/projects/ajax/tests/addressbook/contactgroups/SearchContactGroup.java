package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;



import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.ContactGroupItem;
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
	
	
	@Test(	description = "select contact option, search a contact group ",
			groups = { "functional" })
	public void searchGroupName() throws HarnessException {
		// Create a contact group via Soap then select
		ContactGroupItem group = app.zPageAddressbook.createUsingSOAPSelectContactGroup(app,Action.A_LEFTCLICK);
	  
		// search for group name
		app.zPageSearch.zToolbarPressPulldown(Button.B_SEARCHTYPE, Button.O_SEARCHTYPE_CONTACTS);	 		
		app.zPageSearch.zAddSearchQuery(group.groupName);
		app.zPageSearch.zToolbarPressButton(Button.B_SEARCH);
		ZAssert.assertTrue(app.zPageAddressbook.zIsContactDisplayed(group), "Verify contact " + group.fileAs + " displayed");
				
	}
	


	
}
