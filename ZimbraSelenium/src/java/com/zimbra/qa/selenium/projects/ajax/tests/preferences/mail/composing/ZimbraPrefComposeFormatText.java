package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.composing;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class ZimbraPrefComposeFormatText extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public ZimbraPrefComposeFormatText() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{
				put("zimbraPrefComposeFormat", "html");
			}
		};
	}


	/**
	 * Test case : ReplyAll Msg with html signature and Verify signature through soap
	 * Create signature through soap 
	 * Send message with html signature through soap
	 * ReplyAll same message
	 * Verify html signature in replied msg through soap
	 * @throws HarnessException
	 */
	@Test(
			description = "Set zimbraPrefComposeFormat to 'text'",
			groups = { "functional" }
			)
	public void ZimbraPrefComposeFormatText_01() throws HarnessException {

		// Navigate to preferences -> mail -> composing
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailComposing);

		// Click radio button for compose = html
		// See http://bugzilla.zimbra.com/show_bug.cgi?id=62322
		app.zPagePreferences.sClick("css=input[id='COMPOSE_AS_TEXT_input']");
				
		// Click save
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);
		
		app.zGetActiveAccount().soapSend(
						"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
				+			"<pref name='zimbraPrefComposeFormat'/>"
				+		"</GetPrefsRequest>");
		
		String value = app.zGetActiveAccount().soapSelectValue("//acct:pref[@name='zimbraPrefComposeFormat']", null);
		ZAssert.assertEquals(value, "text", "Verify the preference was changed to 'text'");
		
	}
}
