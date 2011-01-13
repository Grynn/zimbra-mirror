package com.zimbra.qa.selenium.projects.ajax.tests.mail.mail;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class MarkUnReadMail extends AjaxCommonTest {

	public int delaySeconds = 5;
	
	public MarkUnReadMail() {
		logger.info("New "+ MarkUnReadMail.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccount = new ZimbraAccount();
		super.startingAccount.provision();
		super.startingAccount.authenticate();
		super.startingAccount.modifyPreference("zimbraPrefGroupMailBy", "message");
		super.startingAccount.modifyPreference("zimbraPrefMarkMsgRead", ""+ delaySeconds);

	}
	
	@Test(	description = "Mark a message as unread by clicking on it, then using 'mu' hotkeys",
			groups = { "smoke" })
	public void MarkUnReadMail_01() throws HarnessException {
		

		// Create the message data to be sent
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		FolderItem inboxFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		app.zGetActiveAccount().soapSend(
					"<AddMsgRequest xmlns='urn:zimbraMail'>" +
                		"<m l='"+ inboxFolder.getId() +"' f=''>" +
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
		
		// Create a mail item to represent the message
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		ZAssert.assertStringDoesNotContain(mail.getFlags(), "u", "Verify message is initially unread");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
				
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, mail.dSubject);
		
		// TODO: need to L10N this
		app.zPageMail.zKeyboardShortcut(Shortcut.S_MAIL_MARKUNREAD);

		
		// Wait for the client to send the information to the server
		SleepUtil.sleepMedium();

		// Verify the message is marked read in the server (flags attribute should not contain (u)nread)
		mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		ZAssert.assertStringContains(mail.getFlags(), "u", "Verify the message is marked read in the server");
		
		// TODO: Verify the message is not marked unread in the list

		
	}

	@Test(	description = "Mark a message as read by context menu -> mark unread",
			groups = { "functional" })
	public void MarkUnReadMail_02() throws HarnessException {
		throw new HarnessException("implement me");
	}
		



}
