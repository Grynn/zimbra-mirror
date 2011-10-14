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
			groups = { "functional" })
	public void Toolbar_01() throws HarnessException {
		
		AbsTooltip tooltip = app.zPageMail.zHoverOver(Button.B_DELETE);
		
		String contents = tooltip.zGetContents();
		
		ZAssert.assertStringContains(contents, "Trash", "Verify the tool tip text"); // TODO: I18N
		
	}

	@Test(	description = "Hover over Reply button",
			groups = { "functional" })
	public void Toolbar_02() throws HarnessException {
		
		AbsTooltip tooltip = app.zPageMail.zHoverOver(Button.B_REPLY);
		
		String contents = tooltip.zGetContents();
		
		ZAssert.assertStringContains(contents, "Reply", "Verify the tool tip text"); // TODO: I18N

		
	}


}
