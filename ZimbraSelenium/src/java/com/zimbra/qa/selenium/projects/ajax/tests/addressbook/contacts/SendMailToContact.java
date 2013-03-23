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
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contacts;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;

public class SendMailToContact extends AjaxCommonTest  {
	public SendMailToContact() {
		logger.info("New "+ SendMailToContact.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	

	@Test(	description = "Right click then click New Email",
			groups = { "smoke" })
	public void NewEmail() throws HarnessException {

		//--  Data
		
		// The message subject
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		// Create a contact
		String firstName = "First" + ZimbraSeleniumProperties.getUniqueString();
		String lastName = "Last" + ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
	                "<CreateContactRequest xmlns='urn:zimbraMail'>" +
	                		"<cn >" +
	                			"<a n='firstName'>" + firstName +"</a>" +
	                			"<a n='lastName'>" + lastName +"</a>" +
	                			"<a n='email'>" + ZimbraAccount.AccountA().EmailAddress + "</a>" +
                			"</cn>" +
	                "</CreateContactRequest>");
		
		
		
		//-- GUI
		
		// Refresh
		app.zPageAddressbook.zRefresh();
		
		// Right Click -> New Email
        FormMailNew formMailNew = (FormMailNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_NEW, firstName);        

        formMailNew.zFillField(Field.Subject, subject);
        formMailNew.zFillField(Field.Body, "body"+ ZimbraSeleniumProperties.getUniqueString());
        formMailNew.zSubmit();
        
        
        //-- Verification
        
        MailItem message1 = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ subject +")");
        ZAssert.assertNotNull(message1, "Verify the message is received by Account A");

        

	}
	

	

}

