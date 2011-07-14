package com.zimbra.qa.selenium.projects.desktop.tests.briefcase.document;

import java.util.HashMap;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.briefcase.DialogConfirm;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.FormMailNew;

public class SendDocLink extends AjaxCommonTest {

	@SuppressWarnings("serial")
   public SendDocLink() {
		logger.info("New " + SendDocLink.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = new HashMap<String , String>() {{
         put("zimbraPrefComposeFormat", "html");
     }};

	}

	@Test(description = "Create document through SOAP - click Send Link, Cancel & verify through GUI", groups = { "functional" })
	public void SendDocLink_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		// Create document item
		DocumentItem docItem = new DocumentItem();

		String docName = docItem.getName();
		String docText = docItem.getDocText();

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

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageBriefcase.zWaitForDesktopLoadingSpinner(5000);

		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

		// Click on created document
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docItem);

		// Click on Send Link
		DialogConfirm confDlg = (DialogConfirm) app.zPageBriefcase
				.zToolbarPressPulldown(Button.B_SEND, Button.O_SEND_LINK);
		
		// Click Yes on confirmation dialog
		FormMailNew mailform = (FormMailNew) confDlg.zClickButton(Button.B_YES);
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageBriefcase.zWaitForDesktopLoadingSpinner(5000);

		// Verify the new mail form is opened
		ZAssert.assertTrue(mailform.zIsActive(), "Verify the new form opened");
		
		// Verify link
		ZAssert.assertTrue(mailform.zWaitForIframeText(
				FormMailNew.Locators.zLinkText, docName),
				"Verify the link text");

		// Cancel the message
		mailform.zToolbarPressButton(Button.B_CANCEL);
	}
}
