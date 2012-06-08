package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose.session;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;


public class SessionTimeout extends PrefGroupMailByMessageTest {

	public SessionTimeout() {
		logger.info("New "+ SessionTimeout.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		super.startingAccountPreferences.put("zimbraPrefAutoSaveDraftInterval", "90s");
		super.startingAccountPreferences.put("zimbraMailIdleSessionTimeout", "30s");

	}
	
	@Bugs(ids = "75133")
	@Test(	description = "Verify content is saved when compose is interupted by zimbraMailIdleSessionTimeout",
			groups = { "functional" })
	public void SessionTimeout_01() throws HarnessException {
		
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.To, ZimbraAccount.AccountA().EmailAddress);
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, body);


		// Wait for the session timeout
		// User will automatically be logged out
		ZimbraAccount a = app.zGetActiveAccount();
		SleepUtil.sleep(60000);
		app.zPageLogin.zLogin(a);
		
		// Confirm that the mailform is still visible
		ZAssert.assertTrue(mailform.zIsActive(), "Confirm that the mailform is still visible");
		
		
		// Send the message
		mailform.zSubmit();


		MailItem received = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ subject +")");

		// TODO: add checks for TO, Subject, Body
		ZAssert.assertEquals(received.dSubject, subject, "Verify the subject field is correct");
		ZAssert.assertStringContains(received.dBodyText, body, "Verify the body field is correct");
		
	}

	
}
