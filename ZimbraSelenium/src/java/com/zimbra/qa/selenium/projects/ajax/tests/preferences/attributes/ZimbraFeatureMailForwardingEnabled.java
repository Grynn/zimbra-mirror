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
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.attributes;

import java.util.*;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.*;



public class ZimbraFeatureMailForwardingEnabled extends AjaxCommonTest {
	
	public ZimbraFeatureMailForwardingEnabled() {
		logger.info("New "+ ZimbraFeatureMailForwardingEnabled.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageLogin;
		super.startingAccountPreferences = new HashMap<String , String>() {
			private static final long serialVersionUID = 2672327300661475816L;
		{
		    put("zimbraFeatureMailForwardingEnabled", "FALSE");
		}};
		
	}
	
	@Test(	description = "Verify preferences does not show 'Forward a copy to', if zimbraFeatureMailForwardingEnabled=FALSE",
			groups = { "functional" })
	public void zimbraFeatureMailForwardingEnabled_01() throws HarnessException {
		
		//-- DATA		
		
		
		//-- GUI
		
		// Login
		app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());

		// Go to preferences - mail
		app.zPagePreferences.zNavigateTo();
		
		//
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Mail);
		
		


		//-- VERIFICATION
		

		// Verify the field is not present
		String locator = "css=input[id$='MAIL_FORWARDING_ADDRESS']";
		boolean exists = app.zPagePreferences.sIsElementPresent(locator);
		ZAssert.assertFalse(exists, "Verify the forwarding address field is not present");

		locator = "css=td.ZOptionsField:contains('Forward a copy to:')";	// TODO: I18N
		exists = app.zPagePreferences.sIsElementPresent(locator);
		ZAssert.assertFalse(exists, "Verify the 'Forward a copy to:' label is not present");

	}


	@Bugs(ids = "71403")
	@Test(	description = "Bug 71403: Verify duplicate message lables do not exist in preferences ('When a message arrives')",
			groups = { "functional" })
	public void zimbraFeatureMailForwardingEnabled_02() throws HarnessException {
		
		//-- DATA		
		
		
		//-- GUI
		
		// Login
		app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());

		// Go to preferences - mail
		app.zPagePreferences.zNavigateTo();
		
		//
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Mail);
		
		


		//-- VERIFICATION
		

		// Verify the field is not present
		String locator = "css=td.ZOptionsLabel:contains('Message Arrival')";	// TODO: I18N
		
		boolean exists = app.zPagePreferences.sIsElementPresent(locator);
		ZAssert.assertTrue(exists, "Verify the 'Message Arrival' label exists");
		
		int count = app.zPagePreferences.sGetCssCount(locator);
		ZAssert.assertEquals(count, 1, "Verify only 1 'Message Arrival' label exists");


	}


}
