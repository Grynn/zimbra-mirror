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
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.addressbook;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;

public class ZimbraPrefAutoAddAddressEnabledFalse extends AjaxCommonTest {

	public ZimbraPrefAutoAddAddressEnabledFalse() {

		super.startingPage = app.zPagePreferences;
		
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = 1275472695659221683L;
			{				
		 		put("zimbraPrefAutoAddAddressEnabled", "TRUE");
			}
		};
	}

	/**
	 * Test case : Verify select checkbox works (e.g make the option changed to opt-out)
	 * @throws HarnessException
	 */
	@Test(
			description= "Select the checkbox to set zimbraPrefAutoAddAddressEnabled=false ", 
			groups= {"functional" })
	public void ZimbraPrefAutoAddAddressEnabledFalse_01() throws HarnessException {

		//-- DATA Setup
		
		
		
		//-- GUI Actions
		
		// Navigate to preferences -> addressbook
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.AddressBook);

		// Uncheck the box
		app.zPagePreferences.zCheckboxSet("css=input[id$=_AUTO_ADD_ADDRESS]",false);
			
		// Click save
		app.zPagePreferences.zToolbarPressButton(Button.B_SAVE);		

		
		
		//-- VERIFICATION

		app.zGetActiveAccount().soapSend(
                   "<GetPrefsRequest xmlns='urn:zimbraAccount'>"
                 +     "<pref name='zimbraPrefAutoAddAddressEnabled'/>"
                 + "</GetPrefsRequest>");

		String value = app.zGetActiveAccount().soapSelectValue("//acct:pref[@name='zimbraPrefAutoAddAddressEnabled']", null);
		ZAssert.assertEquals(value, "FALSE", "Verify the zimbraPrefAutoAddAddressEnabled preference was changed to 'FALSE'");

		
	}

	
}
