package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.composing;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class ZimbraPrefMailRequestReadReceiptsTrue extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public ZimbraPrefMailRequestReadReceiptsTrue() {

		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{
				put("zimbraPrefMailRequestReadReceipts", "FALSE");
			}
		};
	}

	@Test(
			description = "Set zimbraPrefMailRequestReadReceipts to 'TRUE'",
			groups = { "functional" }
	)
	public void ZimbraPrefMailRequestReadReceiptsTrue_01() throws HarnessException {

		
		//-- GUI steps
		//
		
		
		// Navigate to preferences -> mail -> composing
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Mail);

		// Check the box
		app.zPagePreferences.sClick("css=input[id$='_AUTO_READ_RECEIPT_ENABLED'][type='checkbox']");

		// Click save
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);

		
		//-- VERIFICATION
		//
		
		// Verify the pref
		app.zGetActiveAccount().soapSend(
						"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
				+			"<pref name='zimbraPrefMailRequestReadReceipts'/>"
				+		"</GetPrefsRequest>");

		String value = app.zGetActiveAccount().soapSelectValue("//acct:pref[@name='zimbraPrefMailRequestReadReceipts']", null);
		ZAssert.assertEquals(value, "TRUE", "Verify the preference was changed to 'TRUE'");

	}
}
