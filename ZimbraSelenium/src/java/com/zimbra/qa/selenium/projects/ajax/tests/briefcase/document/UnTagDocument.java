package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.document;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogTag;

public class UnTagDocument extends AjaxCommonTest {

	public UnTagDocument() {
		logger.info("New " + UnTagDocument.class.getCanonicalName());

		// All tests start at the Briefcase page
		super.startingPage = app.zPageBriefcase;

		super.startingAccountPreferences = null;
	}

	@Test(description = "Remove a tag from a Document using Toolbar -> Tag -> Remove Tag", groups = { "smoke" })
	public void UnTagDocument_01() throws HarnessException {
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
		
		/*
		 * String docId =
		 * account.soapSelectValue("//mail:SaveDocumentResponse//mail:doc"
		 * ,"id");
		 * 
		 * // Search for created documentaccount.soapSend(
		 * "<SearchRequest xmlns='urn:zimbraMail' types='document'>" +
		 * "<query>in:" + briefcaseFolder.getName() +
		 * "</query></SearchRequest>");
		 * 
		 * String docId = account.soapSelectValue(
		 * "//mail:SearchResponse//mail:doc[@name='" + docName + "']", "id");
		 * String version = account.soapSelectValue(
		 * "//mail:SearchResponse//mail:doc[@name='" + docName + "']", "id");
		 * 
		 * account.soapSend(
		 * "<SearchRequest xmlns='urn:zimbraMail' types='document'>" + "<query>"
		 * + docName + "</query>" + "</SearchRequest>");
		 * 
		 * docId = account.soapSelectValue("//mail:doc", "id"); version =
		 * account.soapSelectValue("//mail:doc", "ver");
		 */
		
		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);
		
	    // Click on created document
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docName);

      // Create a tag 
		String tagName = "tag"+ ZimbraSeleniumProperties.getUniqueString();
		
		/*
		//this flow needs page reload
		account.soapSend(
				"<CreateTagRequest xmlns='urn:zimbraMail'>" +
                	"<tag name='"+ tagName +"' color='1' />" +
                "</CreateTagRequest>");
		
		String tagId = account.soapSelectValue("//mail:CreateTagResponse/mail:tag", "id");

		account.soapSend(
				"<ItemActionRequest xmlns='urn:zimbraMail'>" +
                	"<action id='"+ docId +"' op='tag' tag='" + tagId + "'/>" +
                "</ItemActionRequest>");
				
		//ClientSessionFactory.session().selenium().refresh();
		*/

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

		// Make sure the tag was applied to the document
		account
		.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
				+ "<query>" + docName + "</query>" + "</SearchRequest>");
		
		String id = account.soapSelectValue("//mail:SearchResponse//mail:doc", "t");
		
		ZAssert.assertEquals(id, tagId,"Verify the tag was attached to the document");
		
		// refresh briefcase page
		app.zTreeBriefcase.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, true);

	    // Click on tagged document
      app.zPageBriefcase.zListItem(Action.A_LEFTCLICK, docName);
      
      // Click Remove Tag
      app.zPageBriefcase.zToolbarPressPulldown(Button.B_TAG, Button.O_TAG_REMOVETAG);

      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

      account
		.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
				+ "<query>" + docName + "</query>" + "</SearchRequest>");
		
		id = account.soapSelectValue("//mail:SearchResponse//mail:doc", "t");
		
		ZAssert.assertStringDoesNotContain(id,tagId, "Verify that the tag is removed from the message");		
	}
}
