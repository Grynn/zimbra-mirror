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
import com.zimbra.qa.selenium.projects.touch.ui.addressbook.DialogWarning;
import com.zimbra.qa.selenium.projects.touch.ui.addressbook.FormContactNew;
import com.zimbra.qa.selenium.projects.touch.ui.addressbook.FormContactNew.Field;
import com.zimbra.qa.selenium.projects.touch.core.TouchCommonTest;


public class CancelCreateContact extends TouchCommonTest  {

	public CancelCreateContact() {
		logger.info("New "+ CancelCreateContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;
	}
	
	

	
	@Test(	description = "Fill up creat contact form with basic attributes and cancel it",
			groups = { "sanity" })
	public void CancelCreateContact() throws HarnessException {
		
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
		
		// click cancel button and cofirm to discard the form data
		DialogWarning dw = (DialogWarning) formContactNew.zToolbarPressButton(Button.B_CANCEL);
		dw.zClickButton(Button.B_NO);

		
		//-- Data Verification
		
		// search the data stored in Zimbra server
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='contact'>"
			+		"<query>#firstname:"+ contactFirst +"</query>"
			+	"</SearchRequest>");
	    String contactId = app.zGetActiveAccount().soapSelectValue("//mail:cn", "id");
	    
	    // make sure if the data is not fetched
	    ZAssert.assertNull(contactId, "Verify the contact is not returned in the search");
		
	}
				

}
