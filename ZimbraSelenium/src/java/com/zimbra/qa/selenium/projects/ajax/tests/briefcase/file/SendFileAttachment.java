package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.file;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;

public class SendFileAttachment extends AjaxCommonTest {

	public SendFileAttachment() {
		logger.info("New " + SendFileAttachment.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = null;
	}

	@Test(description = "Upload file through RestUtil - click Send as attachment, Cancel & verify through GUI", groups = { "functional" })
	public void SendFileAttachment_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem document = new DocumentItem();

		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
				+ "/data/public/other/structure.jpg";
		
		String fileName = document.getFileName(filePath);

		// Upload file to server through RestUtil
		String attachmentId = account.uploadFile(filePath);

		// Save uploaded file to briefcase through SOAP
		account.soapSend("<SaveDocumentRequest xmlns='urn:zimbraMail'>"
				+ "<doc l='" + briefcaseFolder.getId() + "'><upload id='"
				+ attachmentId + "'/></doc></SaveDocumentRequest>");
		
		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Click on created document
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, fileName);
		
		// Click on Send as attachment
		FormMailNew mailform = (FormMailNew) app.zPageBriefcase.zToolbarPressPulldown(Button.B_SEND, Button.O_SEND_AS_ATTACHMENT);
		
		// Verify the new mail form is opened
		ZAssert.assertTrue(mailform.zIsVisible(), "Verify the new form opened");

		ZAssert.assertTrue( app.zPageBriefcase.sIsElementPresent(FormMailNew.Locators.zAttachmentText + fileName + ")"), "Verify the attachment text");
		
		// Cancel the message
		// A warning dialog should appear regarding losing changes
		DialogWarning warningDlg = (DialogWarning) mailform.zToolbarPressButton(Button.B_CANCEL);
		
		ZAssert.assertNotNull(warningDlg, "Verify the dialog is returned");
		
		// Dismiss the dialog
		warningDlg.zClickButton(Button.B_NO);
		
		warningDlg.zWaitForClose(); // Make sure the dialog is dismissed
	}
}
