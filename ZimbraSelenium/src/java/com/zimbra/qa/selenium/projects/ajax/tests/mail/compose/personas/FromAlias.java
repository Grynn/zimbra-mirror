package com.zimbra.qa.selenium.projects.ajax.tests.mail.compose.personas;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew.Field;


public class FromAlias extends PrefGroupMailByMessageTest {

	private String AliasEmailAddress = null;
	private String AliasFromDisplay = null;

	public FromAlias() {
		logger.info("New "+ FromAlias.class.getCanonicalName());
		
		super.startingAccountPreferences.put("zimbraPrefComposeFormat", "text");
		
	}
	
	@BeforeMethod( groups = { "always" } )
	public void addAliasToActiveAccount() throws HarnessException {
		
		AliasFromDisplay = "alias" + ZimbraSeleniumProperties.getUniqueString();
		AliasEmailAddress = AliasFromDisplay + 
					"@" +
					ZimbraSeleniumProperties.getStringProperty("testdomain", "testdomain.com");
		
		String identity = "identity" + ZimbraSeleniumProperties.getUniqueString();
		
		ZimbraAdminAccount.GlobalAdmin().soapSend(
				"<AddAccountAliasRequest xmlns='urn:zimbraAdmin'>"
			+		"<id>"+ app.zGetActiveAccount().ZimbraId +"</id>"
			+		"<alias>"+ AliasEmailAddress +"</alias>"
			+	"</AddAccountAliasRequest>");
		
		app.zGetActiveAccount().soapSend(
				" <CreateIdentityRequest xmlns='urn:zimbraAccount'>"
			+		"<identity name='"+ identity +"'>"
			+			"<a name='zimbraPrefIdentityName'>"+ identity +"</a>"
			+			"<a name='zimbraPrefFromDisplay'>"+ AliasFromDisplay +"</a>"
			+			"<a name='zimbraPrefFromAddress'>"+ AliasEmailAddress +"</a>"
			+			"<a name='zimbraPrefReplyToEnabled'>FALSE</a>"
			+			"<a name='zimbraPrefReplyToDisplay'/>"
			+			"<a name='zimbraPrefDefaultSignatureId'/>"
			+			"<a name='zimbraPrefForwardReplySignatureId'/>"
			+			"<a name='zimbraPrefWhenSentToEnabled'>FALSE</a>"
			+			"<a name='zimbraPrefWhenInFoldersEnabled'>FALSE</a>"
			+		"</identity>"
			+	"</CreateIdentityRequest>");
		
		// Logout and login to pick up the changes
		if ( app.zPageMain.zIsActive() )
			app.zPageMain.zLogout();
		app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());
		this.startingPage.zNavigateTo();
		
	}

	@Test(	description = "Send a mail using an alias as From",
			groups = { "functional" })
	public void FromAlias_01() throws HarnessException {
		
		
		
		// Create the message data to be sent
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		
		
		// Open the new mail form
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
		ZAssert.assertNotNull(mailform, "Verify the new form opened");
		
		// Fill out the form with the data
		mailform.zFillField(Field.From, AliasEmailAddress);
		mailform.zFillField(Field.To, ZimbraAccount.AccountA().EmailAddress);
		mailform.zFillField(Field.Subject, subject);
		mailform.zFillField(Field.Body, "content" + ZimbraSeleniumProperties.getUniqueString());
		
		// Send the message
		mailform.zSubmit();

		
		
		// Verify the message shows as from the alias
		ZimbraAccount.AccountA().soapSend(
					"<SearchRequest types='message' xmlns='urn:zimbraMail'>"
			+			"<query>subject:("+ subject +")</query>"
			+		"</SearchRequest>");
		String id = ZimbraAccount.AccountA().soapSelectValue("//mail:m", "id");

		ZimbraAccount.AccountA().soapSend(
					"<GetMsgRequest xmlns='urn:zimbraMail'>"
			+			"<m id='"+ id +"' html='1'/>"
			+		"</GetMsgRequest>");

		String address = ZimbraAccount.AccountA().soapSelectValue("//mail:e[@t='f']", "a");
		ZAssert.assertEquals(address, AliasEmailAddress, "Verify the from is the alias email address");
		
	}


}
