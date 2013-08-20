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

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.*;


public class ZimbraPrefMailPollingIntervalAsMailArrives extends AjaxCommonTest {

	public static final long AsMailArrives = 500;			// The value set by the client when choosing AsMailArrives
	public static final long AsMailArrivesDelay = 7000;		// Give 7 seconds to show the mail
	
	
	
	public ZimbraPrefMailPollingIntervalAsMailArrives() {
		logger.info("New "+ ZimbraPrefMailPollingIntervalAsMailArrives.class.getCanonicalName());
		
		super.startingPage = app.zPagePreferences;
//		super.startingAccountPreferences = new HashMap<String, String>() {
//			private static final long serialVersionUID = 3090644573042724593L;
//			{
//				put("zimbraPrefMailPollingInterval", "300"); // 5 minutes default
//			} };
//

	}
	
	@Test(	description = "Set 'Check new mail': As Mail Arrives",
			groups = { "functional" })
	public void ZimbraPrefMailPollingIntervalAsMailArrives_01() throws HarnessException {
		
		//-- DATA
		
		


		//-- GUI
		
		// Navigate to preferences -> mail -> displaying messages
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Mail);

		// Click pulldown for "Check New Mail:"
		// See https://bugzilla.zimbra.com/show_bug.cgi?id=81992
		// See https://bugzilla.zimbra.com/attachment.cgi?id=48826
		app.zPagePreferences.zClickAt("css=div.ZmPreferencesPage td.ZOptionsField td[id$='_select_container'] td[id$='dropdown'] div.ImgSelectPullDownArrow", "");

		// Click "As Mail Arrives"
		// See https://bugzilla.zimbra.com/attachment.cgi?id=48828
		app.zPagePreferences.zClickAt("css=div#500_1 td[id$='title']", "");

		// Click save
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);

		
		
		//-- VERIFICATION
		
		app.zGetActiveAccount().soapSend(
						"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
				+			"<pref name='zimbraPrefMailPollingInterval'/>"
				+		"</GetPrefsRequest>");

		String value = app.zGetActiveAccount().soapSelectValue("//acct:pref[@name='zimbraPrefMailPollingInterval']", null);
		ZAssert.assertEquals(value, ""+ AsMailArrives, "Verify the preference was changed to "+ AsMailArrives);

		
	}



}
