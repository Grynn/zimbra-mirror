package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.composing;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class ZimbraPrefComposeFormatHtml extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public ZimbraPrefComposeFormatHtml() {

		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{
				put("zimbraPrefComposeFormat", "text");
			}
		};
	}

	@Test(
			description = "Set zimbraPrefComposeFormat to 'html'",
			groups = { "functional" }
	)
	public void ZimbraPrefComposeFormatHtml_01() throws HarnessException {

		// Navigate to preferences -> mail -> composing
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Mail);

		// Click radio button for compose = html
		// See http://bugzilla.zimbra.com/show_bug.cgi?id=62322
		app.zPagePreferences.sClick("css=input[id='COMPOSE_AS_HTML_input']");

		// Click save
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);

		app.zGetActiveAccount().soapSend(
				"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
				+			"<pref name='zimbraPrefComposeFormat'/>"
				+		"</GetPrefsRequest>");

		String value = app.zGetActiveAccount().soapSelectValue("//acct:pref[@name='zimbraPrefComposeFormat']", null);
		ZAssert.assertEquals(value, "html", "Verify the preference was changed to 'html'");

	}
}
