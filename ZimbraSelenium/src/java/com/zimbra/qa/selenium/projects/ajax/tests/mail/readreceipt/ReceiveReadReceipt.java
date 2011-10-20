package com.zimbra.qa.selenium.projects.ajax.tests.mail.readreceipt;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail.Field;


public class ReceiveReadReceipt extends PrefGroupMailByMessageTest {

	public ReceiveReadReceipt() {
		logger.info("New "+ ReceiveReadReceipt.class.getCanonicalName());
		
		
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		
	}
	
	@Test(	description = "Receive/view a read receipt",
			groups = { "functional" })
	public void CreateMailText_01() throws HarnessException {
		
		// Data setup
		
		// Send a message requesting a read receipt
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" 
			+			"<m>"
			+				"<e t='t' a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>"
			+				"<e t='f' a='"+ app.zGetActiveAccount().EmailAddress +"'/>"
			+				"<e t='n' a='"+ app.zGetActiveAccount().EmailAddress +"'/>"
			+				"<su>"+ subject +"</su>"
			+				"<mp ct='text/plain'>"
			+					"<content>content" + ZimbraSeleniumProperties.getUniqueString() +"</content>" 
			+				"</mp>"
			+			"</m>" 
			+		"</SendMsgRequest>");

		
		MailItem received = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:("+ subject +")");
		
		// Send the read receipt
		ZimbraAccount.AccountA().soapSend("<SendDeliveryReportRequest xmlns='urn:zimbraMail' mid='"+ received.getId() +"'/>");
		
		
		
		
		// GUI verification
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail actual = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		ZAssert.assertEquals(			actual.zGetMailProperty(Field.To),		app.zGetActiveAccount().EmailAddress, "Verify the message is to the test account");
		ZAssert.assertEquals(			actual.zGetMailProperty(Field.From),	ZimbraAccount.AccountA().EmailAddress, "Verify the message is from the destination");
		ZAssert.assertStringContains(	actual.zGetMailProperty(Field.Subject), "Read-Receipt", "Verify the message subject contains the correct value");	// TODO: I18N
		ZAssert.assertStringContains(	actual.zGetMailProperty(Field.Body), 	"The message sent on", "Verify the message subject contains the correct value");	// TODO: I18N
		ZAssert.assertStringContains(	actual.zGetMailProperty(Field.Body), 	subject, "Verify the message subject contains the correct value");	// TODO: I18N
		
		
	}

	


}
