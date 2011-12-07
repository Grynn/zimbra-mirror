package com.zimbra.qa.selenium.projects.octopus.tests.sharing;

import org.testng.annotations.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogFolderShare;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;

public class CreateShare extends OctopusCommonTest {

	private ZimbraAccount granteeAccount = null;
	private boolean _folderIsCreated = false;
	private String _folderName = null;

	public CreateShare() {
		logger.info("New " + CreateShare.class.getCanonicalName());

		// Test starts at the Octopus page
		super.startingPage = app.zPageMyFiles;
		super.startingAccountPreferences = null;
	}

	@BeforeMethod(groups = { "always" })
	public void testReset() {
		granteeAccount = new ZimbraAccount();
		granteeAccount.provision();
		granteeAccount.authenticate();
		_folderName = null;
		_folderIsCreated = false;
	}

	@Test(description = "Create share using SOAP - verify Share info using SOAP", groups = { "sanity" })
	public void CreateShare_01() throws HarnessException {
		ZimbraAccount ownerAccount = app.zGetActiveAccount();

		FolderItem ownerBriefcaseRootFolder = FolderItem.importFromSOAP(
				ownerAccount, SystemFolder.Briefcase);

		ZAssert.assertNotNull(ownerBriefcaseRootFolder,
				"Verify the owner Briefcase root folder exists");

		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerFolder"
				+ ZimbraSeleniumProperties.getUniqueString();

		ownerAccount.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + ownerFoldername + "' l='"
				+ ownerBriefcaseRootFolder.getId() + "' view='document'/>"
				+ "</CreateFolderRequest>");

		// Verify the folder exists on the server
		FolderItem ownerFolderItem = FolderItem.importFromSOAP(ownerAccount,
				ownerFoldername);

		ZAssert.assertNotNull(ownerFolderItem, "Verify the owner folder exists");

		_folderIsCreated = true;
		_folderName = ownerFoldername;

		ownerAccount.soapSend("<FolderActionRequest xmlns='urn:zimbraMail'>"
				+ "<action id='" + ownerFolderItem.getId() + "' op='grant'>"
				+ "<grant d='" + granteeAccount.EmailAddress
				+ "' gt='usr' perm='r'/>" + "</action>"
				+ "</FolderActionRequest>");

		ownerAccount
				.soapSend("<SendShareNotificationRequest xmlns='urn:zimbraMail'>"
						+ "<item id='"
						+ ownerFolderItem.getId()
						+ "'/>"
						+ "<e a='"
						+ granteeAccount.EmailAddress
						+ "'/>"
						+ "<notes _content='I invite you to share the folder'/>"
						+ "</SendShareNotificationRequest>");

		SleepUtil.sleepSmall();

		// grantee verifies notification message
		granteeAccount
				.soapSend("<GetShareNotificationsRequest xmlns='urn:zimbraMail'/>");

		ZAssert.assertTrue(granteeAccount.soapMatch(
				"//mail:GetShareNotificationsResponse//mail:link", "name",
				ownerFoldername),
				"Verify link to the folder in the notification message");

		ZAssert.assertTrue(
				granteeAccount.soapMatch(
						"//mail:GetShareNotificationsResponse//mail:link",
						"perm", "r"),
				"Verify granted permissions for the folder are matched");

	}

	@Test(description = "Create share using Context Menu - verify Share info using GUI", groups = { "smoke" })
	public void CreateShare_02() throws HarnessException {
		ZimbraAccount ownerAccount = app.zGetActiveAccount();

		FolderItem ownerBriefcaseRootFolder = FolderItem.importFromSOAP(
				ownerAccount, SystemFolder.Briefcase);

		ZAssert.assertNotNull(ownerBriefcaseRootFolder,
				"Verify the owner Briefcase root folder exists");

		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerFolder"
				+ ZimbraSeleniumProperties.getUniqueString();

		ownerAccount.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + ownerFoldername + "' l='"
				+ ownerBriefcaseRootFolder.getId() + "' view='document'/>"
				+ "</CreateFolderRequest>");

		// Verify the folder exists on the server
		FolderItem ownerFolderItem = FolderItem.importFromSOAP(ownerAccount,
				ownerFoldername);

		ZAssert.assertNotNull(ownerFolderItem, "Verify the owner folder exists");

		_folderIsCreated = true;
		_folderName = ownerFoldername;

		// click on My Files tab
		// app.zPageOctopus.zToolbarPressButton(Button.B_TAB_MY_FILES);

		// Owner selects Share option from the Context menu
		DialogFolderShare dialogShare = (DialogFolderShare) app.zPageMyFiles
				.zToolbarPressPulldown(Button.B_MY_FILES_LIST_ITEM,
						Button.O_FOLDER_SHARE, ownerFoldername);

		// Click on Permissions input field
		dialogShare.zClick(DialogFolderShare.Locators.zViewAndEditInput);

		// Provide input into Permissions field
		dialogShare.zTypeInput(DialogFolderShare.Locators.zViewEditAndShareInput,
				granteeAccount.EmailAddress);

		// Click on Show Message link
		dialogShare.zClickButton(Button.B_SHOW_MESSAGE);

		// Provide input into Message field
		dialogShare.zTypeInput(DialogFolderShare.Locators.zMessageInput,
				ownerFoldername);

		// Click on Share button
		dialogShare.zClickButton(Button.B_SHARE);

		SleepUtil.sleepSmall();

		// Select Share option from the Context menu
		dialogShare = (DialogFolderShare) app.zPageMyFiles.zToolbarPressPulldown(
				Button.B_MY_FILES_LIST_ITEM, Button.O_FOLDER_SHARE, ownerFoldername);

		// Verify the Share Info appears in the Dialog
		ZAssert.assertTrue(app.zPageSharing.zWaitForElementPresent(
				DialogFolderShare.Locators.zShareInfoField.locator + ":contains("
						+ granteeAccount.EmailAddress + ")", "5000"),
				"Verify Share Info appears in the Dialog");

		dialogShare.zClickButton(Button.B_CANCEL);
	}

	@Test(description = "Create share using SOAP - verify edit and view permissions message using SOAP", groups = { "functional" })
	public void CreateShare_03() throws HarnessException {
		ZimbraAccount ownerAccount = app.zGetActiveAccount();

		FolderItem ownerBriefcaseRootFolder = FolderItem.importFromSOAP(
				ownerAccount, SystemFolder.Briefcase);

		ZAssert.assertNotNull(ownerBriefcaseRootFolder,
				"Verify the owner Briefcase root folder exists");

		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerFolder"
				+ ZimbraSeleniumProperties.getUniqueString();

		ownerAccount.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + ownerFoldername + "' l='"
				+ ownerBriefcaseRootFolder.getId() + "' view='document'/>"
				+ "</CreateFolderRequest>");

		// Verify the folder exists on the server
		FolderItem ownerFolderItem = FolderItem.importFromSOAP(ownerAccount,
				ownerFoldername);

		ZAssert.assertNotNull(ownerFolderItem, "Verify the owner folder exists");

		_folderIsCreated = true;
		_folderName = ownerFoldername;

		ownerAccount.soapSend("<FolderActionRequest xmlns='urn:zimbraMail'>"
				+ "<action id='" + ownerFolderItem.getId() + "' op='grant'>"
				+ "<grant d='" + granteeAccount.EmailAddress
				+ "' gt='usr' perm='rwidx'/>" + "</action>"
				+ "</FolderActionRequest>");

		ownerAccount
				.soapSend("<SendShareNotificationRequest xmlns='urn:zimbraMail'>"
						+ "<item id='"
						+ ownerFolderItem.getId()
						+ "'/>"
						+ "<e a='"
						+ granteeAccount.EmailAddress
						+ "'/>"
						+ "<notes _content='I invite you to share the folder'/>"
						+ "</SendShareNotificationRequest>");

		SleepUtil.sleepSmall();
		
		// grantee verifies view and edit permissions in the notification message
		granteeAccount
				.soapSend("<GetShareNotificationsRequest xmlns='urn:zimbraMail'/>");

		ZAssert.assertTrue(granteeAccount.soapMatch(
				"//mail:GetShareNotificationsResponse//mail:link", "name",
				ownerFoldername),
				"Verify link to the folder in the notification message");

		ZAssert.assertTrue(granteeAccount.soapMatch(
				"//mail:GetShareNotificationsResponse//mail:link", "perm",
				"rwidx"),
				"Verify granted permissions for the folder are matched");
	}

	@Test(description = "Create share using SOAP - verify edit, view and share permissions message using SOAP", groups = { "functional" })
	public void CreateShare_04() throws HarnessException {
		ZimbraAccount ownerAccount = app.zGetActiveAccount();

		FolderItem ownerBriefcaseRootFolder = FolderItem.importFromSOAP(
				ownerAccount, SystemFolder.Briefcase);

		ZAssert.assertNotNull(ownerBriefcaseRootFolder,
				"Verify the owner Briefcase root folder exists");

		// Owner creates a folder, shares it with current user
		String ownerFoldername = "ownerFolder"
				+ ZimbraSeleniumProperties.getUniqueString();

		ownerAccount.soapSend("<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+ "<folder name='" + ownerFoldername + "' l='"
				+ ownerBriefcaseRootFolder.getId() + "' view='document'/>"
				+ "</CreateFolderRequest>");

		// Verify the folder exists on the server
		FolderItem ownerFolderItem = FolderItem.importFromSOAP(ownerAccount,
				ownerFoldername);

		ZAssert.assertNotNull(ownerFolderItem, "Verify the owner folder exists");

		_folderIsCreated = true;
		_folderName = ownerFoldername;

		ownerAccount.soapSend("<FolderActionRequest xmlns='urn:zimbraMail'>"
				+ "<action id='" + ownerFolderItem.getId() + "' op='grant'>"
				+ "<grant d='" + granteeAccount.EmailAddress
				+ "' gt='usr' perm='rwidxa'/>" + "</action>"
				+ "</FolderActionRequest>");

		ownerAccount
				.soapSend("<SendShareNotificationRequest xmlns='urn:zimbraMail'>"
						+ "<item id='"
						+ ownerFolderItem.getId()
						+ "'/>"
						+ "<e a='"
						+ granteeAccount.EmailAddress
						+ "'/>"
						+ "<notes _content='I invite you to share the folder'/>"
						+ "</SendShareNotificationRequest>");

		SleepUtil.sleepSmall();
		
		// grantee verifies view, edit and share permissions
		granteeAccount
				.soapSend("<GetShareNotificationsRequest xmlns='urn:zimbraMail'/>");

		ZAssert.assertTrue(granteeAccount.soapMatch(
				"//mail:GetShareNotificationsResponse//mail:link", "name",
				ownerFoldername),
				"Verify link to the folder in the notification message");

		ZAssert.assertTrue(granteeAccount.soapMatch(
				"//mail:GetShareNotificationsResponse//mail:link", "perm",
				"rwidxa"),
				"Verify granted permissions for the folder are matched");
	}

	
	@AfterMethod(groups = { "always" })
	public void createFolderTestCleanup() {
		if (_folderIsCreated) {
			try {
				// Delete it from Server
				FolderItem
						.deleteUsingSOAP(app.zGetActiveAccount(), _folderName);
			} catch (Exception e) {
				logger.info("Failed while removing the folder.");
				e.printStackTrace();
			} finally {
				_folderName = null;
				_folderIsCreated = false;
			}
		}
	}
}
