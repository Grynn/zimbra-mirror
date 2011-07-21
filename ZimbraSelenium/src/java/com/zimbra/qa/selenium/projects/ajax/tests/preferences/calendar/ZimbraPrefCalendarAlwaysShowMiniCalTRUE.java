package com.zimbra.qa.selenium.projects.ajax.tests.preferences.calendar;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class ZimbraPrefCalendarAlwaysShowMiniCalTRUE extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public ZimbraPrefCalendarAlwaysShowMiniCalTRUE() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{
				put("zimbraPrefCalendarAlwaysShowMiniCal", "FALSE");
			}
		};
	}


	@Test(
			description = "Set zimbraPrefCalendarAlwaysShowMiniCal to 'TRUE'",
			groups = { "functional" }
			)
	public void ZimbraPrefCalendarAlwaysShowMiniCalFALSE_01() throws HarnessException {
		
		// Navigate to preferences -> calendar
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Calendar);

		
		// Click checkbox for zimbraPrefCalendarAlwaysShowMiniCal
		logger.info("Click checkbox for zimbraPrefCalendarAlwaysShowMiniCal");
		app.zPagePreferences.zCheckboxSet("css=input[id$='_CAL_ALWAYS_SHOW_MINI_CAL']", true);
		
		// Not sure why, but sleep is required here
		SleepUtil.sleepLong();

		// Click save
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);		
		

		// Verify the preference is set to false
		app.zGetActiveAccount().soapSend(
						"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
				+			"<pref name='zimbraPrefCalendarAlwaysShowMiniCal'/>"
				+		"</GetPrefsRequest>");
		
		String value = app.zGetActiveAccount().soapSelectValue("//acct:pref[@name='zimbraPrefCalendarAlwaysShowMiniCal']", null);
		ZAssert.assertEquals(value, "TRUE", "Verify zimbraPrefCalendarAlwaysShowMiniCal was changed to 'TRUE'");
		
	}
}
