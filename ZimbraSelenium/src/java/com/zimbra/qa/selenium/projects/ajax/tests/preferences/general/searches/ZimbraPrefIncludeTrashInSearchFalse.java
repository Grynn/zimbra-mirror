/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.general.searches;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class ZimbraPrefIncludeTrashInSearchFalse extends AjaxCommonTest {

	public ZimbraPrefIncludeTrashInSearchFalse() {
		logger.info("New "+ ZimbraPrefIncludeTrashInSearchFalse.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPagePreferences;

		// Make sure we are using an account with conversation view
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -392177078709837040L;
		{
				    put("zimbraPrefIncludeTrashInSearch", "TRUE");
				}};

			
		
	}
	

	@Test(	description = "Verify zimbraPrefIncludeTrashInSearch setting when set to FALSE",
			groups = { "functional" })
	public void ZimbraPrefIncludeTrashInSearchFalse_01() throws HarnessException {

		//-- SETUP


		//-- GUI

		// Go to "General"
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.General);

		// Check the box
		String locator = "css=input[id$=_SEARCH_INCLUDES_TRASH]";
		app.zPagePreferences.zCheckboxSet(locator, false);
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);


		//-- Verification

		// Verify the account preference has been modified

		app.zGetActiveAccount().soapSend(
					"<GetPrefsRequest xmlns='urn:zimbraAccount'>"
				+		"<pref name='zimbraPrefIncludeTrashInSearch'/>"
				+	"</GetPrefsRequest>");

		String value = app.zGetActiveAccount().soapSelectValue("//acct:pref[@name='zimbraPrefIncludeTrashInSearch']", null);
		ZAssert.assertEquals(value, "FALSE", "Verify the zimbraPrefIncludeTrashInSearch preference was changed to 'FALSE'");


	}

}
