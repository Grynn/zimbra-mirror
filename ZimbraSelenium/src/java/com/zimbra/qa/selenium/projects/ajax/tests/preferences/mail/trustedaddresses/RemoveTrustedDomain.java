package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.trustedaddresses;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;




public class RemoveTrustedDomain extends AjaxCommonTest {

	// For RemoveTrustedDomain_01
	public String domain1 = "@domain"+ ZimbraSeleniumProperties.getUniqueString() + ".com";
	
	
	
	public RemoveTrustedDomain() throws HarnessException {
		
		super.startingPage = app.zPagePreferences;
		
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -1475986145425100378L;
			{
				put("zimbraPrefMailTrustedSenderList", domain1);
			}
		};
		
	}

	
	@Test(
			description = "Remove a trusted domain",
			groups = { "smoke" }
			)
	public void RemoveTrustedDomain_01() throws HarnessException {

		/* test properties */

		
		/* GUI steps */

		// Navigate to preferences -> mail -> Trusted Addresses
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailTrustedAddresses);
		
		// Select the email address
		String locator = "css=td[id$='_LISTVIEW'] td:contains("+ domain1 +")";
		app.zPagePreferences.zClick(locator);
		
		// Click "Remove"
		app.zPagePreferences.zClick("css=td[id$='_REMOVE_BUTTON'] td[id$='_title']");
		
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
			if ( e.getText().contains(domain1) ) {
				found = e.getText();
				break;
			}
		}
		ZAssert.assertNull(found, "Verify that the domain is no longer included in the server prefs");
		
	}

}
