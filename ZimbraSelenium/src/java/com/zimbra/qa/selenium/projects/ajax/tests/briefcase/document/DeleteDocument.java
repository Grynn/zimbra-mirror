package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DialogDeleteConfirm;

public class DeleteDocument extends AjaxCommonTest {

	public DeleteDocument() {
		logger.info("New " + DeleteDocument.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

	}

	@Test(description = "Create document through SOAP - delete & verify through GUI", groups = { "smoke" })
	public void DeleteDocument_01() throws HarnessException {

		// Create document item
		DocumentItem document = new DocumentItem();
		String documentLocator = "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width*='auto'] div:contains("
				+ document.getDocName() + ")";

		ZimbraAccount account = app.zGetActiveAccount();
		String briefcaseFolderId = document.GetBriefcaseIdUsingSOAP(account);

		// Create document using SOAP
		account
				.soapSend("<SaveDocumentRequest requestId='0' xmlns='urn:zimbraMail'>"
						+ "<doc name='"
						+ document.getDocName()
						+ "' l='"
						+ briefcaseFolderId
						+ "' ct='application/x-zimbra-doc'>"
						+ "<content>&lt;html>&lt;body>"
						+ document.getDocText()
						+ "&lt;/body>&lt;/html></content>"
						+ "</doc>"
						+ "</SaveDocumentRequest>");

		// Select Briefcase tab
		// SleepUtil.sleepSmall();
		app.zPageBriefcase.zNavigateTo();

		// refresh briefcase page
		app.zPageBriefcase.pageRefresh(true);

		// Click on created document
		app.zPageBriefcase.zClick(documentLocator);

		// Click on Delete document icon in toolbar
		DialogDeleteConfirm deleteConfirm = (DialogDeleteConfirm) app.zPageBriefcase
				.zToolbarPressButton(Button.B_DELETE);

		// Click OK on Confirmation dialog
		if (deleteConfirm.zIsActive())
			deleteConfirm.zClickButton(Button.B_YES);

		// refresh briefcase page
		app.zPageBriefcase.pageRefresh(false);

		// Verify document was deleted
		boolean isPresenet = true;
		isPresenet = !app.zPageBriefcase.waitForCondition(
				"!selenium.isElementPresent(\"" + documentLocator + "\");",
				"5000");

		ZAssert.assertFalse(isPresenet,
				"Verify document was deleted through GUI");
	}
}
