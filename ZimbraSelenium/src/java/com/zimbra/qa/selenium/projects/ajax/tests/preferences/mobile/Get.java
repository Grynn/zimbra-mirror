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
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mobile;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class Get extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public Get() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = new HashMap<String , String>() {{
		    put("zimbraFeatureMobileSyncEnabled", "TRUE");
		}};
	}


	@Test(
			description = "View the shortcuts preference page",
			groups = { "functional", "network" }
			)
	public void Get_01() throws HarnessException {

		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MobileDevices);

		
		// Verify the page is showing
		String locator = "css=div[id$='_deviceList']";
		
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(locator), "Verify the page is present");
		ZAssert.assertTrue(app.zPagePreferences.zIsVisiblePerPosition(locator, 0, 0), "Verify the page is visible");
		
	}
}
