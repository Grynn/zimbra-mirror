package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose.drafts;




import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;


public class AutoSaveDraftMail extends PrefGroupMailByMessageTest {

	public static final int PrefAutoSaveDraftInterval = 10; // seconds

	public AutoSaveDraftMail() {
		logger.info("New "+ AutoSaveDraftMail.class.getCanonicalName());

		
		
		super.startingAccountPreferences.put("zimbraPrefAutoSaveDraftInterval", ""+ PrefAutoSaveDraftInterval +"s");
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");

	}

	@Bugs(ids = "66393")
	@Test(	description = "Auto save a basic draft (subject only)",
			groups = { "smoke" })
	public void AutoSaveDraftMail_01() throws HarnessException {


		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();


		FormMailNew mailform = null;

		try {

			// Open the new mail form
			mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
			ZAssert.assertNotNull(mailform, "Verify the new form opened");

			// Fill out the form with the data
			mailform.zFillField(Field.Subject, subject);
			mailform.zFillField(Field.Body, body);

			// Wait for twice the amount
			SleepUtil.sleep(PrefAutoSaveDraftInterval * 2 * 1000);

			// Get the message from the server
			FolderItem draftsFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Drafts);
			MailItem draft = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");

			// Verify the draft data matches
			ZAssert.assertEquals(draft.dSubject, subject, "Verify the subject field is correct");
			ZAssert.assertEquals(draft.dFolderId, draftsFolder.getId(), "Verify the draft is saved in the drafts folder");

		} finally {

			if ( (mailform != null) && (mailform.zIsActive()) ) {
				// Close out the draft
				mailform.zToolbarPressButton(Button.B_SAVE_DRAFT);
				mailform.zToolbarPressButton(Button.B_CANCEL);
			}
		}



	}
}
