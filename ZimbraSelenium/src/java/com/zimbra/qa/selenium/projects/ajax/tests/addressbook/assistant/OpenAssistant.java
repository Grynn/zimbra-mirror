package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.assistant;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogAssistant;



public class OpenAssistant extends AjaxCommonTest {
	
	public OpenAssistant() {
		logger.info("New "+ OpenAssistant.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageAddressbook;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = null;


	}
	
	@Test(	description = "Open the assistant",
			groups = { "deprecated" })
	public void OpenAssistant_01() throws HarnessException {
		
		DialogAssistant assistant = (DialogAssistant)app.zPageAddressbook.zKeyboardShortcut(Shortcut.S_ASSISTANT);
		assistant.zClickButton(Button.B_CANCEL);
		
	}


}
