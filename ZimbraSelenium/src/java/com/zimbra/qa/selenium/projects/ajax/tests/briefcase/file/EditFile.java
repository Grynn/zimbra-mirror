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

public class EditFile extends AjaxCommonTest {

	public EditFile() {
		logger.info("New " + EditFile.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = null;
	}

	@Test(description = "Upload file through RestUtil - edit name & verify through GUI", groups = { "smoke" })
	public void EditFile_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem document = new DocumentItem();

		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/putty.log";

		String fileName = document.getFileName(filePath);

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to briefcase through SOAP
		account.soapSend(

		"<SaveDocumentRequest xmlns='urn:zimbraMail'>" +

		"<doc l='" + briefcaseFolder.getId() + "'>" +

		"<upload id='" + attachmentId + "'/>" +

		"</doc>" +

		"</SaveDocumentRequest>");

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Click on created document
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, fileName);
		
		// Right click on document, select Rename
		app.zPageBriefcase.zListItem(Action.A_RIGHTCLICK, Button.B_RENAME, fileName);
		
		document.setDocName("name"
				+ ZimbraSeleniumProperties.getUniqueString());

		fileName = document.getDocName();

	    app.zPageBriefcase.rename(fileName);
	  
	 	// Verify document name through GUI;
		boolean present = app.zPageBriefcase.isPresent(fileName);

		ZAssert.assertTrue(present, "Verify document name through GUI");			
	}
}
