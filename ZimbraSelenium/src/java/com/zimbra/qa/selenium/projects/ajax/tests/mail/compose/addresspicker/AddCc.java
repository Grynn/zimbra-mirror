package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose.addresspicker;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.RecipientItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormAddressPicker;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;


public class AddCc extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public AddCc() {
		logger.info("New "+ AddCc.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = new HashMap<String , String>() {{
				    put("zimbraPrefComposeFormat", "text");
				}};
		
	}
	
	@Test(	description = "Select a 'To' address in the addresspicker",
			groups = { "functional" })
	public void AddTo_01() throws HarnessException {
		
		
		// Create the message data to be sent
		MailItem mail = new MailItem();
		mail.dToRecipients.add(new RecipientItem(ZimbraAccount.AccountA()));
		mail.dSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		mail.dBodyText = "body" + ZimbraSeleniumProperties.getUniqueString();
		
		
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		mailform.zFill(mail);

		// Open the addresspicker by clicking Cc
		
		FormAddressPicker pickerform = (FormAddressPicker)mailform.zToolbarPressButton(Button.B_CC);
		ZAssert.assertTrue(pickerform.zIsActive(), "Verify the address picker opened corectly");
		
		pickerform.zFillField(FormAddressPicker.Field.Search, ZimbraAccount.AccountB().EmailAddress);
		pickerform.zToolbarPressButton(Button.B_SEARCH);
		pickerform.zToolbarPressButton(Button.B_CC);
		pickerform.zSubmit();
		
		// Addresspicker should now be closed

		// Send the message
		mailform.zSubmit();

		MailItem sent = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ mail.dSubject +")");
		ZAssert.assertNotNull(sent, "Verify the message appears in the sent folder");
		ZAssert.assertEquals(sent.dToRecipients.get(0).dEmailAddress, ZimbraAccount.AccountB().EmailAddress, "Verify the 'to' field is correct");		
		
	}

	


}
