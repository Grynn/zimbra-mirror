/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 VMware, Inc.
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
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.AccountItem;
import com.zimbra.qa.selenium.projects.admin.ui.WizardCreateAdminAccount;

public class CreateAdminDistributionList extends AdminCommonTest {
	public CreateAdminDistributionList() {
		logger.info("New "+ CreateAdminDistributionList.class.getCanonicalName());

		// All tests start at the "Distribution Lists" page
		super.startingPage = app.zPageManageDistributionList;
	}

	/**
	 * Testcase : Create a basic admin group.
	 * Steps :
	 * 1. Create an admin account from GUI.
	 * 2. Verify account is created using SOAP.
	 * @throws HarnessException
	 */
	@Test(	description = "Create a Admin group",
			groups = { "functional" })
			public void CreateAdminDistributionList_01() throws HarnessException {
	
		// Create a new account in the Admin Console
		AccountItem account = new AccountItem("delegated_admin_group" + ZimbraSeleniumProperties.getUniqueString(),ZimbraSeleniumProperties.getStringProperty("testdomain"));
	
		// Click "New" -> "Admin Account"
		WizardCreateAdminAccount wizard = 
			(WizardCreateAdminAccount)app.zPageManageAccounts.zToolbarPressPulldown(Button.B_GEAR_BOX, Button.O_NEW_ADMIN);
	
		// Fill out the wizard and click Finish
		wizard.setAdminType(WizardCreateAdminAccount.Locators.ADMIN_GROUP);
		wizard.zCompleteWizard(account);
	
	
		// Verify the account exists in the ZCS
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<GetDistributionListRequest xmlns='urn:zimbraAdmin'>" +
	            "<dl by='name'>"+account.getEmailAddress()+"</dl>"+
	          "</GetDistributionListRequest>");
		Element response = ZimbraAdminAccount.AdminConsoleAdmin().soapSelectNode("//admin:GetDistributionListResponse/admin:dl", 1); 
		ZAssert.assertNotNull(response, "Verify the admin group is created successfully");
	}

}
