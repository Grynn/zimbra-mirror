package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DialogChooseFolder;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.PageBriefcase.Locators;
import org.testng.annotations.AfterMethod;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;

public class MoveDocument extends AjaxCommonTest {

	public MoveDocument() {
		logger.info("New " + DeleteDocument.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		// Make sure we are using an account with message view
		// super.startingAccountPreferences = new HashMap<String, String>()
		// {{put("zimbraPrefGroupMailBy", "message");}};
	}

	@Test(description = "Create document through SOAP - move & verify through GUI", groups = { "smoke" })
	public void MoveDocument_01() throws HarnessException {

		ZimbraAccount account = app.zGetActiveAccount();
		String foldername = "folder"
				+ ZimbraSeleniumProperties.getUniqueString();

		// Create a subfolder to move the message into i.e. Briefcase/subfolder
		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// String briefcaseFolderId = document.GetBriefcaseIdUsingSOAP(account);
		String briefcaseFolderId = briefcaseFolder.getId();

		account.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + foldername + "' l='" + briefcaseFolderId
				+ "'/>" + "</CreateFolderRequest>");

		FolderItem subfolder = FolderItem.importFromSOAP(account, foldername);

		String subfolderName = subfolder.getName();

		String subfolderLocator = "css=div[id='zl__BDLV__rows'] td[width*='auto'] div:contains("
				+ subfolderName + ")";

		// refresh briefcase page
		app.zPageBriefcase.pageRefresh(Locators.zBriefcaseFolderIcon,true);

		// Click on created subfolder
		// app.zPageBriefcase.zListItem(Action.A_LEFTCLICK,subfolderName);
		app.zPageBriefcase.waitForElement(subfolderLocator, "5000");
		app.zPageBriefcase.zClick(subfolderLocator);

		// Create document item
		DocumentItem document = new DocumentItem();

		String docName = document.getDocName();
		String docText = document.getDocText();

		String documentLocator = "css=div[id='zl__BDLV__rows'] td[width*='auto'] div:contains("
				+ docName + ")";

		// Create document using SOAP
		String contentHTML = XmlStringUtil.escapeXml("<html>" + "<body>"
				+ docText + "</body>" + "</html>");

		account
				.soapSend("<SaveDocumentRequest requestId='0' xmlns='urn:zimbraMail'>"
						+ "<doc name='"
						+ document.getDocName()
						+ "' l='"
						+ briefcaseFolderId
						+ "' ct='application/x-zimbra-doc'>"
						+ "<content>"
						+ contentHTML
						+ "</content>"
						+ "</doc>"
						+ "</SaveDocumentRequest>");

		// document.importFromSOAP(account, document.getDocName());

		// refresh briefcase page
		app.zPageBriefcase.pageRefresh(Locators.zBriefcaseFolderIcon,true);

		// Click on created document
		// app.zPageBriefcase.zListItem(Action.A_LEFTCLICK,document.getDocName());
		app.zPageBriefcase.waitForElement(documentLocator, "5000");
		app.zPageBriefcase.zClick(documentLocator);

		// Click on Move selected item icon in toolbar
		DialogChooseFolder chooseFolder = (DialogChooseFolder) app.zPageBriefcase
				.zToolbarPressButton(Button.B_MOVE);

		// Click OK on Confirmation dialog
		chooseFolder.zClickTreeFolder(subfolder);
		chooseFolder.zClickButton(Button.B_OK);

		// refresh briefcase page
		app.zPageBriefcase.pageRefresh(Locators.zBriefcaseFolderIcon,false);

		// Verify document was moved from the folder
		boolean isPresenet = true;
		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			isPresenet = app.zPageBriefcase.sIsElementPresent(documentLocator);
		}

		ZAssert.assertFalse(isPresenet,
				"Verify document was moved from the folder");

		// click on subfolder in tree view
		String treeViewSubfolderLocator = "css=td[class='DwtTreeItem-Text']:contains('"
				+ subfolderName + "')";
		
		// refresh briefcase page
		app.zPageBriefcase.pageRefresh(treeViewSubfolderLocator,true);

		// Verify document was moved to the selected folder
		isPresenet = false;
		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			isPresenet = app.zPageBriefcase.sIsElementPresent(documentLocator);
		}
		ZAssert.assertTrue(isPresenet,
				"Verify document was moved to the selected folder");
	}

	@AfterMethod(groups = { "always" })
	public void afterMethod() throws HarnessException {
		logger.info("Checking for the Move Dialog ...");

		// Check if the "Move Dialog is still open
		DialogChooseFolder dialog = new DialogChooseFolder(app);
		if (dialog.zIsActive()) {
			logger.warn(dialog.myPageName()
					+ " was still active.  Cancelling ...");
			dialog.zClickButton(Button.B_CANCEL);
		}

	}
}
