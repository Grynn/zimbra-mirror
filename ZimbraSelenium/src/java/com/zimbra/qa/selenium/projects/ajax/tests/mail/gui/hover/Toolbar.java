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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.gui.hover;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;


public class Toolbar extends PrefGroupMailByMessageTest {

	
	public Toolbar() {
		logger.info("New "+ Toolbar.class.getCanonicalName());
		
	}
	
	@Test(	description = "Hover over Delete button",
			groups = { "deprecated" })		// Toolbar tooltips are now handled in the browser, not the DOM
	public void Toolbar_01() throws HarnessException {
		
		AbsTooltip tooltip = app.zPageMail.zHoverOver(Button.B_DELETE);
		
		String contents = tooltip.zGetContents();
		
		ZAssert.assertStringContains(contents, "Trash", "Verify the tool tip text"); // TODO: I18N
		
	}

	@Test(	description = "Hover over Reply button",
			groups = { "deprecated" })		// Toolbar tooltips are now handled in the browser, not the DOM
	public void Toolbar_02() throws HarnessException {
		
		AbsTooltip tooltip = app.zPageMail.zHoverOver(Button.B_REPLY);
		
		String contents = tooltip.zGetContents();
		
		ZAssert.assertStringContains(contents, "Reply", "Verify the tool tip text"); // TODO: I18N

		
	}


}
