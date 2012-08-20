package com.zimbra.qa.selenium.projects.ajax.tests.preferences.general.login;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;

public class ZimbraPrefSkinSerenity extends AjaxCommonTest {

	public ZimbraPrefSkinSerenity() {
		logger.info("New "+ ZimbraPrefSkinSerenity.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPagePreferences;

		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -1303088148746653112L;
			{

				put("zimbraPrefSkin", "serenity");

			}
		};

	}
	
	@Test(
			description = "Verify the 'zimbraPrefSkin' option can be changed", 
			groups = { "functional" }
			)
	public void ZimbraPrefSkinSerenity_01() throws HarnessException {
				
		// Go to "General"
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.General);
		
		// Click on the "Theme:" pulldown
		// changing condition from true to false to let test fail in natural way rather than explicitly throw an exception
		if ( false ) {
			throw new HarnessException("see bug 75438");
		}
		
		// Click on the "Bare" theme
		String locator = "css=td[id='bare_2_title']";
	
		ZAssert.assertTrue(
				app.zPagePreferences.sIsElementPresent(locator), 
				"Verify the 'Bare' theme selection is present");
	
		app.zPagePreferences.zClick(locator);
		app.zPagePreferences.zWaitForBusyOverlay();
		
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);
		
		app.zGetActiveAccount().soapSend(
						"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
				+			"<pref name='zimbraPrefSkin'/>"
				+		"</GetPrefsRequest>");

		String value = app.zGetActiveAccount().soapSelectValue("//acct:pref[@name='zimbraPrefSkin']", null);
		ZAssert.assertEquals(value, "bare", "Verify the zimbraPrefSkin preference was saved");

	}
}