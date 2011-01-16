package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.thoughtworks.selenium.DefaultSelenium;
import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DocumentBriefcaseNew;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DocumentBriefcaseOpen;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.PageBriefcase.Locators;

public class CreateDocument extends AjaxCommonTest {

	public CreateDocument() {
		logger.info("New " + CreateDocument.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccount = null;

	}

	@BeforeClass(groups = { "always" })
	public void CreateDocumentBeforeClass() throws HarnessException {
		logger.info(this.getClass().getSimpleName() + "BeforeClass start");
		if (startingAccount == null) {
			if (app.zPageMain.zIsActive()
					&& app.zPageMain
							.sIsElementPresent("css=[onclick='ZmZimbraMail._onClickLogOff();']"))
				((DefaultSelenium) ClientSessionFactory.session().selenium())
						.click("css=[onclick='ZmZimbraMail._onClickLogOff();']");
			app.zPageLogin.zWaitForActive();
			logger.info(this.getClass().getSimpleName() + "BeforeClass finish");
		}
	}

	@Test(description = "Create document through GUI - verify through GUI", groups = { "sanity" })
	public void CreateDocument_01() throws HarnessException {

		// Create document item
		DocumentItem document = new DocumentItem();

		ZimbraAccount account = app.zGetActiveAccount();

		// Select Briefcase tab
		SleepUtil.sleepSmall();
		app.zPageBriefcase.zNavigateTo();

		// Open new document page
		DocumentBriefcaseNew documentBriefcaseNew = (DocumentBriefcaseNew) app.zPageBriefcase
				.zToolbarPressButton(Button.O_NEW_DOCUMENT);
		try {
			// Fill out the document with the data
			documentBriefcaseNew.zFill(document);

			// Save and close
			documentBriefcaseNew.zSelectWindow("Zimbra Docs");

			documentBriefcaseNew.zSubmit();
		} finally {
			documentBriefcaseNew.zSelectWindow("Zimbra: Briefcase");
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

		String windowName = document.getDocName();
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

		/*
		*/
	}
}
