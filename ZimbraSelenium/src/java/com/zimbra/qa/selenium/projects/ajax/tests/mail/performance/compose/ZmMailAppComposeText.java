/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
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
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.mail.performance.compose;

import java.util.HashMap;

import org.testng.annotations.Test;


import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.performance.PerfKey;
import com.zimbra.qa.selenium.framework.util.performance.PerfMetrics;
import com.zimbra.qa.selenium.framework.util.performance.PerfToken;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;

public class ZmMailAppComposeText extends AjaxCommonTest {

	public ZmMailAppComposeText() {
		logger.info("New " + ZmMailAppComposeText.class.getCanonicalName());

		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = 7525760124523255182L;
			{
				put("zimbraPrefComposeFormat", "text");
			}
		};

	}

	@Test(description = "Measure the time to load the text compose  window", groups = { "performance" })
	public void ZmMailAppComposeText_01() throws HarnessException {

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmMailAppCompose,"Load the compose window in text view");

		// Click the new button
		//app.zPageMail.zToolbarPressButton(Button.B_NEW);
		app.zPageMail.zClickAt("css=div[id$='__NEW_MENU'] td[id$='__NEW_MENU_title']","");

		PerfMetrics.waitTimestamp(token);

	}
}
