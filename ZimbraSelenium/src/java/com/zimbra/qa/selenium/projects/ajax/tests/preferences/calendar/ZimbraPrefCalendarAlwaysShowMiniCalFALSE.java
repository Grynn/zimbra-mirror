/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.calendar;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class ZimbraPrefCalendarAlwaysShowMiniCalFALSE extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public ZimbraPrefCalendarAlwaysShowMiniCalFALSE() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{
				put("zimbraPrefCalendarAlwaysShowMiniCal", "TRUE");
			}
		};
	}

	@Bugs(ids = "78547")
	@Test(
			description = "Set zimbraPrefCalendarAlwaysShowMiniCal to 'FALSE'",
			groups = { "functional" }
			)
	public void ZimbraPrefCalendarAlwaysShowMiniCalFALSE_01() throws HarnessException {
		
		// Navigate to preferences -> calendar
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Calendar);

		
		// Click checkbox for zimbraPrefCalendarAlwaysShowMiniCal
		logger.info("Click checkbox for zimbraPrefCalendarAlwaysShowMiniCal");
		app.zPagePreferences.zCheckboxSet("css=input[id$='_CAL_ALWAYS_SHOW_MINI_CAL']", false);
		
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
		ZAssert.assertEquals(value, "FALSE", "Verify zimbraPrefCalendarAlwaysShowMiniCal was changed to 'FALSE'");
		
	}
}
