/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.admin.tests.topmenu.logo;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;


public class BasicLogo extends AdminCommonTest {
	
	public BasicLogo() {
		logger.info("New "+ BasicLogo.class.getCanonicalName());
	}
	
	@Test(	description = "Verify the Top Menu displays the Logo image correctly",
			groups = { "skip" })
	public void TopMenu_BasicLogo_01() throws HarnessException {
		throw new HarnessException("Implement me!");
	}


}
