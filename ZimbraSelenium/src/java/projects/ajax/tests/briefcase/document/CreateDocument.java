package projects.ajax.tests.briefcase.document;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.Buttons;
import projects.ajax.ui.DocumentBriefcaseNew;
import projects.ajax.ui.PageBriefcase.Locators;
import framework.core.ClientSessionFactory;
import framework.items.DocumentItem;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;

public class CreateDocument extends AjaxCommonTest {

	public CreateDocument() {
		logger.info("New " + CreateDocument.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		ZimbraAccount account = new ZimbraAccount();
		account.provision();
		account.authenticate();
		super.startingAccount = account;

	}

	@Test(description = "Create document through GUI - verify through SOAP", groups = { "sanity" })
	public void CreateDocument_01() throws HarnessException {

		// Create document item
		DocumentItem document = new DocumentItem();

		// Select Briefcase tab
		SleepUtil.sleepSmall();
		app.zPageBriefcase.navigateTo();

		// Open new document page
		DocumentBriefcaseNew documentBriefcaseNew = (DocumentBriefcaseNew) app.zPageBriefcase
				.zToolbarPressButton(Buttons.O_NEW_DOCUMENT);

		SleepUtil.sleepVeryLong();
		try {
			String newPageTitle = "Zimbra Docs";
			documentBriefcaseNew.zSelectWindow(newPageTitle);

			// ClientSessionFactory.session().selenium().waitForCondition("selenium.browserbot.getUserWindow()","10000");
			// ClientSessionFactory.session().selenium().getEval("selenium.browserbot.getCurrentWindow()");
			// ClientSessionFactory.session().selenium().getEval("selenium.browserbot.getUserWindow()");

			// if name field appears in the toolbar then document page is opened
			if (!documentBriefcaseNew
					.sIsElementPresent("//*[@id='DWT3_item_1']")) {
				throw new HarnessException("could not open a new page");
			} else {
				DocumentBriefcaseNew.pageTitle = newPageTitle;
			}

			// Fill out the document with the data
			documentBriefcaseNew.fill(document);

			// Save and close
			documentBriefcaseNew.submit();
		} finally {
			documentBriefcaseNew.zSelectWindow("Zimbra: Briefcase");
		}
		ZimbraAccount account = app.getActiveAccount();

		// Verify document name through SOAP
		app.getActiveAccount().soapSend(

		"<SearchRequest xmlns='urn:zimbraMail' types='document'>" +

		"<query>" + document.getDocName() + "</query>" +

		"</SearchRequest>");

		String name = app.getActiveAccount().soapSelectValue("//mail:doc",
				"name");

		ZAssert.assertEquals(document.getDocName(), name,
				" Verify document name through SOAP");	
		/*
		*/
	}
}
