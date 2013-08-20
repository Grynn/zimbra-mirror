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


public class ProductHelp extends AjaxCommonTest {

	public ProductHelp() {
		logger.info("New "+ ProductHelp.class.getCanonicalName());
		
		
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;

		
	}
	
	@Test(	description = "Open 'Product Help'",
			groups = { "functional" })
	public void ProductHelp_01() throws HarnessException {
		
		//-- DATA


		//-- GUI
		
		SeparateWindow window = null;
		
		try {
			
			// Click the Account -> Product Help
			window = (SeparateWindow)app.zPageMain.zToolbarPressPulldown(Button.B_ACCOUNT, Button.O_PRODUCT_HELP);
			window.zWaitForActive(); // Make sure the window is there
			ZAssert.assertTrue(window.zIsActive(), "Verify the Product Help dialog opens");

		} finally {
			window.zCloseWindow();
			window = null;
		}

		//-- VERIFICATION
		

	}


}
