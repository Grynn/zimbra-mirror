package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.XmlStringUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

public class DisplayDocument extends AjaxCommonTest {

	public DisplayDocument() {
		logger.info("New " + DisplayDocument.class.getCanonicalName());

		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = null;
	}

	@Test(description = "Create document through SOAP - verify through GUI", groups = { "smoke" })
	public void DisplayDocument_01() throws HarnessException {
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

		// Verify document is created
		// String name = app.zPageBriefcase.getText(docName);
		// ZAssert.assertEquals(name, docName,
		// "Verify document name through GUI");
		boolean present = app.zPageBriefcase.isPresent(docName);

		ZAssert.assertTrue(present, "Verify document name through GUI");

		/*
		 * //name =ClientSessionFactory.session().selenium().getText(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width*='auto'] div[id^=zlif__BDLV__]"
		 * );//ClientSessionFactory.session().selenium().isElementPresent(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] td[width*='auto']>div:contains[id*='zlif__BDLV__']"
		 * );//ClientSessionFactory.session().selenium().isElementPresent(
		 * "css=div[id='zl__BDLV__rows'][class='DwtListView-Rows'] div:contains('name')"
		 * );
		 */
	}
}
