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
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.general.searches;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class ZimbraPrefIncludeTrashInSearchTrue extends AjaxCommonTest {

	public ZimbraPrefIncludeTrashInSearchTrue() {
		logger.info("New "+ ZimbraPrefIncludeTrashInSearchTrue.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPagePreferences;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -4741999506556801160L;
		{
				    put("zimbraPrefIncludeTrashInSearch", "FALSE");
				}};

			
		
	}
	

	@Test(	description = "Change zimbraPrefIncludeTrashInSearch setting TRUE",
			groups = { "functional" })
	public void ZimbraPrefIncludeTrashInSearchTrue_01() throws HarnessException {

		//-- SETUP


		//-- GUI

		// Go to "General"
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.General);

		// Check the box
		String locator = "css=input[id$=_SEARCH_INCLUDES_TRASH]";
		app.zPagePreferences.zCheckboxSet(locator, true);
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);


		//-- Verification

		// Verify the account preference has been modified

		app.zGetActiveAccount().soapSend(
					"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
				+		"<pref name='zimbraPrefIncludeTrashInSearch'/>"
				+	"</GetPrefsRequest>");

		String value = app.zGetActiveAccount().soapSelectValue("//acct:pref[@name='zimbraPrefIncludeTrashInSearch']", null);
		ZAssert.assertEquals(value, "TRUE", "Verify the zimbraPrefIncludeTrashInSearch preference was changed to 'TRUE'");


	}


}
