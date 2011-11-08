package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import java.util.HashMap;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import org.testng.annotations.AfterMethod;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;

public class MoveDocument extends AjaxCommonTest {

	public MoveDocument() {
		logger.info("New " + MoveDocument.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = new HashMap<String, String>() {

			private static final long serialVersionUID = 1L;

			{
				put("zimbraPrefBriefcaseReadingPaneLocation", "bottom");
			}
		};

		// Make sure we are using an account with message view
		// super.startingAccountPreferences = new HashMap<String, String>()
		// {{put("zimbraPrefGroupMailBy", "message");}};
	}

	@Test(description = "Create document through SOAP - move & verify through GUI", groups = { "smoke" })
	public void MoveDocument_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		String briefcaseFolderId = briefcaseFolder.getId();

		String name = "folder" + ZimbraSeleniumProperties.getUniqueString();

		// Create a subfolder to move the message into i.e. Briefcase/subfolder
		account.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + name + "' l='" + briefcaseFolderId + "'/>"
				+ "</CreateFolderRequest>");

		FolderItem subFolderItem = FolderItem.importFromSOAP(account, name);

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// double click on created subfolder
		app.zPageBriefcase.zListItem(Action.A_DOUBLECLICK, subFolderItem);

		// Create document item
		DocumentItem docItem = new DocumentItem();

		// Create document using SOAP
		String contentHTML = XmlStringUtil.escapeXml("<html>" + "<body>"
				+ docItem.getDocText() + "</body>" + "</html>");

		account.soapSend("<SaveDocumentRequest requestId='0' xmlns='urn:zimbraMail'>"
				+ "<doc name='"
				+ docItem.getName()
				+ "' l='"
				+ briefcaseFolderId
				+ "' ct='application/x-zimbra-doc'>"
				+ "<content>"
				+ contentHTML
				+ "</content>"
				+ "</doc>"
				+ "</SaveDocumentRequest>");

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		// document.importFromSOAP(account, document.getDocName());

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		SleepUtil.sleepVerySmall();

		// Click on created document
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docItem);

		// Click on 'Move selected item' icon in toolbar
		if (ZimbraSeleniumProperties.zimbraGetVersionString().contains("8.0.")) {
			// Click move -> subfolder
			app.zPageBriefcase.zToolbarPressPulldown(Button.B_MOVE,
					subFolderItem);
		} else {
			DialogMove chooseFolder = (DialogMove) app.zPageBriefcase
					.zToolbarPressButton(Button.B_MOVE, docItem);

			// Choose folder and click OK on Confirmation dialog
			chooseFolder.zClickTreeFolder(subFolderItem);
			chooseFolder.zClickButton(Button.B_OK);
		}

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// Verify document was moved from the folder
		ZAssert.assertFalse(
				app.zPageBriefcase.isPresentInListView(docItem.getName()),
				"Verify document was moved from the folder");

		SleepUtil.sleepVerySmall();

		// click on subfolder in tree view
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, subFolderItem, true);

		// Verify document was moved to the selected folder
		ZAssert.assertTrue(
				app.zPageBriefcase.isPresentInListView(docItem.getName()),
				"Verify document was moved to the selected folder");
	}

	@Test(description = "Move Document using 'm' keyboard shortcut", groups = { "functional" })
	public void MoveDocument_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseRootFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		String briefcaseRootFolderId = briefcaseRootFolder.getId();

		Shortcut shortcut = Shortcut.S_MOVE;

		String[] subFolderNames = {
				"subFolderName1" + ZimbraSeleniumProperties.getUniqueString(),
				"subFolderName2" + ZimbraSeleniumProperties.getUniqueString() };

		FolderItem[] subFolders = new FolderItem[subFolderNames.length];

		// Create folders to move the message from/to: Briefcase/sub-folder
		for (int i = 0; i < subFolderNames.length; i++) {
			account.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
					+ "<folder name='" + subFolderNames[i] + "' l='"
					+ briefcaseRootFolderId + "'/>" + "</CreateFolderRequest>");

			subFolders[i] = FolderItem.importFromSOAP(account,
					subFolderNames[i]);
		}

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseRootFolder,
				true);

		// Create document item
		DocumentItem docItem = new DocumentItem();

		// Create document in sub-folder1 using SOAP
		String contentHTML = XmlStringUtil.escapeXml("<html>" + "<body>"
				+ docItem.getDocText() + "</body>" + "</html>");

		account.soapSend("<SaveDocumentRequest requestId='0' xmlns='urn:zimbraMail'>"
				+ "<doc name='"
				+ docItem.getName()
				+ "' l='"
				+ subFolders[0].getId()
				+ "' ct='application/x-zimbra-doc'>"
				+ "<content>"
				+ contentHTML
				+ "</content>"
				+ "</doc>"
				+ "</SaveDocumentRequest>");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseRootFolder,
				true);

		// double-click on sub-folder1 in list view
		app.zPageBriefcase.zListItem(Action.A_DOUBLECLICK, subFolders[0]);

		// click on sub-folder1 in tree view to refresh view
		// app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, subFolders[0],
		// true);

		SleepUtil.sleepVerySmall();

		// Click on created document in list view
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docItem);

		// Click the Move keyboard shortcut
		DialogMove chooseFolder = (DialogMove) app.zPageBriefcase
				.zKeyboardShortcut(shortcut);

		// Choose destination folder and Click OK on Confirmation dialog
		chooseFolder.zClickTreeFolder(subFolders[1]);

		chooseFolder.zClickButton(Button.B_OK);

		// app.zPageBriefcase.zClickAt("css=div[id=ChooseFolderDialog_button2]","0,0");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseRootFolder,
				true);

		SleepUtil.sleepVerySmall();

		// click on sub-folder1 in tree view
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, subFolders[0], false);

		// Verify document is no longer in the sub-folder1
		ZAssert.assertFalse(
				app.zPageBriefcase.isPresentInListView(docItem.getName()),
				"Verify document is no longer in the folder: "
						+ subFolders[0].getName());

		SleepUtil.sleepVerySmall();

		// click on sub-folder2 in tree view
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, subFolders[1], true);

		// Verify document was moved to sub-folder2
		ZAssert.assertTrue(
				app.zPageBriefcase.isPresentInListView(docItem.getName()),
				"Verify document was moved to the folder: "
						+ subFolders[1].getName());
	}

	@Test(description = "Create document through SOAP - move using Right Click Context Menu & verify through GUI", groups = { "functional" })
	public void MoveDocument_03() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		String briefcaseFolderId = briefcaseFolder.getId();

		String name = "subFolder" + ZimbraSeleniumProperties.getUniqueString();

		// Create a subfolder to move the message into i.e. Briefcase/subfolder
		account.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + name + "' l='" + briefcaseFolderId + "'/>"
				+ "</CreateFolderRequest>");

		FolderItem subFolderItem = FolderItem.importFromSOAP(account, name);

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// double click on created subfolder
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageBriefcase.zListItem(Action.A_DOUBLECLICK, subFolderItem);

		// Create document item
		DocumentItem docItem = new DocumentItem();

		// Create document using SOAP
		String contentHTML = XmlStringUtil.escapeXml("<html>" + "<body>"
				+ docItem.getDocText() + "</body>" + "</html>");

		account.soapSend("<SaveDocumentRequest requestId='0' xmlns='urn:zimbraMail'>"
				+ "<doc name='"
				+ docItem.getName()
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
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		SleepUtil.sleepVerySmall();

		// Click on created document
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docItem);

		// Move using Right Click Context Menu
		DialogMove chooseFolder = (DialogMove) app.zPageBriefcase.zListItem(
				Action.A_RIGHTCLICK, Button.O_MOVE, docItem);

		// Choose folder and click OK on Confirmation dialog
		chooseFolder.zClickTreeFolder(subFolderItem);
		chooseFolder.zClickButton(Button.B_OK);

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// Verify document was moved from the folder
		ZAssert.assertFalse(
				app.zPageBriefcase.isPresentInListView(docItem.getName()),
				"Verify document was moved from the folder");

		SleepUtil.sleepVerySmall();

		// click on subfolder in tree view
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, subFolderItem, true);

		// Verify document was moved to the selected folder
		ZAssert.assertTrue(
				app.zPageBriefcase.isPresentInListView(docItem.getName()),
				"Verify document was moved to the selected folder");
	}

	@AfterMethod(groups = { "always" })
	public void afterMethod() throws HarnessException {
		logger.info("Checking for the Move Dialog ...");

		// Check if the "Move Dialog is still open
		DialogMove dialog = new DialogMove(app,
				((AppAjaxClient) app).zPageBriefcase);
		if (dialog.zIsActive()) {
			logger.warn(dialog.myPageName()
					+ " was still active.  Cancelling ...");
			dialog.zClickButton(Button.B_CANCEL);
		}

	}
}
