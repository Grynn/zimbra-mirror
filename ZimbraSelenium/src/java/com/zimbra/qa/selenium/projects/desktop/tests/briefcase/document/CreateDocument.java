package com.zimbra.qa.selenium.projects.desktop.tests.briefcase.document;

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
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.briefcase.DocumentBriefcaseNew;
import com.zimbra.qa.selenium.projects.desktop.ui.briefcase.DocumentBriefcaseOpen;
import com.zimbra.qa.selenium.projects.desktop.ui.briefcase.PageBriefcase;

public class CreateDocument extends AjaxCommonTest {

	public CreateDocument() {
		logger.info("New " + CreateDocument.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = null;
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
		app.zPageBriefcase.zWaitForDesktopLoadingSpinner(5000);
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docItem);

		// Click on open in a separate window icon in toolbar
		DocumentBriefcaseOpen documentBriefcaseOpen = (DocumentBriefcaseOpen) app.zPageBriefcase
				.zToolbarPressButton(Button.B_OPEN_IN_SEPARATE_WINDOW, docItem);

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

		// Create document item
		DocumentItem document = new DocumentItem();

		String docName = document.getName();
		String docText = document.getDocText();

		// Open new document page using Pulldown menu
		DocumentBriefcaseNew documentBriefcaseNew = (DocumentBriefcaseNew) app.zPageBriefcase
				.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_DOCUMENT);

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
			GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
			app.zPageBriefcase.zWaitForDesktopLoadingSpinner(5000);
		}

		app.zPageBriefcase.zWaitForWindowClosed(DocumentBriefcaseNew.pageTitle);

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

		ZAssert.assertStringContains(docName, name,
				"Verify document name through GUI");

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
			GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
         app.zPageBriefcase.zWaitForDesktopLoadingSpinner(5000);
		}

		app.zPageBriefcase.zWaitForWindowClosed(DocumentBriefcaseNew.pageTitle);

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

		ZAssert.assertStringContains(account.soapSelectValue("//mail:doc",
				"name"), docName, "Verify document name through GUI");

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

   @Test(description = "Create local document using New menu pulldown menu - verify through SOAP & RestUtil", groups = { "smoke" })
   public void createLocalDocument() throws HarnessException {
      ZimbraAccount account = app.zGetActiveAccount();

      // Create document item
      DocumentItem document = new DocumentItem();

      String docName = document.getName();
      String docText = document.getDocText();

      FolderItem localBriefcaseFolder = FolderItem.importFromSOAP(
            app.zGetActiveAccount(),
            SystemFolder.Briefcase,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);

      app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, localBriefcaseFolder);

      // Open new document page using Pulldown menu
      DocumentBriefcaseNew documentBriefcaseNew = (DocumentBriefcaseNew) app.zPageBriefcase
            .zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_DOCUMENT);

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

      // Display file through RestUtil
      EnumMap<PageBriefcase.Response.ResponsePart, String> response = app.zPageBriefcase
            .displayFile(docName, new HashMap<String, String>() {
               private static final long serialVersionUID = 1L;
               {
                  put("fmt", PageBriefcase.Response.Format.NATIVE
                        .getFormat());
               }
            },
            SOAP_DESTINATION_HOST_TYPE.CLIENT);

      // Search for created document
      account.soapSend(
            "<SearchRequest xmlns='urn:zimbraMail' types='document'>"
                  + "<query>" + docName + "</query>" + "</SearchRequest>",
             SOAP_DESTINATION_HOST_TYPE.CLIENT,
             ZimbraAccount.clientAccountName);

      String name = account.soapSelectValue("//mail:doc", "name");

      ZAssert.assertStringContains(docName, name,
            "Verify document name through GUI");

      HtmlElement element = HtmlElement.clean(response
            .get(PageBriefcase.Response.ResponsePart.BODY));
      HtmlElement.evaluate(element, "//body", null, Pattern.compile(".*"
            + docText + ".*"), 1);

      ZAssert.assertStringContains(response
            .get(PageBriefcase.Response.ResponsePart.BODY), docText,
            "Verify document content through GUI");

      // delete file upon test completion
      app.zPageBriefcase.deleteFileByName(docName,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);
   }

   @AfterMethod(alwaysRun=true)
	public void afterMethod() throws HarnessException {
	   logger.info("Checking for the opened window ...");

	   // Check if the window is still open
	   List<String> windows = app.zPageMain.sGetAllWindowNames();
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
