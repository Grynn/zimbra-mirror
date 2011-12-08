package com.zimbra.qa.selenium.projects.desktop.tests.mail.bugs;

import java.io.File;
import java.util.*;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ConversationItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DisplayMail;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DisplayMail.Field;


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
			put("zimbraPrefReadingPaneLocation", "bottom");
		}};


	}

	@Test(	description = "Verify bug 16213 - Conversation list should show From=<blank>",
			groups = { "functional" })
	public void Bug_16213A() throws HarnessException {

		String subject = "Encoding test";
		String to = "ljk20k00k1je";

		String MimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/private/mime/Bugs/Bug16213";
		LmtpInject.injectFile(ZimbraAccount.AccountZDC().EmailAddress, new File(MimeFolder));

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		List<ConversationItem> items = app.zPageMail.zListGetConversations();
		ConversationItem found = null;
		for ( ConversationItem item : items ) {
			if ( item.gSubject.contains(subject) ) {
				found = item;
				break;
			}
		}
		
		ZAssert.assertNotNull(found, "Verify the message exists in the list");
		ZAssert.assertStringDoesNotContain(found.gFrom, to, "Verify the To is not contained in the From");


	}

	@Test(	description = "Verify bug 16213 - Message display should show From=Unknown",
			groups = { "functional" })
	public void Bug_16213B() throws HarnessException {

		String subject = "Encoding test";
		String from = "Unknown";

		String MimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/private/mime/Bugs/Bug16213";
		LmtpInject.injectFile(ZimbraAccount.AccountZDC().EmailAddress, new File(MimeFolder));

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		DisplayMail display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		
		ZAssert.assertEquals(display.zGetMailProperty(Field.From), from, "Verify the default string for 'From' is 'Unknown'");

	}


}
