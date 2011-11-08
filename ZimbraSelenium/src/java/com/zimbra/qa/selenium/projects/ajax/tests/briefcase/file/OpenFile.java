package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.file;

import java.util.HashMap;
import java.util.List;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DocumentBriefcaseOpen;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.PageBriefcase;

public class OpenFile extends AjaxCommonTest {

	public OpenFile() {
		logger.info("New " + OpenFile.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = new HashMap<String, String>() {
			
			private static final long serialVersionUID = 1L;
			
			{
				put("zimbraPrefBriefcaseReadingPaneLocation", "bottom");
			}
		};
	}

	@Test(description = "Upload file through RestUtil - open & verify through GUI", groups = { "smoke" })
	public void OpenFile_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create file item
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/testtextfile.txt";

		FileItem fileItem = new FileItem(filePath);

		String fileName = fileItem.getName();

		final String fileText = "test";

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to briefcase through SOAP
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+ "<doc l='" + briefcaseFolder.getId() + "'><upload id='"
				+ attachmentId + "'/></doc></SaveDocumentRequest>");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		SleepUtil.sleepVerySmall();

		// Click on created file
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, fileItem);

		// Click on open in a separate window icon in toolbar
	
		DocumentBriefcaseOpen file;
		
		if (ZimbraSeleniumProperties.zimbraGetVersionString().contains("7.1."))
			file = (DocumentBriefcaseOpen)app.zPageBriefcase
					.zToolbarPressButton(Button.B_OPEN_IN_SEPARATE_WINDOW,
							fileItem);
		else
			file = (DocumentBriefcaseOpen)app.zPageBriefcase
					.zToolbarPressPulldown(Button.B_ACTIONS,
							Button.B_LAUNCH_IN_SEPARATE_WINDOW,fileItem);

		app.zPageBriefcase.isOpenFileLoaded(fileName, fileText);

		String text = "";

		// Select document opened in a separate window
		try {
			app.zPageBriefcase.zSelectWindow(fileName);

			text = file.retriveFileText();

			// close
			app.zPageBriefcase.zSelectWindow(fileName);

			app.zPageBriefcase.closeWindow();
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		ZAssert.assertStringContains(text, fileText,
				"Verify document text through GUI");

		// delete file upon test completion
		app.zPageBriefcase.deleteFileByName(fileItem.getName());
	}

	@AfterMethod(groups = { "always" })
	public void afterMethod() throws HarnessException {
		logger.info("Checking for the opened window ...");

		// Check if the window is still open
		List<String> windows = app.zPageBriefcase.sGetAllWindowNames();
		for (String window : windows) {
			if (!window.isEmpty() && !window.contains("null")
					&& !window.contains(PageBriefcase.pageTitle)
					&& !window.contains("main_app_window")
					&& !window.contains("undefined")) {
				logger.warn(window + " window was still active. Closing ...");
				app.zPageBriefcase.zSelectWindow(window);
				app.zPageBriefcase.closeWindow();
			}
		}
		app.zPageBriefcase.zSelectWindow(PageBriefcase.pageTitle);
	}
}
