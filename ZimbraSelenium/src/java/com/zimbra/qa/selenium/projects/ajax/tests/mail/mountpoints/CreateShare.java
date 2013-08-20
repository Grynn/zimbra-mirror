/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.mail.mountpoints;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogShare;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogShare.ShareRole;


public class CreateShare extends PrefGroupMailByMessageTest {

	public CreateShare() {
		logger.info("New "+ CreateShare.class.getCanonicalName());
		
		// This test case seems to be intermittently failing.
		// The problem seems to be when typing an address
		// into the sharing dialog.  Maybe disabling auto-compelte will help?
		//
		// super.startingAccountPreferences.put("zimbraFeatureGalAutoCompleteEnabled", "FALSE");
		
	}
	
	@Test(	description = "Share a folder - Viewer",
			groups = { "smoke" })
	public void CreateShare_01() throws HarnessException {
		
		// TODO: remove this.  For debugging, take screenshots before and after test
		ExecuteHarnessMain.ResultListener.captureScreen();

		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox);
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();


		// Create a subfolder in Inbox
		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername +"' l='" + inbox.getId() +"'/>"
				+	"</CreateFolderRequest>");

		//Need to do Refresh by clicking on getmail button to see folder in the list 
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Make sure the folder was created on the server
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		ZAssert.assertNotNull(subfolder, "Verify the folder exists on the server");


		// Right click on folder, select "Share"
		DialogShare dialog = (DialogShare)app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_SHARE, subfolder);
		ZAssert.assertNotNull(dialog, "Verify the sharing dialog pops up");

		
		// This test case seems to be a bit intermittent.  Maybe
		// there is a timing issue with the dialog showing up
		// fast enough?  Add some sleep here to see if it makes a
		// difference.
		//
		SleepUtil.sleepLong();
		
		
		// Use defaults for all options
		dialog.zSetEmailAddress(ZimbraAccount.AccountA().EmailAddress);
		
		// Send it
		dialog.zClickButton(Button.B_OK);
		
		// Make sure that AccountA now has the share
		ZimbraAccount.AccountA().soapSend(
					"<GetShareInfoRequest xmlns='urn:zimbraAccount'>"
				+		"<grantee type='usr'/>"
				+		"<owner by='name'>"+ app.zGetActiveAccount().EmailAddress +"</owner>"
				+	"</GetShareInfoRequest>");
		
		// Example response:
		//	    <GetShareInfoResponse xmlns="urn:zimbraAccount">
		//	      <share granteeId="0136d047-b771-49c0-a735-12183f3ca654" ownerName="enus12986828702967" granteeDisplayName="enus12986828648903" ownerId="4000b6a8-56bc-4910-ae3e-77528a5d5b18" rights="r" folderPath="/Inbox/folder12986828702964" mid="257" granteeType="usr" ownerEmail="enus12986828702967@testdomain.com" granteeName="enus12986828648903@testdomain.com" folderId="257"/>
		//	    </GetShareInfoResponse>

		String ownerEmail = ZimbraAccount.AccountA().soapSelectValue("//acct:GetShareInfoResponse//acct:share[@folderPath='/Inbox/"+ foldername +"']", "ownerEmail");
		ZAssert.assertEquals(ownerEmail, app.zGetActiveAccount().EmailAddress, "Verify the owner of the shared folder");
		
		String rights = ZimbraAccount.AccountA().soapSelectValue("//acct:GetShareInfoResponse//acct:share[@folderPath='/Inbox/"+ foldername +"']", "rights");
		ZAssert.assertEquals(rights, "r", "Verify the rights are 'read only'");

		String granteeType = ZimbraAccount.AccountA().soapSelectValue("//acct:GetShareInfoResponse//acct:share[@folderPath='/Inbox/"+ foldername +"']", "granteeType");
		ZAssert.assertEquals(granteeType, "usr", "Verify the grantee type is 'user'");

		// TODO: remove this.  For debugging, take screenshots before and after test
		ExecuteHarnessMain.ResultListener.captureScreen();

	}

	
	@Test(	description = "Share a folder - Manager",
			groups = { "smoke" })
	public void CreateShare_02() throws HarnessException {
		
		// TODO: remove this.  For debugging, take screenshots before and after test
		ExecuteHarnessMain.ResultListener.captureScreen();
		
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox);
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();


		// Create a subfolder in Inbox
		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername +"' l='" + inbox.getId() +"'/>"
				+	"</CreateFolderRequest>");

		//Need to do Refresh by clicking on getmail button to see folder in the list 
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		// Make sure the folder was created on the server
		FolderItem subfolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		ZAssert.assertNotNull(subfolder, "Verify the folder exists on the server");


		// Right click on folder, select "Share"
		DialogShare dialog = (DialogShare)app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_SHARE, subfolder);
		ZAssert.assertNotNull(dialog, "Verify the sharing dialog pops up");

		// This test case seems to be a bit intermittent.  Maybe
		// there is a timing issue with the dialog showing up
		// fast enough?  Add some sleep here to see if it makes a
		// difference.
		//
		SleepUtil.sleepLong();
		
		
		// Use defaults for all options
		dialog.zSetEmailAddress(ZimbraAccount.AccountA().EmailAddress);
		dialog.zSetRole(ShareRole.Manager);
		
		// Send it
		dialog.zClickButton(Button.B_OK);
		
		// Make sure that AccountA now has the share
		ZimbraAccount.AccountA().soapSend(
					"<GetShareInfoRequest xmlns='urn:zimbraAccount'>"
				+		"<grantee type='usr'/>"
				+		"<owner by='name'>"+ app.zGetActiveAccount().EmailAddress +"</owner>"
				+	"</GetShareInfoRequest>");
		
		// Example response:
		//	    <GetShareInfoResponse xmlns="urn:zimbraAccount">
		//	      <share granteeId="0136d047-b771-49c0-a735-12183f3ca654" ownerName="enus12986828702967" granteeDisplayName="enus12986828648903" ownerId="4000b6a8-56bc-4910-ae3e-77528a5d5b18" rights="r" folderPath="/Inbox/folder12986828702964" mid="257" granteeType="usr" ownerEmail="enus12986828702967@testdomain.com" granteeName="enus12986828648903@testdomain.com" folderId="257"/>
		//	    </GetShareInfoResponse>

		String ownerEmail = ZimbraAccount.AccountA().soapSelectValue("//acct:GetShareInfoResponse//acct:share[@folderPath='/Inbox/"+ foldername +"']", "ownerEmail");
		ZAssert.assertEquals(ownerEmail, app.zGetActiveAccount().EmailAddress, "Verify the owner of the shared folder");
		
		String rights = ZimbraAccount.AccountA().soapSelectValue("//acct:GetShareInfoResponse//acct:share[@folderPath='/Inbox/"+ foldername +"']", "rights");
		// The order of the rights may not be consistent, check each one
		// ZAssert.assertEquals(rights, "rwidx", "Verify the rights are 'Manager'");
		ZAssert.assertStringContains(rights, "r", "Verify the rights are 'Manager'");
		ZAssert.assertStringContains(rights, "w", "Verify the rights are 'Manager'");
		ZAssert.assertStringContains(rights, "i", "Verify the rights are 'Manager'");
		ZAssert.assertStringContains(rights, "d", "Verify the rights are 'Manager'");
		ZAssert.assertStringContains(rights, "x", "Verify the rights are 'Manager'");
		
		String granteeType = ZimbraAccount.AccountA().soapSelectValue("//acct:GetShareInfoResponse//acct:share[@folderPath='/Inbox/"+ foldername +"']", "granteeType");
		ZAssert.assertEquals(granteeType, "usr", "Verify the grantee type is 'user'");

		// TODO: remove this.  For debugging, take screenshots before and after test
		ExecuteHarnessMain.ResultListener.captureScreen();

	}



	

}
