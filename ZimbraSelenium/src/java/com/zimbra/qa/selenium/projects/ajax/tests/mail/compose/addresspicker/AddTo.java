package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose.addresspicker;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.ExecuteHarnessMain.ResultListener;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormAddressPicker;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;


public class AddTo extends PrefGroupMailByMessageTest {

	public AddTo() {
		logger.info("New "+ AddTo.class.getCanonicalName());
		
		
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		
	}
	
	@Test(	description = "Select a 'To' address in the addresspicker",
			groups = { "functional" })
	public void AddTo_01() throws HarnessException {
		
		// The account must exist before the picker is opened
		// Log it to initialize it
		logger.info("Will add the following account to the To:" + ZimbraAccount.AccountB().EmailAddress);

		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		
		
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, "body"+ ZimbraSeleniumProperties.getUniqueString());

		// Open the addresspicker by clicking To
		
		FormAddressPicker pickerform = (FormAddressPicker)mailform.zToolbarPressButton(Button.B_TO);
		ZAssert.assertTrue(pickerform.zIsActive(), "Verify the address picker opened corectly");
		
		pickerform.zFillField(FormAddressPicker.Field.Search, ZimbraAccount.AccountB().EmailAddress);

		pickerform.zToolbarPressButton(Button.B_SEARCH);
		pickerform.zToolbarPressButton(Button.B_TO);
		
		/* TODO: MATT ... debugging to be removed */
		ResultListener.captureScreen();
		
		pickerform.zSubmit();
		
		// Addresspicker should now be closed

		// Send the message
		mailform.zSubmit();

		MailItem sent = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
		ZAssert.assertNotNull(sent, "Verify the message appears in the sent folder");
		ZAssert.assertEquals(sent.dToRecipients.get(0).dEmailAddress, ZimbraAccount.AccountB().EmailAddress, "Verify the 'to' field is correct");		
		
	}

	

}
