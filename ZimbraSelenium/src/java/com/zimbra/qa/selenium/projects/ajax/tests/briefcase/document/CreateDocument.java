package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import org.testng.annotations.Test;

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
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DocumentBriefcaseNew;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DocumentBriefcaseOpen;

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
		DocumentItem document = new DocumentItem();

		String docName = document.getDocName();
		String docText = document.getDocText();

		// Open new document page
		DocumentBriefcaseNew documentBriefcaseNew = (DocumentBriefcaseNew) app.zPageBriefcase
				.zToolbarPressButton(Button.B_NEW);
		
		try {
			app.zPageBriefcase.zSelectWindow("Zimbra Docs");

			// Fill out the document with the data
			documentBriefcaseNew.zFillField(DocumentBriefcaseNew.Field.Name,
					docName);
			documentBriefcaseNew.zFillField(DocumentBriefcaseNew.Field.Body,
					docText);

			// Save and close
			app.zPageBriefcase.zSelectWindow("Zimbra Docs");

			documentBriefcaseNew.zSubmit();
		} finally {
			documentBriefcaseNew.zSelectWindow("Zimbra: Briefcase");
		}

		app.zPageBriefcase.zIsWindowClosed("Zimbra Docs");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Click on created document
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docName);

		// Click on open in a separate window icon in toolbar
		DocumentBriefcaseOpen documentBriefcaseOpen = (DocumentBriefcaseOpen) app.zPageBriefcase
				.zToolbarPressButton(Button.B_OPEN_IN_SEPARATE_WINDOW);

		app.zPageBriefcase.isOpenDocLoaded(docName, docText);

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
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		ZAssert.assertStringContains(name, docName,
				"Verify document name through GUI");

		ZAssert.assertStringContains(text, docText,
				"Verify document text through GUI");
	}

	@Test(description = "Create document using New menu pulldown menu - verify through SOAP", groups = { "functional" })
	public void CreateDocument_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem document = new DocumentItem();

		String docName = document.getDocName();
		String docText = document.getDocText();

		// Open new document page using keyboard shortcut
		DocumentBriefcaseNew documentBriefcaseNew = (DocumentBriefcaseNew) app.zPageBriefcase
				.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_DOCUMENT);

		try {
			app.zPageBriefcase.zSelectWindow("Zimbra Docs");

			// Fill out the document with the data
			documentBriefcaseNew.zFillField(DocumentBriefcaseNew.Field.Name,
					docName);
			documentBriefcaseNew.zFillField(DocumentBriefcaseNew.Field.Body,
					docText);

			// Save and close
			app.zPageBriefcase.zSelectWindow("Zimbra Docs");

			documentBriefcaseNew.zSubmit();
		} finally {
			documentBriefcaseNew.zSelectWindow("Zimbra: Briefcase");
		}

		app.zPageBriefcase.zIsWindowClosed("Zimbra Docs");

		// Search for created document
		account
				.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
						+ "<query>" + docName + "</query>" + "</SearchRequest>");

		String name = account.soapSelectValue("//mail:doc", "name");
		
		ZAssert.assertStringContains(docName, name,
				"Verify document name through GUI");		
	}

	@Test(description = "Create document using keyboard shortcut - verify through SOAP", groups = { "functional" })
	public void CreateDocument_03() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem document = new DocumentItem();

		String docName = document.getDocName();
		String docText = document.getDocText();

		Shortcut shortcut = Shortcut.S_NEWDOCUMENT;

		// Open new document page using keyboard shortcut
		app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		DocumentBriefcaseNew documentBriefcaseNew = (DocumentBriefcaseNew) app.zPageBriefcase
				.zKeyboardShortcut(shortcut);

		try {
			app.zPageBriefcase.zSelectWindow("Zimbra Docs");

			// Fill out the document with the data
			documentBriefcaseNew.zFillField(DocumentBriefcaseNew.Field.Name,
					docName);
			documentBriefcaseNew.zFillField(DocumentBriefcaseNew.Field.Body,
					docText);

			// Save and close
			app.zPageBriefcase.zSelectWindow("Zimbra Docs");

			documentBriefcaseNew.zSubmit();
		} finally {
			documentBriefcaseNew.zSelectWindow("Zimbra: Briefcase");
		}

		app.zPageBriefcase.zIsWindowClosed("Zimbra Docs");

		// Search for created document
		account
				.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
						+ "<query>" + docName + "</query>" + "</SearchRequest>");

		String name = account.soapSelectValue("//mail:doc", "name");
		
		ZAssert.assertStringContains(docName, name,
				"Verify document name through GUI");		
	}
}
