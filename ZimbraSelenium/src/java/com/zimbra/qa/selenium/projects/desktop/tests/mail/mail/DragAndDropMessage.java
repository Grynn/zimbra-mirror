package com.zimbra.qa.selenium.projects.desktop.tests.mail.mail;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.projects.desktop.ui.Toaster;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;

public class DragAndDropMessage extends AjaxCommonTest {

	@SuppressWarnings("serial")
	public DragAndDropMessage() {
		logger.info("New "+ DragAndDropMessage.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageMail;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {{
				    put("zimbraPrefGroupMailBy", "message");
				}};

	}

	@Test(	description = "Drag and Drop a message from Inbox to subfolder",
			groups = { "smoke" })
	public void DragAndDropMessage_01() throws HarnessException {

		String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
		String foldername = "folder"+ ZimbraSeleniumProperties.getUniqueString();

		// Create a subfolder to move the message into
		// i.e. Inbox/subfolder
		//
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='" + foldername +"' l='"+ inbox.getId() +"'/>" +
					"</CreateFolderRequest>");

		// Click Get Mail button to view folder in list		
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageMail.zWaitForDesktopLoadingSpinner(5000);
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);

		// Send a message to the account
		ZimbraAccount.AccountA().soapSend(
					"<SendMsgRequest xmlns='urn:zimbraMail'>" +
						"<m>" +
							"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
							"<su>"+ subject +"</su>" +
							"<mp ct='text/plain'>" +
								"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
							"</mp>" +
						"</m>" +
					"</SendMsgRequest>");

		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inbox);
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Get the mail item for the new message
		MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");

		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Expand the Inbox folder to see the subfolder?

		// Select the item
		app.zPageMail.zDragAndDrop(
					"css=td[id$='"+ mail.getId() +"__su']",
				"css=div[id^='zti__" + app.zGetActiveAccount().EmailAddress + ":main_Mail__'][id$=':" + subfolder.getId() +"']"); 

		Toaster toast = app.zPageMain.zGetToaster();
      String toastMsg = toast.zGetToastMessage();
      ZAssert.assertStringContains(toastMsg, "1 message moved to \"" + foldername + "\"", "Verify toast message" );
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

		// Get the message, make sure it is in the correct folder
		app.zGetActiveAccount().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>" +
					"<m id='" + mail.getId() +"'/>" +
				"</GetMsgRequest>");
		String folderId = app.zGetActiveAccount().soapSelectValue("//mail:m", "l");

		ZAssert.assertEquals(folderId, subfolder.getId(), "Verify the subfolder ID that the message was moved into");

	}

	@Test(  description = "Drag and Drop a message from ZCS Inbox to local mail folder",
         groups = { "smoke" })
	public void DndMessageFromZimbraAccountToLocalMailFolder() throws HarnessException {

      String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
      String foldername = "folder"+ ZimbraSeleniumProperties.getUniqueString();

      // Create a folder under local to move the message into
      //
      FolderItem rootLocalFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(),
            SystemFolder.UserRoot, SOAP_DESTINATION_HOST_TYPE.CLIENT, ZimbraAccount.clientAccountName);
      app.zGetActiveAccount().soapSend(
               "<CreateFolderRequest xmlns='urn:zimbraMail'>" +
                  "<folder name='" + foldername +"' l='"+ rootLocalFolder.getId() +"'/>" +
               "</CreateFolderRequest>",
               SOAP_DESTINATION_HOST_TYPE.CLIENT,
               ZimbraAccount.clientAccountName);

      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

      FolderItem localFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(),
            foldername,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);

      // Send a message to the account
      ZimbraAccount.AccountA().soapSend(
               "<SendMsgRequest xmlns='urn:zimbraMail'>" +
                  "<m>" +
                     "<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     "<su>"+ subject +"</su>" +
                     "<mp ct='text/plain'>" +
                        "<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
                     "</mp>" +
                  "</m>" +
               "</SendMsgRequest>");

      // Click Get Mail button
      app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
      
      FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
      app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inbox);

      // Get the mail item for the new message
      MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");

      // Select the item
      app.zPageMail.zDragAndDrop(
               "css=td[id$='"+ mail.getId() +"__su']",
            "css=div[id^='zti__" + ZimbraAccount.clientAccountName + ":main_Mail__" + localFolder.getId() +"']"); 

      Toaster toast = app.zPageMain.zGetToaster();
      String toastMsg = toast.zGetToastMessage();
      ZAssert.assertStringContains(toastMsg, "1 message moved to \"" + foldername + "\"", "Verify toast message" );
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

      MailItem.importFromSOAP(app.zGetActiveAccount(),
            "subject:("+ subject +")",
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);

      String folderId = app.zGetActiveAccount().soapSelectValue("//mail:m", "l");

      ZAssert.assertEquals(folderId, localFolder.getId(), "Verify the local folder ID that the message was moved into");

      // Verifying that ZCS returns null when querying the same message
      MailItem zcsMailAfterMove = null;

      try {
         zcsMailAfterMove = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
      } catch (HarnessException he) {
         // This is expected because zcsMailAfterMove is supposed to be null
      }
      ZAssert.assertNull(zcsMailAfterMove, "Verifying Mail Item on ZCS server after being moved to local folders");
	}

	@Test(  description = "Drag and Drop a message from ZCS Inbox to local mail subfolder",
	groups = { "functional" })
	public void DndMessageFromZimbraAccountToLocalMailSubfolder() throws HarnessException {

	   String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
	   String foldername = "folder"+ ZimbraSeleniumProperties.getUniqueString();
	   String subfoldername = "subfolder"+ ZimbraSeleniumProperties.getUniqueString();

	   // Create a folder under local to move the message into
	   //
	   FolderItem rootLocalFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(),
	         SystemFolder.UserRoot, SOAP_DESTINATION_HOST_TYPE.CLIENT, ZimbraAccount.clientAccountName);

	   app.zGetActiveAccount().soapSend(
	         "<CreateFolderRequest xmlns='urn:zimbraMail'>" +
	         "<folder name='" + foldername +"' l='"+ rootLocalFolder.getId() +"'/>" +
	         "</CreateFolderRequest>",
	         SOAP_DESTINATION_HOST_TYPE.CLIENT,
	         ZimbraAccount.clientAccountName);

	   GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

      FolderItem localFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(),
	         foldername,
	         SOAP_DESTINATION_HOST_TYPE.CLIENT,
	         ZimbraAccount.clientAccountName);

	   // Create local sub folder
	   app.zGetActiveAccount().soapSend(
	         "<CreateFolderRequest xmlns='urn:zimbraMail'>" +
	         "<folder name='" + subfoldername +"' l='"+ localFolder.getId() +"'/>" +
	         "</CreateFolderRequest>",
	         SOAP_DESTINATION_HOST_TYPE.CLIENT,
	         ZimbraAccount.clientAccountName);

      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

      FolderItem localSubfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(),
            subfoldername,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            ZimbraAccount.clientAccountName);

	   // Send a message to the account
	   ZimbraAccount.AccountA().soapSend(
	         "<SendMsgRequest xmlns='urn:zimbraMail'>" +
	         "<m>" +
	         "<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
	         "<su>"+ subject +"</su>" +
	         "<mp ct='text/plain'>" +
	         "<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
	         "</mp>" +
	         "</m>" +
	         "</SendMsgRequest>");

	   // Click Get Mail button
	   app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

	   FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);
	   app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, inbox);

	   // Get the mail item for the new message
	   MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");

	   // Select the item
	   app.zPageMail.zDragAndDrop(
	         "css=td[id$='"+ mail.getId() +"__su']",
	         "css=div[id^='zti__" + ZimbraAccount.clientAccountName + ":main_Mail__" + localSubfolder.getId() +"']"); 

	   Toaster toast = app.zPageMain.zGetToaster();
	   String toastMsg = toast.zGetToastMessage();
	   ZAssert.assertStringContains(toastMsg, "1 message moved to \"" + subfoldername + "\"", "Verify toast message" );
	   GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
	   app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

	   MailItem.importFromSOAP(app.zGetActiveAccount(),
	         "subject:("+ subject +")",
	         SOAP_DESTINATION_HOST_TYPE.CLIENT,
	         ZimbraAccount.clientAccountName);

	   String folderId = app.zGetActiveAccount().soapSelectValue("//mail:m", "l");

	   ZAssert.assertEquals(folderId, localSubfolder.getId(), "Verify the local folder ID that the message was moved into");

	   // Verifying that ZCS returns null when querying the same message
	   MailItem zcsMailAfterMove = null;

	   try {
	      zcsMailAfterMove = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
	   } catch (HarnessException he) {
	      // This is expected because zcsMailAfterMove is supposed to be null
	   }
	   ZAssert.assertNull(zcsMailAfterMove, "Verifying Mail Item on ZCS server after being moved to local folders");
	}
}
