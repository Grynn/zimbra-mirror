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
package com.zimbra.qa.selenium.projects.ajax.tests.main;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


public class AboutDialog extends AjaxCommonTest {

	public AboutDialog() {
		logger.info("New "+ AboutDialog.class.getCanonicalName());
		
		
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;

		
	}
	
	@Test(	description = "Open the 'About' dialog",
			groups = { "functional" })
	public void AboutDialog_01() throws HarnessException {
		
		//-- DATA


		//-- GUI
		
		// Click the Account -> About menu
		DialogInformational dialog = (DialogInformational)app.zPageMain.zToolbarPressPulldown(Button.B_ACCOUNT, Button.O_ABOUT);

		//-- VERIFICATION
		ZAssert.assertTrue(dialog.zIsActive(), "Verify the About dialog opens");
		
		dialog.zClickButton(Button.B_OK);

	}


}
