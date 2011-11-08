package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import java.util.HashMap;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
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
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DialogConfirm;

public class DeleteDocument extends AjaxCommonTest {

	public DeleteDocument() {
		logger.info("New " + DeleteDocument.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;
		
		super.startingAccountPreferences = new HashMap<String, String>() {
			
			private static final long serialVersionUID = 1L;
			
			{
				put("zimbraPrefBriefcaseReadingPaneLocation", "bottom");				
			}
		};	
	}

	@Test(description = "Create document through SOAP - delete & check trash", groups = { "smoke" })
	public void DeleteDocument_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		FolderItem trashFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Trash);

		// Create document item
		DocumentItem docItem = new DocumentItem();

		String docName = docItem.getName();
		String docText = docItem.getDocText();

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

		String docId = account.soapSelectValue(
				"//mail:SaveDocumentResponse//mail:doc", "id");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		SleepUtil.sleepVerySmall();

		// Click on created document
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docItem);

		// Click on Delete document icon in toolbar
		DialogConfirm deleteConfirm = (DialogConfirm) app.zPageBriefcase
				.zToolbarPressButton(Button.B_DELETE, docItem);

		// Click OK on Confirmation dialog
		deleteConfirm.zClickButton(Button.B_YES);

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// Verify document was deleted from the list
		boolean isDeleted = app.zPageBriefcase
				.waitForDeletedFromListView(docName);

		ZAssert
				.assertTrue(isDeleted,
						"Verify document was deleted through GUI");

		// Verify document moved to Trash
		account
				.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
						+ "<query>in:"
						+ trashFolder.getName()
						+ " "
						+ docName
						+ "</query>" + "</SearchRequest>");

		String id = account.soapSelectValue("//mail:SearchResponse//mail:doc",
				"id");

		ZAssert.assertNotNull(id,
				"Verify the search response returns the document id");

		ZAssert.assertEquals(id, docId,
				"Verify the document was moved to the trash folder");
	}

	@Test(description = "Create document through SOAP - delete using Delete Key & verify through GUI", groups = { "functional" })
	public void DeleteDocument_02() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem docItem = new DocumentItem();

		String docName = docItem.getName();
		String docText = docItem.getDocText();

		Shortcut shortcut = Shortcut.S_DELETE;

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

		SleepUtil.sleepVerySmall();

		// Click on created document
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docItem);

		// Click the Delete keyboard shortcut
		// app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		DialogConfirm deleteConfirm = (DialogConfirm) app.zPageBriefcase
				.zKeyboardShortcut(shortcut);

		// Click OK on Confirmation dialog
		deleteConfirm.zClickButton(Button.B_YES);

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// Verify document was deleted
		boolean isDeleted = app.zPageBriefcase
				.waitForDeletedFromListView(docName);

		ZAssert
				.assertTrue(isDeleted,
						"Verify document was deleted through GUI");
	}

	@Test(description = "Create document through SOAP - delete using Backspace Key & verify through GUI", groups = { "functional" })
	public void DeleteDocument_03() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem docItem = new DocumentItem();

		String docName = docItem.getName();
		String docText = docItem.getDocText();

		Shortcut shortcut = Shortcut.S_BACKSPACE;

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

		SleepUtil.sleepVerySmall();

		// Click on created document
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docItem);

		// Delete Document using Backspace keyboard shortcut
		// app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		DialogConfirm deleteConfirm = (DialogConfirm) app.zPageBriefcase
				.zKeyboardShortcut(shortcut);

		// Click OK on Confirmation dialog
		deleteConfirm.zClickButton(Button.B_YES);

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// Verify document was deleted
		boolean isDeleted = app.zPageBriefcase
				.waitForDeletedFromListView(docName);

		ZAssert
				.assertTrue(isDeleted,
						"Verify document was deleted through GUI");
	}

	@Test(description = "Create document through SOAP - delete using Right Click context menu & verify through GUI", groups = { "functional" })
	public void DeleteDocument_04() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem docItem = new DocumentItem();

		String docName = docItem.getName();
		String docText = docItem.getDocText();

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
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docItem);

		// Delete Document using Right Click Context Menu
		DialogConfirm deleteConfirm = (DialogConfirm) app.zPageBriefcase
				.zListItem(Action.A_RIGHTCLICK, Button.O_DELETE, docItem);

		// Click OK on Confirmation dialog
		deleteConfirm.zClickButton(Button.B_YES);

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// Verify document was deleted
		boolean isDeleted = app.zPageBriefcase
				.waitForDeletedFromListView(docName);

		ZAssert
				.assertTrue(isDeleted,
						"Verify document was deleted through GUI");
	}

	@Test(description = "Delete multiple documents(3) by selecting check box and delete using toolbar", groups = { "functional" })
	public void DeleteDocument_05() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		FolderItem trashFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Trash);

		// Create documents using SOAP
		DocumentItem[] docItems = {
				new DocumentItem("docName1"
						+ ZimbraSeleniumProperties.getUniqueString()),
				new DocumentItem("docName2"
						+ ZimbraSeleniumProperties.getUniqueString()),
				new DocumentItem("docName3"
						+ ZimbraSeleniumProperties.getUniqueString()) };

		String contentHTML = XmlStringUtil.escapeXml("<html>" + "<body>"
				+ ZimbraSeleniumProperties.getUniqueString() + "</body>"
				+ "</html>");

		for (int i = 0; i < docItems.length; i++) {
			account
					.soapSend("<SaveDocumentRequest requestId='0' xmlns='urn:zimbraMail'>"
							+ "<doc name='"
							+ docItems[i].getName()
							+ "' l='"
							+ briefcaseFolder.getId()
							+ "' ct='application/x-zimbra-doc'>"
							+ "<content>"
							+ contentHTML
							+ "</content>"
							+ "</doc>"
							+ "</SaveDocumentRequest>");

			docItems[i].setDocId(account.soapSelectValue(
					"//mail:SaveDocumentResponse//mail:doc", "id"));

		}

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Select all three items
		for (DocumentItem item : docItems) {
			app.zPageBriefcase.zListItem(Action.A_BRIEFCASE_CHECKBOX, item);
			SleepUtil.sleepVerySmall();
		}

		// Click toolbar delete button
		DialogConfirm deleteConfirm = (DialogConfirm) app.zPageBriefcase
				.zToolbarPressButton(Button.B_DELETE, docItems[0]);

		// Click OK on Confirmation dialog
		deleteConfirm.zClickButton(Button.B_YES);

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		SleepUtil.sleepVerySmall();

		// Verify items are deleted);
		for (DocumentItem item : docItems) {
			String name = item.getName();
			String docId = item.getId();

			// Verify document was deleted from the list
			ZAssert.assertFalse(app.zPageBriefcase.isPresentInListView(name),
					"Verify the document " + name
							+ " is no longer in the list view");

			// Verify document moved to Trash
			account
					.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
							+ "<query>in:"
							+ trashFolder.getName()
							+ " "
							+ name
							+ "</query>" + "</SearchRequest>");

			String id = account.soapSelectValue(
					"//mail:SearchResponse//mail:doc", "id");

			ZAssert.assertNotNull(id,
					"Verify the search response returns the document id");

			ZAssert
					.assertEquals(docId, id, "Verify the deleted document: "
							+ name + " id: " + docId
							+ " was moved to the trash folder");
		}
	}
}