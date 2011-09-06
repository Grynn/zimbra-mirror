package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose.spellcheck;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;

public class SpellCheckHtml extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public SpellCheckHtml() {
		logger.info("New "+ SpellCheckHtml.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = new HashMap<String , String>() {{
				    put("zimbraPrefComposeFormat", "html");
				}};
		
	}
	
	@Test(	description = "Spell Check an HTML message",
			groups = { "functional" })
	public void SpellCheckHtml_01() throws HarnessException {
		
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.To, ZimbraAccount.AccountA().EmailAddress);
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, "write mispeled words here");
		
		// Send the message
		mailform.zToolbarPressButton(Button.B_SPELL_CHECK);
			
		// Verify the misspelled word is highlighted
		ZAssert.assertTrue(
				mailform.sIsElementPresent("css=span[class='ZM-SPELLCHECK-MISSPELLED']:contains(mispeled)"),
				"Verify the misspelled word is highlighted");

		// Verify the misspelled word is highlighted
		ZAssert.assertFalse(
				mailform.sIsElementPresent("css=span[class='ZM-SPELLCHECK-MISSPELLED']:contains(words)"),
				"Verify the correctly spelled words are not highlighted");

		mailform.zToolbarPressButton(Button.B_SEND);
		
	}

}
