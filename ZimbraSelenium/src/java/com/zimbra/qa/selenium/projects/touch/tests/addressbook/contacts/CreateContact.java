/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.touch.tests.addressbook.contacts;

import java.util.HashMap;
import java.util.Map.Entry;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ContactItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraCharsets.ZCharset;

import com.zimbra.qa.selenium.projects.touch.ui.addressbook.FormContactNew;
import com.zimbra.qa.selenium.projects.touch.ui.addressbook.FormContactNew.Field;

import com.zimbra.qa.selenium.projects.touch.core.TouchCommonTest;


public class CreateContact extends TouchCommonTest  {

	public CreateContact() {
		logger.info("New "+ CreateContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;
	}
	
	

	
	@Test(	description = "Create a contact with basic attributes",
			groups = { "sanity" })
	public void CreateContactWithBasicAttrs() throws HarnessException {
		
		//-- DATA
		
		// generate basic attribute values for new account
		String contactFirst = "First" + ZimbraSeleniumProperties.getUniqueString();
		String contactLast = "Last"+ ZimbraSeleniumProperties.getUniqueString();
		String contactCompany = "Company"+ ZimbraSeleniumProperties.getUniqueString();
		
		
		//-- GUI Action
		
		// click +(Add) button
		FormContactNew formContactNew = (FormContactNew)app.zPageAddressbook.zToolbarPressButton(Button.B_NEW);
		
        // fill in the form
		formContactNew.zFillField(Field.FirstName, contactFirst);
		formContactNew.zFillField(Field.LastName, contactLast);
		formContactNew.zFillField(Field.Company, contactCompany);
		
		// click Save button
		formContactNew.zSubmit();

		
		//-- Data Verification
		
		// search the created contact 
		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='contact'>"
				+		"<query>#firstname:"+ contactFirst +"</query>"
				+	"</SearchRequest>");
		String contactId = app.zGetActiveAccount().soapSelectValue("//mail:cn", "id");
		
		// make sure if the data is found by search request
		ZAssert.assertNotNull(contactId, "Verify the contact is returned in the search");
		
		app.zGetActiveAccount().soapSend(
				"<GetContactsRequest xmlns='urn:zimbraMail'>"
			+		"<cn id='"+ contactId +"'/>"
			+	"</GetContactsRequest>");
	    
		// get all the contact data stored in Zimbra server
		String lastname = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='lastName']", null);
		String firstname = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='firstName']", null);
		String company = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='company']", null);
		
		
		// make sure those are equal to one you created from GUI
		ZAssert.assertEquals(lastname, contactLast, "Verify the last name was saved correctly");
		ZAssert.assertEquals(firstname, contactFirst, "Verify the first name was saved correctly");
		ZAssert.assertEquals(company, contactCompany, "Verify the company was saved correctly");
		
	}
	
	@Test(	description = "Create a contact with extended attributes",
			groups = { "sanity" })
	public void CreateContactWithExtendedAttrs() throws HarnessException {				
		
		//-- DATA
		
		// generate all attributes value for new contact
		String contactFirst = "First" + ZimbraSeleniumProperties.getUniqueString();
		String contactLast = "Last"+ ZimbraSeleniumProperties.getUniqueString();
		String contactCompany = "Company"+ ZimbraSeleniumProperties.getUniqueString();
		
		String contactPrefix = "Mr";
		String contactMiddleName = "MiddleName" + ZimbraSeleniumProperties.getUniqueString();
		String contactMaidenName = "MadenName" + ZimbraSeleniumProperties.getUniqueString();
		String contactSuffix = "Sr" ;
		String contactNickname = "Nickname" + ZimbraSeleniumProperties.getUniqueString();
		
		String contactJobTitle = "JobTitle" + ZimbraSeleniumProperties.getUniqueString();
		String contactDepartment = "Department" + ZimbraSeleniumProperties.getUniqueString();
	
		String contactEmail = "Email" + ZimbraSeleniumProperties.getUniqueString() + "@testdomain.co.jp";
		
		String contactMobilePhone = "1-408-555-1212";
		
		String contactOtherStreet = "123 Main St.";
		String contactOtherCity = "City" + ZimbraSeleniumProperties.getUniqueString();
		String contactOtherState = "State" + ZimbraSeleniumProperties.getUniqueString();
		String contactOtherZipcode = "94402";
		String contactOtherCountry = "Country" + ZimbraSeleniumProperties.getUniqueString();
				
		String contactWorkUrl = "http://"+ ZimbraSeleniumProperties.getUniqueString()+".com";
		
								
		//-- GUI Action
				
		// click +(Add) button
		FormContactNew formContactNew = (FormContactNew)app.zPageAddressbook.zToolbarPressButton(Button.B_NEW);
				
		// fill in the form
		// for basic attributes
		formContactNew.zFillField(Field.FirstName, contactFirst);
		formContactNew.zFillField(Field.LastName, contactLast);
		formContactNew.zFillField(Field.Company, contactCompany);
		
		
		// show all hidden field:
		formContactNew.zDisplayHiddenName();
		
		// for extended attributes
		formContactNew.zFillField(Field.NamePrefix, contactPrefix);
		formContactNew.zFillField(Field.MiddleName, contactMiddleName);
		formContactNew.zFillField(Field.MaidenName, contactMaidenName);
		formContactNew.zFillField(Field.NameSuffix, contactSuffix);
		formContactNew.zFillField(Field.Nickname, contactNickname);
		
		formContactNew.zFillField(Field.Department, contactDepartment);
		formContactNew.zFillField(Field.JobTitle, contactJobTitle);
		
		formContactNew.zFillField(Field.Email, contactEmail);
		
		formContactNew.zToolbarPressPulldown(Button.B_PHONE_TYPE, Button.O_MOBILE);		
		formContactNew.zFillField(Field.MobilePhone, contactMobilePhone);
		
		formContactNew.zToolbarPressPulldown(Button.B_ADDRESS_TYPE, Button.O_OTHER);
		formContactNew.zFillField(Field.OtherStreet, contactOtherStreet);
		formContactNew.zFillField(Field.OtherCity, contactOtherCity);
		formContactNew.zFillField(Field.OtherState, contactOtherState);
		formContactNew.zFillField(Field.OtherCountry, contactOtherCountry);
		formContactNew.zFillField(Field.OtherZipcode, contactOtherZipcode);
		
		formContactNew.zToolbarPressPulldown(Button.B_URL_TYPE, Button.O_WORK);
		formContactNew.zFillField(Field.WorkURL, contactWorkUrl);
				
		// click Save button
		formContactNew.zSubmit();

		//-- Verification
		
		// search the created contact
		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='contact'>"
				+		"<query>#firstname:"+ contactFirst +"</query>"
				+	"</SearchRequest>");
		String contactId = app.zGetActiveAccount().soapSelectValue("//mail:cn", "id");
		
		// make sure if the data is found by search request
		ZAssert.assertNotNull(contactId, "Verify the contact is returned in the search");
				
		app.zGetActiveAccount().soapSend(
					"<GetContactsRequest xmlns='urn:zimbraMail'>"
				+		"<cn id='"+ contactId +"'/>"
				+	"</GetContactsRequest>");
		
		// get all the contact data stored in Zimbra server
		String lastname = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='lastName']", null);
		String firstname = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='firstName']", null);
		String company = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='company']", null);
				
		String prefix = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='namePrefix']", null);
		String middlename = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='middleName']", null);
		String maidenname = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='maidenName']", null);
		String suffix = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='nameSuffix']", null);
		String nickname = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='nickname']", null);
				
		String jobtitle = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='jobTitle']", null);
		String department = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='department']", null);
				
		String email = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='email']", null);
				
		String mobile = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='mobilePhone']", null);
		
		String otherstreet = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='otherStreet']", null);
		String othercity = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='otherCity']", null);
		String otherstate = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='otherState']", null);
		String othercountry = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='otherCountry']", null);
		String otherzipcode = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='otherPostalCode']", null);
				
		String workurl = app.zGetActiveAccount().soapSelectValue("//mail:cn[@id='"+ contactId +"']//mail:a[@n='workUrl']", null);
						
		//-- Data Verification
		
		// make sure those are equal to one you created from GUI
		ZAssert.assertEquals(lastname, contactLast, "Verify the last name was saved correctly");
		ZAssert.assertEquals(firstname, contactFirst, "Verify the first name was saved correctly");
		ZAssert.assertEquals(email, contactEmail, "Verify the email was saved correctly");
		ZAssert.assertEquals(company, contactCompany, "Verify the company was saved correctly");
		ZAssert.assertEquals(prefix, contactPrefix, "Verify the prefix was saved correctly");
		ZAssert.assertEquals(middlename, contactMiddleName, "Verify the middle name was saved correctly");
		ZAssert.assertEquals(maidenname, contactMaidenName, "Verify the maiden was saved correctly");
		ZAssert.assertEquals(suffix, contactSuffix, "Verify the suffix was saved correctly");
		ZAssert.assertEquals(nickname, contactNickname, "Verify the nickname was saved correctly");
		ZAssert.assertEquals(jobtitle, contactJobTitle, "Verify the jobtitle was saved correctly");
		ZAssert.assertEquals(department, contactDepartment, "Verify the department was saved correctly");
		ZAssert.assertEquals(mobile, contactMobilePhone, "Verify the mobile phone was saved correctly");
		ZAssert.assertEquals(otherstreet, contactOtherStreet, "Verify the other street was saved correctly");
		ZAssert.assertEquals(othercity, contactOtherCity, "Verify the other city was saved correctly");
		ZAssert.assertEquals(otherstate, contactOtherState, "Verify the other state was saved correctly");
		ZAssert.assertEquals(othercountry, contactOtherCountry, "Verify the other country was saved correctly");
		ZAssert.assertEquals(otherzipcode, contactOtherZipcode, "Verify the other zipcode was saved correctly");
		ZAssert.assertEquals(workurl, contactWorkUrl, "Verify the work url was saved correctly");
	}


}
