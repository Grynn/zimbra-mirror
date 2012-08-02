package com.zimbra.qa.selenium.projects.ajax.tests.mail.gui.hover;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.TooltipContact;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail;


public class Address extends PrefGroupMailByMessageTest {

	
	public Address() {
		logger.info("New "+ Address.class.getCanonicalName());
		
	}
	
	@Test(	description = "Hover over GAL address",
			groups = { "functional" })
	public void Address_01() throws HarnessException {
		
		//-- Data Setup
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		
		// AccountA (in the GAL) sends a message to the test account
		ZimbraAccount.AccountA().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>body" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");
		


		//-- GUI steps
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Hover over the From field
		display.zHoverOver(DisplayMail.Field.From);

		
		
		//-- GUI Verification
		
		TooltipContact tooltip = new TooltipContact(app.zPageMail);
		tooltip.zWaitForActive();
		
		ZAssert.assertTrue(tooltip.zIsActive(), "Verify the tooltip shows");
		
		
	}

	@Test(	description = "Hover over External (non-GAL) address",
			groups = { "functional" })
	public void Address_02() throws HarnessException {
		
		//-- Data Setup
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox);
		
		// AccountA (in the GAL) sends a message to the test account
		app.zGetActiveAccount().soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
		        		+		"<m l='"+ folder.getId() +"' >"
		            	+			"<content>From: foo@foo.com\n"
		            	+				"To: foo@foo.com \n"
		            	+				"Subject: "+ subject +"\n"
		            	+				"MIME-Version: 1.0 \n"
		            	+				"Content-Type: text/plain; charset=utf-8 \n"
		            	+				"Content-Transfer-Encoding: 7bit\n"
		            	+				"\n"
		            	+				"simple text string in the body\n"
		            	+			"</content>"
		            	+		"</m>"
						+	"</AddMsgRequest>");
		


		//-- GUI steps
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Hover over the From field
		display.zHoverOver(DisplayMail.Field.From);

		
		
		//-- GUI Verification
		
		TooltipContact tooltip = new TooltipContact(app.zPageMail);
		tooltip.zWaitForActive();
		
		ZAssert.assertTrue(tooltip.zIsActive(), "Verify the tooltip shows");
		
	}


}
