package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;





import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContactItem.GenerateItemType;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.DisplayContactGroup;


public class ViewContactGroup extends AjaxCommonTest  {
	public ViewContactGroup() {
		logger.info("New "+ ViewContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	
	@Test(	description = "View a contact group",
			groups = { "smoke" })
	public void DisplayContactGroupInfo() throws HarnessException {
		         		
		
		//-- Data
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount(), GenerateItemType.Basic);
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Select the contact group
		DisplayContactGroup groupView = (DisplayContactGroup) app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());
		
		

		//-- Verification
		
		// verify groupname
		ZAssert.assertStringContains(groupView.zGetContactProperty(DisplayContactGroup.Field.Company), group.fileAs  , "Verify contact group email (" + group.fileAs + ") displayed");	
		
		// verify group members
		for (ContactGroupItem.MemberItem m : group.getMemberList()) {
			
			String email = m.getValue();
			String locator = "css=div.ZmContactSplitView span[id$='']:contains('"+ email +"')";
			
			boolean present = app.zPageAddressbook.sIsElementPresent(locator);
			ZAssert.assertTrue(present, "Verify the member "+ email +" is present");
			
			boolean visible = app.zPageAddressbook.zIsVisiblePerPosition(locator, 0, 0);
			ZAssert.assertTrue(visible, "Verify the member "+ email +" is visible");
					
		}
		

	}


	
}

