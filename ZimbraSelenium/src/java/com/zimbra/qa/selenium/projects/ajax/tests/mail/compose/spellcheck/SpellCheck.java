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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose.spellcheck;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;

public class SpellCheck extends PrefGroupMailByMessageTest {

	public SpellCheck() {
		logger.info("New "+ SpellCheck.class.getCanonicalName());

		
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");

	}

	@Test(	description = "Spell Check a single word",
			groups = { "functional" })
			public void SpellCheck_01() throws HarnessException {

		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();

		// Open the new mail form
		FormMailNew mailform = null;

		try {
			mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
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

			// View the suggestions
			mailform.sClick("css=span[class='ZM-SPELLCHECK-MISSPELLED']:contains(mispeled)");
			mailform.zWaitForBusyOverlay();
			
			// Look at the list to make sure the correctly spelled word is there
			ZAssert.assertTrue(
					mailform.sIsElementPresent("css=tr[id*='_sug-'] td[id$='_title']:contains(misspelled)"),
			"Verify the misspelled word is highlighted");

		} finally {
			if ( mailform != null ) {
				mailform.zToolbarPressButton(Button.B_SEND);
				mailform = null;
			}
		}


	}

	@Test(	description = "Spell Check multiple words",
			groups = { "functional" })
			public void SpellCheck_02() throws HarnessException {

		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();

		// Open the new mail form
		FormMailNew mailform = null;

		try {

			mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
			ZAssert.assertNotNull(mailform, "Verify the new form opened");

			// Fill out the form with the data
			mailform.zFillField(Field.To, ZimbraAccount.AccountA().EmailAddress);
			mailform.zFillField(Field.Subject, subject);
			mailform.zFillField(Field.Body, "write mispeled incorect words here");

			// Send the message
			mailform.zToolbarPressButton(Button.B_SPELL_CHECK);

			// Verify the misspelled word is highlighted
			ZAssert.assertTrue(
					mailform.sIsElementPresent("css=span[class='ZM-SPELLCHECK-MISSPELLED']:contains('mispeled')"),
			"Verify the misspelled word is highlighted");
			ZAssert.assertTrue(
					mailform.sIsElementPresent("css=span[class='ZM-SPELLCHECK-MISSPELLED']:contains('incorect')"),
			"Verify the misspelled word is highlighted");

			// View the suggestions
			mailform.sClick("css=span[class='ZM-SPELLCHECK-MISSPELLED']:contains(mispeled)");
			mailform.zWaitForBusyOverlay();
			mailform.sClick("css=span[class='ZM-SPELLCHECK-MISSPELLED']:contains(incorect)");
			mailform.zWaitForBusyOverlay();

			// Look at the list to make sure the correctly spelled word is there
			ZAssert.assertTrue(
					mailform.sIsElementPresent("css=tr[id*='_sug-'] td[id$='_title']:contains('misspelled')"),
			"Verify the misspelled word is highlighted");
			ZAssert.assertTrue(
					mailform.sIsElementPresent("css=tr[id*='_sug-'] td[id$='_title']:contains('incorrect')"),
			"Verify the misspelled word is highlighted");

		} finally {

			if ( mailform != null ) {
				mailform.zToolbarPressButton(Button.B_SEND);
				mailform = null;
			}

		}
	}

	


}
