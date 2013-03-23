/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.admin.tests.distributionlists;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.DistributionListItem;
import com.zimbra.qa.selenium.projects.admin.ui.FormEditDistributionList;
import com.zimbra.qa.selenium.projects.admin.ui.PageMain;
import com.zimbra.qa.selenium.projects.admin.ui.PageSearchResults;

public class EditDistributionList extends AdminCommonTest {
	public EditDistributionList() {
		logger.info("New "+ EditDistributionList.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageDistributionList;

	}

	/**
	 * Testcase : Edit Distribution List name - Manage Distribution List view
	 * Steps :
	 * 1. Create a dl using SOAP.
	 * 2. Go to Manage dl View.
	 * 3. Select a dl.
	 * 4. Edit a dl using delete button in Gear box menu.
	 * 5. Verify dl is edit using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit Distribution List name - Manage Distribution List view",
			groups = { "smoke" })
			public void EditDistributionList_01() throws HarnessException {

		// Create a new dl in the Admin Console using SOAP
		DistributionListItem dl = new DistributionListItem();
		String dlEmailAddress=dl.getEmailAddress();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDistributionListRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + dlEmailAddress + "</name>"
				+		"</CreateDistributionListRequest>");

		// Refresh the list
		app.zPageManageDistributionList.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Click on distribution list to be deleted.
		app.zPageManageDistributionList.zListItem(Action.A_LEFTCLICK, dl.getEmailAddress());
		
		// Click on Edit button
		FormEditDistributionList form = (FormEditDistributionList) app.zPageManageDistributionList.zToolbarPressPulldown(Button.B_GEAR_BOX,Button.O_EDIT);
		//FormEditDistributionList form = (FormEditDistributionList) app.zPageManageDistributionList.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditDistributionList.TreeItem.MEMBERS);

		//Edit the name.
		String editedName = "editedDL_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the dl exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDistributionListRequest xmlns='urn:zimbraAdmin'>" +
				"<dl by='name'>"+editedName+"@"+dl.getDomainName()+	"</dl>"+
		"</GetDistributionListRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDistributionListResponse/admin:dl", 1);
		ZAssert.assertNotNull(response, "Verify the distribution list is edited successfully");
	}
	
	/**
	 * Testcase : Edit Distribution List name - Manage Distribution List view + Right Click Menu
	 * Steps :
	 * 1. Create a dl using SOAP.
	 * 2. Go to Manage dl View.
	 * 3. Right Click on a dl.
	 * 4. Edit a dl using delete button in right click menu.
	 * 5. Verify dl is edited using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit Distribution List name - Manage Distribution List view + Right Click Menu",
			groups = { "functional" })
			public void EditDistributionList_02() throws HarnessException {

		// Create a new dl in the Admin Console using SOAP
		DistributionListItem dl = new DistributionListItem();
		String dlEmailAddress=dl.getEmailAddress();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDistributionListRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + dlEmailAddress + "</name>"
				+		"</CreateDistributionListRequest>");

		// Refresh the list
		app.zPageManageDistributionList.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Right Click on distribution list to be deleted.
		app.zPageManageDistributionList.zListItem(Action.A_RIGHTCLICK, dl.getEmailAddress());
		
		// Click on Edit button
		FormEditDistributionList form = (FormEditDistributionList) app.zPageManageDistributionList.zToolbarPressButton(Button.B_TREE_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditDistributionList.TreeItem.MEMBERS);

		//Edit the name.
		String editedName = "editedDL_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the dl exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDistributionListRequest xmlns='urn:zimbraAdmin'>" +
				"<dl by='name'>"+editedName+"@"+dl.getDomainName()+	"</dl>"+
		"</GetDistributionListRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDistributionListResponse/admin:dl", 1);
		ZAssert.assertNotNull(response, "https://bugzilla.zimbra.com/show_bug.cgi?id=74487");
	}

	/**
	 * Testcase : Edit Admin Distribution List name - Manage Distribution List view
	 * Steps :
	 * 1. Create a admin dl using SOAP.
	 * 2. Go to Manage dl View.
	 * 3. Select a dl.
	 * 4. Edit a dl using delete button in Gear box menu.
	 * 5. Verify dl is edit using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit Admin Distribution List name - Manage Distribution List view",
			groups = { "functional" })
			public void EditDistributionList_03() throws HarnessException {

		// Create a new dl in the Admin Console using SOAP
		DistributionListItem dl = new DistributionListItem();
		String dlEmailAddress=dl.getEmailAddress();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDistributionListRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + dlEmailAddress + "</name>"
				+			"<a xmlns='' n='zimbraIsAdminGroup'>TRUE</a>"
				+		"</CreateDistributionListRequest>");

		// Refresh the list
		app.zPageManageDistributionList.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Click on distribution list to be deleted.
		app.zPageManageDistributionList.zListItem(Action.A_LEFTCLICK, dl.getEmailAddress());
		
		// Click on Edit button
		FormEditDistributionList form = (FormEditDistributionList) app.zPageManageDistributionList.zToolbarPressPulldown(Button.B_GEAR_BOX,Button.O_EDIT);
		//FormEditDistributionList form = (FormEditDistributionList) app.zPageManageDistributionList.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditDistributionList.TreeItem.MEMBERS);

		//Edit the name.
		String editedName = "editedDL_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the dl exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDistributionListRequest xmlns='urn:zimbraAdmin'>" +
				"<dl by='name'>"+editedName+"@"+dl.getDomainName()+	"</dl>"+
		"</GetDistributionListRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDistributionListResponse/admin:dl", 1);
		ZAssert.assertNotNull(response, "Verify the distribution list is edited successfully");
	}

	/**
	 * Testcase : Edit Dynamic Admin Distribution List name - Manage Distribution List view
	 * Steps :
	 * 1. Create a Dynamic admin dl using SOAP.
	 * 2. Go to Manage dl View.
	 * 3. Select a dl.
	 * 4. Edit a dl using delete button in Gear box menu.
	 * 5. Verify dl is edit using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit Dynamic Admin Distribution List name - Manage Distribution List view",
			groups = { "functional" })
			public void EditDistributionList_04() throws HarnessException {

		// Create a new dl in the Admin Console using SOAP
		DistributionListItem dl = new DistributionListItem();
		String dlEmailAddress=dl.getEmailAddress();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDistributionListRequest xmlns='urn:zimbraAdmin' dynamic='1'>"
				+			"<name>" + dlEmailAddress + "</name>"
				+			"<a xmlns='' n='zimbraIsAdminGroup'>TRUE</a>"
				+			"<a xmlns='' n='zimbraIsACLGroup'>TRUE</a>"
				+		"</CreateDistributionListRequest>");

		// Refresh the list
		app.zPageManageDistributionList.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");
		
		// Click on distribution list to be deleted.
		app.zPageManageDistributionList.zListItem(Action.A_LEFTCLICK, dl.getEmailAddress());
		
		// Click on Edit button
		FormEditDistributionList form = (FormEditDistributionList) app.zPageManageDistributionList.zToolbarPressPulldown(Button.B_GEAR_BOX,Button.O_EDIT);
		//FormEditDistributionList form = (FormEditDistributionList) app.zPageManageDistributionList.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditDistributionList.TreeItem.MEMBERS);

		//Edit the name.
		String editedName = "editedDL_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the dl exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDistributionListRequest xmlns='urn:zimbraAdmin'>" +
				"<dl by='name'>"+editedName+"@"+dl.getDomainName()+	"</dl>"+
		"</GetDistributionListRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDistributionListResponse/admin:dl", 1);
		ZAssert.assertNotNull(response, "Verify the distribution list is edited successfully");
	}

	
	/**
	 * Testcase : Verify edit operation for DL - Search distribution list view.
	 * Steps :
	 * 1. Create a dl using SOAP.
	 * 2. Search dl.
	 * 3. Select a dl.
	 * 4. Edit a dl using delete button in Gear box menu.
	 * 5. Verify dl is edited using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify edit operation for distribution list - Search distribution list view",
			groups = { "functional" })
			public void EditDistributionList_05() throws HarnessException {

		// Create a new dl in the Admin Console using SOAP
		DistributionListItem dl = new DistributionListItem();
		String dlEmailAddress=dl.getEmailAddress();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDistributionListRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + dlEmailAddress + "</name>"
				+		"</CreateDistributionListRequest>");

		// Enter the search string to find the dl
		app.zPageSearchResults.zAddSearchQuery(dlEmailAddress);

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);


		// Right Click on distribution list to be Edited.
		app.zPageSearchResults.zListItem(Action.A_LEFTCLICK, dl.getEmailAddress());

		// Click on Edit button
		app.zPageSearchResults.setType(PageSearchResults.TypeOfObject.DISTRIBUTION_LIST);
		FormEditDistributionList form = (FormEditDistributionList) app.zPageManageDistributionList.zToolbarPressPulldown(Button.B_GEAR_BOX,Button.O_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditDistributionList.TreeItem.MEMBERS);

		//Edit the name.
		String editedName = "editedDL_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the dl exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDistributionListRequest xmlns='urn:zimbraAdmin'>" +
				"<dl by='name'>"+editedName+"@"+dl.getDomainName()+	"</dl>"+
		"</GetDistributionListRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDistributionListResponse/admin:dl", 1);
		ZAssert.assertNotNull(response, "Verify the distribution list is edited successfully");

	}
	
	/**
	 * Testcase : Verify edit operation for DL - Search distribution list view + Right click.
	 * Steps :
	 * 1. Create a dl using SOAP.
	 * 2. Search dl.
	 * 3. Right click on dl.
	 * 4. Edit a dl using edit button in Gear box menu.
	 * 5. Verify dl is edited using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Verify edit operation for distribution list - Search distribution list view + right click",
			groups = { "functional" })
			public void EditDistributionList_06() throws HarnessException {

		// Create a new dl in the Admin Console using SOAP
		DistributionListItem dl = new DistributionListItem();
		String dlEmailAddress=dl.getEmailAddress();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateDistributionListRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + dlEmailAddress + "</name>"
				+		"</CreateDistributionListRequest>");

		// Enter the search string to find the dl
		app.zPageSearchResults.zAddSearchQuery(dlEmailAddress);

		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);


		// Right Click on distribution list to be Edited.
		app.zPageSearchResults.zListItem(Action.A_RIGHTCLICK, dl.getEmailAddress());

		// Click on Edit button
		app.zPageSearchResults.setType(PageSearchResults.TypeOfObject.DISTRIBUTION_LIST);
		FormEditDistributionList form = (FormEditDistributionList) app.zPageSearchResults.zToolbarPressButton(Button.B_TREE_EDIT);
	
		//Click on General Information tab.
		form.zClickTreeItem(FormEditDistributionList.TreeItem.MEMBERS);

		//Edit the name.
		String editedName = "editedDL_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the dl exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDistributionListRequest xmlns='urn:zimbraAdmin'>" +
				"<dl by='name'>"+editedName+"@"+dl.getDomainName()+	"</dl>"+
		"</GetDistributionListRequest>");

		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDistributionListResponse/admin:dl", 1);
		ZAssert.assertNotNull(response, "Verify the distribution list is edited successfully");

	}


	
}
