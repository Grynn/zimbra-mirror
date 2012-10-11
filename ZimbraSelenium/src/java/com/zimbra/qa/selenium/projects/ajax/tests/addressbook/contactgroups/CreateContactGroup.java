package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;


import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactGroupNew.Field;


public class CreateContactGroup extends AjaxCommonTest  {

	public CreateContactGroup() {
		logger.info("New "+ CreateContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;		
		
	}
		
	
	
	@Test(	description = "Create a basic contact group with 2 addresses.  New -> Contact Group",
			groups = { "sanity" })
	public void CreateContactGroup_01() throws HarnessException {			

		
		//-- Data
		
		String groupName = "group" + ZimbraSeleniumProperties.getUniqueString();
		String member1 = "m" + ZimbraSeleniumProperties.getUniqueString() + "@example.com";
		String member2 = "m" + ZimbraSeleniumProperties.getUniqueString() + "@example.com";
		
		
		//-- GUI
		
		
		// Refresh the addressbook
		app.zPageAddressbook.zRefresh();
		
		// open contact group form
		FormContactGroupNew form = (FormContactGroupNew)app.zPageAddressbook.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_CONTACTGROUP);
    
		// fill in group name and email addresses
		form.zFillField(Field.GroupName, groupName);
		form.zFillField(Field.FreeFormAddress, member1);
		form.zFillField(Field.FreeFormAddress, member2);
		form.zSubmit();
	   
	
		//-- Data Verification
		
		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='contact'>"
				+		"<query>#nickname:"+ groupName +"</query>"
				+	"</SearchRequest>");
		String contactId = app.zGetActiveAccount().soapSelectValue("//mail:cn", "id");
		
		ZAssert.assertNotNull(contactId, "Verify the contact is returned in the search");
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail'>"
			+		"<cn id='"+ contactId +"'/>"
			+	"</GetContactsRequest>");
	
		String nickname = app.zGetActiveAccount().soapSelectValue("//mail:cn//mail:a[@n='nickname']", null);
		String type = app.zGetActiveAccount().soapSelectValue("//mail:cn//mail:a[@n='type']", null);

		ZAssert.assertEquals(nickname, groupName, "Verify the group name is correct");
		ZAssert.assertEquals(type, "group", "Verify the type is set to 'group'");
		
		boolean found1 = false;
		boolean found2 = false;
		Element[] members = app.zGetActiveAccount().soapSelectNodes("//mail:cn//mail:m");
		for (Element e : members) {
			ZAssert.assertEquals(e.getAttribute("type", "notset"), "I", "Verify member type set to 'I'");
			String address = e.getAttribute("value", "notset");
			if ( address.equals(member1) ) {
				found1 = true;
			}
			if ( address.equals(member2) ) {
				found2 = true;
			}
		}

		ZAssert.assertTrue(found1, "Verify member 1 is in the group");
		ZAssert.assertTrue(found2, "Verify member 2 is in the group");
		
	}
	
	
	@Test(	description = "Create a basic contact group with 2 GAL addresses.",
			groups = { "functional" })
	public void CreateContactGroup_02() throws HarnessException {
		
		//-- Data
		
		String groupName = "group" + ZimbraSeleniumProperties.getUniqueString();

		
		//-- GUI
		
		
		// Refresh the addressbook
		app.zPageAddressbook.zRefresh();
		
		// open contact group form
		FormContactGroupNew form = (FormContactGroupNew)app.zPageAddressbook.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_CONTACTGROUP);

		// Add the group name
		form.zFillField(Field.GroupName, groupName);

	    // Select GAL search
		form.zToolbarPressPulldown(Button.B_CONTACTGROUP_SEARCH_TYPE, Button.O_CONTACTGROUP_SEARCH_GAL);
		form.zFillField(Field.SearchField, ZimbraAccount.AccountA().EmailAddress);
		form.zToolbarPressButton(Button.B_SEARCH);
		form.zToolbarPressButton(Button.B_CONTACTGROUP_ADD_SEARCH_RESULT);
		
		form.zToolbarPressPulldown(Button.B_CONTACTGROUP_SEARCH_TYPE, Button.O_CONTACTGROUP_SEARCH_GAL);
		form.zFillField(Field.SearchField, ZimbraAccount.AccountB().EmailAddress);
		form.zToolbarPressButton(Button.B_SEARCH);
		form.zToolbarPressButton(Button.B_CONTACTGROUP_ADD_SEARCH_RESULT);
		
		// Save the group
		form.zSubmit();
		


		//-- Verification
		
		ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "#nickname:"+ groupName);
		ZAssert.assertNotNull(actual, "Verify the contact group exists in the mailbox");
		
		ZAssert.assertContains(actual.getMemberList(), new ContactGroupItem.MemberItemGAL(ZimbraAccount.AccountA()), "Verify member 1 is in the group");
		ZAssert.assertContains(actual.getMemberList(), new ContactGroupItem.MemberItemGAL(ZimbraAccount.AccountB()), "Verify member 1 is in the group");

	}

	@Test(	description = "Create a contact group with existing contacts",
			groups = { "functional" })
	public void CreateContactGroup_03() throws HarnessException {

		
		//-- Data
		
		// The contact group name
		String groupName = "group" + ZimbraSeleniumProperties.getUniqueString();
		
		// Create two contacts
		ContactItem contact1 = ContactItem.createContactItem(app.zGetActiveAccount());
		ContactItem contact2 = ContactItem.createContactItem(app.zGetActiveAccount());

		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		//open contact group form
		FormContactGroupNew form = (FormContactGroupNew)app.zPageAddressbook.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_CONTACTGROUP);
        
		// fill in group name
		form.zFillField(Field.GroupName, groupName);
	
	    // Select Contact search
		form.zToolbarPressPulldown(Button.B_CONTACTGROUP_SEARCH_TYPE, Button.O_CONTACTGROUP_SEARCH_CONTACTS);
		form.zFillField(Field.SearchField, contact1.email);
		form.zToolbarPressButton(Button.B_SEARCH);
		form.zToolbarPressButton(Button.B_CONTACTGROUP_ADD_SEARCH_RESULT);
		
		form.zToolbarPressPulldown(Button.B_CONTACTGROUP_SEARCH_TYPE, Button.O_CONTACTGROUP_SEARCH_CONTACTS);
		form.zFillField(Field.SearchField, contact2.email);
		form.zToolbarPressButton(Button.B_SEARCH);
		form.zToolbarPressButton(Button.B_CONTACTGROUP_ADD_SEARCH_RESULT);
			
		// click Save
		form.zSubmit(); 
		


		//-- Verification
		
		ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "#nickname:"+ groupName);
		ZAssert.assertNotNull(actual, "Verify the contact group exists in the mailbox");
		
		ZAssert.assertContains(actual.getMemberList(), new ContactGroupItem.MemberItemContact(contact1), "Verify member 1 is in the group");
		ZAssert.assertContains(actual.getMemberList(), new ContactGroupItem.MemberItemContact(contact1), "Verify member 1 is in the group");

	}

	@Test(	description = "Create a contact group with GAL + existing contacts + new emails",
			groups = { "functional" })
	public void CreateContactGroup_04() throws HarnessException {			

		
		//-- Data
		
		// The contact group name
		String groupName = "group" + ZimbraSeleniumProperties.getUniqueString();
		
		// Create a contact
		ContactItem contact1 = ContactItem.createContactItem(app.zGetActiveAccount());

		// A general email address
		String member1 = "m" + ZimbraSeleniumProperties.getUniqueString() + "@example.com";

		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		//open contact group form
		FormContactGroupNew form = (FormContactGroupNew)app.zPageAddressbook.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_CONTACTGROUP);
        
		// fill in group name
		form.zFillField(Field.GroupName, groupName);
	
	    // Select Contact search
		form.zToolbarPressPulldown(Button.B_CONTACTGROUP_SEARCH_TYPE, Button.O_CONTACTGROUP_SEARCH_CONTACTS);
		form.zFillField(Field.SearchField, contact1.email);
		form.zToolbarPressButton(Button.B_SEARCH);
		form.zToolbarPressButton(Button.B_CONTACTGROUP_ADD_SEARCH_RESULT);
		
	    // Select GAL search
		form.zToolbarPressPulldown(Button.B_CONTACTGROUP_SEARCH_TYPE, Button.O_CONTACTGROUP_SEARCH_GAL);
		form.zFillField(Field.SearchField, ZimbraAccount.AccountA().EmailAddress);
		form.zToolbarPressButton(Button.B_SEARCH);
		form.zToolbarPressButton(Button.B_CONTACTGROUP_ADD_SEARCH_RESULT);
		
		// Add the free-form email
		form.zFillField(Field.FreeFormAddress, member1);

			
		// click Save
		form.zSubmit(); 
		


		//-- Verification
		
		ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "#nickname:"+ groupName);
		ZAssert.assertNotNull(actual, "Verify the contact group exists in the mailbox");
		
		ZAssert.assertContains(actual.getMemberList(), new ContactGroupItem.MemberItemContact(contact1), "Verify contact 1 is in the group");
		ZAssert.assertContains(actual.getMemberList(), new ContactGroupItem.MemberItemGAL(ZimbraAccount.AccountA()), "Verify GAL 1 is in the group");
		ZAssert.assertContains(actual.getMemberList(), new ContactGroupItem.MemberItemAddress(member1), "Verify GAL 1 is in the group");

	}


}
