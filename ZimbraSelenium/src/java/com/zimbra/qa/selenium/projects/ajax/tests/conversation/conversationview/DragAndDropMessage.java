package com.zimbra.qa.selenium.projects.ajax.tests.conversation.conversationview;

import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;


public class DragAndDropMessage extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public DragAndDropMessage() {
		logger.info("New "+ DragAndDropMessage.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		
		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = new HashMap<String , String>() {{
				    put("zimbraPrefGroupMailBy", "conversation");
				}};
	
	}
	
	@Test(	description = "Delete a message from a conversation",
			groups = { "smoke" })
	public void DeleteConversation01() throws HarnessException {
		
		// Create a subfolder
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		String foldername = "folder"+ ZimbraSeleniumProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='" + foldername +"' l='"+ inbox.getId() +"'/>" +
					"</CreateFolderRequest>");
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String content1 = "contentA" + ZimbraSeleniumProperties.getUniqueString();
		String content2 = "contentB" + ZimbraSeleniumProperties.getUniqueString();
		
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>RE: "+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ content2 +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");

		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>RE: "+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>"+ content1 +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");

		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Select the conversation
		DisplayConversation display = (DisplayConversation)app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		// Get the list of messages
		List<DisplayConversationMessage> messages = display.zListGetMessages();
				
		// Get the locator to the message
		String id = messages.get(0).getItemId();
		
		// Drag the first message to the subfolder
		app.zPageMail.zDragAndDrop(
				"css=div#"+ id + " div[id$='__header']",
				"css=div[id='zti__main_Mail__"+ subfolder.getId() +"']"); // <div id="zti__main_Mail__67890" .../>
		
		
		//-- Server Verification
		
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>" + content1 + "</query>" 
			+	"</SearchRequest>");
		String folder1 = app.zGetActiveAccount().soapSelectValue("//mail:m", "l");
		ZAssert.assertEquals(folder1, subfolder.getId(), "Verify the first message is in the subfolder");
	
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>" + content2 + "</query>" 
			+	"</SearchRequest>");
		String folder2 = app.zGetActiveAccount().soapSelectValue("//mail:m", "l");
		ZAssert.assertEquals(folder2, inbox.getId(), "Verify the second message remains in the inbox");

		
	}

}
