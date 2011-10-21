package com.zimbra.qa.selenium.projects.ajax.tests.calendar.mountpoints;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogShare;


public class CreateShare extends AjaxCommonTest  {

	public CreateShare() {
		logger.info("New "+ CreateShare.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageCalendar;
		super.startingAccountPreferences = null;

	}
	
	@Test(	description = "Share a calendar - Viewer",
			groups = { "smoke" })
	public void CreateShare_01() throws HarnessException {
		
		String calendarname = "calendar" + ZimbraSeleniumProperties.getUniqueString();


		// Create a calendar
		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + calendarname +"' l='1' view='appointment'/>"
				+	"</CreateFolderRequest>");

		// Make sure the folder was created on the server
		FolderItem calendar = FolderItem.importFromSOAP(app.zGetActiveAccount(), calendarname);
		ZAssert.assertNotNull(calendar, "Verify the folder exists on the server");

		
		
		// Need to do Refresh to see folder in the list 
		app.zPageCalendar.zToolbarPressButton(Button.B_REFRESH);
		

		// Right click on folder, select "Share"
		DialogShare dialog = (DialogShare)app.zTreeCalendar.zTreeItem(Action.A_RIGHTCLICK, Button.B_SHARE, calendar);
		ZAssert.assertNotNull(dialog, "Verify the sharing dialog pops up");

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
		//    <GetShareInfoResponse xmlns="urn:zimbraAccount">
		//		<share granteeId="e3c083c5-102a-416e-bcf4-6d4c59197e20" ownerName="enus13191472607033" granteeDisplayName="enus13191472702505" ownerId="8d5589ff-0548-4562-8d1d-1a4f70e3ca7e" rights="r" folderPath="/folder13191472674374" view="contact" granteeType="usr" ownerEmail="enus13191472607033@testdomain.com" granteeName="enus13191472702505@testdomain.com" folderId="257"/>
	    //	  </GetShareInfoResponse>

		String ownerEmail = ZimbraAccount.AccountA().soapSelectValue("//acct:GetShareInfoResponse//acct:share[@folderPath='/"+ calendarname +"']", "ownerEmail");
		ZAssert.assertEquals(ownerEmail, app.zGetActiveAccount().EmailAddress, "Verify the owner of the shared folder");
		
		String rights = ZimbraAccount.AccountA().soapSelectValue("//acct:GetShareInfoResponse//acct:share[@folderPath='/"+ calendarname +"']", "rights");
		ZAssert.assertEquals(rights, "r", "Verify the rights are 'read only'");

		String granteeType = ZimbraAccount.AccountA().soapSelectValue("//acct:GetShareInfoResponse//acct:share[@folderPath='/"+ calendarname +"']", "granteeType");
		ZAssert.assertEquals(granteeType, "usr", "Verify the grantee type is 'user'");

	}

	
	

}
