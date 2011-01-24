package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DocumentBriefcaseOpen;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.PageBriefcase.Locators;

public class OpenDocument extends AjaxCommonTest {

	public OpenDocument() {
		logger.info("New " + OpenDocument.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = null;
	}

	@Test(description = "Create document through SOAP - open & verify through GUI", groups = { "smoke" })
	public void OpenDocument_01() throws HarnessException {

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
					.zClick("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div:contains("
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
			documentBriefcaseOpen.zSelectWindow(windowName);

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
			documentBriefcaseOpen.zSelectWindow(windowName);

			ClientSessionFactory.session().selenium().close();
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		ZAssert.assertStringContains(text, document.getDocText(),
				"Verify document text through GUI");

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
}
