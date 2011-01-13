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

	@Test(description = "Create document through GUI - verify through SOAP", groups = { "sanity" })
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

		// Verify document name & text through SOAP
		int i = 0;
		int y = 20;

		while (i < y) {
			SleepUtil.sleepSmall();
			account.soapSend(

			"<SearchRequest xmlns='urn:zimbraMail' types='document'>" +

			"<query>" + document.getDocName() + "</query>" +

			"</SearchRequest>");

			if (account.soapSelectValue("//mail:doc", "fr") != null) {
				logger
						.info(i
								+ "sec account.soapSelectValue(//mail:doc,fr) succeeded");
				break;
			}
			i++;
		}

		if (i == y)
			logger.info(i
					+ "sec account.soapSelectValue(//mail:doc,fr) is null");

		String name = account.soapSelectValue("//mail:doc", "name");
		String text = account.soapSelectValue("//mail:doc", "fr");
		if (text != null)
			text = account.soapSelectValue("//mail:doc", "fr").trim();

		ZAssert.assertEquals(document.getDocName(), name,
				" Verify document name through SOAP");
		ZAssert.assertEquals(text, document.getDocText(),
				" Verify document text through SOAP");

		/*
		*/
	}
}
