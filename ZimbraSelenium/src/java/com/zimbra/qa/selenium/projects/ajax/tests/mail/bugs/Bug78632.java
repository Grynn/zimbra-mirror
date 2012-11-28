package com.zimbra.qa.selenium.projects.ajax.tests.mail.bugs;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;


public class Bug78632 extends PrefGroupMailByMessageTest {

	ZimbraAccount account1 = null;
	ZimbraAccount account2 = null;
	
	public Bug78632() {
		logger.info("New "+ Bug78632.class.getCanonicalName());
	
		account1 = (new ZimbraAccount()).provision().authenticate();
		account2 = (new ZimbraAccount()).provision().authenticate();
		
	}
	


	@Test(	description = "Reply to all from the sent folder (alias in Reply-to header)",
			groups = { "functional" })
	public void Bug78632_01() throws HarnessException {

		//-- DATA
		
		// Set an alias on the account
		
		//-- Data setup
		
		String aliasFromDisplay = "alias" + ZimbraSeleniumProperties.getUniqueString();
		String aliasEmailAddress = 
					aliasFromDisplay + 
					"@" +
					ZimbraSeleniumProperties.getStringProperty("testdomain", "testdomain.com");
		
		
		ZimbraAdminAccount.GlobalAdmin().soapSend(
				"<AddAccountAliasRequest xmlns='urn:zimbraAdmin'>"
			+		"<id>"+ app.zGetActiveAccount().ZimbraId +"</id>"
			+		"<alias>"+ aliasEmailAddress +"</alias>"
			+	"</AddAccountAliasRequest>");
		
		// Modify the from address in the primary identity
		app.zGetActiveAccount().soapSend("<GetIdentitiesRequest xmlns='urn:zimbraAccount' />");
		String identity = app.zGetActiveAccount().soapSelectValue("//acct:identity", "id");
		
		app.zGetActiveAccount().soapSend(
				" <ModifyIdentityRequest  xmlns='urn:zimbraAccount'>"
			+		"<identity id='"+ identity +"'>"
			+			"<a name='zimbraPrefFromDisplay'>"+ aliasFromDisplay +"</a>"
			+			"<a name='zimbraPrefFromAddress'>"+ aliasEmailAddress +"</a>"
			+		"</identity>"
			+	"</ModifyIdentityRequest >");
		
		

		// Send a message from the account
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ account1.EmailAddress +"'/>" +
						"<e t='c' a='"+ account2.EmailAddress +"'/>" +
						"<e t='r' a='"+ aliasEmailAddress +"' p='"+ aliasFromDisplay +"'/>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>content" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");



		//-- GUI
		
		// Logout and login to pick up the changes
		app.zPageLogin.zNavigateTo();
		this.startingPage.zNavigateTo();

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Click in sent
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Sent));
		
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Reply the item
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLYALL);

		// Send the message
		mailform.zSubmit();



		//-- Verification
		
		// All sent messages should not have TO: include the test account
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>in:sent subject:("+ subject +")</query>"
			+	"</SearchRequest>");

		Element[] messages = app.zGetActiveAccount().soapSelectNodes("//mail:m");
		
		// Make sure there are m nodes
		ZAssert.assertEquals(messages.length, 2, "Verify 2 messages are found in the sent folder");
		
		// Iterate over the sent messages, make sure the test account is not in the To or CC list
		for (Element message : messages) {
			
			String id = message.getAttribute("id", null);
			
			ZAssert.assertNotNull(id, "Verify the sent message ID is not null");
			
			app.zGetActiveAccount().soapSend(
					"<GetMsgRequest xmlns='urn:zimbraMail' >"
				+		"<m id='"+ id +"'/>"
				+	"</GetMsgRequest>");

			Element[] elements = app.zGetActiveAccount().soapSelectNodes("//mail:e");

			/**
			 *     <GetMsgResponse xmlns="urn:zimbraMail">
			 *     		<m id="257" f="sr" rev="2" d="1354142553000" s="545" sd="1354142553000" l="5" cid="259">
			 *             <fr>content135414321527621</fr>
			 *             <e d="enus135414320622919" t="f" a="enus135414320622919@testdomain.com"/>
			 *             <e d="enus13541431881476" t="t" a="enus13541431881476@testdomain.com"/>
			 *             <e d="enus13541431889627" t="c" a="enus13541431889627@testdomain.com"/>
			 *             <su>subject135414321527620</su>
			 *             <mid>&lt;2117099442.365.1354142553368.JavaMail.root@testdomain.com></mid>
			 *             <mp body="1" s="22" part="1" ct="text/plain">
			 *             		<content>content135414321527621</content>
			 *             </mp>
			 *          </m>
			 *    </GetMsgResponse>
			 */
			
			for ( Element e : elements ) {

				String type = e.getAttribute("t", null);
				String address = e.getAttribute("a", null);
				
				// Check To (t='t') and Cc (t='c') that they don't contain the sender
				if ( "t".equals(type) || "c".equals(type) ) {
					
					ZAssert.assertNotEqual(address, app.zGetActiveAccount().EmailAddress, "Verify the sender is not included in To or Cc");
					ZAssert.assertNotEqual(address, aliasEmailAddress, "Verify the alias is not included in To or Cc");
					
				}

			}
			

		}


	}

	
	@Test(	description = "Reply to all from the sent folder (primary address in Reply-to header)",
			groups = { "functional" })
	public void Bug78632_02() throws HarnessException {

		//-- DATA
		
		// Set the primary address on the account
		
		//-- Data setup
		
		String replyToDisplay = "alias" + ZimbraSeleniumProperties.getUniqueString();
		
		
		// Modify the from address in the primary identity
		app.zGetActiveAccount().soapSend("<GetIdentitiesRequest xmlns='urn:zimbraAccount' />");
		String identity = app.zGetActiveAccount().soapSelectValue("//acct:identity", "id");
		
		app.zGetActiveAccount().soapSend(
				" <ModifyIdentityRequest  xmlns='urn:zimbraAccount'>"
			+		"<identity id='"+ identity +"'>"
			+			"<a name='zimbraPrefFromAddressType'>sendAs</a>"
			+			"<a name='zimbraPrefReplyToEnabled'>TRUE</a>"
			+			"<a name='zimbraPrefFromDisplay'>"+ replyToDisplay +"</a>"
			+			"<a name='zimbraPrefFromAddress'>"+ app.zGetActiveAccount().EmailAddress +"</a>"
			+		"</identity>"
			+	"</ModifyIdentityRequest >");
		
		

		// Send a message from the account
		
		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		app.zGetActiveAccount().soapSend(
				"<SendMsgRequest xmlns='urn:zimbraMail'>" +
					"<m>" +
						"<e t='t' a='"+ account1.EmailAddress +"'/>" +
						"<e t='c' a='"+ account2.EmailAddress +"'/>" +
						"<e t='r' a='"+ app.zGetActiveAccount().EmailAddress +"' p='"+ replyToDisplay +"'/>" +
						"<su>"+ subject +"</su>" +
						"<mp ct='text/plain'>" +
							"<content>content" + ZimbraSeleniumProperties.getUniqueString() +"</content>" +
						"</mp>" +
					"</m>" +
				"</SendMsgRequest>");



		//-- GUI
		
		// Logout and login to pick up the changes
		app.zPageLogin.zNavigateTo();
		this.startingPage.zNavigateTo();

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Click in sent
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Sent));
		
		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Reply the item
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLYALL);

		// Send the message
		mailform.zSubmit();



		//-- Verification
		
		// All sent messages should not have TO: include the test account
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
			+		"<query>in:sent subject:("+ subject +")</query>"
			+	"</SearchRequest>");

		Element[] messages = app.zGetActiveAccount().soapSelectNodes("//mail:m");
		
		// Make sure there are m nodes
		ZAssert.assertEquals(messages.length, 2, "Verify 2 messages are found in the sent folder");
		
		// Iterate over the sent messages, make sure the test account is not in the To or CC list
		for (Element message : messages) {
			
			String id = message.getAttribute("id", null);
			
			ZAssert.assertNotNull(id, "Verify the sent message ID is not null");
			
			app.zGetActiveAccount().soapSend(
					"<GetMsgRequest xmlns='urn:zimbraMail' >"
				+		"<m id='"+ id +"'/>"
				+	"</GetMsgRequest>");

			Element[] elements = app.zGetActiveAccount().soapSelectNodes("//mail:e");

			/**
			 *     <GetMsgResponse xmlns="urn:zimbraMail">
			 *     		<m id="257" f="sr" rev="2" d="1354142553000" s="545" sd="1354142553000" l="5" cid="259">
			 *             <fr>content135414321527621</fr>
			 *             <e d="enus135414320622919" t="f" a="enus135414320622919@testdomain.com"/>
			 *             <e d="enus13541431881476" t="t" a="enus13541431881476@testdomain.com"/>
			 *             <e d="enus13541431889627" t="c" a="enus13541431889627@testdomain.com"/>
			 *             <su>subject135414321527620</su>
			 *             <mid>&lt;2117099442.365.1354142553368.JavaMail.root@testdomain.com></mid>
			 *             <mp body="1" s="22" part="1" ct="text/plain">
			 *             		<content>content135414321527621</content>
			 *             </mp>
			 *          </m>
			 *    </GetMsgResponse>
			 */
			
			for ( Element e : elements ) {

				String type = e.getAttribute("t", null);
				String address = e.getAttribute("a", null);
				
				// Check To (t='t') and Cc (t='c') that they don't contain the sender
				if ( "t".equals(type) || "c".equals(type) ) {
					
					ZAssert.assertNotEqual(address, app.zGetActiveAccount().EmailAddress, "Verify the sender is not included in To or Cc");
					
				}

			}
			

		}

	}
	
}
