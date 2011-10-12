package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.outofoffice;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;

public class ZimbraPrefOutOfOfficeReplyEnabledFalse extends AjaxCommonTest {

	public static final String Message = "message" + ZimbraSeleniumProperties.getUniqueString();
	

	public ZimbraPrefOutOfOfficeReplyEnabledFalse() throws HarnessException {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -3101848474022410670L;
			{
				put("zimbraPrefOutOfOfficeReplyEnabled", "TRUE");
				put("zimbraPrefOutOfOfficeReply", Message);
				put("zimbraPrefOutOfOfficeStatusAlertOnLogin", "TRUE");
			}
		};
		
	}

	@Test(
			description = "Disable out of office",
			groups = { "smoke" }
			)
	public void ZimbraPrefOutOfOfficeReplyEnabledTrue_01() throws HarnessException {

		/* test properties */


	
		/* GUI steps */

		// Navigate to preferences -> mail -> Out of Office
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailOutOfOffice);
		
		// Disable the preferences
		app.zPagePreferences.sClick("css=input[id$='_VACATION_MSG_ENABLED']");
		
		// Click "Save"
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);
		
		// Wait for the ModifyPrefsRequest to complete
		app.zPagePreferences.zWaitForBusyOverlay();
		
		
		/* Test verification */
		
		app.zGetActiveAccount().soapSend(
					"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
				+		"<pref name='zimbraPrefOutOfOfficeReplyEnabled'/>"
				+		"<pref name='zimbraPrefOutOfOfficeReply'/>"
				+		"<pref name='zimbraPrefOutOfOfficeStatusAlertOnLogin'/>"
				+	"</GetPrefsRequest>");

		String zimbraPrefOutOfOfficeReplyEnabled = app.zGetActiveAccount().soapSelectValue("//acct:pref[@name='zimbraPrefOutOfOfficeReplyEnabled']", null);
		String zimbraPrefOutOfOfficeReply = app.zGetActiveAccount().soapSelectValue("//acct:pref[@name='zimbraPrefOutOfOfficeReply']", null);
		String zimbraPrefOutOfOfficeStatusAlertOnLogin = app.zGetActiveAccount().soapSelectValue("//acct:pref[@name='zimbraPrefOutOfOfficeStatusAlertOnLogin']", null);
		
		// zimbraPrefOutOfOfficeReplyEnabled should be FALSE, but
		// all other properties should not be cleared (in case 
		// re-enabled in the future)
		//
		ZAssert.assertEquals(zimbraPrefOutOfOfficeReplyEnabled, "FALSE", "Verify zimbraPrefOutOfOfficeReplyEnabled is FALSE");
		ZAssert.assertEquals(zimbraPrefOutOfOfficeReply, Message, "Verify zimbraPrefOutOfOfficeReply contains the message");
		ZAssert.assertEquals(zimbraPrefOutOfOfficeStatusAlertOnLogin, "TRUE", "Verify zimbraPrefOutOfOfficeStatusAlertOnLogin is TRUE");
		
		
	}

}
