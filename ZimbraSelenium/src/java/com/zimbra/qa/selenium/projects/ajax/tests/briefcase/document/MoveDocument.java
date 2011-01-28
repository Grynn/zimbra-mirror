package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
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

		String subfolderLocator = "css=div[id='zl__BDLV__rows'] td[width*='auto'] div:contains("
			+ subfolder.getName() + ")";
		
		// Select Briefcase tab
		app.zPageBriefcase.zNavigateTo();

		// refresh briefcase page
		// ClientSessionFactory.session().selenium().refresh();
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		app.zPageBriefcase
				.waitForElement(subfolderLocator, "5000");

		// Create document item
		DocumentItem document = new DocumentItem();
		String documentLocator = "css=div[id='zl__BDLV__rows'] td[width*='auto'] div:contains("
				+ document.getDocName() + ")";

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

		// document.importFromSOAP(account, document.getDocName());

		// refresh briefcase page
		// ClientSessionFactory.session().selenium().refresh();
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		app.zPageBriefcase.waitForElement(documentLocator, "5000");

		/*
		 * app.zPageBriefcase.waitForCondition("selenium.isElementPresent(\"" +
		 * documentLocator + "\") && selenium.isVisible(\"" + documentLocator +
		 * "\");", "5000");
		 */

		// Click on created document
		// app.zPageBriefcase.zListItem(Action.A_LEFTCLICK,document.getDocName());
		app.zPageBriefcase.zClick(documentLocator);

		// Click on Move selected item icon in toolbar
		DialogChooseFolder chooseFolder = (DialogChooseFolder) app.zPageBriefcase
				.zToolbarPressButton(Button.B_MOVE);

		// Click OK on Confirmation dialog
		if (chooseFolder.zIsActive())
			chooseFolder.zClickTreeFolder(subfolder);
		chooseFolder.zClickButton(Button.B_OK);

		// refresh briefcase page
		// ClientSessionFactory.session().selenium().refresh();
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		app.zPageBriefcase
				.waitForCondition(
						"selenium.isElementPresent(\"css=[id='zti__main_Briefcase__16_div'][class='DwtTreeItem-selected']\")&&"
								+ "selenium.isElementPresent(\"css=[id='zl__BDLV__rows']\");",
						"5000");

		// Verify document was moved from the folder
		boolean isPresenet = true;
		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			isPresenet = app.zPageBriefcase.sIsElementPresent(documentLocator);
		}
		ZAssert.assertFalse(isPresenet,
				"Verify document was moved from the folder");

		// click on subfolder in tree view
		// app.zPageBriefcase.sIsElementPresent("css=td[class='DwtTreeItem-Text']:contains('"
		// + subfolder.getName() + "')");
		String briefcaseSubfolder = "css=td[class='DwtTreeItem-Text']:contains('"
				+ subfolder.getName() + "')";
		app.zPageBriefcase.zClick(briefcaseSubfolder);

		app.zPageBriefcase
				.waitForCondition(
						"selenium.isElementPresent(\"css=[id^='zti__main_Briefcase__'][class='DwtTreeItem-selected']\")&&"
								+ "selenium.isElementPresent(\"css=[id='zl__BDLV__rows']\");",
						"5000");

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
