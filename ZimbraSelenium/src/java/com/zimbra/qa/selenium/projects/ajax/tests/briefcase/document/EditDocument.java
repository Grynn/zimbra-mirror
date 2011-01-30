package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
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
		// SleepUtil.sleepSmall();
		app.zPageBriefcase.zNavigateTo();

		// ClientSessionFactory.session().selenium().refresh();
		// refresh briefcase page
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		app.zPageBriefcase
				.waitForCondition(
						"selenium.isElementPresent(\"css=[id='zti__main_Briefcase__16_div'][class='DwtTreeItem-selected']\")&&"
								+ "selenium.isElementPresent(\"css=[id='zl__BDLV__rows'] div[class^='Row']\");",
						"5000");

		// Click on created document
		// SleepUtil.sleepLong();

		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			app.zPageBriefcase
					.zClick("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width*='auto'] div:contains("
							+ document.getDocName() + ")");
		}

		// Click on Edit document icon in toolbar
		DocumentBriefcaseEdit documentBriefcaseEdit = (DocumentBriefcaseEdit) app.zPageBriefcase
				.zToolbarPressButton(Button.B_EDIT_FILE);

		String windowName = document.getDocName();
		app.zPageBriefcase.waitForWindow(windowName, "5000");

		// Select edit document window
		// SleepUtil.sleepLong();

		try {
			app.zPageBriefcase.zSelectWindow(windowName);

			// if html body appears then document page is opened
			if (!app.zPageBriefcase.waitForElement("css=iframe[id='DWT9'][class='ZDEditor']", "50000")){
				throw new HarnessException("could not open an edit document page");
			}
		
			app.zPageBriefcase.waitForText("css=iframe[id='DWT9'][class='ZDEditor']", document.getDocText(), "5000");

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

		// refresh briefcase page
		// ClientSessionFactory.session().selenium().refresh();
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		app.zPageBriefcase
				.waitForCondition(
						"selenium.isElementPresent(\"css=[id='zti__main_Briefcase__16_div'][class='DwtTreeItem-selected']\")&&"
								+ "selenium.isElementPresent(\"css=[id='zl__BDLV__rows'] div[class^='Row']\");",
						"5000");

		// Verify document was saved with new data
		// SleepUtil.sleepLong();

		String name = "";
		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			name = app.zPageBriefcase
					.sGetText("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width*='auto'] div:contains("
							+ document.getDocName() + ")");
		}

		ZAssert.assertStringContains(name, document.getDocName(),
				"Verify document name through GUI");

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
		// SleepUtil.sleepSmall();
		app.zPageBriefcase.zNavigateTo();

		// refresh briefcase page
		// ClientSessionFactory.session().selenium().refresh();
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		app.zPageBriefcase
				.waitForCondition(
						"selenium.isElementPresent(\"css=[id='zti__main_Briefcase__16_div'][class='DwtTreeItem-selected']\")&&"
								+ "selenium.isElementPresent(\"css=[id='zl__BDLV__rows'] div[class^='Row']\");",
						"5000");

		// Click on created document
		// SleepUtil.sleepLong();

		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			app.zPageBriefcase
					.zClick("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width*='auto'] div:contains("
							+ document.getDocName() + ")");
		}

		// Click on Edit document icon in toolbar
		DocumentBriefcaseEdit documentBriefcaseEdit = (DocumentBriefcaseEdit) app.zPageBriefcase
				.zToolbarPressButton(Button.B_EDIT_FILE);

		String windowName = document.getDocName();
		app.zPageBriefcase.waitForWindow(windowName, "5000");

		// Select document window opened for editing
		
		try {
			app.zPageBriefcase.zSelectWindow(windowName);

			// if html body appears then document page is opened
			if (!app.zPageBriefcase.waitForElement("css=iframe[id*='DWT'][class='ZDEditor']", "50000")){
				throw new HarnessException("could not open an edit document page");
			}
		
			app.zPageBriefcase.waitForText("css=iframe[id*='DWT'][class='ZDEditor']", document.getDocText(), "5000");

			// Fill out the document with the new data
			document.setDocText("text"
					+ ZimbraSeleniumProperties.getUniqueString());

			documentBriefcaseEdit.typeDocumentText(document.getDocText());

			document.setDocName("name"
					+ ZimbraSeleniumProperties.getUniqueString());

			app.zPageBriefcase.zSelectWindow(windowName);
			documentBriefcaseEdit.typeDocumentName(document.getDocName());

			// Save and close
			app.zPageBriefcase.zSelectWindow(windowName);

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
		app.zPageBriefcase.zNavigateTo();

		// refresh briefcase page
		// ClientSessionFactory.session().selenium().refresh();
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		app.zPageBriefcase
				.waitForCondition(
						"selenium.isElementPresent(\"css=[id='zti__main_Briefcase__16_div'][class='DwtTreeItem-selected']\")&&"
								+ "selenium.isElementPresent(\"css=[id='zl__BDLV__rows'] div[class^='Row']\");",
						"5000");

		// Click on created document
		// SleepUtil.sleepLong();

		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			app.zPageBriefcase
					.zClick("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width*='auto'] div:contains("
							+ document.getDocName() + ")");
		}

		// Click on open in a separate window icon in toolbar
		DocumentBriefcaseOpen documentBriefcaseOpen = (DocumentBriefcaseOpen) app.zPageBriefcase
				.zToolbarPressButton(Button.B_OPEN_IN_SEPARATE_WINDOW);

		windowName = document.getDocName();
		app.zPageBriefcase.waitForWindow(windowName, "5000");

		// Select document opened in a separate window
		// SleepUtil.sleepLong();

		String name = "";
		String text = "";
		try {
			app.zPageBriefcase.zSelectWindow(windowName);

			app.zPageBriefcase.waitForElement(
					"css=td[class='ZhAppContent'] div:contains('"
							+ document.getDocText() + "')", "60000");

			if (!documentBriefcaseOpen.sIsVisible("css=div[id='zdocument']")) {
				throw new HarnessException(
						"could not open a file in a separate window");
			}

			name = documentBriefcaseOpen.retriveDocumentName();
			text = documentBriefcaseOpen.retriveDocumentText();

			// close
			app.zPageBriefcase.zSelectWindow(windowName);

			ClientSessionFactory.session().selenium().close();
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		ZAssert.assertStringContains(name, document.getDocName(),
				"Verify document name through GUI");

		ZAssert.assertStringContains(text, document.getDocText(),
				"Verify document text through GUI");
	}

	@Test(description = "Create document & edit text through SOAP & verify through GUI", groups = { "smoke" })
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
		// SleepUtil.sleepSmall();
		app.zPageBriefcase.zNavigateTo();

		// refresh briefcase page
		// ClientSessionFactory.session().selenium().refresh();
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		app.zPageBriefcase
				.waitForCondition(
						"selenium.isElementPresent(\"css=[id='zti__main_Briefcase__16_div'][class='DwtTreeItem-selected']\")&&"
								+ "selenium.isElementPresent(\"css=[id='zl__BDLV__rows'] div[class^='Row']\");",
						"5000");

		// Click on created document
		// SleepUtil.sleepLong();

		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			app.zPageBriefcase
					.zClick("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width*='auto'] div:contains("
							+ document.getDocName() + ")");
		}

		// Click on open in a separate window icon in toolbar
		DocumentBriefcaseOpen documentBriefcaseOpen = (DocumentBriefcaseOpen) app.zPageBriefcase
				.zToolbarPressButton(Button.B_OPEN_IN_SEPARATE_WINDOW);

		String windowName = document.getDocName();
		app.zPageBriefcase.waitForWindow(windowName, "5000");

		// Select document opened in a separate window
		// SleepUtil.sleepLong();

		String text = "";
		try {
			app.zPageBriefcase.zSelectWindow(windowName);

			app.zPageBriefcase.waitForElement(
					"css=td[class='ZhAppContent'] div:contains('"
							+ document.getDocText() + "')", "60000");

			if (!documentBriefcaseOpen
					.sIsVisible("css=td[class='ZhAppContent'] div[id='zdocument']")) {
				throw new HarnessException(
						"could not open a file in a separate window");
			}

			text = documentBriefcaseOpen.retriveDocumentText();

			// close
			app.zPageBriefcase.zSelectWindow(windowName);

			app.zPageBriefcase.closeWindow();
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		ZAssert.assertStringContains(text, document.getDocText(),
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
		// SleepUtil.sleepSmall();
		app.zPageBriefcase.zNavigateTo();

		// refresh briefcase page
		// ClientSessionFactory.session().selenium().refresh();
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		app.zPageBriefcase
				.waitForCondition(
						"selenium.isElementPresent(\"css=[id='zti__main_Briefcase__16_div'][class='DwtTreeItem-selected']\")&&"
								+ "selenium.isElementPresent(\"css=[id='zl__BDLV__rows'] div[class^='Row']\");",
						"5000");

		// Click on created document
		// SleepUtil.sleepLong();

		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			app.zPageBriefcase
					.zClick("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width*='auto'] div:contains("
							+ document.getDocName() + ")");
		}

		// Click on Edit document icon in toolbar
		DocumentBriefcaseEdit documentBriefcaseEdit = (DocumentBriefcaseEdit) app.zPageBriefcase
				.zToolbarPressButton(Button.B_EDIT_FILE);

		String windowName = document.getDocName();
		app.zPageBriefcase.waitForWindow(windowName, "5000");

		// Select document window opened for editing
		// SleepUtil.sleepLong();

		try {
			app.zPageBriefcase.zSelectWindow(windowName);

			// if html body appears then document page is opened
			if (!app.zPageBriefcase.waitForElement("css=iframe[id*='DWT'][class='ZDEditor']", "50000")){
				throw new HarnessException("could not open an edit document page");
			}
		
			app.zPageBriefcase.waitForText("css=iframe[id*='DWT'][class='ZDEditor']", document.getDocText(), "5000");

			// Fill out the document with the new data
			document.setDocText("text"
					+ ZimbraSeleniumProperties.getUniqueString());

			documentBriefcaseEdit.typeDocumentText(document.getDocText());

			// Save and close
			app.zPageBriefcase.zSelectWindow(windowName);

			documentBriefcaseEdit.zSubmit();
		} catch (Exception ex) {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
			throw new HarnessException("error in editing document "
					+ windowName, ex);
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		// refresh briefcase page
		// ClientSessionFactory.session().selenium().refresh();
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		app.zPageBriefcase
				.waitForCondition(
						"selenium.isElementPresent(\"css=[id='zti__main_Briefcase__16_div'][class='DwtTreeItem-selected']\")&&"
								+ "selenium.isElementPresent(\"css=[id='zl__BDLV__rows'] div[class^='Row']\");",
						"5000");

		// Click on created document
		// SleepUtil.sleepLong();

		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			app.zPageBriefcase
					.zClick("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width*='auto'] div:contains("
							+ document.getDocName() + ")");
		}

		// Click on open in a separate window icon in toolbar
		DocumentBriefcaseOpen documentBriefcaseOpen = (DocumentBriefcaseOpen) app.zPageBriefcase
				.zToolbarPressButton(Button.B_OPEN_IN_SEPARATE_WINDOW);

		app.zPageBriefcase.waitForWindow(windowName, "5000");

		// Select document opened in a separate window
		// SleepUtil.sleepLong();

		String text = "";
		try {
			app.zPageBriefcase.zSelectWindow(windowName);

			app.zPageBriefcase.waitForElement(
					"css=td[class='ZhAppContent'] div:contains('"
							+ document.getDocText() + "')", "60000");

			if (!documentBriefcaseOpen
					.sIsVisible("css=td[class='ZhAppContent'] div[id='zdocument']")) {
				throw new HarnessException(
						"could not open a file in a separate window");
			}

			text = documentBriefcaseOpen.retriveDocumentText();

			// close
			app.zPageBriefcase.zSelectWindow(windowName);

			ClientSessionFactory.session().selenium().close();
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		ZAssert.assertStringContains(text, document.getDocText(),
				"Verify document text through GUI");
	}
}
