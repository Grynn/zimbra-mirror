/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.admin.tests.cos;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.CosItem;
import com.zimbra.qa.selenium.projects.admin.ui.FormEditCos;
import com.zimbra.qa.selenium.projects.admin.ui.PageMain;
import com.zimbra.qa.selenium.projects.admin.ui.PageSearchResults;

public class EditCos extends AdminCommonTest {
	public EditCos() {
		logger.info("New "+ EditCos.class.getCanonicalName());

		// All tests start at the "Accounts" page
		super.startingPage = app.zPageManageCOS;

	}

	/**
	 * Testcase : Edit account name  - Manage Account View
	 * Steps :
	 * 1. Create an cos using SOAP.
	 * 2. Go to Manage Cos View
	 * 3. Select an Cos.
	 * 4. Edit an cos using edit button in Gear box menu.
	 * 5. Verify cos is edited using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit Cos name  - Manage Cos View",
			groups = { "functional" })
			public void EditCos_01() throws HarnessException {

		// Create a new cos in the Admin Console using SOAP
		CosItem cos = new CosItem();
		String cosName=cos.getName();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateCosRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + cosName + "</name>"
				+		"</CreateCosRequest>");

		// Refresh the account list
		app.zPageManageCOS.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");

		// Click on account to be deleted.
		app.zPageManageCOS.zListItem(Action.A_LEFTCLICK, cosName);
		
		// Click on Edit button
		FormEditCos form = (FormEditCos) app.zPageManageCOS.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditCos.TreeItem.GENERAL_INFORMATION);

		//Edit the name.
		String editedName = "editedCos_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the cos exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
		"<GetCosRequest xmlns='urn:zimbraAdmin'>" +
		                     "<cos by='name'>"+editedName+"</cos>"+
		                   "</GetCosRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetCosResponse/admin:cos", 1);
		ZAssert.assertNotNull(response, "Verify the cos is edited successfully");	
	}
	
	
	/**
	 * Testcase : Edit cos name -- right click 
	 * Steps :
	 * 1. Create an cos using SOAP.
	 * 2. Edit the cos name using UI Right Click.
	 * 3. Verify cos name is changed using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit cos name -- right click",
			groups = { "functional" })
			public void EditCos_02() throws HarnessException {
		// Create a new cos in the Admin Console using SOAP
		CosItem cos = new CosItem();
		String cosName=cos.getName();

		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateCosRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + cosName + "</name>"
				+		"</CreateCosRequest>");

		// Refresh the account list
		app.zPageManageCOS.sClickAt(PageMain.Locators.REFRESH_BUTTON, "");

		// Click on account to be deleted.
		app.zPageManageCOS.zListItem(Action.A_LEFTCLICK, cosName);
		
		// Click on Edit button
		FormEditCos form = (FormEditCos) app.zPageManageCOS.zToolbarPressButton(Button.B_TREE_EDIT);
				
		//Click on General Information tab.
		form.zClickTreeItem(FormEditCos.TreeItem.GENERAL_INFORMATION);

		//Edit the name.
		String editedName = "editedCos_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the cos exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
		"<GetCosRequest xmlns='urn:zimbraAdmin'>" +
		                     "<cos by='name'>"+editedName+"</cos>"+
		                   "</GetCosRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetCosResponse/admin:cos", 1);
		ZAssert.assertNotNull(response, "https://bugzilla.zimbra.com/show_bug.cgi?id=79304");
	}

	/**
	 * Testcase : Edit account name  - Manage Account View
	 * Steps :k
	 * 1. Create an cos using SOAP.
	 * 2. Go to Manage Cos View
	 * 3. Select an Cos.
	 * 4. Edit an cos using edit button in Gear box menu.
	 * 5. Verify cos is edited using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit Cos name  - Search Cos View",
			groups = { "functional" })
			public void EditCos_03() throws HarnessException {
	
		// Create a new cos in the Admin Console using SOAP
		CosItem cos = new CosItem();
		String cosName=cos.getName();
	
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateCosRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + cosName + "</name>"
				+		"</CreateCosRequest>");
	
		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(cosName);
	
		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);
		
		// Click on cos to be deleted.
		app.zPageSearchResults.zListItem(Action.A_LEFTCLICK, cos.getName());
	
	
		// Click on Edit button
		app.zPageSearchResults.setType(PageSearchResults.TypeOfObject.COS);
		FormEditCos form = (FormEditCos) app.zPageSearchResults.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditCos.TreeItem.GENERAL_INFORMATION);
	
		//Edit the name.
		String editedName = "editedCos_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the cos exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
		"<GetCosRequest xmlns='urn:zimbraAdmin'>" +
		                     "<cos by='name'>"+editedName+"</cos>"+
		                   "</GetCosRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetCosResponse/admin:cos", 1);
		ZAssert.assertNotNull(response, "Verify the cos is edited successfully");	}

	/**
	 * Testcase : Edit cos name -- right click 
	 * Steps :
	 * 1. Create an cos using SOAP.
	 * 2. Edit the cos name using UI Right Click.
	 * 3. Verify cos name is changed using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Edit cos name -- right click",
			groups = { "functional" })
			public void EditCos_04() throws HarnessException {
		// Create a new cos in the Admin Console using SOAP
		CosItem cos = new CosItem();
		String cosName=cos.getName();
	
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateCosRequest xmlns='urn:zimbraAdmin'>"
				+			"<name>" + cosName + "</name>"
				+		"</CreateCosRequest>");
	
		// Enter the search string to find the account
		app.zPageSearchResults.zAddSearchQuery(cosName);
	
		// Click search
		app.zPageSearchResults.zToolbarPressButton(Button.B_SEARCH);
		
		// Click on cos to be deleted.
		app.zPageSearchResults.zListItem(Action.A_RIGHTCLICK, cos.getName());
	
		// Click on Edit button
		app.zPageSearchResults.setType(PageSearchResults.TypeOfObject.COS);
		FormEditCos form = (FormEditCos) app.zPageSearchResults.zToolbarPressButton(Button.B_TREE_EDIT);
		
		//Click on General Information tab.
		form.zClickTreeItem(FormEditCos.TreeItem.GENERAL_INFORMATION);
	
		//Edit the name.
		String editedName = "editedCos_" + ZimbraSeleniumProperties.getUniqueString();
		form.setName(editedName);
		
		//Submit the form.
		form.zSubmit();
		
		// Verify the cos exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
		"<GetCosRequest xmlns='urn:zimbraAdmin'>" +
		                     "<cos by='name'>"+editedName+"</cos>"+
		                   "</GetCosRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetCosResponse/admin:cos", 1);
		ZAssert.assertNotNull(response, "https://bugzilla.zimbra.com/show_bug.cgi?id=79304");
	}

}
