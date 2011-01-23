package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DocumentBriefcaseEdit;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DocumentBriefcaseOpen;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.PageBriefcase.Locators;

public class EditDocument extends AjaxCommonTest {

	public EditDocument() {
		logger.info("New " + EditDocument.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = null;
	}

	@Test(description = "Create document through SOAP - edit name & verify through GUI", groups = { "smoke" })
	public void EditDocument_01() throws HarnessException {

		// Create document item
		DocumentItem document = new DocumentItem();

		ZimbraAccount account = app.zGetActiveAccount();
		String briefcaseFolderId = document.GetBriefcaseIdUsingSOAP(account);

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

		// Select Briefcase tab
		SleepUtil.sleepSmall();
		app.zPageBriefcase.zNavigateTo();

		// ClientSessionFactory.session().selenium().refresh();
		// refresh briefcase page
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		// Click on created document
		SleepUtil.sleepLong();

		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			app.zPageBriefcase
					.zClick("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div:contains("
							+ document.getDocName() + ")");
		}

		// Click on Edit document icon in toolbar
		DocumentBriefcaseEdit documentBriefcaseEdit = (DocumentBriefcaseEdit) app.zPageBriefcase
				.zToolbarPressButton(Button.B_EDIT_FILE);

		// Select edit document window
		SleepUtil.sleepLong();
		String windowName = document.getDocName();
		try {
			documentBriefcaseEdit.zSelectWindow(windowName);

			// if name field appears in the toolbar then document page is opened
			int i = 0;
			for (; i < 90; i++) {
				if (documentBriefcaseEdit
						.sIsElementPresent("//*[@id='DWT2_item_1']")) {
					logger.info("page loaded after " + i + " seconds");
					break;
				}
				SleepUtil.sleepSmall();
			}

			if (!documentBriefcaseEdit.sIsVisible("//*[@id='DWT2_item_1']")) {
				throw new HarnessException(
						"could not open an edit file page after " + i
								+ " seconds");
			}
			// Fill out the document with the new data
			document.setDocName("name"
					+ ZimbraSeleniumProperties.getUniqueString());

			documentBriefcaseEdit.typeDocumentName(document.getDocName());

			// Save and close
			documentBriefcaseEdit.zSubmit();
		} catch (Exception ex) {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
			throw new HarnessException("error in editing document "
					+ windowName, ex);
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		// ClientSessionFactory.session().selenium().refresh();
		// refresh briefcase page
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		// Verify document was saved with new data
		SleepUtil.sleepLong();

		String name = "";
		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			name = app.zPageBriefcase
					.sGetText("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div:contains("
							+ document.getDocName() + ")");
		}

		ZAssert.assertStringContains(name, document.getDocName(),
				"Verify document name through GUI");

		/*
		 * //name =ClientSessionFactory.session().selenium().getText(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div[id^=zlif__BDLV__]"
		 * );//ClientSessionFactory.session().selenium().isElementPresent(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto']>div:contains[id*='zlif__BDLV__']"
		 * );//ClientSessionFactory.session().selenium().isElementPresent(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] div:contains('name')"
		 * );
		 */
	}

	@Test(description = "Create document through SOAP - edit text & name & verify through GUI", groups = { "smoke" })
	public void EditDocument_02() throws HarnessException {

		// Create document item
		DocumentItem document = new DocumentItem();

		ZimbraAccount account = app.zGetActiveAccount();
		String briefcaseFolderId = document.GetBriefcaseIdUsingSOAP(account);

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

		// Select Briefcase tab
		SleepUtil.sleepSmall();
		app.zPageBriefcase.zNavigateTo();

		// ClientSessionFactory.session().selenium().refresh();
		// refresh briefcase page
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		// Click on created document
		SleepUtil.sleepLong();

		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			app.zPageBriefcase
					.zClick("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div:contains("
							+ document.getDocName() + ")");
		}

		// Click on Edit document icon in toolbar
		DocumentBriefcaseEdit documentBriefcaseEdit = (DocumentBriefcaseEdit) app.zPageBriefcase
				.zToolbarPressButton(Button.B_EDIT_FILE);

		// Select document window opened for editing
		SleepUtil.sleepLong();
		String windowName = document.getDocName();
		try {
			documentBriefcaseEdit.zSelectWindow(windowName);

			// if name field appears in the toolbar then document page is opened
			int i = 0;
			for (; i < 90; i++) {
				if (documentBriefcaseEdit
						.sIsElementPresent("//*[@id='DWT2_item_1']")) {
					logger.info("page loaded after " + i + " seconds");
					break;
				}
				SleepUtil.sleepSmall();
			}

			if (!documentBriefcaseEdit.sIsVisible("//*[@id='DWT2_item_1']")) {
				throw new HarnessException(
						"could not open an edit file page after " + i
								+ " seconds");
			}

			// Fill out the document with the new data
			document.setDocText("text"
					+ ZimbraSeleniumProperties.getUniqueString());

			documentBriefcaseEdit.typeDocumentText(document.getDocText());

			document.setDocName("name"
					+ ZimbraSeleniumProperties.getUniqueString());

			documentBriefcaseEdit.zSelectWindow(windowName);
			documentBriefcaseEdit.typeDocumentName(document.getDocName());

			// Save and close
			documentBriefcaseEdit.zSelectWindow(windowName);

			documentBriefcaseEdit.zSubmit();
		} catch (Exception ex) {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
			throw new HarnessException("error in editing document "
					+ windowName, ex);
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		// Verify document name & text through GUI
		// Select Briefcase tab
		SleepUtil.sleepSmall();
		app.zPageBriefcase.zNavigateTo();

		// ClientSessionFactory.session().selenium().refresh();
		// refresh briefcase page
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		// Click on created document
		SleepUtil.sleepLong();

		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			app.zPageBriefcase
					.zClick("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div:contains("
							+ document.getDocName() + ")");
		}

		// Click on open in a separate window icon in toolbar
		DocumentBriefcaseOpen documentBriefcaseOpen = (DocumentBriefcaseOpen) app.zPageBriefcase
				.zToolbarPressButton(Button.B_OPEN_IN_SEPARATE_WINDOW);

		// Select document opened in a separate window
		SleepUtil.sleepLong();

		windowName = document.getDocName();
		String name = "";
		String text = "";
		try {
			documentBriefcaseOpen.zSelectWindow(windowName);

			// if name field appears in the toolbar then document page is opened
			int i = 0;
			for (; i < 90; i++) {
				if (documentBriefcaseOpen
						.sIsElementPresent("css=div[id='zdocument']")) {
					break;
				}
				SleepUtil.sleepSmall();
			}

			if (!documentBriefcaseOpen.sIsVisible("css=div[id='zdocument']")) {
				throw new HarnessException(
						"could not open a file in a separate window");
			}

			name = documentBriefcaseOpen.retriveDocumentName();
			text = documentBriefcaseOpen.retriveDocumentText();

			// close
			documentBriefcaseOpen.zSelectWindow(windowName);

			ClientSessionFactory.session().selenium().close();
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		ZAssert.assertStringContains(name, document.getDocName(),
				"Verify document name through GUI");

		ZAssert.assertEquals(text, document.getDocText(),
				"Verify document text through GUI");
	}

	@Test(description = "Create document through SOAP - edit text through SOAP & verify through GUI", groups = { "smoke" })
	public void EditDocument_03() throws HarnessException {

		// Create document item
		DocumentItem document = new DocumentItem();

		ZimbraAccount account = app.zGetActiveAccount();
		String briefcaseFolderId = document.GetBriefcaseIdUsingSOAP(account);

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

		// Search for created document
		account
				.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
						+ "<query>"
						+ document.getDocName()
						+ "</query>"
						+ "</SearchRequest>");

		String docId = account.soapSelectValue("//mail:doc", "id");
		String version = account.soapSelectValue("//mail:doc", "ver");

		document
				.setDocText("text" + ZimbraSeleniumProperties.getUniqueString());

		// Edit document through SOAP
		account
				.soapSend("<SaveDocumentRequest requestId='0' xmlns='urn:zimbraMail'>"
						+ "<doc name='"
						+ document.getDocName()
						+ "' l='"
						+ briefcaseFolderId
						+ "' ver='"
						+ version
						+ "' id='"
						+ docId
						+ "' ct='application/x-zimbra-doc'>"
						+ "<content>&lt;html>&lt;body>"
						+ document.getDocText()
						+ "&lt;/body>&lt;/html></content>"
						+ "</doc>"
						+ "</SaveDocumentRequest>");

		// Select Briefcase tab
		SleepUtil.sleepSmall();
		app.zPageBriefcase.zNavigateTo();

		// ClientSessionFactory.session().selenium().refresh();
		// refresh briefcase page
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		// Click on created document
		SleepUtil.sleepLong();

		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			app.zPageBriefcase
					.zClick("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div:contains("
							+ document.getDocName() + ")");
		}

		// Click on open in a separate window icon in toolbar
		DocumentBriefcaseOpen documentBriefcaseOpen = (DocumentBriefcaseOpen) app.zPageBriefcase
				.zToolbarPressButton(Button.B_OPEN_IN_SEPARATE_WINDOW);

		// Select document opened in a separate window
		SleepUtil.sleepLong();

		String windowName = document.getDocName();
		String text = "";
		try {
			documentBriefcaseOpen.zSelectWindow(windowName);

			// if name field appears in the toolbar then document page is opened
			int i = 0;
			for (; i < 90; i++) {
				if (documentBriefcaseOpen
						.sIsElementPresent("css=div[id='zdocument']")) {
					break;
				}
				SleepUtil.sleepSmall();
			}

			if (!documentBriefcaseOpen.sIsVisible("css=div[id='zdocument']")) {
				throw new HarnessException(
						"could not open a file in a separate window");
			}

			text = documentBriefcaseOpen.retriveDocumentText();

			// close
			documentBriefcaseOpen.zSelectWindow(windowName);

			ClientSessionFactory.session().selenium().close();
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		ZAssert.assertEquals(text, document.getDocText(),
				"Verify document text through GUI");
	}

	@Test(description = "Create document through SOAP - edit text & verify through GUI", groups = { "smoke" })
	public void EditDocument_04() throws HarnessException {

		// Create document item
		DocumentItem document = new DocumentItem();

		ZimbraAccount account = app.zGetActiveAccount();
		String briefcaseFolderId = document.GetBriefcaseIdUsingSOAP(account);

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

		// Select Briefcase tab
		SleepUtil.sleepSmall();
		app.zPageBriefcase.zNavigateTo();

		// ClientSessionFactory.session().selenium().refresh();
		// refresh briefcase page
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		// Click on created document
		SleepUtil.sleepLong();

		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			app.zPageBriefcase
					.zClick("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div:contains("
							+ document.getDocName() + ")");
		}

		// Click on Edit document icon in toolbar
		DocumentBriefcaseEdit documentBriefcaseEdit = (DocumentBriefcaseEdit) app.zPageBriefcase
				.zToolbarPressButton(Button.B_EDIT_FILE);

		// Select document window opened for editing
		SleepUtil.sleepLong();
		String windowName = document.getDocName();
		try {
			documentBriefcaseEdit.zSelectWindow(windowName);

			// if name field appears in the toolbar then document page is opened
			int i = 0;
			for (; i < 90; i++) {
				if (documentBriefcaseEdit
						.sIsElementPresent("//*[@id='DWT2_item_1']")) {
					logger.info("page loaded after " + i + " seconds");
					break;
				}
				SleepUtil.sleepSmall();
			}

			if (!documentBriefcaseEdit.sIsVisible("//*[@id='DWT2_item_1']")) {
				throw new HarnessException(
						"could not open an edit file page after " + i
								+ " seconds");
			}

			// Fill out the document with the new data
			document.setDocText("text"
					+ ZimbraSeleniumProperties.getUniqueString());

			documentBriefcaseEdit.typeDocumentText(document.getDocText());

			// Save and close
			documentBriefcaseEdit.zSelectWindow(windowName);

			documentBriefcaseEdit.zSubmit();
		} catch (Exception ex) {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
			throw new HarnessException("error in editing document "
					+ windowName, ex);
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		// ClientSessionFactory.session().selenium().refresh();
		// refresh briefcase page
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		// Click on created document
		SleepUtil.sleepLong();

		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			app.zPageBriefcase
					.zClick("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div:contains("
							+ document.getDocName() + ")");
		}

		// Click on open in a separate window icon in toolbar
		DocumentBriefcaseOpen documentBriefcaseOpen = (DocumentBriefcaseOpen) app.zPageBriefcase
				.zToolbarPressButton(Button.B_OPEN_IN_SEPARATE_WINDOW);

		// Select document opened in a separate window
		SleepUtil.sleepLong();

		String text = "";
		try {
			documentBriefcaseOpen.zSelectWindow(windowName);

			// if name field appears in the toolbar then document page is opened
			int i = 0;
			for (; i < 90; i++) {
				if (documentBriefcaseOpen
						.sIsElementPresent("css=div[id='zdocument']")) {
					break;
				}
				SleepUtil.sleepSmall();
			}

			if (!documentBriefcaseOpen.sIsVisible("css=div[id='zdocument']")) {
				throw new HarnessException(
						"could not open a file in a separate window");
			}

			text = documentBriefcaseOpen.retriveDocumentText();

			// close
			documentBriefcaseOpen.zSelectWindow(windowName);

			ClientSessionFactory.session().selenium().close();
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		ZAssert.assertEquals(text, document.getDocText(),
				"Verify document text through GUI");
	}
}
