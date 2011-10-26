package com.zimbra.qa.selenium.projects.ajax.tests.mail.folders.accounts;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;


public class GetExternal extends PrefGroupMailByMessageTest {

	public GetExternal() {
		logger.info("New "+ GetExternal.class.getCanonicalName());
		
	}
	
	/**
	 * Objective: View an external folder - POP
	 * 
	 * 1. Create an account on the server
	 * 2. Put a message in the inbox
	 * 3. Login to ajax
	 * 4. Create a folder
	 * 5. Add a data source to the account from step 1, associate with the folder in step 4
	 * 6. Right click on the folder -> Get external mail
	 * 7. Verify the message from step 2 appears
	 * 
	 * @throws HarnessException
	 */
	@Test(	description = "View an external folder - POP",
			groups = { "smoke" })
	public void GetExternal_01() throws HarnessException {
		
		
		// Create the external data source on the same server
		ZimbraAccount external = new ZimbraAccount();
		external.provision();
		external.authenticate();
		
		// Add a message to the inbox
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();

		external.soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
    		+		"<m l='"+ FolderItem.importFromSOAP(external, SystemFolder.Inbox).getId() +"' f='u'>"
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

		// Create the folder to put the data source
		String foldername = "external" + ZimbraSeleniumProperties.getUniqueString();
		
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                	"<folder name='"+ foldername +"' l='1'/>" +
                "</CreateFolderRequest>");

		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		ZAssert.assertNotNull(folder, "Verify the subfolder is available");
		
		// Create the data source
		String datasourcename = "datasource" + ZimbraSeleniumProperties.getUniqueString();
		String datasourceHost = ZimbraSeleniumProperties.getStringProperty("server.host");
		String datasourcePopPort = ZimbraSeleniumProperties.getStringProperty("server.pop.port");
		String datasourcePopType = ZimbraSeleniumProperties.getStringProperty("server.pop.type");
		
		app.zGetActiveAccount().soapSend(
				"<CreateDataSourceRequest xmlns='urn:zimbraMail'>"
			+		"<pop3 name='"+ datasourcename +"' l='"+ folder.getId() +"' isEnabled='true' "
			+			"port='"+ datasourcePopPort +"' host='"+ datasourceHost +"' connectionType='"+ datasourcePopType +"' leaveOnServer='true' "
			+			"username='"+ external.EmailAddress +"' password='"+ external.Password +"' "
			+			"useAddressForForwardReply='true' replyToDisplay='Bar Foo' replyToAddress='"+ app.zGetActiveAccount().EmailAddress +"' "
			+			"fromDisplay='Foo Bar' fromAddress='"+ app.zGetActiveAccount().EmailAddress +"' />"
			+	"</CreateDataSourceRequest>");

		
		
		// Need to logout/login to get the new folder
		ZimbraAccount active = app.zGetActiveAccount();
		if ( app.zPageMain.zIsActive() )
			app.zPageMain.zLogout();
		app.zPageLogin.zLogin(active);
		startingPage.zNavigateTo();
		

		
		// Click on the folder and select Sync
		app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_FOLDER_GET_EXTERNAL, folder);
		
		// TODO: how to remove this sleep?  Wait for the toaster?
		Toaster toaster = app.zPageMain.zGetToaster();
		
		// Wait for the toaster to show: "external account is loaded"
		for (int i = 0; i < 15; i++) {
			if ( toaster.zIsActive() )
				break;
			logger.info("Waiting for the toaster: external account is loaded");
			SleepUtil.sleep(1000);
		}
		ZAssert.assertTrue(toaster.zIsActive(), "Verify the toaster showed up");
		
		// Click on the folder, and verify the message appears
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, folder);
		
		// Get the messages
		List<MailItem> messages = app.zPageMail.zListGetMessages();
		ZAssert.assertNotNull(messages, "Verify the message list exists");

		// Make sure the message appears in the list
		MailItem found = null;
		for (MailItem m : messages) {
			logger.info("Subject: looking for "+ subject +" found: "+ m.gSubject);
			if ( subject.equals(m.gSubject) ) {
				found = m;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the message is in the external folder");

		
	}	


}
