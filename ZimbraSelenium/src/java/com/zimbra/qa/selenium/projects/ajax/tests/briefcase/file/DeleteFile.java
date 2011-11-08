package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.file;

import java.util.HashMap;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DialogConfirm;

public class DeleteFile extends AjaxCommonTest {

	public DeleteFile() {
		logger.info("New " + DeleteFile.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;
		
		super.startingAccountPreferences = new HashMap<String, String>() {
			
			private static final long serialVersionUID = 1L;
			
			{
				put("zimbraPrefBriefcaseReadingPaneLocation", "bottom");				
			}
		};

	}

	@Test(description = "Upload file through RestUtil - delete & verify through GUI", groups = { "smoke" })
	public void DeleteFile_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/putty.log";

		FileItem fileItem = new FileItem(filePath);

		String fileName = fileItem.getName();

		// Upload file to server through RestUtil
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

		SleepUtil.sleepVerySmall();
		
		// Click on created document
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, fileItem);

		// Click on Delete document icon in toolbar
		DialogConfirm deleteConfirm = (DialogConfirm) app.zPageBriefcase
				.zToolbarPressButton(Button.B_DELETE, fileItem);

		// Click OK on Confirmation dialog
		deleteConfirm.zClickButton(Button.B_YES);

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// This step is necessary because next test may be uploading the same
		// file
		// if ZD is not synced to ZCS, ZCS will be confused, and the next
		// uploaded file
		// will be deleted per previous command.
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		// Verify document was deleted
		ZAssert.assertFalse(app.zPageBriefcase.isPresentInListView(fileName),
				"Verify document was deleted through GUI");
	}

	@Test(description = "Upload file through RestUtil - delete using Delete Key & check trash", groups = { "functional" })
	public void DeleteFile_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		FolderItem trashFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Trash);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/putty.log";

		FileItem fileItem = new FileItem(filePath);

		String fileName = fileItem.getName();
		
		Shortcut shortcut = Shortcut.S_DELETE;

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to briefcase through SOAP
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'><doc l='"
				+ briefcaseFolder.getId() + "'>" + "<upload id='"
				+ attachmentId + "'/></doc></SaveDocumentRequest>");

		String docId = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		SleepUtil.sleepVerySmall();
		
		// Click on created document
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, fileItem);

		// Click the Delete keyboard shortcut
		DialogConfirm deleteConfirm = (DialogConfirm) app.zPageBriefcase
				.zKeyboardShortcut(shortcut);

		// Click OK on Confirmation dialog
		deleteConfirm.zClickButton(Button.B_YES);

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// Verify file was deleted from the list
		ZAssert.assertFalse(app.zPageBriefcase.isPresentInListView(fileName),
				"Verify file was deleted through GUI");
		
		// Verify document moved to Trash
		account
				.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
						+ "<query>in:"
						+ trashFolder.getName()
						+ " "
						+ fileName
						+ "</query>" + "</SearchRequest>");

		String id = account.soapSelectValue("//mail:SearchResponse//mail:doc",
				"id");
		
		ZAssert.assertEquals(id, docId,
				"Verify the file was moved to the trash folder: "
				+ fileName + " id: " + id);		
	}
	
	@Test(description = "Upload file through RestUtil - delete using <Backspace> Key & check trash", groups = { "functional" })
	public void DeleteFile_03() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		FolderItem trashFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Trash);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/putty.log";

		FileItem fileItem = new FileItem(filePath);

		String fileName = fileItem.getName();
		
		Shortcut shortcut = Shortcut.S_BACKSPACE;

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to briefcase through SOAP
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'><doc l='"
				+ briefcaseFolder.getId() + "'>" + "<upload id='"
				+ attachmentId + "'/></doc></SaveDocumentRequest>");

		String docId = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		SleepUtil.sleepVerySmall();
		
		// Click on created document
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, fileItem);

		// Click the Backspace keyboard shortcut
		DialogConfirm deleteConfirm = (DialogConfirm) app.zPageBriefcase
				.zKeyboardShortcut(shortcut);

		// Click OK on Confirmation dialog
		deleteConfirm.zClickButton(Button.B_YES);

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// Verify file was deleted from the list
		ZAssert.assertFalse(app.zPageBriefcase.isPresentInListView(fileName),
				"Verify file was deleted through GUI");
		
		// Verify document moved to Trash
		account
				.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
						+ "<query>in:"
						+ trashFolder.getName()
						+ " "
						+ fileName
						+ "</query>" + "</SearchRequest>");

		String id = account.soapSelectValue("//mail:SearchResponse//mail:doc",
				"id");
		
		ZAssert.assertEquals(id, docId,
				"Verify the file was moved to the trash folder: "
				+ fileName + " id: " + id);		
	}
	
	@Test(description = "Upload file through RestUtil - delete using Right Click context menu", groups = { "functional" })
	public void DeleteFile_04() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/putty.log";

		FileItem fileItem = new FileItem(filePath);

		String fileName = fileItem.getName();
		
		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to briefcase through SOAP
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'><doc l='"
				+ briefcaseFolder.getId() + "'>" + "<upload id='"
				+ attachmentId + "'/></doc></SaveDocumentRequest>");

		// String docId = account.soapSelectValue("//mail:SaveDocumentResponse//mail:doc", "id");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		SleepUtil.sleepVerySmall();
		
		// Click on created file
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, fileItem);

		// Delete File using Right Click Context Menu
		DialogConfirm deleteConfirm = (DialogConfirm) app.zPageBriefcase
				.zListItem(Action.A_RIGHTCLICK, Button.O_DELETE, fileItem);

		// Click OK on Confirmation dialog
		deleteConfirm.zClickButton(Button.B_YES);

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// Verify file was deleted from the list
		ZAssert.assertFalse(app.zPageBriefcase.isPresentInListView(fileName),
				"Verify file was deleted through GUI");		
	}
}
