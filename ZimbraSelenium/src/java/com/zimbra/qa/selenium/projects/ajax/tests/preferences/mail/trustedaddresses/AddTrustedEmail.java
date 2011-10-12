package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.trustedaddresses;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;

public class AddTrustedEmail extends AjaxCommonTest {


	public AddTrustedEmail() throws HarnessException {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
		
	}

	@Test(
			description = "Add a trusted email address",
			groups = { "smoke" }
			)
	public void AddTrustedEmail_01() throws HarnessException {

		/* test properties */
		final String email = "email" + ZimbraSeleniumProperties.getUniqueString() + "@zimbra.com";

	
		/* GUI steps */

		// Navigate to preferences -> mail -> Trusted Addresses
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailTrustedAddresses);
		
		// Add the email address
		app.zPagePreferences.sType("css=td[id$='_EMAIL_ADDRESS'] input", email);
		
		// Click "Add"
		app.zPagePreferences.zClick("css=td[id$='_ADD_BUTTON'] td[id$='_title']");
		
		// Click "Save"
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);
		
		// Wait for the ModifyPrefsRequest to complete
		app.zPagePreferences.zWaitForBusyOverlay();
		
		
		/* Test verification */
		
		app.zGetActiveAccount().soapSend(
					"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
				+		"<pref name='zimbraPrefMailTrustedSenderList'/>"
				+	"</GetPrefsRequest>");

		String found = null;
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//acct:pref[@name='zimbraPrefMailTrustedSenderList']");
		for (Element e : nodes) {
			if ( e.getText().contains(email) ) {
				found = e.getText();
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify that the address is saved in the server prefs");
		
	}

}
