package com.zimbra.qa.selenium.projects.octopus.tests.sharing;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMyFiles.Locators;

public class VerifySharingContextMenu extends OctopusCommonTest
{

	public VerifySharingContextMenu()
	{
		super.startingPage = app.zPageMyFiles;
    }

	@Bugs(ids="72214")
	@Test(description="Verify if leave this shared folder menu is not displayed for child folders", groups={"functional"})
	public void VerifySharingContextMenuForChild()throws HarnessException
	{
		//Create Grantee account
		ZimbraAccount grantee = new ZimbraAccount();
		grantee.provision();
		grantee.authenticate();

		// Get current active account as owner
		ZimbraAccount owner = app.zGetActiveAccount();

		// Get the root folder of Owner
		FolderItem ownerBriefcase = FolderItem.importFromSOAP(owner, SystemFolder.Briefcase);

		String ownerFolderName = "ownerFolder"+ ZimbraSeleniumProperties.getUniqueString();
		//Create folder Using SOAP under Owner root folder.
		owner.soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>"
						+"<folder name='" + ownerFolderName + "' l='" + ownerBriefcase.getId() + "' view='document'/>"
						+"</CreateFolderRequest>");

		FolderItem ownerFolder = FolderItem.importFromSOAP(owner, ownerFolderName);

		String subFolderName = "subFolder"+ZimbraSeleniumProperties.getUniqueString();
		// Create sub folder Using SOAP under owner folder created under root.
		owner.soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>"
						+"<folder name='" + subFolderName + "' l='" + ownerFolder.getId() + "' view='document'/>"
						+"</CreateFolderRequest>");
		//Share folder with grantee using Admin access
		shareFolderViaSoap(owner, grantee, ownerFolder, SHARE_AS_ADMIN);

		FolderItem granteeBrifcase = FolderItem.importFromSOAP(grantee, SystemFolder.Briefcase);

		String mountPointFolderName = "mountFolder";
		// Create a mount point of shared folder in grantee's account
		grantee
		.soapSend(	
				"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
						+ "<link l='" + granteeBrifcase.getId()+"' name='" + mountPointFolderName
						+ "' view='document' rid='" + ownerFolder.getId()
						+ "' zid='" + owner.ZimbraId + "'/>"
						+"</CreateMountpointRequest>");

		// Logout owner
		app.zPageOctopus.zLogout();

		// Login with grantee's Credentials.
		app.zPageLogin.zLogin(grantee);
		// Navigate to sub folder 
		app.zPageMyFiles.zListItem(Action.A_LEFTCLICK, mountPointFolderName);
		//Assert if option is not available. Option should not be available.
		ZAssert.assertFalse(app.zPageOctopus.zIsContextMenuOptionPresent(Button.B_MY_FILES_LIST_ITEM, OPTION_LEAVE_SHARED_FOLDER, subFolderName), "Verify that leave this shared folder option is not available");

	}

	@Test(description="Verify if leave this shared folder menu is displayed for shared folders with view permissions", groups={"smoke"})
	public void VerifyLeaveThisSharedFolderMenuForViewRights() throws HarnessException
	{
		//Create Grantee account
		ZimbraAccount grantee = new ZimbraAccount();
		grantee.provision();
		grantee.authenticate();

		// Get current active account as owner
		ZimbraAccount owner = app.zGetActiveAccount();

		// Get the root folder of Owner
		FolderItem ownerBriefcase = FolderItem.importFromSOAP(owner, SystemFolder.Briefcase);

		String ownerFolderName = "ownerFolder"+ ZimbraSeleniumProperties.getUniqueString();
		//Create folder Using SOAP under Owner root folder.
		owner.soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>"
						+"<folder name='" + ownerFolderName + "' l='" + ownerBriefcase.getId() + "' view='document'/>"
						+"</CreateFolderRequest>");

		FolderItem ownerFolder = FolderItem.importFromSOAP(owner, ownerFolderName);

		//Share folder with grantee with Read access
		shareFolderViaSoap(owner, grantee, ownerFolder, SHARE_AS_READ);

		FolderItem granteeBrifcase = FolderItem.importFromSOAP(grantee, SystemFolder.Briefcase);

		String mountPointFolderName = "mountFolder";
		// Create a mount point of shared folder in grantee's account
		grantee.soapSend(
				"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
						+ "<link l='" + granteeBrifcase.getId()+"' name='"+mountPointFolderName
						+ "' view='document' rid='" + ownerFolder.getId()
						+ "' zid='" + owner.ZimbraId + "'/>"
						+"</CreateMountpointRequest>");

		// Logout owner
		app.zPageOctopus.zLogout();

		// Login with grantee's Credentials.
		app.zPageLogin.zLogin(grantee);

		//Assert if leave this shared folder option is available with view permission.
		ZAssert.assertTrue(app.zPageOctopus.zIsContextMenuOptionPresent(Button.B_MY_FILES_LIST_ITEM, OPTION_LEAVE_SHARED_FOLDER, mountPointFolderName), "Verify that leave this shared folder option is available");
	}

	@Test(description="Verify if leave this shared folder menu is displayed for shared folders with Edit permissions", groups={"smoke"})
	public void VerifyLeaveThisSharedFolderOption_Edit() throws HarnessException
	{
		//Create Grantee account
		ZimbraAccount grantee = new ZimbraAccount();
		grantee.provision();
		grantee.authenticate();

		// Get current active account as owner
		ZimbraAccount owner = app.zGetActiveAccount();

		// Get the root folder of Owner
		FolderItem ownerBriefcase = FolderItem.importFromSOAP(owner, SystemFolder.Briefcase);

		String ownerFolderName = "ownerFolder"+ ZimbraSeleniumProperties.getUniqueString();
		//Create folder Using SOAP under Owner root folder.
		owner.soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>"
						+"<folder name='" + ownerFolderName + "' l='" + ownerBriefcase.getId() + "' view='document'/>"
						+"</CreateFolderRequest>");

		FolderItem ownerFolder = FolderItem.importFromSOAP(owner, ownerFolderName);

		//Share folder with grantee using View ,Edit access
		shareFolderViaSoap(owner, grantee, ownerFolder, SHARE_AS_READWRITE);

		FolderItem granteeBrifcase = FolderItem.importFromSOAP(grantee, SystemFolder.Briefcase);

		String mountPointFolderName = "mountFolder";
		// Create a mount point of shared folder in grantee's account
		grantee.soapSend(
				"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
						+ "<link l='" + granteeBrifcase.getId()+"' name='" + mountPointFolderName
						+ "' view='document' rid='" + ownerFolder.getId()
						+ "' zid='" + owner.ZimbraId + "'/>"
						+"</CreateMountpointRequest>");
		// Logout owner
		app.zPageOctopus.zLogout();

		// Login with grantee's Credentials.
		app.zPageLogin.zLogin(grantee);

		//Assert if leave this shared folder option is available with view ,Edit permissions. 
		ZAssert.assertTrue(app.zPageOctopus.zIsContextMenuOptionPresent(Button.B_MY_FILES_LIST_ITEM,OPTION_LEAVE_SHARED_FOLDER,mountPointFolderName), "Verify that leave this shared folder option is available for Edit");
	}

}

