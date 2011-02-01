package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DocumentBriefcaseNew;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DocumentBriefcaseOpen;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.PageBriefcase.Locators;

public class CreateDocument extends AjaxCommonTest {

	public CreateDocument() {
		logger.info("New " + CreateDocument.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = null;
	}

	@Test(description = "Create document through GUI - verify through GUI", groups = { "sanity" })
	public void CreateDocument_01() throws HarnessException {

		// Create document item
		DocumentItem document = new DocumentItem();

		// Select Briefcase tab
		// SleepUtil.sleepSmall();
		app.zPageBriefcase.zNavigateTo();

		// Open new document page
		DocumentBriefcaseNew documentBriefcaseNew = (DocumentBriefcaseNew) app.zPageBriefcase
				.zToolbarPressButton(Button.O_NEW_DOCUMENT);

		try {
			app.zPageBriefcase.zSelectWindow("Zimbra Docs");

			// if html body appears then document page is opened
			if (!app.zPageBriefcase.waitForElement(
					"css=iframe[id*='DWT'][class='ZDEditor']", "30000")) {
				throw new HarnessException(
						"could not open an edit document page");
			}

			app.zPageBriefcase.waitForText(
					"css=iframe[id*='DWT'][class='ZDEditor']", "", "5000");

			// Fill out the document with the data
			documentBriefcaseNew.zFill(document);

			// Save and close
			app.zPageBriefcase.zSelectWindow("Zimbra Docs");

			documentBriefcaseNew.zSubmit();
		} finally {
			documentBriefcaseNew.zSelectWindow("Zimbra: Briefcase");
		}

		// Verify document name & text through GUI
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

		String name = "";
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

			name = documentBriefcaseOpen.retriveDocumentName();
			text = documentBriefcaseOpen.retriveDocumentText();

			// close
			documentBriefcaseOpen.zSelectWindow(windowName);

			app.zPageBriefcase.closeWindow();
		} finally {
			app.zPageBriefcase.zSelectWindow("Zimbra: Briefcase");
		}

		ZAssert.assertStringContains(name, document.getDocName(),
				"Verify document name through GUI");

		ZAssert.assertStringContains(text, document.getDocText(),
				"Verify document text through GUI");
	}
}
