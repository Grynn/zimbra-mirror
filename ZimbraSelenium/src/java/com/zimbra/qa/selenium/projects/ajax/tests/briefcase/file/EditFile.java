package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.file;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.regex.Pattern;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.HtmlElement;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.PageBriefcase;

public class EditFile extends AjaxCommonTest {

	public EditFile() {
		logger.info("New " + EditFile.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = new HashMap<String, String>() {
			
			private static final long serialVersionUID = 1L;
			
			{
				put("zimbraPrefBriefcaseReadingPaneLocation", "bottom");				
			}
		};
	}

	@Test(description = "Upload file through RestUtil - Rename File using Right Click Context Menu & verify through GUI", groups = { "smoke" })
	public void EditFile_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/putty.log";

		IItem fileItem = new FileItem(filePath);

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
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, fileItem);

		// Right click on File, select Rename
		app.zPageBriefcase.zListItem(Action.A_RIGHTCLICK, Button.B_RENAME,
				fileItem);

		String fileName2 = "renameFile"
				+ ZimbraSeleniumProperties.getUniqueString();

		app.zPageBriefcase.rename(fileName2);

		// Verify document name through GUI
		ZAssert.assertTrue(app.zPageBriefcase
				.waitForPresentInListView(fileName2),
				"Verify new file name through GUI");
	}

	@Test(description = "Upload file, edit name - verify the content remains the same", groups = { "functional" })
	public void EditFile_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/putty.log";

		FileItem fileItem = new FileItem(filePath);

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to briefcase through SOAP
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+ "<doc l='" + briefcaseFolder.getId() + "'><upload id='"
				+ attachmentId + "'/>" + "</doc></SaveDocumentRequest>");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Retrieve file text through RestUtil
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("fmt", PageBriefcase.Response.Format.NATIVE.getFormat());

		String text = app.zPageBriefcase.displayFile(fileItem.getName(), hm)
				.get(PageBriefcase.Response.ResponsePart.BODY);

		// Right click on File, select Rename
		app.zPageBriefcase.zListItem(Action.A_RIGHTCLICK, Button.B_RENAME,
				fileItem);

		String fileName2 = "renameFile"
				+ ZimbraSeleniumProperties.getUniqueString();

		app.zPageBriefcase.rename(fileName2);

		// Verify document name through GUI
		ZAssert.assertTrue(app.zPageBriefcase
				.waitForPresentInListView(fileName2),
				"Verify new file name through GUI");

		// Display file through RestUtil
		EnumMap<PageBriefcase.Response.ResponsePart, String> response = app.zPageBriefcase
				.displayFile(fileName2, hm);

		HtmlElement element = HtmlElement.clean(response
				.get(PageBriefcase.Response.ResponsePart.BODY));
		HtmlElement.evaluate(element, "//body", null, Pattern.compile(".*"
				+ text + ".*"), 1);

		ZAssert.assertEquals(response
				.get(PageBriefcase.Response.ResponsePart.BODY), text,
				"Verify document content through GUI");
	}

	@Test(description = "Upload file through RestUtil - Verify 'Edit' toolbar button is disabled", groups = { "functional" })
	public void EditFile_03() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/putty.log";

		IItem fileItem = new FileItem(filePath);

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
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, fileItem);

		// Verify 'Edit' tool-bar button is disabled
		ZAssert.assertTrue(app.zPageBriefcase
				.isOptionDisabled(PageBriefcase.Locators.zEditFileBtn),
				"Verify 'Edit' toolbar button is disabled");

		// delete file upon test completion
		app.zPageBriefcase.deleteFileByName(fileItem.getName());
	}

	@Test(description = "Upload file through RestUtil - Verify 'Edit' context menu is disabled", groups = { "functional" })
	public void EditFile_04() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/putty.log";

		IItem fileItem = new FileItem(filePath);

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
		
		SleepUtil.sleepVerySmall();

		// Right Click on created File
		app.zPageBriefcase.zListItem(Action.A_RIGHTCLICK, fileItem);

		// Verify 'Edit' context menu is disabled
		ZAssert.assertTrue(app.zPageBriefcase
				.isOptionDisabled(PageBriefcase.Locators.zEditFileMenuItem),
				"Verify 'Edit' context menu is disabled");

		// delete file upon test completion
		app.zPageBriefcase.deleteFileByName(fileItem.getName());
	}
}
