package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;

public class SendDocAttachment extends AjaxCommonTest {

	public SendDocAttachment() {
		logger.info("New " + SendDocAttachment.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = null;
	}

	@Test(description = "Create document through SOAP - click Send as attachment, Cancel & verify through GUI", groups = { "functional" })
	public void SendDocAttachment_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem document = new DocumentItem();

		String docName = document.getDocName();
		String docText = document.getDocText();

		// Create document using SOAP
		String contentHTML = XmlStringUtil.escapeXml("<html>" + "<body>"
				+ docText + "</body>" + "</html>");

		account
				.soapSend("<SaveDocumentRequest requestId='0' xmlns='urn:zimbraMail'>"
						+ "<doc name='"
						+ docName
						+ "' l='"
						+ briefcaseFolder.getId()
						+ "' ct='application/x-zimbra-doc'>"
						+ "<content>"
						+ contentHTML
						+ "</content>"
						+ "</doc>"
						+ "</SaveDocumentRequest>");

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Click on created document
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docName);
		
		// Click on Send as attachment
		FormMailNew mailform = (FormMailNew) app.zPageBriefcase.zToolbarPressPulldown(Button.B_SEND, Button.O_SEND_AS_ATTACHMENT);
		
		// Verify the new mail form is opened
		ZAssert.assertTrue(mailform.zIsVisible(), "Verify the new form opened");

		ZAssert.assertTrue( app.zPageBriefcase.sIsElementPresent(FormMailNew.Locators.zAttachmentText + docName + ")"), "Verify the attachment text");
		
		// Cancel the message
		// A warning dialog should appear regarding losing changes
		DialogWarning warningDlg = (DialogWarning) mailform.zToolbarPressButton(Button.B_CANCEL);
		
		ZAssert.assertNotNull(warningDlg, "Verify the dialog is returned");
		
		// Dismiss the dialog
		warningDlg.zClickButton(Button.B_NO);
		
		warningDlg.zWaitForClose(); // Make sure the dialog is dismissed
	}
}
