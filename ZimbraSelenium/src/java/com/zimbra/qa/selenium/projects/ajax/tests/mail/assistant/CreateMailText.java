package com.zimbra.qa.selenium.projects.ajax.tests.mail.assistant;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogAssistant;


public class CreateMailText extends PrefGroupMailByMessageTest {
	
	public CreateMailText() {
		logger.info("New "+ CreateMailText.class.getCanonicalName());
		
		
		

	}
	
	@Test(	description = "Send a text mail using the Zimbra Assistant",
			groups = { "deprecated" })
	public void CreateMailText_01() throws HarnessException {
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String command = "mail \"" + subject + "\" to: "+ ZimbraAccount.AccountA().EmailAddress + " body: foo";

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		DialogAssistant assistant = (DialogAssistant)app.zPageMail.zKeyboardShortcut(Shortcut.S_ASSISTANT);
		assistant.zEnterCommand(command);
		assistant.zClickButton(Button.B_OK);
		
		ZimbraAccount.AccountA().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
				+		"<query>subject:("+ subject +")</query>"
				+	"</SearchRequest>");
		Element[] nodes = ZimbraAccount.AccountA().soapSelectNodes("//mail:m");
		ZAssert.assertGreaterThan(nodes.length, 0, "Verify the message was received");

	}


}
