package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogTag;

public class TagDocument extends AjaxCommonTest {

	public TagDocument() {
		logger.info("New " + TagDocument.class.getCanonicalName());

		// All tests start at the Briefcase page
		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = null;
	}

	@Test(description = "Tag a Document using Toolbar -> Tag -> New Tag", groups = { "smoke" })
	public void TagDocument_01() throws HarnessException {
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
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docName);

		// Create a tag using GUI
		String tagName = "tag" + ZimbraSeleniumProperties.getUniqueString();

		// Click on New Tag
		DialogTag dialogTag = (DialogTag) app.zPageBriefcase
				.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_NEWTAG);

		dialogTag.zSetTagName(tagName);
		dialogTag.zClickButton(Button.B_OK);

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		// Make sure the tag was created on the server (get the tag ID)
		account.soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>");

		String tagId = account.soapSelectValue(
				"//mail:GetTagResponse//mail:tag[@name='" + tagName + "']",
				"id");

		// Verify tagged document name
		account
				.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
						+ "<query>tag:"
						+ tagName
						+ "</query>"
						+ "</SearchRequest>");

		String name = account.soapSelectValue(
				"//mail:SearchResponse//mail:doc", "name");
		
		ZAssert.assertEquals(name, docName,
				"Verify tagged document name");
		
		// Make sure the tag was applied to the document
		//account.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
		//				+ "<query>in:briefcase</query></SearchRequest>");

		//String id = account.soapSelectValue(
		//		"//mail:SearchResponse//mail:doc[@name='" + docName + "']", "t");
		
		account
		.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
				+ "<query>" + docName + "</query>" + "</SearchRequest>");
		
		String id = account.soapSelectValue(
				"//mail:SearchResponse//mail:doc", "t");
		
		ZAssert.assertEquals(id, tagId,
		"Verify the tag was attached to the document");
	}
}
