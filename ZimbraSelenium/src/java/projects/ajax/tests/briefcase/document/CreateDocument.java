package projects.ajax.tests.briefcase.document;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.thoughtworks.selenium.DefaultSelenium;
import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.briefcase.DocumentBriefcaseNew;
import framework.core.ClientSessionFactory;
import framework.items.DocumentItem;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;

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
		while (i < 20) {
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
			if (i == 20)
				logger
						.info("after "
								+ i
								+ " seconds account.soapSelectValue(//mail:doc,fr) is null");
		}

		String name = app.zGetActiveAccount().soapSelectValue("//mail:doc",
				"name");
		String text = app.zGetActiveAccount().soapSelectValue("//mail:doc",
				"fr").trim();

		ZAssert.assertEquals(document.getDocName(), name,
				" Verify document name through SOAP");
		ZAssert.assertEquals(document.getDocText(), text,
				" Verify document text through SOAP");

		/*
		*/
	}
}
