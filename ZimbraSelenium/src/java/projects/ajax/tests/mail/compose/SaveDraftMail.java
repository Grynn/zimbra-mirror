package projects.ajax.tests.mail.compose;

import org.testng.annotations.Test;

import projects.ajax.core.AjaxCommonTest;
import projects.ajax.ui.mail.FormMailNew;
import projects.ajax.ui.mail.FormMailNew.Field;
import framework.items.FolderItem;
import framework.items.MailItem;
import framework.items.FolderItem.SystemFolder;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.ZAssert;
import framework.util.ZimbraSeleniumProperties;

public class SaveDraftMail extends AjaxCommonTest {

	public SaveDraftMail() {
		logger.info("New "+ SaveDraftMail.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccount = null;
		
	}
	
	@Test(	description = "Save a basic draft (subject only)",
			groups = { "smoke" })
	public void SaveDraftMail_01() throws HarnessException {
		
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		
		// Send the message
		mailform.zToolbarPressButton(Button.B_SAVE_DRAFT);
		mailform.zToolbarPressButton(Button.B_CANCEL);
		
		// Get the message from the server
		MailItem draft = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		FolderItem draftsFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Drafts);
		
		// Verify the draft data matches
		ZAssert.assertEquals(draft.dSubject, subject, "Verify the subject field is correct");
		ZAssert.assertEquals(draft.dFolderId, draftsFolder.getId(), "Verify the draft is saved in the drafts folder");


	}

}
