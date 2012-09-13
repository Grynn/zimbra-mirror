package com.zimbra.qa.selenium.projects.ajax.tests.conversation.bugs;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.LmtpInject;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class Bug16213 extends AjaxCommonTest {


	

	@SuppressWarnings("serial")
	public Bug16213() {
		logger.info("New "+ Bug16213.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {{
			put("zimbraPrefGroupMailBy", "conversation");
			put("zimbraPrefMessageViewHtmlPreferred", "TRUE");
		}};


	}

	@Test(	description = "Verify bug 16213 - Conversation list should show From=<blank>",
			groups = { "functional" })
	public void Bug_16213CV() throws HarnessException {

		String subject = "Encoding test";
		String to = "ljk20k00k1je";

		String MimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/private/mime/Bugs/Bug16213";
		LmtpInject.injectFile(ZimbraAccount.AccountZWC().EmailAddress, new File(MimeFolder));

		// Click on  folder in the tree
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inbox);

		SleepUtil.sleepSmall();
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		List<MailItem> items = app.zPageMail.zListGetMessages();
		MailItem found = null;
		for ( MailItem item : items ) {
			if ( item.gSubject.contains(subject) ) {
				found = item;
				break;
			}
		}
		
		ZAssert.assertNotNull(found, "Verify the message exists in the list");
		ZAssert.assertStringDoesNotContain(found.gFrom, to, "Verify the To is not contained in the From");


	}

}
