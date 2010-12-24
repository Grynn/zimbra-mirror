package projects.ajax.tests.briefcase.document;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.briefcase.DocumentBriefcaseNew;
import framework.items.DocumentItem;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.ZAssert;

public class CreateDocument extends AjaxCommonTest {

	public CreateDocument() {
		logger.info("New " + CreateDocument.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccount = null;

	}

	@Test(description = "Create document through GUI - verify through SOAP", groups = { "sanity" })
	public void CreateDocument_01() throws HarnessException {

		// Create document item
		DocumentItem document = new DocumentItem();

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
		app.zGetActiveAccount().soapSend(

		"<SearchRequest xmlns='urn:zimbraMail' types='document'>" +

		"<query>" + document.getDocName() + "</query>" +

		"</SearchRequest>");

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
