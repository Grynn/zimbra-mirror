package com.zimbra.qa.selenium.projects.desktop.tests.briefcase.file;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;

public class DisplayFile extends AjaxCommonTest {

	public DisplayFile() {
		logger.info("New " + DisplayFile.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = null;
	}

	@Test(description = "Upload file through RestUtil - verify through GUI", groups = { "smoke" })
	public void DisplayFile_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/testwordfile.doc";

		FileItem file = new FileItem(filePath);

		String fileName = file.getName();

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to briefcase through SOAP
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+ "<doc l='" + briefcaseFolder.getId() + "'><upload id='"
				+ attachmentId + "'/></doc></SaveDocumentRequest>");

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageBriefcase.zWaitForDesktopLoadingSpinner(5000);

      // refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Verify document is created
		String name = app.zPageBriefcase.getItemNameFromListView(fileName);
		ZAssert.assertEquals(name, fileName, "Verify file name through GUI");

		// boolean present = app.zPageBriefcase.isPresent(docName);
		// ZAssert.assertTrue(present, "Verify document name through GUI");

	}
}
