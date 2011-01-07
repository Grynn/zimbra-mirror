package projects.ajax.tests.briefcase.file;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.thoughtworks.selenium.DefaultSelenium;
import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.briefcase.PageBriefcase.Locators;
import framework.core.ClientSessionFactory;
import framework.items.DocumentItem;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;
import framework.util.ZimbraSeleniumProperties;

public class UploadFile extends AjaxCommonTest {

	public UploadFile() {
		logger.info("New " + UploadFile.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccount = null;

	}

	@BeforeClass(groups = { "always" })
	public void UploadFileBeforeClass() throws HarnessException {
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

	@Test(description = "Upload file through SOAP - verify through SOAP", groups = { "smoke" })
	public void UploadFile_01() throws HarnessException {

		// Create document item
		DocumentItem document = new DocumentItem();
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/testtextfile.txt";
		String fileName = document.getFileName(filePath);

		// Upload file to server through SOAP
		ZimbraAccount account = app.zGetActiveAccount();
		String attachmentId = account.uploadFile(filePath);
		String briefcaseFolderId = document.GetBriefcaseIdUsingSOAP(account);

		// Save uploaded file to briefcase through SOAP
		account.soapSend(

		"<SaveDocumentRequest xmlns='urn:zimbraMail'>" +

		"<doc l='" + briefcaseFolderId + "'>" +

		"<upload id='" + attachmentId + "'/>" +

		"</doc>" +

		"</SaveDocumentRequest>");

		account.soapSelectNode("//mail:SaveDocumentResponse", 1);

		// Select Briefcase tab
		SleepUtil.sleepSmall();
		app.zPageBriefcase.zNavigateTo();

		// ClientSessionFactory.session().selenium().refresh();
		// refresh briefcase page
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		// Verify file name through SOAP

		// import from soap
		app.zGetActiveAccount().soapSend(

		"<SearchRequest xmlns='urn:zimbraMail' types='document'>" +

		"<query>" + fileName + "</query>" +

		"</SearchRequest>");

		String name = app.zGetActiveAccount().soapSelectValue("//mail:doc",
				"name");

		ZAssert.assertEquals(name, fileName, "Verify file name through SOAP");
	}

	@Test(description = "Upload file through SOAP - verify through GUI", groups = { "sanity" })
	public void UploadFile_02() throws HarnessException {

		// Create document item
		DocumentItem document = new DocumentItem();
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/structure.jpg";
		String fileName = document.getFileName(filePath);

		// Upload file to server through SOAP
		ZimbraAccount account = app.zGetActiveAccount();
		String attachmentId = account.uploadFile(filePath);
		String briefcaseFolderId = document.GetBriefcaseIdUsingSOAP(account);

		// Save uploaded file to briefcase through SOAP
		account.soapSend(

		"<SaveDocumentRequest xmlns='urn:zimbraMail'>" + "<doc l='"
				+ briefcaseFolderId + "'><upload id='" + attachmentId + "'/>" +

				"</doc>" +

				"</SaveDocumentRequest>");

		// Select Briefcase tab
		SleepUtil.sleepSmall();
		app.zPageBriefcase.zNavigateTo();

		// ClientSessionFactory.session().selenium().refresh();
		// refresh briefcase page
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		// Verify document is created
		SleepUtil.sleepLong();

		String name = "";
		if (app.zPageBriefcase.sIsElementPresent("css=[id='zl__BDLV__rows']")
				&& app.zPageBriefcase.sIsVisible("css=[id='zl__BDLV__rows']")) {
			name = app.zPageBriefcase
					.sGetText("css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div:contains("
							+ fileName + ")");
		}

		ZAssert.assertEquals(name, fileName, "Verify file name through GUI");
	}
}
