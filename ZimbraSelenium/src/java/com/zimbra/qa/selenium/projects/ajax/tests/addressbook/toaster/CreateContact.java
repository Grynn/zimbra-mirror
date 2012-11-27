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
