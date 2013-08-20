/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
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
package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.toaster;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactNew;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactNew.Field;

public class CreateContact extends AjaxCommonTest {

	public CreateContact() {
		logger.info("New " + CreateContact.class.getCanonicalName());
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;
		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = null;

	}

	@Test(description = "Create a basic contact item by click New in page Addressbook and verify toast msg ", groups = { "functional" })
	public void CreateContactToastMsg() throws HarnessException {

		// -- DATA

		String contactFirst = "First"+ ZimbraSeleniumProperties.getUniqueString();
		String contactLast = "Last"+ ZimbraSeleniumProperties.getUniqueString();
		String contactEmail = "email"+ ZimbraSeleniumProperties.getUniqueString() + "@domain.com";

		// -- GUI Action

		FormContactNew formContactNew = (FormContactNew) app.zPageAddressbook.zToolbarPressButton(Button.B_NEW);

		// Fill in the form
		formContactNew.zFillField(Field.FirstName, contactFirst);
		formContactNew.zFillField(Field.LastName, contactLast);
		formContactNew.zFillField(Field.Email, contactEmail);
		formContactNew.zSubmit();

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg, "Contact Created","Verify toast message: Contact Created");

	}
}
