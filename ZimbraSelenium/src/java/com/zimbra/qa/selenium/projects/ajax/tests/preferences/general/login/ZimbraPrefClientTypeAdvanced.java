package com.zimbra.qa.selenium.projects.ajax.tests.preferences.general.login;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;

public class ZimbraPrefClientTypeAdvanced extends AjaxCommonTest {

	public ZimbraPrefClientTypeAdvanced() {
		logger.info("New "+ ZimbraPrefClientTypeAdvanced.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPagePreferences;

		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -1303088148746653112L;
			{

				put("zimbraPrefClientType", "advanced");

			}
		};

	}
	
	@Test(
			description = "Verify the 'Sign in using' option can be toggled", 
			groups = { "functional" }
			)
	public void ZimbraPrefClientTypeAdvanced_01() throws HarnessException {
				
		// Go to "General"
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.General);
		
		String locator = "css=input[id$='_input'][value='standard']";
	
		ZAssert.assertTrue(
				app.zPagePreferences.sIsElementPresent(locator), 
				"Verify the 'Sign in using: advanced' radio is present");
	
		app.zPagePreferences.sClick(locator);
		app.zPagePreferences.zWaitForBusyOverlay();
		
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);
		
		app.zGetActiveAccount().soapSend(
						"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
				+			"<pref name='zimbraPrefClientType'/>"
				+		"</GetPrefsRequest>");

		String value = app.zGetActiveAccount().soapSelectValue("//acct:pref[@name='zimbraPrefClientType']", null);
		ZAssert.assertEquals(value, "standard", "Verify the zimbraPrefClientType preference was changed to 'standard'");

	}
}