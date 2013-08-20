/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.displayingmessages;

import java.util.*;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.*;


public class ZimbraPrefColorMessagesEnabledTrue extends AjaxCommonTest {

	
	
	public ZimbraPrefColorMessagesEnabledTrue() {
		logger.info("New "+ ZimbraPrefColorMessagesEnabledTrue.class.getCanonicalName());
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -6497002017653159621L;
		{
				put("zimbraPrefColorMessagesEnabled", "FALSE");
			} };


	}
	
	@Test(	description = "Set 'Set color of messages and conversations according to tag color.': Enabled",
			groups = { "functional" })
	public void ZimbraPrefColorMessagesEnabledTrue_01() throws HarnessException {
		
		//-- DATA
		
		


		//-- GUI
		
		// Navigate to preferences -> mail -> displaying messages
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Mail);

		// Click checkbox: Set color of messages and conversations according to tag color
		app.zPagePreferences.zCheckboxSet("css=div.ZmPreferencesPage div[id$='COLOR_MESSAGES_control'] input[id$='COLOR_MESSAGES']", true);
		
		// Click save
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);

		
		
		//-- VERIFICATION
		
		app.zGetActiveAccount().soapSend(
						"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
				+			"<pref name='zimbraPrefColorMessagesEnabled'/>"
				+		"</GetPrefsRequest>");

		String value = app.zGetActiveAccount().soapSelectValue("//acct:pref[@name='zimbraPrefColorMessagesEnabled']", null);
		ZAssert.assertEquals(value, "TRUE", "Verify the preference was changed to TRUE");

		
	}



}
