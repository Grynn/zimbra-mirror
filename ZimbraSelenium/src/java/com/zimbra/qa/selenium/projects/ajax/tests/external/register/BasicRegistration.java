package com.zimbra.qa.selenium.projects.ajax.tests.external.register;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;



public class BasicRegistration extends AjaxCommonTest {
	
	public BasicRegistration() {
		logger.info("New "+ BasicRegistration.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageLogin;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Register as and external user",
			groups = { "sanity" })
	public void BasicRegistration_01() throws HarnessException {
		
		
		
		//-- Data Setup
		
		
		ZimbraExternalAccount external = new ZimbraExternalAccount();
		external.setEmailAddress("external" + ZimbraSeleniumProperties.getUniqueString() + "@example.com");
		
		FolderItem inbox = FolderItem.importFromSOAP(ZimbraAccount.AccountZWC(), FolderItem.SystemFolder.Inbox);
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();

		// Create a subfolder in Inbox
		ZimbraAccount.AccountZWC().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername +"' l='" + inbox.getId() +"' view='message'/>"
				+	"</CreateFolderRequest>");
		String folderid = ZimbraAccount.AccountZWC().soapSelectValue("//mail:folder", "id");

		// Share the subfolder
		ZimbraAccount.AccountZWC().soapSend(
					"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ folderid +"' op='grant'>"
				+			"<grant d='"+ external.EmailAddress +"' inh='1' gt='guest' pw='' perm='r'/>"
				+		"</action>"
				+	"</FolderActionRequest>");

		// Send the notification
		ZimbraAccount.AccountZWC().soapSend(
					"<SendShareNotificationRequest xmlns='urn:zimbraMail'>"
				+		"<item id='"+ folderid +"'/>"
				+		"<e a='"+ external.EmailAddress +"'/>"
				+		"<notes/>"
				+	"</SendShareNotificationRequest>");


		// Parse the URL From the sent message
		ZimbraAccount.AccountZWC().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
				+		"<query>in:sent "+ external.EmailAddress +"</query>"
				+	"</SearchRequest>");
		String messageid = ZimbraAccount.AccountZWC().soapSelectValue("//mail:m", "id");
		
		ZimbraAccount.AccountZWC().soapSend(
					"<GetMsgRequest xmlns='urn:zimbraMail'>"
				+		"<m id='"+ messageid +"' html='1'/>"
				+	"</GetMsgRequest>");
		
		// Based on the content of the sent message, the URL's can be determined
		Element response = ZimbraAccount.AccountZWC().soapSelectNode("//mail:GetMsgResponse", 1);
		external.setURL(response);
		
		
		
		//-- GUI Actions
		
		
		// Navigate to the registration page
		app.zPageExternalRegistration.zSetURL(external.getRegistrationURL());
		app.zPageExternalRegistration.zNavigateTo();
		app.zPageExternalRegistration.zLogin(external);

		
		
		//-- Verification
		
		
		// After logging in, make sure the page appears correctly
		app.zPageExternalMain.zWaitForActive();
		boolean loaded = app.zPageExternalMain.zIsActive();
		ZAssert.assertTrue(loaded, "Verify that the main page became active");
		
		
	}


}
