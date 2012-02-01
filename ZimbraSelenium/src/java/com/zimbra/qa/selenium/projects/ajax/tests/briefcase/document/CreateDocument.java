package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.util.HtmlElement;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
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
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DocumentBriefcaseNew;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DocumentBriefcaseOpen;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.PageBriefcase;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.TreeBriefcase;

public class CreateDocument extends AjaxCommonTest {

	public CreateDocument() {
		logger.info("New " + CreateDocument.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = new HashMap<String, String>() {
			
			private static final long serialVersionUID = 1L;
			
			{
				put("zimbraPrefBriefcaseReadingPaneLocation", "bottom");				
			}
		};		
	}

	@Test(description = "Create document through GUI - verify through GUI", groups = { "sanity" })
	public void CreateDocument_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem docItem = new DocumentItem();

		String docName = docItem.getName();
		String docText = docItem.getDocText();

		// Open new document page
		DocumentBriefcaseNew documentBriefcaseNew = (DocumentBriefcaseNew) app.zPageBriefcase
				.zToolbarPressButton(Button.B_NEW, docItem);

		try {
			app.zPageBriefcase.zSelectWindow(DocumentBriefcaseNew.pageTitle);

			// Fill out the document with the data
			documentBriefcaseNew.zFillField(DocumentBriefcaseNew.Field.Name,
					docName);
			documentBriefcaseNew.zFillField(DocumentBriefcaseNew.Field.Body,
					docText);

			// Save and close
			app.zPageBriefcase.zSelectWindow(DocumentBriefcaseNew.pageTitle);

			documentBriefcaseNew.zSubmit();
		} finally {
			app.zPageBriefcase.zSelectWindow(PageBriefcase.pageTitle);
		}

		app.zPageBriefcase.zWaitForWindowClosed(DocumentBriefcaseNew.pageTitle);

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Click on created document
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docItem);

		// Click on open in a separate window icon in toolbar
		DocumentBriefcaseOpen documentBriefcaseOpen;

		if (ZimbraSeleniumProperties.zimbraGetVersionString().contains("7.1."))
			documentBriefcaseOpen = (DocumentBriefcaseOpen) app.zPageBriefcase
					.zToolbarPressButton(Button.B_OPEN_IN_SEPARATE_WINDOW,
							docItem);
		else
			documentBriefcaseOpen = (DocumentBriefcaseOpen) app.zPageBriefcase
					.zToolbarPressPulldown(Button.B_ACTIONS,
							Button.B_LAUNCH_IN_SEPARATE_WINDOW, docItem);

		app.zPageBriefcase.isOpenDocLoaded(docItem);

		String name = "";
		String text = "";

		// Select document opened in a separate window
		try {
			app.zPageBriefcase.zSelectWindow(docName);

			name = documentBriefcaseOpen.retriveDocumentName();
			text = documentBriefcaseOpen.retriveDocumentText();

			// close
			app.zPageBriefcase.zSelectWindow(docName);

			app.zPageBriefcase.closeWindow();
		} finally {
			app.zPageBriefcase.zSelectWindow(PageBriefcase.pageTitle);
		}

		ZAssert.assertStringContains(name, docName,
				"Verify document name through GUI");

		ZAssert.assertStringContains(text, docText,
				"Verify document text through GUI");
	}

	@Test(description = "Create document using New menu pulldown menu - verify through SOAP & RestUtil", groups = { "functional" })
	public void CreateDocument_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem document = new DocumentItem();

		String docName = document.getName();
		String docText = document.getDocText();

		// refresh briefcase page before creating a new document
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		SleepUtil.sleepVerySmall();

		// Create a new document using New pull down menu
		DocumentBriefcaseNew documentBriefcaseNew = (DocumentBriefcaseNew) app.zPageBriefcase
				.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_DOCUMENT,
						document);

		try {
			app.zPageBriefcase.zSelectWindow(DocumentBriefcaseNew.pageTitle);

			// Fill out the document with the data
			documentBriefcaseNew.zFillField(DocumentBriefcaseNew.Field.Name,
					docName);
			documentBriefcaseNew.zFillField(DocumentBriefcaseNew.Field.Body,
					docText);

			// Save and close
			app.zPageBriefcase.zSelectWindow(DocumentBriefcaseNew.pageTitle);

			documentBriefcaseNew.zSubmit();
		} finally {
			app.zPageBriefcase.zSelectWindow(PageBriefcase.pageTitle);
		}

		app.zPageBriefcase.zWaitForWindowClosed(DocumentBriefcaseNew.pageTitle);

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Display file through RestUtil
		EnumMap<PageBriefcase.Response.ResponsePart, String> response = app.zPageBriefcase
				.displayFile(docName, new HashMap<String, String>() {
					private static final long serialVersionUID = 1L;
					{
						put("fmt", PageBriefcase.Response.Format.NATIVE
								.getFormat());
					}
				});

		// Search for created document
		account
				.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
						+ "<query>" + docName + "</query>" + "</SearchRequest>");

		String name = account.soapSelectValue("//mail:doc", "name");

		ZAssert.assertNotNull(name,
				"Verify the search response returns the document name");

		ZAssert.assertStringContains(docName, name,
				"Verify document name through SOAP");

		HtmlElement element = HtmlElement.clean(response
				.get(PageBriefcase.Response.ResponsePart.BODY));
		HtmlElement.evaluate(element, "//body", null, Pattern.compile(".*"
				+ docText + ".*"), 1);

		ZAssert.assertStringContains(response
				.get(PageBriefcase.Response.ResponsePart.BODY), docText,
				"Verify document content through GUI");

		// delete file upon test completion
		app.zPageBriefcase.deleteFileByName(docName);
	}

	@Test(description = "Create document using keyboard shortcut - verify through SOAP & RestUtil", groups = { "functional" })
	public void CreateDocument_03() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem document = new DocumentItem();

		String docName = document.getName();
		String docText = document.getDocText();

		Shortcut shortcut = Shortcut.S_NEWDOCUMENT;

		// Open new document page using keyboard shortcut
		app.zPageBriefcase.zSelectWindow(PageBriefcase.pageTitle);
		DocumentBriefcaseNew documentBriefcaseNew = (DocumentBriefcaseNew) app.zPageBriefcase
				.zKeyboardShortcut(shortcut);

		try {
			app.zPageBriefcase.zSelectWindow(DocumentBriefcaseNew.pageTitle);

			// Fill out the document with the data
			documentBriefcaseNew.zFillField(DocumentBriefcaseNew.Field.Name,
					docName);
			documentBriefcaseNew.zFillField(DocumentBriefcaseNew.Field.Body,
					docText);

			// Save and close
			app.zPageBriefcase.zSelectWindow(DocumentBriefcaseNew.pageTitle);

			documentBriefcaseNew.zSubmit();
		} finally {
			app.zPageBriefcase.zSelectWindow(PageBriefcase.pageTitle);
		}

		app.zPageBriefcase.zWaitForWindowClosed(DocumentBriefcaseNew.pageTitle);

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Display file through RestUtil
		EnumMap<PageBriefcase.Response.ResponsePart, String> response = app.zPageBriefcase
				.displayFile(docName, new HashMap<String, String>() {
					private static final long serialVersionUID = 1L;
					{
						put("fmt", PageBriefcase.Response.Format.NATIVE
								.getFormat());
					}
				});

		// Search for created document
		account
				.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
						+ "<query>" + docName + "</query>" + "</SearchRequest>");

		String name = account.soapSelectValue(
				"//mail:SearchResponse//mail:doc", "name");

		ZAssert.assertNotNull(name,
				"Verify the search response returns the document name");

		ZAssert.assertStringContains(name, docName,
				"Verify document name through SOAP");

		HtmlElement element = HtmlElement.clean(response
				.get(PageBriefcase.Response.ResponsePart.BODY));
		HtmlElement.evaluate(element, "//body", null, Pattern.compile(".*"
				+ docText + ".*"), 1);

		ZAssert.assertStringContains(response
				.get(PageBriefcase.Response.ResponsePart.BODY), docText,
				"Verify document content through GUI");

		// delete file upon test completion
		app.zPageBriefcase.deleteFileByName(docName);
	}

	@Test(description = "Create document through GUI - verify through GUI", groups = { "webdriver" })
	public void CreateDocument_04() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem docItem = new DocumentItem();

		String docName = docItem.getName();
		String docText = docItem.getDocText();

		app.zPageBriefcase
		.zWaitForElementPresent(TreeBriefcase.Locators.briefcaseListView);

		// Open new document page
		DocumentBriefcaseNew documentBriefcaseNew = (DocumentBriefcaseNew) app.zPageBriefcase
				.zToolbarPressButton(Button.B_NEW, docItem);

		SleepUtil.sleepMedium();
		
		try {
			app.zPageBriefcase.zSelectWindow(DocumentBriefcaseNew.pageTitle);

			// Fill out the document with the data
			documentBriefcaseNew.zFillField(DocumentBriefcaseNew.Field.Name,
					docName);
			documentBriefcaseNew.zFillField(DocumentBriefcaseNew.Field.Body,
					docText);

			// Save and close
			app.zPageBriefcase.zSelectWindow(DocumentBriefcaseNew.pageTitle);

			documentBriefcaseNew.zSubmit();
		} finally {
			app.zPageBriefcase.zSelectWindow(PageBriefcase.pageTitle);
		}
		
		SleepUtil.sleepSmall();
		
		app.zPageBriefcase.zWaitForWindowClosed(DocumentBriefcaseNew.pageTitle);

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Click on created document
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docItem);

		// Click on open in a separate window icon in toolbar
		DocumentBriefcaseOpen documentBriefcaseOpen;

		if (ZimbraSeleniumProperties.zimbraGetVersionString().contains("8."))
			documentBriefcaseOpen = (DocumentBriefcaseOpen) app.zPageBriefcase
					.zToolbarPressPulldown(Button.B_ACTIONS,
							Button.B_LAUNCH_IN_SEPARATE_WINDOW, docItem);
		else
			documentBriefcaseOpen = (DocumentBriefcaseOpen) app.zPageBriefcase
			.zToolbarPressButton(Button.B_OPEN_IN_SEPARATE_WINDOW,
					docItem);

		app.zPageBriefcase.isOpenDocLoaded(docItem);

		String name = "";
		String text = "";

		// Select document opened in a separate window
		try {
			app.zPageBriefcase.zSelectWindow(docName);

			name = documentBriefcaseOpen.retriveDocumentName();
			text = documentBriefcaseOpen.retriveDocumentText();

			SleepUtil.sleepSmall();
			
			// close
			app.zPageBriefcase.zSelectWindow(docName);

			app.zPageBriefcase.closeWindow();
		} finally {
			app.zPageBriefcase.zSelectWindow(PageBriefcase.pageTitle);
		}

		ZAssert.assertStringContains(name, docName,
				"Verify document name through GUI");

		ZAssert.assertStringContains(text, docText,
				"Verify document text through GUI");
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
