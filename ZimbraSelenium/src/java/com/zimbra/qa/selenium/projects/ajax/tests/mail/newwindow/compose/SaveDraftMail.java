package com.zimbra.qa.selenium.projects.ajax.tests.mail.newwindow.compose;



import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.SeparateWindowDialog;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.SeparateWindowFormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;


public class SaveDraftMail extends PrefGroupMailByMessageTest {

	public SaveDraftMail() {
		logger.info("New "+ SaveDraftMail.class.getCanonicalName());

		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		super.startingAccountPreferences.put("zimbraPrefComposeInNewWindow", "TRUE");

	}

	@Test(	description = "Save a basic draft (subject only) - in a separate window",
			groups = { "smoke" })
	public void SaveDraftMail_01() throws HarnessException {


		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();


		// Open the new mail form
		SeparateWindowFormMailNew window = null;

		try {

			window = (SeparateWindowFormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW_IN_NEW_WINDOW);

			window.zSetWindowTitle("Compose");
			window.zWaitForActive();		// Make sure the window is there

			/* TODO: ... debugging to be removed */
			window.waitForComposeWindow();
		
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");

			// Fill out the form with the data
			window.zFillField(Field.Subject, subject);

			// Send the message
			window.zToolbarPressButton(Button.B_SAVE_DRAFT);
			
			// Close the window
			window.zToolbarPressButton(Button.B_CANCEL);
			window = null;

		} finally {

			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}

		}

		FolderItem draftsFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Drafts);

		// Get the message from the server
		MailItem draft = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");

		// Verify the draft data matches
		ZAssert.assertEquals(draft.dSubject, subject, "Verify the subject field is correct");
		ZAssert.assertEquals(draft.dFolderId, draftsFolder.getId(), "Verify the draft is saved in the drafts folder");


	}
	/**
	 * Test Case: Save draft using keyboard shortcut 'Escape''
	 * 1.Compose Text mail
	 * 2.Press 'Esc' key of keyboard
	 * 3.Verify 'SaveCurrentMessageAsDraft'Warning Dialog
	 * 4.Press Yes
	 * 5.Verify Message is present in Draft
	 * @throws HarnessException
	 */

	@Test(	description = "Save draft using keyboard shortcut 'Escape'", 
			groups = { "functional" })
	public void SaveDraftMail_02() throws HarnessException {

		// Create the message data to be sent
		String body = "body" + ZimbraSeleniumProperties.getUniqueString();
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();

		// Open the new mail form
		// Open the new mail form
		SeparateWindowFormMailNew window = null;

		try {

			window = (SeparateWindowFormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW_IN_NEW_WINDOW);

			window.zSetWindowTitle("Compose");
			window.zWaitForActive();		// Make sure the window is there

			/* TODO: ... debugging to be removed */
			window.waitForComposeWindow();
		
			ZAssert.assertTrue(window.zIsActive(), "Verify the window is active");

			// Fill out the form with the data
			window.zFillField(Field.Subject, subject);
			window.zFillField(Field.Body, body);

			// Type "esc" key
			SeparateWindowDialog warning = (SeparateWindowDialog)window.zKeyboardShortcut(Shortcut.S_ESCAPE);
			warning.zWaitForActive();
			warning.zClickButton(Button.B_YES);

			// Window closes automatically
			window = null;

		} finally {

			// Make sure to close the window
			if ( window != null ) {
				window.zCloseWindow();
				window = null;
			}

		}

		FolderItem draftsFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Drafts);

		SleepUtil.sleepMedium();
		
		// Get the message from the server
		MailItem draft = MailItem.importFromSOAP(app.zGetActiveAccount(),"subject:(" + subject + ")");

		// Verify the draft data matches
		ZAssert.assertEquals(draft.dSubject, subject,"Verify the subject field is correct");
		ZAssert.assertEquals(draft.dFolderId, draftsFolder.getId(),"Verify the draft is saved in the drafts folder");

	}

}
