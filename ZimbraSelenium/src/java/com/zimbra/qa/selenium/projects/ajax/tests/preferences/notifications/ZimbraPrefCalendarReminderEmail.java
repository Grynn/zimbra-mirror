/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.notifications;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class ZimbraPrefCalendarReminderEmail extends AjaxCommonTest {

	public ZimbraPrefCalendarReminderEmail() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
	}


	@Test(
			description = "Set zimbraPrefCalendarReminderEmail to a valid Email address'",
			groups = { "functional" }
			)
	public void ZimbraPrefCalendarReminderEmail_01() throws HarnessException {
		
		ZimbraAccount destination = new ZimbraAccount();
		destination.provision().authenticate();
		
		
		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Notifications);

		
		// Set the address
		String locator = "css=div[id='ZmNotificationsPage'] input[id='ZmNotificationsPage_EMAIL_input']";
		
		// To activate the Search button, need to focus/click
		app.zPagePreferences.sFocus(locator);
		app.zPagePreferences.zClick(locator);
		app.zPagePreferences.sType(locator, destination.EmailAddress);

		// Click save
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);		
		

		// Verify the preference is set to false
		app.zGetActiveAccount().soapSend(
						"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
				+			"<pref name='zimbraPrefCalendarReminderEmail'/>"
				+		"</GetPrefsRequest>");
		
		String value = app.zGetActiveAccount().soapSelectValue("//acct:pref[@name='zimbraPrefCalendarReminderEmail']", null);
		ZAssert.assertEquals(value, destination.EmailAddress, "Verify zimbraPrefCalendarReminderEmail was changed to "+ destination.EmailAddress);
		
	}
}
