package com.zimbra.qa.selenium.projects.ajax.tests.mail.mail;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class MarkUnSpamMessage extends AjaxCommonTest {

	
	@SuppressWarnings("serial")
	public MarkUnSpamMessage() {
		logger.info("New "+ MarkUnSpamMessage.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String , String>() {{
				    put("zimbraPrefGroupMailBy", "message");
				}};
		
	}
	
	@Test(	description = "Mark a message as not spam, using 'Not Spam' toolbar button",
			groups = { "smoke" })
	public void MarkUnSpamMessage_01() throws HarnessException {
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		// Get the junk and inbox folder
		FolderItem junk = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Junk);
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);


		// Add a message to the account's junk folder
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>" +
            		"<m l='"+ junk.getId() +"'>" +
                		"<content>From: foo@foo.com\n" +
"To: foo@foo.com \n" +
"Subject: "+ subject +"\n" +
"MIME-Version: 1.0 \n" +
"Content-Type: text/plain; charset=utf-8 \n" +
"Content-Transfer-Encoding: 7bit\n" +
"\n" +
"simple text string in the body\n" +
"</content>" +
                	"</m>" +
            	"</AddMsgRequest>");
		
		// Get the mail item for the new message
		// Need 'is:anywhere' to include the spam folder
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:("+ subject +")");
		

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Go to the Junk folder
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, junk);
		
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);
		
		// Click spam
		app.zPageMail.zToolbarPressButton(Button.B_RESPORTNOTSPAM);
		
		// Wait for the client to send the data
		SleepUtil.sleepLong();

		// Get the mail item for the new message
		// Need 'is:anywhere' to include the spam folder
		mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere subject:("+ subject +")");
		ZAssert.assertNotNull(mail, "Make sure the mail is found");

		ZAssert.assertEquals(mail.dFolderId, inbox.getId(), "Verify the message is in the inbox folder");
				
	}


}
