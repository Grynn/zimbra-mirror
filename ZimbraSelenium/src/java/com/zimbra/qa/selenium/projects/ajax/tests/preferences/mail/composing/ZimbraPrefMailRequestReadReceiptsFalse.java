/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.composing;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class ZimbraPrefMailRequestReadReceiptsFalse extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public ZimbraPrefMailRequestReadReceiptsFalse() {

		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = new HashMap<String, String>() {
			{
				put("zimbraPrefMailRequestReadReceipts", "TRUE");
			}
		};
	}

	@Test(
			description = "Set zimbraPrefMailRequestReadReceipts to 'false'",
			groups = { "functional" }
	)
	public void ZimbraPrefMailRequestReadReceipts_01() throws HarnessException {

		
		//-- GUI steps
		//
		
		
		// Navigate to preferences -> mail -> composing
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Mail);

		// Check the box
		app.zPagePreferences.sClick("css=input[id$='_AUTO_READ_RECEIPT_ENABLED'][type='checkbox']");

		// Click save
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);

		
		//-- VERIFICATION
		//
		
		// Verify the pref
		app.zGetActiveAccount().soapSend(
						"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
				+			"<pref name='zimbraPrefMailRequestReadReceipts'/>"
				+		"</GetPrefsRequest>");

		String value = app.zGetActiveAccount().soapSelectValue("//acct:pref[@name='zimbraPrefMailRequestReadReceipts']", null);
		ZAssert.assertEquals(value, "FALSE", "Verify the preference was changed to 'FALSE'");

	}
}
