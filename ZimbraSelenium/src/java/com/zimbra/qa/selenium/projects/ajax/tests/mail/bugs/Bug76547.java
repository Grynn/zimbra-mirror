package com.zimbra.qa.selenium.projects.ajax.tests.mail.bugs;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;


public class Bug76547 extends PrefGroupMailByMessageTest {

	public Bug76547() {
		logger.info("New "+ Bug76547.class.getCanonicalName());
		
		
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		super.startingAccountPreferences.put("zimbraPrefUseKeyboardShortcuts", "FALSE");
		
	}
	
	@Bugs(ids = "76547,80740")
	@Test(	description = "Send a mail using Text editor - zimbraPrefUseKeyboardShortcuts = FALSE",
			groups = { "functional" })
	public void Bug76547_01() throws HarnessException {
		
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(FormMailNew.Field.Subject, subject);
		mailform.zFillField(FormMailNew.Field.Body, "body" + ZimbraSeleniumProperties.getUniqueString());

		// Selenium JS can't repro the bug.  I suppose the To: field is still enabled
		// at the JS level.  But, clicking manually into the field doesn't work.
		//
		// Maybe WebDriver will be better at repro, since it mimics real usage better.
		//
		mailform.sFocus(FormMailNew.Locators.zToField);
		mailform.zClick(FormMailNew.Locators.zToField);
		mailform.zWaitForBusyOverlay();
		mailform.zKeyboard.zTypeCharacters(ZimbraAccount.AccountA().EmailAddress);
		
		// Send the message
		mailform.zSubmit();

		MailItem received = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ subject +")");
		ZAssert.assertNotNull(received, "Verify the message is received by Account A");
		
		
	}



}
