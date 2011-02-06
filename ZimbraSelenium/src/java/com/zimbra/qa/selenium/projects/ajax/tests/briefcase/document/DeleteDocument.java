package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DialogDeleteConfirm;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.PageBriefcase.Locators;

public class DeleteDocument extends AjaxCommonTest {

	public DeleteDocument() {
		logger.info("New " + DeleteDocument.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

	}

	@Test(description = "Create document through SOAP - delete & verify through GUI", groups = { "smoke" })
	public void DeleteDocument_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem document = new DocumentItem();

		String docName = document.getDocName();
		String docText = document.getDocText();

		// Create document using SOAP
		String contentHTML = XmlStringUtil.escapeXml("<html>" + "<body>"
				+ docText + "</body>" + "</html>");

		account
				.soapSend("<SaveDocumentRequest requestId='0' xmlns='urn:zimbraMail'>"
						+ "<doc name='"
						+ docName
						+ "' l='"
						+ briefcaseFolder.getId()
						+ "' ct='application/x-zimbra-doc'>"
						+ "<content>"
						+ contentHTML
						+ "</content>"
						+ "</doc>"
						+ "</SaveDocumentRequest>");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Click on created document
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docName);

		// Click on Delete document icon in toolbar
		DialogDeleteConfirm deleteConfirm = (DialogDeleteConfirm) app.zPageBriefcase
				.zToolbarPressButton(Button.B_DELETE);

		// Click OK on Confirmation dialog
		deleteConfirm.zClickButton(Button.B_YES);

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// Verify document was deleted
		String itemLocator = Locators.briefcaseListView
				+ " td[width*='auto'] div:contains(" + docName + ")";
		boolean isPresenet = true;
		isPresenet = !app.zPageBriefcase.waitForCondition(
				"!selenium.isElementPresent(\"" + itemLocator + "\");", "5000");

		ZAssert.assertFalse(isPresenet,
				"Verify document was deleted through GUI");
	}
}
