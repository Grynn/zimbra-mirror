package projects.ajax.tests.briefcase.file;

import org.testng.annotations.Test;
import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.PageBriefcase.Locators;
import framework.core.ClientSessionFactory;
import framework.items.DocumentItem;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.ZAssert;
import framework.util.ZimbraAccount;

public class UploadFile extends AjaxCommonTest {

	public UploadFile() {
		logger.info("New " + UploadFile.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		ZimbraAccount account = new ZimbraAccount();
		account.provision();
		account.authenticate();
		super.startingAccount = account;

	}

	@Test(description = "Upload file through SOAP - verify through SOAP", groups = { "sanity" })
	public void UploadFile_01() throws HarnessException {

		// Create document item
		DocumentItem document = new DocumentItem();
		String filePath = document.getFilePath_1();

		// Upload file to server through SOAP
		ZimbraAccount account = app.getActiveAccount();
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
		app.zPageBriefcase.navigateTo();

		// ClientSessionFactory.session().selenium().refresh();
		// refresh briefcase page
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		// Verify file name through SOAP
		String[] arr = filePath.split("/");
		String fileName = arr[arr.length - 1];

		// import from soap
		document.importFromSOAP(account, fileName);
		String name = account.soapSelectValue("//mail:doc", "name");

		ZAssert.assertEquals(name, fileName, "Verify file name through SOAP");
	}

	@Test(description = "Upload file through SOAP - verify through GUI", groups = { "sanity" })
	public void UploadFile_02() throws HarnessException {

		// Create document item
		DocumentItem document = new DocumentItem();
		String filePath = document.getFilePath_2();

		// Upload file to server through SOAP
		ZimbraAccount account = app.getActiveAccount();
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
		app.zPageBriefcase.navigateTo();

		// ClientSessionFactory.session().selenium().refresh();
		// refresh briefcase page
		app.zPageBriefcase.zClick(Locators.zBriefcaseFolderIcon);

		// Verify document is created
		SleepUtil.sleepLong();
		String[] arr = filePath.split("/");
		String fileName = arr[arr.length - 1];

		String name = "";
		if (ClientSessionFactory.session().selenium().isElementPresent(
				"css=[id='zl__BDLV__rows']")
				&& ClientSessionFactory.session().selenium().isVisible(
						"css=[id='zl__BDLV__rows']")) {
			name = ClientSessionFactory
					.session()
					.selenium()
					.getText(
							"css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width='auto'] div:contains("
									+ fileName + ")");
		}

		ZAssert.assertEquals(name, fileName, "Verify file name through GUI");
	}
}
