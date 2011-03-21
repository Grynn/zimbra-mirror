package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DocumentBriefcaseEdit;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DocumentBriefcaseOpen;

public class EditDocument extends AjaxCommonTest {

	public EditDocument() {
		logger.info("New " + EditDocument.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = null;
	}

	@Test(description = "Create document through SOAP - edit name & verify through GUI", groups = { "smoke" })
	public void EditDocument_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem document = new DocumentItem();

		String docName = document.getDocName();
		String docText = document.getDocText();

		// Create document using SOAP
		String contentHTML = XmlStringUtil.escapeXml("<html>" + "<body>"
				+ docText + "</body>" + "</html>");

		account
				.soapSend("<SaveDocumentRequest requestId='0' xmlns='urn:zimbraMail'>"
						+ "<doc name='"
						+ docName
						+ "' l='"
						+ briefcaseFolder.getId()
						+ "' ct='application/x-zimbra-doc'>"
						+ "<content>"
						+ contentHTML
						+ "</content>"
						+ "</doc>"
						+ "</SaveDocumentRequest>");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Click on created document
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docName);

		// Click on Edit document icon in toolbar
		DocumentBriefcaseEdit documentBriefcaseEdit = (DocumentBriefcaseEdit) app.zPageBriefcase
				.zToolbarPressButton(Button.B_EDIT_FILE);

		app.zPageBriefcase.isEditDocLoaded(docName, docText);

		// Select edit document window
		try {
			app.zPageBriefcase.zSelectWindow(docName);

			// Fill out the document with the new data
			document.setDocName("name"
					+ ZimbraSeleniumProperties.getUniqueString());

			documentBriefcaseEdit.typeDocumentName(document.getDocName());

			// Save and close
			app.zPageBriefcase.zSelectWindow(docName);

			documentBriefcaseEdit.zSubmit();
		} catch (Exception ex) {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
			throw new HarnessException("error in editing document " + docName,
					ex);
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		docName = document.getDocName();

		// Verify document was saved with new data
		// String name = app.zPageBriefcase.getText(docName);
		// ZAssert.assertStringContains(name, docName,
		// "Verify document name through GUI");
		boolean present = app.zPageBriefcase.isPresent(docName);

		ZAssert.assertTrue(present, "Verify document name through GUI");
		
		//delete file upon test completion
		app.zPageBriefcase.deleteFileByName(docName);
		
		/*
		 * //name =ClientSessionFactory.session().selenium().getText(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width*='auto'] div[id^=zlif__BDLV__]"
		 * );//ClientSessionFactory.session().selenium().isElementPresent(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width*='auto']>div:contains[id*='zlif__BDLV__']"
		 * );//ClientSessionFactory.session().selenium().isElementPresent(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] div:contains('name')"
		 * );
		 */
	}

	@Test(description = "Create document through SOAP - edit text & name & verify through GUI", groups = { "smoke" })
	public void EditDocument_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem document = new DocumentItem();

		String docName = document.getDocName();
		String docText = document.getDocText();

		// Create document using SOAP
		String contentHTML = XmlStringUtil.escapeXml("<html>" + "<body>"
				+ docText + "</body>" + "</html>");

		account
				.soapSend("<SaveDocumentRequest requestId='0' xmlns='urn:zimbraMail'>"
						+ "<doc name='"
						+ docName
						+ "' l='"
						+ briefcaseFolder.getId()
						+ "' ct='application/x-zimbra-doc'>"
						+ "<content>"
						+ contentHTML
						+ "</content>"
						+ "</doc>"
						+ "</SaveDocumentRequest>");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Click on created document
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docName);

		// Click on Edit document icon in toolbar
		DocumentBriefcaseEdit documentBriefcaseEdit = (DocumentBriefcaseEdit) app.zPageBriefcase
				.zToolbarPressButton(Button.B_EDIT_FILE);

		app.zPageBriefcase.isEditDocLoaded(docName, docText);

		// Select document window opened for editing
		try {
			app.zPageBriefcase.zSelectWindow(docName);

			// Fill out the document with the new data
			document.setDocText("text"
					+ ZimbraSeleniumProperties.getUniqueString());

			documentBriefcaseEdit.typeDocumentText(document.getDocText());

			app.zPageBriefcase.zSelectWindow(docName);

			document.setDocName("name"
					+ ZimbraSeleniumProperties.getUniqueString());

			documentBriefcaseEdit.typeDocumentName(document.getDocName());

			// Save and close
			app.zPageBriefcase.zSelectWindow(docName);

			documentBriefcaseEdit.zSubmit();
		} catch (Exception ex) {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
			throw new HarnessException("error in editing document " + docName,
					ex);
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		docName = document.getDocName();
		docText = document.getDocText();

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
		
		//delete file upon test completion
		app.zPageBriefcase.deleteFileByName(docName);
	}

	@Test(description = "Create document & edit text through SOAP & verify through GUI", groups = { "smoke" })
	public void EditDocument_03() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem document = new DocumentItem();

		String docName = document.getDocName();
		String docText = document.getDocText();

		// Create document using SOAP
		String contentHTML = XmlStringUtil.escapeXml("<html>" + "<body>"
				+ docText + "</body>" + "</html>");

		account
				.soapSend("<SaveDocumentRequest requestId='0' xmlns='urn:zimbraMail'>"
						+ "<doc name='"
						+ docName
						+ "' l='"
						+ briefcaseFolder.getId()
						+ "' ct='application/x-zimbra-doc'>"
						+ "<content>"
						+ contentHTML
						+ "</content>"
						+ "</doc>"
						+ "</SaveDocumentRequest>");

		// Search for created document
		account
				.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
						+ "<query>" + docName + "</query>" + "</SearchRequest>");

		String docId = account.soapSelectValue("//mail:doc", "id");
		String version = account.soapSelectValue("//mail:doc", "ver");

		document
				.setDocText("text" + ZimbraSeleniumProperties.getUniqueString());

		docText = document.getDocText();

		// Edit document through SOAP
		account
				.soapSend("<SaveDocumentRequest requestId='0' xmlns='urn:zimbraMail'>"
						+ "<doc name='"
						+ docName
						+ "' l='"
						+ briefcaseFolder.getId()
						+ "' ver='"
						+ version
						+ "' id='"
						+ docId
						+ "' ct='application/x-zimbra-doc'>"
						+ "<content>&lt;html>&lt;body>"
						+ docText
						+ "&lt;/body>&lt;/html></content>"
						+ "</doc>"
						+ "</SaveDocumentRequest>");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Click on created document
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docName);

		// Click on open in a separate window icon in toolbar
		DocumentBriefcaseOpen documentBriefcaseOpen = (DocumentBriefcaseOpen) app.zPageBriefcase
				.zToolbarPressButton(Button.B_OPEN_IN_SEPARATE_WINDOW);

		app.zPageBriefcase.isOpenDocLoaded(docName, docText);

		String text = "";

		// Select document opened in a separate window
		try {
			app.zPageBriefcase.zSelectWindow(docName);

			text = documentBriefcaseOpen.retriveDocumentText();

			// close
			app.zPageBriefcase.zSelectWindow(docName);

			app.zPageBriefcase.closeWindow();
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		ZAssert.assertStringContains(text, docText,
				"Verify document text through GUI");
		
		//delete file upon test completion
		app.zPageBriefcase.deleteFileByName(docName);
	}

	@Test(description = "Create document through SOAP - edit text & verify through GUI", groups = { "smoke" })
	public void EditDocument_04() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem document = new DocumentItem();

		String docName = document.getDocName();
		String docText = document.getDocText();

		// Create document using SOAP
		String contentHTML = XmlStringUtil.escapeXml("<html>" + "<body>"
				+ docText + "</body>" + "</html>");

		account
				.soapSend("<SaveDocumentRequest requestId='0' xmlns='urn:zimbraMail'>"
						+ "<doc name='"
						+ docName
						+ "' l='"
						+ briefcaseFolder.getId()
						+ "' ct='application/x-zimbra-doc'>"
						+ "<content>"
						+ contentHTML
						+ "</content>"
						+ "</doc>"
						+ "</SaveDocumentRequest>");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Click on created document
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docName);

		// Click on Edit document icon in toolbar
		DocumentBriefcaseEdit documentBriefcaseEdit = (DocumentBriefcaseEdit) app.zPageBriefcase
				.zToolbarPressButton(Button.B_EDIT_FILE);

		app.zPageBriefcase.isEditDocLoaded(docName, docText);

		// Select document window opened for editing
		try {
			app.zPageBriefcase.zSelectWindow(docName);

			// Fill out the document with the new data
			document.setDocText("text"
					+ ZimbraSeleniumProperties.getUniqueString());

			documentBriefcaseEdit.typeDocumentText(document.getDocText());

			// Save and close
			app.zPageBriefcase.zSelectWindow(docName);

			documentBriefcaseEdit.zSubmit();
		} catch (Exception ex) {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
			throw new HarnessException("error in editing document " + docName,
					ex);
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		docText = document.getDocText();

		// Click on created document
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docName);

		// Click on open in a separate window icon in toolbar
		DocumentBriefcaseOpen documentBriefcaseOpen = (DocumentBriefcaseOpen) app.zPageBriefcase
				.zToolbarPressButton(Button.B_OPEN_IN_SEPARATE_WINDOW);

		app.zPageBriefcase.isOpenDocLoaded(docName, docText);

		String text = "";

		// Select document opened in a separate window
		try {
			app.zPageBriefcase.zSelectWindow(docName);

			text = documentBriefcaseOpen.retriveDocumentText();

			// close
			app.zPageBriefcase.zSelectWindow(docName);

			app.zPageBriefcase.closeWindow();
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		ZAssert.assertStringContains(text, docText,
				"Verify document text through GUI");
		
		//delete file upon test completion
		app.zPageBriefcase.deleteFileByName(docName);
	}
	
	@Test(description = "Create document through SOAP - Edit Document using Right Click Context Menu & verify through GUI", groups = { "functional" })
	public void EditDocument_05() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem document = new DocumentItem();

		String docName = document.getDocName();
		String docText = document.getDocText();

		// Create document using SOAP
		String contentHTML = XmlStringUtil.escapeXml("<html>" + "<body>"
				+ docText + "</body>" + "</html>");

		account
				.soapSend("<SaveDocumentRequest requestId='0' xmlns='urn:zimbraMail'>"
						+ "<doc name='"
						+ docName
						+ "' l='"
						+ briefcaseFolder.getId()
						+ "' ct='application/x-zimbra-doc'>"
						+ "<content>"
						+ contentHTML
						+ "</content>"
						+ "</doc>"
						+ "</SaveDocumentRequest>");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Click on created document
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docName);

		// Edit Document using Right Click Context Menu
		DocumentBriefcaseEdit documentBriefcaseEdit = (DocumentBriefcaseEdit) app.zPageBriefcase.
		zListItem(Action.A_RIGHTCLICK, Button.O_EDIT, document);
				
		//app.zPageBriefcase.isEditDocLoaded(docName, docText);
		
		// Select edit document window
		try {
			app.zPageBriefcase.zSelectWindow(docName);

			// Fill out the document with the new data
			document.setDocName("name"
					+ ZimbraSeleniumProperties.getUniqueString());

			documentBriefcaseEdit.typeDocumentName(document.getDocName());

			// Save and close
			app.zPageBriefcase.zSelectWindow(docName);

			documentBriefcaseEdit.zSubmit();
		} catch (Exception ex) {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
			throw new HarnessException("error in editing document " + docName,
					ex);
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		docName = document.getDocName();

		// Verify document was saved with new data
		// String name = app.zPageBriefcase.getText(docName);
		// ZAssert.assertStringContains(name, docName,
		// "Verify document name through GUI");
		boolean present = app.zPageBriefcase.isPresent(docName);

		ZAssert.assertTrue(present, "Verify document name through GUI");
		
		//delete file upon test completion
		app.zPageBriefcase.deleteFileByName(docName);
		
		/*
		 * //name =ClientSessionFactory.session().selenium().getText(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width*='auto'] div[id^=zlif__BDLV__]"
		 * );//ClientSessionFactory.session().selenium().isElementPresent(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width*='auto']>div:contains[id*='zlif__BDLV__']"
		 * );//ClientSessionFactory.session().selenium().isElementPresent(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] div:contains('name')"
		 * );
		 */
	}
}
