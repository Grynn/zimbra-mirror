package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.file;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DialogDeleteConfirm;

public class DeleteFile extends AjaxCommonTest {

	public DeleteFile() {
		logger.info("New " + DeleteFile.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

	}

	@Test(description = "Upload file through SOAP - delete & verify through GUI", groups = { "functional" })
	public void DeleteFile_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem document = new DocumentItem();

		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/testtextfile.txt";

		String fileName = document.getFileName(filePath);

		// Upload file to server through SOAP
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to briefcase through SOAP
		account.soapSend(

		"<SaveDocumentRequest xmlns='urn:zimbraMail'>" +

		"<doc l='" + briefcaseFolder.getId() + "'>" +

		"<upload id='" + attachmentId + "'/>" +

		"</doc>" +

		"</SaveDocumentRequest>");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Click on created document
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, fileName);

		// Click on Delete document icon in toolbar
		DialogDeleteConfirm deleteConfirm = (DialogDeleteConfirm) app.zPageBriefcase
				.zToolbarPressButton(Button.B_DELETE);

		// Click OK on Confirmation dialog
		deleteConfirm.zClickButton(Button.B_YES);

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// Verify document was deleted
		boolean isDeleted = app.zPageBriefcase.isDeleted(fileName);

		ZAssert
				.assertTrue(isDeleted,
						"Verify document was deleted through GUI");
	}
}
