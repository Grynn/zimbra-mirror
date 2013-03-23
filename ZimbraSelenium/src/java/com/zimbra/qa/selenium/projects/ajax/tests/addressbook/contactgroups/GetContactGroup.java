/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;





import java.util.*;
import java.util.Map.Entry;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.PageAddressbook;



/**
 * These test cases verify the contact lists display the correct contact gropus
 * @author Matt Rhoades
 *
 */
public class GetContactGroup extends AjaxCommonTest  {
	

	public GetContactGroup() {
		logger.info("New "+ GetContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	

	@Test(	description = "Click Alphabetbar button All: Verify contact groups started with digit and A-Z listed ",
			groups = { "smoke" })
	public void GetContactGroup_01_All_Button() throws HarnessException {
	
		String groupname;

		
		//-- Data
		String member = "email" + ZimbraSeleniumProperties.getUniqueString() + "@example.com";
		
		// Create three contact groups

		groupname = "Bp" + ZimbraSeleniumProperties.getUniqueString();
   		app.zGetActiveAccount().soapSend(
   	            "<CreateContactRequest xmlns='urn:zimbraMail'>" +
   	            		"<cn >" +
   	            			"<a n='type'>group</a>" +
   	            			"<a n='nickname'>" + groupname +"</a>" +
   	            			"<a n='fileAs'>8:" + groupname +"</a>" +
   	            			"<m type='I' value='" + member + "' />" +
   	            		"</cn>" +
   	            "</CreateContactRequest>");
   		ContactGroupItem group1 = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), groupname);
   		

		groupname = "5" + ZimbraSeleniumProperties.getUniqueString();
   		app.zGetActiveAccount().soapSend(
   	            "<CreateContactRequest xmlns='urn:zimbraMail'>" +
   	            		"<cn >" +
   	            			"<a n='type'>group</a>" +
   	            			"<a n='nickname'>" + groupname +"</a>" +
   	            			"<a n='fileAs'>8:" + groupname +"</a>" +
   	            			"<m type='I' value='" + member + "' />" +
   	            		"</cn>" +
   	            "</CreateContactRequest>");
   		ContactGroupItem group2 = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), groupname);
   		

		groupname = "b" + ZimbraSeleniumProperties.getUniqueString();
   		app.zGetActiveAccount().soapSend(
   	            "<CreateContactRequest xmlns='urn:zimbraMail'>" +
   	            		"<cn >" +
   	            			"<a n='type'>group</a>" +
   	            			"<a n='nickname'>" + groupname +"</a>" +
   	            			"<a n='fileAs'>8:" + groupname +"</a>" +
   	            			"<m type='I' value='" + member + "' />" +
   	            		"</cn>" +
   	            "</CreateContactRequest>");
   		ContactGroupItem group3 = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), groupname);
   		


   		//-- GUI
   		
   		app.zPageAddressbook.zRefresh();
   		
        //click All       
		app.zPageAddressbook.zToolbarPressButton(Button.B_AB_ALL);
					
		
		//-- Verification
		
		// Verify group name and members displayed
		List<ContactItem> items = app.zPageAddressbook.zListGetContacts();
		
		boolean found1 = false;
		boolean found2 = false;
		boolean found3 = false;
		
		for (ContactItem item : items) {
			
			if ( item.getName().equals(group1.getName()) ) {
				found1 = true;
			}
			if ( item.getName().equals(group2.getName()) ) {
				found2 = true;
			}
			if ( item.getName().equals(group3.getName()) ) {
				found3 = true;
			}

		}
		
		ZAssert.assertTrue(found1, "Verify contact group starting with B is listed");
		ZAssert.assertTrue(found2, "Verify contact group starting with 5 is listed");
		ZAssert.assertTrue(found3, "Verify contact group starting with b is listed");
		
	}

	@Test(	description = "Click Alphabetbar button All: Verify contact groups started with digit and A-Z listed ",
			groups = { "smoke" })
	public void GetContactGroup_03_123_Button() throws HarnessException {
	
		String groupname;

		
		//-- Data
		String member = "email" + ZimbraSeleniumProperties.getUniqueString() + "@example.com";
		
		// Create three contact groups

		groupname = "Bp" + ZimbraSeleniumProperties.getUniqueString();
   		app.zGetActiveAccount().soapSend(
   	            "<CreateContactRequest xmlns='urn:zimbraMail'>" +
   	            		"<cn >" +
   	            			"<a n='type'>group</a>" +
   	            			"<a n='nickname'>" + groupname +"</a>" +
   	            			"<a n='fileAs'>8:" + groupname +"</a>" +
   	            			"<m type='I' value='" + member + "' />" +
   	            		"</cn>" +
   	            "</CreateContactRequest>");
   		ContactGroupItem group1 = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), groupname);
   		

		groupname = "5" + ZimbraSeleniumProperties.getUniqueString();
   		app.zGetActiveAccount().soapSend(
   	            "<CreateContactRequest xmlns='urn:zimbraMail'>" +
   	            		"<cn >" +
   	            			"<a n='type'>group</a>" +
   	            			"<a n='nickname'>" + groupname +"</a>" +
   	            			"<a n='fileAs'>8:" + groupname +"</a>" +
   	            			"<m type='I' value='" + member + "' />" +
   	            		"</cn>" +
   	            "</CreateContactRequest>");
   		ContactGroupItem group2 = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), groupname);
   		

		groupname = "b" + ZimbraSeleniumProperties.getUniqueString();
   		app.zGetActiveAccount().soapSend(
   	            "<CreateContactRequest xmlns='urn:zimbraMail'>" +
   	            		"<cn >" +
   	            			"<a n='type'>group</a>" +
   	            			"<a n='nickname'>" + groupname +"</a>" +
   	            			"<a n='fileAs'>8:" + groupname +"</a>" +
   	            			"<m type='I' value='" + member + "' />" +
   	            		"</cn>" +
   	            "</CreateContactRequest>");
   		ContactGroupItem group3 = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), groupname);
   		


   		//-- GUI
   		
   		app.zPageAddressbook.zRefresh();
   		
        //click All       
		app.zPageAddressbook.zToolbarPressButton(Button.B_AB_123);
					
		
		//-- Verification
		
		// Verify group name and members displayed
		List<ContactItem> items = app.zPageAddressbook.zListGetContacts();
		
		boolean found1 = false;
		boolean found2 = false;
		boolean found3 = false;
		
		for (ContactItem item : items) {
			
			if ( item.getName().equals(group1.getName()) ) {
				found1 = true;
			}
			if ( item.getName().equals(group2.getName()) ) {
				found2 = true;
			}
			if ( item.getName().equals(group3.getName()) ) {
				found3 = true;
			}

		}
		
		ZAssert.assertFalse(found1, "Verify contact group starting with B is not listed");
		ZAssert.assertTrue(found2, "Verify contact group starting with 5 is listed");
		ZAssert.assertFalse(found3, "Verify contact group starting with b is not listed");
		
	}


	@Test(	description = "Click Alphabetbar button Z: Verify only contact groups started with Z|z is listed ",
			groups = { "functional" })
	public void GetContactGroup_02_B_Button() throws HarnessException {
		
		String groupname;

		
		//-- Data
		String member = "email" + ZimbraSeleniumProperties.getUniqueString() + "@example.com";
		
		// Create three contact groups

		groupname = "Bp" + ZimbraSeleniumProperties.getUniqueString();
   		app.zGetActiveAccount().soapSend(
   	            "<CreateContactRequest xmlns='urn:zimbraMail'>" +
   	            		"<cn >" +
   	            			"<a n='type'>group</a>" +
   	            			"<a n='nickname'>" + groupname +"</a>" +
   	            			"<a n='fileAs'>8:" + groupname +"</a>" +
   	            			"<m type='I' value='" + member + "' />" +
   	            		"</cn>" +
   	            "</CreateContactRequest>");
   		ContactGroupItem group1 = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), groupname);
   		

		groupname = "5" + ZimbraSeleniumProperties.getUniqueString();
   		app.zGetActiveAccount().soapSend(
   	            "<CreateContactRequest xmlns='urn:zimbraMail'>" +
   	            		"<cn >" +
   	            			"<a n='type'>group</a>" +
   	            			"<a n='nickname'>" + groupname +"</a>" +
   	            			"<a n='fileAs'>8:" + groupname +"</a>" +
   	            			"<m type='I' value='" + member + "' />" +
   	            		"</cn>" +
   	            "</CreateContactRequest>");
   		ContactGroupItem group2 = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), groupname);
   		

		groupname = "b" + ZimbraSeleniumProperties.getUniqueString();
   		app.zGetActiveAccount().soapSend(
   	            "<CreateContactRequest xmlns='urn:zimbraMail'>" +
   	            		"<cn >" +
   	            			"<a n='type'>group</a>" +
   	            			"<a n='nickname'>" + groupname +"</a>" +
   	            			"<a n='fileAs'>8:" + groupname +"</a>" +
   	            			"<m type='I' value='" + member + "' />" +
   	            		"</cn>" +
   	            "</CreateContactRequest>");
   		ContactGroupItem group3 = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), groupname);
   		


   		//-- GUI
   		
   		app.zPageAddressbook.zRefresh();
   		
        //click All       
		app.zPageAddressbook.zToolbarPressButton(Button.B_AB_B);
					
		
		//-- Verification
		
		// Verify group name and members displayed
		List<ContactItem> items = app.zPageAddressbook.zListGetContacts();
		
		boolean found1 = false;
		boolean found2 = false;
		boolean found3 = false;
		
		for (ContactItem item : items) {
			
			if ( item.getName().equals(group1.getName()) ) {
				found1 = true;
			}
			if ( item.getName().equals(group2.getName()) ) {
				found2 = true;
			}
			if ( item.getName().equals(group3.getName()) ) {
				found3 = true;
			}

		}
		
		ZAssert.assertTrue(found1, "Verify contact group starting with B is listed");
		ZAssert.assertFalse(found2, "Verify contact group starting with 5 is not listed");
		ZAssert.assertTrue(found3, "Verify contact group starting with b is listed");
		
	}
	
	@Test(	description = "Click all Alphabetbar buttons: Verify only contact group started with the alphabet is listed ",
			groups = { "functional" })
	public void GetContactGroup_04_Iterate_Buttons() throws HarnessException {
	
		// TODO: INTL ... this test case might breaks all INTL locales
		
		
		//-- Data
		
		// A map of buttons to ContactGroupItem
		HashMap<Button, ContactGroupItem> groups = new HashMap<Button, ContactGroupItem>();

		// Create contact groups with each letter

		for ( Entry<Character, Button> entry : PageAddressbook.buttons.entrySet() ) {
			
			Character c = entry.getKey();
			Button b = entry.getValue();

			
			String groupname = c + ZimbraSeleniumProperties.getUniqueString();
	   		app.zGetActiveAccount().soapSend(
	   	            "<CreateContactRequest xmlns='urn:zimbraMail'>" +
	   	            		"<cn >" +
	   	            			"<a n='type'>group</a>" +
	   	            			"<a n='nickname'>" + groupname +"</a>" +
	   	            			"<a n='fileAs'>8:" + groupname +"</a>" +
	   	            			"<m type='I' value='email" + ZimbraSeleniumProperties.getUniqueString() + "@example.com' />" +
	   	            		"</cn>" +
	   	            "</CreateContactRequest>");
	   		ContactGroupItem group = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), groupname);

	   		groups.put(b, group);
	   		
		}
		
		
		//-- GUI
		
		// refresh
		app.zPageAddressbook.zRefresh();
		

		
		//-- Verification
		
		for ( Entry<Button, ContactGroupItem> entry : groups.entrySet() ) {
			
			Button b = entry.getKey();
			ContactGroupItem g = entry.getValue();
			
			// Click each button
			app.zPageAddressbook.zToolbarPressButton(b);
			
			// Verify the group is listed
			boolean found = false;
			for (ContactItem i : app.zPageAddressbook.zListGetContacts()) {
				if ( i.getName().equals(g.getName()) ) {
					found = true;
				}
			}

			ZAssert.assertTrue(found, "Verify contact group "+ g.getName() +" is listed");
			
		}
		
	
    }   
}

