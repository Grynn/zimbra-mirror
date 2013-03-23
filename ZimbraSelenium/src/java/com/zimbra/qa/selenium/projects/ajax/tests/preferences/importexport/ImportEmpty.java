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
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.importexport;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class ImportEmpty extends AjaxCommonTest {

	public ImportEmpty() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
	}


	@Test(
			description = "Click import without specifying a file",
			groups = { "functional" }
			)
	public void ImportEmpty_01() throws HarnessException {

		
		/**
		 * 
		 * TODO: Since selenium doesn't handle the
		 * 'browse' system dialog, just execute
		 * as much as possible - click import
		 * without specifying a file.
		 * 
		 */
		
		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.ImportExport);

		// TODO: See https://bugzilla.zimbra.com/show_bug.cgi?id=63289
		app.zPagePreferences.zClick("css=div[id='IMPORT_BUTTON'] td[id$='_title']");
		
		// Should probably verify toaster that says "Must Specify Import File"
		
	}
}
